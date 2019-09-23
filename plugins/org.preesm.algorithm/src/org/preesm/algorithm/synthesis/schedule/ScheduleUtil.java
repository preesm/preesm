package org.preesm.algorithm.synthesis.schedule;

import java.util.Comparator;
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
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;

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
   * Instanciate a Comparator of AbstractActor that respects both topological order and schedule order;
   */
  public static final Comparator<AbstractActor> getScheduleComparator(final Schedule schedule) {
    return new Comparator<AbstractActor>() {

      private final Map<AbstractActor, ActorSchedule> actorToSchedule = ScheduleUtil.actorToScheduleMap(schedule);

      @Override
      public int compare(final AbstractActor leftActor, final AbstractActor rightActor) {
        if (leftActor.equals(rightActor)) {
          return 0;
        }
        if (actorToSchedule.containsKey(leftActor) && actorToSchedule.containsKey(rightActor)) {
          final int res;
          final ActorSchedule leftActorSchedule = actorToSchedule.get(leftActor);
          final ActorSchedule rightActorSchedule = actorToSchedule.get(rightActor);
          if (leftActorSchedule == rightActorSchedule) {
            res = leftActorSchedule.getActorList().indexOf(leftActor)
                - rightActorSchedule.getActorList().indexOf(rightActor);
          } else {
            // find common schedule ancestor (there is one)
            // check order on that ancestor
            final Pair<Schedule,
                Schedule> lca = ScheduleUtil.findLowestCommonAncestorChildren(leftActorSchedule, rightActorSchedule);
            final Schedule left = lca.getLeft();
            final Schedule right = lca.getRight();

            final HierarchicalSchedule parent = left.getParent();
            if (parent instanceof SequentialSchedule) {
              res = parent.getScheduleTree().indexOf(left) - parent.getScheduleTree().indexOf(right);
            } else if (parent instanceof ParallelSchedule) {
              // check if a path exists
              final boolean isPred = PiSDFTopologyHelper.isPredecessor(leftActor, rightActor);
              if (isPred) {
                res = 1;
              } else {
                final boolean isSucc = PiSDFTopologyHelper.isSuccessor(leftActor, rightActor);
                if (isSucc) {
                  res = -1;
                } else {
                  res = parent.getScheduleTree().indexOf(left) - parent.getScheduleTree().indexOf(right);
                }
              }
            } else {
              throw new UnsupportedOperationException();
            }
          }
          return res;
        } else {
          throw new PreesmRuntimeException();
        }
      }
    };
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
