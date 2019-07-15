package org.preesm.codegen.xtend.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

  final Map<Fifo, Buffer> buffmap;

  final Map<AbstractActor, IntVar> itermap;

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
    this.buffmap = new HashMap<>();
    this.itermap = new HashMap<>();
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
    for (Schedule clusterSchedule : mapper.values()) {
      cb.getLoopBlock().getCodeElts().add(buildClusterBlockRec(clusterSchedule));
    }

    cb.getDefinitions().addAll(buffmap.values());
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

      // Make memory allocation for internal buffer
      buildInternalClusterBuffer(cluster);
      buildExternalClusterBuffer(cluster);

      // Call again itself to explore child
      for (Schedule e : schedule.getChildren()) {
        clusterBlock.getCodeElts().add(buildClusterBlockRec(e));
      }

      // If the cluster has to be repeated few times, build a FiniteLoopBlock
      if (schedule.getRepetition() > 1) {
        FiniteLoopBlock flb = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
        IntVar iterator = CodegenFactory.eINSTANCE.createIntVar();
        iterator.setName("repetition_" + cluster.getName());
        flb.setIter(iterator);
        flb.setNbIter((int) schedule.getRepetition());
        flb.getCodeElts().add(clusterBlock);
        outputBlock = flb;
      } else {
        outputBlock = clusterBlock;
      }

      // If it's an actor firing, build all FunctionCall
    } else if (schedule instanceof SequentialActorSchedule) {

      // Build a set of FunctionCall
      LoopBlock callSet = CodegenFactory.eINSTANCE.createLoopBlock();
      StringBuilder iterName = new StringBuilder();

      // Retrieve each actor to fire
      for (AbstractActor a : schedule.getActors()) {
        FunctionCall functionCall = CodegenFactory.eINSTANCE.createFunctionCall();
        functionCall.setActorName(a.getName());
        functionCall.setName(a.getName());
        iterName.append(a.getName());
        callSet.getCodeElts().add(functionCall);
      }

      // If actors has to be repeated few times, build a FiniteLoopBlock
      if (schedule.getRepetition() > 1) {
        FiniteLoopBlock flb = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
        IntVar iterator = CodegenFactory.eINSTANCE.createIntVar();
        iterator.setName("repetition_" + iterName.toString());
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
    Map<AbstractVertex, Long> repVector = PiBRV.compute(cluster, BRVMethod.LCM);
    for (Fifo fifo : getInternalClusterFifo(cluster)) {
      Buffer buffer = CodegenFactory.eINSTANCE.createBuffer();
      buffer.setName("clust_" + ((AbstractActor) fifo.getSource()).getName() + "_to_"
          + ((AbstractActor) fifo.getTarget()).getName() + "_" + i++);
      buffer.setType(fifo.getType());
      buffer.setTypeSize(scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType()));
      buffer.setSize(fifo.getSourcePort().getExpression().evaluate() * repVector.get(fifo.getSource()));
      buffmap.put(fifo, buffer);
      internalBuffer.add(buffer);
    }

    return internalBuffer;
  }

  private final List<Buffer> buildExternalClusterBuffer(PiGraph cluster) {

    List<Fifo> externalFifo = new LinkedList<Fifo>();
    externalFifo.addAll(cluster.getFifos());
    externalFifo.removeAll(getInternalClusterFifo(cluster));

    List<Buffer> bufferList = new LinkedList<Buffer>();
    for (Fifo fifo : externalFifo) {
      Fifo correspondingFifo = getCorrespondingFifo(fifo);
      bufferList.add(buildIncomingBuffer(correspondingFifo));
    }
    return bufferList;
  }

  private Fifo getCorrespondingOutgoingFifoRec(Fifo inFifo) {
    AbstractActor targetActor = (AbstractActor) inFifo.getTarget();
    Fifo outFifo = inFifo;
    if (targetActor instanceof DataInputInterface) {
      outFifo = getCorrespondingOutgoingFifoRec(
          ((DataOutputPort) ((DataInputInterface) targetActor).getGraphPort()).getOutgoingFifo());
    }
    return outFifo;
  }

  private Buffer buildCorrespondingBuffer(Fifo inFifo) {
    if (inFifo.getSource() instanceof DataInputInterface) {
      return buildOutgoingBuffer(inFifo);
    } else {
      return buildOutgoingBuffer(inFifo);
    }

  }

  private Buffer buildIncomingBuffer(Fifo inFifo) {
    Map<AbstractVertex, Long> repVector = PiBRV.compute(inFifo.getContainingPiGraph(), BRVMethod.LCM);
    AbstractActor source = (AbstractActor) inFifo.getSource();
    Buffer buff = buffmap.get(inFifo);
    // If actors is repeated, prepare a iterated buffer
    if (repVector.get(source) > 1) {
      IteratedBuffer iteratedBuffer = CodegenFactory.eINSTANCE.createIteratedBuffer();
      iteratedBuffer.setIter(itermap.get(source));
      iteratedBuffer.setIterSize(inFifo.getSourcePort().getExpression().evaluate());
      iteratedBuffer.setBuffer(buff);
      buff = iteratedBuffer;
    }
    if (source instanceof PiGraph) {
      PiGraph graph = (PiGraph) source;
      List<DataOutputInterface> doi = new LinkedList<>();
      doi.addAll(graph.getDataOutputInterfaces());
      doi.removeIf(x -> !x.getName().equals(inFifo.getSourcePort().getName()));
      Fifo incomingFifo = ((DataInputPort) doi.get(0).getDataPort()).getIncomingFifo();
      buffmap.put(incomingFifo, buff);
      return buildIncomingBuffer(incomingFifo);
    } else {
      return buff;
    }
  }

  private Buffer buildOutgoingBuffer(Fifo inFifo) {
    Map<AbstractVertex, Long> repVector = PiBRV.compute(inFifo.getContainingPiGraph(), BRVMethod.LCM);
    AbstractActor target = (AbstractActor) inFifo.getTarget();
    Buffer buff = buffmap.get(inFifo);
    // If actors is repeated, prepare a iterated buffer
    if (repVector.get(target) > 1) {
      IteratedBuffer iteratedBuffer = CodegenFactory.eINSTANCE.createIteratedBuffer();
      iteratedBuffer.setIter(itermap.get(target));
      iteratedBuffer.setIterSize(inFifo.getTargetPort().getExpression().evaluate());
      iteratedBuffer.setBuffer(buff);
      buff = iteratedBuffer;
    }
    if (target instanceof PiGraph) {
      PiGraph graph = (PiGraph) target;
      List<DataInputInterface> dii = new LinkedList<>();
      dii.addAll(graph.getDataInputInterfaces());
      dii.removeIf(x -> !x.getName().equals(inFifo.getTargetPort().getName()));
      Fifo outgoingFifo = ((DataOutputPort) dii.get(0).getDataPort()).getOutgoingFifo();
      buffmap.put(outgoingFifo, buff);
      return buildOutgoingBuffer(outgoingFifo);
    } else {
      return buff;
    }
  }

  private Fifo getCorrespondingIncomingFifoRec(Fifo inFifo) {
    AbstractActor sourceActor = (AbstractActor) inFifo.getSource();
    Fifo outFifo = inFifo;
    if (sourceActor instanceof DataOutputInterface) {
      outFifo = getCorrespondingIncomingFifoRec(
          ((DataInputPort) ((DataOutputInterface) sourceActor).getGraphPort()).getIncomingFifo());
    }
    return outFifo;
  }

  private Fifo getCorrespondingFifo(Fifo inFifo) {
    if (inFifo.getSource() instanceof DataInputInterface) {
      return getCorrespondingIncomingFifoRec(inFifo);
    } else {
      return getCorrespondingOutgoingFifoRec(inFifo);
    }
  }

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
