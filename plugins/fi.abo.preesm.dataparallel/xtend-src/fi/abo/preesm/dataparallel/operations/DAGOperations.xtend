package fi.abo.preesm.dataparallel.operations

import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.PureDAGConstructor

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