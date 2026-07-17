package org.preesm.algorithm.synthesis.memalloc;

import java.util.List;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;

public abstract class AClusterAwareMemoryAllocation implements IMemoryAllocation {

  protected List<PiGraph>    clusters;
  protected List<Schedule>   clusterSchedules;
  protected List<Allocation> clusterAllocations;

  public void giveClustersInfos(List<PiGraph> clusters, List<Schedule> clusterSchedules,
      List<Allocation> clusterAllocations) {
    this.clusters = clusters;
    this.clusterSchedules = clusterSchedules;
    this.clusterAllocations = clusterAllocations;
  }

}
