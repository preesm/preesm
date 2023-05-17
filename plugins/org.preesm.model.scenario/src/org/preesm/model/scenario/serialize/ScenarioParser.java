/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
 * Jonathan Piat [jpiat@laas.fr] (2008 - 2011)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2016)
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.pisdf.util.VertexPath;
import org.preesm.model.scenario.MemoryCopySpeedValue;
import org.preesm.model.scenario.PapiComponent;
import org.preesm.model.scenario.PapiComponentType;
import org.preesm.model.scenario.PapiEvent;
import org.preesm.model.scenario.PapiEventModifier;
import org.preesm.model.scenario.PapiEventSet;
import org.preesm.model.scenario.PapiEventSetType;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioFactory;
import org.preesm.model.scenario.Timings;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.serialize.SlamParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * An xml parser retrieving scenario data.
 *
 * @author mpelcat
 */
public class ScenarioParser {

  /** xml tree. */
  private Document dom = null;

  /** scenario being retrieved. */
  private Scenario scenario = null;

  /**
   * Instantiates a new scenario parser.
   */
  public ScenarioParser() {
    this.scenario = ScenarioUserFactory.createScenario();
  }

  /**
   * Gets the dom.
   *
   * @return the dom
   */
  public Document getDom() {
    return this.dom;
  }

  /**
   * Retrieves the DOM document.
   *
   * @param file
   *          the file
   * @return the preesm scenario
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  public Scenario parseXmlFile(final IFile file) throws FileNotFoundException, CoreException {
    // get the factory
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
    dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant

    try {
      // Using factory get an instance of document builder
      final DocumentBuilder db = dbf.newDocumentBuilder();

      // parse using builder to get DOM representation of the XML file
      this.dom = db.parse(file.getContents());
    } catch (final ParserConfigurationException | SAXException | IOException | CoreException e) {

      PreesmLogger.getLogger().log(Level.WARNING, "Could not parse file: " + e.getMessage(), e);
      return null;
    }
    this.scenario.setScenarioURL(file.getFullPath().toString());

    if (this.dom != null) {
      // get the root elememt
      final Element docElt = this.dom.getDocumentElement();

      Node node = docElt.getFirstChild();

      while (node != null) {

        if (node instanceof final Element elt) {
          final String type = elt.getTagName();
          switch (type) {
            case "files":
              parseFileNames(elt);
              break;
            case "constraints":
              parseConstraintGroups(elt);
              break;
            case "timings":
              parseTimings(elt);
              break;
            case "simuParams":
              parseSimuParams(elt);
              break;
            case "parameterValues":
              parseParameterValues(elt);
              break;
            case "papifyConfigs":
              parsePapifyConfigs(elt);
              break;
            case "energyConfigs":
              parseEnergyConfigs(elt);
              break;
            case "variables":
              // deprecated
              break;
            case "relativeconstraints":
              // deprecated
              break;
            case "flags":
              parseFlags(elt);
              break;
            default:
          }
        }

        node = node.getNextSibling();
      }
    }

    if (!this.scenario.getSizesAreInBit()) {
      PreesmLogger.getLogger()
          .severe(() -> "The Scenario was created with an older version of PREESM."
              + " Change the datatype sizes from bytes to bits in the Simulation tab and save the Scenario."
              + " Check the \"Data alignment\" in Workflow tasks.");
    }

    return this.scenario;
  }

  /**
   * Retrieves all the parameter values.
   *
   * @param paramValuesElt
   *          the param values elt
   */
  private void parseParameterValues(final Element paramValuesElt) {

    Node node = paramValuesElt.getFirstChild();

    final PiGraph graph = scenario.getAlgorithm();
    if (graph != null) {
      final Set<Parameter> parameters = new LinkedHashSet<>();
      for (final Parameter p : graph.getAllParameters()) {
        if (!p.isConfigurationInterface()) {
          parameters.add(p);
        }
      }

      while (node != null) {
        if (node instanceof final Element elt) {
          final String type = elt.getTagName();
          if (type.equals("parameter")) {
            final Parameter justParsed = parseParameterValue(elt, graph);
            parameters.remove(justParsed);
          }
        }

        node = node.getNextSibling();
      }

      // Create a parameter value foreach parameter not yet in the
      // scenario
      for (final Parameter p : parameters) {
        this.scenario.getParameterValues().put(p, p.getExpression().getExpressionAsString());
      }
    }
  }

