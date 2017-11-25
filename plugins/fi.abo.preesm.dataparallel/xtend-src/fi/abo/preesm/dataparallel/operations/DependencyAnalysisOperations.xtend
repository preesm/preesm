/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fi.abo.preesm.dataparallel.operations

import java.util.Set
import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.DAGSubset
import fi.abo.preesm.dataparallel.PureDAGConstructor
import fi.abo.preesm.dataparallel.SDF2DAG
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
	 * instance based on the instance independence of its subsets.
	 * <p>
	 * Logic of implementation is that it checks if each of the subset DAG
	 * is instance independent and accumulate instance dependent actors on
	 * its way.
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
	 * <p>
	 * Use {@link DependencyAnalysisOperations#isIndependent} to check if the
	 * DAG is instance independent.
	 * <p>
	 * Use {@link DependencyAnalysisOperations#instanceDependentActors} to give
	 * instance dependent actors.
	 * 
	 * @param dagGen A {@link SDF2DAG} instance
	 */
	override visit(SDF2DAG dagGen) {
		computeIndependence(dagGen)
	}
	
	/**
	 * Visitor method that performs instance dependency analysis on
	 * a {@link DAG2DAG} instance. 
	 * <p>
	 * Use {@link DependencyAnalysisOperations#isIndependent} to check if the
	 * DAG is instance independent.
	 * <p>
	 * Use {@link DependencyAnalysisOperations#instanceDependentActors} to give
	 * instance dependent actors.
	 * 
	 * @param dagGen A {@link DAG2DAG} instance
	 */
	override visit(DAG2DAG dagGen) {
		computeIndependence(dagGen)
	}	
	
	/**
	 * Visitor method that performs instance dependency analysis on
	 * a {@link DAGSubset} instance. 
	 * <p>
	 * Use {@link DependencyAnalysisOperations#isIndependent} to check if the
	 * DAG is instance independent.
	 * <p>
	 * Use {@link DependencyAnalysisOperations#instanceDependentActors} to give
	 * instance dependent actors.
	 * <p>
	 * Instance independence is calculated by getting actors at each level
	 * As we are operating on a subset of a DAG, all the instances of actors
	 * of the SDF must occur at a unique level. If an actor occurs at two different
	 * level, then that actor is not instance independent. We record that actor
	 * in a special variable (nonParallelActors) and report it.
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
		
		if(realNonParallelActors.empty) {
			isIndependent = Boolean.TRUE
		}
		
		instanceDependentActors.clear()
		instanceDependentActors.addAll(realNonParallelActors)
	}
}
