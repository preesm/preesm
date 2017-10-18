package org.abo.preesm.plugin.dataparallel.test.util

import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * POJO that wraps context needed for each ExampleGraphs and is used by certain test functions
 * 
 * @author Sudeep Kanur
 */
@org.eclipse.xtend.lib.annotations.Data class ExampleGraphContext {
	
	/**
	 * Manually written example {@link SDFGraph} graph
	 */
	SDFGraph graph
	
	/**
	 * <code>true</code> if the graph is compatible with computing branch set using method outlined 
	 * in DASIP 2017 paper.
	 */
	boolean isBranchSetCompatible
	
	/**
	 * <code>true</code> if the graph is instance independent
	 */
	boolean isInstanceIndependent
	
	/**
	 * <code>true</code> if the graph is acyclic as well
	 */
	boolean isAcyclic
}