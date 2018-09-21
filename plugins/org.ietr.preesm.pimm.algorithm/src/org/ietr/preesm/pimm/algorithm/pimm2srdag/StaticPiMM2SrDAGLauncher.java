/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultEdgePropertyType;
import org.ietr.dftools.workflow.WorkflowException;
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
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.serialize.PiWriter;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.pimm.algorithm.helper.LCMBasedBRV;
import org.ietr.preesm.pimm.algorithm.helper.PiBRV;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHandler;
import org.ietr.preesm.pimm.algorithm.helper.PiMMHelperException;
import org.ietr.preesm.pimm.algorithm.helper.TopologyBasedBRV;
import org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor.StaticPiMM2ASrPiMMVisitor;
import org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor.StaticPiMM2MapperDAGVisitor;
import org.ietr.preesm.utils.files.ContainersManager;
import org.ietr.preesm.utils.paths.PathTools;

/**
 * The Class StaticPiMM2SDFLauncher.
 */
public class StaticPiMM2SrDAGLauncher extends PiMMSwitch<Boolean> {

  /** The scenario. */
  private final PreesmScenario scenario;

  /** The graph. */
  private final PiGraph graph;

  /** The graph. */
  private final PiMMHandler piHandler;

  /** Map from Pi actors to their Repetition Vector value. */
  protected Map<AbstractVertex, Long> graphBRV = new LinkedHashMap<>();

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

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   * @throws StaticPiMM2SrDAGException
   *           the static pi MM 2 SDF exception
   */
  public MapperDAG launch(final int method) throws StaticPiMM2SrDAGException {
    final StopWatch timer = new StopWatch();
    try {
      timer.start();
      // 1. First we resolve all parameters.
      // It must be done first because, when removing persistence, local parameters have to be known at upper level
      this.piHandler.resolveAllParameters();
      timer.stop();
      String msg = "Parameters and rates evaluations: " + timer + "s.";
      WorkflowLogger.getLogger().log(Level.INFO, msg);
      // 2. We perform the delay transformation step that deals with persistence
      timer.reset();
      timer.start();
      this.piHandler.removePersistence();
      timer.stop();
      String msg2 = "Persistence removal: " + timer + "s.";
      WorkflowLogger.getLogger().log(Level.INFO, msg2);
    } catch (PiMMHelperException e) {
      throw new StaticPiMM2SrDAGException(e.getMessage());
    }
    // 3. Compute BRV following the chosen method
    computeBRV(method);
    // 4. Print the RV values
    printRV();
    // 5. Convert to SR-DAG
    final MapperDAG result = convert2SRDAG();
    // 6. Aggregate edges
    timer.reset();
    timer.start();
    // This is needed as the memory allocator does not yet handle multiple edges
    // There is a potential TODO for someone with a brave heart here
    // if you're doing this, remember to check for addAggregate in TAGDag.java and for createEdge in
    // SRVerticesLinker.java.
    // also in ScriptRunner.xtend, there is a part where the aggregate list is flatten, check that also
    aggregateEdges(result);
    timer.stop();
    final String msg = "Edge aggregation: " + timer + "s.";
    WorkflowLogger.getLogger().log(Level.INFO, msg);
    return result;
  }

