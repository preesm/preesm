package org.abo.preesm.plugin.dataparallel.operations

import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.iterator.SubsetTopologicalIterator
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex

/**
 * DAG operation that finds the instances that needs to be moved in the
 * transient DAG
 * 
 * @author Sudeep Kanur
 */
class MovableInstances implements DAGOperations {
	
	/**
	 * The lookup table of instance and levels that are
	 * obtained after rearranging.
	 * Apart from acyclic-like SDFGs, this map is not 
	 * guaranteed to be parallel.
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, Integer> rearrangedLevels
	
	/**
	 * List of all the instances that are to be moved in the 
	 * transient DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> movableInstances
	
	/**
	 * List of all the instances that also form root instance of
	 * some or the other cycle
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> movableRootInstances
	
	/**
	 * List of all the instances that form the end of the chain
	 * starting from the {@link MovableInstances#movableRootInstances}
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> movableExitInstances
	
	new() {
		rearrangedLevels = newHashMap
		movableInstances = newArrayList
		movableRootInstances = newArrayList
		movableExitInstances = newArrayList
	}
	
	/**
	 * Helper function that finds instances that are to be moved to the transient graph. The
	 * detailed implementation algorithm is found in DASIP-2017 paper.
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @throws SDF4JException If the DAG is not instance independent
	 */
	private def void findMovableInstances(PureDAGConstructor dagGen) throws SDF4JException {
		val parallelVisitor = new DependencyAnalysisOperations
		dagGen.accept(parallelVisitor)
		
		val isDAGInd = parallelVisitor.isIndependent
		if(!isDAGInd) {
			throw new SDF4JException("DAG is not instance independent. Getting movable instance" + 
				" is meaningless.")
		}
		
		// Get all the root instances
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val rootInstances = rootVisitor.rootInstances
		
		// We want pick anchor instance from an actor that is not a 
		// source actor and is part of strongly connected component.
		val nonSourceActors = rootVisitor.rootActors.filter[actor |
			!actor.sources.empty
		].toList
		
		val anchorActor = OperationsUtils.pickElement(nonSourceActors)
		// Get all associated instances that belong to same actor
		val anchorInstances = dagGen.actor2Instances.get(anchorActor).filter[instance |
			rootInstances.contains(instance)
		].toList
		
		val relevantRootInstances = rootInstances.filter[instance |
			!anchorInstances.contains(instance)
		].toList
		
		// Get levels
		val levelVisitor = new LevelsOperations
		dagGen.accept(levelVisitor)
		rearrangedLevels.putAll(levelVisitor.levels)
		
		rearrangeAcyclic(dagGen, relevantRootInstances)
		
		// Maximum level starting from 0, where the instances need to be moved.
		// Make sure the instances from source actors are ignored, otherwise, lbar
		// will always result to 0. This can be done by removing all edges that has delays
		// greater than or equal to production rate (or consumption rate)
		val lbar = (new GetParallelLevelBuilder)
					.addDagGen(dagGen)
					.addOrigLevels(rearrangedLevels)
					.addSubsetLevels(rearrangedLevels)
					.build()
		
		anchorInstances.forEach[anchor |
			val sit = new SubsetTopologicalIterator(dagGen, anchor)
			while(sit.hasNext) {
				val instance = sit.next
				val instanceLevel = rearrangedLevels.get(instance)
				if(instanceLevel < lbar) {
					movableInstances.add(instance)
					
					if(instanceLevel == 0 && !(instance instanceof SDFForkVertex)) {
						movableRootInstances.add(instance)
					} else if(instanceLevel == (lbar -1)) {
						// Check if there is an explode instance of this instance
						if(!(instance instanceof SDFForkVertex) && !(instance instanceof SDFJoinVertex)) {
							val expImpInstances = dagGen.explodeImplodeOrigInstances.filter[expImp, origInstance |
								(origInstance == instance) && (expImp instanceof SDFForkVertex)
							]
							if(expImpInstances.empty) {
								// No explode instance associated with this
								movableExitInstances.add(instance)
							} 
							// else wait until its explode instance is seen and then add it
						} else {
							if(instance instanceof SDFForkVertex) {
								movableExitInstances.add(instance)
							}
						}
					}
				}
			}
		]
	}
	
