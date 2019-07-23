package org.preesm.codegen.xtend.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.ClusterBlock;
import org.preesm.codegen.model.CodegenFactory;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.IteratedBuffer;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.SectionBlock;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.HierarchicalSchedule;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
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
public class CodegenClusterModelGenerator {

  final Schedule schedule;

  final Scenario scenario;

  final CoreBlock operatorBlock;

  final Map<Fifo, Buffer> internalBufferMap;

  final Map<Fifo, Buffer> externalBufferMap;

  final Map<AbstractActor, IntVar> iterMap;

  Map<AbstractVertex, Long> repVector;

  IOutsideFetcher outsideFetcher;

  Map<String, Object> fetcherMap;

  /**
   * @param operatorBlock
   *          core block to print in
   * @param schedule
   *          schedule to print
   * @param scenario
   *          scenario
   * @param outsideFetcher
   *          algorithm use to fetch outside buffer
   * @param fetcherMap
   *          argument for fetcher algorithm
   */
  public CodegenClusterModelGenerator(final CoreBlock operatorBlock, final Schedule schedule, final Scenario scenario,
      final IOutsideFetcher outsideFetcher, final Map<String, Object> fetcherMap) {
    this.schedule = schedule;
    this.scenario = scenario;
    this.operatorBlock = operatorBlock;
    this.internalBufferMap = new HashMap<>();
    this.externalBufferMap = new HashMap<>();
    this.iterMap = new HashMap<>();
    this.outsideFetcher = outsideFetcher;
    this.fetcherMap = fetcherMap;
    this.repVector = null;
  }

  private final void addConfigInputPortArgument(final FunctionCall functionCall, final ConfigInputPort port,
      final FunctionArgument arg) {
    // Search for origin parameter
    final Parameter parameter = ClusteringHelper.getSetterParameter(port);

    // Build a constant
    final Constant constant = CodegenFactory.eINSTANCE.createConstant();
    constant.setValue(parameter.getExpression().evaluate());

    // Set variable name to argument name
    constant.setName(arg.getName());

    // Add parameter to functionCall
    functionCall.addParameter(constant, PortDirection.INPUT);
  }

