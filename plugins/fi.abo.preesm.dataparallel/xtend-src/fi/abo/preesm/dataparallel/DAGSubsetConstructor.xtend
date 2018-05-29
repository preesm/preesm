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
package fi.abo.preesm.dataparallel

import fi.abo.preesm.dataparallel.DAGConstructor
import java.util.List
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import fi.abo.preesm.dataparallel.iterator.SubsetTopologicalIterator
import fi.abo.preesm.dataparallel.operations.DAGCommonOperations

/**
 * Interface for constructing a subset of a DAG constructor
 * A subset DAG (and its corresponding constructor) is the part of
 * the original {@link PureDAGConstructor} instance that is formed by
 * traversing a given root instance from the root instance set of the
 * original {@link PureDAGConstructor}. {@link SubsetTopologicalIterator} is
 * commonly used to construct the subset.
 * <p>
 * The implementations do not return the constructed DAG, but only
 * update the associated data-structures.
 * 
 * @author Sudeep Kanur
 */
interface DAGSubsetConstructor extends DAGConstructor {
	/**
	 * Get the seen nodes in the subset
	 * 
	 * @return Unmodifiable List of nodes that are seen in the subset
	 */
	def List<SDFAbstractVertex> getSeenNodes()
	
	/**
	 * Get the original DAG for the subset
	 * 
	 * @return A {@link PureDAGConstructor} instance that was used to create subset
	 */
	def PureDAGConstructor getOriginalDAG()
	
	/**
	 * Method for operation visitor
	 */
	def void accept(DAGCommonOperations visitor)
}
