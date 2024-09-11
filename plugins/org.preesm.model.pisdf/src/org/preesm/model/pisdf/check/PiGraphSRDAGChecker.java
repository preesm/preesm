/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
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
      final long rateOut = f.getSourcePort().getExpression().evaluateAsLong();
      final long rateIn = f.getTargetPort().getExpression().evaluateAsLong();
      return (rateOut == rateIn) && f.getDelay() == null;
    });
    if (!isSingleRate) {
      return false;
    }
    // check cycles (stop on first one)
    final FifoCycleDetector fcd = new FifoCycleDetector(true);
    fcd.doSwitch(piGraph);
    return !fcd.cyclesDetected();
  }

}
