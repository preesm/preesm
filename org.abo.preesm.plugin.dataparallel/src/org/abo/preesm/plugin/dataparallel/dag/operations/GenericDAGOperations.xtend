package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.logging.Level
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * Implementation that does not concern it self if the DAG is a subset or
 * an entirety
 */
class GenericDAGOperations implements DAGOperations {
	
	val Logger logger
	
	val SDFGraph inputGraph
	
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
	
}