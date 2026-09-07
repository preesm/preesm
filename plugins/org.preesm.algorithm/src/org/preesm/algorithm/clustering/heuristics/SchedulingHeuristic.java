package org.preesm.algorithm.clustering.heuristics;

import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;

/**
 * This abstract {@link Heuristic heuristic} is made to {@link Schedule schedule} a {@link PiGraph graph}. The
 * implementation classes are stored in the package clustering.synthesis
 *
 * @author rcazoulat
 */
public abstract class SchedulingHeuristic extends Heuristic {

  /**
   * This method returns the {@link Schedule schedule} of a {@link PiGraph graph}
   *
   * @param cluster
   *          the graph to schedule
   * @return the schedule
   */
  public abstract Schedule schedule(final PiGraph cluster);
}
