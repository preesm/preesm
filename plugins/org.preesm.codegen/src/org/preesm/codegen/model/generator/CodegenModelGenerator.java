/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Julien Hascoet [jhascoet@kalray.eu] (2016 - 2017)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2018)
 * Leonardo Suriano [leonardo.suriano@upm.es] (2019)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013)
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
package org.preesm.codegen.model.generator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.codegen.idl.ActorPrototypes;
import org.preesm.algorithm.codegen.idl.IDLPrototypeFactory;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.algorithm.codegen.model.CodeGenArgument;
import org.preesm.algorithm.codegen.model.CodeGenParameter;
import org.preesm.algorithm.mapper.ScheduledDAGIterator;
import org.preesm.algorithm.mapper.graphtransfo.BufferAggregate;
import org.preesm.algorithm.mapper.graphtransfo.BufferProperties;
import org.preesm.algorithm.mapper.graphtransfo.ImplementationPropertyNames;
import org.preesm.algorithm.mapper.graphtransfo.VertexType;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.algorithm.memory.script.Range;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.CodeRefinement;
import org.preesm.algorithm.model.CodeRefinement.Language;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.parameters.Argument;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFInitVertex;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.codegen.model.ActorBlock;
import org.preesm.codegen.model.ActorFunctionCall;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.CodeElt;
import org.preesm.codegen.model.CodegenPackage;
import org.preesm.codegen.model.Communication;
import org.preesm.codegen.model.CommunicationNode;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.ConstantString;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.DataTransferAction;
import org.preesm.codegen.model.Delimiter;
import org.preesm.codegen.model.Direction;
import org.preesm.codegen.model.DistributedBuffer;
import org.preesm.codegen.model.DistributedMemoryCommunication;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FifoOperation;
import org.preesm.codegen.model.FpgaLoadAction;
import org.preesm.codegen.model.FreeDataTransferBuffer;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.GlobalBufferDeclaration;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.NullBuffer;
import org.preesm.codegen.model.OutputDataTransfer;
import org.preesm.codegen.model.PapifyAction;
import org.preesm.codegen.model.PapifyFunctionCall;
import org.preesm.codegen.model.PapifyType;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.RegisterSetUpAction;
import org.preesm.codegen.model.SharedMemoryCommunication;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.clustering.CodegenClusterModelGeneratorSwitch;
import org.preesm.codegen.model.clustering.SrDAGOutsideFetcher;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.codegen.model.util.VariableSorter;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.scenario.PapiComponent;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapifyConfig;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.check.FifoTypeChecker;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamMessageRouteStep;

/**
 * The objective of this class is to generate an intermediate model that will be used to print the generated code. <br>
 * The generation of the intermediate model is based on elements resulting from a workflow execution: an {@link Design
 * architecture}, a scheduled {@link DirectedAcyclicGraph DAG}, a {@link MemoryExclusionGraph Memory Allocation} and a
 * {@link PreesmScenario scenario}. The generated model is composed of objects of the {@link CodegenPackage Codegen EMF
 * model}.
 *
 *
 * @author kdesnos
 *
 */
public class CodegenModelGenerator extends AbstractCodegenModelGenerator {

  private static final String OPERATOR_LITERAL = "Operator";

  private static final String PAPIFY_PE_ID_CONSTANT_NAME = "PE_id";

  private static final String ERROR_PATTERN_1 = "MemEx graph memory object (%s) refers to a DAG Vertex %s that does "
      + "not exist in the input DAG.\n" + "Make sure that the MemEx is derived from the input DAG of the codegen.";

  private static final String ERROR_NO_REFINEMENT = "Actor (%s) has no valid refinement (.idl, .h or .graphml)."
      + " Associate a refinement to this actor before generating code.";

  private static final String ERROR_NO_LOOP_INTERFACE = "Actor (%s) has no loop interface in its IDL refinement.";

  private static final String ERROR_NON_C_REFINEMENT = "Actor (%s) has a non standard C refinement.";

  /**
   * {@link Map} of the main {@link Buffer} for the code generation. Each {@link Buffer} in this {@link List} contains
   * one or more {@link SubBuffer} and is associated to a unique memory bank, whose name is given by the associated
   * {@link String} in the {@link Map}.
   */
  private final Map<String, Buffer> mainBuffers;

  /**
   * Map used to keep track of the number of {@link Buffer} created with a given name. Since buffer are named using
   * ports names, duplicates name may happen and number must be added to ensure correctness.
   */
  private final Map<String, Integer> bufferNames;

  /**
   * This {@link Map} associates each {@link ComponentInstance} to its corresponding {@link CoreBlock}.
   */
  protected Map<ComponentInstance, CoreBlock> coreBlocks;

  /**
   * This {@link Map} associates each {@link BufferProperties} aggregated in the {@link DAGEdge edges} of the
   * {@link DirectedAcyclicGraph DAG} to its corresponding {@link Buffer}.
   */
  private final Map<BufferProperties, Buffer> srSDFEdgeBuffers;

  /**
   * This {@link BiMap} associates each {@link DAGEdge} to its corresponding {@link Buffer}.
   */
  private final BiMap<DAGEdge, Buffer> dagEdgeBuffers;

  /**
   * This {@link Map} associates each {@link Pair} of init and end {@link DAGVertex} to their corresponding {@link Pair}
   * of {@link Buffer}, the first for the FIFO head, and the second for the FIFO body (if any).
   */
  private final Map<Pair<DAGVertex, DAGVertex>, Pair<Buffer, Buffer>> dagFifoBuffers;

  /**
   * This {@link Map} associates a {@link SDFInitVertex} to its corresponding {@link FifoOperation#POP Pop}
   * {@link FifoCall}.
   */
  private final Map<DAGVertex, FifoCall> popFifoCalls;

  /**
   * This {@link Map} associates each {@link DAGVertex} to its corresponding {@link Call}. It will be filled during when
   * creating the function call of actors and updated later by inserting {@link Communication} {@link Call calls}. For
   * {@link Communication}, only the End Receive and the Start Send communications will be stored in this map to avoid
   * having multiple calls for a unique {@link DAGVertex}.
   */
  private final BiMap<DAGVertex, Call> dagVertexCalls;

  /**
   * This {@link Map} associates a unique communication ID to a list of all the {@link Communication} {@link Call Calls}
   * in involves. The communication id is a {@link String} formed as follow:<br>
   * <code>SenderCore__SenderVertexName___ReceiverCore__ReceiverVertexName </code>
   */
  private final Map<String, List<Communication>> communications;

  private final Map<AbstractActor, Schedule> scheduleMapping;

  /**
   * This {@link List} stores the PEs that has been already configured for Papify usage.
   */
  protected final List<String> papifiedPEs;

  /**
   * This {@link List} of {@link List} stores the Papify configurations already used.
   */
  protected final List<EList<PapiEvent>> configsAdded;

  /**
   * Constructor of the {@link CodegenModelGenerator}. The constructor performs verification to ensure that the inputs
   * are valid:
   * <ul>
   * <li>The {@link DirectedAcyclicGraph DAG} is scheduled</li>
   * <li>The {@link DirectedAcyclicGraph DAG} is mapped on the input {@link Design architecture}</li>
   * <li>The {@link MemoryExclusionGraph MemEx} is derived from the {@link DirectedAcyclicGraph DAG}</li>
   * <li>The {@link MemoryExclusionGraph MemEx} is allocated</li>
   * </ul>
   *
   * @param archi
   *          See {@link AbstractCodegenPrinter#archi}
   * @param algo
   *          See {@link AbstractCodegenPrinter#dag}
   * @param megs
   *          See {@link AbstractCodegenPrinter#megs}
   * @param scenario
   *          See {@link AbstractCodegenPrinter#scenario}
   */
  public CodegenModelGenerator(final Design archi, final MapperDAG algo, final Map<String, MemoryExclusionGraph> megs,
      final Scenario scenario, final Map<AbstractActor, Schedule> scheduleMapping) {
    super(archi, algo, megs, scenario);

    checkInputs(this.archi, this.algo, this.megs);
    this.bufferNames = new LinkedHashMap<>();
    this.mainBuffers = new LinkedHashMap<>();
    this.coreBlocks = new LinkedHashMap<>();
    this.srSDFEdgeBuffers = new LinkedHashMap<>();
    this.dagEdgeBuffers = HashBiMap.create(algo.edgeSet().size());
    this.dagFifoBuffers = new LinkedHashMap<>();
    this.dagVertexCalls = HashBiMap.create(algo.vertexSet().size());
    this.communications = new LinkedHashMap<>();
    this.popFifoCalls = new LinkedHashMap<>();
    this.papifiedPEs = new ArrayList<>();
    this.configsAdded = new ArrayList<>();
    this.papifyActive = false;
    this.scheduleMapping = scheduleMapping;
    this.multinode = false;
  }

