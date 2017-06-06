/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.ietr.preesm.memory.bounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.ietr.preesm.memory.exclusiongraph.IWeightedVertex;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

// TODO: Auto-generated Javadoc
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
public abstract class AbstractMaximumWeightCliqueSolver<V extends IWeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge> {
  /**
   * This attribute is used by the getN function to store its results. No other method should neither access nor modify it.
   */
  protected HashMap<V, HashSet<V>> adjacentVerticesBackup;

  /** The Graph to analyze. */
  protected SimpleGraph<V, E> graph;

  /** The heaviest clique encountered running the algorithm. */
  protected HashSet<V> heaviestClique;

  /** Store the weight of the heaviestClique. */
  protected int max;

  /**
   * Store the minimum weight of the clique searched.
   */
  protected int min;

  /** Store the number of vertices of the graph. */
  protected int numberVertices;

  /**
   * Constructor of the solver.
   *
   * @param graph
   *          the graph to analyze.
   */
  public AbstractMaximumWeightCliqueSolver(final SimpleGraph<V, E> graph) {
    // Keep a reference to the graph
    this.graph = graph;
    this.numberVertices = graph.vertexSet().size();
    this.heaviestClique = new HashSet<>();
    this.adjacentVerticesBackup = new HashMap<>();
    this.min = 0;
  }

  /**
   * <p>
   * This method returns the subset of vertex adjacent to vertex.
   * </p>
   *
   * <p>
   * In order to speed-up the algorithm, the result of a getN call for a vertex vi is stored in memory. Although this will use a lot of memory, this will avoid
   * the heavy computation induced for vertices with a lot of edges.<br>
   * <b>The returned subset should not be modified as it would corrupt the backed-up copy. Make a copy for local use.</b><br>
   * </p>
   *
   * <p>
   * This method may seems to be a duplicate of method MemoryExclusionGraph.getAdjacentVertexOf(). However, the MaximumWeightClique class is designed to work
   * with other graphs than MemoryExclusionGraph and duplicating the method was thus necessary.
   * </p>
   *
   * @param vertex
   *          the vertex
   * @return the subset of vertices adjacent to vertex.
   *
   * @warning <b>The returned subset must not be modified. Make a copy for local use.</b>
   */
  public HashSet<V> adjacentVerticesOf(final V vertex) {
    // If this node was already treated
    if (this.adjacentVerticesBackup.containsKey(vertex)) {
      return this.adjacentVerticesBackup.get(vertex);
    }

    // Else, treat the node
    final HashSet<V> result = new HashSet<>();

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

  /**
   * Return the heaviest clique found.
   *
   * @return the heaviest clique found.
   */
  public Set<V> getHeaviestClique() {
    return new LinkedHashSet<V>(this.heaviestClique);
  }

  /**
   * This method is used to set the minimum weight of the clique to find.
   *
   * @param minimum
   *          the desired weight
   */
  public void setMin(final int minimum) {
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
  public int sumWeight(final Collection<V> vertexSet) {
    int result = 0;
    for (final V vertex : vertexSet) {
      result += vertex.getWeight();
    }
    return result;
  }

  /**
   * Method to clear the adjacent vertices lists. (cf. MemoryExclusionGraph.clearAdjacentVerticesBackup comments for more info.)
   */
  public void clearAdjacentVerticesBackup() {
    this.adjacentVerticesBackup = new HashMap<>();

    if (this.graph instanceof MemoryExclusionGraph) {
      ((MemoryExclusionGraph) this.graph).clearAdjacentVerticesBackup();
    }
  }

  /**
   * This method checks if a subset of vertices is a clique of the graph.
   *
   * @param subset
   *          the subset to check
   * @return true if the subset is a clique
   */
  public boolean checkClique(final Collection<? extends V> subset) {
    final ArrayList<V> vertices = new ArrayList<>(subset);
    boolean result = true;

    for (int i = 0; result && (i < (vertices.size() - 1)); i++) {
      for (int j = i + 1; result && (j < vertices.size()); j++) {
        result |= this.graph.containsEdge(vertices.get(i), vertices.get(j));
      }
    }
    return result;
  }
}
