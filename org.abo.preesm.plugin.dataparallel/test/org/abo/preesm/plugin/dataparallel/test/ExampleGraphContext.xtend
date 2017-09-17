package org.abo.preesm.plugin.dataparallel.test

import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * Class that wraps context needed for each ExampleGraphs and is used by certain test
 * functions
 */
@org.eclipse.xtend.lib.annotations.Data class ExampleGraphContext {
	
	/**
	 * The example graph
	 */
	SDFGraph graph
	
	/**
	 * True if the graph is compatible with computing branch set using method outlined in
	 * DASIP 2017 paper.
	 */
	boolean isBranchSetCompatible
}