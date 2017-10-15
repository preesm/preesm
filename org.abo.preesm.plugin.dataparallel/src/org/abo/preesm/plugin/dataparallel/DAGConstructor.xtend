package org.abo.preesm.plugin.dataparallel

import java.util.List
import java.util.Map
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex

/**
 * Construct DAG from SDF or from another DAG
 * 
 * @author Sudeep Kanur
 */
interface DAGConstructor {
	
	/**
	 * Optionally return the logger
	 * 
	 * @return A {@link Logger} instance that was passed
	 */
	public def Logger getLogger()
	
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
	 * Get Source actors. 
	 * Source actors are defined as those actors in the original SDF 
	 * that have no inputs
	 * 
	 * @return List of source actors
	 */
	public def List<SDFAbstractVertex> getSourceActors()
	
	/**
	 * Get Sink actors. 
	 * Sink actors are defined as those actors in the original SDFG that have
	 * no outputs
	 * 
	 * @return List of sink actors
	 */
	public def List<SDFAbstractVertex> getSinkActors()
	
	/**
	 * Get instances of source actors
	 * 
	 * @return List of instances of source actors
	 */
	public def List<SDFAbstractVertex> getSourceInstances()
	
	/**
	 * Get instances of sink actors
	 * 
	 * @return List of instances of sink actors
	 */
	public def List<SDFAbstractVertex> getSinkInstances()
}