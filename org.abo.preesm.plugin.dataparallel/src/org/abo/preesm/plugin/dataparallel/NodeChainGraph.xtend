package org.abo.preesm.plugin.dataparallel

import java.util.List
import java.util.Map
import java.util.Set
import java.util.regex.Pattern
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.jgrapht.traverse.TopologicalOrderIterator
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor

/**
 * Apart from the poor choice of name for this class ;) , this class groups the vertices of
 * a single rate graph along with its associated implodes and explodes. This way, it is clear to 
 * distinguish which nodes are instances of actors of its original SDF graph and which nodes are
 * added during creation of single rate graph.
 * 
 * The delegation info is used to appropriately set/get delays. Input delays are placed at the 
 * begining of an implode instance, if present and at the exit of the explode instance, if present.
 * The class handles this information.
 * 
 * Warning! The association of implodes/explodes with a vertex is found out using regular expression.
 * It assumes that the way implode/explodes are named is according to {@link ToHSDFVisitor} class.
 * 
 * @author Sudeep Kanur
 */
class NodeChainGraph {
	
	/**
	 * Lookup table of vertices of a single rate SDF graph and its associated (optional)
	 * implode and explode vertices packaged in {@link NodeChain} instance
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Map<SDFAbstractVertex, NodeChain> nodechains
	
	/**
	 * Lookup table of explode vertex and its associated vertex
	 */
	@Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
	val Map<SDFForkVertex, SDFAbstractVertex> explodeRelatedVertex
	
	/**
	 * single rate SDF graph
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val SDFGraph graph
	
	/**
	 * Regex used to detect implode vertex associated with a vertex. This is based on how
	 * implode nodes are named during the creation of single rate graph in {@link ToHSDFVisitor}
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	val String implodeRegex = "implode_(.*)_(.*)\\z"
	
	/**
	 * Regex used to detect explode vertex associated with a vertex. This is based on how
	 * explode nodes are named during the creation of single rate graph in {@link ToHSDFVisitor}
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	val String explodeRegex = "explode_(.*)_(.*)\\z"
	
	/**
	 * The group to which name of the instance belongs to
	 */
	@Accessors(PRIVATE_SETTER, PRIVATE_GETTER)
	val int originalNameGroup = 1
	
	/**
	 * Constructor. 
	 * 
	 * Pass a single rate graph. Pure SDF graphs can also be passed, but it is useless.
	 * 
	 * @param graph The single rate graph
	 */
	new(SDFGraph graph) {
		nodechains = newHashMap
		explodeRelatedVertex = newHashMap
		this.graph = graph
		val localGraph = graph.clone
		
		// Topological sort only works on acyclic graph. So remove edges that
		// have delays equal to production/consumption rate
		val removableEdges = newArrayList
		localGraph.edgeSet.forEach[edge |
			val prod = edge.prod.intValue
			val delay = edge.delay.intValue
			val rep = edge.source.nbRepeatAsInteger
			if(delay >= rep * prod) {
				removableEdges.add(edge)
			}
		]
		
		removableEdges.forEach[edge |
			localGraph.removeEdge(edge)
		]
		
		// Implode explode patterns
		val implodePattern = Pattern.compile(implodeRegex)
		val explodePattern = Pattern.compile(explodeRegex)
		
		val topit = new TopologicalOrderIterator(localGraph)
		
		while(topit.hasNext) {
			val node = topit.next
			
			// if implode, check that next node is associated with this node by performing
			// regex check. 
			if(node instanceof SDFJoinVertex) {
				// If this is an implode associated with next node
				// then do nothing
				if(!localGraph.outgoingEdgesOf(node).forall[edge |
					val matcher = implodePattern.matcher(node.name)
					if(matcher.find) {
						val originalName = matcher.group(originalNameGroup)
						originalName == edge.target.name 			
					} else {
						false
					}
				]) {
					// Else this implode node was added by the user
					val srsdfNode = DAGUtils.findVertex(node, localGraph, graph)
					if(srsdfNode === null) {
						throw new DAGComputationBug("Couldn't find " + node.name + 
							" in original SrSDF graph. This is impossible!!")
					}
					nodechains.put(srsdfNode, new NodeChain(null, null, srsdfNode))
				}
			} else if(node instanceof SDFForkVertex) {
				// If this is an explode instance associated with previous node
				// Then do nothing as it is already added
				if(!localGraph.incomingEdgesOf(node).forall[edge |
					val matcher = explodePattern.matcher(node.name)
					if(matcher.find) {
						val originalName = matcher.group(originalNameGroup)
						originalName == edge.source.name	
					} else {
						false
					}
				]) {
					// Else this explode node was added by the user
					val srsdfNode = DAGUtils.findVertex(node, localGraph, graph)
					if(srsdfNode === null) {
						throw new DAGComputationBug("Couldn't find " + node.name + 
							" in original SrSDF graph. This is impossible!!")
					}
					nodechains.put(srsdfNode, new NodeChain(null, null, srsdfNode))
				}
			} else {
				val List<SDFJoinVertex> implodeNodes = newArrayList
				val List<SDFForkVertex> explodeNodes = newArrayList
				val srsdfNode = DAGUtils.findVertex(node, localGraph, graph)
				
				graph.incomingEdgesOf(srsdfNode).forEach[edge |
					// Check if its implode node and that if its not added
					// As this is topological walk, implode edges not associated with
					// this edge will already be processed and added
					if(edge.source instanceof SDFJoinVertex && 
						!nodechains.keySet.contains(edge.source)) {
						implodeNodes.add(edge.source as SDFJoinVertex)
					}
				]
				
				graph.outgoingEdgesOf(srsdfNode).forEach[edge |
					// Check if its explode node and it is not added by the user
					val matcher = explodePattern.matcher(edge.target.name)
					var boolean foundMatch
					if(matcher.find) {
						val originalName = matcher.group(originalNameGroup)
						foundMatch = originalName == srsdfNode.name								
					} else {
						foundMatch = false
					}
					
					if(edge.target instanceof SDFForkVertex &&
						foundMatch) {
						explodeNodes.add(edge.target as SDFForkVertex)
						explodeRelatedVertex.put(edge.target as SDFForkVertex, srsdfNode)
					}
				]
				
				nodechains.put(srsdfNode, new NodeChain(explodeNodes, implodeNodes, srsdfNode))
			}	
		}
	}
	
