package org.preesm.algorithm.synthesis.schedule;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;

/**
 *
 * @author anmorvan
 *
 */
public abstract class ScheduleOrderedVisitor extends ScheduleSwitch<Boolean> {

  private final List<AbstractActor>               visited;
  private final Map<AbstractActor, ActorSchedule> actorToScheduleMap;

  public ScheduleOrderedVisitor(final Map<AbstractActor, ActorSchedule> actorToScheduleMap) {
    this.actorToScheduleMap = actorToScheduleMap;
    this.visited = new ArrayList<>();
  }

  private final Deque<Schedule> scheduleStack = new LinkedList<>();

  @Override
  public Boolean caseHierarchicalSchedule(final HierarchicalSchedule object) {
    this.scheduleStack.push(object);
    for (final Schedule child : object.getChildren()) {
      this.doSwitch(child);
    }
    this.scheduleStack.pop();
    return true;
  }

  @Override
  public Boolean caseActorSchedule(final ActorSchedule object) {
    this.scheduleStack.push(object);
    for (final AbstractActor actor : object.getActorList()) {
      this.innerVisit(actor);
    }
    this.scheduleStack.pop();
    return true;
  }

  private final void innerVisit(final Schedule schedule) {
    if (schedule == this.scheduleStack.peek()) {
      // error mon
      throw new PreesmRuntimeException("guru meditation");
    }
    final Pair<Schedule,
        Schedule> lca = ScheduleUtil.findLowestCommonAncestorChildren(schedule, this.scheduleStack.peek());
    final Schedule right = lca.getRight();
    final HierarchicalSchedule parent = right.getParent();
    if (parent instanceof ParallelSchedule) {
      doSwitch(schedule);
    } else {
      doSwitch(right);
    }

  }

  /**
   */
  private void innerVisit(final AbstractActor actor) {
    if (this.visited.contains(actor)) {
      // skip
    } else {
      // make sure predecessors have been visited
      final List<AbstractActor> directPredecessorsOf = PiSDFTopologyHelper.getDirectPredecessorsOf(actor);
      for (final AbstractActor v : directPredecessorsOf) {
        if (!this.visited.contains(v)) {
          final ActorSchedule actorSchedule = this.actorToScheduleMap.get(v);
          // or visit them
          println("Stuck on [%s]: missing exec actor [%s] from schedule [%s]", actor.getName(), v.getName(),
              actorSchedule.shortPrint());
          innerVisit(actorSchedule);
        }
      }
      visit(actor);
      this.visited.add(actor);
    }
  }

  /**
   *
   */
  public abstract void visit(final AbstractActor actor);

  private static final void println(String format, String... args) {
    System.out.println(String.format(format, args));
  }
}
