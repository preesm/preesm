package org.abo.preesm.plugin.dataparallel.test

import java.util.Collection
import java.util.HashMap
import org.abo.preesm.plugin.dataparallel.NodeChainGraph
import org.abo.preesm.plugin.dataparallel.SrSDFToSDF
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized)
class SrSDFToSDFTest {
	protected val SDFGraph sdf
	
	protected val SDFGraph srsdf
	
	new(SDFGraph sdf, SDFGraph srsdf) {
		this.sdf = sdf
		this.srsdf = srsdf
	}	
	
	@Parameters
	public static def Collection<Object[]> instancesToTest() {
		// Contains the following parameters
		// 1. SDF graph
		// 2. SRSDF graph
		
		val parameters = newArrayList
		Util.provideAllGraphsContext.forEach[sdfContext |
			val sdf = sdfContext.graph
			val srsdfVisitor = new ToHSDFVisitor
			sdf.accept(srsdfVisitor)
			val srsdf = srsdfVisitor.output
			parameters.add(#[sdf, srsdf])	
		]
		return parameters
	}
	
	/**
	 * Get total delays present in the graph
	 * @return Total delays in this {@link SDFGraph} instance
	 */
	private def int getTotalDelays(SDFGraph graph) {
		var totalDelays = 0
		for(edge: graph.edgeSet) {
			totalDelays += edge.delay.intValue
		}
		return totalDelays
	}
	
	/**
	 * Helper function to set the delays of input edges of all the nodes present in the {@link NodeChainGraph}
	 * with a given value
	 * @param nodeChainGraph The {@link NodeChainGraph} instance
	 * @param value Value of the delays
	 */
	private def NodeChainGraph setDelays(NodeChainGraph nodeChainGraph, int value) {
		for(node: nodeChainGraph.nodechains.keySet) {
			val edgeDelayMap = nodeChainGraph.getEdgewiseInputDelays(node)
			if(edgeDelayMap !== null) {
				val setDelayMap = new HashMap(edgeDelayMap)
				for(edge: edgeDelayMap.keySet) {
					setDelayMap.put(edge, value)
				} 	
				nodeChainGraph.setEdgewiseInputDelays(node, setDelayMap)
			}
		}
	}
	
	/**
	 * Test that after retiming SrSDF graph and transforming it back to SDF graph, the total
	 * delays in each of the graph is same.
	 * 
	 * First set all delays in the graph to 0, 
	 * then all nodes to some negative value, 
	 * later all nodes to some positive value.
	 * 
	 * Finally, check if original SDF graph can be retrieved.
	 * 
	 * Strong test
	 */
	@Test
	public def void testSDFRetiming() {
		val sdfCopy = sdf.clone
		val srsdfVisitor = new ToHSDFVisitor
		sdfCopy.accept(srsdfVisitor)
		val srsdfCopy = srsdfVisitor.output
		val nodeChainGraph = new NodeChainGraph(srsdfCopy)
		var transform = new SrSDFToSDF(sdfCopy, srsdfCopy)
		
		var srsdfDelays = getTotalDelays(srsdfCopy)
		var sdfDelays = getTotalDelays(transform.getRetimedSDF(srsdfCopy))
		Assert.assertEquals(srsdfDelays, sdfDelays)
		
		// Set all delays to 0
		setDelays(nodeChainGraph, 0)
		
		srsdfDelays = getTotalDelays(srsdfCopy)
		sdfDelays = getTotalDelays(transform.getRetimedSDF(srsdfCopy))
		Assert.assertEquals(sdfDelays, 0)
		Assert.assertEquals(srsdfDelays, sdfDelays)
		
		// Now set all delays to -100
		setDelays(nodeChainGraph, -100)
		srsdfDelays = getTotalDelays(srsdfCopy)
		sdfDelays = getTotalDelays(transform.getRetimedSDF(srsdfCopy))
		Assert.assertEquals(srsdfDelays, sdfDelays)
		
		// Now set all delays to 100
		setDelays(nodeChainGraph, 100)
		srsdfDelays = getTotalDelays(srsdfCopy)
		sdfDelays = getTotalDelays(transform.getRetimedSDF(srsdfCopy))
		Assert.assertEquals(srsdfDelays, sdfDelays)
		
		// Now check if we can get original delay values
		srsdfDelays = getTotalDelays(srsdf)
		sdfDelays = getTotalDelays(transform.originalSDF)
		Assert.assertEquals(srsdfDelays, sdfDelays)
	}
}