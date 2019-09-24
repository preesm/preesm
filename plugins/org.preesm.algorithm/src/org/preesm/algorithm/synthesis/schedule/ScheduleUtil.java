package org.preesm.algorithm.synthesis.schedule;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;

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
   *
   */
  public static final Map<AbstractActor, ActorSchedule> actorToScheduleMap(final Schedule schedule) {
    final Set<ActorSchedule> actorSchedules = getActorSchedules(schedule);
    final Map<AbstractActor, ActorSchedule> res = new LinkedHashMap<>();
    actorSchedules.forEach(aSched -> aSched.getActorList().forEach(a -> res.put(a, aSched)));
    return res;
  }

  /**
   * Get all ActorSchedule children of the given Schedule. Order is not preserved.
   */
  public static final Set<ActorSchedule> getActorSchedules(final Schedule schedule) {
    return new ScheduleSwitch<Set<ActorSchedule>>() {
      @Override
      public Set<ActorSchedule> caseHierarchicalSchedule(final HierarchicalSchedule hSched) {
        return hSched.getScheduleTree().stream().map(this::doSwitch).flatMap(Set::stream)
            .collect(Collectors.toCollection(LinkedHashSet::new));
      }

      @Override
      public Set<ActorSchedule> caseActorSchedule(final ActorSchedule aSched) {
        final Set<ActorSchedule> res = new LinkedHashSet<>();
        res.add(aSched);
        return res;
      }
    }.doSwitch(schedule);
  }

  /**
   * Get all actors scheduled the given Schedule. Order is not preserved.
   */
  public static final Set<AbstractActor> getActors(final Schedule schedule) {
    return getActorSchedules(schedule).stream().map(ActorSchedule::getActorList).flatMap(List::stream)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * Given 2 different schedules of the same schedule tree, returns the lowest common ancestor. Ensures that
   * result.getLeft() == result.getRight().
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
