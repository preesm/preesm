package org.abo.preesm.plugin.dataparallel.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.AbstractGraph
import org.jgrapht.graph.DirectedSubgraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector

/**
 * Test construction of DAG from SDF. Both manual and automatic methods are
 * used to test the construction
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SDF2DAGTest {
	val SDF2DAG dagGen
	
	val AbstractGraph<SDFAbstractVertex, SDFEdge> sdf
	
	val int explodeInstanceCount
	
	val int implodeInstanceCount
	
	val int totalInstanceCount
	
	val boolean checkCounts
	
	val boolean isSDF
	
	new(AbstractGraph<SDFAbstractVertex, SDFEdge> sdf, int explodeInstanceCount, 
		int implodeInstanceCount, int totalInstanceCount, boolean isSDF, boolean checkCounts
	){
		this.sdf = sdf
		if(isSDF) {
			dagGen = new SDF2DAG(sdf as SDFGraph)
		} else {
			dagGen = new SDF2DAG(sdf as DirectedSubgraph<SDFAbstractVertex, SDFEdge>)
		}
		this.explodeInstanceCount = explodeInstanceCount
		this.implodeInstanceCount = implodeInstanceCount
		this.totalInstanceCount = totalInstanceCount
		this.isSDF = isSDF	
		this.checkCounts = checkCounts
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
			#[ExampleGraphs.mixedNetwork2, 7, 7, 33],
			#[ExampleGraphs.nestedStrongGraph, 3, 3, 20]
		]
		parameterArray.forEach[
			// #[sdf, explode count, implode count, total vertices, isSDF, shouldCount?]
			parameters.add(#[it.get(0) as SDFGraph, it.get(1), it.get(2), it.get(3), true, true])
		]
		
		parameterArray.forEach[
			// Get strongly connected components
			val strongCompDetector = new KosarajuStrongConnectivityInspector(it.get(0) as SDFGraph)
		
			val stronglyConnectedComponents = strongCompDetector.stronglyConnectedSets.size
			
			// Collect strongly connected component that has loops in it
			// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
			strongCompDetector.stronglyConnectedComponents.forEach[ subgraph |
				val cycleDetector = new CycleDetector(subgraph as
					DirectedSubgraph<SDFAbstractVertex, SDFEdge>) 
				if(cycleDetector.detectCycles) {
					// ASSUMPTION: Strongly connected component of a directed graph contains atleast
					// one loop
					if( (stronglyConnectedComponents == 1) 
						&& 
						((it.get(0) as SDFGraph).vertexSet.size 
							== 
						strongCompDetector.stronglyConnectedSets.get(0).size)) {
						println(strongCompDetector.stronglyConnectedSets)
						// #[sdf, explode count, implode count, total vertices, isSDF, shouldCount?]
						parameters.add(#[subgraph, it.get(1), it.get(2), it.get(3), false, true])
					} else {
						// #[sdf, explode count, implode count, total vertices, isSDF, shouldCount?]
						parameters.add(#[subgraph, 0, 0, 0, false, false])	
					}
				}
			]
		]
		return parameters
	}
	
	/**
	 * Check the count of explode instances
	 */
	@Test
	public def void checkExplodeInstanceCount() {
		if(checkCounts) {
			val explodeInstance = dagGen.outputGraph.vertexSet.filter[instance |
				instance instanceof SDFForkVertex && instance.name.toLowerCase.contains("explode")
			].size
			Assert.assertEquals(explodeInstance, explodeInstanceCount)	
		}
	}
	
	/**
	 * Check the count of implode instances
	 */
	@Test
	public def void checkImplodeInstanceCount() {
		if(checkCounts) {
			val implodeInstance = dagGen.outputGraph.vertexSet.filter[instance |
				instance instanceof SDFJoinVertex && instance.name.toLowerCase.contains("implode")
			].size
			Assert.assertEquals(implodeInstance, implodeInstanceCount)
		}
	}
	
	/**
	 * Check the count of number of instances
	 */
	@Test
	public def void checkTotalInstanceCount() {
		if(checkCounts) {
			Assert.assertEquals(dagGen.outputGraph.vertexSet.size, totalInstanceCount)
		}
	}
	
	/**
	 * Make sure that the instances of each actor sums up to total instances seen
	 * in the DAG
	 */
	@Test
	public def void actor2InstancesHasAllVertices() {
		if(checkCounts) {
			val allVerticesFromMaps = dagGen.actor2Instances.values.flatten.size
			Assert.assertEquals(dagGen.outputGraph.vertexSet.size, allVerticesFromMaps)
		}
	}
	
	/**
	 * Make sure that DAG has no cycles. As the base graph representation is SDFGraph
	 * this is not automatically gauranteed and hence the test
	 */
	@Test
	public def void dagHasNoCycles() {
		if(isSDF) {
			Assert.assertTrue((sdf as SDFGraph).isSchedulable)
		}
		if(checkCounts) {
			Assert.assertFalse(new CycleDetector<SDFAbstractVertex, 
				SDFEdge>(dagGen.outputGraph).detectCycles)
		}
	}
}