package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.identification.ClusterIdentifier;
import org.preesm.algorithm.clustering.synthesis.ClusterSynthesis;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiSDFExporterTask;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "clustering.creation", name = "Clustering task", category = "Clustering",

    parameters = {

      @Parameter(name = ClusterCreationTask.PARAM_VERTICAL_HEURISTIC, description = """
          Name of which heuristic will be used to vertically clusterize the graph.
          If there is not enough or too many hierarchical level following the heuristic,
          it will create or regroup hierarchical levels. This parameter is optional. If not present
          , the input graph will just be flatten. For now, there is no vertical heuristic.
          """, values = {}),

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

      @Parameter(name = ClusterCreationTask.PARAM_MAPPING_HEURISTIC, description = """
          Name of which heuristic will assign a component type on which the cluster could be executed on:
          - The Simple mapper will select the same component type for every cluster than the main operator one.
          - The Classic mapper will select a component that is at least shared by every actors in the cluster.
          If none, throws an error.
              """,
          values = { @Value(name = HeuristicGetter.CLASSIC_MAPPER), @Value(name = HeuristicGetter.SIMPLE_MAPPER) }),

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

      @Parameter(name = ClusterCreationTask.PARAM_SCHEDULING_HEURISTIC, description = """
          Name of which heuristic will schedule the clusters in the graph. For now, the only available scheduling
          algorithm is the APGAN algorithm. We can't reuse existing scheduling algorithm as they depend on a DAG.
          However, DAG creation must be avoided in the cluster synthesis process.
          [WORK IN PROGRESS]: the FPGA shceduler will soon be implemented for FPGA clusters identified with the FPGA
          heuristic.
          """, values = { @Value(name = HeuristicGetter.APGAN_SCHEDULING) }),

      @Parameter(name = ClusterCreationTask.PARAM_ALLOCATION_HEURISTIC, description = """
          Name of which heuristic will allocate the clusters memory. Work with a cluster scheduler only
          (see scheduling heuristic parameters for all available schedulers that can work with cluster
          allocation).
          """, values = { @Value(name = HeuristicGetter.SIMPLE_ALLOCATION) }),

      @Parameter(name = ClusterCreationTask.PARAM_VERBOSE, description = "More or less logs",
          values = { @Value(name = "True"), @Value(name = "False") }),

      @Parameter(name = ClusterCreationTask.PARAM_DEBUG,
          description = "Enable intermediate graph creation, to check if everything is ok between substeps.",
          values = { @Value(name = "True"), @Value(name = "False") }) },

    inputs = { @Port(name = "scenario", type = Scenario.class, description = "Input scenario"),
      @Port(name = "PiMM", type = PiGraph.class, description = "Input PiGraph algorithm"),
      @Port(name = "architecture", type = Design.class, description = "Input S-LAM architecture graph") },

    outputs = {
      @Port(name = "PiMM", type = PiGraph.class, description = "Output PiGraph algorithm, modified by heuristics"),
      @Port(name = "scenario", type = Scenario.class, description = "Modified scenario, with the clusters constraints"),
      @Port(name = "clusters", type = List.class, description = "List containing the created clusters"),
      @Port(name = "schedules", type = List.class, description = "List containing the clusters schedule"),
      @Port(name = "allocations", type = List.class, description = "List containing the clusters allocation") },

    description = """
        Workflow task responsible for identifying, creating, schedule, map and allocate clusters.
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
        Workflow task responsible for identifying, creating, schedule, map and allocate clusters.
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

  public static final String PARAM_VERTICAL_HEURISTIC   = "Vertical heuristic";
  public static final String PARAM_HORIZONTAL_HEURISTIC = "Horizontal heuristic";
  public static final String PARAM_MAPPING_HEURISTIC    = "Mapping heuristic";
  public static final String PARAM_BALANCING_HEURISTIC  = "Balancing heuristic";
  public static final String PARAM_SCHEDULING_HEURISTIC = "Scheduling heuristic";
  public static final String PARAM_ALLOCATION_HEURISTIC = "Allocation heuristic";
  public static final String PARAM_VERBOSE              = "Verbose";
  public static final String PARAM_DEBUG                = "Debug";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting inputs
    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final boolean debug = "true".equalsIgnoreCase(parameters.get(ClusterCreationTask.PARAM_DEBUG));

    // ========================================================================
    //
    // GRAPH TRANSFO & CLUSTER ID
    //
    // ========================================================================

    // Here, the algorithm parameter will be modified. It will no longer contain actors in clusters, but will contain
    // directly the clusters.
    // The scenario parameter will also be modified. The clusters constraints identified with the mapping heuristic will
    // be added to the scenario.
    final Pair<PiGraph, Map<PiGraph, Component>> result = ClusterIdentifier.identify(algorithm, scenario, architecture,
        parameters, workflow);
    final PiGraph newAlgo = result.getKey();
    final List<PiGraph> clusters = new ArrayList<>(result.getValue().keySet());

    // Debug
    if (debug) {
      Map<String, Object> exportInputs = null;
      Map<String, String> exportParameters;
      exportInputs = new HashMap<>();
      exportInputs.put("PiMM", newAlgo);
      exportParameters = new HashMap<>();
      exportParameters.put("path", "/Algo/generated/clustering/debug/");
      exportParameters.put("hierarchical", "true");
      new PiSDFExporterTask().execute(exportInputs, exportParameters, monitor, nodeName, workflow);
    }

    // ========================================================================
    //
    // CLUSTERS SCHEDULING & ALLOCATION
    //
    // ========================================================================

    // The outputs in the form of a set containing SynthesisResult instances will not have any informations on mapping,
    // the mapping parameter will be kept to null.
    final Set<SynthesisResult> clusterSyntheses = ClusterSynthesis.scheduleAndAllocate(newAlgo, scenario, architecture,
        clusters, parameters);

    // Separating schedules and allocations
    final List<Schedule> schedules = clusterSyntheses.stream().map(x -> x.schedule).toList();
    final List<Allocation> allocations = clusterSyntheses.stream().map(x -> x.alloc).toList();

    final Map<String, Object> outputs = new HashMap<>();

    if (clusters.size() != clusterSyntheses.size()) {
      throw new PreesmRuntimeException("at least one cluster can't be synthesized.");
    }

    // ========================================================================
    //
    // OUTPUTS
    //
    // ========================================================================
    outputs.put("PiMM", newAlgo);
    outputs.put("scenario", scenario);
    outputs.put("clusters", clusters);
    outputs.put("schedules", schedules);
    outputs.put("allocations", allocations);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> result = new HashMap<>();
    result.put(ClusterCreationTask.PARAM_VERTICAL_HEURISTIC, "");
    result.put(ClusterCreationTask.PARAM_HORIZONTAL_HEURISTIC, HeuristicGetter.URC_IDENTIFIER);
    result.put(ClusterCreationTask.PARAM_MAPPING_HEURISTIC, HeuristicGetter.CLASSIC_MAPPER);
    result.put(ClusterCreationTask.PARAM_BALANCING_HEURISTIC, HeuristicGetter.COMPLETE_BALANCING);
    result.put(ClusterCreationTask.PARAM_SCHEDULING_HEURISTIC, HeuristicGetter.APGAN_SCHEDULING);
    result.put(ClusterCreationTask.PARAM_VERBOSE, "true");
    result.put(ClusterCreationTask.PARAM_DEBUG, "true");
    return result;
  }

  @Override
  public String monitorMessage() {
    return "Identifies, creates, maps, schedules, allocates, and generates code for clusters";
  }

}
