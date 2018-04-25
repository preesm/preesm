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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.time.StopWatch;
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
    visitor = new StaticPiMM2SrDAGVisitor(new MapperDAG(new MapperEdgeFactory(), this.graph), this.graphBRV, this.scenario);
    StopWatch timer = new StopWatch();
    timer.start();
    visitor.doSwitch(this.graph);
    timer.stop();
    WorkflowLogger.getLogger().log(Level.INFO, "Dag transformation performed in " + timer.toString() + "s.");
    return visitor.getResult();
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
