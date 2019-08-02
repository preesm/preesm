package org.preesm.algorithm.model.schedule.util;

import org.preesm.algorithm.schedule.model.Schedule;

/**
 * @author dgageot
 *
 *         Interface to ease execution of schedule transform
 *
 */
public interface IScheduleTransform {

  public Schedule performTransform(Schedule schedule);

}
