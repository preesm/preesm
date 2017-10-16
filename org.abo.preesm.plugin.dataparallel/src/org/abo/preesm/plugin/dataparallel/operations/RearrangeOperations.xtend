package org.abo.preesm.plugin.dataparallel.operations

import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.abo.preesm.plugin.dataparallel.NodeChainGraph
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.fifo.FifoActor
import org.abo.preesm.plugin.dataparallel.fifo.FifoActorBeanKey
import org.abo.preesm.plugin.dataparallel.fifo.FifoActorGraph
import org.abo.preesm.plugin.dataparallel.iterator.SrSDFDAGCoIteratorBuilder
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer
import org.abo.preesm.plugin.dataparallel.pojo.RetimingInfo

/**
 * Perform re-timing operation for an instance independent strongly connected component.
 * 
 * @author Sudeep Kanur
 */
class RearrangeOperations implements DAGOperations {
	
	/**
	 * Optional {@link Logger} instance
	 */
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val Logger logger
	
	/**
	 * Hold {@link RetimingInfo} to access and add transient graphs expressed as 
	 * {@link FifoActorGraph}
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val RetimingInfo info
	
	/**
	 * Hold Single rate SDF (SrSDF) graph
	 */
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val SDFGraph srsdf
	
	/**
	 * Lookup table of edges with delays and its associated {@link FifoActor}. The edges are 
	 * incident to a single rate vertex that was originally added by the user in the {@link SDFGraph} 
	 */
	@Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
	val Map<SDFEdge, FifoActor> edgeFifoActors
	
	/**
	 * Lookup table of edges with delays and its associated original {@link FifoActor}. The edges 
	 * are incident to a single rate vertex that was originally added by the user in {@link SDFGraph}
	 * The {@link FifoActor} corresponds to the one that was in the SrSDF graph.
	 */
	@Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
	val Map<SDFEdge, FifoActor> originalEdgeFifoActors
	
	/**
	 * A {@link FifoActorGraph} that holds non-trivial initialization of certain FIFOs
	 */
	@Accessors(PRIVATE_GETTER, PRIVATE_SETTER)
	val FifoActorGraph transientGraph 
	
