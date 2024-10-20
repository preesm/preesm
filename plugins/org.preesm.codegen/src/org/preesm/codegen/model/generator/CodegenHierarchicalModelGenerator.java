/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 * Julien Hascoet [jhascoet@kalray.eu] (2017)
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
package org.preesm.codegen.model.generator;

import com.google.common.collect.BiMap;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.preesm.algorithm.clustering.AbstractClust;
import org.preesm.algorithm.clustering.ClustSequence;
import org.preesm.algorithm.clustering.ClustVertex;
import org.preesm.algorithm.clustering.HSDFBuildLoops;
import org.preesm.algorithm.codegen.idl.ActorPrototypes;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.algorithm.codegen.model.CodeGenArgument;
import org.preesm.algorithm.codegen.model.CodeGenParameter;
import org.preesm.algorithm.mapper.graphtransfo.BufferAggregate;
import org.preesm.algorithm.mapper.graphtransfo.BufferProperties;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.parameters.Argument;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.preesm.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.preesm.codegen.model.Buffer;
import org.preesm.codegen.model.BufferIterator;
import org.preesm.codegen.model.Call;
import org.preesm.codegen.model.CodeElt;
import org.preesm.codegen.model.CodegenPackage;
import org.preesm.codegen.model.Communication;
import org.preesm.codegen.model.Constant;
import org.preesm.codegen.model.ConstantString;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.DistributedBuffer;
import org.preesm.codegen.model.FiniteLoopBlock;
import org.preesm.codegen.model.FunctionCall;
import org.preesm.codegen.model.IntVar;
import org.preesm.codegen.model.LoopBlock;
import org.preesm.codegen.model.PapifyAction;
import org.preesm.codegen.model.PapifyFunctionCall;
import org.preesm.codegen.model.PapifyType;
import org.preesm.codegen.model.PortDirection;
import org.preesm.codegen.model.SpecialCall;
import org.preesm.codegen.model.SpecialType;
import org.preesm.codegen.model.SubBuffer;
import org.preesm.codegen.model.Variable;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.PapiComponent;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapifyConfig;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.SimulationInfo;
import org.preesm.model.slam.Component;

/**
 *
 */
public class CodegenHierarchicalModelGenerator {

  /**
   * This is used to compute working the buffer offset inside the working memory. It is reinitialize to zero at the end
   * of each hierarchical actor print.
   */
  private int currentWorkingMemOffset = 0;

  /**
   * This {@link Map} associates a dag hierarchical vertex to the internal allocated working memory.
   */
  private final Map<DAGVertex, Buffer> linkHSDFVertexBuffer;

  /**
   * During the code generation of hierarchical actors, this {@link Map} associates internal edges to a buffer to link
   * input/output buffer (edge) when printing internal vertex and subbuffers of the internal working memory of
   * hierarchical actor. This {@link Map} is cleared at the end of the hierarchical actor print.
   */
  private final Map<SDFEdge, Buffer> linkHSDFEdgeBuffer;

  /**
   * {@link DirectedAcyclicGraph DAG} used to generate code. This {@link DirectedAcyclicGraph DAG} must be the result of
   * mapping/scheduling process.
   */
  private final DirectedAcyclicGraph dag;

  /**
   * This {@link Map} associates each {@link BufferProperties} aggregated in the {@link DAGEdge edges} of the
   * {@link DirectedAcyclicGraph DAG} to its corresponding {@link Buffer}.
   */
  private final Map<BufferProperties, Buffer> srSDFEdgeBuffers;

  /**
   * This {@link Map} associates each {@link DAGVertex} to its corresponding {@link Call}. It will be filled during when
   * creating the function call of actors and updated later by inserting {@link Communication} {@link Call calls}. For
   * {@link Communication}, only the End Receive and the Start Send communications will be stored in this map to avoid
   * having multiple calls for a unique {@link DAGVertex}.
   */
  private final BiMap<DAGVertex, Call> dagVertexCalls;

  /**
   * {@link PreesmScenario Scenario}.
   */
  private final Scenario scenario;

  /**
   *
   */
  private final SimulationInfo simulationInfo;

  /**
   *
   */

  private static final String  PAPIFY_PE_ID_CONSTANT_NAME = "PE_id";
  /**
   * This {@link List} stores the PEs that has been already configured for Papify usage.
   */
  protected final List<String> papifiedPEs;

  /**
   * This {@link List} of {@link List} stores the Papify configurations already used.
   */
  protected final List<EList<PapiEvent>> configsAdded;

  /** The flag to activate PAPIFY instrumentation. */
  private final boolean papifyActive;

