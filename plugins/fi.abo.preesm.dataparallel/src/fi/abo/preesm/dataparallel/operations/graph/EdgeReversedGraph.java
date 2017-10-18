/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017)
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
package fi.abo.preesm.dataparallel.operations.graph;

/*
 * (C) Copyright 2006-2017, by John V Sichi and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.GraphDelegator;

/**
 * Provides an edge-reversed view g' of a directed graph g. The vertex sets for the two graphs are the same, but g' contains an edge (v2, v1) iff g contains an
 * edge (v1, v2). g' is backed by g, so changes to g are reflected in g', and vice versa.
 *
 * <p>
 * This class allows you to use a directed graph algorithm in reverse. For example, suppose you have a directed graph representing a tree, with edges from
 * parent to child, and you want to find all of the parents of a node. To do this, simply create an edge-reversed graph and pass that as input to
 * {@link org.jgrapht.traverse.DepthFirstIterator}.
 *
 * @param <V>
 *          the graph vertex type
 * @param <E>
 *          the graph edge type
 *
 * @author John V. Sichi
 * @see AsUndirectedGraph
 */
public class EdgeReversedGraph<V, E> extends GraphDelegator<V, E> implements DirectedGraph<V, E> {
  private static final long serialVersionUID = -3806030402468293063L;

  /**
   * Creates a new EdgeReversedGraph.
   *
   * @param g
   *          the base (backing) graph on which the edge-reversed view will be based.
   */
  public EdgeReversedGraph(final Graph<V, E> g) {
    super(g);
  }

  /**
   * @see Graph#getEdge(Object, Object)
   */
  @Override
  public E getEdge(final V sourceVertex, final V targetVertex) {
    return super.getEdge(targetVertex, sourceVertex);
  }

  /**
   * @see Graph#getAllEdges(Object, Object)
   */
  @Override
  public Set<E> getAllEdges(final V sourceVertex, final V targetVertex) {
    return super.getAllEdges(targetVertex, sourceVertex);
  }

  /**
   * @see Graph#addEdge(Object, Object)
   */
  @Override
  public E addEdge(final V sourceVertex, final V targetVertex) {
    return super.addEdge(targetVertex, sourceVertex);
  }

  /**
   * @see Graph#addEdge(Object, Object, Object)
   */
  @Override
  public boolean addEdge(final V sourceVertex, final V targetVertex, final E e) {
    return super.addEdge(targetVertex, sourceVertex, e);
  }

  /**
   * @see DirectedGraph#inDegreeOf(Object)
   */
  @Override
  public int inDegreeOf(final V vertex) {
    return super.outDegreeOf(vertex);
  }

  /**
   * @see DirectedGraph#outDegreeOf(Object)
   */
  @Override
  public int outDegreeOf(final V vertex) {
    return super.inDegreeOf(vertex);
  }

  /**
   * @see DirectedGraph#incomingEdgesOf(Object)
   */
  @Override
  public Set<E> incomingEdgesOf(final V vertex) {
    return super.outgoingEdgesOf(vertex);
  }

  /**
   * @see DirectedGraph#outgoingEdgesOf(Object)
   */
  @Override
  public Set<E> outgoingEdgesOf(final V vertex) {
    return super.incomingEdgesOf(vertex);
  }

  /**
   * @see Graph#removeEdge(Object, Object)
   */
  @Override
  public E removeEdge(final V sourceVertex, final V targetVertex) {
    return super.removeEdge(targetVertex, sourceVertex);
  }

  /**
   * @see Graph#getEdgeSource(Object)
   */
  @Override
  public V getEdgeSource(final E e) {
    return super.getEdgeTarget(e);
  }

  /**
   * @see Graph#getEdgeTarget(Object)
   */
  @Override
  public V getEdgeTarget(final E e) {
    return super.getEdgeSource(e);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @SuppressWarnings("null")
  @Override
  public String toString() {
    return toStringFromSets(vertexSet(), edgeSet(), (Boolean) null);
  }
}

// End EdgeReversedGraph.java