  /**
   * Retrieve a ParameterValue.
   *
   * @param paramValueElt
   *          the param value elt
   * @param graph
   *          the graph
   * @return the parameter
   */
  private Parameter parseParameterValue(final Element paramValueElt, final PiGraph graph) {
    if (graph == null) {
      throw new IllegalArgumentException();
    }

    Parameter currentParameter;

    final String parent = paramValueElt.getAttribute("parent");
    final String name = paramValueElt.getAttribute("name");
    final String stringValue = paramValueElt.getAttribute("value");

    currentParameter = graph.lookupParameterGivenGraph(name, parent);
    if (currentParameter == null) {
      PreesmLogger.getLogger().log(Level.WARNING,
          () -> "Parameter with name '" + name + "' cannot be found in PiGraph '" + parent + "'.");
    } else {
      this.scenario.getParameterValues().put(currentParameter, stringValue);
    }
    return currentParameter;
  }

  /**
   * Parses the simulation parameters.
   *
   * @param filesElt
   *          the files elt
   */
  private void parseSimuParams(final Element filesElt) {

    if (this.scenario.isProperlySet()) {

      Node node = filesElt.getFirstChild();

      while (node != null) {

        if (node instanceof final Element elt) {
          final String type = elt.getTagName();
          final String content = elt.getTextContent();
          switch (type) {
            case "mainCore":
              final ComponentInstance mainCore = scenario.getDesign().getComponentInstance(content);
              this.scenario.getSimulationInfo().setMainOperator(mainCore);
              break;
            case "mainComNode":
              final ComponentInstance mainComNode = scenario.getDesign().getComponentInstance(content);
              this.scenario.getSimulationInfo().setMainComNode(mainComNode);
              break;
            case "averageDataSize":
              this.scenario.getSimulationInfo().setAverageDataSize(Long.valueOf(content));
              break;
            case "dataTypes":
              parseDataTypes(elt);
              break;
            case "specialVertexOperators":
              parseSpecialVertexOperators(elt);
              break;
            default:
          }
        }

        node = node.getNextSibling();
      }
    }
  }

  /**
   * Retrieves the data types.
   *
   * @param dataTypeElt
   *          the data type elt
   */
  private void parseDataTypes(final Element dataTypeElt) {

    Node node = dataTypeElt.getFirstChild();

    while (node != null) {

      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("dataType")) {
          final String name = elt.getAttribute("name");
          final String size = elt.getAttribute("size");

          if (!name.isEmpty() && !size.isEmpty()) {
            this.scenario.getSimulationInfo().getDataTypes().put(name, Long.parseLong(size));
          }
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves the operators able to execute fork/join/broadcast.
   *
   * @param spvElt
   *          the spv elt
   */
  private void parseSpecialVertexOperators(final Element spvElt) {

    Node node = spvElt.getFirstChild();

    final Design design = this.scenario.getDesign();
    while (node != null) {

      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("specialVertexOperator")) {
          final String path = elt.getAttribute("path");

          if (path != null) {
            final ComponentInstance componentInstance = design.getComponentInstance(path);
            if (componentInstance != null) {
              this.scenario.getSimulationInfo().addSpecialVertexOperator(componentInstance);
            } else {
              PreesmLogger.getLogger().log(Level.WARNING,
                  () -> "Could not add special vertex operator '" + path + "' as it is not part of the design");
            }
          }
        }
      }

      node = node.getNextSibling();
    }

    /*
     * It is not possible to remove all operators from special vertex executors: if no operator is selected, all of them
     * are!!
     */
    if (this.scenario.getSimulationInfo().getSpecialVertexOperators().isEmpty()) {
      for (final ComponentInstance opId : design.getOperatorComponentInstances()) {
        this.scenario.getSimulationInfo().addSpecialVertexOperator(opId);
      }
    }
  }

