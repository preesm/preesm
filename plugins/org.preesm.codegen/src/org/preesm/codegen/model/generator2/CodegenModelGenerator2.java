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

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.LogicalBuffer;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.memalloc.model.util.MemoryAllocationSwitch;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.iterator.SimpleScheduleIterator;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.codegen.model.util.VariableSorter;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.SrdagActor;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 *
 * @author anmorvan
 *
 */
public class CodegenModelGenerator2 {

  public static final List<Block> generate(final Design archi, final PiGraph algo, final Scenario scenario,
      final Schedule schedule, final Mapping mapping, final Allocation memAlloc, final boolean papify) {
    return new CodegenModelGenerator2(archi, algo, scenario, schedule, mapping, memAlloc, papify).generate();
  }

  private final Design      archi;
  private final PiGraph     algo;
  private final Scenario    scenario;
  private final Schedule    schedule;
  private final Mapping     mapping;
  private final Allocation  memAlloc;
  private AllocationVisitor allocation;
  private final boolean     papify;

  private CodegenModelGenerator2(final Design archi, final PiGraph algo, final Scenario scenario,
      final Schedule schedule, final Mapping mapping, final Allocation memAlloc, final boolean papify) {
    this.archi = archi;
    this.algo = algo;
    this.scenario = scenario;
    this.schedule = schedule;
    this.mapping = mapping;
    this.memAlloc = memAlloc;
    this.papify = papify;
  }

  /**
   * Allocation switch that create codegen buffers from the allocation buffers;
   */
  private class AllocationVisitor extends MemoryAllocationSwitch<Boolean> {

    private final Deque<Buffer>                                     codegenBufferStack = new LinkedList<>();
    private final Deque<org.preesm.algorithm.memalloc.model.Buffer> allocBufferStack   = new LinkedList<>();

    final BidiMap<org.preesm.algorithm.memalloc.model.Buffer, Buffer> btb            = new DualHashBidiMap<>();
    final Map<Port, Variable>                                         portToVariable = new LinkedHashMap<>();

    @Override
    public Boolean caseAllocation(final Allocation alloc) {
      // init internal variables with physical buffer children
      alloc.getPhysicalBuffers().forEach(this::doSwitch);

      // create variables for Fifos
      for (final Entry<Fifo, FifoAllocation> fifoAllocationEntry : alloc.getFifoAllocations()) {
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
          srcCodegenBuffer.setTypeSize(
              CodegenModelGenerator2.this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));

          final String scomment = fifo.getSourcePort().getId();
          srcCodegenBuffer.setComment(scomment);

          this.portToVariable.put(fifo.getSourcePort(), srcCodegenBuffer);

          final EMap<ComponentInstance,
              org.preesm.algorithm.memalloc.model.Buffer> routeBuffers = fifoAllocation.getRouteBuffers();
          for (final Entry<ComponentInstance,
              org.preesm.algorithm.memalloc.model.Buffer> routeBufferEntry : routeBuffers) {
            // TODO

          }
        } else {
          this.portToVariable.put(fifo.getSourcePort(), tgtCodegenBuffer);
          tgtPrefix = "";
        }

        tgtCodegenBuffer.setName(generateUniqueBufferName(tgtPrefix + fifo.getTargetPort().getId()));
        tgtCodegenBuffer.setType(fifo.getType());
        tgtCodegenBuffer.setTypeSize(
            CodegenModelGenerator2.this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));

        final String tcomment = fifo.getTargetPort().getId();
        srcCodegenBuffer.setComment(tcomment);

