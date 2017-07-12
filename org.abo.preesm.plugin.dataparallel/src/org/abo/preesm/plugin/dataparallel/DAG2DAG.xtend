package org.abo.preesm.plugin.dataparallel

import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGOperations

/**
 * Class that creates re-populates all necessary data-structures for a new
 * {@link PureDAGConstructor} that is constructed from an old {@link PureDAGConstructor}
 * 
 * @author Sudeep Kanur
 */
class DAG2DAG extends AbstractDAGConstructor implements PureDAGConstructor {
	/**
	 * Holds the input {@link SDFGraph} instance
	 */
	protected val SDFGraph newGraph
	
	/**
	 * Constructor to use in plugins
	 * 
	 * @param oldDAGGen Old {@link PureDAGConstructor} instance that has necessary data-structures
	 * @param logger A {@link Logger} instance for workflow logger
	 */
	new(PureDAGConstructor oldDAGGen, Logger logger) {
		super(logger)
		newGraph = new SDFGraph()
		val dag = oldDAGGen.outputGraph
		
		// Copy the old SDFGraph
		
		// Copy the vertex set
		dag.vertexSet.forEach[vertex |
			newGraph.addVertex(vertex)
		]
		
		// Copy the edge set
		dag.edgeSet.forEach[edge |
			val newEdge = newGraph.addEdge(edge.source, edge.target)
			
			// Set the appropriate sinks
			edge.source.sinks.forEach[sink |
				if( (edge.targetInterface !== null) && edge.targetInterface.name.equals(sink.name)) {
					edge.source.setInterfaceVertexExternalLink(newEdge, sink)
				}
			]	
			
			// Set the appropriate sources
			edge.target.sources.forEach[source |
				if( (edge.sourceInterface !== null) && edge.sourceInterface.name.equals(source.name)) {
					edge.target.setInterfaceVertexExternalLink(newEdge, source)
				}
			]
			newEdge.copyProperties(edge)
		] 
		
		// Make sure the ports of special actors are ordered according to their indices
		SpecialActorPortsIndexer.sortIndexedPorts(newGraph)
		newGraph.copyProperties(dag)
		newGraph.propertyBean.setValue("topology", null)
		newGraph.propertyBean.setValue("vrb", null)
		
		// Override calculation of source and sink actors
		sourceActors.clear()
		sourceActors.addAll(oldDAGGen.sourceActors)
		
		sinkActors.clear()
		sinkActors.addAll(oldDAGGen.sinkActors)
		
		// Override calculation of actor and instances maps
		actor2Instances.clear()
		actor2Instances.putAll(oldDAGGen.actor2Instances)
		
		instance2Actor.clear()
		instance2Actor.putAll(oldDAGGen.instance2Actor)
		
		explodeImplodeOrigInstances.clear()
		explodeImplodeOrigInstances.putAll(oldDAGGen.explodeImplodeOrigInstances)
	}
	
	/**
	 * Constructor to use in test-setups
	 * 
	 * @param oldDAGGen Old {@link PureDAGConstructor} instance that has necessary data-structures
	 */
	new(PureDAGConstructor oldDAGGen) {
		this(oldDAGGen, null)
	}
	
	/**
	 * Passes the newly copied graph from the old SDF graph
	 * 
	 * @return The copy {@link SDFGraph} instance that was passed to the constructor
	 */	
	override getOutputGraph() {
		return newGraph
	}
	
	/**
	 * Check if the input is valid and transformation is needed
	 * The input is always valid for this class
	 *  
	 * @return true
	 */
	override checkInputIsValid() throws SDF4JException {
		return true
	}
	
	/**
	 * Accept method for DAG operations
	 * 
	 * @param A {@link DAGOperations} instance
	 */
	override accept(DAGOperations visitor) {
		visitor.visit(this)
	}
}