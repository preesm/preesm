package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.preesm.model.pisdf.AbstractActor;

/**
 * This class provides helper functions to compute the breaking fifo in a cycle.
 * 
 * @author ahonorat
 *
 */
public class FifoBreakingCycleDetector {

  /**
   * Indicate whether or not a node in the cycle has extra input/output.
   *
   * @author ahonorat
   */
  protected enum CycleNodeType {
    NONE('n'), ENTRY('i'), EXIT('o'), BOTH('b');

    public final char abbr;

    CycleNodeType(final char abbr) {
      this.abbr = abbr;
    }

  }

  /**
   * This method needs a small paper to be explicated. Do not try to modify it.
   *
   * @param cycle
   *          Cycle to consider.
   * @param actorsWithEntries
   *          Actors in the cycle having also inputs from actors not being the cycle.
   * @param actorsWithExits
   *          Actors in the cycle having also outputs to actors not being the cycle.
   * @return index in the cycle of the actor after whom the cycle can be broken.
   */
  public static int retrieveBreakingFifoWhenDifficult(final List<AbstractActor> cycle,
      final List<AbstractActor> actorsWithEntries, final List<AbstractActor> actorsWithExits) {
    final List<CycleNodeType> types = new ArrayList<>();
    final Iterator<AbstractActor> itEntries = actorsWithEntries.iterator();
    final Iterator<AbstractActor> itExits = actorsWithExits.iterator();
    AbstractActor nextEntry = itEntries.hasNext() ? itEntries.next() : null;
    AbstractActor nextExit = itExits.hasNext() ? itExits.next() : null;
    int nbBoth = 0;
    final StringBuilder sb = new StringBuilder();
    for (final AbstractActor aa : cycle) {
      if ((aa == nextEntry) && (aa == nextExit)) {
        types.add(CycleNodeType.BOTH);
        nextEntry = itEntries.hasNext() ? itEntries.next() : null;
        nextExit = itExits.hasNext() ? itExits.next() : null;
        nbBoth += 1;
        sb.append(CycleNodeType.BOTH.abbr);
      } else if (aa == nextEntry) {
        types.add(CycleNodeType.ENTRY);
        nextEntry = itEntries.hasNext() ? itEntries.next() : null;
        sb.append(CycleNodeType.ENTRY.abbr);
      } else if (aa == nextExit) {
        types.add(CycleNodeType.EXIT);
        nextExit = itExits.hasNext() ? itExits.next() : null;
        sb.append(CycleNodeType.EXIT.abbr);
      } else {
        types.add(CycleNodeType.NONE);
      }
    }
    // hazardous case with multiple I/O
    if (nbBoth > 1) {
      return types.lastIndexOf(CycleNodeType.BOTH) - 1;
    }
    // test correct cases
    final String str = sb.toString();
    if (str.isEmpty()) {
      return 0;
    }
    // this uses the enum abbr without telling it!
    if (Pattern.matches("i*b?i*", str) || Pattern.matches("o*b?o*", str)) {
      if (nbBoth == 1) {
        final int index = types.indexOf(CycleNodeType.BOTH);
        return index == 0 ? types.size() - 1 : index - 1;
      }
    } else if (Pattern.matches("i*b?o+i*", str)) {
      return types.lastIndexOf(CycleNodeType.EXIT);
    } else if (Pattern.matches("o*i+b?o*", str)) {
      final int index = types.indexOf(CycleNodeType.ENTRY);
      return index == 0 ? types.size() - 1 : index - 1;
    }
    // for all other hazardous cases
    final int index = types.indexOf(CycleNodeType.ENTRY);
    if (index >= 0) {
      return index == 0 ? types.size() - 1 : index - 1;
    }
    return -1;
  }

}
