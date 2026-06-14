package org.preesm.algorithm.clustering.heuristics;

import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;

public abstract class SchedulingHeuristic extends Heuristic {

  public abstract Schedule schedule(final PiGraph cluster);
}
