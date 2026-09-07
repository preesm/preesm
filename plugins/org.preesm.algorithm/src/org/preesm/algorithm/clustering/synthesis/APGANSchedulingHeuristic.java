package org.preesm.algorithm.clustering.synthesis;

import org.preesm.algorithm.clustering.heuristics.SchedulingHeuristic;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.algos.APGANScheduler;
import org.preesm.model.pisdf.PiGraph;

/**
 * This {@link SchedulingHeuristic} will schedule a cluster according to the APGAN scheduling method. The main code is
 * in the {@link APGANScheduler} class.
 *
 * @author rcazoulat
 */
public class APGANSchedulingHeuristic extends SchedulingHeuristic {

  @Override
  public Schedule schedule(PiGraph cluster) {
    final APGANScheduler scheduler = new APGANScheduler();
    return scheduler.scheduleAndMap(cluster, arch, scenario, null).schedule;
  }

}
