package org.preesm.codegen.model.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.model.ClusterRaiserSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.ClusterRaiserBlock;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.DynamicBuffer;
import org.preesm.codegen.model.FiniteLoopClusterRaiserBlock;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.IteratedBuffer;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.Variable;
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
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

public class CodegenModelGeneratorClusteringRaiser {

  final Scenario                                       scenario;
  final Design                                         archi;
  final Map<AbstractActor, IntVar>                     iterMap;
  protected Map<ComponentInstance, ClusterRaiserBlock> clusterBlocks;
  private final String                                 scheduleMapping;
  private final Map<Port, DynamicBuffer>               internalBufferHeapMap;
  private final Map<Port, Buffer>                      internalBufferStackMap;
  private final Map<Port, Buffer>                      externalBufferMap;
  private final Map<Port, DynamicBuffer>               specialBufferHeapMap;
  private final Map<Port, Buffer>                      specialBufferStackMap;

  private final Map<ConfigInputPort, IntVar> parameterMap;

  // private final FunctionCall clusterFunc;
  Long stackSize;

  // private AllocationToCodegenBuffer memoryLinker;
  private FiniteLoopClusterRaiserBlock lastLevel;

  public CodegenModelGeneratorClusteringRaiser(Design archi, Scenario scenario, String schedulesMap, Long stackSize) {
    super();
    this.clusterBlocks = new LinkedHashMap<>();
    this.archi = archi;
    this.scenario = scenario;
    this.iterMap = new HashMap<>();
    this.scheduleMapping = schedulesMap;
    this.internalBufferHeapMap = new LinkedHashMap<>();
    this.internalBufferStackMap = new LinkedHashMap<>();
    this.externalBufferMap = new LinkedHashMap<>();
    this.specialBufferHeapMap = new LinkedHashMap<>();
    this.specialBufferStackMap = new LinkedHashMap<>();
    this.parameterMap = new LinkedHashMap<>();

    // this.clusterFunc = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
    this.stackSize = stackSize;
    this.lastLevel = CodegenModelUserFactory.eINSTANCE.createFiniteLoopClusterRaiserBlock();

  }

