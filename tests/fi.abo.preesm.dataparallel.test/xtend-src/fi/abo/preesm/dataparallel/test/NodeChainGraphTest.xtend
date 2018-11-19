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

import fi.abo.preesm.dataparallel.DAGComputationBug
import fi.abo.preesm.dataparallel.DAGUtils
import fi.abo.preesm.dataparallel.NodeChainGraph
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.operations.RootExitOperations
import fi.abo.preesm.dataparallel.test.util.Util
import java.util.Collection
import java.util.HashMap
import java.util.List
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.SDFGraph
import org.preesm.algorithm.model.sdf.SDFVertex
import org.preesm.algorithm.model.sdf.esdf.SDFBroadcastVertex
import org.preesm.algorithm.model.sdf.esdf.SDFForkVertex
import org.preesm.algorithm.model.sdf.esdf.SDFJoinVertex
import org.preesm.algorithm.model.sdf.visitors.ToHSDFVisitor

/**
 * Parameteric test for {@link NodeChainGraph}s
 *
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class NodeChainGraphTest {
	val SDFGraph sdf

	val SDFGraph srsdf

	val NodeChainGraph nodechain

	val boolean hasImplodes

	val boolean hasExplodes

	/**
	 * Has the following parameters with {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDFGraph} instance
	 * 	<li> Its SrSDF from {@link ToHSDFVisitor}
	 * 	<li> {@link NodeChainGraph} of SrSDFG
	 * 	<li> <code>true</code> if SDFG has user specified implodes, <code>false</code> otherwise
	 * 	<li> <code>true</code> if SDFG has user specified explodes, <code>false</code> otherwise
	 * </ol>
	 */
	new(SDFGraph sdf, SDFGraph srsdf, NodeChainGraph nodechain, boolean hasImplodes, boolean hasExplodes) {
		this.sdf = sdf
		this.srsdf = srsdf
		this.nodechain = nodechain
		this.hasImplodes = hasImplodes
		this.hasExplodes = hasExplodes
	}

	/**
	 * Generates following parameters with {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDFGraph} instance
	 * 	<li> Its SrSDF from {@link ToHSDFVisitor}
	 * 	<li> {@link NodeChainGraph} of SrSDFG
	 * 	<li> <code>true</code> if SDFG has user specified implodes, <code>false</code> otherwise
	 * 	<li> <code>true</code> if SDFG has user specified explodes, <code>false</code> otherwise
	 * </ol>
	 */
	@Parameterized.Parameters
	static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		Util.provideAllGraphs.forEach[sdf |
			//[SDF graph,
			// SrSDF graph,
			// nodechain graph,
			// has user specified implode,
			// has user specified explode]

			// Check if sdf graph has any implodes
			val hasImplodes = sdf.vertexSet.exists[vertex |
				vertex instanceof SDFJoinVertex
			]
			val hasExplodes = sdf.vertexSet.exists[vertex |
				vertex instanceof SDFForkVertex
			]
			val srsdfVisitor = new ToHSDFVisitor
			sdf.accept(srsdfVisitor)
			val srsdf = srsdfVisitor.output
			val nodeChainGraph = new NodeChainGraph(srsdf)
			parameters.add(#[sdf, srsdf, nodeChainGraph, hasImplodes, hasExplodes])
		]

		return parameters
	}

	/**
	 * If the SDF graph has user defined implodes and explodes,
	 * then test that nodechain also has them
	 * <p>
	 * <i>Weak Test</i> as it checks only existence of such nodes
	 */
	@Test def void testNodechainHasUserImplodesExplodes() {
		if(hasImplodes){
			Assert.assertTrue(nodechain.nodechains.keySet.exists[vertex |
				vertex instanceof SDFJoinVertex
			])
		}

		if(hasExplodes) {
			Assert.assertTrue(nodechain.nodechains.keySet.exists[vertex |
				vertex instanceof SDFForkVertex
			])
		}
	}

	/**
	 * If the SDF graph has no user defined implodes and explodes, then
	 * each node vertex is of type SDFVertex
	 * <p>
	 * Somewhat <i>strong test</i>
	 */
	@Test def void testNodechainVertexIsProperType() {
		if(!hasImplodes && !hasExplodes) {
			Assert.assertTrue(nodechain.nodechains.keySet.forall[vertex |
				vertex instanceof SDFVertex || vertex instanceof SDFBroadcastVertex
			])
		}
	}

	/**
	 * <ol>
	 * 	<li> Each implode of the node chain is actually the previous node
	 * of the associated vertex.
	 * 	<li> Each explode node is actually successive node of the associated vertex.
	 * This works on graphs that have no user defined fork and join actors
	 * <p>
	 * <i>Strong test</i>
	 */
	@Test def void testImplodesExplodesAreProperlyAssociated() {
		if(!hasImplodes && !hasExplodes) {
			srsdf.vertexSet.forEach[vertex |

				//1. Each implode of the node chain is actually the previous node
				if(vertex instanceof SDFJoinVertex) {
					Assert.assertTrue(srsdf.outgoingEdgesOf(vertex).size == 1)
					val outEdge = srsdf.outgoingEdgesOf(vertex).get(0)
					Assert.assertTrue(outEdge !== null)
					Assert.assertTrue(nodechain.nodechains.keySet.contains(outEdge.target))
				} else if (vertex instanceof SDFForkVertex) {
				//2. Each explode of the node chain is actually from the successive node
					Assert.assertTrue(srsdf.incomingEdgesOf(vertex).size == 1)
					val inEdge = srsdf.incomingEdgesOf(vertex).get(0)
					Assert.assertTrue(inEdge !== null)
					Assert.assertTrue(nodechain.nodechains.keySet.contains(inEdge.source))
				} else {
					Assert.assertTrue(nodechain.nodechains.keySet.contains(vertex))
				}
			]
		}
	}

	/**
	 * {@link NodeChainGraph#getPreviousNodes} creates previous nodes with edges to the current
	 * node in an appropriate way.
	 * <p>
	 * <i>Strong test</i>
	 */
	@Test def void testAppropriateEdgeExistsBetweenNodes() {
		nodechain.nodechains.forEach[vertex, chain |
			val prevNodes = nodechain.getPreviousNodes(vertex)

			// Source nodes have no previous nodes
			if(srsdf.incomingEdgesOf(vertex).empty) {
				Assert.assertTrue(prevNodes === null)
			} else {
				// There exists a shortest path between each previous node and current node
				// jgrapht 0.8.2
				// prevNodes.forEach[prevNode |
				//  	val pathDetector = new DijkstraShortestPath(srsdf, prevNode.vertex, vertex)
				//   	Assert.assertTrue(pathDetector.pathLength != 0)
				// ]
				// jgrapht 1.1.0
				prevNodes.forEach[prevNode |
					val pathDetector = new DijkstraShortestPath(srsdf);
					//, prevNode.vertex, vertex)
					val path = pathDetector.getPath(prevNode.vertex, vertex)
					Assert.assertTrue(path.length != 0)
				]
			}
		]
	}

	/**
	 * Helper function to fetch root nodes from SrSDF graph that are not source nodes. Thus, it
	 * is guaranteed that these instances will have delay behind them.
	 * <p>
	 * @return List of root instance from SrSDF graph whose actors are not source actors
	 */
	def List<SDFAbstractVertex> getNonSourceRootNodes() {
		val dagGen = new SDF2DAG(sdf)
		val rootVisitor = new RootExitOperations
		dagGen.accept(rootVisitor)
		val rootNodes = rootVisitor.rootInstances

		val srsdfRootNodes = newArrayList
		rootNodes.forEach[root |
			val srsdfRoot = DAGUtils.findVertex(root, dagGen.outputGraph, srsdf)
			if(srsdfRoot === null) {
				throw new DAGComputationBug("Could not find DAG node: " + root + " in SrSDF graph")
			}

			if(!srsdf.incomingEdgesOf(srsdfRoot).empty) {
				// This is not a source node. So there is delay behind it
				srsdfRootNodes.add(srsdfRoot)
			}
		]

		return srsdfRootNodes
	}

	/**
	 * Fetching of input delays using {@link NodeChainGraph#getEdgewiseInputDelays} work well
	 * <p>
	 * There must be delays behind non-source root instances of SrSDF graph. Further, the number
	 * of delays must be greater than equal to its consumption rate
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test def void testFetchingInputDelays() {
		val srsdfRootNodes = nonSourceRootNodes

		// Now each srsdf root nodes (that is not source node) must have
		// delay equal or greater than consumption rate of its edge
		srsdfRootNodes.forEach[root |
			val inEdgeDelay = nodechain.getEdgewiseInputDelays(root)
			srsdf.incomingEdgesOf(root).forEach[inEdge |
				val cons = inEdge.cons.longValue
				val delay = inEdgeDelay.get(inEdge)
				Assert.assertTrue(delay !== null)
				Assert.assertTrue(delay.intValue >= cons)
			]
		]
	}

	/**
	 * Fetching of output delays using {@link NodeChainGraph#getEdgewiseOutputDelays} work well
	 * <p>
	 * This is similar to {@link #testFetchingInputDelays()}. Once non-source root instances are found
	 * we first go to its previous nodes. Then we check its output has non-zero delay at the edges connecting
	 * to this non-source root instances.
	 * <p>
	 * <i>Somewhat Strong Test</i>
	 */
	@Test def void testFetchingOutputDelays() {
		val srsdfRootNodes = nonSourceRootNodes

		srsdfRootNodes.forEach[root |
			// Get previous nodes of srsdf root nodes
			val prevNodes = nodechain.getPreviousNodes(root)
			// Now each output edge that has path to the current srsdf root
			// node must have delay greater or
			//equal to that of its production rate
			prevNodes.forEach[node |
				val outEdgeDelay = nodechain.getEdgewiseOutputDelays(node.vertex)
				val pathDetector = new DijkstraShortestPath(srsdf)
				val path = pathDetector.getPath(node.vertex, root)
				srsdf.outgoingEdgesOf(node.vertex).forEach[outEdge |
					if(path.edgeList.contains(outEdge)) {
						val delay = outEdgeDelay.get(outEdge)
						Assert.assertTrue(delay !== null)
						Assert.assertTrue(delay > 0)
					}
				]
			]
		]
	}

	/**
	 * Setting of delays at input of a node using {@link NodeChainGraph#setEdgewiseInputDelays}
	 * <p>
	 * The test modifies the values of SrSDF graph: first it sets all delays of non-source root nodes
	 * to 0, then to a large negative value and finally back to the initial value
	 * <p>
	 * <b>Warning!<b> This tests changes the delay state of the SrSDF graph and hence it is crucial that
	 * this test completes successfully. Failure of this test may lead to failure of other tests too.
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test def void testSettingInputDelays() {
		val srsdfRootNodes = nonSourceRootNodes

		srsdfRootNodes.forEach[root |
			val inEdgeDelay = nodechain.getEdgewiseInputDelays(root)
			val copyInEdgeDelay = new HashMap(inEdgeDelay)
			val newEdgeDelay = new HashMap(inEdgeDelay)
			newEdgeDelay.keySet.forEach[key | newEdgeDelay.put(key, 0L)]
			nodechain.setEdgewiseInputDelays(root, newEdgeDelay)

			nodechain.getEdgewiseInputDelays(root).forEach[node, delay |
				Assert.assertTrue(delay !== null)
				Assert.assertTrue(delay == 0)
			]

			newEdgeDelay.keySet.forEach[key | newEdgeDelay.put(key, -100L)]
			nodechain.setEdgewiseInputDelays(root, newEdgeDelay)
			nodechain.getEdgewiseInputDelays(root).forEach[node, delay |
				Assert.assertTrue(delay !== null)
				Assert.assertTrue(delay == -100)
			]

			nodechain.setEdgewiseInputDelays(root, copyInEdgeDelay)

			nodechain.getEdgewiseInputDelays(root).forEach[node, delay |
				val oldDelay = copyInEdgeDelay.get(node)
				Assert.assertTrue(oldDelay !== null)
				Assert.assertTrue(oldDelay == delay)
			]
		]
	}

	/**
	 * Setting of delays at output of a node using {@link NodeChainGraph#setEdgewiseOutputDelays}
	 * <p>
	 * The test is similar to {@link #testSettingInputDelays}
	 * <p>
	 * The test modifies the values of SrSDF graph: first it sets all delays of preceding nodes of
	 * non-source root nodes to 0, then to a large negative value and finally back to the initial value
	 * <p>
	 * <b>Warning!</b> This tests changes the delay state of the SrSDF graph and hence it is crucial that
	 * this test completes successfully. Failure of this test may lead to failure of other tests too.
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test def void testSettingOutputDelays() {
		val srsdfRootNodes = nonSourceRootNodes

		srsdfRootNodes.forEach[root |
			val prevNodes = nodechain.getPreviousNodes(root)

			prevNodes.forEach[node |
				val outEdgeDelay = nodechain.getEdgewiseOutputDelays(node.vertex)

				val copyOutEdgeDelay = new HashMap(outEdgeDelay)
				val newOutEdgeDelay = new HashMap(outEdgeDelay)
				newOutEdgeDelay.keySet.forEach[key | newOutEdgeDelay.put(key, 0L)]
				nodechain.setEdgewiseOutputDelays(node.vertex, newOutEdgeDelay)
				nodechain.getEdgewiseOutputDelays(node.vertex).forEach[n, delay |
					Assert.assertTrue(delay !== null)
					Assert.assertTrue(delay == 0)
				]

				newOutEdgeDelay.keySet.forEach[key | newOutEdgeDelay.put(key, -100L)]
				nodechain.setEdgewiseOutputDelays(node.vertex, newOutEdgeDelay)
				nodechain.getEdgewiseOutputDelays(node.vertex).forEach[n, delay |
					Assert.assertTrue(delay !== null)
					Assert.assertTrue(delay == -100)
				]

				nodechain.setEdgewiseOutputDelays(node.vertex, copyOutEdgeDelay)
				nodechain.getEdgewiseOutputDelays(node.vertex).forEach[n, delay |
					val oldDelay = copyOutEdgeDelay.get(n)
					Assert.assertTrue(oldDelay !== null)
					Assert.assertTrue(oldDelay == delay)
				]
			]
		]
	}
}
