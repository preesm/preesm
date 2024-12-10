/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2009 - 2011)
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
package org.ietr.dftools.graphiti.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.Pseudograph;

/**
 * This class is an attributed multigraph (allowing more than one connection between any two vertices). It may be
 * directed or not, as specified at creation.
 *
 * @author Jonathan Piat
 * @author Matthieu Wipliez
 *
 */
public class Graph extends AbstractObject {

  /**
   * String for the "child added" property. Set when a vertex or a port is added to a vertex.
   */
  public static final String PROPERTY_ADD = "child added";

  /**
   * String for the "hasLayout" property. This is a boolean property indicating if the graph has layout information or
   * not. If it has none, the graph should be automatically laid out.
   */
  public static final String PROPERTY_HAS_LAYOUT = "has layout";

  /**
   * String for the "child removed" property. Set when a vertex or a port is removed from a vertex.
   */
  public static final String PROPERTY_REMOVE = "child removed";

  /**
   * The configuration associated with this graph.
   */
  private final Configuration configuration;

  /** The file name. */
  private IPath fileName;

  /** The graph. */
  private AbstractBaseGraph<Vertex, Edge> concreteGraph;

  /** The vertices. */
  private final Map<String, Vertex> vertices;

  /**
   * Creates a new graph, directed or not.
   *
   * @param configuration
   *          The configuration to use with this graph.
   * @param type
   *          The graph type.
   * @param directed
   *          Specifies whether the graph should be directed or not.
   */
  public Graph(final Configuration configuration, final ObjectType type, final boolean directed) {
    super(type);

    this.configuration = configuration;
    if (directed) {
      this.concreteGraph = new DirectedPseudograph<>(Edge.class);
    } else {
      this.concreteGraph = new Pseudograph<>(Edge.class);
    }
    this.vertices = new LinkedHashMap<>();

    // set default values
    final List<Parameter> parameters = type.getParameters();
    for (final Parameter parameter : parameters) {
      setValue(parameter.getName(), parameter.getDefault());
    }

    // initially, no layout
    setValue(Graph.PROPERTY_HAS_LAYOUT, false);

    this.type = type;
  }

  /**
   * Creates a new empty graph with the same properties as the source graph but configuration and type, that are
   * overridden by the supplied parameters.
   *
   * @param graph
   *          The source graph.
   * @param configuration
   *          The configuration to use with this graph.
   * @param type
   *          The graph type.
   */
  public Graph(final Graph graph, final Configuration configuration, final ObjectType type) {
    this(configuration, type, graph.isDirected());
  }

  /**
   * Adds the edge.
   *
   * @param edge
   *          The edge to add
   * @return true, if successful
   */
  public boolean addEdge(final Edge edge) {
    final Vertex source = edge.getSource();
    final Vertex target = edge.getTarget();
    final boolean res = this.concreteGraph.addEdge(source, target, edge);
    source.firePropertyChange(Vertex.PROPERTY_SRC_VERTEX, null, source);
    target.firePropertyChange(Vertex.PROPERTY_DST_VERTEX, null, target);
    return res;
  }

  /**
   * Adds the vertex.
   *
   * @param child
   *          The vertex to add
   * @return true if the vertex is added, false if command failed
   * @see AbstractBaseGraph#addVertex(Vertex)
   */
  public boolean addVertex(final Vertex child) {
    final boolean res = this.concreteGraph.addVertex(child);
    if (res) {
      // only updates this graph and fires property change if vertex not
      // already present in the graph

      child.parent = this;

      this.vertices.put((String) child.getValue(ObjectType.PARAMETER_ID), child);

      firePropertyChange(Graph.PROPERTY_ADD, null, child);
    }
    return res;
  }

  /**
   * Changes the identifier of the given vertex.
   *
   * @param vertex
   *          A vertex.
   * @param id
   *          Its new id.
   */
  void changeVertexId(final Vertex vertex, final String id) {
    final String oldId = (String) vertex.getValue(ObjectType.PARAMETER_ID);
    if ((oldId != null) && (id != null) && !oldId.equals(id)) {
      this.vertices.remove(oldId);
      this.vertices.put(id, vertex);
    }
  }

  /**
   * Edge set.
   *
   * @return the sets the
   * @see AbstractBaseGraph#edgeSet()
   */
  public Set<Edge> edgeSet() {
    return this.concreteGraph.edgeSet();
  }

  /**
   * Finds and returns the vertex of the graph with the given vertex id.
   *
   * @param vertexId
   *          The vertex id.
   * @return The vertex with the given id, or <code>null</code> if not found.
   */
  public Vertex findVertex(final String vertexId) {
    return this.vertices.get(vertexId);
  }

  /**
   * Returns the configuration associated with this Graph.
   *
   * @return The configuration associated with this Graph.
   */
  public Configuration getConfiguration() {
    return this.configuration;
  }

  /**
   * Returns the file in which this graph is defined.
   *
   * @return the file in which this graph is defined
   */
  public IFile getFile() {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getFile(this.fileName);
  }

  /**
   * Returns the name of the file in which this graph is defined.
   *
   * @return the name of the file in which this graph is defined
   */
  public IPath getFileName() {
    return this.fileName;
  }

  /**
   * Incoming edges of.
   *
   * @param vertex
   *          the vertex
   * @return the sets the
   * @see AbstractBaseGraph#incomingEdgesOf(Vertex)
   */
  public Set<Edge> incomingEdgesOf(final Vertex vertex) {
    return this.concreteGraph.incomingEdgesOf(vertex);
  }

  /**
   * Returns true if this graph is directed.
   *
   * @return True if this graph is directed, false otherwise.
   */
  public boolean isDirected() {
    return this.concreteGraph instanceof DirectedPseudograph<?, ?>;
  }

  /**
   * Outgoing edges of.
   *
   * @param vertex
   *          the vertex
   * @return the sets the
   * @see AbstractBaseGraph#outgoingEdgesOf(Vertex)
   */
  public Set<Edge> outgoingEdgesOf(final Vertex vertex) {
    return this.concreteGraph.outgoingEdgesOf(vertex);
  }

  /**
   * Removes the edge.
   *
   * @param edge
   *          the edge
   * @return true, if successful
   * @see AbstractBaseGraph#removeEdge(Edge)
   */
  public boolean removeEdge(final Edge edge) {
    final Vertex source = edge.getSource();
    final Vertex target = edge.getTarget();
    final boolean res = this.concreteGraph.removeEdge(edge);
    source.firePropertyChange(Vertex.PROPERTY_SRC_VERTEX, source, null);
    target.firePropertyChange(Vertex.PROPERTY_DST_VERTEX, target, null);
    return res;
  }

  /**
   * Removes the vertex.
   *
   * @param child
   *          the child
   * @return true, if successful
   * @see AbstractBaseGraph#removeVertex(Graph)
   */
  public boolean removeVertex(final Vertex child) {
    final boolean res = this.concreteGraph.removeVertex(child);
    child.parent = null;

    this.vertices.remove(child.getValue(ObjectType.PARAMETER_ID));

    firePropertyChange(Graph.PROPERTY_REMOVE, null, child);
    return res;
  }

  /**
   * Sets the name of the file in which this graph is defined.
   *
   * @param fileName
   *          a file name
   */
  public void setFileName(final IPath fileName) {
    this.fileName = fileName;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.type.getName();
  }

  /**
   * Vertex set.
   *
   * @return the sets the
   * @see AbstractBaseGraph#vertexSet()
   */
  public Set<Vertex> vertexSet() {
    return this.concreteGraph.vertexSet();
  }

}
