package org.preesm.algorithm.clustering.synthesis;

import org.preesm.algorithm.clustering.heuristics.SchedulingHeuristic;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;

public class APGANSchedulingHeuristic extends SchedulingHeuristic {

  @Override
  public Schedule schedule(PiGraph cluster) {
    ClusterSynthesisHelper.addSpecialActors(cluster);
    return PGANClusterScheduler.performAPGANSchedule(cluster, true, true);
  }

}
