package org.preesm.algorithm.synthesis.schedule.iterator;

import java.util.ArrayList;
import java.util.List;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderedVisitor;
import org.preesm.model.pisdf.AbstractActor;

/**
 * Iterator based on the {@link ScheduleOrderedVisitor} visit order, that is, following schedule order while enforcing
 * graph precedence.
 *
 * @author anmorvan
 */
public class ScheduleAndTopologyIterator extends ScheduleIterator {

  public ScheduleAndTopologyIterator(final Schedule schedule) {
    super(schedule);
  }

  @Override
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
