/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.IRefinement;
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
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
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
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;

/**
 * @author farresti
 *
 */
public class StaticPiMM2SrDAGVisitor extends PiMMSwitch<Boolean> {
  /** Property name for property TARGET_VERTEX. */
  public static final String TARGET_VERTEX = "target_vertex";

  /** Property name for property SOURCE_VERTEX. */
  public static final String SOURCE_VERTEX = "source_vertex";

  /** The result. */
  // SRDAG graph created from the outer graph
  private MapperDAG result;

  /** Basic repetition vector of the graph */
  private Map<AbstractVertex, Integer> brv;

  /** Counter for hierarchical actors indexes */
  private int hCounter;

  /** Counter for actors indexes */
  private int aCounter;

  /** The pi vx 2 SDF vx. */
  // Map from original PiMM vertices to generated DAG vertices
  private Map<AbstractVertex, ArrayList<DAGVertex>> piActor2DAGVertex = new LinkedHashMap<>();

  /** The current SDF refinement. */
  // Current SDF Refinement
  protected IRefinement currentRefinement;

  /**
   * Instantiates a new abstract pi MM 2 SR-DAG visitor.
   *
   * @param dag
   *          the dag
   */
  public StaticPiMM2SrDAGVisitor(final MapperDAG dag, Map<AbstractVertex, Integer> brv) {
    this.result = dag;
    this.brv = brv;
  }

  /**
   * Convert a PiMM AbstractActor to a DAGVertex.
   *
   * @param a
   *          the AbstractActor
   * @return the DAGVertex
   */
  private void pimm2srdag(final AbstractActor a, final DAGVertex vertex) {
    final String nameInfoID = a.getVertexPath() + "_" + Integer.toString(aCounter);
    // Handle vertex's name
    vertex.setName(nameInfoID);
    // Handle vertex's path inside the graph hierarchy
    vertex.setInfo(nameInfoID);
    // Handle ID
    vertex.setId(Integer.toString(aCounter));
    // Set Repetition vector to 1 since it is a single rate vertex
    vertex.setNbRepeat(new DAGDefaultVertexPropertyType(1));
  }

  private DAGJoinVertex addJoinVertex(final String fixID, Integer joinID) {
    final DAGJoinVertex joinVertex = new DAGJoinVertex();
    final String id = fixID + joinID.toString();
    joinVertex.setId(id);
    joinVertex.setName(id);
    joinVertex.setInfo(id);
    joinID++;
    return joinVertex;
  }

