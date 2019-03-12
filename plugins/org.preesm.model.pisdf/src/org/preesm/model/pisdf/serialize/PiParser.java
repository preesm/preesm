/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2014)
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
package org.preesm.model.pisdf.serialize;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.preesm.commons.DomUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionParameter;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.InterfaceKind;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.PortKind;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.model.pisdf.RefinementContainer;
import org.preesm.model.pisdf.check.NameCheckerC;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.util.PiIdentifiers;
import org.preesm.model.pisdf.util.PiSDFXSDValidator;
import org.preesm.model.pisdf.util.SubgraphReconnector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parser for the PiMM Model in the Pi format.
 *
 * @author kdesnos
 * @author jheulot
 */
public class PiParser {

  /**
   * Gets the pi graph.
   *
   * @param algorithmURL
   *          URL of the Algorithm.
   * @return the {@link PiGraph} algorithm.
   * @throws CoreException
   *           the core exception
   */
  public static PiGraph getPiGraph(final String algorithmURL) {
    PiGraph pigraph = null;
    final ResourceSet resourceSet = new ResourceSetImpl();

    final URI uri = URI.createPlatformResourceURI(algorithmURL, true);
    if ((uri.fileExtension() == null) || !uri.fileExtension().contentEquals("pi")) {
      return null;
    }
    final Resource ressource;
    try {
      ressource = resourceSet.getResource(uri, true);
      pigraph = (PiGraph) (ressource.getContents().get(0));

    } catch (final WrappedException e) {
      final String message = "The algorithm file \"" + uri + "\" specified by the scenario does not exist any more.";
      PreesmLogger.getLogger().log(Level.WARNING, message);
    }

    return pigraph;
  }

  /**
   *
   */
  public static PiGraph getPiGraphWithReconnection(final String algorithmURL) {
    final PiGraph graph = getPiGraph(algorithmURL);
    if (graph != null) {
      final SubgraphReconnector connector = new SubgraphReconnector();
      connector.connectSubgraphs(graph);
      PiGraphConsistenceChecker.check(graph, false);
    }
    return graph;
  }

