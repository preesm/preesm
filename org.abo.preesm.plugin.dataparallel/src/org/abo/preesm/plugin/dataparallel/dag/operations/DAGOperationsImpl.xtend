package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.List
import java.util.Map
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.DAGTopologicalIterator
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.jgrapht.traverse.BreadthFirstIterator
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor

/**
 * Implementation of {@link DAGOperations} for DAGs constructed from
 * SDFs
 * 
 * @author Sudeep Kanur
 */
final class DAGOperationsImpl extends AbstractDAGCommonOperations implements DAGOperations {
	
	/**
	 * Holds the loop schedule
	 */
	protected var SDFGraph dagC
	
	/**
	 * Holds the transient schedule
	 */
	protected var SDFGraph dagT
	
	/**
	 * Hold the cycles in the DAG (only if its instance independent)
	 */
	protected val List<List<SDFAbstractVertex>> cycleRoots
	
	/**
	 * Flag to avoid recomputing cycles in DAG
	 */
	protected var boolean computeCycles
	
	/**
	 * Constructor used for test setup
	 * 
	 * @param dagGen The {@link DAGConstructor} instance
	 */
	new(PureDAGConstructor dagGen) {
		this(dagGen, null)
	}
	
	/** Constructor used in plugin
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param logger Workflow logger
	 */
	new(PureDAGConstructor dagGen, Logger logger) {
		super(dagGen, logger)

		this.cycleRoots = newArrayList()
		this.dagC = dagGen.outputGraph
		this.dagT = new SDFGraph
		
		computeDAGInd = false
		computeCycles = false
		
		// Do all associated computations
		initialize()
	}
	
	override initialize() {
		iterator = new DAGTopologicalIterator(dagGen)
		seenNodes = new DAGTopologicalIterator(dagGen).toList
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources = newHashMap()
		inputGraph.vertexSet.forEach[instance |
			instanceSources.put(instance, 
								inputGraph.incomingEdgesOf(instance)
									.map[edge | edge.source].toList)
		]
		super.initialize
		isDAGInd
	}
	
	/**
	 * Get seen nodes in the DAG
	 * 
	 * @return Nodes seen in the DAG
	 */
	override getSeenNodes() {
		return seenNodes
	}
	
	/**
	 * Get the appropriate topological order iterator for the DAG
	 * The topological iterator for a subset of a DAG is different
	 * than the topological iterator of the pure DAG
	 * 
	 * @return Appropriate topological order iterator
	 */
	override getIterator() {
		return iterator
	}
	
	/**
	 * Get fork and join (explode and implode) instances
	 * 
	 * @return Lookup table of fork-join instances mapped to its original instance
	 */
	override getForkJoinOrigInstance() {
		return forkJoinOrigInstance
	}
	
	/**
	 * Get sources of instances
	 * 
	 * @return Lookup table of instances and a list of its sources
	 */
	override getInstanceSources() {
		return instanceSources
	}
	
	/**
	 * Implements {@link DAGOperations#getParallelLevel}
	 */
	public override int getParallelLevel(Map<SDFAbstractVertex, Integer> ls) {
		for(level: getLevelSets(ls)) {
			val currentLevel = ls.get(level.get(0)) 
			val actors = newHashSet()
			val allInstances = newArrayList()
			// Get all actors in the current level
			level.forEach[instance | actors.add(dagGen.instance2Actor.get(instance))]
			
			// Get all instances of the actors seen in current level
			actors.forEach[actor | 
				val instances = dagGen.actor2Instances.get(actor)
				allInstances.addAll(instances)
			]
			
			// A level set is parallel, if a level set contains all instances of all actors
			if(allInstances.filter[instance | !level.contains(instance)].empty) {
				return currentLevel
			}
		}
		// No parallel level exists in the graph
		return -1
	}
	
	/**
	 * Implements {@link DAGOperations#getParallelLevel}
	 */
	public override getParallelLevel() {
		return getParallelLevel(getAllLevels)
	}
	
