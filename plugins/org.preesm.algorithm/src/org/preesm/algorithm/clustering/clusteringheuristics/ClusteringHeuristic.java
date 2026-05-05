package org.preesm.algorithm.clustering.clusteringheuristics;

/**
*
* @author jamorin
*
*/

/***
 * An interface for all merging heuristics used in the clustering phase. The merging test can be different for
 * predecessor actors than for successor actors. See the assess method's description for details.
 */
public abstract class ClusteringHeuristic extends Heuristic {

  /***
   * Heuristic prefix, to know what name will be given to clusters. For example, if getPrefix returns "cluster", the
   * cluster will be named "cluster_A", if seed of cluster is named A.
   *
   * @return the prefix for the cluster name. By default, it is "cluster"
   */
  public String getPrefix() {
    return "cluster";
  }

}
