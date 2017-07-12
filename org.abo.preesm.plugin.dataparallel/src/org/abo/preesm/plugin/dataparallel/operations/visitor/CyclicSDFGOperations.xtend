package org.abo.preesm.plugin.dataparallel.operations.visitor

import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.DAGSubset

/**
 * DAG Operation that finds cycles in SDFG that have some
 * instances in the root instance set. The operation is defined
 * on pure DAG. In addition, the DAG needs to be instance independent
 * 
 * @author Sudeep Kanur
 */
class CyclicSDFGOperations implements DAGOperations {
	
	private var Boolean isDAGInd
	
	protected val List<List<SDFAbstractVertex>> cycleRoots
	
	/**
	 * A {@link Boolean} variable indicates if the SDFG has cycles that have some of its
	 * instances in root. When it is True, the SDFG and corresponding
	 * DAG is not acyclic-like
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean containsCycles
	
	new() {
		isDAGInd = null
		cycleRoots = newArrayList
		containsCycles = null
	}
	
	/**
	 * Get the instances in the root instance set, that belongs to a cycle
	 * 
	 * @return List of Lists. Each mutually exclusive list contains instances of cycle in the root set
	 * @throws SDF4JException If the passed DAG is not instance independent or if visitor method is not called
	 */
	public def List<List<SDFAbstractVertex>> getCycleRoots() throws SDF4JException {
		if(isDAGInd === null) {
			throw new SDF4JException("Make sure a visitor has been passed")
		}
		
		if(!isDAGInd) {
			throw new SDF4JException("Invalid DAG! The DAG is not instance independent")
		}
		
		return cycleRoots
	}
	
	/**
	 * Finds cycles in SDFG that have some instances in the root instance set.
	 * 
	 * Computation logic is based on Lemma 2 (Shared instances in predecessor branch)
	 * In a non-acyclic-like SDFG, if an instance of actor A belongs to the predecessor 
	 * path of an instance of an actor B and further, if reverse happens, then it is 
	 * a cycle and both of the instances are in the part of cycle as well as root instance set.
	 * We perform this check for all the instances in the root instance set.
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @throws {@link SDF4JException} If the DAG is not instance independent
	 */
	protected def void computeCycleRoots(PureDAGConstructor dagGen) {
		// Root cycles can only be found on instance independent DAG
		val parallelVisitor = new DependencyAnalysisOperations
		dagGen.accept(parallelVisitor)
		isDAGInd = parallelVisitor.isIndependent
		if(!isDAGInd) {
			throw new SDF4JException("Invalid DAG! The DAG is not instance independent")
		}
		
		// Set of actors that have already been grouped. This is non-destructive way of computing
		val seenRoots = newHashSet
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val rootInstances = rootVisitor.rootInstances
		
		rootInstances.forEach[rootInstance |
			val rootActor = dagGen.instance2Actor.get(rootInstance)
			if(!seenRoots.contains(rootActor)) {
				val dependentInstances = newHashSet
				
				val restInstances = rootInstances.filter[node | dagGen.instance2Actor.get(node) != rootActor]
				restInstances.forEach[remainingRootInstance |
					//Check if this subset DAG contains rootInstance
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
		
		if(!cycleRoots.empty) {
			containsCycles = Boolean.TRUE
		} else {
			containsCycles = Boolean.FALSE
		}
	}
	
	/**
	 * Visitor method. Finds instances of cycles in SDFG that
	 * have some instances in the root node. 
	 * 
	 * Use {@link CyclicSDFGOperations#cycleRoots} to get the instances
	 * in the root instance set, that belongs to a cycle
	 * 
	 * Use {@link CyclicSDFGOperations#containsCycles} to perform a check
	 * if there are any instances from a cycle in the root instance set
	 * 
	 * @param A {@link SDF2DAG} instance
	 */
	override visit(SDF2DAG dagGen) {
		computeCycleRoots(dagGen)
	}
	
	/**
	 * Visitor method. Finds instances of cycles in SDFG that
	 * have some instances in the root node. 
	 * 
	 * Use {@link CyclicSDFGOperations#cycleRoots} to get the instances
	 * in the root instance set, that belongs to a cycle
	 * 
	 * Use {@link CyclicSDFGOperations#containsCycles} to perform a check
	 * if there are any instances from a cycle in the root instance set
	 * 
	 * @param A {@link DAG2DAG} instance 
	 */
	override visit(DAG2DAG dagGen) {
		computeCycleRoots(dagGen)
	}
	
}