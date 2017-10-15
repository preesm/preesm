package org.abo.preesm.plugin.dataparallel

import org.abo.preesm.plugin.dataparallel.DAGConstructor
import java.util.List
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.abo.preesm.plugin.dataparallel.iterator.SubsetTopologicalIterator
import org.abo.preesm.plugin.dataparallel.operations.DAGCommonOperations

/**
 * Interface for constructing a subset of a DAG constructor
 * A subset DAG (and its corresponding constructor) is the part of
 * the original {@link PureDAGConstructor} instance that is formed by
 * traversing a given root instance from the root instance set of the
 * original {@link PureDAGConstructor}. {@link SubsetTopologicalIterator} is
 * commonly used to construct the subset.
 * <p>
 * The implementations do not return the constructed DAG, but only
 * update the associated data-structures.
 * 
 * @author Sudeep Kanur
 */
interface DAGSubsetConstructor extends DAGConstructor {
	/**
	 * Get the seen nodes in the subset
	 * 
	 * @return Unmodifiable List of nodes that are seen in the subset
	 */
	public def List<SDFAbstractVertex> getSeenNodes()
	
	/**
	 * Get the original DAG for the subset
	 * 
	 * @return A {@link PureDAGConstructor} instance that was used to create subset
	 */
	public def PureDAGConstructor getOriginalDAG()
	
	/**
	 * Method for operation visitor
	 */
	public def void accept(DAGCommonOperations visitor)
}