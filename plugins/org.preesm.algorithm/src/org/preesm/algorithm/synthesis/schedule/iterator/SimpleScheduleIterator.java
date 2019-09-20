package org.preesm.algorithm.synthesis.schedule.iterator;

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
public class SimpleScheduleIterator extends ScheduleIterator {

  public SimpleScheduleIterator(final Schedule schedule) {
    super(schedule);
  }

  @Override
  public List<AbstractActor> createOrder(final Schedule schedule) {
    return new SimpleScheduleSwitch().doSwitch(schedule);
  }

  /**
   *
   * @author anmorvan
   *
   */
  private static class SimpleScheduleSwitch extends ScheduleSwitch<List<AbstractActor>> {
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
