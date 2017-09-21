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
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DirectedSubgraph
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge

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
	
	protected val boolean checkCounts
	
	new(PureDAGConstructor dagGen, 
		List<String> rootNodeNames, 
		List<String> exitNodeNames, 
		List<String> actorNames, 
		boolean checkCounts
	) {
		this.dagGen = dagGen
		this.rootNodeNames = rootNodeNames
		this.exitNodeNames = exitNodeNames
		this.actorNames = actorNames
		this.checkCounts = checkCounts
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		/*
		 * Following parameters are passed
		 * 1. A PureDAGConstructor instance
		 * 2. Names of root instances
		 * 3. Names of exit instances
		 * 4. Names of root actors
		 * 5. Should count?
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
						   , true
			])
		]
		
		// Add DAG2DAG instances
		parameterArray.forEach[
			val dagGen = new SDF2DAG(it.get(0) as SDFGraph)
			parameters.add(#[new DAG2DAG(dagGen)
						   , it.get(1) as List<String>
						   , it.get(2) as List<String>
						   , it.get(3) as List<String>
						   , true
			])
		]
		
		// Test on subgraphs
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
				val subgraphDir = subgraph as DirectedSubgraph<SDFAbstractVertex, SDFEdge>
				if(cycleDetector.detectCycles) {
					// ASSUMPTION: Strongly connected component of a directed graph contains atleast
					// one loop
					if( (stronglyConnectedComponents == 1) 
						&& 
						((it.get(0) as SDFGraph).vertexSet.size 
							== 
						strongCompDetector.stronglyConnectedSets.get(0).size)) {
						
						// Add SDF2DAG instances
						val dagGen = new SDF2DAG(subgraphDir)
						parameters.add(#[dagGen
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, true
						])
						
						// Add DAG2DAG instances
						parameters.add(#[new DAG2DAG(dagGen)
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, true
						])
					} else {
						val dagGen = new SDF2DAG(subgraphDir)
						
						// Add SDF2DAG instances
						parameters.add(#[dagGen
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, false
						])	
						
						// Add DAG2DAG instances
						parameters.add(#[new DAG2DAG(dagGen)
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, false
						])
					}
				}
			]
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
		if(checkCounts) {
			Assert.assertEquals(rootNodeNames, rootOp.rootInstances.map[node | node.name])	
		}
	}
	
	/**
	 * Check that the manually determined exit instances match the computed ones
	 */
	@org.junit.Test
	public def void checkExitInstances() {
		val exitOp = new RootExitOperations
		dagGen.accept(exitOp)
		if(checkCounts) {
			Assert.assertEquals(exitNodeNames, exitOp.exitInstances.map[node | node.name])
		}
	}
	
	/**
	 * Check that the manually determined actors match the computed ones
	 */
	@org.junit.Test
	public def void checkActors() {
		val actorOp = new RootExitOperations
		dagGen.accept(actorOp)
		if(checkCounts) {
			Assert.assertEquals(actorNames, actorOp.rootActors.map[node | node.name])
		}
		
		// Make sure instances are not disturbed
		if(checkCounts) {
			Assert.assertEquals(rootNodeNames, actorOp.rootInstances.map[node | node.name])
		}
	}
}