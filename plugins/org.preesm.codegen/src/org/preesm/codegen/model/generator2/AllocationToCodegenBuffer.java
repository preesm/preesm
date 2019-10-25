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
package org.preesm.codegen.model.generator2;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.memalloc.model.util.MemoryAllocationSwitch;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.scenario.Scenario;

/**
 *
 * @author anmorvan
 *
 */
public class AllocationToCodegenBuffer extends MemoryAllocationSwitch<Boolean> {

  /**
   *
   */
  public static final AllocationToCodegenBuffer link(Allocation memAlloc, Schedule schedule, Scenario scenario,
      PiGraph algo) {
    final AllocationToCodegenBuffer allocationToCodegenBuffer = new AllocationToCodegenBuffer(memAlloc, schedule,
        scenario, algo);
    allocationToCodegenBuffer.link();
    return allocationToCodegenBuffer;
  }

  private final Scenario   scenario;
  private final PiGraph    algo;
  private final Allocation memAlloc;
  private final Schedule   schedule;

  /**
   *
   */
  private AllocationToCodegenBuffer(Allocation memAlloc, Schedule schedule, Scenario scenario, PiGraph algo) {
    this.memAlloc = memAlloc;
    this.schedule = schedule;
    this.scenario = scenario;
    this.algo = algo;
  }

