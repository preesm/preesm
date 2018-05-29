/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Hascoet <jhascoet@kalray.eu> (2016)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.xtend.task;

import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.papi.PapiComponent;
import org.ietr.preesm.core.scenario.papi.PapiEvent;
import org.ietr.preesm.core.scenario.papi.PapiEventModifier;
import org.ietr.preesm.core.scenario.papi.PapifyConfig;
import org.ietr.preesm.core.scenario.papi.PapifyConfigManager;

/**
 * The Class PapifyEngine.
 */
public class PapifyEngine {

  /** The PAPIFY_CONFIGURATION constant **/
  static final String PAPIFY_CONFIGURATION = "papifyConfig";

  /** The PREESM scenario **/
  private final PreesmScenario scenario;

  /** The original DAG **/
  private final DirectedAcyclicGraph dag;

  public PapifyEngine(final DirectedAcyclicGraph dag, final PreesmScenario scenario) {
    this.dag = dag;
    this.scenario = scenario;
  }

  public DirectedAcyclicGraph generate() {

    PapifyConfigManager papifyConfig;
    Set<PapifyConfig> configSet;
    PapifyConfig config;
    PapiComponent comp;
    Set<PapiEvent> events;
    Set<PapiEvent> includedEvents;
    PapiEvent timingEvent = new PapiEvent();
    Set<PapiEventModifier> modifTimingList = null;

    DirectedAcyclicGraph outputDAG = new DirectedAcyclicGraph();

    timingEvent.setName("Timing");
    timingEvent.setDesciption("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);

    if (this.scenario.getPapifyConfigManager() != null) {
      papifyConfig = this.scenario.getPapifyConfigManager();
    }

    String message = "Papifying";

    for (final DAGVertex vertex : this.dag.vertexSet()) {
      // vertex.getPropertyBean().setValue(PAPIFY_CONFIGURATION, true);
      // outputDAG.vertexSet().remove(vertex);
      this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_CONFIGURATION, message);
      System.out.println("B");
      System.out.println(this.dag.getVertex(vertex.getName()).getPropertyBean().getValue(PapifyEngine.PAPIFY_CONFIGURATION, String.class));

    }

    return this.dag;
  }
}
