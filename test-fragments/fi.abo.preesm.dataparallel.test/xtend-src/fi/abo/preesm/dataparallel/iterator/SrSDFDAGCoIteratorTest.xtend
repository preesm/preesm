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
package fi.abo.preesm.dataparallel.iterator

import java.util.Collection
import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.DAGUtils
import fi.abo.preesm.dataparallel.PureDAGConstructor
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.operations.AcyclicLikeSubgraphDetector
import fi.abo.preesm.dataparallel.operations.DependencyAnalysisOperations
import fi.abo.preesm.dataparallel.operations.MovableInstances
import fi.abo.preesm.dataparallel.test.util.ExampleGraphs
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.jgrapht.alg.cycle.CycleDetector
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import fi.abo.preesm.dataparallel.NodeChainGraph
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector
import org.jgrapht.graph.AsSubgraph

/**
 * Parameteric test for {@link SrSDFDAGCoIterator} and {@link SrSDFDAGCoIteratorBuilder}.
 * Contains only instance independent, non-acyclic-like graphs
 *
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SrSDFDAGCoIteratorTest {
	protected val SDFGraph sdf

	protected val PureDAGConstructor dagGen

	new(SDFGraph sdf, PureDAGConstructor dagGen) {
		this.sdf = sdf
		this.dagGen = dagGen
	}

	/**
	 * Generate following parameters using manually defined non-acyclic-like, instance independent
	 * graphs:
	 * <ol>
	 * 	<li> Non-acyclic-like, instance independent {@link SDFGraph}
	 * 	<li> Its DAG using a {@link PureDAGConstructor} implementation
	 * </ol>
	 */
	@Parameterized.Parameters
	static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList

		val parameterArray = #[
			ExampleGraphs.strictlyCyclic,
			ExampleGraphs.strictlyCyclicDual,
			ExampleGraphs.strictlyCyclic2,
			ExampleGraphs.mixedNetwork1
		]

		parameterArray.forEach[sdf |
			val dagGen = new SDF2DAG(sdf)

			val parallelVisitor = new DependencyAnalysisOperations
			dagGen.accept(parallelVisitor)

			if(!parallelVisitor.isIndependent){
				throw new AssertionError("SDF is not instance independent")
			}
			parameters.add(#[sdf, dagGen])
			parameters.add(#[sdf, new DAG2DAG(dagGen)])
		]

		return parameters
	}

	/**
	 * All the visitable instance of DAG is seen when traversing
	 * its corresponding SrSDF
	 */
	@Test
	def void traversalIsCorrect(){
		val hsdfVisitor = new ToHSDFVisitor
		sdf.accept(hsdfVisitor)
		val srsdf = hsdfVisitor.output

		val ncg = new NodeChainGraph(srsdf)

		val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector
		sdf.accept(acyclicLikeVisitor)

		if(!acyclicLikeVisitor.isAcyclicLike) {
			acyclicLikeVisitor.SDFSubgraphs.forEach[sdfSubgraph |
				// Get strongly connected components
				val strongCompDetector = new KosarajuStrongConnectivityInspector(sdfSubgraph)

				// Collect strongly connected component that has loops in it
				// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
				strongCompDetector.getStronglyConnectedComponents.forEach[ subgraph |
					val dirSubGraph = subgraph as AsSubgraph<SDFAbstractVertex, SDFEdge>
					val cycleDetector = new CycleDetector(dirSubGraph)
					if(cycleDetector.detectCycles) {
						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
						// one loop. Perform the tests now. As only instance independent graphs are
						// added, no check is made
						val subgraphDAGGen = new SDF2DAG(dirSubGraph)
						val dag = subgraphDAGGen.outputGraph
						val sc = new KosarajuStrongConnectivityInspector(dirSubGraph)
						val sourceActors = sc.stronglyConnectedComponents.filter[sg |
							val cd = new CycleDetector(sg as
								AsSubgraph<SDFAbstractVertex, SDFEdge>
							)
							!cd.detectCycles
						].map[sg |
							sg.vertexSet
						].flatten
						.toList
						val moveInstanceVisitor = new MovableInstances(sourceActors)
						subgraphDAGGen.accept(moveInstanceVisitor)

						val movableInstances = moveInstanceVisitor.movableInstances

						val srsdfInstancesSeen = newArrayList

						val sit = (new SrSDFDAGCoIteratorBuilder)
									.addDAG(dag)
									.addNodeChainGraph(ncg)
									.addVisitableNodes(movableInstances)
									.build()
						while(sit.hasNext) {
							srsdfInstancesSeen.add(sit.next)
						}

						movableInstances.forEach[instance |
							if( (instance instanceof SDFJoinVertex) || (instance instanceof SDFForkVertex)) {
								Assert.assertTrue(srsdfInstancesSeen.exists[node | node.name == instance.name])
							} else {
								val srsdfInstance = DAGUtils.findVertex(instance, dag, srsdf)
								Assert.assertTrue(srsdfInstance !== null)
								Assert.assertTrue(srsdfInstancesSeen.contains(srsdfInstance))
							}
						]
					}
				]
			]
		}
	}
}
