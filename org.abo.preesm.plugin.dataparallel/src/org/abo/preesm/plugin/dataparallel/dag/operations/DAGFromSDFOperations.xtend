package org.abo.preesm.plugin.dataparallel.dag.operations

import org.abo.preesm.plugin.dataparallel.dag.operations.GenericDAGOperations
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import java.util.logging.Logger
import java.util.Map
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.jgrapht.traverse.TopologicalOrderIterator
import org.eclipse.xtend.lib.annotations.Accessors
import java.util.List
import org.jgrapht.traverse.GraphIterator
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import java.util.ArrayList

class DAGFromSDFOperations extends GenericDAGOperations {
	
	/**
	 * Look up table of instances and its levels
	 */
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, Integer> levels
	
	/**
	 * Flag to avoid recomputing levels again. Its an expensive
	 * operation
	 */
	var boolean computeLevels
	
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val GraphIterator<SDFAbstractVertex, SDFEdge> iterator
	
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, SDFAbstractVertex> forkJoinOrigInstance
	
	new(DAGConstructor dagGen) {
		this(dagGen, null)
	}
	
	new(DAGConstructor dagGen, Logger logger) {
		super(dagGen, logger)
		
		levels = newHashMap()
		inputGraph.vertexSet.forEach[instance | levels.put(instance, new Integer(0))]
		computeLevels = false
		instanceSources = newHashMap()
		inputGraph.vertexSet.forEach[instance |
			instanceSources.put(instance, 
								inputGraph.incomingEdgesOf(instance).map[edge | edge.source].toList)
		]
		iterator = new TopologicalOrderIterator(inputGraph)
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
	}
	
	public override Map<SDFAbstractVertex, Integer> getAllLevels() throws UnsupportedOperationException {
		if(!computeLevels){
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
	
	public override int getMaxLevel() throws UnsupportedOperationException {
		return (getAllLevels.values.max + 1)
	}
	
	public override List<List<SDFAbstractVertex>> getLevelSets() throws UnsupportedOperationException {
		val List<List<SDFAbstractVertex>> levelSet = new ArrayList(getMaxLevel)
		getAllLevels.forEach[instance, level | 
			levelSet.get(level).add(instance)
		]
		return levelSet
	}
}