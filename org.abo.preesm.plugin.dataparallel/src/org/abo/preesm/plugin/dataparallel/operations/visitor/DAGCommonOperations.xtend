package org.abo.preesm.plugin.dataparallel.operations.visitor

import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.DAGConstructor

/**
 * Visitor interface for common operations that can be performed
 * on all instances that implement {@link DAGConstructor}
 * 
 * @author Sudeep Kanur
 */
interface DAGCommonOperations extends DAGOperations {
	/**
	 * Visitor method for Subset DAGs
	 * 
	 * @param dag A {@link DAGSubset} instance
	 */
	def void visit(DAGSubset dagGen)
}