  private void saveGraph(final PiGraph graph) {
    final String url = scenario.getAlgorithmURL();
    final ResourceSet resourceSet = new ResourceSetImpl();

    final URI uriGraph = URI.createPlatformResourceURI(url, true);
    if ((uriGraph.fileExtension() == null) || !uriGraph.fileExtension().contentEquals("pi")) {
      throw new WorkflowException("unhandled file exception: " + uriGraph.fileExtension());
    }
    Resource ressource;
    ressource = resourceSet.getResource(uriGraph, true);

    // Creates the output file now
    final String sXmlPath = PathTools.getAbsolutePath("Algo/generated/", "delays") + "/Algo/generated";
    IPath xmlPath = new Path(sXmlPath);
    // Get a complete valid path with all folders existing
    try {
      if (xmlPath.getFileExtension() != null) {
        ContainersManager.createMissingFolders(xmlPath.removeFileExtension().removeLastSegments(1));
      } else {
        ContainersManager.createMissingFolders(xmlPath);
        xmlPath = xmlPath.append(graph.getName() + ".pi");
      }
    } catch (CoreException | IllegalArgumentException e) {
      throw new WorkflowException("Path " + sXmlPath + " is not a valid path for export.\n" + e.getMessage());
    }

    OutputStream outStream;
    try {
      // Write the Graph to the OutputStream using the Pi format
      final URI uri = URI.createPlatformResourceURI(xmlPath.toString(), true);
      // Get the project
      final String platformString = uri.toPlatformString(true);
      final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
      outStream = new FileOutputStream(documentFile.getLocation().toOSString());

      new PiWriter(uri).write(graph, outStream);
    } catch (FileNotFoundException e) {
      throw new WorkflowException("Could not open outputstream file " + xmlPath.toString());
    }
  }

  /**
   * Convert the PiSDF graph to SRDAG
   * 
   * @return the resulting SR DAG
   */
  private MapperDAG convert2SRDAG() {
    final StopWatch timer = new StopWatch();
    final StaticPiMM2ASrPiMMVisitor visitorPiMM2ASRPiMM = new StaticPiMM2ASrPiMMVisitor(this.graph, this.graphBRV,
        this.scenario);
    timer.start();
    // Transform Multi-Rate PiMM to Acyclic Single-Rate PiMM
    visitorPiMM2ASRPiMM.doSwitch(this.graph);
    timer.stop();
    final String msgPiMM2ASRPiMM = "Acyclic Single-Rate transformation: " + timer + "s.";
    WorkflowLogger.getLogger().log(Level.INFO, msgPiMM2ASRPiMM);
    // Do some optimization on the graph
    timer.reset();
    timer.start();
    final PiGraph acyclicSRPiMM = visitorPiMM2ASRPiMM.getResult();
    // Now let's optimize some shit
    boolean keepGoing = true;
    while (keepGoing) {
      keepGoing = optimizeForkandJoin(acyclicSRPiMM);
    }
    optimizeBRandRB(acyclicSRPiMM);
    timer.stop();
    final String msgOptimsGraphs = "Graph optimizations: " + timer + "s.";
    WorkflowLogger.getLogger().log(Level.INFO, msgOptimsGraphs);
    // saveGraph(acyclicSRPiMM);

    final StaticPiMM2MapperDAGVisitor visitor = new StaticPiMM2MapperDAGVisitor(acyclicSRPiMM, this.scenario);
    // Convert the PiMM vertices to DAG vertices
    timer.reset();
    timer.start();
    visitor.doSwitch(acyclicSRPiMM);
    timer.stop();
    final String msgPiMM2DAG = "Dag conversion: " + timer + "s.";
    WorkflowLogger.getLogger().log(Level.INFO, msgPiMM2DAG);
    timer.reset();
    // Get the result
    return visitor.getResult();
  }

  /**
   * Optimize Fork / Fork and Join / Join connections
   * 
   * @param graph
   *          The graph to optimize
   * @return true if at least one connections was optimized
   */
  private boolean optimizeForkandJoin(final PiGraph graph) {
    boolean retValue = false;
    for (final AbstractActor actor : graph.getActors()) {
      if (actor instanceof ForkActor) {
        retValue |= removeFork(graph, actor);
      } else if (actor instanceof JoinActor) {
        retValue |= removeJoin(graph, actor);
      }
    }
    return retValue;
  }

