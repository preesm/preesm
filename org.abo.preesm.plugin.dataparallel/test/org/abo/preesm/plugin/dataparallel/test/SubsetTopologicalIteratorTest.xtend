package org.abo.preesm.plugin.dataparallel.test

import java.util.Collection
import java.util.NoSuchElementException
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Test setup for {@link SubsetTopologicalIterator} class
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SubsetTopologicalIteratorTest {
	
	val SDF2DAG dagGen
	
	new(SDF2DAG dagGen) {
		this.dagGen = dagGen
	}
	
	/**
	 * Provide all the DAGs for manually constructed SDFs
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		Util.provideAllGraphs.forEach[sdf | 
			parameters.add(#[new SDF2DAG(sdf)])
		]	
		return parameters
	}
	
	/**
	 * Actual test is carried out by this
	 * Checks that if a node has occurs `n' times after going through
	 * all the root node based DAG subset, its targets/successors should also
	 * occur `n' times
	 */
	@Test
	public def void checkSuccBelongsNRootNodes() {
		val occurence = newHashMap() // The lookup table
		val instanceTargets = newHashMap()
		// Get the targets of each node
		dagGen.outputGraph.vertexSet.forEach[node | 
			instanceTargets.put(node, dagGen.outputGraph.outgoingEdgesOf(node).map[edge | edge.target].toList)
		]
		val dagOps = new DAGFromSDFOperations(dagGen)
		// Initialize the lookup table
		dagGen.outputGraph.vertexSet.forEach[node | occurence.put(node, 0)]
		// Mark the occurence for each root node/subset of DAG
		dagOps.rootInstances.forEach[rootNode |
			new SubsetTopologicalIterator(dagGen, rootNode).toList.forEach[node |
				occurence.put(node, occurence.get(node)+1)
			]
		]
		Assert.assertTrue(occurence.entrySet.reject[entry |
			val node = entry.key
			val occurs = entry.value
			instanceTargets.get(node).reject[succ | occurence.get(succ) >= occurs].size == 0
		].size == 0)
	}
	
	/** If a non-root node is passed, the iterator should
	 * create an exception
	 */ 
	@Test(expected = NoSuchElementException)
	public def void nonRootInstanceRaiseException() {
		val rootNodes = new DAGFromSDFOperations(dagGen).rootInstances
		val nonRootNode = dagGen.outputGraph.vertexSet.filter[node | !rootNodes.contains(node)].toList.get(0)
		if(nonRootNode === null) {
			throw new RuntimeException("Non-Root nodes can't be null. Bug in the code!")
		}
		new SubsetTopologicalIterator(dagGen, nonRootNode)
	}
	
	/**
	 * Verify the instance sources calculate by hand is same as the one returning 
	 */ 
	@Test
	public def void verifyInstanceSources() {
		new DAGFromSDFOperations(dagGen).rootInstances.forEach[ rootNode |
			val instanceSources = newHashMap()
			val sit = new SubsetTopologicalIterator(dagGen, rootNode)
			sit.forEach[seenNode | instanceSources.put(seenNode, newArrayList())]
			instanceSources.forEach[seenNode, sources |
			instanceSources.put(seenNode, dagGen.outputGraph.incomingEdgesOf(seenNode).map[edge | edge.source].filter[node | instanceSources.keySet.contains(node)])
			]
			Assert.assertEquals(instanceSources.keySet, sit.instanceSources.keySet)
			instanceSources.forEach[node, sources |
				Assert.assertEquals(sources.toList, sit.instanceSources.get(node))
			]
		]
	}
}