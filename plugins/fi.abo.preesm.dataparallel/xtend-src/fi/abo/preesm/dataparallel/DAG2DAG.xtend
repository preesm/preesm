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
package fi.abo.preesm.dataparallel

import fi.abo.preesm.dataparallel.operations.DAGOperations
import java.util.ArrayList
import java.util.List
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.jgrapht.graph.AbstractGraph
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.SDFEdge
import org.preesm.algorithm.model.sdf.SDFGraph
import org.preesm.algorithm.model.sdf.transformations.SpecialActorPortsIndexer

/**
 * Class that creates re-populates all necessary data-structures for a new
 * {@link PureDAGConstructor} that is constructed from an old {@link PureDAGConstructor}
 * 
 * @author Sudeep Kanur
 */
class DAG2DAG extends AbstractDAGConstructor implements PureDAGConstructor {
	/**
	 * Holds the input {@link SDFGraph} instance
	 */
	protected val SDFGraph newGraph
	
	/**
	 * Holds the cloned version of original input {@link SDFGraph}
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val AbstractGraph<SDFAbstractVertex, SDFEdge> inputGraph
	
	/**
	 * List of all the actors that form the part of the cycles in the original SDFG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> cycleActors
	
	/**
	 * Constructor to use in plugins
	 * 
	 * @param oldDAGGen Old {@link PureDAGConstructor} instance that has necessary data-structures
	 * @param logger A {@link Logger} instance for workflow logger
	 */
	new(PureDAGConstructor oldDAGGen, Logger logger) {
		super(logger)
		newGraph = new SDFGraph()
		val dag = oldDAGGen.outputGraph
		
		inputGraph = oldDAGGen.inputGraph
		
		// Copy the old SDFGraph
		
		// Copy the vertex set
		dag.vertexSet.forEach[vertex |
			newGraph.addVertex(vertex)
		]
		
		// Copy the edge set
		dag.edgeSet.forEach[edge |
			val newEdge = newGraph.addEdge(edge.source, edge.target)
			
			// Set the appropriate sinks
			edge.source.sinks.forEach[sink |
				if( (edge.targetInterface !== null) && edge.targetInterface.name.equals(sink.name)) {
					edge.source.setInterfaceVertexExternalLink(newEdge, sink)
				}
			]	
			
			// Set the appropriate sources
			edge.target.sources.forEach[source |
				if( (edge.sourceInterface !== null) && edge.sourceInterface.name.equals(source.name)) {
					edge.target.setInterfaceVertexExternalLink(newEdge, source)
				}
			]
			newEdge.copyProperties(edge)
		] 
		
		// Make sure the ports of special actors are ordered according to their indices
		SpecialActorPortsIndexer.sortIndexedPorts(newGraph)
		newGraph.copyProperties(dag)
		newGraph.propertyBean.setValue("topology", null)
		newGraph.propertyBean.setValue("vrb", null)
		
		// Override calculation of source and sink actors
		sourceActors.clear()
		sourceActors.addAll(oldDAGGen.sourceActors)
		
		sinkActors.clear()
		sinkActors.addAll(oldDAGGen.sinkActors)
		
		// Override calculation of actor and instances maps
		actor2Instances.clear()
		actor2Instances.putAll(oldDAGGen.actor2Instances)
		
		instance2Actor.clear()
		instance2Actor.putAll(oldDAGGen.instance2Actor)
		
		explodeImplodeOrigInstances.clear()
		explodeImplodeOrigInstances.putAll(oldDAGGen.explodeImplodeOrigInstances)
		
		actorPredecessor.clear()
		actorPredecessor.putAll(oldDAGGen.actorPredecessor)
		
		this.cycleActors = new ArrayList(oldDAGGen.cycleActors)
	}
	
	/**
	 * Constructor to use in test-setups
	 * 
	 * @param oldDAGGen Old {@link PureDAGConstructor} instance that has necessary data-structures
	 */
	new(PureDAGConstructor oldDAGGen) {
		this(oldDAGGen, null)
	}
	
	/**
	 * Passes the newly copied graph from the old SDF graph
	 * 
	 * @return The copy {@link SDFGraph} instance that was passed to the constructor
	 */	
	override getOutputGraph() {
		return newGraph
	}
	
	/**
	 * Accept method for DAG operations
	 * 
	 * @param A {@link DAGOperations} instance
	 */
	override accept(DAGOperations visitor) {
		visitor.visit(this)
	}
}
