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
package fi.abo.preesm.dataparallel.fifo

import java.util.logging.Level
import java.util.logging.Logger
import org.ietr.dftools.algorithm.factories.SDFEdgeFactory
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType
import org.ietr.dftools.algorithm.model.AbstractGraph
import org.ietr.dftools.algorithm.model.IInterface
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.jgrapht.EdgeFactory

/**
 * A special case of {@link SDFGraph} used to denote non-trivial initialization of FIFOs
 * and that store normal {@link SDFAbstractVertex}s and {@link FifoActor}s.
 * <p>
 * Properties of this graph is that, the entry and exit nodes are always FifoActors and
 * the graph contains no delays.
 * @author Sudeep Kanur
 */
class FifoActorGraph extends SDFGraph {
	public static String MODEL = "fag" // ;)

	new (){
		super()
		this.propertyBean.setValue(AbstractGraph.MODEL, MODEL)
	}

	new(EdgeFactory<SDFAbstractVertex, SDFEdge> ef) {
		super(ef)
		this.propertyBean.setValue(AbstractGraph.MODEL, MODEL)
	}

	new(SDFEdgeFactory factory) {
		super(factory)
		this.propertyBean.setValue(AbstractGraph.MODEL, MODEL)
	}

	/**
	 * In addition to adding edge, it verifies that the edge contains no delays
	 *
	 * @param source Source {@link SDFAbstractVertex}
	 * @param sourcePort Output port of the source SDFAbstractVertex
	 * @param sink Sink/Target SDFAbstractVertex
	 * @param sinkPort Input port of the sink SDFAbstractVertex
	 * @param prod Production rate of the edge
	 * @param cons Consumption rate of the edge
	 * @param delay Delay must always be 0.
	 */
	override addEdge(SDFAbstractVertex source,
					 IInterface sourcePort,
					 SDFAbstractVertex sink,
					 IInterface sinkPort,
					 AbstractEdgePropertyType<?> prod,
					 AbstractEdgePropertyType<?> cons,
					 AbstractEdgePropertyType<?> delay) {
		if(delay.longValue > 0) {
			throw new SDF4JException("FIFO-Actor Graphs cannot have delay in their edges")
		}
		return super.addEdge(source, sourcePort, sink, sinkPort, prod, cons, delay)
	}

	/**
	 * In addition to adding edge, it verifies that the edge contains no delays
	 *
	 * @param source Source {@link SDFAbstractVertex}
	 * @param sink Sink/Target SDFAbstractVertex
	 * @param prod Production rate of the edge
	 * @param cons Consumption rate of the edge
	 * @param delay Delay must always be 0.
	 */
	override addEdge(SDFAbstractVertex source,
					 SDFAbstractVertex sink,
					 AbstractEdgePropertyType<?> prod,
					 AbstractEdgePropertyType<?> cons,
					 AbstractEdgePropertyType<?> delay) {
		if(delay.longValue > 0) {
			throw new SDF4JException("FIFO-Actor Graphs cannot have delay in their edges")
		}
		return super.addEdge(source, sink, prod, cons, delay)
	}

	/**
	 * Validates the graph that all the edges have 0 delays.
	 *
	 * @param logger {@link Logger} logger instance
	 */
	override validateModel(Logger logger) {
		if(!edgeSet.forall[edge |
			edge.delay.longValue == 0
		]) {
			val message = "Edges of FIFO Actor cannot have delays in them."
			logger.log(Level.SEVERE, message)
			throw new SDF4JException(message)
		}
		return super.validateModel(logger)
	}
}
