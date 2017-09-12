package org.abo.preesm.plugin.dataparallel

import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.Set
import java.util.logging.Logger
import java.util.regex.Pattern
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer
import org.ietr.dftools.algorithm.model.sdf.types.SDFIntEdgePropertyType
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGOperations
import org.jgrapht.alg.CycleDetector

/**
 * Construct DAG from a SDF Graph
 * 
 * @author Sudeep Kanur
 */
final class SDF2DAG extends AbstractDAGConstructor implements PureDAGConstructor {
	
	/**
	 * Hold the cloned version of original SDF graph
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val SDFGraph inputSDFGraph;
	
	/**
	 * Holds constructed DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var SDFGraph outputGraph
	

	
	/**
	 * Map of all actors with instance. Does not contain implodes and explodes
	 * This is used as intermediate variable while creating linked edges
	 */
	protected val Map<SDFAbstractVertex, List<SDFAbstractVertex>> actor2InstancesLocal;
	
	/**
	 * Check if the input SDF graph has changed
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	protected val boolean hasChanged;
	
	/**
	 * List of all the actors that form the part of the cycles in the original SDFG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> cycleActors
	
	/**
	 * Constructor. Mainly used in the plugin
	 * 
	 * @param sdf the sdf graph for which DAG is created
	 * @param logger log messages to console
	 * @throws SDF4JException If the input sdf graph is not schedulable 
	 */
	new(SDFGraph sdf, Logger logger) throws SDF4JException {
		super(logger)
		
		if(!sdf.isSchedulable) {
			throw new SDF4JException("Graph " + sdf + " not schedulable")
		}
		inputSDFGraph = sdf.clone
		actor2InstancesLocal = newHashMap
		cycleActors = newArrayList
		
		val cycleDetector = new CycleDetector(inputSDFGraph)
		cycleActors.addAll(cycleDetector.findCycles)
		
		inputSDFGraph.vertexSet.forEach[vertex |
			if(inputSDFGraph.incomingEdgesOf(vertex).size == 0) {
				sourceActors.add(vertex)
			}
			if(inputSDFGraph.outgoingEdgesOf(vertex).size == 0) {
				sinkActors.add(vertex)
			}
		]
		
		inputSDFGraph.vertexSet.forEach[vertex |
			val predecessorList = newArrayList
			inputSDFGraph.incomingEdgesOf(vertex).forEach[edge |
				predecessorList.add(edge.source)
			]
			actorPredecessor.put(vertex, predecessorList)
		]
		
		if(checkInputIsValid) {
			hasChanged = true
			this.outputGraph = new SDFGraph()
			createInstances()
			linkEdges()
		} else {
			hasChanged = false
			this.outputGraph = inputSDFGraph
		}
		outputGraph.propertyBean.setValue("schedulable", true)
	}
	
	/**
	 * Constructor without logging information. Mainly used for test 
	 * setup
	 * 
	 * @param sdf the sdf graph for which DAG is created
	 * @throws SDF4JException If the input sdf graph is not schedulable 
	 */
	new(SDFGraph sdf) throws SDF4JException {
		this(sdf, null)
	}
	
	/**
	 * Check if the input is valid and transformation is needed
	 * Checks the following conditions
	 * DAG is flattened, repetition vector is greater than 1, delay tokens exist
	 * 
	 * @return boolean True if input is valid, false if input is already a DAG
	 * @throws SDF4JException if the input graph is not flattened
	 */
	public override boolean checkInputIsValid() throws SDF4JException {
		// Check if DAG is flattened
		for(vertex: inputSDFGraph.vertexSet) {
			if( (vertex.graphDescription !== null) && (vertex.graphDescription instanceof SDFGraph)) {
				throw new SDF4JException("The graph " + inputSDFGraph.name + " must be flattened.")				
			}
		}		
		// Check if repetition vector is greater than 1
		for(vertex: inputSDFGraph.vertexSet) {
			if(vertex.nbRepeatAsInteger > 1) return true
		}		
		// Check if delay tokens exist. DAG should not have any of those
		for(edge: inputSDFGraph.edgeSet) {
			if(edge.delay.intValue > 0) return true
		}
		// Its already a DAG
		return false
	}
	
