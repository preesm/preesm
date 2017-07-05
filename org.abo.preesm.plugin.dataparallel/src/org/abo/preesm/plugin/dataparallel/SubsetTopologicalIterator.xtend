package org.abo.preesm.plugin.dataparallel

import java.util.Collections
import java.util.List
import java.util.Map
import java.util.NoSuchElementException
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.traverse.BreadthFirstIterator

/**
 * A topological order iterator that traverses a subset of DAG
 * 
 * @author Sudeep Kanur 
 */
class SubsetTopologicalIterator extends BreadthFirstIterator<SDFAbstractVertex, SDFEdge> implements DAGTopologicalIteratorInterface {
	
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
	 * Constructor. Mainly used in plugin
	 * 
	 * @param dagGen {@link SDF2DAG} instance containing original DAG
	 * @param rootNode Root node
	 * @param logger For loggin purposes
	 * @throws NoSuchElementException If root node does not exist or is not a root node
	 */
	new(PureDAGConstructor dagGen, SDFAbstractVertex rootNode, Logger logger) throws NoSuchElementException {
		super(dagGen.getOutputGraph, rootNode)
		this.inputGraph = dagGen.getOutputGraph
		this.instanceSources = newHashMap()
		this.instanceEncountered = newArrayList()
		
		val rootInstances = inputGraph.vertexSet
			.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0].toList
			
		if(!rootInstances.contains(rootNode)) {
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
	 * Constructor used in Test setup
	 * 
	 * @param dagGen {@link SDF2DAG} instance containing a original DAG
	 * @param rootNode Root node
	 * @throws NoSuchElementException If root node does not exist or is not a root node
	 */
	new(PureDAGConstructor dagGen, SDFAbstractVertex rootNode) throws NoSuchElementException {
		this(dagGen, rootNode, null)
	}
	
	/**
	 * Overrides {@link BreadthFirstIterator#encounterVertex}
	 * Run encounterVertex only when all of the source nodes (that is seen in the 
	 * subset of the DAG) is encountered before
	 */
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
	 * 
	 * @return Unmodifiable map of instances mapped to a list of its sources
	 */
	public override Map<SDFAbstractVertex, List<SDFAbstractVertex>> getInstanceSources() {
		return Collections.unmodifiableMap(instanceSources)
	}	
}