  /**
   *
   */
  public CodegenHierarchicalModelGenerator(final Scenario scenario, final DirectedAcyclicGraph dag,
      final Map<DAGVertex, Buffer> linkHSDFVertexBuffer, final Map<BufferProperties, Buffer> srSDFEdgeBuffers,
      final BiMap<DAGVertex, Call> dagVertexCalls, final List<String> papifiedPEs, List<EList<PapiEvent>> configsAdded,
      boolean papifyActive) {
    this.dag = dag;
    this.srSDFEdgeBuffers = srSDFEdgeBuffers;
    this.dagVertexCalls = dagVertexCalls;
    this.linkHSDFVertexBuffer = linkHSDFVertexBuffer;
    this.scenario = scenario;
    this.linkHSDFEdgeBuffer = new LinkedHashMap<>();
    this.currentWorkingMemOffset = 0;
    this.simulationInfo = scenario.getSimulationInfo();
    this.papifiedPEs = papifiedPEs;
    this.configsAdded = configsAdded;
    this.papifyActive = papifyActive;
  }

  /**
   *
   */
  public void execute(final CoreBlock operatorBlock, final DAGVertex dagVertex) {
    // Check whether the ActorCall is a call to a hierarchical actor or not.
    final Object refinement = dagVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT_LITERAL);

