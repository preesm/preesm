package org.abo.preesm.plugin.dataparallel.dag.operations

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List

/**
 * All supported operations that can be performed on Graphs constructed by classes that
 * implement DAGConstructor interface
 * @author Sudeep
 */
interface DAGOperations {
	/**
	 * Get root instances of the DAG
	 * Root instances are those instances that have no incoming edges in the DAG
	 * @return List of root instances 
	 */
	public def List<SDFAbstractVertex> getRootInstances()
	
	/**
	 * Get root actors of the DAG
	 * @return List of actors of instances that form root nodes
	 */
	public def List<SDFAbstractVertex> getRootActors()
	
	/**
	 * Get exit instances of the DAG. 
	 * Exit instances are those instances of the DAG that have no outgoing edges 
	 * and are not root instances
	 * @return List of exit instances
	 */
	public def List<SDFAbstractVertex> getExitInstances()
}