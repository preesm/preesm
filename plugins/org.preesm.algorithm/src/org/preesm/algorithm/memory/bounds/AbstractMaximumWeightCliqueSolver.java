/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012)
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
package org.preesm.algorithm.memory.bounds;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.memory.exclusiongraph.IWeightedVertex;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;

/**
 * This abstract class is both a tool-box for Maximum-Weight Clique Solvers and an interface.
 *
 * @author kdesnos
 *
 * @param <V>
 *          The vertices class
 * @param <E>
 *          The edges class
 */
public abstract class AbstractMaximumWeightCliqueSolver<V extends IWeightedVertex<Long>, E extends DefaultEdge> {
  /**
   * This attribute is used by the getN function to store its results. No other method should neither access nor modify
   * it.
   */
  protected final Map<V, Set<V>> adjacentVerticesBackup = new LinkedHashMap<>();

  /** The Graph to analyze. */
  protected final SimpleGraph<V, E> graph;

  /** The heaviest clique encountered running the algorithm. */
  protected Set<V> heaviestClique;

  /** Store the weight of the heaviestClique. */
  protected long max;

  /**
   * Store the minimum weight of the clique searched.
   */
  protected long min;

  /**
   */
  protected AbstractMaximumWeightCliqueSolver(final SimpleGraph<V, E> graph) {
    // Keep a reference to the graph
    this.graph = graph;
    this.heaviestClique = new LinkedHashSet<>();
    this.min = 0;
  }

  /**
   * <p>
   * This method returns the subset of vertex adjacent to vertex.
   * </p>
   *
   * <p>
   * In order to speed-up the algorithm, the result of a getN call for a vertex vi is stored in memory. Although this
   * will use a lot of memory, this will avoid the heavy computation induced for vertices with a lot of edges.<br>
   * <b>The returned subset should not be modified as it would corrupt the backed-up copy. Make a copy for local
   * use.</b><br>
   * </p>
   *
   * <p>
   * This method may seems to be a duplicate of method MemoryExclusionGraph.getAdjacentVertexOf(). However, the
   * MaximumWeightClique class is designed to work with other graphs than MemoryExclusionGraph and duplicating the
   * method was thus necessary.
   * </p>
   *
   * @param vertex
   *          the vertex
   * @return the subset of vertices adjacent to vertex.
   *
   * @warning <b>The returned subset must not be modified. Make a copy for local use.</b>
   */
  public Set<V> adjacentVerticesOf(final V vertex) {
    // If this node was already treated
    if (this.adjacentVerticesBackup.containsKey(vertex)) {
      return this.adjacentVerticesBackup.get(vertex);
    }

    // Else, treat the node
    final Set<V> result = new LinkedHashSet<>();

    // Add to result all vertices that have an edge with vertex
    final Set<E> edges = this.graph.edgesOf(vertex);
    for (final E edge : edges) {
      result.add(this.graph.getEdgeSource(edge));
      result.add(this.graph.getEdgeTarget(edge));
    }

    // Remove vertex from result
    result.remove(vertex);

    // Save the result.
    this.adjacentVerticesBackup.put(vertex, result);
    return result;
  }

  public Set<V> getHeaviestClique() {
    return new LinkedHashSet<>(this.heaviestClique);
  }

  /**
   * This method is used to set the minimum weight of the clique to find.
   *
   * @param minimum
   *          the desired weight
   */
  public void setMin(final long minimum) {
    this.min = minimum;
  }

  /**
   * This method will be called to solve the maximum clique problem on the graph.
   */
  public abstract void solve();

  /**
   * This method computes and returns the sum of the weights of the vertices contained in the passed set of vertices.
   *
   * @param vertexSet
   *          The set of weighted vertices
   * @return The sum of the vertices weights
   */
  public long sumWeight(final Collection<V> vertexSet) {
    long result = 0;
    for (final V vertex : vertexSet) {
      result += vertex.getWeight();
    }
    return result;
  }

  /**
   * Method to clear the adjacent vertices lists. (cf. MemoryExclusionGraph.clearAdjacentVerticesBackup comments for
   * more info.)
   */
  public void clearAdjacentVerticesBackup() {
    this.adjacentVerticesBackup.clear();

    if (this.graph instanceof final MemoryExclusionGraph memExGraph) {
      memExGraph.clearAdjacentVerticesBackup();
    }
  }

}
