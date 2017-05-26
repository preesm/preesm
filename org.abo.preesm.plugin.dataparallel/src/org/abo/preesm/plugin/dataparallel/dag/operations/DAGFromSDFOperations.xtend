package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.traverse.GraphIterator
import org.jgrapht.traverse.TopologicalOrderIterator

class DAGFromSDFOperations implements DAGOperations {
	
	protected var DAGConstructor dagGen
	
	protected var SDFGraph inputGraph
	
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
	
	protected var Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	protected var GraphIterator<SDFAbstractVertex, SDFEdge> iterator
	
	protected var Map<SDFAbstractVertex, SDFAbstractVertex> forkJoinOrigInstance
	
	protected var List<SDFAbstractVertex> seenNodes
	
	new(DAGConstructor dagGen) {
		this(dagGen, null)
	}

	new(DAGConstructor dagGen, Logger logger) {
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
		computeLevels = false
	}
	
	protected def void log(String message) {
		logger?.log(Level.INFO, message)
	}
	
	override getRootInstances() {
		return inputGraph.vertexSet
			.filter[instance | seenNodes.contains(instance)]
			.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0].toList
	}
	
	override getRootActors() {
		return getRootInstances().map[instance | dagGen.instance2Actor.get(instance)].toSet.toList
	}
	
	override getExitInstances() {
		val rootInstances = getRootInstances()
		return inputGraph.vertexSet
				.filter[instance | seenNodes.contains(instance)]
				.filter[instance |
			 		inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)
				].toList
	}
	
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
	
	public override int getMaxLevel() {
		return (getAllLevels.values.max + 1)
	}
	
	public override List<List<SDFAbstractVertex>> getLevelSets() {
		val List<List<SDFAbstractVertex>> levelSet = new ArrayList(getMaxLevel)
		getAllLevels.forEach[instance, level | 
			levelSet.get(level).add(instance)
		]
		return levelSet
	}
}