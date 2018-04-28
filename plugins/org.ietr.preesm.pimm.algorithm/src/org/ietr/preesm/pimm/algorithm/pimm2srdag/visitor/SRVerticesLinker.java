/**
 *
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultEdgePropertyType;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultVertexPropertyType;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.MapperVertexFactory;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;

/**
 *
 * Link SR DAG vertices
 *
 * @author farresti
 *
 */
public class SRVerticesLinker {

  /** Property name for property JOIN_VERTEX. */
  public static final String JOIN_VERTEX = "_Join_";

  /** Property name for property FORK_VERTEX. */
  public static final String FORK_VERTEX = "_Fork_";

  /** Property name for property PIMM_FIFO. */
  public static final String PIMM_FIFO = "pimm_fifo";

  // Number of delays
  private final long delays;

  // Source actor
  private final AbstractActor source;
  // Source Port
  private final DataOutputPort sourcePort;

  // Sink Actor
  private final AbstractActor sink;
  // Sink Port
  private final DataInputPort sinkPort;

  // The fifo
  private final Fifo fifo;

  // The fifo data size
  private final int dataSize;

  // The DAG in which we operate
  private final MapperDAG dag;

  // Counters for unique ID creation of Forks/Joins
  private int joinIDCounter;
  private int forkIDCounter;

  // Port modifiers annotations
  private final String sourceModifier;
  private final String targetModifier;

  /**
   * Retrieve the number of delay tokens contain in a fifo, if any
   *
   * @param fifo
   *          the fifo
   * @return number of delay, 0 if the fifo does not contain any delay
   */
  private static long getNDelays(final Fifo fifo) {
    final Delay delay = fifo.getDelay();
    if (delay == null) {
      return 0;
    }
    // Get the number of delay
    final Expression sizeExpression = fifo.getDelay().getSizeExpression();
    final long nDelays = Long.parseLong(sizeExpression.getExpressionString());
    // Sanity check on delay value
    final DataInputPort targetPort = fifo.getTargetPort();
    final Expression portRateExpression = targetPort.getPortRateExpression();
    final long targetRate = Long.parseLong(portRateExpression.getExpressionString());
    if (nDelays < 0) {
      throw new RuntimeException("Invalid number of delay on fifo[" + fifo.getId() + "]: " + Long.toString(nDelays));
    } else if (nDelays < targetRate) {
      throw new RuntimeException("Insuffisiant number of delay on fifo[" + fifo.getId() + "]: number of delays: " + Long.toString(nDelays) + ", consumption: "
          + Long.toString(targetRate));
    }
    return nDelays;
  }

  /**
   * Get the repetition value for the sink / source actor
   * 
   * @param actor
   *          the sink actor
   * @param brv
   *          the basic repetition vector map
   * @return repetition value of the actor, 1 if it is an interface actor
   */
  private static long getRVorDefault(final AbstractActor actor, final Map<AbstractVertex, Long> brv) {
    if (actor instanceof InterfaceActor) {
      return 1;
    }
    return (long) (brv.get(actor));
  }

