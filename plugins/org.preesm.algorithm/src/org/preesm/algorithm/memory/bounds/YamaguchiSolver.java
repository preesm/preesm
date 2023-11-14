/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.memory.exclusiongraph.IWeightedVertex;

/**
 * This class is used to solve the Maximum-Weight Clique Problem on an undirected weighted graph.
 *
 * <p>
 * The algorithm implemented in this class is the exact algorithm proposed by Kazauki Yamaguchi and Sumio Masuda in
 * <a href = "http://www.ieice.org/proceedings/ITC-CSCC2008/pdf/p317_F3-1.pdf"> this paper </a>.
 * </p>
 *
 *
 * @author kdesnos
 *
 * @param <V>
 *          The vertices class
 * @param <E>
 *          The edges class
 */
public class YamaguchiSolver<V extends IWeightedVertex<Long>, E extends DefaultEdge>
    extends AbstractMaximumWeightCliqueSolver<V, E> {

  private Set<V> graphVertices;

  /**
   */
  public YamaguchiSolver(final SimpleGraph<V, E> graph) {
    super(graph);
    this.min = -1;
  }

  @Override
  public Set<V> adjacentVerticesOf(final V vertex) {
    // If this node was already treated
    if (this.adjacentVerticesBackup.containsKey(vertex)) {
      return this.adjacentVerticesBackup.get(vertex);
    }

    // else
    super.adjacentVerticesOf(vertex);

    for (final V vert : this.adjacentVerticesBackup.get(vertex)) {
      for (final V vertin : this.graphVertices) {
        if (vert.equals(vertin)) {
          vert.setWeight(vertin.getWeight());
          break;
        }
      }
    }

    return this.adjacentVerticesBackup.get(vertex);

  }

  /**
   * This method corresponds to the algorithm 2 in
   * <a href = "http://www.ieice.org/proceedings/ITC-CSCC2008/pdf/p317_F3-1.pdf"> this paper </a>. This method will
   * return the vertices of the maximum-weight clique for the subgraph passed as a parameter.
   *
   * @param subgraphVertices
   *          The vertices of the subgraph to search
   * @param thresold
   *          The minimum weight of the clique to find
   * @return The Maximum-Weight Clique of the subgraph (if any)
   */
  private Set<V> maxWeightClique(final Set<V> subgraphVertices, long thresold) {
    // (1) let C <- 0
    Set<V> clique = new LinkedHashSet<>();

    // (2) get a sequence PI and a(.)
    final List<Long> cost = new ArrayList<>();
    final List<V> orderedVertexSet = orderVertexSet(subgraphVertices, cost);

    // (3) let i <- |V|
    // (8) let i <- i-1
    // (9) Go to (4) if i>0
    for (int i = subgraphVertices.size() - 1; i >= 0; i--) {
      // (4) Exit if a(pi_i) <= theta
      if (cost.get(i) <= thresold) {
        break;
      }

      // (5) Get the maximum Weight clique C' of Pi(G,PI)
      final V currentVertex = orderedVertexSet.get(i);
      subgraphVertices.remove(currentVertex);

      // Si(v)
      final Set<V> subGraph = new LinkedHashSet<>(subgraphVertices.size());

      // N(v) inter Si
      final Set<V> adjacentSet = this.adjacentVerticesOf(currentVertex);
      for (final V vertex : adjacentSet) {
        if (subgraphVertices.contains(vertex)) {
          subGraph.add(vertex);
        }
      }

      // Recursive Call
      final Set<V> subClique = maxWeightClique(subGraph, thresold - currentVertex.getWeight());
      subClique.add(currentVertex);
      final long weightSubClique = sumWeight(subClique);

      // (6) Goto (8) if w(C') < theta
      if (weightSubClique > thresold) {
        // (7) Let C <- C' and Theta <- w(C')
        thresold = weightSubClique;
        clique = subClique;
      }
    }
    return clique;
  }

  /**
   * This method:
   * <ul>
   * <li>orders this vertex set according to Algorithm 1 of the paper
   * <li>return the resulting vertex set
   * <li>compute the upper bounds a(pi<sub>i</sub>) and store them in cost
   * </ul>
   * .
   *
   * @param subgraphVertices
   *          The vertices to order
   * @param cost
   *          the list in which the resulting costs will be stored (in the order of the returned list)
   * @return the ordered list of vertices.
   */
  private List<V> orderVertexSet(final Set<V> subgraphVertices, final List<Long> cost) {
    // (1) let PI be the empty sequence
    final List<V> orderedVertexSet = new ArrayList<>();

    // (2) For each v in V, les a(v) <- w(v)
    // (3) let S <- V
    final Map<V, Long> tempCost = new LinkedHashMap<>();
    final Set<V> unorderedVertexSet = new LinkedHashSet<>();
    for (final V vertex : subgraphVertices) {
      tempCost.put(vertex, vertex.getWeight());
      unorderedVertexSet.add(vertex);
    }

    // (8) Halt if set(PI) = V
    // & (9) Goto (4)
    while (!unorderedVertexSet.isEmpty()) {

      // (4) Choose a vertex v' from S that minimize a(v')
      V selectedVertex = unorderedVertexSet.iterator().next();
      long minCost = tempCost.get(selectedVertex);
      for (final V vertex : unorderedVertexSet) {
        if (tempCost.get(vertex) < minCost) {
          selectedVertex = vertex;
          minCost = tempCost.get(vertex);
        }
      }

      // (5) let S <- S - {v'}
      unorderedVertexSet.remove(selectedVertex);

      // (6) for each u in N(v) inter S, let a(u) <- a(v') + w(u)
      final Set<V> adjacentSet = adjacentVerticesOf(selectedVertex);
      final Set<V> vertexSet = new LinkedHashSet<>(adjacentSet.size());

      adjacentSet.stream().filter(unorderedVertexSet::contains).forEach(vertexSet::add);

      for (final V vertex : vertexSet) {
        tempCost.put(vertex, tempCost.get(selectedVertex) + vertex.getWeight());
      }

      // (7) Insert v' into PI such that PI becomes increasing order
      // according to a(.)
      orderedVertexSet.add(selectedVertex);

      // save tempCost(v') in cost in the order of ordered vertex
      cost.add(tempCost.get(selectedVertex));
      tempCost.remove(selectedVertex);

    }
    return orderedVertexSet;
  }

  public void setGraphVertices(final Set<V> graphVertices) {
    this.graphVertices = graphVertices;
  }

  @Override
  public void solve() {
    this.graphVertices = new LinkedHashSet<>();

    this.graph.vertexSet().stream().forEach(v -> this.graphVertices.add(v));

    this.heaviestClique = maxWeightClique(this.graphVertices, this.min);
    this.max = sumWeight(this.heaviestClique);
  }
}
