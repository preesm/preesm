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
package fi.abo.preesm.dataparallel.operations

import java.util.Collection
import fi.abo.preesm.dataparallel.test.util.ExampleGraphs
import fi.abo.preesm.dataparallel.test.util.Util
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Property based test to verify {@link DataParallelCheckOperations} works as expected
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DataParallelCheckOperationsTest {
	
	protected val SDFGraph sdf
	
	/**
	 * Has the following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDFGraph} instance
	 * </ol>
	 */
	new(SDFGraph sdf) {
		this.sdf = sdf
	}
	
	/**
	 * Generates following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDFGraph} instance
	 * </ol>
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		Util.provideAllGraphs.forEach[sdf |
			parameters.add(#[sdf])
		]		
		
		return parameters
	}
	
	/**
	 * Should throw an exception if a hierarchical SDF is being passed
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test(expected = SDF4JException)
	public def void exceptionHierGraph() {
		val isolatedSubgraphsVisitor = new DataParallelCheckOperations
		ExampleGraphs.acyclicHierarchicalTwoActors.accept(isolatedSubgraphsVisitor)
	}
	
	/**
	 * Assert that there are no dangling port interfaces for any vertex
	 * of isolated subgraph
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test
	public def void isolatedSubgraphsAreComplete() {
		val isolatedSubgraphsVisitor = new DataParallelCheckOperations
		sdf.accept(isolatedSubgraphsVisitor)
		
		isolatedSubgraphsVisitor.isolatedStronglyConnectedComponents.forEach[ subgraph |
			// Check that there are no unconnected interface
			subgraph.vertexSet.forEach[vertex |
				subgraph.incomingEdgesOf(vertex).forEach[edge |
					Assert.assertTrue(subgraph.vertexSet.contains(edge.source))
					Assert.assertTrue(subgraph.vertexSet.contains(edge.target))
				]
				
				subgraph.outgoingEdgesOf(vertex).forEach[edge |
					Assert.assertTrue(subgraph.vertexSet.contains(edge.source))
					Assert.assertTrue(subgraph.vertexSet.contains(edge.target))
				]
			]
		]
	}
}
