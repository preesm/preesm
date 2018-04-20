/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.preesm.pimm.algorithm.pimm2srdag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.time.StopWatch;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultVertexPropertyType;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperEdgeFactory;
import org.ietr.preesm.pimm.algorithm.helper.LCMBasedBRV;
import org.ietr.preesm.pimm.algorithm.helper.PiBRV;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHandler;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;
import org.ietr.preesm.pimm.algorithm.helper.TopologyBasedBRV;
import org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor.StaticPiMM2SrDAGVisitor;

/**
 * The Class StaticPiMM2SDFLauncher.
 */
public class StaticPiMM2SrDAGLauncher extends PiMMSwitch<Boolean> {

  /** The scenario. */
  private final PreesmScenario scenario;

  /** The graph. */
  private final PiGraph graph;

  /** The graph. */
  private PiMMHandler piHandler;

  /** Map from Pi actors to their Repetition Vector value. */
  protected Map<AbstractVertex, Integer> graphBRV = new LinkedHashMap<>();

  /** Map of all parametersValues */
  protected Map<Parameter, Integer> parametersValues;

  /**
   * Instantiates a new static pi MM 2 SDF launcher.
   *
   * @param scenario
   *          the scenario
   * @param graph
   *          the graph
   */
  public StaticPiMM2SrDAGLauncher(final PreesmScenario scenario, final PiGraph graph) {
    this.scenario = scenario;
    this.graph = graph;
    this.piHandler = new PiMMHandler(graph);
  }