  private final void addDataPortArgument(final FunctionCall functionCall, final Actor actor, final DataPort port,
      final FunctionArgument arg) {
    // Retrieve associated Fifo
    Fifo associatedFifo = null;
    if (arg.getDirection().equals(Direction.IN)) {
      associatedFifo = ((DataInputPort) port).getIncomingFifo();
    } else {
      associatedFifo = ((DataOutputPort) port).getOutgoingFifo();
    }

    // Retrieve associated Buffer
    Buffer associatedBuffer = retrieveAssociatedBuffer(associatedFifo);

    // If there is an iteration to run actor, iterate the buffer
    associatedBuffer = buildIteratedBuffer(associatedBuffer, actor, port);

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
      final FunctionCall functionCall = CodegenFactory.eINSTANCE.createFunctionCall();
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

  private final Block buildAbstractActorCall(final ActorSchedule schedule) {
    Block outputBlock = null;

    // Retrieve actor to fire
    final AbstractActor actor = schedule.getActors().get(0);

    // Call set block to put function call element into
    final LoopBlock callSet = CodegenFactory.eINSTANCE.createLoopBlock();

    // If actors has to be repeated few times, build a FiniteLoopBlock
    if (schedule.getRepetition() > 1) {
      outputBlock = buildFiniteLoopBlock(callSet, (int) schedule.getRepetition(), actor, schedule.isParallel());
    } else {
      // Output the LoopBlock
      outputBlock = callSet;
    }

    // Build corresponding actor function/special call
    if (actor instanceof SpecialActor) {
      callSet.getCodeElts().add(buildSpecialActorCall((SpecialActor) actor));
    } else if (actor instanceof ExecutableActor) {
      callSet.getCodeElts().add(buildExecutableActorCall((ExecutableActor) actor));
    }

    return outputBlock;
  }

  private final Block buildClusterBlock(final Schedule schedule) {
    Block outputBlock = null;

    // Retrieve cluster actor
    final PiGraph cluster = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();

    // Build and fill ClusterBlock attributes
    final ClusterBlock clusterBlock = CodegenFactory.eINSTANCE.createClusterBlock();
    clusterBlock.setName(cluster.getName());
    clusterBlock.setSchedule(schedule.shortPrint());
    clusterBlock.setParallel(schedule.isParallel());

    // If the cluster has to be repeated few times, build a FiniteLoopBlock
    if (schedule.getRepetition() > 1) {
      outputBlock = buildFiniteLoopBlock(clusterBlock, (int) schedule.getRepetition(), cluster, false);
    } else {
      // Output the ClusterBlock
      outputBlock = clusterBlock;
    }

    // Make memory allocation for internal buffer
    final List<Buffer> internalClusterBuffer = buildInternalClusterBuffer(cluster);
    clusterBlock.getDefinitions().addAll(internalClusterBuffer);
    // Make memory allocation for external buffer i.e. fifo that goes outside of the hierarchical actor of the cluster
    buildExternalClusterBuffer(cluster);

    // Call again buildClusterBlockRec to explore and build child
    for (final Schedule e : schedule.getChildren()) {
      // If it's a parallel schedule, print section
      if (schedule.isParallel()) {
        final SectionBlock sectionBlock = CodegenFactory.eINSTANCE.createSectionBlock();
        sectionBlock.getCodeElts().add(buildBlockFrom(e));
        clusterBlock.getCodeElts().add(sectionBlock);
      } else {
        clusterBlock.getCodeElts().add(buildBlockFrom(e));
      }
    }

    return outputBlock;
  }

  private final Block buildBlockFrom(final Schedule schedule) {
    Block outputBlock = null;

    if (schedule instanceof HierarchicalSchedule) {
      if (schedule.isDataParallel() && !schedule.hasAttachedActor()) {
        outputBlock = buildDataParallelismBlock(schedule);
      } else {
        // If it's a sequential schedule i.e. cluster, fill information for ClusterBlock
        outputBlock = buildClusterBlock(schedule);
      }
    } else if (schedule instanceof ActorSchedule) {
      // If it's an actor firing, fill information for FunctionCall
      outputBlock = buildAbstractActorCall((ActorSchedule) schedule);
    }

    return outputBlock;
  }

  private final Block buildDataParallelismBlock(final Schedule schedule) {
    // Retrieve cluster from children hierarchical schedule
    final HierarchicalSchedule childrenSchedule = (HierarchicalSchedule) schedule.getChildren().get(0);
    final PiGraph cluster = (PiGraph) childrenSchedule.getAttachedActor();
    // Build FiniteLoopBlock
    return buildFiniteLoopBlock(buildBlockFrom(childrenSchedule), (int) schedule.getRepetition(), cluster, true);
  }

  private final FunctionCall buildExecutableActorCall(final ExecutableActor actor) {
    // Build FunctionCall
    final FunctionCall functionCall = CodegenFactory.eINSTANCE.createFunctionCall();
    functionCall.setActorName(actor.getName());

    // Retrieve Refinement from actor for loop function
    fillFunctionCallArgument(functionCall, (Actor) actor);

    // Retrieve and add init function to operator core block
    addInitFunctionCall((Actor) actor);

    return functionCall;
  }

  private final void buildExternalClusterBuffer(final PiGraph cluster) {
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
      if (this.internalBufferMap.containsKey(outsideFifo)) {
        buffer = this.internalBufferMap.get(outsideFifo);
      } else if (this.externalBufferMap.containsKey(outsideFifo)) {
        buffer = this.externalBufferMap.get(outsideFifo);
      } else {
        // This is actually an outside cluster fifo, so we need to get from outside
        buffer = getOuterClusterBuffer(outsidePort);
      }

      // If cluster is repeated few times, create an iterated buffer
      if (this.repVector.get(cluster) > 1) {
        buffer = buildIteratedBuffer(buffer, cluster, outsidePort);
      }

      // Register external buffer with corresponding fifo
      this.externalBufferMap.put(fifo, buffer);
    }
  }

  private final FiniteLoopBlock buildFiniteLoopBlock(final Block toInclude, final int repetition,
      final AbstractActor actor, final boolean parallel) {
    final FiniteLoopBlock flb = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
    final IntVar iterator = CodegenFactory.eINSTANCE.createIntVar();
    iterator.setName("index_" + actor.getName());
    // Register the iteration var for that specific actor/cluster
    this.iterMap.put(actor, iterator);
    flb.setIter(iterator);
    flb.setNbIter(repetition);
    // Insert ClusterBlock inside FiniteLoopBlock
    flb.getCodeElts().add(toInclude);
    // Disable loop parallelism
    flb.setParallel(parallel);
    return flb;
  }

