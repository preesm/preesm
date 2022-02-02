/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.synthesis.memalloc;

import java.util.List;
import java.util.stream.Collectors;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;

/**
 * Allocate 1 physical buffer on the main com node then 1 logical buffer per Fifo in the graph and 1 buffer per delay
 * (init/end couple).
 *
 * @author anmorvan
 *
 */
public class SimpleMemoryAllocation implements IMemoryAllocation {

  @Override
  public Allocation allocateMemory(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping) {

    if (!SlamDesignPEtypeChecker.isOnlyCPU(slamDesign)) {
      throw new PreesmRuntimeException("This task must be called with a CPU architecture, abandon.");
    }

    final ComponentInstance mainComNode = scenario.getSimulationInfo().getMainComNode();

    final Allocation memAlloc = MemoryAllocationFactory.eINSTANCE.createAllocation();

    final PhysicalBuffer physBuff = MemoryAllocationFactory.eINSTANCE.createPhysicalBuffer();
    memAlloc.getPhysicalBuffers().add(physBuff);
    physBuff.setMemoryBank(mainComNode);

    long totalSize = 0L;

    // allocate fifos
    final List<AbstractActor> orderedList = new ScheduleOrderManager(piGraph, schedule)
        .buildScheduleAndTopologicalOrderedList();
    for (final AbstractActor actor : orderedList) {
      final List<Fifo> fifos = actor.getDataOutputPorts().stream().map(DataPort::getFifo).collect(Collectors.toList());
      for (final Fifo fifo : fifos) {
        final String fifoType = fifo.getType();
        final long fifoTokenSize = fifo.getTargetPort().getPortRateExpression().evaluate();
        final long fifoBufferSize = scenario.getSimulationInfo().getBufferSizeInBit(fifoType, fifoTokenSize);

        final LogicalBuffer logicBuff = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
        logicBuff.setContainingBuffer(physBuff);
        logicBuff.setSizeInBit(fifoBufferSize);
        logicBuff.setOffsetInBit(totalSize);

        final FifoAllocation fifoAllocation = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
        fifoAllocation.setFifo(fifo);
        fifoAllocation.setSourceBuffer(logicBuff);
        fifoAllocation.setTargetBuffer(logicBuff);

        memAlloc.getFifoAllocations().put(fifo, fifoAllocation);

        totalSize += fifoBufferSize;
      }
    }

    // allocate delays
    final List<InitActor> initActors = piGraph.getActors().stream().filter(InitActor.class::isInstance)
        .map(InitActor.class::cast).filter(a -> a.getEndReference() instanceof EndActor).collect(Collectors.toList());
    for (final InitActor initActor : initActors) {
      final String fifoType = initActor.getDataPort().getFifo().getType();
      final long dataTypeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(fifoType);
      final long delayTokenSize = initActor.getDelaySize();
      // final long delayBufferSize = dataTypeSize * delayTokenSize;
      final long delayBufferSize = scenario.getSimulationInfo().getBufferSizeInBit(fifoType, delayTokenSize);

      final LogicalBuffer logicBuff = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
      logicBuff.setContainingBuffer(physBuff);
      logicBuff.setSizeInBit(delayBufferSize);
      logicBuff.setOffsetInBit(totalSize);

      memAlloc.getDelayAllocations().put(initActor, logicBuff);

      totalSize += delayBufferSize;
    }

    physBuff.setSizeInBit(totalSize);
    return memAlloc;
  }

}
