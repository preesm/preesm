package org.abo.preesm.plugin.dataparallel

import org.abo.preesm.plugin.dataparallel.DAGConstructor
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import java.util.Map
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import java.util.logging.Level

public abstract class AbstractDAGConstructor implements DAGConstructor {
	
	/**
	 * Logger to optionally log messages for debugging purposes
	 */
	protected val Logger logger
	
	/**
	 * Map of actor and all of its instances (including implode/explode)
	 */
	@Accessors(NONE)
	protected val Map<SDFAbstractVertex, List<SDFAbstractVertex>> actor2Instances
	
	/**
	 * Map of instance and its corresponding actor
	 */
	@Accessors(NONE)
	protected val Map<SDFAbstractVertex, SDFAbstractVertex> instance2Actor
	
	/**
	 * Holds constructed DAG
	 */
	@Accessors(NONE)
	protected var SDFGraph outputGraph
	
	@Accessors(NONE)
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
	
	public override void log(String message) {
		logger?.log(Level.INFO, message)
	}
	
	public override Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActor2Instances() {
		return actor2Instances
	}
	
	public override Map<SDFAbstractVertex, SDFAbstractVertex> getInstance2Actor() {
		return instance2Actor
	}
	
	public override SDFGraph getOutputGraph() {
		return outputGraph
	}
	
	public override Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances() {
		return exImOrigInstance
	}
}