package org.preesm.algorithm.clustering.heuristics;

import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;

/**
 * This abstract class is made to create {@link Heuristic heuristics} that will balance a {@link PiGraph
 * cluster/subgraph} in its topg-raph, according to the number of available processing elements.
 *
 * @author rcazoulat
 */
public abstract class BalancingHeuristic extends Heuristic {

  /**
   * This method will modify ports rates between the {@link AbstractActor actors} of the top-graph, and the graph ports'
   * cluster's rates in the top graph.
   *
   * @param topgraph
   *          The top-graph (parent graph)
   * @param cluster
   *          The cluster
   * @param nPEs
   *          The number of available processing elements
   * @return The list containing at least the modified cluster, and potential newly created clusters
   */
  public abstract List<PiGraph> balanceFirings(PiGraph topgraph, PiGraph cluster, long nPEs);
}
