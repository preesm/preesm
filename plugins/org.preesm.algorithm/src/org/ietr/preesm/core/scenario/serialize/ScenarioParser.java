/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2008 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2016)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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
package org.ietr.preesm.core.scenario.serialize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.dftools.algorithm.importer.GMLSDFImporter;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.MemCopySpeed;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.scenario.papi.PapiComponent;
import org.ietr.preesm.core.scenario.papi.PapiEvent;
import org.ietr.preesm.core.scenario.papi.PapiEventModifier;
import org.ietr.preesm.core.scenario.papi.PapiEventSet;
import org.ietr.preesm.core.scenario.papi.PapiEventSetType;
import org.ietr.preesm.core.scenario.papi.PapifyConfigActor;
import org.ietr.preesm.core.scenario.papi.PapifyConfigPE;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;
import org.ietr.preesm.experiment.model.pimm.util.ActorPath;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamPackage;
import org.preesm.model.slam.serialize.IPXACTResourceFactoryImpl;
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
  private PreesmScenario scenario = null;

  /** current algorithm. */
  private SDFGraph algoSDF = null;

  /** The algo pi. */
  private PiGraph algoPi = null;

  /**
   * Instantiates a new scenario parser.
   */
  public ScenarioParser() {

    this.scenario = new PreesmScenario();
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
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  public PreesmScenario parseXmlFile(final IFile file)
      throws InvalidModelException, FileNotFoundException, CoreException {
    // get the factory
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {
      // Using factory get an instance of document builder
      final DocumentBuilder db = dbf.newDocumentBuilder();

      // parse using builder to get DOM representation of the XML file
      this.dom = db.parse(file.getContents());
    } catch (final ParserConfigurationException | SAXException | IOException | CoreException e) {
      e.printStackTrace();
    }

    if (this.dom != null) {
      // get the root elememt
      final Element docElt = this.dom.getDocumentElement();

      Node node = docElt.getFirstChild();

      while (node != null) {

        if (node instanceof Element) {
          final Element elt = (Element) node;
          final String type = elt.getTagName();
          switch (type) {
            case "files":
              parseFileNames(elt);
              break;
            case "constraints":
              parseConstraintGroups(elt);
              break;
            case "relativeconstraints":
              parseRelativeConstraints(elt);
              break;
            case "timings":
              parseTimings(elt);
              break;
            case "simuParams":
              parseSimuParams(elt);
              break;
            case "variables":
              parseVariables(elt);
              break;
            case "parameterValues":
              parseParameterValues(elt);
              break;
            case "papifyConfigs":
              parsePapifyConfigs(elt);
              break;
            default:
          }
        }

        node = node.getNextSibling();
      }
    }

    this.scenario.setScenarioURL(file.getFullPath().toString());
    return this.scenario;
  }

  /**
   * Retrieves all the parameter values.
   *
   * @param paramValuesElt
   *          the param values elt
   */
  private void parseParameterValues(final Element paramValuesElt) throws InvalidModelException, CoreException {

    Node node = paramValuesElt.getFirstChild();

    final PiGraph graph = getPiGraph();
    if ((graph != null) && this.scenario.isPISDFScenario()) {
      final Set<Parameter> parameters = new LinkedHashSet<>();
      for (final Parameter p : graph.getAllParameters()) {
        if (!p.isConfigurationInterface()) {
          parameters.add(p);
        }
      }

      while (node != null) {
        if (node instanceof Element) {
          final Element elt = (Element) node;
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
        this.scenario.getParameterValueManager().addParameterValue(p);
      }
    }
  }

  private PiGraph getPiGraph() throws InvalidModelException, CoreException {
    final PiGraph piGraph;
    if (this.algoPi != null) {
      piGraph = this.algoPi;
    } else {
      if (this.scenario == null) {
        piGraph = null;
      } else {
        final String algorithmURL = this.scenario.getAlgorithmURL();
        piGraph = PiParser.getPiGraph(algorithmURL);
      }
    }
    return piGraph;
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

    Parameter currentParameter = null;

    final String type = paramValueElt.getAttribute("type");
    final String parent = paramValueElt.getAttribute("parent");
    final String name = paramValueElt.getAttribute("name");
    String stringValue = paramValueElt.getAttribute("value");

    currentParameter = graph.lookupParameterGivenGraph(name, parent);
    if (currentParameter == null) {
      PreesmLogger.getLogger().log(Level.WARNING,
          "Parameter with name '" + name + "' cannot be found in PiGraph '" + parent + "'.");
    } else {
      switch (type) {
        case "INDEPENDENT":
        case "STATIC": // Retro-compatibility
          this.scenario.getParameterValueManager().addIndependentParameterValue(currentParameter, stringValue, parent);
          break;
        case "ACTOR_DEPENDENT":
        case "DYNAMIC": // Retro-compatibility
          if ((stringValue.charAt(0) == '[') && (stringValue.charAt(stringValue.length() - 1) == ']')) {
            stringValue = stringValue.substring(1, stringValue.length() - 1);
            final String[] values = stringValue.split(",");

            final Set<Integer> newValues = new LinkedHashSet<>();

            try {
              for (final String val : values) {
                newValues.add(Integer.parseInt(val.trim()));
              }
            } catch (final NumberFormatException e) {
              // TODO: Do smthg?
            }
            this.scenario.getParameterValueManager().addActorDependentParameterValue(currentParameter, newValues,
                parent);
          }
          break;
        case "PARAMETER_DEPENDENT":
        case "DEPENDENT": // Retro-compatibility
          final Set<String> inputParameters = new LinkedHashSet<>();
          if (graph != null) {

            for (final Parameter input : currentParameter.getInputParameters()) {
              inputParameters.add(input.getName());
            }
          }
          this.scenario.getParameterValueManager().addParameterDependentParameterValue(currentParameter, stringValue,
              inputParameters, parent);
          break;
        default:
          throw new RuntimeException("Unknown Parameter type: " + type + " for Parameter: " + name);
      }
    }
    return currentParameter;
  }

  /**
   * Retrieves the timings.
   *
   * @param relConsElt
   *          the rel cons elt
   */
  private void parseRelativeConstraints(final Element relConsElt) {

    final String relConsFileUrl = relConsElt.getAttribute("excelUrl");
    this.scenario.getTimingManager().setExcelFileURL(relConsFileUrl);

    Node node = relConsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("relativeconstraint")) {
          parseRelativeConstraint(elt);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves one timing.
   *
   * @param timingElt
   *          the timing elt
   */
  private void parseRelativeConstraint(final Element timingElt) {

    int group = -1;

    if (this.algoSDF != null) {
      final String type = timingElt.getTagName();
      if (type.equals("relativeconstraint")) {
        final String vertexpath = timingElt.getAttribute("vertexname");

        try {
          group = Integer.parseInt(timingElt.getAttribute("group"));
        } catch (final NumberFormatException e) {
          group = -1;
        }

        this.scenario.getRelativeconstraintManager().addConstraint(vertexpath, group);
      }

    }
  }

  /**
   * Retrieves the timings.
   *
   * @param varsElt
   *          the vars elt
   */
  private void parseVariables(final Element varsElt) {

    final String excelFileUrl = varsElt.getAttribute("excelUrl");
    this.scenario.getVariablesManager().setExcelFileURL(excelFileUrl);

    Node node = varsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("variable")) {
          final String name = elt.getAttribute("name");
          final String value = elt.getAttribute("value");

          this.scenario.getVariablesManager().setVariable(name, value);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Parses the simulation parameters.
   *
   * @param filesElt
   *          the files elt
   */
  private void parseSimuParams(final Element filesElt) {

    Node node = filesElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        final String content = elt.getTextContent();
        switch (type) {
          case "mainCore":
            this.scenario.getSimulationManager().setMainOperatorName(content);
            break;
          case "mainComNode":
            this.scenario.getSimulationManager().setMainComNodeName(content);
            break;
          case "averageDataSize":
            this.scenario.getSimulationManager().setAverageDataSize(Long.valueOf(content));
            break;
          case "dataTypes":
            parseDataTypes(elt);
            break;
          case "specialVertexOperators":
            parseSpecialVertexOperators(elt);
            break;
          case "numberOfTopExecutions":
            this.scenario.getSimulationManager().setNumberOfTopExecutions(Integer.parseInt(content));
            break;
          default:
        }
      }

      node = node.getNextSibling();
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

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("dataType")) {
          final String name = elt.getAttribute("name");
          final String size = elt.getAttribute("size");

          if (!name.isEmpty() && !size.isEmpty()) {
            final DataType dataType = new DataType(name, Integer.parseInt(size));
            this.scenario.getSimulationManager().putDataType(dataType);
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

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("specialVertexOperator")) {
          final String path = elt.getAttribute("path");

          if (path != null) {
            this.scenario.getSimulationManager().addSpecialVertexOperatorId(path);
          }
        }
      }

      node = node.getNextSibling();
    }

    /*
     * It is not possible to remove all operators from special vertex executors: if no operator is selected, all of them
     * are!!
     */
    if (this.scenario.getSimulationManager().getSpecialVertexOperatorIds().isEmpty()
        && (this.scenario.getOperatorIds() != null)) {
      for (final String opId : this.scenario.getOperatorIds()) {
        this.scenario.getSimulationManager().addSpecialVertexOperatorId(opId);
      }
    }
  }

  /**
   * Parses the archi and algo files and retrieves the file contents.
   *
   * @param filesElt
   *          the files elt
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  private void parseFileNames(final Element filesElt) throws InvalidModelException, CoreException {

    Node node = filesElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        final String url = elt.getAttribute("url");
        if (url.length() > 0) {
          if (type.equals("algorithm")) {
            this.scenario.setAlgorithmURL(url);
            this.algoSDF = null;
            this.algoPi = null;
            try {
              if (url.endsWith(".graphml")) {
                this.algoSDF = ScenarioParser.getSDFGraph(url);
              } else if (url.endsWith(".pi")) {
                this.algoPi = getPiGraph();
              }
            } catch (final Exception e) {
              throw new ScenarioParserException("Could not parse the algorithm: " + e.getMessage(), e);
            }
          } else if (type.equals("architecture")) {
            try {
              this.scenario.setArchitectureURL(url);
              initializeArchitectureInformation(url);
            } catch (final Exception e) {
              throw new ScenarioParserException("Could not parse the architecture: " + e.getMessage(), e);
            }
          } else if (type.equals("codegenDirectory")) {
            this.scenario.getCodegenManager().setCodegenDirectory(url);
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
  private void initializeArchitectureInformation(final String url) {
    if (url.contains(".design")) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "SLAM architecture 1.0 is no more supported. Use .slam architecture files.");
    } else if (url.contains(".slam")) {

      final Map<String, Object> extToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
      Object instance = extToFactoryMap.get("slam");
      if (instance == null) {
        instance = new IPXACTResourceFactoryImpl();
        extToFactoryMap.put("slam", instance);
      }

      if (!EPackage.Registry.INSTANCE.containsKey(SlamPackage.eNS_URI)) {
        EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI, SlamPackage.eINSTANCE);
      }

      // Extract the root object from the resource.
      final Design design = ScenarioParser.parseSlamDesign(url);

      this.scenario.setOperatorIds(DesignTools.getOperatorInstanceIds(design));
      this.scenario.setComNodeIds(DesignTools.getComNodeInstanceIds(design));
      this.scenario.setOperatorDefinitionIds(DesignTools.getOperatorComponentIds(design));
    }
  }

  /**
   * Parses the slam design.
   *
   * @param url
   *          the url
   * @return the design
   */
  public static Design parseSlamDesign(final String url) {

    final Design slamDesign;
    final ResourceSet resourceSet = new ResourceSetImpl();

    final URI uri = URI.createPlatformResourceURI(url, true);
    if ((uri.fileExtension() == null) || !uri.fileExtension().contentEquals("slam")) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Expecting .slam file");
      return null;
    }
    final Resource ressource;
    try {
      ressource = resourceSet.getResource(uri, true);
      slamDesign = (Design) (ressource.getContents().get(0));
    } catch (final WrappedException e) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "The architecture file \"" + uri + "\" specified by the scenario does not exist any more.");
      return null;
    }

    return slamDesign;
  }

  /**
   * Gets the SDF graph.
   *
   * @param path
   *          the path
   * @return the SDF graph
   * @throws InvalidModelException
   *           the invalid model exception
   */
  public static SDFGraph getSDFGraph(final String path) throws InvalidModelException {
    SDFGraph algorithm = null;
    final GMLSDFImporter importer = new GMLSDFImporter();

    final Path relativePath = new Path(path);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);

    try {
      algorithm = importer.parse(new File(file.getLocation().toOSString()));

      ScenarioParser.addVertexPathProperties(algorithm, "");
    } catch (final InvalidModelException e) {
      e.printStackTrace();
    } catch (final FileNotFoundException e) {
      throw new ScenarioParserException("Could not locate " + path, e);
    }

    return algorithm;
  }

  /**
   * Adding an information that keeps the path of each vertex relative to the hierarchy.
   *
   * @param algorithm
   *          the algorithm
   * @param currentPath
   *          the current path
   */
  private static void addVertexPathProperties(final SDFGraph algorithm, final String currentPath) {

    for (final SDFAbstractVertex vertex : algorithm.vertexSet()) {
      String newPath = currentPath + vertex.getName();
      vertex.setInfo(newPath);
      newPath += "/";
      if (vertex.getGraphDescription() != null) {
        ScenarioParser.addVertexPathProperties((SDFGraph) vertex.getGraphDescription(), newPath);
      }
    }
  }

  /**
   * Retrieves all the constraint groups.
   *
   * @param cstGroupsElt
   *          the cst groups elt
   */
  private void parseConstraintGroups(final Element cstGroupsElt) {

    final String excelFileUrl = cstGroupsElt.getAttribute("excelUrl");
    this.scenario.getConstraintGroupManager().setExcelFileURL(excelFileUrl);

    Node node = cstGroupsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("constraintGroup")) {
          final ConstraintGroup cg = getConstraintGroup(elt);
          this.scenario.getConstraintGroupManager().addConstraintGroup(cg);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves a constraint group.
   *
   * @param cstGroupElt
   *          the cst group elt
   * @return the constraint group
   */
  private ConstraintGroup getConstraintGroup(final Element cstGroupElt) {

    final ConstraintGroup cg = new ConstraintGroup();

    if ((this.algoSDF != null) || (this.algoPi != null)) {
      Node node = cstGroupElt.getFirstChild();

      while (node != null) {
        if (node instanceof Element) {
          final Element elt = (Element) node;
          final String type = elt.getTagName();
          final String name = elt.getAttribute("name");
          if (type.equals(VertexType.TYPE_TASK)) {
            if (getActorFromPath(name) != null) {
              cg.addActorPath(name);
            }
          } else if (type.equals("operator") && (this.scenario.getOperatorIds() != null)) {
            if (this.scenario.getOperatorIds().contains(name)) {
              cg.addOperatorId(name);
            }
          }
        }
        node = node.getNextSibling();
      }
      return cg;
    }

    return cg;
  }

  /**
   * Retrieves all the papifyConfig groups.
   *
   * @param cstGroupsElt
   *          the cst groups elt
   */
  private void parsePapifyConfigs(final Element papifyConfigsElt) {

    final String xmlFileURL = papifyConfigsElt.getAttribute("xmlUrl");
    this.scenario.getPapifyConfigManager().setExcelFileURL(xmlFileURL);

    Node node = papifyConfigsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("papifyConfigActor")) {
          final PapifyConfigActor pg = getPapifyConfigActor(elt);
          this.scenario.getPapifyConfigManager().addPapifyConfigActorGroup(pg);
        } else if (type.equals("papifyConfigPE")) {
          final PapifyConfigPE pg = getPapifyConfigPE(elt);
          this.scenario.getPapifyConfigManager().addPapifyConfigPEGroup(pg);
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
  private PapifyConfigActor getPapifyConfigActor(final Element papifyConfigElt) {

    Node node = papifyConfigElt.getFirstChild();
    PapifyConfigActor pc = new PapifyConfigActor();

    while (node != null) {
      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        String actorPath = "";
        String actorId = "";
        if (type.equals("actorPath")) {
          actorPath = elt.getAttribute("actorPath");
          pc.addActorPath(actorPath);
        } else if (type.equals("actorId")) {
          actorId = elt.getAttribute("actorId");
          pc.addActorId(actorId);
          Node nodeEvents = node.getFirstChild();
          while (nodeEvents != null) {

            if (nodeEvents instanceof Element) {
              final Element eltEvents = (Element) nodeEvents;
              final String typeEvents = eltEvents.getTagName();
              if (typeEvents.equals("component")) {
                final String component = eltEvents.getAttribute("component");
                final Set<PapiEvent> eventSet = new LinkedHashSet<>();
                final List<PapiEvent> eventList = getPapifyEvents(eltEvents);
                eventSet.addAll(eventList);
                pc.addPAPIEventSet(component, eventSet);
              }
            }
            nodeEvents = nodeEvents.getNextSibling();
          }
        }

      }
      node = node.getNextSibling();
    }
    return pc;
  }

  /**
   * Retrieves a papifyConfigPE.
   *
   * @param papifyConfigElt
   *          the papifyConfig group elt
   * @return the papifyConfig
   */
  private PapifyConfigPE getPapifyConfigPE(final Element papifyConfigElt) {

    PapifyConfigPE pc = null;
    Node node = papifyConfigElt.getFirstChild();

    while (node != null) {
      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String peType = elt.getAttribute("peType");
        pc = new PapifyConfigPE(peType);
        final Set<PapiComponent> components = getPAPIComponents(elt);
        pc.addPAPIComponents(components);
      }
      node = node.getNextSibling();
    }
    return pc;
  }

  /**
   * Retrieves all the PAPI components.
   *
   * @param componentsElt
   *          the PAPI components elt
   */
  private Set<PapiComponent> getPAPIComponents(final Element componentsElt) {

    Set<PapiComponent> components = new LinkedHashSet<>();
    Node node = componentsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("PAPIComponent")) {
          final PapiComponent cg = getPapifyComponent(elt);
          components.add(cg);
        }
      }

      node = node.getNextSibling();
    }
    return components;
  }

  /**
   * Retrieves the component info.
   *
   * @param node
   *          the papi component elt
   * @return the papi component
   */
  private PapiComponent getPapifyComponent(final Element papifyComponentElt) {

    PapiComponent component = null;
    final String componentId = papifyComponentElt.getAttribute("componentId");
    final String componentIndex = papifyComponentElt.getAttribute("componentIndex");
    final String componentType = papifyComponentElt.getAttribute("componentType");

    component = new PapiComponent(componentId, componentIndex, componentType);

    final List<PapiEventSet> eventSetList = new ArrayList<>();

    Node node = papifyComponentElt.getFirstChild();
    while (node != null) {
      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getAttribute("type");
        final PapiEventSet eventSet = new PapiEventSet();

        final List<PapiEvent> eventList = getPapifyEvents(elt);
        eventSet.setType(Optional.ofNullable(type).map(PapiEventSetType::valueOf).orElse(null));
        eventSet.setEvents(eventList);
        eventSetList.add(eventSet);
      }
      node = node.getNextSibling();
    }
    component.setEventSets(eventSetList);
    return component;
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
      if (node instanceof Element) {
        final Element elt = (Element) node;
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

    final PapiEvent event = new PapiEvent();
    Node node = papifyEventElt.getFirstChild();
    final List<PapiEventModifier> eventModiferList = new ArrayList<>();
    while (node != null) {
      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("eventDescription")) {
          final String eventDescription = elt.getAttribute("eventDescription");
          event.setDesciption(eventDescription);
        } else if (type.equals("eventId")) {
          final String eventId = elt.getAttribute("eventId");
          event.setIndex(Integer.valueOf(eventId));
        } else if (type.equals("eventName")) {
          final String eventName = elt.getAttribute("eventName");
          event.setName(eventName);
        } else if (type.equals("eventModifier")) {
          final PapiEventModifier eventModifer = new PapiEventModifier();
          final String description = elt.getAttribute("description");
          eventModifer.setDescription(description);
          final String name = elt.getAttribute("name");
          eventModifer.setName(name);
          eventModiferList.add(eventModifer);
        }
      }
      node = node.getNextSibling();
    }
    event.setModifiers(eventModiferList);
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
    this.scenario.getTimingManager().setExcelFileURL(timingFileUrl);

    Node node = timingsElt.getFirstChild();

    while (node != null) {

      if (node instanceof Element) {
        final Element elt = (Element) node;
        final String type = elt.getTagName();
        if (type.equals("timing")) {
          final Timing timing = getTiming(elt);
          if (timing != null) {
            this.scenario.getTimingManager().addTiming(timing);
          }
        } else if (type.equals("memcpyspeed")) {
          retrieveMemcpySpeed(this.scenario.getTimingManager(), elt);
        }
      }

      node = node.getNextSibling();
    }
  }

  /**
   * Retrieves one timing.
   *
   * @param timingElt
   *          the timing elt
   * @return the timing
   */
  private Timing getTiming(final Element timingElt) {

    Timing timing = null;

    if ((this.algoSDF != null) || (this.algoPi != null)) {

      final String type = timingElt.getTagName();
      if (type.equals("timing")) {
        final String vertexpath = timingElt.getAttribute("vertexname");
        final String opdefname = timingElt.getAttribute("opname");
        long time;
        final String stringValue = timingElt.getAttribute("time");
        boolean isEvaluated = false;
        try {
          time = Long.parseLong(stringValue);
          isEvaluated = true;
        } catch (final NumberFormatException e) {
          time = -1;
        }

        String actorName;
        if (vertexpath.contains("/")) {
          actorName = getActorNameFromPath(vertexpath);
        } else {
          actorName = vertexpath;
        }

        if ((actorName != null) && this.scenario.getOperatorDefinitionIds().contains(opdefname)) {
          if (isEvaluated) {
            timing = new Timing(opdefname, actorName, time);
          } else {
            timing = new Timing(opdefname, actorName, stringValue);
          }
        }
      }
    }

    return timing;
  }

  /**
   * Returns an actor Object (either SDFAbstractVertex from SDFGraph or AbstractActor from PiGraph) from the path in its
   * container graph.
   *
   * @param path
   *          the path to the actor, where its segment is the name of an actor and separators are "/"
   * @return the wanted actor, if existing, null otherwise
   */
  private Object getActorFromPath(final String path) {
    Object result = null;
    if (this.algoSDF != null) {
      result = this.algoSDF.getHierarchicalVertexFromPath(path);
    } else if (this.algoPi != null) {
      result = ActorPath.lookup(this.algoPi, path);
    }
    return result;
  }

  /**
   * Returns the name of an actor (either SDFAbstractVertex from SDFGraph or AbstractActor from PiGraph) from the path
   * in its container graph.
   *
   * @param path
   *          the path to the actor, where its segment is the name of an actor and separators are "/"
   * @return the name of the wanted actor, if we found it
   */
  private String getActorNameFromPath(final String path) {
    final Object actor = getActorFromPath(path);
    if (actor != null) {
      if (actor instanceof SDFAbstractVertex) {
        return ((SDFAbstractVertex) actor).getName();
      } else if (actor instanceof AbstractActor) {
        return ((AbstractActor) actor).getName();
      }
    }
    return null;
  }

  /**
   * Retrieves one memcopy speed composed of integer setup time and timeperunit.
   *
   * @param timingManager
   *          the timing manager
   * @param timingElt
   *          the timing elt
   */
  private void retrieveMemcpySpeed(final TimingManager timingManager, final Element timingElt) {

    if ((this.algoSDF != null) || (this.algoPi != null)) {

      final String type = timingElt.getTagName();
      if (type.equals("memcpyspeed")) {
        final String opdefname = timingElt.getAttribute("opname");
        final String sSetupTime = timingElt.getAttribute("setuptime");
        final String sTimePerUnit = timingElt.getAttribute("timeperunit");
        int setupTime;
        float timePerUnit;

        try {
          setupTime = Integer.parseInt(sSetupTime);
          timePerUnit = Float.parseFloat(sTimePerUnit);
        } catch (final NumberFormatException e) {
          setupTime = -1;
          timePerUnit = -1;
        }

        if (this.scenario.getOperatorDefinitionIds().contains(opdefname) && (setupTime >= 0) && (timePerUnit >= 0)) {
          final MemCopySpeed speed = new MemCopySpeed(opdefname, setupTime, timePerUnit);
          timingManager.putMemcpySpeed(speed);
        }
      }

    }
  }
}