  /**
   * Parses the archi and algo files and retrieves the file contents.
   *
   * @param filesElt
   *          the files elt
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  private void parseFileNames(final Element filesElt) {

    Node node = filesElt.getFirstChild();

    while (node != null) {

      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        final String url = elt.getAttribute("url");

        final URI uri = URI.createPlatformResourceURI(url, true);

        // This gives "True" if url is empty
        final ResourceSet resourceSet = new ResourceSetImpl();
        final Boolean exists = resourceSet.getURIConverter().exists(uri, null);

        if (!url.isEmpty()) {

          if (Boolean.TRUE.equals(exists)) {
            switch (type) {
              case "algorithm":
                this.scenario.setAlgorithm(PiParser.getPiGraphWithReconnection(url));
                break;
              case "architecture":
                initializeArchitectureInformation(url);
                break;
              case "codegenDirectory":
                this.scenario.setCodegenDirectory(url);
                break;
              default:
                // Do nothing
            }
          } else {
            PreesmLogger.getLogger().log(Level.WARNING, "Could not find file: {}", url);
          }
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Depending on the architecture model, parses the model and populates the scenario.
   *
   * @param url
   *          the url
   */
  private Design initializeArchitectureInformation(final String url) {
    final Design design = SlamParser.parseSlamDesign(url);
    this.scenario.setDesign(design);
    return design;
  }

