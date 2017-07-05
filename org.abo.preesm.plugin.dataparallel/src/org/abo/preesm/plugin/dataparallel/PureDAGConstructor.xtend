package org.abo.preesm.plugin.dataparallel

import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * Interface for construction of Pure DAGs only. 
 * In additions to the methods from DAGConstructor, classes implementing
 * this interface can also return the DAG
 * 
 * @author Sudeep Kanur
 */
interface PureDAGConstructor extends DAGConstructor {
	/**
	 * Return the DAG that is constructed
	 * 
	 * @return DAG constructed
	 */
	public def SDFGraph getOutputGraph() 
}