  private DAGForkVertex addForkVertex(final String fixID, Integer forkID) {
    final DAGForkVertex forkVertex = new DAGForkVertex();
    final String id = fixID + forkID.toString();
    forkVertex.setId(id);
    forkVertex.setName(id);
    forkVertex.setInfo(id);
    forkID++;
    return forkVertex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.AbstractActor)
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    final DAGVertex vertex = new DAGVertex();
    // Set default properties from the PiMM actor
    pimm2srdag(actor, vertex);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    this.piActor2DAGVertex.get(actor).add(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitActor(org.ietr.preesm.experiment.model.pimm.Actor)
   */
  @Override
  public Boolean caseActor(final Actor actor) {
    final DAGVertex vertex = new DAGVertex();
    // Set default properties from the PiMM actor
    pimm2srdag(actor, vertex);
    // Handle path to memory script of the vertex
    if (actor.getMemoryScriptPath() != null) {
      vertex.setPropertyValue(SDFVertex.MEMORY_SCRIPT, actor.getMemoryScriptPath().toOSString());
    }
    // Handle vertex's refinement (description of the vertex's behavior:
    // function prototypes or subgraphs)
    final Refinement piRefinement = actor.getRefinement();
    doSwitch(piRefinement);
    vertex.setRefinement(this.currentRefinement);
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    this.piActor2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor actor) {
    final DAGBroadcastVertex vertex = new DAGBroadcastVertex();
    pimm2srdag(actor, vertex);
    // Check the good use of the broadcast
    final DataInputPort dataInputPort = actor.getDataInputPorts().get(0);
    final Expression portRateExpression = dataInputPort.getPortRateExpression();
    final long cons = Long.parseLong(portRateExpression.getExpressionString());
    for (final DataOutputPort out : actor.getDataOutputPorts()) {
      final Expression outPortRateExpression = out.getPortRateExpression();
      final long prod = Long.parseLong(outPortRateExpression.getExpressionString());
      if (prod != cons) {
        WorkflowLogger.getLogger()
            .warning("Warning: Broadcast have different production/consumption: prod(" + Long.toString(prod) + ") != cons(" + Long.toString(cons) + ")");
      }
    }
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    this.piActor2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor actor) {
    final DAGJoinVertex vertex = new DAGJoinVertex();
    pimm2srdag(actor, vertex);
    // Check Join use
    if (actor.getDataOutputPorts().size() > 1) {
      WorkflowLogger.getLogger().warning("Warning: Join actors should have only one output.");
    }
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    this.piActor2DAGVertex.get(actor).add(vertex);
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor actor) {
    final DAGForkVertex vertex = new DAGForkVertex();
    pimm2srdag(actor, vertex);
    // Check Fork use
    if (actor.getDataInputPorts().size() > 1) {
      WorkflowLogger.getLogger().warning("Warning: Fork actors should have only one input.");
    }
    // Add the vertex to the DAG
    this.result.addVertex(vertex);
    this.piActor2DAGVertex.get(actor).add(vertex);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitFifo(org.ietr.preesm.experiment.model.pimm.Fifo)
   */
  @Override
  public Boolean caseFifo(final Fifo fifo) {
    final String fifoID = fifo.getId();
    Integer forkID = 0;
    String forkIDString = fifoID + "_Fork_";
    Integer joinID = 0;
    String joinIDString = fifoID + "_Join_";

    long nDelays = 0;
    if (fifo.getDelay() != null) {
      nDelays = Long.parseLong(fifo.getDelay().getSizeExpression().getExpressionString());
    }

    // Sanity check on delay value
    if (nDelays != 0) {
      final long targetRate = Long.parseLong(fifo.getTargetPort().getPortRateExpression().getExpressionString());
      if (nDelays < 0) {
        throw new RuntimeException("Invalid number of delay on fifo[" + fifo.getId() + "]: " + Long.toString(nDelays));
      } else if (nDelays < targetRate) {
        throw new RuntimeException("Insuffisiant number of delay on fifo[" + fifo.getId() + "]: number of delays: " + Long.toString(nDelays) + ", consumption: "
            + Long.toString(targetRate));
      }

    }

    // Evaluate source repetition vector
    final DataOutputPort sourcePort = fifo.getSourcePort();
    final AbstractActor sourceActor = sourcePort.getContainingActor();
    long nbSourceRepetitions = 1;
    if (!(sourceActor instanceof InterfaceActor)) {
      nbSourceRepetitions = this.brv.get(sourceActor);
    }

    // Evaluate target repetition vector
    final DataInputPort sinkPort = fifo.getTargetPort();
    final AbstractActor sinkActor = sinkPort.getContainingActor();
    long nbSinkRepetitions = 1;
    if (!(sinkActor instanceof InterfaceActor)) {
      nbSinkRepetitions = this.brv.get(sinkActor);
    }

    // If the sink actor or the source actor is a delay actor then we already check these
    if (sinkActor instanceof DelayActor || sourceActor instanceof DelayActor) {
      return true;
    }

    final long sourceProduction = Long.parseLong(sourcePort.getPortRateExpression().getExpressionString());
    final long sinkConsumption = Long.parseLong(sinkPort.getPortRateExpression().getExpressionString());

    final List<SourceConnection> sourceConnections = new ArrayList<>();
    final List<SinkConnection> sinkConnections = new ArrayList<>();

    boolean sinkNeedEnd = false;

    // Deals with the source part
    if (sourceActor instanceof InterfaceActor) {
      final DAGVertex sourceVertex = this.piActor2DAGVertex.get(sourceActor).get(0);
      final long totalSinkConsumption = sinkConsumption * nbSinkRepetitions;
      if (sourceProduction == totalSinkConsumption) {
        // We don't need to use broadcast
        // final DataInputInterface dataInputInterface = (DataInputInterface) sourceActor;
        // final DataInputPort dataInputPort = (DataInputPort) dataInputInterface.getDataPort();
        // final AbstractActor interfaceSourceActor = dataInputPort.getIncomingFifo().getSourcePort().getContainingActor();
        final DAGVertex vertex = this.piActor2DAGVertex.get(sourceActor).get(0);
        final SourceConnection sourceConnection = new SourceConnection(vertex, sourcePort, sourceProduction);
      } else {
        final boolean perfectBroadcast = totalSinkConsumption % sourceProduction == 0;
        long nBroadcast = totalSinkConsumption / sourceProduction;
        if (!perfectBroadcast) {
          nBroadcast++;
        }
        // Update the number of repetition of the source
        nbSourceRepetitions = nBroadcast;
        // If we have left over tokens, we need to get rid of them
        sinkNeedEnd = !perfectBroadcast;
        DAGBroadcastVertex vertex = new DAGBroadcastVertex();
        this.result.addEdge(sourceVertex, vertex, new DAGEdge());
        for (int i = 0; i < nBroadcast; ++i) {
          final SourceConnection sourceConnection = new SourceConnection();
          sourceConnection.setSource(vertex);
          sourceConnection.setProd(sourceProduction);
          // setPort ?
          sourceConnections.add(sourceConnection);
        }
      }
    } else if (sourceActor instanceof AbstractActor) {
      if (nDelays == 0) {
        for (int i = 0; i < nbSourceRepetitions; ++i) {
          final DAGVertex vertex = this.piActor2DAGVertex.get(sourceActor).get(i);
          sourceConnections.add(new SourceConnection(vertex, sourcePort, sourceProduction));
        }
      } else {
        nbSourceRepetitions++;
        if (fifo.getDelay().hasSetterActor()) {
          final AbstractActor setterActor = fifo.getDelay().getSetterActor();
          final Integer brvSetter = this.brv.get(setterActor);
          sourceConnections.add(new SourceConnection());
          sourceConnections.get(0).setProd(nDelays);
          if (brvSetter > 1) {
            // We have to add a join actor
            final DAGJoinVertex joinVertex = addJoinVertex(joinIDString, joinID);
            this.result.addVertex(joinVertex);
            for (int i = 0; i < brvSetter; ++i) {
              final DAGVertex setterVertex = this.piActor2DAGVertex.get(setterActor).get(i);
              final DAGEdge edge = new DAGEdge();
              this.result.addEdge(setterVertex, joinVertex, edge);
              final long setterRate = Long.parseLong(fifo.getDelay().getSetterPort().getPortRateExpression().getExpressionString());
              edge.setWeight(new DAGDefaultEdgePropertyType((int) setterRate));
            }
            sourceConnections.get(0).setSource(joinVertex);
          } else {
            final DAGVertex setterVertex = this.piActor2DAGVertex.get(setterActor).get(0);
            sourceConnections.get(0).setSource(setterVertex);
          }
        } else {
          final DAGInitVertex vertex = new DAGInitVertex();
          vertex.setId(fifoID + "_Init");
          this.result.addVertex(vertex);
          sourceConnections.get(0).setSource(vertex);
        }
        for (int i = 1; i < nbSourceRepetitions; ++i) {
          final DAGVertex vertex = this.piActor2DAGVertex.get(sourceActor).get(i - 1);
          sourceConnections.add(new SourceConnection(vertex, sourcePort, sourceProduction));
        }
      }
    }

    // Deals with the sink part
    if (sinkActor instanceof InterfaceActor) {
      if (sinkConsumption == sourceProduction * nbSourceRepetitions) {
        // final DAGEdge edge = outputIfs.get(sinkActor);
        // sinkConnections.add(new SinkConnection(edge, sinkConsumption));
      } else {
        final long nDroppedTokens = sourceProduction * nbSourceRepetitions - sinkConsumption;
        long nEnd = (long) Math.ceil((double) nDroppedTokens / sourceProduction);
        nbSinkRepetitions = nEnd + 1;
        for (int i = 0; i < nEnd; ++i) {
          final SinkConnection sinkConnection = new SinkConnection(new DAGEdge(), sourceProduction);
          final DAGVertex targetVertex = this.piActor2DAGVertex.get(sinkActor).get(i);
          sinkConnection.getEdge().setPropertyValue(StaticPiMM2SrDAGVisitor.TARGET_VERTEX, targetVertex);
          sinkConnections.add(sinkConnection);
        }
        sinkConnections.get((int) nEnd - 1).setCons(nDroppedTokens - (nEnd - 1) * sourceProduction);
        // final DAGEdge edge = outputIfs.get(sinkActor);
        // sinkConnections.add(new SinkConnection(edge, sinkConsumption));
      }
    } else if (sinkActor instanceof AbstractActor) {
      for (int i = 0; i < nbSinkRepetitions; ++i) {
        final SinkConnection sinkConnection = new SinkConnection(new DAGEdge(), sinkConsumption);
        final DAGVertex targetVertex = this.piActor2DAGVertex.get(sinkActor).get(i);
        sinkConnection.getEdge().setPropertyValue(StaticPiMM2SrDAGVisitor.TARGET_VERTEX, targetVertex);
        sinkConnections.add(sinkConnection);
      }
      if (nDelays == 0) {
        if (sinkNeedEnd) {
          final DAGEndVertex endVertex = new DAGEndVertex();
          endVertex.setId(fifoID + "_End");
          this.result.addVertex(endVertex);
          final SinkConnection sinkConnection = new SinkConnection(new DAGEdge(), sourceProduction - sinkConsumption * nbSinkRepetitions);
          sinkConnection.getEdge().setPropertyValue(StaticPiMM2SrDAGVisitor.TARGET_VERTEX, endVertex);
          sinkConnections.add(sinkConnection);
          nbSinkRepetitions++;
        }
      } else {
        // We have at least one edge coming from the delay
        final SinkConnection sinkConnection = new SinkConnection(new DAGEdge(), nDelays);
        sinkConnections.add(sinkConnection);
        // We ge the last "delay provider"
        final DAGVertex sourceVertex = sourceConnections.get(sourceConnections.size() - 1).getSource();
        // this.piActor2DAGVertex.get(sourceActor).get((int) nbSourceRepetitions - 1);
        if (fifo.getDelay().hasGetterActor()) {
          final AbstractActor getterActor = fifo.getDelay().getGetterActor();
          final Integer brvGetter = this.brv.get(getterActor);
          if (brvGetter > 1) {
            // We have to add a fork actor
            final DAGForkVertex forkVertex = addForkVertex(forkIDString, forkID);
            this.result.addVertex(forkVertex);
            // The delay provider actor is connected the fork actor which will provide the getter actor instances
            this.result.addEdge(sourceVertex, forkVertex, sinkConnection.getEdge());
            for (int i = 0; i < brvGetter; ++i) {
              // We add an edge in the DAG going from the fork vertex to the correct instance of the getter actor
              final DAGVertex getterVertex = this.piActor2DAGVertex.get(getterActor).get(i);
              final DAGEdge edge = new DAGEdge();
              this.result.addEdge(forkVertex, getterVertex, edge);
              // Now we add our sink connection to the list
              final long getterRate = Long.parseLong(fifo.getDelay().getGetterPort().getPortRateExpression().getExpressionString());
              // sinkConnections.add(new SinkConnection(edge, getterRate));
            }
          } else {
            // In this case, the last instance of the delay provider actor is directly connected to the getter actor
            final DAGVertex getterVertex = this.piActor2DAGVertex.get(getterActor).get(0);
            this.result.addEdge(sourceVertex, getterVertex, sinkConnection.getEdge());
          }
        } else {
          // In this case we have to get rid of the data tokens
          final DAGEndVertex endVertex = new DAGEndVertex();
          endVertex.setId(fifoID + "_End");
          this.result.addVertex(endVertex);
          this.result.addEdge(sourceVertex, endVertex, sinkConnection.getEdge());
          // sinkConnection.getEdge().setPropertyValue(StaticPiMM2SrDAGVisitor.TARGET_VERTEX, endVertex);
        }
        nbSinkRepetitions++;
      }
    }

    // Now deal with all these connections
    long curSourceToken = sourceConnections.get(0).getProd();
    long curSinkToken = sinkConnections.get(0).getCons();
    int sourceIndex = 0;
    int sinkIndex = 0;

    String cur_fork_id = forkIDString + Integer.toString(-1);
    String cur_join_id = joinIDString + Integer.toString(-1);

    // Iterating until all consumptions are "satisfied".
    while ((sourceIndex < (nbSourceRepetitions - 1)) || (sinkIndex < (nbSinkRepetitions - 1))) {
      final DAGVertex sourceVertex = sourceConnections.get(sourceIndex).getSource();
      // Production/consumption rate for the current source/target.
      final long restToken = Math.min(curSourceToken, curSinkToken);

      // Adding explode / collapse vertices if required.
      if ((restToken < curSourceToken) && !sourceVertex.getId().equals(cur_fork_id)) {
        // Adding an explode vertex.
        final DAGForkVertex forkVertex = addForkVertex(forkIDString, forkID);
        this.result.addVertex(forkVertex);
        cur_fork_id = forkVertex.getId();
        // Adding an edge between the source and the fork.
        this.result.addEdge(sourceVertex, forkVertex, new DAGEdge());
        this.result.getEdge(sourceVertex, forkVertex).setWeight(new DAGDefaultEdgePropertyType((int) curSourceToken));
        // this.result.setEdgeWeight(this.result.getEdge(sourceVertex, forkVertex), (double) curSourceToken);
        sourceConnections.get(sourceIndex).setSource(forkVertex);
      }

      DAGVertex targetVertex = (DAGVertex) sinkConnections.get(sinkIndex).getEdge().getPropertyBean().getValue(StaticPiMM2SrDAGVisitor.TARGET_VERTEX);
      if ((restToken < curSinkToken) && targetVertex != null && !targetVertex.getId().equals(cur_join_id)) {
        // Adding a collapse vertex.
        final DAGJoinVertex joinVertex = addJoinVertex(joinIDString, joinID);
        this.result.addVertex(joinVertex);
        cur_join_id = joinVertex.getId();
        // Replacing the sink vertex by the join vertex in the array of sinks.
        this.result.addEdge(joinVertex, targetVertex, sinkConnections.get(sinkIndex).getEdge());
        sinkConnections.get(sinkIndex).getEdge().setWeight(new DAGDefaultEdgePropertyType((int) sinkConnections.get(sinkIndex).getCons()));
        // this.result.setEdgeWeight(sinkConnections.get(sinkIndex).getEdge(), (double) sinkConnections.get(sinkIndex).getCons());

        sinkConnections.get(sinkIndex).setEdge(new DAGEdge());
        sinkConnections.get(sinkIndex).getEdge().setPropertyValue(StaticPiMM2SrDAGVisitor.TARGET_VERTEX, joinVertex);
        targetVertex = joinVertex;
      }

      // Creating the new edge between normal vertices or between a normal and an explode / collapse one.
      // Reassign target vertex as it may have changed
      this.result.addEdge(sourceConnections.get(sourceIndex).getSource(), targetVertex, sinkConnections.get(sinkIndex).getEdge());
      sinkConnections.get(sinkIndex).getEdge().setWeight(new DAGDefaultEdgePropertyType((int) restToken));
      // this.result.setEdgeWeight(sinkConnections.get(sinkIndex).getEdge(), (double) restToken);

      // Update the number of token produced/consumed by the current source/target.
      curSourceToken -= restToken;
      curSinkToken -= restToken;

      if (curSourceToken == 0 && sourceIndex < (sourceConnections.size() - 1)) {
        sourceIndex++;
        curSourceToken = sourceConnections.get(sourceIndex).getProd();
      }
      if (curSinkToken == 0 && sinkIndex < (sinkConnections.size() - 1)) {
        sinkIndex++;
        curSinkToken = sinkConnections.get(sinkIndex).getCons();
      }
    }

    // while (sourceIndex < nbSourceRepetitions
    // || sinkIndex < nbSinkRepetitions) {
    // // Production/consumption rate for the current source/target.
    // int rest = std::min(curSourceToken, curSinkToken); // Minimum.
    //
    // /*
    // * Adding explode/implode vertices if required.
    // */
    //
    // if (rest < curSinkToken &&
    // snkConnections[sinkIndex].edge->getSnk() &&
    // (snkConnections[sinkIndex].edge->getSnk()->getId() != joinIx)){ // Type == 0 indicates it is a normal vertex.
    //
    // // Adding an join vertex.
    // SRDAGVertex *join_vertex = topSrdag->addJoin(MAX_IO_EDGES);
    // joinIx = join_vertex->getId();
    //
    // // Replacing the sink vertex by the join vertex in the array of sources.
    // snkConnections[sinkIndex].edge->connectSrc(join_vertex, 0);
    // snkConnections[sinkIndex].edge->setRate(snkConnections[sinkIndex].cons);
    // snkConnections[sinkIndex].edge = topSrdag->addEdge();
    // snkConnections[sinkIndex].edge->connectSnk(join_vertex, 0);
    //
    // }else if(snkConnections[sinkIndex].edge->getSnk()
    // && snkConnections[sinkIndex].edge->getSnk()->getId() == joinIx){
    // /* Adding the new edge in join*/
    // SRDAGVertex *join_vertex = snkConnections[sinkIndex].edge->getSnk();
    // snkConnections[sinkIndex].edge = topSrdag->addEdge();
    // snkConnections[sinkIndex].edge->connectSnk(join_vertex, join_vertex->getNConnectedInEdge());
    // }
    //
    // //Creating the new edge between normal vertices or between a normal and an explode/implode one.
    // SRDAGEdge* srcEdge;
    // if((srcEdge = srcConnections[sourceIndex].src->getOutEdge(srcConnections[sourceIndex].portIx)) != 0){
    // snkConnections[sinkIndex].edge->setAlloc(srcEdge->getAlloc());
    // snkConnections[sinkIndex].edge->setAllocIx(srcEdge->getAllocIx());
    // snkConnections[sinkIndex].edge->setRate(srcEdge->getRate());
    //
    // topSrdag->delEdge(srcEdge);
    // snkConnections[sinkIndex].edge->connectSrc(
    // srcConnections[sourceIndex].src,
    // srcConnections[sourceIndex].portIx
    // );
    // }else{
    // snkConnections[sinkIndex].edge->connectSrc(
    // srcConnections[sourceIndex].src,
    // srcConnections[sourceIndex].portIx
    // );
    // snkConnections[sinkIndex].edge->setRate(rest);
    // }
    //
    // }

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
  private void convertAnnotationsFromTo(final DataPort piPort, final SDFEdge edge, final String property) {
    switch (piPort.getAnnotation()) {
      case READ_ONLY:
        edge.setPropertyValue(property, new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
        break;
      case WRITE_ONLY:
        edge.setPropertyValue(property, new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
        break;
      case UNUSED:
        edge.setPropertyValue(property, new SDFStringEdgePropertyType(SDFEdge.MODIFIER_UNUSED));
        break;
      default:
    }
  }

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface dii) {
    // final SDFSourceInterfaceVertex v = new SDFSourceInterfaceVertex();
    // this.piVx2SDFVx.put(dii, v);
    // v.setName(dii.getName());
    //
    // caseAbstractActor(dii);

    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface doi) {
    // final SDFSinkInterfaceVertex v = new SDFSinkInterfaceVertex();
    // this.piVx2SDFVx.put(doi, v);
    // v.setName(doi.getName());
    //
    // caseAbstractActor(doi);

    return true;
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement refinement) {
    this.currentRefinement = new CodeRefinement(refinement.getFilePath());
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
   *
   * @param dop
   *          the dop
   */

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
    if (graph.getActors().isEmpty()) {
      return false;
    }

    // Add SR-Vertices
    // TODO handle interface actors (round buffer)
    for (final AbstractActor actor : graph.getActors()) {
      this.piActor2DAGVertex.put(actor, new ArrayList<>());
      for (int i = 0; i < this.brv.get(actor); ++i) {
        this.aCounter = i;
        // Treat hierarchical graphs as normal actors
        // This populate the DAG with the right amount of hierarchical instances w.r.t the BRV value
        if (actor instanceof PiGraph) {
          caseAbstractActor(actor);
          continue;
        }
        doSwitch(actor);
      }
    }
    // Check for top graph condition
    if (graph.getContainingGraph() != null) {
      final String name = graph.getVertexPath() + "_" + Integer.toString(this.hCounter);
      final DAGVertex vertex = this.result.getVertex(name);
      if (vertex == null) {
        throw new RuntimeException("Failed to convert PiMM 2 SR-DAG.\nVertex [" + name + "] not found.");
      } else {
        // Remove the hierarchical vertex from the DAG
        // We are going to replace it with its contents
        this.result.removeVertex(vertex);
      }
    }

    // Link SR-Vertices
    for (final Fifo fifo : graph.getFifos()) {
      doSwitch(fifo);
    }

    // Go check hierarchical graphs
    // for (final PiGraph g : graph.getChildrenGraphs()) {
    // // We have to iterate for every number of hierarchical graph we populated
    // // TODO this is not optimal. Find better approach
    //
    // // TODO create the inputIF and outputIf needed for Julien implem
    // for (int i = 0; i < this.brv.get(g); ++i) {
    // this.hCounter = i;
    // doSwitch(g);
    // }
    // }
    return true;
  }

  /**
   * The Class SourceConnection.
   */
  private class SourceConnection {
    private DAGVertex source;
    private long      prod;
    private DataPort  port;

    public SourceConnection() {
      // Empty constructor
    }

    public SourceConnection(final DAGVertex source, final DataPort port, final long prod) {
      this.source = source;
      this.prod = prod;
      this.port = port;
    }

    public void setSource(final DAGVertex vertex) {
      source = vertex;
    }

    public DAGVertex getSource() {
      return source;
    }

    public String getSourceID() {
      return this.source.getId();
    }

    public void setProd(final long p) {
      this.prod = p;
    }

    public void setPort(final DataPort port) {
      this.port = port;
    }

    public long getProd() {
      return this.prod;
    }
  }

  /**
   * The Class TargetConnection.
   */
  private class SinkConnection {
    private DAGEdge edge;
    private long    cons;

    public SinkConnection() {
      // Empty constructor
    }

    public SinkConnection(final DAGEdge edge, final long cons) {
      this.edge = edge;
      this.cons = cons;
    }

    public void setEdge(final DAGEdge edge) {
      this.edge = edge;
    }

    public DAGEdge getEdge() {
      return this.edge;
    }

    public void setCons(final long c) {
      this.cons = c;
    }

    public long getCons() {
      return this.cons;
    }
  }

}
