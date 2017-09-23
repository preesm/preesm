package org.abo.preesm.plugin.dataparallel.operations.visitor

import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.jgrapht.alg.CycleDetector

/**
 * Class that detects Acyclic-like patterns from a given subgraph
 * An SDFG is acylic-like when removing the edges containing delay elements equal to 
 * production rate times repetition rate of its source (or consumption rate times repetition rate 
 * of its target) makes the SDFG completely acyclic.
 * 
 * This class operates on DirectedSubgraph only!
 * 
 * @author Sudeep Kanur
 */
class AcyclicLikeSubgraphDetector implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isAcyclicLike
	
	/**
	 * Constructor
	 * 
	 * @param log Logger instance to report
	 */
	new(Logger log) {
		this.isAcyclicLike = null
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
	
	override visit(SDFGraph sdf) throws SDF4JException {
		val processSDF = sdf.clone
		val removableEdges = newArrayList
		processSDF.edgeSet.forEach[edge |
			val prod = edge.prod.intValue
			val cons = edge.cons.intValue
			val delay = edge.delay.intValue
			val sourceRep = edge.source.nbRepeatAsInteger
			val targetRep = edge.target.nbRepeatAsInteger
			
			if((delay >= prod * sourceRep) && 
				(delay >= cons * targetRep)) {
					removableEdges.add(edge)
			}		
		]
		
		removableEdges.forEach[edge |
			processSDF.removeEdge(edge)
		]
		
		val cycleDetector = new CycleDetector(processSDF)
		isAcyclicLike = !cycleDetector.detectCycles
	}
	
	override visit(SDFAbstractVertex sdfVertex) throws SDF4JException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
}