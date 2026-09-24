package org.preesm.algorithm.clustering.synthesis;

import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.memalloc.SrDAGLessMemoryAllocationScheduleSwitch;
import org.preesm.model.pisdf.PiGraph;

/**
 * This {@link AllocationHeuristic heuristic} will create an {@link Allocation allocation} for a given cluster. The
 * interesting code is in the class {@link SrDAGLessMemoryAllocationScheduleSwitch} class. This heuristic is considered
 * simple as it doesn't perform any memory optimization, it only allocate memory in a naive way, without memory reuse.
 */
public class SimpleAllocationHeuristic extends AllocationHeuristic {

  @Override
  public Allocation allocate(PiGraph cluster, Schedule clusterSchedule) {

    // In SimplePiMemoryAllocation, the mapping (last parameter) is not used so it can be set to null.
    return new SrDAGLessMemoryAllocationScheduleSwitch().allocateMemory(cluster, arch, scenario, clusterSchedule, null);

  }

}