  /**
   * Constructor for the SR linker
   *
   * @param fifo
   *          the fifo to link
   * @param dag
   *          the dag in which we operate
   * @param scenario
   *          the scenario. Used to retrieve data size of the fifo
   */
  public SRVerticesLinker(final Fifo fifo, final MapperDAG dag, final PreesmScenario scenario) {
    this.fifo = fifo;
    this.delays = SRVerticesLinker.getNDelays(fifo);
    this.source = fifo.getSourcePort().getContainingActor();
    this.sink = fifo.getTargetPort().getContainingActor();
    this.sourcePort = fifo.getSourcePort();
    this.sinkPort = fifo.getTargetPort();
    final String type = fifo.getType();
    this.dataSize = scenario.getSimulationManager().getDataTypeSizeOrDefault(type);
    this.dag = dag;
    this.joinIDCounter = 0;
    this.forkIDCounter = 0;
    this.sourceModifier = getAnnotationFromPort(this.sourcePort);
    this.targetModifier = getAnnotationFromPort(this.sinkPort);
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

  /**
   * Do the linkage between the dag vertices
   *
   * @param brv
   *          the basic repetition vector map
   *
   * @param pimm2dag
   *          the map between the pimm vertex and the dag vertex
   *
   *
   * @return true if no error, false else
   */
  public Boolean execute(final Map<AbstractVertex, Long> brv, final Map<AbstractVertex, ArrayList<MapperDAGVertex>> pimm2dag) throws PiMMHelperException {
    // These connections are already dealt with
    if ((this.source instanceof DelayActor) || (this.sink instanceof DelayActor)) {
      return true;
    }

    // List of source vertex
    final ArrayList<Pair<DAGVertex, Long>> sourceSet = getSourceSet(brv, pimm2dag);
    // List of sink vertex
    final ArrayList<Pair<DAGVertex, Long>> sinkSet = getSinkSet(brv, pimm2dag);

    // Connect all the source to the sinks
    connectEdges(sourceSet, sinkSet);

    return true;
  }

  /**
   * Connect the sources to the sinks
   *
   * @param sourceSet
   *          set of dag sources
   * @param sinkSet
   *          set of dag sinks
   */
  private void connectEdges(final ArrayList<Pair<DAGVertex, Long>> sourceSet, final ArrayList<Pair<DAGVertex, Long>> sinkSet) {
    while (!sinkSet.isEmpty()) {
      if (connectSources2Sink(sourceSet, sinkSet)) {
        sinkSet.remove(0);
      }
      if (sourceSet.isEmpty()) {
        break;
      }
      if (connectSinks2Source(sinkSet, sourceSet)) {
        sourceSet.remove(0);
      }

    }
  }

  /**
   * Connect sources to current sink
   *
   * @param sourceSet
   *          set of dag sources
   * @param sink
   *          current sink to connect to
   * @return true if it didn't explode, false else
   */
  private boolean connectSources2Sink(final ArrayList<Pair<DAGVertex, Long>> sourceSet, final ArrayList<Pair<DAGVertex, Long>> sinkSet) {
    final ArrayList<Pair<DAGVertex, Long>> toRemove = new ArrayList<>();
    final Pair<DAGVertex, Long> sinkPair = sinkSet.get(0);
    DAGVertex sinkVertex = sinkPair.getLeft();
    long cons = sinkPair.getRight();
    // Check implode condition
    long prod = sourceSet.get(0).getRight();
    if (cons > prod) {
      final DAGVertex join = createJoinVertex(this.fifo.getId() + SRVerticesLinker.JOIN_VERTEX, MapperVertexFactory.getInstance());
      createEdge(join, sinkVertex, Long.toString(cons));
      sinkVertex = join;
    }
    // Connect the edges
    for (final Pair<DAGVertex, Long> src : sourceSet) {
      prod = src.getRight();
      if ((cons == 0) || (prod > cons)) {
        break;
      }
      toRemove.add(src);
      final DAGVertex sourceVertex = src.getLeft();
      createEdge(sourceVertex, sinkVertex, Long.toString(prod));
      cons = cons - prod;
    }
    toRemove.forEach(sourceSet::remove);
    // Reset the current top sink
    sinkSet.set(0, Pair.of(sinkVertex, cons));
    // Explode condition
    final boolean explode = (prod > cons) && (cons != 0);
    return !explode;
  }

  /**
   * Connect sinks to current source
   *
   * @param sinkSet
   *          set of dag sinks
   * @param source
   *          current source to connect from
   * @return true if it didn't implode, false else
   */
  private boolean connectSinks2Source(final ArrayList<Pair<DAGVertex, Long>> sinkSet, final ArrayList<Pair<DAGVertex, Long>> sourceSet) {
    final ArrayList<Pair<DAGVertex, Long>> toRemove = new ArrayList<>();
    final Pair<DAGVertex, Long> sourcePair = sourceSet.get(0);
    DAGVertex sourceVertex = sourcePair.getLeft();
    long prod = sourcePair.getRight();
    long cons = sinkSet.get(0).getRight();
    // Check explode condition
    if (prod > cons) {
      final DAGVertex fork = createForkVertex(this.fifo.getId() + SRVerticesLinker.FORK_VERTEX, MapperVertexFactory.getInstance());
      createEdge(sourceVertex, fork, Long.toString(prod));
      sourceVertex = fork;
    }
    // Connect the edges
    for (final Pair<DAGVertex, Long> snk : sinkSet) {
      cons = snk.getRight();
      if ((prod == 0) || (cons > prod)) {
        break;
      }
      toRemove.add(snk);
      final DAGVertex sinkVertex = snk.getLeft();
      createEdge(sourceVertex, sinkVertex, Long.toString(cons));
      prod = prod - cons;
    }
    toRemove.forEach(sinkSet::remove);
    // Reset the current top source
    sourceSet.set(0, Pair.of(sourceVertex, prod));
    // Implode condition
    final boolean implode = (cons > prod) && (prod != 0);
    return !implode;
  }

  /**
   * Generate DAG set of sources
   *
   * @param brv
   *          repetition vector values
   * @param pimm2dag
   *          map of PiMM actors and their associated list of dag vertices
   * @return set of dag sources
   * @throws PiMMHelperException
   *           the exception
   */
  private ArrayList<Pair<DAGVertex, Long>> getSourceSet(final Map<AbstractVertex, Long> brv, final Map<AbstractVertex, ArrayList<MapperDAGVertex>> pimm2dag)
      throws PiMMHelperException {
    final ArrayList<Pair<DAGVertex, Long>> sourceSet = new ArrayList<>();

    final MapperVertexFactory vertexFactory = MapperVertexFactory.getInstance();
    final String fifoID = this.fifo.getId();
    // Port expressions
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    final long sourceProduction = Long.parseLong(sourceExpression.getExpressionString());

    // Deals with the delay
    if (this.delays != 0) {
      final Delay delay = this.fifo.getDelay();
      if (delay.hasSetterActor()) {
        final AbstractActor setterActor = this.fifo.getDelay().getSetterActor();
        final ArrayList<MapperDAGVertex> setterActorList = pimm2dag.get(setterActor);
        sourceSet.add(addDelaySetterActor(setterActorList));
      } else {
        // Add an init vertex for the first iteration of the sink actor
        final DAGVertex initVertex = createInitVertex(fifoID + "_Init", vertexFactory);
        addPair(sourceSet, initVertex, this.delays);
      }
    }

    if (this.source instanceof InterfaceActor) {
      // // Port expressions
      final Expression sinkExpression = this.sinkPort.getPortRateExpression();
      final long sinkConsumption = Long.parseLong(sinkExpression.getExpressionString());
      // Retrieve corresponding source vertex
      final DAGVertex vertex = pimm2dag.get(this.source).get(0);
      final DAGVertex sourceVertex = getInterfaceSourceVertex(vertex);
      // Repetition values
      final long sinkRV = SRVerticesLinker.getRVorDefault(this.sink, brv);
      if (sourceProduction == sinkConsumption * sinkRV) {
        // We don't need to use broadcast
        sourceSet.add(Pair.of(sourceVertex, sourceProduction));
      } else {
        final boolean perfectBroadcast = (sinkConsumption * sinkRV) % sourceProduction == 0;
        long nBroadcast = (sinkConsumption * sinkRV) / sourceProduction;
        if (!perfectBroadcast) {
          nBroadcast++;
        }
        DAGVertex broadcastVertex = vertexFactory.createVertex(DAGBroadcastVertex.DAG_BROADCAST_VERTEX);
        setVertexDefault(broadcastVertex, fifoID + "_BroadCast");
        this.dag.addVertex(broadcastVertex);
        createEdge(sourceVertex, broadcastVertex, Long.toString(sourceProduction));
        for (int i = 0; i < nBroadcast; ++i) {
          sourceSet.add(Pair.of(broadcastVertex, sourceProduction));
        }
      }
    } else if (this.source instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the source
      pimm2dag.get(this.source).forEach(v -> addPair(sourceSet, v, sourceProduction));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + this.source.getClass().toString());
    }
    return sourceSet;
  }

