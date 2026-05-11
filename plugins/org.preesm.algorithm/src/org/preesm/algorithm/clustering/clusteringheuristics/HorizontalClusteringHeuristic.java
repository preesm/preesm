package org.preesm.algorithm.clustering.clusteringheuristics;

import java.util.Set;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.slam.Component;

public abstract class HorizontalClusteringHeuristic extends ClusteringHeuristic {

  /***
   * Optional method. If there is hierarchy in the top graph, an entire subgraph could already be considered a cluster.
   * Using this method can allow a faster clusterization compared to using assessSeedable and assessMergeable, using a
   * more generalist heuristic.
   *
   * @param graph
   *          the subgraph to inspect
   * @return true if the graph has been clusterized, false otherwise.
   */
  public boolean assessGraph(PiGraph graph) {
    return false;
  }

  /***
   * Assesses whether actor can be merged with the cluster started from the Actor seed.
   *
   * @param seed
   *          the cluster's seed actor
   * @param actor
   *          the Actor to be evaluated for merging
   * @return true or false
   */
  public abstract boolean assessMergeable(AbstractActor seed, AbstractActor actor);

  /***
   * Assesses whether actor can be used as a seed to cluster its neighboring actors.
   *
   * @param actor
   *          the actor
   * @return true or false
   */
  public abstract boolean assesSeedable(AbstractActor actor);

  /***
   * This method can be overrided, and return true if the created cluster is valid, false otherwise. By default, this
   * method return true, as a created cluster is always identified as valid.
   *
   * @param cluster
   *          the cluster to be checked
   * @return the validity of the cluster
   */
  public boolean validateCluster(Set<AbstractActor> cluster) {
    return true;
  }

  /***
   * Picks which of the Components (CPU, GPU, FPGA...) the seed can be mapped to should be used as reference for
   * clustering. NOT the precise instance ! Not necessary for all heuristics, feel free to use it or not. By default,
   * will return the first component of the cluster.
   *
   * @param cluster
   *          all actors of the cluster
   * @return the chosen component type
   */
  public Component pickClusteringComponent(PiGraph cluster) {
    return this.scenario.getPossibleMappings(cluster).getFirst().getComponent();

  }
}
