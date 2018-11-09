/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Hervé Yviquel <hyviquel@gmail.com> (2012)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.exporter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.PropertySource;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.ArgumentSet;
import org.ietr.dftools.algorithm.model.parameters.Parameter;
import org.ietr.dftools.algorithm.model.parameters.ParameterSet;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.parameters.VariableSet;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * Class used to export a SDFGraph into a GML document.
 *
 * @author jpiat
 * @param <V>
 *          The vertex type
 * @param <E>
 *          The edge type
 */
public abstract class GMLExporter<V extends AbstractVertex<?>, E extends AbstractEdge<?, ?>> {

  /** The dom document. */
  protected Document domDocument;

  /** The path. */
  protected String path;

  /** The class key set. */
  protected Map<String, List<Key>> classKeySet;

  /** The index. */
  protected int index = 0;

  /** The root elt. */
  protected Element rootElt;

  /** The graph elt. */
  protected Element graphElt;

  /**
   * Creates a new Instance of GMLExporter.
   */
  public GMLExporter() {
    this.classKeySet = new LinkedHashMap<>();
    addKey(AbstractGraph.PARAMETERS, AbstractGraph.PARAMETERS, "graph", null, null);
    addKey(AbstractGraph.VARIABLES, AbstractGraph.VARIABLES, "graph", null, null);
    addKey(AbstractVertex.ARGUMENTS_LITERAL, AbstractVertex.ARGUMENTS_LITERAL, "node", null, null);
    DOMImplementationRegistry registry;
    DOMImplementation impl;
    try {
      registry = DOMImplementationRegistry.newInstance();
      impl = registry.getDOMImplementation("Core 3.0 XML 3.0 LS");
      this.domDocument = impl.createDocument("http://graphml.graphdrawing.org/xmlns", "graphml", null);
    } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new DFToolsAlgoException("Could not export graph", e);
    }