  /**
   * Retrieve the source vertex corresponding to current data input interface. <br>
   * The corresponding edge is removed.
   * 
   * @param vertex
   *          the vertex
   * @return the corresponding source vertex
   * @throws PiMMHelperException
   *           the exception
   */
  private DAGVertex getInterfaceSourceVertex(final DAGVertex vertex) throws PiMMHelperException {
    DataInputPort correspondingPort = null;
    for (final DataInputPort port : this.source.getContainingPiGraph().getDataInputPorts()) {
      if (port.getName().equals(this.source.getName())) {
        correspondingPort = port;
        break;
      }
    }
    if (correspondingPort == null) {
      final String message = "Data input port corresponding to interface [" + this.source.getName() + "] not found.";
      throw new PiMMHelperException(message);
    }
    final Fifo correspondingFifo = correspondingPort.getFifo();
    final Set<DAGEdge> incomingEdges = vertex.incomingEdges();
    DAGEdge currentEdge = getInterfaceEdge(correspondingFifo, incomingEdges);
    return currentEdge.getSource();
  }

  /**
   * Adds a new pair to a set
   *
   * @param set
   *          the set
   * @param vertex
   *          left element of the pair to add
   * @param value
   *          right element of the pair to add
   */
  private void addPair(final ArrayList<Pair<DAGVertex, Long>> set, final DAGVertex vertex, final long value) {
    set.add(Pair.of(vertex, value));
  }

