/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.util.FifoBreakingCycleDetector.CycleNodeType;

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

    public final String abbr;

    CycleNodeType(final char abbr) {
      this.abbr = String.valueOf(abbr);
    }

  }

  static final String FORMATTED_LOOP_MIDDLE_BOTH1 = String.format("%s*%s?%s*", CycleNodeType.ENTRY.abbr,
      CycleNodeType.BOTH.abbr, CycleNodeType.ENTRY.abbr);
  static final String FORMATTED_LOOP_MIDDLE_BOTH2 = String.format("%s*%s?%s*", CycleNodeType.EXIT.abbr,
      CycleNodeType.BOTH.abbr, CycleNodeType.EXIT.abbr);

  static final String FORMATTED_LOOP_PERM1 = String.format("%s*%s?%s+%s*", CycleNodeType.ENTRY.abbr,
      CycleNodeType.BOTH.abbr, CycleNodeType.EXIT.abbr, CycleNodeType.ENTRY.abbr);
  static final String FORMATTED_LOOP_PERM2 = String.format("%s*%s+%s?%s*", CycleNodeType.EXIT.abbr,
      CycleNodeType.ENTRY.abbr, CycleNodeType.BOTH.abbr, CycleNodeType.EXIT.abbr);

  /**
   * This method needs a small paper to be explicated. Do not try to modify it.
   *
   * @param cycle
   *          Cycle to consider.
   * @param cyclesFifos
   *          List of Fifos between these nodes (of same size as {@code cycle}).
   * @return index in the cycle of the actor after whom the cycle can be broken.
   */
  public static int retrieveBreakingFifoWhenDifficult(final List<AbstractActor> cycle,
      final List<List<Fifo>> cyclesFifos) {
    final List<AbstractActor> actorsWithEntries = new ArrayList<>();
    final List<AbstractActor> actorsWithExits = new ArrayList<>();

    FifoBreakingCycleDetector.computeExitAndEntries(cycle, cyclesFifos, actorsWithEntries, actorsWithExits);
    return retrieveBreakingFifoWhenDifficult(cycle, actorsWithEntries, actorsWithExits);

  }

  /**
   * This method needs a small paper to be explicated. Do not try to modify it.
   *
   * @param cycle
   *          Cycle to consider.
   * @param actorsWithEntries
   *          Actors in the cycle having also inputs from actors not being the cycle. In the same order as in the cycle.
   * @param actorsWithExits
   *          Actors in the cycle having also outputs to actors not being the cycle. In the same order as in the cycle.
   * @return index in the cycle of the actor after whom the cycle can be broken.
   */
  public static int retrieveBreakingFifoWhenDifficult(final List<AbstractActor> cycle,
      final List<AbstractActor> actorsWithEntries, final List<AbstractActor> actorsWithExits) {
    final List<CycleNodeType> types = new ArrayList<>();
    final StringBuilder sb = new StringBuilder();
    final int nbBoth = computeCycleString(cycle, actorsWithEntries, actorsWithExits, types, sb);

    // hazardous case with multiple I/O
    if (nbBoth > 1) {
      return types.lastIndexOf(CycleNodeType.BOTH) - 1;
    }
    // test correct cases
    final String str = sb.toString();
    if (str.isEmpty()) {
      return 0;
    }
    if (Pattern.matches(FORMATTED_LOOP_MIDDLE_BOTH1, str) || Pattern.matches(FORMATTED_LOOP_MIDDLE_BOTH2, str)) {
      if (nbBoth == 1) {
        final int index = types.indexOf(CycleNodeType.BOTH);
        return index == 0 ? types.size() - 1 : index - 1;
      }
    } else if (Pattern.matches(FORMATTED_LOOP_PERM1, str)) {
      return types.lastIndexOf(CycleNodeType.EXIT);
    } else if (Pattern.matches(FORMATTED_LOOP_PERM2, str)) {
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

  /**
   * This method needs a small paper to be explicated. Do not try to modify it.
   *
   * @param cycle
   *          Cycle to consider.
   * @param actorsWithEntries
   *          Actors in the cycle having also inputs from actors not being the cycle. In the same order as in the cycle.
   * @param actorsWithExits
   *          Actors in the cycle having also outputs to actors not being the cycle. In the same order as in the cycle.
   * @param types
   *          List of actor types according to {@link CycleNodeType}, in the same order as actors in the cycle. To be
   *          set, must be initialized and empty.
   * @param sb
   *          String of actors built thanks the type list, in the same order as actors in the cycle, but ommiting
   *          {@link CycleNodeType.NONE}. To be set, must be initialized and empty.
   * @return number of actors being both an entry and an exit.
   */
  public static int computeCycleString(final List<AbstractActor> cycle, final List<AbstractActor> actorsWithEntries,
      final List<AbstractActor> actorsWithExits, final List<CycleNodeType> types, final StringBuilder sb) {
    final Iterator<AbstractActor> itEntries = actorsWithEntries.iterator();
    final Iterator<AbstractActor> itExits = actorsWithExits.iterator();
    AbstractActor nextEntry = itEntries.hasNext() ? itEntries.next() : null;
    AbstractActor nextExit = itExits.hasNext() ? itExits.next() : null;
    int nbBoth = 0;
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
    return nbBoth;
  }

  /**
   * Compute entry and exit actor in a cycle, in the same order as in the cycle (if appearing).
   *
   * @param cycle
   *          List of nodes forming a cycle.
   * @param cyclesFifos
   *          List of Fifos between these nodes (of same size as {@code cycle}).
   * @param actorsWithEntries
   *          Modified list with entry actors, in the same order as {@code cycle}. To be set, must be initialized and
   *          empty.
   * @param actorsWithExits
   *          Modified list with exit actors, in the same order as {@code cycle}. To be set, must be initialized and
   *          empty.
   */
  public static void computeExitAndEntries(final List<AbstractActor> cycle, final List<List<Fifo>> cyclesFifos,
      final List<AbstractActor> actorsWithEntries, final List<AbstractActor> actorsWithExits) {

    final Iterator<AbstractActor> it = cycle.iterator();
    final Iterator<List<Fifo>> itL = cyclesFifos.iterator();
    AbstractActor current = it.next();
    while (it.hasNext()) {
      final AbstractActor next = it.next();
      final List<Fifo> fifos = itL.next();
      final int nbCommonPorts = fifos.size();
      if (current.getDataOutputPorts().size() > nbCommonPorts) {
        actorsWithExits.add(current);
      }
      if (next.getDataInputPorts().size() > nbCommonPorts) {
        actorsWithEntries.add(next);
      }
      current = next;
    }
    final List<Fifo> fifos = itL.next();
    final int nbCommonPorts = fifos.size();
    if (current.getDataOutputPorts().size() > nbCommonPorts) {
      actorsWithExits.add(current);
    }
    final AbstractActor root = cycle.get(0);
    if (root.getDataInputPorts().size() > nbCommonPorts) {
      actorsWithEntries.add(0, root);
    }
  }

}
