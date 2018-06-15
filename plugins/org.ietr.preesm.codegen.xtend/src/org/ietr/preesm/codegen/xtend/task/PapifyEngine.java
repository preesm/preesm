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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.ConstantString;
import org.ietr.preesm.codegen.xtend.model.codegen.PapifyAction;
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
  static final String PAPIFY_CONFIGURATION  = "papifyConfig";
  static final String PAPIFY_ACTION_NAME    = "papifyActionName";
  static final String PAPIFY_CONFIG_NUMBER  = "papifyConfigNumber";
  static final String PAPIFY_MONITOR_EVENTS = "papifyMonitorEvents";
  static final String PAPIFY_MONITOR_TIMING = "papifyMonitorTiming";
  static final String PAPIFY_COMPONENT_NAME = "papifyComponentName";
  static final String PAPIFY_ACTOR_NAME     = "papifyActorName";
  static final String PAPIFY_CODESET_SIZE   = "papifyCodeSetSize";
  static final String PAPIFY_EVENTSET_NAMES = "papifyEventSetNames";

  /** The PREESM scenario **/
  private final PreesmScenario scenario;

  /** The original DAG **/
  private final DirectedAcyclicGraph dag;

  /**
   * Initialize the PapifyEngine
   *
   * @param dag
   *          the input dag
   * @param scenario
   *          the input scenario
   */
  public PapifyEngine(final DirectedAcyclicGraph dag, final PreesmScenario scenario) {
    this.dag = dag;
    this.scenario = scenario;
  }

  /**
   * Include all the properties required by the CodegenModelGenerator to correctly instrument the generated code
   *
   */
  public DirectedAcyclicGraph generate() {

    // Variables to check whether an actor has a monitoring configuration
    PapifyConfigManager papifyConfig = null;
    ArrayList<PapifyConfig> configSet = new ArrayList<>();
    PapifyConfig config;
    PapiComponent comp;
    Set<PapiEvent> events;
    Set<PapiEvent> includedEvents = new LinkedHashSet<>();
    PapiEvent timingEvent = new PapiEvent();
    ArrayList<PapiEventModifier> modifTimingList = new ArrayList<>();

    boolean configAdded = false;
    int configPosition;

    // The timing event
    timingEvent.setName("Timing");
    timingEvent.setDesciption("Event to time through PAPI_get_time()");
    timingEvent.setIndex(9999);
    timingEvent.setModifiers(modifTimingList);

    if (this.scenario.getPapifyConfigManager() != null) {
      papifyConfig = this.scenario.getPapifyConfigManager();

      String message = "Papifying";
      String finalName;

      // For each vertex, check the monitoring
      for (final DAGVertex vertex : this.dag.vertexSet()) {
        finalName = vertex.getInfo();
        if (finalName != null) {
          finalName = vertex.getInfo().substring(vertex.getInfo().indexOf('/') + 1).replace('/', '_');
          config = papifyConfig.getCorePapifyConfigGroups(finalName);
          if (config != null) {
            configAdded = false;
            configPosition = -1;

            // Check if the current monitoring has already been included
            for (PapifyConfig tmp : configSet) {
              if (tmp.getPAPIComponent().equals(config.getPAPIComponent()) && tmp.getPAPIEvents().equals(config.getPAPIEvents())) {
                configAdded = true;
                configPosition = configSet.indexOf(tmp);
              }
            }
            if (!configAdded) {
              configSet.add(config);
              configPosition = configSet.indexOf(config);
            }

            // The variable to store the monitoring
            PapifyAction papifyActionName = CodegenFactory.eINSTANCE.createPapifyAction();
            papifyActionName.setName("papify_actions_".concat(vertex.getName()));

            // Set the id associated to the Papify configuration
            Constant papifyConfigNumber = CodegenFactory.eINSTANCE.createConstant();
            papifyConfigNumber.setValue(configPosition);

            // Get component
            comp = config.getPAPIComponent();
            // Get events
            events = config.getPAPIEvents();
            String eventNames = "";
            includedEvents.clear();

            // At the beginning there is no monitoring
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_MONITOR_EVENTS, "No");
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_MONITOR_TIMING, "No");
            for (PapiEvent singleEvent : events) {
              if (singleEvent.equals(timingEvent)) {
                // Monitoring time
                this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_MONITOR_TIMING, "Yes");
              } else if (comp.containsEvent(singleEvent)) {
                includedEvents.add(singleEvent);
                // Monitoring events
                this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_MONITOR_EVENTS, "Yes");
                if (eventNames.equals("")) {
                  eventNames = singleEvent.getName();
                } else {
                  eventNames = eventNames.concat("," + singleEvent.getName());
                }
              }
            }

            // AAdd the PAPI component name
            ConstantString componentName = CodegenFactory.eINSTANCE.createConstantString();
            componentName.setName("component_name".concat(vertex.getName()));
            componentName.setValue(comp.getId());
            componentName.setComment("PAPI component name");

            // Add the actor name
            ConstantString actorName = CodegenFactory.eINSTANCE.createConstantString();
            actorName.setName("actor_name".concat(vertex.getName()));
            actorName.setValue(vertex.getName());
            actorName.setComment("Actor name");

            // Add the size of the CodeSet
            Constant codeSetSize = CodegenFactory.eINSTANCE.createConstant();
            codeSetSize.setName("CodeSetSize");
            codeSetSize.setValue(includedEvents.size());

            // Add the names of all the events
            ConstantString eventSetNames = CodegenFactory.eINSTANCE.createConstantString();
            eventSetNames.setName("allEventNames");
            eventSetNames.setValue(eventNames);
            eventSetNames.setComment("Papify events");

            // Set all the properties to the vertex
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_CONFIGURATION, message);
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_ACTION_NAME, papifyActionName);
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_COMPONENT_NAME, componentName);
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_ACTOR_NAME, actorName);
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_CODESET_SIZE, codeSetSize);
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_EVENTSET_NAMES, eventSetNames);
            this.dag.getVertex(vertex.getName()).getPropertyBean().setValue(PAPIFY_CONFIG_NUMBER, papifyConfigNumber);
          }
        }
      }
    }

    return this.dag;
  }
}
