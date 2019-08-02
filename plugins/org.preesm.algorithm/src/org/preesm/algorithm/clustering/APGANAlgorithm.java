package org.preesm.algorithm.clustering;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;

/**
 * @author dgageot
 *
 */
public class APGANAlgorithm {

  /**
   * @param couples
   *          list of candidates
   * @param brv
   *          repetition vector
   * @return best candidate
   */
  public static Pair<AbstractActor, AbstractActor> getBestCouple(List<Pair<AbstractActor, AbstractActor>> couples,
      Map<AbstractVertex, Long> brv) {

    // Find the couple that maximize gcd
    long maxGcdRv = 0;
    Pair<AbstractActor, AbstractActor> maxCouple = null;
    for (Pair<AbstractActor, AbstractActor> l : couples) {
      // Compute RV gcd
      long tmpGcdRv = ArithmeticUtils.gcd(brv.get(l.getLeft()), brv.get(l.getRight()));
      if (tmpGcdRv > maxGcdRv) {
        maxGcdRv = tmpGcdRv;
        maxCouple = l;
      }
    }

    // If no couple has been found, throw an exception
    if (maxCouple == null) {
      throw new PreesmRuntimeException("APGANAlgorithm: Cannot find a couple to work on");
    }

    return new ImmutablePair<>(maxCouple.getLeft(), maxCouple.getRight());
  }

}
