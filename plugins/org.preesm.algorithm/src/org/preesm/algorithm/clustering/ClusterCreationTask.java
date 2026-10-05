package org.preesm.algorithm.clustering;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.identification.ClusterIdentifier;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "clustering.creation", name = "Clustering task", category = "Graph Transformation",

    parameters = {

      @Parameter(name = ClusterCreationTask.PARAM_VERTICAL_HEURISTIC, description = """
          Name of which heuristic will be used to vertically clusterize the graph.
          If there is not enough or too many hierarchical level following the heuristic,
          it will create or regroup hierarchical levels. This parameter is optional. If not present
          , the input graph will just be flatten. For now, there is no vertical heuristic.
          """,
          values = { @Value(name = ClusterCreationTask.VALUE_VERTICAL_HEURISTIC_NONE,
              effect = "The input graph will be flatten.") }),

      @Parameter(name = ClusterCreationTask.PARAM_HORIZONTAL_HEURISTIC, description = """
          Name of which heuristic will be used to horizontaly clusterize the graph. Every hierachical
          level of the graph will be explored.
          There is three available horizontal heuristic:
          - The Unique Repetition Count (URC) that will regroup together the actors that are adjacent
          and have the same repetition count in the graph
          - The Single Repetition Vector (SRV) that will create clusters that contain only one actor,
          with a repetition count higher than the number of processing elements. Even if the cluster
          contains one actor, the DAG will be smaller since the cluster in the top level will have a
          lower repetition count than the actor contained in it.
          - [WARNING : WORK IN PROGRESS] The heteregeneous architecture separator that will create
          clusters only composed of actor mapped on FPGA. This heuristic works with a special scheduling method,
          not yet implemented for clustering.""",
          values = { @Value(name = HeuristicGetter.URC_IDENTIFIER), @Value(name = HeuristicGetter.URC_IDENTIFIER),
            @Value(name = HeuristicGetter.SIMPLE_FPGA_IDENTIFIER) }),

      @Parameter(name = ClusterCreationTask.PARAM_BALANCING_HEURISTIC, description = """
          Name of which heuristic will balance the clusters in the graph. By default, clusters are set to its
          maximal repetition, but the balancer can modify that. There is two available balancers :
          - Complete balancer : will handle balancing even if repetition count of cluster is not divisible
          by number of available processing elements, by duplicating the cluster and asdding fork/join actors.
          - Basic balancer : will handle balancing only if repetition count of cluster is a divisor of number of
          available processing elements. It is better to use the complete balancer, as it is robust to a larger
          variety of graphs.
          """,
          values = { @Value(name = HeuristicGetter.COMPLETE_BALANCING),
            @Value(name = HeuristicGetter.BASIC_BALANCING) }),

      @Parameter(name = ClusterCreationTask.PARAM_VERBOSE, description = "More or less logs",
          values = { @Value(name = "True"), @Value(name = "False") }) },

    inputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class,
          description = "Input scenario"),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class,
          description = "Input PiGraph algorithm"),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class,
          description = "Input S-LAM architecture graph") },

    outputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class,
          description = "Output PiGraph algorithm, modified by heuristics"),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class,
          description = "Modified scenario, with the clusters constraints") },

    description = """
        Workflow task responsible for identifying, creating and balancing clusters.
        The goal is to reduce the complexity of the graph without loosing parallelism,
        according to the given heuristics. The available heuristics are described in the parameters section.
        If the user wants to create an heuristic, he will have to create them in the source code of PREESM,
        in the package org.preesm.algorithm.clustering. The created heuristic will have to inherit of an heuristic class
        (that are located in the package org.preesm.algorithm.clustering.heuristics).
        Each heuristic type has an abstract class here. Then, This heuristic has to be added in the static method
        getHeuristic of the HeursiticGetter class. This way, the heuristic will be retrieved by the workflow.
        For more infos, go check the source code.
        """,

    shortDescription = """
        Workflow task responsible for identifying, creating, and balancing clusters.
        The goal is to reduce the complexity of the graph without loosing parallelism,
        according to the given heuristics.
        """,

    seeAlso = """
        - Renaud, Ophélie, Dylan Gageot, Karol Desnos, et Jean-François Nezan.
        SCAPE: HW-Aware Clustering of Dataflow Actors for Tunable Scheduling Complexity.
        In Design and Architecture for Signal and Image Processing, 2023.

        - Renaud, Ophelie, Naouel Haggui, Karol Desnos, et J. F. Nezan. Automated Clustering
        and Pipelining of Dataflow Actors for Controlled Scheduling Complexity. 22 octobre 2023.
        """

)
public class ClusterCreationTask extends AbstractTaskImplementation {

  public static final String PARAM_VERTICAL_HEURISTIC      = "Vertical heuristic";
  public static final String VALUE_VERTICAL_HEURISTIC_NONE = "None";
  public static final String PARAM_HORIZONTAL_HEURISTIC    = "Horizontal heuristic";
  public static final String PARAM_MAPPING_HEURISTIC       = "Mapping heuristic";
  public static final String PARAM_BALANCING_HEURISTIC     = "Balancing heuristic";
  public static final String PARAM_SCHEDULING_HEURISTIC    = "Scheduling heuristic";
  public static final String PARAM_ALLOCATION_HEURISTIC    = "Allocation heuristic";
  public static final String PARAM_VERBOSE                 = "Verbose";
  public static final String PARAM_DEBUG                   = "Debug";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting inputs
    PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    // ========================================================================
    //
    // GRAPH TRANSFO & CLUSTER ID
    //
    // ========================================================================

    // Here, the algorithm parameter will be modified. It will no longer contain actors in clusters, but will contain
    // directly the clusters.The scenario parameter will also be modified. The clusters constraints identified with the
    // mapping heuristic will be added to the scenario.
    algorithm = ClusterIdentifier.identify(algorithm, scenario, architecture, parameters);

    // ========================================================================
    //
    // OUTPUTS
    //
    // ========================================================================
    final Map<String, Object> outputs = new HashMap<>();

    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> result = new HashMap<>();
    result.put(ClusterCreationTask.PARAM_VERTICAL_HEURISTIC, ClusterCreationTask.VALUE_VERTICAL_HEURISTIC_NONE);
    result.put(ClusterCreationTask.PARAM_HORIZONTAL_HEURISTIC, HeuristicGetter.URC_IDENTIFIER);
    result.put(ClusterCreationTask.PARAM_BALANCING_HEURISTIC, HeuristicGetter.COMPLETE_BALANCING);
    result.put(ClusterCreationTask.PARAM_VERBOSE, "true");
    return result;
  }

  @Override
  public String monitorMessage() {
    return "Identifies and balances clusters in graph";
  }

}
