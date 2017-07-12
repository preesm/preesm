package org.abo.preesm.plugin.dataparallel.iterator

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.jgrapht.traverse.GraphIterator
import java.util.List
import java.util.Map

/**
 * Interface that iterates through a DAG in topological 
 * order only. Specialization of GraphIterator for SDFGraphs
 * 
 * @author Sudeep Kanur
 */
public interface DAGTopologicalIteratorInterface extends GraphIterator<SDFAbstractVertex, SDFEdge> {
	/**
	 * Get a look up table of instances and its associated sources
	 * 
	 * @return Unmodifiable map of instances and a list of its sources
	 */
	public def Map<SDFAbstractVertex, List<SDFAbstractVertex>> getInstanceSources()
}
