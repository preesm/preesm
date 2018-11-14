/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2016 - 2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2018)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2013)
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
package org.preesm.codegen.xtend.task;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.xbase.lib.Pair;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.CodeRefinement.Language;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.IDLPrototypeFactory;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.experiment.model.pimm.PersistenceLevel;
import org.ietr.preesm.mapper.ScheduledDAGIterator;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.allocation.MemoryAllocator;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.ietr.preesm.memory.script.Range;
import org.preesm.codegen.CodegenException;
import org.preesm.codegen.model.ActorBlock;
import org.preesm.codegen.model.ActorCall;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.CodegenFactory;
import org.preesm.codegen.model.CodegenPackage;
import org.preesm.codegen.model.Communication;
import org.preesm.codegen.model.CommunicationNode;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.ConstantString;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.Delimiter;
import org.preesm.codegen.model.Direction;
import org.preesm.codegen.model.FifoCall;
import org.preesm.codegen.model.FifoOperation;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.NullBuffer;
import org.preesm.codegen.model.PapifyAction;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.SharedMemoryCommunication;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.logger.PreesmLogger;

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
public class CodegenModelGenerator {

  private static final String PAPIFY_PE_ID_CONSTANT_NAME = "PE_id";

  private static final String ERROR_PATTERN_1 = "MemEx graph memory object (%s) refers to a DAG Vertex %s that does "
      + "not exist in the input DAG.\n" + "Make sure that the MemEx is derived from the input DAG of the codegen.";

  /** Targeted {@link Design Architecture} of the code generation. */
  private final Design archi;

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
   * {@link DirectedAcyclicGraph DAG} used to generate code. This {@link DirectedAcyclicGraph DAG} must be the result of
   * mapping/scheduling process.
   */
  private final MapperDAG algo;

  /**
   * {@link Map} of {@link String} and {@link MemoryExclusionGraph MEG} used to generate code. These
   * {@link MemoryExclusionGraph MemEx MEGs} must be the result of an allocation process. Each {@link String}
   * corresponds to a memory bank where the associated MEG is allocated.
   *
   * @see MemoryAllocator
   */
  private final Map<String, MemoryExclusionGraph> megs;

  /**
   * {@link PreesmScenario Scenario} at the origin of the call to the {@link AbstractCodegenPrinter Code Generator}.
   */
  private final PreesmScenario scenario;

  /** The workflow. */
  private final Workflow workflow;

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

  /**
   * This {@link Map} associates a dag hierarchical vertex to the internal allocated working memory.
   */
  private final Map<DAGVertex, Buffer> linkHSDFVertexBuffer;

  /**
   * This {@link List} stores the PEs that has been already configured for Papify usage.
   */
  protected final List<String> papifiedPEs;

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
   * @param workflow
   *          the workflow
   * @throws CodegenException
   *           When one of the previous verification fails.
   */
  public CodegenModelGenerator(final Design archi, final MapperDAG algo, final Map<String, MemoryExclusionGraph> megs,
      final PreesmScenario scenario, final Workflow workflow) {
    this.archi = archi;
    this.algo = algo;
    this.megs = megs;
    this.scenario = scenario;
    this.workflow = workflow;

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
    this.linkHSDFVertexBuffer = new LinkedHashMap<>();
    this.papifiedPEs = new ArrayList<>();
  }

  public final Design getArchi() {
    return this.archi;
  }

  public final MapperDAG getAlgo() {
    return this.algo;
  }

  public final Map<String, MemoryExclusionGraph> getMegs() {
    return this.megs;
  }

