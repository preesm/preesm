package org.ietr.preesm.codegen.memory;

import java.util.ArrayList;
import java.util.Random;

import org.jgrapht.graph.DefaultEdge;

/**
 * This class purpose is to generate random weighted simple graphs for testing
 * purposes.
 * 
 * @author kdesnos
 * 
 */
public class WeightedGraphGenerator {

	/**
	 * The edge density of the generated graphs.<br>
	 * Default value is 0.5
	 */
	private double edgeDensity = 0.5;

	/**
	 * The maximum weight of vertices of the generated graphs.<br>
	 * Default value is 10
	 */
	private int maximumWeight = 10;

	/**
	 * The minimum weight of vertices of the generated graphs.<br>
	 * Default value is 1
	 */
	private int minimumWeight = 1;

	/**
	 * The number of vertices of the generated graphs.<br>
	 * Default value is 100
	 */
	private int numberVertices = 100;
	

	/**
	 * Default constructor
	 */
	public WeightedGraphGenerator() {
	}
	
	public MemoryExclusionGraph generateMemoryExclusionGraph() {
		// Instantiate the graph
		MemoryExclusionGraph result = new MemoryExclusionGraph();
		
		// Create the random weight generator
		Random generator = new Random();
		
		// Create a list to store the vertices
		ArrayList<MemoryExclusionGraphNode> vertices = new ArrayList<MemoryExclusionGraphNode>(numberVertices);
		
		// Add vertices to the graph
		for(int i = numberVertices - 1; i>=0 ; i--) {
			// Generate a number between minimumWeight and maximumWeight
			int weight = generator.nextInt(maximumWeight - minimumWeight + 1) + minimumWeight;
			MemoryExclusionGraphNode vertex = new MemoryExclusionGraphNode(Integer.toString(i), Integer.toString(i), weight);
			result.addVertex(vertex);
			vertices.add(vertex);
		}
		
		// Create a list to store the edges
		ArrayList<DefaultEdge> edges = new ArrayList<DefaultEdge>();
		
		// Generate all possible edge
		for(int i = 0; i< numberVertices - 1; i++){
			for(int j = i+1; j< numberVertices; j++) {
				edges.add(result.addEdge(vertices.get(i), vertices.get(j)));				
			}
		}
		// Number of edge to remove
		int removeEdge =(int) Math.round( edges.size() * (1.0 - edgeDensity ));
		
		// Remove edges
		for(; removeEdge > 0; removeEdge--){
			int index = generator.nextInt(edges.size());
			result.removeEdge(edges.get(index));
			edges.remove(index);
		}				
		return result;
	}

	/**
	 * @return the edgeDensity
	 */
	public double getEdgeDensity() {
		return edgeDensity;
	}

	/**
	 * @return the maximumWeight
	 */
	public int getMaximumWeight() {
		return maximumWeight;
	}

	/**
	 * @return the minimumWeight
	 */
	public int getMinimumWeight() {
		return minimumWeight;
	}

	/**
	 * @return the numberVertices
	 */
	public int getNumberVertices() {
		return numberVertices;
	}

	/**
	 * @param edgeDensity
	 *            the edgeDensity to set
	 */
	public void setEdgeDensity(double edgeDensity) {
		this.edgeDensity = edgeDensity;
	}

	/**
	 * @param maximumWeight
	 *            the maximumWeight to set
	 */
	public void setMaximumWeight(int maximumWeight) {
		this.maximumWeight = maximumWeight;
	}

	/**
	 * @param minimumWeight
	 *            the minimumWeight to set
	 */
	public void setMinimumWeight(int minimumWeight) {
		this.minimumWeight = minimumWeight;
	}

	/**
	 * @param numberVertices
	 *            the numberVertices to set
	 */
	public void setNumberVertices(int numberVertices) {
		this.numberVertices = numberVertices;
	}
}
