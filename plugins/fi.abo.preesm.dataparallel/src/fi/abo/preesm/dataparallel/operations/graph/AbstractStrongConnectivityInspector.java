/**
 * Copyright or © or Copr. Åbo Akademi University (2017),
 * IETR/INSA - Rennes (2017) :
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
 * (C) Copyright 2005-2017, by Christian Soltenborn and Contributors.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedSubgraph;

/**
 * Base implementation of the strongly connected components algorithm.
 *
 * @param <V>
 *          the graph vertex type
 * @param <E>
 *          the graph edge type
 *
 * @author Christian Soltenborn
 * @author Christian Hammer
 * @author Dimitrios Michail
 */
abstract class AbstractStrongConnectivityInspector<V, E> implements StrongConnectivityAlgorithm<V, E> {
  protected final DirectedGraph<V, E>    graph;
  protected List<Set<V>>                 stronglyConnectedSets;
  protected List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs;

  public AbstractStrongConnectivityInspector(final DirectedGraph<V, E> graph) {
    this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
  }

  @Override
  public Graph<V, E> getGraph() {
    return this.graph;
  }

  @Override
  public boolean isStronglyConnected() {
    return stronglyConnectedSets().size() == 1;
  }

  @Override
  @Deprecated
  public List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs() {
    if (this.stronglyConnectedSubgraphs == null) {
      final List<Set<V>> sets = stronglyConnectedSets();
      this.stronglyConnectedSubgraphs = new ArrayList<>(sets.size());

      for (final Set<V> set : sets) {
        this.stronglyConnectedSubgraphs.add(new DirectedSubgraph<>(this.graph, set, null));
      }
    }
    return this.stronglyConnectedSubgraphs;
  }

  @Override
  public List<Graph<V, E>> getStronglyConnectedComponents() {
    final List<Graph<V, E>> result = new ArrayList<>();
    for (final DirectedSubgraph<V, E> dsg : stronglyConnectedSubgraphs()) {
      result.add(dsg);
    }
    return result;
  }

  // @Override
  // public Graph<Graph<V, E>, DefaultEdge> getCondensation() {
  // List<Set<V>> sets = stronglyConnectedSets();
  //
  // Graph<Graph<V, E>, DefaultEdge> condensation = new SimpleDirectedGraph<>(DefaultEdge.class);
  // Map<V, Graph<V, E>> vertexToComponent = new HashMap<>();
  //
  // for (Set<V> set : sets) {
  // Graph<V, E> component = new AsSubgraph<>(graph, set, null);
  // condensation.addVertex(component);
  // for (V v : set) {
  // vertexToComponent.put(v, component);
  // }
  // }
  //
  // for (E e : graph.edgeSet()) {
  // V s = graph.getEdgeSource(e);
  // Graph<V, E> sComponent = vertexToComponent.get(s);
  //
  // V t = graph.getEdgeTarget(e);
  // Graph<V, E> tComponent = vertexToComponent.get(t);
  //
  // if (sComponent != tComponent) { // reference equal on purpose
  // condensation.addEdge(sComponent, tComponent);
  // }
  // }
  //
  // return condensation;
  // }

}
