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