	/**
	 * Get previous node-chain of a single rate graph, given a vertex
	 * 
	 * @param vertex Previous nodes-chain of this vertex is found
	 * @return List of {@link NodeChain} connecting the vertex
	 */
	public def List<NodeChain> getPreviousNodes(SDFAbstractVertex vertex) {
		val node = nodechains.get(vertex)
		if(node === null) {
			throw new SDF4JException("The vertex is not part of the SrSDF graph used to " +
				"construct this node-chain.")
		}
		
		val previousNodes = newArrayList
		for(inEdge: graph.incomingEdgesOf(node.vertex)) {
			// Check if this edge has an implode associated with this edge
			if(node.implode !== null && node.implode.contains(inEdge.source)) {
				// Then add all nodes connected to this implode
				for(impEdge: graph.incomingEdgesOf(inEdge.source)) {
					previousNodes.addAll(getPreviousNodesHelper(impEdge.source))
				}
			} else {
				previousNodes.addAll(getPreviousNodesHelper(inEdge.source))
			}
		}
		
		if(previousNodes.empty) {
			return null
		} else {
			return previousNodes
		}
	}
	
	/**
	 * Helper function used when it is known that a vertex in an SrSDF graph whose previous node
	 * is being found has an implode vertex connected to it. Thus, the previous node-chain of the 
	 * vertex is in actuality the previous node-chain of the implode node. 
	 * 
	 * Further, the vertex behind this implode node can be an user inserted explode node or an
	 * associated explode node. This function checks this and populates the previous node-chain
	 * 
	 * @param vertex The implode edge whose previous node-chain has to be populated
	 * @return List of previous {@link NodeChain} connected to this implode edge 
	 */
	private def List<NodeChain> getPreviousNodesHelper(SDFAbstractVertex vertex) {
		val previousNodes = newArrayList
		// Check if this is an explode and if it has an associated node
		if(explodeRelatedVertex.keySet.contains(vertex)) {
			val prevVertex = explodeRelatedVertex.get(vertex)
			val prevNodeChain = nodechains.get(prevVertex)
			if(prevNodeChain === null) {
				throw new DAGComputationBug("Vertex " + prevVertex + " has no "
				+ "associated chain in nodechains, but has an associated explode.")
			}
			previousNodes.add(prevNodeChain)
		} else if (nodechains.keySet.contains(vertex)) {
			previousNodes.add(nodechains.get(vertex))
		} else {
			throw new DAGComputationBug("The vertex should either be an associated " +
				"explode or must be in node chains.\nPossible bugs: explodeRelatedVertex" +
				" is not properly populated,\nsome nodes are not contained in nodechains.")
		}	
		return previousNodes
	}
	