    this.rootElt = this.domDocument.getDocumentElement();
  }

  /**
   * Adds a key with the speicified informations.
   *
   * @param id
   *          the id
   * @param name
   *          the name
   * @param elt
   *          the elt
   * @param type
   *          the type
   * @param desc
   *          the desc
   */
  private void addKey(final String id, final String name, final String elt, final String type, final Class<?> desc) {
    final Key key = new Key(name, elt, type, desc);
    if (!this.classKeySet.containsKey(elt)) {
      final ArrayList<Key> keys = new ArrayList<>();
      this.classKeySet.put(elt, keys);
    }
    key.setId(id);
    this.classKeySet.get(elt).add(key);
  }

  /**
   * Adds the key.
   *
   * @param eltType
   *          the elt type
   * @param key
   *          the key
   */
  protected void addKey(final String eltType, final Key key) {
    key.setId(key.getName());
    if (!this.classKeySet.containsKey(eltType)) {
      this.classKeySet.put(eltType, new ArrayList<Key>());
    }
    if (!this.classKeySet.get(eltType).contains(key)) {
      this.classKeySet.get(eltType).add(key);
      final Element newElt = this.domDocument.createElement("key");
      newElt.setAttribute("for", key.getApplyTo());
      newElt.setAttribute("attr.name", key.getName());
      if (key.getType() != null) {
        newElt.setAttribute("attr.type", key.getType());
      }
      this.rootElt.insertBefore(newElt, this.graphElt);
    }
  }

  /**
   * Writes the key set in the DOM document.
   *
   * @param docELement
   *          the doc E lement
   */
  protected void addKeySet(final Element docELement) {
    for (final List<Key> keys : this.classKeySet.values()) {
      for (final Key key : keys) {
        final Element keyElt = appendChild(docELement, "key");
        keyElt.setAttribute("id", key.getId());

        keyElt.setAttribute("for", key.getApplyTo());
        keyElt.setAttribute("attr.name", key.getName());
        if (key.getType() != null) {
          keyElt.setAttribute("attr.type", key.getType());
        }
        if (key.getTypeClass() != null) {
          final Element desc = appendChild(keyElt, "desc");
          desc.setTextContent(key.getTypeClass().getName());
        }
      }
    }
  }

  /**
   * Creates a new child for the given parent Element with the name "name".
   *
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
   * Creates a GML edge.
   *
   * @param parentElement
   *          The parent element of the edge
   * @param sourceId
   *          The id of the source of the edge
   * @param targetId
   *          The id of the target of the edge
   * @return The created element
   */
  public Element createEdge(final Element parentElement, final String sourceId, final String targetId) {
    final Element edgeElt = appendChild(parentElement, "edge");
    edgeElt.setAttribute("source", sourceId);
    edgeElt.setAttribute("target", targetId);
    return edgeElt;
  }

  /**
   * Creates an edge with source port and target port.
   *
   * @param parentElement
   *          The parent element of the edge
   * @param sourceId
   *          The source id
   * @param targetId
   *          The target id
   * @param sourcePort
   *          The source port name
   * @param targetPort
   *          The target port name
   * @return The created edge
   */
  public Element createEdge(final Element parentElement, final String sourceId, final String targetId,
      final String sourcePort, final String targetPort) {
    final Element edgeElt = appendChild(parentElement, "edge");
    edgeElt.setAttribute("source", sourceId);
    edgeElt.setAttribute("sourceport", sourcePort);
    edgeElt.setAttribute("target", targetId);
    edgeElt.setAttribute("targetport", targetPort);
    return edgeElt;
  }

  /**
   * Creates a GML graph.
   *
   * @param parentElement
   *          The parent element of the graph
   * @param directed
   *          True if the graph is directed
   * @return The created element
   */
  public Element createGraph(final Element parentElement, final boolean directed) {
    final Element newElt = appendChild(parentElement, "graph");
    this.graphElt = newElt;
    if (directed) {
      newElt.setAttribute("edgedefault", "directed");
    }
    return newElt;
  }

  /**
   * Creates a GML node.
   *
   * @param parentElement
   *          The parent element of this node
   * @param id
   *          The id of the node
   * @return The created element
   */
  public Element createNode(final Element parentElement, final String id) {
    final Element vertexElt = appendChild(parentElement, "node");
    vertexElt.setAttribute("id", id);
    return vertexElt;
  }

  /**
   * Creates a GML port.
   *
   * @param parentElement
   *          The parent element of the port
   * @param name
   *          The name of the port
   * @return The created element
   */
  public Element createPort(final Element parentElement, final String name) {
    final Element newElt = appendChild(parentElement, "port");
    newElt.setAttribute("name", name);
    return newElt;
  }

  /**
   * Exports the given graph at the given path.
   *
   * @param graph
   *          The graph to export
   * @param path
   *          The path where to export the graph
   */
  public abstract void export(AbstractGraph<V, E> graph, String path);

  /**
   * Export edge.
   *
   * @param edge
   *          the edge
   * @param parentELement
   *          the parent E lement
   * @return the element
   */
  /*
   * Export an Edge in the Document
   *
   * @param edge The edge to export
   *
   * @param parentELement The DOM document parent Element
   */
  protected abstract Element exportEdge(E edge, Element parentELement);

  /**
   * Exports a Graph in the DOM document.
   *
   * @param graph
   *          The graph to export
   * @return the element
   */
  public abstract Element exportGraph(AbstractGraph<V, E> graph);

  /**
   * Export keys.
   *
   * @param source
   *          the source
   * @param forElt
   *          the for elt
   * @param parentElt
   *          the parent elt
   */
  protected void exportKeys(final PropertySource source, final String forElt, final Element parentElt) {
    for (final String key : source.getPublicProperties()) {
      if (!(key.equals("parameters") || key.equals("variables") || key.equals("arguments"))
          && source.getPropertyStringValue(key) != null) {
        final Element dataElt = appendChild(parentElt, "data");
        dataElt.setAttribute("key", key);
        dataElt.setTextContent(source.getPropertyStringValue(key));
        if (source.getPropertyBean().getValue(key) instanceof Number) {
          this.addKey(forElt, new Key(key, forElt, "int", null));
        } else {
          this.addKey(forElt, new Key(key, forElt, "string", null));
        }
      }
    }
  }

  /**
   * Exports a Vertex in the DOM document.
   *
   * @param vertex
   *          The vertex to export
   * @param parentELement
   *          The parent Element in the DOM document
   * @return the element
   */
  protected abstract Element exportNode(V vertex, Element parentELement);

  /**
   * Exports an interface.
   *
   * @param interfaceVertex
   *          The interface to export
   * @param parentELement
   *          The DOM parent Element of this Interface
   * @return the element
   */
  protected abstract Element exportPort(V interfaceVertex, Element parentELement);

  /**
   * Gives this Exporter key set.
   *
   * @return a Map containing this Exporter key set
   */
  public Map<String, List<Key>> getKeySet() {
    return this.classKeySet;
  }

  /**
   * Sets this exporter key set.
   *
   * @param keys
   *          The key set
   */
  public void setKeySet(final Map<String, List<Key>> keys) {
    this.classKeySet = keys;
  }

  /**
   * Transforms the dom to the outputStream.
   *
   * @param out
   *          The output stream to write to
   */
  public void transform(final OutputStream out) {
    final DOMImplementationLS impl = (DOMImplementationLS) this.domDocument.getImplementation();

    final LSOutput output = impl.createLSOutput();
    output.setByteStream(out);

    final LSSerializer serializer = impl.createLSSerializer();
    serializer.getDomConfig().setParameter("format-pretty-print", true);
    serializer.write(this.domDocument, output);
  }

  /**
   * Export parameters.
   *
   * @param parameters
   *          the parameters
   * @param parentELement
   *          the parent E lement
   */
  protected void exportParameters(final ParameterSet parameters, final Element parentELement) {
    final Element dataElt = appendChild(parentELement, "data");
    dataElt.setAttribute("key", "parameters");
    for (final Parameter param : parameters.values()) {
      final Element paramElt = appendChild(dataElt, "parameter");
      paramElt.setAttribute("name", param.getName());
    }
  }

  /**
   * Export arguments.
   *
   * @param arguments
   *          the arguments
   * @param parentELement
   *          the parent E lement
   */
  protected void exportArguments(final ArgumentSet arguments, final Element parentELement) {
    final Element dataElt = appendChild(parentELement, "data");
    dataElt.setAttribute("key", "arguments");
    for (final Argument arg : arguments.values()) {
      final Element argElt = appendChild(dataElt, "argument");
      argElt.setAttribute("name", arg.getName());
      argElt.setAttribute("value", arg.getValue());
    }
  }

  /**
   * Export variables.
   *
   * @param variables
   *          the variables
   * @param parentELement
   *          the parent E lement
   */
  protected void exportVariables(final VariableSet variables, final Element parentELement) {
    final Element dataElt = appendChild(parentELement, "data");
    dataElt.setAttribute("key", "variables");
    for (final Variable var : variables.values()) {
      final Element varElt = appendChild(dataElt, "variable");
      varElt.setAttribute("name", var.getName());
      varElt.setAttribute("value", var.getValue());
    }
  }

}