	/**
	 * Conditional logging
	 * 
	 * @param level Level of the message
	 * @param message String message
	 */
	def void log(Level level, String message) {
		if(this.logger !== null) {
			logger.log(level, message)
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param srsdf The original untransformed single rate SDF graph (SrSDF)
	 * @param info The {@link RetimingInfo} instance that is common among all strongly connected components
	 * @param logger Logger for loggin
	 */
	new(SDFGraph srsdf, RetimingInfo info, Logger logger) {
		this.srsdf = srsdf
		this.info = info
		this.logger = logger
		this.edgeFifoActors = newHashMap
		this.originalEdgeFifoActors = newHashMap
		this.transientGraph = new FifoActorGraph
	}
	
	/**
	 * Constructor for testing purposes
	 * 
	 * @param srsdf The original untransformed single rate SDF graph (SrSDF)
	 * @param info The {@link RetimingInfo} instance that is common among all strongly connected components
	 */
	new(SDFGraph srsdf, RetimingInfo info) {
		this(srsdf, info, null)
	}
	
	/**
	 * Helper function to rearrange a {@link PureDAGConstructor} instance
	 * 
	 * @param dagGen A DAG from which re-timing information can be derived
	 */
	def protected void rearrange(PureDAGConstructor dagGen) {		
		val moveInstanceVisitor = new MovableInstances
		dagGen.accept(moveInstanceVisitor)
		if(moveInstanceVisitor.movableInstances.empty) {
			// Nothing to move
			return
		}
		// We need to rearrange
		
		val nodechains = new NodeChainGraph(srsdf)
		
		moveInstanceVisitor.movableRootInstances.forEach[root |
			var srsit = (new SrSDFDAGCoIteratorBuilder)
							.addDAG(dagGen.outputGraph)
							.addSrSDF(srsdf)
							.addVisitableNodes(moveInstanceVisitor.movableInstances)
							.addStartVertex(root)
							.build
						
			while(srsit.hasNext) {
				val node = srsit.next
				
				if(nodechains.nodechains.keySet.contains(node)) {
					// This is not an associated implode/explode and hence can be modified
					
					// Add the delays to the FifoActor graph
					addInputEdgesToFifoActorGraph(node, nodechains)
					
					val setEdgeDelayMap = newHashMap
					
					// Reduce delay tokens at all its input edges
					val inEdgeDelayMap = nodechains.getEdgewiseInputDelays(node)
					srsdf.incomingEdgesOf(node).forEach[edge |
						// edge of this node
						val delay = inEdgeDelayMap.get(edge)
						if(delay === null) {
							throw new DAGComputationBug("Could not find edge: " + edge +
								" in the input edge-delay map")
						}
						val newDelay = delay.intValue - edge.cons.intValue
						setEdgeDelayMap.put(edge, newDelay)
					]
					nodechains.setEdgewiseInputDelays(node, setEdgeDelayMap)
					
					processOriginalOutputDelayEdges(node, nodechains)
					
					// Increase delay tokens at all its output edges
					setEdgeDelayMap.clear
					val outEdgeDelayMap = nodechains.getEdgewiseOutputDelays(node)
					srsdf.outgoingEdgesOf(node).forEach[edge |
						// edge of this node
						val delay = outEdgeDelayMap.get(edge)
						if(delay === null) {
							throw new DAGComputationBug("Could not find edge: " + edge +
								" in the output edge-delay map")
						}
						val newDelay = delay.intValue + edge.prod.intValue
						setEdgeDelayMap.put(edge, newDelay)
					]
					nodechains.setEdgewiseOutputDelays(node, setEdgeDelayMap)
					
					// Add delays to the FifoActor graph
					addOutputEdgesToFifoActorGraph(node, nodechains)
				}
			}
		]
		
		// Make sure all the ports are in order
		if(!SpecialActorPortsIndexer.checkIndexes(transientGraph)) {
			throw new DAGComputationBug("There are still special actors with non-indexed ports " +
				"in the transient graph")
		}
		SpecialActorPortsIndexer.sortIndexedPorts(transientGraph)
		
		info.initializationGraphs.add(transientGraph)
		
		// Clean the intermediate data-structure to make this object light-weight
		clean
	}
	
	/**
	 * Helper function. A single rate vertex of a user added vertex of the original SDF graph that
	 * is to be added to a transient graph is passed. For each of its incident edge, the function
	 * asserts that there are enough delays and fetches its corresponding {@link FifoActor}. 
	 * Finally, it adds the {@link FifoActor} to the {@link FifoActor} transient graph with 
	 * appropriate production and consumption rates.
	 * 
	 * @param node The node that has to be added to the transient graph
	 * @param nodechains The {@link NodeChainGraph} that contains information about user-added
	 * vertices and vertices added by the compiler
	 */
	private def void addInputEdgesToFifoActorGraph(SDFAbstractVertex node, NodeChainGraph nodechains) {
		if(transientGraph.vertexSet.contains(node)) {
			throw new DAGComputationBug("The node: " + node + " cannot be already added to " +
				"the transient graph")
		}
		transientGraph.addVertex(node)
		
		val inEdgeDelayMap = nodechains.getEdgewiseInputDelays(node)
		inEdgeDelayMap.forEach[edge, delay |
			val cons = edge.cons.intValue
			val rep = edge.target.nbRepeatAsInteger
			println(node + ": " + edge)
			if(delay < cons * rep) {
				throw new DAGComputationBug("While processing " + node + ", for the edge: " + edge 
					+ " not enough delays at the input.")
			}
			
			val fifoActor = if(edgeFifoActors.keySet.contains(edge)) {
				// This edge has been seen and added before
				edgeFifoActors.get(edge)
			} else {
				// This edge has NOT been seen before
				val actor = getFifoActor(edge)
				transientGraph.addVertex(actor)
				edgeFifoActors.put(edge, actor)
				actor
			}
			
			val fifoActorOut = new SDFSinkInterfaceVertex
			fifoActorOut.name = getFifoInterfaceName(fifoActor)
			
			fifoActor.addSink(fifoActorOut)
			val actorIn = edge.targetInterface
			val newEdge = transientGraph.addEdge(fifoActor, fifoActorOut,
										     	 node, actorIn,
										     	 new SDFIntEdgePropertyType(1),
										     	 new SDFIntEdgePropertyType(edge.cons.intValue),
										     	 new SDFIntEdgePropertyType(0))
			newEdge.dataType = edge.dataType.clone
			newEdge.sourcePortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY)
			newEdge.targetPortModifier = edge.targetPortModifier
		]
	}
	
	/**
	 * Helper function called before output edges of the user added single rate vertex is processed.
	 * The function finds {@link FifoActor}s for edges that have delay in the untransformed SrSDFG
	 * 
	 * @param node The node that has to be added to the transient graph
	 * @param nodechains The {@link NodeChainGraph} that contains information about user-added
	 * vertices and vertices added by the compiler 
	 */
	private def void processOriginalOutputDelayEdges(SDFAbstractVertex node, NodeChainGraph nodechains) {
		val outEdgeDelayMap = nodechains.getEdgewiseOutputDelays(node)
		outEdgeDelayMap.forEach[edge, delay |
			val fifoActor = getFifoActor(edge)
			fifoActor.name = fifoActor.name + "_original"
			originalEdgeFifoActors.put(edge, fifoActor)
			transientGraph.addVertex(fifoActor)
		]
	}
	
	/**
	 * Helper function. A single rate vertex of a user added vertex of the original SDF graph that
	 * is to be added to a transient graph is passed. For each of its outgoing edge, the function
	 * asserts that there are enough delays and fetches its corresponding {@link FifoActor}. 
	 * Finally, it adds the {@link FifoActor} to the {@link FifoActor} transient graph with 
	 * appropriate implodes, production and consumption rates.
	 * 
	 * @param node The node whose output edges are to be added to the transient graph
	 * @param nodechains The {@link NodeChainGraph} that contains information about user-added
	 * vertices and vertices added by the compiler 
	 */
	private def void addOutputEdgesToFifoActorGraph(SDFAbstractVertex node, NodeChainGraph nodechains) {
		if(!transientGraph.vertexSet.contains(node)) {
			throw new DAGComputationBug("The node: " + node + " must already by present in " +
				"the transient graph")
		}
		
		val outEdgeDelayMap = nodechains.getEdgewiseOutputDelays(node)
		outEdgeDelayMap.forEach[edge, delay |
			val prod = edge.prod.intValue
			val rep = edge.source.nbRepeatAsInteger
			if(delay < prod * rep) {
				throw new DAGComputationBug("For the edge: " + edge + " not enough delays produced")
			}
			
			val fifoActor = if(edgeFifoActors.keySet.contains(edge)) {
				throw new DAGComputationBug("FifoActor found before traversing the edge: !" + edge)
			} else {
				val actor = getFifoActor(edge)
				transientGraph.addVertex(actor)
				edgeFifoActors.put(edge, actor)
				actor
			}
			val fifoActorIn = new SDFSourceInterfaceVertex
			fifoActorIn.name = getFifoInterfaceName(fifoActor)
			fifoActor.addSource(fifoActorIn)
			
			var SDFEdge fifoInEdge
			// Check if this edge was originally scheduled to be initialised
			if(originalEdgeFifoActors.keySet.contains(edge)) {
				// Create an implode node, first consuming original tokens, then tokens from new 
				// fifoActor
				val originalFifoActor = originalEdgeFifoActors.get(edge)
				val implode = new SDFJoinVertex
				implode.name = "implode_" + edge.source.name + "_" + edge.target.name + "_init"
				transientGraph.addVertex(implode)
				
				// Add edge between original fifo and implode
				val originalFifoActorOut = new SDFSourceInterfaceVertex
				originalFifoActorOut.name =  getFifoInterfaceName(originalFifoActor)
				originalFifoActor.addSink(originalFifoActorOut)
				
				val implodeFifoIn = new SDFSinkInterfaceVertex
				implodeFifoIn.name = "implode_fifo_" + originalFifoActor.startIndex 
				implode.addSource(implodeFifoIn)
				
				val originalFifoImplodeEdge = transientGraph.addEdge(originalFifoActor, originalFifoActorOut,
																	 implode, implodeFifoIn,
																	 new SDFIntEdgePropertyType(1),
																	 new SDFIntEdgePropertyType(originalFifoActor.nbRepeatAsInteger),
																	 new SDFIntEdgePropertyType(0))
				originalFifoImplodeEdge.dataType = edge.dataType
				originalFifoImplodeEdge.sourcePortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY)
				originalFifoImplodeEdge.targetPortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY)
				
				// Add edge between node output and implode
				val implodeNodeIn= new SDFSourceInterfaceVertex
				implodeNodeIn.name = "implode_node_" + originalFifoActor.nbRepeatAsInteger
				implode.addSource(implodeNodeIn)
				val nodeImplodeEdge = transientGraph.addEdge(node, edge.sourceInterface,
															 implode, implodeNodeIn,
															 new SDFIntEdgePropertyType(edge.prod.intValue),
															 new SDFIntEdgePropertyType(edge.prod.intValue),
															 new SDFIntEdgePropertyType(0))
				nodeImplodeEdge.dataType = edge.dataType
				nodeImplodeEdge.sourcePortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY)
				nodeImplodeEdge.targetPortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY)
				
