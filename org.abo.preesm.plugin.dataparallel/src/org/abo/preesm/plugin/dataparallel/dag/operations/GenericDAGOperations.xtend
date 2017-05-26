package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * Implementation that does not concern it self if the DAG is a subset or
 * an entirety
 */
class GenericDAGOperations implements DAGOperations {
	
	val Logger logger
	
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val SDFGraph inputGraph
	
	@Accessors(PROTECTED_GETTER, PRIVATE_SETTER)
	val DAGConstructor dagGen
	
	/**
	 * Constructor
	 * @param dagGen DAGconstructor instance
	 * @param logger Logger for logging purposes
	 */
	new(DAGConstructor dagGen, Logger logger){
		this.logger = logger
		this.inputGraph = dagGen.getOutputGraph
		this.dagGen = dagGen
	}
	
	/**
	 * Constructor
	 * @param dagGen DAGConstructor object
	 */
	new(DAGConstructor dagGen) {
		this(dagGen, null)
	}
	
	protected def void log(String message) {
		logger?.log(Level.INFO, message)
	}
	
	override getRootInstances() {
		return inputGraph.vertexSet.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0].toList
	}
	
	override getRootActors() {
		return getRootInstances().map[instance | dagGen.instance2Actor.get(instance)].toSet.toList
	}
	
	override getExitInstances() {
		val rootInstances = getRootInstances()
		return inputGraph.vertexSet.filter[instance |
			 inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)
		].toList
	}
	
	override Map<SDFAbstractVertex, Integer> getAllLevels() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't compute all levels for a generic graph.\n The graph must be "
													+ "constructed using specific DAGConstructor instance")
	}
	
	override getLevelSets() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't compute level sets for a generic DAG graph.\n The graph must be "
													+ "constructed using specific DAGConstructor instance")
	}
	
	override getMaxLevel() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't compute level sets for a generic DAG graph.\n The graph must be "
													+ "constructed using specific DAGConstructor instance")
	}
	
}