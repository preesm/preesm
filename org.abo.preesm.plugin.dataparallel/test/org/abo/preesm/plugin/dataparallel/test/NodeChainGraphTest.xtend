package org.abo.preesm.plugin.dataparallel.test

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Collection
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.abo.preesm.plugin.dataparallel.NodeChainGraph
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.junit.Assert
import org.ietr.dftools.algorithm.model.sdf.SDFVertex
import org.jgrapht.alg.DijkstraShortestPath
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.operations.visitor.RootExitOperations
import org.abo.preesm.plugin.dataparallel.DAGUtils
import java.util.HashMap
import java.util.List
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex

/**
 * Test construction and operations of NodeChainGraphs
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
	
	new(SDFGraph sdf, SDFGraph srsdf, NodeChainGraph nodechain, boolean hasImplodes, boolean hasExplodes) {
		this.sdf = sdf
		this.srsdf = srsdf
		this.nodechain = nodechain
		this.hasImplodes = hasImplodes
		this.hasExplodes = hasExplodes
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
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
	 * 
	 * Weak Test as it checks only existence of such nodes
	 */
	@org.junit.Test def void testNodechainHasUserImplodesExplodes() {
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
	 * 
	 * Somewhat strong test. 
	 */
	@org.junit.Test def void testNodechainVertexIsProperType() {
		if(!hasImplodes && !hasExplodes) {
			Assert.assertTrue(nodechain.nodechains.keySet.forall[vertex |
				vertex instanceof SDFVertex || vertex instanceof SDFBroadcastVertex
			])
		}
	}
	
	/**
	 * Check each implode of the node chain is actually the previous node
	 * of the associated vertex. Also check each explode node is actually 
	 * successive node of the associated vertex. This works on graphs that
	 * have no user defined fork and join actors
	 * 
	 * Strong test
	 */
	@org.junit.Test def void testImplodesExplodesAreProperlyAssociated() {
		if(!hasImplodes && !hasExplodes) {
			srsdf.vertexSet.forEach[vertex |
				if(vertex instanceof SDFJoinVertex) {
					Assert.assertTrue(srsdf.outgoingEdgesOf(vertex).size == 1)
					val outEdge = srsdf.outgoingEdgesOf(vertex).get(0)
					Assert.assertTrue(outEdge !== null)
					Assert.assertTrue(nodechain.nodechains.keySet.contains(outEdge.target))
				} else if (vertex instanceof SDFForkVertex) {
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
	 * Test that previous nodes has edge to the current node in an appropriate way.
	 * 
	 * Strong test
	 */
	@org.junit.Test def void testAppropriateEdgeExistsBetweenNodes() {
		nodechain.nodechains.forEach[vertex, chain |
			val prevNodes = nodechain.getPreviousNodes(vertex)
			
			// Source nodes have no previous nodes
			if(srsdf.incomingEdgesOf(vertex).empty) {
				Assert.assertTrue(prevNodes === null)
			} else {
				// There exists a shortest path between each previous node and current node
				prevNodes.forEach[prevNode |
					val pathDetector = new DijkstraShortestPath(srsdf, prevNode.vertex, vertex)
					Assert.assertTrue(pathDetector.pathLength != 0)	
				]
			}
		]
	}
	
	/**
	 * Helper function to fetch root nodes from SrSDF graph that are not source nodes. Thus, it
	 * is guaranteed that these instances will have delay behind them.
	 * 
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
	 * Test fetching of input delays work well
	 * 
	 * There must be delays behind non-source root instances of SrSDF graph. Further, the number
	 * of delays must be greater than equal to its consumption rate
	 * 
	 * Strong
	 */
	@org.junit.Test def void testFetchingInputDelays() {
		val srsdfRootNodes = nonSourceRootNodes
		
		// Now each srsdf root nodes (that is not source node) must have
		// delay equal or greater than consumption rate of its edge
		srsdfRootNodes.forEach[root |
			val inEdgeDelay = nodechain.getEdgewiseInputDelays(root)
			srsdf.incomingEdgesOf(root).forEach[inEdge |
				val cons = inEdge.cons.intValue
				val delay = inEdgeDelay.get(inEdge)
				Assert.assertTrue(delay !== null)
				Assert.assertTrue(delay.intValue >= cons)
			]
		]
	}
	
	/**
	 * Test fetching of output delays work well
	 * 
	 * This is similar to {@link testFetchingInputDelays()}. Once non-source root instances are found
	 * we first go to its previous nodes. Then we check its output has non-zero delay at the edges connecting
	 * to this non-source root instances.
	 * 
	 * Somewhat Strong
	 */
	@org.junit.Test def void testFetchingOutputDelays() {
		val srsdfRootNodes = nonSourceRootNodes
		
		srsdfRootNodes.forEach[root |
			// Get previous nodes of srsdf root nodes
			val prevNodes = nodechain.getPreviousNodes(root)
			// Now each output edge that has path to the current srsdf root
			// node must have delay greater or
			//equal to that of its production rate
			prevNodes.forEach[node |
				val outEdgeDelay = nodechain.getEdgewiseOutputDelays(node.vertex)
				val pathDetector = new DijkstraShortestPath(srsdf, node.vertex, root)
				srsdf.outgoingEdgesOf(node.vertex).forEach[outEdge |
					if(pathDetector.pathEdgeList.contains(outEdge)) {						
						val delay = outEdgeDelay.get(outEdge)
						Assert.assertTrue(delay !== null)
						Assert.assertTrue(delay > 0)
					} 	
				]
			]	
		]
	}
	
	/**
	 * Test setting of delays at input of a node
	 * 
	 * The test modifies the values of SrSDF graph: first it sets all delays of non-source root nodes
	 * to 0, then to a large negative value and finally back to the initial value
	 * 
	 * Warning! This tests changes the delay state of the SrSDF graph and hence it is crucial that
	 * this test completes successfully. Failure of this test may lead to failure of other tests too.
	 */
	@org.junit.Test def void testSettingInputDelays() {
		val srsdfRootNodes = nonSourceRootNodes
		
		srsdfRootNodes.forEach[root |
			val inEdgeDelay = nodechain.getEdgewiseInputDelays(root)
			val copyInEdgeDelay = new HashMap(inEdgeDelay)
			val newEdgeDelay = new HashMap(inEdgeDelay)
			newEdgeDelay.keySet.forEach[key | newEdgeDelay.put(key, 0)]
			nodechain.setEdgewiseInputDelays(root, newEdgeDelay)
		
			nodechain.getEdgewiseInputDelays(root).forEach[node, delay |
				Assert.assertTrue(delay !== null)
				Assert.assertTrue(delay == 0)
			]
			
			newEdgeDelay.keySet.forEach[key | newEdgeDelay.put(key, -100)]
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
	 * Test setting of delays at output of a node
	 * 
	 * The test is similar to previous tests
	 * 
	 * The test modifies the values of SrSDF graph: first it sets all delays of preceding nodes of 
	 * non-source root nodes to 0, then to a large negative value and finally back to the initial value
	 * 
	 * Warning! This tests changes the delay state of the SrSDF graph and hence it is crucial that
	 * this test completes successfully. Failure of this test may lead to failure of other tests too.
	 */
	@org.junit.Test def void testSettingOutputDelays() {
		val srsdfRootNodes = nonSourceRootNodes
		
		srsdfRootNodes.forEach[root |
			val prevNodes = nodechain.getPreviousNodes(root)
			
			prevNodes.forEach[node |
				val outEdgeDelay = nodechain.getEdgewiseOutputDelays(node.vertex)
				
				val copyOutEdgeDelay = new HashMap(outEdgeDelay)
				val newOutEdgeDelay = new HashMap(outEdgeDelay)
				newOutEdgeDelay.keySet.forEach[key | newOutEdgeDelay.put(key, 0)]
				nodechain.setEdgewiseOutputDelays(node.vertex, newOutEdgeDelay)
				nodechain.getEdgewiseOutputDelays(node.vertex).forEach[n, delay |
					Assert.assertTrue(delay !== null)
					Assert.assertTrue(delay == 0)
				]
				
				newOutEdgeDelay.keySet.forEach[key | newOutEdgeDelay.put(key, -100)]
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