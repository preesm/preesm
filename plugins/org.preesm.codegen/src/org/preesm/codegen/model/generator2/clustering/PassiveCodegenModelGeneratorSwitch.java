package org.preesm.codegen.model.generator2.clustering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHierarchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.CallFunctionBlock;
import org.preesm.codegen.model.ClusterBlock;
import org.preesm.codegen.model.CodeElt;
import org.preesm.codegen.model.CodegenFactory;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.FunctionBlock;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.IteratedBuffer;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.LoopFunctionBlock;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.SectionBlock;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PassiveActor;
import org.preesm.model.pisdf.PassiveElt;
import org.preesm.model.pisdf.PassivePort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.scenario.Scenario;

/**
 * Creates a intermediate codegen model, that is used by the printer to create the final code. For each cluster, a new
 * file is created, with in it the init function and the loop function of the cluster, as if it was a non-hierarchical
 * actor with a C refinement. It allows to reduce the size of the generated code, as the same instance of a cluster in
 * the SrDAG will share the same file.
 *
 * @author dgageot
 *
 */
public class PassiveCodegenModelGeneratorSwitch extends ScheduleSwitch<CodeElt>
    implements IClusterCodegenModelGenerator {

  /**
   * {@link Scenario} to get data size from.
   */
  private final Scenario scenario;

  /**
   * {@link FunctionBlock} to add generate init code element to.
   */
  private final CallFunctionBlock callFuncBlock;

  /**
   * {@link FunctionBlock} to add generate loop code element to.
   */
  private final LoopFunctionBlock loopFuncBlock;

  /**
   * Will be added in the coreBlock welcoming the call(s) of the cluster
   */
  private final List<Buffer> sinkFifoBuffers;

  /**
   * {@link Map} that registers every {@link AbstractActor} repetition with it's {@link IntVar}.
   */
  private final Map<Buffer, IntVar> iterMap;

  /**
   *
   */
  private final PiGraph originalCluster;

  /**
   * If the cluster to generate the model from contains parallelism information, it is specified in the
   * containParallelism variable.
   */
  private boolean parallelFiringsInside;

  /**
   * The input Schedule, on which the codegen is based on
   */
  Schedule schedule;

  /**
   * Alloc buffer to Codegen buffer
   */
  Map<PassiveElt, Buffer> p2b;

  /**
   * Alloc buffer to Codegen buffer
   */
  Map<DataInterface, Buffer> i2b;

  /**
   *
   */
  Buffer mainBuffer;

  /**
   *
   * @param originalCluster
   *          input cluster
   * @param scenario
   *          the global scenario
   */
  public PassiveCodegenModelGeneratorSwitch(final PiGraph originalCluster, final Scenario scenario,
      final Schedule schedule) {
    super();
    this.originalCluster = originalCluster;
    this.scenario = scenario;
    this.sinkFifoBuffers = new LinkedList<>();
    this.iterMap = new HashMap<>();
    this.parallelFiringsInside = false;
    this.callFuncBlock = CodegenModelUserFactory.eINSTANCE.createCallFunctionBlock();
    this.loopFuncBlock = CodegenModelUserFactory.eINSTANCE.createLoopFunctionBlock();
    callFuncBlock.setName(ClusteringHelper.getInitPrototypeName(originalCluster));
    loopFuncBlock.setName(ClusteringHelper.getLoopPrototypeName(originalCluster));
    p2b = new HashMap<>();
    i2b = new HashMap<>();

    this.schedule = schedule;
  }

  /*
   * ============================================================================================================
   *
   * PUBLIC
   *
   * ============================================================================================================
   */
  /**
   * Main function and class entry point. Generate and set code element for the corresponding cluster inside of
   * callFuncBlock and loopFuncBlock.
   */
  @Override
  public void generate() {

    // Compute the cluster function arguments
    generateClusterConfigParameters(originalCluster);
    generateParameterClusterBuffers(originalCluster);

    // Create memory buffers
    generateBuffers();

    // Print block from input schedule into callFuncBlock and loopFuncBlock
    final CodeElt cluster = doSwitch(schedule);

    // Is final cluster parallel
    if (cluster instanceof final ClusterBlock clusterBlock) {
      clusterBlock.setContainParallelism(parallelFiringsInside);
    }

    // Generate buffer definitions
    recursiveSetBufferCreator(this.mainBuffer, this.loopFuncBlock, false);

    // Add code
    loopFuncBlock.getCodeElts().add(cluster);
  }

  /**
   *
   * @return the call function block
   */
  @Override
  public CallFunctionBlock getCallFunctionBlock() {
    return callFuncBlock;
  }

  /**
   *
   * @return the loop function block
   */
  @Override
  public LoopFunctionBlock getLoopFunctionBlock() {
    return loopFuncBlock;
  }

  /*
   * ============================================================================================================
   *
   * CASES
   *
   * ============================================================================================================
   */
  @Override
  public CodeElt caseSequentialHierarchicalSchedule(final SequentialHierarchicalSchedule schedule) {

    // If parent node is a parallel node with one child? If true, repetition can be parallelized
    boolean parallelRepetition = false;
    final Schedule parentNode = schedule.getParent();
    if ((parentNode != null) && parentNode.isParallel() && (parentNode.getChildren().size() == 1)) {
      parallelRepetition = true;
    }

    // Retrieve cluster actor
    final Pair<CodeElt, ClusterBlock> outputPair = generateClusterBlock(schedule, parallelRepetition);

    // Explore and generate child schedule
    for (final Schedule e : schedule.getChildren()) {
      outputPair.getValue().getCodeElts().add(doSwitch(e));
    }
    return outputPair.getKey();
  }

  @Override
  public CodeElt caseParallelHierarchicalSchedule(final ParallelHierarchicalSchedule schedule) {

    // Is it a data parallelism node?
    if (schedule.getChildren().size() == 1 && !schedule.hasAttachedActor()) {
      return doSwitch(schedule.getChildren().get(0));
    }

    // Retrieve cluster actor
    final Pair<CodeElt, ClusterBlock> outputPair = generateClusterBlock(schedule, false);

    // Explore and generate child schedule
    for (final Schedule e : schedule.getChildren()) {
      if (schedule.getChildren().size() == 1) {
        outputPair.getValue().getCodeElts().add(doSwitch(e));
      } else {
        final SectionBlock sectionBlock = CodegenModelUserFactory.eINSTANCE.createSectionBlock();
        sectionBlock.getCodeElts().add(doSwitch(e));
        outputPair.getValue().getCodeElts().add(sectionBlock);
      }
    }

    return outputPair.getKey();
  }

  @Override
  public CodeElt caseActorSchedule(final ActorSchedule schedule) {

    // If parent node a parallel node with one child? If yes, repetition can be parallelize
    boolean parallelRepetition = false;
    final Schedule parentNode = schedule.getParent();
    if ((parentNode != null) && parentNode.isParallel() && (parentNode.getChildren().size() == 1)) {
      parallelRepetition = true;
    }

    // Retrieve actor to fire
    // clustering process does list actors in actor schedule, we only care about the first one here
    // It may work only with APGAN algorithm -> change this in the future if we use CHOCO solver to schedule clusters ?
    final List<AbstractActor> actors = ScheduleUtil.getAllReferencedActors(schedule);
    final AbstractActor actor = PreesmCopyTracker.getSource(actors.get(0));

    // Generate a LoopBlock to put function call element into
    final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
    final LoopBlock actorBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();

    // If actors has to be repeated few times, build a FiniteLoopBlock
    FiniteLoopBlock finiteLoopBlock = null;
    if (schedule.getRepetition() > 1) {
      finiteLoopBlock = generateFiniteLoopBlock(actorBlock, (int) schedule.getRepetition(), schedule,
          parallelRepetition);
      loopBlock.getCodeElts().add(finiteLoopBlock);
      if (parallelRepetition) {
        parallelFiringsInside = true;
      }
    } else {
      loopBlock.getCodeElts().add(actorBlock);
    }

    // Build corresponding actor function/special call
    if (actor instanceof final SpecialActor specialActor) {
      actorBlock.getCodeElts().add(generateSpecialActorFiring(specialActor, finiteLoopBlock));
    } else if (actor instanceof final Actor a) {
      actorBlock.getCodeElts().add(generateActorFiring(a, finiteLoopBlock));
    }

    // Add delay pop if necessary
    // TODO

    // store buffers on which MD5 can be computed to check validity of transformations
    if (actor.getDataOutputPorts().isEmpty()) {
      final EList<DataInputPort> dataInputPorts = actor.getDataInputPorts();
      for (final DataInputPort dip : dataInputPorts) {
        final Buffer buffer = retrieveAssociatedBuffer(dip);
        sinkFifoBuffers.add(buffer);
      }
    }
    return loopBlock;
  }

  /*
   * ============================================================================================================
   *
   * UTILS
   *
   * ============================================================================================================
   */

  /**
   * Generates the buffers, except the ones that are on the edge of the cluster (fifos connected to an interface rather
   * than a Passive Actor). The resulting buffers are stored on the p2b (passive to buffer) map, and a main buffer is
   * created that regroups all the fifo buffers of the cluster.
   */
  private void generateBuffers() {

    this.mainBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
    final long typeSizeInBit = scenario.getSimulationInfo().getDataTypeSizeInBit("char");
    final String name = "workingMem_" + originalCluster.getName();
    this.mainBuffer.setName(name);
    this.mainBuffer.setType("char");
    this.mainBuffer.setTokenTypeSizeInBit(typeSizeInBit);

    long finalSizeInBit = 0;

    this.p2b = new PassiveBufferGeneratorSwitch(scenario).doSwitch(originalCluster);

    for (final Entry<PassiveElt, Buffer> entry : this.p2b.entrySet()) {
      final Buffer buffer = entry.getValue();

      if (entry.getKey() instanceof final PassiveActor pa) {

        if (buffer instanceof final SubBuffer subBuffer) {

          subBuffer.setOffsetInBit(finalSizeInBit);
          subBuffer.setContainer(this.mainBuffer);
          finalSizeInBit += subBuffer.getSizeInBit();

        } else {
          PreesmLogger.getLogger().warning(
              "The buffer " + buffer.getName() + " attached to actor " + pa.getName() + " should be a SubBuffer");
        }
      }
    }

    this.mainBuffer.setNbToken(finalSizeInBit / typeSizeInBit);
  }

  /**
   * Set for the buffer and its sub buffers the creator block
   *
   * @param variable
   *          the top buffer
   * @param correspondingBlock
   *          the creator block
   * @param isLocal
   *          if it local or not
   */
  private void recursiveSetBufferCreator(final Variable variable, final Block correspondingBlock,
      final boolean isLocal) {
    // Set the creator for the current buffer
    variable.reaffectCreator(correspondingBlock);
    if (variable instanceof final Buffer buffer) {
      buffer.setLocal(isLocal);
      // Do the same recursively for all its children subbuffers
      for (final SubBuffer subBuffer : buffer.getChildrens()) {
        recursiveSetBufferCreator(subBuffer, correspondingBlock, isLocal);
      }
    }
  }

  /**
   * Generates a sub-cluster block
   *
   * @param schedule
   *          the current hierarchical schedule
   * @param parallelRepetition
   *          if the elements of the sub-cluster can be parallelized or not
   * @return a pair that contains : the finiteLoopBLock containing the clusterBlock if the sub-cluster repetition > 1,
   *         or the clusterBlock if the sub-cluster repetition == 1, and the clusterBlock.
   */
  private Pair<CodeElt, ClusterBlock> generateClusterBlock(final HierarchicalSchedule schedule,
      final boolean parallelRepetition) {

    // Build and fill ClusterBlock attributes
    final ClusterBlock clusterBlock = CodegenModelUserFactory.eINSTANCE.createClusterBlock();
    clusterBlock.setName(schedule.shortPrint());
    clusterBlock.setSchedule(schedule.shortPrint());
    clusterBlock.setParallel(schedule.isParallel());
    if (schedule.isParallel()) {
      parallelFiringsInside = true;
    }

    // If the cluster has to be repeated few times, build a FiniteLoopBlock
    CodeElt outputBlock = null;
    if (schedule.getRepetition() > 1) {
      outputBlock = generateFiniteLoopBlock(clusterBlock, (int) schedule.getRepetition(), schedule, parallelRepetition);
      if (parallelRepetition) {
        parallelFiringsInside = true;
      }
    } else {
      // Output the ClusterBlock
      outputBlock = clusterBlock;
    }

    return new ImmutablePair<>(outputBlock, clusterBlock);
  }

  /**
   * Generates a call to the refinement of the actor in the block
   *
   * @param actor
   *          the actor
   * @param flb
   *          the block
   * @return the function call
   */
  private final FunctionCall generateActorFiring(final Actor actor, final FiniteLoopBlock flb) {
    // Build FunctionCall
    final ActorFunctionCall functionCall = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall();
    functionCall.setActorName(actor.getName());
    functionCall.setOriActor(actor);

    // Retrieve Refinement from actor for loop function
    fillFunctionCallArguments(functionCall, actor, flb);

    // Retrieve and add init function to operator core block
    addInitFunctionCall(actor);

    return functionCall;
  }

  /**
   * Generates a call of a special actor in the block
   *
   * @param actor
   *          the special actor
   * @param flb
   *          the block
   * @return the special function call
   */
  private final SpecialCall generateSpecialActorFiring(final SpecialActor actor, final FiniteLoopBlock flb) {
    // Instantiate special call object
    final SpecialCall specialCall = CodegenModelUserFactory.eINSTANCE.createSpecialCall();

    // Set type of special call
    if (actor instanceof ForkActor) {
      specialCall.setType(SpecialType.FORK);
    } else if (actor instanceof JoinActor) {
      specialCall.setType(SpecialType.JOIN);
    } else if (actor instanceof BroadcastActor) {
      specialCall.setType(SpecialType.BROADCAST);
    } else if (actor instanceof RoundBufferActor) {
      specialCall.setType(SpecialType.ROUND_BUFFER);
    } else {
      throw new PreesmRuntimeException(
          "CodegenClusterModelGenerator: can't retrieve type of special actor [" + actor.getName() + "]");
    }

    // Retrieve associated fifo/buffer
    for (final DataPort dp : actor.getAllDataPorts()) {

      PassiveElt passiveElt;
      try {
        passiveElt = getAttachedPassiveElt(dp);
      } catch (final Exception e) {
        passiveElt = null;
      }
      Buffer associatedBuffer = retrieveAssociatedBuffer(dp);
      associatedBuffer = generateIteratedBuffer(associatedBuffer, dp, passiveElt);

      if (dp instanceof DataInputPort) {
        specialCall.addInputBuffer(associatedBuffer);
        if (flb != null) {
          flb.getInBuffers().add((IteratedBuffer) associatedBuffer);
        }
      } else {
        specialCall.addOutputBuffer(associatedBuffer);
        if (flb != null) {
          flb.getOutBuffers().add((IteratedBuffer) associatedBuffer);
        }
      }
    }
    specialCall.setName("call_to_" + actor.getName() + "_in_graph_" + actor.getContainingPiGraph().getName());
    return specialCall;
  }

  /**
   * Fills in the config parameters of the cluster in the call function block and the loop function block
   *
   * @param graph
   *          the cluster
   */
  private void generateClusterConfigParameters(PiGraph graph) {
    for (final ConfigInputInterface i : graph.getConfigInputInterfaces()) {
      addParamToFunctionBlock(i, callFuncBlock);
      addParamToFunctionBlock(i, loopFuncBlock);
    }
  }

  /**
   * Fills in the input config parameter for the following function block container
   *
   * @param cii
   *          the config input parameter
   * @param container
   *          the container
   */
  private void addParamToFunctionBlock(ConfigInputInterface cii, FunctionBlock container) {
    final IntVar param = CodegenModelUserFactory.eINSTANCE.createIntVar();
    param.setName(cii.getName());
    param.setCreator(container);
    param.getUsers().add(container);
    param.setType("int");
    container.getInputParams().add(param);

  }

  /**
   * Instantiate the Input/Output buffers of cluster
   *
   * @param i
   *          the data interface (it can be a input or output interface)
   */
  private void generateParameterClusterBuffers(PiGraph graph) {
    for (final DataInterface i : graph.getDataInterfaces()) {

      // creating input/output Buffer
      final Buffer ioBuffer = CodegenFactory.eINSTANCE.createBuffer();

      ioBuffer.setName(i.getName());
      ioBuffer.setNbToken(i.getDataPort().getExpression().evaluateAsLong());
      ioBuffer.setTokenTypeSizeInBit(
          scenario.getSimulationInfo().getDataTypeSizeInBit(i.getDataPort().getFifo().getType()));
      ioBuffer.setType(i.getDataPort().getFifo().getType());

      i2b.put(i, ioBuffer);

      if (i instanceof DataInputInterface) {
        loopFuncBlock.getInputArgs().add(ioBuffer);
      } else {
        loopFuncBlock.getOutputArgs().add(ioBuffer);
      }
    }
  }

  /**
   * Generates a finite loop block
   *
   * @param toInclude
   *          the code elements to include
   * @param repetition
   *          the repetition of the loop block
   * @param actor
   *          the sub-cluster attached to this finite loop block
   * @param parallel
   *          if the elements in the finite loop block are parallel or not
   * @return the finite loop block
   */
  private final FiniteLoopBlock generateFiniteLoopBlock(final CodeElt toInclude, final int repetition,
      final Schedule schedule, final boolean parallel) {
    final FiniteLoopBlock flb = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
    final IntVar iterator = CodegenModelUserFactory.eINSTANCE.createIntVar();

    switch (schedule) {
      case final HierarchicalSchedule hierSched when hierSched.getAttachedActor() != null -> {
        final PiGraph subCluster = (PiGraph) hierSched.getAttachedActor();
        iterator.setName("index_" + subCluster.getName());

        for (final DataInterface di : subCluster.getDataInterfaces()) {
          DataPort innerPort = di.getDataPort().getOppositePort();
          while (innerPort.getContainingActor() instanceof final PiGraph tmp) {
            final DataPort tmpInnerPort = innerPort;
            innerPort = tmp.getDataInterfaces().stream().filter(i -> i.getGraphPort() == tmpInnerPort).toList()
                .getFirst().getDataPort().getOppositePort();
          }
          final AbstractActor sourceContainer = PreesmCopyTracker.getSource(innerPort.getContainingActor());
          final DataPort tmpInnerPort = innerPort;
          final List<DataPort> sourcePorts = sourceContainer.getAllDataPorts().stream()
              .filter(dp -> Objects.equals(dp.getName(), tmpInnerPort.getName())).toList();
          final DataPort sourcePort = sourcePorts.getFirst();
          iterMap.put(retrieveAssociatedBuffer(sourcePort), iterator);
        }
      }
      case final ActorSchedule actSched -> {
        final AbstractActor actor = PreesmCopyTracker.getSource(actSched.getActorList().getFirst());
        iterator.setName("index_" + actor.getName());
        for (final DataPort dp : actor.getAllDataPorts()) {
          iterMap.put(retrieveAssociatedBuffer(dp), iterator);
        }
      }
      default -> throw new PreesmRuntimeException(
          "Can't build a finite loop block with a hierarchical schedule that has no attached actor. "
              + "Please contact PREESM developers.");
    }

    flb.setIter(iterator);
    flb.setNbIter(repetition);

    // Insert block inside FiniteLoopBlock
    if (toInclude != null) {
      flb.getCodeElts().add(toInclude);
    }

    // Set loop parallelism
    flb.setParallel(parallel);

    return flb;
  }

  /**
   * Generates an iterated buffer for the buffers that are in a finite loop block
   *
   * @param buffer
   *          the buffer
   * @param actor
   *          the actor using the buffer
   * @param dataPort
   *          the data port linked to the buffer
   * @param passiveElt
   *          the passive element linked to the buffer, if any (if == null, it means that it is at the edge of the
   *          cluster)
   * @return the iterated Buffer
   */
  final Buffer generateIteratedBuffer(final Buffer buffer, final DataPort dataPort, final PassiveElt passiveElt) {
    if (!iterMap.containsKey(buffer)) {
      return buffer;
    }

    PassiveActor pa = null;
    if (passiveElt != null) {
      if (passiveElt instanceof final PassivePort passivePort) {
        pa = (PassiveActor) passivePort.getContainingActor();
      } else {
        pa = (PassiveActor) passiveElt;
      }
    }
    // If iteration map contain actor, it means that buffer has to be iterated
    final IteratedBuffer iteratedBuffer;
    if (pa == null || pa.isSimple()) {
      iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedBuffer();
    } else {
      iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedPassiveBuffer();
    }
    iteratedBuffer.setBuffer(buffer);
    iteratedBuffer.setIter(iterMap.get(buffer));
    iteratedBuffer.setNbToken(dataPort.getExpression().evaluateAsLong());
    iteratedBuffer.setType(buffer.getType());
    iteratedBuffer.setTokenTypeSizeInBit(buffer.getTokenTypeSizeInBit());
    return iteratedBuffer;

  }

  /**
   * Fills the function call arguments
   *
   * @param functionCall
   *          the function call
   * @param actor
   *          the actor that is going to be called
   * @param flb
   *          the finite loop block containing the call
   */
  private final void fillFunctionCallArguments(final FunctionCall functionCall, final Actor actor,
      final FiniteLoopBlock flb) {

    // Retrieve Refinement from actor
    if (actor.getRefinement() instanceof final CHeaderRefinement cheader) {

      // Retrieve C header refinement
      if (cheader.getLoopPrototype() == null) {
        throw new PreesmRuntimeException(
            "CodegenClusterModelGenerator: cannot find C loop function prototype for actor " + actor.getName());
      }

      // Retrieve function argument
      final List<FunctionArgument> arguments = cheader.getLoopPrototype().getArguments();

      // Retrieve function name
      functionCall.setName(cheader.getLoopPrototype().getName());

      // Associate argument with buffer
      for (final FunctionArgument a : arguments) {

        // Search for the corresponding port into actor ports list
        final Port associatedPort = actor.lookupPort(a.getName());

        // Add argument into function call
        if (associatedPort instanceof final DataPort associatedDataPort) {
          addDataPortArgument(functionCall, associatedDataPort, a, flb);
        } else if (associatedPort instanceof final ConfigInputPort associatedCip) {
          addConfigInputPortArgument(functionCall, associatedCip, a);
        }
      }
    }
  }

  /**
   * Adds the config parameters in the function call
   *
   * @param functionCall
   *          the function call
   * @param port
   *          the config port
   * @param arg
   *          the argument (linked to the config port)
   */
  private final void addConfigInputPortArgument(final FunctionCall functionCall, final ConfigInputPort port,
      final FunctionArgument arg) {

    // Search for origin parameter
    final ConfigInputInterface parameter = (ConfigInputInterface) port.getIncomingDependency().getSetter();

    // Build a constant
    final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
    constant.setValue(parameter.getExpression().evaluateAsLong());

    // Set variable name to argument name
    constant.setName(arg.getName());

    // Add parameter to functionCall
    functionCall.addParameter(constant, PortDirection.INPUT);
  }

  /**
   * Adds the data argument
   *
   * @param functionCall
   *          the function call
   * @param actor
   *          the actor
   * @param port
   *          the port
   * @param arg
   *          the arg (linked to the port)
   * @param flb
   *          the finite loop block containing the call
   */
  private final void addDataPortArgument(final FunctionCall functionCall, final DataPort port,
      final FunctionArgument arg, final FiniteLoopBlock flb) {

    // Retrieve associated Buffer
    Buffer associatedBuffer = retrieveAssociatedBuffer(port);

    PassiveElt passiveElt;
    try {
      passiveElt = getAttachedPassiveElt(port);
    } catch (final Exception e) {
      passiveElt = null;
    }

    // If there is a repetition over the actor, iterate the buffer
    associatedBuffer = generateIteratedBuffer(associatedBuffer, port, passiveElt);

    // Add parameter to functionCall
    functionCall.addParameter(associatedBuffer,
        (arg.getDirection().equals(Direction.IN) ? PortDirection.INPUT : PortDirection.OUTPUT));

    if (flb != null) {
      if (arg.getDirection().equals(Direction.IN)) {
        flb.getInBuffers().add((IteratedBuffer) associatedBuffer);
      } else {
        flb.getOutBuffers().add((IteratedBuffer) associatedBuffer);
      }
    }
  }

  /**
   * Adds in the call function block the potential init function call of the actor
   *
   * @param actor
   *          the actor
   */
  private final void addInitFunctionCall(final Actor actor) {

    // Retrieve Refinement from actor
    if (actor.getRefinement() instanceof final CHeaderRefinement cheader) {

      // Verify that a init prototype is present
      if (cheader.getInitPrototype() == null) {
        return;
      }

      // Create function call
      final FunctionCall functionCall = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
      functionCall.setActorName(actor.getName());

      // Retrieve function argument
      final List<FunctionArgument> arguments = cheader.getInitPrototype().getArguments();
      // Retrieve function name
      functionCall.setName(cheader.getInitPrototype().getName());

      // Associate argument with buffer
      for (final FunctionArgument a : arguments) {

        // Search for the corresponding port into actor ports list
        final Port associatedPort = actor.lookupPort(a.getName());

        // Add argument to function call
        if (associatedPort instanceof final ConfigInputPort associatedCip) {
          addConfigInputPortArgument(functionCall, associatedCip, a);
        }
      }

      // Add function call to core block init loop
      callFuncBlock.getCodeElts().add(functionCall);
    }
  }

  /**
   * Retrieves the buffer associated to the fifo connected to the data port dp
   *
   * @param dp
   *          the data port
   * @return the buffer. If the dp is connected to a dataInterface, it will retrieve the buffer in the i2b map. If there
   *         is no buffer at the end, an error is raised Otherwise in the p2b map.
   */
  private final Buffer retrieveAssociatedBuffer(DataPort dp) {

    Buffer result;

    // If dp is at the edge of the cluster, we should look in i2b
    if (dp.getOppositePort().getContainingActor() instanceof final DataInterface di) {

      // Checking that the top down allocation approach has been respected
      if (di.getContainingGraph() != originalCluster) {
        throw new PreesmRuntimeException(
            "The assumption is that at an interface, it is the fifo linked to the graph port that should be"
                + " instanciated, not the one connected to the data port. In the current way of doing, it not seems "
                + "to be the case for interface " + di.getName());
      }

      result = i2b.get(di);

      if (result == null) {
        throw new PreesmRuntimeException("the data interface " + di.getName() + " doesn't have an associated buffer");
      }

      // If dp is not at the edge of the cluster, we should look in p2b
    } else {
      final PassiveElt passiveElt = getAttachedPassiveElt(dp);

      result = p2b.get(passiveElt);

      if (result == null) {
        throw new PreesmRuntimeException("the passiveElt " + dp.getName() + " doesn't have an associated buffer");
      }
    }
    return result;
  }

  /**
   * Get the attached passive element of a data port (surely it will be a PassivePort, but we keep the return type as a
   * PassiveElt because of the p2b map that takes as a key a PassiveElt).
   *
   * @param dp
   *          the data port
   * @return the connected passive element. If there is not, an error will be raised
   */
  private static PassiveElt getAttachedPassiveElt(DataPort dp) {
    final DataPort attached = dp.getOppositePort();
    if (!(attached instanceof final PassivePort attachedPort)) {
      throw new PreesmRuntimeException("a Passive Actor should be there between actor "
          + attached.getContainingActor().getName() + " and " + dp.getContainingActor().getName());
    }
    return attachedPort;
  }
}
