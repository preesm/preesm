package org.abo.preesm.plugin.dataparallel.test

import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.junit.runners.Parameterized
import org.junit.runner.RunWith
import java.util.Collection
import org.junit.Assert
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.jgrapht.alg.CycleDetector
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge

/**
 * Test construction of DAG from SDF. Both manual and automatic methods are
 * used to test the construction
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SDF2DAGTest {
	val SDF2DAG dagGen
	
	val int explodeInstanceCount
	
	val int implodeInstanceCount
	
	val int totalInstanceCount
	
	new(SDFGraph sdf, int explodeInstanceCount, int implodeInstanceCount, int totalInstanceCount){
		dagGen = new SDF2DAG(sdf)
		this.explodeInstanceCount = explodeInstanceCount
		this.implodeInstanceCount = implodeInstanceCount
		this.totalInstanceCount = totalInstanceCount	
	}
	
	/**
	 * Provide the original sdf, and the count of various instances seen and is
	 * required by the tests
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		val parameterArray = #[
//			#[sdf, explode count, implode count, total vertices
			#[ExampleGraphs.acyclicTwoActors, 1, 2, 11],
			#[ExampleGraphs.twoActorSelfLoop, 1, 2, 11],
			#[ExampleGraphs.twoActorLoop, 4, 4, 16],
			#[ExampleGraphs.semanticallyAcyclicCycle, 3, 4, 17],
			#[ExampleGraphs.strictlyCyclic, 4, 6, 20],
			#[ExampleGraphs.mixedNetwork1, 6, 7, 32],
			#[ExampleGraphs.mixedNetwork2, 7, 7, 33]
		]
		parameterArray.forEach[
			parameters.add(#[it.get(0) as SDFGraph, it.get(1), it.get(2), it.get(3)])
		]
		return parameters
	}
	
	/**
	 * Check that check input SDF is a valid input.
	 */
	@org.junit.Test
	public def void sdfIsNotHSDF() {
		Assert.assertTrue(dagGen.checkInputIsValid())
	}
	
	/**
	 * Should throw an exception if a hierarchical SDF
	 * is being passed
	 */
	@org.junit.Test(expected = SDF4JException)
	public def void exceptionHierGraph() {
		new SDF2DAG(ExampleGraphs.acyclicHierarchicalTwoActors)
	}
	
	/**
	 * Check the count of explode instances
	 */
	@org.junit.Test
	public def void checkExplodeInstanceCount() {
		val explodeInstance = dagGen.outputGraph.vertexSet.filter[instance |
			instance instanceof SDFForkVertex && instance.name.toLowerCase.contains("explode")
		].size
		Assert.assertEquals(explodeInstance, explodeInstanceCount)
	}
	
	/**
	 * Check the count of implode instances
	 */
	@org.junit.Test
	public def void checkImplodeInstanceCount() {
		val implodeInstance = dagGen.outputGraph.vertexSet.filter[instance |
			instance instanceof SDFJoinVertex && instance.name.toLowerCase.contains("implode")
		].size
		Assert.assertEquals(implodeInstance, implodeInstanceCount)
	}
	
	/**
	 * Check the count of number of instances
	 */
	@org.junit.Test
	public def void checkTotalInstanceCount() {
		Assert.assertEquals(dagGen.outputGraph.vertexSet.size, totalInstanceCount)
	}
	
	/**
	 * Make sure DAG has no delays
	 */
	@org.junit.Test
	public def void noDelays() {
		dagGen.outputGraph.edgeSet.forEach[edge |
			Assert.assertTrue(edge.delay.intValue == 0)
		]
	}
	
	/**
	 * Make sure that the instances of each actor sums up to total instances seen
	 * in the DAG
	 */
	@org.junit.Test
	public def void actor2InstancesHasAllVertices() {
		val allVerticesFromMaps = dagGen.actor2Instances.values.flatten.size
		Assert.assertEquals(dagGen.outputGraph.vertexSet.size, allVerticesFromMaps)
	}
	
	/**
	 * Make sure that DAG has no cycles. As the base graph representation is SDFGraph
	 * this is not automatically gauranteed and hence the test
	 */
	@org.junit.Test
	public def void dagHasNoCycles() {
		Assert.assertFalse(new CycleDetector<SDFAbstractVertex, SDFEdge>(dagGen.outputGraph).detectCycles)
	}
}