  /**
   * Retrieve the value of a property of the given {@link Element}. A property is a data element child of the given
   * element.<br>
   * <br>
   *
   * <p>
   * This method will iterate over the properties of the element so it might not be a good idea to use it in a method
   * that would successively retrieve all properties of the element.
   * </p>
   *
   * @author Jonathan Piat
   * @param elt
   *          The element containing the property
   * @param propertyName
   *          The name of the property
   * @return The property value or null if the property was not found
   */
  protected static String getProperty(final Element elt, final String propertyName) {
    final NodeList childList = elt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("data")
          && ((Element) childList.item(i)).getAttribute("key").equals(propertyName)) {
        return childList.item(i).getTextContent();
      }
    }
    return null;
  }

  /** The URI of the parsed file. */
  private final URI documentURI;

  /**
   * Instantiates a new pi parser.
   *
   * @param uri
   *          the uri
   */
  public PiParser(final URI uri) {
    this.documentURI = uri;
  }

  /**
   * Parse the PiMM {@link PiGraph} from the given {@link InputStream} using the Pi format.
   *
   * @param inputStream
   *          The Parsed input stream
   * @return The parsed Graph or null is something went wrong
   */
  public PiGraph parse(final InputStream inputStream) {
    // Instantiate the graph that will be filled with parser informations
    final PiGraph graph = PiMMUserFactory.instance.createPiGraph();

    // wrap the input stream in one that supports mark/reset
    final BufferedInputStream bis = new BufferedInputStream(inputStream);

    try {
      bis.mark(Integer.MAX_VALUE);
      final String pisdfContent = IOUtils.toString(bis, Charset.forName("UTF-8"));
      PiSDFXSDValidator.validate(pisdfContent);
      bis.reset();
    } catch (final IOException ex) {
      throw new PreesmRuntimeException("Could not parse the input graph: \n" + ex.getMessage(), ex);
    }

    // Parse the input stream
    final Document document = DomUtil.parseDocument(bis);

    // Retrieve the root element
    final Element rootElt = document.getDocumentElement();

    try {
      // Fill the graph with parsed information
      parsePi(rootElt, graph);
    } catch (final RuntimeException e) {
      throw new PreesmRuntimeException("Could not parse the input graph: \n" + e.getMessage(), e);
    }

    return graph;
  }

  /**
   * Parse a node {@link Element} with kind "actor".
   *
   * @param nodeElt
   *          the {@link Element} to parse
   * @param graph
   *          the deserialized {@link PiGraph}
   * @return the created actor
   */
  protected Actor parseActor(final Element nodeElt, final PiGraph graph) {
    // Instantiate the new actor
    final Actor actor = PiMMUserFactory.instance.createActor();

    // Get the actor properties
    final String name = nodeElt.getAttribute(PiIdentifiers.ACTOR_NAME);

    if (!NameCheckerC.isValidName(name)) {
      throw new PreesmRuntimeException(
          "Parsed actor " + name + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }

    actor.setName(name);

    final String attribute = nodeElt.getAttribute(PiIdentifiers.ACTOR_PERIOD);
    if (attribute != null && !attribute.isEmpty()) {
      actor.setExpression(attribute);
    } else {
      // 0 means the actor is aperiodic, negative generates an error during evaluation
      actor.setExpression(0);
    }

    // Add the actor to the parsed graph
    graph.addActor(actor);

    parseRefinement(nodeElt, actor);

    final String memoryScript = PiParser.getProperty(nodeElt, PiIdentifiers.ACTOR_MEMORY_SCRIPT);
    if ((memoryScript != null) && !memoryScript.isEmpty()) {
      final IPath path = getWorkspaceRelativePathFrom(new Path(memoryScript));
      actor.setMemoryScriptPath(path);
    }

    return actor;
  }

  /**
   * Parses the refinement.
   *
   * @param nodeElt
   *          the node elt
   * @param actor
   *          the actor
   */
  private void parseRefinement(final Element nodeElt, final RefinementContainer actor) {
    if (!(actor instanceof DelayActor)) {
      actor.setRefinement(PiMMUserFactory.instance.createPiSDFRefinement());
    }
    final String refinement = PiParser.getProperty(nodeElt, PiIdentifiers.REFINEMENT);
    if ((refinement != null) && !refinement.isEmpty()) {
      final IPath path = getWorkspaceRelativePathFrom(new Path(refinement));

      // If the refinement is a .h file, then we need to create a
      // HRefinement
      if (path.getFileExtension().equals("h")) {
        final CHeaderRefinement hrefinement;
        // Delays already have a default refinement by default at creation time
        if (actor instanceof DelayActor) {
          hrefinement = (CHeaderRefinement) actor.getRefinement();
        } else {
          hrefinement = PiMMUserFactory.instance.createCHeaderRefinement();
        }
        // The nodeElt should have a loop element, and may have an init
        // element
        final NodeList childList = nodeElt.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
          final Node elt = childList.item(i);
          final String eltName = elt.getNodeName();
          Element elmt;
          switch (eltName) {
            case PiIdentifiers.REFINEMENT_LOOP:
              elmt = (Element) elt;
              hrefinement.setLoopPrototype(
                  parseFunctionPrototype(elmt, elmt.getAttribute(PiIdentifiers.REFINEMENT_FUNCTION_PROTOTYPE_NAME)));
              break;
            case PiIdentifiers.REFINEMENT_INIT:
              elmt = (Element) elt;
              hrefinement.setInitPrototype(
                  parseFunctionPrototype(elmt, elmt.getAttribute(PiIdentifiers.REFINEMENT_FUNCTION_PROTOTYPE_NAME)));
              break;
            default:
              // ignore #text and other children
          }
        }
        actor.setRefinement(hrefinement);
      }

      actor.getRefinement().setFilePath(path);
    }
  }

  /**
   * Parses the function prototype.
   *
   * @param protoElt
   *          the proto elt
   * @param protoName
   *          the proto name
   * @return the function prototype
   */
  private FunctionPrototype parseFunctionPrototype(final Element protoElt, final String protoName) {
    final FunctionPrototype proto = PiMMUserFactory.instance.createFunctionPrototype();

    proto.setName(protoName);
    final NodeList childList = protoElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      final Node elt = childList.item(i);
      final String eltName = elt.getNodeName();
      if (eltName == PiIdentifiers.REFINEMENT_PARAMETER) {
        proto.getParameters().add(parseFunctionParameter((Element) elt));
      }
    }
    return proto;
  }

  /**
   * Parses the function parameter.
   *
   * @param elt
   *          the elt
   * @return the function parameter
   */
  private FunctionParameter parseFunctionParameter(final Element elt) {
    final FunctionParameter param = PiMMUserFactory.instance.createFunctionParameter();
    param.setName(elt.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_NAME));
    param.setType(elt.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_TYPE));
    param.setDirection(Direction.valueOf(elt.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_DIRECTION)));
    param.setIsConfigurationParameter(Boolean.valueOf(elt.getAttribute(PiIdentifiers.REFINEMENT_PARAMETER_IS_CONFIG)));

    return param;
  }

  /**
   * Parse a ConfigInputInterface (i.e. a {@link Parameter}) of the Pi File.
   *
   * @param nodeElt
   *          The node {@link Element} holding the {@link Parameter} properties.
   * @param graph
   *          the deserialized {@link PiGraph}.
   * @return the {@link AbstractVertex} of the {@link Parameter}.
   */
  protected ConfigInputInterface parseConfigInputInterface(final Element nodeElt, final PiGraph graph) {
    // Instantiate the new Config Input Interface
    final ConfigInputInterface param = PiMMUserFactory.instance.createConfigInputInterface();

    // Get the actor properties
    final String attribute = nodeElt.getAttribute(PiIdentifiers.PARAMETER_NAME);
    if (!NameCheckerC.isValidName(attribute)) {
      throw new PreesmRuntimeException("Parsed ConfigInputInterface " + attribute
          + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }
    param.setName(attribute);

    // Add the actor to the parsed graph
    graph.addParameter(param);

    return param;
  }

  /**
   * Parse a node {@link Element} with kind "dependency".
   *
   * @param edgeElt
   *          the {@link Element} to parse
   * @param graph
   *          the deserialized {@link PiGraph}
   */
  protected void parseDependencies(final Element edgeElt, final PiGraph graph) {
    // Instantiate the new Dependency
    final Dependency dependency = PiMMUserFactory.instance.createDependency();

    // Find the source and target of the fifo
    final String setterName = edgeElt.getAttribute(PiIdentifiers.DEPENDENCY_SOURCE);
    final String getterName = edgeElt.getAttribute(PiIdentifiers.DEPENDENCY_TARGET);
    final AbstractVertex source = graph.lookupVertex(setterName);
    AbstractVertex target = graph.lookupVertex(getterName);
    if (source == null) {
      throw new PreesmRuntimeException("Dependency source vertex " + setterName + " does not exist.");
    }
    if (target == null) {
      // The target can also be a Delay associated to a Fifo
      final Fifo targetFifo = graph.lookupFifo(getterName);

      if (targetFifo == null) {
        throw new PreesmRuntimeException("Dependency target " + getterName + " does not exist.");
      }

      if (targetFifo.getDelay() == null) {
        throw new PreesmRuntimeException(
            "Dependency fifo target " + getterName + " has no delay to receive the dependency.");
      } else {
        target = targetFifo.getDelay();
      }
    }

    // Get the sourcePort and targetPort
    if (source instanceof ExecutableActor) {

      String sourcePortName = edgeElt.getAttribute(PiIdentifiers.DEPENDENCY_SOURCE_PORT);
      sourcePortName = (sourcePortName.isEmpty()) ? null : sourcePortName;
      final ConfigOutputPort oPort = (ConfigOutputPort) ((ExecutableActor) source).lookupPort(sourcePortName);
      if (oPort == null) {
        throw new PreesmRuntimeException(
            "Edge source port " + sourcePortName + " does not exist for vertex " + setterName);
      }
      dependency.setSetter(oPort);
    }
    if (source instanceof Parameter) {
      dependency.setSetter((ISetter) source);
    }

    if (target instanceof ExecutableActor) {
      String targetPortName = edgeElt.getAttribute(PiIdentifiers.DEPENDENCY_TARGET_PORT);
      targetPortName = (targetPortName.isEmpty()) ? null : targetPortName;
      final ConfigInputPort iPort = (ConfigInputPort) target.lookupPort(targetPortName);
      if (iPort == null) {
        throw new PreesmRuntimeException(
            "Dependency target port " + targetPortName + " does not exist for vertex " + getterName);
      }
      dependency.setGetter(iPort);
    }

    if (target instanceof DelayActor) {
      target = ((DelayActor) target).getLinkedDelay();
    }

    if ((target instanceof Parameter) || (target instanceof InterfaceActor) || (target instanceof Delay)) {
      final ConfigInputPort iCfgPort = PiMMUserFactory.instance.createConfigInputPort();
      ((Configurable) target).getConfigInputPorts().add(iCfgPort);
      dependency.setGetter(iCfgPort);
    }

    if ((dependency.getGetter() == null) || (dependency.getSetter() == null)) {
      throw new PreesmRuntimeException(
          "There was a problem parsing the following dependency: " + setterName + "=>" + getterName);
    }

    // Add the new dependency to the graph
    graph.addDependency(dependency);
  }

  /**
   * Parse an edge {@link Element} of the Pi description. An edge {@link Element} can be a parameter dependency or a
   * FIFO of the parsed graph.
   *
   * @param edgeElt
   *          The edge {@link Element} to parse
   * @param graph
   *          The deserialized graph
   */
  protected void parseEdge(final Element edgeElt, final PiGraph graph) {
    // Identify if the node is an actor or a parameter
    final String edgeKind = edgeElt.getAttribute(PiIdentifiers.EDGE_KIND);

    switch (edgeKind) {
      case PiIdentifiers.FIFO:
        parseFifo(edgeElt, graph);
        break;
      case PiIdentifiers.DEPENDENCY:
        parseDependencies(edgeElt, graph);
        break;
      default:
        throw new PreesmRuntimeException("Parsed edge has an unknown kind: " + edgeKind);
    }
  }

  /**
   * Search for a DataPort with name "name" in a given list of DataPort
   *
   * @param ports
   *          List of DataPort
   * @param name
   *          Name of the DataPort to find
   * @return Found DataPort if any, null else
   */
  private static DataPort lookForPort(final List<? extends DataPort> ports, final String name) {
    for (final DataPort dp : ports) {
      if (dp.getName().equals(name)) {
        return dp;
      }
    }
    return null;
  }

  /**
   * Parse a node {@link Element} with kind "fifo".
   *
   * @param edgeElt
   *          the edge elt
   * @param graph
   *          the deserialized {@link PiGraph}
   */
  protected void parseFifo(final Element edgeElt, final PiGraph graph) {
    // Instantiate the new FIFO

    // Find the source and target of the fifo
    final String sourceName = edgeElt.getAttribute(PiIdentifiers.FIFO_SOURCE);
    final String targetName = edgeElt.getAttribute(PiIdentifiers.FIFO_TARGET);
    final AbstractActor source = (AbstractActor) graph.lookupVertex(sourceName);
    final AbstractActor target = (AbstractActor) graph.lookupVertex(targetName);

    if (source == null) {
      throw new PreesmRuntimeException("Edge source vertex " + sourceName + " does not exist.");
    }
    if (target == null) {
      throw new PreesmRuntimeException("Edge target vertex " + targetName + " does not exist.");
    }
    // Get the type
    String type = edgeElt.getAttribute(PiIdentifiers.FIFO_TYPE);
    // If none is find, add the default type
    if ((type == null) || type.equals("")) {
      type = "void";
    }
    // Get the sourcePort and targetPort
    String sourcePortName = edgeElt.getAttribute(PiIdentifiers.FIFO_SOURCE_PORT);
    sourcePortName = (sourcePortName.isEmpty()) ? null : sourcePortName;
    String targetPortName = edgeElt.getAttribute(PiIdentifiers.FIFO_TARGET_PORT);
    targetPortName = (targetPortName.isEmpty()) ? null : targetPortName;
    final DataOutputPort oPort = (DataOutputPort) lookForPort(source.getDataOutputPorts(), sourcePortName);
    final DataInputPort iPort = (DataInputPort) lookForPort(target.getDataInputPorts(), targetPortName);

    if (oPort == null) {
      throw new PreesmRuntimeException(
          "Edge source port " + sourcePortName + " does not exist for vertex " + sourceName);
    }
    if (iPort == null) {
      throw new PreesmRuntimeException(
          "Edge target port " + targetPortName + " does not exist for vertex " + targetName);
    }

    final Fifo fifo = PiMMUserFactory.instance.createFifo(oPort, iPort, type);

    // Check if the fifo has a delay

    final String fifoDelay = PiParser.getProperty(edgeElt, PiIdentifiers.DELAY);
    if ((fifoDelay != null)) {
      Delay delay;
      if (fifoDelay.isEmpty()) {
        // Support for old ".pi" files
        delay = PiMMUserFactory.instance.createDelay();
        delay.setExpression(edgeElt.getAttribute(PiIdentifiers.DELAY_EXPRESSION));
        graph.addDelay(delay);
      } else {
        // Find the delay of the FIFO
        // Delays are seen as nodes so the delay is already created and parsed by now
        delay = graph.lookupDelay(fifoDelay);
        if (delay == null) {
          throw new PreesmRuntimeException("Edge delay " + fifoDelay + " does not exist.");
        }
      }
      // Adds the delay to the FIFO (and sets the FIFO of the delay at the same time)
      fifo.setDelay(delay);
    }

    // Add the new Fifo to the graph
    graph.addFifo(fifo);
  }

  /**
   * Parse a node {@link Element} with kind "delay".
   *
   * @param nodeElt
   *          the {@link Element} to parse
   * @param graph
   *          the deserialized {@link PiGraph}
   * @return the created delay
   */
  protected DelayActor parseDelay(final Element nodeElt, final PiGraph graph) {
    // 1. Instantiate the new delay
    final Delay delay = PiMMUserFactory.instance.createDelay();

    // 2. Set the delay expression
    delay.setExpression(nodeElt.getAttribute(PiIdentifiers.DELAY_EXPRESSION));

    // 3. Get the delay ID
    delay.setName(nodeElt.getAttribute(PiIdentifiers.DELAY_NAME));

    // 4. Set the persistence level
    final String persistenceLevel = nodeElt.getAttribute(PiIdentifiers.DELAY_PERSISTENCE_LEVEL);
    delay.setLevel(PersistenceLevel.get(persistenceLevel));

    // 5. Setting properties of the non executable actor associated with the delay
    final DelayActor delayActor = delay.getActor();

    // 5.1 Setting name of input port
    final NodeList childList = nodeElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      final Node elt = childList.item(i);
      final String eltName = elt.getNodeName();

      if (PiIdentifiers.PORT.equals(eltName)) {
        final Element element = (Element) elt;
        final String portName = element.getAttribute(PiIdentifiers.PORT_NAME);
        final String portKind = element.getAttribute(PiIdentifiers.PORT_KIND);
        if (PortKind.get(portKind).equals(PortKind.DATA_INPUT)) {
          delayActor.getDataInputPort().setName(portName);
        } else if (PortKind.get(portKind).equals(PortKind.DATA_OUTPUT)) {
          delayActor.getDataOutputPort().setName(portName);
        }
      }
    }

    // 6. Adds Setter / Getter actors to the delay (if any)
    final String setterName = nodeElt.getAttribute(PiIdentifiers.DELAY_SETTER);
    final AbstractActor setter = (AbstractActor) graph.lookupVertex(setterName);
    // final String getterName = nodeElt.getAttribute(PiIdentifiers.DELAY_GETTER);
    // final AbstractActor getter = (AbstractActor) graph.lookupVertex(getterName);
    // if ((setter == null) && !setterName.isEmpty()) {
    // throw new PreesmRuntimeException("Delay setter vertex " + setterName + " does not exist.");
    // }
    // if ((getter == null) && !getterName.isEmpty()) {
    // throw new PreesmRuntimeException("Delay getter vertex " + getterName + " does not exist.");
    // }

    // 7. Add the refinement for the INIT of the delay (if it exists)
    // Any refinement is ignored if the delay is already connected to a setter actor
    if (setter == null) {
      parseRefinement(nodeElt, delayActor);
      // Checks the validity of the H refinement of the delay
      if (delayActor.getRefinement() instanceof CHeaderRefinement) {
        final CHeaderRefinement hrefinement = (CHeaderRefinement) delayActor.getRefinement();
        if (!delayActor.isValidRefinement(hrefinement)) {
          throw new PreesmRuntimeException(
              "Delay INIT prototype must match following prototype: void init(IN int size, OUT <type>* fifo)");
        }
        final String delayInitPrototype = "Delay INIT function used: " + hrefinement.getLoopPrototype().getName();
        PreesmLogger.getLogger().log(Level.INFO, delayInitPrototype);
      }
    }

    // 8. Add the delay to the parsed graph
    graph.addDelay(delay);

    return delayActor;
  }

  /**
   * Retrieve and parse the graph element of the Pi description.
   *
   * @param rootElt
   *          The root element (that must have a graph child)
   * @param graph
   *          The deserialized {@link PiGraph}
   */
  protected void parseGraph(final Element rootElt, final PiGraph graph) {
    // Retrieve the Graph Element
    final NodeList graphElts = rootElt.getElementsByTagName(PiIdentifiers.GRAPH);
    if (graphElts.getLength() == 0) {
      throw new PreesmRuntimeException("No graph was found in the parsed document");
    }
    if (graphElts.getLength() > 1) {
      throw new PreesmRuntimeException("More than one graph was found in the parsed document");
    }
    // If this code is reached, a unique graph element was found in the
    // document
    final Element graphElt = (Element) graphElts.item(0);

    // TODO parseGraphProperties() of the graph

    // Parse the elements of the graph
    final NodeList childList = graphElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      final Node elt = childList.item(i);

      final String eltName = elt.getNodeName();

      switch (eltName) {
        case PiIdentifiers.DATA:
          // Properties of the Graph.
          // TODO transfer this code in a separate function
          // parseGraphProperties()
          final String keyName = elt.getAttributes().getNamedItem(PiIdentifiers.DATA_KEY).getNodeValue();
          final String keyValue = elt.getTextContent();
          if (keyName.equals(PiIdentifiers.GRAPH_NAME)) {
            graph.setName(keyValue);
          }
          break;
        case PiIdentifiers.NODE:
          // Node elements
          parseNode((Element) elt, graph);
          break;
        case PiIdentifiers.EDGE:
          // Edge elements
          parseEdge((Element) elt, graph);
          break;
        default:

      }
    }
  }

  /**
   * Parse a node {@link Element} of the Pi description. A node {@link Element} can be a parameter or an vertex of the
   * parsed graph.
   *
   * @param nodeElt
   *          The node {@link Element} to parse
   * @param graph
   *          The deserialized {@link PiGraph}
   */
  protected void parseNode(final Element nodeElt, final PiGraph graph) {
    String nodeName = nodeElt.getNodeName();
    // Identify if the node is an actor or a parameter
    final String nodeKind = nodeElt.getAttribute(PiIdentifiers.NODE_KIND);
    Configurable vertex;

    final InterfaceKind ik = InterfaceKind.get(nodeKind);
    if (ik != null) {
      switch (ik) {
        case DATA_INPUT:
          vertex = parseSourceInterface(nodeElt, graph);
          break;
        case DATA_OUTPUT:
          vertex = parseSinkInterface(nodeElt, graph);
          break;
        case CFG_INPUT:
          vertex = parseConfigInputInterface(nodeElt, graph);
          break;
        case CFG_OUTPUT:
          vertex = parseConfigOutputInterface(nodeElt, graph);
          break;

        default:
          throw new PreesmRuntimeException("Parsed node " + nodeName + " has an unknown kind: " + nodeKind);
      }
    } else {
      switch (nodeKind) {
        case PiIdentifiers.ACTOR:
          vertex = parseActor(nodeElt, graph);
          break;
        case PiIdentifiers.BROADCAST:
        case PiIdentifiers.FORK:
        case PiIdentifiers.JOIN:
        case PiIdentifiers.ROUND_BUFFER:
        case PiIdentifiers.END:
        case PiIdentifiers.INIT:
          vertex = parseSpecialActor(nodeElt, graph);
          break;
        case PiIdentifiers.PARAMETER:
          vertex = parseParameter(nodeElt, graph);
          break;
        case PiIdentifiers.DELAY:
          parseDelay(nodeElt, graph);
          // Ignore parsing of ports
          // Delays have pre-defined ports created at delay actor instantiation
          return;
        default:
          throw new PreesmRuntimeException("Parsed node " + nodeName + " has an unknown kind: " + nodeKind);
      }
    }

    // Parse the elements of the node
    final NodeList childList = nodeElt.getChildNodes();
    for (int i = 0; i < childList.getLength(); i++) {
      final Node elt = childList.item(i);
      final String eltName = elt.getNodeName();

      if (PiIdentifiers.PORT.equals(eltName)) {
        parsePort((Element) elt, vertex);
      }
    }
    // Sanity check for special actors
    if ((vertex instanceof BroadcastActor) && (((AbstractActor) vertex).getDataInputPorts().size() > 1)) {
      throw new PreesmRuntimeException("Broadcast with multiple input detected [" + vertex.getName()
          + "].\n Broadcast actors can only have one input!");
    } else if ((vertex instanceof ForkActor) && (((AbstractActor) vertex).getDataInputPorts().size() > 1)) {
      throw new PreesmRuntimeException(
          "ForkActor with multiple input detected [" + vertex.getName() + "].\n Fork actors can only have one input!");
    }
  }

  /**
   * Parse a {@link Parameter} of the Pi File.
   *
   * @param nodeElt
   *          The node {@link Element} holding the {@link Parameter} properties.
   * @param graph
   *          the deserialized {@link PiGraph}.
   * @return the {@link AbstractVertex} of the {@link Parameter}.
   */
  protected Parameter parseParameter(final Element nodeElt, final PiGraph graph) {
    // Instantiate the new Parameter
    final Parameter param = PiMMUserFactory.instance.createParameter();
    param.setExpression(nodeElt.getAttribute(PiIdentifiers.PARAMETER_EXPRESSION));

    // Get the actor properties
    String name = nodeElt.getAttribute(PiIdentifiers.PARAMETER_NAME);
    if (!NameCheckerC.isValidName(name)) {
      throw new PreesmRuntimeException(
          "Parsed parameter " + name + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }
    param.setName(name);

    // Add the actor to the parsed graph
    graph.addParameter(param);

    return param;
  }

  /**
   * Parse the root element of the Pi description.
   *
   * @param rootElt
   *          the root elt
   * @param graph
   *          The deserialized {@link PiGraph}
   */
  protected void parsePi(final Element rootElt, final PiGraph graph) {
    // TODO parseKeys() (Not sure if it is really necessary to do that)

    // Parse the graph element
    parseGraph(rootElt, graph);

  }

  /**
   * Parse a {@link Port} of the Pi file.
   *
   * @param elt
   *          the {@link Element} holding the {@link Port} properties.
   * @param vertex
   *          the {@link AbstractVertex} owning this {@link Port}
   */
  protected void parsePort(final Element elt, final Configurable vertex) {
    final String portName = elt.getAttribute(PiIdentifiers.PORT_NAME);
    if (!NameCheckerC.isValidName(portName)) {
      throw new PreesmRuntimeException(
          "Parsed port " + portName + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }

    final String portKind = elt.getAttribute(PiIdentifiers.PORT_KIND);

    final String attribute = elt.getAttribute(PiIdentifiers.PORT_EXPRESSION);
    final String annotation = elt.getAttribute(PiIdentifiers.PORT_MEMORY_ANNOTATION);
    final PortMemoryAnnotation portMemoryAnnotation = PortMemoryAnnotation.get(annotation);

    switch (PortKind.get(portKind)) {
      case DATA_INPUT:
        // Throw an error if the parsed vertex is not an actor
        if (!(vertex instanceof AbstractActor)) {
          throw new PreesmRuntimeException(
              "Parsed data port " + portName + " cannot belong to the non-actor vertex " + vertex.getName());
        }

        DataInputPort iPort;

        // Do not create data ports for InterfaceActor since the unique port
        // is automatically created when the vertex is instantiated
        // same for delays
        if (!(vertex instanceof InterfaceActor)) {
          iPort = PiMMUserFactory.instance.createDataInputPort();
          ((AbstractActor) vertex).getDataInputPorts().add(iPort);
          iPort.setName(portName);
        } else {
          iPort = ((AbstractActor) vertex).getDataInputPorts().get(0);
        }
        iPort.setExpression(attribute);
        iPort.setAnnotation(portMemoryAnnotation);
        break;
      case DATA_OUTPUT:
        // Throw an error if the parsed vertex is not an actor
        if (!(vertex instanceof AbstractActor)) {
          throw new PreesmRuntimeException(
              "Parsed data port " + portName + " cannot belong to the non-actor vertex " + vertex.getName());
        }

        DataOutputPort oPort;

        // Do not create data ports for InterfaceActor since the unique port
        // is automatically created when the vertex is instantiated
        // same for delays
        if (!(vertex instanceof InterfaceActor)) {
          oPort = PiMMUserFactory.instance.createDataOutputPort();
          ((AbstractActor) vertex).getDataOutputPorts().add(oPort);
          oPort.setName(portName);
        } else {
          oPort = ((AbstractActor) vertex).getDataOutputPorts().get(0);
        }
        oPort.setExpression(attribute);
        oPort.setAnnotation(portMemoryAnnotation);
        break;
      case CFG_INPUT:
        final ConfigInputPort iCfgPort = PiMMUserFactory.instance.createConfigInputPort();
        iCfgPort.setName(portName);
        vertex.getConfigInputPorts().add(iCfgPort);
        break;

      case CFG_OUTPUT:
        // Throw an error if the parsed vertex is not an actor
        if (!(vertex instanceof AbstractActor)) {
          throw new PreesmRuntimeException(
              "Parsed config. port " + portName + " cannot belong to the non-actor vertex " + vertex.getName());
        }
        final ConfigOutputPort oCfgPort = PiMMUserFactory.instance.createConfigOutputPort();
        oCfgPort.setName(portName);
        ((AbstractActor) vertex).getConfigOutputPorts().add(oCfgPort);
        break;
      default:
        throw new PreesmRuntimeException("Parsed port " + portName + " has children of unknown kind: " + portKind);
    }
  }

  /**
   * Parse a node {@link Element} with kind "cfg_out_iface".
   *
   * @param nodeElt
   *          the {@link Element} to parse
   * @param graph
   *          the deserialized {@link PiGraph}
   * @return the created {@link ConfigOutputInterface}
   */
  protected AbstractActor parseConfigOutputInterface(final Element nodeElt, final PiGraph graph) {
    // Instantiate the new Interface and its corresponding port
    final ConfigOutputInterface cfgOutIf = PiMMUserFactory.instance.createConfigOutputInterface();

    // Set the Interface properties
    final String name = nodeElt.getAttribute(PiIdentifiers.CONFIGURATION_OUTPUT_INTERFACE_NAME);
    if (!NameCheckerC.isValidName(name)) {
      throw new PreesmRuntimeException(
          "Parsed ConfigOutputInterface " + name + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }

    cfgOutIf.setName(name);
    cfgOutIf.getDataPort().setName(name);

    // Add the actor to the parsed graph
    graph.addActor(cfgOutIf);

    return cfgOutIf;
  }

  /**
   * Parse a node {@link Element} with kind "snk".
   *
   * @param nodeElt
   *          the {@link Element} to parse
   * @param graph
   *          the deserialized {@link PiGraph}
   * @return the created {@link DataOutputInterface}
   */
  protected AbstractActor parseSinkInterface(final Element nodeElt, final PiGraph graph) {
    // Instantiate the new Interface and its corresponding port
    final DataOutputInterface snkInterface = PiMMUserFactory.instance.createDataOutputInterface();

    // Set the sourceInterface properties
    final String name = nodeElt.getAttribute(PiIdentifiers.DATA_OUTPUT_INTERFACE_NAME);
    if (!NameCheckerC.isValidName(name)) {
      throw new PreesmRuntimeException(
          "Parsed SinkInterface " + name + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }

    snkInterface.setName(name);
    snkInterface.getDataPort().setName(name);

    // Add the actor to the parsed graph
    graph.addActor(snkInterface);

    return snkInterface;
  }

  /**
   * Parse a node {@link Element} with kind "src".
   *
   * @param nodeElt
   *          the {@link Element} to parse
   * @param graph
   *          the deserialized {@link PiGraph}
   * @return the created {@link DataInputInterface}
   */
  protected AbstractActor parseSourceInterface(final Element nodeElt, final PiGraph graph) {
    // Instantiate the new Interface and its corresponding port
    final DataInputInterface srcInterface = PiMMUserFactory.instance.createDataInputInterface();

    // Set the sourceInterface properties
    final String name = nodeElt.getAttribute(PiIdentifiers.DATA_INPUT_INTERFACE_NAME);
    if (!NameCheckerC.isValidName(name)) {
      throw new PreesmRuntimeException(
          "Parsed SourceInterface " + name + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }

    srcInterface.setName(name);
    srcInterface.getDataPort().setName(name);

    // Add the actor to the parsed graph
    graph.addActor(srcInterface);

    return srcInterface;
  }

  /**
   * Parse a node {@link Element} with kind "broadcast", "fork", "join", "roundbuffer".
   *
   * @param nodeElt
   *          the {@link Element} to parse
   * @param graph
   *          the deserialized {@link PiGraph}
   * @return the created actor
   */
  protected AbstractActor parseSpecialActor(final Element nodeElt, final PiGraph graph) {
    // Identify if the node is an actor or a parameter
    final String nodeKind = nodeElt.getAttribute(PiIdentifiers.NODE_KIND);
    AbstractActor actor = null;

    String name = nodeElt.getAttribute(PiIdentifiers.ACTOR_NAME);
    if (!NameCheckerC.isValidName(name)) {
      throw new PreesmRuntimeException(
          "Parsed special actor " + name + " has an invalide name (should meet " + NameCheckerC.REGEX_C + ")");
    }

    // Instantiate the actor.
    switch (nodeKind) {
      case PiIdentifiers.BROADCAST:
        actor = PiMMUserFactory.instance.createBroadcastActor();
        break;
      case PiIdentifiers.FORK:
        actor = PiMMUserFactory.instance.createForkActor();
        break;
      case PiIdentifiers.JOIN:
        actor = PiMMUserFactory.instance.createJoinActor();
        break;
      case PiIdentifiers.ROUND_BUFFER:
        actor = PiMMUserFactory.instance.createRoundBufferActor();
        break;
      case PiIdentifiers.INIT:
        actor = PiMMUserFactory.instance.createInitActor();
        final String endRef = nodeElt.getAttribute(PiIdentifiers.INIT_END_REF);
        if (endRef != null) {
          final AbstractVertex lookupVertex = graph.lookupVertex(endRef);
          if (lookupVertex != null) {
            ((InitActor) actor).setEndReference((AbstractActor) lookupVertex);
            if (lookupVertex instanceof EndActor) {
              ((EndActor) lookupVertex).setInitReference(actor);
            }
          }
        }
        break;
      case PiIdentifiers.END:
        actor = PiMMUserFactory.instance.createEndActor();
        final String initRef = nodeElt.getAttribute(PiIdentifiers.INIT_END_REF);
        if (initRef != null) {
          final AbstractVertex lookupVertex = graph.lookupVertex(initRef);
          if (lookupVertex != null) {
            ((EndActor) actor).setInitReference((AbstractActor) lookupVertex);
            if (lookupVertex instanceof InitActor) {
              ((InitActor) lookupVertex).setEndReference(actor);
            }
          }
        }
        break;
      default:
        throw new IllegalArgumentException("Given node element has an unknown kind");
    }

    // Get the actor properties
    actor.setName(name);

    // Add the actor to the parsed graph
    graph.addActor(actor);

    return actor;
  }

  /**
   * Transform a project relative path to workspace relative path.
   *
   * @param path
   *          the IPath to transform
   * @return the path to the file inside the project containing the parsed file if this file exists, path otherwise
   */
  private IPath getWorkspaceRelativePathFrom(final IPath path) {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    // If the file pointed by path does not exist, we try to add the
    // name of the project containing the file we parse to it
    if (!root.getFile(path).exists()) {
      // Get the project
      final String platformString = this.documentURI.toPlatformString(true);
      final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
      final IProject documentProject = documentFile.getProject();
      // Create a new path using the project name
      final IPath newPath = new Path(documentProject.getName()).append(path);
      // Check there is a file where newPath points, if yes, use it
      // instead of path
      if (root.getFile(newPath).exists()) {
        return newPath;
      }
    }
    return path;
  }
}
