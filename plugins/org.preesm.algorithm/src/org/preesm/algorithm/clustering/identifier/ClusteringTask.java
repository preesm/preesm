package org.preesm.algorithm.clustering.identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.heuristics.HorizontalHeuristic;
import org.preesm.algorithm.clustering.heuristics.MappingHeuristic;
import org.preesm.algorithm.clustering.heuristics.PartitionerHeuristic;
import org.preesm.algorithm.clustering.heuristics.VerticalHeuristic;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/***
 * @author jmorin
 * @author rcazoulat
 **/

@PreesmTask(id = "clustering.identifier", name = "Cluster Identifier",

    parameters = { @Parameter(name = ClusteringTask.PARAM_VERTICAL_HEURISTIC, description = "", values = {}),
      @Parameter(name = ClusteringTask.PARAM_HORIZONTAL_HEURISTIC, description = "",
          values = { @Value(name = HeuristicGetter.DEFAULT_IDENTIFIER), @Value(name = "srv") }),
      @Parameter(name = ClusteringTask.PARAM_MAPPING_HEURISTIC, description = "",
          values = { @Value(name = HeuristicGetter.DEFAULT_MAPPER) }),
      @Parameter(name = ClusteringTask.PARAM_PARTITIONING_HEURISTIC, description = "",
          values = { @Value(name = HeuristicGetter.DEFAULT_PARTITIONER) }) },

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
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, type = List.class,
          description = "Output clusters, in PiGraph form"),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class,
          description = "Modified scenario") },

    description = "Workflow task responsible for clustering actors."

)
public class ClusteringTask extends AbstractTaskImplementation {
  public static final String PARAM_VERTICAL_HEURISTIC     = "Vertical heuristic";
  public static final String PARAM_HORIZONTAL_HEURISTIC   = "Horizontal heuristic";
  public static final String PARAM_MAPPING_HEURISTIC      = "Mapping heuristic";
  public static final String PARAM_PARTITIONING_HEURISTIC = "Partitioning heuristic";
  public static final String PARAM_VERBOSE                = "Verbose";
  public static final String PARAM_CLUSTERIZE             = "Clusterize";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    /**
     * 4 Steps:
     *
     * 1. Vertical cluster identification
     *
     * 2. Horizontal cluster identification
     *
     * 3. Setting constraints of clusters in scenario
     *
     * 4. Partitioning (balancing) clusters weights, and creating additional clusters if needed (e. g., if nPEs %
     * nClusterRep != 0)
     */

    // Getting inputs
    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    // Getting parameters
    final boolean CLUSTERIZE = "true".equalsIgnoreCase(parameters.get(PARAM_CLUSTERIZE));

    List<PiGraph> clustersList = new ArrayList<>();

    if (CLUSTERIZE) {

      final boolean verbose = "true".equalsIgnoreCase(parameters.get(PARAM_VERBOSE));
      final String verticalIdentifierName = parameters.getOrDefault(PARAM_VERTICAL_HEURISTIC, "").toLowerCase();
      String horizontalIdentifierName = parameters.getOrDefault(PARAM_HORIZONTAL_HEURISTIC, "").toLowerCase();
      final String mapperName = parameters.getOrDefault(PARAM_MAPPING_HEURISTIC, "").toLowerCase();
      final String partitionerName = parameters.getOrDefault(PARAM_PARTITIONING_HEURISTIC, "").toLowerCase();

      // ---------------------------------------------------------------------------------------------- //
      // Vertical clusters
      // ---------------------------------------------------------------------------------------------- //
      // It will modify the algorithm graph

      // Vertical clusterization is OPTIONAL.
      // If there is no heuristic, it will just flatten the graph
      final VerticalHeuristic vertiIdentifier = (VerticalHeuristic) HeuristicGetter
          .getHeuristic(verticalIdentifierName);
      if (vertiIdentifier != null) {
        vertiIdentifier.initHeuristicParameters(algorithm, scenario, architecture, parameters);
      }

      ClusterBuilder.buildVerticalClusters(null, algorithm, vertiIdentifier);

      if (verbose) {
        PreesmLogger.getLogger().info("Clustering Id: building vertical clusters done.");
      }

      // ---------------------------------------------------------------------------------------------- //
      // Horizontal clusters
      // ---------------------------------------------------------------------------------------------- //
      // It will modify the algorithm graph & return the created subgraphs list

      if (horizontalIdentifierName == null || "".equals(horizontalIdentifierName)) {
        horizontalIdentifierName = HeuristicGetter.DEFAULT_IDENTIFIER;
      }
      final HorizontalHeuristic horizIdentifier = (HorizontalHeuristic) HeuristicGetter
          .getHeuristic(horizontalIdentifierName);

      horizIdentifier.initHeuristicParameters(algorithm, scenario, architecture, parameters);

      clustersList = ClusterBuilder.buildHorizontalClusters(algorithm /* will be modified */, architecture, scenario,
          horizIdentifier, verbose);

      if (verbose) {
        PreesmLogger.getLogger()
            .info("Clustering Id: building horizontal clusters with " + horizontalIdentifierName + " heuristic done.");
      }

      // ---------------------------------------------------------------------------------------------- //
      // Setting clusters constraints in scenario
      // ---------------------------------------------------------------------------------------------- //
      // It will modify the scenario by adding constraints.

      // Create Refinement link in advance
      clustersList.parallelStream().forEach(cluster -> ClusterBuilder.buildClusterRefinement(cluster, scenario));

      final MappingHeuristic clusterMapper = (MappingHeuristic) HeuristicGetter.getHeuristic(mapperName);

      clusterMapper.initHeuristicParameters(algorithm, scenario /* will be modified */, architecture, parameters);

      for (final PiGraph cluster : clustersList) {

        // Picking the component type of cluster thanks to the mapping heuristic
        final Component clusterComponent = clusterMapper.selectComponent(cluster);

        // Adding the constraints of all the component instances for the given cluster (works in a mono-node only)
        for (final ComponentInstance ci : architecture.getComponentInstancesOfType(clusterComponent)) {
          scenario.addConstraint(ci, cluster);
        }
      }

      if (verbose) {
        PreesmLogger.getLogger().info("Clustering Id: clusters constraints Id done");
      }

      // ---------------------------------------------------------------------------------------------- //
      // Partitioning
      // ---------------------------------------------------------------------------------------------- //
      // It will modify cluster lists and clusters' input and output weights

      final PartitionerHeuristic clusterPartitioner = (PartitionerHeuristic) HeuristicGetter
          .getHeuristic(partitionerName);

      clusterPartitioner.initHeuristicParameters(algorithm, scenario, architecture, parameters);

      final List<PiGraph> newClustersList = new ArrayList<>();
      clustersList.stream().forEach(cluster -> newClustersList
          .addAll(clusterPartitioner.balanceFirings(algorithm /* will be modified */, cluster)));

      clustersList = newClustersList;

      if (verbose) {
        PreesmLogger.getLogger().info("Clustering Id: partitioning clusters done");
      }

    } else {
      PreesmLogger.getLogger().info("Clustering Id: clustering was not activated.");
    }

    // Building outputs list
    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, clustersList);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> defaultParams = new HashMap<>();
    defaultParams.put(PARAM_HORIZONTAL_HEURISTIC, HeuristicGetter.DEFAULT_IDENTIFIER);
    defaultParams.put(PARAM_MAPPING_HEURISTIC, HeuristicGetter.DEFAULT_MAPPER);
    defaultParams.put(PARAM_PARTITIONING_HEURISTIC, HeuristicGetter.DEFAULT_PARTITIONER);

    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return null;
  }

}