  /**
   * Optimize Broadcast / Fork and Join / Roundbuffer connections
   * 
   * Remove Broadcast / Roundbuffer with unique input / output same input / output rates
   * 
   * @param graph
   *          The graph to optimize
   */
  private void optimizeBRandRB(final PiGraph graph) {
    for (final AbstractActor actor : graph.getActors()) {
      if (actor instanceof BroadcastActor) {
        removeFork(graph, actor);
        if (actor.getDataInputPorts().size() == 1 && actor.getDataOutputPorts().size() == 1) {
          // We remove broadcasts that have only one input / output and with same rates
          final DataInputPort dataInputPort = actor.getDataInputPorts().get(0);
          final Expression inputRateExpression = dataInputPort.getPortRateExpression();
          final long inputRate = Long.parseLong(inputRateExpression.getExpressionString());
          final DataOutputPort dataOutputPort = actor.getDataOutputPorts().get(0);
          final Expression outputRateExpression = dataOutputPort.getPortRateExpression();
          final long outputRate = Long.parseLong(outputRateExpression.getExpressionString());
          if (inputRate == outputRate) {
            // We can remove the one of the FIFO and the actor
            final Fifo outgoingFifo = dataOutputPort.getOutgoingFifo();
            final Fifo incomingFifo = dataInputPort.getIncomingFifo();
            outgoingFifo.setSourcePort(incomingFifo.getSourcePort());
            graph.removeFifo(incomingFifo);
            graph.removeActor(actor);
          }
        }
      } else if (actor instanceof RoundBufferActor) {
        removeJoin(graph, actor);
        if (actor.getDataInputPorts().size() == 1 && actor.getDataOutputPorts().size() == 1) {
          // We remove broadcasts that have only one input / output and with same rates
          final DataInputPort dataInputPort = actor.getDataInputPorts().get(0);
          final Expression inputRateExpression = dataInputPort.getPortRateExpression();
          final long inputRate = Long.parseLong(inputRateExpression.getExpressionString());
          final DataOutputPort dataOutputPort = actor.getDataOutputPorts().get(0);
          final Expression outputRateExpression = dataOutputPort.getPortRateExpression();
          final long outputRate = Long.parseLong(outputRateExpression.getExpressionString());
          if (inputRate == outputRate) {
            // We can remove the one of the FIFO and the actor
            final Fifo outgoingFifo = dataOutputPort.getOutgoingFifo();
            final Fifo incomingFifo = dataInputPort.getIncomingFifo();
            outgoingFifo.setSourcePort(incomingFifo.getSourcePort());
            graph.removeFifo(incomingFifo);
            graph.removeActor(actor);
          }
        }
      }
    }
  }

  /**
   * Remove the Join -> Roundbuffer / Join connections
   * 
   * <pre>
   * in_0 -> | J | -> in | RB | 
   * in_1 -> |   |  
   * 
   * becomes  in_0 -> | RB | 
   *          in_1 -> |    |
   * </pre>
   * 
   * @param graph
   *          the graph
   * @param actor
   *          the roundbuffer or join actor to evaluate
   */
  private boolean removeJoin(final PiGraph graph, final AbstractActor actor) {
    int offset = 0;
    final List<DataInputPort> toRemove = new ArrayList<>();
    final Map<Integer, List<DataInputPort>> toReplace = new LinkedHashMap<>();
    for (final DataInputPort dip : actor.getDataInputPorts()) {
      final Fifo incomingFifo = dip.getIncomingFifo();
      final DataOutputPort targetPort = incomingFifo.getSourcePort();
      final AbstractActor sourceActor = targetPort.getContainingActor();
      if (sourceActor instanceof JoinActor) {
        final int index = actor.getDataInputPorts().indexOf(dip);
        toReplace.put(index + offset, sourceActor.getDataInputPorts());
        offset += sourceActor.getDataInputPorts().size() - 1;
        graph.removeActor(sourceActor);
        graph.removeFifo(incomingFifo);
        toRemove.add(dip);
      }
    }
    toRemove.forEach(actor.getDataInputPorts()::remove);
    toReplace.forEach((k, v) -> actor.getDataInputPorts().addAll(k, v));
    return !toReplace.isEmpty();
  }

