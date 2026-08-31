/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2025) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2025)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.clustering.ClusterHelper;
import org.preesm.algorithm.clustering.identification.ClusterIdentifier;
import org.preesm.algorithm.clustering.synthesis.ClusterSynthesisHelper;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.CallFunctionBlock;
import org.preesm.codegen.model.ClusterBlock;
import org.preesm.codegen.model.CodeElt;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FifoOperation;
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
import org.preesm.codegen.model.clustering.CodegenClusterModelGeneratorSwitch;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
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
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;
import org.preesm.model.scenario.Scenario;

/**
 * This class is inspired by the {@link CodegenClusterModelGeneratorSwitch} class, that creates a intermediate codegen
 * model, that is used by the printer to create the final code. This class differs in the way that, for each cluster, a
 * new file is created, with in it the init function and the loop function of the cluster, as if it was a
 * non-hierarchical actor with a C refinement. It allows this class to be called in any order regarding the global
 * codegen, as the URL for the created files are defined during the {@link ClusterIdentifier cluster identification}
 * process.
 *
 * @author rcazoulat
 *
 */
public class PiCodegenModelGenerator extends ScheduleSwitch<CodeElt> {

  /**
   * {@link Scenario} to get data size from.
   */
  protected final Scenario scenario;

  /**
   * {@link FunctionBlock} to add generate init code element to.
   */
  protected final CallFunctionBlock callFuncBlock;

  /**
   * {@link FunctionBlock} to add generate loop code element to.
   */
  protected final LoopFunctionBlock loopFuncBlock;

  /**
   * Will be added in the coreBlock welcoming the call(s) of the cluster
   */
  protected final List<Buffer> sinkFifoBuffers;

  /**
   * {@link Map} that registers every cluster-internal {@link Buffer} with it's {@link Fifo}.
   */
  protected final Map<Fifo, Buffer> bufferMap;

  /**
   * {@link Map} that registers every {@link Buffer} that is between 2 sub-clusters, with it's {@link Fifo}.
   */
  protected final Map<Fifo, Buffer> interfaceBufferMap;

  /**
   * {@link Map} that registers every cluster-external {@link Buffer} (parameter of the generated function) with it's
   * {@link Fifo}.
   */
  protected final Map<Fifo, Buffer> parameterBufferMap;

  /**
   * {@link Map} that registers every cluster-external {@link Buffer} with it's {@link Fifo}.
   */
  protected final Set<IntVar> externalIntVars;

  /**
   * {@link Map} that registers every delay {@link Buffer} with it's {@link Fifo}.
   */
  protected final List<Buffer> delayBufferList;

  /**
   * {@link Map} that registers every delay {@link Buffer} with it's {@link Fifo}.
   */
  protected final Map<Fifo, Triple<SubBuffer, SubBuffer, SubBuffer>> delaySubBufferMap;

  /**
   * {@link Map} that registers every {@link AbstractActor} repetition with it's {@link IntVar}.
   */
  protected final Map<AbstractActor, IntVar> iterMap;

  /**
   * Repetition vector to get actors repetition from.
   */
  protected Map<AbstractVertex, Long> repVector;

  /**
   * Top cluster, of the cluster modified by scheduling.
   */
  protected PiGraph topCluster;

  /**
   * If the cluster to generate the model from contains parallelism information, it is specified in the
   * containParallelism variable.
   */
  protected boolean parallelFiringsInside;

