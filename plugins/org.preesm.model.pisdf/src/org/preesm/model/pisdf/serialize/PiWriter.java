/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2014)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2013 - 2014)
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.preesm.commons.DomUtil;
import org.preesm.commons.GMLKey;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.InterfaceKind;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.MoldableParameter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.Parameterizable;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.reconnection.SubgraphOriginalActorTracker;
import org.preesm.model.pisdf.util.PiIdentifiers;
import org.preesm.model.pisdf.util.PiSDFXSDValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Writer for the PiMM Model in the Pi format.
 *
 * @author kdesnos
 * @author jheulot
 */
public class PiWriter {

  /** The document created by this writer. */
  protected Document domDocument;

  /** The document URI. */
  // URI where the document will be saved
  private final URI documentURI;

  /**
   * This LinkedHashMap associates a List to each <b>element</b> (graph, node, edge, port) of a Pi description. For each
   * <b>element</b>, a list of {@link GMLKey} s is associated. A {@link GMLKey} can be seen as an attribute of this
   * element.
   */
  protected Map<String, List<GMLKey>> elementKeys;

  /** Graph {@link Element} of the DOM {@link Document} of this Writer. */
  protected Element graphElement;

  /** Root {@link Element} of the DOM {@link Document} of this Writer. */
  protected Element rootElement;

  /**
   * Default constructor of the {@link PiWriter}.
   *
   * @param uri
   *          the output document URI
   */
  public PiWriter(final URI uri) {
    // Instantiate an empty elementKeys Map
    this.elementKeys = new LinkedHashMap<>();

    // Initialize attributes to null
    this.rootElement = null;
    this.graphElement = null;
    this.documentURI = uri;
  }

  /**
   * Creates and appends the Graph Element of the document.
   *
   * @param parentElement
   *          The parent element of the graph
   * @return The created element
   */
  protected Element addGraphElt(final Element parentElement) {
    final Element newElt = appendChild(parentElement, PiIdentifiers.GRAPH);
    this.graphElement = newElt;
    newElt.setAttribute(PiIdentifiers.GRAPH_EDGE_DEFAULT, PiIdentifiers.GRAPH_DIRECTED);

    return newElt;
  }

  /**
   * Adds a Key to the elementKeys map with the specified informations. Also insert the key at the document root.
   *
   * @param id
   *          Id of the key (identical to name)
   * @param name
   *          The Name of the key
   * @param elt
   *          The Class this key applies to
   * @param type
   *          The value type of this key (can be null)
   * @param desc
   *          This key description (can be null)
   */
  protected void addKey(final String id, final String name, final String elt, final String type, final Class<?> desc) {
    // Create the new Key
    final GMLKey key = new GMLKey(name, elt, type, desc);
    key.setId(id);

    // Create the corresponding keyElement (if it does not exists)
    final Element keyElt = createKeyElt(key);

    // If the new keyElement was created
    if (keyElt != null) {
      // Put the Key in the list of its element
      this.elementKeys.get(key.getApplyTo()).add(key);

      // Add the KeyElement to the document
      // also works if graphElt does not exist yet
      this.rootElement.insertBefore(keyElt, this.graphElement);
    }

  }

  /**
   * Creates a new child for the given parent Element with the name "name".
   *
   * @author Jonathan Piat
   * @param parentElement
   *          The element to add a child
   * @param name
   *          The name of this Element
   * @return The created Element
   */
  protected Element appendChild(final Node parentElement, final String name) {
    final Element newElt = this.domDocument.createElement(name);
    parentElement.appendChild(newElt);
    return newElt;
  }

