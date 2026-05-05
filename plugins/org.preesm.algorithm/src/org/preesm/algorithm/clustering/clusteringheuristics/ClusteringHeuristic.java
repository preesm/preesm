package org.preesm.algorithm.clustering.clusteringheuristics;

import java.util.Map;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
*
* @author jamorin
*
*/

/***
 * An interface for all merging heuristics used in the clustering phase. The merging test can be different for
 * predecessor actors than for successor actors. See the assess method's description for details.
 */
public abstract class ClusteringHeuristic {

  // Map<String, Object> params; // --> powerful relic of ancient time ...
  PiGraph  graph    = null;
  Scenario scenario = null;
  Design   arch     = null;

  /***
   *
   * @param graph
   *          the main graph to cluster
   * @param scenario
   *          the scenario of the graph
   * @param arch
   *          the architecture S-LAM Graph
   * @param taskParameters
   *          all the parameters of the workflow task, that could give useful informations to the heuristic
   */
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    this.graph = graph;
    this.scenario = scenario;
    this.arch = arch;

  }

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
