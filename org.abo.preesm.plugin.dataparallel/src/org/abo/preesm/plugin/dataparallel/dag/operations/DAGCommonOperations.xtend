package org.abo.preesm.plugin.dataparallel.dag.operations

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import java.util.Map
import java.util.Collection
import java.util.Set

/**
 * Operations on DAG that can be performed on a pure DAG
 * and on a subset of the DAG i.e. classes that implement
 * DAGConstructor. To implement this class, implement
 * {@link DAGCommonOperationsImplAssistant} interface instead.
 * 
 * @author Sudeep Kanur
 */
public interface DAGCommonOperations {
	/**
	 * Get root instances of the DAG
	 * Root instances are those instances that have no incoming edges in the DAG
	 * 
	 * @return Unmodifiable List of root instances 
	 */
	public def List<SDFAbstractVertex> getRootInstances()
	
	/**
	 * Get root actors of the DAG
	 * 
	 * @return Unmodifiable List of actors of instances that form root nodes
	 */
	public def List<SDFAbstractVertex> getRootActors()
	
	/**
	 * Get exit instances of the DAG. 
	 * Exit instances are those instances of the DAG that have no outgoing edges 
	 * and are not root instances
	 * 
	 * @return Unmodifiable List of exit instances
	 */
	public def List<SDFAbstractVertex> getExitInstances()
	
	/**
	 * Get all the levels of the nodes as a lookup table. Considers implode and 
	 * explode instance of an actor to be at the same level of its respective
	 * instance 
	 * 
	 * @return Unmodifiable lookup table of levels of nodes
	 */
	public def Map<SDFAbstractVertex, Integer> getAllLevels()
	
	/**
	 * Get the maximum depth/level of the graph. 
	 * Does not generic DAGs. Need a specific instance
	 * 
	 * @return Maximum level of the graph
	 */
	public def int getMaxLevel()
	
	/**
	 * Get all the level sets for a given DAG. A level set is a set of all instances
	 * seen at a particular level. Instead of set, we represent them as a list for
	 * space saving reason. The index of the level set, in this way, represents the level
	 * at which the set exists. 
	 * 
	 * @return Unmodifiable list of lists of instances seen at the level given by the index of the outer list
	 */
	public def List<List<SDFAbstractVertex>> getLevelSets()
	
	/**
	 * Check if the DAG is instance independent. A DAG is instance independent if no
	 * instance of an actor depends on another instance of the same actor. 
	 * 
	 * @return True if DAG is instance independent
	 */
	public def boolean isDAGInd()
	
	/**
	 * Get a set of actors that are the cause for non-parallelism in the DAG
	 * The list contains actors only if isDAGInd() returns false
	 * 
	 * @return actors that have instances dependencies. Empty if there are no such 
	 * actors
	 */
	public def Set<SDFAbstractVertex> getNonParallelActors()
	
	/**
	 * Pick a random instance or actor from a given set
	 * 
	 * @param The collection from which the element needs to be picked
	 * @return Instance or actor that is randomly picked from a set 
	 */
	public def SDFAbstractVertex pickElement(Collection<SDFAbstractVertex> set)
	
	/**
	 * Check if the DAG is data-parallel. A data-parallel DAG has a
	 * level set where all instances of an actor are contained in
	 * the same set
	 * 
	 * @return True if DAG is data-parallel as well
	 */
	public def boolean isDAGParallel()
}