package org.ietr.preesm.experiment.memory.bounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.experiment.memory.exclusiongraph.MemoryExclusionGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This abstract class is both a tool-box for Maximum-Weight Clique Solvers and
 * an interface.
 * 
 * @author kdesnos
 * 
 * @param <V>
 *            The vertices class
 * @param <E>
 *            The edges class
 */
public abstract class AbstractMaximumWeightCliqueSolver<V extends IWeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge> {
	/**
	 * This attribute is used by the getN function to store its results. No
	 * other method should neither access nor modify it.
	 */
	protected HashMap<V, HashSet<V>> adjacentVerticesBackup;

	/**
	 * The Graph to analyze
	 */
	protected SimpleGraph<V, E> graph;

	/**
	 * The heaviest clique encountered running the algorithm
	 */
	protected HashSet<V> heaviestClique;

	/**
	 * Store the weight of the heaviestClique
	 */
	protected int max;

	/**
	 * Store the minimum weight of the clique searched.
	 */
	protected int min;

	/**
	 * Store the number of vertices of the graph
	 */
	protected int numberVertices;

	/**
	 * Constructor of the solver
	 * 
	 * @param graph
	 *            the graph to analyze.
	 */
	public AbstractMaximumWeightCliqueSolver(SimpleGraph<V, E> graph) {
		// Keep a reference to the graph
		this.graph = graph;
		numberVertices = graph.vertexSet().size();
		heaviestClique = new HashSet<V>();
		adjacentVerticesBackup = new HashMap<V, HashSet<V>>();
		min = 0;
	}

	/**
	 * This method returns the subset of vertex adjacent to vertex.
	 * 
	 * In order to speed-up the algorithm, the result of a getN call for a
	 * vertex vi is stored in memory. Although this will use a lot of memory,
	 * this will avoid the heavy computation induced for vertices with a lot of
	 * edges.<br>
	 * <b>The returned subset should not be modified as it would corrupt the
	 * backed-up copy. Make a copy for local use.</b><br>
	 * 
	 * This method may seems to be a duplicate of method
	 * MemoryExclusionGraph.getAdjacentVertexOf(). However, the
	 * MaximumWeightClique class is designed to work with other graphs than
	 * MemoryExclusionGraph and duplicating the method was thus necessary.
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

		// Else, treat the node
		HashSet<V> result = new HashSet<V>();

		// Add to result all vertices that have an edge with vertex
		Set<E> edges = graph.edgesOf(vertex);
		for (E edge : edges) {
			result.add(graph.getEdgeSource(edge));
			result.add(graph.getEdgeTarget(edge));
		}

		// Remove vertex from result
		result.remove(vertex);

		// Save the result.
		adjacentVerticesBackup.put(vertex, result);
		return result;
	}

	/**
	 * Return the heaviest clique found.
	 * 
	 * @return the heaviest clique found.
	 */
	@SuppressWarnings("unchecked")
	public HashSet<V> getHeaviestClique() {
		return (HashSet<V>) heaviestClique.clone();
	}

	/**
	 * This method is used to set the minimum weight of the clique to find.
	 * 
	 * @param minimum
	 *            the desired weight
	 */
	public void setMin(int minimum) {
		min = minimum;
	}

	/**
	 * This method will be called to solve the maximum clique problem on the
	 * graph.
	 */
	public abstract void solve();

	/**
	 * This method computes and returns the sum of the weights of the vertices
	 * contained in the passed set of vertices.
	 * 
	 * @param vertexSet
	 *            The set of weighted vertices
	 * @return The sum of the vertices weights
	 */
	public int sumWeight(Collection<V> vertexSet) {
		int result = 0;
		for (V vertex : vertexSet) {
			result += vertex.getWeight();
		}
		return result;
	}

	/**
	 * Method to clear the adjacent vertices lists. (cf.
	 * MemoryExclusionGraph.clearAdjacentVerticesBackup comments for more info.)
	 */
	public void clearAdjacentVerticesBackup() {
		adjacentVerticesBackup = new HashMap<V, HashSet<V>>();

		if (graph instanceof MemoryExclusionGraph) {
			((MemoryExclusionGraph) graph).clearAdjacentVerticesBackup();
		}
	}

	/**
	 * This method checks if a subset of vertices is a clique of the graph.
	 * 
	 * @param subset
	 *            the subset to check
	 * @return true if the subset is a clique
	 */
	public boolean checkClique(Collection<? extends V> subset) {
		ArrayList<V> vertices = new ArrayList<V>(subset);
		boolean result = true;

		for (int i = 0; result && (i < vertices.size() - 1); i++) {
			for (int j = i + 1; result && (j < vertices.size()); j++) {
				result |= this.graph.containsEdge(vertices.get(i),
						vertices.get(j));
			}
		}
		return result;
	}
}