  private final List<Buffer> buildInternalClusterBuffer(final PiGraph cluster) {
    // List of local internal buffer that will be defined in cluster scope
    final List<Buffer> localInternalBuffer = new LinkedList<>();

    int i = 0;
    for (final Fifo fifo : ClusteringHelper.getInternalClusterFifo(cluster)) {
      // Allocate a buffer for each internalFifo
      final Buffer buffer = CodegenFactory.eINSTANCE.createBuffer();
      buffer.setName("mem_" + ((AbstractActor) fifo.getSource()).getName() + "_to_"
          + ((AbstractActor) fifo.getTarget()).getName() + "_" + i++);
      // Fill buffer information by looking at the Fifo
      buffer.setType(fifo.getType());
      buffer.setTypeSize(this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
      buffer.setSize(fifo.getSourcePort().getExpression().evaluate() * this.repVector.get(fifo.getSource()));
      // Register the buffer to the corresponding Fifo
      this.internalBufferMap.put(fifo, buffer);
      localInternalBuffer.add(buffer);
    }

    return localInternalBuffer;
  }

  private final Buffer buildIteratedBuffer(final Buffer buffer, final AbstractActor actor, final DataPort dataPort) {
    // If iteration map contain actor, it means that buffer has to be iterated
    if (this.iterMap.containsKey(actor)) {
      IteratedBuffer iteratedBuffer = null;
      iteratedBuffer = CodegenFactory.eINSTANCE.createIteratedBuffer();
      iteratedBuffer.setBuffer(buffer);
      if (!this.iterMap.containsKey(actor)) {
        throw new PreesmRuntimeException("CodegenClusterModelGenerator: cannot find iteration for " + actor);
      }
      iteratedBuffer.setIter(this.iterMap.get(actor));
      iteratedBuffer.setSize(dataPort.getExpression().evaluate());
      return iteratedBuffer;
    } else {
      return buffer;
    }
  }

  private final SpecialCall buildSpecialActorCall(final SpecialActor actor) {
    // Instantiate special call object
    final SpecialCall specialCall = CodegenFactory.eINSTANCE.createSpecialCall();

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
      throw new PreesmRuntimeException("CodegenClusterModelGenerator can't retrieve type of special actor");
    }

    // Retrieve associated fifo/buffer
    for (final DataPort dp : actor.getAllDataPorts()) {
      Buffer associatedBuffer = null;
      if (dp instanceof DataInputPort) {
        associatedBuffer = retrieveAssociatedBuffer(((DataInputPort) dp).getIncomingFifo());
        associatedBuffer = buildIteratedBuffer(associatedBuffer, actor, dp);
        specialCall.addInputBuffer(associatedBuffer);
      } else {
        associatedBuffer = retrieveAssociatedBuffer(((DataOutputPort) dp).getOutgoingFifo());
        associatedBuffer = buildIteratedBuffer(associatedBuffer, actor, dp);
        specialCall.addOutputBuffer(associatedBuffer);
      }
    }

    return specialCall;
  }

  private final long computeMemorySize() {
    long size = 0;
    for (final Buffer buffer : this.internalBufferMap.values()) {
      size += buffer.getSize();
    }
    return size;
  }

  private final void fillFunctionCallArgument(final FunctionCall functionCall, final Actor actor) {
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

  /**
   *
   */
  public void generate() {
    // Get PiGraph
    final PiGraph graph = (PiGraph) ((HierarchicalSchedule) this.schedule).getAttachedActor();
    // Compute repetition vector for the whole process
    this.repVector = PiBRV.compute(graph, BRVMethod.LCM);
    // Print cluster into operatorBlock
    this.operatorBlock.getLoopBlock().getCodeElts().add(buildBlockFrom(this.schedule));
    // Print memory consumption of the cluster
    String memoryLog = "Memory allocation for cluster " + graph.getName() + ": " + computeMemorySize() + " bytes";
    PreesmLogger.getLogger().log(Level.INFO, memoryLog);
  }

  private Buffer getOuterClusterBuffer(final DataPort graphPort) {
    return this.outsideFetcher.getOuterClusterBuffer(graphPort, this.fetcherMap);
  }

  private final Buffer retrieveAssociatedBuffer(final Fifo fifo) {
    Buffer associatedBuffer = null;
    if (this.internalBufferMap.containsKey(fifo)) {
      associatedBuffer = this.internalBufferMap.get(fifo);
    } else if (this.externalBufferMap.containsKey(fifo)) {
      associatedBuffer = this.externalBufferMap.get(fifo);
    } else {
      throw new PreesmRuntimeException("Cannot associate actors FIFO with buffer in Codegen model generation");
    }
    return associatedBuffer;
  }

}
