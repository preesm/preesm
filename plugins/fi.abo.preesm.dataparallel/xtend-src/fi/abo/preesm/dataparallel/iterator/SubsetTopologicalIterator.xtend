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

import java.util.Collections
import java.util.List
import java.util.Map
import java.util.NoSuchElementException
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.traverse.BreadthFirstIterator
import fi.abo.preesm.dataparallel.PureDAGConstructor

/**
 * A topological order iterator that traverses a subset of DAG
 * 
 * @author Sudeep Kanur 
 */
class SubsetTopologicalIterator extends BreadthFirstIterator<SDFAbstractVertex, SDFEdge> implements DAGTopologicalIteratorInterface {
	
	/**
	 * Instances and its associated sources
	 */
	private val Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	/**
	 * Instances encountered
	 */
	private val List<SDFAbstractVertex> instanceEncountered
	
	/**
	 * The original graph supplied by the client
	 */
	private val SDFGraph inputGraph
	
	/**
	 * Constructor. Mainly used in plugin
	 * 
	 * @param dagGen {@link PureDAGConstructor} instance containing original DAG
	 * @param rootNode Root node
	 * @param logger For loggin purposes
	 * @throws NoSuchElementException If root node does not exist or is not a root node
	 */
	new(PureDAGConstructor dagGen, SDFAbstractVertex rootNode, Logger logger) throws NoSuchElementException {
		super(dagGen.getOutputGraph, rootNode)
		this.inputGraph = dagGen.getOutputGraph
		this.instanceSources = newHashMap
		this.instanceEncountered = newArrayList
		
		val rootInstances = inputGraph.vertexSet
			.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0].toList
			
		if(!rootInstances.contains(rootNode)) {
			if(!dagGen.outputGraph.vertexSet.contains(rootNode)) {
				throw new NoSuchElementException("Node " + rootNode.name + " does not exist in the DAG!")
			} else {
				throw new NoSuchElementException("Node " + rootNode.name + " is not a root node!")
			}
		}
		
		// Iterate first to get the subset of DAG in question
		new BreadthFirstIterator<SDFAbstractVertex, SDFEdge>(inputGraph, rootNode)
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
	 * Constructor used in Test setup
	 * 
	 * @param dagGen {@link PureDAGConstructor} instance containing a original DAG
	 * @param rootNode Root node
	 * @throws NoSuchElementException If root node does not exist or is not a root node
	 */
	new(PureDAGConstructor dagGen, SDFAbstractVertex rootNode) throws NoSuchElementException {
		this(dagGen, rootNode, null)
	}
	
	/**
	 * Overrides {@link BreadthFirstIterator#encounterVertex}.
	 * Run encounterVertex only when all of the source nodes (that is seen in the 
	 * subset of the DAG) is encountered before.
	 */
	protected override encounterVertex(SDFAbstractVertex node, SDFEdge edge) {
		val sources = instanceSources.get(node)
		if(sources.isEmpty) {
			// the node is source node
			instanceEncountered.add(node)
			super.encounterVertex(node, edge)
		} else {
			// Check if all the nodes have been visited
			if(sources.filter[!instanceEncountered.contains(it)].size == 0){
				instanceEncountered.add(node)
				super.encounterVertex(node, edge)
			}
		}
	}
	
	/**
	 * Get a look up table of instances and its associated sources
	 * 
	 * @return Unmodifiable map of instances mapped to a list of its sources
	 */
	public override Map<SDFAbstractVertex, List<SDFAbstractVertex>> getInstanceSources() {
		return Collections.unmodifiableMap(instanceSources)
	}	
}
