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
package fi.abo.preesm.dataparallel.operations

import java.util.List
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.jgrapht.alg.cycle.CycleDetector
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.SDFEdge
import org.preesm.algorithm.model.sdf.SDFGraph
import org.preesm.algorithm.model.visitors.IGraphVisitor
import org.preesm.algorithm.model.visitors.SDF4JException

/**
 * Class that detects Acyclic-like patterns from a given subgraph.
 * <p>
 * An SDFG is acylic-like when removing the edges containing delay elements equal to
 * production rate times repetition rate of its source (or consumption rate times repetition rate
 * of its target) makes the SDFG completely acyclic.
 * <p>
 * This class operates on DirectedSubgraph only!
 * <p>
 * @author Sudeep Kanur
 */
class AcyclicLikeSubgraphDetector implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isAcyclicLike

	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var SDFGraph processedSDF

	/**
	 * Constructor
	 *
	 * @param log Logger instance to report
	 */
	new(Logger log) {
		this.isAcyclicLike = null
		this.processedSDF = null
	}

	/**
	 * Constructor used for test
	 *
	 * @param vertexSet Set of vertices whose connected edges have to be investigated
	 */
	new() {
		this(null)
	}

	override visit(SDFEdge sdfEdge) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	/**
	 * Process {@link SDFGraph} to check whether it is acyclic-like or not.
	 *
	 * @param sdf A {@link SDFGraph} instance that has to be checked
	 */
	override visit(SDFGraph sdf) throws SDF4JException {
		processedSDF = sdf.copy
		val removableEdges = newArrayList
		processedSDF.edgeSet.forEach[edge |
			val prod = edge.prod.longValue
			val cons = edge.cons.longValue
			val delay = edge.delay.longValue
			val sourceRep = edge.source.nbRepeatAsLong
			val targetRep = edge.target.nbRepeatAsLong

			if((delay >= prod * sourceRep) &&
				(delay >= cons * targetRep)) {
					removableEdges.add(edge)
			}
		]

		removableEdges.forEach[edge |
			processedSDF.removeEdge(edge)
		]

		val cycleDetector = new CycleDetector(processedSDF)
		isAcyclicLike = !cycleDetector.detectCycles
	}

	/**
	 * Return list of independent SDF subgraphs whose connectivity may be broken because of
	 * removing edges. Edges that have delays >= production rate (or consumption rate) can create
	 * breakage in the otherwise connected SDF graph.
	 *
	 * @return List of subgraphs that are cloned from original SDF graph, but contains only connected
	 * components
	 */
	def List<SDFGraph> getSDFSubgraphs() {
		val sdfSubgraphs = newArrayList

		processedSDF.allSubGraphs.forEach[subgraphVertexList |
			val sdfSubgraph = processedSDF.copy
			val removableEdges = newArrayList
			val removableVertices = newArrayList

			sdfSubgraph.edgeSet.forEach[edge |
				if(! subgraphVertexList.contains(edge.source)) {
					removableEdges.add(edge)
					removableVertices.add(edge.source)
				}
				if(! subgraphVertexList.contains(edge.target)) {
					removableEdges.add(edge)
					removableVertices.add(edge.target)
				}
			]

			removableEdges.forEach[edge |
				if(sdfSubgraph.edgeSet.contains(edge)) {
					sdfSubgraph.removeEdge(edge)
				}
			]
			removableVertices.forEach[vertex |
				sdfSubgraph.removeVertex(vertex)
			]
			sdfSubgraphs.add(sdfSubgraph)
		]

		return sdfSubgraphs
	}

	override visit(SDFAbstractVertex sdfVertex) throws SDF4JException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

}