  /**
   *
   */
  private void link() {
    this.doSwitch(this.memAlloc);

    // link variables for Fifos and set names
    final List<
        AbstractActor> orderedList = new ScheduleOrderManager(algo, schedule).buildScheduleAndTopologicalOrderedList();
    for (final AbstractActor actor : orderedList) {
      final List<Fifo> fifos = actor.getDataInputPorts().stream().map(DataPort::getFifo).collect(Collectors.toList());
      for (final Fifo fifo : fifos) {
        final FifoAllocation fifoAllocation = this.memAlloc.getFifoAllocations().get(fifo);

        if (fifoAllocation == null) {
          throw new PreesmRuntimeException("Fifo [" + fifo.getId() + "] has no allocation.");
        }

        final org.preesm.algorithm.memalloc.model.Buffer srcBuffer = fifoAllocation.getSourceBuffer();
        final org.preesm.algorithm.memalloc.model.Buffer tgtBuffer = fifoAllocation.getTargetBuffer();
        final Buffer srcCodegenBuffer = this.btb.get(srcBuffer);
        final Buffer tgtCodegenBuffer = this.btb.get(tgtBuffer);

        final long allocSize = srcCodegenBuffer.getSize();
        final long typeSize = scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType());

        if (tgtCodegenBuffer != srcCodegenBuffer) {
          // generate 2 codegen buffers and route
          tgtCodegenBuffer.setName(generateUniqueBufferName("tgt_" + fifo.getTargetPort().getId()));
          srcCodegenBuffer.setName(generateUniqueBufferName("src_" + fifo.getSourcePort().getId()));
          srcCodegenBuffer.setType(fifo.getType());

          if (allocSize % typeSize != 0) {
            throw new PreesmRuntimeException("Buffer size in bytes is not a multiple of its type sizes.");
          }
          srcCodegenBuffer.setSize(allocSize / typeSize);
          srcCodegenBuffer.setTypeSize(typeSize);

          final String scomment = fifo.getSourcePort().getId();
          srcCodegenBuffer.setComment(scomment);

          this.portToVariable.put(fifo.getSourcePort(), srcCodegenBuffer);

          // TODO handle all route buffers
        } else {
          this.portToVariable.put(fifo.getSourcePort(), tgtCodegenBuffer);

          // XXX old style naming
          tgtCodegenBuffer.setName(
              generateUniqueBufferName(fifo.getSourcePort().getName() + "__" + fifo.getTargetPort().getName()));
        }

        tgtCodegenBuffer.setType(fifo.getType());

        tgtCodegenBuffer.setSize(allocSize / typeSize);
        tgtCodegenBuffer.setTypeSize(typeSize);

        final String tcomment = fifo.getTargetPort().getId();
        tgtCodegenBuffer.setComment(tcomment);

        this.portToVariable.put(fifo.getTargetPort(), tgtCodegenBuffer);
      }
    }
    // link variables for Delays and set names
    for (final Entry<InitActor, org.preesm.algorithm.memalloc.model.Buffer> delayAllocation : this.memAlloc
        .getDelayAllocations()) {
      final InitActor initActor = delayAllocation.getKey();
      final Fifo fifo = initActor.getDataPort().getFifo();
      final org.preesm.algorithm.memalloc.model.Buffer buffer = delayAllocation.getValue();
      final Buffer codegenBuffer = this.btb.get(buffer);

      // XXX old naming
      final String sink = initActor.getName();
      final String source = initActor.getEndReference().getName();
      final String comment = source + " > " + sink;
      codegenBuffer.setComment(comment);

      final String name = source + "__" + sink;
      final String uniqueName = generateUniqueBufferName(MemoryExclusionGraph.FIFO_HEAD_PREFIX + name);
      codegenBuffer.setName(uniqueName);
      codegenBuffer.setType(fifo.getType());
      final long allocSize = codegenBuffer.getSize();
      final long typeSize = scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType());
      codegenBuffer.setSize(allocSize / typeSize);
      codegenBuffer.setTypeSize(typeSize);
    }

    final EList<AbstractActor> allActors = this.algo.getAllActors();
    for (final AbstractActor actor : allActors) {
      for (final ConfigInputPort cip : actor.getConfigInputPorts()) {
        final ISetter setter = cip.getIncomingDependency().getSetter();
        if (setter instanceof Parameter) {
          final long evaluate = ((Parameter) setter).getValueExpression().evaluate();
          portToVariable.put(cip, CodegenModelUserFactory.eINSTANCE.createConstant(cip.getName(), evaluate));
        } else {
          throw new PreesmRuntimeException();
        }
      }
    }
  }

  private final Deque<Buffer>                                     codegenBufferStack = new LinkedList<>();
  private final Deque<org.preesm.algorithm.memalloc.model.Buffer> allocBufferStack   = new LinkedList<>();

  private final BidiMap<org.preesm.algorithm.memalloc.model.Buffer, Buffer> btb            = new DualHashBidiMap<>();
  private final Map<Port, Variable>                                         portToVariable = new LinkedHashMap<>();

  // for generating unique names
  private final Map<String, Long> bufferNames = new LinkedHashMap<>();

  private String generateUniqueBufferName(final String name) {
    final String candidate = name.replace(".", "_").replace("-", "_");
    long idx;
    String key = candidate;
    if (key.length() > 58) {
      key = key.substring(0, 58);
    }
    if (this.bufferNames.containsKey(key)) {
      idx = this.bufferNames.get(key);
    } else {
      idx = 0;
      this.bufferNames.put(key, idx);
    }

    final String bufferName = key + "__" + idx;
    idx += 1;
    this.bufferNames.put(key, idx);
    return bufferName;
  }

  @Override
  public Boolean caseAllocation(final Allocation alloc) {
    // init internal variables with physical buffer children
    alloc.getPhysicalBuffers().forEach(this::doSwitch);
    return true;
  }

  @Override
  public Boolean caseLogicalBuffer(final LogicalBuffer logicalBuffer) {
    final SubBuffer subBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    subBuffer.setSize(logicalBuffer.getSize());
    final long offset = logicalBuffer.getOffset();
    subBuffer.setOffset(offset);

    this.btb.put(logicalBuffer, subBuffer);
    this.codegenBufferStack.push(subBuffer);
    this.allocBufferStack.push(logicalBuffer);

    final org.preesm.algorithm.memalloc.model.Buffer memory = logicalBuffer.getContainingBuffer();
    final Buffer buffer = this.btb.get(memory);
    subBuffer.reaffectContainer(buffer);

    for (final org.preesm.algorithm.memalloc.model.Buffer l : logicalBuffer.getChildren()) {
      doSwitch(l);
    }

    this.codegenBufferStack.pop();
    this.allocBufferStack.pop();
    return true;
  }

  @Override
  public Boolean casePhysicalBuffer(final PhysicalBuffer phys) {
    final Buffer mainBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
    mainBuffer.setSize(phys.getSize());
    mainBuffer.setName(phys.getMemoryBank().getInstanceName());
    mainBuffer.setType("char");
    mainBuffer.setTypeSize(1); // char is 1 byte
    this.btb.put(phys, mainBuffer);
    this.codegenBufferStack.push(mainBuffer);
    this.allocBufferStack.push(phys);

    for (final org.preesm.algorithm.memalloc.model.Buffer l : phys.getChildren()) {
      doSwitch(l);
    }

    this.codegenBufferStack.pop();
    this.allocBufferStack.pop();
    return true;
  }

  public List<Buffer> getCodegenBuffers() {
    return new ArrayList<>(this.btb.values());
  }

  public org.preesm.algorithm.memalloc.model.Buffer getAllocationBuffer(Buffer codegenBuffer) {
    return this.btb.getKey(codegenBuffer);
  }

  public Buffer getCodegenBuffer(org.preesm.algorithm.memalloc.model.Buffer allocationBuffer) {
    return this.btb.get(allocationBuffer);
  }

  public Map<Port, Variable> getPortToVariableMap() {
    return this.portToVariable;
  }
}
