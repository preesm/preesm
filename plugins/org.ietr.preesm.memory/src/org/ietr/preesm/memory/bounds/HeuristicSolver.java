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
package org.ietr.preesm.memory.bounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.ietr.preesm.memory.exclusiongraph.IWeightedVertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

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
public class HeuristicSolver<V extends IWeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
    extends AbstractMaximumWeightCliqueSolver<V, E> {

  /**
   * This subclass is used to store values associated to a vertex (in a map). The first is the weight of a vertex
   * neighborhood, and the second is the density gain of a vertex removal. The last one is a list of vertex that were
   * merged with this vertex.
   *
   * @author kdesnos
   *
   */
  public class VertexCost {
    /**
     * Number of edges of a vertex.
     */
    private Integer nbEdges;

    /** Sum of the weights of the neighborhood (including the vertex). */
    private Integer neighborsWeight;

    /**
     * Constructor.
     *
     * @param weight
     *          the weight of its neighbors
     * @param nbEdges
     *          the number of edges
     */
    public VertexCost(final int weight, final int nbEdges) {
      this.neighborsWeight = weight;
      this.nbEdges = nbEdges;
    }

    /**
     * Gets the nb edges.
     *
     * @return the nbEdges
     */
    public Integer getNbEdges() {
      return this.nbEdges;
    }

    /**
     * Gets the neighbors weight.
     *
     * @return the neighborsWeight
     */
    public Integer getNeighborsWeight() {
      return this.neighborsWeight;
    }

    /**
     * Sets the nb edges.
     *
     * @param nbEdges
     *          the nbEdges to set
     */
    public void setNbEdges(final Integer nbEdges) {
      this.nbEdges = nbEdges;
    }

    /**
     * Sets the neighbors weight.
     *
     * @param neighborsWeight
     *          the neighborsWeight to set
     */
    public void setNeighborsWeight(final Integer neighborsWeight) {
      this.neighborsWeight = neighborsWeight;
    }
  }

  /**
   * This SubClass is used to represent a pair of vertices.
   *
   * @author kdesnos
   * @deprecated Not used anymore in the HeuristicSolver algorithm
   */
  @Deprecated
  public class VerticesPair {

    /** The first. */
    private V first;

    /** The second. */
    private V second;

    /**
     * Instantiates a new vertices pair.
     *
     * @param first
     *          the first
     * @param second
     *          the second
     */
    public VerticesPair(final V first, final V second) {
      this.first = first;
      this.second = second;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
      if (other instanceof HeuristicSolver.VerticesPair) {
        if (other.getClass() == this.getClass()) {
          @SuppressWarnings("unchecked")
          final VerticesPair otherPair = (VerticesPair) other;
          return ((((this.first == otherPair.first)
              || ((this.first != null) && (otherPair.first != null) && this.first.equals(otherPair.first)))
              && ((this.second == otherPair.second)
                  || ((this.second != null) && (otherPair.second != null) && this.second.equals(otherPair.second))))
              || (((this.first == otherPair.second)
                  || ((this.first != null) && (otherPair.second != null) && this.first.equals(otherPair.second)))
                  && ((this.second == otherPair.first)
                      || ((this.second != null) && (otherPair.first != null) && this.second.equals(otherPair.first)))));
        }
      }

      return false;
    }

    /**
     * Gets the first.
     *
     * @return the first
     */
    public V getFirst() {
      return this.first;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public V getSecond() {
      return this.second;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int hashFirst = this.first != null ? this.first.hashCode() : 0;
      final int hashSecond = this.second != null ? this.second.hashCode() : 0;

      return ((hashFirst + hashSecond) * hashSecond) + hashFirst;
    }

    /**
     * Sets the first.
     *
     * @param first
     *          the new first
     */
    public void setFirst(final V first) {
      this.first = first;
    }

    /**
     * Sets the second.
     *
     * @param second
     *          the new second
     */
    public void setSecond(final V second) {
      this.second = second;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "(" + this.first + ", " + this.second + ")";
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

  /**
   * This method merge two "Similar" nodes A and B of the graph if:<br>
   * - Node A and node B are linked by an edge.<br>
   * - Node A and B have ALL their neighbors in common.<br>
   * The node resulting from the merge keep the name of one of the two nodes, and its weight is equal to the sum of the
   * merged nodes weight.<br>
   * <br>
   * This method also clears the adjacentverticesBackup lists.
   *
   * @return A list of merged vertices
   * @deprecated Not used anymore in the HeuristicSolver algorithm
   */
  @Deprecated
  public Set<VerticesPair> mergeSimilarVertices() {
    final ArrayList<V> vertices = new ArrayList<>(this.graph.vertexSet());

    final Set<VerticesPair> result = mergeSimilarVertices(vertices);

    // Clear all adjacent vertices backup list (as they may still contains
    // merged nodes)
    clearAdjacentVerticesBackup();

    return result;
  }

  /**
   * This method merge two "Similar" nodes A and B of the graph if:<br>
   * - Node A and node B are linked by an edge.<br>
   * - Node A and B have ALL their neighbors in common.<br>
   * The node resulting from the merge keep the name of one of the two nodes, and its weight is equal to the sum of the
   * merged nodes weight.
   *
   * @param vertices
   *          The list of vertices of the graph where similar vertices are to merge.
   *
   *          This method does NOT clear the adjacentverticesBackup lists. They might be corrupted.
   *
   * @return A list of merged vertices
   * @deprecated Not used anymore in the HeuristicSolver algorithm
   */
  @Deprecated
  public Set<VerticesPair> mergeSimilarVertices(final Collection<? extends V> vertices) {
    final Set<V> mergedVertices = new LinkedHashSet<>();
    final Set<VerticesPair> mergeList = new LinkedHashSet<>();
    final ArrayList<V> list = new ArrayList<>(vertices);

    // For each vertex, check all its neighbors
    for (final V vertex : list) {
      // If the node was already merged, skip the node
      if (!mergedVertices.contains(vertex)) {
        // Retrieve the neighbors
        final Set<V> neighbors = this.adjacentVerticesOf(vertex);
        neighbors.add(vertex);

        // For each neighbor, check if all neighbors of the two nodes
        // are in common
        for (final V neighbor : neighbors) {
          // If the neighbor is the vertex itself.. skip it !
          if (!neighbor.equals(vertex)) {
            // Retrieves the neighbors of the neighbor
            final Set<V> nextNeighbors = this.adjacentVerticesOf(neighbor);
            nextNeighbors.add(neighbor);

            // Check the desired property (cf function comments)
            if (neighbors.size() == nextNeighbors.size()) {
              if (neighbors.containsAll(nextNeighbors)) {
                // Merge
                vertex.setWeight(vertex.getWeight() + neighbor.getWeight());
                mergedVertices.add(neighbor);
                mergeList.add(new VerticesPair(vertex, neighbor));
              }
            }
          }
        }
        // Remove from the graph the merged node.
        // (The node might still exists in the adjacentVerticesBackup
        // list of the graph)
        this.graph.removeAllVertices(mergedVertices);
      }
    }
    return mergeList;
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
      int weight;
      int nbEdges;

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

        final boolean equalGain = (entry.getValue().getNbEdges().intValue() == selectedEntry.getValue().getNbEdges()
            .intValue());
        // The less edges the vertex has, the greater the density gain
        // will be
        final boolean largerGain = (entry.getValue().getNbEdges() < selectedEntry.getValue().getNbEdges());
        final boolean smallerWeight = (entry.getValue().getNeighborsWeight().intValue() < selectedEntry.getValue()
            .getNeighborsWeight().intValue());
        final boolean equalWeight = (entry.getValue().getNeighborsWeight().intValue() == selectedEntry.getValue()
            .getNeighborsWeight().intValue());
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
