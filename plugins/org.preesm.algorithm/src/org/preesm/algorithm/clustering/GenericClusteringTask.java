package org.preesm.algorithm.clustering;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.clusteringheuristics.ClusteringHeuristic;
import org.preesm.algorithm.clustering.clusteringheuristics.HorizontalClusteringHeuristic;
import org.preesm.algorithm.clustering.clusteringheuristics.SRVHeuristic;
import org.preesm.algorithm.clustering.clusteringheuristics.URCHeuristic;
import org.preesm.algorithm.clustering.clusteringheuristics.VerticalClusteringHeuristic;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/***
 * @author jmorin
 **/

@PreesmTask(id = "clustering.generic", name = "GenericClustering",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class) },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, type = List.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },

    description = "Workflow task responsible for clustering hierarchical actors."

)
public class GenericClusteringTask extends AbstractTaskImplementation {
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
    String verticalHeuristicName = parameters.get("vertical heuristic");
    String horizontalHeuristicName = parameters.get("horizontal heuristic");

    if (verticalHeuristicName == null) {
      verticalHeuristicName = "";
    }
    if (horizontalHeuristicName == null) {
      horizontalHeuristicName = "";
    }

    List<PiGraph> clustersList = new LinkedList<>();

    if (CLUSTERIZE) {
      PreesmLogger.getLogger().info(" -- Clustering task --");

      // --------------------------------
      // -- Building vertical clusters --
      // It will modify the algorithm graph
      // Vertical clusterization is **optional**. If there is no heuristic, it will just flatten the graph
      final VerticalClusteringHeuristic vertiHeuristic = (VerticalClusteringHeuristic) getHeuristic(
          verticalHeuristicName);
      if (vertiHeuristic != null) {
        vertiHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);
      }

      GenericClusterBuilder.buildVerticalClusters(null, algorithm, vertiHeuristic);

      // ----------------------------------
      // -- Building horizontal clusters --
      // It will modify the algorithm graph, in addition to returning the list of all created clusters
      // first, retrieving the heuristic and initializes its parameters
      HorizontalClusteringHeuristic horizHeuristic = (HorizontalClusteringHeuristic) getHeuristic(
          horizontalHeuristicName);

      // horizontal heuristic can't be null, so we set the one by default
      if (horizHeuristic == null) {
        horizHeuristic = new URCHeuristic();
      }

      horizHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);

      clustersList = GenericClusterBuilder.buildHorizontalClusters(algorithm, scenario, architecture, horizHeuristic);

      // Mark all subgraph actors as mappable to the same components as the graph, but only them
      updateSubgraphsMappings(algorithm.getAllClusters(), scenario);

    } else {
      PreesmLogger.getLogger().info(" - Clustering was not activated");
    }

    // Building outputs list
    PreesmLogger.getLogger().info(" -- End Clustering task --");

    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, clustersList);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    return outputs;
  }

  /***
   * Sets all actors in each cluster to be mapped to a single PE type, the same as the cluster that contains it.
   *
   * @param clustersList
   *          the list of clusters
   * @param scenario
   *          the scenario
   */
  private void updateSubgraphsMappings(List<PiGraph> clustersList, Scenario scenario) {
    for (final PiGraph cluster : clustersList) {
      // a cluster is mapped to only 1 component
      final List<ComponentInstance> mappings = scenario.getPossibleMappings(cluster);

      for (final ComponentInstance mapping : mappings) {

        for (final AbstractActor actor : cluster.getActors()) {

          // Get all cores and their mapped actors. If the core is not the same as mapping and actor is mapped to it,
          // remove actor from the constraint
          scenario.getConstraints().getGroupConstraints().stream().filter(entry -> entry.getValue().contains(actor))
              .forEach(entry -> {
                if (entry.getKey() != mapping) { // if the actor is mapped to another core than mapping
                  // remove that mapping
                  entry.getValue().remove(actor);
                }
              });
        }
      }
    }
  }

  /**
   * Returns the merging heuristic corresponding to heuristicName. Expand at will !
   *
   * @param heuristicName
   *          the name
   *
   * @return a MergingHeuristic implementation class
   */
  private static ClusteringHeuristic getHeuristic(String heuristicName) {
    return switch (heuristicName) {
      case "SRV" -> new SRVHeuristic();
      case "URC" -> new URCHeuristic();
      default -> null;
    };
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return null;
  }

  @Override
  public String monitorMessage() {
    return null;
  }

}
