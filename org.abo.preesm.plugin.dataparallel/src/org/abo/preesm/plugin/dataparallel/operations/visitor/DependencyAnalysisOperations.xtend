package org.abo.preesm.plugin.dataparallel.operations.visitor

import java.util.Set
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.jgrapht.traverse.BreadthFirstIterator

/**
 * Operation that performs instance dependencies in a DAG and return
 * instance dependent actors if it is not instance independent
 * 
 * @author Sudeep Kanur
 */
class DependencyAnalysisOperations implements DAGCommonOperations {
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isIndependent
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Set<SDFAbstractVertex> instanceDependentActors
	
	new() {
		isIndependent = null
		instanceDependentActors = newHashSet
	}
	
	/**
	 * Helper function to analyse instance independence on {@link PureDAGConstructor} 
	 * instance based on the instance independence of its subsets
	 * 
	 * Logic of implementation is that it checks if each of the subset DAG
	 * is instance independent and accumulate instance dependent actors on
	 * its way
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 */
	protected def void computeIndependence(PureDAGConstructor dagGen) {
		// Get root instances first
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val rootInstances = rootVisitor.rootInstances
		

		val dagIndState = newArrayList
		rootInstances.forEach[rootNode |
			val dagSub = new DAGSubset(dagGen, rootNode)
			// Check instance independence of this subset
			val subsetParallelCheck = new DependencyAnalysisOperations
			dagSub.accept(subsetParallelCheck)
			dagIndState.add(subsetParallelCheck.isIndependent)
			instanceDependentActors.addAll(subsetParallelCheck.instanceDependentActors)		
		]
		
		if(dagIndState.forall[state | state == Boolean.TRUE]) {
			isIndependent = Boolean.TRUE
		} else {
			isIndependent= Boolean.FALSE
		}
	}
	
	/**
	 * Visitor method that performs instance dependency analysis on
	 * a {@link SDF2DAG} instance. 
	 * 
	 * Use {@link DependencyAnalysisOperations#isIndependent} to check if the
	 * DAG is instance independent
	 * 
	 * Use {@link DependencyAnalysisOperations#instanceDependentActors} to give
	 * instance dependent actors
	 * 
	 * @param dagGen A {@link SDF2DAG} instance
	 */
	override visit(SDF2DAG dagGen) {
		computeIndependence(dagGen)
	}
	
	/**
	 * Visitor method that performs instance dependency analysis on
	 * a {@link DAG2DAG} instance. 
	 * 
	 * Use {@link DependencyAnalysisOperations#isIndependent} to check if the
	 * DAG is instance independent
	 * 
	 * Use {@link DependencyAnalysisOperations#instanceDependentActors} to give
	 * instance dependent actors
	 * 
	 * @param dagGen A {@link DAG2DAG} instance
	 */
	override visit(DAG2DAG dagGen) {
		computeIndependence(dagGen)
	}	
	
	/**
	 * Visitor method that performs instance dependency analysis on
	 * a {@link DAGSubset} instance. 
	 * 
	 * Use {@link DependencyAnalysisOperations#isIndependent} to check if the
	 * DAG is instance independent
	 * 
	 * Use {@link DependencyAnalysisOperations#instanceDependentActors} to give
	 * instance dependent actors
	 * 
	 * Instance independence is calculated by getting actors at each level
	 * As we are operating on a subset of a DAG, all the instances of actors
	 * of the SDF must occur at a unique level. If an actor occurs at two different
	 * level, then that actor is not instance independent. We record that actor
	 * in a special variable (nonParallelActors) and report it
	 * 
	 * @param dagGen A {@link DAGSubset} instance
	 */
	override visit(DAGSubset dagGen) {
		val actorsSeen = newHashSet
		isIndependent = Boolean.TRUE
		
		// Get level sets
		val levelOp = new LevelsOperations()
		dagGen.accept(levelOp)
		val levelSets = OperationsUtils.getLevelSets(levelOp.levels)
		
		levelSets.forEach[levelSet |
			val Set<SDFAbstractVertex> actorsSeenInLevelSet = newHashSet
			levelSet.forEach[ instance |
				actorsSeenInLevelSet.add(dagGen.instance2Actor.get(instance)) 
			]
			actorsSeenInLevelSet.forEach[actor |
				if(actorsSeen.contains(actor)) {
					isIndependent = Boolean.FALSE
					instanceDependentActors.add(actor)
				} 
			]
			actorsSeen.addAll(actorsSeenInLevelSet)
		]
		
		// Filter the instanceDependentActors according to the seen nodes
		val realNonParallelActors = newHashSet
		val forkJoinInstances = dagGen.explodeImplodeOrigInstances.keySet
		
		instanceDependentActors.forEach[actor | 
			// For all the instances of the actor, except implode/explodes
			val instances = dagGen.actor2Instances.get(actor)
								.filter[instance | !forkJoinInstances.contains(instance)]
			
			if(instances.filter[instance |
				// Any iterator can be used. Breadth first implementation is cheaper than rest
				new BreadthFirstIterator<SDFAbstractVertex, SDFEdge>(dagGen.originalDAG.outputGraph, instance)
					.filter[it != instance] // Make sure we don't see this instance again
					.filter[!forkJoinInstances.contains(it)] // No implode/explode
					.map[node | dagGen.instance2Actor.get(node)] // We get the actor
					.toSet
					.contains(actor) // If this actor has been seen before
			].size > 0) realNonParallelActors.add(actor) // Size is zero if it is a DAGind
		]
		
		instanceDependentActors.clear()
		instanceDependentActors.addAll(realNonParallelActors)
	}
}