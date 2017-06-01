package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.ArrayList
import java.util.Set
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.workflow.tools.WorkflowLogger
import org.jgrapht.traverse.BreadthFirstIterator
import org.abo.preesm.plugin.dataparallel.SDF2DAG

/**
 * Implement DAGOperations for subsets of DAG. The class does not modify
 * the original DAG, but only uses/modifies the associated data-structures
 * 
 * @author Sudeep Kanur
 */
class DAGSubsetOperations extends DAGFromSDFOperations {
	
	/**
	 * Holds the root node
	 */
	private val SDFAbstractVertex rootNode
	
	/**
	 * Getting real non-parallel actors in a DAG is compute intensive
	 * This flag is used to avoid repeated computation of non-parallel actors
	 */
	private var boolean computeNonParallelActors 
	
	/**
	 * Constructor used in plugins
	 * 
	 * @param dagGen {@link SDF2DAG} instance
	 * @param rootNode The root node with which subset of DAG is constructed
	 * @param logger {@link WorkflowLogger} instance
	 */
	new(SDF2DAG dagGen, SDFAbstractVertex rootNode, Logger logger) {
		super(dagGen, logger)
		this.rootNode = rootNode
		val sit = new SubsetTopologicalIterator(dagGen, rootNode)
		iterator = sit
		instanceSources = sit.instanceSources
		seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).toList
		forkJoinOrigInstance = new DAGSubset(dagGen, rootNode).explodeImplodeOrigInstances
		computeNonParallelActors = false
	}
	
	/**
	 * Constructor used for testing purposes
	 * 
	 * @param dagGen {@link SDF2DAG} instance
	 * @param rootNode The root node with which subset of DAG is constructed
	 */
	new(SDF2DAG dagGen, SDFAbstractVertex rootNode) {
		this(dagGen, rootNode, null)
	}
	
	/**
	 * Compute if DAG is instance independent. Overrides {@link DAGOperations#isDAGInd}
	 * 
	 * @returns True if DAG is instance independent
	 */
	override boolean isDAGInd() {
		/*
		 * Instance independence is calculated by getting actors at each level
		 * As we are operating on a subset of a DAG, all the instances of actors
		 * of the SDF must occur at a unique level. If an actor occurs at two different
		 * level, then that actor is not instance independent. We record that actor
		 * in a special variable (nonParallelActors) and report it
		 */
		if(!computeDAGInd) {
			val actorsSeen = newHashSet
			val isDAGInd = new ArrayList(#[true])
			getLevelSets().forEach[levelSet |
				val Set<SDFAbstractVertex> actorsSeenInLevelSet = newHashSet
				levelSet.forEach[ instance |
					actorsSeenInLevelSet.add(dagGen.instance2Actor.get(instance)) 
				]
				actorsSeenInLevelSet.forEach[actor |
					if(actorsSeen.contains(actor)) {
						isDAGInd.set(0, false)
						nonParallelActors.add(actor)
					} 
				]
				actorsSeen.addAll(actorsSeenInLevelSet)
			]
			computeDAGInd = true
			dagInd = isDAGInd.get(0)
		}
		return dagInd
	}
	
	/**
	 * Compute the non-parallel actors in the DAG subset. isDAGInd gives all
	 * the actors that are at different levels, but that does not mean they are
	 * non-data-parallel. 
	 * 
	 * @return set of non-parallel actors in a DAG subset
	 */
	override getNonParallelActors() {
		/*
		 * For each actor found in different levels, this function
	 	 * performs a reachablility test for each of its instances. If an instance cannot
		 * reach another instance of its own actor, then we remove that actor from the
		 * list
		 */
		if(!computeNonParallelActors) {
			val realNonParallelActors = newHashSet()
			val forkJoinInstances = forkJoinOrigInstance.keySet
			
			nonParallelActors.forEach[actor |
				// For all instances of the actor, except implode/explodes
				val instances = new DAGSubset(dagGen, rootNode)
										.actor2Instances.get(actor)
										.filter[instance | !forkJoinInstances.contains(instance)]
				if(instances.filter[instance |
					// Any iterator can be used. Breadth first implementation is cheaper than others
					new BreadthFirstIterator<SDFAbstractVertex, SDFEdge>(dagGen.outputGraph, instance)
										.filter[it != instance] // Make sure we don't see this instance again
										.filter[!forkJoinInstances.contains(it)] // No implode/explodes
										.map[node | dagGen.instance2Actor.get(node)] // We get the actor
										.toSet
										.contains(actor) // If the actor has been seen before
				].size > 0) realNonParallelActors.add(actor) // Size if zero if it is a DAG Ind
			]
			nonParallelActors.clear()
			nonParallelActors.addAll(realNonParallelActors)
			computeNonParallelActors = true
		}
		
		return nonParallelActors
	}
}