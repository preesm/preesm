package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.List
import java.util.Map
import java.util.Set
import java.util.logging.Level
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.traverse.GraphIterator
import org.jgrapht.traverse.TopologicalOrderIterator
import org.abo.preesm.plugin.dataparallel.SDF2DAG

/**
 * Implementation of {@link DAGOperations} for DAGs constructed from
 * SDFs
 * 
 * @author Sudeep Kanur
 */
class DAGFromSDFOperations implements DAGOperations {
	
	/**
	 * Holds the original DAGConstructor instance
	 */
	protected var SDF2DAG dagGen
	
	/**
	 * Holds the original DAG
	 */
	protected var SDFGraph inputGraph
	
	/**
	 * Optional logging
	 */
	protected var Logger logger
	
	/**
	 * Look up table of instances and its levels
	 */
	protected val Map<SDFAbstractVertex, Integer> levels
	
	/**
	 * Flag to avoid recomputing levels again. Its an expensive
	 * operation
	 */
	private var boolean computeLevels
	
	/**
	 * Lookup table of instances and its source instances
	 */
	protected var Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	/**
	 * The topological iterator used. Could be TopologicaOrderIterator or 
	 * SubsetTopologicalIterator
	 */
	protected var GraphIterator<SDFAbstractVertex, SDFEdge> iterator
	
	/**
	 * The map of explode and implode instances linked to its original instance
	 * This is extracted from dagGen
	 */
	protected var Map<SDFAbstractVertex, SDFAbstractVertex> forkJoinOrigInstance
	
	/**
	 * Relevant nodes seen in the DAG or its subset
	 */
	protected var List<SDFAbstractVertex> seenNodes
	
	/**
	 * Set of non-data parallel actors
	 */
	protected val Set<SDFAbstractVertex> nonParallelActors
	
	/**
	 * Constructor used for test setup
	 * 
	 * @param dagGen The {@link DAGConstructor} instance
	 */
	new(SDF2DAG dagGen) {
		this(dagGen, null)
	}
	
	/** Constructor used in plugin
	 * 
	 * @param dagGen A {@link SDF2DAG} instance
	 * @param logger Workflow logger
	 */
	new(SDF2DAG dagGen, Logger logger) {
		this.dagGen = dagGen
		this.logger = logger
		this.inputGraph = dagGen.outputGraph
		iterator = new TopologicalOrderIterator(inputGraph)
		seenNodes = new TopologicalOrderIterator(inputGraph).toList
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources = newHashMap()
		inputGraph.vertexSet.forEach[instance |
			instanceSources.put(instance, 
								inputGraph.incomingEdgesOf(instance)
									.map[edge | edge.source].toList)
		]
		// Anything above this line should be overridden
		levels = newHashMap()
		nonParallelActors = newHashSet()
		computeLevels = false
	}
	
	/**
	 * Optionally log message when {@link DAGFromSDFOperations#new(dagGen, logger)} is used
	 * 
	 * @param message The message to log
	 */
	protected def void log(String message) {
		logger?.log(Level.INFO, message)
	}
	
	/**
	 * Overrides {@link DAGOperations#getRootInstances}
	 * Filtered by only those instances that are seen in the DAG
	 */
	override getRootInstances() {
		return inputGraph.vertexSet
			.filter[instance | seenNodes.contains(instance)]
			.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0].toList
	}
	
	/**
	 * Overrides {@link DAGOperations#getRootActors}
	 * Filtered by only those actors that are seen in the DAG
	 */
	override getRootActors() {
		return getRootInstances().map[instance | dagGen.instance2Actor.get(instance)].toSet.toList
	}
	
	/**
	 * Overrides {@link DAGOperations#getExitInstances}
	 * Filtered by only those instances that are seen in the DAG
	 */
	override getExitInstances() {
		val rootInstances = getRootInstances()
		return inputGraph.vertexSet
				.filter[instance | seenNodes.contains(instance)]
				.filter[instance |
			 		inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)
				].toList
	}
	
	/**
	 * Overrides {@link DAGOperations#getAllLevels}
	 * Only those instances seen in the DAG are considered
	 */
	public override Map<SDFAbstractVertex, Integer> getAllLevels() {
		if(!computeLevels){
			inputGraph.vertexSet
				.filter[instance | seenNodes.contains(instance)]
				.forEach[instance | levels.put(instance, new Integer(0))]
			while(iterator.hasNext()) {
				val seenNode = iterator.next()
				val predecessors = instanceSources.get(seenNode)
				if(predecessors.isEmpty) {
					levels.put(seenNode, new Integer(0))
				} else {
					val predecessorLevel = levels.filter[node, value | predecessors.contains(node)].values.max
					if(forkJoinOrigInstance.keySet.contains(seenNode)) {
						levels.put(seenNode, new Integer(predecessorLevel))
					} else {
						levels.put(seenNode, new Integer(predecessorLevel+1))
					}
				}
			}
			
			// Properly set the implode and explode according to the level of its original
			levels.keySet.forEach[node |
				if(forkJoinOrigInstance.keySet.contains(node)) 
					levels.put(node, levels.get(forkJoinOrigInstance.get(node)))
			]
			computeLevels = true
		}
		return levels
	}
	
	/**
	 * Overrides {@link DAGOperations#getMaxLevel}
	 */
	public override int getMaxLevel() {
		return (getAllLevels.values.max + 1)
	}
	
	/**
	 * Overrides {@link DAGOperations#getLevelSets}
	 */
	public override List<List<SDFAbstractVertex>> getLevelSets() {
		val List<List<SDFAbstractVertex>> levelSet = newArrayList()
		(0..<getMaxLevel).forEach[levelSet.add(newArrayList)]
		getAllLevels.forEach[instance, level | 
			levelSet.get(level).add(instance)
		]
		return levelSet
	}
	
	/**
	 * Overrides {@link DAGOperations#isDAGInd}
	 */
	override boolean isDAGInd() {
		val dagIndState = newArrayList
		rootInstances.forEach[rootNode |
			val dagOps = new DAGSubsetOperations(dagGen, rootNode)
			dagIndState.add(dagOps.isDAGInd)
			nonParallelActors.addAll(dagOps.getNonParallelActors)
		]
		return dagIndState.forall[state | state == true]
	} 
	
	/**
	 * Overrides {@link DAGOperations#getNonParallelActors}
	 */
	override getNonParallelActors() {
		return nonParallelActors
	}
}