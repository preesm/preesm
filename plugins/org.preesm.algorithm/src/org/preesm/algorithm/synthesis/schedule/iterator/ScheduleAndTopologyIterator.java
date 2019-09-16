package org.preesm.algorithm.synthesis.schedule.iterator;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.model.pisdf.AbstractActor;

/**
 *
 *
 *
 * @author anmorvan
 *
 */
public class ScheduleAndTopologyIterator extends ScheduleIterator {

  public ScheduleAndTopologyIterator(final Schedule schedule) {
    super(schedule);
  }

  @Override
  public List<AbstractActor> createOrder(final Schedule schedule) {
    final TopologyScheduleSwitch tss = new TopologyScheduleSwitch();
    tss.doSwitch(schedule);
    return tss.visitedActors;
  }

  /**
   *
   * @author anmorvan
   *
   */
  private static class TopologyScheduleSwitch extends ScheduleSwitch<Boolean> {

    List<AbstractActor> visitedActors = new ArrayList<>();
    List<AbstractActor> toVisitActors = new ArrayList<>();

    private Deque<Schedule> scheduleStack = new LinkedList<>();

    @Override
    public Boolean caseActorSchedule(final ActorSchedule object) {
      scheduleStack.push(object);
      for (final AbstractActor a : object.getActorList()) {
        caseAbstractActor(a);
      }
      scheduleStack.pop();
      return true;
    }

    @Override
    public Boolean caseHierarchicalSchedule(HierarchicalSchedule object) {
      scheduleStack.push(object);
      for (final Schedule a : object.getScheduleTree()) {
        doSwitch(a);
      }
      scheduleStack.pop();

      if (scheduleStack.isEmpty()) {
        // if visit is ending
        while (!toVisitActors.isEmpty()) {
          for (AbstractActor a : toVisitActors) {
            caseAbstractActor(a);
          }
          toVisitActors.removeAll(visitedActors);
        }
      }
      return true;
    }

    @Override
    public Boolean caseAbstractActor(final AbstractActor object) {
      final List<AbstractActor> dependenciesSource = object.getDataInputPorts().stream()
          .map(p -> p.getFifo().getSourcePort().getContainingActor()).collect(Collectors.toList());
      if (visitedActors.containsAll(dependenciesSource)) {
        visitedActors.add(object);
      } else {
        if (!toVisitActors.contains(object)) {
          toVisitActors.add(object);
        }
      }
      return true;
    }
  }

}
