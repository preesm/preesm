package org.preesm.algorithm.synthesis.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.commons.CollectionUtil;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.slam.ComponentInstance;

/**
 * Schedule manager class. Helps build ordered list for the AbstractActors of a schedule, inserting actors, querying,
 * etc.
 *
 *
 *
 * @author anmorvan
 */
public class ScheduleOrderManager {

  /** Schedule managed by this class */
  private final Schedule                          schedule;
  /**  */
  private final Map<AbstractActor, ActorSchedule> actorToScheduleMap;

  /**
   */
  public ScheduleOrderManager(final Schedule schedule) {
    this.schedule = schedule;
    this.actorToScheduleMap = ScheduleOrderManager.actorToScheduleMap(schedule);
  }

  /**
   * Build the order following the appearance in the lists of the schedule tree. This order may not respect topological
   * order of the actors, thus this order should not be used as valid execution scheme.
   *
   * Uses a simple {@link ScheduleSwitch} to build the internal list.
   *
   * The result list is unmodifiable.
   */
  public final List<AbstractActor> buildNonTopologicalOrderedList() {
    return Collections.unmodifiableList(new InternalSimpleScheduleOrderBuilder().createOrder(this.schedule));
  }

  /**
   * Build the order following the appearance in the schedule tree but also in the topological order. This order is a
   * valid execution scheme according to both schedule and graph topology.
   *
   * Uses {@link ScheduleOrderedVisitor} to build the internal list.
   *
   * The result list is unmodifiable.
   */
  public final List<AbstractActor> buildScheduleAndTopologicalOrderedList() {
    return Collections.unmodifiableList(new InternalScheduleAndTopologyOrderBuilder().createOrder(this.schedule));
  }

  /**
   * Builds the list of actors that will execute on operator according to the given mapping, in schedule and topological
   * order.
   *
   * The result list is unmodifiable.
   */
  public final List<AbstractActor> buildScheduleAndTopologicalOrderedList(final Mapping mapping,
      final ComponentInstance operator) {
    final List<AbstractActor> res = new ArrayList<>();
    final List<AbstractActor> scheduleAndTopologicalOrderedList = buildScheduleAndTopologicalOrderedList();
    for (final AbstractActor actor : scheduleAndTopologicalOrderedList) {
      if (mapping.getMapping(actor).contains(operator)) {
        res.add(actor);
      }
    }
    return Collections.unmodifiableList(res);
  }

  /**
   * Find the Schedule in which referenceActor appears, insert newActors after referenceActor in the found Schedule,
   * update internal structure.
   */
  public final void insertAfter(final AbstractActor referenceActor, final AbstractActor... newActors) {
    final ActorSchedule actorSchedule = actorToScheduleMap.get(referenceActor);
    final EList<AbstractActor> srcActorList = actorSchedule.getActorList();
    CollectionUtil.insertAfter(srcActorList, referenceActor, newActors);
    for (final AbstractActor newActor : newActors) {
      actorToScheduleMap.put(newActor, actorSchedule);
    }
  }

  /**
   * Find the Schedule in which referenceActor appears, insert newActors after referenceActor in the found Schedule,
   * update internal structure.
   */
  public final void insertBefore(final AbstractActor referenceActor, final AbstractActor... newActors) {
    final ActorSchedule actorSchedule = actorToScheduleMap.get(referenceActor);
    final EList<AbstractActor> srcActorList = actorSchedule.getActorList();
    CollectionUtil.insertBefore(srcActorList, referenceActor, newActors);
    for (final AbstractActor newActor : newActors) {
      actorToScheduleMap.put(newActor, actorSchedule);
    }
  }

  /**
   * Builds a map that associate for every actor in the schedule its refering ActorSchedule.
   */
  private static final Map<AbstractActor, ActorSchedule> actorToScheduleMap(final Schedule schedule) {
    final Map<AbstractActor, ActorSchedule> res = new LinkedHashMap<>();
    new ScheduleSwitch<Boolean>() {
      @Override
      public Boolean caseHierarchicalSchedule(final HierarchicalSchedule hSched) {
        hSched.getScheduleTree().forEach(this::doSwitch);
        return true;
      }

      @Override
      public Boolean caseActorSchedule(final ActorSchedule aSched) {
        aSched.getActorList().forEach(a -> res.put(a, aSched));
        return true;
      }
    }.doSwitch(schedule);
    return res;
  }

  /**
   */
  private class InternalScheduleAndTopologyOrderBuilder {

    public List<AbstractActor> createOrder(final Schedule schedule) {
      final List<AbstractActor> res = new ArrayList<>();
      new ScheduleOrderedVisitor(ScheduleOrderManager.this.actorToScheduleMap) {
        @Override
        public void visit(final AbstractActor actor) {
          res.add(actor);
        }

      }.doSwitch(schedule);
      return res;
    }
  }

  /**
   */
  private static class InternalSimpleScheduleOrderBuilder {

    public List<AbstractActor> createOrder(final Schedule schedule) {
      return new InternalSimpleScheduleSwitch().doSwitch(schedule);
    }

    /**
     */
    private static class InternalSimpleScheduleSwitch extends ScheduleSwitch<List<AbstractActor>> {
      @Override
      public List<AbstractActor> caseHierarchicalSchedule(final HierarchicalSchedule object) {
        return object.getChildren().stream().map(this::doSwitch).flatMap(List::stream).collect(Collectors.toList());
      }

      @Override
      public List<AbstractActor> caseActorSchedule(final ActorSchedule object) {
        return object.getActorList();
      }
    }
  }
}
