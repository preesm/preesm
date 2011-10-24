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
 * The algorithm implemented in this class is the exact algorithm proposed by
 * Kazauki Yamaguchi and Sumio Masuda in <a href =
 * "http://www.ieice.org/proceedings/ITC-CSCC2008/pdf/p317_F3-1.pdf"> this paper
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
public class YamaguchiSolver<V extends WeightedVertex<Integer> & Comparable<V>, E extends DefaultEdge>
		extends MaximumWeightCliqueSolver<V, E> {
	
	private HashMap<Integer,V> graphVertices; 

	/**
	 * @param graphVertices the graphVertices to set
	 */
	public void setGraphVertices(HashMap<Integer, V> graphVertices) {
		this.graphVertices = graphVertices;
	}

	/**
	 * Solver constructor
	 * 
	 * @param graph
	 *            The graph to analyze
	 */
	public YamaguchiSolver(SimpleGraph<V, E> graph) {
		super(graph);
		min = -1;
		//times = new ArrayList<Long>(Collections.nCopies(15, (long)0));
	}
	/*
	private ArrayList<Long> times;
	private long tStart;
	private long tStop;*/

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
	public HashSet<V> maxWeightClique(HashMap<Integer,V> subgraphVertices, int thresold) {
		// (1) let C <- 0
		HashSet<V> clique = new HashSet<V>();

		// (2) get a sequence PI and a(.)
		ArrayList<Integer> cost = new ArrayList<Integer>();
		ArrayList<V> orderedVertexSet = orderVertexSet(subgraphVertices, cost);		

		// (3) let i <- |V|
		// (8) let i <- i-1
		// (9) Go to (4) if i>0
		for (int i = subgraphVertices.size() - 1; i >= 0; i--) {
			// (4) Exit if a(pi_i) <= theta
			if (cost.get(i) <= thresold) {
				break;
			}

			

			// (5) Get the maximum Weight clique C' of Pi(G,PI)
			V currentVertex = orderedVertexSet.get(i);
			subgraphVertices.remove(currentVertex.getIdentifier());
		

			// Si(v)
			HashMap<Integer,V> subGraph = new HashMap<Integer,V>(subgraphVertices.size());
			
		

			// N(v) inter Si
			//subGraph.retainAll(this.adjacentVerticesOf(currentVertex));
			//HashMap<Integer,V> tempGraph = new HashMap<Integer, V>(subGraph.size());
			HashSet<V> adjacentSet = this.adjacentVerticesOf(currentVertex);
			for(V vertex : adjacentSet)
			{
				if(subgraphVertices.containsKey(vertex.getIdentifier()))
					subGraph.put(vertex.getIdentifier(), vertex);
			}

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
	public ArrayList<V> orderVertexSet(HashMap<Integer,V> subgraphVertices,
			ArrayList<Integer> cost) {
		// (1) let PI be the empty sequence
		ArrayList<V> orderedVertexSet = new ArrayList<V>();

		// (2) For each v € V, les a(v) <- w(v)
		// (3) let S <- V
		HashMap<Integer, Integer> tempCost = new HashMap<Integer, Integer>();
		HashMap<Integer,V> unorderedVertexSet = new HashMap<Integer,V>();
		for (V vertex : subgraphVertices.values()) {
			tempCost.put(vertex.getIdentifier(), vertex.getWeight());
			unorderedVertexSet.put(vertex.getIdentifier(), vertex);
		}

		// (8) Halt if set(PI) = V
		// & (9) Goto (4)
		while (!unorderedVertexSet.isEmpty()) {
			
			// (4) Choose a vertex v' from S that minimize a(v')
			V selectedVertex = unorderedVertexSet.values().iterator().next();
			int minCost = tempCost.get(selectedVertex.getIdentifier());
			for (V vertex : unorderedVertexSet.values()) {
				if (tempCost.get(vertex.getIdentifier()) < minCost) {
					selectedVertex = vertex;
					minCost = tempCost.get(vertex.getIdentifier());
				}
			}
			
			// (5) let S <- S - {v'}
			unorderedVertexSet.remove(selectedVertex.getIdentifier());

			// (6) for each u€N(v) inter S, let a(u) <- a(v') + w(u)
			HashSet<V> adjacentSet = adjacentVerticesOf(selectedVertex);
			HashSet<V> vertexSet = new HashSet<V>(adjacentSet.size());
	
			for(V vertex : adjacentSet){
				if(unorderedVertexSet.containsKey(vertex.getIdentifier())){
					vertexSet.add(vertex);					
				}
			}
			
			for (V vertex : vertexSet) {
				tempCost.put(vertex.getIdentifier(),
						tempCost.get(selectedVertex.getIdentifier()) + vertex.getWeight());
			}
			
			
			// (7) Insert v' into PI such that PI becomes increasing order
			// according to a(.)
			orderedVertexSet.add(selectedVertex);

			// save tempCost(v') in cost in the order of ordered vertex
			cost.add(tempCost.get(selectedVertex.getIdentifier()));
			tempCost.remove(selectedVertex.getIdentifier());
			
		}
		return orderedVertexSet;
	}

	@Override
	public void solve() {		
		graphVertices = new HashMap<Integer,V>();
		int index=0;
		for(V vertex : graph.vertexSet()){
			vertex.setIdentifier(index++);
			graphVertices.put(vertex.getIdentifier(), vertex);
		}
		
		this.heaviestClique = maxWeightClique(graphVertices, min);
		max = sumWeight(heaviestClique);
	}
	
	/**
	 * 
	 * @param vertex
	 * @return
	 */
	public HashSet<V> adjacentVerticesOf(V vertex){		
		// If this node was already treated
		if (adjacentVerticesBackup.containsKey(vertex)){
			return adjacentVerticesBackup.get(vertex);
		}
		
		// else		
		super.adjacentVerticesOf(vertex);
		
		for(V vert : adjacentVerticesBackup.get(vertex)){
			for(V vertin : this.graphVertices.values()){
				if(vert.equals(vertin)){
					vert.setIdentifier(vertin.getIdentifier());
					break;
				}
			}
		}
		
		return adjacentVerticesBackup.get(vertex);
		
	}
}
