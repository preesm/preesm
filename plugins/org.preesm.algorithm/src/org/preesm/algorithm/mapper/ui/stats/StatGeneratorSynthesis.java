package org.preesm.algorithm.mapper.ui.stats;

import java.util.Map.Entry;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.timer.ActorExecutionTiming;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Builds stats from synthesis (schedule, mapping and memory allocation)
 * 
 * @author ahonorat
 *
 */
public class StatGeneratorSynthesis extends AbstractStatGenerator {

  private final Mapping     mapping;
  private final Allocation  memAlloc;
  private final LatencyCost latencyCost;

  private final GanttData gd;

  /**
   * Builds stats from synthesis results.
   * 
   * @param mapping
   *          Mapping of the PiGraph actors.
   * @param memAlloc
   *          MemoryAllocation of the PiGraph.
   * @param latencyCost
   *          Latency of the schedule.
   */
  public StatGeneratorSynthesis(final Design architecture, final Scenario scenario, final Mapping mapping,
      final Allocation memAlloc, final LatencyCost latencyCost) {
    super(architecture, scenario);
    this.mapping = mapping;
    this.memAlloc = memAlloc;
    this.latencyCost = latencyCost;

    this.gd = new GanttData();
    gd.insertSchedulerMapping(mapping, latencyCost.getExecTimings());
  }

  @Override
  public long getDAGSpanLength() {
    // TODO Auto-generated method stub
    return 0L;
  }

  @Override
  public long getDAGWorkLength() {
    // TODO Auto-generated method stub
    return 0L;
  }

  @Override
  public long getFinalTime() {
    return latencyCost.getValue();
  }

  @Override
  public long getLoad(ComponentInstance operator) {
    long res = 0L;
    for (Entry<AbstractActor, ActorExecutionTiming> e : latencyCost.getExecTimings().entrySet()) {
      AbstractActor aa = e.getKey();
      ActorExecutionTiming aet = e.getValue();
      for (ComponentInstance ci : mapping.getMapping(aa)) {
        if (ci.equals(operator)) {
          res += aet.getDuration();
        }
      }
    }
    return res;
  }

  @Override
  public long getMem(ComponentInstance operator) {
    long res = 0L;
    for (PhysicalBuffer pb : memAlloc.getPhysicalBuffers()) {
      res += pb.getSize();
    }
    return res;
  }

  @Override
  public int getNbUsedOperators() {
    return mapping.getAllInvolvedComponentInstances().size();
  }

  @Override
  public GanttData getGanttData() {
    return gd;
  }

}
