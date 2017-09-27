package org.abo.preesm.plugin.dataparallel.operations.visitor

import java.util.List
import java.util.logging.Level
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGComputationBug
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor
import org.ietr.dftools.algorithm.model.visitors.SDF4JException
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DirectedSubgraph
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor

/**
 * Isolate strongly connected components of the original
 * {@link SDFGraph}. 
 * 
 * @author Sudeep Kanur
 */
class DataParallelCheckOperations implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {
	
	/**
	 * Strongly connected subgraphs isolated from the original SDF. The subgraph is gauranteed
	 * to contain atleast one loop/cycle/strongly connected component
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<DirectedSubgraph<SDFAbstractVertex, SDFEdge> > isolatedStronglyConnectedComponents
	
	/**
	 * Output single rate graph. 
	 * If the graph is instance independent, then this graph is guaranteed to be data-parallel, as
	 * it is rearranged according to DASIP 2017 paper "Detection of Data-Parallelism in SDFG"
	 * Otherwise, it contains original input graph.
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	var SDFGraph cyclicGraph
	
	/**
	 * Retiming info. Information required for scheduling and code generation stages.
	 * WARNING! This is not stable yet
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	var RetimingInfo info
	
	/**
	 * True if the @{link SDFGraph} is data-parallel as well
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isDataParallel
	
	/**
	 * True if @{link SDFGraph} is instance independent
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isInstanceIndependent
	
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val Logger logger
		
	/**
	 * Constructor
	 * 
	 * @param logger A Workflow logger for logging purposes
	 */
	new(Logger logger) {
		isolatedStronglyConnectedComponents = newArrayList
		this.logger = logger
		this.info = null
	}
	
	/**
	 * Constructor for testing purposes.
	 */
	new() {
		this(null)
	}
	
	/**
	 * Conditional logging
	 * @param level Level of the message
	 * @param message String message
	 */
	def void log(Level level, String message) {
		if(this.logger !== null) {
			logger.log(level, message)
		}
	}
	
	override visit(SDFGraph sdf) throws SDF4JException {
		if(!sdf.isSchedulable) {
			throw new SDF4JException("Graph " + sdf + " not schedulable")
		}
		
		// Check if DAG is flattened
		for(vertex: sdf.vertexSet) {
			if( (vertex.graphDescription !== null) && (vertex.graphDescription instanceof SDFGraph)) {
				throw new SDF4JException("The graph " + sdf.name + " must be flattened.")				
			}
		}
		
		val topLevelCycleDetector = new CycleDetector(sdf)
		
		if(!topLevelCycleDetector.detectCycles) {
			log(Level.INFO, "SDF is acyclic. Hence, independent and data-parallel")
			this.isDataParallel = true
			this.isInstanceIndependent = true
		}
		
		// Generate the mandatory single rate graph
		val srsdfVisitor = new ToHSDFVisitor
		sdf.accept(srsdfVisitor) 
		val srsdf = srsdfVisitor.output
		
		// Check if the graph is acyclic like
		val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector(logger)
		sdf.accept(acyclicLikeVisitor)
		
		if(acyclicLikeVisitor.isAcyclicLike) {
			log(Level.INFO, "SDF is acyclic-like. Hence, independent and data-parallel")
			this.isDataParallel = true
			this.isInstanceIndependent = true
		} else {
			// SDF has other kinds of loops. So it can never be data-parallel on its own
			this.isDataParallel = false
			
			// Arrays to collect dependency information from each strongly connected component of
			// each SDF subgraph
			val subgraphInstInd = newArrayList
			val subgraphDepActors = newArrayList
			
			// WIP
//			val info = new RetimingInfo(srsdf, newHashMap, newHashMap)
			
			// Process each unconnected SDF subgraphs at a time
			acyclicLikeVisitor.SDFSubgraphs.forEach[sdfSubgraph |
				// Get strongly connected components
				val strongCompDetector = new KosarajuStrongConnectivityInspector(sdfSubgraph)
			
				// Collect strongly connected component that has loops in it
				// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
				strongCompDetector.getStronglyConnectedComponents.forEach[ subgraph |
					val cycleDetector = new CycleDetector(subgraph as 
						DirectedSubgraph<SDFAbstractVertex, SDFEdge>
					) 
					if(cycleDetector.detectCycles) {
						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
						// one loop
						isolatedStronglyConnectedComponents.add(subgraph as 
							DirectedSubgraph<SDFAbstractVertex, SDFEdge>
						)
					}
				]	
								
				// Perform DAG instance check on each strongly connected subgraph
				isolatedStronglyConnectedComponents.forEach[subgraph |
					
					val subgraphDAGGen = new SDF2DAG(subgraph)
					val depOps = new DependencyAnalysisOperations
					subgraphDAGGen.accept(depOps)
					subgraphInstInd.add(depOps.isIndependent)
					
					if(depOps.isIndependent) {
						// Rearrange the loops as the subgraph is instance independent
						// WIP
//						val movableInstanceVisitor = new MovableInstances
//						subgraphDAGGen.accept(movableInstanceVisitor)
//						val retimingVisitor = new RearrangeOperations(srsdf, info)
//						subgraphDAGGen.accept(retimingVisitor)
						
					} else {					
						if(!depOps.instanceDependentActors.empty) {
							subgraphDepActors.addAll(depOps.instanceDependentActors.toList)
						} else {
							throw new DAGComputationBug("SDFG has instance dependence. But dependent " +
								" actor set is empty!")
						}
					}
				]
			]			
			
			this.isInstanceIndependent = subgraphInstInd.forall[value | value == true]
			
			if(isInstanceIndependent) {
				log(Level.INFO, "SDF is instance-independent, but not data-parallel. Rearranging")
			} else {
				log(Level.INFO, "SDF is not instance-independent, therefore not data-parallel.")
				log(Level.INFO, "Actors with instance dependency are: " + subgraphDepActors)
			}
		}
		this.cyclicGraph = srsdf
//		this.info = info // WIP
	}
	
	override visit(SDFEdge sdfEdge) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override visit(SDFAbstractVertex sdfVertex) throws SDF4JException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
}