  public final PreesmScenario getScenario() {
    return this.scenario;
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
   * @throws CodegenException
   *           When one of the previous verification fails.
   */
  protected void checkInputs(final Design archi, final DirectedAcyclicGraph dag,
      final Map<String, MemoryExclusionGraph> megs) {
    // Check that the input DAG is scheduled and Mapped on the targeted
    // architecture
    for (final DAGVertex vertex : dag.vertexSet()) {
      final ComponentInstance operator = (ComponentInstance) vertex.getPropertyBean().getValue("Operator");
      if (operator == null) {
        final String msg = "The DAG Actor " + vertex + " is not mapped on any operator.\n"
            + " All actors must be mapped before using the code generation.";
        throw new CodegenException(msg);
      }

      if (!archi.getComponentInstances().contains(operator)) {
        final String msg = "The DAG Actor " + vertex + " is not mapped on an operator " + operator
            + " that does not belong to the ipnut architecture.";
        throw new CodegenException(msg);
      }
    }

    for (final MemoryExclusionGraph meg : megs.values()) {
      for (final MemoryExclusionVertex memObj : meg.vertexSet()) {
        // Check that the MemEx is derived from the Input DAG
        String sourceName = memObj.getSource();
        final String sinkName = memObj.getSink();

        // If the MObject is a part of a divide buffer
        sourceName = sourceName.replaceFirst("^part[0-9]+_", "");

        final boolean isFifo = sourceName.startsWith("FIFO");
        if (isFifo) {
          sourceName = sourceName.substring(10, sourceName.length());
        }

        final DAGVertex sourceVertex = dag.getVertex(sourceName);
        final DAGVertex sinkVertex = dag.getVertex(sinkName);

        // Check that vertices exist
        final boolean sourceVertexIsNull = sourceVertex == null;
        final boolean sinkVertexIsNull = sinkVertex == null;
        if (sourceVertexIsNull) {
          throw new CodegenException(
              String.format(CodegenModelGenerator.ERROR_PATTERN_1, memObj.toString(), sourceName));
        }
        if (sinkVertexIsNull) {
          throw new CodegenException(String.format(CodegenModelGenerator.ERROR_PATTERN_1, memObj.toString(), sinkName));
        }

        // Check that the edge is part of the memeExGraph
        final boolean sinkAndSourceNamesDiffer = !sinkName.equals(sourceName);
        final boolean memObjectIsAnEdge = sinkAndSourceNamesDiffer && !isFifo;
        final boolean memExGraphContainsEdge = dag.containsEdge(sourceVertex, sinkVertex);
        if (memObjectIsAnEdge && !memExGraphContainsEdge) {
          throw new CodegenException("MemEx graph memory object (" + memObj + ") refers to a DAG Edge"
              + " that does not exist in the input DAG.\n"
              + "Make sure that the MemEx is derived from the input DAG of the codegen.");
        }

        // Check that the MemEx graph is allocated.
        final Long offset = (Long) memObj.getPropertyBean().getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);
        if (offset == null) {
          throw new CodegenException("MemEx graph memory object (" + memObj + ") was not allocated in memory. \n"
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
   * @throws CodegenException
   *           throws an exception if the {@link MemoryExclusionVertex} associated to a {@link DAGEdge} could not be
   *           found in any {@link #megs}.
   */
  protected MemoryExclusionVertex findMObject(final DAGEdge dagEdge) {
    MemoryExclusionVertex mObject = null;
    // Find the associated memory object
    for (final MemoryExclusionGraph meg : this.megs.values()) {
      mObject = meg.getVertex(new MemoryExclusionVertex(dagEdge));
      if (mObject != null) {
        break;
      }
    }
    if (mObject == null) {
      throw new CodegenException(
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
   * @throws CodegenException
   *           If a vertex has an unknown {@link DAGVertex#getKind() Kind}.
   */
  public List<Block> generate() {
    final List<Block> resultList;
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

    // 1 - iterate over dag vertices in SCHEDULING Order !
    final ScheduledDAGIterator scheduledDAGIterator = new ScheduledDAGIterator(algo);
    scheduledDAGIterator.forEachRemaining(vert -> {

      // 1.0 - Identify the core used.
      ComponentInstance operator = null;
      CoreBlock operatorBlock = null;
      // This call can not fail as checks were already performed in
      // the constructor
      operator = (ComponentInstance) vert.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_Operator);
      // If this is the first time this operator is encountered,
      // Create a Block and store it.
      operatorBlock = this.coreBlocks.get(operator);
      if (operatorBlock == null) {
        operatorBlock = CodegenModelUserFactory.createCoreBlock();
        operatorBlock.setName(operator.getInstanceName());
        operatorBlock.setCoreType(operator.getComponent().getVlnv().getName());
        this.coreBlocks.put(operator, operatorBlock);
      }

      // 1.1 - Construct the "loop" of each core.
      final String vertexType = ((VertexType) vert.getPropertyBean()
          .getValue(ImplementationPropertyNames.Vertex_vertexType)).toString();
      switch (vertexType) {

        case VertexType.TYPE_TASK:
          // May be an actor (Hierarchical or not) call
          // or a Fork Join call
          final String vertKind = vert.getPropertyBean().getValue(AbstractVertex.KIND_LITERAL).toString();
          switch (vertKind) {
            case DAGVertex.DAG_VERTEX:
              generateActorFiring(operatorBlock, vert);
              break;
            case DAGForkVertex.DAG_FORK_VERTEX:
            case DAGJoinVertex.DAG_JOIN_VERTEX:
            case DAGBroadcastVertex.DAG_BROADCAST_VERTEX:
              generateSpecialCall(operatorBlock, vert);
              break;
            case DAGInitVertex.DAG_INIT_VERTEX:
            case DAGEndVertex.DAG_END_VERTEX:
              generateFifoCall(operatorBlock, vert);
              break;
            default:
              final String message = "DAG Vertex " + vert + " has an unknown kind: " + vertKind;
              throw new CodegenException(message);
          }
          break;

        case VertexType.TYPE_SEND:
          generateCommunication(operatorBlock, vert, VertexType.TYPE_SEND);
          break;

        case VertexType.TYPE_RECEIVE:
          generateCommunication(operatorBlock, vert, VertexType.TYPE_RECEIVE);
          break;
        default:
          throw new CodegenException("Vertex " + vert + " has an unknown type: " + vert.getKind());
      }
    });

    // 2 - Set codeBlockI ID
    // This objective is to give a unique ID to each coreBlock.
    // Alphabetical order of coreBlock name is used to determine the id (in an attempt to limit randomness)

    final Comparator<CoreBlock> c = new Comparator<CoreBlock>() {
      public int compare(CoreBlock cb1, CoreBlock cb2) {
        final String o1 = cb1.getName();
        final String o2 = cb2.getName();

        final String o1StringPart = o1.replaceAll("\\d", "");
        final String o2StringPart = o2.replaceAll("\\d", "");

        if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
          return extractInt(o1) - extractInt(o2);
        } else {
          return o1.compareTo(o2);
        }
      }

      int extractInt(String s) {
        String num = s.replaceAll("\\D", "");
        // return 0 if no digits found
        return num.isEmpty() ? 0 : Integer.parseInt(num);
      }
    };

    // Need this because non-final argument cannot be used within lambda expressions.
    final AtomicInteger id = new AtomicInteger(0);

    resultList = new ArrayList<>(this.coreBlocks.size());
    this.coreBlocks.values().stream().sorted(c).forEach(cb -> {
      cb.setCoreID(id.getAndIncrement());
      resultList.add(cb);
    });

    // 3 - Put the buffer definition in their right place
    generateBufferDefinitions();

    return Collections.unmodifiableList(resultList);
  }

  private void p(final String s) {
    final Logger logger = PreesmLogger.getLogger();
    logger.log(Level.INFO, s);
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
   * @throws CodegenException
   *           Exception is thrown if:
   *           <ul>
   *           <li>An unflattened hierarchical {@link SDFVertex actor} is encountered an actor without refinement</li>
   *           </ul>
   *
   */
  protected void generateActorFiring(final CoreBlock operatorBlock, final DAGVertex dagVertex) {
    // Check whether the ActorCall is a call to a hierarchical actor or not.
    final Object refinement = dagVertex.getRefinement();

    // If the actor is hierarchical
    if (refinement instanceof AbstractGraph) {
      // try to generate for loop on a hierarchical actor
      p("tryGenerateRepeatActorFiring " + dagVertex.getName());
      try {
        final CodegenHierarchicalModelGenerator h = new CodegenHierarchicalModelGenerator(this.scenario, this.algo,
            this.linkHSDFVertexBuffer, this.srSDFEdgeBuffers, this.dagVertexCalls);
        if (h.execute(operatorBlock, dagVertex) == 0) {
          p("Hierarchical actor " + dagVertex.getName() + " generation Successed");
        } else {
          p("Hierarchical actor " + dagVertex.getName() + " printing Failed");
          throw new CodegenException("Unflattened hierarchical actors (" + dagVertex
              + ") are not yet supported by the Xtend Code Generation.\n"
              + "Flatten the graph completely before using this code-generation.");
        }
      } catch (final SDF4JException e) {
        throw new WorkflowException("Codegen for " + dagVertex.getName() + "failed.", e);
      }
    } else {
      ActorPrototypes prototypes = null;
      // If the actor has an IDL refinement
      if ((refinement instanceof CodeRefinement) && (((CodeRefinement) refinement).getLanguage() == Language.IDL)) {
        // Retrieve the prototypes associated to the actor
        prototypes = getActorPrototypes(dagVertex);
      } else if (refinement instanceof ActorPrototypes) {
        // Or if we already extracted prototypes from a .h refinement
        prototypes = (ActorPrototypes) refinement;
      }

      if (prototypes != null) {
        // Generate the loop functionCall
        final Prototype loopPrototype = prototypes.getLoopPrototype();
        if (loopPrototype == null) {
          throw new CodegenException("Actor " + dagVertex + " has no loop interface in its IDL refinement.");
        }
        final FunctionCall functionCall = generateFunctionCall(dagVertex, loopPrototype, false);

        // Check for papify in the dagVertex
        String papifying = (String) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_CONFIGURATION);
        // In case there is any monitoring add start functions
        if (papifying != null && papifying.equals("Papifying")) {
          // Add the function to configure the monitoring in this PE (operatorBlock)
          if (!(this.papifiedPEs.contains(operatorBlock.getName()))) {
            this.papifiedPEs.add(operatorBlock.getName());
            final FunctionCall functionCallPapifyConfigurePE = generatePapifyConfigurePEFunctionCall(operatorBlock);
            operatorBlock.getInitBlock().getCodeElts().add(functionCallPapifyConfigurePE);
          }
          // Add the papify_action_s variable to the code
          Buffer papifyActionS = CodegenFactory.eINSTANCE.createBuffer();
          papifyActionS.setName(
              ((PapifyAction) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTION_NAME)).getName());
          papifyActionS.setSize(1);
          papifyActionS.setType("papify_action_s");
          papifyActionS.setComment("papify configuration variable");
          operatorBlock.getDefinitions().add(papifyActionS);
          // Add the function to configure the monitoring of this actor (dagVertex)
          final FunctionCall functionCallPapifyConfigureActor = generatePapifyConfigureActorFunctionCall(dagVertex);
          operatorBlock.getInitBlock().getCodeElts().add(functionCallPapifyConfigureActor);

          // Check for papify in the dagVertex
          String papifyMonitoringEvents = (String) dagVertex.getPropertyBean()
              .getValue(PapifyEngine.PAPIFY_MONITOR_EVENTS);
          if (papifyMonitoringEvents != null && papifyMonitoringEvents.equals("Yes")) {
            // Generate Papify start function for events
            final FunctionCall functionCallPapifyStart = generatePapifyStartFunctionCall(dagVertex, operatorBlock);
            // Add the Papify start function for events to the loop
            operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyStart);
          }
          String papifyMonitoringTiming = (String) dagVertex.getPropertyBean()
              .getValue(PapifyEngine.PAPIFY_MONITOR_TIMING);
          if (papifyMonitoringTiming != null && papifyMonitoringTiming.equals("Yes")) {
            // Generate Papify start timing function
            final FunctionCall functionCallPapifyTimingStart = generatePapifyStartTimingFunctionCall(dagVertex,
                operatorBlock);
            // Add the Papify start timing function to the loop
            operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyTimingStart);
          }
        }

        registerCallVariableToCoreBlock(operatorBlock, functionCall);
        // Add the function call to the operatorBlock
        operatorBlock.getLoopBlock().getCodeElts().add(functionCall);

        // In case there is any monitoring add stop functions
        if (papifying != null && papifying.equals("Papifying")) {
          String papifyMonitoringTiming = (String) dagVertex.getPropertyBean()
              .getValue(PapifyEngine.PAPIFY_MONITOR_TIMING);
          if (papifyMonitoringTiming != null && papifyMonitoringTiming.equals("Yes")) {
            // Generate Papify stop timing function
            final FunctionCall functionCallPapifyTimingStop = generatePapifyStopTimingFunctionCall(dagVertex,
                operatorBlock);
            // Add the Papify stop timing function to the loop
            operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyTimingStop);
          }
          String papifyMonitoringEvents = (String) dagVertex.getPropertyBean()
              .getValue(PapifyEngine.PAPIFY_MONITOR_EVENTS);
          if (papifyMonitoringEvents != null && papifyMonitoringEvents.equals("Yes")) {
            // Generate Papify stop function for events
            final FunctionCall functionCallPapifyStop = generatePapifyStopFunctionCall(dagVertex, operatorBlock);
            // Add the Papify stop function for events to the loop
            operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyStop);
          }
          // Generate Papify writing function
          final FunctionCall functionCallPapifyWriting = generatePapifyWritingFunctionCall(dagVertex, operatorBlock);
          // Add the Papify writing function to the loop
          operatorBlock.getLoopBlock().getCodeElts().add(functionCallPapifyWriting);
        }

        // Save the functionCall in the dagvertexFunctionCall Map
        this.dagVertexCalls.put(dagVertex, functionCall);

        // Generate the init FunctionCall (if any)
        final Prototype initPrototype = prototypes.getInitPrototype();
        if (initPrototype != null) {
          final FunctionCall functionCall2 = generateFunctionCall(dagVertex, initPrototype, true);

          registerCallVariableToCoreBlock(operatorBlock, functionCall2);
          // Add the function call to the operatorBlock
          operatorBlock.getInitBlock().getCodeElts().add(functionCall2);

        }
      } else {
        // If the actor has no refinement
        throw new CodegenException("Actor (" + dagVertex + ") has no valid refinement (.idl, .h or .graphml)."
            + " Associate a refinement to this actor before generating code.");
      }
    }

  }

  /**
   * Generate the {@link Buffer} definition. This method sets the {@link Buffer#reaffectCreator(Block) Creator}
   * attributes. Also re-order the buffer definitions list so that containers are always defined before content.
   *
   */
  protected void generateBufferDefinitions() {
    for (final Entry<String, Buffer> entry : this.mainBuffers.entrySet()) {

      final String memoryBank = entry.getKey();
      final Buffer mainBuffer = entry.getValue();

      // Identify the corresponding operator block.
      // (also find out if the Buffer is local (i.e. not shared between
      // several CoreBlock)
      CoreBlock correspondingOperatorBlock = null;
      final boolean isLocal;
      final String correspondingOperatorID;

      if (memoryBank.equals("Shared")) {
        // If the memory bank is shared, let the main operator
        // declare the Buffer.
        correspondingOperatorID = this.scenario.getSimulationManager().getMainOperatorName();
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
          mainOperatorBlock = CodegenModelUserFactory.createCoreBlock();
          final ComponentInstance componentInstance = this.archi.getComponentInstance(correspondingOperatorID);
          mainOperatorBlock.setName(componentInstance.getInstanceName());
          mainOperatorBlock.setCoreType(componentInstance.getComponent().getVlnv().getName());
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
      recusriveSetBufferCreator(mainBuffer, correspondingOperatorBlock, isLocal);
      if (correspondingOperatorBlock != null) {
        final EList<Variable> definitions = correspondingOperatorBlock.getDefinitions();
        ECollections.sort(definitions, (o1, o2) -> {
          if ((o1 instanceof Buffer) && (o2 instanceof Buffer)) {
            int sublevelO1 = 0;
            if (o1 instanceof SubBuffer) {
              Buffer b1 = (Buffer) o1;
              while (b1 instanceof SubBuffer) {
                sublevelO1++;
                b1 = ((SubBuffer) b1).getContainer();
              }
            }

            int sublevelO2 = 0;
            if (o2 instanceof SubBuffer) {
              Buffer b2 = (Buffer) o2;
              while (b2 instanceof SubBuffer) {
                sublevelO2++;
                b2 = ((SubBuffer) b2).getContainer();
              }
            }

            return sublevelO1 - sublevelO2;
          }
          if (o1 instanceof Buffer) {
            return 1;
          }
          if (o2 instanceof Buffer) {
            return -1;
          }
          return 0;
        });
      }
    }
  }

  /**
   * This method creates a {@link Buffer} for each {@link DAGEdge} of the {@link #dag}. It also calls
   * {@link #generateSubBuffers(Buffer, DAGEdge, Integer)} to create distinct {@link SubBuffer} corresponding to all the
   * {@link SDFEdge} of the single-rate {@link SDFGraph} from which the {@link #dag} is derived.<br>
   * <br>
   * In this method, the {@link #sharedBuffer}, and the {@link #dagEdgeBuffers} attributes are filled.
   *
   * @throws CodegenException
   *           if a {@link DAGEdge} is associated to several {@link MemoryExclusionVertex}. (Happens if
   *           {@link AbstractMemoryAllocatorTask#VALUE_DISTRIBUTION_DISTRIBUTED_ONLY} distribution policy is used
   *           during memory allocation.)
   *
   */
  protected void generateBuffers() {
    // Create a main Buffer for each MEG
    for (final Entry<String, MemoryExclusionGraph> entry : this.megs.entrySet()) {

      final String memoryBank = entry.getKey();
      final MemoryExclusionGraph meg = entry.getValue();

      // Create the Main Shared buffer
      final long size = (long) meg.getPropertyBean().getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE);

      final Buffer mainBuffer = CodegenFactory.eINSTANCE.createBuffer();
      mainBuffer.setSize(size);
      mainBuffer.setName(memoryBank);
      mainBuffer.setType("char");
      mainBuffer.setTypeSize(1); // char is 1 byte
      this.mainBuffers.put(memoryBank, mainBuffer);

      @SuppressWarnings("unchecked")
      final Map<DAGEdge, Long> allocation = (Map<DAGEdge, Long>) meg.getPropertyBean()
          .getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);

      // generate the subbuffer for each dagedge
      for (final Entry<DAGEdge, Long> dagAlloc : allocation.entrySet()) {
        final DAGEdge edge = dagAlloc.getKey();
        final DAGVertex source = edge.getSource();
        final DAGVertex target = edge.getTarget();
        // If the buffer is not a null buffer
        if (dagAlloc.getValue() != -1) {
          final SubBuffer dagEdgeBuffer = CodegenFactory.eINSTANCE.createSubBuffer();

          // Old Naming (too long)
          final String comment = source.getName() + " > " + target.getName();
          dagEdgeBuffer.setComment(comment);

          String name = source.getName() + "__" + target.getName();

          name = generateUniqueBufferName(name);
          dagEdgeBuffer.setName(name);
          dagEdgeBuffer.reaffectContainer(mainBuffer);
          dagEdgeBuffer.setOffset(dagAlloc.getValue());
          dagEdgeBuffer.setType("char");
          dagEdgeBuffer.setTypeSize(1);

          // Generate subsubbuffers. Each subsubbuffer corresponds to
          // an edge of the single rate SDF Graph
          final Long dagEdgeSize = generateSubBuffers(dagEdgeBuffer, edge);

          // also accessible with dagAlloc.getKey().getWeight()
          dagEdgeBuffer.setSize(dagEdgeSize);

          // Save the DAGEdgeBuffer
          final DAGVertex originalSource = this.algo.getVertex(source.getName());
          final DAGVertex originalTarget = this.algo.getVertex(target.getName());
          final DAGEdge originalDagEdge = this.algo.getEdge(originalSource, originalTarget);
          if (!this.dagEdgeBuffers.containsKey(originalDagEdge)) {
            this.dagEdgeBuffers.put(originalDagEdge, dagEdgeBuffer);
          } else {
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
            throw new CodegenException("\n" + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY
                + " distribution policy during memory allocation not yet supported in code generation.\n" + "DAGEdge "
                + originalDagEdge + " is already associated to a Buffer and cannot be associated to a second one.");
          }
        } else {
          // the buffer is a null buffer
          final NullBuffer dagEdgeBuffer = CodegenModelUserFactory.createNullBuffer();

          // Old Naming (too long)
          final String comment = source.getName() + " > " + target.getName();
          dagEdgeBuffer.setComment("NULL_" + comment);
          dagEdgeBuffer.reaffectContainer(mainBuffer);

          // Generate subsubbuffers. Each subsubbuffer corresponds to
          // an
          // edge
          // of the single rate SDF Graph
          final Long dagEdgeSize = generateSubBuffers(dagEdgeBuffer, edge);

          // We set the size to keep the information
          dagEdgeBuffer.setSize(dagEdgeSize);

          // Save the DAGEdgeBuffer
          final DAGVertex originalSource = this.algo.getVertex(source.getName());
          final DAGVertex originalTarget = this.algo.getVertex(target.getName());
          final DAGEdge originalDagEdge = this.algo.getEdge(originalSource, originalTarget);
          this.dagEdgeBuffers.put(originalDagEdge, dagEdgeBuffer);
        }
      }

      // Generate buffers for each fifo
      @SuppressWarnings("unchecked")
      final Map<MemoryExclusionVertex, Long> fifoAllocation = (Map<MemoryExclusionVertex, Long>) meg.getPropertyBean()
          .getValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION);
      for (final Entry<MemoryExclusionVertex, Long> fifoAlloc : fifoAllocation.entrySet()) {
        final SubBuffer fifoBuffer = CodegenFactory.eINSTANCE.createSubBuffer();

        // Old Naming (too long)
        final String comment = fifoAlloc.getKey().getSource() + " > " + fifoAlloc.getKey().getSink();
        fifoBuffer.setComment(comment);

        String name = fifoAlloc.getKey().getSource() + "__" + fifoAlloc.getKey().getSink();
        name = generateUniqueBufferName(name);
        fifoBuffer.setName(name);
        fifoBuffer.reaffectContainer(mainBuffer);
        fifoBuffer.setOffset(fifoAlloc.getValue());
        fifoBuffer.setType("char");
        fifoBuffer.setSize(fifoAlloc.getKey().getWeight());

        // Get Init vertex
        final DAGVertex dagEndVertex = this.algo
            .getVertex(fifoAlloc.getKey().getSource().substring(("FIFO_Head_").length()));
        final DAGVertex dagInitVertex = this.algo.getVertex(fifoAlloc.getKey().getSink());

        final Pair<DAGVertex, DAGVertex> key = new Pair<>(dagEndVertex, dagInitVertex);
        Pair<Buffer, Buffer> value = this.dagFifoBuffers.get(key);
        if (value == null) {
          value = new Pair<>(null, null);
          this.dagFifoBuffers.put(key, value);
        }
        if (fifoAlloc.getKey().getSource().startsWith("FIFO_Head_")) {
          this.dagFifoBuffers.put(key, new Pair<Buffer, Buffer>(fifoBuffer, value.getValue()));
        } else {
          this.dagFifoBuffers.put(key, new Pair<Buffer, Buffer>(value.getKey(), fifoBuffer));
        }
      }
      // Generate subbuffers for each working mem.
      @SuppressWarnings("unchecked")
      final Map<MemoryExclusionVertex, Long> workingMemoryAllocation = (Map<MemoryExclusionVertex,
          Long>) (meg.getPropertyBean().getValue(MemoryExclusionGraph.WORKING_MEM_ALLOCATION));
      for (final Entry<MemoryExclusionVertex, Long> e : workingMemoryAllocation.entrySet()) {
        final SubBuffer workingMemBuffer = CodegenFactory.eINSTANCE.createSubBuffer();
        final MemoryExclusionVertex mObj = e.getKey();
        final long weight = mObj.getWeight();
        workingMemBuffer.reaffectContainer(mainBuffer);
        workingMemBuffer.setOffset(e.getValue());
        workingMemBuffer.setSize(weight);
        workingMemBuffer.setName("wMem_" + mObj.getVertex().getName());
        workingMemBuffer.setType("char");
        workingMemBuffer.setTypeSize(1); // char is 1 byte
        this.linkHSDFVertexBuffer.put(this.algo.getVertex(mObj.getVertex().getName()), workingMemBuffer);
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
   * @throws CodegenException
   *           Exception is thrown if:
   *           <ul>
   *           <li>There is a mismatch between the {@link Prototype} parameter and and the actor ports</li>
   *           <li>an actor port is connected to no edge.</li>
   *           <li>No {@link Buffer} in {@link #srSDFEdgeBuffers} corresponds to the edge connected to a port of the
   *           {@link DAGVertex}</li>
   *           <li>There is a mismatch between Parameters declared in the IDL and in the {@link SDFGraph}</li>
   *           <li>There is a missing argument in the IDL Loop {@link Prototype}</li>
   *           </ul>
   */
  protected Entry<List<Variable>, List<PortDirection>> generateCallVariables(final DAGVertex dagVertex,
      final Prototype prototype, final boolean isInit) {

    // Sorted list of the variables used by the prototype.
    // The integer is only used to order the variable and is retrieved
    // from the prototype
    final Map<Integer, Variable> variableList = new TreeMap<>();
    final Map<Integer, PortDirection> directionList = new TreeMap<>();

    // Retrieve the Variable corresponding to the arguments of the prototype
    for (final CodeGenArgument arg : prototype.getArguments().keySet()) {
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
        throw new CodegenException(
            "Mismatch between actor (" + dagVertex + ") ports and IDL loop prototype argument " + arg.getName());
      }

      // Retrieve the Edge corresponding to the current Argument
      DAGEdge dagEdge = null;
      BufferProperties subBufferProperties = null;
      switch (arg.getDirection()) {
        case CodeGenArgument.OUTPUT:
          final Set<DAGEdge> outEdges = this.algo.outgoingEdgesOf(dagVertex);
          for (final DAGEdge edge : outEdges) {
            final BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean()
                .getValue(BufferAggregate.propertyBeanName);
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
            final BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean()
                .getValue(BufferAggregate.propertyBeanName);
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
      }

      if ((dagEdge == null) || (subBufferProperties == null)) {
        throw new CodegenException(
            "The DAGEdge connected to the port  " + arg.getName() + " of Actor (" + dagVertex + ") does not exist.\n"
                + "Possible cause is that the DAG" + " was altered before entering" + " the Code generation.\n"
                + "This error may also happen if the port type " + "in the graph and in the IDL are not identical");
      }

      // At this point, the dagEdge, srsdfEdge corresponding to the
      // current argument were identified
      // Get the corresponding Variable
      final Variable var = this.srSDFEdgeBuffers.get(subBufferProperties);
      if (var == null) {
        throw new CodegenException(
            "Edge connected to " + arg.getDirection() + " port " + arg.getName() + " of DAG Actor " + dagVertex
                + " is not present in the input MemEx.\n" + "There is something wrong in the Memory Allocation task.");
      }

      variableList.put(prototype.getArguments().get(arg), var);
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
        throw new CodegenException(
            "Actor " + dagVertex + " has no match for parameter " + param.getName() + " declared in the IDL.");
      }

      final Constant constant = CodegenFactory.eINSTANCE.createConstant();
      constant.setName(param.getName());
      constant.setValue(actorParam.longValue());
      constant.setType("long");
      variableList.put(prototype.getParameters().get(param), constant);
      directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
    }

    return new AbstractMap.SimpleEntry<>(new ArrayList<>(variableList.values()),
        new ArrayList<>(directionList.values()));
  }

  private boolean checkEdgesExist(final String portName, final DAGVertex dagVertex, final Prototype prototype) {
    for (final CodeGenArgument arguments : prototype.getArguments().keySet()) {
      if (portName.equals(arguments.getName())) {
        return true;
      }
    }
    throw new CodegenException("SDF port \"" + portName + "\" of actor \"" + dagVertex
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
   * @throws CodegenException
   *           Exception is thrown if:
   *           <ul>
   *           </ul>
   *
   */
  protected void generateCommunication(final CoreBlock operatorBlock, final DAGVertex dagVertex,
      final String direction) {
    // Create the communication
    final SharedMemoryCommunication newComm = CodegenFactory.eINSTANCE.createSharedMemoryCommunication();
    final Direction dir = (direction.equals(VertexType.TYPE_SEND)) ? Direction.SEND : Direction.RECEIVE;
    final Delimiter delimiter = (direction.equals(VertexType.TYPE_SEND)) ? Delimiter.START : Delimiter.END;
    newComm.setDirection(dir);
    newComm.setDelimiter(delimiter);
    final MessageRouteStep routeStep = (MessageRouteStep) dagVertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.SendReceive_routeStep);
    for (final ComponentInstance comp : routeStep.getNodes()) {
      final CommunicationNode comNode = CodegenFactory.eINSTANCE.createCommunicationNode();
      comNode.setName(comp.getInstanceName());
      comNode.setType(comp.getComponent().getVlnv().getName());
      newComm.getNodes().add(comNode);
    }

    // Find the corresponding DAGEdge buffer(s)
    final DAGEdge dagEdge = (DAGEdge) dagVertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge);
    final Buffer buffer = this.dagEdgeBuffers.get(dagEdge);
    if (buffer == null) {
      throw new CodegenException("No buffer found for edge" + dagEdge);
    }
    newComm.setData(buffer);
    newComm.getParameters().clear();
    if (buffer != null) {
      newComm.addParameter(buffer, PortDirection.NONE);
    }

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
    final SharedMemoryCommunication newCommZoneComplement = CodegenFactory.eINSTANCE.createSharedMemoryCommunication();
    newCommZoneComplement.setDirection(dir);
    newCommZoneComplement.setDelimiter((delimiter.equals(Delimiter.START)) ? Delimiter.END : Delimiter.START);
    newCommZoneComplement.setData(buffer);
    newCommZoneComplement.getParameters().clear();
    if (buffer != null) {
      newCommZoneComplement.addParameter(buffer, PortDirection.NONE);
    }

    newCommZoneComplement.setName(((newComm.getDirection().equals(Direction.SEND)) ? "SE" : "RS") + commName);
    for (final ComponentInstance comp : routeStep.getNodes()) {
      final CommunicationNode comNode = CodegenFactory.eINSTANCE.createCommunicationNode();
      comNode.setName(comp.getInstanceName());
      comNode.setType(comp.getComponent().getVlnv().getName());
      newCommZoneComplement.getNodes().add(comNode);
    }

    // Find corresponding communications (SS/SE/RS/RE)
    registerCommunication(newCommZoneComplement, dagEdge, dagVertex);

    // Insert the new communication to the loop of the codeblock
    insertCommunication(operatorBlock, dagVertex, newCommZoneComplement);

    // No semaphore here, semaphore are only for SS->RE and RE->SR

    final Integer gid = (Integer) dagVertex.getPropertyBean().getValue("SYNC_GROUP");
    if (gid != null) {
      newComm.setComment("SyncComGroup = " + gid);
    }
    // Check if this is a redundant communication
    final Boolean b = (Boolean) dagVertex.getPropertyBean().getValue("Redundant");
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
   * @throws CodegenException
   *           if the passed vertex is not a {@link DAGInitVertex} nor a {@link DAGEndVertex}
   */
  protected void generateFifoCall(final CoreBlock operatorBlock, final DAGVertex dagVertex) {
    // Create the Fifo call and set basic property
    final FifoCall fifoCall = CodegenFactory.eINSTANCE.createFifoCall();
    fifoCall.setName(dagVertex.getName());

    // Find the type of FiFo operation
    final String kind = dagVertex.getKind();
    switch (kind) {
      case DAGInitVertex.DAG_INIT_VERTEX:
        fifoCall.setOperation(FifoOperation.POP);
        break;
      case DAGEndVertex.DAG_END_VERTEX:
        fifoCall.setOperation(FifoOperation.PUSH);
        break;
      default:
        final String message = "DAGVertex " + dagVertex + " does not corresponds to a Fifo primitive.";
        throw new CodegenException(message);
    }

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
    Buffer buffer = null;
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
      throw new CodegenException(
          "DAGVertex " + dagVertex + " is not connected to any " + VertexType.TYPE_TASK + " vertex.");
    }

    final BufferAggregate aggregate = (BufferAggregate) edge.getPropertyBean()
        .getValue(BufferAggregate.propertyBeanName);
    final BufferProperties bufferProperty = aggregate.get(0);
    buffer = this.srSDFEdgeBuffers.get(bufferProperty);
    if (buffer == null) {
      throw new CodegenException("DAGEdge " + edge + " was not allocated in memory.");
    }
    fifoCall.addParameter(buffer, dir);

    // Retrieve the internal buffer
    DAGVertex dagEndVertex;
    DAGVertex dagInitVertex;
    final String endReferenceName = (String) dagVertex.getPropertyBean().getValue(SDFInitVertex.END_REFERENCE);
    if (fifoCall.getOperation().equals(FifoOperation.POP)) {
      dagInitVertex = dagVertex;
      dagEndVertex = (DAGVertex) dagInitVertex.getBase().getVertex(endReferenceName);
    } else {
      dagEndVertex = dagVertex;
      dagInitVertex = (DAGVertex) dagEndVertex.getBase().getVertex(endReferenceName);
    }
    final Pair<Buffer, Buffer> buffers = this.dagFifoBuffers.get(new Pair<>(dagEndVertex, dagInitVertex));
    if ((buffers == null) || (buffers.getKey() == null)) {
      throw new CodegenException("No buffer was allocated for the the following pair of end/init vertices: "
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
      final FifoCall fifoInitCall = CodegenFactory.eINSTANCE.createFifoCall();
      fifoInitCall.setOperation(FifoOperation.INIT);
      fifoInitCall.setFifoHead(fifoCall);
      fifoInitCall.setName(fifoCall.getName());
      fifoInitCall.setHeadBuffer(fifoCall.getHeadBuffer());
      fifoInitCall.setBodyBuffer(fifoCall.getBodyBuffer());
      final PersistenceLevel level = (PersistenceLevel) dagInitVertex.getPropertyBean()
          .getValue(DAGInitVertex.PERSISTENCE_LEVEL);
      if (level == null || PersistenceLevel.PERMANENT.equals(level)) {
        operatorBlock.getInitBlock().getCodeElts().add(fifoInitCall);
      } else {
        operatorBlock.getLoopBlock().getCodeElts().add(fifoInitCall);
      }
    }

    // Register associated fifo calls (push/pop)
    if (fifoCall.getOperation().equals(FifoOperation.POP)) {
      // Pop operations are the first to be encountered.
      // We simply store the dagVertex with its associated fifoCall in a
      // Map. This Map will be used when processing the associated Push
      // operation
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
   * @throws CodegenException
   *           the codegen exception
   */
  protected FunctionCall generateFunctionCall(final DAGVertex dagVertex, final Prototype prototype,
      final boolean isInit) {
    // Create the corresponding FunctionCall
    final FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
    func.setName(prototype.getFunctionName());
    func.setActorName(dagVertex.getName());
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
   * This method creates the event configure PE function call for PAPI instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link CoreBlock operatorBlock} firing.
   */
  protected FunctionCall generatePapifyConfigurePEFunctionCall(final CoreBlock operatorBlock) {
    // Create the corresponding FunctionCall
    final FunctionCall configurePapifyPE = CodegenFactory.eINSTANCE.createFunctionCall();
    configurePapifyPE.setName("configure_papify_PE");
    // Create the variable associated to the PE name
    ConstantString papifyPEName = CodegenFactory.eINSTANCE.createConstantString();
    papifyPEName.setValue(operatorBlock.getName());
    // Create the variable associated to the PAPI component
    String compsSupported = "";
    ConstantString papifyComponentName = CodegenFactory.eINSTANCE.createConstantString();
    for (String compType : this.getScenario().getPapifyConfigManager()
        .getCorePapifyConfigGroupPE(operatorBlock.getCoreType()).getPAPIComponentIDs()) {
      if (compsSupported.equals("")) {
        compsSupported = compType;
      } else {
        compsSupported = compsSupported.concat(",").concat(compType);
      }
    }
    papifyComponentName.setValue(compsSupported);
    // Create the variable associated to the PE id
    Constant papifyPEId = CodegenFactory.eINSTANCE.createConstant();
    papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);
    papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
    // Add the function parameters
    configurePapifyPE.addParameter(papifyPEName, PortDirection.INPUT);
    configurePapifyPE.addParameter(papifyComponentName, PortDirection.INPUT);
    configurePapifyPE.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function comment
    configurePapifyPE.setActorName("Papify --> configure papification of ".concat(operatorBlock.getName()));

    return configurePapifyPE;
  }

  /**
   * This method creates the event configure actor function call for PAPI instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FunctionCall generatePapifyConfigureActorFunctionCall(final DAGVertex dagVertex) {
    // Create the corresponding FunctionCall
    final FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
    func.setName("configure_papify_actor");
    // Add the function parameters
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTION_NAME),
        PortDirection.OUTPUT);
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_COMPONENT_NAME),
        PortDirection.INPUT);
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTOR_NAME),
        PortDirection.INPUT);
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_CODESET_SIZE),
        PortDirection.INPUT);
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_EVENTSET_NAMES),
        PortDirection.INPUT);
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_CONFIG_NUMBER),
        PortDirection.INPUT);
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_COUNTER_CONFIGS),
        PortDirection.INPUT);
    // Add the function comment
    func.setActorName("Papify --> configure papification of ".concat(dagVertex.getName()));

    return func;
  }

  /**
   * This method creates the event start function call for PAPI instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FunctionCall generatePapifyStartFunctionCall(final DAGVertex dagVertex, final CoreBlock operatorBlock) {
    // Create the variable associated to the PE id
    Constant papifyPEId = CodegenFactory.eINSTANCE.createConstant();
    papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);
    papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
    // Create the corresponding FunctionCall
    final FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
    func.setName("event_start");
    // Add the function parameters
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTION_NAME),
        PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function actor name
    func.setActorName(dagVertex.getName());

    return func;
  }

  /**
   * This method creates the event start Papify timing function call for PAPI instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FunctionCall generatePapifyStartTimingFunctionCall(final DAGVertex dagVertex,
      final CoreBlock operatorBlock) {
    // Create the variable associated to the PE id
    Constant papifyPEId = CodegenFactory.eINSTANCE.createConstant();
    papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);
    papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
    // Create the corresponding FunctionCall
    final FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
    func.setName("event_start_papify_timing");
    // Add the function parameters
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTION_NAME),
        PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function actor name
    func.setActorName(dagVertex.getName());
    return func;
  }

  /**
   * This method creates the event stop function call for PAPI instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FunctionCall generatePapifyStopFunctionCall(final DAGVertex dagVertex, final CoreBlock operatorBlock) {
    // Create the variable associated to the PE id
    Constant papifyPEId = CodegenFactory.eINSTANCE.createConstant();
    papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);
    papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
    // Create the corresponding FunctionCall
    final FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
    func.setName("event_stop");
    // Add the function parameters
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTION_NAME),
        PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function actor name
    func.setActorName(dagVertex.getName());
    return func;
  }

  /**
   * This method creates the event stop Papify timing function call for PAPI instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FunctionCall generatePapifyStopTimingFunctionCall(final DAGVertex dagVertex,
      final CoreBlock operatorBlock) {
    // Create the variable associated to the PE id
    Constant papifyPEId = CodegenFactory.eINSTANCE.createConstant();
    papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);
    papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
    // Create the corresponding FunctionCall
    final FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
    func.setName("event_stop_papify_timing");
    // Add the function parameters
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTION_NAME),
        PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function actor name
    func.setActorName(dagVertex.getName());
    return func;
  }

  /**
   * This method creates the event write Papify function call for PAPI instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @return The {@link FunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected FunctionCall generatePapifyWritingFunctionCall(final DAGVertex dagVertex, final CoreBlock operatorBlock) {
    // Create the corresponding FunctionCall
    final FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
    func.setName("event_write_file");
    // Create the variable associated to the PE id
    Constant papifyPEId = CodegenFactory.eINSTANCE.createConstant();
    papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);
    papifyPEId.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
    // Add the function parameters
    func.addParameter((Variable) dagVertex.getPropertyBean().getValue(PapifyEngine.PAPIFY_ACTION_NAME),
        PortDirection.INPUT);
    func.addParameter(papifyPEId, PortDirection.INPUT);
    // Add the function actor name
    func.setActorName(dagVertex.getName());
    return func;
  }

  private void addBuffer(final DAGVertex source, final DAGVertex target, final DAGEdge dagEdge, final SpecialCall f) {
    for (final AbstractEdge<?, ?> agg : dagEdge.getAggregate()) {
      final DAGEdge edge = (DAGEdge) agg;
      // For broadcast and round
      // buffersf.getType().equals(SpecialType.BROADCAST) vertices,
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
        throw new CodegenException("DAGEdge corresponding to srSDFEdge " + edge + " was not found.");
      }

      // Find the corresponding BufferProperty
      BufferProperties subBuffProperty = null;
      final BufferAggregate buffers = (BufferAggregate) correspondingEdge.getPropertyBean()
          .getValue(BufferAggregate.propertyBeanName);
      for (final BufferProperties subBufferProperties : buffers) {
        // The source and target actor are the same, check that the
        // ports are corrects
        if (edge.getTargetLabel().equals(subBufferProperties.getDestInputPortID())
            && edge.getSourceLabel().equals(subBufferProperties.getSourceOutputPortID())) {
          subBuffProperty = subBufferProperties;
          break;
        }
      }
      if (subBuffProperty == null) {
        throw new CodegenException("Buffer property with ports " + edge.getTargetLabel() + " and "
            + edge.getSourceLabel() + " was not found in DAGEdge aggregate " + correspondingEdge);
      }

      // Get the corresponding Buffer
      final Buffer buffer = this.srSDFEdgeBuffers.get(subBuffProperty);
      if (buffer == null) {
        throw new CodegenException("Buffer corresponding to DAGEdge" + correspondingEdge + "was not allocated.");
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
   * @throws CodegenException
   *           the codegen exception
   */
  protected void generateSpecialCall(final CoreBlock operatorBlock, final DAGVertex dagVertex) {
    final SpecialCall f = CodegenFactory.eINSTANCE.createSpecialCall();
    f.setName(dagVertex.getName());
    final String vertexType = dagVertex.getPropertyStringValue(AbstractVertex.KIND_LITERAL);
    switch (vertexType) {
      case DAGForkVertex.DAG_FORK_VERTEX:
        f.setType(SpecialType.FORK);
        break;
      case DAGJoinVertex.DAG_JOIN_VERTEX:
        f.setType(SpecialType.JOIN);
        break;
      case DAGBroadcastVertex.DAG_BROADCAST_VERTEX:
        final String specialKind = (String) dagVertex.getPropertyBean().getValue(DAGBroadcastVertex.SPECIAL_TYPE);
        if (specialKind == null) {
          throw new CodegenException("Broadcast DAGVertex " + dagVertex + " has null special type");
        }
        if (specialKind.equals(DAGBroadcastVertex.SPECIAL_TYPE_BROADCAST)) {
          f.setType(SpecialType.BROADCAST);
        } else if (specialKind.equals(DAGBroadcastVertex.SPECIAL_TYPE_ROUNDBUFFER)) {
          f.setType(SpecialType.ROUND_BUFFER);
        } else {
          throw new CodegenException(
              "Broadcast DAGVertex " + dagVertex + " has an unknown special type: " + specialKind);
        }
        break;
      default:
        throw new CodegenException("DAGVertex " + dagVertex + " has an unknown type: " + vertexType);
    }

    if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
      dagVertex.outgoingEdges().forEach(edge -> addBuffer(dagVertex, edge.getTarget(), edge, f));
    } else {
      dagVertex.incomingEdges().forEach(edge -> addBuffer(edge.getSource(), dagVertex, edge, f));
    }

    // Find the last buffer that correspond to the
    // exploded/broadcasted/joined/roundbuffered edge
    DAGEdge lastEdge = null;
    // The vertex may have a maximum of 2 incoming/outgoing edges
    // but only one should be linked to the producer/consumer
    // the other must be linked to a send/receive vertex
    Set<DAGEdge> candidates;
    if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
      candidates = this.algo.incomingEdgesOf(dagVertex);
    } else {
      candidates = this.algo.outgoingEdgesOf(dagVertex);
    }

    if (candidates.size() > 2) {
      String direction;
      if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
        direction = "incoming";
      } else {
        direction = "outgoing";
      }
      throw new CodegenException(f.getType().getName() + " vertex " + dagVertex + " more than 1 " + direction
          + "edge. Check the exported DAG.");
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
      // This should never happen. It would mean that a
      // "special vertex" does receive data only from send/receive
      // vertices
      throw new CodegenException(f.getType().getName() + " vertex " + dagVertex + "is not properly connected.");
    }

    final BufferAggregate bufferAggregate = (BufferAggregate) lastEdge.getPropertyBean()
        .getValue(BufferAggregate.propertyBeanName);
    // there should be only one buffer in the aggregate
    final BufferProperties lastBuffProperty = bufferAggregate.get(0);
    final Buffer lastBuffer = this.srSDFEdgeBuffers.get(lastBuffProperty);

    // Add it to the specialCall
    if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
      f.addInputBuffer(lastBuffer);
    } else {
      f.addOutputBuffer(lastBuffer);
    }

    operatorBlock.getLoopBlock().getCodeElts().add(f);
    this.dagVertexCalls.put(dagVertex, f);

    identifyMergedInputRange(new AbstractMap.SimpleEntry<List<Variable>, List<PortDirection>>(f.getParameters(),
        f.getParameterDirections()));
    registerCallVariableToCoreBlock(operatorBlock, f);
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
   * @param offset
   *          the of the {@link DAGEdge} in the {@link Buffer}
   * @return the total size of the subbuffers
   * @throws CodegenException
   *           If a {@link DataType} used in the graph is not declared in the {@link PreesmScenario}.
   *
   */
  protected long generateSubBuffers(final Buffer parentBuffer, final DAGEdge dagEdge) {

    final Map<String, DataType> dataTypes = this.scenario.getSimulationManager().getDataTypes();

    final BufferAggregate buffers = (BufferAggregate) dagEdge.getPropertyBean()
        .getValue(BufferAggregate.propertyBeanName);

    // Retrieve the corresponding memory object from the MEG
    final MemoryExclusionVertex memObject = findMObject(dagEdge);
    @SuppressWarnings("unchecked")
    final List<Long> interSubbufferSpace = (List<Long>) memObject.getPropertyBean()
        .getValue(MemoryExclusionVertex.INTER_BUFFER_SPACES);

    long aggregateOffset = 0;
    int idx = 0;
    for (final BufferProperties subBufferProperties : buffers) {
      Buffer buff = null;
      // If the parent buffer is not null
      if (!(parentBuffer instanceof NullBuffer)) {
        final SubBuffer subBuff = CodegenFactory.eINSTANCE.createSubBuffer();
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
        subBuff.setOffset(aggregateOffset);
        subBuff.setType(subBufferProperties.getDataType());
        subBuff.setSize(subBufferProperties.getSize());

        // Save the created SubBuffer
        this.srSDFEdgeBuffers.put(subBufferProperties, subBuff);
      } else {
        // The parent buffer is a null buffer
        final NullBuffer nullBuff = CodegenModelUserFactory.createNullBuffer();
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

      // Increment the aggregate offset with the size of the current
      // subBuffer multiplied by the size of the datatype
      if (subBufferProperties.getDataType().equals("typeNotFound")) {
        throw new CodegenException("There is a problem with datatypes.\n"
            + "Please make sure that all data types are defined in the Simulation tab of the scenario editor.");
      }
      final DataType subBuffDataType = dataTypes.get(subBufferProperties.getDataType());
      if (subBuffDataType == null) {
        throw new CodegenException("Data type " + subBufferProperties.getDataType() + " is undefined in the scenario.");
      }
      buff.setTypeSize(subBuffDataType.getSize());
      aggregateOffset += (buff.getSize() * subBuffDataType.getSize());
    }

    return aggregateOffset;
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
    if (key.length() > 28) {
      key = key.substring(0, 28);
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
   * @throws CodegenException
   *           Exception is thrown if:
   *           <ul>
   *           <li>The {@link DAGVertex} has no IDL Refinement</li>
   *           </ul>
   */
  protected ActorPrototypes getActorPrototypes(final DAGVertex dagVertex) {
    final Object refinement = dagVertex.getRefinement();

    // Check that it has an IDL refinement.
    if (!(refinement instanceof CodeRefinement) || (((CodeRefinement) refinement).getLanguage() != Language.IDL)) {
      throw new CodegenException("generateFunctionCall was called with a DAG Vertex withoud IDL");
    }

    // Retrieve the IDL File
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();

    IPath path = ((CodeRefinement) refinement).getPath();
    IFile idlFile;
    // XXX: workaround for existing IBSDF projects where refinements are
    // under the form "../folder/file"
    if (path.toOSString().startsWith("..")) {
      final String projectName = this.workflow.getProjectName();
      final IProject project = root.getProject(projectName);
      path = new Path(project.getLocation() + path.toString().substring(2));
      idlFile = root.getFileForLocation(path);
    } else {
      idlFile = root.getFile(path);
    }

    // Retrieve the ActorPrototype
    if (idlFile != null) {
      final IPath rawPath = idlFile.getRawLocation();
      final String rawLocation = rawPath.toOSString();
      return IDLPrototypeFactory.INSTANCE.create(rawLocation);
    } else {
      throw new NullPointerException();
    }
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
   * @throws CodegenException
   *           throws an exception if the {@link MemoryExclusionVertex} associated to a {@link DAGEdge} could not be
   *           found in any {@link #megs}.
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

    // For each output find the allocated range
    // (or Ranges in case of a divided buffer)
    final List<Pair<Buffer, Range>> outputRanges = new ArrayList<>();
    for (final Buffer output : outputs) {
      // If the input is not a NullBufer
      if (!(output instanceof NullBuffer)) {
        // Find the parent Buffer container b
        // and the offset within b.
        int start = 0;
        Buffer b = output;
        while (b instanceof SubBuffer) {
          start += ((SubBuffer) b).getOffset();
          b = ((SubBuffer) b).getContainer();
        }
        final long end = start + (output.getSize() * output.getTypeSize());

        // Save allocated range
        outputRanges.add(new Pair<>(b, new Range(start, end)));
      } else {
        // The output is a NullBuffer (i.e. it is divided)
        // Find the allocation of its ranges
        final DAGEdge dagEdge = this.dagEdgeBuffers.inverse().get(((NullBuffer) output).getContainer());

        // Get the real ranges from the memObject
        final MemoryExclusionVertex mObject = findMObject(dagEdge);

        @SuppressWarnings("unchecked")
        final List<Pair<MemoryExclusionVertex,
            Pair<Range, Range>>> realRanges = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) mObject
                .getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);

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
          while (b instanceof SubBuffer) {
            start += ((SubBuffer) b).getOffset();
            b = ((SubBuffer) b).getContainer();
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
      // If the input is not a NullBufer
      if (!(input instanceof NullBuffer)) {
        // Find the parent Buffer container b
        // and the offset within b.
        int start = 0;
        Buffer b = input;
        while (b instanceof SubBuffer) {
          start += ((SubBuffer) b).getOffset();
          b = ((SubBuffer) b).getContainer();
        }
        final long end = start + (input.getSize() * input.getTypeSize());

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
   * @throws CodegenException
   *           if the newComm is a SendRelease or a ReceiveReserve.
   */
  protected void insertCommunication(final CoreBlock operatorBlock, final DAGVertex dagVertex,
      final Communication newComm) {

    // Do this only for SS and RE
    if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
        || (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.RECEIVE))) {

      // Do the insertion
      operatorBlock.getLoopBlock().getCodeElts().add(newComm);

      // Save the communication in the dagVertexCalls map only if it
      // is a
      // SS or a ER
      if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
          || (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.RECEIVE))) {
        this.dagVertexCalls.put(dagVertex, newComm);
      }

    } else {
      // Code reached for RS, SE, RR and SR
      // Retrieve the corresponding ReceiveEnd or SendStart
      Call zoneReference = null;
      if (newComm.getDirection().equals(Direction.SEND)) {
        zoneReference = newComm.getSendStart();
      }
      if (newComm.getDirection().equals(Direction.RECEIVE)) {
        zoneReference = newComm.getReceiveEnd();
      }

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
  private void recusriveSetBufferCreator(final Buffer buffer, final CoreBlock correspondingOperatorBlock,
      final boolean isLocal) {
    // Set the creator for the current buffer
    buffer.reaffectCreator(correspondingOperatorBlock);
    buffer.setLocal(isLocal);

    // Do the same recursively for all its children subbuffers
    for (final SubBuffer subBuffer : buffer.getChildrens()) {
      recusriveSetBufferCreator(subBuffer, correspondingOperatorBlock, isLocal);
    }
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
    // Retrieve the routeStep corresponding to the vertex.
    // In case of multi-step communication, this is the easiest
    // way to retrieve the target and source of the communication
    // corresponding to the current Send/ReceiveVertex
    final MessageRouteStep routeStep = (MessageRouteStep) dagVertex.getPropertyBean()
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

  /**
   * The purpose of this function is to restore to their original size the {@link MemoryExclusionVertex} that were
   * merged when applying memory scripts.
   */
  protected void restoreHostedVertices() {
    for (final MemoryExclusionGraph meg : this.megs.values()) {
      @SuppressWarnings("unchecked")
      final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostBuffers = (Map<MemoryExclusionVertex,
          Set<MemoryExclusionVertex>>) meg.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
      if (hostBuffers != null) {
        for (final Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : hostBuffers.entrySet()) {
          // Since host vertices are naturally aligned, no need to
          // restore
          // them

          // Restore the real size of hosted vertices
          final Set<MemoryExclusionVertex> vertices = entry.getValue();

          for (final MemoryExclusionVertex vertex : vertices) {
            // For non-divided vertices
            if (vertex.getWeight() != 0) {
              final long emptySpace = (long) vertex.getPropertyBean()
                  .getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE);

              // Put the vertex back to its real size
              vertex.setWeight(vertex.getWeight() - emptySpace);

              // And set the allocated offset
              final long allocatedOffset = (long) vertex.getPropertyBean()
                  .getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);

              vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, allocatedOffset + emptySpace);
              @SuppressWarnings("unchecked")
              final Map<DAGEdge, Long> dagEdgeAllocation = (Map<DAGEdge, Long>) meg.getPropertyBean()
                  .getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);
              dagEdgeAllocation.put(vertex.getEdge(), allocatedOffset + emptySpace);
            }
          }
        }
      }
    }
  }
}
