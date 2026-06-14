package org.preesm.algorithm.clustering.heuristics;

import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;

public abstract class AllocationHeuristic extends Heuristic {

  public abstract Allocation allocate(PiGraph cluster, Schedule clusterSchedule, Mapping clusterMapping);

}
