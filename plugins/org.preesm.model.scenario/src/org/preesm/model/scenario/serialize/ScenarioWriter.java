/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2013)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
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
import java.util.logging.Level;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
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
import org.preesm.model.slam.TimingType;
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
      PreesmLogger.getLogger().log(Level.WARNING, "Could not write scenario", e);
    }

  }

  /**
   * Generate scenario DOM.
   *
   * @return the document
   */
  public Document generateScenarioDOM() {

    final Element root = this.dom.getDocumentElement();

    writeFlags(root);
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
   * Adds the flags.
   *
   * @param parent
   *          the parent
   */
  private void writeFlags(final Element parent) {
    final Element flags = this.dom.createElement(ScenarioConstants.FLAGS);
    parent.appendChild(flags);

    final Element sizesAreInBit = this.dom.createElement(ScenarioConstants.SIZE_ARE_IN_BIT);
    flags.appendChild(sizesAreInBit);
  }

  /**
   * Adds the parameter values.
   *
   * @param parent
   *          the parent
   */
  private void writeParameterValues(final Element parent) {
    final Element valuesElt = this.dom.createElement(ScenarioConstants.PARAMETER_VALUES);
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
    String valueToPrint;
    valueToPrint = "" + value.getValue();
    if (!value.getKey().getExpression().getExpressionAsString().equals(valueToPrint)) {
      needToBeSerialized = true;
    }
    if (needToBeSerialized) {
      final Element valueElt = this.dom.createElement(ScenarioConstants.PARAMETER);
      parent.appendChild(valueElt);

      valueElt.setAttribute(ScenarioConstants.NAME, value.getKey().getName());
      valueElt.setAttribute(ScenarioConstants.PARENT, value.getKey().getContainingPiGraph().getName());

      valueElt.setAttribute(ScenarioConstants.VALUE, valueToPrint);
    }
  }

  /**
   * Adds the papify configurations.
   *
   * @param parent
   *          the parent
   */
  private void writePapifyConfigs(final Element parent) {
    final Element papifyConfigs = this.dom.createElement(ScenarioConstants.PAPIFY_CONFIGS);
    parent.appendChild(papifyConfigs);

    papifyConfigs.setAttribute(ScenarioConstants.XML_URL, this.scenario.getPapifyConfig().getXmlFileURL());

    final PapifyConfig manager = this.scenario.getPapifyConfig();

    final EMap<AbstractActor,
        EMap<String, EList<PapiEvent>>> papifyConfigGroupsActors = manager.getPapifyConfigGroupsActors();
    for (final Entry<AbstractActor, EMap<String, EList<PapiEvent>>> config : papifyConfigGroupsActors) {
      writePapifyConfigActor(papifyConfigs, config.getKey(), config.getValue());
    }
    for (final Entry<Component, EList<PapiComponent>> config : manager.getPapifyConfigGroupsPEs()) {
      writePapifyConfigPE(papifyConfigs, config.getKey(), config.getValue());
    }
    for (final Entry<Component, EMap<PapiEvent, Double>> energyKPIModel : manager.getPapifyEnergyKPIModels()) {
      writePapifyEnergyKPIModel(papifyConfigs, energyKPIModel.getKey(), energyKPIModel.getValue());
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
      final Element papifyConfigElt = this.dom.createElement(ScenarioConstants.PAPIFY_CONFIG_ACTOR);
      parent.appendChild(papifyConfigElt);

      final Element actorPath = this.dom.createElement(ScenarioConstants.ACTOR_PATH);
      papifyConfigElt.appendChild(actorPath);
      actorPath.setAttribute(ScenarioConstants.ACTOR_PATH, actor.getVertexPath());
      final EMap<String, EList<PapiEvent>> eventSets = config;
      final Set<String> keys = eventSets.keySet();
      for (final String key : keys) {
        final List<PapiEvent> eventSet = eventSets.get(key);
        if (!eventSet.isEmpty()) {
          final Element component = this.dom.createElement(ScenarioConstants.COMPONENT);
          actorPath.appendChild(component);
          component.setAttribute(ScenarioConstants.COMPONENT, key);
          for (final PapiEvent event : eventSet) {
            final Element singleEvent = this.dom.createElement(ScenarioConstants.EVENT);
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
      final Element papifyConfigElt = this.dom.createElement(ScenarioConstants.PAPIFY_CONFIG_PE);
      parent.appendChild(papifyConfigElt);

      final Element peType = this.dom.createElement(ScenarioConstants.PE_TYPE);
      papifyConfigElt.appendChild(peType);
      peType.setAttribute(ScenarioConstants.PE_TYPE, slamComponent.getVlnv().getName());

      for (final PapiComponent component : papiComponents) {
        final Element singleComponent = this.dom.createElement(ScenarioConstants.PAPI_COMPONENT);
        peType.appendChild(singleComponent);
        writePapifyComponent(singleComponent, component);
      }
    }
  }

  /**
   * Adds the papify energy KPI model.
   *
   * @param parent
   *          the parent
   * @param slamComponent
   *          the slamComponent
   * @param modelParams
   *          the model parameters
   */
  private void writePapifyEnergyKPIModel(final Element parent, final Component slamComponent,
      final EMap<PapiEvent, Double> modelParams) {
    if (slamComponent != null && (modelParams != null) && !modelParams.isEmpty()) {
      final Element energyModelPETypeElt = this.dom.createElement(ScenarioConstants.ENERGY_MODEL_PE_TYPE);
      parent.appendChild(energyModelPETypeElt);

      final Element peType = this.dom.createElement(ScenarioConstants.PE_TYPE);
      energyModelPETypeElt.appendChild(peType);
      peType.setAttribute(ScenarioConstants.PE_TYPE, slamComponent.getVlnv().getName());
      for (final ComponentInstance compInstance : slamComponent.getInstances()) {
        final Element peInstance = this.dom.createElement(ScenarioConstants.PE_INSTANCE);
        peType.appendChild(peInstance);
        peInstance.setAttribute(ScenarioConstants.PE_INSTANCE, compInstance.getInstanceName());
      }

      for (final Entry<PapiEvent, Double> singleParameter : modelParams) {
        final Element modelParameter = this.dom.createElement(ScenarioConstants.MODEL_PARAMETER);
        peType.appendChild(modelParameter);
        writePapifyEnergyKPIModelParameter(modelParameter, singleParameter);
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

    component.setAttribute(ScenarioConstants.COMPONENT_ID, papiComponent.getId());
    component.setAttribute(ScenarioConstants.COMPONENT_TYPE, papiComponent.getType().toString());
    component.setAttribute(ScenarioConstants.COMPONENT_INDEX, Integer.toString(papiComponent.getIndex()));

    for (final PapiEventSet eventSet : papiComponent.getEventSets()) {
      final Element singleEventSet = this.dom.createElement(ScenarioConstants.EVENT_SET);
      component.appendChild(singleEventSet);
      singleEventSet.setAttribute(ScenarioConstants.TYPE, eventSet.getType().toString());
      for (final PapiEvent event : eventSet.getEvents()) {
        final Element singleEvent = this.dom.createElement(ScenarioConstants.EVENT);
        singleEventSet.appendChild(singleEvent);
        writePapifyEvent(singleEvent, event);
      }
    }
  }

  /**
   * Adds the papify energy KPI model parameter.
   *
   * @param modelParameter
   *          the parent modelParameter
   * @param modelEnergyKPIModel
   *          the single parameter
   */
  private void writePapifyEnergyKPIModelParameter(final Element modelParameter,
      final Entry<PapiEvent, Double> modelEnergyKPIModel) {

    final Element papiEvent = this.dom.createElement(ScenarioConstants.PAPI_EVENT);
    writePapifyEvent(papiEvent, modelEnergyKPIModel.getKey());
    modelParameter.appendChild(papiEvent);
    modelParameter.setAttribute(ScenarioConstants.PARAM_VALUE, modelEnergyKPIModel.getValue().toString());
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

    final Element eventId = this.dom.createElement(ScenarioConstants.EVENT_ID);
    event.appendChild(eventId);
    eventId.setAttribute(ScenarioConstants.EVENT_ID, Integer.toString(papiEvent.getIndex()));
    final Element eventName = this.dom.createElement(ScenarioConstants.EVENT_NAME);
    event.appendChild(eventName);
    eventName.setAttribute(ScenarioConstants.EVENT_NAME, papiEvent.getName());
    final Element eventDescription = this.dom.createElement(ScenarioConstants.EVENT_DESCRIPTION);
    event.appendChild(eventDescription);
    eventDescription.setAttribute(ScenarioConstants.EVENT_DESCRIPTION, papiEvent.getDescription());

    for (final PapiEventModifier eventModifier : papiEvent.getModifiers()) {
      final Element singleEventModifier = this.dom.createElement(ScenarioConstants.EVENT_MODIFIER);
      event.appendChild(singleEventModifier);
      singleEventModifier.setAttribute(ScenarioConstants.NAME, eventModifier.getName());
      singleEventModifier.setAttribute(ScenarioConstants.DESCRIPTION, eventModifier.getDescription());
    }
  }

  /**
   * Adds the simu params.
   *
   * @param parent
   *          the parent
   */
  private void writeSimuParams(final Element parent) {

    final Element params = this.dom.createElement(ScenarioConstants.SIMU_PARAMS);
    parent.appendChild(params);

    final Element core = this.dom.createElement(ScenarioConstants.MAIN_CORE);
    params.appendChild(core);
    final ComponentInstance mainOperator = this.scenario.getSimulationInfo().getMainOperator();
    if (mainOperator != null) {
      core.setTextContent(mainOperator.getInstanceName());
    }

    final Element medium = this.dom.createElement(ScenarioConstants.MAIN_COM_NODE);
    params.appendChild(medium);
    final ComponentInstance mainComNode = this.scenario.getSimulationInfo().getMainComNode();
    if (mainComNode != null) {
      medium.setTextContent(mainComNode.getInstanceName());
    }

    final Element dataSize = this.dom.createElement(ScenarioConstants.AVERAGE_DATA_SIZE);
    params.appendChild(dataSize);
    dataSize.setTextContent(String.valueOf(this.scenario.getSimulationInfo().getAverageDataSize()));

    final Element dataTypes = this.dom.createElement(ScenarioConstants.DATA_TYPES);
    params.appendChild(dataTypes);

    final EMap<String, Long> types = this.scenario.getSimulationInfo().getDataTypes();
    for (final Entry<String, Long> dataType : types) {
      writeDataType(dataTypes, dataType.getKey(), dataType.getValue());
    }

    final Element sVOperators = this.dom.createElement(ScenarioConstants.SPECIAL_VERTEX_OPERATORS);
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

    final Element dataTypeElt = this.dom.createElement(ScenarioConstants.DATA_TYPE);
    parent.appendChild(dataTypeElt);
    dataTypeElt.setAttribute(ScenarioConstants.NAME, dataTypeName);
    dataTypeElt.setAttribute(ScenarioConstants.SIZE, Long.toString(dataTypeSize));
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

    final Element dataTypeElt = this.dom.createElement(ScenarioConstants.SPECIAL_VERTEX_OPERATOR);
    parent.appendChild(dataTypeElt);
    dataTypeElt.setAttribute(ScenarioConstants.PATH, opId.getInstanceName());
  }

  /**
   * Adds the files.
   *
   * @param parent
   *          the parent
   */
  private void writeFiles(final Element parent) {

    final Element files = this.dom.createElement(ScenarioConstants.FILES);
    parent.appendChild(files);

    final PiGraph algorithm = this.scenario.getAlgorithm();
    if (algorithm != null) {
      final Element algo = this.dom.createElement(ScenarioConstants.ALGORITHM);
      files.appendChild(algo);
      algo.setAttribute(ScenarioConstants.URL, algorithm.getUrl());
    }

    final Design design = this.scenario.getDesign();
    if (design != null) {
      final Element archi = this.dom.createElement(ScenarioConstants.ARCHITECTURE);
      files.appendChild(archi);
      archi.setAttribute(ScenarioConstants.URL, design.getUrl());
    }

    final Element codeGenDir = this.dom.createElement(ScenarioConstants.CODEGEN_DIRECTORY);
    files.appendChild(codeGenDir);
    String codeGenDirStr = this.scenario.getCodegenDirectory();
    if (codeGenDirStr == null) {
      codeGenDirStr = "";
    }
    codeGenDir.setAttribute(ScenarioConstants.URL, codeGenDirStr);
  }

  /**
   * Adds the constraints.
   *
   * @param parent
   *          the parent
   */
  private void writeConstraints(final Element parent) {

    final Element constraints = this.dom.createElement(ScenarioConstants.CONSTRAINTS);
    parent.appendChild(constraints);

    final String groupConstraintsFileURL = this.scenario.getConstraints().getGroupConstraintsFileURL();
    constraints.setAttribute(ScenarioConstants.EXCEL_URL, groupConstraintsFileURL);

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
   * Adds the constraint (only for regular actors).
   *
   * @param parent
   *          the parent
   * @param cst
   *          the cst
   */
  private void writeConstraints(final Element parent, final ComponentInstance cmpi, final List<AbstractActor> actors) {

    final Element constraintGroupElt = this.dom.createElement(ScenarioConstants.CONSTRAINT_GROUP);
    parent.appendChild(constraintGroupElt);

    final Element opdefelt = this.dom.createElement(ScenarioConstants.OPERATOR);
    constraintGroupElt.appendChild(opdefelt);
    final String instanceName = cmpi.getInstanceName();
    opdefelt.setAttribute(ScenarioConstants.NAME, instanceName);

    for (final AbstractActor actor : actors) {
      if (actor instanceof Actor || actor instanceof PiGraph) {
        final Element vtxelt = this.dom.createElement(ScenarioConstants.TASK);
        constraintGroupElt.appendChild(vtxelt);
        vtxelt.setAttribute(ScenarioConstants.NAME, actor.getVertexPath());
      }
    }
  }

  /**
   * Adds the timings.
   *
   * @param parent
   *          the parent
   */
  private void writeTimings(final Element parent) {

    final Element timingsElement = this.dom.createElement(ScenarioConstants.TIMINGS);
    parent.appendChild(timingsElement);

    final String excelFileURL = this.scenario.getTimings().getExcelFileURL();
    timingsElement.setAttribute(ScenarioConstants.EXCEL_URL, excelFileURL);

    final EMap<AbstractActor,
        EMap<Component, EMap<TimingType, String>>> actorsTimings = this.scenario.getTimings().getActorTimings();
    for (final Entry<AbstractActor, EMap<Component, EMap<TimingType, String>>> actorTimings : actorsTimings) {
      for (final Entry<Component, EMap<TimingType, String>> actorComponentTimings : actorTimings.getValue()) {
        for (final Entry<TimingType, String> timings : actorComponentTimings.getValue()) {
          writeTiming(timingsElement, actorTimings.getKey(), actorComponentTimings.getKey(), timings.getKey(),
              timings.getValue());
        }
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
      final TimingType timingType, final String timing) {
    final Element timingelt = this.dom.createElement(ScenarioConstants.TIMING);
    parent.appendChild(timingelt);
    timingelt.setAttribute(ScenarioConstants.VERTEX_NAME, actor.getVertexPath());
    timingelt.setAttribute(ScenarioConstants.OPNAME, component.getVlnv().getName());
    timingelt.setAttribute(ScenarioConstants.TIMING_TYPE, timingType.getName());
    timingelt.setAttribute(ScenarioConstants.TIME, timing);
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

    final Element timingelt = this.dom.createElement(ScenarioConstants.MEMCPY_SPEED);
    parent.appendChild(timingelt);
    timingelt.setAttribute(ScenarioConstants.OPNAME, opDef.getVlnv().getName());
    timingelt.setAttribute(ScenarioConstants.SETUP_TIME, Long.toString(memcpySetupTime));
    timingelt.setAttribute(ScenarioConstants.TIME_PER_UNIT, Double.toString(memcpyTimePerUnit));
  }

  /**
   * Adds the Energy configurations.
   *
   * @param parent
   *          the parent
   */
  private void writeEnergyConfigs(final Element parent) {
    final Element energyConfigs = this.dom.createElement(ScenarioConstants.ENERGY_CONFIGS);
    parent.appendChild(energyConfigs);

    energyConfigs.setAttribute(ScenarioConstants.XML_URL, this.scenario.getEnergyConfig().getExcelFileURL());

    final EnergyConfig manager = this.scenario.getEnergyConfig();

    writePerformanceObjective(energyConfigs, manager.getPerformanceObjective());
    for (final Entry<String, Double> opDef : manager.getPlatformPower()) {
      writePlatformPower(energyConfigs, opDef.getKey(), opDef.getValue());
    }

    final Element energyActors = this.dom.createElement(ScenarioConstants.PE_ACTORS_ENERGY);
    energyConfigs.appendChild(energyActors);
    for (final Entry<AbstractActor, EMap<Component, String>> peActorEnergy : manager.getAlgorithmEnergy()) {
      for (final Entry<Component, String> e : peActorEnergy.getValue()) {
        writePeActorEnergy(energyActors, peActorEnergy.getKey(), e.getKey(), e.getValue());
      }
    }

    for (final Entry<String, EMap<String, Double>> peCommsEnergy : manager.getCommsEnergy()) {
      writePeCommsEnergy(energyConfigs, peCommsEnergy.getKey(), peCommsEnergy.getValue());
    }
  }

  /**
   * Adds the PerformanceObjective.
   *
   * @param parent
   *          the parent
   * @param performanceObjective
   *          the performance objective
   */
  private void writePerformanceObjective(final Element parent, final PerformanceObjective performanceObjective) {

    final Element performanceObjectiveElt = this.dom.createElement(ScenarioConstants.PERF_OBJECTIVE);
    parent.appendChild(performanceObjectiveElt);

    performanceObjectiveElt.setAttribute(ScenarioConstants.OBJECTIVE_EPS,
        Double.toString(performanceObjective.getObjectiveEPS()));
  }

  /**
   * Adds the PlatformPower.
   *
   * @param parent
   *          the parent
   * @param opDefName
   *          the operator name
   * @param pePower
   *          the associated static power
   */
  private void writePlatformPower(final Element parent, final String opDefName, final double pePower) {

    final Element pePowerElt = this.dom.createElement(ScenarioConstants.PE_POWER);
    parent.appendChild(pePowerElt);

    pePowerElt.setAttribute(ScenarioConstants.OP_NAME, opDefName);
    pePowerElt.setAttribute(ScenarioConstants.PE_POWER, Double.toString(pePower));
  }

  /**
   * Adds the peActorEnergy.
   *
   * @param parent
   *          the parent
   * @param actor
   *          the actor
   * @param component
   *          the component executing the actors
   * @param pePower
   *          the energy associated to each of the actors
   */
  private void writePeActorEnergy(final Element parent, final AbstractActor actor, final Component component,
      final String pePower) {

    final Element peActorEnergy = this.dom.createElement(ScenarioConstants.PE_ACTOR_ENERGY);
    parent.appendChild(peActorEnergy);

    peActorEnergy.setAttribute(ScenarioConstants.VERTEX_NAME, actor.getVertexPath());
    peActorEnergy.setAttribute(ScenarioConstants.OPNAME, component.getVlnv().getName());
    peActorEnergy.setAttribute(ScenarioConstants.ENERGY, pePower);
  }

  /**
   * Adds the peCommsEnergy.
   *
   * @param parent
   *          the parent
   * @param sourceComm
   *          the source of the communication (PE type)
   * @param destinationNodes
   *          the list of PE types with a valid energy value
   */
  private void writePeCommsEnergy(final Element parent, final String sourceComm,
      final EMap<String, Double> destinationNodes) {

    final Element peTypeCommsEnergy = this.dom.createElement(ScenarioConstants.PE_TYPE_COMMS_ENERGY);
    parent.appendChild(peTypeCommsEnergy);

    peTypeCommsEnergy.setAttribute(ScenarioConstants.SOURCE_PE_TYPE, sourceComm);
    for (final Entry<String, Double> destinationEnergy : destinationNodes) {
      writeCommNodeEnergy(peTypeCommsEnergy, destinationEnergy.getKey(), destinationEnergy.getValue());
    }
  }

  /**
   * Adds the peActorEnergy.
   *
   * @param parent
   *          the parent
   * @param actor
   *          the actor
   * @param energyValue
   *          the energy value
   */
  private void writeCommNodeEnergy(final Element parent, final String destinationPeType, Double energyValue) {

    final Element destinationEnergy = this.dom.createElement(ScenarioConstants.DESTINATION_TYPE);
    parent.appendChild(destinationEnergy);

    destinationEnergy.setAttribute(ScenarioConstants.DESTINATION_PE_TYPE, destinationPeType);
    destinationEnergy.setAttribute(ScenarioConstants.ENERGY_VALUE, energyValue.toString());
  }
}
