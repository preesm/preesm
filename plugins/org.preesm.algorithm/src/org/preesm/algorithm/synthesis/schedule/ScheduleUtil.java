/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.synthesis.schedule;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 *
 * @author anmorvan
 *
 */
public class ScheduleUtil {

  private ScheduleUtil() {
    // forbid instantiation
  }

  /**
   * Given 2 different schedules of the same schedule tree, returns the lowest common ancestor. Ensures that
   * result.getLeft().getParent() == result.getRight().getParent().
   *
   * Throws exception if sched1 == sched2 or both schedules do not belong to the same schedule tree.
   */
  public static final Pair<Schedule, Schedule> findLowestCommonAncestorChildren(final Schedule sched1,
      final Schedule sched2) {
    if (sched1 == sched2) {
      throw new IllegalArgumentException("schedules should be different");
    }
    if (sched1.getRoot() != sched2.getRoot()) {
      throw new IllegalArgumentException("schedules do not belong to the same tree");
    }
    final List<Schedule> parentsOfSched1 = new LinkedList<>();
    parentsOfSched1.add(sched1);
    Schedule parent = sched1.getParent();
    while (parent != null) {
      parentsOfSched1.add(parent);
      parent = parent.getParent();
    }

    final List<Schedule> parentsOfSched2 = new LinkedList<>();
    parentsOfSched2.add(sched2);
    parent = sched2;
    while ((parent != null) && !parentsOfSched1.contains(parent)) {
      parent = parent.getParent();
      parentsOfSched2.add(parent);
    }
    if (parent != null) {
      final int indexOfSched1Parent = parentsOfSched1.indexOf(parent);
      final int indexOfSched2Parent = parentsOfSched2.indexOf(parent);
      return Pair.of(parentsOfSched1.get(Math.max(indexOfSched1Parent - 1, 0)),
          parentsOfSched2.get(Math.max(indexOfSched2Parent - 1, 0)));
    } else {
      throw new PreesmRuntimeException("guru meditation");
    }
  }
}
