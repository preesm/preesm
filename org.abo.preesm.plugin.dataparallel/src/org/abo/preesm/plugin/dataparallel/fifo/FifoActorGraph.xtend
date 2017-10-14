package org.abo.preesm.plugin.dataparallel.fifo

import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.AbstractGraph
import org.jgrapht.EdgeFactory
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.factories.SDFEdgeFactory
import org.ietr.dftools.algorithm.model.IInterface
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import java.util.logging.Logger
import java.util.logging.Level

/**
 * A special case of {@link SDFGraph} used to denote non-trivial initialization of FIFOs
 * and that store normal {@link SDFAbstractVertex}s and {@link FifoActor}s.
 * 
 * Properties of this graph is that, the entry and exit nodes are always {@link FifoActor}s and 
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
	 * @param sourcePort Output port of the source {@link SDFAbstractVertex}
	 * @param sink Sink/Target {@link SDFAbstractVertex}
	 * @param sinkPort Input port of the sink {@link SDFAbstractVertex}
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
		if(delay.intValue > 0) {
			throw new SDF4JException("FIFO-Actor Graphs cannot have delay in their edges")
		}
		return super.addEdge(source, sourcePort, sink, sinkPort, prod, cons, delay)
	}
	
	/**
	 * In addition to adding edge, it verifies that the edge contains no delays
	 * 
	 * @param source Source {@link SDFAbstractVertex}
	 * @param sink Sink/Target {@link SDFAbstractVertex}
	 * @param prod Production rate of the edge
	 * @param cons Consumption rate of the edge
	 * @param delay Delay must always be 0.
	 */
	override addEdge(SDFAbstractVertex source,
					 SDFAbstractVertex sink,
					 AbstractEdgePropertyType<?> prod,
					 AbstractEdgePropertyType<?> cons,
					 AbstractEdgePropertyType<?> delay) {
		if(delay.intValue > 0) {
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
			edge.delay.intValue == 0
		]) {
			val message = "Edges of FIFO Actor cannot have delays in them."
			logger.log(Level.SEVERE, message)
			throw new SDF4JException(message)
		}
		return super.validateModel(logger)
	}
}