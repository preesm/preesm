package org.preesm.algorithm.synthesis.memalloc;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.WorkingMemory;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.algos.APGANPiMMScheduler;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * Allocate from a schedule made by the {@link APGANPiMMScheduler APGAN scheduler} a {@link PiGraph PiSDF} graph (not in
 * its SrDAG form). This graph might certainly contain hierarchy, because of how the {@link APGANPiMMScheduler APGAN
 * scheduler} works. This allocation processes the hierarchy in a top-down approach, which means that the outer
 * {@link Fifo} of an {@link DataInterface interface} is allocated, but not the inner {@link Fifo} of this
 * {@link DataInterface interface}. This has to be taken into account in the Codegen model generation step, which is
 * only made by {@link PiCodegenModelGenerator2 this generator} for now.
 *
 * @author rcazoulat
 */
<<<<<<<< HEAD:plugins/org.preesm.algorithm/src/org/preesm/algorithm/synthesis/memalloc/SimplePiMMMemoryAllocation.java
public class SimplePiMMMemoryAllocation extends ScheduleSwitch<Boolean> implements IMemoryAllocation {
========
public class SrDAGLessMemoryAllocationScheduleSwitch extends ScheduleSwitch<Boolean> implements IMemoryAllocation {
>>>>>>>> 2121a213a (minor synthax fixes every where (sorry)):plugins/org.preesm.algorithm/src/org/preesm/algorithm/synthesis/memalloc/SrDAGLessMemoryAllocationScheduleSwitch.java

  Map<AbstractVertex, Long> clusterBrv;
  Scenario                  scenario;
  WorkingMemory             workingMem;
  Long                      allocSize;
  PhysicalBuffer            mainBuffer;

  public SrDAGLessMemoryAllocationScheduleSwitch() {
    memAlloc = MemoryAllocationFactory.eINSTANCE.createAllocation();
    allocSize = 0L;

    // We create main buffer only to pass finalBitSize to the next graph hierarchy level.
    // For now it is useless, but it will be used to set the working memory of the cluster actor
    // If getPhysicalBuffer().size() == 1 && getPhysicalBuffer().get(0).getMemoryBank() == null
    mainBuffer = MemoryAllocationFactory.eINSTANCE.createPhysicalBuffer();
    memAlloc.getPhysicalBuffers().add(mainBuffer);
  }

  @Override
  public WorkingMemory allocateMemory(PiGraph oriCluster, Design slamDesign, Scenario scenario, Schedule schedule,
      Mapping mapping) {
    // Here the oriCluster argument is not used as it is already present in the scheduled form in the root schedule node
    // as the attached actor.

    // mapping and slamDesign are not used neither.

    this.scenario = scenario;

    // It will be the starting point of allocation. The original cluster (without potential schedule transformations) is
    // not used here. This Allocation class has been made to work with the APGANScheduler algorithm. It is intended that
    // the scheduled cluster contains sub-clusters, used to schedule it. For more informations, see the APGANScheduler
    // class.
    final PiGraph scheduledCluster = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();
    clusterBrv = PiBRV.compute(scheduledCluster, BRVMethod.LCM);

<<<<<<<< HEAD:plugins/org.preesm.algorithm/src/org/preesm/algorithm/synthesis/memalloc/SimplePiMMMemoryAllocation.java
    // Initializing the allocation and its size
    workingMem = MemoryAllocationFactory.eINSTANCE.createWorkingMemory();
    workingMem.setAttachedActor(oriCluster);
    allocSize = 0L;

    final LogicalBuffer mainBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
    workingMem.setMainBuffer(mainBuffer);

========
>>>>>>>> 2121a213a (minor synthax fixes every where (sorry)):plugins/org.preesm.algorithm/src/org/preesm/algorithm/synthesis/memalloc/SrDAGLessMemoryAllocationScheduleSwitch.java
    // The allocation memAlloc is filled by visiting the schedule with the doSwitch method.
    // Fore more information on its effect, go see the method caseHierarchicalSchedule.
    doSwitch(schedule);

    // The allocSize has been defined during the doSwitch
    mainBuffer.setSizeInBit(allocSize);

    // The returned memAlloc was allocated using the top-down approach when manipulating the sub-clusters.
    return workingMem;
  }

  @Override
  public Boolean caseHierarchicalSchedule(HierarchicalSchedule hierSchedule) {

    // TOP-DOWN approach: we allocate FIFOs in top level before going down to bottom levels. In other words, when a FIFO
    // is linked to an interface, we can consider that the external FIFO of the interface is already allocated, so there
    // is no need to make the allocation again.
    if (hierSchedule.hasAttachedActor()) {

      final PiGraph subCluster = (PiGraph) hierSchedule.getAttachedActor();
      final List<Fifo> internalFifos = ClusteringHelper.getInternalClusterFifo(subCluster);
      final List<Fifo> externalFifos = new LinkedList<>(subCluster.getFifos());
      externalFifos.removeAll(ClusteringHelper.getInternalClusterFifo(subCluster));

      // Internal FIFOs buffer generation
      generateInternalAllocBuffers(internalFifos);

      // External FIFOs buffer generation
      generateExternalAllocBuffers(hierSchedule, externalFifos);
    }
    for (final Schedule child : hierSchedule.getChildren()) {
      doSwitch(child);
    }
    return true;
  }

