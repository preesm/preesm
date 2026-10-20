package org.preesm.algorithm.clustering.heuristics;

import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;

/**
 * This abstract class is made to create {@link Heuristic heuristics} that will balance a {@link PiGraph
 * cluster/subgraph} in its topg-raph, according to the number of available processing elements. The implementation
 * classes are stored in the package clustering.balancing
 *
 *
 * @author rcazoulat
 */
public abstract class BalancingHeuristic implements Heuristic {

  public void balance(PiGraph topGraph, long nPEs) {

    /** Explore sub-graphs (cluster included) */
    for (final PiGraph child : topGraph.getChildrenGraphs()) {
      balance(child, nPEs);
    }

    /** Balance clusters */
    for (final PiGraph cluster : topGraph.getChildrenGraphs().stream().filter(g -> g.isCluster()).toList()) {
      balanceCluster(topGraph, cluster, nPEs);
    }
  }

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
  protected abstract List<PiGraph> balanceCluster(PiGraph topgraph, PiGraph cluster, long nPEs);

  /**
   * Useful method to make logs if balancing is in verbose mode.
   *
   * @param actor
   *          Input actor
   * @param containingGraph
   *          parent graph
   * @param oldExprs
   *          old weights expressions. They will be compared to the new one, attached to the actors
   * @return The string log
   */
  public static String makeBalancingLog(final AbstractActor actor, final PiGraph containingGraph,
      final List<Long> oldExprs) {
    final StringBuilder log = new StringBuilder();
    if (oldExprs == null) {
      log.append("Cluster balancing: Creating " + actor.getName() + " in cluster " + containingGraph.getName());
    } else {
      log.append("Cluster balancing: Modifying " + actor.getName() + " in cluster " + containingGraph.getName());
    }
    return log.toString();
  }
}
