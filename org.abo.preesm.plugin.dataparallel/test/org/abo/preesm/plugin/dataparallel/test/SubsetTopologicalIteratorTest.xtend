package org.abo.preesm.plugin.dataparallel.test

import java.util.NoSuchElementException
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator
import org.junit.Assert
import org.junit.Test
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations

class SubsetTopologicalIteratorTest {
	/**
	 * Actual test is carried out by this
	 * Checks that if a node has occurs `n' times after going through
	 * all the root node based DAG subset, its targets/successors should also
	 * occur `n' times
	 */
	protected def boolean checkSuccBelongsNRootNodes(DAGConstructor dagGen) {
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
		return true
	}
	
	/**
	 * If a node belongs to `n' root nodes, then all its 
	 * successors belongs to atleast `n' root nodes
	 */
	@Test
	public def void succBelongsNRootNodes() {
		Util.provideAllGraphs
		.map[graph | new SDF2DAG(graph)]
		.forall[dag | checkSuccBelongsNRootNodes(dag) == true]
	}
	
	/** If a non-root node is passed, the iterator should
	 * create an exception
	 */ 
	@Test
	public def void nonRootInstanceRaiseException() {
		Util.provideAllGraphs
		.map[graph | new SDF2DAG(graph)]
		.forEach[dagGen |
			val rootNodes = new DAGFromSDFOperations(dagGen).rootInstances
			val nonRootNode = dagGen.outputGraph.vertexSet.filter[node | !rootNodes.contains(node)].toList.get(0)
			if(nonRootNode === null) {
				throw new NoSuchElementException("Non-Root nodes can't be null. Bug in the code!")
			}
			Assert.assertTrue(try {
				new SubsetTopologicalIterator(dagGen, nonRootNode)
				false
			} catch(NoSuchElementException e) {
				true
			} catch(Exception e) {
				false
			})
		]
	}
	
	/**
	 * Verify the instance sources calculate by hand is same as the one returning 
	 */ 
	@Test
	public def void verifyInstanceSources() {
		Util.provideAllGraphs
		.map[graph | new SDF2DAG(graph)]
		.forEach[dagGen |
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
		]
	}
}