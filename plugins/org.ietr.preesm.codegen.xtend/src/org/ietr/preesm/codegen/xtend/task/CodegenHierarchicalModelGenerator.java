package org.ietr.preesm.codegen.xtend.task;

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
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.clustering.AbstractClust;
import org.ietr.preesm.clustering.ClustSequence;
import org.ietr.preesm.clustering.ClustVertex;
import org.ietr.preesm.clustering.HSDFBuildLoops;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.IntVar;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialType;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.mapper.model.MapperDAG;

/**
 *
 */
public class CodegenHierarchicalModelGenerator {

  /**
   * This is used to compute working the buffer offset inside the working memory. It is reinitialize to zero at the end of each hierarchical actor print.
   */
  private int currentWorkingMemOffset = 0;

  /**
   * This {@link Map} associates a dag hierarchical vertex to the internal allocated working memory.
   */
  private final Map<DAGVertex, Buffer> linkHSDFVertexBuffer;

  /**
   * During the code generation of hierarchical actors, this {@link Map} associates internal edges to a buffer to link input/output buffer (edge) when printing
   * internal vertex and subbuffers of the internal working memory of hierarchical actor. This {@link Map} is cleared at the end of the hierarchical actor
   * print.
   */
  private final Map<SDFEdge, Buffer> linkHSDFEdgeBuffer;

  /**
   * {@link DirectedAcyclicGraph DAG} used to generate code. This {@link DirectedAcyclicGraph DAG} must be the result of mapping/scheduling process.
   */
  private final DirectedAcyclicGraph dag;

  /**
   * This {@link Map} associates each {@link BufferProperties} aggregated in the {@link DAGEdge edges} of the {@link DirectedAcyclicGraph DAG} to its
   * corresponding {@link Buffer}.
   */
  private final Map<BufferProperties, Buffer> srSDFEdgeBuffers;

  /**
   * This {@link Map} associates each {@link DAGVertex} to its corresponding {@link Call}. It will be filled during when creating the function call of actors
   * and updated later by inserting {@link Communication} {@link Call calls}. For {@link Communication}, only the End Receive and the Start Send communications
   * will be stored in this map to avoid having multiple calls for a unique {@link DAGVertex}.
   */
  private final BiMap<DAGVertex, Call> dagVertexCalls;

  /**
   * {@link PreesmScenario Scenario}.
   */
  private final PreesmScenario scenario;

  /**
   *
   */
  private final Map<String, DataType> dataTypes;

  /**
   *
   */
  public CodegenHierarchicalModelGenerator(final PreesmScenario scenario, final DirectedAcyclicGraph dag, final Map<DAGVertex, Buffer> linkHSDFVertexBuffer,
      final Map<BufferProperties, Buffer> srSDFEdgeBuffers, final BiMap<DAGVertex, Call> dagVertexCalls) {
    this.dag = dag;
    this.srSDFEdgeBuffers = srSDFEdgeBuffers;
    this.dagVertexCalls = dagVertexCalls;
    this.linkHSDFVertexBuffer = linkHSDFVertexBuffer;
    this.scenario = scenario;
    this.linkHSDFEdgeBuffer = new LinkedHashMap<>();
    this.currentWorkingMemOffset = 0;
    this.dataTypes = scenario.getSimulationManager().getDataTypes();
  }