	/**
	 * Overrides {@link DAGOperations#isDAGInd}
	 */
	override boolean isDAGInd() {
		val dagIndState = newArrayList
		rootInstances.forEach[rootNode |
			val dagOps = new DAGSubsetOperationsImpl(dagGen, rootNode)
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
	
	/**
	 * Overrides {@link DAGOperations#isDAGParallel}
	 */
	override isDAGParallel() {
		/*
		 * Logic of implementation is that if all the instances
		 * of an actor is not seen in a given level set, then other
		 * instances must be elsewhere and will be subsequently seen.
		 * When that happens, we return false, otherwise we return
		 * true
		 */
		getLevelSets		
		val instance2Actor = dagGen.instance2Actor
		val seenActors = newHashSet()
		for(levelSet: levelSets) {
			val seenInLevel = newHashSet()
			for(instance: levelSet) {
				val actor = instance2Actor.get(instance)
				if(actor === null) {
					throw new SDF4JException("Bug! Contact Sudeep Kanur (skanur@abo.fi) with the graph that caused the issue")
				}
				if(seenActors.contains(actor)) {
					return false
				}
				seenInLevel.add(actor)
			}
			seenActors.addAll(seenInLevel)
		}
		return true
	}
	
	/**
	 * Overrides {@link DAGOperations#rearrange}
	 */
	override rearrange() throws SDF4JException {
		getAllLevels
		if(!DAGInd){
			throw new SDF4JException("DAG is not instance independent. Rearranging is meaningless.")
		}
		val cycles = getCycleRoots
		if(cycles.empty) {
			// DAG is acyclic like
			rearrange(getRootInstances)
		} else {
			// DAG is non-acylic like
			val allInstancesInCycles = newArrayList()
			cycles.forEach[cycle |
				allInstancesInCycles.addAll(cycle)
			]
			
			cycles.forEach[cycle |
				val rootInstancesToBeSorted = newArrayList()
				val anchor = pickElement(cycle)
				val instancesOfOtherCycles = allInstancesInCycles.filter[instance | !cycle.contains(instance)].toList
				val sourceInstances = dagGen.sourceInstances
				
				// Filter out instances from other cycles and source instances
				getRootInstances.forEach[instance |
					if(!(instancesOfOtherCycles.contains(instance)) && !(sourceInstances.contains(instance)) && (instance != anchor)) {
						rootInstancesToBeSorted.add(instance)
					}
				]
				rearrange(rootInstancesToBeSorted)
			]
			
			// Now obtain DAG_C and DAG_T
		}
		// levels are sorted, send out the level sets
		return getLevelSets
	}
	
	/**
	 * Rearranges the DAG and returns the new level set. 
	 * Only the subset of DAG present in the root instances
	 * 
	 * @param rootInstances
	 */
	protected def List<List<SDFAbstractVertex>> rearrange(List<SDFAbstractVertex> rootInstances) {
		/*
		 * Rearranging level set is same as rearranging levels
		 * Getting level sets is computed based on levels. So 
		 * we just manipulates the levels of the node
		 */
		rootInstances.forEach[rootNode |
			val actor = dagGen.instance2Actor.get(rootNode)
			if(actor === null) {
				throw new SDF4JException("Bug! Contact Sudeep Kanur (skanur@abo.fi) with the graph that caused the exception")
			}
			if(getMaxActorLevel(actor) > 0) {
				// All the instances of this actor needs rearranging
				val instancesInRoot = rootInstances.filter[instance | dagGen.actor2Instances.get(actor).contains(instance)]
				// Take each instance that needs rearranging as root node, construct
				// a DAG subset and rearrange all the nodes seen in its path
				instancesInRoot.forEach[instance |
					val bit = new BreadthFirstIterator(dagGen.outputGraph, instance)
					while(bit.hasNext) {
						val node = bit.next()
						val levelOfNode = levels.get(node)
						val maxActorLevel = getMaxActorLevel(dagGen.instance2Actor.get(node))
						if(levelOfNode != maxActorLevel) {
							// Change the levels of this node and 
							// associated fork/joins
							val forkJoinInstances = dagGen.explodeImplodeOrigInstances.filter[forkJoin, origInstance |
								origInstance == node
							]
							if(!forkJoinInstances.isEmpty) {
								forkJoinInstances.forEach[forkJoin, level | 
									levels.put(forkJoin, maxActorLevel)
								]
							}
							levels.put(node, maxActorLevel)
						}
					}
				]
			}
		]
		return getLevelSets
	}
	
	/**
	 * Overrides {@link DAGOperations#getCycles}
	 */
	override getCycleRoots() {
		if(!computeCycles) {
			if(!isDAGInd) {
				throw new SDF4JException("Invalid DAG! The DAG is not instance independent")
			}
			
			// Set of actors that have already been grouped. This is non-destructive way of computing
			val seenRoots = newHashSet()
			
			rootInstances.forEach[rootInstance |
				val rootActor = dagGen.instance2Actor.get(rootInstance)
				if(!seenRoots.contains(rootActor)) {
					val dependentInstances = newHashSet()
					
					val restInstances = getRootInstances.filter[node | dagGen.instance2Actor.get(node) != rootActor]
					restInstances.forEach[ remainingRootInstance |
						// Check if this subset DAG contains rootInstance
						val remainingRootActor = dagGen.instance2Actor.get(remainingRootInstance)
						if(new DAGSubset(dagGen, remainingRootInstance).actor2Instances.keySet.contains(rootActor)) {
							// Then perform a counter check
							
							if(new DAGSubset(dagGen, rootInstance).actor2Instances.keySet.contains(remainingRootActor)) {
								// The two nodes are part of the cycle, add them to seen nodes
								seenRoots.add(rootActor)
								seenRoots.add(remainingRootActor)
								dependentInstances.add(remainingRootInstance)
								dependentInstances.add(rootInstance)										
							}
						}
					]
					if(!dependentInstances.empty) {
						cycleRoots.add(dependentInstances.toList)
					}
				}
			]
			computeCycles = true
		}	
		return cycleRoots
	}
	
	/**
	 * Get maximum of all the instances of a given actor.
	 * 
	 * @param The actor 
	 * @return maximum level of the actor
	 */
	protected def int getMaxActorLevel(SDFAbstractVertex actor) {
		val levels = getAllLevels()
		val instances = dagGen.actor2Instances.get(actor)
		if(instances === null) {
			throw new SDF4JException("Bug! Contact Sudeep Kanur (skanur@abo.fi) with the graph that caused the exception")
		}
		val levelsOfInstances = newArrayList()
		instances.forEach[instance | 
			levelsOfInstances.add(levels.get(instance))
		]
		return levelsOfInstances.max
	}
	
	/**
	 * Overrides @{link DAGOperations#getDAGC}
	 */
	override SDFGraph getDAGC() {
		return dagC
	}
	
	/**
	 * Overrides @{link DAGOperations#getDAGT}
	 */
	override SDFGraph getDAGT() {
		return dagT
	}
}