	/**
	 * Get sum total of all the delay present at the input of this vertex.
	 * 
	 * @param vertex The vertex for which sum-total of input delays must be calculated
	 * @return Total delays at its input
	 */
	public def int getTotalInputDelays(SDFAbstractVertex vertex) {
		var totalDelays = 0
		val edgeDelayMap = getEdgewiseInputDelays(vertex)
		if(edgeDelayMap === null) {
			return 0
		}
		for(edge: edgeDelayMap.keySet) {
			val delay = edgeDelayMap.get(edge)
			if(delay === null) {
				throw new DAGComputationBug("delay for edge: " + edge + " cannot be null!")
			}
			totalDelays += delay.intValue
		}
		return totalDelays
	}
	
	/**
	 * Get delays present at each input edge of the vertex. The number of delays
	 * returned is same as the number of edges to the vertex. The edges are related to the vertex
	 * and has no information of implode or explode edges (intentionally omitted). 
	 * 
	 * @param vertex The delays per edge of this vertex is returned
	 * @return Lookup table of {@link SDFEdge} connected to the vertex and its delay values
	 */
	public def Map<SDFEdge, Integer> getEdgewiseInputDelays(SDFAbstractVertex vertex) {
		val node = nodechains.get(vertex)
		if(node === null) {
			throw new SDF4JException("The vertex is not part of the SrSDF graph used to " +
				"construct this node-chain.")
		}
		
		var delayMap = newHashMap
		for(inEdge: graph.incomingEdgesOf(node.vertex)) {
			// Check if this edge has an implode associated with this edge
			if(node.implode !== null && node.implode.contains(inEdge.source)) {
				var delays = 0
				for(impEdge: graph.incomingEdgesOf(inEdge.source)) {
					delays += impEdge.delay.intValue
				}
				
				delayMap.put(inEdge, delays)
			} else {
				delayMap.put(inEdge, inEdge.delay.intValue)
			}
		}
		
		if(delayMap.empty) {
			return null
		} else {
			return delayMap
		}
	}
	
	/**
	 * Get delays present at each output edge of the vertex. The number of delays
	 * returned is same as the number of edges out of the vertex. The edges are related to the vertex
	 * and has no information of implode or explode edges (intentionally omitted). 
	 * 
	 * @param vertex The delays per edge of this vertex is returned
	 * @return Lookup table of {@link SDFEdge} connected out of the vertex and its delay values
	 */
	public def Map<SDFEdge, Integer> getEdgewiseOutputDelays(SDFAbstractVertex vertex) {
		val node = nodechains.get(vertex)
		if(node === null) {
			throw new SDF4JException("The vertex is not part of the SrSDF graph used to " +
				"construct this node-chain.")
		}
		
		var delayMap = newHashMap
		for(outEdge: graph.outgoingEdgesOf(node.vertex)) {
			// Check if this edge has an explode instance associated with this edge
			var delays = 0
			if(node.explode !== null && node.explode.contains(outEdge.target)) {
				for(exEdge: graph.outgoingEdgesOf(outEdge.target)) {
					delays += exEdge.delay.intValue
				}
				delayMap.put(outEdge, delays)
			} else {
				delayMap.put(outEdge, outEdge.delay.intValue)
			}
		}
		if(delayMap.empty) {
			return null
		} else {
			return delayMap
		}
	}
	
	/**
	 * Get sum total of all the delay present at the output of this vertex.
	 * 
	 * @param vertex The vertex for which sum-total of output delays must be calculated
	 * @return Total delays at its output
	 */
	public def int getTotalOutputDelays(SDFAbstractVertex vertex) {
		var totalDelays = 0
		val edgeDelayMap = getEdgewiseOutputDelays(vertex)
		if(edgeDelayMap === null) {
			return 0
		}
		for(edge: edgeDelayMap.keySet) {
			val delay = edgeDelayMap.get(edge)
			if(delay === null) {
				throw new DAGComputationBug("delay for edge: " + edge + " cannot be null!")
			}
			totalDelays += delay.intValue
		}
		return totalDelays
	}
	
