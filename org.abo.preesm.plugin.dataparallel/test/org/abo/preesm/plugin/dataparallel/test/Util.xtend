package org.abo.preesm.plugin.dataparallel.test

import java.util.List
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.NoSuchElementException
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator

/**
 * Utility class that holds many useful static functions
 * used for unit testing
 */
class Util {
	/**
	 * Provide all example SDF graphs in this order
	 * acyclicTwoActors
	 * twoActorSelfLoop
	 * twoActorLoop
	 * semanticallyAcyclicCycle
	 * strictlyCyclic
	 * mixedNetwork1
	 * mixedNetowrk2
	 * @return List of all example SDF graphs
	 */
	public static def List<SDFGraph> provideAllGraphs() {
		return newArrayList(
			ExampleGraphs.acyclicTwoActors,
			ExampleGraphs.twoActorSelfLoop,
			ExampleGraphs.twoActorLoop,
			ExampleGraphs.semanticallyAcyclicCycle,
			ExampleGraphs.strictlyCyclic,
			ExampleGraphs.mixedNetwork1,
			ExampleGraphs.mixedNetwork2
		)
	}
	
	/**
	 * Return a vertex that has the name supplied
	 * @param graph Search the vertex in this graph
	 * @param vertexName Name of the vertex
	 * @returns the vertex of the name vertexName
	 * @throws NoSuchElementException If the vertex with the name is not found
	 */
	public static def SDFAbstractVertex getRootNode(SDFGraph graph, String vertexName) throws NoSuchElementException {
		val vertex = graph.vertexSet.filter[node | node.name.equals(vertexName)].toList
		if(vertex === null || vertex.size == 0) {
			throw new NoSuchElementException("No vertex named " + vertexName + " found in the graph!")
		}
		return vertex.get(0)
	}
	
	/**
	 * Return the array of names of nodes seen in the subset of DAG created by given root node
	 * @param dagGen the dag constructor object holding the main DAG
	 * @param rootName the name of the root node
	 * @returns array of names of nodes seen in the subset of the DAG
	 */
	public static def String[] getNodes(DAGConstructor dagGen, String rootName) {
		val rootNode = Util.getRootNode(dagGen.outputGraph, rootName)
		return new SubsetTopologicalIterator(dagGen, rootNode).toList.map[node | node.name]
	}
}