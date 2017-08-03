package org.abo.preesm.plugin.dataparallel.operations.visitor.test

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import java.util.List
import java.util.Collection
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.operations.visitor.RootExitOperations
import org.junit.Assert

/**
 * Manual test for verifying root and exit instances and actors
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class RootExitVisitorManualTest {
	
	protected val PureDAGConstructor dagGen
	
	protected val List<String> rootNodeNames
	
	protected val List<String> exitNodeNames
	
	protected val List<String> actorNames
	
	new(PureDAGConstructor dagGen, List<String> rootNodeNames, List<String> exitNodeNames, List<String> actorNames) {
		this.dagGen = dagGen
		this.rootNodeNames = rootNodeNames
		this.exitNodeNames = exitNodeNames
		this.actorNames = actorNames
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		/*
		 * Following parameters are passed
		 * 1. A PureDAGConstructor instance
		 * 2. Names of root instances
		 * 3. Names of exit instances
		 * 4. Names of root actors
		 */
		val parameters = newArrayList
		
		val parameterArray = #[
			#[ExampleGraphs.acyclicTwoActors, #["a_0", "a_1", "a_2", "a_3", "a_4", "b_0"], #["b_1", "b_2"], #["a", "b"]],
			#[ExampleGraphs.twoActorSelfLoop, #["a_0", "b_0"], #["a_4", "b_1", "b_2"], #["a", "b"]],
			#[ExampleGraphs.twoActorLoop, #["a_0", "a_1" ], #["b_2"], #["a"]],
			#[ExampleGraphs.semanticallyAcyclicCycle, #["c_0", "c_1", "c_2", "d_0"], #["b_0", "b_1"], #["c", "d"]],
			#[ExampleGraphs.strictlyCyclic, #["a_0", "c_0"], #["a_2", "b_1", "d_1"], #["a", "c"]],
			#[ExampleGraphs.strictlyCyclicDual, #["b_0", "d_0"], #["a_1", "a_2", "c_1", "c_2"], #["b", "d"]],
			#[ExampleGraphs.strictlyCyclic2, #["a0_0", "c0_0", "a1_0", "c1_0"], #["a0_2", "b0_1", "d0_1", "a1_2", "b1_1", "d1_1"], #["a0", "c0", "a1", "c1"]],
			#[ExampleGraphs.mixedNetwork1, #["b_0", "c_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5"], #["a_1", "a_2", "b_1", "e_0", "e_1", "e_2"], #["b", "c", "z"]],
			#[ExampleGraphs.mixedNetwork2, #["b_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5"], #["a_1", "a_2", "e_0", "e_1", "e_2"], #["b", "z"]]
		]
		
		// Add SDF2DAG instances
		parameterArray.forEach[
			parameters.add(#[new SDF2DAG(it.get(0) as SDFGraph)
						   , it.get(1) as List<String>
						   , it.get(2) as List<String>
						   , it.get(3) as List<String>
			])
		]
		
		// Add DAG2DAG instances
		parameterArray.forEach[
			val dagGen = new SDF2DAG(it.get(0) as SDFGraph)
			parameters.add(#[new DAG2DAG(dagGen)
						   , it.get(1) as List<String>
						   , it.get(2) as List<String>
						   , it.get(3) as List<String>
			])
		]
		
		return parameters
	}
	
	/**
	 * Check that the manually determined root instances match the computed ones
	 */
	@org.junit.Test
	public def void checkRootInstances() {
		val rootOp = new RootExitOperations
		dagGen.accept(rootOp)
		Assert.assertEquals(rootNodeNames, rootOp.rootInstances.map[node | node.name])
	}
	
	/**
	 * Check that the manually determined exit instances match the computed ones
	 */
	@org.junit.Test
	public def void checkExitInstances() {
		val exitOp = new RootExitOperations
		dagGen.accept(exitOp)
		Assert.assertEquals(exitNodeNames, exitOp.exitInstances.map[node | node.name])
	}
	
	/**
	 * Check that the manually determined actors match the computed ones
	 */
	@org.junit.Test
	public def void checkActors() {
		val actorOp = new RootExitOperations
		dagGen.accept(actorOp)
		Assert.assertEquals(actorNames, actorOp.rootActors.map[node | node.name])
		
		// Make sure instances are not disturbed
		Assert.assertEquals(rootNodeNames, actorOp.rootInstances.map[node | node.name])
	}
}