package org.preesm.algorithm.synthesis.memalloc;

import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.MemoryAllocationFactory;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Allocate 1 physical buffer on the main com node and 1 logical buffer per Fifo in the graph.
 *
 * @author anmorvan
 *
 */
public class SimpleMemoryAllocation implements IMemoryAllocation {

  @Override
  public Allocation allocateMemory(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping) {

    final ComponentInstance mainComNode = scenario.getSimulationInfo().getMainComNode();

    final Allocation memAlloc = MemoryAllocationFactory.eINSTANCE.createAllocation();

    final PhysicalBuffer physBuff = MemoryAllocationFactory.eINSTANCE.createPhysicalBuffer();
    memAlloc.getPhysicalBuffers().add(physBuff);
    physBuff.setMemory(mainComNode);

    long totalSize = 0L;
    final EList<Fifo> fifos = piGraph.getFifos();
    for (final Fifo fifo : fifos) {
      final String fifoType = fifo.getType();
      final long dataTypeSize = scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifoType);
      final long fifoTokenSize = fifo.getTargetPort().getPortRateExpression().evaluate();
      final long fifoSize = dataTypeSize * fifoTokenSize;

      final LogicalBuffer logicBuff = MemoryAllocationFactory.eINSTANCE.createLogicalBuffer();
      logicBuff.setMemory(physBuff);
      logicBuff.setSize(fifoSize);
      logicBuff.setOffset(totalSize);

      memAlloc.getAllocations().put(fifo, logicBuff);

      totalSize += fifoSize;
    }

    physBuff.setSize(totalSize);
    return memAlloc;
  }

}
