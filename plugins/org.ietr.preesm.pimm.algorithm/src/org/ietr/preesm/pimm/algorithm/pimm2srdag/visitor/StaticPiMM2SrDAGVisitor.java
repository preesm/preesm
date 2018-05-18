/**
 *
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultVertexPropertyType;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.MapperVertexFactory;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;

/**
 * @author farresti
 *
 */
public class StaticPiMM2SrDAGVisitor extends PiMMSwitch<Boolean> {
  /** Property name for property TARGET_VERTEX. */
  public static final String PARENT_DAG_VERTEX = "parent_dag_vertex";

  /** The result. */
  // SRDAG graph created from the outer graph
  private final MapperDAG result;

  // The factories
  private final MapperVertexFactory vertexFactory;

  /** Basic repetition vector of the graph */
  private final Map<AbstractVertex, Long> brv;

  /** Map from original PiMM vertices to generated DAG vertices */
  private final Map<AbstractVertex, ArrayList<MapperDAGVertex>> pimmVertex2DAGVertex = new LinkedHashMap<>();

  /** The current SDF refinement. */
  protected IRefinement currentRefinement;

  /** The scenario. */
  private final PreesmScenario scenario;

  /** Current Graph name */
  String graphName;

  /** Current actor name */
  String currentActorName;

  /**
   * Instantiates a new abstract pi MM 2 SR-DAG visitor.
   *
   * @param dag
   *          the dag
   */
  public StaticPiMM2SrDAGVisitor(final MapperDAG dag, final Map<AbstractVertex, Long> brv, final PreesmScenario scenario) {
    this.result = dag;
    this.brv = brv;
    this.vertexFactory = MapperVertexFactory.getInstance();
    this.scenario = scenario;
    this.graphName = "";
  }

