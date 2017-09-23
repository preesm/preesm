package org.abo.preesm.plugin.dataparallel.operations.visitor.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.operations.visitor.AcyclicLikeSubgraphDetector
import org.abo.preesm.plugin.dataparallel.test.Util
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Class to test operations on SDF graphs
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SDFCommonOperationsTest {
	protected val SDFGraph sdf
	
	protected val Boolean isAcyclic
	
	new(SDFGraph sdf
	   , Boolean isAcyclic
	) {
		this.sdf = sdf
		this.isAcyclic = isAcyclic		
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		/*
		 * Contains following parameters
		 * 1. SDFGraph
		 * 2. Is SDFGraph acyclic?
		 */
		val parameters = newArrayList
		
		Util.provideAllGraphsContext.forEach[sdfContext |
			parameters.add(#[sdfContext.graph, sdfContext.isAcyclic])	
		]
		
		return parameters
	}
	
	/**
	 * Check whether Acyclic graphs are indeed detected as so by {@link AcyclicLikeSubgraphDetector}
	 * class
	 * 
	 * Warning! Not a generic test. Test depends on manually defined parameters
	 * 
	 * Strong test. Manually defined
	 */
	@Test
	public def void sdfIsAcyclic() {
		if(isAcyclic) {	
			val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector
			sdf.accept(acyclicLikeVisitor)
			
			Assert.assertTrue(acyclicLikeVisitor.isAcyclicLike)
		}
	}
}