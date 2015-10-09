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

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Set
import org.ietr.dftools.algorithm.model.dag.DAGEdge
import org.ietr.dftools.architecture.slam.ComponentInstance
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex
import org.ietr.preesm.memory.script.Range

import static extension org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask.*
import static extension org.ietr.preesm.memory.script.Range.*

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
	static def Map<String, MemoryExclusionGraph> distributeMeg(String valuePolicy, MemoryExclusionGraph memEx) throws RuntimeException {
		var Map<String, MemoryExclusionGraph> memExes
		memExes = new HashMap<String, MemoryExclusionGraph>

		// Split merged buffers if a distributed policy
		// is used
		if(valuePolicy == VALUE_DISTRIBUTION_MIXED || valuePolicy == VALUE_DISTRIBUTION_DISTRIBUTED_ONLY) {
			splitMergedBuffers(valuePolicy, memEx)
		}

		// Generate output
		// Each entry of this map associate a memory to the set
		// of vertices of its MemEx. This map will be differently
		// depending on the policy chosen.
		var memExesVerticesSet = switch valuePolicy {
			case VALUE_DISTRIBUTION_MIXED:
				distributeMegMixed(memEx)
			case VALUE_DISTRIBUTION_DISTRIBUTED_ONLY:
				distributeMegDistributedOnly(memEx)
			case VALUE_DISTRIBUTION_SHARED_ONLY,
			case VALUE_DISTRIBUTION_DEFAULT:
				distributeMegSharedOnly(memEx)
			default:
				throw new RuntimeException(
					"Unexpected distribution policy: " + valuePolicy + ".\n Allowed values are " + VALUE_DISTRIBUTION_DEFAULT
				)
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
	 * Finds the {@link MemoryExclusionVertex} in the given {@link 
	 * MemoryExclusionGraph} that result from a merge operation of the memory 
	 * scripts, and split them into several memory objects according to the 
	 * selected distribution policy (see {@link AbstractMemoryAllocatorTask} 
	 * parameters).<br>
	 * <b>This method modifies the {@link MemoryExclusionGraph} passed as a 
	 * parameter.</b>
	 * @param policy 
	 * 			{@link String} of the distribution policy selected (see {@link 
	 * 			AbstractMemoryAllocatorTask} parameters).
	 * @param meg
	 * 			{@link MemoryExclusionGraph} whose {@link 
	 * 			MemoryExclusionVertex} are processed. This graph will be 
	 * 			modified by the method.
	 * 			
	 */
	protected def static splitMergedBuffers(String policy, MemoryExclusionGraph meg) {
		// Get the map of host Mobjects
		// (A copy of the map is used because the original map will be modified during iterations)
		var hosts = (meg.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) //.immutableCopy

		// Exit method if no host Mobjects can be found in this MEG
		if(hosts == null || hosts.empty) {
			return
		}

		// Iterate over the Host MObjects of the MEG 
		for (entry : hosts.entrySet) {
			// Create a group of MObject for each memory similarly to what is done 
			// in distributeMeg method.		
			// Map storing the groups of Mobjs
			val mobjByBank = new HashMap<String, Set<MemoryExclusionVertex>>

			// Iteration List including the host Mobj
			for (mobj : #[entry.key] + entry.value) {
				switch (policy) {
					case VALUE_DISTRIBUTION_MIXED:
						findMObjBankMixed(mobj, mobjByBank)
					case VALUE_DISTRIBUTION_DISTRIBUTED_ONLY:
						findMObjBankDistributedOnly(mobj, mobjByBank)
					default:
						throw new RuntimeException(policy + " is not a valid distribution policy to split a MEG." + " Contact Preesm developers.")
				}
			}
			
			// Create the reverse bankByMobj map
			val bankByMobj = new HashMap<MemoryExclusionVertex,String>
			mobjByBank.forEach[bank, mobjs| mobjs.forEach[bankByMobj.put(it,bank)]]
			
			// Set of all the divided memory objects that can not be divided
			// because they are matched in several banks.
			val mObjsToUndivide = newHashSet 

			// For each bank, create as many hosts as the number of
			// non-contiguous ranges formed by the memory objects falling
			// into this memory bank.
			for (bankEntry : mobjByBank.entrySet) {
				// Iterate over Mobjects to build the range(s) of this memory 
				// bank (all ranges are relative to the host mObj)
				val List<Range> rangesInBank = new ArrayList
				for (mobj : bankEntry.value) {
					val rangesInHost = mobj.getPropertyBean.getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY) as List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>
					// Process non-divided buffers 
					if(rangesInHost.size == 1) {
						// add the range covered by this buffer to the
						// rangesInBank (use a clone because the range will be 
						// modified during call to the union method
						rangesInBank.union(rangesInHost.get(0).value.value.clone as Range); 
					} else {
						// Divided buffers: 
						// Check if all parts of the MObj were allocated in the same memory bank
						// i.e. check if source and dest memory objects are 
						// all stored in the same memory bank
						val dividedPartsHosts = mobj.propertyBean.getValue(MemoryExclusionVertex.DIVIDED_PARTS_HOSTS) as List<MemoryExclusionVertex>
						val partsHostsSet = dividedPartsHosts.map[bankByMobj.get(it)].toSet
						if(partsHostsSet.size == 1 && partsHostsSet.get(0).equals(bankEntry.key)){
							// All hosts were allocated in the same bank
							// And this bank is the current bankEntry
							// The split is maintained, and rangesInHost must be updated
							// (use a clone because the range will be 
							// modified during call to the union method)
							rangesInHost.forEach[range | rangesInBank.union(range.value.value.clone as Range)]
						} else {
							// Not all hosts were allocated in the same bank
							// The mObj cannot be splitted
							// => It must be restored to its original size in 
							// the graph
							mObjsToUndivide.add(mobj)				
						}
						println(rangesInHost)
					}
				}
				
				// Create a memory object for each range in the bank
				print("\n"+bankEntry.key+"-->")
				for(rangeInBank : rangesInBank){
					// 1. Get the list of mObjects in this range
					// 2. Select the first object as the new host
					// 3. Update the MEG
				}
			}
			
			// Process the mObjects to "undivide".
			for(mObj : mObjsToUndivide){
				// Remove the Mobject from the MEG HOST_MEM_OBJ property
				val hostMemObjProp = meg.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>
				hostMemObjProp.forEach[hostMObj, hostedMobjs| hostedMobjs.remove(mObj)]
				
				// Add the mobj back to the Meg
				meg.addVertex(mObj)
				
				// Restore original exclusions of the mobj
				val adjacentMObjs = mObj.propertyBean.getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP) as List<MemoryExclusionVertex>
				for(adjacentMObj : adjacentMObjs){
					// Check if the adjacent mObj is already in the graph
					if(meg.vertexSet.contains(adjacentMObj)) {
						// the adjacent mObj is already in the graph
						// Add the exclusion back
						meg.addEdge(mObj,adjacentMObj)
					} else {
						// The adjacent mObj is not in the graph
						// It must be merged within a host 
						// (or several host in case of a division)
						hostMemObjProp.forEach[
							hostMObj, hostedMObjs |
							if (hostedMObjs.contains(adjacentMObj)) {
								meg.addEdge(mObj, hostMObj)
							}
						]
					}
				}
			}
		}
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
		val memExesVerticesSet = new HashMap<String, Set<MemoryExclusionVertex>>
		for (MemoryExclusionVertex memExVertex : memEx.vertexSet) {

			// For source then sink of DAG edge corresponding to the memex
			// vertex
			findMObjBankDistributedOnly(memExVertex, memExesVerticesSet)
		}
		return memExesVerticesSet
	}

	/**
	 * The purpose of this method is to find the bank associated to a given 
	 * {@link MemoryExclusionVertex} according to the DISTRIBUTED_ONLY 
	 * distribution policy. The mObj is put in the {@link Map} passed as a 
	 * parameter where keys are the names of the memory banks and values are 
	 * the {@link Set} of associated {@link MemoryExclusionVertex}.
	 * 
	 * @param mObj
	 * 			The {@link MemoryExclusionVertex} whose memory banks are 
	 * 			identified.
	 * @param mObjByBank
	 * 			The {@link Map} in which results of this method are put.
	 */
	protected def static findMObjBankDistributedOnly(MemoryExclusionVertex mObj, Map<String, Set<MemoryExclusionVertex>> mObjByBank) {
		for (var i = 0; i < 2; i++) {
			// Retrieve the component on which the DAG Vertex is mapped
			var ComponentInstance component
			var edge = mObj.edge
			if(edge == null) {
				throw new RuntimeException("Feedback fifos not yet supported wit this policy.")
			}
			var dagVertex = if(i == 0) {
					edge.source
				} else {
					edge.target
				}

			component = dagVertex.propertyBean.getValue("Operator") as ComponentInstance

			var verticesSet = mObjByBank.get(component.instanceName)
			if(verticesSet == null) {
				// If the component is not yet in the map, add it
				verticesSet = new HashSet<MemoryExclusionVertex>
				mObjByBank.put(component.instanceName, verticesSet)
			}

			// Add the memEx Vertex to the set of vertex of the
			// component
			verticesSet.add(mObj)
		}
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
		val memExesVerticesSet = new HashMap<String, Set<MemoryExclusionVertex>>
		for (MemoryExclusionVertex memExVertex : memEx.vertexSet) {
			Distributor.findMObjBankMixed(memExVertex, memExesVerticesSet)
		}
		return memExesVerticesSet
	}

	/**
	 * The purpose of this method is to find the bank associated to a given 
	 * {@link MemoryExclusionVertex} according to the MIXED distribution 
	 * policy. The mObj is put in the {@link Map} passed as a parameter where
	 * keys are the names of the memory banks and values are the {@link Set} 
	 * of associated {@link MemoryExclusionVertex}.
	 * 
	 * @param mObj
	 * 			The {@link MemoryExclusionVertex} whose memory banks are 
	 * 			identified.
	 * @param mObjByBank
	 * 			The {@link Map} in which results of this method are put.
	 */
	protected def static findMObjBankMixed(MemoryExclusionVertex mObj, Map<String, Set<MemoryExclusionVertex>> mObjByBank) {
		var memory = "Shared"

		// If dag edge source and target are mapped to the same
		// component
		if(mObj.edge != null) {
			// If source and target are mapped to te same core
			if(mObj.edge.source.propertyBean.getValue("Operator") == mObj.edge.target.propertyBean.getValue("Operator")) {
				var ComponentInstance component
				var dagVertex = mObj.edge.source
				component = dagVertex.propertyBean.getValue("Operator") as ComponentInstance
				memory = component.instanceName
			} // Else => Shared memory
		} else {
			// The MObject is not associated to a DAGEdge
			// It is either a FIFO_head/body or working memory
			// For now these mobjects are put in shared memory
		}

		var verticesSet = mObjByBank.get(memory)
		if(verticesSet == null) {
			// If the component is not yet in the map, add it
			verticesSet = new HashSet<MemoryExclusionVertex>
			mObjByBank.put(memory, verticesSet)
		}

		// Add the memEx Vertex to the set of vertex of the
		// component
		verticesSet.add(mObj)
	}
}