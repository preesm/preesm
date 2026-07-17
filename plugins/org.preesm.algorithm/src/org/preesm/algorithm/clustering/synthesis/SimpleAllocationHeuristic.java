package org.preesm.algorithm.clustering.synthesis;

import java.util.Map;
import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.slam.ComponentInstance;

/**
 *
 */
public class SimpleAllocationHeuristic extends AllocationHeuristic {

  private Map<AbstractVertex, Long> clusterBrv;
  private Map<AbstractVertex, Long> parentBrv;
  private PiGraph                   cluster;

  @Override
  public Allocation allocate(PiGraph cluster, Schedule clusterSchedule, Mapping mapping) {

    this.cluster = cluster;
    clusterBrv = PiBRV.compute(cluster, BRVMethod.LCM);
    parentBrv = PiBRV.compute(cluster.getContainingPiGraph(), BRVMethod.LCM);

    final Allocation clusterAllocation = MemoryAllocationFactory.eINSTANCE.createAllocation();
    final PhysicalBuffer mainBuffer = MemoryAllocationFactory.eINSTANCE.createPhysicalBuffer();

    // TODO: this works for mono-node only, change this when multi-node available
    final ComponentInstance memory = scenario.getSimulationInfo().getMainComNode();
    clusterAllocation.getPhysicalBuffers().add(mainBuffer);
    mainBuffer.setMemoryBank(memory);

    long finalBitSize = recursiveAllocation(clusterSchedule, clusterAllocation, 0);

    finalBitSize = interfaceAllocation(cluster, clusterAllocation, finalBitSize);

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
   * @param s
   *          the current {@link Schedule schedule} to study. It can be a scope containing other schedules, or a list of
   *          actors that can be executed in parallel or sequentially depending on the type of {@link Schedule
   *          subSchedule}.
   * @param clusterMapping
   *          the mapping of the cluster.
   * @return the final bit size, called offset during intermediate call of {@link recursiveAllocation}
   */
  private long recursiveAllocation(final Schedule s, final Allocation clusterAllocation, long offset) {

    if (s instanceof final ActorSchedule actorSchedule) {

      // We take care of standard actors
      for (final AbstractActor a : actorSchedule.getActorList()) {

        for (final DataOutputPort sourcePort : a.getDataOutputPorts()) {

          final long portExpr = sourcePort.getExpression().evaluateAsLong();
          final Fifo fifo = sourcePort.getFifo();
          AbstractActor target = fifo.getTarget();

          long bufferSize = 0;

          // Interface actors are not supposed to physically exists (they are here to ensure that there is a broadcast
          // or a roundbuffer actor to respect the encapsulation of the different hierarchical levels). If the next
          // actor is a interface actor, then the real targeted actor is the one linked to its graph port. The kept fifo
          // is the one inside the graph (it could be the other, but I had to make a choice).
          // For now, a cluster in a cluster is not taken into account. For that, we would have to check if the data
          // interface is linked to hierarchical actor or not.
          if (target instanceof final DataOutputInterface outputInterface) {

            final Fifo targetFifo = outputInterface.getGraphPort().getFifo();
            target = targetFifo.getTarget();
            final DataInputPort targetPort = targetFifo.getTargetPort();
            final long targetRep = parentBrv.get(target);

            // Because of encapsulation, this value is guaranteed to be equal to clusterRep * interfacePortExpr
            bufferSize = targetPort.getExpression().evaluateAsLong() * targetRep;

          } else {

            // I think we can't do worse in term of fifo sizing, but it will do the trick for now
            final long targetPortExpr = fifo.getTargetPort().getExpression().evaluateAsLong();
            bufferSize = Math.max(clusterBrv.get(a) * portExpr, clusterBrv.get(target) * targetPortExpr);
          }

          // Computing the buffer size in bit, making the allocation & updating the offset
          final long bufferSizeInBit = scenario.getSimulationInfo().getBufferSizeInBit(fifo.getType(), bufferSize);
          makeAllocation(fifo, bufferSizeInBit, clusterAllocation, offset);
          offset += bufferSizeInBit;
        }
      }
    } else {

      for (final Schedule child : s.getChildren()) {

        offset += recursiveAllocation(child, clusterAllocation, offset);
      }
    }
    return offset;
  }

  private long interfaceAllocation(final PiGraph cluster, final Allocation clusterAllocation, long offset) {

    // We only take care of DataInputInterfaces (DataOutputInterfaces have been processed in previous for loop)
    for (final DataInputInterface i : cluster.getDataInputInterfaces()) {

      final Fifo sourceFifo = i.getGraphPort().getFifo(); // outer cluster fifo
      final DataOutputPort sourcePort = sourceFifo.getSourcePort();
      final AbstractActor sourceActor = sourceFifo.getSource();
      final Fifo targetFifo = i.getDataPort().getFifo(); // inner cluster fifo
      final DataInputPort targetPort = targetFifo.getTargetPort();

      final long bufferSize = sourcePort.getExpression().evaluateAsLong() * parentBrv.get(sourceActor);
      final long bufferSizeInBit = scenario.getSimulationInfo().getBufferSizeInBit(targetFifo.getType(), bufferSize);
      makeAllocation(targetFifo, bufferSizeInBit, clusterAllocation, offset);
      offset += bufferSizeInBit;
    }
    return offset;
  }

  private void makeAllocation(final Fifo fifo, final long bufferSizeInBit, Allocation allocation, long offset) {

    // Creating the FifoAllocation
    final FifoAllocation fifoAllocation = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
    fifoAllocation.setFifo(fifo);
    allocation.getFifoAllocations().put(fifo, fifoAllocation);

    // Creating the LogicalBuffer of the fifo
    final LogicalBuffer fifoBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
    fifoBuffer.setOffsetInBit(offset);
    fifoBuffer.setSizeInBit(bufferSizeInBit);
    fifoAllocation.setSourceBuffer(fifoBuffer);
    fifoAllocation.setTargetBuffer(fifoBuffer);

    // TODO handle multiple memory banks (useful in a cluster scope ?)
    final PhysicalBuffer mainBuffer = allocation.getPhysicalBuffers().get(0);
    mainBuffer.getChildren().add(fifoBuffer);
  }
}