  /**
   * Deals with the setter actor of a delay. <br>
   * If the RV of the setter actor is greater than 1, then a join actor is created to connect all of its instances. <br>
   *
   * @param setterActorList
   *          list of sr dag instances of the setter actor
   * @return setter actor if its RV = 1, the join actor created else
   */
  private Pair<DAGVertex, Long> addDelaySetterActor(final ArrayList<MapperDAGVertex> setterActorList) {
    final MapperVertexFactory vertexFactory = MapperVertexFactory.getInstance();
    final Delay delay = this.fifo.getDelay();
    final int setterRV = setterActorList.size();
    if (setterRV > 1) {
      // Add a join vertex for the first iteration of the sink actor
      // Connect all instances of the setter actor to the join actor
      final DAGVertex joinVertex = createJoinVertex(this.fifo.getId() + SRVerticesLinker.JOIN_VERTEX, vertexFactory);
      for (int i = 0; i < setterRV; ++i) {
        final DAGVertex currentSetterActor = setterActorList.get(i);
        final String rateExpression = delay.getSetterPort().getPortRateExpression().getExpressionString();
        createEdge(currentSetterActor, joinVertex, rateExpression);
      }
      return Pair.of(joinVertex, this.delays);
    } else {
      return Pair.of(setterActorList.get(0), this.delays);
    }
  }

  /**
   * Generate DAG set of sinks
   *
   * @param brv
   *          repetition vector values
   * @param pimm2dag
   *          map of PiMM actors and their associated list of dag vertices
   * @return set of dag sink
   * @throws PiMMHelperException
   *           the exception
   */
  private ArrayList<Pair<DAGVertex, Long>> getSinkSet(final Map<AbstractVertex, Long> brv, final Map<AbstractVertex, ArrayList<MapperDAGVertex>> pimm2dag)
      throws PiMMHelperException {
    final ArrayList<Pair<DAGVertex, Long>> sinkSet = new ArrayList<>();

    final MapperVertexFactory vertexFactory = MapperVertexFactory.getInstance();
    final String fifoID = this.fifo.getId();

    // Port expressions
    final Expression sinkExpression = this.sinkPort.getPortRateExpression();
    final long sinkConsumption = Long.parseLong(sinkExpression.getExpressionString());
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    final long sourceProduction = Long.parseLong(sourceExpression.getExpressionString());

    if (this.sink instanceof InterfaceActor) {
      // Repetition values
      final long sourceRV = getRVorDefault(this.source, brv);
      // Retrieve corresponding sink vertex
      final DAGVertex vertex = pimm2dag.get(this.sink).get(0);
      final DAGVertex sinkVertex = getInterfaceSinkVertex(vertex);
      if (sinkConsumption == sourceProduction * sourceRV) {
        sinkSet.add(Pair.of(sinkVertex, sinkConsumption));
      } else {
        final long nDroppedTokens = (sourceProduction * sourceRV) - sinkConsumption;
        long nEnd = (long) Math.ceil((double) nDroppedTokens / sourceProduction);
        for (long i = 0; i < (nEnd - 1); ++i) {
          final DAGVertex endVertex = createEndVertex(fifoID + "_SinkEnd_" + Long.toString(i), vertexFactory);
          sinkSet.add(Pair.of(endVertex, sourceProduction));
        }
        final DAGVertex endVertex = createEndVertex(fifoID + "_SinkEnd_" + Long.toString(nEnd - 1), vertexFactory);
        sinkSet.add(Pair.of(endVertex, nDroppedTokens - (nEnd - 1) * sourceProduction));
        sinkSet.add(Pair.of(sinkVertex, sinkConsumption));
      }
    } else if (this.sink instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the sink
      pimm2dag.get(this.sink).forEach(v -> addPair(sinkSet, v, sinkConsumption));

      // This is only true in the case of an interface
      final long sinkRV = getRVorDefault(this.sink, brv);
      final long leftOver = (sinkConsumption * sinkRV) % sourceProduction;
      final boolean sinkNeedEnd = leftOver != 0;
      if (sinkNeedEnd) {
        // Add an end vertex for the round buffer of the interface
        final DAGVertex endVertex = createEndVertex(fifoID + "_InterfaceEnd", vertexFactory);
        addPair(sinkSet, endVertex, sourceProduction - leftOver);
      }
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + this.sink.getClass().toString());
    }

