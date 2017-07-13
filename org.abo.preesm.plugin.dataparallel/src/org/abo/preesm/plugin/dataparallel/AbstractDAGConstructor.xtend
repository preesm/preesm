package org.abo.preesm.plugin.dataparallel

import java.util.List
import java.util.Map
import java.util.logging.Level
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * Implement common getter methods and instance variables for {@link DAGConstructor} class
 * 
 * @author Sudeep Kanur
 */
public abstract class AbstractDAGConstructor implements DAGConstructor {
	
	/**
	 * Logger to optionally log messages for debugging purposes
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Logger logger
	
	/**
	 * Map of actor and all of its instances (including implode/explode)
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Map<SDFAbstractVertex, List<SDFAbstractVertex>> actor2Instances
	
	/**
	 * Map of instance and its corresponding actor
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Map<SDFAbstractVertex, SDFAbstractVertex> instance2Actor
	
	/**
	 * List of source actors
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val List<SDFAbstractVertex> sourceActors
	
	/**
	 * List of sink actors
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val List<SDFAbstractVertex> sinkActors
	
	/**
	 * Map of implode/explode instances to its original instance
	 */
	@Accessors(PUBLIC_GETTER, PROTECTED_SETTER)
	val Map<SDFAbstractVertex, SDFAbstractVertex> explodeImplodeOrigInstances
	
	/**
	 * Map of actor from original input SDFG to all its immediate predecessors
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, List<SDFAbstractVertex>> actorPredecessor
	
	protected new(Logger logger){
		this.logger = logger
		this.actor2Instances = newHashMap
		this.instance2Actor = newHashMap
		this.explodeImplodeOrigInstances = newHashMap
		this.sourceActors = newArrayList
		this.sinkActors = newArrayList
		this.actorPredecessor = newHashMap
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
	 * {@link DAGConstructor#getSourceInstances}
	 */
	override List<SDFAbstractVertex> getSourceInstances() {
		val sourceInstances = newArrayList
		sourceActors.forEach[ actor |
			sourceInstances.addAll(actor2Instances.get(actor))
		]
		return sourceInstances
	}
	
	/**
	 * {@link DAGConstructor#getSinkInstances}
	 */
	override List<SDFAbstractVertex> getSinkInstances() {
		val sinkInstances = newArrayList
		sinkActors.forEach[actor|
			sinkInstances.addAll(actor2Instances.get(actor))
		]
		return sinkInstances
	}
}