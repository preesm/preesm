package org.preesm.algorithm.scheduler;

import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 *
 */
public interface IScheduler {
  public SchedulerResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario);
}
