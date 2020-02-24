/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
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
package org.preesm.algorithm.pisdf.pimm2srdag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.clustering.Clustering;
import org.preesm.algorithm.codegen.idl.ActorPrototypes;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.algorithm.codegen.model.CodeGenArgument;
import org.preesm.algorithm.codegen.model.CodeGenParameter;
import org.preesm.algorithm.mapper.graphtransfo.SdfToDagConverter;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.MapperVertexFactory;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.IRefinement;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.parameters.Argument;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.types.LongEdgePropertyType;
import org.preesm.algorithm.model.types.LongVertexPropertyType;
import org.preesm.algorithm.model.types.StringEdgePropertyType;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.model.PreesmCopyTracker;
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
import org.preesm.model.pisdf.FunctionArgument;
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
import org.preesm.model.scenario.Constraints;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * @author farresti
 *
 */
public class StaticPiMM2MapperDAGVisitor extends PiMMSwitch<Boolean> {

  /**
   *
   */
  public static final MapperDAG convert(final PiGraph piGraph, final Design architecture, final Scenario scenario) {
    final StaticPiMM2MapperDAGVisitor visitor = new StaticPiMM2MapperDAGVisitor(piGraph, architecture, scenario);
    visitor.doSwitch(piGraph);
    return visitor.getResult();
  }

  /** The result. */
  // SRDAG graph created from the outer graph
  private final MapperDAG result;

  // The factories
  private final MapperVertexFactory vertexFactory;

  /** The current SDF refinement. */
  private IRefinement currentRefinement;

  /** The scenario. */
  private final Scenario scenario;

  private final Design architecture;

