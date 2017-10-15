package org.abo.preesm.plugin.dataparallel.operations

import java.util.Map
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * Helper class to properly initialize and use {@link OperationsUtils#getParallelLevel}.
 * <p>
 * In order to properly assign appropriate levels, we use builder pattern.
 * 
 * @author Sudeep Kanur
 */
class GetParallelLevelBuilder {
	
	/**
	 * Get the maximum parallel level of the nodes that belong to this
	 * levels
	 */
	private var Map<SDFAbstractVertex, Integer> subsetLevels
	
	/**
	 * The superset of {@link GetParallelLevelBuilder#subsetLevels}
	 */
	private var Map<SDFAbstractVertex, Integer> origLevels
	
	/**
	 * Both of the level sets belong to this DAG constructor
	 */
	private var PureDAGConstructor dagGen
	
	new() {
		this.subsetLevels = null
		this.origLevels = null
		this.dagGen = null
	}
	
	/**
	 * Add levels that forms the superset of "subset levels"
	 * 
	 * @param origLevels Levels that form the superset of the "subset levels"
	 * @return Instance of this builder
	 */
	public def GetParallelLevelBuilder addOrigLevels(Map<SDFAbstractVertex, Integer> origLevels) {
		this.origLevels = origLevels
		return this
	}
	
	/**
	 * Add "subset levels". The maximum parallel level is found of the nodes that belong to this level
	 * 
	 * @param subsetLevels The maximum parallel level is found for instances that belong to this level
	 * @return Instance of this builder
	 */
	public def GetParallelLevelBuilder addSubsetLevels(Map<SDFAbstractVertex, Integer> subsetLevels) {
		this.subsetLevels = subsetLevels
		return this
	}
	
	/**
	 * Add a {@link PureDAGConstructor} instance
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @return Instance of this builder
	 */
	public def GetParallelLevelBuilder addDagGen(PureDAGConstructor dagGen) {
		this.dagGen = dagGen
		return this
	}
	
	/**
	 * Get the maximum parallel level
	 * 
	 * @return Maximum parallel level
	 * @throws SDF4JException if the builder is not properly initialized
	 */
	public def Integer build() {
		if(subsetLevels === null) {
			throw new SDF4JException("Use addSubsetLevels method to initialize levels for which " +
				"maximum parallel level needs to be found")
		}
		
		if(origLevels === null) {
			throw new SDF4JException("Use addOrigLevels method to initialize levels that " +
				"forms the superset of subsetLevels")
		}
		
		if(dagGen === null) {
			throw new SDF4JException("Initialize the DAGConstructor instance using addDagGen method")
		}
		
		return OperationsUtils.getParallelLevel(dagGen, origLevels, subsetLevels)
	}
}