package org.ietr.preesm.plugin.codegen.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This class is used to solve the Maximum-Weight Clique Problem on an
 * undirected weighted graph.
 * 
 * The algorithm implemented in this class is the exact algorithm proposed by
 * Patric R.J. Ostergard in <a href =
 * "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this paper
 * </a>.
 * 
 * 
 * @author kdesnos
 * 
 * @param <V>
 *            The vertices class
 * @param <E>
 *            The edges class
 */
public class MaximumWeightCliqueSolver<V extends WeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge> {

	/**
	 * The Graph to analyze
	 */
	private SimpleGraph<V, E> graph;

	/**
	 * This vertex set will be used as the iteration base of the algorithm.
	 */
	private ArrayList<V> orderedVertexSet;

	/**
	 * This vertex set is the current set of fixed vertices of the algorithm.
	 */
	private ArrayList<V> workingSet;

	/**
	 * The heaviest clique encountered running the algorithm
	 */
	private HashSet<V> heaviestClique;

	/**
	 * Store the weight of the heaviestClique
	 */
	private int max;

	/**
	 * Store the number of vertices of the graph
	 */
	private int numberVertices;

	/**
	 * cost corresponds to the c(i) function in <a href =
	 * "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this
	 * paper </a>.
	 * 
	 * It stores the weight of the heaviest clique found for the Subset
	 * S<sub>i</sub>.
	 */
	private ArrayList<Integer> cost;

	/**
	 * dcost corresponds to the d(i) function in <a href =
	 * "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this
	 * paper </a>.
	 * 
	 * It stores the weight of the heaviest clique found for the Subset
	 * S'<sub>i</sub>.
	 */
	private ArrayList<Integer> dcost;

	/**
	 * This boolean is set to true if a clique with weight c(i+1) + w(vi) is
	 * found during an iteration of the wnew. This clique is maximum for Si.
	 */
	private boolean found;

	/**
	 * This attribute is used by the getN function to store its results. No
	 * other method should neither access nor modify it.
	 */
	private HashMap<V, HashSet<V>> getNBackup;

	final private boolean speedup;

	/**
	 * Initialize the MaximumWeightCliqueSolver with a graph instance
	 * 
	 * @param graph
	 *            the graph to analyze.
	 * @param speedUp
	 *            true if the computation of D(i) must be performed to speed-up
	 *            the algorithm
	 */
	public MaximumWeightCliqueSolver(SimpleGraph<V, E> graph, boolean speedUp) {

		// Keep a reference to the graph
		this.graph = graph;

		numberVertices = graph.vertexSet().size();

		orderedVertexSet = new ArrayList<V>(numberVertices);

		workingSet = new ArrayList<V>();

		heaviestClique = new HashSet<V>();

		getNBackup = new HashMap<V, HashSet<V>>();

		// Initialize cost Array with 0 values. An extra 0 is added to enable
		// c(i+1) for all i=0..n-1
		cost = new ArrayList<Integer>(
				Collections.nCopies(numberVertices + 1, 0));

		// Initialize dcost Array with 0 values
		dcost = new ArrayList<Integer>(Collections.nCopies(numberVertices, 0));

		this.speedup = speedUp;

		// The constructor might be filled with graph checks in the future.
		// For example, the edge density could be computed here in order to
		// select a more efficient algorithm in case where this density is >
		// 0.8.

	}

	/**
	 ** Initialize the MaximumWeightCliqueSolver with a graph instance.
	 * 
	 * The D(i) speedup will not be used
	 * 
	 * @param graph
	 *            the graph to analyze.
	 */
	public MaximumWeightCliqueSolver(SimpleGraph<V, E> graph) {
		this(graph, false);
	}

