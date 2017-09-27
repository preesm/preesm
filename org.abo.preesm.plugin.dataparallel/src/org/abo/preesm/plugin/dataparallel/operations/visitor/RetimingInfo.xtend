package org.abo.preesm.plugin.dataparallel.operations.visitor

import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import java.util.Map
import org.ietr.dftools.algorithm.model.sdf.SDFEdge

/**
 * Class that contains information required to perform retiming transformation
 * 
 * @author Sudeep Kanur
 */
@org.eclipse.xtend.lib.annotations.Data class RetimingInfo {
	/**
	 * Contains the retimed SrSDF graph. Used for reference
	 */
	SDFGraph graph
	
	/**
	 * Lookup table consisting of edges to its original delay values for reference
	 */
	Map<SDFEdge, Integer> originalDelayValues
	
	/**
	 * Lookup table of edge and its related vertices that transform its delay values
	 */
	Map<SDFEdge, Integer> transformingInstances
}