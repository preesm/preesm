/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.dftools.algorithm.model.listenable;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.jgraph.graph.Edge;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.VertexSetListener;

/**
 * Class used to represent a listenable DAG.
 *
 * @author pthebault
 * @author kdesnos
 */
public class DAGListenableGraph extends DirectedAcyclicGraph implements ListenableGraph<DAGVertex, DAGEdge> {

  /**
   * A reuseable edge event.
   *
   * @author Barak Naveh
   * @param <V>
   *          the generic type
   * @param <E>
   *          the generic type
   * @since Aug 10, 2003
   */
  private static class FlyweightEdgeEvent<V, E> extends GraphEdgeChangeEvent<V, E> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3907207152526636089L;

    /**
     * Instantiates a new flyweight edge event.
     *
     * @param eventSource
     *          the event source
     * @param type
     *          the type
     * @param e
     *          the e
     * @see GraphEdgeChangeEvent#GraphEdgeChangeEvent(Object, int, Edge)
     */
    FlyweightEdgeEvent(final Object eventSource, final int type, final E e, V edgeSource, V edgeTarget) {
      super(eventSource, type, e, edgeSource, edgeTarget);
    }

    /**
     * Sets the edge of this event.
     *
     * @param e
     *          the edge to be set.
     */
    protected void setEdge(final E e) {
      this.edge = e;
    }

