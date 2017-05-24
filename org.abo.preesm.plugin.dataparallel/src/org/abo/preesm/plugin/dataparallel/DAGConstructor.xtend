package org.abo.preesm.plugin.dataparallel

import java.util.Map
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

interface DAGConstructor {
	
	/**
	 * Optionally log message to the console
	 */
	public def void log(String message)
	
	/**
	 * Returns the instances associated with the actor. Includes implodes and explodes
	 */
	public def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActor2Instances()
	
	/**
	 * Returns the actor associated with the instance. Includes implodes and explodes
	 */
	public def Map<SDFAbstractVertex, SDFAbstractVertex> getInstance2Actor()
	
	/**
	 * Gets the original instance associated with an implode or explode. 
	 * The key is only implode and explode nodes
	 */
	public def Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances()
	
	/**
	 * Return the DAG that is constructed
	 */
	public def SDFGraph getOutputGraph()
}