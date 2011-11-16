package org.ietr.preesm.plugin.codegen.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This Solver is an heuristic non-exact solver for the Maximum-Weight-Clique problem. It is design to provide a clique which is not always the maximum-weight clique but whose weight is high. The main advantage of this algorithm over exact algorithms is that it runs significantly faster.
 * @author kdesnos
 *
 * 
 * @param <V>
 *            The vertices class
 * @param <E>
 *            The edges class
 */
public class HeuristicSolver<V extends WeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
		extends MaximumWeightCliqueSolver<V, E> {

	/**
	 * This subclass is used to store values associated to a vertex (in a map).
	 * The first is the weight of a vertex neighborhood, and the second is the
	 * density gain of a vertex removal. The last one is a list of vertex that
	 * were merged with this vertex.
	 * 
	 * @author kdesnos
	 * 
	 */
	public class VertexCostAndMerge {

		/**
		 * A list of vertices merged with the associated vertex.
		 */
		public ArrayList<V> mergedVertex;

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
		public VertexCostAndMerge(int weight, int nbEdges) {
			this.neighborsWeight = weight;
			this.nbEdges = nbEdges;
			this.mergedVertex = new ArrayList<V>();
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
	 * 
	 */
	public class VerticesPair {
		private V first;
		private V second;

		public VerticesPair(V first, V second) {
			this.first = first;
			this.second = second;
		}

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

		public String toString() {
			return "(" + first + ", " + second + ")";
		}
	}

	protected SimpleGraph<V, E> graphBackupCopy;

	public HeuristicSolver(SimpleGraph<V, E> graph) {
		super(graph);
	}

	/**
	 * This method returns the subset of vertex adjacent to vertex.
	 * 
	 * In order to speed-up the algorithm, the result of a getN call for a
	 * vertex vi is stored in memory. Although this will use a lot of memory,
	 * this will avoid the heavy computation induced for vertices with a lot of
	 * edges.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return the subset of vertices adjacent to vertex.
	 * 
	 * @warning <b>The returned subset must not be modified. Make a copy for
	 *          local use.</b>
	 */
	public HashSet<V> adjacentVerticesOf(V vertex) {
		// If this node was already treated
		if (adjacentVerticesBackup.containsKey(vertex))
			return adjacentVerticesBackup.get(vertex);

		super.adjacentVerticesOf(vertex);

		HashSet<V> result;
		//
		// if (graph instanceof MemoryExclusionGraph) {
		// result = (HashSet<V>) ((MemoryExclusionGraph) graph)
		// .getAdjacentVertexOf((MemoryExclusionGraphNode) vertex);
		//
		// } else {
		// // Else, treat the node
		// result = new HashSet<V>();
		//
		// // Add to result all vertices that have an edge with vertex
		// Set<E> edges = graph.edgesOf(vertex);
		// for (E edge : edges) {
		// result.add(graph.getEdgeSource(edge));
		// result.add(graph.getEdgeTarget(edge));
		// }
		//
		// // Remove vertex from result
		// result.remove(vertex);
		// }
		//
		// // Save the result.
		// adjacentVerticesBackup.put(vertex, result);

		// Correct it.
		// HashSet<V> toRemove = new HashSet<V>();
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
	 * 
	 */
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
	 */
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
				removeAllVertices(mergedVertices);
			}
		}
		return mergeList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void solve() {
		// 1 - Backup the graph and make a working copy
		graphBackupCopy = graph;
		graph = new SimpleGraph<V, E>(graphBackupCopy.getEdgeFactory());
		for (V vertex : graphBackupCopy.vertexSet()) {
			graph.addVertex((V) vertex.getClone());
		}
		for (E edge : graphBackupCopy.edgeSet()) {
			graph.addEdge(graphBackupCopy.getEdgeSource(edge),
					graphBackupCopy.getEdgeTarget(edge));
		}

		// 2 - Merge "similar" vertices of the graph
		HashSet<VerticesPair> preMergedVertex = mergeSimilarVertices(graph
				.vertexSet());

		// The maximum number of edges
		double maxEdges = (graph.vertexSet().size()
				* (graph.vertexSet().size() - 1) / 2.0);

		double graphDensity = graph.edgeSet().size() / maxEdges;

		// 3 - Initialize the costs list
		HashMap<V, VertexCostAndMerge> costsList = new HashMap<V, VertexCostAndMerge>();
		for (V vertex : graph.vertexSet()) {
			int weight;
			int nbEdges;

			nbEdges = graph.edgesOf(vertex).size();

			HashSet<V> adjacentVertices = adjacentVerticesOf(vertex);
			weight = sumWeight(adjacentVertices) + vertex.getWeight();

			costsList.put(vertex, new VertexCostAndMerge(weight, nbEdges));
		}
		// Update the merge list of the kept vertex
		for (VerticesPair pair : preMergedVertex) {
			VertexCostAndMerge updatedCost = costsList.get(pair.getFirst());
			updatedCost.mergedVertex.add(pair.getSecond());
		}

		// 4 - Iterate while the graph density is not 1.0 (while the remaining
		// vertices do not form a clique)
		while (graphDensity < 1.0) {

			Entry<V, VertexCostAndMerge> selectedEntry = costsList.entrySet()
					.iterator().next();

			// 5 - Search for the vertex to remove
			for (Entry<V, VertexCostAndMerge> entry : costsList.entrySet()) {

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

				// // If the current entry is to be selected
				// if (largerGain
				// || (equalGain && (smallerWeight || (equalWeight &&
				// smallerVertexWeight)))) {
				// selectedEntry = entry;
				// }

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

			// 6 - Remove the selected vertex, and update the costs and graph
			// density

			// Retrieve the neighbors whose costs will be impacted
			HashSet<V> updatedVertex = adjacentVerticesOf(selectedEntry
					.getKey());

			// We use the removeAll method to keep the adjacentVerticesBackup up
			// to date
			ArrayList<V> removedVertex = new ArrayList<V>();
			removedVertex.add(selectedEntry.getKey());
			removeAllVertices(removedVertex);
			costsList.remove(selectedEntry.getKey());

			for (V vertex : updatedVertex) {
				VertexCostAndMerge cost = costsList.get(vertex);
				cost.setNbEdges(cost.getNbEdges() - 1);
				cost.setNeighborsWeight(cost.getNeighborsWeight()
						- selectedEntry.getKey().getWeight());
			}

			// 7 - Check for new merges (and consequences)
			HashSet<VerticesPair> mergedVertex = mergeSimilarVertices(updatedVertex);
			for (VerticesPair pair : mergedVertex) {
				// Update the merge list of the kept vertex
				VertexCostAndMerge updatedCost = costsList.get(pair.getFirst());
				VertexCostAndMerge mergedCost = costsList.get(pair.getSecond());

				updatedCost.mergedVertex.add(pair.getSecond());
				updatedCost.mergedVertex.addAll(mergedCost.mergedVertex);

				HashSet<V> updated = adjacentVerticesOf(pair.getFirst());
				for (V vertex : updated) {
					VertexCostAndMerge cost = costsList.get(vertex);
					cost.setNbEdges(cost.getNbEdges() - 1);
				}
				costsList.remove(pair.second);
			}

			// Update graph density
			maxEdges = (graph.vertexSet().size()
					* (graph.vertexSet().size() - 1) / 2.0);
			graphDensity = (maxEdges != 0) ? graph.edgeSet().size() / maxEdges
					: 1.0;
		}

		this.max = this.sumWeight(graph.vertexSet());
		// Retrieve the vertices of the found clique.
		HashSet<V> tempHeaviestClique = new HashSet<V>();
		for (V vertex : graph.vertexSet()) {
			VertexCostAndMerge cost = costsList.get(vertex);
			tempHeaviestClique.add(vertex);
			tempHeaviestClique.addAll(cost.mergedVertex);
		}

		// replace graph with its original backup
		this.graph = this.graphBackupCopy;

		Iterator<V> iter = this.graph.vertexSet().iterator();
		while (iter.hasNext()) {
			V vertex = iter.next();
			if (tempHeaviestClique.contains(vertex)) {
				heaviestClique.add(vertex);
			}
		}
	}
}
