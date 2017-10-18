/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fi.abo.preesm.dataparallel.operations

import java.util.Map
import fi.abo.preesm.dataparallel.PureDAGConstructor
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