  /**
   * Remove the Broadcast or Fork -> Fork connections
   * 
   * <pre>
   *               | F | -> out_0 
   * | BR | -> out |   | -> out_1
   * 
   * becomes  | BR | -> out_0
   *          |    | -> out_1
   * </pre>
   * 
   * @param graph
   *          the graph
   * @param actor
   *          the broadcast or fork actor to evaluate
   */
  private boolean removeFork(final PiGraph graph, final AbstractActor actor) {
    int offset = 0;
    final List<DataOutputPort> toRemove = new ArrayList<>();
    final Map<Integer, List<DataOutputPort>> toReplace = new LinkedHashMap<>();
    for (final DataOutputPort dop : actor.getDataOutputPorts()) {
      final Fifo outgoingFifo = dop.getOutgoingFifo();
      final DataInputPort targetPort = outgoingFifo.getTargetPort();
      final AbstractActor targetActor = targetPort.getContainingActor();
      if (targetActor instanceof ForkActor) {
        final int index = actor.getDataOutputPorts().indexOf(dop);
        toReplace.put(index + offset, targetActor.getDataOutputPorts());
        offset += targetActor.getDataOutputPorts().size() - 1;
        graph.removeActor(targetActor);
        graph.removeFifo(outgoingFifo);
        toRemove.add(dop);
      }
    }
    toRemove.forEach(actor.getDataOutputPorts()::remove);
    toReplace.forEach((k, v) -> actor.getDataOutputPorts().addAll(k, v));
    return !toReplace.isEmpty();
  }

  /**
   * Computes the BRV of a PiSDF graph using either LCM method or Topology Matrix.
   * 
   * @param method
   *          the method to use for computing the BRV
   * @throws StaticPiMM2SrDAGException
   *           the StaticPiMM2SrDAGException exception
   */
  private void computeBRV(final int method) throws StaticPiMM2SrDAGException {
    PiBRV piBRVAlgo;
    if (method == 0) {
      piBRVAlgo = new TopologyBasedBRV(this.piHandler);
    } else if (method == 1) {
      piBRVAlgo = new LCMBasedBRV(this.piHandler);
    } else {
      throw new StaticPiMM2SrDAGException("unexpected value for BRV method: [" + Integer.toString(method) + "]");
    }
    try {
      final StopWatch timer = new StopWatch();
      timer.start();
      piBRVAlgo.execute();
      this.graphBRV = piBRVAlgo.getBRV();
      timer.stop();
      final String msg = "Repetition vector computed in" + timer + "s.";
      WorkflowLogger.getLogger().log(Level.INFO, msg);
    } catch (final PiMMHelperException e) {
      throw new StaticPiMM2SrDAGException(e.getMessage());
    }
  }

  /**
   * Print the BRV values of every vertex
   */
  private void printRV() {
    for (final Map.Entry<AbstractVertex, Long> rv : this.graphBRV.entrySet()) {
      final String msg = rv.getKey().getVertexPath() + " x" + Long.toString(rv.getValue());
      WorkflowLogger.getLogger().log(Level.INFO, msg);
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
        if (allEdges.size() == 1 || toRemove.contains(allEdges.get(1))) {
          continue;
        }
        // Get the first edge
        final DAGEdge firstEdge = allEdges.remove(0);
        for (final DAGEdge extraEdge : allEdges) {
          // Update the weight
          firstEdge.setWeight(
              new DAGDefaultEdgePropertyType(firstEdge.getWeight().intValue() + extraEdge.getWeight().intValue()));
          // Add the aggregate edge
          firstEdge.getAggregate().add(extraEdge.getAggregate().get(0));
          toRemove.add(extraEdge);
        }
      }
      // Removes the extra edges
      toRemove.forEach(dag::removeEdge);
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
