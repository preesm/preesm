/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
package org.preesm.codegen.model.clustering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.ClusterBlock;
import org.preesm.codegen.model.CodeElt;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FifoOperation;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.IteratedBuffer;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.SectionBlock;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
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
 * @author dgageot
 *
 */
public class CodegenClusterModelGeneratorSwitch extends ScheduleSwitch<CodeElt> {

  /**
   * @{link Scenario} to get data size from.
   */
  final Scenario scenario;

  /**
   * @{link {@link CoreBlock} to add generate code element to.
   */
  final CoreBlock operatorBlock;

  /**
   * @{link Map} that registers every cluster-internal @{link Buffer} with it's @{link Fifo}.
   */
  final Map<Fifo, Buffer> internalBufferMap;

  /**
   * @{link Map} that registers every cluster-external @{link Buffer} with it's @{link Fifo}.
   */
  final Map<Fifo, Buffer> externalBufferMap;

  /**
   * @{link Map} that registers every delay @{link Buffer} with it's @{link Fifo}.
   */
  final List<Buffer> delayBufferList;

  /**
   * @{link Map} that registers every delay @{link Buffer} with it's @{link Fifo}.
   */
  final Map<Fifo, Triple<SubBuffer, SubBuffer, SubBuffer>> delaySubBufferMap;

  /**
   * @{link Map} that registers every perfect fit delay @{link Buffer} with it's @{link InitActor}.
   */
  final Map<InitActor, Buffer> endInitBufferMap;

  /**
   * @{link Map} that registers every @{link AbstractActor} repetition with it's @{link IntVar}.
   */
  final Map<AbstractActor, IntVar> iterMap;

  /**
   * Repetition vector to get actors repetition from.
   */
  Map<AbstractVertex, Long> repVector;

  /**
   * {@link IOutsideFetcher} to get external buffer (outside our cluster) from.
   */
  IOutsideFetcher outsideFetcher;

  /**
   * {@link Map} that register options to get through the outside fetcher.
   */
  Map<String, Object> fetcherMap;

  private final PiGraph graph;

  /**
   * @param operatorBlock
   *          core block to print in
   * @param scenario
   *          scenario
   * @param outsideFetcher
   *          algorithm use to fetch outside buffer
   * @param fetcherMap
   *          argument for fetcher algorithm
   */
  public CodegenClusterModelGeneratorSwitch(final PiGraph graph, final CoreBlock operatorBlock, final Scenario scenario,
      final IOutsideFetcher outsideFetcher, final Map<String, Object> fetcherMap) {
    super();
    this.graph = graph;
    this.scenario = scenario;
    this.operatorBlock = operatorBlock;
    this.outsideFetcher = outsideFetcher;
    this.fetcherMap = fetcherMap;
    this.internalBufferMap = new HashMap<>();
    this.externalBufferMap = new HashMap<>();
    this.endInitBufferMap = new HashMap<>();
    this.delaySubBufferMap = new HashMap<>();
    this.delayBufferList = new LinkedList<>();
    this.iterMap = new HashMap<>();
    this.repVector = null;
  }

  /**
   * generate and set code element for the corresponding cluster inside of CoreBlock.
   */
  public void generate(final Schedule schedule) {
    // Get PiGraph
    final PiGraph graph = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();
    // Compute repetition vector for the whole process
    this.repVector = PiBRV.compute(graph, BRVMethod.LCM);
    // Print block from input schedule into operatorBlock
    CodeElt cluster = doSwitch(schedule);
    if (cluster instanceof ClusterBlock) {
      this.operatorBlock.getDefinitions().addAll(((ClusterBlock) cluster).getDefinitions());
      ((ClusterBlock) cluster).getDefinitions().clear();
    }
    this.operatorBlock.getLoopBlock().getCodeElts().add(cluster);
    this.operatorBlock.getDefinitions().addAll(this.delayBufferList);
    // Add end-init buffer in global
    this.operatorBlock.getDefinitions().addAll(this.endInitBufferMap.values());
    // Add delay buffer and sub-buffer in global
    for (final Triple<SubBuffer, SubBuffer, SubBuffer> triple : this.delaySubBufferMap.values()) {
      this.operatorBlock.getDefinitions().add(triple.getLeft());
      this.operatorBlock.getDefinitions().add(triple.getMiddle());
      this.operatorBlock.getDefinitions().add(triple.getRight());
    }
  }

