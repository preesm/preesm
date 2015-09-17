/*********************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.memory.distributed

import java.util.HashMap
import java.util.HashSet
import java.util.Map
import org.ietr.dftools.architecture.slam.ComponentInstance
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex

/**
 * This class contains all the code responsible for splitting a {@link 
 * MemoryExclusionGraph} into several graphs, each corresponding to a specific 
 * memory bank.
 */
class Distributor {

	/**
	 * This method analyzes a {@link MemoryExclusionGraph} and divides it into 
	 * several {@link MemoryExclusionGraph}, each corresponding to a memory 
	 * bank.<br>
	 * <br>
	 * Decisions of this methods are based on the {@link DAGEdge} associated 
	 * to each {@link MemoryExclusionVertex} of the processed {@link 
	 * MemoryExclusionGraph}. Number of created {@link MemoryExclusionGraph} 
	 * also depends on the selected distribution policy (see {@link 
	 * AbstractMemoryAllocatorTask} parameters.).
	 * It should be noted that each {@link MemoryExclusionVertex} of the 
	 * processed {@link MemoryExclusionGraph} will be put in one or more
	 * output {@link MemoryExclusionGraph}.
	 * 
	 * @param valuePolicy 
	 * 			 {@link String} containing the selected policy for the 
	 * 			 distribution of {@link MemoryExclusionVertex} in separate 
	 * 			 memory banks. (see {@link 
	 * 			 AbstractMemoryAllocatorTask} parameters.)
	 * @param memEx
	 * 			 The processed {@link MemoryExclusionGraph}
	 * @return
	 * 	A {@link Map} of {@link String} and {@link 
	 * 		MemoryExclusionGraph}. Each {@link String} identifies a memory 
	 * 		banks in which the associated {@link MemoryExclusionGraph} should
	 * 		be allocated.
	 * 
	 * @throws RuntimeException
	 * 		Currently thrown <ul>
	 * 		<li>if the distributed_only policy is applied to a 
	 * 		graph containing feedback FIFOs.</li>
	 * 		<li>if an unknown distribution policy is selected.</li>
	 * 		</ul>
	 */
	static def Map<String, MemoryExclusionGraph> distributeMeg(String valuePolicy,
		MemoryExclusionGraph memEx) throws RuntimeException {
		var Map<String, MemoryExclusionGraph> memExes
		memExes = new HashMap<String, MemoryExclusionGraph>

		// Generate output
		// Each entry of this map associate a memory to the set
		// of vertices of its MemEx. This map will be differently
		// depending on the policy chosen.
		var memExesVerticesSet = switch valuePolicy {
			case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED:
				distributeMegMixed(memEx)
			case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY:
				distributeMegDistributedOnly(memEx)
			case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY,
			case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT:
				distributeMegSharedOnly(memEx)
			default:
				throw new RuntimeException(
					"Unexpected distribution policy: " + valuePolicy + ".\n Allowed values are " +
						AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT)
		}

		// Create Memory Specific MemEx using their verticesSet
		for (String memory : memExesVerticesSet.keySet) {
			// Clone the input exclusion graph
			var copiedMemEx = memEx.clone as MemoryExclusionGraph
			// Obtain the list of vertices to remove from it
			var verticesToRemove = new HashSet<MemoryExclusionVertex>(copiedMemEx.vertexSet)
			verticesToRemove.removeAll(memExesVerticesSet.get(memory))
			// Remove them
			copiedMemEx.removeAllVertices(verticesToRemove)
			// Save the MemEx
			memExes.put(memory, copiedMemEx)
		}
		return memExes
	}

	/**
	 * Method used to separate the {@link MemoryExclusionVertex} of a {@link 
	 * MemoryExclusionGraph} into {@link HashSet subsets} according to the 
	 * SHARED_ONLY policy (see {@link AbstractMemoryAllocatorTask} parameters).
	 * <br><br>
	 * With this policy, all {@link MemoryExclusionVertex} are put in a single
	 * Shared memory.
	 * 
	 * @param memEx
	 * 			The processed {@link MemoryExclusionGraph}.
	 * @return {@link Map} containing the name of the memory banks and the 
	 * 		   associated {@link HashSet subsets} of {@link MemoryExclusionVertex}. 
	 */
	protected def static distributeMegSharedOnly(MemoryExclusionGraph memEx) {
		val memExesVerticesSet = new HashMap<String, HashSet<MemoryExclusionVertex>>
		memExesVerticesSet.put("Shared", new HashSet(memEx.vertexSet))
		memExesVerticesSet
	}

