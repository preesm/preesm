package org.preesm.algorithm.synthesis.memalloc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.WorkingMemory;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.algos.APGANScheduler;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * Allocate from a schedule made by the {@link APGANScheduler APGAN scheduler} a {@link PiGraph PiSDF} graph (not in its
 * SrDAG form). This graph might certainly contain hierarchy, because of how the {@link APGANScheduler APGAN scheduler}
 * works. This allocation processes the hierarchy in a top-down approach, which means that the outer {@link Fifo} of an
 * {@link DataInterface interface} is allocated, but not the inner {@link Fifo} of this {@link DataInterface interface}.
 * This has to be taken into account in the Codegen model generation step, which is only made by
 * {@link PiCodegenModelGenerator2 this generator} for now.
 *
 * @author rcazoulat
 */
public class SimplePiMMAllocation extends ScheduleSwitch<Boolean> implements IMemoryAllocation {

  Map<AbstractVertex, Long> clusterBrv;
  Scenario                  scenario;
  WorkingMemory             workMem;
  Long                      allocSizeInBit;

  @Override
  public WorkingMemory allocateMemory(PiGraph oriCluster, Design slamDesign, Scenario scenario, Schedule schedule,
      Mapping mapping) {
    // Here the oriCluster argument is not used as it is already present in the scheduled form in the root schedule node
    // as the attached actor. Mapping and slamDesign are not used neither.

    this.scenario = scenario;

    clusterBrv = PiBRV.compute(oriCluster, BRVMethod.LCM);

    // Initializing the allocation and its size
    workMem = MemoryAllocationFactory.eINSTANCE.createWorkingMemory();
    workMem.setAttachedActor(oriCluster);
    allocSizeInBit = 0L;

    final LogicalBuffer mainBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
    mainBuffer.setWorkMemValue(true);
    workMem.setMainBuffer(mainBuffer);

    // The allocation memAlloc is filled by visiting the schedule with the doSwitch method.
    // Fore more information on its effect, go see the method caseHierarchicalSchedule.
    doSwitch(schedule);

    // The allocSize has been defined during the doSwitch
    mainBuffer.setSizeInBit(allocSizeInBit);
    PreesmLogger.getLogger().info(() -> "Naive allocation: working memory for cluster " + oriCluster.getName() + " = "
        + allocSizeInBit + " bit(s)");

    // The returned memAlloc was allocated using the top-down approach when manipulating the sub-clusters.
    return workMem;
  }

  @Override
  public Boolean caseHierarchicalSchedule(HierarchicalSchedule hierSchedule) {
    hierSchedule.getChildren().forEach(this::doSwitch);
    return true;
  }

  Set<Fifo> alreadyAllocated = new HashSet<>();

  @Override
  public Boolean caseActorSchedule(ActorSchedule actorSched) {

    for (final AbstractActor actor : actorSched.getActorList()) {
      final AbstractActor sourceActor = PreesmCopyTracker.getSource(actor);
      for (final Fifo fifo : sourceActor.getAllDataPorts().stream().map(DataPort::getFifo)
          .filter(fifo -> !alreadyAllocated.contains(fifo)).toList()) {

        // "begin" means that the port is attached to actor a
        final DataOutputPort beginPort = fifo.getSourcePort();
        final AbstractActor beginActor = fifo.getSource();
        final long beginPortExpr = beginPort.getExpression().evaluateAsLong();

        // "end" means that the port/actor is at the other end of the fifo starting at beginPort
        final DataInputPort endPort = fifo.getTargetPort();
        final AbstractActor endActor = fifo.getTarget();
        final long endPortExpr = endPort.getExpression().evaluateAsLong();

        // Computing buffer size in bit
        final long beginActorRate = beginActor instanceof DataInterface ? 1 : clusterBrv.get(beginActor);
        final long endActorRate = endActor instanceof DataInterface ? 1 : clusterBrv.get(endActor);

        final long bufferSize = Math.max(beginActorRate * beginPortExpr, endActorRate * endPortExpr);

        final long bufferSizeInBit = scenario.getSimulationInfo().getBufferSizeInBit(fifo.getType(), bufferSize);

        // Creating the FifoAllocation
        final FifoAllocation fifoAllocation = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
        fifoAllocation.setFifo(fifo);
        workMem.getFifoAllocations().put(fifo, fifoAllocation);

        final LogicalBuffer fifoBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
        fifoBuffer.setSizeInBit(bufferSizeInBit);
        fifoAllocation.setSourceBuffer(fifoBuffer);
        fifoAllocation.setTargetBuffer(fifoBuffer);

        if (beginActor instanceof final DataInterface beginI) {
          fifoBuffer.setOffsetInBit(0);
          workMem.getInterfaceBuffers().put(beginI, fifoBuffer);

        } else if (endActor instanceof final DataInterface endI) {
          fifoBuffer.setOffsetInBit(0);
          workMem.getInterfaceBuffers().put(endI, fifoBuffer);

        } else {
          fifoBuffer.setOffsetInBit(allocSizeInBit);
          workMem.getMainBuffer().getChildren().add(fifoBuffer);
          allocSizeInBit += bufferSizeInBit;
        }
        alreadyAllocated.add(fifo);
      }
    }

    return true;
  }
}
