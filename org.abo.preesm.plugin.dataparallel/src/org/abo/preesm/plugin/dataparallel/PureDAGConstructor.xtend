package org.abo.preesm.plugin.dataparallel

import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGOperations

/**
 * Interface for construction of Pure DAGs only. 
 * In additions to the methods from DAGConstructor, classes implementing
 * this interface can also return the constructed DAG
 * 
 * @author Sudeep Kanur
 */
interface PureDAGConstructor extends DAGConstructor {
	/**
	 * Return the DAG that is constructed
	 * The DAG is the loop schedule
	 * 
	 * @return DAG constructed
	 */
	public def SDFGraph getOutputGraph()
	
	/**
	 * Method for operation visitor
	 */
	public def void accept(DAGOperations visitor)
}