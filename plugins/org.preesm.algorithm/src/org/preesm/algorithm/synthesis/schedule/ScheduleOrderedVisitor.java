package org.preesm.algorithm.synthesis.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
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

  private final List<Object>                      visited;
  private final Map<AbstractActor, ActorSchedule> actorToScheduleMap;

  public ScheduleOrderedVisitor(final Map<AbstractActor, ActorSchedule> actorToScheduleMap) {
    this.actorToScheduleMap = actorToScheduleMap;
    this.visited = new ArrayList<>();
  }

  private final Deque<Schedule> scheduleStack = new LinkedList<>();

  @Override
  public Boolean caseHierarchicalSchedule(final HierarchicalSchedule object) {
    if (visited.contains(object)) {
      // skip
    } else {
      this.scheduleStack.push(object);
      try {
        final EList<Schedule> children = object.getChildren();
        for (final Schedule child : children) {
          this.doSwitch(child);
        }
        visited.add(object);
      } finally {
        this.scheduleStack.pop();
      }
    }
    return true;
  }

  @Override
  public Boolean caseActorSchedule(final ActorSchedule object) {
    if (visited.contains(object)) {
      // skip
    } else {
      this.scheduleStack.push(object);
      try {
        final EList<AbstractActor> actorList = object.getActorList();
        for (final AbstractActor actor : actorList) {
          this.innerVisit(actor);
        }
        visited.add(object);
      } finally {
        this.scheduleStack.pop();
      }
    }
    return true;
  }

  private final void innerVisit(final Schedule schedule) {
    if (schedule == this.scheduleStack.peek() || this.scheduleStack.peek() == null || schedule == null) {
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

  private final Deque<AbstractActor> stuckStack = new LinkedList<>();

  /**
   *
   */
  private class VisitException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    final AbstractActor       actor;

    public VisitException(final AbstractActor actor) {
      this.actor = actor;
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
      if (actor instanceof CommunicationActor) {
        directPredecessorsOf.addAll(new CommunicationPrecedence().doSwitch(actor));
      }
      for (final AbstractActor v : directPredecessorsOf) {
        if (!this.visited.contains(v)) {
          final ActorSchedule actorSchedule = this.actorToScheduleMap.get(v);
          try {
            stuckStack.push(v);
            innerVisit(actorSchedule);
          } catch (VisitException e) {
            if (v == e.actor) {
              stuckStack.pop();
            } else {
              throw e;
            }
          }
        }
      }
      visit(actor);
      this.visited.add(actor);
      if (stuckStack.peek() == actor) {
        throw new VisitException(actor);
      }
    }
  }

  /**
   */
  private static class CommunicationPrecedence extends ScheduleSwitch<List<AbstractActor>> {
    @Override
    public List<AbstractActor> caseReceiveEndActor(ReceiveEndActor rea) {
      final ReceiveStartActor receiveStart = rea.getReceiveStart();
      return ListUtils.union(doSwitch(receiveStart), Arrays.asList(receiveStart));
    }

    @Override
    public List<AbstractActor> caseReceiveStartActor(ReceiveStartActor rsa) {
      final SendEndActor sourceSendEnd = rsa.getReceiveEnd().getSourceSendStart().getSendEnd();
      return ListUtils.union(doSwitch(sourceSendEnd), Arrays.asList(sourceSendEnd));
    }

    @Override
    public List<AbstractActor> caseSendEndActor(SendEndActor sea) {
      final SendStartActor sendStart = sea.getSendStart();
      return ListUtils.union(doSwitch(sendStart), Arrays.asList(sendStart));
    }

    @Override
    public List<AbstractActor> caseSendStartActor(SendStartActor ssa) {
      return Arrays.asList();
    }
  }

  /**
   *
   */
  public abstract void visit(final AbstractActor actor);

}