  /**
   * Retrieves all the constraint groups.
   *
   * @param cstGroupsElt
   *          the cst groups elt
   */
  private void parseConstraintGroups(final Element cstGroupsElt) {

    final String excelFileUrl = cstGroupsElt.getAttribute("excelUrl");
    this.scenario.getConstraints().setGroupConstraintsFileURL(excelFileUrl);

    Node node = cstGroupsElt.getFirstChild();

    while (node != null) {

      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("constraintGroup")) {
          parseConstraintGroup(elt);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves a constraint group (only for regular actors).
   *
   * @param cstGroupElt
   *          the cst group elt
   * @return the constraint group
   */
  private void parseConstraintGroup(final Element cstGroupElt) {
    ComponentInstance opId = null;

    final Set<AbstractActor> actors = new LinkedHashSet<>();

    if (scenario.isProperlySet()) {
      Node node = cstGroupElt.getFirstChild();
      while (node != null) {
        if (node instanceof final Element elt) {
          final String type = elt.getTagName();
          final String name = elt.getAttribute("name");
          if (type.equals("task")) {
            final AbstractActor actorFromPath = getActorFromPath(name);
            if (actorFromPath instanceof Actor || actorFromPath instanceof PiGraph) {
              actors.add(actorFromPath);
            }
          } else if (type.equals("operator") && this.scenario.getDesign().containsComponentInstance(name)) {
            opId = this.scenario.getDesign().getComponentInstance(name);
          }
        }
        node = node.getNextSibling();
      }
    }
    if (opId != null) {
      this.scenario.getConstraints().addConstraints(opId, ECollections.asEList(new ArrayList<>(actors)));
    }
  }

  /**
   * Retrieves all the papifyConfig groups.
   *
   * @param cstGroupsElt
   *          the cst groups elt
   */
  private void parsePapifyConfigs(final Element papifyConfigsElt) {

    final String xmlFileURL = papifyConfigsElt.getAttribute("xmlUrl");
    this.scenario.getPapifyConfig().setXmlFileURL(xmlFileURL);

    Node node = papifyConfigsElt.getFirstChild();

    while (node != null) {

      if (node instanceof final Element elt) {
        final String type = elt.getTagName();

        switch (type) {
          case "papifyConfigActor":
            parsePapifyConfigActor(elt);
            break;
          case "papifyConfigPE":
            parsePapifyConfigPE(elt);
            break;
          case "energyModelPEType":
            parsePapifyEnergyKPIModel(elt);
            break;
          default:
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves a papifyConfigActor.
   *
   * @param papifyConfigElt
   *          the papifyConfig group elt
   * @return the papifyConfig
   */
  private void parsePapifyConfigActor(final Element papifyConfigElt) {

    Node node = papifyConfigElt.getFirstChild();

    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        String actorPath = "";
        if (type.equals("actorPath")) {
          actorPath = elt.getAttribute("actorPath");

          final AbstractActor lookup = VertexPath.lookup(this.scenario.getAlgorithm(), actorPath);

          Node nodeEvents = node.getFirstChild();
          while (nodeEvents != null) {

            if (nodeEvents instanceof final Element eltEvents) {
              final String typeEvents = eltEvents.getTagName();
              if (typeEvents.equals("component")) {
                final String component = eltEvents.getAttribute("component");
                final List<PapiEvent> eventList = getPapifyEvents(eltEvents);
                for (final PapiEvent event : eventList) {
                  this.scenario.getPapifyConfig().addActorConfigEvent(lookup, component, event);
                }
              }
            }
            nodeEvents = nodeEvents.getNextSibling();
          }
        }

      }
      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves a papifyConfigPE.
   *
   * @param papifyConfigElt
   *          the papifyConfig group elt
   * @return the papifyConfig
   */
  private void parsePapifyConfigPE(final Element papifyConfigElt) {

    Node node = papifyConfigElt.getFirstChild();

    Component slamComponent = null;

    while (node != null) {
      if (node instanceof final Element elt) {
        final String peType = elt.getAttribute("peType");
        slamComponent = this.scenario.getDesign().getComponent(peType);
        parsePAPIComponents(elt, slamComponent);
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves a papify energy KPI model.
   *
   * @param papifyModelElt
   *          the papifyModelElt elt
   * @return the papifyConfig
   */
  private void parsePapifyEnergyKPIModel(final Element papifyModelElt) {

    Node node = papifyModelElt.getFirstChild();

    Component slamComponent = null;

    while (node != null) {
      if (node instanceof final Element elt) {
        final String peType = elt.getAttribute("peType");
        slamComponent = this.scenario.getDesign().getComponent(peType);
        parsePapifyEnergyKPIModelParameter(elt, slamComponent);
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves all the PAPI components.
   *
   * @param componentsElt
   *          the PAPI components elt
   */
  private void parsePAPIComponents(final Element componentsElt, Component slamComponent) {

    Node node = componentsElt.getFirstChild();

    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("PAPIComponent")) {
          parsePapifyComponent(elt, slamComponent);
        }
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves all the parameters of the model.
   *
   * @param modelElt
   *          the model parameters elt
   */
  private void parsePapifyEnergyKPIModelParameter(final Element modelElt, Component slamComponent) {

    Node node = modelElt.getFirstChild();

    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("modelParameter")) {
          parsePapifyModelParameter(elt, slamComponent);
        }
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves the component info.
   *
   * @param node
   *          the papi component elt
   * @return the papi component
   */
  private void parsePapifyComponent(final Element papifyComponentElt, Component slamComponent) {

    final String componentId = papifyComponentElt.getAttribute("componentId");
    final String componentIndex = papifyComponentElt.getAttribute("componentIndex");
    final String componentType = papifyComponentElt.getAttribute("componentType");

    final PapiComponent component = ScenarioFactory.eINSTANCE.createPapiComponent();
    component.setIndex(Integer.valueOf(componentIndex));
    component.setId(componentId);
    component.setType(PapiComponentType.valueOf(componentType.toUpperCase()));

    final List<PapiEventSet> eventSetList = new ArrayList<>();

    Node node = papifyComponentElt.getFirstChild();
    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getAttribute("type");
        final PapiEventSet eventSet = ScenarioFactory.eINSTANCE.createPapiEventSet();

        final List<PapiEvent> eventList = getPapifyEvents(elt);
        eventSet.setType(Optional.ofNullable(type).map(PapiEventSetType::valueOf).orElse(null));
        eventSet.getEvents().addAll(eventList);
        eventSetList.add(eventSet);
      }
      node = node.getNextSibling();
    }
    component.getEventSets().addAll(eventSetList);

    this.scenario.getPapifyConfig().addComponent(slamComponent, component);

  }

  /**
   * Retrieves the model info.
   *
   * @param node
   *          the papi component elt
   * @return the papi component
   */
  private void parsePapifyModelParameter(final Element papifyParameterElt, Component slamComponent) {

    Node papiEventNode = papifyParameterElt.getFirstChild();
    while (papiEventNode != null) {
      if (papiEventNode instanceof final Element elt) {
        final PapiEvent papiEvent = getPapifyEvent(elt);
        final String paramValueRead = papifyParameterElt.getAttribute("paramValue");
        final double paramValue = Double.parseDouble(paramValueRead);
        this.scenario.getPapifyConfig().addEnergyParam(slamComponent, papiEvent, paramValue);
      }
      papiEventNode = papiEventNode.getNextSibling();
    }
  }

  /**
   * Retrieves the events info.
   *
   * @param node
   *          the event set elt
   * @return the papi list of events
   */
  private List<PapiEvent> getPapifyEvents(final Element papifyEventsElt) {

    final List<PapiEvent> eventList = new ArrayList<>();
    Node node = papifyEventsElt.getFirstChild();
    while (node != null) {
      if (node instanceof final Element elt) {
        final PapiEvent event = getPapifyEvent(elt);
        eventList.add(event);
      }
      node = node.getNextSibling();
    }
    return eventList;
  }

  /**
   * Retrieves the event info.
   *
   * @param node
   *          the event elt
   * @return the papi event
   */
  private PapiEvent getPapifyEvent(final Element papifyEventElt) {

    final PapiEvent event = ScenarioFactory.eINSTANCE.createPapiEvent();
    Node node = papifyEventElt.getFirstChild();
    final List<PapiEventModifier> eventModiferList = new ArrayList<>();
    while (node != null) {
      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("eventDescription")) {
          final String eventDescription = elt.getAttribute("eventDescription");
          event.setDescription(eventDescription);
        } else if (type.equals("eventId")) {
          final String eventId = elt.getAttribute("eventId");
          event.setIndex(Integer.valueOf(eventId));
        } else if (type.equals("eventName")) {
          final String eventName = elt.getAttribute("eventName");
          event.setName(eventName);
        } else if (type.equals("eventModifier")) {
          final PapiEventModifier eventModifer = ScenarioFactory.eINSTANCE.createPapiEventModifier();
          final String description = elt.getAttribute("description");
          eventModifer.setDescription(description);
          final String name = elt.getAttribute("name");
          eventModifer.setName(name);
          eventModiferList.add(eventModifer);
        }
      }
      node = node.getNextSibling();
    }
    event.getModifiers().addAll(eventModiferList);
    return event;
  }

  /**
   * Retrieves the timings.
   *
   * @param timingsElt
   *          the timings elt
   */
  private void parseTimings(final Element timingsElt) {

    final String timingFileUrl = timingsElt.getAttribute("excelUrl");
    this.scenario.getTimings().setExcelFileURL(timingFileUrl);

    Node node = timingsElt.getFirstChild();
    if (scenario.isProperlySet()) {
      while (node != null) {

        if (node instanceof final Element elt) {
          final String type = elt.getTagName();
          if (type.equals("timing")) {
            parseTiming(elt);
          } else if (type.equals("memcpyspeed")) {
            retrieveMemcpySpeed(this.scenario.getTimings(), elt);
          }
        }

        node = node.getNextSibling();
      }
    }
  }

  /**
   * Retrieves one timing.
   *
   * @param timingElt
   *          the timing elt
   * @return the timing
   */
  private void parseTiming(final Element timingElt) {
    if (scenario.getAlgorithm() != null) {
      final String type = timingElt.getTagName();
      if (type.equals("timing")) {
        final String vertexpath = timingElt.getAttribute("vertexname");
        final String opdefname = timingElt.getAttribute("opname");
        final String timingTypeName = timingElt.getAttribute("timingtype");
        final String stringValue = timingElt.getAttribute("time");

        final Design design = this.scenario.getDesign();
        final boolean contains = design.containsComponent(opdefname);
        final AbstractActor lookup = VertexPath.lookup(this.scenario.getAlgorithm(), vertexpath);
        TimingType timingType = TimingType.getByName(timingTypeName);
        // Mutation to load Scenario not specialized per TimingType
        if (timingType == null) {
          timingType = TimingType.EXECUTION_TIME;
        }
        if ((lookup != null) && contains) {
          final Component component = design.getComponent(opdefname);
          this.scenario.getTimings().setTiming(lookup, component, timingType, stringValue);
        }
      }
    }
  }

  /**
   * Returns an actor Object (either SDFAbstractVertex from SDFGraph or AbstractActor from PiGraph) from the path in its
   * container graph.
   *
   * @param path
   *          the path to the actor, where its segment is the name of an actor and separators are "/"
   * @return the wanted actor, if existing, null otherwise
   */
  private AbstractActor getActorFromPath(final String path) {
    AbstractActor result = null;
    if (scenario.getAlgorithm() != null) {
      result = VertexPath.lookup(scenario.getAlgorithm(), path);
    }
    return result;
  }

  /**
   * Retrieves one memcopy speed composed of integer setup time and timeperunit.
   *
   * @param timingManager
   *          the timing manager
   * @param timingElt
   *          the timing elt
   */
  private void retrieveMemcpySpeed(final Timings timingManager, final Element timingElt) {

    if (scenario.isProperlySet()) {
      final String type = timingElt.getTagName();
      if (type.equals("memcpyspeed")) {
        final String opdefname = timingElt.getAttribute("opname");
        final String sSetupTime = timingElt.getAttribute("setuptime");
        final String sTimePerUnit = timingElt.getAttribute("timeperunit");
        long setupTime;
        double timePerUnit;

        try {
          setupTime = Long.parseLong(sSetupTime);
          timePerUnit = Double.parseDouble(sTimePerUnit);
        } catch (final NumberFormatException e) {
          setupTime = -1;
          timePerUnit = -1;
        }

        final boolean contains = this.scenario.getDesign().containsComponent(opdefname);
        if (contains && (setupTime >= 0) && (timePerUnit >= 0)) {
          final Component component = this.scenario.getDesign().getComponent(opdefname);
          final MemoryCopySpeedValue createMemoryCopySpeedValue = ScenarioFactory.eINSTANCE
              .createMemoryCopySpeedValue();
          createMemoryCopySpeedValue.setSetupTime(setupTime);
          createMemoryCopySpeedValue.setTimePerUnit(timePerUnit);
          timingManager.getMemTimings().put(component, createMemoryCopySpeedValue);
        }
      }

    }
  }

  /**
   * Retrieves all the energyConfigs groups.
   *
   * @param cstGroupsElt
   *          the cst groups elt
   */
  private void parseEnergyConfigs(final Element energyConfigsElt) {

    final String xmlFileURL = energyConfigsElt.getAttribute("xmlUrl");
    this.scenario.getEnergyConfig().setExcelFileURL(xmlFileURL);

    Node node = energyConfigsElt.getFirstChild();

    while (node != null) {

      if (node instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("performanceObjective")) {
          parsePerformanceObjective(elt);
        } else if (type.equals("pePower")) {
          parsePlatformPower(elt);
        } else if (type.equals("peActorsEnergy")) {
          parsePeActorEnergy(elt);
        } else if (type.equals("peTypeCommsEnergy")) {
          parsePeCommsEnergy(elt);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves a performanceObjective.
   *
   * @param performanceObjectiveElt
   *          the performanceObjective group elt
   * @return the performanceObjective
   */
  private void parsePerformanceObjective(final Element performanceObjectiveElt) {

    final Node node = performanceObjectiveElt.getAttributeNode("objectiveEPS");
    if (node != null) {
      final String objectiveEPS = node.getNodeValue();
      final double objectiveEPSValue = Double.parseDouble(objectiveEPS);
      this.scenario.getEnergyConfig().getPerformanceObjective().setObjectiveEPS(objectiveEPSValue);
    }
  }

  /**
   * Retrieves a PlatformPower.
   *
   * @param PlatformPower
   *          the PlatformPower group elt
   * @return the PlatformPower
   */
  private void parsePlatformPower(final Element platformPowerElt) {

    final Node nodeOpName = platformPowerElt.getAttributeNode("opName");
    final Node nodePePower = platformPowerElt.getAttributeNode("pePower");
    if (nodeOpName != null && nodePePower != null) {
      final String opName = nodeOpName.getNodeValue();
      final String pePower = nodePePower.getNodeValue();
      final double pePowerValue = Double.parseDouble(pePower);
      this.scenario.getEnergyConfig().getPlatformPower().put(opName, pePowerValue);
    }
  }

  /**
   * Retrieves a peActorEnergy.
   *
   * @param peActorEnergy
   *          the peActorEnergy group elt
   * @return the peActorEnergy
   */
  private void parsePeActorEnergy(final Element peActorEnergy) {

    Node nodeChild = peActorEnergy.getFirstChild();

    while (nodeChild != null) {
      if (nodeChild instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("peActorEnergy")) {
          parseActorEnergy(elt);
        }
      }
      nodeChild = nodeChild.getNextSibling();
    }
  }

  /**
   * Retrieves a actorEnergy.
   *
   * @param actorEnergyElt
   *          the actorEnergy group elt
   * @return the actorEnergy
   */
  private void parseActorEnergy(final Element actorEnergyElt) {

    final Node nodeEnergyValue = actorEnergyElt.getAttributeNode("energy");
    final Node nodeOpName = actorEnergyElt.getAttributeNode("opname");
    final Node nodeVertexPath = actorEnergyElt.getAttributeNode("vertexname");

    if (nodeEnergyValue != null && nodeOpName != null && nodeVertexPath != null) {
      final String energyValue = nodeEnergyValue.getNodeValue();
      final String opName = nodeOpName.getNodeValue();
      final String vertexPath = nodeVertexPath.getNodeValue();
      final AbstractActor actor = getActorFromPath(vertexPath);
      final Component component = this.scenario.getDesign().getComponent(opName);
      this.scenario.getEnergyConfig().setActorPeEnergy(actor, component, energyValue);
    }
  }

  /**
   * Retrieves a peCommsEnergy.
   *
   * @param peCommsEnergy
   *          the peCommsEnergy group elt
   * @return the peCommsEnergy
   */
  private void parsePeCommsEnergy(final Element peCommsEnergy) {

    final Node nodeSourcePeType = peCommsEnergy.getAttributeNode("sourcePeType");
    final String sourcePeType = nodeSourcePeType.getNodeValue();
    Node nodeChild = peCommsEnergy.getFirstChild();

    while (nodeChild != null) {
      if (nodeChild instanceof final Element elt) {
        final String type = elt.getTagName();
        if (type.equals("destinationType")) {
          parseCommNodeEnergy(elt, sourcePeType);
        }
      }
      nodeChild = nodeChild.getNextSibling();
    }
  }

  /**
   * Retrieves a commNodeEnergy.
   *
   * @param commNodeEnergy
   *          the commNodeEnergy group elt
   * @param sourcePeType
   *          the sourcePeType
   * @return the commNodeEnergy
   */
  private void parseCommNodeEnergy(final Element commNodeEnergyElt, final String sourcePeType) {

    final Node nodeEnergyValue = commNodeEnergyElt.getAttributeNode("energyValue");
    final Node nodeDestinationPeType = commNodeEnergyElt.getAttributeNode("destinationPeType");
    if (nodeEnergyValue != null && nodeDestinationPeType != null) {
      final String energyValue = nodeEnergyValue.getNodeValue();
      final String destinationPeType = nodeDestinationPeType.getNodeValue();
      final double commEnergyValue = Double.parseDouble(energyValue);
      this.scenario.getEnergyConfig().setCommEnergy(sourcePeType, destinationPeType, commEnergyValue);
    }
  }

  /**
   * Retrieves flags from scenario.
   *
   * @param flagsElt
   *          the param values elt
   */
  private void parseFlags(final Element flagsElt) {

    Node node = flagsElt.getFirstChild();

    while (node != null) {

      if (node instanceof final Element elt) {
        final String type = elt.getTagName();

        switch (type) {
          case "sizesAreInBit":
            this.scenario.setSizesAreInBit(true);
            break;
          default:
        }
      }

      node = node.getNextSibling();
    }
  }
}
