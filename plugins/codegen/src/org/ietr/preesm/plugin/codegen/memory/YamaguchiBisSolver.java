package org.ietr.preesm.plugin.codegen.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This class is used to solve the Maximum-Weight Clique Problem on an
 * undirected weighted graph.
 * 
 * The algorithm implemented in this class is a modified version of the exact
 * algorithm proposed by Kazauki Yamaguchi and Sumio Masuda in <a href =
 * "http://www.ieice.org/proceedings/ITC-CSCC2008/pdf/p317_F3-1.pdf"> this paper
 * </a>.
 * 
 * The modification made consists in first calculating the vertex sequence and
 * the associated cost for all vertices of the graphs. Then, the ordering
 * algorithm will not be called for subgraphs and the cost will not be
 * re-computed.
 * 
 * Unfortunately, this approach does not improve the overall execution time of
 * the algorithm. The result is not obtained in reasonable time for RACH
 * exclusion graph.
 * 
 * @author kdesnos
 * 
 * @param <V>
 *            The vertices class
 * @param <E>
 *            The edges class
 * 
 * @deprecated Use YamaguchiSolver instead
 */
public class YamaguchiBisSolver<V extends WeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
		extends MaximumWeightCliqueSolver<V, E> {

	private ArrayList<Integer> cost;

	private ArrayList<V> orderedVertexSet;

	public YamaguchiBisSolver(SimpleGraph<V, E> graph) {
		super(graph);

		cost = new ArrayList<Integer>();
		min = -1;

	}

	/**
	 * This method:
	 * <ul>
	 * <li>orders this vertex set according to Algorithm 1 of the paper
	 * <li>return the resulting vertex set
	 * <li>compute the upper bounds a(pi<sub>i</sub>) and store them in cost
	 * </ul>
	 * 
	 * @param subgraphVertices
	 *            The vertices to order
	 * @param cost
	 *            the list in which the resulting costs will be stored (in the
	 *            order of the returned list)
	 * @return the ordered list of vertices.
	 */
	public ArrayList<V> OrderVertexSet(HashSet<V> subgraphVertices,
			ArrayList<Integer> cost) {
		// (1) let PI be the empty sequence
		ArrayList<V> orderedVertexSet = new ArrayList<V>();

		// (2) For each v € V, les a(v) <- w(v)
		HashMap<V, Integer> tempCost = new HashMap<V, Integer>();

		for (V vertex : subgraphVertices) {
			tempCost.put(vertex, vertex.getWeight());
		}

		// (3) let S <- V
		ArrayList<V> unorderedVertexSet = new ArrayList<V>();
		unorderedVertexSet.addAll(subgraphVertices);

		// (8) Halt if set(PI) = V
		// & (9) Goto (4)
		while (!unorderedVertexSet.isEmpty()) {
			// (4) Choose a vertex v' from S that minimize a(v')
			V selectedVertex = unorderedVertexSet.get(0);
			int minCost = tempCost.get(selectedVertex);
			for (V vertex : unorderedVertexSet) {
				if (tempCost.get(vertex) < minCost) {
					selectedVertex = vertex;
					minCost = tempCost.get(vertex);
				}
			}

			// (5) let S <- S - {v'}
			unorderedVertexSet.remove(selectedVertex);

			// (6) for each u€N(v) inter S, let a(u) <- a(v') + w(u)
			@SuppressWarnings("unchecked")
			HashSet<V> vertexSet = (HashSet<V>) GetN(selectedVertex).clone();
			vertexSet.retainAll(unorderedVertexSet);

			for (V vertex : vertexSet) {
				tempCost.put(vertex,
						tempCost.get(selectedVertex) + vertex.getWeight());
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

	/**
	 * This method corresponds to the algorithm 2 in <a href =
	 * "http://www.ieice.org/proceedings/ITC-CSCC2008/pdf/p317_F3-1.pdf"> this
	 * paper </a>. This method will return the vertices of the maximum-weight
	 * clique for the subgraph passed as a parameter.
	 * 
	 * @param subgraphVertices
	 *            The vertices of the subgraph to search
	 * @param thresold
	 *            The minimum weight of the clique to find
	 * @return The Maximum-Weight Clique of the subgraph (if any)
	 */
	public HashSet<V> maxWeightClique(ArrayList<V> subgraphVertices,
			int thresold) {
		// (1) let C <- 0
		HashSet<V> clique = new HashSet<V>();

		// (2) get a sequence PI and a(.)

		// (3) let i <- |V|
		// (8) let i <- i-1
		// (9) Go to (4) if i>0
		for (int i = subgraphVertices.size() - 1; i >= 0; i--) {
			V currentVertex = subgraphVertices.get(i);

			// (4) Exit if a(pi_i) <= theta
			int indexVertex = orderedVertexSet.indexOf(currentVertex);
			if (cost.get(indexVertex) <= thresold)
				break;

			// (5) Get the maximum Weight clique C' of Pi(G,PI)
			// subgraphVertices.remove(currentVertex);
			ArrayList<V> subGraph = GetSeti(indexVertex);
			subGraph.retainAll(subgraphVertices);

			// N(v)
			// @SuppressWarnings("unchecked")
			// HashSet<V> subGraph = (HashSet<V>)
			// this.GetN(currentVertex).clone();

			// N(v) inter Si
			subGraph.retainAll(GetN(currentVertex));

			// Recursive Call
			HashSet<V> subClique = maxWeightClique(subGraph, thresold
					- currentVertex.getWeight());
			subClique.add(currentVertex);
			int weightSubClique = sumWeight(subClique);

			// (6) Goto (8) if w(C') < theta
			if (weightSubClique > thresold) {
				// (7) Let C <- C' and Theta <- w(C')
				thresold = weightSubClique;
				clique = subClique;
			}
		}

		return clique;
	}

	@Override
	public void Solve() {

		HashSet<V> graphVertices = new HashSet<V>();
		graphVertices.addAll(graph.vertexSet());

		orderedVertexSet = OrderVertexSet(graphVertices, cost);

		this.heaviestClique = maxWeightClique(orderedVertexSet, min);

		max = sumWeight(heaviestClique);

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
	protected ArrayList<V> GetSeti(int i) {

		ArrayList<V> si = new ArrayList<V>();

		for (int j = 0; j <= i; j++) {
			si.add(orderedVertexSet.get(j));
		}

		return si;
	}

}
