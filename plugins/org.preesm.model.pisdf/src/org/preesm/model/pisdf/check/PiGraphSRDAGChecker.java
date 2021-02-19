package org.preesm.model.pisdf.check;

import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.FifoCycleDetector;

/**
 * This class provides a method to check that a PiGraph is indeed a SRDAG (which means: no hierarchy, repetition vector
 * being one, no cycles, no delays).
 * 
 * @author ahonorat
 */
public class PiGraphSRDAGChecker {

  private PiGraphSRDAGChecker() {
    // do nothing, avoids to instantiate this class
  }

  /**
   * Checks if a given PiGraph is a SRDAG (no hierarchy, single-rate, no delay, no cycle).
   * 
   * @param piGraph
   *          the PiGraph to check
   * @return true if SRADG, false otherwise
   */
  public static boolean isPiGraphSRADG(final PiGraph piGraph) {

    // check hierarchy
    if (!piGraph.getChildrenGraphs().isEmpty()) {
      return false;
    }
    // check single-rate and delays
    final boolean isSingleRate = piGraph.getAllFifos().stream().allMatch(f -> {
      final long rateOut = f.getSourcePort().getExpression().evaluate();
      final long rateIn = f.getTargetPort().getExpression().evaluate();
      return (rateOut == rateIn) && f.getDelay() == null;
    });
    if (!isSingleRate) {
      return false;
    }
    // check cycles (stop on first one)
    FifoCycleDetector fcd = new FifoCycleDetector(true);
    fcd.doSwitch(piGraph);
    return !fcd.cyclesDetected();
  }

}
