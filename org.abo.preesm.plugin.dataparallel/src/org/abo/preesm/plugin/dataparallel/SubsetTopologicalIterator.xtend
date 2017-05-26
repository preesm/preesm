package org.abo.preesm.plugin.dataparallel

import java.util.List
import java.util.Map
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.traverse.BreadthFirstIterator
import java.util.NoSuchElementException
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations

class SubsetTopologicalIterator extends BreadthFirstIterator<SDFAbstractVertex, SDFEdge> {
	
	/**
	 * Instances and its associated sources
	 */
	private val Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	/**
	 * Instances encountered
	 */
	private val List<SDFAbstractVertex> instanceEncountered
	
	/**
	 * The original graph supplied by the client
	 */
	private val SDFGraph inputGraph
	
	/**
	 * Constructor
	 * @param dagGen DAGGenerator instance containing a DAG
	 * @param rootNode Root node
	 * @param logger For loggin purposes
	 * @throws NoSuchElementException If root node does not exist or is not a root node
	 */
	new(DAGConstructor dagGen, SDFAbstractVertex rootNode, Logger logger) throws NoSuchElementException {
		super(dagGen.getOutputGraph, rootNode)
		this.inputGraph = dagGen.getOutputGraph
		this.instanceSources = newHashMap()
		this.instanceEncountered = newArrayList()
		
		if(!new DAGFromSDFOperations(dagGen).rootInstances.contains(rootNode)){
			if(!dagGen.outputGraph.vertexSet.contains(rootNode)) {
				throw new NoSuchElementException("Node " + rootNode.name + " does not exist in the DAG!")
			} else {
				throw new NoSuchElementException("Node " + rootNode.name + " is not a root node!")
			}
		}
		
		// Iterate first to get the subset of DAG in question
		new BreadthFirstIterator<SDFAbstractVertex, SDFEdge>(inputGraph, rootNode)
		.forEach[seenNode | 
			instanceSources.put(seenNode, newArrayList())
		]
		
		// Now find the predecessor/source of relevant instances
		instanceSources.forEach[node, sources |
			sources.addAll(inputGraph.incomingEdgesOf(node)
				.map[edge | edge.source]
				.filter[source | instanceSources.keySet.contains(source)]
				.toList)
		]
	}
	
	/**
	 * Constructor
	 * @param dagGen DAGGenerator instance containing a DAG
	 * @param rootNode Root node
	 * @throws NoSuchElementException If root node does not exist or is not a root node
	 */
	new(DAGConstructor dagGen, SDFAbstractVertex rootNode) throws NoSuchElementException {
		this(dagGen, rootNode, null)
	}
	
	protected override encounterVertex(SDFAbstractVertex node, SDFEdge edge) {
		val sources = instanceSources.get(node)
		if(sources.isEmpty) {
			// the node is source node
			instanceEncountered.add(node)
			super.encounterVertex(node, edge)
		} else {
			// Check if all the nodes have been visited
			if(sources.filter[!instanceEncountered.contains(it)].size == 0){
				instanceEncountered.add(node)
				super.encounterVertex(node, edge)
			}
		}
	}
	
	/**
	 * Get a look up table of instances and its associated sources
	 * @return Instances mapped to a list of its sources
	 */
	public def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getInstanceSources() {
		return instanceSources
	}	
}