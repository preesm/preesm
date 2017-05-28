package org.abo.preesm.plugin.dataparallel

import java.util.List
import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * Implement common getter methods and instance variables for {@link DAGConstructor} class
 * 
 * @author Sudeep Kanur
 */
public abstract class AbstractDAGConstructor implements DAGConstructor {
	
	/**
	 * Logger to optionally log messages for debugging purposes
	 */
	protected val Logger logger
	
	/**
	 * Map of actor and all of its instances (including implode/explode)
	 */
	protected val Map<SDFAbstractVertex, List<SDFAbstractVertex>> actor2Instances
	
	/**
	 * Map of instance and its corresponding actor
	 */
	protected val Map<SDFAbstractVertex, SDFAbstractVertex> instance2Actor
	
	/**
	 * Holds constructed DAG
	 */
	protected var SDFGraph outputGraph
	
	/**
	 * Map of implode/explode instances to its original instance
	 */
	protected val Map<SDFAbstractVertex, SDFAbstractVertex> exImOrigInstance
	
	protected new(Logger logger){
		this.logger = logger
		this.actor2Instances = newHashMap()
		this.instance2Actor = newHashMap()
		this.outputGraph = new SDFGraph()
		this.exImOrigInstance = newHashMap()
	} 
	
	protected new() {
		this(null)
	}
	
	/**
	 * {@link DAGConstructor#log}
	 */
	public override void log(String message) {
		logger?.log(Level.INFO, message)
	}
	
	/**
	 * {@link DAGConstructor#getActor2Instances}
	 * @return Lookup table of actors and their corresponding instances
	 */
	public override Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActor2Instances() {
		return actor2Instances
	}
	
	/**
	 * {@link DAGConstructor#getInstance2Actor}
	 */
	public override Map<SDFAbstractVertex, SDFAbstractVertex> getInstance2Actor() {
		return instance2Actor
	}
	
	/**
	 * {@link DAGConstructor#getOutputGraph}
	 */
	public override SDFGraph getOutputGraph() {
		return outputGraph
	}
	
	/**
	 * {@link DAGConstructor#getExplodeImplodeOrigInstances}
	 */
	public override Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances() {
		return exImOrigInstance
	}
}