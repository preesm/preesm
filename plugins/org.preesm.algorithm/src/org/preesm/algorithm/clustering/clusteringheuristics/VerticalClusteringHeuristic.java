package org.preesm.algorithm.clustering.clusteringheuristics;

import org.preesm.model.pisdf.PiGraph;

public abstract class VerticalClusteringHeuristic extends ClusteringHeuristic {

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
  public void assessFlattening(PiGraph topGraph, PiGraph subGraph) {

  }
}
