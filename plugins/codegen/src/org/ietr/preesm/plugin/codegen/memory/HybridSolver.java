package org.ietr.preesm.plugin.codegen.memory;

import java.util.ArrayList;
import java.util.HashSet;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class HybridSolver<V extends WeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
	extends MaximumWeightCliqueSolver<V, E> {
	
	YamaguchiSolver<V, E> yamaSolver;
	OstergardSolver<V, E> ostSolver;

	public HybridSolver(SimpleGraph<V, E> graph) {
		super(graph);
		
		// Sub-solver creation
		yamaSolver = new YamaguchiSolver<V, E>(graph);
		ostSolver = new OstergardSolver<V, E>(graph);
		
		
	}

	@Override
	public void Solve() {
		
		// First retrieve the costs from Yamaguchi
		ArrayList<Integer> cost = new ArrayList<Integer>();
		HashSet<V> graphVertices = new HashSet<V>(graph.vertexSet());
		
		yamaSolver.OrderVertexSet(graphVertices, cost);
		
		// Pre-order Ostergard
		ostSolver.OrderVertexSet();
		
		for(int i = cost.size() -1 ;
				i>=0 && ostSolver.GetHeaviestClique().isEmpty() ;
				i--)
		{
			if(cost.get(i) < min)
				break;
			
			ostSolver.SetMin(cost.get(i));
			ostSolver.Wnew();
		}	
		
		heaviestClique = ostSolver.GetHeaviestClique();
		
		max = sumWeight(heaviestClique);
	}

}
