package org.preesm.algorithm.clustering;

import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;

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
   * Assesses whether actor can be merged with the cluster started from the Actor seed.
   *
   * @param seed
   *          the cluster's seed actor
   * @param scenario
   *          the scenario containing the actor's mappings
   * @param refArchi
   *          the PE architecture on which the clustering is based
   * @param actor
   *          the Actor to be evaluated for merging
   * @param position
   *          whether the actor is a predecessor or successor to the seed
   * @return whether actor can be merged
   */
  abstract boolean assess(AbstractActor seed, AbstractActor actor, Map<String, Object> params);

}
