package org.preesm.algorithm.synthesis.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
 * Mixed ScheduleSwitch (iterating over schedules in the order of appearance in the ordered list), and Topology visitor
 * (iterating over actors in the topological order of the PiSDF SRDAG graph). Override {@link #visit(AbstractActor)} to
 * specify behavior.
 *
 * Exception will be thrown if the schedule and topology orders are not compatible.
 *
 * @author anmorvan
 */
public abstract class ScheduleOrderedVisitor extends ScheduleSwitch<Boolean> {

  /** Keeps track of visited actors (for the algorithm) and schedules (for speeding up) */
  private final List<Object>                visited            = new ArrayList<>();
  /** List of schedule under visit. Used to find out */
  private final Deque<Schedule>             scheduleStack      = new LinkedList<>();
  /** List of actors encountered during schedule visit for which topological dependency has not been visited. */
  private final Deque<AbstractActor>        stuckStack         = new LinkedList<>();
  /**
   * Associate for each actor the schedule that sets its order. Used to speedup query (instead of iterating over all
   * schedule tree every time we need to find the schedule). Initialized lazily upon need (see
   * {@link #innerVisitScheduleOf(AbstractActor)}.
   */
  private Map<AbstractActor, ActorSchedule> actorToScheduleMap = null;

  @Override
  public final Boolean caseHierarchicalSchedule(final HierarchicalSchedule object) {
    if (this.visited.contains(object)) {
      // skip
    } else {
      this.scheduleStack.push(object);
      try {
        final EList<Schedule> children = object.getChildren();
        for (final Schedule child : children) {
          this.doSwitch(child);
        }
        this.visited.add(object);
      } finally {
        this.scheduleStack.pop();
      }
    }
    return true;
  }

  @Override
  public final Boolean caseActorSchedule(final ActorSchedule object) {
    if (this.visited.contains(object)) {
      // skip
    } else {
      this.scheduleStack.push(object);
      try {
        final EList<AbstractActor> actorList = object.getActorList();
        for (final AbstractActor actor : actorList) {
          this.innerVisit(actor);
        }
        this.visited.add(object);
      } finally {
        this.scheduleStack.pop();
      }
    }
    return true;
  }

  @Override
  public final Boolean caseAbstractActor(final AbstractActor actor) {
    throw new PreesmRuntimeException("Visit should start with a schedule object");
  }

  private final void innerVisit(final Schedule schedule) {
    if ((this.scheduleStack.peek() == null) || (schedule == null)) {
      throw new NullPointerException();
    }
    if (schedule == this.scheduleStack.peek()) {
      // this method has been triggered because an actor depends on another one in the same schedule, but later in the
      // visit => schedule is not topology compliant
      throw new PreesmRuntimeException("Schedule is not topology compliant.");
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
   * Exception thrown when a topological dependency actor has been visited.
   */
  private static final class PredecessorFoundException extends RuntimeException {
    private static final long     serialVersionUID = 1L;
    final transient AbstractActor actor;

    public PredecessorFoundException(final AbstractActor actor) {
      this.actor = actor;
    }
  }

  private final void innerVisit(final AbstractActor actor) {
    if (this.visited.contains(actor)) {
      // skip
    } else {
      // make sure predecessors have been visited
      innerVisitPredecessors(actor);
      visit(actor);
      this.visited.add(actor);
      if (this.stuckStack.peek() == actor) {
        // if we were visiting predecessors, resume the visit of the predecessors
        // this trick is to avoid having too deep call stacks and avoid StackOverflowError with complex graphs
        throw new PredecessorFoundException(actor);
      }
    }
  }

  private final void innerVisitPredecessors(final AbstractActor actor) {
    final List<AbstractActor> directPredecessorsOf = PiSDFTopologyHelper.getDirectPredecessorsOf(actor);
    if (actor instanceof CommunicationActor) {
      directPredecessorsOf.addAll(new CommunicationPrecedence().doSwitch(actor));
    }
    for (final AbstractActor v : directPredecessorsOf) {
      if (!this.visited.contains(v)) {
        try {
          this.stuckStack.push(v);
          innerVisitScheduleOf(v);
        } catch (final PredecessorFoundException e) {
          if (v == e.actor) {
            // if exception was thrown when encountering the predecessor, pop it and resume visit of predecessors
            this.stuckStack.pop();
          } else {
            // else it means the schedule is not topology compliant
            throw new PreesmRuntimeException("Schedule is not topology compliant.");
          }
        }
      }
    }
  }

  private final void innerVisitScheduleOf(final AbstractActor v) {
    if (this.actorToScheduleMap == null) {
      if (this.scheduleStack.isEmpty()) {
        throw new PreesmRuntimeException("Visit should start with a schedule object");
      }
      final Schedule peekFirst = this.scheduleStack.peekFirst();
      this.actorToScheduleMap = ScheduleUtil.actorToScheduleMap(peekFirst.getRoot());
    }
    final ActorSchedule actorSchedule = this.actorToScheduleMap.get(v);
    innerVisit(actorSchedule);
  }

  /**
   * Inner visitor to build a list of predecessors for communication actors.
   */
  private static final class CommunicationPrecedence extends ScheduleSwitch<List<AbstractActor>> {
    @Override
    public final List<AbstractActor> caseReceiveEndActor(final ReceiveEndActor rea) {
      final ReceiveStartActor receiveStart = rea.getReceiveStart();
      final SendEndActor sourceSendEnd = rea.getSourceSendStart().getSendEnd();

      final List<AbstractActor> res = new ArrayList<>();
      res.add(receiveStart);
      res.add(sourceSendEnd);
      res.addAll(doSwitch(receiveStart));
      res.addAll(doSwitch(sourceSendEnd));

      return res;
    }

    @Override
    public final List<AbstractActor> caseReceiveStartActor(final ReceiveStartActor rsa) {
      final SendStartActor sourceSendStart = rsa.getReceiveEnd().getSourceSendStart();

      final List<AbstractActor> res = new ArrayList<>();
      res.add(sourceSendStart);
      res.addAll(doSwitch(sourceSendStart));

      return res;
    }

    @Override
    public final List<AbstractActor> caseSendEndActor(final SendEndActor sea) {
      final SendStartActor sendStart = sea.getSendStart();

      final List<AbstractActor> res = new ArrayList<>();
      res.add(sendStart);
      res.addAll(doSwitch(sendStart));

      return res;
    }

    @Override
    public final List<AbstractActor> caseSendStartActor(final SendStartActor ssa) {
      return Arrays.asList();
    }
  }

  /**
   *
   */
  public abstract void visit(final AbstractActor actor);

}
