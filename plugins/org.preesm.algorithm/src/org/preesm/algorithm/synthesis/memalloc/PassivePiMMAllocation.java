package org.preesm.algorithm.synthesis.memalloc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.AbstractAllocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.WorkingMemory;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.memalloc.passive.PassiveAllocBufferGeneratorSwitch;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveElt;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * @author rcazoulat
 *
 *         WIP
 *
 *         TODO: delays !
 */
public class PassivePiMMAllocation extends ScheduleSwitch<Boolean> implements IMemoryAllocation {

  PiGraph                        pafg;
  Scenario                       scenario;
  WorkingMemory                  workMem;
  LogicalBuffer                  mainBuffer;
  Long                           allocSizeInBit;
  Map<PassiveElt, LogicalBuffer> passiveBufferMap;

  public PassivePiMMAllocation() {
    this.passiveBufferMap = new HashMap<>();
  }

  /**
   * Only for shared-memory allocation (or/and distributed not supported yet)
   */
  @Override
  public AbstractAllocation allocateMemory(PiGraph cluster, Design slamDesign, Scenario scenario, Schedule schedule,
      Mapping mapping) {

    this.pafg = cluster;
    this.scenario = scenario;

    // Creating the working memory
    final WorkingMemory workingMem = MemoryAllocationFactory.eINSTANCE.createWorkingMemory();
    this.workMem = workingMem;
    workingMem.setAttachedActor(pafg);

    // Creating the main buffer
    this.mainBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
    this.mainBuffer.setWorkMemValue(true);
    workingMem.setMainBuffer(this.mainBuffer);

    // Creating all buffers from pafg
    generateInterfaceBuffers();
    generatePassiveBuffers();

    // making a do switch to populate absAlloc
    doSwitch(schedule);

    // In case the adjacent actor of an interface is passive, the fifo linked to its data port is never visited.
    // We have to do it manually.
    allocateInterfaceFifos();

    return workMem;
  }

  private void generateInterfaceBuffers() {
    for (final DataInterface di : pafg.getDataInterfaces()) {
      final LogicalBuffer ioBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
      ioBuffer.setSizeInBit(di.getDataPort().getExpression().evaluateAsLong());
      ioBuffer.setOffsetInBit(0);
      this.workMem.getInterfaceBuffers().put(di, ioBuffer);
    }
  }

  /**
   * Generates the buffers, except the ones that are on the edge of the cluster (fifos connected to an interface rather
   * than a Passive Actor). The resulting buffers are stored on the p2b (passive to buffer) map, and a main buffer is
   * created that regroups all the fifo buffers of the cluster.
   */
  private void generatePassiveBuffers() {
    long finalSizeInBit = 0;
    this.passiveBufferMap = new PassiveAllocBufferGeneratorSwitch(scenario, this.workMem.getInterfaceBuffers())
        .doSwitch(this.pafg);

    for (final Entry<PassiveElt, LogicalBuffer> entry : this.passiveBufferMap.entrySet()) {
      final MemBuffer buffer = entry.getValue();

      // We verify that a logicalBuffer attached to a Passive Actor is not a sub buffer of an interface (i. e. has no
      // containing buffer yet). If so, no need to instantiate it.
      if (entry.getKey() instanceof PassiveActor && buffer instanceof final LogicalBuffer logicalBuffer
          && logicalBuffer.getContainingBuffer() == null) {

        logicalBuffer.setOffsetInBit(finalSizeInBit);
        logicalBuffer.setContainingBuffer(this.mainBuffer);
        finalSizeInBit += logicalBuffer.getSizeInBit();
      }
    }

    this.mainBuffer.setSizeInBit(finalSizeInBit);
  }

  @Override
  public Boolean caseHierarchicalSchedule(HierarchicalSchedule hierSchedule) {
    hierSchedule.getChildren().forEach(this::doSwitch);
    return true;
  }

  Set<Fifo> alreadyAllocated = new HashSet<>();

  @Override
  public Boolean caseActorSchedule(ActorSchedule schedule) {

    for (final AbstractActor actor : schedule.getActorList()) {

      final AbstractActor sourceActor = PreesmCopyTracker.getSource(actor);

      // Creating a fifo allocation for each port
      for (final DataPort dp : sourceActor.getAllDataPorts()) {

        // Skipping this port if the attached fifo has already been allocated
        if (this.alreadyAllocated.contains(dp.getFifo())) {
          continue;
        }
        this.alreadyAllocated.add(dp.getFifo());

        final AbstractActor oppositeActor = dp.getOppositePort().getContainingActor();

        // Creating the fifo allocation of the fifo attached to dp
        final FifoAllocation fifoAlloc = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
        fifoAlloc.setFifo(dp.getFifo());

        /** Case if opposite actor in PAFG is a Data Interface */
        if (oppositeActor instanceof final DataInterface di) {
          fifoAlloc.setSourceBuffer(workMem.getInterfaceBuffers().get(di));
          fifoAlloc.setTargetBuffer(workMem.getInterfaceBuffers().get(di));

          /** Case if opposite actor in PAFG is a Passive Actor */
        } else if (oppositeActor instanceof PassiveActor) {
          final PassivePort sourcePp = (PassivePort) dp.getOppositePort();
          fifoAlloc.setSourceBuffer(passiveBufferMap.get(sourcePp));
          fifoAlloc.setTargetBuffer(passiveBufferMap.get(sourcePp));
        }

        workMem.getFifoAllocations().put(dp.getFifo(), fifoAlloc);
      }
    }
    return true;
  }

  private void allocateInterfaceFifos() {
    for (final DataInterface di : this.pafg.getDataInterfaces()) {
      final Fifo fifo = di.getDataPort().getFifo();
      if (this.alreadyAllocated.contains(fifo)) {
        continue;
      }
      this.alreadyAllocated.add(fifo);
      // Creating the fifo allocation of the fifo attached to dp
      final FifoAllocation fifoAlloc = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
      fifoAlloc.setFifo(fifo);
      fifoAlloc.setSourceBuffer(workMem.getInterfaceBuffers().get(di));
      fifoAlloc.setTargetBuffer(workMem.getInterfaceBuffers().get(di));
      workMem.getFifoAllocations().put(fifo, fifoAlloc);

    }
  }

}
