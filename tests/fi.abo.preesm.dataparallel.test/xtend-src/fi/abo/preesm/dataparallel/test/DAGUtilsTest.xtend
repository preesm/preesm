/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2018),
 * IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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

import fi.abo.preesm.dataparallel.DAGUtils
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.test.util.Util
import java.util.Collection
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.preesm.algorithm.model.sdf.SDFGraph
import org.preesm.algorithm.model.sdf.esdf.SDFForkVertex
import org.preesm.algorithm.model.sdf.esdf.SDFJoinVertex
import org.preesm.algorithm.model.sdf.visitors.ToHSDFVisitor

/**
 * Property based test for {@link DAGUtils#findVertex}
 *
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DAGUtilsTest {
	val SDFGraph hsdf

	val SDFGraph dag

	/**
	 * Has the following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> SrSDF generated from {@link ToHSDFVisitor}
	 * 	<li> DAG generated from {@link SDF2DAG}
	 * </ol>
	 */
	new(SDFGraph hsdf, SDFGraph dag) {
		this.hsdf = hsdf
		this.dag = dag
	}

	/**
	 * Generates the following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> SrSDF generated from {@link ToHSDFVisitor}
	 * 	<li> DAG generated from {@link SDF2DAG}
	 * </ol>
	 */
	@Parameterized.Parameters
	static def Collection<Object[]> instancesToTest() {
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
	 * Vertices in each graphs have unique names
	 * <p>
	 * This forms the basis for coiteration. The test ensures that is the case
	 * <i>Strong Test</i>
	 */
	@Test
	def void nameAreUnique() {
		val hsdfNameList = newArrayList
		hsdf.vertexSet.forEach[vertex |
			Assert.assertTrue(!hsdfNameList.contains(vertex.name))
			hsdfNameList.add(vertex.name)
		]

		val dagNameList = newArrayList
		dag.vertexSet.forEach[vertex |
			Assert.assertTrue(!dagNameList.contains(vertex.name))
			dagNameList.add(vertex.name)
		]
	}

	/**
	 * All vertices, except implode/explode of SrSDF should also exist in DAG
	 * <p>
	 * <i>Weak Test</i>
	 */
	@Test
	def void hsdfAndDAGareSame() {

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