        this.portToVariable.put(fifo.getTargetPort(), tgtCodegenBuffer);
      }

      // create variables for Delays
      for (final Entry<InitActor, org.preesm.algorithm.memalloc.model.Buffer> delayAllocation : alloc
          .getDelayAllocations()) {
        final InitActor initActor = delayAllocation.getKey();
        final Fifo fifo = initActor.getDataPort().getFifo();
        final org.preesm.algorithm.memalloc.model.Buffer buffer = delayAllocation.getValue();
        final Buffer codegenBuffer = this.btb.get(buffer);

        codegenBuffer.setName("delay_" + generateUniqueBufferName(fifo.getId()));
        codegenBuffer.setType(fifo.getType());
        codegenBuffer.setTypeSize(
            CodegenModelGenerator2.this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));

        final String comment = "Delay : " + initActor.getEndReference().getName() + " -> " + initActor.getName();
        codegenBuffer.setComment(comment);
      }

      return true;
    }

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
    public Boolean caseLogicalBuffer(final LogicalBuffer logicalBuffer) {
      final SubBuffer subBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
      subBuffer.setSize(logicalBuffer.getSize());
      subBuffer.setOffset(logicalBuffer.getOffset());

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
  }

  private List<Block> generate() {
    final String msg = "Starting codegen2 with papify set to " + papify;
    PreesmLogger.getLogger().log(Level.FINE, msg);

    final Map<ComponentInstance, CoreBlock> coreBlocks = new LinkedHashMap<>();

    // 0- init blocks
    final EList<ComponentInstance> cmps = this.archi.getOperatorComponentInstances();
    for (final ComponentInstance cmp : cmps) {
      final CoreBlock createCoreBlock = CodegenModelUserFactory.eINSTANCE.createCoreBlock(cmp);
      coreBlocks.put(cmp, createCoreBlock);
    }

    // 1- generate variables
    final Map<Port, Variable> portToVariable = allocate();

    // 2- generate code
    generateCode(coreBlocks, portToVariable);

    // sort blocks
    final List<Block> resultList = coreBlocks.entrySet().stream()
        .sorted((e1, e2) -> e1.getKey().getHardwareId() - e2.getKey().getHardwareId()).map(Entry::getValue)
        .collect(Collectors.toList());

    // generate buffer definitions
    generateBuffers(coreBlocks);

    return Collections.unmodifiableList(resultList);
  }

  private void generateBuffers(final Map<ComponentInstance, CoreBlock> coreBlocks) {
    for (final Entry<?, Buffer> entry : this.allocation.btb.entrySet()) {
      final Buffer mainBuffer = entry.getValue();
      generateBuffer(coreBlocks, mainBuffer);
    }
  }

  private void generateBuffer(final Map<ComponentInstance, CoreBlock> coreBlocks, final Buffer mainBuffer) {
    final org.preesm.algorithm.memalloc.model.Buffer key = this.allocation.btb.getKey(mainBuffer);
    final PhysicalBuffer memoryBankObj = key.getBank();
    final String memoryBank = memoryBankObj.getMemoryBank().getInstanceName();

    // Identify the corresponding operator block.
    // (also find out if the Buffer is local (i.e. not shared between
    // several CoreBlock)
    CoreBlock correspondingOperatorBlock = null;
    final boolean isLocal;
    final String correspondingOperatorID;

    if (memoryBank.equalsIgnoreCase("shared_mem")) {
      // If the memory bank is shared, let the main operator
      // declare the Buffer.
      correspondingOperatorID = this.scenario.getSimulationInfo().getMainOperator().getInstanceName();
      isLocal = false;

      // Check that the main operator block exists.
      CoreBlock mainOperatorBlock = null;
      for (final Entry<ComponentInstance, CoreBlock> componentEntry : coreBlocks.entrySet()) {
        if (componentEntry.getKey().getInstanceName().equals(correspondingOperatorID)) {
          mainOperatorBlock = componentEntry.getValue();
        }
      }

      // If the main operator does not exist
      if (mainOperatorBlock == null) {
        // Create it
        mainOperatorBlock = CodegenModelUserFactory.eINSTANCE.createCoreBlock(null);
        final ComponentInstance componentInstance = this.archi.getComponentInstance(correspondingOperatorID);
        mainOperatorBlock.setName(componentInstance.getInstanceName());
        mainOperatorBlock.setCoreType(componentInstance.getComponent().getVlnv().getName());
        coreBlocks.put(componentInstance, mainOperatorBlock);
      }

    } else {
      // else, the operator corresponding to the memory bank will
      // do the work
      correspondingOperatorID = memoryBank;
      isLocal = true;
    }

    // Find the block
    for (final Entry<ComponentInstance, CoreBlock> componentEntry : coreBlocks.entrySet()) {
      if (componentEntry.getKey().getInstanceName().equals(correspondingOperatorID)) {
        correspondingOperatorBlock = componentEntry.getValue();
      }
    }
    // Recursively set the creator for the current Buffer and all its
    // subBuffer
    recursiveSetBufferCreator(mainBuffer, correspondingOperatorBlock, isLocal);
    sortDefinitions(correspondingOperatorBlock);
  }

  private void sortDefinitions(CoreBlock correspondingOperatorBlock) {
    if (correspondingOperatorBlock != null) {
      final EList<Variable> definitions = correspondingOperatorBlock.getDefinitions();
      ECollections.sort(definitions, new VariableSorter());
    }
  }

  private void recursiveSetBufferCreator(final Variable buffer, final CoreBlock correspondingOperatorBlock,
      final boolean isLocal) {
    // Set the creator for the current buffer
    buffer.reaffectCreator(correspondingOperatorBlock);
    if (buffer instanceof Buffer) {
      ((Buffer) buffer).setLocal(isLocal);
      // Do the same recursively for all its children subbuffers
      for (final SubBuffer subBuffer : ((Buffer) buffer).getChildrens()) {
        recursiveSetBufferCreator(subBuffer, correspondingOperatorBlock, isLocal);
      }
    }
  }

  private void generateCode(final Map<ComponentInstance, CoreBlock> coreBlocks,
      final Map<Port, Variable> portToVariable) {
    // iterate in order

    final List<AbstractActor> actors = new SimpleScheduleIterator(this.schedule).getOrderedList();
    for (final AbstractActor actor : actors) {
      final EList<ComponentInstance> actorMapping = this.mapping.getMapping(actor);
      final ComponentInstance componentInstance = actorMapping.get(0);
      final CoreBlock coreBlock = coreBlocks.get(componentInstance);

      if (actor instanceof Actor) {
        generateActorFiring((Actor) actor, portToVariable, coreBlock);
      } else if (actor instanceof UserSpecialActor) {
        generateSpecialActor((UserSpecialActor) actor, portToVariable, coreBlock);
      } else if (actor instanceof SrdagActor) {
        // TODO handle init/end
        // generateFifoCall((SrdagActor) actor, coreBlock);
        throw new PreesmRuntimeException("Unsupported actor [" + actor + "]");
      } else if (actor instanceof CommunicationActor) {
        // TODO handle send/receive + enabler triggers
        throw new PreesmRuntimeException("Unsupported actor [" + actor + "]");
      } else {
        throw new PreesmRuntimeException("Unsupported actor [" + actor + "]");
      }
    }
  }

  private void generateSpecialActor(final SpecialActor actor, final Map<Port, Variable> portToVariable,
      final CoreBlock operatorBlock) {
    final SpecialCall specialCall = CodegenModelUserFactory.eINSTANCE.createSpecialCall();
    specialCall.setName(actor.getName());

    final Fifo uniqueFifo;
    final Buffer lastBuffer;
    if (actor instanceof JoinActor) {
      specialCall.setType(SpecialType.JOIN);
      uniqueFifo = actor.getDataOutputPorts().get(0).getFifo();
      lastBuffer = allocation.btb.get(memAlloc.getFifoAllocations().get(uniqueFifo).getSourceBuffer());
    } else if (actor instanceof ForkActor) {
      specialCall.setType(SpecialType.FORK);
      uniqueFifo = actor.getDataInputPorts().get(0).getFifo();
      lastBuffer = allocation.btb.get(memAlloc.getFifoAllocations().get(uniqueFifo).getTargetBuffer());
    } else if (actor instanceof BroadcastActor) {
      specialCall.setType(SpecialType.BROADCAST);
      uniqueFifo = actor.getDataInputPorts().get(0).getFifo();
      lastBuffer = allocation.btb.get(memAlloc.getFifoAllocations().get(uniqueFifo).getTargetBuffer());
    } else if (actor instanceof RoundBufferActor) {
      specialCall.setType(SpecialType.ROUND_BUFFER);
      uniqueFifo = actor.getDataInputPorts().get(0).getFifo();
      lastBuffer = allocation.btb.get(memAlloc.getFifoAllocations().get(uniqueFifo).getTargetBuffer());
    } else {
      throw new PreesmRuntimeException("special actor " + actor + " has an unknown special type");
    }

    // Add it to the specialCall
    if (actor instanceof JoinActor) {
      specialCall.addOutputBuffer(lastBuffer);
      // actor.getDataInputPorts().stream().map(DataPort::getFifo).map(memAlloc.getFifoAllocations()::get)
      // .map(allocation.btb::get).forEach(specialCall::addInputBuffer);
      actor.getDataInputPorts().stream().map(port -> ((Buffer) portToVariable.get(port)))
          .forEach(specialCall::addInputBuffer);
    } else {
      specialCall.addInputBuffer(lastBuffer);
      // actor.getDataOutputPorts().stream().map(DataPort::getFifo).map(memAlloc.getFifoAllocations()::get)
      // .map(allocation.btb::get).forEach(specialCall::addOutputBuffer);
      actor.getDataOutputPorts().stream().map(port -> ((Buffer) portToVariable.get(port)))
          .forEach(specialCall::addOutputBuffer);
    }

    operatorBlock.getLoopBlock().getCodeElts().add(specialCall);

    registerCallVariableToCoreBlock(operatorBlock, specialCall);
  }

  protected void registerCallVariableToCoreBlock(final CoreBlock operatorBlock, final Call call) {
    // Register the core Block as a user of the function variable
    for (final Variable var : call.getParameters()) {
      // Currently, constants do not need to be declared nor
      // have creator since their value is directly used.
      // Consequently the used block can also be declared as the creator
      if (var instanceof Constant) {
        var.reaffectCreator(operatorBlock);
      }
      var.getUsers().add(operatorBlock);
    }
  }

  private void generateActorFiring(final Actor actor, final Map<Port, Variable> portToVariable,
      final CoreBlock coreBlock) {
    final Refinement refinement = actor.getRefinement();
    if (refinement instanceof CHeaderRefinement) {
      final FunctionPrototype initPrototype = ((CHeaderRefinement) refinement).getInitPrototype();
      if (initPrototype != null) {
        final ActorFunctionCall init = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall(actor, initPrototype,
            portToVariable);
        coreBlock.getInitBlock().getCodeElts().add(init);
      }
      final FunctionPrototype loopPrototype = ((CHeaderRefinement) refinement).getLoopPrototype();
      final ActorFunctionCall loop = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall(actor, loopPrototype,
          portToVariable);
      coreBlock.getLoopBlock().getCodeElts().add(loop);
    }
  }

  private Map<Port, Variable> allocate() {
    this.allocation = new AllocationVisitor();
    this.allocation.doSwitch(this.memAlloc);
    final Map<Port, Variable> portToVariable = this.allocation.portToVariable;

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
    return portToVariable;
  }

}
