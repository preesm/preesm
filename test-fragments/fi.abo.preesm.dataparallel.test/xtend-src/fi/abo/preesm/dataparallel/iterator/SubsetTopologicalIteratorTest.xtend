/**
 * Copyright or © or Copr. Åbo Akademi University (2017), IETR/INSA - Rennes (2017):
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017)
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
package fi.abo.preesm.dataparallel.iterator

import java.util.Collection
import java.util.NoSuchElementException
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.operations.RootExitOperations
import fi.abo.preesm.dataparallel.test.util.Util
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Parameteric test for {@link SubsetTopologicalIterator}
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SubsetTopologicalIteratorTest {
	
	val SDF2DAG dagGen
	
	/**
	 * Has the following parameters using {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDF2DAG} of example graphs
	 * </ol>
	 */
	new(SDF2DAG dagGen) {
		this.dagGen = dagGen
	}
	
	/**
	 * Generate following parameters using {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDF2DAG} of example SDF graphs
	 * </ol>
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		Util.provideAllGraphs.forEach[sdf | 
			parameters.add(#[new SDF2DAG(sdf)])
		]	
		return parameters
	}
	
	/**
	 * Verify if a node has occurs `n' times after going through
	 * all the root node based DAG subset, its targets/successors should also
	 * occur `n' times
	 */
	@Test
	public def void checkSuccBelongsNRootNodes() {
		val occurence = newHashMap // The lookup table
		val instanceTargets = newHashMap
		// Get the targets of each node
		dagGen.outputGraph.vertexSet.forEach[node | 
			instanceTargets.put(node, dagGen.outputGraph.outgoingEdgesOf(node).map[edge | edge.target].toList)
		]
		val rootOp = new RootExitOperations
		dagGen.accept(rootOp)
		val rootInstances = rootOp.rootInstances

		// Initialize the lookup table
		dagGen.outputGraph.vertexSet.forEach[node | occurence.put(node, 0)]
		// Mark the occurence for each root node/subset of DAG
		rootInstances.forEach[rootNode |
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
	
	/** If a non-root node is passed, the iterator should create an exception
	 */ 
	@Test(expected = NoSuchElementException)
	public def void nonRootInstanceRaiseException() {
		val rootOp = new RootExitOperations
		dagGen.accept(rootOp)
		val rootNodes = rootOp.rootInstances
		
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
		val rootOp = new RootExitOperations
		dagGen.accept(rootOp)
		val rootInstances = rootOp.rootInstances
		
		rootInstances.forEach[ rootNode |
			val instanceSources = newHashMap
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
