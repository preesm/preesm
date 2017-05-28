package org.abo.preesm.plugin.dataparallel

import java.util.Map
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * Construct DAG from SDF or from another DAG
 * 
 * @author Sudeep Kanur
 */
interface DAGConstructor {
	
	/**
	 * Optionally log message to the console
	 * 
	 * @param message Message to log
	 */
	public def void log(String message)
	
	/**
	 * Returns the instances associated with the actor. Includes implodes and explodes
	 * 
	 * @return Lookup table of actors and their associated instances
	 */
	public def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActor2Instances()
	
	/**
	 * Returns the actor associated with the instance. Includes implodes and explodes
	 * 
	 * @return Lookup table of instances and its associated actor
	 */
	public def Map<SDFAbstractVertex, SDFAbstractVertex> getInstance2Actor()
	
	/**
	 * Gets the original instance associated with an implode or explode. 
	 * The key is only implode and explode nodes
	 * 
	 * @return Lookup table of implode/explode and its associated instance
	 */
	public def Map<SDFAbstractVertex, SDFAbstractVertex> getExplodeImplodeOrigInstances()
	
	/**
	 * Return the DAG that is constructed
	 * Note that not all implementations need to return DAG. Some only modify
	 * its associated data structures, but return the original graph
	 * 
	 * @return DAG constructed
	 */
	public def SDFGraph getOutputGraph()
	
	/**
	 * Check whether the input is valid
	 * 
	 * @return true if input is valid
	 */
	public def boolean checkInputIsValid() throws SDF4JException
}