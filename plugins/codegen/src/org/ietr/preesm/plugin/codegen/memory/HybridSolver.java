package org.ietr.preesm.plugin.codegen.memory;

import java.util.ArrayList;
import java.util.HashMap;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This Class implements a custom algorithm to solve the exact Maximum-Weight
 * Clique problem. This algorithm is inspired both by Ostergard Algorithm (cf.
 * OstergardSolver) and Yagamuchi Algorithm (cf. YamaguchiSolver).<br>
 * <br>
 * It consists in using the ordering algorithm from Yamaguchi's algorithm to
 * compute a cost sequence and using those costs as minimum weight for searches
 * with Ostergard Algorithm.
 * 
 * @see OstergardSolver
 * @see YamaguchiSolver
 * @author kdesnos
 * 
 * @param <V>
 *            The vertices class
 * @param <E>
 *            The edges class
 */
public class HybridSolver<V extends WeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
		extends MaximumWeightCliqueSolver<V, E> {
	/**
	 * An OstergardSolver instance to use the search algorithm
	 */
	private OstergardSolver<V, E> ostSolver;
	/**
	 * An YamaguchiSolver instance to use the ordering algorithm
	 */
	private YamaguchiSolver<V, E> yamaSolver;

	/**
	 * The constructor of the solver
	 * 
	 * @param graph
	 *            the graph to analyse
	 */
	public HybridSolver(SimpleGraph<V, E> graph) {
		super(graph);

		// Sub-solver creation
		yamaSolver = new YamaguchiSolver<V, E>(graph);
		ostSolver = new OstergardSolver<V, E>(graph);
	}

	@Override
	public void solve() {
		// First retrieve the costs from Yamaguchi
		ArrayList<Integer> cost = new ArrayList<Integer>();
	
		
		HashMap<Integer,V> graphVertices = new HashMap<Integer,V>();
		int index=0;
		for(V vertex : graph.vertexSet()){
			vertex.setIdentifier(index++);
			graphVertices.put(vertex.getIdentifier(), vertex);
		}
		
		
		yamaSolver.setGraphVertices(graphVertices);
		yamaSolver.orderVertexSet(graphVertices, cost);

		// Pre-order Ostergard
		ostSolver.OrderVertexSet();
		for (int i = cost.size() - 1; i >= 0
				&& ostSolver.getHeaviestClique().isEmpty(); i--) {
			if (cost.get(i) < min) {
				break;
			}			
				ostSolver.setMin(cost.get(i));
				ostSolver.wNew();

			
		}
		heaviestClique = ostSolver.getHeaviestClique();
		max = sumWeight(heaviestClique);
	}
}