  /**
   *
   * @param originalCluster
   *          input cluster
   * @param scenario
   *          the global scenario
   */
  public PiCodegenModelGenerator(final PiGraph originalCluster, final Scenario scenario) {
    super();
    this.interfaceBufferMap = new HashMap<>();
    this.parameterBufferMap = new HashMap<>();
    this.externalIntVars = new HashSet<>();
    this.topCluster = null;
    this.scenario = scenario;
    this.sinkFifoBuffers = new LinkedList<>();
    this.bufferMap = new HashMap<>();
    this.delaySubBufferMap = new HashMap<>();
    this.delayBufferList = new LinkedList<>();
    this.iterMap = new HashMap<>();
    this.repVector = null;
    this.parallelFiringsInside = false;
    this.callFuncBlock = CodegenModelUserFactory.eINSTANCE.createCallFunctionBlock();
    this.loopFuncBlock = CodegenModelUserFactory.eINSTANCE.createLoopFunctionBlock();
    callFuncBlock.setName(ClusterHelper.getInitPrototypeName(originalCluster));
    loopFuncBlock.setName(ClusterHelper.getLoopPrototypeName(originalCluster));
  }

  /*
   * ============================================================================================================
   *
   * GENERATE
   *
   * ============================================================================================================
   */
  /**
   * Main function and class entry point. Generate and set code element for the corresponding cluster inside of
   * callFuncBlock and loopFuncBlock.
   */
  public void generate(final Schedule schedule) {

    // Get PiGraph
    if (topCluster == null) {
      topCluster = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();
    }
    // Compute repetition vector for the whole process
    repVector = PiBRV.compute(topCluster, BRVMethod.LCM);

    // Compute the cluster function arguments
    generateClusterConfigParameters(topCluster);
    generateParameterClusterBuffers(topCluster);

    // Print block from input schedule into callFuncBlock and loopFuncBlock
    final CodeElt cluster = doSwitch(schedule);

    if (cluster instanceof final ClusterBlock clusterBlock) {
      clusterBlock.setContainParallelism(parallelFiringsInside);
    }
    loopFuncBlock.getCodeElts().add(cluster);

    // Add delay buffer and sub-buffer in global
    loopFuncBlock.getDefinitions().addAll(delayBufferList);
    for (final Triple<SubBuffer, SubBuffer, SubBuffer> triple : delaySubBufferMap.values()) {
      loopFuncBlock.getDefinitions().add(triple.getLeft());
      loopFuncBlock.getDefinitions().add(triple.getMiddle());
      loopFuncBlock.getDefinitions().add(triple.getRight());
    }
  }

