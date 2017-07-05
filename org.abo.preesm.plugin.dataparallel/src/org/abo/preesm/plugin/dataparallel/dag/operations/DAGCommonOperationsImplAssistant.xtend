package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.DAGTopologicalIteratorInterface
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex

/**
 * Package level interface for assisting implementation of some
 * common operations. This is to enforce certain contract for the implementers
 * of the DAGCommonOperations. The classes implementing DAGCommonOperations should
 * implement this interface instead
 */
interface DAGCommonOperationsImplAssistant extends DAGCommonOperations {
	
	/**
	 * Initialize the appropriate data-structures
	 * by calling appropriate computation functions in 
	 * required order
	 * 
	 * The method should be called in the constructor of final class so 
	 * that all the computations happens in required order
	 */
	def void initialize()
	
	/**
	 * Get seen nodes in the DAG
	 * 
	 * @return Nodes seen in the DAG
	 */
	def List<SDFAbstractVertex> getSeenNodes()
	
	/**
	 * Get the appropriate topological order iterator for the DAG
	 * The topological iterator for a subset of a DAG is different
	 * than the topological iterator of the pure DAG
	 * 
	 * @return Appropriate topological order iterator
	 */
	def DAGTopologicalIteratorInterface getIterator()
	
	/**
	 * Get fork and join (explode and implode) instances
	 * 
	 * @return Lookup table of fork-join instances mapped to its original instance
	 */
	def Map<SDFAbstractVertex, SDFAbstractVertex> getForkJoinOrigInstance()
	
	/**
	 * Get sources of instances
	 * 
	 * @return Lookup table of instances and a list of its sources
	 */
	def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getInstanceSources()
}