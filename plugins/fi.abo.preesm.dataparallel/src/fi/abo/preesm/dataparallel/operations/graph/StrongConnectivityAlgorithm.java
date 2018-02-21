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
 * (C) Copyright 2013-2017, by Sarah Komla-Ebri and Contributors.
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
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedSubgraph;

/**
 * A strong connectivity inspector algorithm.
 *
 * @param <V>
 *          the graph vertex type
 * @param <E>
 *          the graph edge type
 *
 * @author Sarah Komla-Ebri
 * @since September 2013
 */
public interface StrongConnectivityAlgorithm<V, E> {
  /**
   * Return the underlying graph.
   *
   * @return the underlying graph
   */
  Graph<V, E> getGraph();

  /**
   * Returns true if the graph is strongly connected, false otherwise.
   *
   * @return true if the graph is strongly connected, false otherwise
   */
  boolean isStronglyConnected();

  /**
   * Computes a {@link List} of {@link Set}s, where each set contains vertices which together form a strongly connected component within the given graph.
   *
   * @return <code>List</code> of <code>Set</code> s containing the strongly connected components
   */
  List<Set<V>> stronglyConnectedSets();

  /**
   * Computes a list of subgraphs of the given graph. Each subgraph will represent a strongly connected component and will contain all vertices of that
   * component. The subgraph will have an edge (u,v) iff u and v are contained in the strongly connected component.
   *
   * @return a list of subgraphs representing the strongly connected components
   * @deprecated In favor of {@link #getStronglyConnectedComponents()}.
   */
  @Deprecated
  List<DirectedSubgraph<V, E>> stronglyConnectedSubgraphs();

  /**
   * Computes a list of subgraphs of the given graph. Each subgraph will represent a strongly connected component and will contain all vertices of that
   * component. The subgraph will have an edge (u,v) iff u and v are contained in the strongly connected component.
   *
   * @return a list of subgraphs representing the strongly connected components
   */
  List<Graph<V, E>> getStronglyConnectedComponents();
}

// End StrongConnectivityAlgorithm.java
