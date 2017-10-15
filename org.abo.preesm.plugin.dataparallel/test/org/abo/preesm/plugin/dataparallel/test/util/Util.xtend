package org.abo.preesm.plugin.dataparallel.test.util

import java.util.List
import java.util.NoSuchElementException
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.abo.preesm.plugin.dataparallel.iterator.SubsetTopologicalIterator

/**
 * Utility class that holds many useful static functions
 * used for unit testing
 * 
 * @author Sudeep Kanur
 */
class Util {
	/**
	 * Provide all example SDF graph
	 * 
	 * @return List of all example SDF graphs
	 */
	public static def List<SDFGraph> provideAllGraphs() {
		return newArrayList(
			ExampleGraphs.acyclicTwoActors,
			ExampleGraphs.twoActorSelfLoop,
			ExampleGraphs.twoActorLoop,
			ExampleGraphs.semanticallyAcyclicCycle,
			ExampleGraphs.strictlyCyclic,
			ExampleGraphs.strictlyCyclicDual,
			ExampleGraphs.strictlyCyclic2,
			ExampleGraphs.mixedNetwork1,
			ExampleGraphs.mixedNetwork2,
			ExampleGraphs.nestedStrongGraph,
			ExampleGraphs.costStrongComponent
		)
	}
	
	/**
	 * Provide all example SDF graphs with context
	 * The context contains information if the relevant graph should be 
	 * executed to check branch sets
	 * 
	 * @return List of all example SDF graphs
	 */
	public static def List<ExampleGraphContext> provideAllGraphsContext() {
		return newArrayList(
			// SDFG, isBranchSetCompatible, isInstanceIndependent, isAcyclic
			new ExampleGraphContext(ExampleGraphs.acyclicTwoActors, true, true, false),
			new ExampleGraphContext(ExampleGraphs.twoActorSelfLoop, true, false, false),
			new ExampleGraphContext(ExampleGraphs.twoActorLoop, true, false, false),
			new ExampleGraphContext(ExampleGraphs.semanticallyAcyclicCycle, true, true, true),
			new ExampleGraphContext(ExampleGraphs.strictlyCyclic, true, true, false),
			new ExampleGraphContext(ExampleGraphs.strictlyCyclicDual, true, true, false),
			new ExampleGraphContext(ExampleGraphs.strictlyCyclic2, true, true, false),
			new ExampleGraphContext(ExampleGraphs.mixedNetwork1, true, true, false),
			new ExampleGraphContext(ExampleGraphs.mixedNetwork2, true, false, false),
			new ExampleGraphContext(ExampleGraphs.nestedStrongGraph, true, true, true),
			new ExampleGraphContext(ExampleGraphs.costStrongComponent, false, false, false)
		)
	}
	
	/**
	 * Return a vertex that has the name supplied
	 * 
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
	 * 
	 * @param dagGen the dag constructor object holding the main DAG
	 * @param rootName the name of the root node
	 * @returns array of names of nodes seen in the subset of the DAG
	 */
	public static def String[] getNodes(SDF2DAG dagGen, String rootName) {
		val rootNode = Util.getRootNode(dagGen.outputGraph, rootName)
		return new SubsetTopologicalIterator(dagGen, rootNode).toList.map[node | node.name]
	}
}