package org.preesm.algorithm.clustering;

import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;

/**
*
* @author jamorin
*
*/

/***
 * An interface for all merging heuristics used in the clustering phase. The merging test can be different for
 * predecessor actors than for successor actors. See the assess method's description for details.
 */
public abstract class MergingHeuristic {

  public static final int predecessor = 0;
  public static final int successor   = 1;

  /***
   * Initializes the parameters of the heuristic. Can be skipped.
   *
   * @param graph
   *          the subgraph to inspect
   * @param scenario
   *          the scenario that links to the whole PiSDF graph, the S-LAM graph, the constraints...
   * @param params
   *          the output of the method, that will be pass to the other methods to build the clusters
   */
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Map<String, Object> params) {
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
   * @param scenario
   *          the scenario
   * @param params
   *          any parameter the heuristic requires
   */
  public void assessFlattening(PiGraph topGraph, PiGraph subGraph, Scenario scenario, Map<String, Object> params) {

  }

  /***
   * Optional method. If there is hierarchy in the top graph, an entire subgraph could already be considered a cluster.
   * Using this method can allow a faster clusterization compared to using assessSeedable and assessMergeable, using a
   * more generalist heuristic.
   *
   * @param graph
   *          the subgraph to inspect
   * @param params
   *          any parameter the heuristic requires
   * @return true if the graph has been clusterized, false otherwise.
   */
  public boolean assessGraph(PiGraph graph, Map<String, Object> params) {
    return false;
  }

  /***
   * Assesses whether actor can be merged with the cluster started from the Actor seed.
   *
   * @param seed
   *          the cluster's seed actor
   * @param actor
   *          the Actor to be evaluated for merging
   * @param params
   *          any parameter the heuristic requires
   * @return true or false
   */
  public abstract boolean assessMergeable(AbstractActor seed, AbstractActor actor, Map<String, Object> params);

  /***
   * Assesses whether actor can be used as a seed to cluster its neighboring actors.
   *
   * @param actor
   *          the actor
   * @param params
   *          any parameter necessary for the heuristic
   * @return true or false
   */
  public abstract boolean assesSeedable(AbstractActor actor, Map<String, Object> params);

  /***
   * Picks which of the Components (CPU, GPU, FPGA...) the seed can be mapped to should be used as reference for
   * clustering. NOT the precise instance ! Not necessary for all heuristics, feel free to use it or not.
   *
   * @param seed
   *          the seed
   * @param params
   *          any parameter the heuristic requires
   * @return the chosen component type
   */
  public Component pickClusteringComponent(AbstractActor seed, Map<String, Object> params) {
    return null;
  }

}
