/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import java.util.ArrayList;
import java.util.Map;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultEdgePropertyType;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultVertexPropertyType;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
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

  private int joinIDCounter;
  private int forkIDCounter;

  /**
   * Retrieve the number of delay tokens contain in a fifo, if any
   * 
   * @param fifo
   * @return number of delay, 0 if the fifo does not contain any delay
   */
  private static long getNDelays(final Fifo fifo) {
    final Delay delay = fifo.getDelay();
    if (delay == null) {
      return 0;
    }
    // Get the number of delay
    final Expression sizeExpression = fifo.getDelay().getSizeExpression();
    long nDelays = Long.parseLong(sizeExpression.getExpressionString());
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
  private static long getRVorDefault(final AbstractActor actor, final Map<AbstractVertex, Integer> brv) {
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
    this.delays = getNDelays(fifo);
    this.source = fifo.getSourcePort().getContainingActor();
    this.sink = fifo.getTargetPort().getContainingActor();
    this.sourcePort = fifo.getSourcePort();
    this.sinkPort = fifo.getTargetPort();
    final String type = fifo.getType();
    this.dataSize = scenario.getSimulationManager().getDataTypeSizeOrDefault(type);
    this.dag = dag;
    this.joinIDCounter = 0;
    this.forkIDCounter = 0;
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
   * @param dag
   *          the dag in which the vertices need to be connected
   * 
   * @return true if no error, false else
   */
  public Boolean execute(final Map<AbstractVertex, Integer> brv, final Map<AbstractVertex, ArrayList<MapperDAGVertex>> pimm2dag) throws PiMMHelperException {
    // These connections are already dealt with
    if (source instanceof DelayActor || sink instanceof DelayActor) {
      return true;
    }
    // Repetition values
    final long sinkRV = getRVorDefault(sink, brv);
    final long sourceRV = getRVorDefault(source, brv);

    // Port expressions
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    final Expression sinkExpression = this.sinkPort.getPortRateExpression();
    // Production / Consumption
    final long sourceProduction = Long.parseLong(sourceExpression.getExpressionString());
    final long sinkConsumption = Long.parseLong(sinkExpression.getExpressionString());

    // List of source vertex
    ArrayList<Pair<DAGVertex, Long>> sourceSet = getSourceSet(brv, pimm2dag);
    // List of sink vertex
    ArrayList<Pair<DAGVertex, Long>> sinkSet = getSinkSet(brv, pimm2dag);

    // TODO handle the connections now.

    // for (final Pair<DAGVertex, Long> srcPair : sourceSet) {
    // DAGVertex srcVertex = srcPair.getFirst();
    // for (final Pair<DAGVertex, Long> snkPair : sinkSet) {
    //
    // }
    // }
    //
    // long curSourceToken = sourceSet.get(0).getSecond();
    // long curSinkToken = sinkSet.get(0).getSecond();
    // int sourceIndex = 0;
    // int sinkIndex = 0;
    //
    // String currentForkID = this.fifo.getId() + "_Fork_" + Integer.toString(-1);
    // String currentJoinID = this.fifo.getId() + "_Join_" + Integer.toString(-1);
    //
    // final MapperVertexFactory vertexFactory = MapperVertexFactory.getInstance();
    //
    // // Iterating until all consumptions are "satisfied".
    // while ((sourceIndex < sourceSet.size()) || (sinkIndex < sinkSet.size())) {
    // final DAGVertex sourceVertex = sourceSet.get(sourceIndex).getFirst();
    // // Production/consumption rate for the current source/target.
    // final long restToken = Math.min(curSourceToken, curSinkToken);
    //
    // // Adding explode / collapse vertices if required.
    // if ((restToken < curSourceToken) && !sourceVertex.getId().equals(currentForkID)) {
    // // Adding an explode vertex.
    // final DAGVertex forkVertex = createForkVertex(this.fifo.getId() + "_Fork_", vertexFactory);
    // this.dag.addVertex(forkVertex);
    // currentForkID = forkVertex.getId();
    // // Adding an edge between the source and the fork.
    // createEdge(sourceVertex, forkVertex, Long.toString(curSourceToken));
    // // this.result.setEdgeWeight(this.result.getEdge(sourceVertex, forkVertex), (double) curSourceToken);
    // sourceSet.get(sourceIndex).setFirst(forkVertex);
    // }
    //
    // DAGVertex targetVertex = sinkSet.get(sinkIndex).getFirst();
    // if ((restToken < curSinkToken) && targetVertex != null && !targetVertex.getId().equals(currentJoinID)) {
    // // Adding a collapse vertex.
    // final DAGVertex joinVertex = createJoinVertex(this.fifo.getId() + "_Join_", vertexFactory);
    // this.dag.addVertex(joinVertex);
    // currentJoinID = joinVertex.getId();
    // // Replacing the sink vertex by the join vertex in the array of sinks.
    // createEdge(joinVertex, targetVertex, Long.toString(sinkSet.get(sinkIndex).getSecond()));
    // sinkSet.get(sinkIndex).setFirst(joinVertex);
    // targetVertex = joinVertex;
    // }
    //
    // // Creating the new edge between normal vertices or between a normal and an explode / collapse one.
    // // Reassign target vertex as it may have changed
    // createEdge(sourceSet.get(sourceIndex).getFirst(), targetVertex, Long.toString(restToken));
    //
    // // Update the number of token produced/consumed by the current source/target.
    // curSourceToken -= restToken;
    // curSinkToken -= restToken;
    //
    // if (curSourceToken == 0 && (sourceIndex < sourceSet.size() - 1)) {
    // sourceIndex++;
    // curSourceToken = sourceSet.get(sourceIndex).getSecond();
    // }
    // if (curSinkToken == 0 && (sinkIndex < sinkSet.size() - 1)) {
    // sinkIndex++;
    // curSinkToken = sinkSet.get(sinkIndex).getSecond();
    // }
    // }

    return true;
  }

  private ArrayList<Pair<DAGVertex, Long>> getSourceSet(final Map<AbstractVertex, Integer> brv, final Map<AbstractVertex, ArrayList<MapperDAGVertex>> pimm2dag)
      throws PiMMHelperException {
    ArrayList<Pair<DAGVertex, Long>> sourceSet = new ArrayList<>();

    final MapperVertexFactory vertexFactory = MapperVertexFactory.getInstance();
    final String fifoID = this.fifo.getId();
    // Port expressions
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    final Expression sinkExpression = this.sinkPort.getPortRateExpression();
    final long sourceProduction = Long.parseLong(sourceExpression.getExpressionString());

    if (this.source instanceof InterfaceActor) {
      // We will see that later
      // Repetition values
      // final long sinkRV = getRVorDefault(this.sink, brv);
      // Port expressions
      // final Expression sourceExpression = this.sourcePort.getPortRateExpression();
      // final Expression sinkExpression = this.sinkPort.getPortRateExpression();
      // final long sourceProduction = Long.parseLong(sourceExpression.getExpressionString());
      // final long sinkConsumption = Long.parseLong(sinkExpression.getExpressionString());
      // final DAGVertex sourceVertex = this.piActor2DAGVertex.get(sourceActor).get(0);
      // if (sourceProduction == sinkConsumption * sinkRV) {
      // // We don't need to use broadcast
      // // final DataInputInterface dataInputInterface = (DataInputInterface) sourceActor;
      // // final DataInputPort dataInputPort = (DataInputPort) dataInputInterface.getDataPort();
      // // final AbstractActor interfaceSourceActor = dataInputPort.getIncomingFifo().getSourcePort().getContainingActor();
      // final DAGVertex vertex = this.piActor2DAGVertex.get(sourceActor).get(0);
      // final SourceConnection sourceConnection = new SourceConnection(vertex, sourcePort, sourceProduction);
      // } else {
      // final boolean perfectBroadcast = totalSinkConsumption % sourceProduction == 0;
      // long nBroadcast = totalSinkConsumption / sourceProduction;
      // if (!perfectBroadcast) {
      // nBroadcast++;
      // }
      // // Update the number of repetition of the source
      // nbSourceRepetitions = nBroadcast;
      // // If we have left over tokens, we need to get rid of them
      // sinkNeedEnd = !perfectBroadcast;
      // DAGVertex vertex = this.vertexFactory.createVertex(DAGBroadcastVertex.DAG_BROADCAST_VERTEX);
      // this.result.addEdge(sourceVertex, vertex);
      // for (int i = 0; i < nBroadcast; ++i) {
      // final SourceConnection sourceConnection = new SourceConnection();
      // sourceConnection.setSource(vertex);
      // sourceConnection.setProd(sourceProduction);
      // sourceConnections.add(sourceConnection);
      // }
      // }
    } else if (this.source instanceof AbstractActor) {
      if (this.delays != 0) {
        // Deals with the delay
        final Delay delay = this.fifo.getDelay();
        if (delay.hasSetterActor()) {
          final AbstractActor setterActor = this.fifo.getDelay().getSetterActor();
          final ArrayList<MapperDAGVertex> setterActorList = pimm2dag.get(setterActor);
          sourceSet.add(addDelaySetterActor(setterActorList));
        } else {
          // Add an init vertex for the first iteration of the sink actor
          final DAGVertex initVertex = vertexFactory.createVertex(DAGInitVertex.DAG_INIT_VERTEX);
          setVertexDefault(initVertex, fifoID + "_Init");
          addPair(sourceSet, initVertex, this.delays);
        }
      }
      // Add the list of the SR-DAG vertex associated with the source
      pimm2dag.get(this.source).forEach(v -> addPair(sourceSet, v, sourceProduction));
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + source.getClass().toString());
    }
    return sourceSet;
  }

  private void addPair(final ArrayList<Pair<DAGVertex, Long>> set, final DAGVertex vertex, final long value) {
    set.add(new Pair<>(vertex, value));
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
      final DAGVertex joinVertex = createJoinVertex(this.fifo.getId() + "_Join_", vertexFactory);
      this.dag.addVertex(joinVertex);
      for (int i = 0; i < setterRV; ++i) {
        final DAGVertex currentSetterActor = setterActorList.get(i);
        final String rateExpression = delay.getSetterPort().getPortRateExpression().getExpressionString();
        createEdge(currentSetterActor, joinVertex, rateExpression);
      }
      return new Pair<>(joinVertex, this.delays);
    } else {
      return new Pair<>(setterActorList.get(0), this.delays);
    }
  }

  private ArrayList<Pair<DAGVertex, Long>> getSinkSet(final Map<AbstractVertex, Integer> brv, final Map<AbstractVertex, ArrayList<MapperDAGVertex>> pimm2dag)
      throws PiMMHelperException {
    ArrayList<Pair<DAGVertex, Long>> sinkSet = new ArrayList<>();

    final MapperVertexFactory vertexFactory = MapperVertexFactory.getInstance();
    final String fifoID = this.fifo.getId();

    // Port expressions
    final Expression sourceExpression = this.sourcePort.getPortRateExpression();
    final Expression sinkExpression = this.sinkPort.getPortRateExpression();
    final long sourceProduction = Long.parseLong(sourceExpression.getExpressionString());
    final long sinkConsumption = Long.parseLong(sinkExpression.getExpressionString());

    if (this.sink instanceof InterfaceActor) {
      // We will see that later
      // Repetition values
      // final long sourceRV = getRVorDefault(this.source, brv);

      // final DAGVertex sourceVertex = this.piActor2DAGVertex.get(sourceActor).get(0);
      // if (sinkConsumption == sourceProduction * sourceRV) {
      // // final DAGEdge edge = outputIfs.get(sinkActor);
      // // sinkConnections.add(new SinkConnection(edge, sinkConsumption));
      // } else {
      // final long nDroppedTokens = sourceProduction * nbSourceRepetitions - sinkConsumption;
      // long nEnd = (long) Math.ceil((double) nDroppedTokens / sourceProduction);
      // nbSinkRepetitions = nEnd + 1;
      // for (int i = 0; i < nEnd; ++i) {
      // final SinkConnection sinkConnection = new SinkConnection(new DAGEdge(), sourceProduction);
      // final DAGVertex targetVertex = this.piActor2DAGVertex.get(sinkActor).get(i);
      // sinkConnection.getEdge().setPropertyValue(StaticPiMM2SrDAGVisitor.TARGET_VERTEX, targetVertex);
      // sinkConnections.add(sinkConnection);
      // }
      // sinkConnections.get((int) nEnd - 1).setCons(nDroppedTokens - (nEnd - 1) * sourceProduction);
      // // final DAGEdge edge = outputIfs.get(sinkActor);
      // // sinkConnections.add(new SinkConnection(edge, sinkConsumption));
      // }
    } else if (this.sink instanceof AbstractActor) {
      // Add the list of the SR-DAG vertex associated with the sink
      pimm2dag.get(this.sink).forEach(v -> addPair(sinkSet, v, sinkConsumption));
      // TODO check the sinkNeedEnd boolean
      if (this.delays != 0) {
        // Deals with the delay
        final Delay delay = this.fifo.getDelay();
        if (delay.hasGetterActor()) {
          final AbstractActor getterActor = this.fifo.getDelay().getGetterActor();
          final ArrayList<MapperDAGVertex> getterActorList = pimm2dag.get(getterActor);
          sinkSet.add(addDelayGetterActor(getterActorList));
        } else {
          // Add an end vertex for the last iteration of the source actor
          final DAGVertex endVertex = vertexFactory.createVertex(DAGEndVertex.DAG_END_VERTEX);
          setVertexDefault(endVertex, fifoID + "_End");
          addPair(sinkSet, endVertex, this.delays);
        }
      }
    } else {
      throw new PiMMHelperException("Unhandled type of actor: " + this.sink.getClass().toString());
    }
    return sinkSet;
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
      final DAGVertex forkVertex = createForkVertex(this.fifo.getId() + "_Fork_", vertexFactory);
      this.dag.addVertex(forkVertex);
      for (int i = 0; i < getterRV; ++i) {
        final DAGVertex currentGetterActor = getterActorList.get(i);
        final String rateExpression = delay.getGetterPort().getPortRateExpression().getExpressionString();
        createEdge(forkVertex, currentGetterActor, rateExpression);
      }
      return new Pair<>(forkVertex, this.delays);
    } else {
      return new Pair<>(getterActorList.get(0), this.delays);
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
    edge.setPropertyValue(SDFEdge.DATA_TYPE, this.fifo.getType());
    edge.setPropertyValue(SDFEdge.DATA_SIZE, this.dataSize);
    edge.setWeight(new DAGDefaultEdgePropertyType(weight));
    return edge;
  }

  /**
   * Creates a new join actor with a unique ID
   * 
   * @param fixID
   *          fix part of the join actor ID
   * @param vertexFactory
   *          DAGVertex factory
   * @return the Join DAGVertex
   */
  private DAGVertex createJoinVertex(final String fixID, final MapperVertexFactory vertexFactory) {
    final DAGVertex joinVertex = vertexFactory.createVertex(DAGJoinVertex.DAG_JOIN_VERTEX);
    final String id = fixID + Integer.toString(joinIDCounter++);
    setVertexDefault(joinVertex, id);
    return joinVertex;
  }

  /**
   * Creates a new fork actor with a unique ID
   * 
   * @param fixID
   *          fix part of the fork actor ID
   * @param vertexFactory
   *          DAGVertex factory
   * @return the Fork DAGVertex
   */
  private DAGVertex createForkVertex(final String fixID, final MapperVertexFactory vertexFactory) {
    final DAGVertex forkVertex = vertexFactory.createVertex(DAGForkVertex.DAG_FORK_VERTEX);
    final String id = fixID + Integer.toString(forkIDCounter++);
    setVertexDefault(forkVertex, id);
    return forkVertex;
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

  private class Pair<F, S> {

    // First element of the pair
    private F first;
    // Second element of the pair
    private S second;

    /**
     * Construct a pair from the parameters.
     * 
     * @param a
     *          First element.
     * @param b
     *          Second element.
     */
    public Pair(F a, S b) {
      first = a;
      second = b;
    }

    /**
     * @return First element in the pair.
     */
    public F getFirst() {
      return first;
    }

    /**
     * @return Second element in the pair.
     */
    public S getSecond() {
      return second;
    }

    /**
     * Sets the first element
     * 
     * @param a
     *          First element.
     */
    public void setFirst(F a) {
      first = a;
    }

    /**
     * Sets the second element
     * 
     * @param b
     *          Second element.
     */
    public void setSecond(S b) {
      second = b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "<" + first + ", " + second + ">";
    }
  }

}
