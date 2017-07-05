package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.List
import java.util.Map
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * All supported operations that can be performed on pure DAGS only
 * constructed using SDF2DAG
 * 
 * @author Sudeep Kanur
 */
public interface DAGOperations extends DAGCommonOperationsImplAssistant {
	
	/**
	 * Get maximum depth/level at which all instances of an actor are contained
	 * in the same level
	 * 
	 * @return If DAG has a parallel level, then it returns the maximum level. Otherwise it returns -1
	 */
	public def int getParallelLevel()
	
	/**
	 * Get maximum depth/level at which all instances of an actor are contained for a given levels
	 * 
	 * @param Given levels
	 * Rest are same as {@link DAGOperations#getParallelLevel}
	 */
	public def int getParallelLevel(Map<SDFAbstractVertex, Integer> levels)
	
	/**
	 * Returns a list of root instances that form a part of cycles
	 * Each list is a separate cycle and contains root instances from the
	 * DAG that is affected by the cycle under consideration
	 * 
	 * @return List of lists each containing instances from cycles
	 * @throws SDF4JException The DAG is not instance independent
	 */
	public def List<List<SDFAbstractVertex>> getCycleRoots() throws SDF4JException
	
	/**
	 * Rearrange the level sets of DAG such that the DAG is 
	 * data parallel as well. The function checks if DAG is
	 * instance independent and returns the level set
	 * after rearraning (if needed)
	 * 
	 * @return list of lists of instances seen at the level given by the index of the outer list
	 * @throws SDF4JException if the DAG is not instance independent
	 */
	public def List<List<SDFAbstractVertex>> rearrange() throws SDF4JException
	
	/**
	 * Get the DAG that forms the loop schedule
	 * 
	 * @returns SDFGraph The DAG that forms the loop schedule
	 * @throws SDF4JException if the DAG is not instance independent
	 */
	public def SDFGraph getDAGC() throws SDF4JException
	
	/**
	 * Get the DAG that forms the transient part
	 * If the DAG is acyclic-like or is instance dependent,
	 * then it returns null
	 * 
	 * @return SDFGraph Transient DAG, Null if DAG is acyclic-like
	 * @throws SDF4JException If the DAG is not instance independent
	 */
	public def SDFGraph getDAGT() throws SDF4JException
}