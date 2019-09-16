package org.preesm.algorithm.synthesis.schedule.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.AbstractActor;

/**
 *
 * @author anmorvan
 *
 */
public abstract class ScheduleIterator implements Iterator<AbstractActor> {

  protected final List<AbstractActor> orderedList;

  private int currentIndex;

  public ScheduleIterator(final Schedule schedule) {
    this.orderedList = createOrder(schedule);
  }

  protected abstract List<AbstractActor> createOrder(final Schedule schedule);

  public final List<AbstractActor> getOrderedList() {
    return this.orderedList;
  }

  @Override
  public final boolean hasNext() {
    return currentIndex < orderedList.size();
  }

  @Override
  public final AbstractActor next() {
    if (hasNext()) {
      return orderedList.get(currentIndex++);
    } else {
      throw new NoSuchElementException();
    }
  }

}
