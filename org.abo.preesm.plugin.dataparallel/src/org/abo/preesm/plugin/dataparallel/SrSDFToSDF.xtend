package org.abo.preesm.plugin.dataparallel

import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.jgrapht.alg.DijkstraShortestPath
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * Helper class to represent delays present in an SrSDF graph back in its original SDF graph
 * 
 * The primary purpose of this class is to check whether re-timing transformation works correctly.
 * After re-timing, the SDF graph must be acyclic-like. SrSDF graph cannot be checked for acyclic-like
 * property directly, hence we represent the delays of SrSDF graph back in SDF graph and then check
 * the SDF graph for acyclic-like property
 * 
 * The class takes in original SDF graph, original SrSDF graph (before re-timing) and DAG constructor.
 * All properties of original SDF graph, except the delay at their edges is preserved. Original
 * SrSDF graph is used to construct a {@link NodeChainGraph}, which groups implode/explodes together
 * with its associated vertex. The {@link PureDAGConstructor} derived from same SDF graph is used to
 * get the instance-to-actor relationship so that when a vertex (not its implode/explode) is obtained
 * from {@link NodeChainGraph}, we can trace which actor it belonged to in the original SDF graph.
 *  
 * @author Sudeep Kanur
 */
class SrSDFToSDF {
	/**
	 * Original SDF Graph
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	val SDFGraph sdf
	
	/**
	 * A {@PureDAGConstructor} instance derived from the original SDF graph
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	val PureDAGConstructor dagGen
	
	/**
	 * Clone of original SDF graph. Preserves the initial state of the delays
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	val SDFGraph originalSDF
	
	/**
	 * A {@link NodeChainGraph} instance derived from the SrSDF graph (that is in turn derived from
	 * the original SDF graph).
	 * 
	 * Used to obtain only those nodes that have direct actor in the SDF graph (not associated implode/
	 * explodes)
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	val NodeChainGraph nodeChainGraph
	
	/**
	 * True if the SDF graph has been re-timed
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	var boolean hasRetimed
	
	/**
	 * Constructor
	 * 
	 * @param sdf Original SDF graph
	 * @param srsdf A single rate graph created from the original SDF graph
	 */
	new(SDFGraph sdf, SDFGraph srsdf) {
		if(!srsdf.vertexSet.forall[vertex |
			vertex.nbRepeatAsInteger == 1
		]) {
			throw new SDF4JException("SrSDF graph has instances with repeat count greater than 1.
						\nCheck that argument order is not reversed")
		}
		
		this.sdf = sdf
		this.originalSDF = sdf.clone
		
		this.nodeChainGraph = new NodeChainGraph(srsdf)
		this.dagGen = new SDF2DAG(sdf)
		this.hasRetimed = false
	}
	
	/**
	 * Get the SDF graph that was re-timed according to the SrSDF graph passed
	 * @return SDF graph that was re-timed 
	 */
	def SDFGraph getRetimedSDF(SDFGraph srsdf) {
		if(!nodeChainGraph.nodechains.keySet.forall[node |
			srsdf.vertexSet.contains(node)
		]) {
			throw new SDF4JException("The single rate graph passed during construction is not
						same as the single rate graph passed for re-timing purposes")
		}
		
		hasRetimed = true
		// First set delays of SDF to 0
		sdf.edgeSet.forEach[edge |
			edge.delay = new SDFIntEdgePropertyType(0)
		]
		
		val seenConnectingEdges = newArrayList
		nodeChainGraph.nodechains.keySet.forEach[srsdfNode |
			val dagNode = DAGUtils.findVertex(srsdfNode, srsdf, dagGen.outputGraph)
			if(dagNode === null) {
				throw new DAGComputationBug("Couldn't find the related instance for instance: " +
					srsdfNode + " in the DAG graph")
			}
			val actor = dagGen.instance2Actor.get(dagNode)
			if(actor === null) {
				throw new DAGComputationBug("Couldn't find the related actor for instance: " +
					dagNode + " in the SDF graph")
			}
			
			// Get previous instances
			val prevSrSDFNodes = nodeChainGraph.getPreviousNodes(srsdfNode)
			if(prevSrSDFNodes !== null){
				val edgeDelayMap = nodeChainGraph.getEdgewiseInputDelays(srsdfNode)
				prevSrSDFNodes.forEach[prevSrSDFNode |
						
					val dagSource = DAGUtils.findVertex(prevSrSDFNode.vertex, srsdf, dagGen.outputGraph)
					if(dagSource === null) {
						throw new DAGComputationBug("Couldn't find the related instance for instance: " +
							prevSrSDFNode + " in the DAG graph")
					}
					val actorSource = dagGen.instance2Actor.get(dagSource)
					if(actorSource === null) {
						throw new DAGComputationBug("Couldn't find the related actor for instance: " +
							dagSource + " in the SDF graph")
					}
					// find edge connecting srsdfNode & preSrSDFNode
					val pathDetector = new DijkstraShortestPath(srsdf, prevSrSDFNode.vertex, srsdfNode)
					val connectingEdge = edgeDelayMap.keySet.findFirst[edge |
						pathDetector.pathEdgeList.contains(edge)
					]
					if(connectingEdge === null) {
						throw new DAGComputationBug("Couldn't find connecting edge between " 
							+ actor + " and " + actorSource + " in edge-delay map")
					}
					
					if(!seenConnectingEdges.contains(connectingEdge)) {
						seenConnectingEdges.add(connectingEdge)
						
						val actorEdge = sdf.edgeSet.findFirst[edge |
							edge.source == actorSource &&
							edge.target == actor
						]
						if(actorEdge === null) {
							throw new DAGComputationBug("Couldn't find similar edge " + connectingEdge 
								+ " from the original SDF graph in the re-timed SDF graph")
						}
						val prevDelay = actorEdge.delay.intValue
						val delay = edgeDelayMap.get(connectingEdge)					
						if(delay === null) {
							throw new DAGComputationBug("Couldn't find delay for the edge: " + connectingEdge)
						}
						actorEdge.delay = new SDFIntEdgePropertyType(prevDelay + delay.intValue) 
					}
				]
			}
		]
		return sdf
	}
	
	/**
	 * Get the SDF graph that was sent before it was re-timed
	 * @return Original SDF graph, before retiming was performed
	 */
	def SDFGraph getOriginalSDF() {
		if(hasRetimed) {
			originalSDF.edgeSet.forEach[edge |
				// Find the same edge in the retimed SDF graph
				val similarEdges = sdf.edgeSet.filter[sdfEdge |
					sdfEdge.source.name == edge.source.name &&
					sdfEdge.target.name == edge.target.name &&
					sdfEdge.prod.intValue == edge.prod.intValue &&
					sdfEdge.cons.intValue == edge.cons.intValue 
				]
				if(similarEdges.size != 1) {
					if(similarEdges.empty) {
						throw new DAGComputationBug("Couldn't find similar edge: " + edge + " in from
								 the original SDF graph in re-timed SDF graph")
					} else if(similarEdges.size > 1) {
						throw new DAGComputationBug("Found " + similarEdges.size + " edges instead of 
								 1 for the edge: " + edge)
					}
				} else {
					val retimedEdge = similarEdges.get(0)
					retimedEdge.delay = new SDFIntEdgePropertyType(edge.delay.intValue)
				}
			]
		}
		return sdf
	}
}