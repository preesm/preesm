package org.abo.preesm.plugin.dataparallel.dag.operations.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGOperations
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGSubsetOperations
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs
import org.abo.preesm.plugin.dataparallel.test.Util
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Setup to test re-timing transformations SDFG
 * We need a different setup here because unlinke DAGOperationsTest
 * the expected values of some graphs vary with the graph in
 * question. i.e. This class does not perform property tests
 */
@RunWith(Parameterized)
class RearrangeTest {
	
	protected val SDF2DAG dagGen
	
	protected val DAGOperations dagOps
	
	/**
	 * Flag to distinguish SDF graphs that are to be tested with
	 * rearranging acyclic graphs
	 */
	protected val boolean isRearrangeAcyclic
	
	new(SDFGraph sdf, DAGOperations dagOps, Boolean isRearrangeAcyclic) {
		this.dagGen = new SDF2DAG(sdf)
		this.dagOps = dagOps
		this.isRearrangeAcyclic = isRearrangeAcyclic.booleanValue
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		val parameterArray = #[
			// [DAG from SDF, test acyclic rearraning?]
			#[ExampleGraphs.acyclicTwoActors, Boolean.TRUE],
			#[ExampleGraphs.semanticallyAcyclicCycle, Boolean.TRUE]
		]
		parameterArray.forEach[
			val sdf = it.get(0) as SDFGraph
			parameters.add(#[sdf, new DAGFromSDFOperations(new SDF2DAG(sdf)), it.get(1)])
		]
		
		Util.provideAllGraphs
		.forEach[sdf |
			val dagGen = new SDF2DAG(sdf)
			new DAGFromSDFOperations(dagGen).rootInstances.forEach[rootNode |
				parameters.add(#[sdf, new DAGSubsetOperations(dagGen, rootNode), Boolean.TRUE])
			]
		]
		return parameters
	}
	
	/**
	 * Test rearranging of acyclic-like graphs only
	 */
	@Test
	public def void rearrangedDAGisParallel() {
		if(isRearrangeAcyclic && dagOps.isDAGInd){
			dagOps.rearrange
			Assert.assertTrue(dagOps.isDAGParallel)
		}
	}
}