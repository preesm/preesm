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

import fi.abo.preesm.dataparallel.CannotRearrange
import fi.abo.preesm.dataparallel.DAGComputationBug
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.pojo.RetimingInfo
import java.util.List
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector
import org.jgrapht.alg.cycle.CycleDetector
import org.jgrapht.graph.AsSubgraph
import org.preesm.algorithm.model.IGraphVisitor
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.SDFEdge
import org.preesm.algorithm.model.sdf.SDFGraph
import org.preesm.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.preesm.commons.exceptions.PreesmException

/**
 * Isolate strongly connected components of the original
 * {@link SDFGraph}.
 *
 * @author Sudeep Kanur
 */
class DataParallelCheckOperations implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

	/**
	 * Strongly connected subgraphs isolated from the original SDF. The subgraph is guaranteed
	 * to contain at least one loop/cycle/strongly connected component
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<AsSubgraph<SDFAbstractVertex, SDFEdge> > isolatedStronglyConnectedComponents

	/**
	 * Output single rate graph.
	 * If the graph is instance independent, then this graph is guaranteed to be data-parallel, as
	 * it is rearranged according to DASIP 2017 paper "Detection of Data-Parallelism in SDFG".
	 * Otherwise, it contains original input graph.
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	var SDFGraph cyclicGraph

	/**
	 * {@link RetimingInfo} instance. Information required for scheduling and code generation stages.
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	var RetimingInfo info

	/**
	 * <code>true</code> if the @{link SDFGraph} is data-parallel as well
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isDataParallel

	/**
	 * <code>true</code> if @{link SDFGraph} is instance independent
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isInstanceIndependent

	/**
	 * <code>true</code> if the @{link SDFGraph} is acyclic-like
	 * This naturally means that it is data-parallel and instance independent
	 * Extra flag provided to denote the class
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var Boolean isAcyclicLike

	/**
	 * Optional Logging
	 */
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val Logger logger

	/**
	 * List of actor that have instance dependence. Empty if the DAG is instance independent
	 * Each sublist is a strongly connected component that exhibits instance dependency
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var List<List<SDFAbstractVertex>> dependentActors

	/**
	 * List of actor sets that were failed to rearrange. Empty if either DAG is entirely instance
	 * dependent or if all strongly connected components were rearranged successfully
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	var List<List<SDFAbstractVertex>> rearrangeFailedActors

	/**
	 * Constructor
	 *
	 * @param logger A Workflow logger for logging purposes
	 */
	new(Logger logger) {
		isolatedStronglyConnectedComponents = newArrayList
		isDataParallel = Boolean.FALSE
		isInstanceIndependent = Boolean.FALSE
		isAcyclicLike = Boolean.FALSE
		dependentActors = newArrayList
		rearrangeFailedActors = newArrayList
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

	/**
	 * Perform data-parallel check and re-timing transformation on the {@link SDFGraph} given by the
	 * user.
	 */
	override visit(SDFGraph sdf) throws PreesmException {
		if(!sdf.isSchedulable) {
			throw new PreesmException("Graph " + sdf + " not schedulable")
		}

		// Check if DAG is flattened
		for(vertex: sdf.vertexSet) {
			if( (vertex.graphDescription !== null) && (vertex.graphDescription instanceof SDFGraph)) {
				throw new PreesmException("The graph " + sdf.name + " must be flattened.")
			}
		}

		val topLevelCycleDetector = new CycleDetector(sdf)

		if(!topLevelCycleDetector.detectCycles) {
			log(Level.INFO, "SDF is acyclic. Hence, independent and data-parallel")
			isDataParallel = Boolean.TRUE
			isInstanceIndependent = Boolean.TRUE
			isAcyclicLike = Boolean.TRUE
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
			isDataParallel = Boolean.TRUE
			isInstanceIndependent = Boolean.TRUE
			isAcyclicLike = Boolean.TRUE
		} else {
			log(Level.FINE, "SDF is instance-independent, but not data-parallel. Attempting rearranging...")
			// SDF has other kinds of loops. So it can never be data-parallel on its own

			// Arrays to collect dependency information from each strongly connected component of
			// each SDF subgraph
			val subgraphDepActors = newArrayList

			val info = new RetimingInfo(newArrayList)

			// Get strongly connected components
			val strongCompDetector = new KosarajuStrongConnectivityInspector(sdf)

			// Collect strongly connected component that has loops in it
			// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
			strongCompDetector.getStronglyConnectedComponents.forEach[ subgraph |
				val cycleDetector = new CycleDetector(subgraph as
					AsSubgraph<SDFAbstractVertex, SDFEdge>
				)
				if(cycleDetector.detectCycles) {
					// ASSUMPTION: Strongly connected component of a directed graph contains atleast
					// one loop

					// We need not only strongly connected components, but also vertices that
					// connect to the rest of the graph. This is because, calculation of root
					// and exit vertices also depends if there are enough delay tokens at the
					// interface edges.
					val relevantVertices = newLinkedHashSet
					val relevantEdges = newLinkedHashSet

					sdf.vertexSet.forEach[vertex |
						if(subgraph.vertexSet.contains(vertex)) {
							sdf.incomingEdgesOf(vertex).forEach[edge |
								if(!subgraph.vertexSet.contains(edge.source)) {
									relevantVertices.add(edge.source)
									relevantEdges.add(edge)
								}
							]

							sdf.outgoingEdgesOf(vertex).forEach[edge |
								if(!subgraph.vertexSet.contains(edge.target)) {
									relevantVertices.add(edge.target)
									relevantEdges.add(edge)
								}
							]
						}
					]
					relevantVertices.addAll(subgraph.vertexSet)
					relevantEdges.addAll(subgraph.edgeSet)
					val subgraphInterfaceVertices = new AsSubgraph(sdf, relevantVertices, relevantEdges)

					isolatedStronglyConnectedComponents.add(subgraphInterfaceVertices)
				}
			]

			// Perform DAG instance check on each strongly connected subgraph
			isolatedStronglyConnectedComponents.forEach[subgraph |

				val subgraphDAGGen = new SDF2DAG(subgraph, logger)
				val depOps = new DependencyAnalysisOperations
				subgraphDAGGen.accept(depOps)

				if(depOps.isIndependent) {
					// Rearrange the loops as the subgraph is instance independent
					log(Level.FINE, "Rearranging " + subgraph.vertexSet)

					// Get source/interface vertices that are not part of this strongly connected
					// component
					val sc = new KosarajuStrongConnectivityInspector(subgraph)
					val neighInterfaceActors = sc.stronglyConnectedComponents.filter[sg |
						val cycleDetector = new CycleDetector(sg as
							AsSubgraph<SDFAbstractVertex, SDFEdge>
						)
						!cycleDetector.detectCycles
					].map[sg |
						sg.vertexSet
					].flatten
					.toList
					val sccActors = subgraph.vertexSet.filter[actor |
						!neighInterfaceActors.contains(actor)
					]

					val retimingVisitor = new RearrangeOperations(srsdf, info, neighInterfaceActors, logger)
					try {
						subgraphDAGGen.accept(retimingVisitor)
					} catch(CannotRearrange c) {
						rearrangeFailedActors.add(newArrayList(sccActors))
						log(Level.WARNING, "Could not rearrange the strongly connected component containing actors:\n"
							+ sccActors)
					}

				} else {
					if(!depOps.instanceDependentActors.empty) {
						subgraphDepActors.addAll(depOps.instanceDependentActors.toList)
					} else {
						throw new DAGComputationBug(subgraphDAGGen, srsdf, "SDFG has instance dependence. But dependent" +
							" actor set is empty!")
					}
				}
			]

			log(Level.INFO, "SDF has one or more strongly connected components.")
			if(subgraphDepActors.empty) {
				isInstanceIndependent = Boolean.TRUE
				log(Level.INFO, "SDF is also instance independent.")
				if(rearrangeFailedActors.empty) {
					isDataParallel = Boolean.TRUE
					log(Level.INFO, "Rearranging was successful. SDF is now data-parallel as well.")
				} else {
					isDataParallel = Boolean.FALSE
					var message = "However, following strongly connected components could not " +
						"be rearranged:\n"
					for(failedActorSet: rearrangeFailedActors) {
						message += failedActorSet.toString + "\n"
					}
					log(Level.INFO, message)
				}
			} else {
				isInstanceIndependent = Boolean.FALSE
				isDataParallel = Boolean.FALSE
				log(Level.INFO, "SDF is **not** instance independent. Instance dependency " +
					"occurs in:\n" + subgraphDepActors)
			}

			this.info = info
		}
		this.cyclicGraph = srsdf
	}

	override visit(SDFEdge sdfEdge) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

	override visit(SDFAbstractVertex sdfVertex) throws PreesmException {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}

}
