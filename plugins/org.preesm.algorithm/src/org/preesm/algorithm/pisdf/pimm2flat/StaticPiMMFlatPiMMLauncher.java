/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
 * Alexandre Honorat <alexandre.honorat@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.pisdf.pimm2flat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.time.StopWatch;
import org.preesm.algorithm.pisdf.pimm2flat.visitor.StaticPiMM2FlatPiMMVisitor;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.statictools.PiMMHandler;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.PreesmScenario;

/**
 * The Class StaticPiMM2SDFLauncher.
 */
public class StaticPiMMFlatPiMMLauncher extends PiMMSwitch<Boolean> {

  /** The scenario. */
  private final PreesmScenario scenario;

  /** The graph. */
  private final PiGraph graph;

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
  public StaticPiMMFlatPiMMLauncher(final PreesmScenario scenario, final PiGraph graph) {
    this.scenario = scenario;
    this.graph = graph;
  }

  /**
   * Precondition: All.
   *
   * @return the SDFGraph obtained by visiting graph
   */
  public PiGraph launch() {
    final StopWatch timer = new StopWatch();
    timer.start();
    // 1. First we resolve all parameters.
    // It must be done first because, when removing persistence, local parameters have to be known at upper level
    PiMMHandler.resolveAllParameters(this.graph);
    timer.stop();
    String msg = "Parameters and rates evaluations: " + timer + "s.";
    PreesmLogger.getLogger().log(Level.INFO, msg);
    // 2. We perform the delay transformation step that deals with persistence
    timer.reset();
    timer.start();
    PiMMHandler.removePersistence(this.graph);
    timer.stop();
    String msg2 = "Persistence removal: " + timer + "s.";
    PreesmLogger.getLogger().log(Level.INFO, msg2);
    // 3. Compute BRV following the chosen method
    this.graphBRV = PiBRV.compute(graph, BRVMethod.LCM);
    // 4. Print the RV values
    PiBRV.printRV(graphBRV);
    // 4.5 Check periods with BRV
    PiMMHandler.checkPeriodicity(this.graphBRV);
    // 5. Now, flatten the graph
    return convert2FlatPiMM();
  }

  /**
   * Convert the PiSDF graph to flat PiSDF graph
   *
   * @return the resulting flatten graph
   */
  private PiGraph convert2FlatPiMM() {
    final StopWatch timer = new StopWatch();
    final StaticPiMM2FlatPiMMVisitor visitor = new StaticPiMM2FlatPiMMVisitor(this.graph, this.graphBRV, this.scenario);
    timer.start();
    // Transform Multi-Rate PiMM to Acyclic Single-Rate PiMM
    visitor.doSwitch(this.graph);
    timer.stop();
    final String msgPiMM2ASRPiMM = "Flattening transformation: " + timer + "s.";
    PreesmLogger.getLogger().log(Level.INFO, msgPiMM2ASRPiMM);
    final PiGraph result = visitor.getResult();
    return result;
  }

}