  @Override
  public CodeElt caseSequentialHiearchicalSchedule(final SequentialHiearchicalSchedule schedule) {

    // If parent node a parallel node with one child? If yes, repetition can be parallelize
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
    final List<AbstractActor> actors = ScheduleUtil.getAllReferencedActors(schedule);
    final AbstractActor actor = actors.get(0);

    // Generate a LoopBlock to put function call element into
    final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
    final LoopBlock actorBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();

    // If actors has to be repeated few times, build a FiniteLoopBlock
    if (schedule.getRepetition() > 1) {
      FiniteLoopBlock finiteLoopBlock = generateFiniteLoopBlock(actorBlock, (int) schedule.getRepetition(), actor,
          parallelRepetition);
      loopBlock.getCodeElts().add(finiteLoopBlock);
    } else {
      loopBlock.getCodeElts().add(actorBlock);
    }

    // Build corresponding actor function/special call
    if ((actor instanceof EndActor) || (actor instanceof InitActor)) {
      actorBlock.getCodeElts().add(generateEndInitActorFiring((SpecialActor) actor));
    } else if (actor instanceof SpecialActor) {
      actorBlock.getCodeElts().add(generateSpecialActorFiring((SpecialActor) actor));
    } else if (actor instanceof ExecutableActor) {
      actorBlock.getCodeElts().add(generateExecutableActorFiring((ExecutableActor) actor));
    }

    // Add delay pop if necessary
    generateDelayPop(actor, loopBlock);

    // store buffers on which MD5 can be computed to check validity of transformations
    if (actor.getDataOutputPorts().isEmpty()) {
      final EList<DataInputPort> dataInputPorts = actor.getDataInputPorts();
      for (final DataInputPort dip : dataInputPorts) {
        final Variable variable = retrieveAssociatedBuffer(dip.getFifo(), PortKind.DATA_INPUT);
        operatorBlock.getSinkFifoBuffers().add((Buffer) variable);
      }
    }

    return loopBlock;
  }

  private void generateDelayPop(final AbstractActor actor, final LoopBlock loopBlock) {

    final PiSDFTopologyHelper helper = new PiSDFTopologyHelper(graph);
    // Explore data port for delay
    for (final DataPort dp : actor.getAllDataPorts()) {
      final Fifo associatedFifo = dp.getFifo();
      // If fifo is delayed
      if (this.delaySubBufferMap.containsKey(associatedFifo)) {
        // If the fifo goes to an actor that already has been executed, it means that we should generate a pop after
        // a write, otherwise we print a pop only if it's in input
        final boolean precedence = helper.isPredecessor((AbstractActor) associatedFifo.getTarget(),
            (AbstractActor) associatedFifo.getSource());

        if (((dp.getKind() == PortKind.DATA_INPUT) && !precedence)
            || ((dp.getKind() == PortKind.DATA_OUTPUT) && precedence)) {
          // Retrieve buffer and sub-buffer for corresponding delay
          final Triple<SubBuffer, SubBuffer, SubBuffer> delayBufferTriple = this.delaySubBufferMap.get(associatedFifo);
          // Generate a memcpy function call
          final FunctionCall memcpyCall = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
          memcpyCall.setName("memcpy");
          // remaining_tokens transfered at the top of delay buffer
          memcpyCall.addParameter(delayBufferTriple.getLeft(), PortDirection.INPUT); // delay buffer
          memcpyCall.addParameter(delayBufferTriple.getRight(), PortDirection.INPUT); // remaining subbuffer
          // Compute size of transfer
          final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
          constant.setValue(delayBufferTriple.getRight().getSize() * delayBufferTriple.getRight().getTypeSize());
          memcpyCall.addParameter(constant, PortDirection.INPUT);
          // Add pop to the loop block
          loopBlock.getCodeElts().add(memcpyCall);
        }
      }
    }
  }

  private final Pair<CodeElt, ClusterBlock> generateClusterBlock(final HierarchicalSchedule schedule,
      final boolean parallelRepetition) {
    // Retrieve cluster actor
    final PiGraph clusterGraph = (PiGraph) schedule.getAttachedActor();
    // Build and fill CloopBlock.getDefinitions()lusterBlock attributes
    final ClusterBlock clusterBlock = CodegenModelUserFactory.eINSTANCE.createClusterBlock();
    clusterBlock.setName(clusterGraph.getName());
    clusterBlock.setSchedule(schedule.shortPrint());
    clusterBlock.setParallel(schedule.isParallel());

    // If the cluster has to be repeated few times, build a FiniteLoopBlock
    CodeElt outputBlock = null;
    if (schedule.getRepetition() > 1) {
      outputBlock = generateFiniteLoopBlock(clusterBlock, (int) schedule.getRepetition(), clusterGraph,
          parallelRepetition);
    } else {
      // Output the ClusterBlock
      outputBlock = clusterBlock;
    }

    // Make memory allocation for internal buffer
    final List<Buffer> internalClusterBuffer = generateInternalClusterBuffers(clusterGraph);
    // Attach buffer definition to cluster
    clusterBlock.getDefinitions().addAll(internalClusterBuffer);
    // Make memory allocation for external buffer
    // i.e. fifo that goes outside of the hierarchical actor of the cluster
    generateExternalClusterBuffers(clusterGraph);

    return new ImmutablePair<>(outputBlock, clusterBlock);

  }

