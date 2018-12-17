/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.preesm.algorithm.memory.bounds;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.preesm.algorithm.memory.exclusiongraph.IWeightedVertex;

// TODO: Auto-generated Javadoc
/**
 * This Solver is an heuristic non-exact solver for the Maximum-Weight-Clique problem. It is design to provide a clique
 * which is not always the maximum-weight clique but whose weight is high. The main advantage of this algorithm over
 * exact algorithms is that it runs significantly faster.
 *
 * @author kdesnos
 *
 *
 * @param <V>
 *          The vertices class
 * @param <E>
 *          The edges class
 */
public class HeuristicSolver<V extends IWeightedVertex<Long> & Comparable<V>, E extends DefaultEdge>
    extends AbstractMaximumWeightCliqueSolver<V, E> {

  /**
   * This subclass is used to store values associated to a vertex (in a map). The first is the weight of a vertex
   * neighborhood, and the second is the density gain of a vertex removal. The last one is a list of vertex that were
   * merged with this vertex.
   *
   * @author kdesnos
   *
   */
  private class VertexCost {
    /**
     * Number of edges of a vertex.
     */
    private long nbEdges;

    /** Sum of the weights of the neighborhood (including the vertex). */
    private long neighborsWeight;

    /**
     * Constructor.
     *
     * @param weight
     *          the weight of its neighbors
     * @param nbEdges
     *          the number of edges
     */
    private VertexCost(final long weight, final long nbEdges) {
      this.neighborsWeight = weight;
      this.nbEdges = nbEdges;
    }

    /**
     * Gets the nb edges.
     *
     * @return the nbEdges
     */
    private long getNbEdges() {
      return this.nbEdges;
    }

    /**
     * Gets the neighbors weight.
     *
     * @return the neighborsWeight
     */
    public long getNeighborsWeight() {
      return this.neighborsWeight;
    }

    /**
     * Sets the nb edges.
     *
     * @param nbEdges
     *          the nbEdges to set
     */
    public void setNbEdges(final long nbEdges) {
      this.nbEdges = nbEdges;
    }

    /**
     * Sets the neighbors weight.
     *
     * @param neighborsWeight
     *          the neighborsWeight to set
     */
    public void setNeighborsWeight(final long neighborsWeight) {
      this.neighborsWeight = neighborsWeight;
    }
  }

  /**
   * Instantiates a new heuristic solver.
   *
   * @param graph
   *          the graph
   */
  public HeuristicSolver(final SimpleGraph<V, E> graph) {
    super(graph);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.memory.bounds.AbstractMaximumWeightCliqueSolver#adjacentVerticesOf(org.ietr.preesm.memory.
   * exclusiongraph.IWeightedVertex)
   */
  @Override
  public Set<V> adjacentVerticesOf(final V vertex) {
    // If this node was already treated
    if (this.adjacentVerticesBackup.containsKey(vertex)) {
      return this.adjacentVerticesBackup.get(vertex);
    }

    super.adjacentVerticesOf(vertex);

    Set<V> result;

    // Correct the adjacent vertices list so that it contains only
    // references to vertices from graph.vertexSet()
    final Set<V> toAdd = new LinkedHashSet<>();
    result = this.adjacentVerticesBackup.get(vertex);

    for (final V vert : result) {
      for (final V vertin : this.graph.vertexSet()) {
        if (vert.equals(vertin)) {
          // toRemove.add(vert);
          toAdd.add(vertin);
          break;
        }
      }
    }

    result.clear();
    result.addAll(toAdd);

    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.memory.bounds.AbstractMaximumWeightCliqueSolver#solve()
   */
  @Override
  public void solve() {

    // old 2 - Merge "similar" vertices of the graph
    // This step was removed as it did not improve the performances of the
    // algorithm
    // nor its efficiency to find a larger clique.
    // Moreover, because of this step, e copy of the exclusion graph must be
    // made, which is no longer the case if we delete it.
    //
    // Checkout SVN version 1227 to get the code that includes this step and
    // its consequences.

    // The maximum number of edges
    double maxEdges = ((this.graph.vertexSet().size() * (this.graph.vertexSet().size() - 1)) / 2.0);

    double totalNbEdges = this.graph.edgeSet().size();
    double graphDensity = totalNbEdges / maxEdges;

    // 1 - Initialize the costs list
    final Map<V, VertexCost> costsList = new LinkedHashMap<>();
    for (final V vertex : this.graph.vertexSet()) {
      long weight;
      long nbEdges;

      nbEdges = this.graph.edgesOf(vertex).size();

      final Set<V> adjacentVertices = adjacentVerticesOf(vertex);
      weight = sumWeight(adjacentVertices) + vertex.getWeight();

      costsList.put(vertex, new VertexCost(weight, nbEdges));
    }

    // 2 - Iterate while the graph density is not 1.0 (while the remaining
    // vertices do not form a clique)
    while (graphDensity < 1.0) {

      Entry<V, VertexCost> selectedEntry = costsList.entrySet().iterator().next();

      // 3 - Search for the vertex to remove
      for (final Entry<V, VertexCost> entry : costsList.entrySet()) {

        final boolean equalGain = (entry.getValue().getNbEdges() == selectedEntry.getValue().getNbEdges());
        // The less edges the vertex has, the greater the density gain
        // will be
        final boolean largerGain = (entry.getValue().getNbEdges() < selectedEntry.getValue().getNbEdges());
        final boolean smallerWeight = (entry.getValue().getNeighborsWeight() < selectedEntry.getValue()
            .getNeighborsWeight());
        final boolean equalWeight = (entry.getValue().getNeighborsWeight() == selectedEntry.getValue()
            .getNeighborsWeight());
        final boolean smallerVertexWeight = (entry.getKey().getWeight().intValue() < selectedEntry.getKey().getWeight()
            .intValue());

        // Explanation :
        // The vertex with the lowest neighborhood weight is selected.
        // If two vertices have the same (lowest) neighborhood weight,
        // then the one with the lowest number of neighbor is selected
        // If they have the same number of neighbors,
        // then, the one with the smaller weight is selected.
        if (smallerWeight || (equalWeight && (largerGain || (equalGain && smallerVertexWeight)))) {
          selectedEntry = entry;
        }
      }

      // 4 - Remove the selected vertex, and update the costs and graph
      // density

      // Retrieve the neighbors whose costs will be impacted
      final Set<V> updatedVertex = adjacentVerticesOf(selectedEntry.getKey());
      totalNbEdges -= updatedVertex.size();

      // We keep the adjacentVerticesBackup up to date
      final V removedVertex = selectedEntry.getKey();
      for (final Set<V> backup : this.adjacentVerticesBackup.values()) {
        backup.remove(removedVertex);
      }

      costsList.remove(removedVertex);

      for (final V vertex : updatedVertex) {
        final VertexCost cost = costsList.get(vertex);
        cost.setNbEdges(cost.getNbEdges() - 1);
        cost.setNeighborsWeight(cost.getNeighborsWeight() - selectedEntry.getKey().getWeight());
      }

      // Update graph density
      maxEdges = ((costsList.size() * (costsList.size() - 1)) / 2.0);
      graphDensity = (maxEdges != 0) ? totalNbEdges / maxEdges : 1.0;
    }

    // Retrieve the vertices of the found clique.
    this.heaviestClique = new LinkedHashSet<>(costsList.keySet());
    clearAdjacentVerticesBackup();

    // // Check check if the found clique is maximal
    // // Because of the slow implementation of both : adjacentVercicesOf()
    // and
    // // containsAll(), this code considerably slow down the algorithm
    // (x1.5)
    // // and is not used.
    // // However, it is left here in case it could be used one day !
    // //
    // LinkedHashSet<V> cliqueCandidates = new LinkedHashSet<V>(
    // adjacentVerticesOf(heaviestClique.iterator().next()));
    // cliqueCandidates.removeAll(heaviestClique);
    // for (V vertex : cliqueCandidates) {
    // if (adjacentVerticesOf(vertex).containsAll(heaviestClique)) {
    // heaviestClique.add(vertex);
    // }
    // }

    this.max = sumWeight(costsList.keySet());
  }
}
