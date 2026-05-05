package org.preesm.algorithm.clustering.clusteringheuristics;

import org.preesm.model.pisdf.PiGraph;

public abstract class PartitionerHeuristic extends Heuristic {
  /***
   * This method will modify ports rates between the actors of the subgraph, and the rates of the subgraph in the top
   * graph.
   *
   * @param topgraph
   *          the top graph
   * @param cluster
   *          the sub graph
   */
  public abstract void balanceFirings(PiGraph topgraph, PiGraph cluster);
}