	/**
	 * Helper function to completely sort acyclic-like DAGs and partially
	 * sort cyclic-DAGs. The algorithm and its description is given in 
	 * DASIP-2017 paper.
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param rootInstance Root instances that needs to be considered for sorting
	 */
	private def void rearrangeAcyclic(PureDAGConstructor dagGen, 
		List<SDFAbstractVertex> rootInstances) {
		/*
		 * Rearranging level set is same as rearranging levels
		 * Getting levels sets is computed based on levels. So we
		 * just manipulate the levels of the node
		 */
		
		// Get root instances
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val dagGenRootInstances = rootVisitor.rootInstances
		val relevantRootInstances = dagGenRootInstances.filter[ instance |
			rootInstances.contains(instance)
		]
		if(relevantRootInstances.empty && !rootInstances.empty) {
			throw new DAGComputationBug("Instances passed as root may not be in root instances set")
		}
		
		relevantRootInstances.forEach[rootNode |
			val actor = dagGen.instance2Actor.get(rootNode)
			if(actor === null) {
				throw new DAGComputationBug("Instance to actor map should return appropriate"					
				+ " actor for an instance")
			}
			
			if(OperationsUtils.getMaxActorLevel(dagGen, actor) > 0) {
				// All the instances of this actor needs rearranging
				val instancesInRoot = relevantRootInstances.filter[instance |
					dagGen.actor2Instances.get(actor).contains(instance)
				]
				
				// Take each instance that needs rearranging as root node, construct a
				// DAG subset and rearrange all the nodes seen in the path
				instancesInRoot.forEach[instance |
					val sit = new SubsetTopologicalIterator(dagGen, instance)
					val instanceSources = sit.instanceSources
					val seenNodes = newHashSet // List of nodes that are not implode/explode but are seen during iteration
					while(sit.hasNext) {
						val node = sit.next
						var levelOfNode = 0
						
						// Calculate the current level of the node
						val predecessors = instanceSources.get(node)
						if(predecessors.isEmpty) {
							levelOfNode = 0
							seenNodes.add(node)
						} else {
							val predecessorLevel = rearrangedLevels.filter[ vertex, value |
								predecessors.contains(vertex)].values.max
								
							if(dagGen.explodeImplodeOrigInstances.keySet.contains(node)) {
								val origInstance = dagGen.explodeImplodeOrigInstances.get(node)
								if(seenNodes.contains(origInstance)){
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

						rearrangedLevels.put(node, levelOfNode)
							
						val maxActorLevel = OperationsUtils.getMaxActorLevel(dagGen, 
							dagGen.instance2Actor.get(node), rearrangedLevels)
						if(levelOfNode != maxActorLevel) {
							// Change the levels of this node and associated fork/joins
							val forkJoinInstances = dagGen.explodeImplodeOrigInstances.filter[forkJoin, origInstance |
								origInstance == node
							]
							forkJoinInstances.forEach[forkJoin, level |
								rearrangedLevels.put(forkJoin, maxActorLevel)
							]
							
							rearrangedLevels.put(node, maxActorLevel)
						}
					}
				]
			}
		]
		
		// Update the levels of implode/expldoe instances not seen by the root instance,
		// but may be connected through some other path
		// Properly set the implode and explode according to the level of its original instance
		// TODO Iterate only through implode explode instances instead of checking the existence of node
		rearrangedLevels.keySet.forEach[vertex |
			if(dagGen.explodeImplodeOrigInstances.keySet.contains(vertex)) {
				val origNode = dagGen.explodeImplodeOrigInstances.get(vertex)
				rearrangedLevels.put(vertex, rearrangedLevels.get(origNode))
			}
		]
	}
	
	/**
	 * Visitor method that provides movable instances of a {@link SDF2DAG}
	 * instance.
	 * <p>
	 * Use {@link MovableInstances#movableInstances} to get list of movable instances
	 * @throws SDF4JException If the DAG is not instance independent.
	 */
	override visit(SDF2DAG dagGen) {
		findMovableInstances(dagGen)
	}
	
	/**
	 * Visitor method that provides movable instances of a {@link DAG2DAG}
	 * instance.
	 * <p>
	 * Use {@link MovableInstances#movableInstances} to get list of movable instances
	 * @throws SDF4JException If the DAG is not instance independent.
	 */
	override visit(DAG2DAG dagGen) {
		findMovableInstances(dagGen)
	}
	
}