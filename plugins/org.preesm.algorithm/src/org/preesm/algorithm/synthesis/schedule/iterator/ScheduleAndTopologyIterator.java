package org.preesm.algorithm.synthesis.schedule.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderedVisitor;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
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

    final Map<AbstractActor, ActorSchedule> actorToScheduleMap = ScheduleUtil.actorToScheduleMap(schedule);

    final List<AbstractActor> res = new ArrayList<>();

    new ScheduleOrderedVisitor(actorToScheduleMap) {
      @Override
      public void visit(AbstractActor actor) {
        res.add(actor);
      }

    }.doSwitch(schedule);
    return res;
  }
}
