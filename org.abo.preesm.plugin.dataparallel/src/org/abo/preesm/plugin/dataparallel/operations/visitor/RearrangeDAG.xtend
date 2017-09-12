package org.abo.preesm.plugin.dataparallel.operations.visitor

import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.abo.preesm.plugin.dataparallel.DAGUtils
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.iterator.SrSDFDAGCoIteratorBuilder
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex

class RearrangeDAG implements DAGOperations {
	
	/**
	 * The transient single rate transformed (HSDF) graph that 
	 * must be executed once. The graph can be null if the original
	 * SDF is already data-parallel
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var SDFGraph transientGraph
	
	/**
	 * The cyclic single rate transformed (HSDF) graph that
	 * must be executed multiple times. The DAG of this graph is
	 * guaranteed to be data-parallel if the original SDF is 
	 * instance independent
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var SDFGraph cyclicGraph
	
	@Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
	val SDFGraph inputSDF
	
	/**
	 * Optional logger
	 */
	@Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
	val Logger logger
	
	/**
	 * Constructor
	 * 
	 * @param sdf Original flattened SDF graph
	 * @param Logger A workflow logger instance
	 */
	new(SDFGraph sdf, Logger logger) {
		this.logger = logger
		this.inputSDF = sdf
		this.transientGraph = null
		
		val cyclicSDF = sdf.clone
		val cyclicHsdfVisitor = new ToHSDFVisitor
		cyclicSDF.accept(cyclicHsdfVisitor)
		cyclicGraph = cyclicHsdfVisitor.output
	}
	
	/**
	 * Constructor for testing purposes
	 * 
	 * @param sdf Original flattened SDF graph 
	 */
	new(SDFGraph sdf) {
		this(sdf, null)
	}
	
	/**
	 * Helper function
	 */
	private def void rearrange(PureDAGConstructor dagGen) {
		val levelOps = new LevelsOperations
		dagGen.accept(levelOps)
		val isParallel = OperationsUtils.isParallel(dagGen, levelOps.levels)
		
		if(isParallel) {
			// The sdf graph is already data-parallel. Nothing to do
			return
		}
		
		// We need to rearrange 
		rearrangeTransient(dagGen)
		rearrangeCyclic(dagGen)
	}
	
	/**
	 * Helper function to rearrange the transient graph
	 */
	private def void rearrangeTransient(PureDAGConstructor dagGen) {
		val transientHsdfVisitor = new ToHSDFVisitor
		inputSDF.clone.accept(transientHsdfVisitor)
		transientGraph = transientHsdfVisitor.output
		
		val moveInstanceVisitor = new MovableInstances
		dagGen.accept(moveInstanceVisitor)
		
		moveInstanceVisitor.movableRootInstances.forEach[root |
			val srsdfRoot = DAGUtils.findVertex(root, dagGen.outputGraph, transientGraph)
			val srsit = (new SrSDFDAGCoIteratorBuilder)
							.addDAG(dagGen.outputGraph)
							.addSrSDF(transientGraph)
							.addVisitableNodes(moveInstanceVisitor.movableInstances)
							.addStartVertex(root)
							.build()
							
			val copiesPile = newHashMap
						
			while(srsit.hasNext) {
				val node = srsit.next
				// Create a new copy of this node and update the transient SDF
				val newNode = node.clone
				newNode.name = node.name + "_T"
				transientGraph.addVertex(newNode)
				copiesPile.put(node, newNode)
				
				if(node == srsdfRoot) {
					// Create the copy of each preceeding node and edges
					val edge = transientGraph.incomingEdgesOf(node)					
					if(edge.size != 1) {
						throw new DAGComputationBug("It is assumed to have only one " + 
						"incoming edge. But node: " + node + " has " + edge.size + " edges.")
						
					}
					val oldEdge = edge.get(0)
					
					val prevNode = oldEdge.source
					val newEdge = transientGraph.addEdge(prevNode, newNode)
					newEdge.sourceInterface = oldEdge.sourceInterface
					newEdge.targetInterface = newNode.sources.get(0)
					newEdge.copyProperties(oldEdge)
					newEdge.delay = new SDFIntEdgePropertyType(0)
					
					transientGraph.removeEdge(oldEdge)
				} else {
					transientGraph.incomingEdgesOf(node).forEach[edge |
						val prevNode = edge.source
						if(copiesPile.keySet.contains(prevNode)) {
							// Create an edge between this clone and prevNode clone
							val prevNodeClone = copiesPile.get(prevNode)
							val newEdge = transientGraph.addEdge(prevNodeClone, newNode)
							
							prevNodeClone.sinks.forEach[sink |
								if(sink.name == edge.sourceInterface.name) {
									newEdge.sourceInterface = sink
								}
							]
							newNode.sources.forEach[source |
								if(source.name == edge.targetInterface.name) {
									newEdge.targetInterface = source
								}
							]
							newEdge.copyProperties(edge)
						}
					]
				}
			}
		]
	}
	
	/**
	 * Helper function to rearrange the cyclic graph
	 */
	private def void rearrangeCyclic(PureDAGConstructor dagGen) {
		val moveInstanceVisitor = new MovableInstances
		dagGen.accept(moveInstanceVisitor)
		
		// Remove delay from each root
		moveInstanceVisitor.movableRootInstances.forEach[root |
			val srsdfRoot = DAGUtils.findVertex(root, dagGen.outputGraph, cyclicGraph)
			if(srsdfRoot === null) {
				throw new DAGComputationBug("Root node:(" + root + ") can't be absent in SrSDF node")
			}
			
			cyclicGraph.incomingEdgesOf(srsdfRoot).forEach[inEdge |
				if(inEdge.delay.intValue > 0) {
					inEdge.delay = new SDFIntEdgePropertyType(0)
				}
				// Check if its previous node is implode
				if(inEdge.source instanceof SDFJoinVertex) {
					// Then iterate through all its edges and set the delay to zero
					cyclicGraph.incomingEdgesOf(inEdge.source).forEach[implodeEdge |
						if(implodeEdge.delay.intValue > 0) {
							implodeEdge.delay = new SDFIntEdgePropertyType(0)
						}
					]
				}
			]
		]
		
		// Set delay at the exit of each exit
		moveInstanceVisitor.movableExitInstances.forEach[exit |
			val srsdfExit = DAGUtils.findVertex(exit, dagGen.outputGraph, cyclicGraph)
			if(srsdfExit === null) {
				throw new DAGComputationBug("Exit node: (" + exit + ") can't be absent in SrSDF node")
			}
			
			cyclicGraph.outgoingEdgesOf(srsdfExit).forEach[outEdge |
				// Make sure next node is not explode
				if(outEdge.target instanceof SDFForkVertex){
					throw new DAGComputationBug("Can't have explode after the exit node.")
				}
				
				val consRate = outEdge.cons.intValue
				val existingDelay = outEdge.delay.intValue
				if(existingDelay != consRate) {
					// Set the appropriate delay after this node
					outEdge.delay = new SDFIntEdgePropertyType(outEdge.cons.intValue)	
				}
			]
		]
	}
	
	override visit(SDF2DAG dagGen) {
		rearrange(dagGen)
	}
	
	override visit(DAG2DAG dagGen) {
		rearrange(dagGen)
	}
	
}