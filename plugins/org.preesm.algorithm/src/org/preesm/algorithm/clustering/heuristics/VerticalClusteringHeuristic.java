package org.preesm.algorithm.clustering.heuristics;

import java.util.Map;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public abstract class VerticalClusteringHeuristic extends ClusteringHeuristic {
  public enum FlatteningOrder {
    TOP_DOWN, BOTTOM_UP
  }

  FlatteningOrder flatteningOrder;

  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);
    flatteningOrder = setFlatteningOrder();
  }

  /***
   *
   */
  abstract FlatteningOrder setFlatteningOrder();

  /***
   *
   * @return the flattening order
   */
  public FlatteningOrder getFlatteningOrder() {
    return flatteningOrder;
  }

  /***
   * Optional method. For some heuristics, it should be interesting to flatten subgraphs, especially if there is a lot
   * of hierarchical levels, to accelerate horizontal clusterization or express parallelism to unlock new clusterization
   * opportunities. If assessFlattening is not override, the graph will just be flattened.
   *
   * @param topGraph
   *          the top graph
   * @param subGraph
   *          the current graph
   */
  public abstract void assessFlattening(PiGraph topGraph, PiGraph subGraph);
}
