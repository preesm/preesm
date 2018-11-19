/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2018),
 * IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Sudeep Kanur <skanur@abo.fi> (2017 - 2018)
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

import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.DAGConstructor
import fi.abo.preesm.dataparallel.DAGSubset
import fi.abo.preesm.dataparallel.PureDAGConstructor
import fi.abo.preesm.dataparallel.SDF2DAG
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import org.preesm.algorithm.model.sdf.SDFAbstractVertex

/**
 * Calculate root (entry) and exit nodes of a DAG
 * 
 * @author Sudeep Kanur
 */
class RootExitOperations implements DAGCommonOperations {
	
	/**
	 * Root (entry) nodes of the DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> rootInstances
	
	/**
	 * Actors corresponding to root (entry) nodes of the DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> rootActors
	
	/**
	 * Exit nodes of the DAG
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val List<SDFAbstractVertex> exitInstances
	
	new() {
		rootInstances = newArrayList
		rootActors = newArrayList
		exitInstances = newArrayList
	} 
	
	/**
	 * Common function to compute root instances, exit instance and root actors
	 * for DAG constructors of type PureDAGConstructor
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance 
	 */	
	protected def void compute(PureDAGConstructor dagGen) {
		// Compute root Instances
		val inputGraph = dagGen.outputGraph
		rootInstances.addAll(inputGraph.vertexSet.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0])
		
		// Compute Root Actors
		computeRootActors(dagGen)
		
		// Compute Exit instances
		exitInstances.addAll(
			inputGraph.vertexSet
				.filter[instance | inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)]
				.toList
		)
	}
	
	protected def void computeRootActors(DAGConstructor dagGen) {
		rootActors.addAll(rootInstances.map[instance |
				dagGen.instance2Actor.get(instance)
			].toSet.toList)
	}
	
	/**
	 * Compute root and exit nodes of the DAG
	 * 
	 * @pure {@link SDF2DAG} instance
	 */
	override visit(SDF2DAG dagGen) {
		compute(dagGen)
	}
	
	/**
	 * In addition to finding instances that have no roots, this
	 * function also filters the set based on the instance present in the
	 * subset
	 * 
	 * @param A {@link DAGSubset} instance
	 */
	override visit(DAGSubset dagGen) {
		val seenNodes = dagGen.seenNodes
		val inputGraph = dagGen.originalDAG.outputGraph
		
		// Compute root instances
		rootInstances.addAll(
			inputGraph.vertexSet
				.filter[instance | seenNodes.contains(instance)]
				.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0]
				.toList
		)
		
		// Compute root actors
		computeRootActors(dagGen)
		
		// Compute exit instances
		exitInstances.addAll(
			inputGraph.vertexSet
				.filter[instance | seenNodes.contains(instance)]
				.filter[instance | inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)]
				.toList
		)
	}
	
	/**
	 * Compute root (entry) and exit nodes of the DAG
	 * 
	 * @param A {@link DAG2DAG} instance
	 */
	override visit(DAG2DAG dag) {
		compute(dag)
	}
	
}