				if(nodeImplodeEdge.dataType != originalFifoImplodeEdge.dataType) {
					throw new DAGComputationBug("Data type of fifo-implode edge: (" + 
						originalFifoImplodeEdge.dataType + ") is not equal to node-implode edge: (" +
						nodeImplodeEdge.dataType)
				}
				
				// Add edge between implode output and FifoActor
				val implodeOut = new SDFSourceInterfaceVertex
				implodeOut.name = implode.name + "_out"
				implode.addSink(implodeOut)
				
				val implodeProd = originalFifoActor.nbRepeatAsInteger + edge.prod.intValue
				fifoInEdge = transientGraph.addEdge(implode, implodeOut,
													fifoActor, fifoActorIn,
													new SDFIntEdgePropertyType(implodeProd),
													new SDFIntEdgePropertyType(1),
													new SDFIntEdgePropertyType(0))
				
			} else {
				// Edge is just the edge coming out of the node
				fifoInEdge = transientGraph.addEdge(node, edge.sourceInterface,
													fifoActor, fifoActorIn,
													new SDFIntEdgePropertyType(edge.prod.intValue),
													new SDFIntEdgePropertyType(1),
													new SDFIntEdgePropertyType(0))
			}
			
			fifoInEdge.dataType = edge.dataType
			fifoInEdge.sourcePortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY)
			fifoInEdge.targetPortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY)
		]
	}
	
	/**
	 * Helper function to get the {@link FifoActor}. It checks if an edge has a {@link FifoActor}
	 * associated with it. Otherwise, it creates one with starting edge set to 0.
	 * 
	 * @param edge {@link FifoActor} of this edge is found
	 * @return {@link FifoActor}
	 */
	private def FifoActor getFifoActor(SDFEdge edge) {
		var FifoActor fifoActor
		val value = edge.propertyBean.getValue(FifoActorBeanKey.key)
		if(value === null) {
			val delay = edge.delay.intValue
			
			// A fifoActor refinement was never added before. Possible reasons
			// 1. FIFO refinement extension has not been implemented
			// 2. It is trivial FIFO initialisation, where all delays have same values
			// Either ways, token order does not matter, thus, starting edge can be set to 0
			fifoActor = new FifoActor(0)
			fifoActor.nbRepeat = new SDFIntEdgePropertyType(delay)
			fifoActor.name = edge.source.name + "_" + edge.target.name + "_init"
		} else {
			fifoActor = value as FifoActor
		}
		return fifoActor
	}
	
	/**
	 * Helper function to properly set the name of the {@link FifoActor}
	 * 
	 * @param fifoActor The {@link FifoActor} instance that has to be named 
	 * @return Name of the fifoActor
	 */
	private def String getFifoInterfaceName(FifoActor fifoActor) {
		return fifoActor + "_out_" + fifoActor.startIndex + "_" + 
			   (fifoActor.startIndex * fifoActor.nbRepeatAsInteger)
	}
	
	/**
	 * Clear the intermediate data-structures to make the object light-weight.
	 * This function is usually called at the end of rearraning transformation
	 */
	private def void clean() {
		edgeFifoActors.clear
		originalEdgeFifoActors.clear
	}
	
	/**
	 * Perform re-timing transformation
	 * 
	 * @param dagGen The DAG from which re-timing information can be extracted
	 */
	override visit(SDF2DAG dagGen) {
		rearrange(dagGen)
	}
	
	/**
	 * Perform re-timing transformation
	 * 
	 * @param dagGen The DAG from which re-timing information can be extracted
	 */
	override visit(DAG2DAG dagGen) {
		rearrange(dagGen)
	}
	
}