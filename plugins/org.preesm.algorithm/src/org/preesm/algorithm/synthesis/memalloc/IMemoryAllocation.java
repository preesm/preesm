package org.preesm.algorithm.synthesis.memalloc;

import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * Memory allocation of a PiSDF under schedule/mapping constraints
 */
public interface IMemoryAllocation {

  public Allocation allocateMemory(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping);

}
