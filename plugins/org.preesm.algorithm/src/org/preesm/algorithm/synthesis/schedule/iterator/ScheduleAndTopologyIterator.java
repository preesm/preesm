package org.preesm.algorithm.synthesis.schedule.iterator;

import java.util.List;
import org.preesm.algorithm.schedule.model.Schedule;
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
    // TODO
    return null;
  }

}
