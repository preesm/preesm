package org.preesm.codegen.xtend.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.ClusterBlock;
import org.preesm.codegen.model.CodegenFactory;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.IteratedBuffer;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.algorithm.schedule.SequentialActorSchedule;
import org.preesm.model.algorithm.schedule.SequentialHiearchicalSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;

/**
 * @author dgageot
 *
 */
public class CodegenClusterModelGenerator extends AbstractCodegenModelGenerator {

  final Map<AbstractActor, Schedule> mapper;

  final Map<Fifo, Buffer> internalBufferMap;

  final Map<Fifo, Buffer> externalBufferMap;

  final Map<AbstractActor, IntVar> iterMap;

  Map<AbstractVertex, Long> repVector;

  final PiGraph piAlgo;

  /**
   * @param archi
   *          architecture
   * @param algo
   *          PiGraph algorithm
   * @param scenario
   *          scenario
   * @param workflow
   *          workflow
   * @param mapper
   *          schedule associated with cluster
   */
  public CodegenClusterModelGenerator(final Design archi, final PiGraph algo, final Scenario scenario,
      final Workflow workflow, final Map<AbstractActor, Schedule> mapper) {
    super(archi, new MapperDAG(algo), null, scenario, workflow);
    this.mapper = mapper;
    this.piAlgo = algo;
    this.internalBufferMap = new HashMap<>();
    this.externalBufferMap = new HashMap<>();
    this.iterMap = new HashMap<>();
    this.repVector = null;
  }

  @Override
  public List<Block> generate() {

    // // For every clustered actors of the map that contains a Schedule
    // for (Entry<AbstractActor, Schedule> e : mapper.entrySet()) {
    // // Get every FIFO and build a FIFO Buffer Map
    // Map<Fifo, Buffer> bufferMap = generateFifoBuffer(e.getValue());
    //
    // }

    // Build CoreBlock
    CoreBlock cb = CodegenModelUserFactory.createCoreBlock();
    cb.setCoreID(0);
    cb.setName(scenario.getSimulationInfo().getMainOperator().getInstanceName());
    cb.setCoreType("x86");

    // Construct codegen model corresponding to cluster in
    // 1st step: Parse every cluster
    for (Entry<AbstractActor, Schedule> clusterSchedule : mapper.entrySet()) {
      this.repVector = PiBRV.compute((PiGraph) clusterSchedule.getKey(), BRVMethod.LCM);
      cb.getLoopBlock().getCodeElts().add(buildClusterBlockRec(clusterSchedule.getValue()));
    }

    cb.getDefinitions().addAll(internalBufferMap.values());
    List<Block> blockList = new LinkedList<>();
    blockList.add(cb);

    return blockList;
  }

  private final Block buildClusterBlockRec(final Schedule schedule) {
    Block outputBlock = null;

    // If it's a sequential schedule, fill information for ClusterBlock
    if (schedule instanceof SequentialHiearchicalSchedule) {

      // Retrieve cluster actor
      PiGraph cluster = (PiGraph) ((SequentialHiearchicalSchedule) schedule).getAttachedActor();

      // Build and fill ClusterBlock
      ClusterBlock clusterBlock = CodegenFactory.eINSTANCE.createClusterBlock();
      clusterBlock.setName(cluster.getName());
      clusterBlock.setSchedule(ClusteringHelper.printScheduleRec(schedule));

      // If the cluster has to be repeated few times, build a FiniteLoopBlock
      if (schedule.getRepetition() > 1) {
        FiniteLoopBlock flb = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
        IntVar iterator = CodegenFactory.eINSTANCE.createIntVar();
        iterator.setName("repetition_" + cluster.getName());
        iterMap.put(cluster, iterator);
        flb.setIter(iterator);
        flb.setNbIter((int) schedule.getRepetition());
        flb.getCodeElts().add(clusterBlock);
        outputBlock = flb;
      } else {
        outputBlock = clusterBlock;
      }

      // Make memory allocation for internal buffer
      buildInternalClusterBuffer(cluster);
      buildExternalClusterBuffer(cluster);

      // Call again itself to explore child
      for (Schedule e : schedule.getChildren()) {
        clusterBlock.getCodeElts().add(buildClusterBlockRec(e));
      }

      // If it's an actor firing, build all FunctionCall
    } else if (schedule instanceof SequentialActorSchedule) {

      // Retrieve each actor to fire
      AbstractActor actor = schedule.getActors().get(0);
      FunctionCall functionCall = CodegenFactory.eINSTANCE.createFunctionCall();
      functionCall.setActorName(actor.getName());
      functionCall.setName(actor.getName());
      LoopBlock callSet = CodegenFactory.eINSTANCE.createLoopBlock();
      callSet.getCodeElts().add(functionCall);

      // If actors has to be repeated few times, build a FiniteLoopBlock
      if (schedule.getRepetition() > 1) {
        FiniteLoopBlock flb = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
        IntVar iterator = CodegenFactory.eINSTANCE.createIntVar();
        iterator.setName("repetition_" + actor.getName());
        iterMap.put(actor, iterator);
        flb.setIter(iterator);
        flb.setNbIter((int) schedule.getRepetition());
        flb.getCodeElts().addAll(callSet.getCodeElts());
        outputBlock = flb;
      } else {
        outputBlock = callSet;
      }

    }

    return outputBlock;
  }

