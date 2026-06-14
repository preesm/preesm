package org.preesm.algorithm.clustering.synthesis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

public class SimpleAllocationHeuristic extends AllocationHeuristic {

  Map<AbstractVertex, Long> clusterBrv;

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);

    /* Here, graph = cluster. We keep "graph" to match the override */
    clusterBrv = PiBRV.compute(graph, BRVMethod.LCM);

  }

  @Override
  public Allocation allocate(PiGraph cluster, Schedule clusterSchedule, Mapping clusterMapping) {

    final Allocation clusterAllocation = MemoryAllocationFactory.eINSTANCE.createAllocation();
    final PhysicalBuffer mainBuffer = MemoryAllocationFactory.eINSTANCE.createPhysicalBuffer();

    // TODO : awfull
    ComponentInstance componentInstance = arch
        .getComponentInstance(clusterMapping.getAllInvolvedComponentInstances().get(0).toString());
    if (componentInstance == null) {
      componentInstance = scenario.getSimulationInfo().getMainComNode();
    }
    clusterAllocation.getPhysicalBuffers().add(mainBuffer);
    mainBuffer.setMemoryBank(componentInstance);

    final long finalBitSize = recursiveAllocation(cluster, clusterSchedule, clusterMapping, clusterAllocation, 0);

    mainBuffer.setSizeInBit(finalBitSize);

    return clusterAllocation;

  }

  /**
   * a {@link Schedule schedule} can be recursive, and because the {@link Allocation allocation} of a cluster is based
   * on the schedule in this {@link SimpleAllocationHeuristic heuristic}, the {@link Allocation allocation} has to be
   * built recursively.
   *
   * @param cluster
   *          the current cluster
   * @param subSchedule
   *          the current {@link Schedule schedule} to study. It can be a scope containing other schedules, or a list of
   *          actors that can be executed in parallel or sequentially depending on the type of {@link Schedule
   *          subSchedule}.
   * @param clusterMapping
   *          the mapping of the cluster.
   * @return the {@link Allocation allocation} based on {@link Schedule subSchedule}
   */
  private long recursiveAllocation(PiGraph cluster, Schedule subSchedule, Mapping clusterMapping,
      Allocation clusterAllocation, long offset) {

    final long size = 0;

    // TODO handle multiple memory banks (useful in a cluster scope ?)
    final PhysicalBuffer mainBuffer = clusterAllocation.getPhysicalBuffers().get(0);

    for (final Schedule schedule : subSchedule.getChildren()) {

      if (schedule instanceof final SequentialActorSchedule actorSchedule) {

        /* The repetition of the current scope */
        long totalScopeRepetition = actorSchedule.getRepetition();
        Schedule parent = schedule.getParent();
        while (parent != null) {
          totalScopeRepetition *= parent.getRepetition();
          parent = parent.getParent();
        }

        /* All the actor in the scope */
        final List<AbstractActor> actors = actorSchedule.getActorList();

        // Set used to track already computed fifos
        final Set<Fifo> trackedFifos = new HashSet<>();

        for (final AbstractActor actor : actors) {

          for (final DataOutputPort port : actor.getDataOutputPorts()) {

            final Fifo fifo = port.getFifo();

            if (trackedFifos.contains(fifo)) {
              continue;
            }

            // Max token produced by source in the scope
            final long sourceRate = port.getExpression().evaluateAsLong() * clusterBrv.get(actor)
                / totalScopeRepetition;

            // Max token consumed by target in the scope
            final long targetRate = fifo.getTargetPort().getExpression().evaluateAsLong()
                * clusterBrv.get(fifo.getTarget()) / totalScopeRepetition;

            // Computing the buffer size in bit
            final long bufferSizeInBit = scenario.getSimulationInfo().getBufferSizeInBit(fifo.getType(),
                Math.max(sourceRate, targetRate));

            // Making the allocation
            final FifoAllocation fifoAllocation = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
            fifoAllocation.setFifo(fifo);
            clusterAllocation.getFifoAllocations().put(fifo, fifoAllocation);
            final LogicalBuffer fifoBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
            fifoAllocation.setSourceBuffer(fifoBuffer);
            fifoAllocation.setTargetBuffer(fifoBuffer);
            mainBuffer.getChildren().add(fifoBuffer);
            fifoBuffer.setOffsetInBit(offset);
            fifoBuffer.setSizeInBit(bufferSizeInBit);
            offset += bufferSizeInBit;
            // TODO : handle delays in allocation

          }
        }
      } else {
        offset = recursiveAllocation(cluster, schedule, clusterMapping, clusterAllocation, offset);
      }
    }
    return offset;
  }
}