	/**
	 * Accept method for DAG operations
	 * 
	 * @param A {@link DAGOperations} instance
	 */
	override accept(DAGOperations visitor) {
		visitor.visit(this)
	}
	
	/**
	 * Create instances according to the repetition count. Also rename the 
	 * instances
	 */
	 protected def void createInstances() {
	 	// Create instances repetition vector times
	 	for(actor: inputSDFGraph.vertexSet) {
	 		log("Actor " + actor + " has " + actor.nbRepeatAsInteger + " instances.")
	 		val instances = newArrayList
	 		 
	 		for(var ii = 0; ii < actor.nbRepeatAsInteger; ii++) {
	 			// Clone and set properties
	 			val instance = actor.clone
	 			instance.name = actor.name + "_" + ii;
	 			instance.nbRepeat = 1
	 			
	 			// Add to maps
	 			outputGraph.addVertex(instance)
	 			instances.add(instance)
	 			instance2Actor.put(instance, actor)
	 		}
	 		// Add to reverse map
	 		actor2InstancesLocal.put(actor, instances)
	 		actor2Instances.put(actor, new ArrayList(instances))
	 	}
	 }
	 
	 /**
	  * Link the instances created by {@link SDF2DAG#createInstances}
	  */
	protected def void linkEdges() {
		// Edges that have delay tokens greater than buffer size need not have any
		// links in the DAG
		val filteredEdges = inputSDFGraph.edgeSet.filter[edge |
			edge.delay.intValue < edge.cons.intValue * edge.target.nbRepeatAsInteger
		]
		
		for(edge: filteredEdges) {
			// Legacy code. Usage debatable. Check the comment below (search the usage of inputVertex)
			// var SDFInterfaceVertex inputVertex = null
			// var SDFInterfaceVertex outputVertex = null
			
			// Total number of tokens that must be stored in the buffer
			val bufferSize = edge.cons.intValue * edge.target.nbRepeatAsInteger
			
			val sourceInstances = actor2InstancesLocal.get(edge.source)
			val targetInstances = actor2InstancesLocal.get(edge.target)
			val originalSourceInstances = new ArrayList(sourceInstances)
			val originalTargetInstances = new ArrayList(targetInstances)
			
			var absoluteTarget = edge.delay.intValue
			var absoluteSource = 0
			var currentBufferSize = edge.delay.intValue
			
			val newEdges = newArrayList
			while(currentBufferSize < bufferSize) {
				// Index of currently processed source instance among the instances of source actor. 
				// Similarly for target actor
				val sourceIndex = (absoluteSource/edge.prod.intValue) % sourceInstances.size
				val targetIndex = (absoluteTarget/edge.cons.intValue) % targetInstances.size
				
				// Number of tokens already consumed and produced by currently indexed instances
				val sourceProd = absoluteSource % edge.prod.intValue
				val targetCons = absoluteTarget % edge.cons.intValue
				
				// Remaining tokens that needs to be handled in the next iteration
				val restSource = edge.prod.intValue - sourceProd
				val restTarget = edge.cons.intValue - targetCons
				val rest = Math.min(restSource, restTarget)
				
				// The below part of the code is from ToHSDFVisitor. It should not be relevant here as
		        // iterationDiff is never greater than 1. But I have included here for legacy reasons
		        // and added an exception when it occurs
		
		        // This int represent the number of iteration separating the
		        // currently indexed source and target (between which an edge is
		        // added)
		        // If this int is > to 0, this means that the added edge must
		        // have
		        // delays (with delay=prod=cons of the added edge).
		        // With the previous example:
		        // A_1 will target B_(1+targetIndex%3) = B_0 (with a delay of 1)
		        // A_2 will target B_(2+targetIndex%3) = B_1 (with a delay of 1)
		        // Warning, this integer division is not factorable
		        val iterationDiff = (absoluteTarget / bufferSize) - (absoluteSource / bufferSize);
		        if (iterationDiff > 0) {
		        	throw new SDF4JException("iterationDiff is greater than 0.")
		        }
		        
		        // For inserting explode and implode when boolean is true
		        val explode = rest < edge.prod.intValue
		        val implode = rest < edge.cons.intValue
		        
		        // Add explode instance for non-broadcast, non-roundbuffer, non-fork/join actors 
				if(explode && !(sourceInstances.get(sourceIndex) instanceof SDFForkVertex) 
					&& (!(sourceInstances.get(sourceIndex) instanceof SDFBroadcastVertex) || !(sourceInstances.get(sourceIndex) instanceof SDFRoundBufferVertex))) {
						val explodeInstance = new SDFForkVertex()
						explodeInstance.name = "explode_" + sourceInstances.get(sourceIndex).name + "_" + edge.sourceInterface.name
						outputGraph.addVertex(explodeInstance)
						val originalInstance = sourceInstances.get(sourceIndex)
						// Replace the source vertex by explode in the sourceInstances list
						sourceInstances.set(sourceIndex, explodeInstance)
						// Add to maps
						updateMaps(originalInstance, explodeInstance)
						// Add an edge between the explode and its instance
						val newEdge = outputGraph.addEdge(originalInstance, explodeInstance)
						newEdge.delay = new SDFIntEdgePropertyType(0)
						newEdge.prod = new SDFIntEdgePropertyType(edge.prod.intValue)
						newEdge.cons = new SDFIntEdgePropertyType(edge.prod.intValue)
						newEdge.dataType = edge.dataType
						// Name the ports and set its attributes
						explodeInstance.addInterface(edge.targetInterface)
						newEdge.sourceInterface = edge.sourceInterface
						newEdge.targetInterface = edge.targetInterface
						newEdge.sourcePortModifier = edge.sourcePortModifier
						// As its explode, target port modifier is read-only
						newEdge.targetPortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY)
				}
				
				// Add implode instance for non-fork/join and non-roundbuffer
				if(implode && !(targetInstances.get(targetIndex) instanceof SDFJoinVertex) && !(targetInstances.get(targetIndex) instanceof SDFRoundBufferVertex)) {
					val implodeInstance = new SDFJoinVertex()
					implodeInstance.name = "implode_" + targetInstances.get(targetIndex).name + "_" + edge.targetInterface.name
					val originalInstance = targetInstances.get(targetIndex)
					outputGraph.addVertex(implodeInstance)
					// Replace the target vertex by implode in the targetInstances list
					targetInstances.set(targetIndex, implodeInstance)
					// Add to maps
					updateMaps(originalInstance, implodeInstance)
					// Add an edge between the implode and its instance
					val newEdge = outputGraph.addEdge(implodeInstance, originalInstance)
					newEdge.delay = new SDFIntEdgePropertyType(0)
					newEdge.prod = new SDFIntEdgePropertyType(edge.cons.intValue)
					newEdge.cons = new SDFIntEdgePropertyType(edge.cons.intValue)
					newEdge.dataType = edge.dataType
					// Name the ports and set its attributes
					implodeInstance.addInterface(edge.sourceInterface)
					newEdge.sourceInterface = edge.sourceInterface
					newEdge.targetInterface = edge.targetInterface
					newEdge.targetPortModifier = edge.targetPortModifier
					// As its implode, source port modifier is write-only
					newEdge.sourcePortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY)
				}
				
				// Create the new edge for the output graph
				val newEdge = outputGraph.addEdge(sourceInstances.get(sourceIndex), targetInstances.get(targetIndex))
				newEdges.add(newEdge)
				
				// Set the source interface of the new edge
				// If the source is a newly added fork/broadcast (or extra output added to existing fork/broadcast)
				// We rename the output ports. Contrary to the ports of join/roundbuffer, no special processing
				// is needed to order the edges
				if( (sourceInstances.get(sourceIndex) == originalSourceInstances.get(sourceIndex))
					&& (!explode || !((originalSourceInstances.get(sourceIndex) instanceof SDFBroadcastVertex) 
						|| (originalSourceInstances.get(sourceIndex) instanceof SDFForkVertex)))) {
					// if the source does not need new ports
					if(sourceInstances.get(sourceIndex).getSink(edge.sourceInterface.name) !== null) {
						// Source already has an interface. Just assign the correct name
						newEdge.sourceInterface = sourceInstances.get(sourceIndex).getSink(edge.sourceInterface.name)
					} else {
						// source does not have an interface
						newEdge.sourceInterface = edge.sourceInterface.clone
						sourceInstances.get(sourceIndex).addInterface(newEdge.sourceInterface)
					}
					// Copy the source port modifier of the original source
					newEdge.sourcePortModifier = edge.sourcePortModifier
				} else {
					// if the source is a fork (new or not) or a broadcast with a new port
					val sourceInterface = edge.sourceInterface.clone
					var newInterfaceName = sourceInterface.name + "_" + sourceProd
					
					// Get the current index of the port (if any) and update it
					if(sourceInterface.name.matches(SpecialActorPortsIndexer.indexRegex)) {
						val pattern = Pattern.compile(SpecialActorPortsIndexer.indexRegex)
						val matcher = pattern.matcher(sourceInterface.name)
						matcher.find
						val existingIdx = Integer.decode(matcher.group(SpecialActorPortsIndexer.groupXX))
						val newIdx = existingIdx + sourceProd
						newInterfaceName = sourceInterface.name.substring(0, matcher.start(SpecialActorPortsIndexer.groupXX)) + newIdx
					}
					
					sourceInterface.name = newInterfaceName
					newEdge.sourceInterface = sourceInterface
					newEdge.source.addInterface(sourceInterface)
					// Add a source port modifier
					newEdge.sourcePortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY)
				}
				