    // Deals with the delay
    if (this.delays != 0) {
      final Delay delay = this.fifo.getDelay();
      if (delay.hasGetterActor()) {
        final AbstractActor getterActor = this.fifo.getDelay().getGetterActor();
        final ArrayList<MapperDAGVertex> getterActorList = pimm2dag.get(getterActor);
        sinkSet.add(addDelayGetterActor(getterActorList));
      } else {
        // Add an end vertex for the last iteration of the source actor
        final DAGVertex endVertex = createEndVertex(fifoID + "_End", vertexFactory);
        addPair(sinkSet, endVertex, this.delays);
      }
    }
    return sinkSet;
  }

  /**
   * Retrieve the source vertex corresponding to current data input interface. <br>
   * The corresponding edge is removed.
   * 
   * @param vertex
   *          the vertex
   * @return the corresponding sink vertex
   * @throws PiMMHelperException
   *           the exception
   */
  private DAGVertex getInterfaceSinkVertex(final DAGVertex vertex) throws PiMMHelperException {
    DataOutputPort correspondingPort = null;
    for (final DataOutputPort port : this.sink.getContainingPiGraph().getDataOutputPorts()) {
      if (port.getName().equals(this.sink.getName())) {
        correspondingPort = port;
        break;
      }
    }
    if (correspondingPort == null) {
      final String message = "Data output port corresponding to interface [" + this.source.getName() + "] not found.";
      throw new PiMMHelperException(message);
    }
    final Fifo correspondingFifo = correspondingPort.getFifo();
    final Set<DAGEdge> outgoingEdges = vertex.outgoingEdges();
    DAGEdge currentEdge = getInterfaceEdge(correspondingFifo, outgoingEdges);
    return currentEdge.getTarget();
  }

  /**
   * Retrieve the corresponding edge connected to the interface of a PiGraph
   * 
   * @param correspondingFifo
   *          the corresponding fifo to the interface actor and the PiGraph
   * @param setOfEdges
   *          set of edges
   * @return the edge connected to the hierarchical graph to which the interface belongs
   * @throws PiMMHelperException
   */
  private DAGEdge getInterfaceEdge(final Fifo correspondingFifo, final Set<DAGEdge> setOfEdges) throws PiMMHelperException {
    DAGEdge currentEdge = null;
    final String id = correspondingFifo.getId();
    for (final DAGEdge edge : setOfEdges) {
      final String edgeFifoID = edge.getPropertyStringValue(SRVerticesLinker.PIMM_FIFO);
      if (edgeFifoID.equals(id)) {
        currentEdge = edge;
      }
    }
    if (currentEdge == null) {
      final String message = "Edge corresponding to fifo [" + id + "] not found.";
      throw new PiMMHelperException(message);
    }
    return currentEdge;
  }

  /**
   * Deals with the getter actor of a delay. <br>
   * If the RV of the getter actor is greater than 1, then a fork actor is created to connect all of its instances. <br>
   *
   * @param getterActorList
   *          list of sr dag instances of the getter actor
   * @return getter actor if its RV = 1, the fork actor created else
   */
  private Pair<DAGVertex, Long> addDelayGetterActor(final ArrayList<MapperDAGVertex> getterActorList) {
    final MapperVertexFactory vertexFactory = MapperVertexFactory.getInstance();
    final Delay delay = this.fifo.getDelay();
    final int getterRV = getterActorList.size();
    if (getterRV > 1) {
      // Add a fork vertex for the last iteration of the source actor
      // Connect all instances of the getter actor to the fork actor
      final DAGVertex forkVertex = createForkVertex(this.fifo.getId() + SRVerticesLinker.FORK_VERTEX, vertexFactory);
      for (int i = 0; i < getterRV; ++i) {
        final DAGVertex currentGetterActor = getterActorList.get(i);
        final String rateExpression = delay.getGetterPort().getPortRateExpression().getExpressionString();
        createEdge(forkVertex, currentGetterActor, rateExpression);
      }
      return Pair.of(forkVertex, this.delays);
    } else {
      return Pair.of(getterActorList.get(0), this.delays);
    }
  }

  /**
   * Creates a MapperDAGEdge and sets initial properties. <br>
   * The created edge is added to the MapperDAG.
   *
   *
   * @param source
   *          source vertex of the edge
   * @param target
   *          target vertex of the edge
   * @param rateExpression
   *          rate expression of the edge
   * @return the created edge
   */
  private DAGEdge createEdge(final DAGVertex source, final DAGVertex target, final String rateExpression) {
    final DAGEdge edge = this.dag.addEdge(source, target);
    final int weight = this.dataSize * Integer.parseInt(rateExpression);
    edge.setPropertyValue(SDFEdge.SOURCE_PORT_MODIFIER, new SDFStringEdgePropertyType(this.sourceModifier));
    edge.setPropertyValue(SDFEdge.TARGET_PORT_MODIFIER, new SDFStringEdgePropertyType(this.targetModifier));
    edge.setPropertyValue(SRVerticesLinker.PIMM_FIFO, new SDFStringEdgePropertyType(this.fifo.getId()));
    edge.setPropertyValue(SDFEdge.DATA_TYPE, this.fifo.getType());
    edge.setPropertyValue(SDFEdge.DATA_SIZE, this.dataSize);
    edge.setWeight(new DAGDefaultEdgePropertyType(weight));
    return edge;
  }

  /**
   * Creates a new join actor with a unique ID. <br>
   * The created vertex is automatically added to the DAG.
   *
   * @param fixID
   *          fix part of the join actor ID
   * @param vertexFactory
   *          DAGVertex factory
   * @return the Join DAGVertex
   */
  private DAGVertex createJoinVertex(final String fixID, final MapperVertexFactory vertexFactory) {
    final DAGVertex joinVertex = vertexFactory.createVertex(DAGJoinVertex.DAG_JOIN_VERTEX);
    final String id = fixID + Integer.toString(this.joinIDCounter++);
    setVertexDefault(joinVertex, id);
    this.dag.addVertex(joinVertex);
    return joinVertex;
  }

  /**
   * Creates a new fork actor with a unique ID. <br>
   * The created vertex is automatically added to the DAG.
   *
   * @param fixID
   *          fix part of the fork actor ID
   * @param vertexFactory
   *          DAGVertex factory
   * @return the Fork DAGVertex
   */
  private DAGVertex createForkVertex(final String fixID, final MapperVertexFactory vertexFactory) {
    final DAGVertex forkVertex = vertexFactory.createVertex(DAGForkVertex.DAG_FORK_VERTEX);
    final String id = fixID + Integer.toString(this.forkIDCounter++);
    setVertexDefault(forkVertex, id);
    this.dag.addVertex(forkVertex);
    return forkVertex;
  }

  /**
   * Creates a new init actor. <br>
   * The created vertex is automatically added to the DAG.
   *
   * @param fixID
   *          id of the vertex
   * @param vertexFactory
   *          DAGVertex factory
   * @return the init DAGVertex
   */
  private DAGVertex createInitVertex(final String fixID, final MapperVertexFactory vertexFactory) {
    final DAGVertex initVertex = vertexFactory.createVertex(DAGInitVertex.DAG_INIT_VERTEX);
    setVertexDefault(initVertex, fixID);
    initVertex.getPropertyBean().setValue(DAGInitVertex.INIT_SIZE, this.delays);
    this.dag.addVertex(initVertex);
    return initVertex;
  }

  /**
   * Creates a new end actor. <br>
   * The created vertex is automatically added to the DAG.
   *
   * @param fixID
   *          id of the vertex
   * @param vertexFactory
   *          DAGVertex factory
   * @return the end DAGVertex
   */
  private DAGVertex createEndVertex(final String fixID, final MapperVertexFactory vertexFactory) {
    final DAGVertex endVertex = vertexFactory.createVertex(DAGEndVertex.DAG_END_VERTEX);
    setVertexDefault(endVertex, fixID);
    this.dag.addVertex(endVertex);
    return endVertex;
  }

  /**
   * Set default properties of a DAGVertex. <br>
   * id = name = info <br>
   * nbRepeat = 1
   *
   * @param vertex
   *          the vertex to init
   * @param id
   *          the id to set
   */
  private void setVertexDefault(final DAGVertex vertex, final String id) {
    vertex.setId(id);
    vertex.setName(id);
    vertex.setInfo(id);
    vertex.setNbRepeat(new DAGDefaultVertexPropertyType(1));
  }
}
