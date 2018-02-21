/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2018),
 * IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017 - 2018)
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
package fi.abo.preesm.dataparallel.test

import java.util.Collection
import fi.abo.preesm.dataparallel.SDF2DAG
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
import fi.abo.preesm.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import fi.abo.preesm.dataparallel.test.util.ExampleGraphs

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
