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
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.time.StopWatch;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultEdgePropertyType;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
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

  private static void printRV(final Map<AbstractVertex, Long> graphBRV) {
    for (final Map.Entry<AbstractVertex, Long> rv : graphBRV.entrySet()) {
      final String msg = rv.getKey().getVertexPath() + " x" + Long.toString(rv.getValue());
      WorkflowLogger.getLogger().log(Level.INFO, msg);
    }
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   * @throws StaticPiMM2SrDAGException
   *           the static pi MM 2 SDF exception
   */
  public MapperDAG launch(final int method) throws StaticPiMM2SrDAGException {
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
      final StopWatch timer = new StopWatch();
      timer.start();
      this.piHandler.resolveAllParameters();
      timer.stop();
      String msg = "Parameters and rates evaluations: " + timer + "s.";
      WorkflowLogger.getLogger().log(Level.INFO, msg);
      timer.reset();
      timer.start();
      piBRVAlgo.execute();
      this.graphBRV = piBRVAlgo.getBRV();
      timer.stop();
      msg = "Repetition vector computed in" + timer + "s.";
      WorkflowLogger.getLogger().log(Level.INFO, msg);
    } catch (final PiMMHelperException e) {
      throw new StaticPiMM2SrDAGException(e.getMessage());
    }
    StaticPiMM2SrDAGLauncher.printRV(this.graphBRV);
    // Visitor creating the SR-DAG
    StaticPiMM2SrDAGVisitor visitor;
    visitor = new StaticPiMM2SrDAGVisitor(new MapperDAG(new MapperEdgeFactory(), this.graph), this.graphBRV, this.scenario);
    final StopWatch timer = new StopWatch();
    timer.start();
    // Do the actual transformation of PiMM to Single Rate DAG
    visitor.doSwitch(this.graph);
    timer.stop();
    final String msg = "Dag transformation performed in " + timer + "s.";
    WorkflowLogger.getLogger().log(Level.INFO, msg);
    timer.reset();
    // Get the result
    final MapperDAG result = visitor.getResult();
    timer.start();
    // Aggregate edges
    // This is needed as the memory allocator does not yet handle multiple edges
    // There is a potential TODO for someone with a brave heart here
    // if you're doing this, remember to check for addAggregate in TAGDag.java and for createEdge in SRVerticesLinker.java.
    // also in ScriptRunner.xtend, there is a part where the aggregate list is flatten, check that also
    aggregateEdges(result);
    timer.stop();
    final String msg2 = "Edge aggregation performed in " + timer + "s.";
    WorkflowLogger.getLogger().log(Level.INFO, msg2);
    return result;
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
          firstEdge.setWeight(new DAGDefaultEdgePropertyType(firstEdge.getWeight().intValue() + extraEdge.getWeight().intValue()));
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
