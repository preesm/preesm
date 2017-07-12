package org.abo.preesm.plugin.dataparallel.operations.visitor

import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.iterator.SubsetTopologicalIterator
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * DAG operation that rearranges the DAG to give out a cyclic
 * and transient DAG. The original DAG is unmodified and only 
 * the newly created DAG and its levels are returned
 * 
 * @author Sudeep Kanur
 */
class RearrangeOperations extends CyclicSDFGOperations implements DAGOperations {
	
	/**
	 * The cyclic DAG after rearranging operation.
	 * Regardless of the type of the DAG, as long as it
	 * is instance independent, the cyclic DAG is provided
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var PureDAGConstructor dagC
	
	/**
	 * The transient DAG after rearranging operation.
	 * For an acyclic-like DAG/SDFG, transient DAG is null.
	 * Otherwise, this DAG is the initial schedule that must
	 * be run before running {@link RearrangeOperations#dagC}
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var PureDAGConstructor dagT
	
	/**
	 * The lookup table of instance and levels for the 
	 * {@link RearrangeOperations#dagC} after rearranging
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, Integer> dagCLevels
	
	/**
	 * The lookup table of instances and levels for the
	 * {@link RearrangeOperations#dagT} after rearranging
	 * This is empty when {@link RearrangeOperations#dagT} is
	 * null
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, Integer> dagTLevels
	
	new() {
		super()
		dagC = null
		dagT = null
		dagCLevels = newHashMap
		dagTLevels = newHashMap
	}
	
	/**
	 * Top level helper function that rearranges a DAG according
	 * to algorithm 3 of the DASIP-2017 paper
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @throws SDF4JException If the DAG is not instance independent
	 */
	protected def void rearrange(PureDAGConstructor dagGen) throws SDF4JException {
		val  parallelVisitor = new DependencyAnalysisOperations
		dagGen.accept(parallelVisitor)
		val isDAGInd = parallelVisitor.isIndependent
		if(!isDAGInd) {
			throw new SDF4JException("DAG is not instance independent. Rearranging is meaningless")
		}
		
		// Initialize all the relevant data-structure
		dagC = new DAG2DAG(dagGen, dagGen.logger)
		
		// Get all root instances
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val rootInstances = rootVisitor.rootInstances
		
		// Get levels
		val levelVisitor = new LevelsOperations
		dagGen.accept(levelVisitor)
		dagCLevels.putAll(levelVisitor.levels)
		
		// Get cycles 
		super.computeCycleRoots(dagGen)
		if(!super.containsCycles) {
			// DAG is acyclic-like
			rearrange(dagC, rootInstances)
		} else {
			dagT = new DAG2DAG(dagGen, dagGen.logger)
			// DAG is non-acyclic like
			val allInstancesInCycles = newArrayList
			val cycles = super.cycleRoots
			cycles.forEach[cycle | 
				allInstancesInCycles.addAll(cycle)
			]
			
			cycles.forEach[cycle |
				val rootInstancesToBeSorted = newArrayList
				val anchor = OperationsUtils.pickElement(cycle)
				val instancesOfOtherCycles = allInstancesInCycles.filter[instance | !cycle.contains(instance)].toList
				val sourceInstances = dagGen.sourceInstances
				
				// Filter out instances from other cycles and source instances
				rootInstances.forEach[instance |
					if(!(instancesOfOtherCycles.contains(instance)) && !(sourceInstances.contains(instance)) && (instance != anchor)) {
						rootInstancesToBeSorted.add(instance)
					}
				]
				rearrange(dagC, rootInstancesToBeSorted)
			]
			// Now obtain dagC and dagT
		}
	}
	
