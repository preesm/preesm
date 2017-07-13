package org.abo.preesm.plugin.dataparallel.operations.visitor

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import java.util.List
import java.util.Map

@org.eclipse.xtend.lib.annotations.Data class StrictDAGCycles {
	/**
	 * Instances of this strict cycle that appear in the root set
	 */
	List<SDFAbstractVertex> roots
	
	/**
	 * Levels of the instances that belong only belong to this
	 * strict cycle
	 */
	Map<SDFAbstractVertex, Integer> levels
}