  /**
   * Verification to ensure that the inputs are valid:
   * <ul>
   * <li>The {@link DirectedAcyclicGraph DAG} is scheduled</li>
   * <li>The {@link DirectedAcyclicGraph DAG} is mapped on the input {@link Design architecture}</li>
   * <li>The {@link MemoryExclusionGraph MEGs} are derived from the {@link DirectedAcyclicGraph DAG}</li>
   * <li>The {@link MemoryExclusionGraph MEGs} are allocated</li>
   * </ul>
   * .
   *
   * @param archi
   *          See {@link AbstractCodegenPrinter#archi}
   * @param dag
   *          See {@link AbstractCodegenPrinter#dag}
   * @param megs
   *          See {@link AbstractCodegenPrinter#megs}
   */
  protected void checkInputs(final Design archi, final DirectedAcyclicGraph dag,
      final Map<String, MemoryExclusionGraph> megs) {
    // Check that the input DAG is scheduled and Mapped on the targeted architecture
    for (final DAGVertex vertex : dag.vertexSet()) {
      final ComponentInstance operator = vertex.getPropertyBean().getValue(OPERATOR_LITERAL);
      if (operator == null) {
        final String msg = "The DAG Actor " + vertex + " is not mapped on any operator.\n"
            + " All actors must be mapped before using the code generation.";
        throw new PreesmRuntimeException(msg);
      }

      if (!archi.getComponentInstances().contains(operator)) {
        final String msg = "The DAG Actor " + vertex + " is not mapped on an operator " + operator
            + " that does not belong to the ipnut architecture.";
        throw new PreesmRuntimeException(msg);
      }
    }

    for (final MemoryExclusionGraph meg : megs.values()) {
      for (final MemoryExclusionVertex memObj : meg.vertexSet()) {
        // Check that the MemEx is derived from the Input DAG
        String sourceName = memObj.getSource();
        final String sinkName = memObj.getSink();

        // If the MObject is a part of a divide buffer
        sourceName = sourceName.replaceFirst("^part\\d+_", "");

        final boolean isFifo = sourceName.startsWith("FIFO");
        if (isFifo) {
          sourceName = sourceName.substring(10);
        }

        final DAGVertex sourceVertex = dag.getVertex(sourceName);
        final DAGVertex sinkVertex = dag.getVertex(sinkName);

        // Check that vertices exist
        final boolean sourceVertexIsNull = sourceVertex == null;
        final boolean sinkVertexIsNull = sinkVertex == null;
        if (sourceVertexIsNull) {
          throw new PreesmRuntimeException(
              String.format(CodegenModelGenerator.ERROR_PATTERN_1, memObj.toString(), sourceName));
        }
        if (sinkVertexIsNull) {
          throw new PreesmRuntimeException(
              String.format(CodegenModelGenerator.ERROR_PATTERN_1, memObj.toString(), sinkName));
        }

        // Check that the edge is part of the memeExGraph
        final boolean sinkAndSourceNamesDiffer = !sinkName.equals(sourceName);
        final boolean memObjectIsAnEdge = sinkAndSourceNamesDiffer && !isFifo;
        final boolean memExGraphContainsEdge = dag.containsEdge(sourceVertex, sinkVertex);
        if (memObjectIsAnEdge && !memExGraphContainsEdge) {
          throw new PreesmRuntimeException("MemEx graph memory object (" + memObj + ") refers to a DAG Edge"
              + " that does not exist in the input DAG.\n"
              + "Make sure that the MemEx is derived from the input DAG of the codegen.");
        }

        // Check that the MemEx graph is allocated.
        final Long offset = memObj.getPropertyBean().getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);
        if (offset == null) {
          throw new PreesmRuntimeException("MemEx graph memory object (" + memObj + ") was not allocated in memory. \n"
              + "Make sure that the MemEx is processed by an allocation task before entering the codegen.");
        }
      }
    }
  }

  /**
   * Finds the {@link MemoryExclusionVertex} associated to the given {@link DAGEdge} in the {@link #megs}.
   *
   * @param dagEdge
   *          {@link DAGEdge} whose associated {@link MemoryExclusionVertex} is to be found.
   * @return the found {@link MemoryExclusionVertex}
   */
  protected MemoryExclusionVertex findMObject(final DAGEdge dagEdge) {
    MemoryExclusionVertex mObject = null;
    // Find the associated memory object
    for (final MemoryExclusionGraph meg : this.megs.values()) {
      mObject = meg.getVertex(new MemoryExclusionVertex(dagEdge, this.scenario));
      if (mObject != null) {
        break;
      }
    }
    if (mObject == null) {
      throw new PreesmRuntimeException(
          "Memory Object associated to DAGEdge " + dagEdge + " could not be found in any memory exclusion graph.");
    }

    return mObject;
  }

  /**
   * Method to generate the intermediate model of the codegen based on the {@link Design architecture}, the
   * {@link MemoryExclusionGraph MemEx graph} , the {@link DirectedAcyclicGraph DAG} and the {@link PreesmScenario
   * scenario}.
   *
   * @return a set of {@link Block blocks}. Each of these block corresponds to a part of the code to generate:
   *         <ul>
   *         <li>{@link CoreBlock A block corresponding to the code executed by a core}</li>
   *         <li>{@link ActorBlock A block corresponding to the code of an non-flattened hierarchical actor}</li>
   *         </ul>
   */
  public List<Block> generate() {
    // -1- Add all hosted MemoryObject back in te MemEx
    // 0 - Create the Buffers of the MemEx

    // 1 - Iterate on the actors of the DAG
    // 1.0 - Identify the core used.
    // 1.1 - Construct the "loop" & "init" of each core.
    // 2 - Set CoreBlock ID
    // 3 - Put the buffer declaration in their right place

    // -1 - Add all hosted MemoryObject back in te MemEx
    restoreHostedVertices();

    // 0 - Create the Buffers of the MemEx
    generateBuffers();

    // init coreBlocks
    for (final ComponentInstance cmp : this.archi.getOperatorComponentInstances()) {
      this.coreBlocks.computeIfAbsent(cmp, key -> {
        final CoreBlock operatorBlock = CodegenModelUserFactory.eINSTANCE.createCoreBlock(key);
        operatorBlock.setMultinode(multinode);
        return operatorBlock;
      });
    }

    // 1 - iterate over dag vertices in SCHEDULING Order !
    final ScheduledDAGIterator scheduledDAGIterator = new ScheduledDAGIterator(algo);
    scheduledDAGIterator.forEachRemaining(vert -> {

      // 1.0 - Identify the core used.
      // This call can not fail as checks were already performed in the constructor
      final ComponentInstance operator = vert.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_Operator);
      // If this is the first time this operator is encountered, create a Block and store it.
      if (!this.coreBlocks.containsKey(operator)) {
        throw new PreesmRuntimeException();
      }
      final CoreBlock operatorBlock = this.coreBlocks.get(operator);
      // 1.1 - Construct the "loop" of each core.
      final String vertexType = vert.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .toString();
      final DAGEdge dagEdge = vert.getPropertyBean()
          .getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge);
      final Buffer buffer = this.dagEdgeBuffers.get(dagEdge);

      switch (vertexType) {
        case VertexType.TYPE_TASK:
          // May be an actor (Hierarchical or not) call or a Fork Join call
          final String vertKind = vert.getPropertyBean().getValue(AbstractVertex.KIND_LITERAL).toString();
          switch (vertKind) {
            case DAGVertex.DAG_VERTEX -> generateActorFiring(operatorBlock, vert);
            case MapperDAGVertex.DAG_FORK_VERTEX, MapperDAGVertex.DAG_JOIN_VERTEX,
                MapperDAGVertex.DAG_BROADCAST_VERTEX ->
              generateSpecialCall(operatorBlock, vert);
            case MapperDAGVertex.DAG_INIT_VERTEX, MapperDAGVertex.DAG_END_VERTEX ->
              generateInitEndFifoCall(operatorBlock, vert);
            default -> throw new PreesmRuntimeException("DAG Vertex " + vert + " has an unknown kind: " + vertKind);
          }
          break;

        case VertexType.TYPE_SEND, VertexType.TYPE_RECEIVE:
          if (buffer instanceof DistributedBuffer) {
            generateDistributedCommunication(operatorBlock, vert, vertexType);
          } else {
            generateCommunication(operatorBlock, vert, vertexType);
          }
          break;
        default:
          throw new PreesmRuntimeException("Vertex " + vert + " has an unknown type: " + vert.getKind());
      }
    });

    final List<Block> resultList = this.coreBlocks.entrySet().stream()
        .sorted((e1, e2) -> e1.getKey().getHardwareId() - e2.getKey().getHardwareId()).map(Entry::getValue)
        .collect(Collectors.toList());

    // 3 - Put the buffer definition in their right place
    generateBufferDefinitions();
    generateTopBuffers(resultList);
    // 4 - Set enough info to compact instrumentation code
    compactPapifyUsage(resultList);

    return Collections.unmodifiableList(resultList);
  }

  void compactPapifyUsage(List<Block> allBlocks) {
    for (final Block cluster : allBlocks) {
      if (cluster instanceof final CoreBlock coreBlock) {
        final EList<Variable> definitions = cluster.getDefinitions();
        final EList<CodeElt> loopBlockElts = coreBlock.getLoopBlock().getCodeElts();
        final EList<CodeElt> initBlockElts = coreBlock.getInitBlock().getCodeElts();

        /*
         * Only one #ifdef _PREESM_MONITORING_INIT in the definition code Assumption: All the PapifyActions are printed
         * consecutively (AS CONSTANTS ARE NOT PRINTED, THIS IS USUALLY TRUE)
         */
        if (!definitions.isEmpty()) {
          for (final Variable definition : definitions) {
            if (definition instanceof final PapifyAction papifyAction) {
              papifyAction.setOpening(true);
              break;
            }
          }

          for (int iterator = definitions.size() - 1; iterator >= 0; iterator--) {
            if (definitions.get(iterator) instanceof final PapifyAction papifyAction) {
              papifyAction.setClosing(true);
              break;
            }
          }
        }
        /*
         * Minimizing the number of #ifdef _PREESM_MONITORING_INIT in the loop
         */
        if (!loopBlockElts.isEmpty()) {
          if (loopBlockElts.get(0) instanceof final PapifyFunctionCall pfc) {
            pfc.setOpening(true);
            if (!(loopBlockElts.get(1) instanceof PapifyFunctionCall)) {
              pfc.setClosing(true);
            }
          }
          for (int iterator = 1; iterator < loopBlockElts.size() - 1; iterator++) {
            if (loopBlockElts.get(iterator) instanceof final PapifyFunctionCall pfc
                && !(loopBlockElts.get(iterator - 1) instanceof PapifyFunctionCall)) {
              pfc.setOpening(true);
            }
            if (loopBlockElts.get(iterator) instanceof final PapifyFunctionCall pfc
                && !(loopBlockElts.get(iterator + 1) instanceof PapifyFunctionCall)) {
              pfc.setClosing(true);
            }
          }
          if (loopBlockElts.get(loopBlockElts.size() - 1) instanceof final PapifyFunctionCall pfc) {
            pfc.setClosing(true);
            if (!(loopBlockElts.get(loopBlockElts.size() - 2) instanceof PapifyFunctionCall)) {
              pfc.setOpening(true);
            }
          }
        }
        if (!initBlockElts.isEmpty()) {
          if (initBlockElts.get(0) instanceof final PapifyFunctionCall pfc) {
            pfc.setOpening(true);
            if (!(initBlockElts.get(1) instanceof PapifyFunctionCall)) {
              pfc.setClosing(true);
            }
          }
          for (int iterator = 1; iterator < initBlockElts.size() - 1; iterator++) {
            if (initBlockElts.get(iterator) instanceof final PapifyFunctionCall pfc
                && !(initBlockElts.get(iterator - 1) instanceof PapifyFunctionCall)) {
              pfc.setOpening(true);
            }
            if (initBlockElts.get(iterator) instanceof final PapifyFunctionCall pfc
                && !(initBlockElts.get(iterator + 1) instanceof PapifyFunctionCall)) {
              pfc.setClosing(true);
            }
          }
          if (initBlockElts.get(initBlockElts.size() - 1) instanceof final PapifyFunctionCall pfc) {
            pfc.setClosing(true);
            if (!(initBlockElts.get(initBlockElts.size() - 2) instanceof PapifyFunctionCall)) {
              pfc.setOpening(true);
            }
          }
        }
      }
    }
  }

  /**
   * Generate the {@link CodegenPackage Codegen Model} for an actor firing. This method will create an {@link ActorCall}
   * or a {@link FunctionCall} and place it in the {@link LoopBlock} of the {@link CoreBlock} passed as a parameter. If
   * the called {@link DAGVertex actor} has an initialization function, this method will check if it has already been
   * called. If not, it will create a call in the current {@link CoreBlock}.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the actor firing is performed.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the actor firing.
   *
   */
  protected void generateActorFiring(final CoreBlock operatorBlock, final DAGVertex dagVertex) {

    // store buffers on which MD5 can be computed to check validity of transformations
    if (dagVertex.outgoingEdges().isEmpty()) {
      final Set<DAGEdge> incomingEdges = dagVertex.incomingEdges();
      for (final DAGEdge inEdge : incomingEdges) {
        final BufferAggregate bufferAggregate = inEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
        for (final BufferProperties buffProperty : bufferAggregate) {
          final Buffer buffer = srSDFEdgeBuffers.get(buffProperty);
          if (buffer != null) {
            operatorBlock.getSinkFifoBuffers().add(buffer);
          }
        }
      }
    }

    // Check whether the ActorCall is a call to a hierarchical actor or not.
    final Object refinement = dagVertex.getRefinement();

    // If the actor is hierarchical
    if (dagVertex.getPropertyBean().getValue(ClusteringHelper.PISDF_ACTOR_IS_CLUSTER) != null) {
      // try to generate for loop on a hierarchical actor
      PreesmLogger.getLogger().log(Level.FINE, () -> "tryGenerateRepeatActorFiring " + dagVertex.getName());

      // prepare option for SrDAGOutsideFetcher
      final Map<String, Object> outsideFetcherOption = new LinkedHashMap<>();
      outsideFetcherOption.put("dagVertex", dagVertex);
      outsideFetcherOption.put("dag", this.algo);
      outsideFetcherOption.put("coreBlock", operatorBlock);
      outsideFetcherOption.put("srSDFEdgeBuffers", this.srSDFEdgeBuffers);

      // Retrieve original cluster actor
      final AbstractActor actor = dagVertex.getPropertyBean().getValue(ClusteringHelper.PISDF_REFERENCE_ACTOR);
      final AbstractActor originalActor = PreesmCopyTracker.getOriginalSource(actor);
      if (!this.scheduleMapping.containsKey(originalActor)) {
        throw new PreesmRuntimeException("Codegen for " + dagVertex.getName() + " failed.");
      }
      new CodegenClusterModelGeneratorSwitch(this.algo.getReferencePiMMGraph(), operatorBlock, scenario,
          new SrDAGOutsideFetcher(), outsideFetcherOption).generate(this.scheduleMapping.get(originalActor));

    } else {
      ActorPrototypes prototypes = null;
      // If the actor has an IDL refinement
      if ((refinement instanceof final CodeRefinement cr) && (cr.getLanguage() == Language.IDL)) {
        // Retrieve the prototypes associated to the actor
        prototypes = getActorPrototypes(dagVertex);
      } else if (refinement instanceof final ActorPrototypes actorPrototypes) {
        // Or if we already extracted prototypes from a .h refinement
        prototypes = actorPrototypes;
      }

      if (prototypes == null) {
        // If the actor has no refinement
        throw new PreesmRuntimeException(ERROR_NO_REFINEMENT.formatted(dagVertex));
      }
      // Generate the loop functionCall
      final Prototype loopPrototype = prototypes.getLoopPrototype();
      if (loopPrototype == null) {
        throw new PreesmRuntimeException(ERROR_NO_LOOP_INTERFACE.formatted(dagVertex));
      }
      if (!loopPrototype.getIsStandardC()) {
        throw new PreesmRuntimeException(ERROR_NON_C_REFINEMENT.formatted(dagVertex));
      }
      // adding the call to the FPGA load functions only once. The printFpgaLoad will
      // return a no-null string only with the right printer and nothing for the others
      // Visit all codeElements already present in the InitBlock
      final EList<CodeElt> codeElts = operatorBlock.getInitBlock().getCodeElts();
      if (codeElts.isEmpty()) {
        final FpgaLoadAction fpgaLoadActionFunctionCalls = generateFpgaLoadFunctionCalls(dagVertex, loopPrototype,
            false);
        // Add the function call to load the hardware accelerators into the FPGA (when needed)
        operatorBlock.getInitBlock().getCodeElts().add(fpgaLoadActionFunctionCalls);
        // Add also the GlobalBufferDeclaration just after the fpga load, before the loop
        final GlobalBufferDeclaration globalBufferDeclarationCall = generateGlobalBufferDeclaration(dagVertex,
            loopPrototype, false);
        operatorBlock.getInitBlock().getCodeElts().add(globalBufferDeclarationCall);
      }
      final RegisterSetUpAction registerSetUpActionFunctionCall = generateRegisterSetUpFunctionCall(dagVertex,
          loopPrototype, false);
      final FreeDataTransferBuffer freeDataTransferBufferFunctionCall = generateFreeDataTransferBuffer(dagVertex,
          loopPrototype, false);
      final DataTransferAction dataTransferActionFunctionCall = generateDataTransferFunctionCall(dagVertex,
          loopPrototype, false);
      final OutputDataTransfer outputDataTransferFunctionCall = generateOutputDataTransferFunctionCall(dagVertex,
          loopPrototype, false);
      final ActorFunctionCall functionCall = generateFunctionCall(dagVertex, loopPrototype, false);

      boolean monitoringTiming = false;
      boolean monitoringEvents = false;
      final PapifyAction papifyActionS = CodegenModelUserFactory.eINSTANCE.createPapifyAction();
      final Constant papifyPEId = CodegenModelUserFactory.eINSTANCE.createConstant();
      // Check if this actor has a monitoring configuration
      final PapifyConfig papifyConfig = this.scenario.getPapifyConfig();
      final AbstractActor referencePiVertex = dagVertex.getReferencePiVertex();
      if (this.papifyActive && papifyConfig.hasPapifyConfig(referencePiVertex)) {
        // Add the papify action variable
        papifyActionS.setName("papify_actions_".concat(dagVertex.getName()));
        papifyActionS.setType("papify_action_s");
        papifyActionS.setComment("papify configuration variable");
        operatorBlock.getDefinitions().add(papifyActionS);

        // Add the function to configure the monitoring in this PE (operatorBlock)
        papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);

        // Add the function to configure the monitoring in this PE (operatorBlock)
        if (!(this.papifiedPEs.contains(operatorBlock.getName()))) {
          this.papifiedPEs.add(operatorBlock.getName());
          // Create the variable associated to the PE id
          papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
          final FunctionCall functionCallPapifyConfigurePE = generatePapifyConfigurePEFunctionCall(operatorBlock,
              papifyConfig, papifyPEId);
          operatorBlock.getInitBlock().getCodeElts().add(functionCallPapifyConfigurePE);
        } else {
          // Create the variable associated to the PE id
          papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
        }

        // Add the function to configure the monitoring of this actor (dagVertex)
        final PapifyFunctionCall functionCallPapifyConfigureActor = generatePapifyConfigureActorFunctionCall(dagVertex,
            papifyConfig, papifyActionS);
        operatorBlock.getInitBlock().getCodeElts().add(functionCallPapifyConfigureActor);

        // What are we monitoring?
        monitoringEvents = papifyConfig.isMonitoringEvents(referencePiVertex);
        monitoringTiming = papifyConfig.isMonitoringTiming(referencePiVertex);
        if (monitoringEvents) {
          // Generate Papify start function for events
          final PapifyFunctionCall functionCallPapifyStart = generatePapifyStartFunctionCall(dagVertex, papifyPEId,
              papifyActionS);
          // Add the Papify start function for events to the loop
          operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyStart);
        }

        if (monitoringTiming) {
          // Generate Papify start timing function
          final PapifyFunctionCall functionCallPapifyTimingStart = generatePapifyStartTimingFunctionCall(dagVertex,
              papifyPEId, papifyActionS);
          // Add the Papify start timing function to the loop
          operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyTimingStart);
        }
      }
      // Add the function call for RegisterSetUp to the loopBlock just before the function call
      operatorBlock.getLoopBlock().getCodeElts().add(registerSetUpActionFunctionCall);
      // Add the function call for DataTransfer to the loopBlock just before the function call
      operatorBlock.getLoopBlock().getCodeElts().add(dataTransferActionFunctionCall);

      registerCallVariableToCoreBlock(operatorBlock, functionCall);
      // Add the function call to the operatorBlock
      operatorBlock.getLoopBlock().getCodeElts().add(functionCall);
      // Free buffer data transfer that may be used freeDataTransferBufferFunctionCall
      operatorBlock.getLoopBlock().getCodeElts().add(freeDataTransferBufferFunctionCall);
      // Add the function call for OutputDataTransfer to the loopBlock just after the function call
      operatorBlock.getLoopBlock().getCodeElts().add(outputDataTransferFunctionCall);

      if (this.papifyActive && papifyConfig.hasPapifyConfig(referencePiVertex)) {
        if (monitoringTiming) {
          // Generate Papify stop timing function
          final PapifyFunctionCall functionCallPapifyTimingStop = generatePapifyStopTimingFunctionCall(dagVertex,
              papifyPEId, papifyActionS);
          // Add the Papify stop timing function to the loop
          operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyTimingStop);
        }
        if (monitoringEvents) {
          // Generate Papify stop function for events
          final PapifyFunctionCall functionCallPapifyStop = generatePapifyStopFunctionCall(dagVertex, papifyPEId,
              papifyActionS);
          // Add the Papify stop function for events to the loop
          operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyStop);
        }
        // Generate Papify writing function
        final PapifyFunctionCall functionCallPapifyWriting = generatePapifyWritingFunctionCall(dagVertex, papifyPEId,
            papifyActionS);
        // Add the Papify writing function to the loop
        operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyWriting);
      }
      // Save the functionCall in the dagvertexFunctionCall Map
      this.dagVertexCalls.put(dagVertex, functionCall);

      // Generate the init FunctionCall (if any)
      final Prototype initPrototype = prototypes.getInitPrototype();
      if (initPrototype != null) {
        if (!initPrototype.getIsStandardC()) {
          throw new PreesmRuntimeException("The actor " + dagVertex + " has a non standard C refinement.");
        }

        final FunctionCall functionCall2 = generateFunctionCall(dagVertex, initPrototype, true);

        registerCallVariableToCoreBlock(operatorBlock, functionCall2);
        // Add the function call to the operatorBlock
        operatorBlock.getInitBlock().getCodeElts().add(functionCall2);
      }
    }

  }

  /**
   * Generate the {@link Buffer} definition. This method sets the {@link Buffer#reaffectCreator(Block) Creator}
   * attributes. Also re-order the buffer definitions list so that containers are always defined before content.
   *
   */
  protected void generateBufferDefinitions() {
    this.mainBuffers.entrySet().forEach(e -> generateBufferDefinition(e.getKey(), e.getValue()));
  }

  private void generateBufferDefinition(final String memoryBank, final Buffer mainBuffer) {
    // Identify the corresponding operator block.
    // (also find out if the Buffer is local (i.e. not shared between
    // several CoreBlock)
    CoreBlock correspondingOperatorBlock = null;
    final boolean isLocal;
    final String correspondingOperatorID;

    if (memoryBank.equals("Shared")) {
      // If the memory bank is shared, let the main operator
      // declare the Buffer.
      correspondingOperatorID = this.scenario.getSimulationInfo().getMainOperator().getInstanceName();
      isLocal = false;

      // Check that the main operator block exists.
      CoreBlock mainOperatorBlock = null;
      for (final Entry<ComponentInstance, CoreBlock> componentEntry : this.coreBlocks.entrySet()) {
        if (componentEntry.getKey().getInstanceName().equals(correspondingOperatorID)) {
          mainOperatorBlock = componentEntry.getValue();
        }
      }

      // If the main operator does not exist
      if (mainOperatorBlock == null) {
        // Create it
        final ComponentInstance componentInstance = this.archi.getComponentInstance(correspondingOperatorID);
        mainOperatorBlock = CodegenModelUserFactory.eINSTANCE.createCoreBlock(componentInstance);
        this.coreBlocks.put(componentInstance, mainOperatorBlock);
      }
    } else {
      // else, the operator corresponding to the memory bank will
      // do the work
      correspondingOperatorID = memoryBank;
      isLocal = true;
    }

    // Find the block
    for (final Entry<ComponentInstance, CoreBlock> componentEntry : this.coreBlocks.entrySet()) {
      if (componentEntry.getKey().getInstanceName().equals(correspondingOperatorID)) {
        correspondingOperatorBlock = componentEntry.getValue();
      }
    }
    // Recursively set the creator for the current Buffer and all its
    // subBuffer
    recursiveSetBufferCreator(mainBuffer, correspondingOperatorBlock, isLocal);

    if (correspondingOperatorBlock != null) {
      final EList<Variable> definitions = correspondingOperatorBlock.getDefinitions();
      ECollections.sort(definitions, new VariableSorter());
    }
  }

  /**
   * This method creates a {@link Buffer} for each {@link DAGEdge} of the {@link #dag}. It also calls
   * {@link #generateSubBuffers(Buffer, DAGEdge, Integer)} to create distinct {@link SubBuffer} corresponding to all the
   * {@link SDFEdge} of the single-rate {@link SDFGraph} from which the {@link #dag} is derived.<br>
   * <br>
   * In this method, the {@link #sharedBuffer}, and the {@link #dagEdgeBuffers} attributes are filled.
   *
   *
   */
  protected void generateBuffers() {
    // Create a main Buffer for each MEG
    boolean showWarningOnce = false;
    for (final Entry<String, MemoryExclusionGraph> entry : this.megs.entrySet()) {

      final String memoryBank = entry.getKey();
      final MemoryExclusionGraph meg = entry.getValue();

      // Create the Main Shared buffer
      final long size = meg.getPropertyBean().getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE);

      final Buffer mainBuffer = CodegenModelUserFactory.eINSTANCE.createBuffer();
      mainBuffer.setName(memoryBank);
      mainBuffer.setType("char");
      mainBuffer.setTokenTypeSizeInBit(8); // char is 8 bits

      // To be removed
      // mainBuffer.setSizeInBit(size);
      // mainBuffer.setNbToken((long) (size + 7L) / mainBuffer.getTypeSize());
      mainBuffer.setNbToken((long) Math.ceil((double) size / (double) mainBuffer.getTokenTypeSizeInBit()));
      this.mainBuffers.put(memoryBank, mainBuffer);

      final Map<DAGEdge, Long> allocation = meg.getPropertyBean().getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);

      // generate the subbuffer for each dagedge
      for (final Entry<DAGEdge, Long> dagAlloc : allocation.entrySet()) {
        final DAGEdge edge = dagAlloc.getKey();
        final DAGVertex source = edge.getSource();
        final DAGVertex target = edge.getTarget();
        // If the buffer is not a null buffer
        final Long alloc = dagAlloc.getValue();
        if (alloc != -1) {
          final SubBuffer dagEdgeBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();

          // Old Naming (too long)
          final String comment = source.getName() + " > " + target.getName();
          dagEdgeBuffer.setComment(comment);

          String name = source.getName() + "__" + target.getName();

          name = generateUniqueBufferName(name);
          dagEdgeBuffer.setName(name);
          dagEdgeBuffer.reaffectContainer(mainBuffer);
          dagEdgeBuffer.setOffsetInBit(alloc);
          dagEdgeBuffer.setType("char");
          dagEdgeBuffer.setTokenTypeSizeInBit(8);

          // Save the DAGEdgeBuffer
          final DAGVertex originalSource = this.algo.getVertex(source.getName());
          final DAGVertex originalTarget = this.algo.getVertex(target.getName());
          final DAGEdge originalDagEdge = this.algo.getEdge(originalSource, originalTarget);
          if (!this.dagEdgeBuffers.containsKey(originalDagEdge)) {
            // Generate subsubbuffers. Each subsubbuffer corresponds to
            // an edge of the single rate SDF Graph
            final Long dagEdgeSize = generateSubBuffers(dagEdgeBuffer, edge);

            // also accessible with dagAlloc.getKey().getWeight()
            // dagEdgeBuffer.setSize(dagEdgeSize);
            dagEdgeBuffer
                .setNbToken((long) Math.ceil((double) dagEdgeSize / (double) dagEdgeBuffer.getTokenTypeSizeInBit()));
            this.dagEdgeBuffers.put(originalDagEdge, dagEdgeBuffer);
          } else {
            if (!showWarningOnce) {
              PreesmLogger.getLogger().info(
                  "Using DistributedOnly memory distribution. This method is not optimized and it's not stable yet.\n"
                      + "Please take a look at Issue #142 in PREESM github");
              showWarningOnce = true;
            }
            // Generate subsubbuffers. Each subsubbuffer corresponds to an edge of the single rate SDF Graph
            final Long dagEdgeSize = generateTwinSubBuffers(dagEdgeBuffer, edge);

            // also accessible with dagAlloc.getKey().getWeight()
            // dagEdgeBuffer.setSize(dagEdgeSize);
            dagEdgeBuffer
                .setNbToken((long) Math.ceil((double) dagEdgeSize / (double) dagEdgeBuffer.getTokenTypeSizeInBit()));
            final DistributedBuffer duplicatedBuffer = generateDistributedBuffer(
                this.dagEdgeBuffers.get(originalDagEdge), dagEdgeBuffer);
            this.dagEdgeBuffers.put(originalDagEdge, duplicatedBuffer);
            /**
             * Notes for future development of this feature If you are reading this because you want to adapt the code
             * generation for distributed only memory allocation, here is a TODO for you: The updateWithSchedule method
             * (in the MemoryExclusionGraph) does not take into account communications which, as commented there, have
             * no effects when allocating memory in shared memory. However, communications may have a very strong impact
             * when targetting shared memory. In particular, as soon as a SendEnd has passed, it can be assumed that the
             * memory associated to an outgoing buffer can be freed (contrary to shared mem where you have to wait for
             * the firing of its consumer actor). So in order to optimize memory allocation for distributed memory
             * allocation, a new update of the MemEx has to be done, taking communication into account this time.
             *
             */
          }
        } else {
          // the buffer is a null buffer
          final NullBuffer dagEdgeBuffer = CodegenModelUserFactory.eINSTANCE.createNullBuffer();

          // Old Naming (too long)
          final String comment = source.getName() + " > " + target.getName();
          dagEdgeBuffer.setComment("NULL_" + comment);
          dagEdgeBuffer.reaffectContainer(mainBuffer);

          // Generate subsubbuffers. Each subsubbuffer corresponds to an edge of the single rate SDF Graph
          final Long dagEdgeSize = generateSubBuffers(dagEdgeBuffer, edge);

          // We set the size to keep the information
          dagEdgeBuffer
              .setNbToken((long) Math.ceil((double) dagEdgeSize / (double) dagEdgeBuffer.getTokenTypeSizeInBit()));
          // dagEdgeBuffer.setSize(dagEdgeSize);

          // Save the DAGEdgeBuffer
          final DAGVertex originalSource = this.algo.getVertex(source.getName());
          final DAGVertex originalTarget = this.algo.getVertex(target.getName());
          final DAGEdge originalDagEdge = this.algo.getEdge(originalSource, originalTarget);
          this.dagEdgeBuffers.put(originalDagEdge, dagEdgeBuffer);
        }
      }

      // Generate buffers for each fifo
      final Map<MemoryExclusionVertex,
          Long> fifoAllocation = meg.getPropertyBean().getValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION);
      for (final Entry<MemoryExclusionVertex, Long> fifoAlloc : fifoAllocation.entrySet()) {
        final SubBuffer fifoBuffer = CodegenModelUserFactory.eINSTANCE.createSubBuffer();

        // Old Naming (too long)
        final MemoryExclusionVertex fifoAllocKey = fifoAlloc.getKey();
        final String source = fifoAllocKey.getSource();
        final String sink = fifoAllocKey.getSink();
        final String comment = source + " > " + sink;
        fifoBuffer.setComment(comment);

        String name = source + "__" + sink;
        name = generateUniqueBufferName(name);
        fifoBuffer.setName(name);
        fifoBuffer.reaffectContainer(mainBuffer);
        fifoBuffer.setOffsetInBit(fifoAlloc.getValue());
        fifoBuffer.setType("char");
        fifoBuffer.setTokenTypeSizeInBit(8);
        fifoBuffer.setNbToken(fifoAllocKey.getWeightInByte());

        // Get Init vertex
        final DAGVertex dagEndVertex = this.algo
            .getVertex(source.substring((MemoryExclusionGraph.FIFO_HEAD_PREFIX).length()));
        final DAGVertex dagInitVertex = this.algo.getVertex(sink);

        final Pair<DAGVertex, DAGVertex> key = new Pair<>(dagEndVertex, dagInitVertex);
        Pair<Buffer, Buffer> value = this.dagFifoBuffers.get(key);
        if (value == null) {
          value = new Pair<>(null, null);
          this.dagFifoBuffers.put(key, value);
        }
        if (source.startsWith(MemoryExclusionGraph.FIFO_HEAD_PREFIX)) {
          this.dagFifoBuffers.put(key, new Pair<>(fifoBuffer, value.getValue()));
        } else {
          this.dagFifoBuffers.put(key, new Pair<>(value.getKey(), fifoBuffer));
        }
      }
    }
  }

  /**
   * This method retrieve the sink and source actor involved in the communication internode (in multinode context). To
   * simplify the later allocation communication buffer are indexed on the first block
   *
   * @param resultList
   *          the list of block
   *
   */
  private void generateTopBuffers(List<Block> resultList) {
    if (!multinode) {
      return;
    }

    final CoreBlock firstBlock = (CoreBlock) resultList.stream().findFirst().orElseThrow();

    for (final Block block : resultList) {
      final CoreBlock coreBlock = (CoreBlock) block;

      final Iterator<Buffer> iterBuffer = coreBlock.getSinkFifoBuffers().iterator();
      while (iterBuffer.hasNext()) {
        final Buffer buffer = iterBuffer.next();

        if (buffer.getComment().contains("> snk")) {
          buffer.setComment(buffer.getComment().replaceFirst("^.* > ", ""));
          buffer.setComment(buffer.getComment().replaceFirst("_\\d+_in$", ""));
          firstBlock.getTopBuffers().add(buffer);
          iterBuffer.remove();
        }
      }

      for (final Variable v : coreBlock.getDeclarations()) {

        if (!(v instanceof final Buffer buffer) || ((Buffer) v).getComment() == null) {
          continue;
        }

        if (buffer.getComment().matches("^src.*_0_out >.*")) {
          buffer.setComment(buffer.getComment().replaceFirst("_0_out >.*$", ""));
          if (firstBlock.getTopBuffers().stream().noneMatch(x -> x.getComment().equals(buffer.getComment()))) {
            firstBlock.getTopBuffers().add(buffer);
          }
        }
      }
    }
  }

  /**
   * This method generates the list of variable corresponding to a prototype of the {@link DAGVertex} firing. The
   * {@link Prototype} passed as a parameter must belong to the processed {@link DAGVertex}.
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param prototype
   *          the prototype whose {@link Variable variables} are retrieved
   * @param isInit
   *          Whethet the given prototype is an Init or a loop call. (We do not check missing arguments in the IDL for
   *          init Calls)
   * @return the entry
   */
  protected SimpleEntry<List<Variable>, List<PortDirection>> generateCallVariables(final DAGVertex dagVertex,
      final Prototype prototype, final boolean isInit) {

    // Sorted list of the variables used by the prototype.
    // The integer is only used to order the variable and is retrieved
    // from the prototype
    final Map<Integer, Variable> variableList = new TreeMap<>();
    final Map<Integer, PortDirection> directionList = new TreeMap<>();

    // Retrieve the Variable corresponding to the arguments of the prototype
    for (final CodeGenArgument arg : prototype.getArguments().keySet()) {

      // Check that the Actor has the right ports
      final PortDirection dir = checkActorPort(dagVertex, arg);

      // Retrieve the Edge corresponding to the current Argument
      final BufferProperties subBufferProperties = getEdgeFromArgument(dagVertex, arg);

      // At this point, the dagEdge, srsdfEdge corresponding to the current argument were identified
      // Get the corresponding Variable
      final Variable varFirstFound = this.srSDFEdgeBuffers.get(subBufferProperties);
      Variable variable = null;
      if (varFirstFound instanceof final DistributedBuffer distributedBuffer) {
        final EList<Buffer> repeatedBuffers = distributedBuffer.getDistributedCopies();
        final String coreBlockName = dagVertex.getPropertyStringValue(OPERATOR_LITERAL);
        for (final Buffer bufferRepeatedChecker : repeatedBuffers) {
          final SubBuffer subBufferChecker = (SubBuffer) bufferRepeatedChecker;
          final SubBuffer repeatedContainer = (SubBuffer) subBufferChecker.getContainer();
          if (repeatedContainer.getContainer().getName().equals(coreBlockName)) {
            variable = subBufferChecker;
            break;
          }
        }
      } else {
        variable = varFirstFound;
      }
      if (variable == null) {
        throw new PreesmRuntimeException(
            "Edge connected to " + arg.getDirection() + " port " + arg.getName() + " of DAG Actor " + dagVertex
                + " is not present in the input MemEx.\n" + "There is something wrong in the Memory Allocation task.");
      }

      variableList.put(prototype.getArguments().get(arg), variable);
      directionList.put(prototype.getArguments().get(arg), dir);
    }

    // Check that all incoming DAGEdge exist in the function call
    if (!isInit) {
      if (dagVertex.getSinkNameList() != null) {
        dagVertex.getSinkNameList().forEach(v -> checkEdgesExist(v, dagVertex, prototype));
      }
      if (dagVertex.getSourceNameList() != null) {
        dagVertex.getSourceNameList().forEach(v -> checkEdgesExist(v, dagVertex, prototype));
      }
    }

    // Retrieve the Variables corresponding to the Parameters of the
    // prototype
    for (final CodeGenParameter param : prototype.getParameters().keySet()) {
      // Check that the actor has the right parameter
      final Argument actorParam = dagVertex.getArgument(param.getName());

      if (actorParam == null) {
        throw new PreesmRuntimeException(
            "Actor " + dagVertex + " has no match for parameter " + param.getName() + " declared in the IDL.");
      }

      final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
      constant.setName(param.getName());
      constant.setValue(actorParam.longValue());
      constant.setType("long");
      variableList.put(prototype.getParameters().get(param), constant);
      directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
    }

    return new SimpleEntry<>(new ArrayList<>(variableList.values()), new ArrayList<>(directionList.values()));
  }

  private PortDirection checkActorPort(final DAGVertex dagVertex, CodeGenArgument arg) {
    PortDirection dir = null;

    // Check that the Actor has the right ports
    boolean containsPort;
    switch (arg.getDirection()) {
      case CodeGenArgument.OUTPUT:
        containsPort = dagVertex.getSinkNameList().contains(arg.getName());
        dir = PortDirection.OUTPUT;
        break;
      case CodeGenArgument.INPUT:
        containsPort = dagVertex.getSourceNameList().contains(arg.getName());
        dir = PortDirection.INPUT;
        break;
      default:
        containsPort = false;
    }
    if (!containsPort) {
      throw new PreesmRuntimeException(
          "Mismatch between actor (" + dagVertex + ") ports and IDL loop prototype argument " + arg.getName());
    }

    return dir;
  }

  private BufferProperties getEdgeFromArgument(final DAGVertex dagVertex, CodeGenArgument arg) {

    // Retrieve the Edge corresponding to the current Argument
    DAGEdge dagEdge = null;
    BufferProperties subBufferProperties = null;
    switch (arg.getDirection()) {
      case CodeGenArgument.OUTPUT:
        final Set<DAGEdge> outEdges = this.algo.outgoingEdgesOf(dagVertex);
        for (final DAGEdge edge : outEdges) {
          final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
          for (final BufferProperties buffProperty : bufferAggregate) {
            // check that this edge is not connected to a receive vertex
            if (buffProperty.getSourceOutputPortID().equals(arg.getName()) && edge.getTarget().getKind() != null) {
              dagEdge = edge;
              subBufferProperties = buffProperty;
            }
          }
        }
        break;
      case CodeGenArgument.INPUT:
        final Set<DAGEdge> inEdges = this.algo.incomingEdgesOf(dagVertex);
        for (final DAGEdge edge : inEdges) {
          final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
          for (final BufferProperties buffProperty : bufferAggregate) {
            // check that this edge is not connected to a send vertex
            if (buffProperty.getDestInputPortID().equals(arg.getName()) && edge.getSource().getKind() != null) {
              dagEdge = edge;
              subBufferProperties = buffProperty;
            }
          }
        }
        break;
      default:
        break;
    }

    if ((dagEdge == null) || (subBufferProperties == null)) {
      throw new PreesmRuntimeException("The DAGEdge connected to the port  " + arg.getName() + " of Actor (" + dagVertex
          + ") does not exist.\n" + "Possible cause is that the DAG was altered before entering the Code generation.\n"
          + "This error may also happen if the port type in the graph and in the IDL are not identical");
    }

    return subBufferProperties;
  }

  /**
   * This method generates the list of SubBuffer corresponding to a prototype of the {@link DAGVertex} firing. The
   * {@link Prototype} passed as a parameter must belong to the processed {@link DAGVertex}. These SubBuffer are going
   * to be used by the DataTransfer function (when needed).
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param prototype
   *          the prototype whose {@link Variable variables} are retrieved
   * @param isInit
   *          Whethet the given prototype is an Init or a loop call. (We do not check missing arguments in the IDL for
   *          init Calls)
   * @return the entry
   */
  protected Entry<List<Buffer>, List<PortDirection>> generateBufferList(final DAGVertex dagVertex,
      final Prototype prototype, final boolean isInit) {
    // Sorted list of the variables used by the prototype.
    // The integer is only used to order the variable and is retrieved from the prototype
    final Map<Integer, Buffer> bufferList = new TreeMap<>();
    final Map<Integer, PortDirection> directionList = new TreeMap<>();
    // Retrieve the Variable corresponding to the arguments of the prototype
    for (final CodeGenArgument arg : prototype.getArguments().keySet()) {

      // Check that the Actor has the right ports
      final PortDirection dir = checkActorPort(dagVertex, arg);

      // Retrieve the Edge corresponding to the current Argument
      final BufferProperties subBufferProperties = getEdgeFromArgument(dagVertex, arg);

      // At this point, the dagEdge, srsdfEdge corresponding to the
      // current argument were identified
      // Get the corresponding Variable
      final Buffer firstFound = this.srSDFEdgeBuffers.get(subBufferProperties);
      Buffer buffer = null;
      final String coreBlockName = dagVertex.getPropertyStringValue(OPERATOR_LITERAL);
      if (firstFound instanceof final DistributedBuffer distributedBuffer) {
        final EList<Buffer> repeatedBuffers = distributedBuffer.getDistributedCopies();
        for (final Buffer repeatedBufferChecker : repeatedBuffers) {
          final SubBuffer repeatedSubBuffer = (SubBuffer) repeatedBufferChecker;
          final SubBuffer repeatedContainer = (SubBuffer) repeatedSubBuffer.getContainer();
          if (repeatedContainer.getContainer().getName().equals(coreBlockName)) {
            buffer = repeatedSubBuffer;
            break;
          }
        }
      } else {
        buffer = firstFound;
      }
      if (buffer == null) {
        throw new PreesmRuntimeException(
            "Edge connected to " + arg.getDirection() + " port " + arg.getName() + " of DAG Actor " + dagVertex
                + " is not present in the input MemEx.\n" + "There is something wrong in the Memory Allocation task.");
      }
      bufferList.put(prototype.getArguments().get(arg), buffer);
      directionList.put(prototype.getArguments().get(arg), dir);
    }

    return new SimpleEntry<>(new ArrayList<>(bufferList.values()), new ArrayList<>(directionList.values()));

  }

  /**
   * This method generates the list of constant variable (no buffers involved) corresponding to a prototype of the
   * {@link DAGVertex} firing. The {@link Prototype} passed as a parameter must belong to the processed
   * {@link DAGVertex}.
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param prototype
   *          the prototype whose {@link Variable variables} are retrieved
   * @param isInit
   *          Whethet the given prototype is an Init or a loop call. (We do not check missing arguments in the IDL for
   *          init Calls)
   * @return the entry
   */
  protected Entry<List<Variable>, List<PortDirection>> generateVariableList(final DAGVertex dagVertex,
      final Prototype prototype, final boolean isInit) {

    // Sorted list of the variables used by the prototype.
    // The integer is only used to order the variable and is retrieved
    // from the prototype
    final Map<Integer, Variable> variableList = new TreeMap<>();
    final Map<Integer, PortDirection> directionList = new TreeMap<>();

    // Retrieve the Variables corresponding to the Parameters of the
    // prototype
    for (final CodeGenParameter param : prototype.getParameters().keySet()) {
      // Check that the actor has the right parameter
      final Argument actorParam = dagVertex.getArgument(param.getName());

      if (actorParam == null) {
        throw new PreesmRuntimeException(
            "Actor " + dagVertex + " has no match for parameter " + param.getName() + " declared in the IDL.");
      }

      final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
      constant.setName(param.getName());
      constant.setValue(actorParam.longValue());
      constant.setType("long");
      variableList.put(prototype.getParameters().get(param), constant);
      directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
    }

    return new SimpleEntry<>(new ArrayList<>(variableList.values()), new ArrayList<>(directionList.values()));
  }

  private boolean checkEdgesExist(final String portName, final DAGVertex dagVertex, final Prototype prototype) {
    for (final CodeGenArgument arguments : prototype.getArguments().keySet()) {
      if (portName.equals(arguments.getName())) {
        return true;
      }
    }
    throw new PreesmRuntimeException("SDF port \"" + portName + "\" of actor \"" + dagVertex
        + "\" has no corresponding parameter in the associated IDL.");
  }

  /**
   * Generate the {@link CodegenPackage Codegen Model} for communication "firing". This method will create an
   * {@link Communication} and place it in the {@link LoopBlock} of the {@link CoreBlock} passed as a parameter.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the actor {@link Communication} is performed.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the actor firing.
   * @param direction
   *          the Type of communication ({@link VertexType#TYPE_SEND} or {@link VertexType#TYPE_RECEIVE}).
   *
   */
  protected void generateCommunication(final CoreBlock operatorBlock, final DAGVertex dagVertex,
      final String direction) {
    // Create the communication
    final SharedMemoryCommunication newComm = CodegenModelUserFactory.eINSTANCE.createSharedMemoryCommunication();
    final Direction dir = (direction.equals(VertexType.TYPE_SEND)) ? Direction.SEND : Direction.RECEIVE;
    final Delimiter delimiter = (direction.equals(VertexType.TYPE_SEND)) ? Delimiter.START : Delimiter.END;
    newComm.setDirection(dir);
    newComm.setDelimiter(delimiter);
    final SlamMessageRouteStep routeStep = dagVertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.SendReceive_routeStep);
    for (final ComponentInstance comp : routeStep.getNodes()) {
      final CommunicationNode comNode = CodegenModelUserFactory.eINSTANCE.createCommunicationNode();
      comNode.setName(comp.getInstanceName());
      comNode.setType(comp.getComponent().getVlnv().getName());
      newComm.getNodes().add(comNode);
    }

    // Find the corresponding DAGEdge buffer(s)
    final DAGEdge dagEdge = dagVertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge);
    final Buffer buffer = this.dagEdgeBuffers.get(dagEdge);
    if (buffer == null) {
      throw new PreesmRuntimeException("No buffer found for edge" + dagEdge);
    }
    newComm.setData(buffer);
    newComm.getParameters().clear();
    newComm.addParameter(buffer, PortDirection.NONE);

    // Set the name of the communication
    // SS <=> Start Send
    // RE <=> Receive End
    String commName = "__" + buffer.getName();
    commName += "__" + operatorBlock.getName();
    newComm.setName(((newComm.getDirection().equals(Direction.SEND)) ? "SS" : "RE") + commName);

    // Find corresponding communications (SS/SE/RS/RE)
    registerCommunication(newComm, dagEdge, dagVertex);

    // Insert the new communication to the loop of the codeblock
    insertCommunication(operatorBlock, dagVertex, newComm);

    // Register the dag buffer to the core
    registerCallVariableToCoreBlock(operatorBlock, newComm);

    // Create the corresponding SE or RS
    final SharedMemoryCommunication newCommZoneComplement = CodegenModelUserFactory.eINSTANCE
        .createSharedMemoryCommunication();
    newCommZoneComplement.setDirection(dir);
    newCommZoneComplement.setDelimiter((delimiter.equals(Delimiter.START)) ? Delimiter.END : Delimiter.START);
    newCommZoneComplement.setData(buffer);
    newCommZoneComplement.getParameters().clear();
    newCommZoneComplement.addParameter(buffer, PortDirection.NONE);

    newCommZoneComplement.setName(((newComm.getDirection().equals(Direction.SEND)) ? "SE" : "RS") + commName);
    for (final ComponentInstance comp : routeStep.getNodes()) {
      final CommunicationNode comNode = CodegenModelUserFactory.eINSTANCE.createCommunicationNode();
      comNode.setName(comp.getInstanceName());
      comNode.setType(comp.getComponent().getVlnv().getName());
      newCommZoneComplement.getNodes().add(comNode);
    }

    // Find corresponding communications (SS/SE/RS/RE)
    registerCommunication(newCommZoneComplement, dagEdge, dagVertex);

    // Insert the new communication to the loop of the codeblock
    insertCommunication(operatorBlock, dagVertex, newCommZoneComplement);

    // No semaphore here, semaphore are only for SS->RE and RE->SR

    // Check if this is a redundant communication
    final Boolean b = dagVertex.getPropertyBean().getValue("Redundant");
    if (b != null && b.equals(Boolean.valueOf(true))) {
      // Mark communication are redundant.
      newComm.setRedundant(true);
      newCommZoneComplement.setRedundant(true);
    }
  }

  /**
   * Generate the {@link CodegenPackage Codegen Model} for communication "firing". This method will create an
   * {@link Communication} and place it in the {@link LoopBlock} of the {@link CoreBlock} passed as a parameter.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the actor {@link Communication} is performed.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the actor firing.
   * @param direction
   *          the Type of communication ({@link VertexType#TYPE_SEND} or {@link VertexType#TYPE_RECEIVE}).
   *
   */
  protected void generateDistributedCommunication(final CoreBlock operatorBlock, final DAGVertex dagVertex,
      final String direction) {
    // Create the communication
    final DistributedMemoryCommunication newComm = CodegenModelUserFactory.eINSTANCE
        .createDistributedMemoryCommunication();
    final Direction dir = (direction.equals(VertexType.TYPE_SEND)) ? Direction.SEND : Direction.RECEIVE;
    final Delimiter delimiter = (direction.equals(VertexType.TYPE_SEND)) ? Delimiter.START : Delimiter.END;
    newComm.setDirection(dir);
    newComm.setDelimiter(delimiter);
    final SlamMessageRouteStep routeStep = dagVertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.SendReceive_routeStep);
    for (final ComponentInstance comp : routeStep.getNodes()) {
      final CommunicationNode comNode = CodegenModelUserFactory.eINSTANCE.createCommunicationNode();
      comNode.setName(comp.getInstanceName());
      comNode.setType(comp.getComponent().getVlnv().getName());
      newComm.getNodes().add(comNode);
    }

    // Find the corresponding DAGEdge buffer(s)
    final DAGEdge dagEdge = dagVertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge);
    final DistributedBuffer distributedBuffer = (DistributedBuffer) this.dagEdgeBuffers.get(dagEdge);
    final String coreBlockName = operatorBlock.getName();

    final List<Buffer> repeatedBuffers = distributedBuffer.getDistributedCopies();
    Buffer buffer = null;
    for (final Buffer bufferRepeatedChecker : repeatedBuffers) {
      final SubBuffer subBufferChecker = (SubBuffer) bufferRepeatedChecker;
      if (subBufferChecker.getContainer().getName().equals(coreBlockName)) {
        buffer = subBufferChecker;
        break;
      }
    }
    if (buffer == null) {
      throw new PreesmRuntimeException("No buffer found for edge" + dagEdge);
    }

    newComm.setData(buffer);
    newComm.getParameters().clear();
    newComm.addParameter(buffer, PortDirection.NONE);

    // Set the name of the communication
    // SS <=> Start Send
    // RE <=> Receive End
    String commName = "__" + buffer.getName();
    commName += "__" + operatorBlock.getName();
    newComm.setName(((newComm.getDirection().equals(Direction.SEND)) ? "SS" : "RE") + commName);

    // Find corresponding communications (SS/SE/RS/RE)
    registerCommunication(newComm, dagEdge, dagVertex);

    // Insert the new communication to the loop of the codeblock
    insertCommunication(operatorBlock, dagVertex, newComm);

    // Register the dag buffer to the core
    registerCallVariableToCoreBlock(operatorBlock, newComm);

    // Create the corresponding SE or RS
    final DistributedMemoryCommunication newCommZoneComplement = CodegenModelUserFactory.eINSTANCE
        .createDistributedMemoryCommunication();
    newCommZoneComplement.setDirection(dir);
    newCommZoneComplement.setDelimiter((delimiter.equals(Delimiter.START)) ? Delimiter.END : Delimiter.START);
    newCommZoneComplement.setData(buffer);
    newCommZoneComplement.getParameters().clear();
    newCommZoneComplement.addParameter(buffer, PortDirection.NONE);

    newCommZoneComplement.setName(((newComm.getDirection().equals(Direction.SEND)) ? "SE" : "RS") + commName);
    for (final ComponentInstance comp : routeStep.getNodes()) {
      final CommunicationNode comNode = CodegenModelUserFactory.eINSTANCE.createCommunicationNode();
      comNode.setName(comp.getInstanceName());
      comNode.setType(comp.getComponent().getVlnv().getName());
      newCommZoneComplement.getNodes().add(comNode);
    }

    // Find corresponding communications (SS/SE/RS/RE)
    registerCommunication(newCommZoneComplement, dagEdge, dagVertex);

    // Insert the new communication to the loop of the codeblock
    insertCommunication(operatorBlock, dagVertex, newCommZoneComplement);

    // No semaphore here, semaphore are only for SS->RE and RE->SR

    // Check if this is a redundant communication
    final Boolean b = dagVertex.getPropertyBean().getValue("Redundant");
    if (b != null && b.equals(Boolean.valueOf(true))) {
      // Mark communication are redundant.
      newComm.setRedundant(true);
      newCommZoneComplement.setRedundant(true);
    }
  }

  /**
   * Generate the {@link FifoCall} that corresponds to the {@link DAGVertex} passed as a parameter and add it to the
   * {@link CoreBlock#getLoopBlock() loop block} of the given {@link CoreBlock}. Also generate the corresponding init
   * call and add it to the {@link CoreBlock#getInitBlock()}.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} that executes the {@link DAGVertex}
   * @param dagVertex
   *          A {@link DAGInitVertex} or a {@link DAGEndVertex} that respectively correspond to a pull and a push
   *          operation.
   */
  protected void generateInitEndFifoCall(final CoreBlock operatorBlock, final DAGVertex dagVertex) {
    // Create the Fifo call and set basic property
    final FifoCall fifoCall = CodegenModelUserFactory.eINSTANCE.createFifoCall();
    fifoCall.setName(dagVertex.getName());

    // Find the type of FiFo operation
    final FifoOperation fifoOp = switch (dagVertex.getKind()) {
      case MapperDAGVertex.DAG_INIT_VERTEX -> FifoOperation.POP;
      case MapperDAGVertex.DAG_END_VERTEX -> FifoOperation.PUSH;
      default ->
        throw new PreesmRuntimeException("The DAGVertex " + dagVertex + " does not corresponds to a Fifo primitive.");
    };
    fifoCall.setOperation(fifoOp);

    // Get buffer used by the FifoCall (in/out)
    Set<DAGEdge> edges;
    PortDirection dir;
    if (fifoCall.getOperation().equals(FifoOperation.POP)) {
      edges = this.algo.outgoingEdgesOf(dagVertex);
      dir = PortDirection.OUTPUT;
    } else {
      edges = this.algo.incomingEdgesOf(dagVertex);
      dir = PortDirection.INPUT;
    }
    // There might be more than one edge, if one is connected to a
    // send/receive
    DAGEdge edge = null;
    for (final DAGEdge currentEdge : edges) {
      final DAGVertex source = currentEdge.getSource();
      final DAGVertex target = currentEdge.getTarget();
      if (source.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType).equals(VertexType.TASK)
          && target.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType).equals(VertexType.TASK)) {
        edge = currentEdge;
      }
    }
    if (edge == null) {
      throw new PreesmRuntimeException(
          "DAGVertex " + dagVertex + " is not connected to any " + VertexType.TYPE_TASK + " vertex.");
    }

    final BufferAggregate aggregate = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
    final BufferProperties bufferProperty = aggregate.get(0);
    final Buffer buffer = this.srSDFEdgeBuffers.get(bufferProperty);
    if (buffer == null) {
      throw new PreesmRuntimeException("DAGEdge " + edge + " was not allocated in memory.");
    }
    fifoCall.addParameter(buffer, dir);

    // Retrieve the internal buffer
    DAGVertex dagEndVertex;
    DAGVertex dagInitVertex;
    final String endReferenceName = dagVertex.getPropertyBean().getValue(SDFInitVertex.END_REFERENCE);
    if (fifoCall.getOperation().equals(FifoOperation.POP)) {
      dagInitVertex = dagVertex;
      dagEndVertex = (DAGVertex) dagInitVertex.getBase().getVertex(endReferenceName);
    } else {
      dagEndVertex = dagVertex;
      dagInitVertex = (DAGVertex) dagEndVertex.getBase().getVertex(endReferenceName);
    }
    final Pair<Buffer, Buffer> buffers = this.dagFifoBuffers.get(new Pair<>(dagEndVertex, dagInitVertex));
    if ((buffers == null) || (buffers.getKey() == null)) {
      throw new PreesmRuntimeException("No buffer was allocated for the the following pair of end/init vertices: "
          + dagEndVertex.getName() + " " + dagInitVertex.getName());
    }
    fifoCall.setHeadBuffer(buffers.getKey());
    fifoCall.setBodyBuffer(buffers.getValue());
    if (fifoCall.getOperation().equals(FifoOperation.POP)) {
      buffers.getKey().reaffectCreator(operatorBlock);
      if (buffers.getValue() != null) {
        buffers.getValue().reaffectCreator(operatorBlock);
      }
    }

    buffers.getKey().getUsers().add(operatorBlock);
    if (buffers.getValue() != null) {
      buffers.getValue().getUsers().add(operatorBlock);
    }

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
      final PersistenceLevel level = dagInitVertex.getPropertyBean().getValue(MapperDAGVertex.PERSISTENCE_LEVEL);
      if (level == null || PersistenceLevel.PERMANENT.equals(level)) {
        operatorBlock.getInitBlock().getCodeElts().add(fifoInitCall);
      } else {
        operatorBlock.getLoopBlock().getCodeElts().add(fifoInitCall);
      }
    }

    // Register associated fifo calls (push/pop)
    if (fifoCall.getOperation().equals(FifoOperation.POP)) {
      // Pop operations are the first to be encountered.
      // We simply store the dagVertex with its associated fifoCall in a Map.
      // This Map will be used when processing the associated Push operation
      this.popFifoCalls.put(dagInitVertex, fifoCall);

    } else { // Push case
      // Retrieve the corresponding Pop
      final FifoCall popCall = this.popFifoCalls.remove(dagInitVertex);
      popCall.setFifoHead(fifoCall);
      fifoCall.setFifoTail(popCall);
    }

    // Add the Fifo call to the loop of its coreBlock
    operatorBlock.getLoopBlock().getCodeElts().add(fifoCall);
    this.dagVertexCalls.put(dagVertex, fifoCall);
    buffer.getUsers().add(operatorBlock);
  }

  /**
   * This method generate the {@link FunctionCall} corresponding to a {@link Prototype} associated to a
   * {@link DAGVertex}, both passed as parameters.
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param prototype
   *          the {@link Prototype IDL prototype} of the {@link FunctionCall} to generate.
   * @param isInit
   *          Indicicate whether this function call corresponds to an initialization call (in such case,
   *          {@link #generateCallVariables(DAGVertex, Prototype, boolean)} does not need to check for missing parameter
   *          in the prototype.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected ActorFunctionCall generateFunctionCall(final DAGVertex dagVertex, final Prototype prototype,
      final boolean isInit) {
    // Create the corresponding FunctionCall
    final ActorFunctionCall func = CodegenModelUserFactory.eINSTANCE.createActorFunctionCall();
    func.setName(prototype.getFunctionName());
    func.setActorName(dagVertex.getName());
    func.setOriActor(PreesmCopyTracker.getOriginalSource(dagVertex.getReferencePiVertex()));
    // Retrieve the Arguments that must correspond to the incoming data
    // fifos
    final Entry<List<Variable>, List<PortDirection>> callVars = generateCallVariables(dagVertex, prototype, isInit);
    // Put Variables in the function call
    for (int idx = 0; idx < callVars.getKey().size(); idx++) {
      func.addParameter(callVars.getKey().get(idx), callVars.getValue().get(idx));
    }

    identifyMergedInputRange(callVars);

    return func;
  }

  /**
   * This method creates the function call for the Output Data Transfer (when needed)
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link OutputDataTransfer}.
   * @return The {@link OutputDataTransfer} corresponding to the {@link DAGVertex actor} firing.
   */
  protected OutputDataTransfer generateOutputDataTransferFunctionCall(final DAGVertex dagVertex,
      final Prototype prototype, final boolean isInit) {
    // Creating a new action for DataTransfer
    final OutputDataTransfer func = CodegenModelUserFactory.eINSTANCE.createOutputDataTransfer();
    func.setName(prototype.getFunctionName());
    func.setActorName(dagVertex.getName());
    // Retrieve the Arguments that must correspond to the incoming data fifos
    final Entry<List<Buffer>, List<PortDirection>> callBuffer = generateBufferList(dagVertex, prototype, isInit);
    // Put Buffer in the DataTransfer function call
    for (int idx = 0; idx < callBuffer.getKey().size(); idx++) {
      func.addBuffer(callBuffer.getKey().get(idx), callBuffer.getValue().get(idx));
    }
    return func;
  }

  /**
   * This method creates the function call for the Output Data Transfer (when needed)
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link DataTransferAction}.
   * @return The {@link DataTransferAction} corresponding to the {@link DAGVertex actor} firing.
   */
  protected DataTransferAction generateDataTransferFunctionCall(final DAGVertex dagVertex, final Prototype prototype,
      final boolean isInit) {
    // Creating a new action for DataTransfer
    final DataTransferAction func = CodegenModelUserFactory.eINSTANCE.createDataTransferAction();
    func.setName(prototype.getFunctionName());
    func.setActorName(dagVertex.getName());
    // Retrieve the Arguments that must correspond to the incoming data
    // fifos
    final Entry<List<Buffer>, List<PortDirection>> callBuffer = generateBufferList(dagVertex, prototype, isInit);
    // Put Buffer in the DataTransfer function call
    for (int idx = 0; idx < callBuffer.getKey().size(); idx++) {
      func.addBuffer(callBuffer.getKey().get(idx), callBuffer.getValue().get(idx));
    }
    return func;
  }

  /**
   * This method creates the function call for the Registers set up (when needed)
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected RegisterSetUpAction generateRegisterSetUpFunctionCall(final DAGVertex dagVertex, final Prototype prototype,
      final boolean isInit) {
    // Creating a new action for DataTransfer
    final RegisterSetUpAction func = CodegenModelUserFactory.eINSTANCE.createRegisterSetUpAction();
    func.setName(prototype.getFunctionName());
    func.setActorName(dagVertex.getName());
    // Retrieve the Arguments that must correspond to the incoming data
    // fifos
    final Entry<List<Variable>, List<PortDirection>> callVariable = generateVariableList(dagVertex, prototype, isInit);
    // Put Buffer in the DataTransfer function call
    for (int idx = 0; idx < callVariable.getKey().size(); idx++) {
      func.addParameter(callVariable.getKey().get(idx), callVariable.getValue().get(idx));
    }
    return func;
  }

  /**
   * This method creates the function calls for the FPGA Load. One each accelerator
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FpgaLoadAction generateFpgaLoadFunctionCalls(final DAGVertex dagVertex, final Prototype prototype,
      final boolean isInit) {
    // Creating a new action for FpgaLoad

    /**
     * TODO: in order to load the bitstream just once per function, a check of the already loaded "kernel" should be
     * performed
     */

    final FpgaLoadAction func = CodegenModelUserFactory.eINSTANCE.createFpgaLoadAction();
    func.setName(prototype.getFunctionName());
    return func;
  }

  /**
   * This method creates the function calls for FreeDataTransferBuffer.
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FreeDataTransferBuffer generateFreeDataTransferBuffer(final DAGVertex dagVertex, final Prototype prototype,
      final boolean isInit) {
    // Creating a new action for FreeDataTransferBuffer
    final FreeDataTransferBuffer func = CodegenModelUserFactory.eINSTANCE.createFreeDataTransferBuffer();
    func.setName(prototype.getFunctionName());
    func.setActorName(dagVertex.getName());
    // Retrieve the Arguments
    // fifos
    final Entry<List<Buffer>, List<PortDirection>> callBuffer = generateBufferList(dagVertex, prototype, isInit);
    // Put Buffer in the FreeDataTransferBuffer function call
    for (int idx = 0; idx < callBuffer.getKey().size(); idx++) {
      func.addBuffer(callBuffer.getKey().get(idx), callBuffer.getValue().get(idx));
    }
    return func;
  }

  /**
   * This method creates the function call for the GlobalBufferDeclaration (when needed)
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected GlobalBufferDeclaration generateGlobalBufferDeclaration(final DAGVertex dagVertex,
      final Prototype prototype, final boolean isInit) {
    // Creating a new action for DataTransfer
    final GlobalBufferDeclaration func = CodegenModelUserFactory.eINSTANCE.createGlobalBufferDeclaration();
    func.setName(prototype.getFunctionName());
    func.setActorName(dagVertex.getName());
    // Retrieve the Arguments that must correspond to the incoming data
    // fifos
    final Entry<List<Buffer>, List<PortDirection>> callBuffer = generateBufferList(dagVertex, prototype, isInit);
    // Put Buffer in the DataTransfer function call
    for (int idx = 0; idx < callBuffer.getKey().size(); idx++) {
      func.addBuffer(callBuffer.getKey().get(idx), callBuffer.getValue().get(idx));
    }
    return func;
  }

  /**
   * This method creates the event configure PE function call for PAPIFY instrumentation
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the {@link PapifyFunctionCall} will be added.
   * @param papifyConfig
   *          the {@link PapifyConfig} storing all the instrumentation information.
   * @param papifyPEId
   *          the {@link Constant} identifying the PE that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link CoreBlock operatorBlock} firing.
   */
  protected PapifyFunctionCall generatePapifyConfigurePEFunctionCall(final CoreBlock operatorBlock,
      final PapifyConfig papifyConfig, final Constant papifyPEId) {
    // Create the corresponding FunctionCall
    final PapifyFunctionCall configurePapifyPE = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    configurePapifyPE.setName("configure_papify_PE");
    // Create the variable associated to the PE name
    final ConstantString papifyPEName = CodegenModelUserFactory.eINSTANCE.createConstantString();
    papifyPEName.setValue(operatorBlock.getName());
    // Create the variable associated to the PAPI component
    String componentsSupported = "";
    final ConstantString papifyComponentName = CodegenModelUserFactory.eINSTANCE.createConstantString();
    final Component component = scenario.getDesign().getComponent(operatorBlock.getCoreType());
    for (final PapiComponent papiComponent : papifyConfig.getSupportedPapiComponents(component)) {
      if (componentsSupported.equals("")) {
        componentsSupported = papiComponent.getId();
      } else {
        componentsSupported = componentsSupported.concat(",").concat(papiComponent.getId());
      }
    }
    papifyComponentName.setValue(componentsSupported);
    // Add the function parameters
    configurePapifyPE.addParameter(papifyPEName, PortDirection.INPUT);
    configurePapifyPE.addParameter(papifyComponentName, PortDirection.INPUT);
    configurePapifyPE.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function comment
    configurePapifyPE.setActorName("Papify --> configure papification of ".concat(operatorBlock.getName()));

    // Add type of Papify function
    configurePapifyPE.setPapifyType(PapifyType.CONFIGPE);

    return configurePapifyPE;
  }

  /**
   * This method creates the event configure actor function call for PAPIFY instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param papifyConfig
   *          the {@link PapifyConfig} storing all the instrumentation information.
   * @param papifyActionS
   *          the {@link PapifyAction} identifying the actor that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected PapifyFunctionCall generatePapifyConfigureActorFunctionCall(final DAGVertex dagVertex,
      final PapifyConfig papifyConfig, final PapifyAction papifyActionS) {
    // Create the corresponding FunctionCall
    final PapifyFunctionCall func = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    func.setName("configure_papify_actor");

    // Add the PAPI component name
    final AbstractActor referencePiVertex = dagVertex.getReferencePiVertex();
    final EList<String> compsWithConfig = papifyConfig.getActorAssociatedPapiComponents(referencePiVertex);
    String compNames = "";
    for (final String compName : compsWithConfig) {
      if (compNames.equals("")) {
        compNames = compName;
      } else {
        compNames = compNames.concat(",").concat(compName);
      }
    }
    final ConstantString componentName = CodegenModelUserFactory.eINSTANCE.createConstantString();
    componentName.setName("component_name".concat(dagVertex.getName()));
    componentName.setValue(compNames);
    componentName.setComment("PAPI component name");

    // Add the size of the configs
    final Constant numConfigs = CodegenModelUserFactory.eINSTANCE.createConstant();
    numConfigs.setName("numConfigs");
    numConfigs.setValue(compsWithConfig.size());

    // Add the actor name
    final String actorOriginalIdentifier = papifyConfig.getActorOriginalIdentifier(referencePiVertex);
    final ConstantString actorName = CodegenModelUserFactory.eINSTANCE.createConstantString();
    actorName.setName("actor_name".concat(actorOriginalIdentifier));
    actorName.setValue(actorOriginalIdentifier);
    actorName.setComment("Actor name");

    // Add the PAPI event names
    final EList<PapiEvent> actorEvents = papifyConfig.getActorAssociatedEvents(referencePiVertex);
    String eventNames = "";
    for (final PapiEvent oneEvent : actorEvents) {
      if (eventNames.equals("")) {
        eventNames = oneEvent.getName();
      } else {
        eventNames = eventNames.concat(",").concat(oneEvent.getName());
      }
    }
    final ConstantString eventSetNames = CodegenModelUserFactory.eINSTANCE.createConstantString();
    eventSetNames.setName("allEventNames");
    eventSetNames.setValue(eventNames);
    eventSetNames.setComment("Papify events");

    // Add the size of the CodeSet
    final Constant codeSetSize = CodegenModelUserFactory.eINSTANCE.createConstant();
    codeSetSize.setName("CodeSetSize");
    codeSetSize.setValue(actorEvents.size());

    // Set the id associated to the Papify configuration
    final EList<String> actorSupportedComps = papifyConfig.getActorAssociatedPapiComponents(referencePiVertex);
    String configIds = "";
    for (final String papiComponent : actorSupportedComps) {
      final EList<PapiEvent> oneConfig = papifyConfig.getActorComponentEvents(referencePiVertex, papiComponent);
      boolean found = false;
      int positionConfig = -1;
      for (final EList<PapiEvent> storedConfig : this.configsAdded) {
        if (EcoreUtil.equals(storedConfig, oneConfig)) {
          found = true;
          positionConfig = this.configsAdded.indexOf(storedConfig);
        }
      }
      if (!found) {
        this.configsAdded.add(oneConfig);
        positionConfig = this.configsAdded.indexOf(oneConfig);
      }
      if (configIds.equals("")) {
        configIds = Integer.toString(positionConfig);
      } else {
        configIds = configIds.concat(",").concat(Integer.toString(positionConfig));
      }
    }
    final ConstantString papifyConfigNumber = CodegenModelUserFactory.eINSTANCE.createConstantString();
    papifyConfigNumber.setName("PAPIFY_configs_".concat(dagVertex.getName()));
    papifyConfigNumber.setValue(configIds);
    papifyConfigNumber.setComment("PAPIFY actor configs");

    // Add the function parameters
    func.addParameter(papifyActionS, PortDirection.OUTPUT);
    func.addParameter(componentName, PortDirection.INPUT);
    func.addParameter(actorName, PortDirection.INPUT);
    func.addParameter(codeSetSize, PortDirection.INPUT);
    func.addParameter(eventSetNames, PortDirection.INPUT);
    func.addParameter(papifyConfigNumber, PortDirection.INPUT);
    func.addParameter(numConfigs, PortDirection.INPUT);

    // Add the function comment
    func.setActorName("Papify --> configure papification of ".concat(dagVertex.getName()));

    // Add type of Papify function
    func.setPapifyType(PapifyType.CONFIGACTOR);

    return func;
  }

  /**
   * This method creates the event start function call for PAPIFY instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param papifyPEId
   *          the {@link Constant} identifying the PE that is being configured.
   * @param papifyActionS
   *          the {@link PapifyAction} identifying the actor that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected PapifyFunctionCall generatePapifyStartFunctionCall(final DAGVertex dagVertex, final Constant papifyPEId,
      final PapifyAction papifyActionS) {
    // Create the corresponding FunctionCall
    final PapifyFunctionCall func = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    func.setName("event_start");
    // Add the function parameters
    func.addParameter(papifyActionS, PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);

    // Add the function actor name
    func.setActorName(dagVertex.getName());

    // Add type of Papify function
    func.setPapifyType(PapifyType.EVENTSTART);
    return func;
  }

  /**
   * This method creates the event start Papify timing function call for PAPIFY instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param papifyPEId
   *          the {@link Constant} identifying the PE that is being configured.
   * @param papifyActionS
   *          the {@link PapifyAction} identifying the actor that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected PapifyFunctionCall generatePapifyStartTimingFunctionCall(final DAGVertex dagVertex,
      final Constant papifyPEId, final PapifyAction papifyActionS) {
    // Create the corresponding FunctionCall
    final PapifyFunctionCall func = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    func.setName("event_start_papify_timing");
    func.addParameter(papifyActionS, PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function actor name
    func.setActorName(dagVertex.getName());

    // Add type of Papify function
    func.setPapifyType(PapifyType.TIMINGSTART);
    return func;
  }

  /**
   * This method creates the event stop function call for PAPIFY instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param papifyPEId
   *          the {@link Constant} identifying the PE that is being configured.
   * @param papifyActionS
   *          the {@link PapifyAction} identifying the actor that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected PapifyFunctionCall generatePapifyStopFunctionCall(final DAGVertex dagVertex, final Constant papifyPEId,
      final PapifyAction papifyActionS) {
    // Create the corresponding FunctionCall
    final PapifyFunctionCall func = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    func.setName("event_stop");
    // Add the function parameters
    func.addParameter(papifyActionS, PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);

    // Add the function actor name
    func.setActorName(dagVertex.getName());

    // Add type of Papify function
    func.setPapifyType(PapifyType.EVENTSTOP);
    return func;
  }

  /**
   * This method creates the event stop Papify timing function call for PAPIFY instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param papifyPEId
   *          the {@link Constant} identifying the PE that is being configured.
   * @param papifyActionS
   *          the {@link PapifyAction} identifying the actor that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected PapifyFunctionCall generatePapifyStopTimingFunctionCall(final DAGVertex dagVertex,
      final Constant papifyPEId, final PapifyAction papifyActionS) {
    // Create the corresponding FunctionCall
    final PapifyFunctionCall func = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    func.setName("event_stop_papify_timing");
    // Add the function parameters
    func.addParameter(papifyActionS, PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);

    // Add the function actor name
    func.setActorName(dagVertex.getName());

    // Add type of Papify function
    func.setPapifyType(PapifyType.TIMINGSTOP);
    return func;
  }

  /**
   * This method creates the event write Papify function call for PAPIFY instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param papifyPEId
   *          the {@link Constant} identifying the PE that is being configured.
   * @param papifyActionS
   *          the {@link PapifyAction} identifying the actor that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected PapifyFunctionCall generatePapifyWritingFunctionCall(final DAGVertex dagVertex, final Constant papifyPEId,
      final PapifyAction papifyActionS) {
    // Create the corresponding FunctionCall
    final PapifyFunctionCall func = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    func.setName("event_write_file");
    // Add the function parameters
    func.addParameter(papifyActionS, PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);

    // Add the function actor name
    func.setActorName(dagVertex.getName());

    // Add type of Papify function
    func.setPapifyType(PapifyType.WRITE);
    return func;
  }

  private void addBuffer(final DAGVertex source, final DAGVertex target, final DAGEdge dagEdge, final SpecialCall f) {
    for (final AbstractEdge<?, ?> agg : dagEdge.getAggregate()) {
      final DAGEdge edge = (DAGEdge) agg;
      // For broadcast and round buffers f.getType().equals(SpecialType.BROADCAST) vertices,
      // respectively skip the input and the outputs
      if ((f.getType().equals(SpecialType.BROADCAST) || f.getType().equals(SpecialType.ROUND_BUFFER))
          && (target != null) && target.equals(source)) {
        continue;
      }
      // If neither source nor target is a task then return
      if (!source.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType).equals(VertexType.TASK)
          || !target.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
              .equals(VertexType.TASK)) {
        return;
      }
      // Corresponding edge
      final DAGEdge correspondingEdge = this.algo.getEdge(source, target);
      if (correspondingEdge == null) {
        throw new PreesmRuntimeException("DAGEdge corresponding to srSDFEdge " + edge + " was not found.");
      }

      // Find the corresponding BufferProperty
      final BufferAggregate buffers = correspondingEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);

      // The source and target actor are the same, check that the ports are corrects
      final BufferProperties subBuffProperty = buffers.stream()
          .filter(subBufProp -> edge.getTargetLabel().equals(subBufProp.getDestInputPortID())
              && edge.getSourceLabel().equals(subBufProp.getSourceOutputPortID()))
          .findFirst().orElse(null);

      if (subBuffProperty == null) {
        throw new PreesmRuntimeException("Buffer property with ports " + edge.getTargetLabel() + " and "
            + edge.getSourceLabel() + " was not found in DAGEdge aggregate " + correspondingEdge);
      }

      // Get the corresponding Buffer
      final Buffer firstFound = this.srSDFEdgeBuffers.get(subBuffProperty);
      Buffer buffer = null;
      if (firstFound instanceof final DistributedBuffer distributedBuffer) {

        final String coreBlockName = switch (f.getType()) {
          case FORK, BROADCAST -> source.getPropertyStringValue(OPERATOR_LITERAL);
          default -> target.getPropertyStringValue(OPERATOR_LITERAL);
        };

        final EList<Buffer> repeatedBuffers = distributedBuffer.getDistributedCopies();
        for (final Buffer bufferRepeatedChecker : repeatedBuffers) {
          final SubBuffer subBufferChecker = (SubBuffer) bufferRepeatedChecker;
          final SubBuffer repeatedContainer = (SubBuffer) subBufferChecker.getContainer();
          if (repeatedContainer.getContainer().getName().equals(coreBlockName)) {
            buffer = subBufferChecker;
            break;
          }
        }
      } else {
        buffer = firstFound;
      }

      if (buffer == null) {
        throw new PreesmRuntimeException("Buffer corresponding to DAGEdge" + correspondingEdge + "was not allocated.");
      }
      // Add it to the specialCall
      if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
        f.addOutputBuffer(buffer);
      } else {
        f.addInputBuffer(buffer);
      }
    }
  }

  /**
   * Generate the {@link CodegenPackage Codegen Model} for a "special actor" (fork, join, broadcast or roundbuffer)
   * firing. This method will create an {@link SpecialCall} and place it in the {@link LoopBlock} of the
   * {@link CoreBlock} passed as a parameter.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the special actor firing is performed.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the actor firing.
   */
  protected void generateSpecialCall(final CoreBlock operatorBlock, final DAGVertex dagVertex) {
    final SpecialCall specialCall = CodegenModelUserFactory.eINSTANCE.createSpecialCall();
    specialCall.setName(dagVertex.getName());
    final String vertexType = dagVertex.getPropertyStringValue(AbstractVertex.KIND_LITERAL);

    final SpecialType specialType = switch (vertexType) {
      case MapperDAGVertex.DAG_FORK_VERTEX -> SpecialType.FORK;
      case MapperDAGVertex.DAG_JOIN_VERTEX -> SpecialType.JOIN;
      case MapperDAGVertex.DAG_BROADCAST_VERTEX -> {
        final String specialKind = dagVertex.getPropertyBean().getValue(MapperDAGVertex.SPECIAL_TYPE);
        if (specialKind == null) {
          throw new PreesmRuntimeException("Broadcast DAGVertex " + dagVertex + " has null special type");
        }

        final SpecialType specialBroadcastType = switch (specialKind) {
          case MapperDAGVertex.SPECIAL_TYPE_BROADCAST -> SpecialType.BROADCAST;
          case MapperDAGVertex.SPECIAL_TYPE_ROUNDBUFFER -> SpecialType.ROUND_BUFFER;
          default -> throw new PreesmRuntimeException(
              "Broadcast DAGVertex " + dagVertex + " has an unknown special type: " + specialKind);
        };
        yield specialBroadcastType;
      }
      default -> throw new PreesmRuntimeException("DAGVertex " + dagVertex + " has an unknown type: " + vertexType);
    };
    specialCall.setType(specialType);

    if (specialCall.getType().equals(SpecialType.FORK) || specialCall.getType().equals(SpecialType.BROADCAST)) {
      final Set<DAGEdge> outgoingEdges = dagVertex.outgoingEdges();
      final List<String> sinkOrder = dagVertex.getSinkNameList();
      final List<DAGEdge> orderedList = orderOutEdges(outgoingEdges, sinkOrder);

      orderedList.forEach(edge -> addBuffer(dagVertex, edge.getTarget(), edge, specialCall));
    } else {
      final Set<DAGEdge> incomingEdges = dagVertex.incomingEdges();
      final List<String> sourceOrder = dagVertex.getSourceNameList();
      final List<DAGEdge> orderedList = orderInEdges(incomingEdges, sourceOrder);

      orderedList.forEach(edge -> addBuffer(edge.getSource(), dagVertex, edge, specialCall));
    }

    // Find the last buffer that correspond to the exploded/broadcasted/joined/roundbuffered edge
    DAGEdge lastEdge = null;
    // The vertex may have a maximum of 2 incoming/outgoing edges but only one should be linked to the producer/consumer
    // the other must be linked to a send/receive vertex
    final Set<DAGEdge> candidates = switch (specialCall.getType()) {
      case FORK, BROADCAST -> this.algo.incomingEdgesOf(dagVertex);
      default -> this.algo.outgoingEdgesOf(dagVertex);
    };

    if (candidates.size() > 2) {
      final String direction = switch (specialCall.getType()) {
        case FORK, BROADCAST -> "incoming";
        default -> "outgoing";
      };
      throw new PreesmRuntimeException(specialCall.getType().getName() + " vertex " + dagVertex + " more than 1 "
          + direction + "edge. Check the exported DAG.");
    }

    for (final DAGEdge edge : candidates) {
      if (edge.getSource().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
          .equals(VertexType.TASK)
          && edge.getTarget().getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType)
              .equals(VertexType.TASK)) {
        lastEdge = edge;
      }
    }

    if (lastEdge == null) {
      // This should never happen. It would mean that a "special vertex" does receive data only from send/receive
      // vertices
      throw new PreesmRuntimeException(
          specialCall.getType().getName() + " vertex " + dagVertex + "is not properly connected.");
    }

    final BufferAggregate bufferAggregate = lastEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
    // there should be only one buffer in the aggregate
    final BufferProperties lastBuffProperty = bufferAggregate.get(0);
    final Buffer lastBufferFirstFound = this.srSDFEdgeBuffers.get(lastBuffProperty);
    Buffer lastBuffer = null;
    if (lastBufferFirstFound instanceof final DistributedBuffer distributedBuffer) {
      final String coreBlockName = operatorBlock.getName();
      final EList<Buffer> repeatedBuffers = distributedBuffer.getDistributedCopies();
      for (final Buffer bufferRepeatedChecker : repeatedBuffers) {
        final SubBuffer subBufferChecker = (SubBuffer) bufferRepeatedChecker;
        final SubBuffer repeatedContainer = (SubBuffer) subBufferChecker.getContainer();
        if (repeatedContainer.getContainer().getName().equals(coreBlockName)) {
          lastBuffer = subBufferChecker;
          break;
        }
      }
    } else {
      lastBuffer = lastBufferFirstFound;
    }

    // Add it to the specialCall
    if (specialCall.getType().equals(SpecialType.FORK) || specialCall.getType().equals(SpecialType.BROADCAST)) {
      specialCall.addInputBuffer(lastBuffer);
    } else {
      specialCall.addOutputBuffer(lastBuffer);
    }

    operatorBlock.getLoopBlock().getCodeElts().add(specialCall);
    this.dagVertexCalls.put(dagVertex, specialCall);

    identifyMergedInputRange(new SimpleEntry<>(specialCall.getParameters(), specialCall.getParameterDirections()));
    registerCallVariableToCoreBlock(operatorBlock, specialCall);
  }

  private List<DAGEdge> orderInEdges(final Set<DAGEdge> incomingEdges, final List<String> sourceOrder) {
    if (incomingEdges.size() < 2) {
      return new ArrayList<>(incomingEdges);
    }

    final List<DAGEdge> orderedList = new ArrayList<>();
    for (final String source : sourceOrder) {
      DAGEdge correspondingEdge = null;

      for (final DAGEdge edge : incomingEdges) {
        if (edge instanceof MapperDAGEdge) {
          final String targetLabel = edge.getTargetLabel();
          if (source.equals(targetLabel)) {
            correspondingEdge = edge;
          }
        }

        if (correspondingEdge == null) {
          final BufferAggregate buffAggr = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
          if (buffAggr != null && !buffAggr.isEmpty()) {
            final BufferProperties bufferProperties = buffAggr.get(0);
            final String destInputPortID = bufferProperties.getDestInputPortID();
            if (source.equals(destInputPortID)) {
              correspondingEdge = edge;
            }
          }
        }

        if (correspondingEdge != null) {
          break;
        }
      }
      if (correspondingEdge == null) {
        throw new PreesmRuntimeException("No corresponding edge found for source port " + source);
      }
      orderedList.add(correspondingEdge);
    }
    return orderedList;
  }

  private List<DAGEdge> orderOutEdges(final Set<DAGEdge> outgoingEdges, final List<String> sinkOrder) {
    if (outgoingEdges.size() < 2) {
      return new ArrayList<>(outgoingEdges);
    }

    final List<DAGEdge> orderedList = new ArrayList<>();
    for (final String sink : sinkOrder) {
      DAGEdge correspondingEdge = null;

      for (final DAGEdge edge : outgoingEdges) {
        if (edge instanceof MapperDAGEdge) {
          final String targetLabel = edge.getTargetLabel();
          if (sink.equals(targetLabel)) {
            correspondingEdge = edge;
          }
        }

        if (correspondingEdge == null) {
          final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
          if (bufferAggregate != null && !bufferAggregate.isEmpty()) {
            for (final BufferProperties bufferProperty : bufferAggregate) {
              final String sourceOutputPortID = bufferProperty.getSourceOutputPortID();
              if (sink.equals(sourceOutputPortID)) {
                correspondingEdge = edge;
                break; // Break out of inner loop
              }
            }
          }
        }

        if (correspondingEdge != null) {
          break; // Break out of middle loop
        }
      }
      if (correspondingEdge == null) {
        throw new PreesmRuntimeException("No corresponding edge found for sink port " + sink);
      }
      orderedList.add(correspondingEdge);
    }
    return orderedList;
  }

  /**
   * This method create a {@link SubBuffer} for each {@link DAGEdge} aggregated in the given {@link DAGEdge}.
   * {@link SubBuffer} information are retrieved from the {@link #megs} of the {@link CodegenModelGenerator} . All
   * created {@link SubBuffer} are referenced in the {@link #srSDFEdgeBuffers} map.
   *
   * @param parentBuffer
   *          the {@link Buffer} containing the generated {@link SubBuffer}
   * @param dagEdge
   *          the {@link DAGEdge} whose {@link Buffer} is generated.
   * @param offsetInBit
   *          the of the {@link DAGEdge} in the {@link Buffer}
   * @return the total size of the subbuffers
   *
   */
  protected long generateSubBuffers(final Buffer parentBuffer, final DAGEdge dagEdge) {

    final BufferAggregate buffers = dagEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);

    // Retrieve the corresponding memory object from the MEG
    final MemoryExclusionVertex memObject = findMObject(dagEdge);
    final List<
        Long> interSubbufferSpace = memObject.getPropertyBean().getValue(MemoryExclusionVertex.INTER_BUFFER_SPACES);

    long aggregateOffset = 0;
    int idx = 0;
    for (final BufferProperties subBufferProperties : buffers) {
      Buffer buff = null;
      // If the parent buffer is not null
      final String dataType = subBufferProperties.getDataType();
      if (!(parentBuffer instanceof NullBuffer)) {
        final SubBuffer subBuff = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
        buff = subBuff;
        // Old naming techniques with complete path to port. (too long, kept as a comment)
        final StringBuilder comment = new StringBuilder(dagEdge.getSource().getName());
        comment.append('_' + subBufferProperties.getSourceOutputPortID());
        comment.append(" > " + dagEdge.getTarget().getName());
        comment.append('_' + subBufferProperties.getDestInputPortID());
        subBuff.setComment(comment.toString());

        // Buffer is named only with ports ID
        final String tmpBufferName = subBufferProperties.getSourceOutputPortID() + "__"
            + subBufferProperties.getDestInputPortID();
        final String name = generateUniqueBufferName(tmpBufferName);

        subBuff.setName(name);
        subBuff.reaffectContainer(parentBuffer);
        subBuff.setOffsetInBit(aggregateOffset);
        subBuff.setType(dataType);
        subBuff.setTokenTypeSizeInBit(scenario.getSimulationInfo().getDataTypeSizeInBit(dataType));
        subBuff.setNbToken(subBufferProperties.getNbToken());

        // Save the created SubBuffer
        this.srSDFEdgeBuffers.put(subBufferProperties, subBuff);
      } else {
        // The parent buffer is a null buffer
        final NullBuffer nullBuff = CodegenModelUserFactory.eINSTANCE.createNullBuffer();
        buff = nullBuff;
        // Old naming techniques with complete path to port. (too long, kept as a comment)

        final StringBuilder comment = new StringBuilder("NULL_" + dagEdge.getSource().getName());
        comment.append('_' + subBufferProperties.getSourceOutputPortID());
        comment.append(" > " + dagEdge.getTarget().getName());
        comment.append('_' + subBufferProperties.getDestInputPortID());
        nullBuff.setComment(comment.toString());

        nullBuff.reaffectContainer(parentBuffer);

        // Save the created SubBuffer
        this.srSDFEdgeBuffers.put(subBufferProperties, nullBuff);
      }

      // If an interSubbufferSpace was defined, add it
      if (interSubbufferSpace != null) {
        aggregateOffset += interSubbufferSpace.get(idx);
      }
      idx++;

      // Increment the aggregate offset with the size of the current subBuffer multiplied by the size of the datatype
      if (dataType.equals("typeNotFound")) {
        throw new PreesmRuntimeException("There is a problem with datatypes.\n"
            + "Please make sure that all data types are defined in the Simulation tab of the scenario editor.");
      }

      FifoTypeChecker.checkMissingFifoTypeSizes(this.scenario);

      buff.setTokenTypeSizeInBit(this.scenario.getSimulationInfo().getDataTypeSizeInBit(dataType));
      aggregateOffset += buff.getSizeInBit();
    }

    return aggregateOffset;
  }

  /**
   * This method create a {@link DistributedBuffer} for each {@link SubBuffer}. {@link SubBuffer} information are
   * retrieved from the {@link #megs} of the {@link CodegenModelGenerator} . All created {@link SubBuffer} are
   * referenced in the {@link #srSDFEdgeBuffers} map.
   *
   * @param parentBuffer
   *          the {@link Buffer} containing the generated {@link SubBuffer}
   * @param dagEdge
   *          the {@link DAGEdge} whose {@link Buffer} is generated.
   * @return the total size of the subbuffers
   *
   */
  protected long generateTwinSubBuffers(final Buffer repeatedParentBuffer, final DAGEdge dagEdge) {

    /*
     * Backup original
     */
    final Map<BufferProperties, Buffer> backUpOriginal = new LinkedHashMap<>();

    final BufferAggregate buffers = dagEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
    for (final BufferProperties subBufferProperties : buffers) {
      final Buffer buff = this.srSDFEdgeBuffers.get(subBufferProperties);
      backUpOriginal.put(subBufferProperties, buff);
    }
    /*
     * Generate subbuffers for the distributed buffer
     */
    final long aggregateOffset = generateSubBuffers(repeatedParentBuffer, dagEdge);

    /*
     * Get new buffers
     */
    final Map<BufferProperties, Buffer> newBuffers = new LinkedHashMap<>();
    for (final BufferProperties subBufferProperties : buffers) {
      final Buffer buff = this.srSDFEdgeBuffers.get(subBufferProperties);
      newBuffers.put(subBufferProperties, buff);
    }
    /*
     * Generate and store repeated buffers
     */
    for (final BufferProperties subBufferProperties : buffers) {
      final DistributedBuffer duplicatedBuffer = generateDistributedBuffer(backUpOriginal.get(subBufferProperties),
          newBuffers.get(subBufferProperties));
      this.srSDFEdgeBuffers.put(subBufferProperties, duplicatedBuffer);
    }
    return aggregateOffset;
  }

  /**
   * This method create a {@link DistributedBuffer} aggregated in the given original {@link Buffer}.
   *
   * @param originalBuffer
   *          the {@link Buffer} containing the originalBuffer
   * @param repeatedBuffer
   *          the {@link Buffer} repeatedBuffer
   * @return the distributed buffer
   *
   */
  protected DistributedBuffer generateDistributedBuffer(final Buffer originalBuffer, final Buffer repeatedBuffer) {

    final DistributedBuffer duplicatedBuffer = CodegenModelUserFactory.eINSTANCE.createDistributedBuffer();
    if (originalBuffer instanceof final DistributedBuffer distributedBuffer) {
      duplicatedBuffer.getDistributedCopies().addAll(distributedBuffer.getDistributedCopies());
    } else {
      duplicatedBuffer.getDistributedCopies().add(originalBuffer);
    }
    duplicatedBuffer.getDistributedCopies().add(repeatedBuffer);
    return duplicatedBuffer;
  }

  /**
   * Using the {@link #bufferNames} map, this methods gives a new unique {@link Buffer} name beginning with the string
   * passed as a parameter. Names that are longer than 28 characters will be shortened to this length..
   *
   * @param name
   *          the buffer name
   * @return a unique name for the buffer
   */
  protected String generateUniqueBufferName(final String name) {
    int idx;
    String key = name;
    if (key.length() > 58) {
      key = key.substring(0, 58);
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

  /**
   * Retrieve the {@link ActorPrototypes prototypes} defined in the IDL {@link CodeRefinement refinement} of the
   * {@link SDFVertex} passed as a parameter.
   *
   * @param dagVertex
   *          the {@link SDFVertex} whose IDL refinement is parsed to retrieve the corresponding {@link ActorPrototypes}
   * @return the parsed {@link ActorPrototypes}.
   */
  protected ActorPrototypes getActorPrototypes(final DAGVertex dagVertex) {
    final Object refinement = dagVertex.getRefinement();

    // Check that it has an IDL refinement.
    if (!(refinement instanceof final CodeRefinement codeRefinement)
        || (codeRefinement.getLanguage() != Language.IDL)) {
      throw new PreesmRuntimeException("generateFunctionCall was called with a DAG Vertex withoud IDL");
    }

    // Retrieve the IDL File
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();

    final IPath path = ((CodeRefinement) refinement).getPath();
    final IFile idlFile = root.getFile(path);

    // Retrieve the ActorPrototype
    if (idlFile != null) {
      final IPath rawPath = idlFile.getRawLocation();
      final String rawLocation = rawPath.toOSString();
      return IDLPrototypeFactory.INSTANCE.create(rawLocation);
    }
    throw new NullPointerException();
  }

  /**
   * The purpose of this method is to identify {@link Range} of input {@link Buffer} that are allocated in a memory
   * space overlapping with a {@link Range} of output {@link Buffer}. Information on overlapping {@link Range} is saved
   * in the {@link Buffer#getMergedRange() mergedRange} of the input {@link Buffer}. This information will be used for
   * cache coherence purpose during code generation.
   *
   * @param callVars
   *          {@link Entry} containing a {@link List} of call {@link Variable} of a function associated to a
   *          {@link List} of their {@link PortDirection}.
   */
  protected void identifyMergedInputRange(final Entry<List<Variable>, List<PortDirection>> callVars) {

    // Separate input and output buffers
    final List<Buffer> inputs = new ArrayList<>();
    final List<Buffer> outputs = new ArrayList<>();

    for (int i = 0; i < callVars.getKey().size(); i++) {
      if (callVars.getValue().get(i) == PortDirection.INPUT) {
        inputs.add((Buffer) callVars.getKey().get(i));
      } else if (callVars.getValue().get(i) == PortDirection.OUTPUT) {
        outputs.add((Buffer) callVars.getKey().get(i));
      }
    }

    // For each output find the allocated range (or Ranges in case of a divided buffer)
    final List<Pair<Buffer, Range>> outputRanges = new ArrayList<>();
    for (final Buffer output : outputs) {
      // If the input is not a NullBufer
      if (!(output instanceof final NullBuffer nullBuffer)) {
        // Find the parent Buffer container b and the offset within b.
        int start = 0;
        Buffer b = output;
        while (b instanceof final SubBuffer subBuffer) {
          start += subBuffer.getOffsetInBit();
          b = subBuffer.getContainer();
        }
        final long end = start + (output.getNbToken() * output.getTokenTypeSizeInBit());

        // Save allocated range
        outputRanges.add(new Pair<>(b, new Range(start, end)));
      } else {
        // The output is a NullBuffer (i.e. it is divided)
        // Find the allocation of its ranges
        final DAGEdge dagEdge = this.dagEdgeBuffers.inverse().get(nullBuffer.getContainer());

        // Get the real ranges from the memObject
        final MemoryExclusionVertex mObject = findMObject(dagEdge);

        final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realRanges = mObject.getPropertyBean()
            .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);

        // Find the actual allocation range of each real range.
        for (final Pair<MemoryExclusionVertex, Pair<Range, Range>> realRange : realRanges) {
          final DAGEdge hostDagEdge = realRange.getKey().getEdge();
          final DAGVertex originalSource = this.algo.getVertex(hostDagEdge.getSource().getName());
          final DAGVertex originalTarget = this.algo.getVertex(hostDagEdge.getTarget().getName());
          final DAGEdge originalDagEdge = this.algo.getEdge(originalSource, originalTarget);
          final Buffer hostBuffer = this.dagEdgeBuffers.get(originalDagEdge);
          // Get the allocated range
          long start = realRange.getValue().getValue().getStart();
          Buffer b = hostBuffer;
          while (b instanceof final SubBuffer subBuffer) {
            start += subBuffer.getOffsetInBit();
            b = subBuffer.getContainer();
          }
          final long end = start + realRange.getValue().getValue().getLength();
          // Save allocated range
          outputRanges.add(new Pair<>(b, new Range(start, end)));
        }
      }
    }

    // Find if an inputBuffer has an overlap with an outputRange
    // For each input find the allocated range
    for (final Buffer input : inputs) {

      if ((input instanceof NullBuffer)) {
        continue;
      }

      // If the input is not a NullBufer
      // Find the parent Buffer container b
      // and the offset within b.
      int start = 0;
      Buffer b = input;
      while (b instanceof final SubBuffer subBuffer) {
        start += subBuffer.getOffsetInBit();
        b = subBuffer.getContainer();
      }
      final long end = start + (input.getNbToken() * input.getTokenTypeSizeInBit());

      // Find the input range that are also covered by the output
      // ranges
      List<Range> inRanges = new ArrayList<>();
      inRanges.add(new Range(start, end));

      // Check output ranges one by one
      for (final Pair<Buffer, Range> outputRange : outputRanges) {
        if (outputRange.getKey() == b) {
          inRanges = Range.difference(inRanges, outputRange.getValue());
        }
      }
      List<Range> mergedRanges = new ArrayList<>();
      mergedRanges.add(new Range(start, end));
      mergedRanges = Range.difference(mergedRanges, inRanges);

      // Save only if a part of the input buffer is merged
      if (!mergedRanges.isEmpty()) {
        Range.translate(mergedRanges, -start);
        input.setMergedRange(new BasicEList<>(mergedRanges));
      }

    }

  }

  /**
   * Insert the {@link Communication} calls in the {@link LoopBlock} of the given {@link CoreBlock}.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} on which the communication is executed.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the given {@link Communication}.
   * @param newComm
   *          the {@link Communication} {@link Call} to insert.
   *
   */
  protected void insertCommunication(final CoreBlock operatorBlock, final DAGVertex dagVertex,
      final Communication newComm) {

    // Do this only for SS and RE
    if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
        || (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.RECEIVE))) {

      // Do the insertion
      operatorBlock.getLoopBlock().getCodeElts().add(newComm);
      this.dagVertexCalls.put(dagVertex, newComm);

    } else {
      // Code reached for RS, SE, RR and SR
      // Retrieve the corresponding ReceiveEnd or SendStart
      final Call zoneReference = switch (newComm.getDirection()) {
        case SEND -> newComm.getSendStart();
        case RECEIVE -> newComm.getReceiveEnd();
        default -> null;
      };

      // Get the index for the zone complement
      final int index = operatorBlock.getLoopBlock().getCodeElts().indexOf(zoneReference);

      // For SE and RS
      if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.RECEIVE))
          || (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.SEND))) {

        // DO the insertion
        if (newComm.getDelimiter().equals(Delimiter.START)) {
          // Insert the RS before the RE
          operatorBlock.getLoopBlock().getCodeElts().add(index, newComm);
        } else {
          // Insert the SE after the SS
          operatorBlock.getLoopBlock().getCodeElts().add(index + 1, newComm);
        }
        // DO NOT save the SE and RS in the dagVertexCall.
      }
    }
  }

  /**
   * {@link Buffer#reaffectCreator(Block) Set the creator} of the given {@link Buffer} to the given {@link CoreBlock},
   * and recursively iterate over the {@link Buffer#getChildrens() children} {@link SubBuffer} of this {@link Buffer} to
   * set the same {@link Buffer#reaffectCreator(Block) creator} for them.
   *
   * @param buffer
   *          The {@link Buffer} whose creator is to be set.
   * @param correspondingOperatorBlock
   *          The creator {@link Block}.
   * @param isLocal
   *          boolean used to set the {@link Buffer#isLocal()} property of all {@link Buffer}
   */
  private void recursiveSetBufferCreator(final Buffer buffer, final CoreBlock correspondingOperatorBlock,
      final boolean isLocal) {
    // Set the creator for the current buffer
    buffer.reaffectCreator(correspondingOperatorBlock);
    buffer.setLocal(isLocal);
    // Do the same recursively for all its children subbuffers
    buffer.getChildrens().forEach(sb -> recursiveSetBufferCreator(sb, correspondingOperatorBlock, isLocal));
  }

  /**
   * Register the {@link Variable} used by the {@link Call} as used by the {@link CoreBlock} passed as a parameter.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} that is a user of the variables.
   * @param call
   *          the {@link Call} whose {@link Variable variables} are registered
   */
  protected void registerCallVariableToCoreBlock(final CoreBlock operatorBlock, final Call call) {
    // Register the core Block as a user of the function variable
    for (final Variable variable : call.getParameters()) {
      // Currently, constants do not need to be declared nor have creator since their value is directly used.
      // Consequently the used block can also be declared as the creator
      if (variable instanceof Constant) {
        variable.reaffectCreator(operatorBlock);
      }
      variable.getUsers().add(operatorBlock);
    }
  }

  /**
   * This method find the {@link Communication communications} associated to the {@link Communication} passed as a
   * parameter. Communication are associated if they are involved in the communication of the same buffer but with
   * different {@link Direction} and {@link Delimiter}. The {@link Communication#getSendStart()},
   * {@link Communication#getSendEnd()}, {@link Communication#getSendReserve()},
   * {@link Communication#getReceiveStart()}, {@link Communication#getReceiveEnd()} and
   * {@link Communication#getReceiveRelease()} attributes are updated by this method.<br>
   * <br>
   * The methods also associates a common {@link Communication#getId() Id} to all associated communications.
   *
   * @param newCommmunication
   *          The {@link Communication} to register.
   * @param dagEdge
   *          The {@link DAGEdge} associated to the communication.
   * @param dagVertex
   *          the {@link DAGVertex} (Send or Receive) at the origin of the newCommunication creation.
   */
  protected void registerCommunication(final Communication newCommmunication, final DAGEdge dagEdge,
      final DAGVertex dagVertex) {
    // Retrieve the routeStep corresponding to the vertex. In case of multi-step communication, this is the easiest way
    // to retrieve the target and source of the communication corresponding to the current Send/ReceiveVertex
    final SlamMessageRouteStep routeStep = dagVertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.SendReceive_routeStep);

    String commID = routeStep.getSender().getInstanceName();
    commID += "__" + dagEdge.getSource().getName();
    commID += "___" + routeStep.getReceiver().getInstanceName();
    commID += "__" + dagEdge.getTarget().getName();
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
        switch (com.getDelimiter()) {
          case START -> newCommmunication.setSendStart(com);
          case END -> newCommmunication.setSendEnd(com);
          default -> {
            // Empty clause imposed by checkstyle
          }
        }

      } else { // RECEIVE
        switch (com.getDelimiter()) {
          case START -> newCommmunication.setReceiveStart(com);
          case END -> newCommmunication.setReceiveEnd(com);
          default -> {
            // Empty clause imposed by checkstyle
          }
        }
      }
    }

    // Register new comm to its co-workers
    associatedCommunications.add(newCommmunication);
    for (final Communication com : associatedCommunications) {
      if (newCommmunication.getDirection().equals(Direction.SEND)) {
        switch (newCommmunication.getDelimiter()) {
          case START -> com.setSendStart(newCommmunication);
          case END -> com.setSendEnd(newCommmunication);
          default -> {
            // Empty clause imposed by checkstyle
          }
        }
      } else { // Direction.RECEIVE
        switch (newCommmunication.getDelimiter()) {
          case START -> com.setReceiveStart(newCommmunication);
          case END -> com.setReceiveEnd(newCommmunication);
          default -> {
            // Empty clause imposed by checkstyle
          }
        }
      }
    }
  }

  /**
   * The purpose of this function is to restore to their original size the {@link MemoryExclusionVertex} that were
   * merged when applying memory scripts.
   */
  protected void restoreHostedVertices() {
    for (final MemoryExclusionGraph meg : this.megs.values()) {
      final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostBuffers = meg.getPropertyBean()
          .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
      if (hostBuffers != null) {
        for (final Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : hostBuffers.entrySet()) {
          // Since host vertices are naturally aligned, no need to restore them

          // Restore the real size of hosted vertices
          final Set<MemoryExclusionVertex> vertices = entry.getValue();

          for (final MemoryExclusionVertex vertex : vertices) {
            // For non-divided vertices
            if (vertex.getWeight() == 0) {
              continue;
            }
            final long emptySpace = vertex.getPropertyBean().getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE);

            // Put the vertex back to its real size
            vertex.setWeight(vertex.getWeight() - emptySpace);

            // And set the allocated offset
            final long allocatedOffset = vertex.getPropertyBean()
                .getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);

            vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, allocatedOffset + emptySpace);
            final Map<DAGEdge,
                Long> dagEdgeAllocation = meg.getPropertyBean().getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);
            dagEdgeAllocation.put(vertex.getEdge(), allocatedOffset + emptySpace);

          }
        }
      }
    }
  }
}
