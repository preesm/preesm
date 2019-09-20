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
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.iterator.SimpleScheduleIterator;
import org.preesm.codegen.model.Block;
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
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
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
  final Map<Fifo, Buffer> delayBufferMap;

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

  HierarchicalSchedule parentNode;

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
  public CodegenClusterModelGeneratorSwitch(final CoreBlock operatorBlock, final Scenario scenario,
      final IOutsideFetcher outsideFetcher, final Map<String, Object> fetcherMap) {
    super();
    this.scenario = scenario;
    this.operatorBlock = operatorBlock;
    this.outsideFetcher = outsideFetcher;
    this.fetcherMap = fetcherMap;
    this.internalBufferMap = new HashMap<>();
    this.externalBufferMap = new HashMap<>();
    this.endInitBufferMap = new HashMap<>();
    this.delayBufferMap = new HashMap<>();
    this.iterMap = new HashMap<>();
    this.repVector = null;
    this.parentNode = null;
  }

  /**
   * generate and set code element for the corresponding cluster inside of CoreBlock.
   */
  public void generate(Schedule schedule) {
    // Get PiGraph
    final PiGraph graph = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();
    // Compute repetition vector for the whole process
    this.repVector = PiBRV.compute(graph, BRVMethod.LCM);
    // Print block from input schedule into operatorBlock
    this.operatorBlock.getLoopBlock().getCodeElts().add(doSwitch(schedule));
    // Set end-init and delay buffer in global
    this.operatorBlock.getDefinitions().addAll(endInitBufferMap.values());
    this.operatorBlock.getDefinitions().addAll(delayBufferMap.values());
  }

  @Override
  public CodeElt caseSequentialHiearchicalSchedule(SequentialHiearchicalSchedule schedule) {

    // If parent node a parallel node with one child? If yes, repetition can be parallelize
    boolean parallelRepetition = false;
    if (this.parentNode != null && this.parentNode.isParallel() && this.parentNode.getChildren().size() == 1) {
      parallelRepetition = true;
    }

    // Retrieve cluster actor
    Pair<CodeElt, ClusterBlock> outputPair = generateClusterBlock(schedule, parallelRepetition);

    // Explore and generate child schedule
    for (final Schedule e : schedule.getChildren()) {
      this.parentNode = schedule;
      outputPair.getValue().getCodeElts().add(doSwitch(e));
    }

    return outputPair.getKey();
  }

  @Override
  public CodeElt caseParallelHiearchicalSchedule(ParallelHiearchicalSchedule schedule) {

    // Is it a data parallelism node?
    if (schedule.getChildren().size() == 1) {
      this.parentNode = schedule;
      return doSwitch(schedule.getChildren().get(0));
    }

    // Retrieve cluster actor
    Pair<CodeElt, ClusterBlock> outputPair = generateClusterBlock(schedule, false);

    // Explore and generate child schedule
    for (final Schedule e : schedule.getChildren()) {
      this.parentNode = schedule;
      final SectionBlock sectionBlock = CodegenModelUserFactory.eINSTANCE.createSectionBlock();
      sectionBlock.getCodeElts().add(doSwitch(e));
      outputPair.getValue().getCodeElts().add(sectionBlock);
    }

    return outputPair.getKey();
  }

  @Override
  public CodeElt caseActorSchedule(ActorSchedule schedule) {

    // Retrieve actor to fire
    // clustering process does list actors in actor schedule, we only care about the first one here
    final List<AbstractActor> actors = new SimpleScheduleIterator(schedule).getOrderedList();
    final AbstractActor actor = actors.get(0);

    // Generate a LoopBlock to put function call element into
    final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();

    // If actors has to be repeated few times, build a FiniteLoopBlock
    CodeElt outputBlock = null;
    if (schedule.getRepetition() > 1) {
      outputBlock = generateFiniteLoopBlock(loopBlock, (int) schedule.getRepetition(), actor, schedule.isParallel());
    } else {
      // Output the LoopBlock
      outputBlock = loopBlock;
    }

    // Build corresponding actor function/special call
    if (actor instanceof EndActor || actor instanceof InitActor) {
      loopBlock.getCodeElts().add(generateEndInitActorFiring((SpecialActor) actor));
    } else if (actor instanceof SpecialActor) {
      loopBlock.getCodeElts().add(generateSpecialActorFiring((SpecialActor) actor));
    } else if (actor instanceof ExecutableActor) {
      loopBlock.getCodeElts().add(generateExecutableActorFiring((ExecutableActor) actor));
    }

    return outputBlock;
  }

  private final Pair<CodeElt, ClusterBlock> generateClusterBlock(final HierarchicalSchedule schedule,
      final boolean parallelRepetition) {
    // Retrieve cluster actor
    final PiGraph clusterGraph = (PiGraph) schedule.getAttachedActor();

    // Build and fill ClusterBlock attributes
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
    final FunctionCall functionCall = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
    functionCall.setActorName(actor.getName());

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
      associatedBuffer = retrieveAssociatedBuffer(dp.getFifo());
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
    FifoCall fifoCall = CodegenModelUserFactory.eINSTANCE.createFifoCall();

    DataPort dp = null;
    InitActor initReference = null;

    // Build Buffer corresponding to the End-Init couple
    if (actor instanceof InitActor) {
      initReference = (InitActor) actor;
      if (!endInitBufferMap.containsKey(initReference)) {
        generateEndInitBuffer(initReference);
      }
      fifoCall.setOperation(FifoOperation.POP);
      dp = initReference.getDataOutputPort();
    } else if (actor instanceof EndActor) {
      initReference = (InitActor) ((EndActor) actor).getInitReference();
      if (!endInitBufferMap.containsKey(initReference)) {
        generateEndInitBuffer(initReference);
      }
      fifoCall.setOperation(FifoOperation.PUSH);
      dp = ((EndActor) actor).getDataInputPort();
    } else {
      throw new PreesmRuntimeException("CodegenClusterModelGenerator: can't generate model for " + actor);
    }

    Buffer associatedBuffer = retrieveAssociatedBuffer(dp.getFifo());
    Buffer newBuffer = generateIteratedBuffer(associatedBuffer, actor, dp);
    fifoCall.setHeadBuffer(endInitBufferMap.get(initReference));
    fifoCall.addParameter(newBuffer, PortDirection.NONE);

    return fifoCall;
  }

  private final Buffer generateDelayedBuffer(Fifo fifo, int iterator) {
    final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();

    // Fill buffer information by looking at the Fifo
    buffer.setName("delay_" + ((AbstractActor) fifo.getSource()).getName() + "_to_"
        + ((AbstractActor) fifo.getTarget()).getName() + "_" + iterator);
    buffer.setType(fifo.getType());
    buffer.setTypeSize(this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
    buffer.setSize(fifo.getSourcePort().getExpression().evaluate());
    this.delayBufferMap.put(fifo, buffer);
    // Build call for fifo initialization
    FifoCall fifoInit = CodegenModelUserFactory.eINSTANCE.createFifoCall();
    fifoInit.setHeadBuffer(buffer);
    fifoInit.setOperation(FifoOperation.INIT);
    this.operatorBlock.getInitBlock().getCodeElts().add(fifoInit);

    return buffer;
  }

  private final Buffer generateEndInitBuffer(final InitActor actor) {
    Buffer pipelineBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
    Fifo outgoingFifo = actor.getDataOutputPort().getOutgoingFifo();
    pipelineBuffer.setType(outgoingFifo.getType());
    pipelineBuffer.setTypeSize(this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(outgoingFifo.getType()));
    pipelineBuffer.setSize(actor.getDataOutputPort().getExpression().evaluate());
    pipelineBuffer.setName("pipeline_" + actor.getName().substring(5));
    endInitBufferMap.put(actor, pipelineBuffer);
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
      // Determine Fifo direction
      if (fifo.getSource() instanceof DataInputInterface) {
        outsideFifo = ClusteringHelper.getOutsideIncomingFifo(fifo);
        outsidePort = outsideFifo.getTargetPort();
      } else {
        outsideFifo = ClusteringHelper.getOutsideOutgoingFifo(fifo);
        outsidePort = outsideFifo.getSourcePort();
      }

      if ((outsideFifo == null) || (outsidePort == null)) {
        throw new PreesmRuntimeException(
            "CodegenClusterModelGenerator: cannot retrieve external fifo of cluster " + cluster);
      }

      // Retrieve from map the corresponding parent buffer
      Buffer buffer = null;
      boolean inside = true;
      if (this.internalBufferMap.containsKey(outsideFifo)) {
        buffer = this.internalBufferMap.get(outsideFifo);
      } else if (this.externalBufferMap.containsKey(outsideFifo)) {
        buffer = this.externalBufferMap.get(outsideFifo);
      } else if (this.delayBufferMap.containsKey(outsideFifo)) {
        buffer = this.delayBufferMap.get(outsideFifo);
      } else {
        // This is actually an outside cluster fifo, so we need to get from outside
        buffer = getOuterClusterBuffer(outsidePort);
        inside = false;
      }

      // If cluster is repeated few times, create an iterated buffer
      if (inside && this.repVector.get(cluster) > 1) {
        buffer = generateIteratedBuffer(buffer, cluster, outsidePort);
      }

      // Register external buffer with corresponding fifo
      this.externalBufferMap.put(fifo, buffer);
    }
  }

  private final FiniteLoopBlock generateFiniteLoopBlock(final Block toInclude, final int repetition,
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
      if (fifo.getDelay() != null) {
        generateDelayedBuffer(fifo, i);
      } else {
        Buffer buffer = generateBuffer(fifo, i);
        localInternalBuffer.add(buffer);
      }
      i++;
    }

    return localInternalBuffer;
  }

  private final Buffer generateIteratedBuffer(final Buffer buffer, final AbstractActor actor, final DataPort dataPort) {
    // If iteration map contain actor, it means that buffer has to be iterated
    // The last condition about delay prevent to iterate into a delay that is permanent or none-persistant
    if (this.iterMap.containsKey(actor) && !this.delayBufferMap.values().contains(buffer)) {
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

  private final Buffer generateBuffer(Fifo fifo, int iterator) {
    // Allocate a buffer for each internalFifo
    final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();

    // Fill buffer information by looking at the Fifo
    buffer.setName("mem_" + ((AbstractActor) fifo.getSource()).getName() + "_to_"
        + ((AbstractActor) fifo.getTarget()).getName() + "_" + iterator);
    buffer.setType(fifo.getType());
    buffer.setTypeSize(this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
    buffer.setSize(fifo.getSourcePort().getExpression().evaluate() * this.repVector.get(fifo.getSource()));

    // Register the buffer to the corresponding Fifo
    this.internalBufferMap.put(fifo, buffer);

    return buffer;
  }

  private final void fillFunctionCallArguments(final FunctionCall functionCall, final Actor actor) {
    // Retrieve Refinement from actor
    if (actor.getRefinement() instanceof CHeaderRefinement) {
      final CHeaderRefinement cheader = (CHeaderRefinement) actor.getRefinement();
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
    Fifo associatedFifo = port.getFifo();

    // Retrieve associated Buffer
    Buffer associatedBuffer = retrieveAssociatedBuffer(associatedFifo);

    // If there is an repetion over actor, iterate the buffer
    associatedBuffer = generateIteratedBuffer(associatedBuffer, actor, port);

    // If function call already has this parameter, we need to copy it because parameter list of FunctionCall accept
    // only unique reference
    if (functionCall.getParameters().contains(associatedBuffer)) {
      associatedBuffer = EcoreUtil.copy(associatedBuffer);
    }

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

  private final Buffer retrieveAssociatedBuffer(final Fifo fifo) {
    if (this.internalBufferMap.containsKey(fifo)) {
      return this.internalBufferMap.get(fifo);
    } else if (this.externalBufferMap.containsKey(fifo)) {
      return this.externalBufferMap.get(fifo);
    } else if (this.delayBufferMap.containsKey(fifo)) {
      return this.delayBufferMap.get(fifo);
    } else {
      throw new PreesmRuntimeException(
          "CodegenClusterModelGenerator: cannot associate actors FIFO [" + fifo + "] with buffer");
    }
  }

  private Buffer getOuterClusterBuffer(final DataPort graphPort) {
    if (this.outsideFetcher != null) {
      return this.outsideFetcher.getOuterClusterBuffer(graphPort, this.fetcherMap);
    } else {
      throw new PreesmRuntimeException("CodegenClusterModelGenerator: no outside fetcher is set");
    }
  }

}
