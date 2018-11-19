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

import fi.abo.preesm.dataparallel.iterator.SubsetTopologicalIterator
import fi.abo.preesm.dataparallel.operations.DAGCommonOperations
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import java.util.logging.Logger
import javax.naming.OperationNotSupportedException
import org.eclipse.xtend.lib.annotations.Accessors
import org.jgrapht.alg.cycle.CycleDetector
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.SDFEdge
import org.preesm.algorithm.model.sdf.SDFGraph
import org.preesm.algorithm.model.visitors.SDF4JException

/**
 * A subset of DAG is the set of all the instances that has a reachable path
 * from the specified root instances to all the reachable exit nodes
 * The class does not create a new DAG, but only filters the current ones to
 * give relevant actor to instances table, explode/implode to relevant instance table
 * and instance to actor table.
 *
 * @autor Sudeep Kanur
 */
final class DAGSubset extends AbstractDAGConstructor implements DAGSubsetConstructor {
	/**
	 * Holds the original DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val SDFGraph inputGraph

	/**
	 * Holds the root node
	 */
	val SDFAbstractVertex rootNode

	/**
	 * Holds the original DAG constructor
	 */
	val PureDAGConstructor dagGen

	/**
	 * List of nodes that are seen in the current subset of DAG
	 */
	val List<SDFAbstractVertex> seenNodes

	/**
	 * List of instances of source actors
	 */
	val List<SDFAbstractVertex> sourceInstances

	/**
	 * List of instances of sink actors
	 */
	val List<SDFAbstractVertex> sinkInstances

	/**
	 * Constructor
	 *
	 * @param dagGen {@link SDF2DAG} instance containing original DAG
	 * @param rootNode A root node that is used to construct subset of DAG
	 * @param logger Logger instance to log debug messages to output
	 * @throws SDF4JException If the input graph is not valid
	 */
	new(PureDAGConstructor dagGen, SDFAbstractVertex rootNode, Logger logger) throws SDF4JException {
		super(logger)

		// Check if input is valid
		try{
			this.inputGraph = dagGen.getOutputGraph
		} catch(OperationNotSupportedException o) {
			throw new SDF4JException("The DAG should be original DAG, not an instance of DAGSubset")
		}

		this.rootNode = rootNode
		this.dagGen = dagGen
		checkInputIsValid()

		// Create subset of the new root node
		this.seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).toList

		sinkInstances = newArrayList
		sourceInstances = newArrayList

		sourceActors.forEach[actor |
			sourceInstances.addAll(dagGen.actor2Instances.get(actor).filter[instance | seenNodes.contains(instance)].toList)
		]

		sinkActors.forEach[actor |
			sinkInstances.addAll(dagGen.actor2Instances.get(actor).filter[instance | seenNodes.contains(instance)].toList)
		]

		sourceActors.clear
		sinkActors.clear

		sourceInstances.forEach[instance |
			sourceActors.add(dagGen.instance2Actor.get(instance))
		]

		sinkInstances.forEach[instance |
			sinkActors.add(dagGen.instance2Actor.get(instance))
		]
	}

	/**
	 * Constructor
	 *
	 * @param dagGen {@link SDF2DAG} object containing original dag
	 * @param rootNode A root node that is used to construct subset of DAG
	 * @throws SDF4JException If the input graph is not valid
	 */
	new(PureDAGConstructor dagGen, SDFAbstractVertex rootNode) throws SDF4JException {
		this(dagGen, rootNode, null)
	}

	/**
	 * Filter the relevant instances and actors from the original map.
	 * Returns a filtered copy of original look up table.
	 *
	 * @return Look up table consisting of relevant actors and its instances
	 */
	override Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActor2Instances() {
		val actor2Instances = new LinkedHashMap(dagGen.getActor2Instances)
		for(actor: dagGen.actor2Instances.keySet) {
			val instances = actor2Instances.get(actor).filter[instance | seenNodes.contains(instance)].toList
			if(instances.isEmpty){
				actor2Instances.remove(actor)
			} else {
				actor2Instances.put(actor, instances)
			}
		}
		return actor2Instances
	}

	/**
	 * Filter the relevant instances and actors from the original map.
	 * Returns a copy of filtered instances.
	 *
	 * @return Lookup table consisting of instances and its relevant actors
	 */
	override Map<SDFAbstractVertex, SDFAbstractVertex> getInstance2Actor() {
		return new LinkedHashMap(dagGen.instance2Actor.filter[key, value | seenNodes.contains(key)])
	}

	/**
	 * Filter the relevant instances and implode/explode from the original map.
	 * Returns a copy of filtered implode/explode and its instances.
	 *
	 * @return Lookup table consisting of implode/explode instances belonging to the DAG and its instances
	 */
	override Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances() {
		return new LinkedHashMap(dagGen.explodeImplodeOrigInstances.filter[key, value | seenNodes.contains(value)])
	}

	/**
	 * Check whether the input is valid. Following checks are made.
	 * Cycles, delays and repetition vector, root node exist and is valid root node.
	 *
	 * @return true if input is valid, or exception is thrown (false is never returned)
	 * @throws SDF4JException if the input graph is not a valid DAG or if root node does not exist
	 */
	def boolean checkInputIsValid() throws SDF4JException {
		// Check if there are cycles
		val cycleDetector = new CycleDetector<SDFAbstractVertex, SDFEdge>(inputGraph)
		if(cycleDetector.detectCycles) {
			throw new SDF4JException("Cycles found in DAG. DAG can't have cycles!")
		}

		// Check if there are any delays
		for(edge: inputGraph.edgeSet) {
			if(edge.delay.longValue != 0) {
				throw new SDF4JException("Delay of " + edge.delay.longValue + " found at edge between "
					+ edge.source.name + " and " + edge.target.name + ". DAG must have no delays!" )
			}
		}

		// Check that repetition vector is exactly one for all the vertices
		for(node: inputGraph.vertexSet) {
			if(node.nbRepeatAsLong != 1) {
				throw new SDF4JException("Node " + node.name + " has repetition count of " + node.nbRepeatAsLong
					+ ". DAG must have all repetition counts as one.");
			}
		}

		// Input graph can't be hierarchical as its already arriving from DAGConstructor

		if(!inputGraph.vertexSet.contains(rootNode)) {
			throw new SDF4JException("Root node " + rootNode.name + " does not exist in the DAG!")
		}

		val rootInstances = inputGraph.vertexSet
			.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0].toList

		if(!rootInstances.contains(rootNode)) {
			throw new SDF4JException("Node " + rootNode.name + " is not a root node of the graph")
		}

		return true
	}

	/**
	 * {@link DAGConstructor#getSourceInstances}
	 */
	override getSourceInstances() {
		return sourceInstances
	}

	/**
	 * {@link DAGConstructor#getSinkInstances}
	 */
	override getSinkInstances() {
		return sinkInstances
	}

	/**
	 * Accept method for DAG operations
	 *
	 * @param A {@link DAGCommonOperations} instance
	 */
	override accept(DAGCommonOperations visitor) {
		visitor.visit(this)
	}

	/**
	 * Get the seen nodes in the subset
	 *
	 * @return Unmodifiable List of nodes that are seen in the subset
	 */
	override List<SDFAbstractVertex> getSeenNodes() {
		return seenNodes
	}

	/**
	 * Get the original DAG for the subset
	 *
	 * @return A {@link PureDAGConstructor} instance that was used to create subset
	 */
	override PureDAGConstructor getOriginalDAG() {
		return dagGen
	}

}