	/**
	 * This method:
	 * <ul>
	 * <li>retrieves the vertex set from the graph
	 * <li>orders this vertex set according to Ostergard's order
	 * <li>store the resulting vertex set in the orderedVertexSet attribute
	 * </ul>
	 */
	public void OrderVertexSet() {

		// Retrieve the vertices of the graph
		ArrayList<V> unorderedSet = new ArrayList<V>(numberVertices);
		unorderedSet.addAll(graph.vertexSet());

		// Make a local shallow copy of the graph to work on
		@SuppressWarnings("unchecked")
		SimpleGraph<V, E> graphCopy = (SimpleGraph<V, E>) graph.clone();

		// First, the list is ordered in the ascending order of weights
		Collections.sort(unorderedSet);

		// Vertices are added one by one to the ordered vertex set
		while (!unorderedSet.isEmpty()) {

			// Select the vertex with the smallest weight but the largest sum of
			// weight of adjacent nodes
			Iterator<V> iter = unorderedSet.iterator();

			V selectedVertex = unorderedSet.get(0);
			int selectedWeight = unorderedSet.get(0).getWeight();
			int selectedAdjacentWeight = 0;

			while (iter.hasNext()) {

				V currentVertex = iter.next();

				// If the vertex weight is equal to the selectedWeight
				if (currentVertex.getWeight() == selectedWeight) {

					// Sum the weight of vertices adjacent to the current vertex
					// in graphCopy
					int currentAdjacentWeight = 0;
					Set<E> edges = graphCopy.edgesOf(currentVertex);
					for (E edge : edges) {
						// As we don't know if the current vertex is source or
						// target of the edge,
						// both weights are added and the currentVertex Weight
						// is substracted
						currentAdjacentWeight += graphCopy.getEdgeSource(edge)
								.getWeight();
						currentAdjacentWeight += graphCopy.getEdgeTarget(edge)
								.getWeight();
						currentAdjacentWeight -= selectedWeight;
					}

					// If the weight of adacent vertices is higher, the current
					// vertex is selected
					if (currentAdjacentWeight > selectedAdjacentWeight) {
						selectedVertex = currentVertex;
						selectedAdjacentWeight = currentAdjacentWeight;
					}
				} else {
					// Leave the loop, the current vertex is heavier than the
					// minimum weight of unsorted vertices
					break;
				}
			}

			// Add the selected vertex to the ordered set
			orderedVertexSet.add(0, selectedVertex);

			// Remove the selected vertex from the unordered Set and the graph
			// copy
			unorderedSet.remove(selectedVertex);
			graphCopy.removeVertex(selectedVertex);
		}
	}

	/**
	 * This method corresponds to the wnew function in Algorithm 1 in <a href =
	 * "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this
	 * paper </a>.
	 */
	public void Wnew() {

		// Logger logger = WorkflowLogger.getLogger();

		if (speedup) {
			/* Speed-Up 2 : Compute D(i) (i=1..n/2) */

			// Reverse the order of the list to treat it backward
			// From this point until the re-reversion, indice i = numberVertices
			// - j -1
			Collections.reverse(orderedVertexSet);

			max = 0;

			// Compute D(i) with j = n-1 .. ceil((n)/2).
			// As the order of the list was reversed, this corresponds to the
			// calculation of D(i) for i=0..floor((n-1)/2)
			for (int j = numberVertices - 1; j >= Math
					.ceil(numberVertices / 2.0); j--) {

				// logger.log(
				// Level.INFO,
				// "PreIter : "
				// + (-j + numberVertices)
				// + "/"
				// + (numberVertices - Math
				// .ceil(numberVertices / 2.0)));

				// wclique(S'i inter N(vi), w(i))
				ArrayList<V> vertexSet = GetSi(j); // Get S'i
				V fixedVertex = vertexSet.get(0);
				vertexSet.retainAll(GetN(fixedVertex));

				// for speed-up purpose
				found = false;

				// Add the fixed vertex to the working set
				workingSet.add(fixedVertex);

				Wclique(vertexSet, fixedVertex.getWeight());

				// Remove the fixedVertex from the working set before next
				// iteration
				workingSet.remove(fixedVertex);

				// C[j] := max (Ci is used here because it is global and used in
				// wclique, in reality, D(i) is calculated)
				cost.set(j, max);

				// D[i] := max
				dcost.set(numberVertices - j - 1, max);

			}

			// Clean-up cost
			cost = new ArrayList<Integer>(Collections.nCopies(
					numberVertices + 1, 0));

			// Re-reverse the list order for the algo
			Collections.reverse(orderedVertexSet);
		}

		/* End of the Speed-Up 2 */

		// Algorithm 1 (as in the paper)

		// max := 0
		max = 0;

		// for i:=n downto 1 do
		for (int i = numberVertices - 1; i >= 0; i--) {

			// logger.log(Level.INFO, "Iter : " + (-i + numberVertices) + "/"
			// + numberVertices);

			// if D(i) was not calculated for this i or
			// D(i) + C(i+1) > max, compute the wclique
			if (!speedup || (dcost.get(i) == 0)
					|| (dcost.get(i) + cost.get(i + 1) > max)) {

				// wclique(Si inter N(vi), w(i))
				ArrayList<V> vertexSet = GetSi(i);
				V fixedVertex = vertexSet.get(0);
				vertexSet.retainAll(GetN(fixedVertex));

				// for speed-up purpose
				found = false;

				// Add the fixed vertex to the working set
				workingSet.add(fixedVertex);

				Wclique(vertexSet, fixedVertex.getWeight());

				// Remove the fixedVertex from the working set before next
				// iteration
				workingSet.remove(fixedVertex);
			} else {
				// This code is reached if D(i) was calculated for the current i
				// and D(i) + C(i+1) <= max
				cost.set(i, max);

				// Then, exit the search !
				return;
			}

			// C[i] := max
			cost.set(i, max);
		}
	}

