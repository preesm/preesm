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
import org.abo.preesm.plugin.dataparallel.test.util.ExampleGraphs

/**
 * Manual test for {@link SDF2DAG}
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
	
	/**
	 * Generate following manually defined parameters:
	 * <ol>
	 * 	<li> A {@link SDFGraph} instance
	 * 	<li> Count of explode vertices in the graph
	 * 	<li> Count of implode vertices in the graph
	 * 	<li> Total count of vertices in the graph
	 * 	<li> <code>true</code> if graph is instance independent, <code>false</code> otherwise
	 * </ol>
	 */
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
	 * Generate following manually defined parameters:
	 * <ol>
	 * 	<li> A {@link SDFGraph} instance
	 * 	<li> Count of explode vertices in the graph
	 * 	<li> Count of implode vertices in the graph
	 * 	<li> Total count of vertices in the graph
	 * 	<li> <code>true</code> if graph is instance independent, <code>false</code> otherwise
	 * </ol>
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		val parameterArray = #[
//			#[sdf, explode count, implode count, total vertices, isInstanceIndependent?
			#[ExampleGraphs.acyclicTwoActors, 1, 2, 11],
			#[ExampleGraphs.twoActorSelfLoop, 1, 2, 11],
			#[ExampleGraphs.twoActorLoop, 4, 4, 16],
			#[ExampleGraphs.semanticallyAcyclicCycle, 3, 4, 17],
			#[ExampleGraphs.strictlyCyclic, 4, 6, 20],
			#[ExampleGraphs.mixedNetwork1, 6, 7, 32],
			#[ExampleGraphs.mixedNetwork2, 7, 7, 33],
			#[ExampleGraphs.nestedStrongGraph, 3, 2, 19],
			#[ExampleGraphs.costStrongComponent, 1, 1, 80]
		]
		parameterArray.forEach[
			// #[sdf, explode count, implode count, total vertices, isSDF, shouldCount?]
			parameters.add(#[it.get(0) as SDFGraph, it.get(1), it.get(2), it.get(3), true, true])
		]
		
		parameterArray.forEach[ 
			val sdf = it.get(0) as SDFGraph
			// Get strongly connected components
			val strongCompDetector = new KosarajuStrongConnectivityInspector(sdf)
		
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
	 * Verify count of explode instances
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
	 * Verify count of implode instances
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
	 * Verify count of number of instances
	 */
	@Test
	public def void checkTotalInstanceCount() {
		if(checkCounts) {
			Assert.assertEquals(dagGen.outputGraph.vertexSet.size, totalInstanceCount)
		}
	}
	
	/**
	 * Verify instances of each actor sums up to total instances seen in the DAG
	 */
	@Test
	public def void actor2InstancesHasAllVertices() {
		if(checkCounts) {
			val allVerticesFromMaps = dagGen.actor2Instances.values.flatten.size
			Assert.assertEquals(dagGen.outputGraph.vertexSet.size, allVerticesFromMaps)
		}
	}
	
	/**
	 * Verify DAG has no cycles. As the base graph representation is SDFGraph
	 * this is not automatically guaranteed and hence the test
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