  private static void printRV(final Map<AbstractVertex, Integer> graphBRV) {
    for (final Map.Entry<AbstractVertex, Integer> rv : graphBRV.entrySet()) {
      WorkflowLogger.getLogger().log(Level.INFO, rv.getKey().getVertexPath() + " x" + Integer.toString(rv.getValue()));
    }
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   * @throws StaticPiMM2SrDAGException
   *           the static pi MM 2 SDF exception
   */
  public MapperDAG launch(int method) throws StaticPiMM2SrDAGException {
    // Compute BRV following the chosen method
    PiBRV piBRVAlgo;
    if (method == 0) {
      piBRVAlgo = new TopologyBasedBRV(this.piHandler);
    } else if (method == 1) {
      piBRVAlgo = new LCMBasedBRV(this.piHandler);
    } else {
      throw new StaticPiMM2SrDAGException("unexpected value for BRV method: [" + Integer.toString(method) + "]");
    }
    try {
      StopWatch timer = new StopWatch();
      timer.start();
      this.piHandler.resolveAllParameters();
      timer.stop();
      WorkflowLogger.getLogger().log(Level.INFO, "Parameters and rates evaluations: " + timer.toString() + "s.");
      timer.reset();
      timer.start();
      piBRVAlgo.execute();
      this.graphBRV = piBRVAlgo.getBRV();
      timer.stop();
      WorkflowLogger.getLogger().log(Level.INFO, "Repetition vector computed in " + timer.toString() + "s.");
    } catch (PiMMHelperException e) {
      throw new StaticPiMM2SrDAGException(e.getMessage());
    }
    printRV(this.graphBRV);
    // Visitor creating the SR-DAG
    StaticPiMM2SrDAGVisitor visitor;
    visitor = new StaticPiMM2SrDAGVisitor(new MapperDAG(new MapperEdgeFactory(), this.graph), this.graphBRV);
    visitor.doSwitch(this.graph);
    // if (!visitor.doSwitch(this.graph)) {
    // if (visitor.getResult() == null) {
    // throw new StaticPiMM2SrDAGException("Cannot convert to Sr-DAG, top graph does not contain any actors.");
    // }
    // }
    return visitor.getResult();
  }

  private MapperDAG computeSRDag() throws StaticPiMM2SrDAGException {
    // Creates "top" sr dag with PiSDF reference graph
    final MapperDAG topDAG = new MapperDAG(new MapperEdgeFactory(), this.graph);
    // Iterates over every actors and levels of hierarchy to add vertices
    iterativeComputeSRDag(topDAG, this.piHandler);
    return topDAG;
  }

  private void iterativeComputeSRDag(final MapperDAG dag, final PiMMHandler piHandler) throws StaticPiMM2SrDAGException {
    try {
      // Add vertex for current hierarchical graph
      for (final List<AbstractActor> connectedComponent : this.piHandler.getAllConnectedComponents()) {
        for (final AbstractActor actor : connectedComponent) {
          addSRVertex(actor, dag);
        }
        final List<Fifo> ccFifos = piHandler.getFifosFromCC(connectedComponent);
        // Creates the edges and link vertices
        linkSRVertices(dag, ccFifos);
      }
      // Recursive call for child graphs
      for (final PiMMHandler ph : piHandler.getChildrenGraphsHandler()) {
        iterativeComputeSRDag(dag, ph);
      }
    } catch (PiMMHelperException e) {
      throw new StaticPiMM2SrDAGException(e.getMessage());
    }
  }

  private void linkSRVertices(final MapperDAG dag, final List<Fifo> ccFifos) {
    // link the single rate vertices
    for (final Fifo fifo : ccFifos) {
      // do stuff
      final long nDelays = Long.parseLong(fifo.getDelay().getSizeExpression().getExpressionString());

      // Evaluate source repetition vector
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      int nbSourceRepetitions = 1;
      if (!(sourceActor instanceof InterfaceActor)) {
        nbSourceRepetitions = this.graphBRV.get(sourceActor);
      }

      // Evaluate target repetition vector
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      int nbTargetRepetitions = 1;
      if (!(targetActor instanceof InterfaceActor)) {
        nbTargetRepetitions = this.graphBRV.get(targetActor);
      }

      final long sourceProduction = Long.parseLong(fifo.getSourcePort().getPortRateExpression().getExpressionString());
      final long targetConsumption = Long.parseLong(fifo.getTargetPort().getPortRateExpression().getExpressionString());
      // int piSrcIx = edge->getSrcPortIx();

      // int sourceIndex = 0, sinkIndex = 0;
      // int curSourceToken, curSinkToken;

      // typedef struct SrcConnection{
      // SRDAGVertex* src;
      // int prod;
      // int portIx;
      // } SrcConnection;
      //
      // typedef struct SnkConnection{
      // SRDAGEdge* edge;
      // int cons;
      // } SnkConnection;
      //
      // SrcConnection* srcConnections = 0;
      // SnkConnection* snkConnections = 0;
      // bool sinkNeedEnd = false;

      int forkIx = -1;
      int joinIx = -1;

      final List<SourceConnection> sourceConnections = new ArrayList<>();

      // MapperDAGEdge edge = new MapperDAGEdge(source, destination);
      // edge.setWeight(new DAGDefaultEdgePropertyType(1));

      // Fill source/sink repetition list
      if (sourceActor instanceof InterfaceActor) {
        if (sourceProduction == targetConsumption * nbTargetRepetitions) {
          // No need of Broadcast
          // final DAGVertex sourceIF = new DAGVertex();
          // pimm2DAGVertex(sourceActor, sourceIF);
          // final SourceConnection sourceConnection = new SourceConnection();
          // sourceConnection.setProd(sourceProduction);
          // sourceConnection.setPortID(0);
          // sourceConnection.addSource(sourceIF);
          // sourceConnections.add(sourceConnection);

          // if(srcConnections[0].src == 0){
          // srcConnections[0].src = topSrdag->addRoundBuffer();
          // job->inputIfs[edge->getSrc()->getTypeId()]->connectSnk(srcConnections[0].src, 0);
          // srcConnections[0].portIx = 0;
          // srcConnections[0].prod = sourceProduction;
          // }else{
          // srcConnections[0].portIx = job->inputIfs[edge->getSrc()->getTypeId()]->getSrcPortIx();
          // srcConnections[0].prod = sourceProduction;
          // }

        } else {
          // bool perfectBr = sinkConsumption*nbSinkRepetitions%sourceProduction == 0;
          // int nBr = sinkConsumption*nbSinkRepetitions/sourceProduction;
          // if(!perfectBr) nBr++;
          //
          // nbSourceRepetitions = nBr;
          // sinkNeedEnd = !perfectBr;
          //// lastCons = sourceProduction - sinkConsumption*nbSinkRepetitions;
          //
          // SRDAGVertex* broadcast = topSrdag->addBroadcast(MAX_IO_EDGES);
          // job->inputIfs[edge->getSrc()->getTypeId()]->connectSnk(broadcast, 0);
          //
          // srcConnections = CREATE_MUL(TRANSFO_STACK, nBr, SrcConnection);
          // for(int i=0; i<nBr; i++){
          // srcConnections[i].src = broadcast;
          // srcConnections[i].portIx = i;
          // srcConnections[i].prod = sourceProduction;
          // }
        }
      } else {
        if (nDelays == 0) {
          // srcConnections = CREATE_MUL(TRANSFO_STACK, nbSourceRepetitions, SrcConnection);
          // for(int i=0; i<nbSourceRepetitions; i++){
          // srcConnections[i].src = job->bodies[edge->getSrc()->getTypeId()][i];
          // srcConnections[i].portIx = piSrcIx;
          // srcConnections[i].prod = sourceProduction;
          // }
        } else {
          // nbSourceRepetitions++;
          // srcConnections = CREATE_MUL(TRANSFO_STACK, nbSourceRepetitions, SrcConnection);
          //
          // if(edge->getDelaySetter()){
          // PiSDFVertex* ifDelaySetter = edge->getDelaySetter();
          // SRDAGEdge* setterEdge = job->inputIfs[ifDelaySetter->getTypeId()];
          // if(setterEdge->getRate() == nbDelays){
          // srcConnections[0].src = setterEdge->getSrc();
          // if(srcConnections[0].src == 0){
          // srcConnections[0].src = topSrdag->addRoundBuffer();
          // job->inputIfs[edge->getSrc()->getTypeId()]->connectSnk(srcConnections[0].src, 0);
          // srcConnections[0].portIx = 0;
          // }else{
          // srcConnections[0].portIx = setterEdge->getSrcPortIx();
          // }
          // }else{
          // throw "Setter of a delay must be of the same rate than delay";
          // }
          // }else{
          // srcConnections[0].src = topSrdag->addInit();
          // srcConnections[0].portIx = 0;
          // }
          // srcConnections[0].prod = nbDelays;
          //
          // for(int i=1; i<nbSourceRepetitions; i++){
          // srcConnections[i].src = job->bodies[edge->getSrc()->getTypeId()][i-1];
          // srcConnections[i].portIx = piSrcIx;
          // srcConnections[i].prod = sourceProduction;
          // }
        }
      }
    }
  }

  /**
   * Convert a PiMM AbstractActor to a DAGVertex.
   *
   * @param a
   *          the AbstractActor
   * @return the DAGVertex
   */
  private void pimm2DAGVertex(final AbstractActor a, final DAGVertex vertex) {
    // Handle vertex's name
    vertex.setName(a.getVertexPath());
    // Handle vertex's path inside the graph hierarchy
    vertex.setInfo(a.getVertexPath());
    // Handle ID
    vertex.setId(a.getVertexPath());
    // Set Repetition vector
    vertex.setNbRepeat(new DAGDefaultVertexPropertyType(this.graphBRV.get(a)));
  }

  private void addSRVertex(final AbstractActor vertex, final MapperDAG dag) {
    final int brv = this.graphBRV.get(vertex);
    if (vertex instanceof BroadcastActor) {
      for (int i = 0; i < brv; ++i) {
        // Creates a Broadcast DAG vertex
        DAGVertex dagVertex = new DAGBroadcastVertex();
        final DataInputPort dataInputPort = vertex.getDataInputPorts().get(0);
        final Expression portRateExpression = dataInputPort.getPortRateExpression();
        final long cons = Long.parseLong(portRateExpression.getExpressionString());
        for (final DataOutputPort out : vertex.getDataOutputPorts()) {
          final Expression outPortRateExpression = out.getPortRateExpression();
          final long prod = Long.parseLong(outPortRateExpression.getExpressionString());
          if (prod != cons) {
            WorkflowLogger.getLogger()
                .warning("Warning: Broadcast have different production/consumption: prod(" + Long.toString(prod) + ") != cons(" + Long.toString(cons) + ")");
          }
        }
        // add a dag vertex to the single rate graph
        pimm2DAGVertex(vertex, dagVertex);
        dag.addVertex(dagVertex);
      }
    } else if (vertex instanceof ForkActor) {
      for (int i = 0; i < brv; ++i) {
        DAGVertex dagVertex = new DAGForkVertex();
        // add a dag vertex to the single rate graph
        pimm2DAGVertex(vertex, dagVertex);
        dag.addVertex(dagVertex);
      }
    } else if (vertex instanceof JoinActor) {
      for (int i = 0; i < brv; ++i) {
        DAGVertex dagVertex = new DAGJoinVertex();
        // add a dag vertex to the single rate graph
        pimm2DAGVertex(vertex, dagVertex);
        dag.addVertex(dagVertex);
      }
    } else {
      // Default vertex type
      for (int i = 0; i < brv; ++i) {
        DAGVertex dagVertex = new DAGVertex();
        // add a dag vertex to the single rate graph
        pimm2DAGVertex(vertex, dagVertex);
        dag.addVertex(dagVertex);
      }
    }
  }

  /**
   * The Class SourceConnection.
   */
  private class SourceConnection {
    private List<DAGVertex> sources;
    private long            production;
    private long            portID;

    public SourceConnection() {
      // Empty constructor
    }

    public void addSource(final DAGVertex vertex) {
      sources.add(vertex);
    }

    public void setProd(final long p) {
      this.production = p;
    }

    public void setPortID(final long pID) {
      this.portID = pID;
    }

  }

  /**
   * The Class SourceConnection.
   */
  private class TargetConnection {
    private List<DAGEdge> edges;
    private long          consumption;

    public TargetConnection() {
      // Empty constructor
    }

    public void addEdge(final DAGEdge edge) {
      edges.add(edge);
    }

    public void setCons(final long c) {
      this.consumption = c;
    }
  }

  /**
   * The Class StaticPiMM2SrDaGException.
   */
  public class StaticPiMM2SrDAGException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8272147472427685537L;

    /**
     * Instantiates a new static pi MM 2 SDF exception.
     *
     * @param message
     *          the message
     */
    public StaticPiMM2SrDAGException(final String message) {
      super(message);
    }
  }

}