	/**
	 * Method used to separate the {@link MemoryExclusionVertex} of a {@link 
	 * MemoryExclusionGraph} into {@link HashSet subsets} according to the 
	 * DISTRIBUTED_ONLY policy (see {@link AbstractMemoryAllocatorTask} 
	 * parameters).
	 * <br><br>
	 * With this policy, each {@link MemoryExclusionVertex} is put in as many 
	 * memory banks as the number of processing elements accessing it during an
	 * iteration of the original dataflow graph.
	 * 
	 * @param memEx
	 * 			The processed {@link MemoryExclusionGraph}.
	 * @return {@link Map} containing the name of the memory banks and the 
	 * 		   associated {@link HashSet subsets} of {@link MemoryExclusionVertex}. 
	 */
	protected def static distributeMegDistributedOnly(MemoryExclusionGraph memEx) {
		val memExesVerticesSet = new HashMap<String, HashSet<MemoryExclusionVertex>>
		for (MemoryExclusionVertex memExVertex : memEx.vertexSet) {

			// For source then sink of DAG edge corresponding to the memex
			// vertex
			for (var i = 0; i < 2; i++) {
				// Retrieve the component on which the DAG Vertex is mapped
				var ComponentInstance component
				var edge = memExVertex.edge
				if (edge == null) {
					throw new RuntimeException("Feedback fifos not yet supported wit this policy.")
				}
				var dagVertex = if (i == 0) {
						edge.source
					} else {
						edge.target
					}

				component = dagVertex.propertyBean.getValue("Operator") as ComponentInstance

				var verticesSet = memExesVerticesSet.get(component.instanceName)
				if (verticesSet == null) {
					// If the component is not yet in the map, add it
					verticesSet = new HashSet<MemoryExclusionVertex>
					memExesVerticesSet.put(component.instanceName, verticesSet)
				}

				// Add the memEx Vertex to the set of vertex of the
				// component
				verticesSet.add(memExVertex)
			}
		}
		return memExesVerticesSet
	}

	/**
	 * Method used to separate the {@link MemoryExclusionVertex} of a {@link 
	 * MemoryExclusionGraph} into {@link HashSet subsets} according to the 
	 * MIXED policy (see {@link AbstractMemoryAllocatorTask} parameters).
	 * <br><br>
	 * With this policy, each {@link MemoryExclusionVertex} is put<ul><li>
	 * in the memory banks of a processing elements if it is the only PE
	 * accessing it during an iteration of the original dataflow graph.</li>
	 * <li> in shared memory otherwise (i.e. if multiple PE access this memory
	 * object during a graph iteration).</li></ul>
	 * 
	 * @param memEx
	 * 			The processed {@link MemoryExclusionGraph}.
	 * @return {@link Map} containing the name of the memory banks and the 
	 * 		   associated {@link HashSet subsets} of {@link MemoryExclusionVertex}. 
	 */
	protected def static distributeMegMixed(MemoryExclusionGraph memEx) {
		val memExesVerticesSet = new HashMap<String, HashSet<MemoryExclusionVertex>>
		for (MemoryExclusionVertex memExVertex : memEx.vertexSet) {
			var memory = "Shared"

			// If dag edge source and target are mapped to the same
			// component
			if (memExVertex.edge != null) {
				// If source and target are mapped to te same core
				if (memExVertex.edge.source.propertyBean.getValue("Operator") ==
					memExVertex.edge.target.propertyBean.getValue("Operator")) {
					var ComponentInstance component
					var dagVertex = memExVertex.edge.source
					component = dagVertex.propertyBean.getValue("Operator") as ComponentInstance
					memory = component.instanceName
				} // Else => Shared memory
			} else {
				// The MObject is not associated to a DAGEdge
				// It is either a FIFO_head/body or working memory
				// For now these mobjects are put in shared memory
			}

			var HashSet<MemoryExclusionVertex> verticesSet = memExesVerticesSet.get(memory)
			if (verticesSet == null) {
				// If the component is not yet in the map, add it
				verticesSet = new HashSet<MemoryExclusionVertex>
				memExesVerticesSet.put(memory, verticesSet)
			}

			// Add the memEx Vertex to the set of vertex of the
			// component
			verticesSet.add(memExVertex)
		}
		return memExesVerticesSet
	}
}