  /*
   * ============================================================================================================
   *
   * CASES
   *
   * ============================================================================================================
   */
  @Override
  public CodeElt caseSequentialHiearchicalSchedule(final SequentialHiearchicalSchedule schedule) {

    // If parent node is a parallel node with one child? If true, repetition can be parallelize
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
  public CodeElt caseParallelHiearchicalSchedule(final ParallelHiearchicalSchedule schedule) {

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
    final AbstractActor actor = actors.get(0);

    // Generate a LoopBlock to put function call element into
    final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
    final LoopBlock actorBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();

    // If actors has to be repeated few times, build a FiniteLoopBlock
    FiniteLoopBlock finiteLoopBlock = null;
    if (schedule.getRepetition() > 1) {
      finiteLoopBlock = generateFiniteLoopBlock(actorBlock, (int) schedule.getRepetition(), actor, parallelRepetition);
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
    } else if (actor instanceof final ExecutableActor executableActor) {
      actorBlock.getCodeElts().add(generateExecutableActorFiring(executableActor, finiteLoopBlock));
    }

    // Add delay pop if necessary
    generateDelayPop(actor, loopBlock);

    // store buffers on which MD5 can be computed to check validity of transformations
    if (actor.getDataOutputPorts().isEmpty()) {
      final EList<DataInputPort> dataInputPorts = actor.getDataInputPorts();
      for (final DataInputPort dip : dataInputPorts) {
        final Buffer buffer = retrieveAssociatedBuffer(dip.getFifo(), PortKind.DATA_INPUT);
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
  protected void generateDelayPop(final AbstractActor actor, final LoopBlock loopBlock) {

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(topCluster);
    // Explore data port for delay
    for (final DataPort dp : actor.getAllDataPorts()) {
      final Fifo associatedFifo = dp.getFifo();
      // If fifo is delayed
      if (delaySubBufferMap.containsKey(associatedFifo)) {
        // If the fifo goes to an actor that already has been executed, it means that we should generate a pop after
        // a write, otherwise we print a pop only if it's in input
        final boolean precedence = helper.isPredecessor(associatedFifo.getTarget(), associatedFifo.getSource());

        if (((dp.getKind() == PortKind.DATA_INPUT) && !precedence)
            || ((dp.getKind() == PortKind.DATA_OUTPUT) && precedence)) {
          // Retrieve buffer and sub-buffer for corresponding delay
          final Triple<SubBuffer, SubBuffer, SubBuffer> delayBufferTriple = delaySubBufferMap.get(associatedFifo);
          // Generate a memcpy function call
          final FunctionCall memcpyCall = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
          memcpyCall.setName("memcpy");
          // remaining_tokens transfered at the top of delay buffer
          memcpyCall.addParameter(delayBufferTriple.getLeft(), PortDirection.INPUT); // delay buffer
          memcpyCall.addParameter(delayBufferTriple.getRight(), PortDirection.INPUT); // remaining subbuffer
          // Compute size of transfer
          final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
          constant.setValue(
              delayBufferTriple.getRight().getNbToken() * delayBufferTriple.getRight().getTokenTypeSizeInBit());
          memcpyCall.addParameter(constant, PortDirection.INPUT);
          // Add pop to the loop block
          loopBlock.getCodeElts().add(memcpyCall);
        }
      }
    }
  }

  protected Pair<CodeElt, ClusterBlock> generateClusterBlock(final HierarchicalSchedule schedule,
      final boolean parallelRepetition) {

    // Build and fill ClusterBlock attributes
    final PiGraph clusterGraph = (PiGraph) schedule.getAttachedActor();
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
      outputBlock = generateFiniteLoopBlock(clusterBlock, (int) schedule.getRepetition(), clusterGraph,
          parallelRepetition);
      if (parallelRepetition) {
        parallelFiringsInside = true;
      }
    } else {
      // Output the ClusterBlock
      outputBlock = clusterBlock;
    }

    // Make memory allocation for internal buffer & Attach buffers definition to cluster

    final long scopeRep = ClusterSynthesisHelper.computeScopeRepetition(schedule);
    final List<Buffer> internalClusterBuffers = generateInternalClusterBuffers(clusterGraph, scopeRep);
    clusterBlock.getDefinitions().addAll(internalClusterBuffers);

    // Make memory allocation for external buffer
    // i.e. fifo that goes outside of the hierarchical actor of the cluster
    generateExternalClusterBuffers(clusterGraph, schedule.getRepetition(), outputBlock);

    return new ImmutablePair<>(outputBlock, clusterBlock);

  }

  protected final FunctionCall generateExecutableActorFiring(final ExecutableActor actor, final FiniteLoopBlock flb) {
    // Build FunctionCall
    final ActorFunctionCall functionCall = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall();
    functionCall.setActorName(actor.getName());
    functionCall.setOriActor(actor);

    // Retrieve Refinement from actor for loop function
    fillFunctionCallArguments(functionCall, (Actor) actor, flb);

    // Retrieve and add init function to operator core block
    addInitFunctionCall((Actor) actor);

    return functionCall;
  }

  protected final SpecialCall generateSpecialActorFiring(final SpecialActor actor, final FiniteLoopBlock flb) {
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
      Buffer associatedBuffer = null;
      associatedBuffer = retrieveAssociatedBuffer(dp.getFifo(), dp.getKind());
      associatedBuffer = generateIteratedBuffer(associatedBuffer, actor, dp);
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

  protected void generateExternalClusterBuffers(final PiGraph cluster, final long clusterRep, final CodeElt block) {
    // Get the list of external Fifo in the current cluster
    final List<Fifo> externalFifo = new LinkedList<>(cluster.getFifos());
    externalFifo.removeAll(ClusterHelper.getInternalClusterFifo(cluster));

    // For all external Fifo
    for (final Fifo fifo : externalFifo) {

      // checks if fifo is a cluster function parameter, stored in parameterbufferMap.
      // It is for the special case when we are generating the top-level cluster
      Buffer buffer = parameterBufferMap.get(fifo);
      DataPort insidePort;
      if (buffer != null) {
        continue;
      }
      Fifo outsideFifo = null;
      DataPort outsidePort = null;

      // Determine Fifo direction
      if (fifo.getSource() instanceof DataInputInterface) {
        outsideFifo = ClusterHelper.getOutsideIncomingFifo(fifo);
        outsidePort = outsideFifo.getTargetPort();
        insidePort = fifo.getTargetPort();
      } else {
        outsideFifo = ClusterHelper.getOutsideOutgoingFifo(fifo);
        outsidePort = outsideFifo.getSourcePort();
        insidePort = fifo.getSourcePort();
      }

      if (outsidePort == null) {
        throw new PreesmRuntimeException(
            "CodegenClusterModelGenerator: cannot retrieve external fifo of cluster " + cluster);
      }

      buffer = retrieveAssociatedBuffer(outsideFifo, insidePort.getKind());
      // If cluster is repeated multiple times, create an iterated buffer
      if (clusterRep > 1) {
        buffer = generateIteratedBuffer(buffer, cluster, insidePort);
        final FiniteLoopBlock flb = (FiniteLoopBlock) block;
        if (fifo.getSource() instanceof DataInputInterface) {
          flb.getInBuffers().add((IteratedBuffer) buffer);
        } else {
          flb.getOutBuffers().add((IteratedBuffer) buffer);
        }
      }

      // Register external buffer with corresponding fifo
      interfaceBufferMap.put(fifo, buffer);
    }
  }

  protected final Buffer generateDelayBuffer(final Fifo fifo, final Buffer delayBuffer, final int iterator) {

    // Fill delay buffer information
    final long workingBufferSize = delayBuffer.getNbToken();
    final long delayCapacity = fifo.getDelay().getExpression().evaluateAsLong();
    delayBuffer.setName("delay_" + fifo.getSource().getName() + "_to_" + fifo.getTarget().getName() + "_" + iterator);
    delayBuffer.setNbToken(delayCapacity + workingBufferSize);
    delayBufferList.add(delayBuffer);

    // Initialize SubBuffer for reading into delay's fifo
    final SubBuffer readBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    readBuffer.setContainer(delayBuffer);
    readBuffer.setOffsetInBit(0);
    readBuffer.setName("read_to_" + delayBuffer.getName());
    readBuffer.setType(delayBuffer.getType());
    readBuffer.setTokenTypeSizeInBit(delayBuffer.getTokenTypeSizeInBit());
    readBuffer.setNbToken(fifo.getTargetPort().getExpression().evaluateAsLong());

    // Initialize SubBuffer for writting into delay's fifo
    final SubBuffer writeBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    writeBuffer.setContainer(delayBuffer);
    writeBuffer.setOffsetInBit(delayCapacity);
    writeBuffer.setName("write_to_" + delayBuffer.getName());
    writeBuffer.setType(delayBuffer.getType());
    writeBuffer.setTokenTypeSizeInBit(delayBuffer.getTokenTypeSizeInBit());
    writeBuffer.setNbToken(workingBufferSize);

    // Initialize SubBuffer for shifting remaining value in delay's fifo
    final SubBuffer remainingTokensBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    remainingTokensBuffer.setContainer(delayBuffer);
    remainingTokensBuffer.setOffsetInBit(workingBufferSize);
    remainingTokensBuffer.setName("remaining_tokens_of_" + delayBuffer.getName());
    remainingTokensBuffer.setType(delayBuffer.getType());
    remainingTokensBuffer.setTokenTypeSizeInBit(delayBuffer.getTokenTypeSizeInBit());
    remainingTokensBuffer.setNbToken(delayCapacity);

    // Add buffers to delay buffer map
    delaySubBufferMap.put(fifo, new ImmutableTriple<>(readBuffer, writeBuffer, remainingTokensBuffer));

    // Build call for fifo initialization
    final FifoCall fifoInit = CodegenModelUserFactory.eINSTANCE.createFifoCall();
    fifoInit.setHeadBuffer(delayBuffer);
    fifoInit.setOperation(FifoOperation.INIT);
    // Add delay buffer initialization to the init block of operator block
    callFuncBlock.getCodeElts().add(fifoInit);

    return delayBuffer;
  }

  protected void generateClusterConfigParameters(PiGraph graph) {
    for (final ConfigInputInterface i : graph.getConfigInputInterfaces()) {
      addParamToFunctionBlock(i, callFuncBlock);
      addParamToFunctionBlock(i, loopFuncBlock);
    }
  }

  protected void addParamToFunctionBlock(ConfigInputInterface i, FunctionBlock container) {
    final IntVar param = CodegenModelUserFactory.eINSTANCE.createIntVar();
    param.setName(i.getName());
    param.setCreator(container);
    param.getUsers().add(container);
    param.setType("int");
    container.getInputParams().add(param);
    externalIntVars.add(param);

  }

  /**
   * Instantiate the Input/Output buffers of cluster
   *
   * @param i
   *          the data interface (it can be a input or output interface)
   */
  protected void generateParameterClusterBuffers(PiGraph graph) {
    for (final DataInterface i : graph.getDataInterfaces()) {
      final Buffer externalBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
      final Fifo fifo = i.getDataPort().getFifo();
      final long bufferSize = i.getDataPort().getExpression().evaluateAsLong();
      externalBuffer.setName(i.getName());
      externalBuffer.setType(fifo.getType());
      externalBuffer.setNbToken(bufferSize);

      parameterBufferMap.put(fifo, externalBuffer);
      if (i instanceof DataInputInterface) {
        loopFuncBlock.getInputArgs().add(externalBuffer);
      } else {
        loopFuncBlock.getOutputArgs().add(externalBuffer);
      }
    }
  }

  protected final FiniteLoopBlock generateFiniteLoopBlock(final CodeElt toInclude, final int repetition,
      final AbstractActor actor, final boolean parallel) {
    final FiniteLoopBlock flb = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
    final IntVar iterator = CodegenModelUserFactory.eINSTANCE.createIntVar();
    iterator.setName("index_" + actor.getName());
    // Register the iteration var for that specific actor/cluster
    iterMap.put(actor, iterator);
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

  protected List<Buffer> generateInternalClusterBuffers(final PiGraph cluster, final long scopeRep) {

    // List of local internal buffer that will be defined in cluster scope
    final List<Buffer> localInternalBuffer = new LinkedList<>();

    int i = 0;
    for (final Fifo fifo : ClusterHelper.getInternalClusterFifo(cluster)) {

      // Build different buffer regarding of delay on the fifo
      final Buffer buffer = generateBuffer(fifo, i, scopeRep);
      localInternalBuffer.add(buffer);
      i++;
    }

    return localInternalBuffer;
  }

  protected final Buffer generateIteratedBuffer(final Buffer buffer, final AbstractActor actor,
      final DataPort dataPort) {
    if (!iterMap.containsKey(actor)) {
      return buffer;
    }
    // If iteration map contain actor, it means that buffer has to be iterated
    final IteratedBuffer iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedBuffer();
    iteratedBuffer.setBuffer(buffer);
    iteratedBuffer.setIter(iterMap.get(actor));
    iteratedBuffer.setNbToken(dataPort.getExpression().evaluateAsLong());
    iteratedBuffer.setType(buffer.getType());
    iteratedBuffer.setTokenTypeSizeInBit(buffer.getTokenTypeSizeInBit());
    return iteratedBuffer;

  }

  protected final Buffer generateBuffer(final Fifo fifo, final int iterator, long scopeRep) {
    // Allocate a buffer for each internalFifo
    final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();

    // Fill buffer information by looking at the Fifo
    buffer.setName("mem_" + fifo.getSource().getName() + "_to_" + fifo.getTarget().getName() + "_" + iterator);
    buffer.setType(fifo.getType());
    buffer.setTokenTypeSizeInBit(scenario.getSimulationInfo().getDataTypeSizeInBit(fifo.getType()));
    buffer
        .setNbToken(fifo.getTargetPort().getExpression().evaluateAsLong() * repVector.get(fifo.getTarget()) / scopeRep);

    if (fifo.getDelay() != null) {
      // Is the fifo delayed?
      generateDelayBuffer(fifo, buffer, iterator);
    } else {
      // Register the buffer to the corresponding Fifo
      bufferMap.put(fifo, buffer);
    }

    return buffer;
  }

  protected final void fillFunctionCallArguments(final FunctionCall functionCall, final Actor actor,
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
          addDataPortArgument(functionCall, actor, associatedDataPort, a, flb);
        } else if (associatedPort instanceof final ConfigInputPort associatedCip) {
          addConfigInputPortArgument(functionCall, associatedCip, a);
        }
      }
    }
  }

  protected final void addConfigInputPortArgument(final FunctionCall functionCall, final ConfigInputPort port,
      final FunctionArgument arg) {
    // Search for origin parameter
    // final Parameter parameter = ClusteringHelper.getSetterParameter(port);
    final ConfigInputInterface parameter = (ConfigInputInterface) port.getIncomingDependency().getSetter();
    // Build a constant
    final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
    constant.setValue(parameter.getExpression().evaluateAsLong());

    // Set variable name to argument name
    constant.setName(arg.getName());

    // Add parameter to functionCall
    functionCall.addParameter(constant, PortDirection.INPUT);
  }

  protected final void addDataPortArgument(final FunctionCall functionCall, final Actor actor, final DataPort port,
      final FunctionArgument arg, final FiniteLoopBlock flb) {
    // Retrieve associated Fifo
    final Fifo associatedFifo = port.getFifo();

    // Retrieve associated Buffer
    Buffer associatedBuffer = retrieveAssociatedBuffer(associatedFifo, port.getKind());

    // If there is an repetion over actor, iterate the buffer
    associatedBuffer = generateIteratedBuffer(associatedBuffer, actor, port);

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

  protected final void addInitFunctionCall(final Actor actor) {
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

  protected Buffer retrieveAssociatedBuffer(final Fifo fifo, final PortKind dir) {
    if (bufferMap.containsKey(fifo)) {
      return bufferMap.get(fifo);
    }
    if (interfaceBufferMap.containsKey(fifo)) {
      return interfaceBufferMap.get(fifo);
    }
    if (parameterBufferMap.containsKey(fifo)) {
      return parameterBufferMap.get(fifo);
    }
    if (delaySubBufferMap.containsKey(fifo)) {
      final Triple<SubBuffer, SubBuffer, SubBuffer> delayBufferTriple = delaySubBufferMap.get(fifo);
      switch (dir) {
        case DATA_INPUT:
          return delayBufferTriple.getLeft();
        case DATA_OUTPUT:
          return delayBufferTriple.getMiddle();
        default:
      }
    }
    throw new PreesmRuntimeException(
        "CodegenClusterModelGenerator: cannot associate actors FIFO [" + fifo + "] with buffer");
  }

  public CallFunctionBlock getCallFunctionBlock() {
    return callFuncBlock;
  }

  public LoopFunctionBlock getLoopFunctionBlock() {
    return loopFuncBlock;
  }

}
