package org.abo.preesm.plugin.dataparallel

import java.util.List
import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex

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
	 * List of source actors
	 */
	protected val List<SDFAbstractVertex> sourceActors
	
	/**
	 * List of sink actors
	 */
	protected val List<SDFAbstractVertex> sinkActors
	
	/**
	 * Map of implode/explode instances to its original instance
	 */
	protected val Map<SDFAbstractVertex, SDFAbstractVertex> exImOrigInstance
	
	protected new(Logger logger){
		this.logger = logger
		this.actor2Instances = newHashMap()
		this.instance2Actor = newHashMap()
		this.exImOrigInstance = newHashMap()
		this.sourceActors = newArrayList()
		this.sinkActors = newArrayList()
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
	 * {@link DAGConstructor#getExplodeImplodeOrigInstances}
	 */
	public override Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances() {
		return exImOrigInstance
	}
	
	/**
	 * {@link DAGConstructor#getSourceActors}
	 */
	override List<SDFAbstractVertex> getSourceActors() {
		return sourceActors
	}
	
	/**
	 * {@link DAGConstructor#getSinkActors}
	 */
	override List<SDFAbstractVertex> getSinkActors() {
		return sinkActors
	}
	
	/**
	 * {@link DAGConstructor#getSourceInstances}
	 */
	override List<SDFAbstractVertex> getSourceInstances() {
		val sourceInstances = newArrayList()
		sourceActors.forEach[ actor |
			sourceInstances.addAll(actor2Instances.get(actor))
		]
		return sourceInstances
	}
	
	/**
	 * {@link DAGConstructor#getSinkInstances}
	 */
	override List<SDFAbstractVertex> getSinkInstances() {
		val sinkInstances = newArrayList()
		sinkActors.forEach[actor|
			sinkInstances.addAll(actor2Instances.get(actor))
		]
		return sinkInstances
	}
}