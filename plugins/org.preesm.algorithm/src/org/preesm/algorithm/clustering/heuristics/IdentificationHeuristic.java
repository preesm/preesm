package org.preesm.algorithm.clustering.heuristics;

/**
 * An interface class for all merging @{link Heuristic heuristics} used in the clustering phase. It is the parent class
 * of {@link VerticalHeuristic} and {@link HorizontalHeuristic}.
 *
 * @author rcazoulat
 */
public interface IdentificationHeuristic extends Heuristic {

  /***
   * Heuristic prefix, to know what name will be given to clusters. For example, if getPrefix returns "cluster", the
   * cluster will be named "cluster_A", if seed of cluster is named A.
   *
   * @return the prefix for the cluster name.
   */
  public String getPrefix();

}
