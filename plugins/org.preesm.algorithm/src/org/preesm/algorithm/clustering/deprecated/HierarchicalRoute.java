package org.preesm.algorithm.clustering.deprecated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.PiGraph;

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
}