    /**
     * Set the event type of this event.
     *
     * @param type
     *          the type to be set.
     */
    protected void setType(final int type) {
      this.type = type;
    }
  }

  /**
   * A reuseable vertex event.
   *
   * @author Barak Naveh
   * @param <V>
   *          the generic type
   * @since Aug 10, 2003
   */
  private static class FlyweightVertexEvent<V> extends GraphVertexChangeEvent<V> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3257848787857585716L;

    /**
     * Instantiates a new flyweight vertex event.
     *
     * @param eventSource
     *          the event source
     * @param type
     *          the type
     * @param vertex
     *          the vertex
     * @see GraphVertexChangeEvent#GraphVertexChangeEvent(Object, int, Object)
     */
    FlyweightVertexEvent(final Object eventSource, final int type, final V vertex) {
      super(eventSource, type, vertex);
    }

    /**
     * Set the event type of this event.
     *
     * @param type
     *          type to be set.
     */
    protected void setType(final int type) {
      this.type = type;
    }

    /**
     * Sets the vertex of this event.
     *
     * @param vertex
     *          the vertex to be set.
     */
    protected void setVertex(final V vertex) {
      this.vertex = vertex;
    }
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7651455929185604666L;

  /**
   * Adds the to listener list.
   *
   * @param <L>
   *          the generic type
   * @param list
   *          the list
   * @param l
   *          the l
   */
  private static <L extends EventListener> void addToListenerList(final List<L> list, final L l) {
    if (!list.contains(l)) {
      list.add(l);
    }
  }

  /** The graph listeners. */
  private final ArrayList<GraphListener<DAGVertex, DAGEdge>> graphListeners = new ArrayList<>();

  /** The reuseable edge event. */
  private FlyweightEdgeEvent<DAGVertex, DAGEdge> reuseableEdgeEvent;

  /** The reuseable vertex event. */
  private FlyweightVertexEvent<DAGVertex> reuseableVertexEvent;

  // ~ Methods
  // ----------------------------------------------------------------

  /** The reuse events. */
  private boolean reuseEvents;

  /** The vertex set listeners. */
  private final ArrayList<VertexSetListener<DAGVertex>> vertexSetListeners = new ArrayList<>();

  /**
   * Creates a new DAGListenableGraph.
   */
  public DAGListenableGraph() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph#addEdge(org.ietr.dftools.algorithm.model.dag.DAGVertex,
   * org.ietr.dftools.algorithm.model.dag.DAGVertex)
   */
  @Override
  public DAGEdge addEdge(final DAGVertex sourceVertex, final DAGVertex targetVertex) {
    final DAGEdge e = super.addEdge(sourceVertex, targetVertex);

    if (e != null) {
      fireEdgeAdded(e);
    }

    return e;
  }

  /**
   * Adds the edge.
   *
   * @param sourceVertex
   *          the source vertex
   * @param targetVertex
   *          the target vertex
   * @param e
   *          the e
   * @return true, if successful
   * @see Graph#addEdge(Object, Object, Object)
   */
  @Override
  public boolean addEdge(final DAGVertex sourceVertex, final DAGVertex targetVertex, final DAGEdge e) {
    final boolean added = super.addEdge(sourceVertex, targetVertex, e);

    if (added) {
      fireEdgeAdded(e);
    }

    return added;
  }

  /**
   * @see ListenableGraph#addGraphListener(GraphListener)
   */
  @Override
  public void addGraphListener(final GraphListener<DAGVertex, DAGEdge> l) {
    DAGListenableGraph.addToListenerList(this.graphListeners, l);
  }

  /**
   * Adds the vertex.
   *
   * @param v
   *          the v
   * @return true, if successful
   * @see Graph#addVertex(Object)
   */
  @Override
  public boolean addVertex(final DAGVertex v) {
    final boolean modified = super.addVertex(v);

    if (modified) {
      fireVertexAdded(v);
    }

    return modified;
  }

  /**
   * Adds the vertex set listener.
   *
   * @param l
   *          the l
   * @see ListenableGraph#addVertexSetListener(VertexSetListener)
   */
  @Override
  public void addVertexSetListener(final VertexSetListener<DAGVertex> l) {
    DAGListenableGraph.addToListenerList(this.vertexSetListeners, l);
  }

  /**
   * Creates the graph edge change event.
   *
   * @param eventType
   *          the event type
   * @param edge
   *          the edge
   * @return the graph edge change event
   */
  private GraphEdgeChangeEvent<DAGVertex, DAGEdge> createGraphEdgeChangeEvent(final int eventType, final DAGEdge edge) {
    if (this.reuseEvents) {
      this.reuseableEdgeEvent.setType(eventType);
      this.reuseableEdgeEvent.setEdge(edge);
      return this.reuseableEdgeEvent;
    } else {
      return new GraphEdgeChangeEvent<>(this, eventType, edge, this.getEdgeSource(edge), this.getEdgeSource(edge));
    }
  }

  /**
   * Creates the graph vertex change event.
   *
   * @param eventType
   *          the event type
   * @param vertex
   *          the vertex
   * @return the graph vertex change event
   */
  private GraphVertexChangeEvent<DAGVertex> createGraphVertexChangeEvent(final int eventType, final DAGVertex vertex) {
    if (this.reuseEvents) {
      this.reuseableVertexEvent.setType(eventType);
      this.reuseableVertexEvent.setVertex(vertex);

      return this.reuseableVertexEvent;
    } else {
      return new GraphVertexChangeEvent<>(this, eventType, vertex);
    }
  }

  /**
   * Notify listeners that the specified edge was added.
   *
   * @param edge
   *          the edge that was added.
   */
  protected void fireEdgeAdded(final DAGEdge edge) {
    final GraphEdgeChangeEvent<DAGVertex,
        DAGEdge> e = createGraphEdgeChangeEvent(GraphEdgeChangeEvent.EDGE_ADDED, edge);

    for (int i = 0; i < this.graphListeners.size(); i++) {
      final GraphListener<DAGVertex, DAGEdge> l = this.graphListeners.get(i);

      l.edgeAdded(e);
    }
  }

  /**
   * Notify listeners that the specified edge was removed.
   *
   * @param edge
   *          the edge that was removed.
   */
  protected void fireEdgeRemoved(final DAGEdge edge) {
    final GraphEdgeChangeEvent<DAGVertex,
        DAGEdge> e = createGraphEdgeChangeEvent(GraphEdgeChangeEvent.EDGE_REMOVED, edge);

    for (int i = 0; i < this.graphListeners.size(); i++) {
      final GraphListener<DAGVertex, DAGEdge> l = this.graphListeners.get(i);

      l.edgeRemoved(e);
    }
  }

  /**
   * Notify listeners that the specified vertex was added.
   *
   * @param vertex
   *          the vertex that was added.
   */
  protected void fireVertexAdded(final DAGVertex vertex) {
    final GraphVertexChangeEvent<
        DAGVertex> e = createGraphVertexChangeEvent(GraphVertexChangeEvent.VERTEX_ADDED, vertex);

    for (int i = 0; i < this.vertexSetListeners.size(); i++) {
      final VertexSetListener<DAGVertex> l = this.vertexSetListeners.get(i);

      l.vertexAdded(e);
    }

    for (int i = 0; i < this.graphListeners.size(); i++) {
      final GraphListener<DAGVertex, DAGEdge> l = this.graphListeners.get(i);

      l.vertexAdded(e);
    }
  }

  /**
   * Notify listeners that the specified vertex was removed.
   *
   * @param vertex
   *          the vertex that was removed.
   */
  protected void fireVertexRemoved(final DAGVertex vertex) {
    final GraphVertexChangeEvent<
        DAGVertex> e = createGraphVertexChangeEvent(GraphVertexChangeEvent.VERTEX_REMOVED, vertex);

    for (int i = 0; i < this.vertexSetListeners.size(); i++) {
      final VertexSetListener<DAGVertex> l = this.vertexSetListeners.get(i);

      l.vertexRemoved(e);
    }

    for (int i = 0; i < this.graphListeners.size(); i++) {
      final GraphListener<DAGVertex, DAGEdge> l = this.graphListeners.get(i);

      l.vertexRemoved(e);
    }
  }

  /**
   * Tests whether the <code>reuseEvents</code> flag is set. If the flag is set to <code>true</code> this class will
   * reuse previously fired events and will not create a new object for each event. This option increases performance
   * but should be used with care, especially in multithreaded environment.
   *
   * @return the value of the <code>reuseEvents</code> flag.
   */
  public boolean isReuseEvents() {
    return this.reuseEvents;
  }

  /**
   * Removes the edge.
   *
   * @param e
   *          the e
   * @return true, if successful
   * @see Graph#removeEdge(Object)
   */
  @Override
  public boolean removeEdge(final DAGEdge e) {
    final boolean modified = super.removeEdge(e);

    if (modified) {
      fireEdgeRemoved(e);
    }

    return modified;
  }

  /**
   * Removes the edge.
   *
   * @param sourceVertex
   *          the source vertex
   * @param targetVertex
   *          the target vertex
   * @return the DAG edge
   * @see Graph#removeEdge(Object, Object)
   */
  @Override
  @Deprecated
  public DAGEdge removeEdge(final DAGVertex sourceVertex, final DAGVertex targetVertex) {
    final DAGEdge e = super.removeEdge(sourceVertex, targetVertex);

    if (e != null) {
      fireEdgeRemoved(e);
    }

    return e;
  }

  /**
   * Removes the graph listener.
   *
   * @param l
   *          the l
   * @see ListenableGraph#removeGraphListener(GraphListener)
   */
  @Override
  public void removeGraphListener(final GraphListener<DAGVertex, DAGEdge> l) {
    this.graphListeners.remove(l);
  }

  /**
   * Removes the vertex.
   *
   * @param v
   *          the v
   * @return true, if successful
   * @see Graph#removeVertex(Object)
   */
  @Override
  public boolean removeVertex(final DAGVertex v) {
    if (containsVertex(v)) {
      final Set<DAGEdge> touchingEdgesList = edgesOf(v);

      // copy set to avoid ConcurrentModificationException
      removeAllEdges(new ArrayList<>(touchingEdgesList));

      super.removeVertex(v); // remove the vertex itself

      fireVertexRemoved(v);

      return true;
    } else {
      return false;
    }
  }

  // ~ Inner Classes
  // ----------------------------------------------------------

  /**
   * Removes the vertex set listener.
   *
   * @param l
   *          the l
   * @see ListenableGraph#removeVertexSetListener(VertexSetListener)
   */
  @Override
  public void removeVertexSetListener(final VertexSetListener<DAGVertex> l) {
    this.vertexSetListeners.remove(l);
  }

  /**
   * If the <code>reuseEvents</code> flag is set to <code>true</code> this class will reuse previously fired events and
   * will not create a new object for each event. This option increases performance but should be used with care,
   * especially in multithreaded environment.
   *
   * @param reuseEvents
   *          whether to reuse previously fired event objects instead of creating a new event object for each event.
   */
  public void setReuseEvents(final boolean reuseEvents) {
    this.reuseEvents = reuseEvents;
  }
}