  /**
   *
   */
  public int execute(final CoreBlock operatorBlock, final DAGVertex dagVertex) throws SDF4JException, WorkflowException {
    // Check whether the ActorCall is a call to a hierarchical actor or not.
    final SDFVertex sdfVertex = dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFVertex.class);
    final Object refinement = sdfVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT);

    // p("Generating code for hierarchical actor " + sdfVertex.getName());
    if (refinement instanceof AbstractGraph) {
      // p(sdfVertex.getName());
      final SDFGraph graph = (SDFGraph) sdfVertex.getGraphDescription();
      final List<SDFAbstractVertex> repVertexs = new ArrayList<>();
      final List<SDFInterfaceVertex> interfaces = new ArrayList<>();

      // we need to flat everything here
      final IbsdfFlattener flattener = new IbsdfFlattener(graph, 10);
      SDFGraph resultGraph = null;
      try {
        flattener.flattenGraph();
        resultGraph = flattener.getFlattenedGraph();
      } catch (final SDF4JException e) {
        throw (new WorkflowException(e.getMessage(), e));
      }
      resultGraph.validateModel(WorkflowLogger.getLogger()); // compute repetition vectors
      if (resultGraph.isSchedulable() == false) {
        throw (new WorkflowException("HSDF Build Loops generate clustering: Graph not schedulable"));
      }

      // Check nb actor for loop generation as only one actor in the
      for (final SDFAbstractVertex v : resultGraph.vertexSet()) {
        if (v instanceof SDFVertex) {
          repVertexs.add(v);
          // p("Actor " + v.getName() + " repeated " + getSDFVertexNbRepeated(v));
        }
        /*
         * if (v instanceof SDFBroadcastVertex) { repVertexs.add(v); p("Broadcast Actor " + v.getName()); } if (v instanceof SDFRoundBufferVertex) {
         * repVertexs.add(v); p("Round Buffer Actor " + v.getName()); }
         */
        if (v instanceof SDFInterfaceVertex) {
          interfaces.add((SDFInterfaceVertex) v);
          // p("Interface Vertex " + v.getName());
        }
      }

      final HSDFBuildLoops loopBuilder = new HSDFBuildLoops(this.scenario);
      final AbstractClust clust = graph.getPropertyBean().getValue(MapperDAG.CLUSTERED_VERTEX, AbstractClust.class);
      if (clust == null) {
        throw (new WorkflowException("Loop Codegen failed. Please make sure the clustering workflow is run."));
      }

      // check that hierarchical actor interfaces sinks or sources size is
      final List<SDFAbstractVertex> inputRepVertexs = new ArrayList<>();
      final List<SDFAbstractVertex> outputRepVertexs = new ArrayList<>();
      for (final SDFInterfaceVertex i : interfaces) {
        // p("Current interface " + i.getName() + " dir " + i.getDirection());
        for (final SDFInterfaceVertex s : i.getSources()) {
          final SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
          final SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
          if ((a instanceof SDFVertex) || (a instanceof SDFRoundBufferVertex) || (a instanceof SDFBroadcastVertex)) {
            outputRepVertexs.add(a);
            // p("1 input target " + a.getName());
          }
          if ((b instanceof SDFVertex) || (b instanceof SDFRoundBufferVertex) || (b instanceof SDFBroadcastVertex)) {
            inputRepVertexs.add(b);
            // p("2 input source " + b.getName());
          }
        }
        for (final SDFInterfaceVertex s : i.getSinks()) {
          final SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
          final SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
          if ((a instanceof SDFVertex) || (a instanceof SDFRoundBufferVertex) || (a instanceof SDFBroadcastVertex)) {
            inputRepVertexs.add(a);
            // p("3 output target " + a.getName());
          }
          if ((b instanceof SDFVertex) || (b instanceof SDFRoundBufferVertex) || (b instanceof SDFBroadcastVertex)) {
            outputRepVertexs.add(b);
            // p("4 output source " + b.getName());
          }
        }
      }

      // for (final SDFAbstractVertex s : inputRepVertexs) {
      // p("Hierarchical Input Vertex: " + s.getName());
      // }
      // for (final SDFAbstractVertex s : outputRepVertexs) {
      // p("Hierarchical Output Vertex " + s.getName());
      // }

      /*
       * final List<AbstractClust> listScheduleLoop = loopBuilder.getLoopClust(clust); for (final AbstractClust c : listScheduleLoop) { if (c instanceof
       * ClustVertex) { // p("ListScheduleLoop ClustVertex " + ((ClustVertex)c).getVertex().getName() + " repeat " +
       * getSDFVertexNbRepeated(((ClustVertex)c).getVertex()) + " // rep clust " + ((ClustVertex)c).getRepeat()); } else if (c instanceof ClustSequence) { //
       * p("ListScheduleLoop ClustSequence ForLoop iter " + ((ClustSequence)c).getRepeat()); } else { // p("ListScheduleLoop Failed to dump cluster"); } }
       */

      // p("Printing Code\n");
      int forLoopIter = 0;
      AbstractClust current = loopBuilder.getLoopClustFirstV2(clust);
      final List<FiniteLoopBlock> upperLoops = new ArrayList<>();
      while (current != null) {
        // AbstractClust current = listScheduleLoop.get(currentIdx);
        if (current instanceof ClustVertex) {
          final SDFAbstractVertex repVertex = ((ClustVertex) current).getVertex();
          // p("Printing " + repVertex.getName());
          // Vertex
          if (repVertex instanceof SDFVertex) {
            // p("ClustVertex " + repVertex.getName() + " repetition vector " + getSDFVertexNbRepeated(repVertex));
            // p("Codegen Model Generator " + repVertex.getName());
            ActorPrototypes prototypes = null;
            final Object vertex_ref = repVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT);
            if (vertex_ref instanceof ActorPrototypes) {
              prototypes = (ActorPrototypes) vertex_ref;
            }
            if (prototypes != null) {
              final String iteratorIndex = new String("iteratorIndex" + Integer.toString(forLoopIter++));
              final Prototype loopPrototype = prototypes.getLoopPrototype();
              final int vertexRep = current.getRepeat();
              // p("Actor " + repVertex.getName() + " Repeat " + vertexRep);
              // create code elements and setup them
              final FunctionCall repFunc = CodegenFactory.eINSTANCE.createFunctionCall();
              final FiniteLoopBlock forLoop = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
              final IntVar var = CodegenFactory.eINSTANCE.createIntVar();
              var.setName(iteratorIndex);
              forLoop.setIter(var);
              forLoop.setNbIter(vertexRep);
              operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
              repFunc.setName(loopPrototype.getFunctionName());
              repFunc.setActorName(dagVertex.getName()); // Function call set to the hierarchical actor

              // retrieve and set variables to be called by the function
              final SDFAbstractVertex repVertexCallVar = resultGraph.getVertex(((ClustVertex) current).getVertex().getName());
              final Entry<List<Variable>, List<PortDirection>> callVars = generateRepeatedCallVariables(operatorBlock, forLoop, upperLoops, dagVertex,
                  repVertexCallVar, loopPrototype, var, inputRepVertexs, outputRepVertexs, interfaces);
              for (int idx = 0; idx < callVars.getKey().size(); idx++) {
                repFunc.addParameter(callVars.getKey().get(idx), callVars.getValue().get(idx)); // Put Variables in the function call
                // p("Called var " + idx + " " + callVars.getKey().get(idx).getName() + " " + callVars.getValue().get(idx).getName());
              }
              // identifyMergedInputRange(callVars); //NOT SUPPORTED YET
              // for (CodeGenArgument arg : loopPrototype.getArguments().keySet()) { p("Arg Buffer "
              // + arg.getName()); } for (CodeGenParameter param : loopPrototype.getParameters().keySet()) {
              // p("Arg Parameter " + param.getName()); }
              forLoop.getCodeElts().add(repFunc); // Add the function call to the for loop block
              registerCallVariableToCoreBlock(operatorBlock, repFunc); // for declaration in the file
              this.dagVertexCalls.put(dagVertex, repFunc); // Save the functionCall in the dagvertexFunctionCall Map

              if (upperLoops.size() != 0) {
                upperLoops.get(upperLoops.size() - 1).getCodeElts().add(forLoop);
              }

            } else {
              throw new CodegenException("Actor (" + sdfVertex + ") has no valid refinement (.idl, .h or .graphml)."
                  + " Associate a refinement to this actor before generating code.");
            }

            // Special actors
          } else if ((repVertex instanceof SDFBroadcastVertex) || (repVertex instanceof SDFRoundBufferVertex)) {
            // p("Got Broadcast or RoundBuffer " + repVertex.getName());
            final SDFAbstractVertex repVertexCallVar = resultGraph.getVertex(((ClustVertex) current).getVertex().getName());
            final String iteratorIndex = new String("iteratorIndex" + Integer.toString(forLoopIter++));
            final FiniteLoopBlock forLoop = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
            final IntVar var = CodegenFactory.eINSTANCE.createIntVar();
            final int vertexRep = current.getRepeat();
            var.setName(iteratorIndex);
            forLoop.setIter(var);
            forLoop.setNbIter(vertexRep);
            generateRepeatedSpecialCall(operatorBlock, forLoop, dagVertex, repVertexCallVar, inputRepVertexs, outputRepVertexs, interfaces);
            operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
            if (upperLoops.size() != 0) {
              upperLoops.get(upperLoops.size() - 1).getCodeElts().add(forLoop);
            }

          } else {
            throw new CodegenException("Unsupported codegen for Actor: " + sdfVertex + " (Should be Fork or Join).");
          }

          // clust Sequence ForLoop only
        } else if (current instanceof ClustSequence) {
          if (current.getRepeat() != 1) {
            final String iteratorIndex = new String("clustSeqIteratorIndex" + Integer.toString(forLoopIter++));
            final FiniteLoopBlock forLoop = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
            final IntVar var = CodegenFactory.eINSTANCE.createIntVar();
            var.setName(iteratorIndex);
            forLoop.setIter(var);
            forLoop.setNbIter(current.getRepeat());
            operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
            if (upperLoops.size() != 0) {
              upperLoops.get(upperLoops.size() - 1).getCodeElts().add(forLoop);
            }
            upperLoops.add(forLoop);
            // p("ClustSequence ForLoop " + iteratorIndex + " repetition " + current.getRepeat());
          }
        }
        current = loopBuilder.getLoopClustV2(clust);
        // nbClust--;
      }
      this.linkHSDFEdgeBuffer.clear();
      this.currentWorkingMemOffset = 0;
      // p("hierarchial actor dump done ok");
    }
    return 0;
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
        var.setCreator(operatorBlock);
      }
      var.getUsers().add(operatorBlock);
    }
  }

  /**
   * This method generates the list of variable corresponding to a prototype of the {@link DAGVertex} firing. The {@link Prototype} passed as a parameter must
   * belong to the processedoutput__input__1 {@link DAGVertex}.
   *
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the {@link FunctionCall}.
   * @param prototype
   *          the prototype whose {@link Variable variables} are retrieved
   * @param isInit
   *          Whethet the given prototype is an Init or a loop call. (We do not check missing arguments in the IDL for init Calls)
   * @throws CodegenException
   *           Exception is thrown if:
   *           <ul>
   *           <li>There is a mismatch between the {@link Prototype} parameter and and the actor ports</li>
   *           <li>an actor port is connected to no edge.</li>
   *           <li>No {@link Buffer} in {@link #srSDFEdgeBuffers} corresponds to the edge connected to a port of the {@link DAGVertex}</li>
   *           <li>There is a mismatch between Parameters declared in the IDL and in the {@link SDFGraph}</li>
   *           <li>There is a missing argument in the IDL Loop {@link Prototype}</li>
   *           </ul>
   */
  protected Entry<List<Variable>, List<PortDirection>> generateRepeatedCallVariables(final CoreBlock operatorBlock, final FiniteLoopBlock loopBlock,
      final List<FiniteLoopBlock> upperLoops, final DAGVertex dagVertex, final SDFAbstractVertex sdfAbsVertex, final Prototype prototype, final IntVar iterVar,
      final List<SDFAbstractVertex> inputRepVertexs, final List<SDFAbstractVertex> outputRepVertexs, final List<SDFInterfaceVertex> interfaces)
      throws CodegenException {
    final SDFVertex sdfVertex = (SDFVertex) sdfAbsVertex;
    // p("generateRepeatedCallVariables sdfAbsVertex " + sdfAbsVertex.getName() + " function name " + prototype.getFunctionName() + " dagVertex " +
    // dagVertex.getName());
    final TreeMap<Integer, Variable> variableList = new TreeMap<>();
    final TreeMap<Integer, PortDirection> directionList = new TreeMap<>();

    final boolean isInputActor = inputRepVertexs.contains(sdfVertex);
    final boolean isOutputActor = outputRepVertexs.contains(sdfVertex);
    // for (final SDFAbstractVertex v : inputRepVertexs) {
    // if (v == sdfVertex) {
    // isInputActor = true;
    // // p("Actor " + sdfVertex.getName() + " is an input vertex of
    // // hirarchical actor " + dagVertex.getName());
    // }
    // }
    // for (final SDFAbstractVertex v : outputRepVertexs) {
    // if (v == sdfVertex) {
    // isOutputActor = true;
    // // p("Actor " + sdfVertex.getName() + " is an output vertex of
    // // hirarchical actor " + dagVertex.getName());
    // }
    // }
    // Retrieve the Variable corresponding to the arguments of the prototype
    // This loop manages only buffers (data buffer and NOT parameters)
    for (final CodeGenArgument arg : prototype.getArguments().keySet()) {
      final IntVar currentIterVar = CodegenFactory.eINSTANCE.createIntVar();
      currentIterVar.setName(iterVar.getName());

      PortDirection dir = null;
      boolean isInputActorTmp = isInputActor;
      boolean isOutputActorTmp = isOutputActor;

      // Check that the Actor has the right ports
      SDFInterfaceVertex port;
      switch (arg.getDirection()) {
        case CodeGenArgument.OUTPUT: {
          port = sdfVertex.getSink(arg.getName());
          dir = PortDirection.OUTPUT;
          if (isInputActorTmp == true) {
            // check target not an interface of hierarchical actor
            if ((sdfVertex.getAssociatedEdge(port).getTarget() instanceof SDFInterfaceVertex) == false) {
              isInputActorTmp = false;
            }
          }
          // p("Codegen interface OUTPUT " + port.getName() + " value " +
          // arg.getName());
          break;
        }
        case CodeGenArgument.INPUT: {
          port = sdfVertex.getSource(arg.getName());
          dir = PortDirection.INPUT;
          if (isOutputActorTmp == true) {
            // check target not an interface of hierarchical actor
            if ((sdfVertex.getAssociatedEdge(port).getSource() instanceof SDFInterfaceVertex) == false) {
              isOutputActorTmp = false;
            }
          }
          // p("Codegen interface INPUT " + port.getName() + " value " +
          // arg.getName() + " " +
          // sdfVertex.getSource(arg.getName()).getName() );
          break;
        }
        default: {
          port = null;
        }
      }
      if (port == null) {
        throw new CodegenException("Mismatch between actor (" + sdfVertex + ") ports and IDL loop prototype argument " + arg.getName());
      }

      // Retrieve the Edge corresponding to the current Argument
      // This is only done because of the scheduler that is merging
      // consecutive buffers of an actor
      DAGEdge dagEdge = null;
      BufferProperties subBufferProperties = null;
      if ((isInputActorTmp == true) || (isOutputActorTmp == true)) {
        switch (arg.getDirection()) {
          case CodeGenArgument.OUTPUT: {
            final Set<DAGEdge> edges = this.dag.outgoingEdgesOf(dagVertex);
            for (final DAGEdge edge : edges) {
              final BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
              for (final BufferProperties buffProperty : bufferAggregate) {
                final String portHsdfName = sdfVertex.getAssociatedEdge(port).getTargetLabel();
                if (buffProperty.getSourceOutputPortID().equals(portHsdfName)) {
                  // check that this edge is not connected to a receive vertex
                  if (edge.getTarget().getKind() != null) {
                    dagEdge = edge;
                    subBufferProperties = buffProperty;
                  }
                }
              }
            }
            break;
          }
          case CodeGenArgument.INPUT: {
            final Set<DAGEdge> edges = this.dag.incomingEdgesOf(dagVertex);
            for (final DAGEdge edge : edges) {
              final BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
              for (final BufferProperties buffProperty : bufferAggregate) {
                final String portHsdfName = sdfVertex.getAssociatedEdge(port).getSourceLabel();
                if (buffProperty.getDestInputPortID().equals(portHsdfName)) {
                  // check that this edge is not connected to a send vertex
                  if (edge.getSource().getKind() != null) {
                    dagEdge = edge;
                    subBufferProperties = buffProperty;
                  }
                }
              }
            }
            break;
          }
          default:

        }
        /*
         * logger.log(Level.INFO, "Edge " + dagEdge.getSource().getName() + " " + dagEdge.getTarget().getName() + " to args " +
         * subBufferProperties.getDestInputPortID() + " " + subBufferProperties.getSourceOutputPortID() + " " + subBufferProperties.getDataType() );
         */
        if ((dagEdge == null) || (subBufferProperties == null)) {
          throw new CodegenException(
              "The DAGEdge connected to the port  " + port + " of Hierarchical Actor (" + dagVertex + ") does not exist for\nrepeated actor " + sdfVertex
                  + ".\n" + "DagEdge " + dagEdge + " subBuffer " + subBufferProperties + ".\nPossible cause is that the DAG" + " was altered before entering"
                  + " the Code generation.\n" + "This error may also happen if the port type " + "in the graph and in the IDL are not identical");
        }
      }

      // At this point, the dagEdge, srsdfEdge corresponding to the
      // current argument were identified
      // Get the corresponding Variable
      // p("dagEdge subBufferProperties " + subBufferProperties.getSize()
      // + " " + subBufferProperties.getDataType());

      Variable var = null;
      final SDFEdge currentEdge = sdfVertex.getAssociatedEdge(port);
      int bufIterSize = 0;
      int bufSize = 0;

      int rep = 1;
      try {
        rep = sdfVertex.getNbRepeatAsInteger();
      } catch (final InvalidExpressionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      if ((isInputActorTmp == true) || (isOutputActorTmp == true)) {
        var = this.srSDFEdgeBuffers.get(subBufferProperties);
        bufIterSize = subBufferProperties.getSize() / rep;
        bufSize = subBufferProperties.getSize();
      } else {
        try {
          if (arg.getDirection() == CodeGenArgument.INPUT) {
            bufIterSize = currentEdge.getCons().intValue();// / rep;
            bufSize = currentEdge.getCons().intValue() * rep;
          } else {
            bufIterSize = currentEdge.getProd().intValue();// / rep;
            bufSize = currentEdge.getProd().intValue() * rep;
          }
        } catch (final InvalidExpressionException ex) {
          // TODO Auto-generated catch block
          ex.printStackTrace();
        }

        final SubBuffer workingMemBuf = (SubBuffer) this.linkHSDFVertexBuffer.get(dagVertex);
        SubBuffer buf = (SubBuffer) this.linkHSDFEdgeBuffer.get(currentEdge);
        // p("Tried get linkHSDFVertexBuffer key " + dagVertex.getName() + " working buf " + workingMemBuf.getName());
        if (buf == null) {
          buf = CodegenFactory.eINSTANCE.createSubBuffer();
          // p("linkHSDFEdgeBuffer buffer Name " + e.getValue().getName() + " " + e.getKey() + " coretype " + operatorBlock.getCoreType() + " corename " +
          // operatorBlock.getName());
          // currentIterVar.setName(upperLoopOffsets + " + " + currentIterVar.getName());
          buf.setName(workingMemBuf.getName() + "_" + Integer.toString(this.currentWorkingMemOffset));
          buf.setContainer(workingMemBuf);
          buf.setOffset(this.currentWorkingMemOffset);
          buf.setSize(bufSize);
          buf.setType(currentEdge.getDataType().toString());
          buf.setTypeSize(this.dataTypes.get(currentEdge.getDataType().toString()).getSize()); // sorry lign of the death
          this.currentWorkingMemOffset += bufSize * this.dataTypes.get(currentEdge.getDataType().toString()).getSize();
          // p("Internal working buffer " + buf.getName());
          this.linkHSDFEdgeBuffer.put(currentEdge, buf);
        }
        var = buf;
      }

      final BufferIterator bufIter = CodegenFactory.eINSTANCE.createBufferIterator();
      if (var == null) {
        throw new CodegenException("Edge connected to " + arg.getDirection() + " port " + arg.getName() + " of DAG Actor " + dagVertex
            + " is not present in the input MemEx.\n" + "There is something wrong in the Memory Allocation task.");
      }

      String upperLoopOffsets = new String();
      if (upperLoops.size() != 0) {
        upperLoopOffsets = "(" + Integer.toString(loopBlock.getNbIter()) + "*" + Integer.toString(bufIterSize) + ") * ( "
            + upperLoops.get(upperLoops.size() - 1).getIter().getName();
        for (int i = 0; i < (upperLoops.size() - 1); i++) { // buffer iterations for nested loops
          upperLoopOffsets += " + (" + upperLoops.get(upperLoops.size() - 2 - i).getIter().getName() + "*"
              + Integer.toString(upperLoops.get(upperLoops.size() - 1 - i).getNbIter()) + ")";
        }
        upperLoopOffsets += " )";
        currentIterVar.setName(upperLoopOffsets + " + " + currentIterVar.getName());
      }

      bufIter.setName(var.getName());
      bufIter.setContainer(((SubBuffer) var).getContainer());
      bufIter.setIter(currentIterVar);
      bufIter.setTypeSize(((SubBuffer) var).getTypeSize());
      bufIter.setType(((SubBuffer) var).getType());
      bufIter.setOffset(((SubBuffer) var).getOffset());
      bufIter.setIterSize(bufIterSize);
      bufIter.setSize(bufSize);

      if (arg.getDirection() == CodeGenArgument.INPUT) {
        loopBlock.getInBuffers().add(bufIter);
      } else if (arg.getDirection() == CodeGenArgument.OUTPUT) {
        loopBlock.getOutBuffers().add(bufIter);
      } else {
        throw new CodegenException("Args INPUT / OUTPUT failed\n");
      }

      /* register to call block */
      if (var instanceof Constant) {
        var.setCreator(operatorBlock);
      }
      var.getUsers().add(operatorBlock);
      // var.setCreator(operatorBlock);
      // registerCallVariableToCoreBlock(operatorBlock, var);
      /*
       * if(var.getCreator() == null) { throw new CodegenException("GenerateRepeatedCallVariable " + var.getName() + " getCreator is null boooo"); }
       */

      variableList.put(prototype.getArguments().get(arg), bufIter);
      directionList.put(prototype.getArguments().get(arg), dir);
      // logger.log(Level.INFO, "Get corresponding variable " +
      // prototype.getFunctionName() + " nbargs " + prototype.getNbArgs()
      // + " args " +
      // prototype.getArguments().get(arg) + " " +
      // prototype.getArguments().get(arg).toString() + " " +
      // var.getName());
    }

    // Retrieve the Variables corresponding to the Parameters of the
    // prototype
    // This loop manages only parameters (parameters and NOT buffers)
    for (final CodeGenParameter param : prototype.getParameters().keySet()) {
      // Check that the actor has the right parameter
      final Argument actorParam = sdfVertex.getArgument(param.getName());

      if (actorParam == null) {
        throw new CodegenException("Actor " + sdfVertex + " has no match for parameter " + param.getName() + " declared in the IDL.");
      }

      final Constant constant = CodegenFactory.eINSTANCE.createConstant();
      constant.setName(param.getName());
      try {
        constant.setValue(actorParam.intValue());
      } catch (final Exception e) {
        // Exception should never happen here since the expression was
        // evaluated before during the Workflow execution
        e.printStackTrace();
      }
      constant.setType("long");
      variableList.put(prototype.getParameters().get(param), constant);
      directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
      // p("Variable to Param " + prototype.getParameters().get(param) + "
      // " + param.getName() + " " + constant.getName());

      // // Retrieve the variable from its context (i.e. from its original
      // // (sub)graph)
      // org.ietr.dftools.algorithm.model.parameters.Variable originalVar
      // =
      // originalSDF
      // .getHierarchicalVertexFromPath(sdfVertex.getInfo())
      // .getBase().getVariables().getVariable(actorParam.getName());
      //
      // Constant constant = sdfVariableConstants.get(originalVar);
      // if (constant == null) {
      // constant = CodegenFactory.eINSTANCE.createConstant();
      // constant.setName(originalVar.getName());
      // //constant.setValue(originalVar.getValue());
      // }
      directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
    }
    return new AbstractMap.SimpleEntry<>(new ArrayList<>(variableList.values()), new ArrayList<>(directionList.values()));
  }

  /**
   * Generate the {@link CodegenPackage Codegen Model} for a "repeated special actor" (fork, join, broadcast or roundbuffer) firing. This method will create an
   * {@link SpecialCall} and place it in the {@link LoopBlock} of the {@link CoreBlock} passed as a parameter. Called in hierarchical code printing.
   *
   * @param operatorBlock
   *          the {@link CoreBlock} where the special actor firing is performed.
   * @param dagVertex
   *          the {@link DAGVertex} corresponding to the actor firing.
   * @throws CodegenException
   *           the codegen exception
   */
  protected void generateRepeatedSpecialCall(final CoreBlock operatorBlock, final FiniteLoopBlock loopBlock, final DAGVertex dagVertex,
      final SDFAbstractVertex repVertex, final List<SDFAbstractVertex> inputRepVertexs, final List<SDFAbstractVertex> outputRepVertexs,
      final List<SDFInterfaceVertex> interfaces) throws CodegenException {
    final boolean isInputActor = inputRepVertexs.contains(repVertex);
    final boolean isOutputActor = outputRepVertexs.contains(repVertex);
    p("generateRepeatedSpecialCall " + repVertex.getName() + " isInputActor: " + isInputActor + " isOutputActor: " + isOutputActor);
    final SpecialCall f = CodegenFactory.eINSTANCE.createSpecialCall();
    final String vertexType = repVertex.getPropertyStringValue(AbstractVertex.KIND);
    f.setName(repVertex.getName());
    if (repVertex instanceof SDFRoundBufferVertex) {
      f.setType(SpecialType.ROUND_BUFFER);
    } else if (repVertex instanceof SDFBroadcastVertex) {
      f.setType(SpecialType.BROADCAST);
    } else {
      throw new CodegenException("DAGVertex " + dagVertex + " has an unknown type: " + vertexType);
    }
    /*
     * else if (repVertex instanceof SDFForkVertex) { f.setType(SpecialType.FORK); } else if (repVertex instanceof SDFJoinVertex) { f.setType(SpecialType.JOIN);
     * }
     */

    final List<SDFInterfaceVertex> repVertexInterfaces = new ArrayList<>();
    repVertexInterfaces.addAll(repVertex.getSources());
    repVertexInterfaces.addAll(repVertex.getSinks());
    for (final SDFInterfaceVertex port : repVertexInterfaces) {
      // p("Specical Call " + repVertex.getName() + " port " + port.getName());
      boolean isInputActorTmp = isInputActor;
      boolean isOutputActorTmp = isOutputActor;
      final SDFEdge currentEdge = repVertex.getAssociatedEdge(port);
      if ((isInputActor == true) && ((currentEdge.getSource() instanceof SDFInterfaceVertex) == false)) {
        isInputActorTmp = false;
      }
      if ((isOutputActor == true) && ((currentEdge.getTarget() instanceof SDFInterfaceVertex) == false)) {
        isOutputActorTmp = false;
      }
      // DAGEdge dagEdge = null;
      BufferProperties subBufferProperties = null;
      if ((isInputActorTmp == true) || (isOutputActorTmp == true)) {
        Set<DAGEdge> edges = null;
        if (isInputActorTmp == true) {
          edges = this.dag.incomingEdgesOf(dagVertex);
        } else {
          edges = this.dag.outgoingEdgesOf(dagVertex);
        }
        boolean edgeEarlyExit = false;
        for (final DAGEdge edge : edges) {
          final BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean().getValue(BufferAggregate.propertyBeanName);
          for (final BufferProperties buffProperty : bufferAggregate) {
            if (isInputActorTmp == true) {
              final String portHsdfName = repVertex.getAssociatedEdge(port).getSourceLabel();
              if (buffProperty.getDestInputPortID().equals(portHsdfName)) {
                if (edge.getTarget().getKind() != null) { // check that this edge is not connected to a receive vertex
                  // dagEdge = edge;
                  subBufferProperties = buffProperty;
                  edgeEarlyExit = true;
                  break;
                }
              }
            }
            if (isOutputActorTmp == true) {
              final String portHsdfName = repVertex.getAssociatedEdge(port).getTargetLabel();
              if (buffProperty.getSourceOutputPortID().equals(portHsdfName)) {
                if (edge.getTarget().getKind() != null) { // check that this edge is not connected to a receive vertex
                  // dagEdge = edge;
                  subBufferProperties = buffProperty;
                  edgeEarlyExit = true;
                  break;
                }
              }
            }
          }
          if (edgeEarlyExit == true) {
            break;
          }
        }
        final Buffer buffer = this.srSDFEdgeBuffers.get(subBufferProperties);
        if (isInputActorTmp == true) {
          // p("SpecialCall external input buffer " + buffer.getName() + " size " + buffer.getSize());
          f.addInputBuffer(buffer);
        } else {
          // p("SpecialCall external output buffer " + buffer.getName() + " size " + buffer.getSize());
          f.addOutputBuffer(buffer);
        }
      } else { // working mem
        final SubBuffer workingMemBuf = (SubBuffer) this.linkHSDFVertexBuffer.get(dagVertex);
        SubBuffer buf = (SubBuffer) this.linkHSDFEdgeBuffer.get(currentEdge);
        int rep = 0;
        int bufSize = 0;
        // p("Tried get linkHSDFVertexBuffer key " + dagVertex.getName() + " working buf " + workingMemBuf.getName());
        if (buf == null) {
          rep = repVertex.getNbRepeatAsInteger();
          if (port.getDirection().toString().equals("Input") == true) {
            bufSize = currentEdge.getCons().intValue() * rep;
          } else {
            bufSize = currentEdge.getProd().intValue() * rep;
          }
          buf = CodegenFactory.eINSTANCE.createSubBuffer();
          buf.setName(workingMemBuf.getName() + "_" + Integer.toString(this.currentWorkingMemOffset));
          buf.setContainer(workingMemBuf);
          buf.setOffset(this.currentWorkingMemOffset);
          buf.setSize(bufSize);
          buf.setType(currentEdge.getDataType().toString());
          buf.setTypeSize(this.dataTypes.get(currentEdge.getDataType().toString()).getSize());
          this.currentWorkingMemOffset += bufSize * this.dataTypes.get(currentEdge.getDataType().toString()).getSize();
          // p("Internal working buffer " + buf.getName());
          this.linkHSDFEdgeBuffer.put(currentEdge, buf);
        }
        if (port.getDirection().toString().equals("Input") == true) {
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
    final Logger logger = WorkflowLogger.getLogger();
    logger.log(Level.INFO, s);
  }
}
