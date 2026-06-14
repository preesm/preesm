package org.preesm.algorithm.clustering;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.heuristics.ClusteringHeuristic;
import org.preesm.algorithm.clustering.heuristics.HorizontalClusteringHeuristic;
import org.preesm.algorithm.clustering.heuristics.PartitionerHeuristic;
import org.preesm.algorithm.clustering.heuristics.VerticalClusteringHeuristic;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/***
 * @author jmorin
 * @author rcazoulat
 **/

@PreesmTask(id = "clustering.generic", name = "GenericClustering",

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
          description = "Output scenario, modified by heuristics") },

    description = "Workflow task responsible for clustering actors."

)
public class ClusteringTask extends AbstractTaskImplementation {
  static final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting inputs
    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    // Getting parameters
    final boolean CLUSTERIZE = "true".equalsIgnoreCase(parameters.get("clusterize"));
    final boolean verbose = "true".equalsIgnoreCase(parameters.get("verbose"));
    List<PiGraph> clustersList = new LinkedList<>();

    if (CLUSTERIZE) {

      // Getting heuristic names. Names are always lowered, to avoid stupid bugs.
      final String verticalHeuristicName = parameters.getOrDefault("vertical heuristic", "").toLowerCase();
      final String horizontalHeuristicName = parameters.getOrDefault("horizontal heuristic", "").toLowerCase();
      final String partitionerName = parameters.getOrDefault("partitioner", "").toLowerCase();

      // ---------------------------------------------------------------------------------------------- //
      // Vertical clusters
      // ---------------------------------------------------------------------------------------------- //
      // It will modify the algorithm graph

      if (verbose) {
        PreesmLogger.getLogger().info("Clustering : building vertical clusters...");
      }

      // Vertical clusterization is **optional**. If there is no heuristic, it will just flatten the graph
      final VerticalClusteringHeuristic vertiHeuristic = (VerticalClusteringHeuristic) getHeuristic(
          verticalHeuristicName);
      if (vertiHeuristic != null) {
        vertiHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);
      }

      ClusterBuilder.buildVerticalClusters(null, algorithm, vertiHeuristic);

      if (verbose) {
        PreesmLogger.getLogger().info("Clustering : building vertical clusters done.");
      }

      // ---------------------------------------------------------------------------------------------- //
      // Horizontal clusters
      // ---------------------------------------------------------------------------------------------- //
      // It will modify the algorithm graph, in addition to returning the list of all created clusters

      if (verbose) {
        PreesmLogger.getLogger().info("Clustering : building horizontal clusters...");
      }

      // First, retrieving the heuristic and initializes its parameters
      HorizontalClusteringHeuristic horizHeuristic = (HorizontalClusteringHeuristic) getHeuristic(
          horizontalHeuristicName);

      // horizontal heuristic can't be null, so we set the one by default
      if (horizHeuristic == null) {
        horizHeuristic = new URCHeuristic();
      }

      horizHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);

      // Then, retrieving partitioner heuristic, if there is one (it is not mandatory)
      final PartitionerHeuristic partitioner = getPartitioner(partitionerName);
      if (partitioner != null) {
        partitioner.initHeuristicParameters(algorithm, scenario, architecture, parameters);
      }

      // Creating horizontal clusters, according to horizontal clusterization heuristic, and partitioner heuristic
      clustersList = ClusterBuilder.buildHorizontalClusters(algorithm, scenario, architecture, horizHeuristic,
          partitioner, verbose);

      if (verbose) {
        PreesmLogger.getLogger().info("Clustering : building horizontal clusters done.");
      }

    } else {
      PreesmLogger.getLogger().info("Clustering was not activated.");
    }

    // Building outputs list
    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, clustersList);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    return outputs;
  }

  /**
   * Returns the merging heuristic corresponding to heuristicName. Expand at will !
   *
   * @param heuristicName
   *          the name
   *
   * @return a MergingHeuristic implementation class
   */
  public static ClusteringHeuristic getHeuristic(String heuristicName) {
    return switch (heuristicName) {
      case null -> null;
      case "srv" -> new SRVHeuristic();
      case "urc" -> new URCHeuristic();
      case "heterogeneous" -> new SimpleHeteroArchClusteringHeuristic();
      default -> null;
    };
  }

  public static PartitionerHeuristic getPartitioner(String heuristicName) {
    return switch (heuristicName) {
      case null -> null;
      case "simple" -> new SimplePartitionerHeuristic();
      default -> null;
    };
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return null;
  }

}
