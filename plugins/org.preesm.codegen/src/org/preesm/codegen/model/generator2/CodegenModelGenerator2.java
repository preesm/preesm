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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.FifoAllocation;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SendActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.Communication;
import org.preesm.codegen.model.CommunicationNode;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.Delimiter;
import org.preesm.codegen.model.Direction;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FifoOperation;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.SharedMemoryCommunication;
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
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PersistenceLevel;
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
import org.preesm.model.slam.SlamMessageRouteStep;
import org.preesm.model.slam.SlamRouteStep;

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

  private final Design     archi;
  private final PiGraph    algo;
  private final Scenario   scenario;
  private final Schedule   schedule;
  private final Mapping    mapping;
  private final Allocation memAlloc;

  private AllocationToCodegenBuffer memoryLinker;

  private final boolean papify;

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

    // 1- generate variables (and keep track of them with a linker)
    this.memoryLinker = AllocationToCodegenBuffer.link(memAlloc, schedule, scenario, algo);

    // 2- generate code
    generateCode(coreBlocks);

    // sort blocks
    final List<Block> resultList = coreBlocks.entrySet().stream()
        .sorted((e1, e2) -> e1.getKey().getHardwareId() - e2.getKey().getHardwareId()).map(Entry::getValue)
        .collect(Collectors.toList());

    // generate buffer definitions
    generateBuffers(coreBlocks);

    return Collections.unmodifiableList(resultList);
  }

  private void generateBuffers(final Map<ComponentInstance, CoreBlock> coreBlocks) {
    final List<Buffer> buffers = this.memoryLinker.getCodegenBuffers();
    for (Buffer buffer : buffers) {
      generateBuffer(coreBlocks, buffer);
    }
  }

  private void generateBuffer(final Map<ComponentInstance, CoreBlock> coreBlocks, final Buffer mainBuffer) {
    final org.preesm.algorithm.memalloc.model.Buffer key = this.memoryLinker.getAllocationBuffer(mainBuffer);
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

  private void recursiveSetBufferCreator(final Variable variable, final CoreBlock correspondingOperatorBlock,
      final boolean isLocal) {
    // Set the creator for the current buffer
    variable.reaffectCreator(correspondingOperatorBlock);
    if (variable instanceof Buffer) {
      final Buffer buffer = (Buffer) variable;
      buffer.setLocal(isLocal);
      // Do the same recursively for all its children subbuffers
      for (final SubBuffer subBuffer : buffer.getChildrens()) {
        recursiveSetBufferCreator(subBuffer, correspondingOperatorBlock, isLocal);
      }
    }
  }

  private void generateCode(final Map<ComponentInstance, CoreBlock> coreBlocks) {
    // iterate in order

    final List<AbstractActor> actors = new ScheduleOrderManager(this.schedule).buildScheduleAndTopologicalOrderedList();
    for (final AbstractActor actor : actors) {
      final EList<ComponentInstance> actorMapping = this.mapping.getMapping(actor);
      final ComponentInstance componentInstance = actorMapping.get(0);
      final CoreBlock coreBlock = coreBlocks.get(componentInstance);

      if (actor instanceof Actor) {
        generateActorFiring((Actor) actor, this.memoryLinker.getPortToVariableMap(), coreBlock);
      } else if (actor instanceof UserSpecialActor) {
        generateSpecialActor((UserSpecialActor) actor, this.memoryLinker.getPortToVariableMap(), coreBlock);
      } else if (actor instanceof SrdagActor) {
        generateFifoCall((SrdagActor) actor, coreBlock);
      } else if (actor instanceof CommunicationActor) {
        generateCommunication((CommunicationActor) actor, coreBlock);
      } else {
        throw new PreesmRuntimeException("Unsupported actor [" + actor + "]");
      }
    }
  }

  private void generateCommunication(final CommunicationActor actor, final CoreBlock coreBlock) {
    // Create the communication
    final SharedMemoryCommunication newComm = CodegenModelUserFactory.eINSTANCE.createSharedMemoryCommunication();

    final Direction dir;
    final Delimiter delimiter;
    final PortDirection direction;
    if (actor instanceof SendActor) {
      dir = Direction.SEND;
      direction = PortDirection.NONE;
      if (actor instanceof SendStartActor) {
        delimiter = Delimiter.START;
      } else {
        delimiter = Delimiter.END;
      }
    } else {
      dir = Direction.RECEIVE;
      direction = PortDirection.NONE;
      if (actor instanceof ReceiveStartActor) {
        delimiter = Delimiter.START;
      } else {
        delimiter = Delimiter.END;
      }
    }

    newComm.setDirection(dir);
    newComm.setDelimiter(delimiter);
    final SlamRouteStep routeStep = actor.getRouteStep();
    if (routeStep instanceof SlamMessageRouteStep) {
      final SlamMessageRouteStep msgRouteStep = (SlamMessageRouteStep) routeStep;
      for (final ComponentInstance comp : msgRouteStep.getNodes()) {
        final CommunicationNode comNode = CodegenModelUserFactory.eINSTANCE.createCommunicationNode();
        comNode.setName(comp.getInstanceName());
        comNode.setType(comp.getComponent().getVlnv().getName());
        newComm.getNodes().add(comNode);
      }

      final Buffer buffer;

      // Find the corresponding DAGEdge buffer(s)
      final Fifo fifo = actor.getFifo();
      final FifoAllocation fifoAllocation = memAlloc.getFifoAllocations().get(fifo);

      if (actor instanceof SendActor) {
        final org.preesm.algorithm.memalloc.model.Buffer allocBuffer = fifoAllocation.getSourceBuffer();
        buffer = memoryLinker.getCodegenBuffer(allocBuffer);
      } else {
        final org.preesm.algorithm.memalloc.model.Buffer allocBuffer = fifoAllocation.getTargetBuffer();
        buffer = memoryLinker.getCodegenBuffer(allocBuffer);
      }
      newComm.setData(buffer);
      newComm.getParameters().clear();

      newComm.addParameter(buffer, direction);

      // Set the name of the communication
      // SS <=> Start Send
      // RE <=> Receive End
      String commName = "__" + buffer.getName();
      commName += "__" + coreBlock.getName();
      newComm.setName(((newComm.getDirection().equals(Direction.SEND)) ? "S" : "R")
          + ((newComm.getDelimiter().equals(Delimiter.START)) ? "S" : "E") + commName);

      registerCommunication(newComm, fifo, actor, routeStep);
      coreBlock.getLoopBlock().getCodeElts().add(newComm);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  private final Map<String, List<Communication>> communications = new LinkedHashMap<>();

  protected void registerCommunication(final Communication newCommmunication, final Fifo dagEdge,
      final CommunicationActor dagVertex, final SlamRouteStep routeStep) {
    // Retrieve the routeStep corresponding to the vertex.
    // In case of multi-step communication, this is the easiest
    // way to retrieve the target and source of the communication
    // corresponding to the current Send/ReceiveVertex

    String commID = routeStep.getSender().getInstanceName();
    commID += "__" + dagEdge.getSourcePort().getContainingActor().getName();
    commID += "___" + routeStep.getReceiver().getInstanceName();
    commID += "__" + dagEdge.getTargetPort().getContainingActor().getName();
    List<Communication> associatedCommunications = this.communications.get(commID);

    // Get associated Communications and set ID
    if (associatedCommunications == null) {
      associatedCommunications = new ArrayList<>();
      newCommmunication.setId(this.communications.size());
      this.communications.put(commID, associatedCommunications);
    } else {
      newCommmunication.setId(associatedCommunications.get(0).getId());
    }

    // Register other comm to the new
    for (final Communication com : associatedCommunications) {
      if (com.getDirection().equals(Direction.SEND)) {
        if (com.getDelimiter().equals(Delimiter.START)) {
          newCommmunication.setSendStart(com);
        }
        if (com.getDelimiter().equals(Delimiter.END)) {
          newCommmunication.setSendEnd(com);
        }
      }
      if (com.getDirection().equals(Direction.RECEIVE)) {
        if (com.getDelimiter().equals(Delimiter.START)) {
          newCommmunication.setReceiveStart(com);
        }
        if (com.getDelimiter().equals(Delimiter.END)) {
          newCommmunication.setReceiveEnd(com);
        }
      }
    }

    // Register new comm to its co-workers
    associatedCommunications.add(newCommmunication);
    for (final Communication com : associatedCommunications) {
      if (newCommmunication.getDirection().equals(Direction.SEND)) {
        if (newCommmunication.getDelimiter().equals(Delimiter.START)) {
          com.setSendStart(newCommmunication);
        }
        if (newCommmunication.getDelimiter().equals(Delimiter.END)) {
          com.setSendEnd(newCommmunication);
        }
      } else {
        if (newCommmunication.getDelimiter().equals(Delimiter.START)) {
          com.setReceiveStart(newCommmunication);
        }
        if (newCommmunication.getDelimiter().equals(Delimiter.END)) {
          com.setReceiveEnd(newCommmunication);
        }
      }
    }
  }

  private void generateFifoCall(SrdagActor actor, CoreBlock coreBlock) {
    // Create the Fifo call and set basic property
    final FifoCall fifoCall = CodegenModelUserFactory.eINSTANCE.createFifoCall();
    fifoCall.setName(actor.getName());

    final PortDirection dir;
    final InitActor initActor;
    if (actor instanceof InitActor) {
      initActor = (InitActor) actor;
      fifoCall.setOperation(FifoOperation.POP);
      dir = PortDirection.OUTPUT;

    } else if (actor instanceof EndActor) {
      final EndActor endActor = (EndActor) actor;
      initActor = (InitActor) endActor.getInitReference();
      fifoCall.setOperation(FifoOperation.PUSH);
      dir = PortDirection.INPUT;

    } else {
      throw new PreesmRuntimeException();
    }

    final Buffer buffer = (Buffer) memoryLinker.getPortToVariableMap().get(actor.getDataPort());
    fifoCall.addParameter(buffer, dir);

    final org.preesm.algorithm.memalloc.model.Buffer buffer2 = memAlloc.getDelayAllocations().get(initActor);
    final Buffer delayBuffer = memoryLinker.getCodegenBuffer(buffer2);

    buffer.getUsers().add(coreBlock);
    delayBuffer.getUsers().add(coreBlock);
    fifoCall.setHeadBuffer(delayBuffer);
    fifoCall.setBodyBuffer(null);

    // Create the INIT call (only the first time the fifo is encountered)
    // @farresti:
    // the management of local/none and permanent delays is a bit dirty here.
    // Ideally, this should be called only for permanent delay.
    // local/none delays should only have a call to the init function, no need for pop/push.
    // The case where the end vertex is alone should not be considered as it means that the tokens convoyed by the
    // delay are discarded.
    // Actually, only the permanent delays really need the pop/push mechanism.
    if (fifoCall.getOperation().equals(FifoOperation.POP)) {
      final FifoCall fifoInitCall = CodegenModelUserFactory.eINSTANCE.createFifoCall();
      fifoInitCall.setOperation(FifoOperation.INIT);
      fifoInitCall.setFifoHead(fifoCall);
      fifoInitCall.setName(fifoCall.getName());
      fifoInitCall.setHeadBuffer(fifoCall.getHeadBuffer());
      fifoInitCall.setBodyBuffer(fifoCall.getBodyBuffer());
      final PersistenceLevel level = initActor.getPersistenceLevel();
      if (level == null || PersistenceLevel.PERMANENT.equals(level)) {
        coreBlock.getInitBlock().getCodeElts().add(fifoInitCall);
      } else {
        coreBlock.getLoopBlock().getCodeElts().add(fifoInitCall);
      }
    }
    // Add the Fifo call to the loop of its coreBlock
    coreBlock.getLoopBlock().getCodeElts().add(fifoCall);
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
      lastBuffer = this.memoryLinker.getCodegenBuffer(memAlloc.getFifoAllocations().get(uniqueFifo).getSourceBuffer());
    } else if (actor instanceof ForkActor) {
      specialCall.setType(SpecialType.FORK);
      uniqueFifo = actor.getDataInputPorts().get(0).getFifo();
      lastBuffer = this.memoryLinker.getCodegenBuffer(memAlloc.getFifoAllocations().get(uniqueFifo).getTargetBuffer());
    } else if (actor instanceof BroadcastActor) {
      specialCall.setType(SpecialType.BROADCAST);
      uniqueFifo = actor.getDataInputPorts().get(0).getFifo();
      lastBuffer = this.memoryLinker.getCodegenBuffer(memAlloc.getFifoAllocations().get(uniqueFifo).getTargetBuffer());
    } else if (actor instanceof RoundBufferActor) {
      specialCall.setType(SpecialType.ROUND_BUFFER);
      uniqueFifo = actor.getDataInputPorts().get(0).getFifo();
      lastBuffer = this.memoryLinker.getCodegenBuffer(memAlloc.getFifoAllocations().get(uniqueFifo).getTargetBuffer());
    } else {
      throw new PreesmRuntimeException("special actor " + actor + " has an unknown special type");
    }

    // Add it to the specialCall
    if (actor instanceof JoinActor) {
      specialCall.addOutputBuffer(lastBuffer);
      actor.getDataInputPorts().stream().map(port -> ((Buffer) portToVariable.get(port)))
          .forEach(specialCall::addInputBuffer);
    } else {
      specialCall.addInputBuffer(lastBuffer);
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
      registerCallVariableToCoreBlock(coreBlock, loop);
    }
  }
}