	/**
	 * Helper function. This function handles setting delays at delegated nodes.
	 * 
	 * @param edge The edge has a delegated (associated implode/explode) node
	 * @param delay The value of the delay
	 * @param isInput The direction of the edges. True if its input
	 * @return Lookup table of delegated edge and the final delay value at it
	 */
	private def Map<SDFEdge, Integer> implodeExplodeDelayCalculator(SDFEdge edge, int delay, boolean isInput) {
		val edgeDelayMap = newHashMap
		var remainingDelays = delay
		
		val edgeSet = if(isInput) {
			graph.incomingEdgesOf(edge.source)
		} else {
			graph.outgoingEdgesOf(edge.target)
		}
		
		for(impEdge: edgeSet){
			edgeDelayMap.put(impEdge, 0)
		}
		
		val positiveDelays = remainingDelays > 0
		var iterate = true
		
		while(iterate) {
			for(impEdge: edgeSet) {
				val prevDelay = edgeDelayMap.get(impEdge)
				var cons = impEdge.cons.intValue
				
				if(positiveDelays && remainingDelays >= cons && iterate) {
					edgeDelayMap.put(impEdge, prevDelay + cons)
					remainingDelays -= cons	
				} else if(positiveDelays && remainingDelays < cons && iterate) {
					edgeDelayMap.put(impEdge, prevDelay + remainingDelays)
					iterate = false
					remainingDelays = 0
				} else if(!positiveDelays && (remainingDelays * -1) >= cons && iterate) {
					edgeDelayMap.put(impEdge, prevDelay - cons)
					remainingDelays += cons
				} else if(!positiveDelays && (remainingDelays * -1) < cons && iterate) {
					edgeDelayMap.put(impEdge, prevDelay + remainingDelays)
					iterate = false
					remainingDelays = 0
				}
			}
		}
		
		return edgeDelayMap
	}
	
	/**
	 * Helper function. Direction agnostic delay setting function. The direction is supplied as a
	 * parameter. The helper function does not handle delegated nodes.
	 * 
	 * @param vertex The delays are set to this vertex
	 * @param delays Lookup table of edges of this vertex and its delays
	 * @param isInput True if the delays of input are being set
	 */
	private def void setEdgewiseDelays(SDFAbstractVertex vertex, Map<SDFEdge, Integer> delays, boolean isInput) {
		val node = nodechains.get(vertex)
		if(node === null) {
			throw new SDF4JException("The vertex is not part of the SrSDF graph used to " + 
				"construct this node-chain.")
		}
		
		var Set<SDFEdge> edgeSet
		if(isInput) {
			edgeSet = graph.incomingEdgesOf(node.vertex)
		} else {
			edgeSet = graph.outgoingEdgesOf(node.vertex)
		}
		
		if(delays.size != edgeSet.size) {
			throw new SDF4JException("The number of delays in the list: " + delays.size 
				+ " is not equal to the edges (" + graph.incomingEdgesOf(node.vertex).size
				+ ") of the vertex " + node.vertex)
		}
		
		val edgeDelayMap = newHashMap
		
		for(edge: edgeSet) {
			if(delays.get(edge) === null) {
				throw new DAGComputationBug("Edge: " + edge + 
				" of SrSDF graph has no corresponding edge in the delay map.")
			}
			
			// Check if delays are present at associated implode and explode maps
			var hasDelegatedNode = true
			
			if(isInput) {
				hasDelegatedNode = node.implode !== null && node.implode.contains(edge.source)	
			} else {
				hasDelegatedNode = node.explode !== null && node.explode.contains(edge.target) 
			}
			
			if(hasDelegatedNode) {
				val delegatedDelayMap = implodeExplodeDelayCalculator(edge, delays.get(edge).intValue, isInput)
				edgeDelayMap.putAll(delegatedDelayMap)
			} else {
				edgeDelayMap.put(edge, delays.get(edge).intValue)
			}
		}
		
		// Now set the delays
		edgeDelayMap.forEach[edge, delay |
			edge.delay = new SDFIntEdgePropertyType(delay.intValue)
		]
	}
	
	/**
	 * Set input delays of each input edge of a given vertex.
	 * Best way to supply the lookup table is to get it from {@link NodeChainGraph#getEdgewiseInputDelays}
	 * If an edge has delegated node (associated implodes) then the value of delay is evenly
	 * distributed across all the incident edges of this implode node.
	 * 
	 * @param vertex The delays of edges input to this vertex are modified
	 * @param Lookup table of edges input to the vertex and desired values to be set
	 */
	public def void setEdgewiseInputDelays(SDFAbstractVertex vertex, Map<SDFEdge, Integer> delays) {
		setEdgewiseDelays(vertex, delays, true)
	}
	
	/**
	 * Set output delays of each input edge of a given vertex.
	 * Best way to supply the lookup table is to get it from {@link NodeChainGraph#getEdgewiseOutputDelays}
	 * If an edge has delegated node (associated explode) then the value of the delay is evenly
	 * distributed across all the outgoing edges of this explode node. 
	 * 
	 * @param vertex The delays of edges output to this vertex are modified
	 * @param Lookup table of edges output to the vertex and desired values to be set
	 */
	public def void setEdgewiseOutputDelays(SDFAbstractVertex vertex, Map<SDFEdge, Integer> delays) {
		setEdgewiseDelays(vertex, delays, false)
	}
}