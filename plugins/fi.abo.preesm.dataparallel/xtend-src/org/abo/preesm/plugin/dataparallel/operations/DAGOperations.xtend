package org.abo.preesm.plugin.dataparallel.operations

import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor

/**
 * Visitor interface for operations carried out only on
 * {@link PureDAGConstructor} instances
 * 
 * @author Sudeep Kanur
 */
interface DAGOperations {
	
	/**
	 * Visitor method for DAGs constructed from SDFs
	 * 
	 * @param dag A {@link SDF2DAG} instance
	 */
	def void visit(SDF2DAG dagGen)
	
	/**
	 * Visitor method for DAGs constructed from other DAGs
	 * 
	 * @param dag A {@link DAG2DAG} instance
	 */
	def void visit(DAG2DAG dagGen)
}