  /**
   * Check if the key already exists.
   *
   * @param key
   *          the key
   * @return the element
   */
  protected Element createKeyElt(final GMLKey key) {
    // Check if the element already has a Key list
    if (this.elementKeys.get(key.getApplyTo()) == null) {
      // If not, create the Key list for this element and add it to elementKeys
      final ArrayList<GMLKey> keys = new ArrayList<>();
      this.elementKeys.put(key.getApplyTo(), keys);
    } else if (this.elementKeys.get(key.getApplyTo()).contains(key)) {
      // If the element already exists, check if this key is already in its Key list
      // The element already exists, no need to create a new one
      return null;
    }

    // If this code is reached, the new element must be created
    // Create the element
    final Element newElt = this.domDocument.createElement("key");
    newElt.setAttribute("attr.name", key.getName());
    if (key.getApplyTo() != null) {
      newElt.setAttribute("for", key.getApplyTo());
    }
    if (key.getType() != null) {
      newElt.setAttribute("attr.type", key.getType());
    }
    if (key.getId() != null) {
      newElt.setAttribute("id", key.getId());
    }
    if (key.getTypeClass() != null) {
      final Element descElt = appendChild(newElt, "desc");
      descElt.setTextContent(key.getTypeClass().getName());
    }

    return newElt;
  }

  /**
   * Write the PiMM {@link PiGraph} to the given {@link OutputStream} using the Pi format.
   *
   * @param graph
   *          The Graph to write
   * @param outputStream
   *          The written OutputStream
   */
  public void write(final PiGraph graph, final OutputStream outputStream) {

    // Check consistency of the graph (throw exception if fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
        CheckerErrorLevel.NONE);
    pgcc.check(graph);

    // Create the domDocument
    this.domDocument = DomUtil.createDocument("http://graphml.graphdrawing.org/xmlns", "graphml");

    // Retrieve the root element of the document
    this.rootElement = this.domDocument.getDocumentElement();

    // Fill the root Element with the Graph
    writePi(this.rootElement, graph);

    final OutputStream tmpStream = new OutputStream() {
      StringBuilder buffer = new StringBuilder();

      @Override
      public void write(final int b) throws IOException {
        outputStream.write(b);
        this.buffer.append((char) b);
      }

      @Override
      public String toString() {
        return this.buffer.toString();
      }
    };

    // Produce the output file
    DomUtil.writeDocument(this.domDocument, tmpStream);

