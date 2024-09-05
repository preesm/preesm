package org.preesm.algorithm.clustering.scape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import org.preesm.algorithm.clustering.partitioner.ScapeMode;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

import org.preesm.model.pisdf.util.ClusteringPatternSeekerLoop;

/**
 * This class arranges the hierarchical levels for efficient routing. Level 0 is the top n++ for the subgraph below.
 */

public class HierarchicalRoute {
  private HierarchicalRoute() {

  }

  /**
   * Order the hierarchical subgraph in order to compute cluster in the bottom up way
   */
  public static Map<Long, List<PiGraph>> fillHierarchicalStructure(PiGraph graph) {
    final Map<Long, List<PiGraph>> hierarchicalLevelOrdered = new HashMap<>();
    for (final PiGraph g : graph.getAllChildrenGraphs()) {
      Long count = 0L;
      PiGraph tempg = g;
      while (tempg.getContainingPiGraph() != null) {
        tempg = tempg.getContainingPiGraph();
        count++;
      }
      final List<PiGraph> list = new ArrayList<>();
      list.add(g);
      if (hierarchicalLevelOrdered.get(count) == null) {
        hierarchicalLevelOrdered.put(count, list);
      } else {
        hierarchicalLevelOrdered.get(count).add(g);
      }

    }
    final List<PiGraph> list = new ArrayList<>();
    list.add(graph);
    hierarchicalLevelOrdered.put(0L, list);
    return hierarchicalLevelOrdered;
  }

  /**
   * Compute the hierarchical level to be coarsely clustered and identify hierarchical level to be cleverly clustered.
   *
   * @return levelBound level bound
   */

  public static Long computeClusterableLevel(PiGraph graph, Scenario scenario, int mode, int levelNumber,
      Map<Long, List<PiGraph>> hierarchicalLevelOrdered) {
    final Long totalLevelNumber = (long) (hierarchicalLevelOrdered.size() - 1);
    if (mode == 0 || mode == 1) {
      return (long) levelNumber;

  public static Long computeClusterableLevel(PiGraph graph, ScapeMode scapeMode, int levelNumber,

      Map<Long, List<PiGraph>> hierarchicalLevelOrdered) {

    // TODO Debug this function

    final Long totalLevelNumber = (long) (hierarchicalLevelOrdered.size() - 1);

    if (scapeMode == ScapeMode.DATA || scapeMode == ScapeMode.DATA_PIPELINE) {
      final String message = "Level configuration: 0-> full clustering, " + (totalLevelNumber + 1)
          + "-> nothing, between -> cluster partially";
      PreesmLogger.getLogger().log(Level.INFO, () -> message);
      return (long) levelNumber;
    }

    Long count = totalLevelNumber;
    // detect the highest delay
    for (final Fifo fd : graph.getFifosWithDelay()) {
      // detect loop --> no pipeline and contains hierarchical graph
      final List<AbstractActor> graphSingleLOOPs = new ClusteringPatternSeekerLoop(graph).singleLocalseek();
      if (!graphSingleLOOPs.isEmpty() && graphSingleLOOPs.stream().anyMatch(PiGraph.class::isInstance)) {
        // compute high
        for (Long i = 0L; i < totalLevelNumber; i++) {
          if (hierarchicalLevelOrdered.get(i).contains(fd.getContainingPiGraph())) {
            count = Math.min(count, i);
          }
        }

      }
    }

    return count + 1;
  }

}
