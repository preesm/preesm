/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.memory.bounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.ietr.preesm.memory.exclusiongraph.IWeightedVertex;
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
public class OstergardSolver<V extends IWeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
		extends AbstractMaximumWeightCliqueSolver<V, E> {
	/**
	 * cost corresponds to the c(i) function in <a href =
	 * "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this
	 * paper </a>.
	 * 
	 * It stores the weight of the heaviest clique found for the Subset
	 * S<sub>i</sub>.
	 */
	protected ArrayList<Integer> cost;

	/**
	 * dcost corresponds to the d(i) function in <a href =
	 * "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this
	 * paper </a>.
	 * 
	 * It stores the weight of the heaviest clique found for the Subset
	 * S'<sub>i</sub>.
	 */
	protected ArrayList<Integer> dcost;

	/**
	 * This boolean is set to true if a clique with weight c(i+1) + w(vi) is
	 * found during an iteration of the wnew. This clique is maximum for Si.
	 */
	protected boolean found;

	/**
	 * This vertex set will be used as the iteration base of the algorithm.
	 */
	protected ArrayList<V> orderedVertexSet;

	/**
	 * Specify if the speed-up technique is used or not (May not be efficient
	 * for graphs with high edge density)
	 */
	final protected boolean speedup;

	/**
	 * This vertex set is the current set of fixed vertices of the algorithm.
	 */
	protected ArrayList<V> workingSet;

	/**
	 ** Initialize the MaximumWeightCliqueSolver with a graph instance.
	 * 
	 * The D(i) speedup will not be used
	 * 
	 * @param graph
	 *            the graph to analyze.
	 */
	public OstergardSolver(SimpleGraph<V, E> graph) {
		this(graph, false);
	}

	/**
	 * Initialize the MaximumWeightCliqueSolver with a graph instance
	 * 
	 * @param graph
	 *            the graph to analyze.
	 * @param speedUp
	 *            true if the computation of D(i) must be performed to speed-up
	 *            the algorithm
	 */
	public OstergardSolver(SimpleGraph<V, E> graph, boolean speedUp) {
		super(graph);
		orderedVertexSet = new ArrayList<V>(numberVertices);
		workingSet = new ArrayList<V>();

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
	 * This method returns the subset S<sub>i</sub> of the orderedVertexSet.
	 * 
	 * S<sub>i</sub> is defined as S<sub>i</sub> =
	 * {v<sub>i</sub>,v<sub>i+1</sub>, ... , v<sub>n</sub> }
	 * 
	 * @param i
	 *            the vertex index of the desired subset
	 * @return the subset S<sub>i</sub>
	 */
	protected ArrayList<V> getSi(int i) {
		ArrayList<V> si = new ArrayList<V>();
		for (; i < orderedVertexSet.size(); i++) {
			si.add(orderedVertexSet.get(i));
		}
		return si;
	}

	/**
	 * This method:
	 * <ul>
	 * <li>retrieves the vertex set from the graph
	 * <li>orders this vertex set according to Ostergard's order
	 * <li>store the resulting vertex set in the orderedVertexSet attribute
	 * </ul>
	 */
	public void orderVertexSet() {
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

	@Override
	public void solve() {
		orderVertexSet();
		wNew();
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
	public void wClique(ArrayList<V> vertexSet, int weight) {
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

			// i:=min{j|vj�U}
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
			vertexSetCopy.retainAll(adjacentVerticesOf(fixedVertex));
			// recursive call
			wClique(vertexSetCopy, weight + fixedVertex.getWeight());

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
	 * This method corresponds to the wnew function in Algorithm 1 in <a href =
	 * "http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.25.4408"> this
	 * paper </a>.
	 */
	public void wNew() {
		if (speedup) {
			/* Speed-Up 2 : Compute D(i) (i=1..n/2) */
			// Reverse the order of the list to treat it backward
			// From this point until the re-reversion, indice i = numberVertices
			// - j -1
			Collections.reverse(orderedVertexSet);
			max = min;

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
				ArrayList<V> vertexSet = getSi(j); // Get S'i
				V fixedVertex = vertexSet.get(0);
				vertexSet.retainAll(adjacentVerticesOf(fixedVertex));

				// for speed-up purpose
				found = false;

				// Add the fixed vertex to the working set
				workingSet.add(fixedVertex);
				wClique(vertexSet, fixedVertex.getWeight());

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
		max = min;

		// for i:=n downto 1 do
		for (int i = numberVertices - 1; i >= 0; i--) {
			// logger.log(Level.INFO, "Iter : " + (-i + numberVertices) + "/"
			// + numberVertices);

			// if D(i) was not calculated for this i or
			// D(i) + C(i+1) > max, compute the wclique
			if (!speedup || (dcost.get(i) == 0)
					|| (dcost.get(i) + cost.get(i + 1) > max)) {

				// wclique(Si inter N(vi), w(i))
				ArrayList<V> vertexSet = getSi(i);
				V fixedVertex = vertexSet.get(0);
				vertexSet.retainAll(adjacentVerticesOf(fixedVertex));

				// for speed-up purpose
				found = false;

				// Add the fixed vertex to the working set
				workingSet.add(fixedVertex);
				wClique(vertexSet, fixedVertex.getWeight());

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
}
