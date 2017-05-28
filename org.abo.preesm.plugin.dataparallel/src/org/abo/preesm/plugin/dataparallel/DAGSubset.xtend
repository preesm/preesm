package org.abo.preesm.plugin.dataparallel

import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.jgrapht.alg.CycleDetector
import javax.naming.OperationNotSupportedException
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations

/**
 * A subset of DAG is the set of all the instances that has a reachable path 
 * from the specified root instances to all the reachable exit nodes
 * The class does not create a new DAG, but only filters the current ones to
 * give relevant actor to instances table, explode/implode to relevant instance table
 * and instance to actor table.
 *  
 * @autor Sudeep Kanur
 */
class DAGSubset extends AbstractDAGConstructor {
	/**
	 * Holds the original DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	private val SDFGraph inputGraph
	
	/**
	 * Holds the root node
	 */
	private val SDFAbstractVertex rootNode
	
	/**
	 * Holds the original DAG constructor
	 */
	private val SDF2DAG dagGen
	
	/**
	 * List of nodes that are seen in the current subset of DAG
	 */
	private val List<SDFAbstractVertex> seenNodes
	
	/**
	 * Constructor
	 * 
	 * @param dagGen {@link SDF2DAG} instance containing original DAG
	 * @param rootNode A root node that is used to construct subset of DAG
	 * @param logger Logger instance to log debug messages to output
	 * @throws SDF4JException If the input graph is not valid
	 */
	new(SDF2DAG dagGen, SDFAbstractVertex rootNode, Logger logger) throws SDF4JException {
		super(logger)
		
		// Check if input is valid
		try{
			this.inputGraph = dagGen.getOutputGraph	
		} catch(OperationNotSupportedException o) {
			throw new SDF4JException("The DAG should be original DAG, not an instance of DAGSubset")
		}
		
		this.rootNode = rootNode
		this.dagGen = dagGen
		checkInputIsValid()
				
		// Create subset of the new root node
		this.seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).toList
	}
	
	/**
	 * Constructor
	 * 
	 * @param dagGen {@link SDF2DAG} object containing original dag
	 * @param rootNode A root node that is used to construct subset of DAG
	 * @throws SDF4JException If the input graph is not valid
	 */
	new(SDF2DAG dagGen, SDFAbstractVertex rootNode) throws SDF4JException {
		this(dagGen, rootNode, null)
	}
	
	/**
	 * Filter the relevant instances and actors from the original map
	 * Returns a filtered copy of original look up table
	 * 
	 * @return Look up table consisting of relevant actors and its instances 
	 */
	public override Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActor2Instances() {
		val actor2Instances = new HashMap(dagGen.getActor2Instances)
		for(actor: dagGen.actor2Instances.keySet) {
			val instances = actor2Instances.get(actor).filter[instance | seenNodes.contains(instance)].toList
			if(instances.isEmpty){
				actor2Instances.remove(actor)
			} else {
				actor2Instances.put(actor, instances)
			}
		}
		return actor2Instances
	}
	
	/**
	 * Filter the relevant instances and actors from the original map
	 * Returns a copy of filtered instances
	 * 
	 * @return Lookup table consisting of instances and its relevant actors
	 */
	public override Map<SDFAbstractVertex, SDFAbstractVertex> getInstance2Actor() {
		return new HashMap(dagGen.instance2Actor.filter[key, value | seenNodes.contains(key)])
	}
	
	/**
	 * Filter the relevant instances and implode/explode from the original map
	 * Returns a copy of filtered implode/explode and its instances
	 * 
	 * @return Lookup table consisting of implode/explode instances belonging to the DAG and its instances
	 */
	public override Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances() {
		return new HashMap(dagGen.getExplodeImplodeOrigInstances.filter[key, value | seenNodes.contains(value)])
	}
	
	/**
	 * The class does not modify the input DAG, but only the associated data-structures.
	 * No there is nothing to return. Either use the inputGraph or original 
	 * DAG that was passed
	 * 
	 * @throws OperationNotSupportedException 
	 */
	public override SDFGraph getOutputGraph() {
		throw new OperationNotSupportedException("This object does not construct subset. Nothing to return")
	}
	
	/**
	 * Check whether the input is valid. Following checks are made
	 * Cycles, delays and repetition vector, root node exist and is valid root node
	 * 
	 * @return true if input is valid, or exception is thrown (false is never returned)
	 * @throws SDF4JException if the input graph is not a valid DAG or if root node does not exist
	 */
	override checkInputIsValid() throws SDF4JException {
		// Check if there are cycles
		val cycleDetector = new CycleDetector<SDFAbstractVertex, SDFEdge>(inputGraph)
		if(cycleDetector.detectCycles) {
			throw new SDF4JException("Cycles found in DAG. DAG can't have cycles!")
		}
		
		// Check if there are any delays
		for(edge: inputGraph.edgeSet) {
			if(edge.delay.intValue != 0) {
				throw new SDF4JException("Delay of " + edge.delay.intValue + " found at edge between " 
					+ edge.source.name + " and " + edge.target.name + ". DAG must have no delays!" )
			}
		}
		
		// Check that repetition vector is exactly one for all the vertices
		for(node: inputGraph.vertexSet) {
			if(node.nbRepeatAsInteger != 1) {
				throw new SDF4JException("Node " + node.name + " has repetition count of " + node.nbRepeatAsInteger
					+ ". DAG must have all repetition counts as one.");
			}	
		}
		
		// Input graph can't be hierarchical as its already arriving from DAGConstructor
		
		// Check if root node exists
		if(!inputGraph.vertexSet.contains(rootNode)) {
			throw new SDF4JException("Root node " + rootNode.name + " does not exist in the graph")
		}
		
		// The root instance should belong to the original DAG 
		if(!new DAGFromSDFOperations(dagGen).rootInstances.contains(rootNode)) {
			throw new SDF4JException("Node " + rootNode.name + " is not a root node of the graph")
		}
		
		return true
	}
	
}