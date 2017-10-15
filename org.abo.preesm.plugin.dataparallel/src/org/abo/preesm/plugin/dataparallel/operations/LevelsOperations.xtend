package org.abo.preesm.plugin.dataparallel.operations

import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.iterator.DAGTopologicalIterator
import org.abo.preesm.plugin.dataparallel.iterator.DAGTopologicalIteratorInterface
import org.abo.preesm.plugin.dataparallel.iterator.SubsetTopologicalIterator
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * DAG Operation to obtain the level set of a DAG.
 * Use {@link OperationsUtils} to obtain level sets and other information
 * that can be gleamed from levels.
 * 
 * @author Sudeep Kanur
 */
class LevelsOperations implements DAGCommonOperations {
	
	private var SDFGraph dag
	
	private var List<SDFAbstractVertex> seenNodes
	
	private var DAGTopologicalIteratorInterface iterator
	
	private var Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	private var Map<SDFAbstractVertex, SDFAbstractVertex> forkJoinOrigInstance
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, Integer> levels
	
	new() {
		levels = newHashMap
		instanceSources = newHashMap
	}
	
	/**
	 * Helper function to compute all the levels. 
	 * The computation leverages the fact that only those instances
	 * seen in a DAG is considered. Hence, the implementation is safe
	 * for a pure DAG as well as a subset of a DAG.
	 */
	protected def void computeAllLevels() {
		dag.vertexSet
			.filter[instance | seenNodes.contains(instance)]
			.forEach[instance | levels.put(instance, new Integer(0))]
		while(iterator.hasNext()) {
			val seenNode = iterator.next()
			val predecessors = iterator.instanceSources.get(seenNode)
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
	}
	
	/**
	 * Visitor method to compute levels of a {@link SDF2DAG} instance.
	 * <p>
	 * Use {@link LevelsOperations#levels} to get the levels computed.
	 * 
	 * @param dagGen A {@link SDF2DAG} instance
	 */
	override visit(SDF2DAG dagGen) {
		dag = dagGen.outputGraph
		seenNodes = dag.vertexSet.toList
		iterator = new DAGTopologicalIterator(dagGen)
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources.putAll(iterator.instanceSources)
		computeAllLevels
	}
	
	/**
	 * Visitor method to compute levels of a {@link DAGSubset} instance.
	 * <p> 
	 * Use {@link LevelsOperations#levels} to get the levels computed.
	 * <p>
	 * @param dagGen A {@link DAGSubset} instance
	 */
	override visit(DAGSubset dagGen) {
		dag = dagGen.inputGraph
		seenNodes = dagGen.seenNodes
		
		// Get the root node and subsequently the iterator
		val rootVisitor = new RootExitOperations()
		dagGen.accept(rootVisitor)
		val rootNodes = rootVisitor.rootInstances
		if(rootNodes.size !== 1) {
			throw new DAGComputationBug("Root nodes size must be 1, but is " + rootNodes.size)
		} else if (rootNodes.empty) {
			throw new DAGComputationBug("Root instances set cannot be empty")
		}
		val rootNode = rootNodes.get(0)
		
		iterator = new SubsetTopologicalIterator(dagGen.originalDAG, rootNode)
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources.putAll(new DAGTopologicalIterator(dagGen.originalDAG).instanceSources)
		computeAllLevels
	}
	
	/**
	 * Visitor method to compute levels of a {@link DAG2DAG} instance.
	 * <p>
	 * Use {@link LevelsOperations#levels} to get the levels computed.
	 * 
	 * @param dagGen A {@link DAG2DAG} instance
	 */
	override visit(DAG2DAG dagGen) {
		dag = dagGen.outputGraph
		seenNodes = dag.vertexSet.toList
		iterator = new DAGTopologicalIterator(dagGen)
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources.putAll(iterator.instanceSources)
		computeAllLevels	
	}
	
}