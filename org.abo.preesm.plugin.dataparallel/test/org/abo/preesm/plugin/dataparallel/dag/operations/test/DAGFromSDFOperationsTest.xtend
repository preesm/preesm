package org.abo.preesm.plugin.dataparallel.dag.operations.test

import java.util.Collection
import java.util.List
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Test setup for {@link DAGFromSDFOperations}
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DAGFromSDFOperationsTest {
	
	protected val SDF2DAG dagGen
	
	protected val List<String> rootNodeNames
	
	protected val List<String> exitNodeNames
	
	protected val List<String> actorNames
	
	new(SDF2DAG dagGen, List<String> rootNodeNames, List<String> exitNodeNames, List<String> actorNames) {
		this.dagGen = dagGen
		this.rootNodeNames = rootNodeNames
		this.exitNodeNames = exitNodeNames
		this.actorNames = actorNames
	}
	
	/**
	 * Manually specify the names of root nodes, exit nodes and actor names for every
	 * SDF graph in question
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		val parameterArray = #[
			#[ExampleGraphs.acyclicTwoActors, #["a_0", "a_1", "a_2", "a_3", "a_4", "b_0"], #["b_1", "b_2"], #["a", "b"]],
			#[ExampleGraphs.twoActorSelfLoop, #["a_0", "b_0"], #["a_4", "b_1", "b_2"], #["a", "b"]],
			#[ExampleGraphs.twoActorLoop, #["a_0", "a_1" ], #["b_2"], #["a"]],
			#[ExampleGraphs.semanticallyAcyclicCycle, #["c_0", "c_1", "c_2", "d_0"], #["b_0", "b_1"], #["c", "d"]],
			#[ExampleGraphs.strictlyCyclic, #["a_0", "c_0"], #["a_2", "b_1", "d_1"], #["a", "c"]],
			#[ExampleGraphs.mixedNetwork1, #["b_0", "c_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5"], #["a_1", "a_2", "b_1", "e_0", "e_1", "e_2"], #["b", "c", "z"]],
			#[ExampleGraphs.mixedNetwork2, #["b_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5"], #["a_1", "a_2", "e_0", "e_1", "e_2"], #["b", "z"]]
		]
		parameterArray.forEach[
			parameters.add(#[new SDF2DAG(it.get(0) as SDFGraph), it.get(1) as List<String>, it.get(2) as List<String>, it.get(3) as List<String>])
		]
		return parameters
	}
	
	/**
	 * Check that the manually determined root instances match the computed ones
	 */
	@Test
	public def void checkRootInstances() {
		Assert.assertEquals(rootNodeNames, 
			new DAGFromSDFOperations(dagGen).rootInstances.map[node | node.name])
	}
	
	/**
	 * Check that the manually determined exit instances match the computed ones
	 */
	@Test
	public def void checkExitInstances() {
		Assert.assertEquals(exitNodeNames, 
			new DAGFromSDFOperations(dagGen).exitInstances.map[node | node.name])
	}
	
	/**
	 * Check that the manually determined actors match the computed ones
	 */
	@Test
	public def void checkActors() {
		Assert.assertEquals(actorNames,
			new DAGFromSDFOperations(dagGen).rootActors.map[node | node.name])
	}
}