package org.preesm.algorithm.clustering.heuristics;

import java.util.Set;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;

/**
 * Abstract {@link heuristic} made to horizontally clusterize a {@link PiGraph graph}. The implementation classes are
 * stored in the package clustering.identification
 *
 * @author rcazoulat
 */
public abstract class HorizontalHeuristic extends IdentificationHeuristic {

  /***
   * Assesses whether an {@link AbstractActor actor} can be merged with the cluster started from the seed. The seed is
   * not necessarily the real seed that started the clustering process, but an adjacent {@link AbstractActor actor} that
   * is the current seed.
   *
   * @param seed
   *          the current seed, first or current one.
   * @param actor
   *          the {@link AbstractActor actor} to be evaluated for merging
   * @return true if actor is decided to be part of the cluster, false otherwise.
   */
  public abstract boolean assessMergeable(AbstractActor seed, AbstractActor actor);

  /***
   * Assesses whether an {@link AbstractActor actor} can be used as a seed to cluster its (un)direct neighboring actors.
   *
   * @param actor
   *          the actor
   * @return true if it can be a seed, false otherwise.
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
}
