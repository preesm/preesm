/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2013)
 * Pengcheng Mu [pengcheng.mu@insa-rennes.fr] (2008)
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
package org.preesm.model.scenario.serialize;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.EnergyConfig;
import org.preesm.model.scenario.MemoryCopySpeedValue;
import org.preesm.model.scenario.PapiComponent;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapiEventModifier;
import org.preesm.model.scenario.PapiEventSet;
import org.preesm.model.scenario.PapifyConfig;
import org.preesm.model.scenario.PerformanceObjective;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

/**
 * Writes a scenario as an XML.
 *
 * @author mpelcat
 */
public class ScenarioWriter {

  /** Current document. */
  private Document dom;

  /** Current scenario. */
  private final Scenario scenario;

  /**
   * Instantiates a new scenario writer.
   *
   * @param scenario
   *          the scenario
   */
  public ScenarioWriter(final Scenario scenario) {
    super();

    this.scenario = scenario;

    try {
      DOMImplementation impl;
      impl = DOMImplementationRegistry.newInstance().getDOMImplementation("Core 3.0 XML 3.0 LS");
      this.dom = impl.createDocument("", "scenario", null);
    } catch (final Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Generate scenario DOM.
   *
   * @return the document
   */
  public Document generateScenarioDOM() {

    final Element root = this.dom.getDocumentElement();

    writeFiles(root);
    writeConstraints(root);
    writeTimings(root);
    writeSimuParams(root);
    writeParameterValues(root);
    writePapifyConfigs(root);
    writeEnergyConfigs(root);

    return this.dom;
  }

  /**
   * Adds the parameter values.
   *
   * @param parent
   *          the parent
   */
  private void writeParameterValues(final Element parent) {
    final Element valuesElt = this.dom.createElement("parameterValues");
    parent.appendChild(valuesElt);

    final EMap<Parameter, String> parameterValues = this.scenario.getParameterValues();

    for (final Entry<Parameter, String> e : parameterValues) {
      writeParameterValue(valuesElt, e);
    }
  }

  /**
   * Adds the parameter value.
   *
   * @param parent
   *          the parent
   * @param value
   *          the value
   */
  private void writeParameterValue(final Element parent, final Entry<Parameter, String> value) {
    // Serialize only if the kept value(s) is different from the "default value" found in the PiGraph:
    // - if the parameter is actor dependent, there is no default value
    // - otherwise, compare the kept value to the parameter expression
    boolean needToBeSerialized = false;
    String valueToPrint = "";
    valueToPrint = "" + value.getValue();
    if (!value.getKey().getExpression().getExpressionAsString().equals(valueToPrint)) {
      needToBeSerialized = true;
    }
    if (needToBeSerialized) {
      final Element valueElt = this.dom.createElement("parameter");
      parent.appendChild(valueElt);

      valueElt.setAttribute("name", value.getKey().getName());
      valueElt.setAttribute("parent", value.getKey().getContainingPiGraph().getName());

      valueElt.setAttribute("value", valueToPrint);
    }
  }

  /**
   * Adds the papify configurations.
   *
   * @param parent
   *          the parent
   */
  private void writePapifyConfigs(final Element parent) {
    final Element papifyConfigs = this.dom.createElement("papifyConfigs");
    parent.appendChild(papifyConfigs);

    papifyConfigs.setAttribute("xmlUrl", this.scenario.getPapifyConfig().getXmlFileURL());

    final PapifyConfig manager = this.scenario.getPapifyConfig();

    final EMap<AbstractActor,
        EMap<String, EList<PapiEvent>>> papifyConfigGroupsActors = manager.getPapifyConfigGroupsActors();
    for (final Entry<AbstractActor, EMap<String, EList<PapiEvent>>> config : papifyConfigGroupsActors) {
      writePapifyConfigActor(papifyConfigs, config.getKey(), config.getValue());
    }
    for (final Entry<Component, EList<PapiComponent>> config : manager.getPapifyConfigGroupsPEs()) {
      writePapifyConfigPE(papifyConfigs, config.getKey(), config.getValue());
    }
  }

  /**
   * Adds the papify config Actor.
   *
   * @param parent
   *          the parent
   * @param value
   *          the value
   */
  private void writePapifyConfigActor(final Element parent, final AbstractActor actor,
      final EMap<String, EList<PapiEvent>> config) {

    if (actor != null && (config != null) && !config.isEmpty()) {
      final Element papifyConfigElt = this.dom.createElement("papifyConfigActor");
      parent.appendChild(papifyConfigElt);

      final Element actorPath = this.dom.createElement("actorPath");
      papifyConfigElt.appendChild(actorPath);
      actorPath.setAttribute("actorPath", actor.getVertexPath());
      final EMap<String, EList<PapiEvent>> eventSets = config;
      final Set<String> keys = eventSets.keySet();
      for (final String key : keys) {
        final List<PapiEvent> eventSet = eventSets.get(key);
        if (!eventSet.isEmpty()) {
          final Element component = this.dom.createElement("component");
          actorPath.appendChild(component);
          component.setAttribute("component", key);
          for (final PapiEvent event : eventSet) {
            final Element singleEvent = this.dom.createElement("event");
            component.appendChild(singleEvent);
            writePapifyEvent(singleEvent, event);
          }
        }
      }
    }
  }

  /**
   * Adds the papify config PE.
   *
   * @param parent
   *          the parent
   * @param value
   *          the value
   */
  private void writePapifyConfigPE(final Element parent, final Component slamComponent,
      final List<PapiComponent> papiComponents) {
    if (slamComponent != null && (papiComponents != null) && !papiComponents.isEmpty()) {
      final Element papifyConfigElt = this.dom.createElement("papifyConfigPE");
      parent.appendChild(papifyConfigElt);

      final Element peType = this.dom.createElement("peType");
      papifyConfigElt.appendChild(peType);
      peType.setAttribute("peType", slamComponent.getVlnv().getName());

      for (final PapiComponent component : papiComponents) {
        final Element singleComponent = this.dom.createElement("PAPIComponent");
        peType.appendChild(singleComponent);
        writePapifyComponent(singleComponent, component);
      }
    }
  }

  /**
   * Adds the papify component.
   *
   * @param component
   *          the parent component
   * @param papiComponent
   *          the papiComponent itself
   */
  private void writePapifyComponent(final Element component, final PapiComponent papiComponent) {

    component.setAttribute("componentId", papiComponent.getId());
    component.setAttribute("componentType", papiComponent.getType().toString());
    component.setAttribute("componentIndex", Integer.toString(papiComponent.getIndex()));

    for (final PapiEventSet eventSet : papiComponent.getEventSets()) {
      final Element singleEventSet = this.dom.createElement("eventSet");
      component.appendChild(singleEventSet);
      singleEventSet.setAttribute("type", eventSet.getType().toString());
      for (final PapiEvent event : eventSet.getEvents()) {
        final Element singleEvent = this.dom.createElement("event");
        singleEventSet.appendChild(singleEvent);
        writePapifyEvent(singleEvent, event);
      }
    }
  }

  /**
   * Adds the papify event.
   *
   * @param event
   *          the parent event
   * @param papiEvent
   *          the papiEvent itself
   */
  private void writePapifyEvent(final Element event, final PapiEvent papiEvent) {

    final Element eventId = this.dom.createElement("eventId");
    event.appendChild(eventId);
    eventId.setAttribute("eventId", Integer.toString(papiEvent.getIndex()));
    final Element eventName = this.dom.createElement("eventName");
    event.appendChild(eventName);
    eventName.setAttribute("eventName", papiEvent.getName());
    final Element eventDescription = this.dom.createElement("eventDescription");
    event.appendChild(eventDescription);
    eventDescription.setAttribute("eventDescription", papiEvent.getDescription());

    for (final PapiEventModifier eventModifier : papiEvent.getModifiers()) {
      final Element singleEventModifier = this.dom.createElement("eventModifier");
      event.appendChild(singleEventModifier);
      singleEventModifier.setAttribute("name", eventModifier.getName());
      singleEventModifier.setAttribute("description", eventModifier.getDescription());
    }
  }

  /**
   * Adds the simu params.
   *
   * @param parent
   *          the parent
   */
  private void writeSimuParams(final Element parent) {

    final Element params = this.dom.createElement("simuParams");
    parent.appendChild(params);

    final Element core = this.dom.createElement("mainCore");
    params.appendChild(core);
    final ComponentInstance mainOperator = this.scenario.getSimulationInfo().getMainOperator();
    if (mainOperator != null) {
      core.setTextContent(mainOperator.getInstanceName());
    }

    final Element medium = this.dom.createElement("mainComNode");
    params.appendChild(medium);
    final ComponentInstance mainComNode = this.scenario.getSimulationInfo().getMainComNode();
    if (mainComNode != null) {
      medium.setTextContent(mainComNode.getInstanceName());
    }

    final Element dataSize = this.dom.createElement("averageDataSize");
    params.appendChild(dataSize);
    dataSize.setTextContent(String.valueOf(this.scenario.getSimulationInfo().getAverageDataSize()));

    final Element dataTypes = this.dom.createElement("dataTypes");
    params.appendChild(dataTypes);

    final EMap<String, Long> types = this.scenario.getSimulationInfo().getDataTypes();
    for (final Entry<String, Long> dataType : types) {
      writeDataType(dataTypes, dataType.getKey(), dataType.getValue());
    }

    final Element sVOperators = this.dom.createElement("specialVertexOperators");
    params.appendChild(sVOperators);

    for (final ComponentInstance opId : this.scenario.getSimulationInfo().getSpecialVertexOperators()) {
      writeSpecialVertexOperator(sVOperators, opId);
    }
  }

  /**
   * Adds the data type.
   *
   * @param parent
   *          the parent
   * @param dataType
   *          the data type
   */
  private void writeDataType(final Element parent, final String dataTypeName, final long dataTypeSize) {

    final Element dataTypeElt = this.dom.createElement("dataType");
    parent.appendChild(dataTypeElt);
    dataTypeElt.setAttribute("name", dataTypeName);
    dataTypeElt.setAttribute("size", Long.toString(dataTypeSize));
  }

  /**
   * Adds the special vertex operator.
   *
   * @param parent
   *          the parent
   * @param opId
   *          the op id
   */
  private void writeSpecialVertexOperator(final Element parent, final ComponentInstance opId) {

    final Element dataTypeElt = this.dom.createElement("specialVertexOperator");
    parent.appendChild(dataTypeElt);
    dataTypeElt.setAttribute("path", opId.getInstanceName());
  }

  /**
   * Adds the files.
   *
   * @param parent
   *          the parent
   */
  private void writeFiles(final Element parent) {

    final Element files = this.dom.createElement("files");
    parent.appendChild(files);

    final PiGraph algorithm = this.scenario.getAlgorithm();
    if (algorithm != null) {
      final Element algo = this.dom.createElement("algorithm");
      files.appendChild(algo);
      algo.setAttribute("url", algorithm.getUrl());
    }

    final Design design = this.scenario.getDesign();
    if (design != null) {
      final Element archi = this.dom.createElement("architecture");
      files.appendChild(archi);
      archi.setAttribute("url", design.getUrl());
    }

    final Element codeGenDir = this.dom.createElement("codegenDirectory");
    files.appendChild(codeGenDir);
    codeGenDir.setAttribute("url", this.scenario.getCodegenDirectory());

  }

  /**
   * Adds the constraints.
   *
   * @param parent
   *          the parent
   */
  private void writeConstraints(final Element parent) {

    final Element constraints = this.dom.createElement("constraints");
    parent.appendChild(constraints);

    String groupConstraintsFileURL = this.scenario.getConstraints().getGroupConstraintsFileURL();
    constraints.setAttribute("excelUrl", groupConstraintsFileURL);

    for (final Entry<ComponentInstance, EList<AbstractActor>> cst : this.scenario.getConstraints()
        .getGroupConstraints()) {
      final ComponentInstance component = cst.getKey();
      if (component != null) {
        final EList<AbstractActor> actors = cst.getValue();
        writeConstraints(constraints, component, actors);
      }
    }
  }

  /**
   * Adds the constraint.
   *
   * @param parent
   *          the parent
   * @param cst
   *          the cst
   */
  private void writeConstraints(final Element parent, final ComponentInstance cmpi, final List<AbstractActor> actors) {

    final Element constraintGroupElt = this.dom.createElement("constraintGroup");
    parent.appendChild(constraintGroupElt);

    final Element opdefelt = this.dom.createElement("operator");
    constraintGroupElt.appendChild(opdefelt);
    final String instanceName = cmpi.getInstanceName();
    opdefelt.setAttribute("name", instanceName);

    for (final AbstractActor actor : actors) {
      final Element vtxelt = this.dom.createElement("task");
      constraintGroupElt.appendChild(vtxelt);
      vtxelt.setAttribute("name", actor.getVertexPath());
    }
  }

  /**
   * Adds the timings.
   *
   * @param parent
   *          the parent
   */
  private void writeTimings(final Element parent) {

    final Element timingsElement = this.dom.createElement("timings");
    parent.appendChild(timingsElement);

    String excelFileURL = this.scenario.getTimings().getExcelFileURL();
    timingsElement.setAttribute("excelUrl", excelFileURL);

    final EMap<AbstractActor, EMap<Component, String>> actorsTimings = this.scenario.getTimings().getActorTimings();
    for (final Entry<AbstractActor, EMap<Component, String>> actorTimings : actorsTimings) {
      for (final Entry<Component, String> timing : actorTimings.getValue()) {
        writeTiming(timingsElement, actorTimings.getKey(), timing.getKey(), timing.getValue());
      }
    }

    final EMap<Component, MemoryCopySpeedValue> memTimings = this.scenario.getTimings().getMemTimings();
    for (final Entry<Component, MemoryCopySpeedValue> opDef : this.scenario.getTimings().getMemTimings()) {
      writeMemcpySpeed(timingsElement, opDef.getKey(), memTimings.get(opDef.getKey()).getSetupTime(),
          memTimings.get(opDef.getKey()).getTimePerUnit());
    }
  }

  /**
   * Adds the timing.
   *
   * @param parent
   *          the parent
   * @param timing
   *          the timing
   */
  private void writeTiming(final Element parent, final AbstractActor actor, final Component component,
      final String timing) {
    final Element timingelt = this.dom.createElement("timing");
    parent.appendChild(timingelt);
    timingelt.setAttribute("vertexname", actor.getVertexPath());
    timingelt.setAttribute("opname", component.getVlnv().getName());
    timingelt.setAttribute("time", timing);
  }

  /**
   * Adds the memcpy speed.
   *
   * @param parent
   *          the parent
   * @param opDef
   *          the op def
   * @param memcpySetupTime
   *          the memcpy setup time
   * @param memcpyTimePerUnit
   *          the memcpy time per unit
   */
  private void writeMemcpySpeed(final Element parent, final Component opDef, final long memcpySetupTime,
      final double memcpyTimePerUnit) {

    final Element timingelt = this.dom.createElement("memcpyspeed");
    parent.appendChild(timingelt);
    timingelt.setAttribute("opname", opDef.getVlnv().getName());
    timingelt.setAttribute("setuptime", Long.toString(memcpySetupTime));
    timingelt.setAttribute("timeperunit", Double.toString(memcpyTimePerUnit));
  }

  /**
   * Adds the Energy configurations.
   *
   * @param parent
   *          the parent
   */
  private void writeEnergyConfigs(final Element parent) {
    final Element energyConfigs = this.dom.createElement("energyConfigs");
    parent.appendChild(energyConfigs);

    // energyConfigs.setAttribute("xmlUrl", this.scenario.getPapifyConfig().getXmlFileURL());

    final EnergyConfig manager = this.scenario.getEnergyConfig();

    writePerformanceObjective(energyConfigs, manager.getPerformanceObjective());
  }

  /**
   * Adds the PerformanceObjective.
   *
   * @param parent
   *          the parent
   * @param value
   *          the value
   */
  private void writePerformanceObjective(final Element parent, final PerformanceObjective performanceObjective) {

    final Element performanceObjectiveElt = this.dom.createElement("performanceObjective");
    parent.appendChild(performanceObjectiveElt);

    performanceObjectiveElt.setAttribute("objectiveEPS", Float.toString(performanceObjective.getObjectiveEPS()));
    performanceObjectiveElt.setAttribute("toleranceEPS", Float.toString(performanceObjective.getToleranceEPS()));
  }
}
