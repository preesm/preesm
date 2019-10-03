package org.preesm.algorithm.synthesis.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.model.pisdf.AbstractActor;

/**
 * Builds ordered list for the AbstractActors of a schedule.
 *
 * @author anmorvan
 */
public class ScheduleOrderManager {

  /**
   * Build the order following the appearance in the lists of the schedule tree. This order may not respect topological
   * order of the actors, thus this order should not be used as valid execution scheme.
   *
   * Uses a simple {@link ScheduleSwitch} to build the internal list;
   */
  public static final List<AbstractActor> getSimpleOrderedList(final Schedule schedule) {
    return new InternalSimpleScheduleOrderBuilder().createOrder(schedule);
  }

  /**
   * Build the order following the appearance in the schedule tree but also in the topological order. This order is a
   * valid execution scheme according to both schedule and graph topology.
   *
   * Uses {@link ScheduleOrderedVisitor} to build the internal list;
   */
  public static final List<AbstractActor> getScheduleAndTopologicalOrderedList(final Schedule schedule) {
    return new InternalScheduleAndTopologyOrderBuilder().createOrder(schedule);
  }

  /**
   */
  private interface IOrderBuilder {
    public List<AbstractActor> createOrder(final Schedule schedule);
  }

  /**
   */
  private static class InternalScheduleAndTopologyOrderBuilder implements IOrderBuilder {

    public List<AbstractActor> createOrder(final Schedule schedule) {
      final List<AbstractActor> res = new ArrayList<>();
      new ScheduleOrderedVisitor() {
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
  private static class InternalSimpleScheduleOrderBuilder implements IOrderBuilder {

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