  private static final List<Fifo> getInternalClusterFifo(PiGraph cluster) {
    List<Fifo> internalFifo = new LinkedList<>();
    for (Fifo fifo : cluster.getFifos()) {
      if ((fifo.getSource() instanceof DataInputInterface) || (fifo.getTarget() instanceof DataOutputInterface)) {
        // do nothing
      } else {
        internalFifo.add(fifo);
      }
    }
    return internalFifo;
  }

  private final List<Buffer> buildInternalClusterBuffer(PiGraph cluster) {
    List<Buffer> internalBuffer = new LinkedList<>();
    // Make memory allocation for internal buffer
    int i = 0;
    for (Fifo fifo : getInternalClusterFifo(cluster)) {
      Buffer buffer = CodegenFactory.eINSTANCE.createBuffer();
      buffer.setName("clust_" + ((AbstractActor) fifo.getSource()).getName() + "_to_"
          + ((AbstractActor) fifo.getTarget()).getName() + "_" + i++);
      buffer.setType(fifo.getType());
      buffer.setTypeSize(scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
      buffer.setSize(fifo.getSourcePort().getExpression().evaluate() * this.repVector.get(fifo.getSource()));
      internalBufferMap.put(fifo, buffer);
      internalBuffer.add(buffer);
    }

    return internalBuffer;
  }

  private final List<Buffer> buildExternalClusterBuffer(PiGraph cluster) {
    List<Buffer> externalBuffer = new LinkedList<>();
    List<Fifo> externalFifo = new LinkedList<Fifo>();
    externalFifo.addAll(cluster.getFifos());
    externalFifo.removeAll(getInternalClusterFifo(cluster));
    for (Fifo fifo : externalFifo) {
      Fifo outsideFifo = null;
      PortDirection direction;
      if (fifo.getSource() instanceof DataInputInterface) {
        outsideFifo = getOutsideIncomingFifo(fifo);
        direction = PortDirection.INPUT;
      } else {
        outsideFifo = getOutsideOutgoingFifo(fifo);
        direction = PortDirection.OUTPUT;
      }

      Buffer buffer = internalBufferMap.get(outsideFifo);
      if (buffer == null) {
        externalBufferMap.get(outsideFifo);
      }
      if (this.repVector.get(cluster) > 1) {
        IteratedBuffer iteratedBuffer = CodegenFactory.eINSTANCE.createIteratedBuffer();
        iteratedBuffer.setBuffer(buffer);
        iteratedBuffer.setIter(iterMap.get(cluster));
        if (direction.equals(PortDirection.INPUT)) {
          iteratedBuffer.setIterSize(outsideFifo.getTargetPort().getExpression().evaluate());
        } else {
          iteratedBuffer.setIterSize(outsideFifo.getSourcePort().getExpression().evaluate());
        }
        buffer = iteratedBuffer;
      }

      externalBufferMap.put(fifo, buffer);
      externalBuffer.add(buffer);
    }
    return externalBuffer;
  }

  private Fifo getOutsideOutgoingFifo(Fifo inFifo) {
    AbstractActor targetActor = (AbstractActor) inFifo.getTarget();
    Fifo outFifo = null;
    if (targetActor instanceof DataOutputInterface) {
      outFifo = ((DataOutputPort) ((DataOutputInterface) targetActor).getGraphPort()).getOutgoingFifo();
    }
    return outFifo;
  }

  private Fifo getOutsideIncomingFifo(Fifo inFifo) {
    AbstractActor sourceActor = (AbstractActor) inFifo.getSource();
    Fifo outFifo = null;
    if (sourceActor instanceof DataInputInterface) {
      outFifo = ((DataInputPort) ((DataInputInterface) sourceActor).getGraphPort()).getIncomingFifo();
    }
    return outFifo;
  }

  // private Buffer buildIncomingBuffer(Fifo inFifo) {
  // Map<AbstractVertex, Long> repVector = PiBRV.compute(inFifo.getContainingPiGraph(), BRVMethod.LCM);
  // AbstractActor source = (AbstractActor) inFifo.getSource();
  // Buffer buff = buffmap.get(inFifo);
  // // If actors is repeated, prepare a iterated buffer
  // if (repVector.get(source) > 1) {
  // IteratedBuffer iteratedBuffer = CodegenFactory.eINSTANCE.createIteratedBuffer();
  // iteratedBuffer.setIter(itermap.get(source));
  // iteratedBuffer.setIterSize(inFifo.getSourcePort().getExpression().evaluate());
  // iteratedBuffer.setBuffer(buff);
  // buff = iteratedBuffer;
  // }
  // if (source instanceof PiGraph) {
  // PiGraph graph = (PiGraph) source;
  // List<DataOutputInterface> doi = new LinkedList<>();
  // doi.addAll(graph.getDataOutputInterfaces());
  // doi.removeIf(x -> !x.getName().equals(inFifo.getSourcePort().getName()));
  // Fifo incomingFifo = ((DataInputPort) doi.get(0).getDataPort()).getIncomingFifo();
  // buffmap.put(incomingFifo, buff);
  // return buildIncomingBuffer(incomingFifo);
  // } else {
  // return buff;
  // }
  // }
  //
  // private Buffer buildOutgoingBuffer(Fifo inFifo) {
  // Map<AbstractVertex, Long> repVector = PiBRV.compute(inFifo.getContainingPiGraph(), BRVMethod.LCM);
  // AbstractActor target = (AbstractActor) inFifo.getTarget();
  // Buffer buff = buffmap.get(inFifo);
  // // If actors is repeated, prepare a iterated buffer
  // if (repVector.get(target) > 1) {
  // IteratedBuffer iteratedBuffer = CodegenFactory.eINSTANCE.createIteratedBuffer();
  // iteratedBuffer.setIter(itermap.get(target));
  // iteratedBuffer.setIterSize(inFifo.getTargetPort().getExpression().evaluate());
  // iteratedBuffer.setBuffer(buff);
  // iteratedBuffer.setName("iterBuff_" + target.getName());
  // buff = iteratedBuffer;
  // }
  // if (target instanceof PiGraph) {
  // PiGraph graph = (PiGraph) target;
  // List<DataInputInterface> dii = new LinkedList<>();
  // dii.addAll(graph.getDataInputInterfaces());
  // dii.removeIf(x -> !x.getName().equals(inFifo.getTargetPort().getName()));
  // Fifo outgoingFifo = ((DataOutputPort) dii.get(0).getDataPort()).getOutgoingFifo();
  // buffmap.put(outgoingFifo, buff);
  // return buildOutgoingBuffer(outgoingFifo);
  // } else {
  // return buff;
  // }
  // }

  // private Map<Fifo, Buffer> generateFifoBuffer(Schedule schedule) {
  // Map<Fifo, Buffer> bufferMap = new LinkedHashMap<>();
  //
  // // Retrieve Fifo involve in the schedule
  // Set<Fifo> fifoSet = new LinkedHashSet<>();
  // for (AbstractActor a : schedule.getActors()) {
  // a.getDataInputPorts().stream().forEach(x -> fifoSet.add(x.getIncomingFifo()));
  // a.getDataOutputPorts().stream().forEach(x -> fifoSet.add(x.getOutgoingFifo()));
  // }
  //
  // // Build Buffer block for each Fifo
  // Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(this.piAlgo, BRVMethod.LCM);
  // for (Fifo fifo : fifoSet) {long
  // Buffer buffer = CodegenFactory.eINSTANCE.createBuffer();
  // DataOutputPort dop = fifo.getSourcePort();
  // DataInputPort dip = fifo.getTargetPort();
  // // Retrieve the fifo outside the cluster if not included
  // if (dop.getContainingActor() instanceof DataInputInterface) {
  // // Case of a DataInputInterface
  // DataInputInterface dii = (DataInputInterface) dop.getContainingActor();
  // DataInputPort outsideDip = (DataInputPort) dii.getGraphPort();
  // DataOutputPort outsideDop = outsideDip.getIncomingFifo().getSourcePort();
  // buffer.setSize(outsideDip.getExpression().evaluate() * repetitionVector.get(outsideDip.getContainingActor()));
  // buffer.setName(outsideDop.getContainingActor().getName() + dip.getContainingActor().getName());
  // } else if (dip.getContainingActor() instanceof DataOutputInterface) {
  // // Case of a DataOutputInterface
  // DataOutputInterface doi = (DataOutputInterface) dip.getContainingActor();
  // DataOutputPort outsideDop = (DataOutputPort) doi.getGraphPort();
  // DataInputPort outsideDip = outsideDop.getOutgoingFifo().getTargetPort();
  // buffer.setSize(outsideDop.getExpression().evaluate() * repetitionVector.get(outsideDop.getContainingActor()));
  // buffer.setName(dop.getContainingActor().getName() + outsideDip.getContainingActor().getName());
  // } else {
  // // Fifo inside the cluster
  // buffer.setSize(dop.getExpression().evaluate() * repetitionVector.get(dop.getContainingActor()));
  // buffer.setType(fifo.getType());
  // buffer.setName(dop.getContainingActor().getName() + dip.getContainingActor().getName());
  // }
  // buffer.setTypeSize(scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
  // bufferMap.put(fifo, buffer);
  // }
  //
  // return bufferMap;
  //
  // }

  // private AbstractActor retrieveNextAbstractActor(Fifo inputFifo) {
  //
  // DataOutputPort dop = inputFifo.getSourcePort();
  // DataInputPort dip = inputFifo.getTargetPort();
  // while ((dip instanceof InterfaceActor) || (dop instanceof InterfaceActor)) {
  // if (dop.getContainingActor() instanceof DataInputInterface) {
  // // Case of a DataInputInterface
  // } else if (dip.getContainingActor() instanceof DataOutputInterface) {
  // // Case of a DataOutputInterface
  // }
  //
  // }
  //
  // return Fifo;
  // }

}
