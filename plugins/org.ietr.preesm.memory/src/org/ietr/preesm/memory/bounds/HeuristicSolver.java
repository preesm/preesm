/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos

[mpelcat,jnezan,kdesnos]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.memory.bounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.ietr.preesm.memory.exclusiongraph.IWeightedVertex;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This Solver is an heuristic non-exact solver for the Maximum-Weight-Clique
 * problem. It is design to provide a clique which is not always the
 * maximum-weight clique but whose weight is high. The main advantage of this
 * algorithm over exact algorithms is that it runs significantly faster.
 * 
 * @author kdesnos
 * 
 * 
 * @param <V>
 *            The vertices class
 * @param <E>
 *            The edges class
 */
public class HeuristicSolver<V extends IWeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
		extends AbstractMaximumWeightCliqueSolver<V, E> {

	/**
	 * This subclass is used to store values associated to a vertex (in a map).
	 * The first is the weight of a vertex neighborhood, and the second is the
	 * density gain of a vertex removal. The last one is a list of vertex that
	 * were merged with this vertex.
	 * 
	 * @author kdesnos
	 * 
	 */
	public class VertexCost {
		/**
		 * Number of edges of a vertex.
		 */
		private Integer nbEdges;

		/**
		 * Sum of the weights of the neighborhood (including the vertex)
		 */
		private Integer neighborsWeight;

		/**
		 * Constructor
		 * 
		 * @param weight
		 *            the weight of its neighbors
		 * @param nbEdges
		 *            the number of edges
		 */
		public VertexCost(int weight, int nbEdges) {
			this.neighborsWeight = weight;
			this.nbEdges = nbEdges;
		}

		/**
		 * @return the nbEdges
		 */
		public Integer getNbEdges() {
			return nbEdges;
		}

		/**
		 * @return the neighborsWeight
		 */
		public Integer getNeighborsWeight() {
			return neighborsWeight;
		}

		/**
		 * @param nbEdges
		 *            the nbEdges to set
		 */
		public void setNbEdges(Integer nbEdges) {
			this.nbEdges = nbEdges;
		}

		/**
		 * @param neighborsWeight
		 *            the neighborsWeight to set
		 */
		public void setNeighborsWeight(Integer neighborsWeight) {
			this.neighborsWeight = neighborsWeight;
		}
	}

	/**
	 * This SubClass is used to represent a pair of vertices
	 * 
	 * @author kdesnos
	 * @deprecated Not used anymore in the HeuristicSolver algorithm
	 */
	@Deprecated
	public class VerticesPair {
		private V first;
		private V second;

		public VerticesPair(V first, V second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof HeuristicSolver.VerticesPair) {
				if (other.getClass() == this.getClass()) {
					@SuppressWarnings("unchecked")
					VerticesPair otherPair = (VerticesPair) other;
					return (((this.first == otherPair.first || (this.first != null
							&& otherPair.first != null && this.first
								.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
							&& otherPair.second != null && this.second
								.equals(otherPair.second)))) || ((this.first == otherPair.second || (this.first != null
							&& otherPair.second != null && this.first
								.equals(otherPair.second))) && (this.second == otherPair.first || (this.second != null
							&& otherPair.first != null && this.second
								.equals(otherPair.first)))));
				}
			}

			return false;
		}

		public V getFirst() {
			return first;
		}

		public V getSecond() {
			return second;
		}

		@Override
		public int hashCode() {
			int hashFirst = first != null ? first.hashCode() : 0;
			int hashSecond = second != null ? second.hashCode() : 0;

			return (hashFirst + hashSecond) * hashSecond + hashFirst;
		}

		public void setFirst(V first) {
			this.first = first;
		}

		public void setSecond(V second) {
			this.second = second;
		}

		@Override
		public String toString() {
			return "(" + first + ", " + second + ")";
		}
	}

	public HeuristicSolver(SimpleGraph<V, E> graph) {
		super(graph);
	}

	@Override
	public HashSet<V> adjacentVerticesOf(V vertex) {
		// If this node was already treated
		if (adjacentVerticesBackup.containsKey(vertex))
			return adjacentVerticesBackup.get(vertex);

		super.adjacentVerticesOf(vertex);

		HashSet<V> result;

		// Correct the adjacent vertices list so that it contains only
		// references to vertices from graph.vertexSet()
		HashSet<V> toAdd = new HashSet<V>();
		result = adjacentVerticesBackup.get(vertex);

		for (V vert : result) {
			for (V vertin : graph.vertexSet()) {
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
	 * The node resulting from the merge keep the name of one of the two nodes,
	 * and its weight is equal to the sum of the merged nodes weight.<br>
	 * <br>
	 * This method also clears the adjacentverticesBackup lists.
	 * 
	 * @return A list of merged vertices
	 * @deprecated Not used anymore in the HeuristicSolver algorithm
	 */
	@Deprecated
	public HashSet<VerticesPair> mergeSimilarVertices() {
		ArrayList<V> vertices = new ArrayList<V>(graph.vertexSet());

		HashSet<VerticesPair> result = mergeSimilarVertices(vertices);

		// Clear all adjacent vertices backup list (as they may still contains
		// merged nodes)
		this.clearAdjacentVerticesBackup();

		return result;
	}

	/**
	 * This method merge two "Similar" nodes A and B of the graph if:<br>
	 * - Node A and node B are linked by an edge.<br>
	 * - Node A and B have ALL their neighbors in common.<br>
	 * The node resulting from the merge keep the name of one of the two nodes,
	 * and its weight is equal to the sum of the merged nodes weight.
	 * 
	 * @param vertices
	 *            The list of vertices of the graph where similar vertices are
	 *            to merge.
	 * 
	 *            This method does NOT clear the adjacentverticesBackup lists.
	 *            They might be corrupted.
	 * 
	 * @return A list of merged vertices
	 * @deprecated Not used anymore in the HeuristicSolver algorithm
	 */
	@Deprecated
	public HashSet<VerticesPair> mergeSimilarVertices(
			Collection<? extends V> vertices) {
		HashSet<V> mergedVertices = new HashSet<V>();
		HashSet<VerticesPair> mergeList = new HashSet<VerticesPair>();
		ArrayList<V> list = new ArrayList<V>(vertices);

		// For each vertex, check all its neighbors
		for (V vertex : list) {
			// If the node was already merged, skip the node
			if (!mergedVertices.contains(vertex)) {
				// Retrieve the neighbors
				HashSet<V> neighbors = this.adjacentVerticesOf(vertex);
				neighbors.add(vertex);

				// For each neighbor, check if all neighbors of the two nodes
				// are in common
				for (V neighbor : neighbors) {
					// If the neighbor is the vertex itself.. skip it !
					if (!neighbor.equals(vertex)) {
						// Retrieves the neighbors of the neighbor
						HashSet<V> nextNeighbors = this
								.adjacentVerticesOf(neighbor);
						nextNeighbors.add(neighbor);

						// Check the desired property (cf function comments)
						if (neighbors.size() == nextNeighbors.size()) {
							if (neighbors.containsAll(nextNeighbors)) {
								// Merge
								vertex.setWeight(vertex.getWeight()
										+ neighbor.getWeight());
								mergedVertices.add(neighbor);
								mergeList
										.add(new VerticesPair(vertex, neighbor));
							}
						}
					}
				}
				// Remove from the graph the merged node.
				// (The node might still exists in the adjacentVerticesBackup
				// list of the graph)
				graph.removeAllVertices(mergedVertices);
			}
		}
		return mergeList;
	}

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
		double maxEdges = (graph.vertexSet().size()
				* (graph.vertexSet().size() - 1) / 2.0);

		double totalNbEdges = graph.edgeSet().size();
		double graphDensity = totalNbEdges / maxEdges;

		// 1 - Initialize the costs list
		HashMap<V, VertexCost> costsList = new HashMap<V, VertexCost>();
		for (V vertex : graph.vertexSet()) {
			int weight;
			int nbEdges;

			nbEdges = graph.edgesOf(vertex).size();

			HashSet<V> adjacentVertices = adjacentVerticesOf(vertex);
			weight = sumWeight(adjacentVertices) + vertex.getWeight();

			costsList.put(vertex, new VertexCost(weight, nbEdges));
		}

		// 2 - Iterate while the graph density is not 1.0 (while the remaining
		// vertices do not form a clique)
		while (graphDensity < 1.0) {

			Entry<V, VertexCost> selectedEntry = costsList.entrySet()
					.iterator().next();

			// 3 - Search for the vertex to remove
			for (Entry<V, VertexCost> entry : costsList.entrySet()) {

				boolean equalGain = (entry.getValue().getNbEdges() == selectedEntry
						.getValue().getNbEdges());
				// The less edges the vertex has, the greater the density gain
				// will be
				boolean largerGain = (entry.getValue().getNbEdges() < selectedEntry
						.getValue().getNbEdges());
				boolean smallerWeight = (entry.getValue().getNeighborsWeight() < selectedEntry
						.getValue().getNeighborsWeight());
				boolean equalWeight = (entry.getValue().getNeighborsWeight() == selectedEntry
						.getValue().getNeighborsWeight());
				boolean smallerVertexWeight = (entry.getKey().getWeight() < selectedEntry
						.getKey().getWeight());

				// Explanation :
				// The vertex with the lowest neighborhood weight is selected.
				// If two vertices have the same (lowest) neighborhood weight,
				// then the one with the lowest number of neighbor is selected
				// If they have the same number of neighbors,
				// then, the one with the smaller weight is selected.
				if (smallerWeight
						|| (equalWeight && (largerGain || (equalGain && smallerVertexWeight)))) {
					selectedEntry = entry;
				}
			}

			// 4 - Remove the selected vertex, and update the costs and graph
			// density

			// Retrieve the neighbors whose costs will be impacted
			HashSet<V> updatedVertex = adjacentVerticesOf(selectedEntry
					.getKey());
			totalNbEdges -= updatedVertex.size();

			// We keep the adjacentVerticesBackup up to date
			V removedVertex = selectedEntry.getKey();
			for (HashSet<V> backup : adjacentVerticesBackup.values()) {
				backup.remove(removedVertex);
			}

			costsList.remove(removedVertex);

			for (V vertex : updatedVertex) {
				VertexCost cost = costsList.get(vertex);
				cost.setNbEdges(cost.getNbEdges() - 1);
				cost.setNeighborsWeight(cost.getNeighborsWeight()
						- selectedEntry.getKey().getWeight());
			}

			// Update graph density
			maxEdges = (costsList.size() * (costsList.size() - 1) / 2.0);
			graphDensity = (maxEdges != 0) ? totalNbEdges / maxEdges : 1.0;
		}

		// Retrieve the vertices of the found clique.
		heaviestClique = new HashSet<V>(costsList.keySet());
		this.clearAdjacentVerticesBackup();

		// // Check check if the found clique is maximal
		// // Because of the slow implementation of both : adjacentVercicesOf()
		// and
		// // containsAll(), this code considerably slow down the algorithm
		// (x1.5)
		// // and is not used.
		// // However, it is left here in case it could be used one day !
		// //
		// HashSet<V> cliqueCandidates = new HashSet<V>(
		// adjacentVerticesOf(heaviestClique.iterator().next()));
		// cliqueCandidates.removeAll(heaviestClique);
		// for (V vertex : cliqueCandidates) {
		// if (adjacentVerticesOf(vertex).containsAll(heaviestClique)) {
		// heaviestClique.add(vertex);
		// }
		// }

		this.max = this.sumWeight(costsList.keySet());
	}
}