    try {
      PiSDFXSDValidator.validate(tmpStream.toString());
    } catch (final IOException e) {
      throw new PreesmRuntimeException("Fatal Error: the wirtten PiSDF does not comply to the XSD Schema", e);
    }

  }

  /**
   * Create and add a node {@link Element} to the given parent {@link Element} for the given {@link AbstractActor} and
   * write its informations.
   *
   * @param graphElt
   *          The parent element of the node element (i.e. the graph of the document)
   * @param abstractActor
   *          The {@link AbstractActor} to write in the {@link Document}
   */
  protected void writeAbstractActor(final Element graphElt, final AbstractActor abstractActor) {
    // Add the node to the document
    final Element vertexElt = appendChild(graphElt, PiIdentifiers.NODE);

    // Set the unique ID of the node (equal to the vertex name)
    vertexElt.setAttribute(PiIdentifiers.ACTOR_NAME, abstractActor.getName());

    // Add the name in the data of the node

    if (abstractActor instanceof final PiGraph piGraph) {
      writePiGraphAsHActor(vertexElt, piGraph);
    } else if (abstractActor instanceof final Actor actor) {
      writeActor(vertexElt, actor);
    } else if (abstractActor instanceof ExecutableActor) {
      writeSpecialActor(vertexElt, abstractActor);
    } else if (abstractActor instanceof final InterfaceActor iActor) {
      writeInterfaceVertex(vertexElt, iActor);
    }
  }

  private void writePiGraphAsHActor(final Element vertexElt, final PiGraph childGraph) {
    final Actor originalActor = SubgraphOriginalActorTracker.getOriginalActor(childGraph);

    // Set the kind of the Actor
    vertexElt.setAttribute(PiIdentifiers.NODE_KIND, PiIdentifiers.ACTOR);

    final String periodExpr = originalActor.getPeriod().getExpressionAsString();
    if (!periodExpr.equals("0")) {
      vertexElt.setAttribute(PiIdentifiers.ACTOR_PERIOD, periodExpr);
    }

    final Refinement refinement = originalActor.getRefinement();
    if (refinement != null) {
      writeRefinement(vertexElt, refinement);
    }
    final IPath memoryScriptPath = Optional.ofNullable(originalActor.getMemoryScriptPath()).map(Path::new).orElse(null);
    if (memoryScriptPath != null) {
      writeMemoryScript(vertexElt, getProjectRelativePathFrom(memoryScriptPath));
    }
    // Write ports of the actor
    writePorts(vertexElt, childGraph.getConfigInputPorts());
    writePorts(vertexElt, childGraph.getConfigOutputPorts());
    writePorts(vertexElt, childGraph.getDataInputPorts());
    writePorts(vertexElt, childGraph.getDataOutputPorts());

  }

  /**
   * Write information of the {@link Actor} in the given {@link Element}.
   *
   * @param vertexElt
   *          The {@link Element} to write
   * @param actor
   *          The {@link Actor} to serialize
   */
  protected void writeActor(final Element vertexElt, final Actor actor) {
    // Set the kind of the Actor
    vertexElt.setAttribute(PiIdentifiers.NODE_KIND, PiIdentifiers.ACTOR);
    final String periodExpr = actor.getPeriod().getExpressionAsString();
    if (!periodExpr.equals("0")) {
      vertexElt.setAttribute(PiIdentifiers.ACTOR_PERIOD, periodExpr);
    }

    final Refinement refinement = actor.getRefinement();
    if (refinement != null) {
      writeRefinement(vertexElt, refinement);
    }
    final IPath memoryScriptPath = Optional.ofNullable(actor.getMemoryScriptPath()).map(Path::new).orElse(null);
    if (memoryScriptPath != null) {
      writeMemoryScript(vertexElt, getProjectRelativePathFrom(memoryScriptPath));
    }
    // Write ports of the actor
    writePorts(vertexElt, actor.getConfigInputPorts());
    writePorts(vertexElt, actor.getConfigOutputPorts());
    writePorts(vertexElt, actor.getDataInputPorts());
    writePorts(vertexElt, actor.getDataOutputPorts());

  }

  /**
   * Add a data child {@link Element} to the parent {@link Element} whit the given key name and the given content. If
   * the {@link GMLKey} does not exist yet, it will be created automatically.
   *
   * @param parentElt
   *          The element to which the data is added
   * @param keyName
   *          The name of the key for the added data
   * @param textContent
   *          The text content of the data element
   */
  protected void writeDataElt(final Element parentElt, final String keyName, final String textContent) {
    addKey(null, keyName, parentElt.getTagName(), "string", null);
    final Element nameElt = appendChild(parentElt, PiIdentifiers.DATA);
    nameElt.setAttribute(PiIdentifiers.DATA_KEY, keyName);
    nameElt.setTextContent(textContent);
  }

  /**
   * Write the {@link Delay} information in the given {@link Element}.
   *
   * @param fifoElt
   *          the {@link Element} to write
   * @param delay
   *          the {@link Delay} to serialize
   */
  protected void writeDelayVertex(final Element graphElt, final Delay delay) {
    // Add the node to the document
    final Element vertexElt = appendChild(graphElt, PiIdentifiers.NODE);

    // Set the unique ID of the node (equal to the vertex name)
    vertexElt.setAttribute(PiIdentifiers.DELAY_NAME, delay.getActor().getName());

    // Set the delay attribute to the node
    vertexElt.setAttribute(PiIdentifiers.NODE_KIND, PiIdentifiers.DELAY);

    // Writes the persistence level of the delay
    vertexElt.setAttribute(PiIdentifiers.DELAY_PERSISTENCE_LEVEL, delay.getLevel().getLiteral());

    // Write setter and getter names if delay has any
    final DelayActor actor = delay.getActor();
    final String setterName = delay.hasSetterActor() ? actor.getSetterActor().getName() : "";
    vertexElt.setAttribute(PiIdentifiers.DELAY_SETTER, setterName);
    final String getterName = delay.hasGetterActor() ? actor.getGetterActor().getName() : "";
    vertexElt.setAttribute(PiIdentifiers.DELAY_GETTER, getterName);
    vertexElt.setAttribute(PiIdentifiers.DELAY_EXPRESSION, delay.getSizeExpression().getExpressionAsString());

    // Checks if the delay has refinement in case of no setter is provided
    if (!delay.hasSetterActor()) {
      final Refinement refinement = actor.getRefinement();
      if (refinement instanceof CHeaderRefinement) {
        writeRefinement(vertexElt, refinement);
      }
    }

    if (actor != null) {
      writePorts(vertexElt, actor.getDataInputPorts());
      writePorts(vertexElt, actor.getDataOutputPorts());
    }
  }

  /**
   * Create and add a node {@link Element} to the given parent {@link Element} for the given {@link Dependency} and
   * write its informations.
   *
   * @param graphElt
   *          The parent element of the node element (i.e. the graph of the document)
   * @param dependency
   *          The {@link Dependency} to write in the {@link Document}
   */
  protected void writeDependency(final Element graphElt, final Dependency dependency) {
    // Add the node to the document
    final Element dependencyElt = appendChild(graphElt, PiIdentifiers.EDGE);
    dependencyElt.setAttribute(PiIdentifiers.EDGE_KIND, PiIdentifiers.DEPENDENCY);

    // Set the source and target attributes
    final ISetter setter = dependency.getSetter();
    if (setter == null) {
      throw new NullPointerException("Setter of the dependency is null");
    }

    final ConfigInputPort getter = dependency.getGetter();
    if (getter == null) {
      throw new NullPointerException("Getter of the dependency is null");
    }

    AbstractVertex source = null;
    if (setter instanceof ConfigOutputPort) {
      source = (AbstractVertex) setter.eContainer();
    }

    if (setter instanceof Parameter) {
      source = (AbstractVertex) setter;
    }

    if (source == null) {
      throw new PreesmRuntimeException(
          "Setter of the dependency has a type not supported by the writer: " + setter.getClass());
    }
    dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_SOURCE, source.getName());
    if (setter instanceof ConfigOutputPort) {
      dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_SOURCE_PORT, ((Port) setter).getName());
    }

    final Parameterizable target = (Parameterizable) getter.eContainer();
    if (target instanceof final AbstractVertex vertex) {
      dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_TARGET, vertex.getName());

      if (target instanceof ExecutableActor) {
        dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_TARGET_PORT, getter.getName());
      }

      if (target instanceof PiGraph) {
        dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_TARGET_PORT, getter.getName());
      }
    }

    if (target instanceof final Delay d) {
      dependencyElt.setAttribute(PiIdentifiers.DEPENDENCY_TARGET, d.getActor().getName());
    }
  }

  /**
   * Create and add a node {@link Element} to the given parent {@link Element} for the given fifo and write its
   * informations.
   *
   * @param graphElt
   *          The parent element of the node element (i.e. the graph of the document)
   * @param fifo
   *          The {@link Fifo} to write in the {@link Document}
   */
  protected void writeFifos(final Element graphElt, final Fifo fifo) {
    // Add the node to the document
    final Element fifoElt = appendChild(graphElt, PiIdentifiers.EDGE);

    // Set the source and target attributes
    final AbstractActor source = (AbstractActor) fifo.getSourcePort().eContainer();
    final AbstractActor target = (AbstractActor) fifo.getTargetPort().eContainer();
    fifoElt.setAttribute(PiIdentifiers.EDGE_KIND, PiIdentifiers.FIFO);
    fifoElt.setAttribute(PiIdentifiers.FIFO_TYPE, fifo.getType());
    fifoElt.setAttribute(PiIdentifiers.FIFO_SOURCE, source.getName());
    fifoElt.setAttribute(PiIdentifiers.FIFO_TARGET, target.getName());
    fifoElt.setAttribute(PiIdentifiers.FIFO_SOURCE_PORT, fifo.getSourcePort().getName());
    fifoElt.setAttribute(PiIdentifiers.FIFO_TARGET_PORT, fifo.getTargetPort().getName());

    if (fifo.getDelay() != null) {
      writeDataElt(fifoElt, PiIdentifiers.DELAY, fifo.getDelay().getActor().getName());
      fifoElt.setAttribute(PiIdentifiers.DELAY_EXPRESSION, fifo.getDelay().getSizeExpression().getExpressionAsString());
    }
  }

  /**
   * Create the Graph Element of the document and fill it.
   *
   * @param rootElt
   *          The parent element of the Graph element (i.e. the root of the document)
   * @param graph
   *          The serialized Graph
   */
  protected void writeGraph(final Element rootElt, final PiGraph graph) {
    // Create and add the graphElt to the Document
    final Element graphElt = addGraphElt(rootElt);
    writeDataElt(graphElt, PiIdentifiers.GRAPH_NAME, graph.getName());
    final String periodExpr = graph.getPeriod().getExpressionAsString();
    if (!periodExpr.equals("0")) {
      graphElt.setAttribute(PiIdentifiers.ACTOR_PERIOD, periodExpr);
    }
    // Write cluster value into graph element
    if (graph.isCluster()) {
      graphElt.setAttribute(PiIdentifiers.CLUSTER, "true");
    }

    for (final Parameter param : graph.getParameters()) {
      writeParameter(graphElt, param);
    }

    // Write the vertices of the graph
    for (final AbstractActor actor : graph.getActors()) {
      // ignore all non executable actors
      if (actor instanceof DelayActor) {
        continue;
      }
      writeAbstractActor(graphElt, actor);
    }

    for (final Delay delay : graph.getDelays()) {
      writeDelayVertex(graphElt, delay);
    }

    // For the diagram creation, FIFO with delays need to be written / parse first
    for (final Fifo fifo : graph.getFifosWithDelay()) {
      writeFifos(graphElt, fifo);
    }
    for (final Fifo fifo : graph.getFifosWithoutDelay()) {
      writeFifos(graphElt, fifo);
    }

    for (final Dependency dependency : graph.getDependencies()) {
      writeDependency(graphElt, dependency);
    }
  }

  /**
   * Write information of the {@link InterfaceActor} in the given {@link Element}.
   *
   * @param vertexElt
   *          The {@link Element} to write
   * @param vertex
   *          The {@link InterfaceActor} to serialize
   */
  protected void writeInterfaceVertex(final Element vertexElt, final InterfaceActor vertex) {
    // Set the kind of the Actor
    vertexElt.setAttribute(PiIdentifiers.NODE_KIND, vertex.getKind().getLiteral());
    // Write ports of the actor
    switch (vertex.getKind()) {
      case DATA_INPUT:
        writePorts(vertexElt, vertex.getDataOutputPorts());
        break;
      case DATA_OUTPUT:
        writePorts(vertexElt, vertex.getDataInputPorts());
        break;
      default:
    }

  }

  /**
   * Write information of the memory script in the given {@link Element}.
   *
   * @param vertexElt
   *          The {@link Element} to write
   * @param memScriptPath
   *          The memory script path to serialize
   */
  protected void writeMemoryScript(final Element vertexElt, final IPath memScriptPath) {
    if (memScriptPath != null) {
      // The makeRelative() call ensures that the path is relative to the
      // project.
      writeDataElt(vertexElt, PiIdentifiers.ACTOR_MEMORY_SCRIPT, memScriptPath.makeRelative().toPortableString());
    }
  }

  /**
   * Create and add a node {@link Element} to the given parent {@link Element} for the given parameter and write its
   * informations.
   *
   * @param graphElt
   *          The parent element of the node element (i.e. the graph of the document)
   * @param param
   *          The {@link Parameter} to write in the {@link Document}
   */
  protected void writeParameter(final Element graphElt, final Parameter param) {
    // Add the node to the document
    final Element paramElt = appendChild(graphElt, PiIdentifiers.NODE);

    // Set the unique ID of the node (equal to the param name)
    paramElt.setAttribute(PiIdentifiers.PARAMETER_NAME, param.getName());

    // Set the kind of the node
    if (param instanceof final MoldableParameter mParam) {
      paramElt.setAttribute(PiIdentifiers.NODE_KIND, PiIdentifiers.MOLDABLE_PARAMETER);
      paramElt.setAttribute(PiIdentifiers.PARAMETER_EXPRESSION, param.getValueExpression().getExpressionAsString());
      paramElt.setAttribute(PiIdentifiers.MOLDABLE_PARAMETER_EXPRESSION, mParam.getUserExpression());
    } else if (!param.isConfigurationInterface()) {
      paramElt.setAttribute(PiIdentifiers.NODE_KIND, PiIdentifiers.PARAMETER);
      paramElt.setAttribute(PiIdentifiers.PARAMETER_EXPRESSION, param.getValueExpression().getExpressionAsString());
    } else {
      paramElt.setAttribute(PiIdentifiers.NODE_KIND, InterfaceKind.CFG_INPUT.getLiteral());
      final String value = param.getExpression().getExpressionAsString();
      if (!value.equals("0")) {
        paramElt.setAttribute(PiIdentifiers.PARAM_CII_DEFAULT, value);
      }

    }
  }

  /**
   * Fill the {@link Element} with a description of the input {@link PiGraph}.
   *
   * @param parentElt
   *          The Element to fill (could be removed later if it is always rootElt)
   * @param graph
   *          The serialized Graph
   */
  protected void writePi(final Element parentElt, final PiGraph graph) {
    // Add IBSDF Keys - Might not be needed.
    addKey("parameters", "parameters", "graph", null, null);
    addKey("variables", "variables", "graph", null, null);
    addKey("arguments", "arguments", "node", null, null);

    // Write the Graph
    writeGraph(parentElt, graph);
  }

  /**
   * Write the {@link Port} in the given {@link Element}.
   *
   * @param vertexElt
   *          the {@link Element} to write
   * @param ports
   *          the ports
   */
  protected void writePorts(final Element vertexElt, final EList<? extends Port> ports) {
    for (final Port port : ports) {
      final Element portElt = appendChild(vertexElt, PiIdentifiers.PORT);

      portElt.setAttribute(PiIdentifiers.PORT_NAME, port.getName());
      portElt.setAttribute(PiIdentifiers.PORT_KIND, port.getKind().getLiteral());

      switch (port.getKind()) {
        case DATA_INPUT, DATA_OUTPUT:
          final Expression portRateExpression = ((DataPort) port).getPortRateExpression();
          final String expressionAsString = portRateExpression.getExpressionAsString();
          portElt.setAttribute(PiIdentifiers.PORT_EXPRESSION, expressionAsString);
          break;
        case CFG_INPUT, CFG_OUTPUT:
        default:
          break;
      }
      if (port instanceof final DataPort dataPort && (dataPort.getAnnotation() != null)) {
        portElt.setAttribute(PiIdentifiers.PORT_MEMORY_ANNOTATION, dataPort.getAnnotation().toString());
      }
    }
  }

  /**
   * Write information of the {@link PiSDFRefinement} in the given {@link Element}.
   *
   * @param vertexElt
   *          The {@link Element} to write
   * @param refinement
   *          The {@link PiSDFRefinement} to serialize
   */
  protected void writeRefinement(final Element vertexElt, final Refinement refinement) {
    final IPath refinementFilePath = Optional.ofNullable(refinement.getFilePath()).map(Path::new).orElse(null);
    if (refinementFilePath != null) {

      final IPath refinementPath = getProjectRelativePathFrom(refinementFilePath);

      // The makeRelative() call ensures that the path is relative to the
      // project.
      writeDataElt(vertexElt, PiIdentifiers.REFINEMENT, refinementPath.makeRelative().toPortableString());
      if (refinement instanceof final CHeaderRefinement hrefinement) {
        // Special case of the delay that only has an INIT refinement
        if (hrefinement.getLoopPrototype() != null) {
          writeFunctionPrototype(vertexElt, hrefinement.getLoopPrototype(), PiIdentifiers.REFINEMENT_LOOP);
        }
        if (hrefinement.getInitPrototype() != null) {
          writeFunctionPrototype(vertexElt, hrefinement.getInitPrototype(), PiIdentifiers.REFINEMENT_INIT);
        }
      }
    }
  }

  /**
   * Returns an IPath without the project name (project relative IPath) if the file pointed by path is contained by the
   * same project as the file we write.
   *
   * @param path
   *          the IPath to make project relative
   * @return a project relative IPath if possible, path otherwise
   */
  private IPath getProjectRelativePathFrom(final IPath path) {
    // If the refinement file is contained in the same project than the .pi
    // we are serializing, then save a project relative path
    // Get the project
    final String platformString = this.documentURI.toPlatformString(true);
    final IFile documentFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
    final IProject documentProject = documentFile.getProject();
    // If the project name is the first segment of the refinement file path,
    // then remove it
    if (path.segment(0).equals(documentProject.getName())) {
      return path.removeFirstSegments(1);
    }
    return path;
  }

  /**
   * Write function prototype.
   *
   * @param vertexElt
   *          the vertex elt
   * @param prototype
   *          the prototype
   * @param functionName
   *          the function name
   */
  private void writeFunctionPrototype(final Element vertexElt, final FunctionPrototype prototype,
      final String functionName) {
    final Element protoElt = appendChild(vertexElt, functionName);
    protoElt.setAttribute(PiIdentifiers.REFINEMENT_FUNCTION_PROTOTYPE_NAME, prototype.getName());
    final boolean isCPPdef = prototype.isIsCPPdefinition();
    if (isCPPdef) {
      protoElt.setAttribute(PiIdentifiers.REFINEMENT_FUNCTION_PROTOTYPE_IS_CPPDEF, String.valueOf(isCPPdef));
    }
    for (final FunctionArgument p : prototype.getArguments()) {
      writeFunctionParameter(protoElt, p);
    }
  }

  /**
   * Write function parameter.
   *
   * @param prototypeElt
   *          the prototype elt
   * @param p
   *          the p
   */
  private void writeFunctionParameter(final Element prototypeElt, final FunctionArgument p) {
    final Element protoElt = appendChild(prototypeElt, PiIdentifiers.REFINEMENT_PARAMETER);
    protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_NAME, p.getName());
    protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_TYPE, p.getType());
    protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_DIRECTION, p.getDirection().toString());
    protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_IS_CONFIG, String.valueOf(p.isIsConfigurationParameter()));
    final boolean isCPPdef = p.isIsCPPdefinition();
    if (isCPPdef) {
      protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_IS_CPPDEF, String.valueOf(isCPPdef));
    }
    final boolean isPassedByRef = p.isIsPassedByReference();
    if (isPassedByRef) {
      protoElt.setAttribute(PiIdentifiers.REFINEMENT_PARAMETER_IS_PASSEDBYREF, String.valueOf(isPassedByRef));
    }
  }

  /**
   * Write information of the {@link Actor} in the given {@link Element}. The {@link AbstractActor} serialized by this
   * method is either: {@link BroadcastActor}, {@link JoinActor}, {@link ForkActor}, and {@link RoundBufferActor}.
   *
   * @param vertexElt
   *          The {@link Element} to write
   * @param actor
   *          The {@link Actor} to serialize
   */
  protected void writeSpecialActor(final Element vertexElt, final AbstractActor actor) {
    String kind = null;
    if (actor instanceof BroadcastActor) {
      kind = PiIdentifiers.BROADCAST;
    } else if (actor instanceof JoinActor) {
      kind = PiIdentifiers.JOIN;
    } else if (actor instanceof ForkActor) {
      kind = PiIdentifiers.FORK;
    } else if (actor instanceof RoundBufferActor) {
      kind = PiIdentifiers.ROUND_BUFFER;
    } else if (actor instanceof final EndActor endActor) {
      kind = PiIdentifiers.END;
      if (endActor.getInitReference() != null) {
        vertexElt.setAttribute(PiIdentifiers.INIT_END_REF, endActor.getInitReference().getName());
      }
    } else if (actor instanceof final InitActor initActor) {
      kind = PiIdentifiers.INIT;
      if (initActor.getEndReference() != null) {
        vertexElt.setAttribute(PiIdentifiers.INIT_END_REF, initActor.getEndReference().getName());
      }
    }
    vertexElt.setAttribute(PiIdentifiers.NODE_KIND, kind);

    writePorts(vertexElt, actor.getConfigInputPorts());
    writePorts(vertexElt, actor.getDataInputPorts());
    writePorts(vertexElt, actor.getDataOutputPorts());
  }
}
