package org.preesm.algorithm.schedule.evaluation;

import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;

/**
 *
 * @author anmorvan
 *
 * @param <T>
 *          The schedule cost type. See {@link IScheduleCost}
 */
public interface IScheduleEvaluator<T extends IScheduleCost<?>> {

  public T evaluate(final Mapping mapping, final Schedule schedule, final Allocation alloc);

}