	/**
	 * Helper function to completely sort acyclic-like DAGs and
	 * partially sort cyclic-DAGs. The algorithm and its description
	 * is given in DASIP-2017 paper
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param rootInstance Root instances that needs to be considered for sorting
	 */
	protected def void rearrange(PureDAGConstructor dagGen, List<SDFAbstractVertex> rootInstances) {
		/*
		 * Rearranging level set is same as rearranging levels
		 * Getting level sets is computed based on levels. So
		 * we just manipulate the levels of the node
		 */
		
		// Get root instances
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val dagGenRootInstances = rootVisitor.rootInstances
		val relevantRootInstances = dagGenRootInstances.filter[instance | rootInstances.contains(instance)]
		
		if(relevantRootInstances.empty && !rootInstances.empty) {
			throw new SDF4JException("Instances passed as root may not be in root instances set")
		}
		
		relevantRootInstances.forEach[rootNode |
			val actor = dagGen.instance2Actor.get(rootNode)
			if(actor === null) {
				throw new DAGComputationBug("Instance to Actor map should return appropriate actor for an instance")
			}
			
			if(OperationsUtils.getMaxActorLevel(dagGen, actor) > 0) {
				// All the instances of this actor needs rearranging
				val instancesInRoot = relevantRootInstances.filter[instance | dagGen.actor2Instances.get(actor).contains(instance)]
				
				// Take each instance that needs rearranging as root node, construct
				// a DAG subset and rearrange all the nodes seen in its path
				instancesInRoot.forEach[instance |
					val sit = new SubsetTopologicalIterator(dagGen, instance)
					val instanceSources = sit.instanceSources
					val seenNodes = newHashSet // List of nodes that are not implode/explode node but are seen during iteration
					while(sit.hasNext) {
						val node = sit.next
						var levelOfNode = 0
						
						// Calculate the current level of the node
						val predecessors = instanceSources.get(node)
						if(predecessors.isEmpty) {
							levelOfNode = 0
							seenNodes.add(node)
						} else {
							val predecessorLevel = dagCLevels.filter[vertex, value | predecessors.contains(vertex)].values.max
							
							if(dagGen.explodeImplodeOrigInstances.keySet.contains(node)) {
								val origInstance = dagGen.explodeImplodeOrigInstances.get(node)
								if(seenNodes.contains(origInstance)) {
									levelOfNode = predecessorLevel
								} else {
									levelOfNode = predecessorLevel + 1
									seenNodes.add(origInstance)
								}
							} else {
								if(seenNodes.contains(node)) {
									levelOfNode = predecessorLevel
								} else {
									levelOfNode = predecessorLevel + 1
									seenNodes.add(node)
								}
							}
						}
			
						dagCLevels.put(node, levelOfNode)
						
						val maxActorLevel = OperationsUtils.getMaxActorLevel(dagGen, dagGen.instance2Actor.get(node), dagCLevels)
						if(levelOfNode != maxActorLevel) {
							// Change the levels of this node and
							// associated fork/joins
							
							val forkJoinInstances = dagGen.explodeImplodeOrigInstances.filter[forkJoin, origInstance |
								origInstance == node
							]
							if(!forkJoinInstances.isEmpty) {
								forkJoinInstances.forEach[forkJoin, level |
									dagCLevels.put(forkJoin, maxActorLevel)
								]
							}
							dagCLevels.put(node, maxActorLevel)
						}
					}
				]
			}
		]
		
		// Update the levels of implode/explode instances not seen by the root instance,
		// but may be connected through some other path
		// Properly set the implode and explode according to the level of its original
		dagCLevels.keySet.forEach[vertex |
			if(dagGen.explodeImplodeOrigInstances.keySet.contains(vertex)) {
				val origNode = dagGen.explodeImplodeOrigInstances.get(vertex)
				dagCLevels.put(vertex, dagCLevels.get(origNode))								
			}
				
		]
	}
	
	/**
	 * Visitor method that rearranges a {@link SDF2DAG} instance
	 * 
	 * Use {@link RearrangeOperations#dagC} to get cyclic DAG
	 * Use {@link RearrangeOperations#dagT} to get transient DAG
	 * Use {@link RearrangeOperations#dagCLevels} to get levels of cyclic DAG
	 * Use {@link RearrangeOperations#dagTLevels} to get levels of transient DAG
	 * 
	 * @param dagGen A {@link SDF2DAG} instance
	 */
	override visit(SDF2DAG dagGen) {
		rearrange(dagGen)
	}
	
	/**
	 * Visitor method that rearranges a {@link SDF2DAG} instance
	 * 
	 * Use {@link RearrangeOperations#dagC} to get cyclic DAG
	 * Use {@link RearrangeOperations#dagT} to get transient DAG
	 * Use {@link RearrangeOperations#dagCLevels} to get levels of cyclic DAG
	 * Use {@link RearrangeOperations#dagTLevels} to get levels of transient DAG
	 * 
	 * @param dagGen A {@link DAG2DAG} instance
	 */
	override visit(DAG2DAG dagGen) {
		rearrange(dagGen)
	}
}