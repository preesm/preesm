package org.preesm.algorithm.clustering.heuristics;

import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;

/**
 * This abstract class can be used to create {@link Heuristic heuristics} that make an {@link Allocation allocation} of
 * a {@link PiGraph graph/cluster}. Methods to allocate memory of a graph already exist, but this heuristic targets
 * specifically methods that works directly on PiSDF graphs that are not SrDAGed. The reason is that clusters won't be
 * transformed in a SrDAG graph, so the classic allocation methods won't work.
 *
 * @author rcazoulat
 */
public abstract class AllocationHeuristic extends Heuristic {

  /**
   * The method will create an {@link Allocation allocation} for a given {@link PiGraph cluster}, already
   * {@link Schedule scheduled}.
   *
   * @param cluster
   *          The studied cluster
   * @param clusterSchedule
   *          The cluster schedule
   * @return The allocation of the cluster
   */
  public abstract Allocation allocate(PiGraph cluster, Schedule clusterSchedule);

}
