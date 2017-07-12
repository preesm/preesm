package org.abo.preesm.plugin.dataparallel.operations.visitor

import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.DAGConstructor

class RootExitOperations implements DAGCommonOperations {
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> rootInstances
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> rootActors
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> exitInstances
	
	new() {
		rootInstances = newArrayList
		rootActors = newArrayList
		exitInstances = newArrayList
	} 
	
	/**
	 * Common function to compute root instances, exit instance and root actors
	 * for DAG constructors of type PureDAGConstructor
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance 
	 */	
	protected def void compute(PureDAGConstructor dagGen) {
		// Compute root Instances
		val inputGraph = dagGen.outputGraph
		rootInstances.addAll(inputGraph.vertexSet.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0])
		
		// Compute Root Actors
		computeRootActors(dagGen)
		
		// Compute Exit instances
		exitInstances.addAll(
			inputGraph.vertexSet
				.filter[instance | inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)]
				.toList
		)
	}
	
	protected def void computeRootActors(DAGConstructor dagGen) {
		rootActors.addAll(rootInstances.map[instance |
				dagGen.instance2Actor.get(instance)
			].toSet.toList)
	}
	
	override visit(SDF2DAG dagGen) {
		compute(dagGen)
	}
	
	/**
	 * In addition to finding instances that have no roots, this
	 * function also filters the set based on the instance present in the
	 * subset
	 * 
	 * @param A {@link DAGSubset} instance
	 */
	override visit(DAGSubset dagGen) {
		val seenNodes = dagGen.seenNodes
		val inputGraph = dagGen.originalDAG.outputGraph
		
		// Compute root instances
		rootInstances.addAll(
			inputGraph.vertexSet
				.filter[instance | seenNodes.contains(instance)]
				.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0]
				.toList
		)
		
		// Compute root actors
		computeRootActors(dagGen)
		
		// Compute exit instances
		exitInstances.addAll(
			inputGraph.vertexSet
				.filter[instance | seenNodes.contains(instance)]
				.filter[instance | inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)]
				.toList
		)
	}
	
	override visit(DAG2DAG dag) {
		compute(dag)
	}
	
}