  /**
   * Instantiates a new StaticPiMM2MapperDAGVisitor
   *
   * @param piGraph
   *          The Single-Rate Acyclic PiGraph to convert to MapperDAG
   * @param scenario
   *          The scenario
   */
  private StaticPiMM2MapperDAGVisitor(final PiGraph piGraph, final Design architecture, final Scenario scenario) {
    this.architecture = architecture;
    this.result = new MapperDAG(piGraph);
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

    vertex.setId(actor.getName());
    vertex.setName(actor.getName());
    vertex.setInfo(actor.getVertexPath());
    vertex.setNbRepeat(new LongVertexPropertyType(1));
    vertex.setPropertyValue(Clustering.PISDF_REFERENCE_ACTOR, actor);

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
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGVertex.DAG_VERTEX, actor);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Set hierarchical refinement if AbstractActor is a cluster
    if (actor.isCluster()) {
      vertex.setPropertyValue(Clustering.PISDF_ACTOR_IS_CLUSTER, true);
    }
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
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(DAGVertex.DAG_VERTEX, actor);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Handle path to memory script of the vertex
    if (actor.getMemoryScriptPath() != null) {
      vertex.setPropertyValue(SDFVertex.MEMORY_SCRIPT, new Path(actor.getMemoryScriptPath()).toOSString());
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
        .createVertex(MapperDAGVertex.DAG_BROADCAST_VERTEX, actor);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Set the special type of the Broadcast
    vertex.getPropertyBean().setValue(MapperDAGVertex.SPECIAL_TYPE, MapperDAGVertex.SPECIAL_TYPE_BROADCAST);
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
        .createVertex(MapperDAGVertex.DAG_BROADCAST_VERTEX, actor);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Set the special type of the RoundBufferActor
    vertex.getPropertyBean().setValue(MapperDAGVertex.SPECIAL_TYPE, MapperDAGVertex.SPECIAL_TYPE_ROUNDBUFFER);
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
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(MapperDAGVertex.DAG_JOIN_VERTEX,
        actor);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Check Join use
    if (actor.getDataOutputPorts().size() > 1) {
      final String message = "Join actors should have only one output. Bad use on [" + actor.getVertexPath() + "]";
      throw new PreesmRuntimeException(message);
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
    final MapperDAGVertex vertex = (MapperDAGVertex) this.vertexFactory.createVertex(MapperDAGVertex.DAG_FORK_VERTEX,
        actor);
    // Set default properties from the PiMM actor
    setDAGVertexPropertiesFromPiMM(actor, vertex);
    // Check Fork use
    if (actor.getDataInputPorts().size() > 1) {
      final String message = "Fork actors should have only one input. Bad use on [" + actor.getVertexPath() + "]";
      throw new PreesmRuntimeException(message);
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
    final DAGVertex vertex = this.vertexFactory.createVertex(MapperDAGVertex.DAG_INIT_VERTEX, actor);
    final DataOutputPort dataOutputPort = actor.getDataOutputPorts().get(0);

    // Set the number of delay
    vertex.getPropertyBean().setValue(MapperDAGVertex.INIT_SIZE, dataOutputPort.getPortRateExpression().evaluate());

    setDAGVertexPropertiesFromPiMM(actor, vertex);

    // Set the PERSISTENCE_LEVEL property
    vertex.setPropertyValue(MapperDAGVertex.PERSISTENCE_LEVEL, actor.getLevel());

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
    final DAGVertex vertex = this.vertexFactory.createVertex(MapperDAGVertex.DAG_END_VERTEX, actor);

    setDAGVertexPropertiesFromPiMM(actor, vertex);

    final AbstractActor initReference = actor.getInitReference();
    final String delayInitID = initReference.getName();
    // Handle the END_REFERENCE property
    final DAGVertex initVertex = this.result.getVertex(delayInitID);
    if (initVertex != null) {
      initVertex.getPropertyBean().setValue(MapperDAGVertex.END_REFERENCE, vertex.getName());
      vertex.getPropertyBean().setValue(MapperDAGVertex.END_REFERENCE, delayInitID);
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
    throw new UnsupportedOperationException("Flat single-rate can not have children graph as refinement (see vertex "
        + refinement.getRefinementContainer() + ").");
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
    final long dataSize = this.scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifo.getType());
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
  private Prototype currentPrototype;

  /** The current Argument and Parameter. */
  private CodeGenArgument currentArgument;

  /** The current parameter. */
  private CodeGenParameter currentParameter;

  @Override
  public Boolean caseCHeaderRefinement(final CHeaderRefinement h) {
    final String osStringPath = Optional.ofNullable(h.getFilePath()).map(s -> new Path(s).toOSString()).orElse(null);

    final ActorPrototypes actorPrototype = new ActorPrototypes(osStringPath);
    if (osStringPath != null) {
      doSwitch(h.getLoopPrototype());
      actorPrototype.setLoopPrototype(this.currentPrototype);

      if (h.getInitPrototype() != null) {
        doSwitch(h.getInitPrototype());
        actorPrototype.setInitPrototype(this.currentPrototype);
      }
    }
    this.currentRefinement = actorPrototype;
    return true;
  }

  @Override
  public Boolean caseFunctionPrototype(final FunctionPrototype f) {
    this.currentPrototype = new Prototype(f.getName());
    for (final FunctionArgument p : f.getArguments()) {
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
  public Boolean caseFunctionArgument(final FunctionArgument f) {
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

  /**
   * Update the Scenario with timing/mapping constraints for copyActor.
   */
  private static final void updateScenarioData(final AbstractActor copyActor, final Scenario scenario) {
    final AbstractActor actor = PreesmCopyTracker.getOriginalSource(copyActor);
    // Add the scenario constraints
    final List<ComponentInstance> currentOperatorIDs = new ArrayList<>();
    final List<Entry<ComponentInstance, EList<AbstractActor>>> constraintGroups = scenario.getConstraints()
        .getGroupConstraints();
    for (final Entry<ComponentInstance, EList<AbstractActor>> cg : constraintGroups) {
      final List<AbstractActor> vertexPaths = cg.getValue();
      final ComponentInstance operatorId = cg.getKey();
      if (vertexPaths.contains(actor)) {
        currentOperatorIDs.add(operatorId);
      }
    }

    final Constraints constraintGroupManager = scenario.getConstraints();
    currentOperatorIDs.forEach(s -> constraintGroupManager.addConstraint(s, copyActor));
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {

    if (graph.isCluster()) {
      return caseAbstractActor(graph);
    }

    checkInput(graph);

    // Convert vertices
    for (final AbstractActor actor : graph.getActors()) {
      StaticPiMM2MapperDAGVisitor.updateScenarioData(actor, this.scenario);
      doSwitch(actor);
    }

    // Convert FIFOs
    for (final Fifo fifo : graph.getFifos()) {
      doSwitch(fifo);
    }

    // 6. Aggregate edges
    aggregateEdges(this.result);

    SdfToDagConverter.addInitialProperties(this.result, this.architecture, this.scenario);
    return true;
  }

  private void checkInput(final PiGraph graph) {
    // If there is no actor we leave
    if (graph.getActors().isEmpty()) {
      throw new UnsupportedOperationException(
          "Can not convert an empty graph. Check the refinement for [" + graph.getVertexPath() + "].");
    }

    // Check that we are indeed in a flat graph
    for (PiGraph childrenGraph : graph.getChildrenGraphs()) {
      if (!childrenGraph.isCluster()) {
        throw new UnsupportedOperationException("This method is not applicable for non flatten PiMM Graphs.");
      }
    }

  }

  /**
   * Creates edge aggregate for all multi connection between two vertices.
   *
   * @param dag
   *          the dag on which to perform
   */
  private void aggregateEdges(final MapperDAG dag) {
    for (final DAGVertex vertex : dag.vertexSet()) {
      // List of extra edges to remove
      final ArrayList<DAGEdge> toRemove = new ArrayList<>();
      for (final DAGEdge edge : vertex.incomingEdges()) {
        final DAGVertex source = edge.getSource();
        // Maybe doing the copy is not optimal
        final ArrayList<DAGEdge> allEdges = new ArrayList<>(dag.getAllEdges(source, vertex));
        // if there is only one connection no need to modify anything
        if ((allEdges.size() == 1) || toRemove.contains(edge)) {
          continue;
        }
        // Get the first edge
        final DAGEdge firstEdge = allEdges.remove(0);
        for (final DAGEdge extraEdge : allEdges) {
          // Update the weight
          firstEdge.setWeight(
              new LongEdgePropertyType(firstEdge.getWeight().longValue() + extraEdge.getWeight().longValue()));
          // Add the aggregate edge
          firstEdge.getAggregate().add(extraEdge.getAggregate().get(0));
          toRemove.add(extraEdge);
        }
      }
      // Removes the extra edges
      toRemove.forEach(dag::removeEdge);
    }
  }
}
