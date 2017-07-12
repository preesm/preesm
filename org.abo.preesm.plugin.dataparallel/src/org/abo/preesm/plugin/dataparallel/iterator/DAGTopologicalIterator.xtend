package org.abo.preesm.plugin.dataparallel.iterator

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.jgrapht.traverse.TopologicalOrderIterator
import java.util.logging.Logger
import java.util.List
import java.util.Map
import java.util.Collections
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor

/**
 * A topological order iterator specialized to traverse a SDFGraph
 * In addition, also provides a lookup table of instances and its
 * sources.
 * 
 * @author Sudeep Kanur
 */
class DAGTopologicalIterator extends TopologicalOrderIterator<SDFAbstractVertex, SDFEdge> implements DAGTopologicalIteratorInterface {
	
	protected val Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	/**
	 * Constructor used for plugin
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param logger A Workflow logger instance
	 */
	new(PureDAGConstructor dagGen, Logger logger) {
		super(dagGen.outputGraph)
		
		instanceSources = newHashMap
		val inputGraph = dagGen.outputGraph
				
		// Iterate to get the nodes seen in the DAG
		new TopologicalOrderIterator<SDFAbstractVertex, SDFEdge>(inputGraph)
		.forEach[seenNode | 
			instanceSources.put(seenNode, newArrayList)
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
	 * Constructor for testing purposes
	 * 
	 * @param A {@link PureDAGConstructor} instance
	 */
	new(PureDAGConstructor dagGen) {
		this(dagGen, null)
	}
	
	/**
	 * Get a look up table of instances and its associated sources
	 * 
	 * @return Unmodifiable map of instances and a list of its sources
	 */
	override getInstanceSources() {
		return Collections.unmodifiableMap(instanceSources)
	}
	
}