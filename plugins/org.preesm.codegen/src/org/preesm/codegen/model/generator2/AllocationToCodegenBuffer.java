package org.preesm.codegen.model.generator2;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.memalloc.model.util.MemoryAllocationSwitch;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 *
 * @author anmorvan
 *
 */
public class AllocationToCodegenBuffer extends MemoryAllocationSwitch<Boolean> {

  /**
   *
   */
  public static final AllocationToCodegenBuffer link(Allocation memAlloc, Scenario scenario, PiGraph algo) {
    final AllocationToCodegenBuffer allocationToCodegenBuffer = new AllocationToCodegenBuffer(memAlloc, scenario, algo);
    allocationToCodegenBuffer.link();
    return allocationToCodegenBuffer;
  }

  private final Scenario   scenario;
  private final PiGraph    algo;
  private final Allocation memAlloc;

  /**
   *
   */
  private AllocationToCodegenBuffer(Allocation memAlloc, Scenario scenario, PiGraph algo) {
    this.memAlloc = memAlloc;
    this.scenario = scenario;
    this.algo = algo;
  }

  /**
   *
   */
  private void link() {
    this.doSwitch(this.memAlloc);

    // link variables for Fifos and set names
    for (final Entry<Fifo, FifoAllocation> fifoAllocationEntry : this.memAlloc.getFifoAllocations()) {
      final Fifo fifo = fifoAllocationEntry.getKey();
      final FifoAllocation fifoAllocation = fifoAllocationEntry.getValue();

      final org.preesm.algorithm.memalloc.model.Buffer srcBuffer = fifoAllocation.getSourceBuffer();
      final org.preesm.algorithm.memalloc.model.Buffer tgtBuffer = fifoAllocation.getTargetBuffer();
      final Buffer srcCodegenBuffer = this.btb.get(srcBuffer);
      final Buffer tgtCodegenBuffer = this.btb.get(tgtBuffer);

      final String tgtPrefix;
      if (tgtCodegenBuffer != srcCodegenBuffer) {
        // generate 2 codegen buffers and route
        tgtPrefix = "tgt_";
        srcCodegenBuffer.setName(generateUniqueBufferName("src_" + fifo.getSourcePort().getId()));
        srcCodegenBuffer.setType(fifo.getType());
        srcCodegenBuffer.setTypeSize(scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));

        final String scomment = fifo.getSourcePort().getId();
        srcCodegenBuffer.setComment(scomment);

        this.portToVariable.put(fifo.getSourcePort(), srcCodegenBuffer);

        final EMap<ComponentInstance,
            org.preesm.algorithm.memalloc.model.Buffer> routeBuffers = fifoAllocation.getRouteBuffers();
        for (final Entry<ComponentInstance,
            org.preesm.algorithm.memalloc.model.Buffer> routeBufferEntry : routeBuffers) {
          // TODO
          throw new UnsupportedOperationException(routeBufferEntry.toString());

        }
      } else {
        this.portToVariable.put(fifo.getSourcePort(), tgtCodegenBuffer);
        tgtPrefix = "";
      }

      tgtCodegenBuffer.setName(generateUniqueBufferName(tgtPrefix + fifo.getTargetPort().getId()));
      tgtCodegenBuffer.setType(fifo.getType());
      tgtCodegenBuffer.setTypeSize(scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));

      final String tcomment = fifo.getTargetPort().getId();
      srcCodegenBuffer.setComment(tcomment);

      this.portToVariable.put(fifo.getTargetPort(), tgtCodegenBuffer);
    }

    // link variables for Delays and set names
    for (final Entry<InitActor, org.preesm.algorithm.memalloc.model.Buffer> delayAllocation : this.memAlloc
        .getDelayAllocations()) {
      final InitActor initActor = delayAllocation.getKey();
      final Fifo fifo = initActor.getDataPort().getFifo();
      final org.preesm.algorithm.memalloc.model.Buffer buffer = delayAllocation.getValue();
      final Buffer codegenBuffer = this.btb.get(buffer);

      codegenBuffer.setName("delay_" + generateUniqueBufferName(fifo.getId()));
      codegenBuffer.setType(fifo.getType());
      codegenBuffer.setTypeSize(scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));

      final String comment = "Delay : " + initActor.getEndReference().getName() + " -> " + initActor.getName();
      codegenBuffer.setComment(comment);
    }

    final Map<Port, Variable> portToVariable = this.portToVariable;

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
    if (key.length() > 35) {
      key = key.substring(0, 35);
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
    subBuffer.setOffset(logicalBuffer.getOffset());
    subBuffer.setTypeSize(logicalBuffer.getTypeSize());

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
    mainBuffer.setName("Shared_" + phys.getMemoryBank().getHardwareId());
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
