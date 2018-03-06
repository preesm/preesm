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
package fi.abo.preesm.dataparallel.test.util

import java.util.List
import java.util.NoSuchElementException
import fi.abo.preesm.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import fi.abo.preesm.dataparallel.iterator.SubsetTopologicalIterator

/**
 * Utility class that holds many useful static functions
 * used for unit testing
 * 
 * @author Sudeep Kanur
 */
class Util {
	/**
	 * Provide all example SDF graph
	 * 
	 * @return List of all example SDF graphs
	 */
	public static def List<SDFGraph> provideAllGraphs() {
		return newArrayList(
			ExampleGraphs.acyclicTwoActors
		,	ExampleGraphs.twoActorSelfLoop
		,	ExampleGraphs.twoActorLoop
		,	ExampleGraphs.semanticallyAcyclicCycle
		,	ExampleGraphs.strictlyCyclic
		,	ExampleGraphs.strictlyCyclicDual
		,	ExampleGraphs.strictlyCyclic2
		,	ExampleGraphs.mixedNetwork1
		,	ExampleGraphs.mixedNetwork2
		,	ExampleGraphs.nestedStrongGraph
		,	ExampleGraphs.costStrongComponent
		)
	}
	
	/**
	 * Provide all example SDF graphs with context
	 * The context contains information if the relevant graph should be 
	 * executed to check branch sets
	 * 
	 * @return List of all example SDF graphs
	 */
	public static def List<ExampleGraphContext> provideAllGraphsContext() {
		return newArrayList(
			// SDFG, isBranchSetCompatible, isInstanceIndependent, isAcyclic
			new ExampleGraphContext(ExampleGraphs.acyclicTwoActors, true, true, false)
		,	new ExampleGraphContext(ExampleGraphs.twoActorSelfLoop, true, false, false)
		,	new ExampleGraphContext(ExampleGraphs.twoActorLoop, true, false, false)
		,	new ExampleGraphContext(ExampleGraphs.semanticallyAcyclicCycle, true, true, true)
		,	new ExampleGraphContext(ExampleGraphs.strictlyCyclic, true, true, false)
		,	new ExampleGraphContext(ExampleGraphs.strictlyCyclicDual, true, true, false)
		,	new ExampleGraphContext(ExampleGraphs.strictlyCyclic2, true, true, false)
		,	new ExampleGraphContext(ExampleGraphs.mixedNetwork1, true, true, false)
		,	new ExampleGraphContext(ExampleGraphs.mixedNetwork2, true, false, false)
		,	new ExampleGraphContext(ExampleGraphs.nestedStrongGraph, true, true, true)
		,	new ExampleGraphContext(ExampleGraphs.costStrongComponent, false, false, false)
		)
	}
	
	/**
	 * Return a vertex that has the name supplied
	 * 
	 * @param graph Search the vertex in this graph
	 * @param vertexName Name of the vertex
	 * @returns the vertex of the name vertexName
	 * @throws NoSuchElementException If the vertex with the name is not found
	 */
	public static def SDFAbstractVertex getRootNode(SDFGraph graph, String vertexName) throws NoSuchElementException {
		val vertex = graph.vertexSet.filter[node | node.name.equals(vertexName)].toList
		if(vertex === null || vertex.size == 0) {
			throw new NoSuchElementException("No vertex named " + vertexName + " found in the graph!")
		}
		return vertex.get(0)
	}
	
	/**
	 * Return the array of names of nodes seen in the subset of DAG created by given root node
	 * 
	 * @param dagGen the dag constructor object holding the main DAG
	 * @param rootName the name of the root node
	 * @returns array of names of nodes seen in the subset of the DAG
	 */
	public static def String[] getNodes(SDF2DAG dagGen, String rootName) {
		val rootNode = Util.getRootNode(dagGen.outputGraph, rootName)
		return new SubsetTopologicalIterator(dagGen, rootNode).toList.map[node | node.name]
	}
}
