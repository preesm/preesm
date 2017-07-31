package org.abo.preesm.plugin.dataparallel.test

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import java.util.Collection
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.DAGUtils
import org.junit.Assert
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex

@RunWith(Parameterized)
class DAGUtilsTest {
	val SDFGraph hsdf
	
	val SDFGraph dag
	
	new(SDFGraph hsdf, SDFGraph dag) {
		this.hsdf = hsdf
		this.dag = dag
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		Util.provideAllGraphs
			.forEach[ sdf |
				val dagGen = new SDF2DAG(sdf)
				val toHSDF = new ToHSDFVisitor
				sdf.accept(toHSDF)
				parameters.add(#[toHSDF.output, dagGen.outputGraph])
			]
		
		return parameters
	}
	
	/**
	 * All vertices, except implode/explode of HSDF should also 
	 * exist in DAG
	 * 
	 * Weak Test
	 */
	@org.junit.Test
	public def void hsdfAndDAGareSame() {
		
		// Check every vertex of hsdf is in dag
		hsdf.vertexSet.forEach[vertex |
			val dagVertex = DAGUtils.findVertex(vertex, hsdf, dag)
			if(! (vertex instanceof SDFForkVertex) && !(vertex instanceof SDFJoinVertex)) {
				Assert.assertFalse(dagVertex === null)
			}
		]
		
		// Check every vertex of dag is in hsdf
		dag.vertexSet.forEach[vertex | 
			val hsdfVertex = DAGUtils.findVertex(vertex, dag, hsdf)
			if(! (vertex instanceof SDFForkVertex) && !(vertex instanceof SDFJoinVertex)) {
				Assert.assertFalse(hsdfVertex === null)
			}
		]
	}
}