    if (refinement instanceof AbstractGraph) {
      final SDFGraph graph = (SDFGraph) dagVertex.getGraphDescription();
      final List<SDFAbstractVertex> repVertexs = new ArrayList<>();
      final List<SDFInterfaceVertex> interfaces = new ArrayList<>();

      // we need to flat everything here
      final IbsdfFlattener flattener = new IbsdfFlattener(graph, -1);
      SDFGraph resultGraph = null;
      try {
        flattener.flattenGraph();
        resultGraph = flattener.getFlattenedGraph();
      } catch (final PreesmException e) {
        throw new PreesmRuntimeException(e.getMessage(), e);
      }
      // compute repetition vectors
      resultGraph.validateModel();
      if (!resultGraph.isSchedulable()) {
        throw new PreesmRuntimeException("HSDF Build Loops generate clustering: Graph not schedulable");
      }

      // Check nb actor for loop generation as only one actor in the
      for (final SDFAbstractVertex v : resultGraph.vertexSet()) {
        if (v instanceof SDFVertex) {
          repVertexs.add(v);
        }
        if (v instanceof final SDFInterfaceVertex sdfIfVertex) {
          interfaces.add(sdfIfVertex);
        }
      }

      final HSDFBuildLoops loopBuilder = new HSDFBuildLoops(this.scenario, null);
      final AbstractClust clust = graph.getPropertyBean().getValue(MapperDAG.CLUSTERED_VERTEX);
      if (clust == null) {
        throw new PreesmRuntimeException("Loop Codegen failed. Please make sure the clustering workflow is run.");
      }

      // check that hierarchical actor interfaces sinks or sources size is
      final List<SDFAbstractVertex> inputRepVertexs = new ArrayList<>();
      final List<SDFAbstractVertex> outputRepVertexs = new ArrayList<>();
      for (final SDFInterfaceVertex i : interfaces) {
        for (final SDFInterfaceVertex s : i.getSources()) {
          final SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
          final SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
          if ((a instanceof SDFVertex) || (a instanceof SDFRoundBufferVertex) || (a instanceof SDFBroadcastVertex)) {
            inputRepVertexs.add(a);
          }
          if ((b instanceof SDFVertex) || (b instanceof SDFRoundBufferVertex) || (b instanceof SDFBroadcastVertex)) {
            outputRepVertexs.add(b);
          }
        }
        for (final SDFInterfaceVertex s : i.getSinks()) {
          final SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
          final SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
          if ((a instanceof SDFVertex) || (a instanceof SDFRoundBufferVertex) || (a instanceof SDFBroadcastVertex)) {
            inputRepVertexs.add(a);
          }
          if ((b instanceof SDFVertex) || (b instanceof SDFRoundBufferVertex) || (b instanceof SDFBroadcastVertex)) {
            outputRepVertexs.add(b);
          }
        }
      }

      int forLoopIter = 0;
      AbstractClust current = loopBuilder.getLoopClustFirstV2(clust);
      final List<FiniteLoopBlock> upperLoops = new ArrayList<>();
      while (current != null) {
        if (current instanceof final ClustVertex clustVertex) {
          final SDFAbstractVertex repVertex = clustVertex.getVertex();
          // Vertex
          if (repVertex instanceof final SDFVertex repSdfVertex) {
            ActorPrototypes prototypes = null;
            final Object vertexRef = repSdfVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT_LITERAL);
            if (vertexRef instanceof final ActorPrototypes actorPrototypes) {
              prototypes = actorPrototypes;
            }
            if (prototypes == null) {
              throw new PreesmRuntimeException(
                  "Actor (" + dagVertex + ") has no valid refinement (.idl, .h or .graphml)."
                      + " Associate a refinement to this actor before generating code.");
            }
            final String iteratorIndex = "iteratorIndex" + Integer.toString(forLoopIter++);
            final Prototype loopPrototype = prototypes.getLoopPrototype();
            final long vertexRep = current.getRepeat();
            // create code elements and setup them
            final FunctionCall repFunc = CodegenModelUserFactory.eINSTANCE.createFunctionCall();
            final FiniteLoopBlock forLoop = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
            final IntVar intVar = CodegenModelUserFactory.eINSTANCE.createIntVar();
            intVar.setName(iteratorIndex);
            forLoop.setIter(intVar);
            forLoop.setNbIter((int) vertexRep);
            operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
            repFunc.setName(loopPrototype.getFunctionName());
            repFunc.setActorName(dagVertex.getName()); // Function call set to the hierarchical actor

            // retrieve and set variables to be called by the function
            final SDFAbstractVertex repVertexCallVar = resultGraph.getVertex(clustVertex.getVertex().getName());
            final Entry<List<Variable>,
                List<PortDirection>> callVars = generateRepeatedCallVariables(operatorBlock, forLoop, upperLoops,
                    dagVertex, repVertexCallVar, loopPrototype, intVar, inputRepVertexs, outputRepVertexs);
            for (int idx = 0; idx < callVars.getKey().size(); idx++) {
              // Put Variables in the function call
              repFunc.addParameter(callVars.getKey().get(idx), callVars.getValue().get(idx));
            }
            // Adding starting PAPIFY instrumentation
            papifyStartingFunctions(operatorBlock, dagVertex, forLoop, repSdfVertex);
            forLoop.getCodeElts().add(repFunc); // Add the function call to the for loop block
            // Adding stopping PAPIFY instrumentation
            papifyStoppingFunctions(dagVertex, forLoop, repSdfVertex);
            // Adding info to include the pragmas
            compactPapifyUsage(forLoop);

            registerCallVariableToCoreBlock(operatorBlock, repFunc); // for declaration in the file
            this.dagVertexCalls.put(dagVertex, repFunc); // Save the functionCall in the dagvertexFunctionCall Map

            if (!upperLoops.isEmpty()) {
              upperLoops.get(upperLoops.size() - 1).getCodeElts().add(forLoop);
            }

            // Special actors
          } else if ((repVertex instanceof SDFBroadcastVertex) || (repVertex instanceof SDFRoundBufferVertex)) {
            final SDFAbstractVertex repVertexCallVar = resultGraph
                .getVertex(((ClustVertex) current).getVertex().getName());
            final String iteratorIndex = "iteratorIndex" + Integer.toString(forLoopIter++);
            final FiniteLoopBlock forLoop = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
            final IntVar intVar = CodegenModelUserFactory.eINSTANCE.createIntVar();
            final long vertexRep = current.getRepeat();
            intVar.setName(iteratorIndex);
            forLoop.setIter(intVar);
            forLoop.setNbIter((int) vertexRep);
            generateRepeatedSpecialCall(operatorBlock, forLoop, dagVertex, repVertexCallVar, inputRepVertexs,
                outputRepVertexs);
            operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
            if (!upperLoops.isEmpty()) {
              upperLoops.get(upperLoops.size() - 1).getCodeElts().add(forLoop);
            }

          } else {
            throw new PreesmRuntimeException(
                "Unsupported codegen for Actor: " + dagVertex + " (Should be Fork or Join).");
          }

          // clust Sequence ForLoop only
        } else if (current instanceof ClustSequence && current.getRepeat() != 1) {
          final String iteratorIndex = "clustSeqIteratorIndex" + Integer.toString(forLoopIter++);
          final FiniteLoopBlock forLoop = CodegenModelUserFactory.eINSTANCE.createFiniteLoopBlock();
          final IntVar intVar = CodegenModelUserFactory.eINSTANCE.createIntVar();
          intVar.setName(iteratorIndex);
          forLoop.setIter(intVar);
          forLoop.setNbIter((int) current.getRepeat());
          operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
          if (!upperLoops.isEmpty()) {
            upperLoops.get(upperLoops.size() - 1).getCodeElts().add(forLoop);
          }
          upperLoops.add(forLoop);
        }
        current = loopBuilder.getLoopClustV2(clust);
      }
      this.linkHSDFEdgeBuffer.clear();
      this.currentWorkingMemOffset = 0;
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
    for (final Variable varariable : call.getParameters()) {
      // Currently, constants do not need to be declared nor
      // have creator since their value is directly used.
      // Consequently the used block can also be declared as the creator
      if (varariable instanceof Constant) {
        varariable.reaffectCreator(operatorBlock);
      }
      varariable.getUsers().add(operatorBlock);
    }
  }

  /**
   * This method generates the list of variable corresponding to a prototype of the {@link DAGVertex} firing. The
   * {@link Prototype} passed as a parameter must belong to the processedoutput__input__1 {@link DAGVertex}.
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param prototype
   *          the prototype whose {@link Variable variables} are retrieved
   * @param isInit
   *          Whethet the given prototype is an Init or a loop call. (We do not check missing arguments in the IDL for
   *          init Calls)
   */
  protected Entry<List<Variable>, List<PortDirection>> generateRepeatedCallVariables(final CoreBlock operatorBlock,
      final FiniteLoopBlock loopBlock, final List<FiniteLoopBlock> upperLoops, final DAGVertex dagVertex,
      final SDFAbstractVertex sdfAbsVertex, final Prototype prototype, final IntVar iterVar,
      final List<SDFAbstractVertex> inputRepVertexs, final List<SDFAbstractVertex> outputRepVertexs) {
    final SDFVertex sdfVertex = (SDFVertex) sdfAbsVertex;
    final TreeMap<Integer, Variable> variableList = new TreeMap<>();
    final TreeMap<Integer, PortDirection> directionList = new TreeMap<>();

    final boolean isInputActor = inputRepVertexs.contains(sdfVertex);
    final boolean isOutputActor = outputRepVertexs.contains(sdfVertex);

    // Retrieve the Variable corresponding to the arguments of the prototype
    // This loop manages only buffers (data buffer and NOT parameters)
    for (final CodeGenArgument arg : prototype.getArguments().keySet()) {
      final IntVar currentIterVar = CodegenModelUserFactory.eINSTANCE.createIntVar();
      currentIterVar.setName(iterVar.getName());

      PortDirection dir = null;
      boolean isInputActorTmp = isInputActor;
      boolean isOutputActorTmp = isOutputActor;

      // Check that the Actor has the right ports
      SDFInterfaceVertex port;
      switch (arg.getDirection()) {
        case CodeGenArgument.OUTPUT:
          port = sdfVertex.getSink(arg.getName());
          dir = PortDirection.OUTPUT;
          if (isInputActorTmp && !(sdfVertex.getAssociatedEdge(port).getTarget() instanceof SDFInterfaceVertex)) {
            isInputActorTmp = false;
          }
          break;
        case CodeGenArgument.INPUT:
          port = sdfVertex.getSource(arg.getName());
          dir = PortDirection.INPUT;
          if (isOutputActorTmp && !(sdfVertex.getAssociatedEdge(port).getSource() instanceof SDFInterfaceVertex)) {
            isOutputActorTmp = false;
          }
          break;
        default:
          port = null;
      }
      if (port == null) {
        throw new PreesmRuntimeException(
            "Mismatch between actor (" + sdfVertex + ") ports and IDL loop prototype argument " + arg.getName());
      }

      // Retrieve the Edge corresponding to the current Argument
      // This is only done because of the scheduler that is merging
      // consecutive buffers of an actor
      DAGEdge dagEdge = null;
      BufferProperties subBufferProperties = null;
      if (isInputActorTmp || isOutputActorTmp) {
        switch (arg.getDirection()) {
          case CodeGenArgument.OUTPUT:
            final Set<DAGEdge> outEdges = this.dag.outgoingEdgesOf(dagVertex);
            for (final DAGEdge edge : outEdges) {
              final BufferAggregate bufferAggregate = edge.getPropertyBean()
                  .getValue(BufferAggregate.PROPERTY_BEAN_NAME);
              for (final BufferProperties buffProperty : bufferAggregate) {
                final String portHsdfName = sdfVertex.getAssociatedEdge(port).getTargetLabel();
                if (buffProperty.getSourceOutputPortID().equals(portHsdfName) && edge.getTarget().getKind() != null) {
                  dagEdge = edge;
                  subBufferProperties = buffProperty;
                }
              }
            }
            break;
          case CodeGenArgument.INPUT:
            final Set<DAGEdge> inEdges = this.dag.incomingEdgesOf(dagVertex);
            for (final DAGEdge edge : inEdges) {
              final BufferAggregate bufferAggregate = edge.getPropertyBean()
                  .getValue(BufferAggregate.PROPERTY_BEAN_NAME);
              for (final BufferProperties buffProperty : bufferAggregate) {
                final String portHsdfName = sdfVertex.getAssociatedEdge(port).getSourceLabel();
                if (buffProperty.getDestInputPortID().equals(portHsdfName) && edge.getSource().getKind() != null) {
                  dagEdge = edge;
                  subBufferProperties = buffProperty;
                }
              }
            }
            break;
          default:

        }
        if ((dagEdge == null) || (subBufferProperties == null)) {
          throw new PreesmRuntimeException("The DAGEdge connected to the port  " + port + " of Hierarchical Actor ("
              + dagVertex + ") does not exist for\nrepeated actor " + sdfVertex + ".\n" + "DagEdge " + dagEdge
              + " subBuffer " + subBufferProperties + ".\nPossible cause is that the DAG"
              + " was altered before entering" + " the Code generation.\n"
              + "This error may also happen if the port type " + "in the graph and in the IDL are not identical");
        }
      }

      // At this point, the dagEdge, srsdfEdge corresponding to the
      // current argument were identified
      // Get the corresponding Variable

      Variable variable = null;
      final SDFEdge currentEdge = sdfVertex.getAssociatedEdge(port);
      long bufIterSize = 0;
      long bufSize = 0;

      long rep;
      rep = sdfVertex.getNbRepeatAsLong();

      // THIS IS WHERE OUTPUT AND INPUT OUTSIDE CLUSTER IS USED
      if (isInputActorTmp || isOutputActorTmp) {
        variable = this.srSDFEdgeBuffers.get(subBufferProperties);
        if (variable instanceof final DistributedBuffer distributedBuffer) {
          final EList<Buffer> repeatedBuffers = distributedBuffer.getDistributedCopies();
          final String coreBlockName = operatorBlock.getName();
          for (final Buffer bufferRepeatedChecker : repeatedBuffers) {
            final SubBuffer subBufferChecker = (SubBuffer) bufferRepeatedChecker;
            final SubBuffer repeatedContainer = (SubBuffer) subBufferChecker.getContainer();
            if (repeatedContainer.getContainer().getName().equals(coreBlockName)) {
              variable = subBufferChecker;
              break;
            }
          }
        }
        bufIterSize = subBufferProperties.getNbToken() / rep;
        bufSize = subBufferProperties.getNbToken();
      } else {
        if (CodeGenArgument.INPUT.equals(arg.getDirection())) {
          bufIterSize = currentEdge.getCons().longValue();
          bufSize = currentEdge.getCons().longValue() * rep;
        } else {
          bufIterSize = currentEdge.getProd().longValue();
          bufSize = currentEdge.getProd().longValue() * rep;
        }

        final SubBuffer workingMemBuf = (SubBuffer) this.linkHSDFVertexBuffer.get(dagVertex);
        SubBuffer buf = (SubBuffer) this.linkHSDFEdgeBuffer.get(currentEdge);
        if (buf == null) {
          buf = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
          buf.setName(workingMemBuf.getName() + "_" + Integer.toString(this.currentWorkingMemOffset));
          buf.reaffectContainer(workingMemBuf);
          buf.setOffsetInBit(this.currentWorkingMemOffset);
          buf.setNbToken((int) bufSize);
          buf.setType(currentEdge.getDataType().toString());
          // sorry lign of the death
          final long edgeDataSize = this.simulationInfo.getDataTypeSizeInBit(currentEdge.getDataType().toString());
          buf.setTokenTypeSizeInBit(edgeDataSize);
          // this.currentWorkingMemOffset += bufSize * edgeDataSize;
          this.currentWorkingMemOffset += this.simulationInfo.getBufferSizeInBit(currentEdge.getDataType().toString(),
              bufSize);
          this.linkHSDFEdgeBuffer.put(currentEdge, buf);
        }
        variable = buf;
      }

      final BufferIterator bufIter = CodegenModelUserFactory.eINSTANCE.createBufferIterator();
      if (variable == null) {
        throw new PreesmRuntimeException(
            "Edge connected to " + arg.getDirection() + " port " + arg.getName() + " of DAG Actor " + dagVertex
                + " is not present in the input MemEx.\n" + "There is something wrong in the Memory Allocation task.");
      }

      final StringBuilder upperLoopOffsets = new StringBuilder();
      if (!upperLoops.isEmpty()) {
        upperLoopOffsets.append("(" + Integer.toString(loopBlock.getNbIter()) + "*" + Long.toString(bufIterSize)
            + ") * ( " + upperLoops.get(upperLoops.size() - 1).getIter().getName());
        for (int i = 0; i < (upperLoops.size() - 1); i++) { // buffer iterations for nested loops
          upperLoopOffsets.append(" + (" + upperLoops.get(upperLoops.size() - 2 - i).getIter().getName() + "*"
              + Integer.toString(upperLoops.get(upperLoops.size() - 1 - i).getNbIter()) + ")");
        }
        upperLoopOffsets.append(" )");
        currentIterVar.setName(upperLoopOffsets.toString() + " + " + currentIterVar.getName());
      }

      bufIter.setName(variable.getName());
      bufIter.reaffectContainer(((SubBuffer) variable).getContainer());
      bufIter.setIter(currentIterVar);
      bufIter.setTokenTypeSizeInBit(((SubBuffer) variable).getTokenTypeSizeInBit());
      bufIter.setType(((SubBuffer) variable).getType());
      bufIter.setOffsetInBit(((SubBuffer) variable).getOffsetInBit());
      bufIter.setIterSize(bufIterSize);
      bufIter.setNbToken(bufSize);

      if (!CodeGenArgument.INPUT.equals(arg.getDirection()) && !CodeGenArgument.OUTPUT.equals(arg.getDirection())) {
        throw new PreesmRuntimeException("Args INPUT / OUTPUT failed\n");
      }

      /* register to call block */
      if (variable instanceof Constant) {
        variable.reaffectCreator(operatorBlock);
      }
      variable.getUsers().add(operatorBlock);

      variableList.put(prototype.getArguments().get(arg), bufIter);
      directionList.put(prototype.getArguments().get(arg), dir);
    }

    // Retrieve the Variables corresponding to the Parameters of the
    // prototype
    // This loop manages only parameters (parameters and NOT buffers)
    for (final CodeGenParameter param : prototype.getParameters().keySet()) {
      // Check that the actor has the right parameter
      final Argument actorParam = sdfVertex.getArgument(param.getName());

      if (actorParam == null) {
        throw new PreesmRuntimeException(
            "Actor " + sdfVertex + " has no match for parameter " + param.getName() + " declared in the IDL.");
      }

      final Constant constant = CodegenModelUserFactory.eINSTANCE.createConstant();
      constant.setName(param.getName());
      try {
        constant.setValue(actorParam.longValue());
      } catch (final Exception e) {
        throw new PreesmRuntimeException("Could not evaluate parameter value", e);
      }
      constant.setType("long");
      variableList.put(prototype.getParameters().get(param), constant);
      directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
    }
    return new AbstractMap.SimpleEntry<>(new ArrayList<>(variableList.values()),
        new ArrayList<>(directionList.values()));
  }

  /**
   * Generate the {@link CodegenPackage Codegen Model} for a "repeated special actor" (fork, join, broadcast or
   * roundbuffer) firing. This method will create an {@link SpecialCall} and place it in the {@link LoopBlock} of the
   * {@link CoreBlock} passed as a parameter. Called in hierarchical code printing.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the special actor firing is performed.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the actor firing.
   */
  protected void generateRepeatedSpecialCall(final CoreBlock operatorBlock, final FiniteLoopBlock loopBlock,
      final DAGVertex dagVertex, final SDFAbstractVertex repVertex, final List<SDFAbstractVertex> inputRepVertexs,
      final List<SDFAbstractVertex> outputRepVertexs) {
    final boolean isInputActor = inputRepVertexs.contains(repVertex);
    final boolean isOutputActor = outputRepVertexs.contains(repVertex);
    p("generateRepeatedSpecialCall " + repVertex.getName() + " isInputActor: " + isInputActor + " isOutputActor: "
        + isOutputActor);
    final SpecialCall f = CodegenModelUserFactory.eINSTANCE.createSpecialCall();
    final String vertexType = repVertex.getPropertyStringValue(AbstractVertex.KIND_LITERAL);
    f.setName(repVertex.getName());
    if (repVertex instanceof SDFRoundBufferVertex) {
      f.setType(SpecialType.ROUND_BUFFER);
    } else if (repVertex instanceof SDFBroadcastVertex) {
      f.setType(SpecialType.BROADCAST);
    } else {
      throw new PreesmRuntimeException("DAGVertex " + dagVertex + " has an unknown type: " + vertexType);
    }

    final List<SDFInterfaceVertex> repVertexInterfaces = new ArrayList<>();
    repVertexInterfaces.addAll(repVertex.getSources());
    repVertexInterfaces.addAll(repVertex.getSinks());
    for (final SDFInterfaceVertex port : repVertexInterfaces) {
      boolean isInputActorTmp = isInputActor;
      boolean isOutputActorTmp = isOutputActor;
      final SDFEdge associatedEdge = repVertex.getAssociatedEdge(port);
      final SDFEdge currentEdge = associatedEdge;
      if ((isInputActor) && (!(currentEdge.getSource() instanceof SDFInterfaceVertex))) {
        isInputActorTmp = false;
      }
      if ((isOutputActor) && (!(currentEdge.getTarget() instanceof SDFInterfaceVertex))) {
        isOutputActorTmp = false;
      }
      BufferProperties subBufferProperties = null;
      if ((isInputActorTmp) || (isOutputActorTmp)) {
        Set<DAGEdge> edges = null;
        if (isInputActorTmp) {
          edges = this.dag.incomingEdgesOf(dagVertex);
        } else {
          edges = this.dag.outgoingEdgesOf(dagVertex);
        }
        boolean edgeEarlyExit = false;
        for (final DAGEdge edge : edges) {
          final BufferAggregate bufferAggregate = edge.getPropertyBean().getValue(BufferAggregate.PROPERTY_BEAN_NAME);
          for (final BufferProperties buffProperty : bufferAggregate) {

            if (isInputActorTmp || isOutputActorTmp) {
              final String portHsdfName = isInputActorTmp ? associatedEdge.getSourceLabel()
                  : associatedEdge.getTargetLabel();
              final String destPortID = isInputActorTmp ? buffProperty.getDestInputPortID()
                  : buffProperty.getSourceOutputPortID();
              if (destPortID.equals(portHsdfName) && edge.getTarget().getKind() != null) {
                subBufferProperties = buffProperty;
                edgeEarlyExit = true;
                break;
              }
            }

          }
          if (edgeEarlyExit) {
            break;
          }
        }
        final Buffer buffer = this.srSDFEdgeBuffers.get(subBufferProperties);
        if (isInputActorTmp) {
          f.addInputBuffer(buffer);
        } else {
          f.addOutputBuffer(buffer);
        }
      } else { // working mem
        final SubBuffer workingMemBuf = (SubBuffer) this.linkHSDFVertexBuffer.get(dagVertex);
        SubBuffer buf = (SubBuffer) this.linkHSDFEdgeBuffer.get(currentEdge);
        long rep = 0;
        long bufSize = 0;
        if (buf == null) {
          rep = repVertex.getNbRepeatAsLong();
          if (port.getDirection().toString().equals("Input")) {
            bufSize = currentEdge.getCons().longValue() * rep;
          } else {
            bufSize = currentEdge.getProd().longValue() * rep;
          }
          buf = CodegenModelUserFactory.eINSTANCE.createSubBuffer();
          buf.setName(workingMemBuf.getName() + "_" + Integer.toString(this.currentWorkingMemOffset));
          buf.reaffectContainer(workingMemBuf);
          buf.setOffsetInBit(this.currentWorkingMemOffset);
          buf.setNbToken(bufSize);
          buf.setType(currentEdge.getDataType().toString());
          final long value = this.simulationInfo.getDataTypeSizeInBit(currentEdge.getDataType().toString());
          buf.setTokenTypeSizeInBit(value);
          this.currentWorkingMemOffset += bufSize * value;
          this.linkHSDFEdgeBuffer.put(currentEdge, buf);
        }
        if (port.getDirection().toString().equals("Input")) {
          f.addInputBuffer(buf);
        } else {
          f.addOutputBuffer(buf);
        }
      }
    }
    loopBlock.getCodeElts().add(f);

    registerCallVariableToCoreBlock(operatorBlock, f);
  }

  private void p(final String s) {
    final Logger logger = PreesmLogger.getLogger();
    logger.log(Level.INFO, s);
  }

  /**
   * Function to add all the required starting functions for the PAPIFY instrumentation
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the {@link PapifyFunctionCall} will be added.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param forLoop
   *          the {@link FiniteLoopBlock} of the clustered actors.
   * @param repVertex
   *          the specific {@link SDFVertex} identifying the actor that is being monitored.
   */

  private void papifyStartingFunctions(CoreBlock operatorBlock, DAGVertex dagVertex, FiniteLoopBlock forLoop,
      SDFVertex repVertex) {

    final PapifyAction papifyActionS = CodegenModelUserFactory.eINSTANCE.createPapifyAction();
    final Constant papifyPEId = CodegenModelUserFactory.eINSTANCE.createConstant();
    // Check if this actor has a monitoring configuration
    final PapifyConfig papifyConfig = this.scenario.getPapifyConfig();
    final AbstractActor referencePiVertex = repVertex.getReferencePiVertex();
    if (this.papifyActive && papifyConfig.hasPapifyConfig(referencePiVertex)) {

      papifyActionS.setName("papify_actions_".concat(papifyConfig.getActorOriginalIdentifier(referencePiVertex)));
      papifyActionS.setType("papify_action_s");
      papifyActionS.setComment("papify configuration variable");
      operatorBlock.getDefinitions().add(papifyActionS);

      // Add the function to configure the monitoring in this PE (operatorBlock)
      papifyPEId.setName(PAPIFY_PE_ID_CONSTANT_NAME);

      // Add the function to configure the monitoring in this PE (operatorBlock)
      if (!(this.papifiedPEs.contains(operatorBlock.getName()))) {
        this.papifiedPEs.add(operatorBlock.getName());
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
          repVertex, papifyConfig, papifyActionS);
      operatorBlock.getInitBlock().getCodeElts().add(functionCallPapifyConfigureActor);

      // What are we monitoring?
      if (papifyConfig.isMonitoringEvents(referencePiVertex)) {
        // Generate Papify start function for events
        final PapifyFunctionCall functionCallPapifyStart = generatePapifyStartFunctionCall(dagVertex, papifyPEId,
            papifyActionS);
        // Add the Papify start function for events to the loop
        forLoop.getCodeElts().add(functionCallPapifyStart);
      }
      if (papifyConfig.isMonitoringTiming(referencePiVertex)) {
        // Generate Papify start timing function
        final PapifyFunctionCall functionCallPapifyTimingStart = generatePapifyStartTimingFunctionCall(dagVertex,
            papifyPEId, papifyActionS);
        // Add the Papify start timing function to the loop
        forLoop.getCodeElts().add(functionCallPapifyTimingStart);
      }

    }
  }

  /**
   * Function to add all the required stopping functions for the PAPIFY instrumentation
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param forLoop
   *          the {@link FiniteLoopBlock} of the clustered actors.
   * @param repVertex
   *          the specific {@link SDFVertex} identifying the actor that is being monitored.
   */

  private void papifyStoppingFunctions(DAGVertex dagVertex, FiniteLoopBlock forLoop, SDFVertex repVertex) {

    final PapifyAction papifyActionS = CodegenModelUserFactory.eINSTANCE.createPapifyAction();
    final Constant papifyPEId = CodegenModelUserFactory.eINSTANCE.createConstant();
    // Check if this actor has a monitoring configuration
    final PapifyConfig papifyConfig = this.scenario.getPapifyConfig();
    final AbstractActor referencePiVertex = repVertex.getReferencePiVertex();
    if (this.papifyActive && papifyConfig.hasPapifyConfig(referencePiVertex)) {
      papifyActionS.setName("papify_actions_".concat(papifyConfig.getActorOriginalIdentifier(referencePiVertex)));
      papifyActionS.setType("papify_action_s");
      papifyActionS.setComment("papify configuration variable");
      // What are we monitoring?
      if (papifyConfig.isMonitoringTiming(referencePiVertex)) {
        // Generate Papify stop timing function
        final PapifyFunctionCall functionCallPapifyTimingStop = generatePapifyStopTimingFunctionCall(dagVertex,
            papifyPEId, papifyActionS);
        // Add the Papify stop timing function to the loop
        forLoop.getCodeElts().add(functionCallPapifyTimingStop);
      }
      if (papifyConfig.isMonitoringEvents(referencePiVertex)) {
        // Generate Papify stop function for events
        final PapifyFunctionCall functionCallPapifyStop = generatePapifyStopFunctionCall(dagVertex, papifyPEId,
            papifyActionS);
        // Add the Papify stop function for events to the loop
        forLoop.getCodeElts().add(functionCallPapifyStop);
      }
      // Generate Papify writing function
      final PapifyFunctionCall functionCallPapifyWriting = generatePapifyWritingFunctionCall(dagVertex, papifyPEId,
          papifyActionS);
      // Add the Papify writing function to the loop
      forLoop.getCodeElts().add(functionCallPapifyWriting);
    }
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
      PapifyConfig papifyConfig, Constant papifyPEId) {
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
    // Create the variable associated to the PE id
    final Constant papifyPEIdTask = CodegenModelUserFactory.eINSTANCE.createConstant();
    papifyPEIdTask.setName(PAPIFY_PE_ID_CONSTANT_NAME);
    papifyPEIdTask.setValue(this.papifiedPEs.indexOf(operatorBlock.getName()));
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
   * @param repVertex
   *          the specific {@link SDFVertex} identifying the actor that is being monitored.
   * @param papifyConfig
   *          the {@link PapifyConfig} storing all the instrumentation information.
   * @param papifyActionS
   *          the {@link PapifyAction} identifying the actor that is being configured.
   * @return The {@link PapifyFunctionCall} corresponding to the {@link DAGVertex actor} firing.
   */
  protected PapifyFunctionCall generatePapifyConfigureActorFunctionCall(final DAGVertex dagVertex,
      final SDFVertex repVertex, PapifyConfig papifyConfig, PapifyAction papifyActionS) {

    // Create the corresponding FunctionCall
    final PapifyFunctionCall func = CodegenModelUserFactory.eINSTANCE.createPapifyFunctionCall();
    func.setName("configure_papify_actor");
    final AbstractActor referencePiVertex = repVertex.getReferencePiVertex();
    // Add the PAPI component name
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
    componentName.setName("component_name".concat(repVertex.getName()));
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
    papifyConfigNumber.setName("PAPIFY_configs_".concat(repVertex.getName()));
    papifyConfigNumber.setValue(configIds);
    papifyConfigNumber.setComment("PAPIFY actor configs");

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
    // Add the function parameters
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

  void compactPapifyUsage(FiniteLoopBlock forLoop) {
    final EList<CodeElt> loopBlockElts = forLoop.getCodeElts();
    int iterator = 0;
    /*
     * Minimizing the number of #ifdef _PREESM_MONITORING_INIT in the loop
     */
    if (!loopBlockElts.isEmpty()) {
      if (loopBlockElts.get(0) instanceof final PapifyFunctionCall papifyFctCall) {
        papifyFctCall.setOpening(true);
        if (!(loopBlockElts.get(1) instanceof PapifyFunctionCall)) {
          papifyFctCall.setClosing(true);
        }
      }
      for (iterator = 1; iterator < loopBlockElts.size() - 1; iterator++) {
        if (loopBlockElts.get(iterator) instanceof PapifyFunctionCall
            && !(loopBlockElts.get(iterator - 1) instanceof PapifyFunctionCall)) {
          ((PapifyFunctionCall) loopBlockElts.get(iterator)).setOpening(true);
        }
        if (loopBlockElts.get(iterator) instanceof PapifyFunctionCall
            && !(loopBlockElts.get(iterator + 1) instanceof PapifyFunctionCall)) {
          ((PapifyFunctionCall) loopBlockElts.get(iterator)).setClosing(true);
        }
      }
      if (loopBlockElts.get(loopBlockElts.size() - 1) instanceof PapifyFunctionCall) {
        ((PapifyFunctionCall) loopBlockElts.get(loopBlockElts.size() - 1)).setClosing(true);
        if (!(loopBlockElts.get(loopBlockElts.size() - 2) instanceof PapifyFunctionCall)) {
          ((PapifyFunctionCall) loopBlockElts.get(loopBlockElts.size() - 1)).setOpening(true);
        }
      }
    }
  }
}