  /**
   * Simple FIFO Allocation creation for all internal {@link Fifo}s of a sub-cluster. A sub-cluster is the result of the
   * {@link APGANPiMMScheduler APGAN algorithm} that creates pairs of sub-cluster to schedule the cluster.
   *
   * @param internalFifos
   *          the internal FIFOs of the sub-cluster. A FIFO is consider internal if neither of its attached actors are
   *          an {@link DataInterface interface actor}.
   */
  void generateInternalAllocBuffers(List<Fifo> internalFifos) {
    for (final Fifo fifo : internalFifos) {
      // "begin" means that the port is attached to actor a
      final DataOutputPort beginPort = fifo.getSourcePort();
      final AbstractActor beginActor = fifo.getSource();
      final long beginPortExpr = beginPort.getExpression().evaluateAsLong();

      // "end" means that the port/actor is at the other end of the fifo starting at beginPort
      final DataInputPort endPort = fifo.getTargetPort();
      final AbstractActor endActor = fifo.getTarget();
      final long endPortExpr = endPort.getExpression().evaluateAsLong();

      // Computing buffer size in bit
      final long bufferSize = Math.max(clusterBrv.get(beginActor) * beginPortExpr,
          clusterBrv.get(endActor) * endPortExpr);

      final long bufferSizeInBit = scenario.getSimulationInfo().getBufferSizeInBit(fifo.getType(), bufferSize);

      // Making the allocation & updating the global size of cluster
      createFifoAllocation(fifo, bufferSizeInBit);
    }
  }

  /**
   * Simple FIFO Allocation creation for all external {@link Fifo}s. Because it is a top-down allocation, there will be
   * a FIFO allocation only if the cluster attached to hierSchedule is the root one.
   *
   * @param hierSchedule
   *          The hierarchical schedule
   * @param externalFifos
   *          all the external FIFOs of the sub-cluster
   */
  void generateExternalAllocBuffers(HierarchicalSchedule hierSchedule, List<Fifo> externalFifos) {
    if (checkIfTopCluster(hierSchedule)) {
      for (final Fifo fifo : externalFifos) {
        final DataInterface i = fifo.getSource() instanceof final DataInterface dataInterface ? dataInterface
            : (DataInterface) fifo.getTarget();
        final long bufferSize = i.getDataPort().getExpression().evaluateAsLong();
        final long bufferSizeInBit = scenario.getSimulationInfo().getBufferSizeInBit(fifo.getType(), bufferSize);
        createFifoAllocation(fifo, bufferSizeInBit);
      }
    }
  }

  /**
   * a {@link Schedule schedule} can be recursive, and because the {@link Allocation allocation} of a cluster is based
   * on the schedule in this {@link SimpleAllocationHeuristic heuristic}, the {@link Allocation allocation} has to be
   * built recursively.
   *
   * @param cluster
   *          the current cluster
   * @param s
   *          the current {@link Schedule schedule} to study. It can be a scope containing other schedules, or a list of
   *          actors that can be executed in parallel or sequentially depending on the type of {@link Schedule
   *          subSchedule}.
   * @param clusterMapping
   *          the mapping of the cluster.
   * @return the final bit size, called offset during intermediate call of {@link recursiveAllocation}
   */
  void createFifoAllocation(final Fifo fifo, final long bufferSizeInBit) {

    // Creating the FifoAllocation
    final FifoAllocation fifoAllocation = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
    fifoAllocation.setFifo(fifo);
    workingMem.getFifoAllocations().put(fifo, fifoAllocation);

    // Creating the LogicalBuffer of the fifo
    final LogicalBuffer fifoBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
    fifoBuffer.setOffsetInBit(allocSize);
    fifoBuffer.setSizeInBit(bufferSizeInBit);
    fifoAllocation.setSourceBuffer(fifoBuffer);
    fifoAllocation.setTargetBuffer(fifoBuffer);

<<<<<<<< HEAD:plugins/org.preesm.algorithm/src/org/preesm/algorithm/synthesis/memalloc/SimplePiMMMemoryAllocation.java
    workingMem.getMainBuffer().getChildren().add(fifoBuffer);
========
    // We consider that a cluster will only have one physical buffer. A cluster is supposed to be run on one processing
    // element (PE), and this PE is supposed to have only one memory (a PE can be, at the maximum granularity, a compute
    // node with one RAM)
    mainBuffer.getChildren().add(fifoBuffer);

    // Updating the allocSize so that no buffers overlap each other
    allocSize += bufferSizeInBit;
>>>>>>>> 2121a213a (minor synthax fixes every where (sorry)):plugins/org.preesm.algorithm/src/org/preesm/algorithm/synthesis/memalloc/SrDAGLessMemoryAllocationScheduleSwitch.java
  }

  /**
   * This method checks if the current schedule is the root schedule.
   *
   * @param schedule
   *          the current schedule
   * @return true if the schedule is the root one, false otherwise.
   */
  boolean checkIfTopCluster(Schedule schedule) {
    if (schedule.getParent() != null) {
      if (schedule.getParent().hasAttachedActor()) {
        return false;
      }
      return checkIfTopCluster(schedule.getParent());
    }
    return true;
  }

}