  private final FunctionCall generateExecutableActorFiring(final ExecutableActor actor) {
    // Build FunctionCall
    final ActorFunctionCall functionCall = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall();
    functionCall.setActorName(actor.getName());
    functionCall.setActor(actor);

    // Retrieve Refinement from actor for loop function
    fillFunctionCallArguments(functionCall, (Actor) actor);

    // Retrieve and add init function to operator core block
    addInitFunctionCall((Actor) actor);

    return functionCall;
  }

  private final SpecialCall generateSpecialActorFiring(final SpecialActor actor) {
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
      } else {
        specialCall.addOutputBuffer(associatedBuffer);
      }
    }

    return specialCall;
  }

  private final FifoCall generateEndInitActorFiring(final SpecialActor actor) {
    // Build a FifoCall
    final FifoCall fifoCall = CodegenModelUserFactory.eINSTANCE.createFifoCall();

    DataPort dp = null;
    InitActor initReference = null;

    // Build Buffer corresponding to the End-Init couple
    if (actor instanceof InitActor) {
      initReference = (InitActor) actor;
      if (!this.endInitBufferMap.containsKey(initReference)) {
        generateEndInitBuffer(initReference);
      }
      fifoCall.setOperation(FifoOperation.POP);
      dp = initReference.getDataOutputPort();
    } else if (actor instanceof EndActor) {
      initReference = (InitActor) ((EndActor) actor).getInitReference();
      if (!this.endInitBufferMap.containsKey(initReference)) {
        generateEndInitBuffer(initReference);
      }
      fifoCall.setOperation(FifoOperation.PUSH);
      dp = ((EndActor) actor).getDataInputPort();
    } else {
      throw new PreesmRuntimeException("CodegenClusterModelGenerator: can't generate model for " + actor);
    }

    final Buffer associatedBuffer = retrieveAssociatedBuffer(dp.getFifo(), dp.getKind());
    final Buffer newBuffer = generateIteratedBuffer(associatedBuffer, actor, dp);
    fifoCall.setHeadBuffer(this.endInitBufferMap.get(initReference));
    fifoCall.addParameter(newBuffer, PortDirection.NONE);

    return fifoCall;
  }

  private final Buffer generateDelayBuffer(final Fifo fifo, final Buffer delayBuffer, final int iterator) {

    // Fill delay buffer information
    final long workingBufferSize = delayBuffer.getSize();
    final long delayCapacity = fifo.getDelay().getExpression().evaluate();
    delayBuffer.setName("delay_" + ((AbstractActor) fifo.getSource()).getName() + "_to_"
        + ((AbstractActor) fifo.getTarget()).getName() + "_" + iterator);
    delayBuffer.setSize(delayCapacity + workingBufferSize);
    this.delayBufferList.add(delayBuffer);

    // Initialize SubBuffer for reading into delay's fifo
    final SubBuffer readBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    readBuffer.setContainer(delayBuffer);
    readBuffer.setOffset(0);
    readBuffer.setName("read_to_" + delayBuffer.getName());
    readBuffer.setType(delayBuffer.getType());
    readBuffer.setTypeSize(delayBuffer.getTypeSize());
    readBuffer.setSize(fifo.getTargetPort().getExpression().evaluate());

    // Initialize SubBuffer for writting into delay's fifo
    final SubBuffer writeBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    writeBuffer.setContainer(delayBuffer);
    writeBuffer.setOffset(delayCapacity);
    writeBuffer.setName("write_to_" + delayBuffer.getName());
    writeBuffer.setType(delayBuffer.getType());
    writeBuffer.setTypeSize(delayBuffer.getTypeSize());
    writeBuffer.setSize(workingBufferSize);

    // Initialize SubBuffer for shifting remaining value in delay's fifo
    final SubBuffer remainingTokensBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
    remainingTokensBuffer.setContainer(delayBuffer);
    remainingTokensBuffer.setOffset(workingBufferSize);
    remainingTokensBuffer.setName("remaining_tokens_of_" + delayBuffer.getName());
    remainingTokensBuffer.setType(delayBuffer.getType());
    remainingTokensBuffer.setTypeSize(delayBuffer.getTypeSize());
    remainingTokensBuffer.setSize(delayCapacity);

    // Add buffers to delay buffer map
    this.delaySubBufferMap.put(fifo, new ImmutableTriple<>(readBuffer, writeBuffer, remainingTokensBuffer));

    // Build call for fifo initialization
    final FifoCall fifoInit = CodegenModelUserFactory.eINSTANCE.createFifoCall();
    fifoInit.setHeadBuffer(delayBuffer);
    fifoInit.setOperation(FifoOperation.INIT);
    // Add delay buffer initialization to the init block of operator block
    this.operatorBlock.getInitBlock().getCodeElts().add(fifoInit);

    return delayBuffer;
  }

  private final Buffer generateEndInitBuffer(final InitActor actor) {
    final Buffer pipelineBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
    final Fifo outgoingFifo = actor.getDataOutputPort().getOutgoingFifo();
    pipelineBuffer.setType(outgoingFifo.getType());
    pipelineBuffer.setTypeSize(this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(outgoingFifo.getType()));
    pipelineBuffer.setSize(actor.getDataOutputPort().getExpression().evaluate());
    pipelineBuffer.setName("pipeline_" + actor.getName().substring(5));
    this.endInitBufferMap.put(actor, pipelineBuffer);
    return pipelineBuffer;
  }

  private final void generateExternalClusterBuffers(final PiGraph cluster) {
    // Get the list of external Fifo in the current cluster
    final List<Fifo> externalFifo = new LinkedList<>();
    externalFifo.addAll(cluster.getFifos());
    externalFifo.removeAll(ClusteringHelper.getInternalClusterFifo(cluster));

    // For all external Fifo
    for (final Fifo fifo : externalFifo) {
      Fifo outsideFifo = null;
      DataPort outsidePort = null;
      DataPort insidePort = null;
      // Determine Fifo direction
      if (fifo.getSource() instanceof DataInputInterface) {
        outsideFifo = ClusteringHelper.getOutsideIncomingFifo(fifo);
        outsidePort = outsideFifo.getTargetPort();
        insidePort = fifo.getTargetPort();
      } else {
        outsideFifo = ClusteringHelper.getOutsideOutgoingFifo(fifo);
        outsidePort = outsideFifo.getSourcePort();
        insidePort = fifo.getSourcePort();
      }

      if ((outsideFifo == null) || (outsidePort == null)) {
        throw new PreesmRuntimeException(
            "CodegenClusterModelGenerator: cannot retrieve external fifo of cluster " + cluster);
      }

      // Retrieve from map the corresponding parent buffer
      Buffer buffer = null;
      boolean inside = true;
      try {
        buffer = retrieveAssociatedBuffer(outsideFifo, insidePort.getKind());
      } catch (final PreesmRuntimeException e) {
        // This is actually an outside cluster fifo, so we need to get from outside
        buffer = getOuterClusterBuffer(outsidePort);
        inside = false;
      }

      // If cluster is repeated few times, create an iterated buffer
      if (inside && (this.repVector.get(cluster) > 1)) {
        buffer = generateIteratedBuffer(buffer, cluster, outsidePort);
      }

      // Register external buffer with corresponding fifo
      this.externalBufferMap.put(fifo, buffer);
    }
  }

  private final FiniteLoopBlock generateFiniteLoopBlock(final CodeElt toInclude, final int repetition,
      final AbstractActor actor, final boolean parallel) {
    final FiniteLoopBlock flb = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
    final IntVar iterator = CodegenModelUserFactory.eINSTANCE.createIntVar();
    iterator.setName("index_" + actor.getName());
    // Register the iteration var for that specific actor/cluster
    this.iterMap.put(actor, iterator);
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

  private final List<Buffer> generateInternalClusterBuffers(final PiGraph cluster) {
    // List of local internal buffer that will be defined in cluster scope
    final List<Buffer> localInternalBuffer = new LinkedList<>();

    int i = 0;
    for (final Fifo fifo : ClusteringHelper.getInternalClusterFifo(cluster)) {
      // Build different buffer regarding of delay on the fifo
      final Buffer buffer = generateBuffer(fifo, i);
      localInternalBuffer.add(buffer);
      i++;
    }

    return localInternalBuffer;
  }

  private final Buffer generateIteratedBuffer(final Buffer buffer, final AbstractActor actor, final DataPort dataPort) {
    // If iteration map contain actor, it means that buffer has to be iterated
    if (this.iterMap.containsKey(actor)) {
      IteratedBuffer iteratedBuffer = null;
      iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedBuffer();
      iteratedBuffer.setBuffer(buffer);
      iteratedBuffer.setIter(this.iterMap.get(actor));
      iteratedBuffer.setSize(dataPort.getExpression().evaluate());
      iteratedBuffer.setType(buffer.getType());
      iteratedBuffer.setTypeSize(buffer.getTypeSize());
      return iteratedBuffer;
    } else {
      return buffer;
    }
  }

  private final Buffer generateBuffer(final Fifo fifo, final int iterator) {
    // Allocate a buffer for each internalFifo
    final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();

    // Fill buffer information by looking at the Fifo
    buffer.setName("mem_" + ((AbstractActor) fifo.getSource()).getName() + "_to_"
        + ((AbstractActor) fifo.getTarget()).getName() + "_" + iterator);
    buffer.setType(fifo.getType());
    buffer.setTypeSize(this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
    buffer.setSize(fifo.getSourcePort().getExpression().evaluate() * this.repVector.get(fifo.getSource()));

    if (fifo.getDelay() != null) {
      // Is the fifo delayed?
      generateDelayBuffer(fifo, buffer, iterator);
    } else {
      // Register the buffer to the corresponding Fifo
      this.internalBufferMap.put(fifo, buffer);
    }

    return buffer;
  }

  private final void fillFunctionCallArguments(final FunctionCall functionCall, final Actor actor) {
    // Retrieve Refinement from actor
    if (actor.getRefinement() instanceof CHeaderRefinement) {
      // Retrieve C header refinement
      final CHeaderRefinement cheader = (CHeaderRefinement) actor.getRefinement();
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
        if (associatedPort instanceof DataPort) {
          addDataPortArgument(functionCall, actor, (DataPort) associatedPort, a);
        } else if (associatedPort instanceof ConfigInputPort) {
          addConfigInputPortArgument(functionCall, (ConfigInputPort) associatedPort, a);
        }
      }
    }
  }

  private final void addConfigInputPortArgument(final FunctionCall functionCall, final ConfigInputPort port,
      final FunctionArgument arg) {
    // Search for origin parameter
    final Parameter parameter = ClusteringHelper.getSetterParameter(port);

    // Build a constant
    final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
    constant.setValue(parameter.getExpression().evaluate());

    // Set variable name to argument name
    constant.setName(arg.getName());

    // Add parameter to functionCall
    functionCall.addParameter(constant, PortDirection.INPUT);
  }

  private final void addDataPortArgument(final FunctionCall functionCall, final Actor actor, final DataPort port,
      final FunctionArgument arg) {
    // Retrieve associated Fifo
    final Fifo associatedFifo = port.getFifo();

    // Retrieve associated Buffer
    Buffer associatedBuffer = retrieveAssociatedBuffer(associatedFifo, port.getKind());

    // If there is an repetion over actor, iterate the buffer
    associatedBuffer = generateIteratedBuffer(associatedBuffer, actor, port);

    // Add parameter to functionCall
    functionCall.addParameter(associatedBuffer,
        (arg.getDirection().equals(Direction.IN) ? PortDirection.INPUT : PortDirection.OUTPUT));

  }

  private final void addInitFunctionCall(final Actor actor) {
    // Retrieve Refinement from actor
    if (actor.getRefinement() instanceof CHeaderRefinement) {

      final CHeaderRefinement cheader = (CHeaderRefinement) actor.getRefinement();
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
        if (associatedPort instanceof ConfigInputPort) {
          addConfigInputPortArgument(functionCall, (ConfigInputPort) associatedPort, a);
        }
      }

      // Add function call to core block init loop
      this.operatorBlock.getInitBlock().getCodeElts().add(functionCall);
    }
  }

  private final Buffer retrieveAssociatedBuffer(final Fifo fifo, final PortKind dir) {
    if (this.internalBufferMap.containsKey(fifo)) {
      return this.internalBufferMap.get(fifo);
    } else if (this.externalBufferMap.containsKey(fifo)) {
      return this.externalBufferMap.get(fifo);
    } else if (this.delaySubBufferMap.containsKey(fifo)) {
      final Triple<SubBuffer, SubBuffer, SubBuffer> delayBufferTriple = this.delaySubBufferMap.get(fifo);
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

  private Buffer getOuterClusterBuffer(final DataPort graphPort) {
    if (this.outsideFetcher != null) {
      return this.outsideFetcher.getOuterClusterBuffer(graphPort, this.fetcherMap);
    } else {
      throw new PreesmRuntimeException("CodegenClusterModelGenerator: no outside fetcher is set");
    }
  }

}