  public List<Block> generate(boolean isLastCluster, Map<AbstractVertex, Long> brv, List<String[]> loopedBuffer) {

    // 0. init coreBlocks

    for (final ComponentInstance cmp : this.archi.getOperatorComponentInstances()) {
      if (!this.clusterBlocks.containsKey(cmp)) {
        final ClusterRaiserBlock operatorBlock = CodegenModelUserFactory.eINSTANCE.createClusterRaiserBlock();
        final String coreType = this.archi.getComponentHolder().getComponents().get(0).getVlnv().getName();
        operatorBlock.setCoreType(coreType);
        this.clusterBlocks.put(cmp, operatorBlock);
      }

    }

    // final int[] indexLoopElt = new int[100];
    // final int indexLoopEltClosed[] = new int[100];

    // final Map<org.preesm.model.pisdf.AbstractVertex,
    // Long> brv = PiBRV.compute(this.scenario.getAlgorithm(), BRVMethod.TOPOLOGY);

    // 1. Retrieve original cluster actor
    final List<ClusterRaiserSchedule> cs = new LinkedList<>();
    final List<AbstractActor> actorList = this.scenario.getAlgorithm().getAllExecutableActors();
    // for (Entry<AbstractActor, Schedule> entry : this.scheduleMapping.entrySet()) {

    final String schedule = this.scheduleMapping;
    final String scheduleMonoCore = schedule.replaceAll("/", ""); // doesn't deal with parallelism
    final String[] splitActor = scheduleMonoCore.split("\\*");
    String[] splitRate;
    int openLoopCounter = 0;

    for (int i = 0; i < splitActor.length; i++) {
      final ClusterRaiserSchedule sc = ScheduleFactory.eINSTANCE.createClusterRaiserSchedule();
      if (splitActor[i].contains("(")) {
        sc.setBeginLoop(true);
        openLoopCounter++;
        splitRate = splitActor[i].split("\\(");
        sc.setIterator(Integer.parseInt(splitRate[0]));
        splitActor[i] = splitRate[1];
      } else {
        sc.setBeginLoop(false);
        sc.setIterator(1);
      }
      if (splitActor[i].contains(")")) {
        openLoopCounter--;
        sc.setEndLoop(true);
        sc.setEndLoopNb(compterOccurrences(splitActor[i], ')'));
        splitActor[i] = splitActor[i].replaceAll("\\)", "");

      } else {
        sc.setEndLoop(false);
      }
      sc.setActor(PiMMUserFactory.instance.createActor());
      sc.getActor().setName(splitActor[i]);

      if ((openLoopCounter >= 1 && !sc.isBeginLoop())) {
        sc.setLoopPrec(true);
      } else {
        sc.setLoopPrec(false);
      }
      // sc.setCmp(cmp);
      cs.add(sc);

    }
    // }
    for (final ClusterRaiserSchedule element : cs) {
      for (final AbstractActor element2 : actorList) {
        if (element.getActor().getName().equals(element2.getName())) {
          element.setActor(element2);
        }
      }
    }
    // identify core used
    final ComponentInstance cmp = this.archi.getOperatorComponentInstances().get(0);
    final ClusterRaiserBlock operatorBlock = this.clusterBlocks.get(cmp);

    operatorBlock.setComment("cluster");
    final String fileName = this.scenario.getAlgorithm().getActorPath().replace("/", "_");
    operatorBlock.setName("Cluster_" + fileName);

    // operatorBlock.setName("Cluster_" + this.scenario.getAlgorithm().getName());
    // 2. return buffer to printer
    final List<Buffer> buffers = new ArrayList<>();
    Long stackSizeDecounter = stackSize;
    for (final ClusterRaiserSchedule sc : cs) {
      // boolean SuccessorIsInterface = false;
      // boolean PredecessorIsInterface = false;
      // if (!sc.getActor().getDirectSuccessors().isEmpty()) {
      // for (int i = 0; i < sc.getActor().getDirectSuccessors().size(); i++)
      // if (sc.getActor().getDirectSuccessors().get(i) instanceof DataOutputInterface) {
      // SuccessorIsInterface = true;
      // }
      // }
      // if (!sc.getActor().getDirectPredecessors().isEmpty()) {
      // for (int i = 0; i < sc.getActor().getDirectPredecessors().size(); i++)
      // if (sc.getActor().getDirectPredecessors().get(i) instanceof DataInputInterface) {
      // PredecessorIsInterface = true;
      // }
      // }
      // 2.1- retrieve interface buffer
      int nb_buffAtoA = sc.getActor().getDataOutputPorts().size();

      if (sc.getActor().getDataInputPorts().size() > 0) {
        for (int i = 0; i < sc.getActor().getDataInputPorts().size(); i++) {
          if (sc.getActor().getDataInputPorts().get(i).getFifo().getSource() instanceof DataInputInterface) {

            final Long nbTokenIn = ((DataInputInterface) sc.getActor().getDataInputPorts().get(i).getFifo().getSource())
                .getDataPort().getExpression().evaluate() * brv.get(sc.getActor());
            final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();

            buffer.setName(
                ((DataInputInterface) sc.getActor().getDataInputPorts().get(i).getFifo().getSource()).getName());
            buffer.setNbToken(nbTokenIn);

            final String str = ((DataInputInterface) sc.getActor().getDataInputPorts().get(i).getFifo().getSource())
                .getDataPort().getFifo().getType();
            buffer.setType(str);
            buffer.setComment("input interface");
            buffer.setTokenTypeSizeInBit(8);
            operatorBlock.getSinkFifoBuffers().add(buffer);

            this.externalBufferMap.put(sc.getActor().getDataInputPorts().get(i), buffer);
            buffers.add(buffer);
          }
        }
      }
      if (sc.getActor().getDataOutputPorts().size() > 0) {
        nb_buffAtoA = sc.getActor().getDataOutputPorts().size()
            - this.scenario.getAlgorithm().getDataOutputInterfaces().size();
        for (int i = 0; i < sc.getActor().getDataOutputPorts().size(); i++) {
          if (sc.getActor().getDataOutputPorts().get(i).getFifo().getTarget() instanceof DataOutputInterface) {

            final Long nbTokenIn = sc.getActor().getDataOutputPorts().get(i).getExpression().evaluate()
                * brv.get(sc.getActor());
            final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
            buffer.setName(
                ((DataOutputInterface) sc.getActor().getDataOutputPorts().get(i).getFifo().getTarget()).getName());
            buffer.setNbToken(nbTokenIn);
            final String str = ((DataOutputInterface) sc.getActor().getDataOutputPorts().get(i).getFifo().getTarget())
                .getDataInputPorts().get(0).getIncomingFifo().getType();
            buffer.setType(str);
            buffer.setComment("output interface");
            buffer.setTokenTypeSizeInBit(8);
            operatorBlock.getSinkFifoBuffers().add(buffer);
            this.externalBufferMap.put(sc.getActor().getDataOutputPorts().get(i), buffer);
            buffers.add(buffer);
          }
        }
      }

      if (sc.getActor() instanceof Actor || sc.getActor() instanceof DataOutputInterface) {

        if (sc.getActor().getDataOutputPorts().size() > 0) {
          for (int i = 0; i < sc.getActor().getDataOutputPorts().size(); i++) {
            if (!(sc.getActor().getDataOutputPorts().get(i).getFifo().getTarget() instanceof DataOutputInterface)) {
              final FunctionCall fct = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
              final Long nbTokenIn = sc.getActor().getDataOutputPorts().get(i).getExpression().evaluate()
                  * brv.get(sc.getActor());

              if (stackSizeDecounter - nbTokenIn < 0) { // if stack overflow

                final DynamicBuffer buffer = CodegenModelUserFactory.eINSTANCE.createDynamicBuffer();
                buffer.setName(sc.getActor().getDataOutputPorts().get(i).getName() + "__"
                    + sc.getActor().getDataOutputPorts().get(i).getFifo().getTargetPort().getName() + "__"
                    + sc.getActor().getName() + "__"
                    + ((AbstractActor) sc.getActor().getDataOutputPorts().get(i).getFifo().getTarget()).getName());
                buffer.setNbToken(nbTokenIn);

                buffer.setType(sc.getActor().getDataOutputPorts().get(i).getFifo().getType());
                buffer.setComment("store data port");
                buffer.setTokenTypeSizeInBit(8);
                fct.addParameter(buffer, PortDirection.OUTPUT);
                operatorBlock.getSinkFifoBuffers().add(buffer);

                if (sc.getActor().getDataOutputPorts().get(i).getFifo().isHasADelay()) {
                  buffer.setComment(String.valueOf(
                      sc.getActor().getDataOutputPorts().get(i).getFifo().getDelay().getExpression().evaluate()));
                  operatorBlock.getInitBuffers().add(buffer);

                }
                this.internalBufferHeapMap.put(sc.getActor().getDataOutputPorts().get(i), buffer);
                operatorBlock.getDefinitions().add(buffer);
                operatorBlock.getDeclarations().add(buffer);
                buffers.add(buffer);
              } else {

                final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
                buffer.setName(sc.getActor().getDataOutputPorts().get(i).getName() + "__"
                    + sc.getActor().getDataOutputPorts().get(i).getFifo().getTargetPort().getName() + "__"
                    + sc.getActor().getName() + "__"
                    + ((AbstractActor) sc.getActor().getDataOutputPorts().get(i).getFifo().getTarget()).getName());
                buffer.setNbToken(nbTokenIn);

                buffer.setType(sc.getActor().getDataOutputPorts().get(i).getFifo().getType());
                buffer.setComment("store data port");
                buffer.setTokenTypeSizeInBit(8);
                fct.addParameter(buffer, PortDirection.OUTPUT);
                operatorBlock.getSinkFifoBuffers().add(buffer);

                if (sc.getActor().getDataOutputPorts().get(i).getFifo().isHasADelay()) {
                  buffer.setComment(String.valueOf(
                      sc.getActor().getDataOutputPorts().get(i).getFifo().getDelay().getExpression().evaluate()));
                  operatorBlock.getInitBuffers().add(buffer);

                }
                this.internalBufferStackMap.put(sc.getActor().getDataOutputPorts().get(i), buffer);
                operatorBlock.getDefinitions().add(buffer);
                buffers.add(buffer);

                final int size = sizeof(buffer.getType());
                stackSizeDecounter = stackSizeDecounter - nbTokenIn * size;
              }

            }
          }
        }
      } else if (sc.getActor() instanceof BroadcastActor) {
        for (int i = 0; i < sc.getActor().getDataOutputPorts().size(); i++) {
          final FunctionCall fct = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
          final Long nbTokenIn = sc.getActor().getDataOutputPorts().get(i).getExpression().evaluate()
              * brv.get(sc.getActor());
          if (stackSizeDecounter - nbTokenIn < 0) {
            final DynamicBuffer buffer = CodegenModelUserFactory.eINSTANCE.createDynamicBuffer();
            // final String str = ((AbstractActor)
            // sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0)
            // .getName();
            // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName();
            buffer.setName(sc.getActor().getDataOutputPorts().get(i).getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0).getName()
                + "__" + sc.getActor().getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName());
            buffer.setNbToken(nbTokenIn);
            buffer.setType(sc.getActor().getDataOutputPorts().get(i).getFifo().getType());
            buffer.setComment("store data port");
            buffer.setTokenTypeSizeInBit(8);
            fct.addParameter(buffer, PortDirection.OUTPUT);
            operatorBlock.getSinkFifoBuffers().add(buffer);
            this.specialBufferHeapMap.put(sc.getActor().getDataOutputPorts().get(i), buffer);
            operatorBlock.getDefinitions().add(buffer);
            operatorBlock.getDeclarations().add(buffer);
            buffers.add(buffer);
          } else {
            final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
            // final String str = ((AbstractActor)
            // sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0)
            // .getName();
            // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName();
            buffer.setName(sc.getActor().getDataOutputPorts().get(i).getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0).getName()
                + "__" + sc.getActor().getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName());
            buffer.setNbToken(nbTokenIn);
            buffer.setType(sc.getActor().getDataOutputPorts().get(i).getFifo().getType());
            buffer.setComment("store data port");
            buffer.setTokenTypeSizeInBit(8);
            fct.addParameter(buffer, PortDirection.OUTPUT);
            operatorBlock.getSinkFifoBuffers().add(buffer);
            this.specialBufferStackMap.put(sc.getActor().getDataOutputPorts().get(i), buffer);
            operatorBlock.getDefinitions().add(buffer);
            buffers.add(buffer);
            final int size = sizeof(buffer.getType());
            stackSizeDecounter = stackSizeDecounter - nbTokenIn * size;
          }
        }
      } else if (sc.getActor() instanceof RoundBufferActor
          && !(sc.getActor().getDirectSuccessors().get(0) instanceof DataOutputInterface)) {

        final FunctionCall fct = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
        final Long nbTokenIn = sc.getActor().getDataOutputPorts().get(0).getExpression().evaluate()
            * brv.get(sc.getActor());
        if (stackSizeDecounter - nbTokenIn < 0) {
          final DynamicBuffer buffer = CodegenModelUserFactory.eINSTANCE.createDynamicBuffer();
          // final String str = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0)
          // .getName();
          // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName();
          buffer.setName(sc.getActor().getDataOutputPorts().get(0).getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0).getName() + "__"
              + sc.getActor().getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName());
          buffer.setNbToken(nbTokenIn);
          buffer.setType(sc.getActor().getDataOutputPorts().get(0).getFifo().getType());
          buffer.setComment("store data port");
          buffer.setTokenTypeSizeInBit(8);
          fct.addParameter(buffer, PortDirection.OUTPUT);
          operatorBlock.getSinkFifoBuffers().add(buffer);
          this.specialBufferHeapMap.put(sc.getActor().getDataOutputPorts().get(0), buffer);
          operatorBlock.getDefinitions().add(buffer);
          operatorBlock.getDeclarations().add(buffer);
          buffers.add(buffer);
        } else {
          final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createDynamicBuffer();
          // final String str = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0)
          // .getName();
          // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName();
          buffer.setName(sc.getActor().getDataOutputPorts().get(0).getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0).getName() + "__"
              + sc.getActor().getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName());
          buffer.setNbToken(nbTokenIn);
          buffer.setType(sc.getActor().getDataOutputPorts().get(0).getFifo().getType());
          buffer.setComment("store data port");
          buffer.setTokenTypeSizeInBit(8);
          fct.addParameter(buffer, PortDirection.OUTPUT);
          operatorBlock.getSinkFifoBuffers().add(buffer);
          this.specialBufferStackMap.put(sc.getActor().getDataOutputPorts().get(0), buffer);
          operatorBlock.getDefinitions().add(buffer);
          buffers.add(buffer);
          final int size = sizeof(buffer.getType());
          stackSizeDecounter = stackSizeDecounter - nbTokenIn * size;
        }

      } else if (sc.getActor() instanceof ForkActor) {
        for (int i = 0; i < sc.getActor().getDataOutputPorts().size(); i++) {
          final FunctionCall fct = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
          final Long nbTokenIn = sc.getActor().getDataOutputPorts().get(i).getExpression().evaluate()
              * brv.get(sc.getActor());// ?
          if (stackSizeDecounter - nbTokenIn < 0) {
            final DynamicBuffer buffer = CodegenModelUserFactory.eINSTANCE.createDynamicBuffer();
            // final String str = ((AbstractActor)
            // sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0)
            // .getName();
            // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName();
            buffer.setName(sc.getActor().getDataOutputPorts().get(i).getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0).getName()
                + "__" + sc.getActor().getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName());
            buffer.setNbToken(nbTokenIn);
            buffer.setType(sc.getActor().getDataOutputPorts().get(i).getFifo().getType());
            buffer.setComment("store data port");
            buffer.setTokenTypeSizeInBit(8);
            fct.addParameter(buffer, PortDirection.OUTPUT);
            operatorBlock.getSinkFifoBuffers().add(buffer);
            this.specialBufferHeapMap.put(sc.getActor().getDataOutputPorts().get(i), buffer);
            operatorBlock.getDefinitions().add(buffer);
            operatorBlock.getDeclarations().add(buffer);
            buffers.add(buffer);
          } else {
            final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
            // final String str = ((AbstractActor)
            // sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0)
            // .getName();
            // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName();
            buffer.setName(sc.getActor().getDataOutputPorts().get(i).getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getDataInputPorts().get(0).getName()
                + "__" + sc.getActor().getName() + "__"
                + ((AbstractActor) sc.getActor().getDirectSuccessors().get(i)).getName());
            buffer.setNbToken(nbTokenIn);
            buffer.setType(sc.getActor().getDataOutputPorts().get(i).getFifo().getType());
            buffer.setComment("store data port");
            buffer.setTokenTypeSizeInBit(8);
            fct.addParameter(buffer, PortDirection.OUTPUT);
            operatorBlock.getSinkFifoBuffers().add(buffer);
            this.specialBufferStackMap.put(sc.getActor().getDataOutputPorts().get(i), buffer);
            operatorBlock.getDefinitions().add(buffer);
            buffers.add(buffer);
            final int size = sizeof(buffer.getType());
            stackSizeDecounter = stackSizeDecounter - nbTokenIn * size;
          }
        }
      } else if (sc.getActor() instanceof JoinActor) {

        final FunctionCall fct = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
        final Long nbTokenIn = sc.getActor().getDataOutputPorts().get(0).getExpression().evaluate()
            * brv.get(sc.getActor());// oo
        if (stackSizeDecounter - nbTokenIn < 0) {
          final DynamicBuffer buffer = CodegenModelUserFactory.eINSTANCE.createDynamicBuffer();
          // final String str = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0)
          // .getName();
          // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName();
          buffer.setName(sc.getActor().getDataOutputPorts().get(0).getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0).getName() + "__"
              + sc.getActor().getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName());
          buffer.setNbToken(nbTokenIn);
          buffer.setType(sc.getActor().getDataOutputPorts().get(0).getFifo().getType());
          buffer.setComment("store data port");
          buffer.setTokenTypeSizeInBit(8);
          fct.addParameter(buffer, PortDirection.OUTPUT);
          operatorBlock.getSinkFifoBuffers().add(buffer);
          this.specialBufferHeapMap.put(sc.getActor().getDataOutputPorts().get(0), buffer);
          operatorBlock.getDefinitions().add(buffer);
          operatorBlock.getDeclarations().add(buffer);
          buffers.add(buffer);
        } else {
          final Buffer buffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
          // final String str = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0)
          // .getName();
          // final String str2 = ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName();
          buffer.setName(sc.getActor().getDataOutputPorts().get(0).getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getDataInputPorts().get(0).getName() + "__"
              + sc.getActor().getName() + "__"
              + ((AbstractActor) sc.getActor().getDirectSuccessors().get(0)).getName());
          buffer.setNbToken(nbTokenIn);
          buffer.setType(sc.getActor().getDataOutputPorts().get(0).getFifo().getType());
          buffer.setComment("store data port");
          buffer.setTokenTypeSizeInBit(8);
          fct.addParameter(buffer, PortDirection.OUTPUT);
          operatorBlock.getSinkFifoBuffers().add(buffer);
          this.specialBufferStackMap.put(sc.getActor().getDataOutputPorts().get(0), buffer);
          operatorBlock.getDefinitions().add(buffer);
          buffers.add(buffer);
          final int size = sizeof(buffer.getType());
          stackSizeDecounter = stackSizeDecounter - nbTokenIn * size;
        }

      }

    }
    this.stackSize = stackSizeDecounter;

    // 3. Iterate over actors in SCHEDULING Order !
    // final LoopBlock loop = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
    // int indexBuffer = 0;
    Boolean precLoop = false;
    // final FiniteLoopClusterRaiserBlock lastlevel = null;
    AbstractActor iterActor = null;
    for (final ClusterRaiserSchedule sc : cs) {

      // boolean SuccessorIsInterface = false;
      // boolean PredecessorIsInterface = false;
      // if (!sc.getActor().getDirectSuccessors().isEmpty()) {
      // if (sc.getActor().getDirectSuccessors().get(0) instanceof DataOutputInterface) {
      // SuccessorIsInterface = true;
      // }
      // }
      // if (!sc.getActor().getDirectPredecessors().isEmpty()) {
      // if (sc.getActor().getDirectPredecessors().get(0) instanceof DataInputInterface) {
      // PredecessorIsInterface = true;
      // }
      // }
      // 3.0 link buffer to actor

      final Map<Port, Variable> portToVariable = new LinkedHashMap<>();

      for (final DataPort element : sc.getActor().getAllDataPorts()) {

        if (element instanceof DataInputPort) {
          final Port p = element.getFifo().getSourcePort();
          if (this.internalBufferHeapMap.containsKey(p)) {
            portToVariable.put(element, this.internalBufferHeapMap.get(p));
          }
          if (this.specialBufferHeapMap.containsKey(p)) {
            portToVariable.put(element, this.specialBufferHeapMap.get(p));
          }
          if (this.internalBufferStackMap.containsKey(p)) {
            portToVariable.put(element, this.internalBufferStackMap.get(p));
          }
          if (this.specialBufferStackMap.containsKey(p)) {
            portToVariable.put(element, this.specialBufferStackMap.get(p));
          }
          if (element.getFifo().getTarget() instanceof DataOutputInterface) {
            portToVariable.put(element, this.externalBufferMap.get(p));
          }
        }

        if (this.internalBufferHeapMap.containsKey(element)) {
          portToVariable.put(element, this.internalBufferHeapMap.get(element));
        }
        if (this.externalBufferMap.containsKey(element)) {
          portToVariable.put(element, this.externalBufferMap.get(element));
        }
        if (this.specialBufferHeapMap.containsKey(element)) {
          portToVariable.put(element, this.specialBufferHeapMap.get(element));
        }
        if (this.internalBufferStackMap.containsKey(element)) {
          portToVariable.put(element, this.internalBufferStackMap.get(element));
        }
        // if (this.externalBufferMap.containsKey(sc.actor.getAllDataPorts().get(indexPort))) {
        // portToVariable.put(sc.actor.getAllDataPorts().get(indexPort),
        // this.externalBufferMap.get(sc.actor.getAllDataPorts().get(indexPort)));
        // }
        if (this.specialBufferStackMap.containsKey(element)) {
          portToVariable.put(element, this.specialBufferStackMap.get(element));
        }
        if (element.getFifo().getTarget() instanceof DataOutputInterface) {
          portToVariable.put(element, this.externalBufferMap.get(element));
        }

      }

      // link config input port
      for (final ConfigInputPort element : sc.getActor().getConfigInputPorts()) {
        final IntVar var = CodegenModelUserFactory.eINSTANCE.createIntVar();
        var.setType("int");
        if (element.getIncomingDependency().getSetter() instanceof Parameter) {
          var.setComment(
              String.valueOf(((Parameter) element.getIncomingDependency().getSetter()).getExpression().evaluate()));
        }
        var.setName(((Parameter) element.getIncomingDependency().getSetter()).getName());
        portToVariable.put(element, var);
        this.parameterMap.put(element, var);

        // if (!(sc.getActor().getConfigInputPorts().get(indexConfigInputPort).getIncomingDependency()
        // .getSetter() instanceof ConfigInputInterface)) {
        // var.setComment(String.valueOf(((Parameter) sc.getActor().getConfigInputPorts().get(indexConfigInputPort)
        // .getIncomingDependency().getSetter()).getExpression().evaluate()));
        //
        // var.setName(sc.getActor().getConfigInputPorts().get(indexConfigInputPort).getName());
        // portToVariable.put(
        // sc.getActor().getConfigInputPorts().get(indexConfigInputPort).getIncomingDependency().getGetter(), var);
        // this.parameterMap.put(
        // sc.getActor().getConfigInputPorts().get(indexConfigInputPort).getIncomingDependency().getGetter(), var);
        // } else {
        // var.setComment(String.valueOf(((ConfigInputInterface) sc.getActor().getConfigInputPorts()
        // .get(indexConfigInputPort).getIncomingDependency().getSetter()).getExpression().evaluate()));
        // var.setName(sc.getActor().getConfigInputPorts().get(indexConfigInputPort).getName());
        // portToVariable.put(((ConfigInputInterface) sc.getActor().getConfigInputPorts().get(indexConfigInputPort)
        // .getIncomingDependency().getSetter()).getGraphPort(), var);
        // this.parameterMap.put(((ConfigInputInterface) sc.getActor().getConfigInputPorts().get(indexConfigInputPort)
        // .getIncomingDependency().getSetter()).getGraphPort(), var);
        // }
      }
      // delay manage
      // for (int i = 0; i < sc.getActor().getAllDataPorts().size(); i++)
      // if (sc.getActor().getAllDataPorts().get(i).getFifo().isHasADelay())
      // generateInitEndFifoCall(operatorBlock, sc.getActor().getAllDataPorts().get(i), sc.getActor());

      // 3.1 link actor firing

      if (sc.getActor() instanceof Actor) {
        // init
        // get init actor
        if (((Actor) sc.getActor()).getRefinement() != null) {
          if (((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getInitPrototype() != null) {
            final ActorFunctionCall init = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall(
                (Actor) sc.getActor(), ((CHeaderRefinement) ((Actor) sc.getActor()).getRefinement()).getInitPrototype(),
                portToVariable);

            if (operatorBlock.getInitBlock() == null) {
              final Block block = CodegenModelUserFactory.eINSTANCE.createBlock();
              block.setName(init.getName());
              block.getCodeElts().add(init);
              operatorBlock.setInitBlock(block);
              /// initBlock.setBodyBlock(block);
            } else {
              operatorBlock.getInitBlock().getCodeElts().add(init);
              // initBlock.getBodyBlock().getCodeElts().add(init);
            }

            // initBlock.getBodyBlock().getCodeElts().add(init);
          }
        }

        if (!sc.isBeginLoop() && !sc.isEndLoop() && !sc.isLoopPrec()) {
          generateActorFiring((Actor) sc.getActor(), portToVariable, operatorBlock);

        } else if (sc.isBeginLoop() && !precLoop) {
          iterActor = sc.getActor();
          generateFiniteLoopClusterRaiserBlock(sc.getIterator(), sc.getActor(), portToVariable, operatorBlock,
              iterActor);

          if (sc.isEndLoop()) {
            precLoop = false;
          } else {
            precLoop = true;
          }

        } else if (!sc.isBeginLoop() && precLoop) {
          retrieveLastLevel(operatorBlock);
          generateActorFiringInFiniteLoopClusterRaiserBlock(sc.getIterator(), (Actor) sc.getActor(), portToVariable,
              this.lastLevel, iterActor);
        } else {
          retrieveLastLevel(operatorBlock);
          addEltFiniteLoopClusterRaiserBlock(sc.getIterator(), sc.getActor(), portToVariable, this.lastLevel,
              iterActor);

        }

      } else if (sc.getActor() instanceof SpecialActor) {
        if (!sc.isBeginLoop() && !sc.isEndLoop() && !sc.isLoopPrec()) {
          generateSpecialActorFiring(sc.getIterator(), (SpecialActor) sc.getActor(), portToVariable, operatorBlock);

        } else if (sc.isBeginLoop() && !precLoop) {
          iterActor = sc.getActor();
          generateSpecialFiniteLoopClusterRaiserBlock(sc.getIterator(), (SpecialActor) sc.getActor(), portToVariable,
              operatorBlock, iterActor);

          if (sc.isEndLoop()) {
            precLoop = false;
          } else {
            precLoop = true;
          }

        } else if (!sc.isBeginLoop() && precLoop) {
          retrieveLastLevel(operatorBlock);
          generateSpecialActorFiringInFiniteLoopClusterRaiserBlock(sc.getIterator(), (SpecialActor) sc.getActor(),
              portToVariable, this.lastLevel, iterActor);

        }

      } else {
        throw new PreesmRuntimeException("Unsupported actor [" + sc.getActor() + "]");
      }

    }
    // add memcpy elt
    if (!loopedBuffer.isEmpty()) {
      for (int i = 0; i < loopedBuffer.size(); i++) {

        final FunctionCall memcpy = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
        memcpy.setActorName("memcpy_" + i);
        memcpy.setName("memcpy");
        final Buffer bufferIn = CodegenModelUserFactory.eINSTANCE.createBuffer();
        bufferIn.setName(loopedBuffer.get(i)[0]);
        bufferIn.setComment("In");
        memcpy.addParameter(bufferIn, PortDirection.NONE);

        final Buffer bufferOut = CodegenModelUserFactory.eINSTANCE.createBuffer();
        bufferOut.setName(loopedBuffer.get(i)[1]);
        bufferOut.setComment("Out");
        memcpy.addParameter(bufferOut, PortDirection.NONE);

        final Buffer bufferSize = CodegenModelUserFactory.eINSTANCE.createBuffer();
        bufferSize.setName(loopedBuffer.get(i)[2]);
        bufferSize.setComment("size");
        memcpy.addParameter(bufferSize, PortDirection.NONE);

        if (!(operatorBlock.getBodyBlock().getCodeElts().get(0) instanceof Block)) {
          operatorBlock.getBodyBlock().getCodeElts().add(memcpy);
        } else {
          ((Block) operatorBlock.getBodyBlock().getCodeElts().get(0)).getCodeElts().add(memcpy);
          //
        }
      }
    }

    // 4. return func arg
    final FunctionCall fct2 = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
    fct2.setActorName(this.scenario.getAlgorithm().getName());
    fct2.setName(fileName);
    if (!this.scenario.getAlgorithm().getConfigInputInterfaces().isEmpty()) {
      for (final ConfigInputInterface element : this.scenario.getAlgorithm().getConfigInputInterfaces()) {
        final Variable argCfg = CodegenModelUserFactory.eINSTANCE.createConstant();
        argCfg.setName(element.getName());
        argCfg.setType("int");

        argCfg.setComment("cfg"); // name
        fct2.addParameter(argCfg, PortDirection.NONE);
        // operatorBlock.getClusterFunc().addParameter(argParam, PortDirection.INPUT);
      }
    }
    if (!this.scenario.getAlgorithm().getOnlyParameters().isEmpty()) {
      for (final Parameter element : this.scenario.getAlgorithm().getParameters()) {
        final Variable argParam = CodegenModelUserFactory.eINSTANCE.createConstant();
        argParam.setName(element.getName());// retrieve cluster parameters
        argParam.setType("int");
        if (!isLastCluster) {
          argParam.setComment(String.valueOf(element.getExpression().evaluate())); // name
        } else {
          argParam.setComment("cfg"); // name
        }

        fct2.addParameter(argParam, PortDirection.NONE);
        // operatorBlock.getClusterFunc().addParameter(argParam, PortDirection.INPUT);
      }
    }
    if (!this.scenario.getAlgorithm().getDataInputInterfaces().isEmpty()) {
      for (final DataInputInterface element : this.scenario.getAlgorithm().getDataInputInterfaces()) {
        final Variable argIn = CodegenModelUserFactory.eINSTANCE.createConstant();
        argIn.setName("*" + element.getName());
        argIn.setType(element.getDataOutputPorts().get(0).getFifo().getType());
        argIn.setComment("input"); // name
        fct2.addParameter(argIn, PortDirection.INPUT);
        // operatorBlock.getClusterFunc().addParameter(argIn, PortDirection.INPUT);
      }
    }
    if (!this.scenario.getAlgorithm().getDataOutputInterfaces().isEmpty()) {
      for (final DataOutputInterface element : this.scenario.getAlgorithm().getDataOutputInterfaces()) {
        final Variable argOut = CodegenModelUserFactory.eINSTANCE.createConstant();
        argOut.setName("*" + element.getName());
        argOut.setType(element.getDataInputPorts().get(0).getFifo().getType());
        // argOut.setType("unsigned char");
        argOut.setComment("output"); // name
        fct2.addParameter(argOut, PortDirection.OUTPUT);
      }
    }
    operatorBlock.setClusterFunc(fct2);

    final List<Block> resultList = this.clusterBlocks.entrySet().stream()
        .sorted((e1, e2) -> e1.getKey().getHardwareId() - e2.getKey().getHardwareId()).map(Entry::getValue)
        .collect(Collectors.toList());
    // if (initBlock.getBodyBlock() != null || !initBlock.getSinkFifoBuffers().isEmpty()) {
    // FunctionCall fctInit = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
    // fctInit.setName("Cluster_Init");
    // resultList.addAll(this.clusterInitBlock.entrySet().stream()
    // .sorted((e1, e2) -> e1.getKey().getHardwareId() - e2.getKey().getHardwareId()).map(Entry::getValue)
    // .collect(Collectors.toList()));
    // }

    return Collections.unmodifiableList(resultList);
  }

  private void generateSpecialActorFiringInFiniteLoopClusterRaiserBlock(int iterator, SpecialActor actor,
      Map<Port, Variable> portToVariable, FiniteLoopClusterRaiserBlock lastlevel, AbstractActor iterActor) {
    // TODO Auto-generated method stub
    // Instantiate special call object
    final SpecialCall specialCall = CodegenModelUserFactory.eINSTANCE.createSpecialCall();
    specialCall.setName(actor.getName());

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
      associatedBuffer = retrieveAssociatedBuffer(actor, dp);
      // associatedBuffer = generateIteratedBuffer(associatedBuffer, actor, dp);

      if (dp instanceof DataInputPort) {
        specialCall.addInputBuffer(associatedBuffer);
      } else {
        specialCall.addOutputBuffer(associatedBuffer);
      }
    }

    for (final ConfigInputPort cp : actor.getConfigInputPorts()) {
      IntVar associatedVar = null;
      associatedVar = retrieveAssociatedVar(actor, cp);

      specialCall.addParameter(associatedVar, PortDirection.NONE);
    }

    for (int i = 0; i < specialCall.getParameters().size(); i++) {
      if (!(specialCall.getParameters().get(i) instanceof IteratedBuffer)) {
        specialCall.getParameters().remove(i);
        i--;
      }
    }

    lastlevel.getCodeElts().add(specialCall);
    registerCallVariableToFiniteLoopClusterRaiserBlock(lastlevel, specialCall);
  }

  // private void generateSpecialActorFiring(int iterator, SpecialActor actor, Map<Port, Variable> portToVariable,
  // ClusterRaiserBlock operatorBlock) {
  // // TODO Auto-generated method stub
  //
  // }

  private void generateActorFiringInFiniteLoopClusterRaiserBlock(int iterator, Actor actor,
      Map<Port, Variable> portToVariable, FiniteLoopClusterRaiserBlock lastlevel, AbstractActor iterActor) {
    // TODO Auto-generated method stub
    final Refinement refinement = actor.getRefinement();
    if (refinement instanceof CHeaderRefinement) {

      final FunctionPrototype loopPrototype = ((CHeaderRefinement) refinement).getLoopPrototype();
      final ActorFunctionCall loop = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall(actor, loopPrototype,
          portToVariable);

      loop.setActorName(actor.getName());
      loop.setName(actor.getName());
      loop.setName(loopPrototype.getName());
      loop.setOriActor(actor);

      // Retrieve function argument
      final List<FunctionArgument> arguments = loopPrototype.getArguments();
      // Associate argument with buffer
      for (final FunctionArgument a : arguments) {
        // Search for the corresponding port into actor ports list
        final Port associatedPort = actor.lookupPort(a.getName());
        // Add argument into function call
        if (associatedPort instanceof DataPort) {
          addDataPortArgument(loop, actor, (DataPort) associatedPort, a, lastlevel, iterActor);
        } else if (associatedPort instanceof ConfigInputPort) {
          addConfigInputPortArgument(loop, (ConfigInputPort) associatedPort, a);
        }
      }

      for (int i = 0; i < loop.getParameters().size(); i++) {
        if (!(loop.getParameters().get(i) instanceof IteratedBuffer)) {
          loop.getParameters().remove(i);
          i--;
        }
      }

      lastlevel.getCodeElts().add(loop);
      registerCallVariableToFiniteLoopClusterRaiserBlock(lastlevel, loop);
    }

  }

  private int sizeof(String type) {
    // TODO Auto-generated method stub
    if (type.equals("byte") || type.equals("boolean")) {
      return 1;
    }
    if (type.equals("short") || type.equals("char") || type.equals("uchar")) {
      return 1;
    }
    if (type.equals("int") || type.equals("float")) {
      return 4;
    }
    if (type.equals("Long") || type.equals("double")) {
      return 8;
    }
    return 4;
  }

  private void generateSpecialActorFiring(int brv, SpecialActor actor, Map<Port, Variable> portToVariable,
      ClusterRaiserBlock clusterRaiserBlock) {

    // Instantiate special call object
    final SpecialCall specialCall = CodegenModelUserFactory.eINSTANCE.createSpecialCall();
    specialCall.setName(actor.getName());

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
      associatedBuffer = retrieveAssociatedBuffer(actor, dp);
      // associatedBuffer = generateIteratedBuffer(associatedBuffer, actor, dp);

      if (dp instanceof DataInputPort) {
        specialCall.addInputBuffer(associatedBuffer);
      } else {
        specialCall.addOutputBuffer(associatedBuffer);
      }
    }

    for (final ConfigInputPort cp : actor.getConfigInputPorts()) {
      IntVar associatedVar = null;
      associatedVar = retrieveAssociatedVar(actor, cp);

      specialCall.addParameter(associatedVar, PortDirection.NONE);
    }

    if (clusterRaiserBlock.getBodyBlock() == null) {
      final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
      loopBlock.getCodeElts().add(specialCall);
      clusterRaiserBlock.setBodyBlock(loopBlock);
    } else {
      clusterRaiserBlock.getBodyBlock().getCodeElts().add(specialCall);
    }
    registerCallVariableToclusterRaiserBlock(clusterRaiserBlock, specialCall);

  }

  private IntVar retrieveAssociatedVar(SpecialActor actor, ConfigInputPort cp) {
    if (this.parameterMap.containsKey(cp)) { // com A to A
      // buffer
      return this.parameterMap.get(cp);
    }
    return null;
  }

  private void generateSpecialFiniteLoopClusterRaiserBlock(int brv, SpecialActor actor,
      Map<Port, Variable> portToVariable, ClusterRaiserBlock clusterRaiserBlock, AbstractActor iterActor) {
    final FiniteLoopClusterRaiserBlock flcrb = CodegenModelUserFactory.eINSTANCE.createFiniteLoopClusterRaiserBlock();
    final IntVar iterator = CodegenModelUserFactory.eINSTANCE.createIntVar();
    iterator.setName("index_" + actor.getName());
    this.iterMap.put(actor, iterator);
    flcrb.setIter(iterator);
    flcrb.setNbIter(brv);

    // Instantiate special call object
    final SpecialCall specialCall = CodegenModelUserFactory.eINSTANCE.createSpecialCall();
    specialCall.setName(actor.getName());

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
      associatedBuffer = retrieveAssociatedBuffer(actor, dp);

      // final Buffer iteratedBuffer = null;
      // generateSpecialIteratedBuffer(associatedBuffer, actor, dp, iterActor);

      if (dp instanceof DataInputPort) {
        if (specialCall.getType().equals(SpecialType.BROADCAST)) {
          specialCall.addInputBuffer(associatedBuffer);
        }
        if (specialCall.getType().equals(SpecialType.ROUND_BUFFER)) {

          addSpecialDataPortArgument(specialCall, actor, dp, flcrb, iterActor, brv);
        }

        // iteratedBuffer = generateSpecialIteratedBuffer(associatedBuffer, actor, dp, iterActor);
        // specialCall.addInputBuffer(iteratedBuffer);
      } else if (specialCall.getType().equals(SpecialType.BROADCAST)
          || specialCall.getType().equals(SpecialType.ROUND_BUFFER)) {
        addSpecialDataPortArgument(specialCall, actor, dp, flcrb, iterActor, brv);
        // iteratedBuffer = generateSpecialIteratedBuffer(associatedBuffer, actor, dp, iterActor);
        // specialCall.addOutputBuffer(iteratedBuffer);
      }
    }

    for (final ConfigInputPort cp : actor.getConfigInputPorts()) {
      IntVar associatedVar = null;
      associatedVar = retrieveAssociatedVar(actor, cp);

      specialCall.addParameter(associatedVar, PortDirection.NONE);
    }

    // for (int i = 0; i < specialCall.getParameters().size(); i++) {
    // if (!(specialCall.getParameters().get(i) instanceof IteratedBuffer)) {
    // specialCall.getParameters().remove(i);
    // i--;
    // }
    // }
    // return specialCall;
    flcrb.getCodeElts().add(specialCall);

    if (clusterRaiserBlock.getBodyBlock() == null) {
      final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
      loopBlock.getCodeElts().add(flcrb);
      clusterRaiserBlock.setBodyBlock(loopBlock);
    } else {
      clusterRaiserBlock.getBodyBlock().getCodeElts().add(flcrb);
    }

  }

  private void addEltFiniteLoopClusterRaiserBlock(int brv, AbstractActor actor, Map<Port, Variable> portToVariable,
      FiniteLoopClusterRaiserBlock finiteLoopClusterRaiserBlock, AbstractActor iterActor) {
    final FiniteLoopClusterRaiserBlock flcrb = CodegenModelUserFactory.eINSTANCE.createFiniteLoopClusterRaiserBlock();
    final IntVar iterator = CodegenModelUserFactory.eINSTANCE.createIntVar();
    iterator.setName("index_" + actor.getName());
    this.iterMap.put(actor, iterator);
    flcrb.setIter(iterator);
    flcrb.setNbIter(brv);

    final Refinement refinement = ((Actor) actor).getRefinement();
    if (refinement instanceof CHeaderRefinement) {

      final FunctionPrototype loopPrototype = ((CHeaderRefinement) refinement).getLoopPrototype();
      final ActorFunctionCall loop = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall();

      loop.setActorName(actor.getName());
      loop.setName(actor.getName());
      loop.setName(loopPrototype.getName());
      loop.setOriActor(actor);

      // Retrieve function argument
      final List<FunctionArgument> arguments = loopPrototype.getArguments();
      // Associate argument with buffer
      for (final FunctionArgument a : arguments) {
        // Search for the corresponding port into actor ports list
        final Port associatedPort = actor.lookupPort(a.getName());
        // Add argument into function call
        if (associatedPort instanceof DataPort) {
          addDataPortArgument(loop, actor, (DataPort) associatedPort, a, flcrb, iterActor);
        } else if (associatedPort instanceof ConfigInputPort) {
          addConfigInputPortArgument(loop, (ConfigInputPort) associatedPort, a);
        }
      }

      flcrb.getCodeElts().add(loop);
      registerCallVariableToFiniteLoopClusterRaiserBlock(flcrb, loop);

    }

    // clusterRaiserBlock.getBodyBlock().getCodeElts().add(flcrb);
    finiteLoopClusterRaiserBlock.getCodeElts().add(flcrb);

  }

  private void generateFiniteLoopClusterRaiserBlock(int brv, AbstractActor actor, Map<Port, Variable> portToVariable,
      ClusterRaiserBlock clusterRaiserBlock, AbstractActor iterActor) {
    final FiniteLoopClusterRaiserBlock flcrb = CodegenModelUserFactory.eINSTANCE.createFiniteLoopClusterRaiserBlock();
    final IntVar iterator = CodegenModelUserFactory.eINSTANCE.createIntVar();
    iterator.setName("index_" + actor.getName());
    this.iterMap.put(actor, iterator);
    flcrb.setIter(iterator);
    flcrb.setNbIter(brv);

    final Refinement refinement = ((Actor) actor).getRefinement();
    if (refinement instanceof CHeaderRefinement) {

      final FunctionPrototype loopPrototype = ((CHeaderRefinement) refinement).getLoopPrototype();
      final ActorFunctionCall loop = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall();

      loop.setActorName(actor.getName());
      loop.setName(actor.getName());
      loop.setName(loopPrototype.getName());
      loop.setOriActor(actor);

      // Retrieve function argument
      final List<FunctionArgument> arguments = loopPrototype.getArguments();
      // Associate argument with buffer
      for (final FunctionArgument a : arguments) {
        // Search for the corresponding port into actor ports list
        final Port associatedPort = actor.lookupPort(a.getName());
        // Add argument into function call
        if (associatedPort instanceof DataPort) {
          addDataPortArgument(loop, actor, (DataPort) associatedPort, a, flcrb, iterActor);
        } else if (associatedPort instanceof ConfigInputPort) {
          addConfigInputPortArgument(loop, (ConfigInputPort) associatedPort, a);
        }
      }

      flcrb.getCodeElts().add(loop);
      registerCallVariableToFiniteLoopClusterRaiserBlock(flcrb, loop);

    }

    if (clusterRaiserBlock.getBodyBlock() == null) {
      final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
      loopBlock.getCodeElts().add(flcrb);
      clusterRaiserBlock.setBodyBlock(loopBlock);
    } else {
      clusterRaiserBlock.getBodyBlock().getCodeElts().add(flcrb);
    }

  }

  private final void retrieveLastLevel(ClusterRaiserBlock clusterRaiserBlock) {
    int lastIndex = clusterRaiserBlock.getBodyBlock().getCodeElts().size() - 1;
    if (clusterRaiserBlock.getBodyBlock().getCodeElts().get(lastIndex) instanceof FiniteLoopClusterRaiserBlock
        && this.lastLevel != (FiniteLoopClusterRaiserBlock) clusterRaiserBlock.getBodyBlock().getCodeElts()
            .get(lastIndex)) {
      this.lastLevel = (FiniteLoopClusterRaiserBlock) clusterRaiserBlock.getBodyBlock().getCodeElts().get(lastIndex);
      lastIndex = this.lastLevel.getCodeElts().size() - 1;
      while (this.lastLevel.getCodeElts().get(lastIndex) instanceof FiniteLoopClusterRaiserBlock
          && this.lastLevel != (FiniteLoopClusterRaiserBlock) clusterRaiserBlock.getBodyBlock().getCodeElts()
              .get(lastIndex)) {
        this.lastLevel = (FiniteLoopClusterRaiserBlock) clusterRaiserBlock.getBodyBlock().getCodeElts().get(lastIndex);
        lastIndex = this.lastLevel.getCodeElts().size() - 1;
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

  private final void addDataPortArgument(final FunctionCall functionCall, final AbstractActor actor,
      final DataPort port, final FunctionArgument arg, final FiniteLoopClusterRaiserBlock flcrb,
      AbstractActor iterActor) {
    // Retrieve associated Fifo
    // final Fifo associatedFifo = port.getFifo();

    // Retrieve associated Buffer
    Buffer associatedBuffer = retrieveAssociatedBuffer(actor, port);

    // If there is an repetion over actor, iterate the buffer
    associatedBuffer = generateIteratedBuffer(associatedBuffer, actor, port, iterActor);

    // Add parameter to functionCall
    functionCall.addParameter(associatedBuffer,
        (arg.getDirection().equals(Direction.IN) ? PortDirection.INPUT : PortDirection.OUTPUT));

    if (flcrb != null) {
      if (arg.getDirection().equals(Direction.IN)) {
        flcrb.getInBuffers().add((IteratedBuffer) associatedBuffer);
      } else {
        flcrb.getOutBuffers().add((IteratedBuffer) associatedBuffer);
      }
    }

  }

  private final void addSpecialDataPortArgument(final SpecialCall functionCall, final AbstractActor actor,
      final DataPort port, final FiniteLoopClusterRaiserBlock flcrb, AbstractActor iterActor, int brv) {
    // Retrieve associated Fifo
    // final Fifo associatedFifo = port.getFifo();

    // Retrieve associated Buffer
    final Buffer associatedBuffer = retrieveAssociatedBuffer(actor, port);

    // If there is an repetion over actor, iterate the buffer
    final Buffer iteratedBuffer = generateSpecialIteratedBuffer(PiMMUserFactory.instance.copy(associatedBuffer), actor,
        port, iterActor);
    iteratedBuffer.setNbToken(iteratedBuffer.getNbToken() / brv);
    // Add parameter to functionCall
    functionCall.addParameter(iteratedBuffer,
        (port.getKind().getValue() == 1 ? PortDirection.INPUT : PortDirection.OUTPUT));

    if (flcrb != null) {
      if (port.getKind().getValue() == 1) {
        functionCall.addInputBuffer(iteratedBuffer);
        flcrb.getInBuffers().add((IteratedBuffer) iteratedBuffer);
      } else {
        functionCall.addOutputBuffer(iteratedBuffer);
        flcrb.getOutBuffers().add((IteratedBuffer) iteratedBuffer);

      }
    }

  }

  private Buffer generateIteratedBuffer(Buffer buffer, AbstractActor actor, DataPort dataPort,
      AbstractActor iterActor) {
    if (this.iterMap.containsKey(actor)) {
      IteratedBuffer iteratedBuffer = null;
      iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedBuffer();
      iteratedBuffer.setBuffer(buffer);
      iteratedBuffer.setIter(this.iterMap.get(actor));
      iteratedBuffer.setNbToken(dataPort.getExpression().evaluate());
      iteratedBuffer.setType(buffer.getType());
      iteratedBuffer.setTokenTypeSizeInBit(buffer.getTokenTypeSizeInBit());
      return iteratedBuffer;
    }
    // retrieve iter actor
    // AbstractActor iterActor = actor;
    // do {
    // iterActor = (Actor) iterActor.getDirectPredecessors().get(0);
    // } while (!this.iterMap.containsKey(iterActor));
    final IteratedBuffer iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedBuffer();
    iteratedBuffer.setBuffer(buffer);
    iteratedBuffer.setIter(this.iterMap.get(iterActor));
    iteratedBuffer.setNbToken(dataPort.getExpression().evaluate());
    iteratedBuffer.setType(buffer.getType());
    iteratedBuffer.setTokenTypeSizeInBit(buffer.getTokenTypeSizeInBit());

    return iteratedBuffer;
  }

  private Buffer generateSpecialIteratedBuffer(Buffer buffer, AbstractActor actor, DataPort dataPort,
      AbstractActor iterActor) {
    if (this.iterMap.containsKey(actor)) {
      IteratedBuffer iteratedBuffer = null;
      iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedBuffer();
      iteratedBuffer.setBuffer(buffer);
      iteratedBuffer.setIter(this.iterMap.get(actor));
      iteratedBuffer.setNbToken(dataPort.getExpression().evaluate());
      iteratedBuffer.setType(buffer.getType());
      iteratedBuffer.setTokenTypeSizeInBit(buffer.getTokenTypeSizeInBit());
      return iteratedBuffer;
    }
    // retrieve iter actor
    // AbstractActor iterActor = actor;
    do {
      iterActor = (SpecialActor) iterActor.getDirectPredecessors().get(0);
    } while (!this.iterMap.containsKey(iterActor));
    final IteratedBuffer iteratedBuffer = CodegenModelUserFactory.eINSTANCE.createIteratedBuffer();
    iteratedBuffer.setBuffer(buffer);
    iteratedBuffer.setIter(this.iterMap.get(iterActor));
    iteratedBuffer.setNbToken(dataPort.getExpression().evaluate());
    iteratedBuffer.setType(buffer.getType());
    iteratedBuffer.setTokenTypeSizeInBit(buffer.getTokenTypeSizeInBit());

    return iteratedBuffer;
  }

  private Buffer retrieveAssociatedBuffer(AbstractActor actor, DataPort port) {

    DataPort pa = null;

    if (port.getKind().getValue() == 1 && port.getFifo().getSource() instanceof SpecialActor) {
      pa = port.getFifo().getSourcePort();//
    }

    if (port.getKind().getValue() == 1 && this.externalBufferMap.containsKey(port)) {
      // buffer
      if (this.externalBufferMap.get(port).getComment().equals("input interface")) {
        return this.externalBufferMap.get(port);
      }

    }
    if (port.getKind().getValue() == 1 && this.internalBufferHeapMap.containsKey(port.getFifo().getSourcePort())) {
      return this.internalBufferHeapMap.get(port.getFifo().getSourcePort());
    }

    if (port.getKind().getValue() == 2 && this.externalBufferMap.containsKey(port)) { // com atoi
      if (this.externalBufferMap.get(port).getComment().equals("output interface")) {
        return this.externalBufferMap.get(port);
      }

    }
    if (port.getKind().getValue() == 2 && this.internalBufferHeapMap.containsKey(port)) { // com atoa
      return this.internalBufferHeapMap.get(port);
    }

    if (this.specialBufferHeapMap.containsKey(port)) { // com atos
      return this.specialBufferHeapMap.get(port);
    }
    if (pa != null) {
      if (this.specialBufferHeapMap.containsKey(pa)) { // com atos
        return this.specialBufferHeapMap.get(pa);
      }
    }

    if (port.getKind().getValue() == 1 && this.internalBufferStackMap.containsKey(port.getFifo().getSourcePort())) {

      return this.internalBufferStackMap.get(port.getFifo().getSourcePort());
    }

    // if (port.getKind().getValue() == 2 && this.externalBufferMap.containsKey(port)) { // com atoi
    // if (this.externalBufferMap.get(port).getComment().equals("output interface")) {
    // return this.externalBufferMap.get(port);
    // }
    // }
    if (port.getKind().getValue() == 2 && this.internalBufferStackMap.containsKey(port)) { // com atoa
      return this.internalBufferStackMap.get(port);
    }

    if (this.specialBufferStackMap.containsKey(port)) { // com atos
      return this.specialBufferStackMap.get(port);
    }
    return null;
  }

  private void registerCallVariableToFiniteLoopClusterRaiserBlock(FiniteLoopClusterRaiserBlock flcrb, Call loop) {
    // Register the core Block as a user of the function variable
    for (final Variable var : loop.getParameters()) {
      // Currently, constants do not need to be declared nor
      // have creator since their value is directly used.
      // Consequently the used block can also be declared as the creator
      if (var instanceof Constant) {
        var.reaffectCreator(flcrb);
      }
      var.getUsers().add(flcrb);
    }
  }

  protected void registerCallVariableToclusterRaiserBlock(final ClusterRaiserBlock clusterRaiserBlock,
      final Call call) {
    // Register the core Block as a user of the function variable
    for (final Variable var : call.getParameters()) {
      // Currently, constants do not need to be declared nor
      // have creator since their value is directly used.
      // Consequently the used block can also be declared as the creator
      if (var instanceof Constant) {
        var.reaffectCreator(clusterRaiserBlock);
      }
      var.getUsers().add(clusterRaiserBlock);
    }
  }

  private void generateActorFiring(final Actor actor, final Map<Port, Variable> portToVariable,
      final ClusterRaiserBlock clusterRaiserBlock) {

    final Refinement refinement = actor.getRefinement();
    if (refinement instanceof CHeaderRefinement) {

      final FunctionPrototype loopPrototype = ((CHeaderRefinement) refinement).getLoopPrototype();
      final ActorFunctionCall loop = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall(actor, loopPrototype,
          portToVariable);
      loop.setName(loopPrototype.getName());
      if (clusterRaiserBlock.getBodyBlock() == null) {
        final LoopBlock loopBlock = CodegenModelUserFactory.eINSTANCE.createLoopBlock();
        loopBlock.getCodeElts().add(loop);
        clusterRaiserBlock.setBodyBlock(loopBlock);
      } else {
        clusterRaiserBlock.getBodyBlock().getCodeElts().add(loop);
      }
      registerCallVariableToclusterRaiserBlock(clusterRaiserBlock, loop);
    }
  }

  public final PortDirection createPortDirection(final PortKind direction) {
    switch (direction) {
      case CFG_INPUT:
        return PortDirection.NONE;
      case DATA_INPUT:
        return PortDirection.INPUT;
      case DATA_OUTPUT:
        return PortDirection.OUTPUT;
      case CFG_OUTPUT:
      default:
        throw new PreesmRuntimeException();
    }
  }

  public static int compterOccurrences(String maChaine, char recherche) {
    int nb = 0;
    for (int i = 0; i < maChaine.length(); i++) {
      if (maChaine.charAt(i) == recherche) {
        nb++;
      }
    }
    return nb;
  }

}