  /**
   * Convert a PiMM AbstractActor to a DAGVertex.
   *
   * @param actor
   *          the AbstractActor
   * @return the DAGVertex
   */
  private void setDAGVertexPropertiesFromPiMM(final AbstractActor actor, final DAGVertex vertex) {
    // Handle vertex's name
    vertex.setName(this.currentActorName);
    // Handle vertex's path inside the graph hierarchy
    vertex.setInfo(actor.getVertexPath());
    // Handle ID
    vertex.setId(actor.getName());
    // Set Repetition vector to 1 since it is a single rate vertex
    vertex.setNbRepeat(new DAGDefaultVertexPropertyType(1));
    // Set default time property
    vertex.setTime(new DAGDefaultVertexPropertyType(0));
    // Adds the list of source ports
    for (final DataInputPort port : actor.getDataInputPorts()) {
      vertex.addSourceName(port.getName());
    }
    // Adds the list of sink ports
    for (final DataOutputPort port : actor.getDataOutputPorts()) {
      vertex.addSinkName(port.getName());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.AbstractActor)
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGVertex.DAG_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    // Update the map of PiMM actor to DAG Vertices
    this.pimmVertex2DAGVertex.get(actor).add(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitActor(org.ietr.preesm.experiment.model.pimm.Actor)
   */
  @Override
  public Boolean caseActor(final Actor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGVertex.DAG_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Handle path to memory script of the vertex
    if (actor.getMemoryScriptPath() != null) {
      vertex.setPropertyValue(SDFVertex.MEMORY_SCRIPT, actor.getMemoryScriptPath().toOSString());
    }
    // Handle vertex's refinement (description of the vertex's behavior:
    // function prototypes or subgraphs)
    final Refinement piRefinement = actor.getRefinement();
    doSwitch(piRefinement);
    vertex.setRefinement(this.currentRefinement);
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    // Update the map of PiMM actor to DAG Vertices
    this.pimmVertex2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGBroadcastVertex.DAG_BROADCAST_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Check the good use of the broadcast
    final DataInputPort dataInputPort = actor.getDataInputPorts().get(0);
    final Expression portRateExpression = dataInputPort.getPortRateExpression();
    final long cons = Long.parseLong(portRateExpression.getExpressionString());
    for (final DataOutputPort out : actor.getDataOutputPorts()) {
      final Expression outPortRateExpression = out.getPortRateExpression();
      final long prod = Long.parseLong(outPortRateExpression.getExpressionString());
      if (prod != cons) {
        final String msg = "Warning: Broadcast [" + actor.getVertexPath() + "] have different production/consumption: prod(" + Long.toString(prod)
            + ") != cons(" + Long.toString(cons) + ")";
        WorkflowLogger.getLogger().warning(msg);
      }
    }
    // Set the special type of the Broadcast
    vertex.getPropertyBean().setValue(DAGBroadcastVertex.SPECIAL_TYPE, DAGBroadcastVertex.SPECIAL_TYPE_BROADCAST);
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    // Update the map of PiMM actor to DAG Vertices
    this.pimmVertex2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGJoinVertex.DAG_JOIN_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Check Join use
    if (actor.getDataOutputPorts().size() > 1) {
      final String message = "Join actors should have only one output. Bad use on [" + actor.getVertexPath() + "]";
      WorkflowLogger.getLogger().severe(message);
      throw new RuntimeException(message);
    }
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    // Update the map of PiMM actor to DAG Vertices
    this.pimmVertex2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGForkVertex.DAG_FORK_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Check Fork use
    if (actor.getDataInputPorts().size() > 1) {
      final String message = "Fork actors should have only one input. Bad use on [" + actor.getVertexPath() + "]";
      WorkflowLogger.getLogger().severe(message);
      throw new RuntimeException(message);
    }
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    // Update the map of PiMM actor to DAG Vertices
    this.pimmVertex2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface actor) {
    final String name = this.graphName;
    final MapperDAGVertex vertex = (MapperDAGVertex) this.result.getVertex(name);
    if (vertex == null) {
      throw new RuntimeException("Failed to convert PiMM 2 SR-DAG.\nVertex [" + name + "] not found.");
    }
    // Update the map of PiMM actor to DAG Vertices
    this.pimmVertex2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface actor) {
    final String name = this.graphName;
    final MapperDAGVertex vertex = (MapperDAGVertex) this.result.getVertex(name);
    if (vertex == null) {
      throw new RuntimeException("Failed to convert PiMM 2 SR-DAG.\nVertex [" + name + "] not found.");
    }
    // Update the map of PiMM actor to DAG Vertices
    this.pimmVertex2DAGVertex.get(actor).add(vertex);
    return true;
  }

  /**
   * Set the arguments of a DAG Vertex from a PiMM actor properties.
   *
   * @param actor
   *          the PiMM Actor
   * @param vertex
   *          the DAG vertex
   */
  private void setArguments(final AbstractActor actor, final MapperDAGVertex vertex) {
    for (final ConfigInputPort p : actor.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue((param.getExpression().getExpressionString()));
        vertex.getArguments().addArgument(arg);
      }
    }
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement refinement) {
    this.currentRefinement = new CodeRefinement(refinement.getFilePath());
    return true;
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    final SRVerticesLinker srVerticesLinker = new SRVerticesLinker(fifo, this.result, this.scenario);
    try {
      srVerticesLinker.execute(this.brv, this.pimmVertex2DAGVertex);
    } catch (final PiMMHelperException e) {
      throw new RuntimeException(e.getMessage());
    }
    return true;
  }

  /** The current prototype. */
  protected Prototype currentPrototype;

  /** The current Argument and Parameter. */
  protected CodeGenArgument currentArgument;

  /** The current parameter. */
  protected CodeGenParameter currentParameter;

  @Override
  public Boolean caseCHeaderRefinement(final CHeaderRefinement h) {
    final ActorPrototypes actorPrototype = new ActorPrototypes(h.getFilePath().toOSString());

    doSwitch(h.getLoopPrototype());
    actorPrototype.setLoopPrototype(this.currentPrototype);

    if (h.getInitPrototype() != null) {
      doSwitch(h.getInitPrototype());
      actorPrototype.setInitPrototype(this.currentPrototype);
    }

    this.currentRefinement = actorPrototype;
    return true;
  }

  @Override
  public Boolean caseFunctionPrototype(final FunctionPrototype f) {
    this.currentPrototype = new Prototype(f.getName());
    for (final FunctionParameter p : f.getParameters()) {
      doSwitch(p);
      if (p.isIsConfigurationParameter()) {
        this.currentPrototype.addParameter(this.currentParameter);
      } else {
        this.currentPrototype.addArgument(this.currentArgument);
      }
    }
    return true;
  }

  @Override
  public Boolean caseFunctionParameter(final FunctionParameter f) {
    if (f.isIsConfigurationParameter()) {
      int direction = 0;
      switch (f.getDirection()) {
        case IN:
          direction = 0;
          break;
        case OUT:
          direction = 1;
          break;
        default:
      }
      this.currentParameter = new CodeGenParameter(f.getName(), direction);
    } else {
      String direction = "";
      switch (f.getDirection()) {
        case IN:
          direction = CodeGenArgument.INPUT;
          break;
        case OUT:
          direction = CodeGenArgument.OUTPUT;
          break;
        default:
      }
      this.currentArgument = new CodeGenArgument(f.getName(), direction);
      this.currentArgument.setType(f.getType());
    }
    return true;
  }

  /**
   * Gets the result.
   *
   * @return the result
   */
  public MapperDAG getResult() {
    return this.result;
  }

  /**
   * Methods below are unused and unimplemented visit methods.
   */

  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataOutputPort(final DataOutputPort dop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataInputPort(final DataInputPort dip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort cip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigOutputPort(final ConfigOutputPort cop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor ea) {
    throw new UnsupportedOperationException();
  }

  /**
   * Convert a PiMM actor to a set of DAG Vertices w.r.t the repetition factor of the actor.
   *
   * @param actor
   *          the actor
   */
  private void convertPiMMActor2DAGVertices(final AbstractVertex actor) {
    // Ignore delay actors, after this transformation, they no longer serve any purpose.
    if (actor instanceof DelayActor) {
      return;
    }
    // Add a new entry in the map for the current actor
    this.pimmVertex2DAGVertex.put(actor, new ArrayList<>());
    // Populate the DAG with the appropriate number of instances of the actor
    final Long actorNBRepeat = this.brv.get(actor);
    // We treat hierarchical graphs as normal actors
    // This populate the DAG with the right amount of hierarchical instances w.r.t the BRV value
    final String prefixGraphName = this.graphName.isEmpty() ? "" : this.graphName + "_";
    if (actorNBRepeat > 1) {
      for (long i = 0; i < actorNBRepeat; ++i) {
        // Setting the correct name
        this.currentActorName = prefixGraphName + actor.getName() + "_" + Long.toString(i);
        doConvert(actor);
      }
    } else {
      // In this case we don't need to add number to names
      this.currentActorName = prefixGraphName + actor.getName();
      doConvert(actor);
    }
  }

  /**
   * Populate the DAG with 1 instance of the PiMM actor.
   *
   * @param actor
   *          the actor
   */
  public void doConvert(final AbstractVertex actor) {
    if (actor instanceof PiGraph) {
      caseAbstractActor((AbstractActor) actor);
    } else {
      doSwitch(actor);
    }
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // If there is no actor we leave
    if (graph.getActors().isEmpty()) {
      throw new UnsupportedOperationException("Can not convert an empty graph. Check the refinement for [" + graph.getVertexPath() + "].");
    }

    // Add SR-Vertices
    for (final AbstractActor actor : graph.getActors()) {
      // If the actor is an interface, its repetition factor is 1.
      if (actor instanceof InterfaceActor) {
        this.brv.put(actor, (long) 1);
      }
      convertPiMMActor2DAGVertices(actor);
    }

    // Link SR-Vertices
    for (final Fifo fifo : graph.getFifos()) {
      doSwitch(fifo);
    }

    // Save the current graph name
    final String backupGraphName = this.graphName;
    // Go check hierarchical graphs
    // The hierarchy gets flatten as we go deeper
    for (final PiGraph g : graph.getChildrenGraphs()) {
      final ArrayList<MapperDAGVertex> graphVertices = this.pimmVertex2DAGVertex.get(g);
      final ArrayList<String> graphNames = new ArrayList<>();
      graphVertices.forEach(v -> graphNames.add(v.getName()));
      for (final String graphVertexName : graphNames) {
        this.graphName = graphVertexName;
        // Visit the subgraph
        doSwitch(g);
        // Remove the hierarchical graph
        removeInstanceOfGraph();
      }
    }
    // Restore the graph name
    this.graphName = backupGraphName;
    return true;
  }

  /**
   * Removes the instance of the PiGraph of the DAG
   */
  private void removeInstanceOfGraph() {
    final DAGVertex vertex = this.result.getVertex(this.graphName);
    if (vertex != null) {
      this.result.removeVertex(vertex);
    }
  }
}
