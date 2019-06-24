package org.preesm.algorithm.scheduler;

import org.preesm.model.algorithm.mapping.Mapping;
import org.preesm.model.algorithm.schedule.Schedule;

/**
 *
 */
public class SchedulerResult {
  public final Mapping  mapping;
  public final Schedule schedule;

  public SchedulerResult(final Mapping mapping, final Schedule schedule) {
    this.mapping = mapping;
    this.schedule = schedule;
  }
}