				// Set the target interface of the new edge
				// If the target is a newly added join/roundbuffer we need to take extra care to
				// make sure the incoming edges are in the right order (which might be a little 
				// bit complex when playing with delays)
				
				// If the target is not an instance with new ports (because of an explosion)
				if((targetInstances.get(targetIndex) == originalTargetInstances.get(targetIndex)) 
					&& (!implode || !((originalTargetInstances.get(targetIndex) instanceof SDFRoundBufferVertex)
						|| (originalTargetInstances.get(targetIndex) instanceof SDFJoinVertex)))) {
					// if the target already has appropriate interface
					if(targetInstances.get(targetIndex).getSource(edge.targetInterface.name) !== null) {
						newEdge.targetInterface = targetInstances.get(targetIndex).getSource(edge.targetInterface.name)
					} else {
						// if the target does not have the interface
						newEdge.targetInterface = edge.targetInterface.clone
						targetInstances.get(targetIndex).addInterface(newEdge.targetInterface)
					}
					// Copy the target port modifier of the original source, except roundbuffers
					if(!(newEdge.target instanceof SDFRoundBufferVertex)) {
						newEdge.targetPortModifier = edge.targetPortModifier
					} else {
						// The processing of round buffer is done after the while loop
					}
				} else {
					// if the target is join (new or not)/ roundbuffer with new ports
					val targetInterface = edge.targetInterface.clone
					var newInterfaceName = targetInterface.name + "_" + targetCons
					
					// Get the current index of the port (if any) and update it
					if(targetInterface.name.matches(SpecialActorPortsIndexer.indexRegex)) {
						val pattern = Pattern.compile(SpecialActorPortsIndexer.indexRegex)
						val matcher = pattern.matcher(targetInterface.name)
						matcher.find
						val existingIdx = Integer.decode(matcher.group(SpecialActorPortsIndexer.groupXX))
						val newIdx = existingIdx + targetCons
						newInterfaceName = targetInterface.name.substring(0, matcher.start(SpecialActorPortsIndexer.groupXX)) + newIdx
					}
					
					targetInterface.name = newInterfaceName
					newEdge.targetInterface = targetInterface
					newEdge.target.addInterface(targetInterface)
					// Add a target port modifier
					newEdge.targetPortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY)
				}
				
				// Associate the interfaces to the new edge
		        // TODO Not sure what it does. Adding the edge and its associated interface was done in previous if-else block
		        // Why do it again? And setInterfaceVertexExternalLink purpose is ambiguous. Git blame shows this is a legacy code
		        // not touched by kdesnos
		        // I'm going to comment this part now and see if this changes anything
		
		        // if (targetInstances.get(targetIndex) instanceof SDFVertex) {
		        // if (((SDFVertex) targetInstances.get(targetIndex)).getSource(edge.getTargetInterface().getName()) != null) {
		        // inputVertex = ((SDFVertex) targetInstances.get(targetIndex)).getSource(edge.getTargetInterface().getName());
		        // ((SDFVertex) targetInstances.get(targetIndex)).setInterfaceVertexExternalLink(newEdge, inputVertex);
		        // }
		        // }
		        // if (sourceInstances.get(sourceIndex) instanceof SDFVertex) {
		        // if (((SDFVertex) sourceInstances.get(sourceIndex)).getSink(edge.getSourceInterface().getName()) != null) {
		        // outputVertex = ((SDFVertex) sourceInstances.get(sourceIndex)).getSink(edge.getSourceInterface().getName());
		        // ((SDFVertex) sourceInstances.get(sourceIndex)).setInterfaceVertexExternalLink(newEdge, outputVertex);
		        // }
		        // }
		        
		        // Set the properties of the new edge
		        newEdge.prod = new SDFIntEdgePropertyType(rest)
		        newEdge.cons = new SDFIntEdgePropertyType(rest)
		        newEdge.dataType = edge.dataType
		        newEdge.delay = new SDFIntEdgePropertyType(0)
		        
		        absoluteTarget += rest
		        absoluteSource += rest
		        currentBufferSize += rest
		        
		        // Below code although in ToHSDFVisitor class, does not occur in my case
		        if ((currentBufferSize == bufferSize) && (targetInstances.get(0) instanceof SDFInterfaceVertex)
		            && ((absoluteSource / edge.getProd().intValue()) < sourceInstances.size())) {
		          throw new SDF4JException("CurrentBuffersize never needs to be reset. There is a bug, so contact" + " Sudeep Kanur (skanur@abo.fi) with the SDF graph that caused this.")
		          // currentBufferSize = 0;
		        }
			}
			
			// if the edge target was a round buffer we set the port modifiers here
			if(edge.target instanceof SDFRoundBufferVertex) {
				// Set all the target modifiers as unused and sort list of input
				SpecialActorPortsIndexer.sortFifoList(newEdges, false);
				val iter = newEdges.listIterator()
				while(iter.hasNext()) {
					iter.next().targetPortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_UNUSED)
				}
				val portModifier = edge.targetPortModifier
				if( (portModifier !== null) && !portModifier.toString.equals(SDFEdge.MODIFIER_UNUSED)) {
					// If the target is unused, set last edges targetModifier as read-only
					var tokensToProduce = (edge.target.base.outgoingEdgesOf(edge.target) as Set<SDFEdge>).iterator.next.prod.intValue
					
					// Scan the edges in reverse order
					while ((tokensToProduce > 0) && iter.hasPrevious()) {
						val SDFEdge newEdge = iter.previous()
						newEdge.targetPortModifier = new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY)
						tokensToProduce -= newEdge.cons.intValue
					}
				}
			}
			// If fork/join vertices were added during the function call, put back the true source/target in the
			// instances copies map
			// TODO Check this is needed in my case
			// TODO Also add reverse map
			for(var ii = 0; ii < sourceInstances.size(); ii++) {
				if((sourceInstances.get(ii) instanceof SDFForkVertex)&& !originalSourceInstances.get(ii).equals(sourceInstances.get(ii))) {
					var SDFAbstractVertex trueSource = null
					// TODO Again, this code does not make sense. The size of incomingEdges will always be one!
					if (outputGraph.incomingEdgesOf(sourceInstances.get(ii)).size > 1) {
					    throw new SDF4JException("Explode instance has more than one edge from its source instance! "
							+ "Something is wrong in understanding and could be a potential bug! Please contact "
							+ "Sudeep Kanur (skanur@abo.fi) with the SDF that caused this." + "Number of outgoing edges for explode instance ("
							+ sourceInstances.get(ii).name + ") is: " + outputGraph.incomingEdgesOf(sourceInstances.get(ii)).size);
					}
					for(inEdge: outputGraph.incomingEdgesOf(sourceInstances.get(ii))) {
						trueSource = inEdge.source
					}
					sourceInstances.set(ii, trueSource)				
				}
			}
			
			for(var ii = 0; ii < targetInstances.size(); ii++) {
				if((targetInstances.get(ii) instanceof SDFJoinVertex) && !originalTargetInstances.get(ii).equals(sourceInstances.get(ii))) {
					var SDFAbstractVertex trueTarget = null
					// TODO Again, this code does not make sense. The size of incomingEdges will always be one!
					if (outputGraph.outgoingEdgesOf(targetInstances.get(ii)).size > 1) {
					    throw new SDF4JException("Implode instance has more than one edge from its target instance! "
							+ "Something is wrong in understanding and could be a potential bug! Please contact "
							+ "Sudeep Kanur (skanur@abo.fi) with the SDF that caused this." + "Number of incoming edges for implode instance ("
							+ targetInstances.get(ii).name + ") is: " + outputGraph.outgoingEdgesOf(targetInstances.get(ii)).size);
					}
					for(inEdge: outputGraph.outgoingEdgesOf(targetInstances.get(ii))) {
						trueTarget = inEdge.target
					}
					targetInstances.set(ii, trueTarget)
				}
			}
		}
		
		// Remove any unconnected implode and explode actors and related interfaces
		val removableVertices = newArrayList // Mark the vertices to be removed
		outputGraph.vertexSet.forEach[vertex |
			if(outputGraph.incomingEdgesOf(vertex).empty) {
				if(vertex instanceof SDFJoinVertex) {
					removableVertices.add(vertex)
				}
			}
			if(outputGraph.outgoingEdgesOf(vertex).empty) {
				if(vertex instanceof SDFForkVertex) {
					removableVertices.add(vertex)
				}
			}
		]
		removableVertices.forEach[vertex| // Remove the actual vertex
			outputGraph.removeVertex(vertex)
		]
		
		// Make sure all the ports are in order
		if(!SpecialActorPortsIndexer.checkIndexes(outputGraph)) {
			throw new SDF4JException("There are still special actors with non-indexed ports. Contact PREESM developers")
		}
		SpecialActorPortsIndexer.sortIndexedPorts(outputGraph)
	}
	
	/**
	 * Update the relevant data-structures associated with the class. 
	 */
	protected def void updateMaps(SDFAbstractVertex sourceInstance, SDFAbstractVertex exImInstance) {
		explodeImplodeOrigInstances.put(exImInstance, sourceInstance)
		val actor = instance2Actor.get(sourceInstance)
		val updatedInstances = actor2Instances.get(actor)
		updatedInstances.add(exImInstance)
		actor2Instances.put(actor, updatedInstances)
		instance2Actor.put(exImInstance, actor)
	}
}