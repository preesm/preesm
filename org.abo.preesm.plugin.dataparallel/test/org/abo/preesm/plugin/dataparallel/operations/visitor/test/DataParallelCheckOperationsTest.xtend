package org.abo.preesm.plugin.dataparallel.operations.visitor.test

import org.junit.runners.Parameterized
import org.junit.runner.RunWith
import java.util.Collection
import org.abo.preesm.plugin.dataparallel.test.Util
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.junit.Assert
import org.abo.preesm.plugin.dataparallel.operations.visitor.DataParallelCheckOperations
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector

/**
 * Check {@link DataParallelCheckOperations} works as expected
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DataParallelCheckOperationsTest {
	
	protected val SDFGraph sdf
	
	new(SDFGraph sdf) {
		this.sdf = sdf
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		Util.provideAllGraphs.forEach[sdf |
			parameters.add(#[sdf])
		]		
		
		return parameters
	}
	
	/**
	 * Should throw an exception if a hierarchical SDF
	 * is being passed
	 */
	@org.junit.Test(expected = SDF4JException)
	public def void exceptionHierGraph() {
		val isolatedSubgraphsVisitor = new DataParallelCheckOperations
		ExampleGraphs.acyclicHierarchicalTwoActors.accept(isolatedSubgraphsVisitor)
	}
	
	/**
	 * Assert that each isolated subgraph is indeed a
	 * strongly connected component. The method isStronglyConnected is never
	 * used to isolate them, so it is a good candidate to test.
	 */
	@org.junit.Test
	public def void isolatedSubgraphsAreStronglyConnected() {
		val isolatedSubgraphsVisitor = new DataParallelCheckOperations
		sdf.accept(isolatedSubgraphsVisitor)
		
		isolatedSubgraphsVisitor.isolatedStronglyConnectedComponents.forEach[ subgraph |
			val strongCompDetector = new KosarajuStrongConnectivityInspector(subgraph)
			Assert.assertTrue(strongCompDetector.isStronglyConnected)
		]
	}
	
	/**
	 * Assert that there are no dangling port interfaces for any vertex
	 * of isolated subgraph
	 */
	@org.junit.Test
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