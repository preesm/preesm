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
package fi.abo.preesm.dataparallel.iterator

import fi.abo.preesm.dataparallel.PureDAGConstructor
import java.util.Collections
import java.util.List
import java.util.Map
import java.util.logging.Logger
import org.jgrapht.traverse.TopologicalOrderIterator
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.SDFEdge

/**
 * A topological order iterator specialised to traverse a SDFGraph
 * In addition, also provides a lookup table of instances and its
 * sources.
 *
 * @author Sudeep Kanur
 */
class DAGTopologicalIterator extends TopologicalOrderIterator<SDFAbstractVertex, SDFEdge> implements DAGTopologicalIteratorInterface {

	protected val Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources

	/**
	 * Constructor used for plugin
	 *
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param logger A Workflow logger instance
	 */
	new(PureDAGConstructor dagGen, Logger logger) {
		super(dagGen.outputGraph)

		instanceSources = newLinkedHashMap
		val inputGraph = dagGen.outputGraph

		// Iterate to get the nodes seen in the DAG
		new TopologicalOrderIterator<SDFAbstractVertex, SDFEdge>(inputGraph)
		.forEach[seenNode |
			instanceSources.put(seenNode, newArrayList)
		]

		// Now find the predecessor/source of relevant instances
		instanceSources.forEach[node, sources |
			sources.addAll(inputGraph.incomingEdgesOf(node)
				.map[edge | edge.source]
				.filter[source | instanceSources.keySet.contains(source)]
				.toList)
		]
	}

	/**
	 * Constructor for testing purposes
	 *
	 * @param A {@link PureDAGConstructor} instance
	 */
	new(PureDAGConstructor dagGen) {
		this(dagGen, null)
	}

	/**
	 * Get a look up table of instances and its associated sources
	 *
	 * @return Unmodifiable map of instances and a list of its sources
	 */
	override getInstanceSources() {
		return Collections.unmodifiableMap(instanceSources)
	}

}
