/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
/**
 *
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.LongVertexPropertyType;
import org.ietr.dftools.algorithm.model.types.StringEdgePropertyType;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.MapperEdgeFactory;
import org.ietr.preesm.mapper.model.MapperVertexFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionParameter;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * @author farresti
 *
 */
public class StaticPiMM2MapperDAGVisitor extends PiMMSwitch<Boolean> {
  /** The result. */
  // SRDAG graph created from the outer graph
  private final MapperDAG result;

  // The factories
  private final MapperVertexFactory vertexFactory;

  /** The current SDF refinement. */
  protected IRefinement currentRefinement;

  /** The scenario. */
  private final PreesmScenario scenario;

  /**
   * Instantiates a new StaticPiMM2MapperDAGVisitor
   *
   * @param piGraph
   *          The Single-Rate Acyclic PiGraph to convert to MapperDAG
   * @param scenario
   *          The scenario
   */
  public StaticPiMM2MapperDAGVisitor(final PiGraph piGraph, final PreesmScenario scenario) {
    this.result = new MapperDAG(new MapperEdgeFactory(), piGraph);
    this.vertexFactory = MapperVertexFactory.getInstance();
    this.scenario = scenario;
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
    vertex.setName(actor.getName());
    // Handle vertex's path inside the graph hierarchy
    vertex.setInfo(actor.getVertexPath());
    // Handle ID
    vertex.setId(actor.getName());
    // Set Repetition vector to 1 since it is a single rate vertex
    vertex.setNbRepeat(new LongVertexPropertyType(1));
    // Set default time property
    vertex.setTime(new LongVertexPropertyType(0));
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
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.
   * AbstractActor)
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGVertex.DAG_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
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
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitBroadcastActor(org.ietr.preesm.experiment.model.pimm.
   * BroadcastActor)
   */
  @Override
  public Boolean caseBroadcastActor(final BroadcastActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory
        .createVertex(DAGBroadcastVertex.DAG_BROADCAST_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Set the special type of the Broadcast
    vertex.getPropertyBean().setValue(DAGBroadcastVertex.SPECIAL_TYPE, DAGBroadcastVertex.SPECIAL_TYPE_BROADCAST);
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitRoundBufferActor(org.ietr.preesm.experiment.model.pimm.
   * RoundBufferActor)
   */
  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory
        .createVertex(DAGBroadcastVertex.DAG_BROADCAST_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Set the special type of the RoundBufferActor
    vertex.getPropertyBean().setValue(DAGBroadcastVertex.SPECIAL_TYPE, DAGBroadcastVertex.SPECIAL_TYPE_ROUNDBUFFER);
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitJoinActor(org.ietr.preesm.experiment.model.pimm.
   * JoinActor)
   */
  @Override
  public Boolean caseJoinActor(final JoinActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGJoinVertex.DAG_JOIN_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Check Join use
    if (actor.getDataOutputPorts().size() > 1) {
      final String message = "Join actors should have only one output. Bad use on [" + actor.getVertexPath() + "]";
      throw new WorkflowException(message);
    }
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitForkActor(org.ietr.preesm.experiment.model.pimm.
   * ForkActor)
   */
  @Override
  public Boolean caseForkActor(final ForkActor actor) {
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGForkVertex.DAG_FORK_VERTEX);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Check Fork use
    if (actor.getDataInputPorts().size() > 1) {
      final String message = "Fork actors should have only one input. Bad use on [" + actor.getVertexPath() + "]";
      throw new WorkflowException(message);
    }
    // Handle input parameters as instance arguments
    setArguments(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitDelayActor(org.ietr.preesm.experiment.model.pimm.
   * DelayActor)
   */
  @Override
  public Boolean caseDelayActor(final DelayActor actor) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitInitActor(org.ietr.preesm.experiment.model.pimm.
   * InitActor)
   */
  @Override
  public Boolean caseInitActor(final InitActor actor) {
    final DAGVertex vertex = vertexFactory.createVertex(DAGInitVertex.DAG_INIT_VERTEX);
    final DataOutputPort dataOutputPort = actor.getDataOutputPorts().get(0);

    // Set the number of delay
    vertex.getPropertyBean().setValue(DAGInitVertex.INIT_SIZE, dataOutputPort.getPortRateExpression().evaluate());
    vertex.setId(actor.getName());
    vertex.setName(actor.getName());
    vertex.setInfo(actor.getName());
    vertex.setNbRepeat(new LongVertexPropertyType(1));

    // Set the PERSISTENCE_LEVEL property
    vertex.setPropertyValue(DAGInitVertex.PERSISTENCE_LEVEL, actor.getLevel());

    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitEndActor(org.ietr.preesm.experiment.model.pimm.
   * EndActor)
   */
  @Override
  public Boolean caseEndActor(final EndActor actor) {
    final DAGVertex vertex = vertexFactory.createVertex(DAGEndVertex.DAG_END_VERTEX);

    vertex.setId(actor.getName());
    vertex.setName(actor.getName());
    vertex.setInfo(actor.getName());
    vertex.setNbRepeat(new LongVertexPropertyType(1));

    final String delayInitID = actor.getEndReference();
    // Handle the END_REFERENCE property
    final DAGVertex initVertex = this.result.getVertex(delayInitID);
    if (initVertex != null) {
      initVertex.getPropertyBean().setValue(DAGInitVertex.END_REFERENCE, vertex.getName());
      vertex.getPropertyBean().setValue(DAGInitVertex.END_REFERENCE, delayInitID);
    }

    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    return true;
  }

  /**
   * Set the arguments of a DAG Vertex from a PiMM actor properties.
   *
   * @param actor
   *          the PiMM Actor
   * @param vertex
   *          the MapperDAG vertex
   */
  private void setArguments(final AbstractActor actor, final MapperDAGVertex vertex) {
    for (final ConfigInputPort p : actor.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue((param.getExpression().getExpressionAsString()));
        vertex.getArguments().addArgument(arg);
      }
    }
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement refinement) {
    throw new UnsupportedOperationException("Flat single-rate can not have PiSDFRefinement for a vertex.");
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    if (fifo.getSourcePort() == null) {
      throw new UnsupportedOperationException(
          fifo.getTargetPort().getName() + " from " + fifo.getTargetPort().getContainingActor().getName());
    }
    final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
    final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
    final String sourceName = sourceActor.getName();
    final String targetName = targetActor.getName();
    // 0. Find source / target vertex corresponding to this edge
    final DAGVertex sourceVertex = this.result.getVertex(sourceName);
    final DAGVertex targetVertex = this.result.getVertex(targetName);

    if (sourceVertex == null) {
      throw new UnsupportedOperationException("Can not create edge, sourceVertex [" + sourceName + "] not found.");
    }
    if (targetVertex == null) {
      throw new UnsupportedOperationException("Can not create edge, targetVertex [" + targetName + "] not found.");
    }

    // 1. Create the edge
    // 1.1 Retrieve the rate
    final long weight = fifo.getSourcePort().getPortRateExpression().evaluate();
    // 1.2 Add an edge between the sourceVertex and the targetVertex in the MapperDAG
    final DAGEdge edge = this.result.addEdge(sourceVertex, targetVertex);
    // 1.3 For the rest of the workflow we need EdgeAggregation so...
    final DAGEdge newEdge = new DAGEdge();
    final String sourceModifier = getAnnotationFromPort(fifo.getSourcePort());
    final String targetModifier = getAnnotationFromPort(fifo.getTargetPort());
    if (!sourceModifier.isEmpty()) {
      newEdge.setSourcePortModifier(new StringEdgePropertyType(sourceModifier));
    }
    if (!targetModifier.isEmpty()) {
      newEdge.setTargetPortModifier(new StringEdgePropertyType(targetModifier));
    }
    // 1.4 Set the different properties of the Edge
    final long dataSize = this.scenario.getSimulationManager().getDataTypeSizeOrDefault(fifo.getType());
    newEdge.setPropertyValue(SDFEdge.DATA_TYPE, fifo.getType());
    newEdge.setPropertyValue(SDFEdge.DATA_SIZE, dataSize);
    newEdge.setWeight(new LongEdgePropertyType(weight));
    newEdge.setSourceLabel(fifo.getSourcePort().getName());
    newEdge.setTargetLabel(fifo.getTargetPort().getName());
    newEdge.setPropertyValue(AbstractEdge.BASE, this.result);
    newEdge.setContainingEdge(edge);

    edge.getAggregate().add(newEdge);
    edge.setWeight(new LongEdgePropertyType(weight * dataSize));

    return true;
  }

  /**
   * Convert annotations from to.
   *
   * @param piPort
   *          the pi port
   * @param edge
   *          the edge
   * @param property
   *          the property
   */
  private String getAnnotationFromPort(final DataPort piPort) {
    switch (piPort.getAnnotation()) {
      case READ_ONLY:
        return SDFEdge.MODIFIER_READ_ONLY;
      case WRITE_ONLY:
        return SDFEdge.MODIFIER_WRITE_ONLY;
      case UNUSED:
        return SDFEdge.MODIFIER_UNUSED;
      default:
        return "";
    }
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

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface actor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface actor) {
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

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // If there is no actor we leave
    if (graph.getActors().isEmpty()) {
      throw new UnsupportedOperationException(
          "Can not convert an empty graph. Check the refinement for [" + graph.getVertexPath() + "].");
    }

    // Check that we are indeed in a flat graph
    if (!graph.getChildrenGraphs().isEmpty()) {
      throw new UnsupportedOperationException("This method is not applicable for non flatten PiMM Graphs.");
    }

    // Convert vertices
    for (final AbstractActor actor : graph.getActors()) {
      doSwitch(actor);
    }

    // Convert FIFOs
    for (final Fifo fifo : graph.getFifos()) {
      doSwitch(fifo);
    }
    return true;
  }
}
