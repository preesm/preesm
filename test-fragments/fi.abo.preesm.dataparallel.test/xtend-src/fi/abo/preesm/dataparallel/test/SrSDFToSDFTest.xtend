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

import java.util.Collection
import java.util.HashMap
import fi.abo.preesm.dataparallel.NodeChainGraph
import fi.abo.preesm.dataparallel.SrSDFToSDF
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import fi.abo.preesm.dataparallel.test.util.Util

/**
 * Parametric test for {@link SrSDFToSDF}
 *
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SrSDFToSDFTest {
	protected val SDFGraph sdf

	protected val SDFGraph srsdf

	/**
	 * Has the following parameters from {@link Util#provideAllGraphsContext}:
	 * <ol>
	 * 	<li> A {@link SDFGraph} instance
	 * 	<li> Its SrSDF generated from {@link ToHSDFVisitor}
	 * </ol>
	 */
	new(SDFGraph sdf, SDFGraph srsdf) {
		this.sdf = sdf
		this.srsdf = srsdf
	}

	/**
	 * Generate following parameters from {@link Util#provideAllGraphsContext}:
	 * <ol>
	 * 	<li> A {@link SDFGraph} instance
	 * 	<li> Its SrSDF generated from {@link ToHSDFVisitor}
	 * </ol>
	 */
	@Parameters
	static def Collection<Object[]> instancesToTest() {
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
	 *
	 * @param graph The SDFGraph
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
	 * After re-timing SrSDF graph and transforming it back to SDF graph, the total
	 * delays in each of the graph is same.
	 * <p>
	 * Following operations are carried out one after the other:
	 * <ol>
	 * 	<li> Set all delays in the graph to 0,
	 * 	<li> Set all nodes to some negative value,
	 * 	<li> Set all nodes to some positive value.
	 * 	<li> Revert to original delays
	 * </ol>
	 * <p>
	 * <i>Strong test</i>
	 */
	@Test
	def void testSDFRetiming() {
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