	/**
	 * This method corresponds to the wclique function in Algorithm 1 in <a href
	 * = "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this
	 * paper </a>.
	 * 
	 * @param vertexSet
	 *            a subset of vertices of the graph
	 * @param weight
	 *            of the vertices fixed so far
	 */
	public void Wclique(ArrayList<V> vertexSet, int weight) {
		// if |U| = 0 then
		if (vertexSet.isEmpty()) {
			// if weight > max then
			if (weight > max) {
				// max := weight
				max = weight;
				// New record; save it
				heaviestClique.clear();
				heaviestClique.addAll(workingSet);

				// If the new record corresponds to
				// C(i+1) + w(vi), with i the current iteration from
				// wnew, then quit this iteration
				int iplusone = 1 + orderedVertexSet.indexOf(workingSet.get(0));
				if (iplusone < numberVertices)
					found = (max == (workingSet.get(0).getWeight() + cost
							.get(iplusone)));
			}
			return;
		}

		// Compute wt(U) once for all. Weight of the vertices removed from
		// U must be substracted manually from weightVertexSet
		int weightVertexSet = sumWeight(vertexSet);

		// while U != 0 do
		while (!vertexSet.isEmpty()) {
			// if weight + wt(U) <= max then return
			if ((weight + weightVertexSet) <= max) {
				return;
			}

			// i:=min{j|vj€U}
			V fixedVertex = vertexSet.get(0);
			int i = orderedVertexSet.indexOf(fixedVertex);

			// if weight + C[i] <= max then return
			if ((weight + cost.get(i)) <= max) {
				return;
			}

			// Add the fixed vertex to the working set
			workingSet.add(fixedVertex);

			// U := U \ {vi}
			vertexSet.remove(0);

			// substract the fixedVertexWeight from weightVertexSet
			weightVertexSet -= fixedVertex.getWeight();

			// wclique(U inter N(vi), weight+w(i))
			// Copy of the vertex set to be passed to the recursive function
			// call
			@SuppressWarnings("unchecked")
			ArrayList<V> vertexSetCopy = (ArrayList<V>) vertexSet.clone();
			// Compute the intersection of vertexSet and N(i)
			vertexSetCopy.retainAll(GetN(fixedVertex));
			// recursive call
			Wclique(vertexSetCopy, weight + fixedVertex.getWeight());

			// Remove the fixedVertex from the working set before next iteration
			workingSet.remove(fixedVertex);

			// Speed-up : Quit the search if a clique with weight C(i+1) + w(i)
			// was found
			if (found == true)
				return;
		}
		return;
	}

	/**
	 * This method returns the subset S<sub>i</sub> of the orderedVertexSet.
	 * 
	 * S<sub>i</sub> is defined as S<sub>i</sub> =
	 * {v<sub>i</sub>,v<sub>i+1</sub>, ... , v<sub>n</sub> }
	 * 
	 * @param i
	 *            the vertex index of the desired subset
	 * @return the subset S<sub>i</sub>
	 */
	private ArrayList<V> GetSi(int i) {

		ArrayList<V> si = new ArrayList<V>();

		for (; i < orderedVertexSet.size(); i++) {
			si.add(orderedVertexSet.get(i));
		}

		return si;
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
	 * @return the subset of vertices adjacent to vertex
	 */
	private HashSet<V> GetN(V vertex) {

		// If this node was already treated
		if (getNBackup.containsKey(vertex))
			return getNBackup.get(vertex);

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
		getNBackup.put(vertex, result);

		return result;
	}

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

	@SuppressWarnings("unchecked")
	public HashSet<V> GetHeaviestClique() {
		return (HashSet<V>) heaviestClique.clone();
	}
}
