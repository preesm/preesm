package org.abo.preesm.plugin.dataparallel

import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGOperations
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import java.util.Map

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
	 * Return the map of actor from original SDFG to all its immediate
	 * predecessor
	 * 
	 * @return Map of actor to list of immediate predecessor in original SDFG
	 */
	public def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getActorPredecessor()
	
	/**
	 * Return a list of all the actors of the original SDFG that form a part of
	 * a cycle
	 * 
	 * @return List of all actors of all the cycles in the SDFG
	 */
	public def List<SDFAbstractVertex> getCycleActors()
	
	/**
	 * Method for operation visitor
	 */
	public def void accept(DAGOperations visitor)
}