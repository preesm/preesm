package org.preesm.algorithm.synthesis.memalloc;

import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.AbstractAllocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.memalloc.passiveactors.PassiveActorEngine;
import org.preesm.algorithm.synthesis.memalloc.passiveactors.PassiveAllocation;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class PassivePiMMAllocation implements IMemoryAllocation {

  long alignment;

  public PassivePiMMAllocation(final long alignment) {
    this.alignment = alignment;
  }

  @Override
  public AbstractAllocation allocateMemory(PiGraph piGraph, Design slamDesign, Scenario scenario, Schedule schedule,
      Mapping mapping) {

    if (piGraph.getContainingPiGraph() == null) {
      throw new PreesmRuntimeException("top graph PiMM Allocation with passive actors is not supported "
          + "in the current version of Preesm. Please contact developers.");
    }

    final PassiveActorEngine engine = new PassiveActorEngine(piGraph, scenario, alignment);
    engine.processPassiveActors();
    engine.composePassiveActors();
    final PiGraph passiveIR = engine.getPassiveIR();
    final PassiveAllocation allocator = new PassiveAllocation(passiveIR, scenario);
    return allocator.makeAllocation();
  }

}
