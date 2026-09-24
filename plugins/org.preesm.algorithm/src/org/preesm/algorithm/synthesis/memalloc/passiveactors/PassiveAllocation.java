package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.memalloc.model.AbstractAllocation;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.Buffer;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.memalloc.model.WorkingMemory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveInputPort;
import org.preesm.model.pisdf.PassiveOutputPort;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SimplePassiveActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

public class PassiveAllocation {
  PiGraph  graph;
  Scenario scenario;

  List<PassiveActor> passiveFifos;

  public PassiveAllocation(PiGraph graph, Scenario scenario) {
    this.graph = graph;
    this.scenario = scenario;
  }

  public AbstractAllocation makeAllocation() {
    addSimplePassiveActors(graph);
    pruneSimplePassiveActors(graph);

    AbstractAllocation result;
    if (graph.getContainingPiGraph() == null) {
      result = MemoryAllocationFactory.eINSTANCE.createAllocation();
    } else {
      result = MemoryAllocationFactory.eINSTANCE.createWorkingMemory();
    }
    createAllocationFromPassiveIR(graph, result);
    return result;
  }

  private void addSimplePassiveActors(PiGraph graph) {
    for (final PiGraph child : graph.getChildrenGraphs()) {
      addSimplePassiveActors(child);
    }

    for (final Fifo fifo : graph.getFifos()) {
      final SimplePassiveActor passiveFifo = PiMMUserFactory.instance.createSimplePassiveActor(fifo);
      graph.addActor(passiveFifo);
      final DataInputPort targetPort = fifo.getTargetPort();
      fifo.setTargetPort(passiveFifo.getDataInputPorts().getFirst());
      final Fifo outFifo = PiMMUserFactory.instance.createFifo(passiveFifo.getDataOutputPorts().getFirst(), targetPort,
          fifo.getType());
      graph.addFifo(outFifo);
      passiveFifos.add(passiveFifo);
    }
  }

  private void pruneSimplePassiveActors(PiGraph graph) {
    for (final PiGraph child : graph.getChildrenGraphs()) {
      pruneSimplePassiveActors(child);
    }

    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);

    for (final PassiveActor complexPa : graph.getPassiveActors().stream().filter(a -> !a.isSimple()).toList()) {
      for (final PassivePort pp : complexPa.getAllPassivePorts()) {
        if (PassiveActorVerifier.verifyPassivePortConditions(pp, brv)
            && pp.getOppositePort().getContainingActor() instanceof final SimplePassiveActor passiveFifo) {
          pp.setLinkedFifo(passiveFifo.getLinkedFifo());
          final Fifo oppositeFifo = pp instanceof PassiveInputPort
              ? passiveFifo.getDataInputPorts().getFirst().getFifo()
              : passiveFifo.getDataOutputPorts().getFirst().getFifo();
          graph.removeFifo(pp.getFifo());
          graph.removeActor(passiveFifo);
          if (pp instanceof final PassiveInputPort pip) {
            oppositeFifo.setTargetPort(pip);
          } else {
            oppositeFifo.setSourcePort((PassiveOutputPort) pp);
          }
        }
      }
    }
  }

  private void createAllocationFromPassiveIR(PiGraph currentGraph, AbstractAllocation currentAlloc) {

    final Buffer parentBuffer;
    long currentOffset = 0L;

    if (currentAlloc instanceof final Allocation mainAlloc) {
      final List<PhysicalBuffer> physicalBuffers = mainAlloc.getPhysicalBuffers();
      final ComponentInstance mainComNode = this.scenario.getSimulationInfo().getMainComNode();
      parentBuffer = physicalBuffers.stream().filter(b -> b.getMemoryBank() == mainComNode).toList().getFirst();
      if (parentBuffer == null) {
        throw new PreesmRuntimeException(
            "currentAlloc is an Allocation, and should have a physical buffer instanciated.");
      }
    } else {
      final WorkingMemory workMem = (WorkingMemory) currentAlloc;
      parentBuffer = workMem.getMainBuffer();
    }

    for (final PassiveActor pa : currentGraph.getPassiveActors()) {
      final long bufferSize = pa.getBufferSize();
      final LogicalBuffer paBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
      paBuffer.setSizeInBit(bufferSize);
      paBuffer.setOffsetInBit(currentOffset);
      paBuffer.setContainingBuffer(parentBuffer);
      currentOffset += bufferSize;

      for (final PassivePort pp : pa.getAllPassivePorts()) {
        final long subBufferSize = pp.getSubBufferSize();
        final LogicalBuffer subBuffer = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
        subBuffer.setSizeInBit(subBufferSize);
        subBuffer.setOffsetInBit(pp.getOffset());
        subBuffer.setContainingBuffer(paBuffer);

        Fifo currentFifo = pp.getLinkedFifo();
        if (currentFifo == null) {
          currentFifo = PreesmCopyTracker.getOriginalSource(pp.getFifo());
        }

        final FifoAllocation fifoAlloc = MemoryAllocationFactory.eINSTANCE.createFifoAllocation();
        fifoAlloc.setFifo(currentFifo);
        fifoAlloc.setSourceBuffer(subBuffer);
        fifoAlloc.setTargetBuffer(subBuffer);
        currentAlloc.getFifoAllocations().put(currentFifo, fifoAlloc);
      }
    }

    // TODO : delays :D

    for (final PiGraph childGraph : currentGraph.getChildrenGraphs()) {

      // Creates and populates the child graph's working memory
      final WorkingMemory childMemory = MemoryAllocationFactory.eINSTANCE.createWorkingMemory();
      createAllocationFromPassiveIR(childGraph, childMemory);

      // Linking the child memory's main buffer with parent memory's main buffer
      currentAlloc.getActorsWorkingMemory().add(childMemory);
      final LogicalBuffer childMainBuffer = childMemory.getMainBuffer();
      childMainBuffer.setContainingBuffer(parentBuffer);
      childMainBuffer.setOffsetInBit(currentOffset);
      currentOffset += childMainBuffer.getSizeInBit();
    }

    parentBuffer.setSizeInBit(currentOffset);

  }
}
