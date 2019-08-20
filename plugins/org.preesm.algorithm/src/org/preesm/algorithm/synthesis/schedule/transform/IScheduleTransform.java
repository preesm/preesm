package org.preesm.algorithm.synthesis.schedule.transform;

import org.preesm.algorithm.schedule.model.Schedule;

/**
 * @author dgageot
 *
 *         Interface to ease execution of schedule transform
 *
 */
@FunctionalInterface
public interface IScheduleTransform {

  public Schedule performTransform(Schedule schedule);

}
