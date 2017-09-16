package org.abo.preesm.plugin.dataparallel.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.abo.preesm.plugin.dataparallel.iterator.SubsetTopologicalIterator
import org.abo.preesm.plugin.dataparallel.operations.visitor.RootExitOperations
import org.jgrapht.alg.CycleDetector
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import org.jgrapht.graph.DirectedSubgraph
import org.ietr.dftools.algorithm.model.sdf.SDFEdge

/**
 * Property based test for {@link DAGSubset} instance
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DAGSubsetTest {
	val SDF2DAG dagGen
	
	val SDFAbstractVertex rootNode
	
	new(SDF2DAG dagGen, SDFAbstractVertex rootNode) {
		this.dagGen = dagGen
		this.rootNode = rootNode
	}
	
	/**
	 * Provide all manually constructed SDFs and its root nodes
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		Util.provideAllGraphs.forEach[sdf |
			val dagGen = new SDF2DAG(sdf)
			
			val rootOp = new RootExitOperations
			dagGen.accept(rootOp)
			val rootInstances = rootOp.rootInstances
			rootInstances.forEach[rootNode |
				parameters.add(#[dagGen, rootNode])
			]
		]
		
		Util.provideAllGraphs.forEach[ sdf |			
			// Get strongly connected components
			val strongCompDetector = new KosarajuStrongConnectivityInspector(sdf)
					
			// Collect strongly connected component that has loops in it
			// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
			strongCompDetector.stronglyConnectedComponents.forEach[ subgraph |
				val cycleDetector = new CycleDetector(subgraph as 
					DirectedSubgraph<SDFAbstractVertex, SDFEdge>) 
				if(cycleDetector.detectCycles) {
					// ASSUMPTION: Strongly connected component of a directed graph contains atleast
					// one loop
					val dagGen = new SDF2DAG(subgraph as
						DirectedSubgraph<SDFAbstractVertex, SDFEdge>)
					
					val rootOp = new RootExitOperations
					dagGen.accept(rootOp)
					val rootInstances = rootOp.rootInstances
					rootInstances.forEach[rootNode |
						parameters.add(#[dagGen, rootNode])
					]
				}
			]
		]
		return parameters
	}
	/**
	 * Check that the actor2Instances have actors and instances
	 * from the subset of DAG only. SubsetTopologicalIterator is 
	 * used to verify this fact
	 */
	@Test
	public def void actorsHaveRightInstances() {
		val subsetActor2Instances = new DAGSubset(dagGen, rootNode).actor2Instances
		val seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).instanceSources.keySet
		dagGen.actor2Instances.forEach[actor, instances|
			// Consider only those instances that are in the subset (found by iterator)
			val filteredInstances = instances.filter[instance | seenNodes.contains(instance)].toList
			if(filteredInstances.isEmpty){
				// Clearly, this actor and its instance was filtered out
				Assert.assertTrue(!subsetActor2Instances.keySet.contains(actor))
			} else {
				// Both actor should exist and their instances should be seen
				Assert.assertTrue(subsetActor2Instances.keySet.contains(actor))
				Assert.assertEquals(filteredInstances, subsetActor2Instances.get(actor))
			}
		]	
	}
	
	/**
	 * Check that instance2Actor has instances and actors from the subset of DAG only.
	 * SubsetTopologicalIterator is used to verify 
	 */
	@Test
	public def void instancesHaveRightActors() {
		val subsetInstance2Actor = new DAGSubset(dagGen, rootNode).instance2Actor
		val seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).instanceSources.keySet
		dagGen.instance2Actor.forEach[instance, actor |
			// Consider only those instances that are in the subset (found by iterator)
			if(seenNodes.contains(instance)){
				// If instance is seen, then it should be in the lookup table as well
				Assert.assertTrue(subsetInstance2Actor.keySet.contains(instance))
				Assert.assertEquals(subsetInstance2Actor.get(instance), actor)
			} else {
				Assert.assertTrue(!subsetInstance2Actor.keySet.contains(instance))
			}
		]
	}
}