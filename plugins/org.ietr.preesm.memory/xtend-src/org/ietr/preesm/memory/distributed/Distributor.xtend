/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien_Hascoet <jhascoet@kalray.eu> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015 - 2016)
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
package org.ietr.preesm.memory.distributed

import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.List
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.dag.DAGEdge
import org.ietr.dftools.architecture.slam.ComponentInstance
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex
import org.ietr.preesm.memory.script.Range

import static extension org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask.*
import static extension org.ietr.preesm.memory.script.Range.*
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph

/**
 * This class contains all the code responsible for splitting a {@link 
 * MemoryExclusionGraph} into several graphs, each corresponding to a specific 
 * memory bank.
 */
@SuppressWarnings("unchecked")
class Distributor {

	/**
	 * {@link Logger} used to provide feedback on the distribution to the 
	 * developer.
	 */
	@Accessors
	static protected Logger logger;

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
	 * 
	 * @param alignment
	 * 			 This property is used to represent the alignment of buffers 
	 * 			 in memory. The same value, or a multiple should always be 
	 * 			 used in the memory allocation.
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
	static def Map<String, MemoryExclusionGraph> distributeMeg(String valuePolicy, MemoryExclusionGraph memEx, int alignment) throws RuntimeException {
		var Map<String, MemoryExclusionGraph> memExes
		memExes = new LinkedHashMap<String, MemoryExclusionGraph>

		// Generate output
		// Each entry of this map associate a memory to the set
		// of vertices of its MemEx. This map will be differently
		// depending on the policy chosen.
		var memExesVerticesSet = switch valuePolicy {
			case VALUE_DISTRIBUTION_MIXED: {
				// Split merged buffers (! modifies the memEx !)
				splitMergedBuffersMixed(memEx, alignment)
				distributeMegMixed(memEx)
			}
			case VALUE_DISTRIBUTION_MIXED_MERGED:
				distributeMegMixedMerged(memEx)
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

		// Update the memExexVerticesSet to include hosted mObjects
		// (splitMergedBuffers ensured that all mObjects hosted by another do 
		// fall in the same memory bank)
		val hosts = memEx.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>
		if(hosts !== null) {
			for (entry : memExesVerticesSet.entrySet) {
				// Filter: Get hosts falling in this bank
				// Values: Get the hosted sets of these hosts
				// Flatten: create an iterable of these hosted sets
				// addAll: Add these memory objects to the entry sets of mObject 
				entry.value.addAll(hosts.filter[host, hosted|entry.value.contains(host)].values.flatten)
			}
		}

		// Create Memory Specific MemEx using their verticesSet
		for (String memory : memExesVerticesSet.keySet) {
			// Clone the input exclusion graph
			var copiedMemEx = memEx.deepClone
			// Obtain the list of vertices to remove from it (including hosted vertices)
			var verticesToRemove = new LinkedHashSet<MemoryExclusionVertex>(copiedMemEx.totalSetOfVertices)
			verticesToRemove.removeAll(memExesVerticesSet.get(memory))
			// Remove them
			copiedMemEx.deepRemoveAllVertices(verticesToRemove)
			// If the DistributedOnl policy is used, split the merge memory 
			// objects.
			if(valuePolicy == VALUE_DISTRIBUTION_DISTRIBUTED_ONLY) {
				splitMergedBuffersDistributedOnly(copiedMemEx, alignment, memory)
			}
			// Save the MemEx
			memExes.put(memory, copiedMemEx)
		}
		return memExes
	}

	/**
	 * Finds the {@link MemoryExclusionVertex} in the given {@link 
	 * MemoryExclusionGraph} that result from a merge operation of the memory 
	 * scripts, and split them into several memory objects according to the 
	 * DistributedOnly distribution policy (see {@link 
	 * AbstractMemoryAllocatorTask} parameters).<br>
	 * <b>This method modifies the {@link MemoryExclusionGraph} passed as a 
	 * parameter.</b>
	 * 
	 * @param meg
	 * 			{@link MemoryExclusionGraph} whose {@link 
	 * 			MemoryExclusionVertex} are processed. This graph will be 
	 * 			modified by the method.
	 * 
	 * @param alignment
	 * 			 This property is used to represent the alignment of buffers 
	 * 			 in memory. The same value, or a multiple should always be 
	 * 			 used in the memory allocation.
	 * 
	 * @param memory
	 * 			Contains the name of the component to which the given {@link 
	 * 			MemoryExclusionGraph} is associated. Only merged {@link 
	 * 			MemoryExclusionVertex} accessed by this core will be kept in 
	 * 			the {@link MemoryExclusionGraph} when the split is applied. 
	 * 			(others will be removed).
	 * 			
	 */
	protected def static void splitMergedBuffersDistributedOnly(MemoryExclusionGraph meg, int alignment, String memory) {
		// Get the map of host Mobjects
		// (A copy of the map is used because the original map will be modified during iterations)
		val hosts = (meg.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>)

		// Exit method if no host Mobjects can be found in this MEG
		if(hosts === null || hosts.empty) {
			return
		}

		var hostsCopy = hosts.immutableCopy // Immutable copy for iteration purposes
		// Iterate over the Host MObjects of the MEG 
		// identify mObject to be removed because the belong to another
		// memory.
		val mObjectsToRemove = new LinkedHashSet<MemoryExclusionVertex>
		for (entry : hostsCopy.entrySet) {
			// Create a group of MObject for each memory similarly to what is done 
			// in distributeMeg method.		
			// Map storing the groups of Mobjs
			val mobjByBank = new LinkedHashMap<String, Set<MemoryExclusionVertex>>

			// Iteration List including the host Mobj
			for (mobj : #[entry.key] + entry.value) {
				findMObjBankDistributedOnly(mobj, mobjByBank, meg)
			}

			// If only one bank is used for all MObjs of this host,
			// skip the split operation as it is useless (and may induce extra 
			// bytes for alignment reasons)
			if(mobjByBank.size != 1) {
				// Apply only for mObj falling in the current bank
				splitMergedBuffers(mobjByBank, #{memory}, entry, meg, alignment);

				// Fill the mObjectsToRemove list
				// with all mObjects that are not in the current memory
				val mObjInOtherMem = mobjByBank.filter[bank, mObjects|bank != memory].values.flatten.toList
				// remove mobj that are duplicated in the current memory from this list
				mObjInOtherMem.removeAll(mobjByBank.get(memory))

				mObjectsToRemove.addAll(mObjInOtherMem)

			} else {
				// Check that this unique bank is the current one, otherwise, 
				// something went wrong or this and/or this meg is not the 
				// result of a call to distributeMegDistributedOnly method
				if(mobjByBank.keySet.get(0) != memory) {
					throw new RuntimeException(
						"Merged memory objects " + mobjByBank.values + " should not be allocated in memory bank " + mobjByBank.keySet.get(0) + " but in memory " + memory + " instead."
					)
				}
			}
		}

		// Remove all mObjects from other banks
		meg.deepRemoveAllVertices(mObjectsToRemove)
	}

	/**
	 * Finds the {@link MemoryExclusionVertex} in the given {@link 
	 * MemoryExclusionGraph} that result from a merge operation of the memory 
	 * scripts, and split them into several memory objects according to the 
	 * Mixed distribution policy (see {@link AbstractMemoryAllocatorTask} 
	 * parameters).<br>
	 * <b>This method modifies the {@link MemoryExclusionGraph} passed as a 
	 * parameter.</b>
	 * 
	 * @param meg
	 * 			{@link MemoryExclusionGraph} whose {@link 
	 * 			MemoryExclusionVertex} are processed. This graph will be 
	 * 			modified by the method.
	 * 
	 * @param alignment
	 * 			 This property is used to represent the alignment of buffers 
	 * 			 in memory. The same value, or a multiple should always be 
	 * 			 used in the memory allocation.
	 * 			
	 */
	protected def static void splitMergedBuffersMixed(MemoryExclusionGraph meg, int alignment) {
		// Get the map of host Mobjects
		// (A copy of the map is used because the original map will be modified during iterations)
		val hosts = (meg.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>)

		// Exit method if no host Mobjects can be found in this MEG
		if(hosts === null || hosts.empty) {
			return
		}

		var hostsCopy = hosts.immutableCopy // Immutable copy for iteration purposes
		// Iterate over the Host MObjects of the MEG 
		for (entry : hostsCopy.entrySet) {
			// Create a group of MObject for each memory similarly to what is done 
			// in distributeMeg method.		
			// Map storing the groups of Mobjs
			val mobjByBank = new LinkedHashMap<String, Set<MemoryExclusionVertex>>

			// Iteration List including the host Mobj
			for (mobj : #[entry.key] + entry.value) {
				findMObjBankMixed(mobj, mobjByBank)
			}

			// If only one bank is used for all MObjs of this host,
			// skip the split operation as it is useless (and may induce extra 
			// bytes for alignment reasons)
			if(mobjByBank.size != 1) {

				// Create the reverse bankByMobj map
				splitMergedBuffers(mobjByBank, mobjByBank.keySet, entry, meg, alignment)
			}
		}
	}

	/**
	 * Split the host {@link MemoryExclusionVertex} from the {@link 
	 * MemoryExclusionGraph}.
	 * 
	 * @param mobjByBank
	 * 		{@link Map} of {@link String} and {@link Set} of {@link 
	 * 		MemoryExclusionVertex}. Each memory bank (the {@link String}) is
	 * 		associated to the {@link Set} of {@link MemoryExclusionVertex} 
	 * 		that is to be allocated in this bank.
	 * @param banks
	 * 		{@link Set} of {@link String} representing the bank to process.
	 * 		This must be a sub-set of the keys of the mobjByBank param.
	 * @param hosts
	 * 		{@link MemoryExclusionVertex#HOST_MEMORY_OBJECT_PROPERTY} from the 
	 * 		given {@link MemoryExclusionGraph}.
	 * @param entry
	 * 		{@link Entry} copied from an immutable copy of the {@link 
	 * 		MemoryExclusionVertex#HOST_MEMORY_OBJECT_PROPERTY} of the given {@link 
	 *  	MemoryExclusionGraph}.
	 * 		This {@link Entry} is the only one to be processed when calling this 
	 * 		method. (i.e. the only one that is split.)
	 * @param meg
	 * 		{@link MemoryExclusionGraph} whose {@link 
	 * 		MemoryExclusionVertex} are processed. This graph will be 
	 * 		modified by the method.
	 * @param alignment
	 *  	This property is used to represent the alignment of buffers 
	 * 		in memory. The same value, or a multiple should always be 
	 * 		used in the memory allocation.
	 */
	protected def static void splitMergedBuffers(
		Map<String, Set<MemoryExclusionVertex>> mobjByBank,
		Set<String> banks,
		Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry,
		MemoryExclusionGraph meg,
		int alignment
	) {
		val hosts = (meg.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>)

		val bankByMobj = new LinkedHashMap<MemoryExclusionVertex, String>
		mobjByBank.forEach[bank, mobjs|mobjs.forEach[bankByMobj.put(it, bank)]]

		// Set of all the divided memory objects that can not be divided
		// because they are matched in several banks.
		val mObjsToUndivide = newLinkedHashSet

		// Remove information of the current host from the MEG
		// This is safe since a copy of the hosts is used for iteration
		hosts.remove(entry.key)
		meg.removeVertex(entry.key)
		val realRange = (entry.key.propertyBean.getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY) as List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>).get(0).value.value
		entry.key.weight = realRange.length

		// Put the real range in the same referential as other ranges (cf REAL_TOKEN_RANGE_PROPERTY comments)
		realRange.translate(-realRange.start)

		// For each bank, create as many hosts as the number of
		// non-contiguous ranges formed by the memory objects falling
		// into this memory bank.
		val newHostsMObjs = new LinkedHashSet<MemoryExclusionVertex>
		for (bankEntry : mobjByBank.filter[bank, mobjs|banks.contains(bank)].entrySet) {
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
					if(partsHostsSet.size == 1 && partsHostsSet.get(0).equals(bankEntry.key)) {
						// All hosts were allocated in the same bank
						// And this bank is the current bankEntry
						// The split is maintained, and rangesInHost must be updated
						// (use a clone because the range will be 
						// modified during call to the union method)
						rangesInHost.forEach[range|rangesInBank.union(range.value.value.clone as Range)]
					} else {
						// Not all hosts were allocated in the same bank
						// The mObj cannot be splitted
						// => It must be restored to its original size in 
						// the graph
						mObjsToUndivide.add(mobj)
					}
				}
			}

			// Find the list of mObjs falling in each range
			// Store the result in the rangesInBankAndMObjs map
			// mObjsToUndivide are not part of these memory objects
			val mObjInCurrentBank = mobjByBank.get(bankEntry.key)
			mObjInCurrentBank.removeAll(mObjsToUndivide)
			val Map<Range, List<MemoryExclusionVertex>> rangesInBankAndMObjs = newLinkedHashMap
			for (currentRange : rangesInBank) {
				// 1. Get the list of mObjects falling in this range
				val mObjInCurrentRange = new ArrayList<MemoryExclusionVertex>
				for (mObj : mObjInCurrentBank) {
					val ranges = mObj.getPropertyBean.getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY) as List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>
					if(ranges.size == 1) {
						// Buffer is undivided, check if it intersect with 
						// the current range in bank 
						if(ranges.get(0).value.value.intersection(currentRange) !== null) {
							// Add undivided object at the beginning of the list
							// to make sure that no divided object will ever be selected
							// as a host in the next step
							mObjInCurrentRange.add(0, mObj)
						}
					} else {
						// Buffer is divided, check if *any* of its range
						// intersects with the current range in bank
						// (i.e. check if *not* none of its range intersect with the range)
						if(! ranges.forall[range|range.value.value.intersection(currentRange) === null]) {
							// Add divided object at the end of the list
							// to make sure that no divided object will ever be selected
							// as a host in the next step
							mObjInCurrentRange.add(mObj)
						}
					}
				}
				rangesInBankAndMObjs.put(currentRange, mObjInCurrentRange)
			}

			// Create a memory object for each range in the bank
			for (currentRangeAndMobjs : rangesInBankAndMObjs.entrySet) {
				val currentRange = currentRangeAndMobjs.key
				val mObjInCurrentRange = currentRangeAndMobjs.value

				// 1. Select the first object as the new host 
				// (cannot be a divided mObject as divided mObjects were 
				// always added at the end of the list)
				val newHostMobj = mObjInCurrentRange.get(0)
				newHostsMObjs.add(newHostMobj)

				// 2. Give the new host the right size
				// (pay attention to alignment)
				// Get aligned min index range
				val newHostOldRange = newHostMobj.propertyBean.getValue(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY) as List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>
				val minIndex = if(alignment <= 0) {
						currentRange.start
					} else {
						// Make sure that index aligned in the buffer are in 
						// fact aligned.
						// This goal is here to make sure that
						// index 0 of the new host buffer is aligned !
						val newHostOldStart = newHostOldRange.get(0).value.value.start; // .value.value
						// If the result of te modulo is not null, unaligned 
						// corresponds to the number of "extra" bytes making
						// index 0 of newHost not aligned with respect to 
						// currentRangeStart
						val unaligned = (newHostOldStart - currentRange.start) % alignment
						if(unaligned == 0)
							// Index 0 of new host is already aligned
							currentRange.start
						else
							// Index 0 of new host is not aligned
							// Extra-bytes are added to the new range
							// to re-align it.
							currentRange.start - (alignment - unaligned)
					}
				newHostMobj.setWeight(currentRange.end - minIndex)

				if(mObjInCurrentRange.size <= 1) {
					// The new host Mobj does not host any other MObj
					// last code was still applied to make sure that 
					// the mObject has the right size (although it 
					// will never be larger than its original weight
					// and never by misaligned as it does not not 
					// host anything) 
					newHostMobj.propertyBean.removeProperty(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY)
				} else {
					// The remaining processing should only be applied if 
					// the mObject is not alone in its range and does 
					// actually host other mObjects
					// Update newHostMobj realTokenRange property
					val newHostRealTokenRange = newHostOldRange.get(0).value.key
					val newHostActualRealTokenRange = newHostOldRange.get(0).value.value.translate(-minIndex)
					val ranges = newArrayList
					ranges.add(newHostMobj -> (newHostRealTokenRange -> newHostActualRealTokenRange))
					newHostMobj.setPropertyValue(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY, ranges)

					// Add the mobj to the meg hosts list
					val hostMObjSet = newLinkedHashSet
					hosts.put(newHostMobj, hostMObjSet)

					// 3. Add all hosted mObjects to the list
					// and set their properties
					// (exclude first mObj from iteration, as it is the 
					// new host)
					for (mObj : mObjInCurrentRange.tail) {
						// update the real token range property by translating
						// ranges to the current range referential
						val mObjRanges = mObj.propertyBean.getValue(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY) as List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>
						val mObjNewRanges = newArrayList
						for (mObjRangePair : mObjRanges) {
							// Check if the current mObjRangePair overlaps with
							// the current range. 
							// Always OK for undivided buffers
							// If a divided buffer is mapped into several 
							// hosts, this code makes sure that each mapped 
							// ranged is updated only when the corresponding 
							// range is processed.
							if(mObjRangePair.value.value.hasOverlap(currentRange)) {
								// case for Undivided buffer and divided 
								// range falling into the current range
								mObjNewRanges.add(newHostMobj -> (mObjRangePair.value.key -> mObjRangePair.value.value.translate(-minIndex - newHostActualRealTokenRange.start) ))
							} else {
								// Case for divided range not falling into 
								// the current range
								mObjNewRanges.add(mObjRangePair)
							}
						}

						// Save property and update hostMObjSet
						mObj.propertyBean.setValue(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY, mObjNewRanges)
						hostMObjSet.add(mObj)
					}
				}
			}
		}

		// Add the new host MObjects to the MEG
		// and add exclusion of the host
		for (newHostMobj : newHostsMObjs) {
			// Add new host to the MEG
			meg.addVertex(newHostMobj)
			// Add exclusions
			restoreExclusions(meg, newHostMobj)
		}

		if(logger !== null && !mObjsToUndivide.empty) {
			logger.log(Level.WARNING, "The following divided memory object " + mObjsToUndivide + " are unified again during the memory distribution process.\n" + "This unification was applied because divided memory objects cannot be merged in several distinct memories.\n" +
				"Deactivating memory script causing this division may lead to lower memory footprints in this distributed memory context.")
		}

		// Process the mObjects to "undivide".
		for (mObj : mObjsToUndivide) {

			// Remove the Mobject from the MEG HOST_MEM_OBJ property
			// Already done when host are removed from HOST_MEM_OBJ in previous loop
			// val hostMemObjProp = meg.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>
			// hostMemObjProp.forEach[hostMObj, hostedMobjs|hostedMobjs.remove(mObj)]
			// Add the mobj back to the Meg
			meg.addVertex(mObj)

			// Restore original exclusions of the mobj
			restoreExclusions(meg, mObj)
		}
	}

	/**
	 * Restore exclusions in the {@link MemoryExclusionGraph MEG} for the given
	 * {@link MemoryExclusionVertex}, (and its hosted
	 * {@link MemoryExclusionVertex}, if any).
	 * </br></br>
	 * <b>Method called by {@link #splitMergedBuffers(String, MemoryExclusionGraph, int)} only.</b>
	 * 
	 * @param meg
	 *            the {@link MemoryExclusionGraph} to which the exclusion are to
	 *            be added.
	 * @param mObj
	 *            the {@link MemoryExclusionVertex} whose exclusions are
	 *            restored (may be a host vertex).
	 */
	protected def static void restoreExclusions(MemoryExclusionGraph meg, MemoryExclusionVertex mObj) {
		// Get the hosts property of the MEG
		val hosts = (meg.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>)

		// iteration over the host and its hosted vertices, if any
		val mObjAndHostedMObjs = #{mObj} + (hosts.get(mObj) ?: #{})

		for (curentMObj : mObjAndHostedMObjs) {
			val adjacentMObjs = curentMObj.propertyBean.getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP) as List<MemoryExclusionVertex>
			for (adjacentMObj : adjacentMObjs) {
				// Check if the adjacent mObj is already in the graph
				if(adjacentMObj != mObj // No self-exclusion 
				&& meg.vertexSet.contains(adjacentMObj)) {
					// the adjacent mObj is already in the graph
					// Add the exclusion back
					meg.addEdge(mObj, adjacentMObj)
				} else {
					// Happens if adjacentMObj is:
					// - a hosted MObj
					// - a mobjToUndivide (not yet added back to the graph)
					// The adjacent mObj is not in the graph
					// It must be merged within a host 
					// (or several host in case of a division)
					hosts.forEach [ hostMObj, hostedMObjs |
						if(hostMObj != mObj // No self-exclusion 
						&& hostedMObjs.contains(adjacentMObj) // Does the tested host contain the adjacentMObj
						&& meg.containsVertex(hostMObj) // If hostMobj was already added to the MEG
						// && !meg.containsEdge(mObj,hostMObj) // Exclusion does not already exists
						) {
							meg.addEdge(mObj, hostMObj)
						}
					]
				// If the adjacent mObj was not yet added back to the MEG, 
				// this forEach will have no impact, but edges will be
				// created on processing of this adjacent mObjs in  
				// a following iteration of the current for loop.
				}
			}
		}
	}

	/**
	 * Method used to separate the {@link MemoryExclusionVertex} of a {@link 
	 * MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the 
	 * SHARED_ONLY policy (see {@link AbstractMemoryAllocatorTask} parameters).
	 * <br><br>
	 * With this policy, all {@link MemoryExclusionVertex} are put in a single
	 * Shared memory.
	 * 
	 * @param memEx
	 * 			The processed {@link MemoryExclusionGraph}.
	 * @return {@link Map} containing the name of the memory banks and the 
	 * 		   associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}. 
	 */
	protected def static Map<String, Set<MemoryExclusionVertex>> distributeMegSharedOnly(MemoryExclusionGraph memEx) {
		val memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>
		memExesVerticesSet.put("Shared", new LinkedHashSet(memEx.vertexSet))
		return memExesVerticesSet
	}

	/**
	 * Method used to separate the {@link MemoryExclusionVertex} of a {@link 
	 * MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the 
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
	 * 		   associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}. 
	 */
	protected def static Map<String, Set<MemoryExclusionVertex>> distributeMegDistributedOnly(MemoryExclusionGraph memEx) {
		val memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>
		val hosts = memEx.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>
		for (MemoryExclusionVertex memExVertex : memEx.vertexSet) {
			val hostedMObjs = if(hosts !== null) {
					hosts.get(memExVertex);
				} else {
					null
				}

			// For source then sink of DAG edge corresponding to the memex
			// vertex
			findMObjBankDistributedOnly(memExVertex, memExesVerticesSet, memEx)

			// special processing for host Mobj
			if(hostedMObjs !== null) {
				// Create a fake map that will store the theoretical banks of all
				// hosted mObjects
				val hostedMObjsBanks = new LinkedHashMap<String, Set<MemoryExclusionVertex>>
				for (hostedMobj : hostedMObjs) {
					findMObjBankDistributedOnly(hostedMobj, hostedMObjsBanks, memEx)
				}

				// Add the banks for the hosted MObjs (the split of hosted mObj will 
				// be done later, after duplication of the MEG to avoid having the 
				// same MObj several time in a pre-duplication MEG)
				for (bank : hostedMObjsBanks.keySet) {
					var verticesSet = memExesVerticesSet.get(bank)
					if(verticesSet === null) {
						// If the component is not yet in the map, add it
						verticesSet = new LinkedHashSet<MemoryExclusionVertex>
						memExesVerticesSet.put(bank, verticesSet)
					}
					// Add the memEx Vertex to the set of vertex of the
					// component
					verticesSet.add(memExVertex)
				}
			}
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
	 * @param memEx
	 * 			The {@link MemoryExclusionGraph} whose vertices are allocated.
	 * 			(only used to retrieved the corresponding {@link 
	 * 			DirectedAcyclicGraph}).
	 */
	protected def static void findMObjBankDistributedOnly(MemoryExclusionVertex mObj, Map<String, Set<MemoryExclusionVertex>> mObjByBank, MemoryExclusionGraph memEx) {
		// Process the given mObj
		for (var i = 0; i < 2; i++) {
			// Retrieve the component on which the DAG Vertex is mapped
			var ComponentInstance component
			var edge = mObj.edge
			var dagVertex = if(edge !== null) {
					if(i == 0) {
						edge.source
					} else {
						edge.target
					}
				} else {
					// retrieve
					val dag = memEx.propertyBean.getValue(MemoryExclusionGraph.SOURCE_DAG) as DirectedAcyclicGraph
					if(i == 0) {
						dag.getVertex(mObj.getSource.substring(("FIFO_Head_").length))
					} else {
						dag.getVertex(mObj.getSink)
					}
				}

			component = dagVertex.propertyBean.getValue("Operator") as ComponentInstance

			var verticesSet = mObjByBank.get(component.instanceName)
			if(verticesSet === null) {
				// If the component is not yet in the map, add it
				verticesSet = new LinkedHashSet<MemoryExclusionVertex>
				mObjByBank.put(component.instanceName, verticesSet)
			}

			// Add the memEx Vertex to the set of vertex of the
			// component
			verticesSet.add(mObj)
		}
	}

	/**
	 * Method used to separate the {@link MemoryExclusionVertex} of a {@link 
	 * MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the 
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
	 * 		   associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}. 
	 */
	protected def static Map<String, Set<MemoryExclusionVertex>> distributeMegMixed(MemoryExclusionGraph memEx) {
		val memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>
		for (MemoryExclusionVertex memExVertex : memEx.vertexSet) {
			Distributor.findMObjBankMixed(memExVertex, memExesVerticesSet)
		}
		return memExesVerticesSet
	}

	/**
	 * Method used to separate the {@link MemoryExclusionVertex} of a {@link 
	 * MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the 
	 * MIXED_MERGED policy (see {@link AbstractMemoryAllocatorTask} 
	 * parameters).
	 * <br><br>
	 * With this policy, each {@link MemoryExclusionVertex} is put<ul><li>
	 * in the memory banks of a processing elements if it is the only PE
	 * accessing it during an iteration of the original dataflow graph.</li>
	 * <li> in shared memory if it is a merged buffer, unless all mObjects of 
	 * the merged buffer fall in the same memory bank.</li>
	 * <li> in shared memory otherwise (i.e. if multiple PE access this memory
	 * object during a graph iteration).</li></ul>
	 * 
	 * @param memEx
	 * 			The processed {@link MemoryExclusionGraph}.
	 * @return {@link Map} containing the name of the memory banks and the 
	 * 		   associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}. 
	 */
	protected def static Map<String, Set<MemoryExclusionVertex>> distributeMegMixedMerged(MemoryExclusionGraph memEx) {
		val memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>
		val hosts = memEx.propertyBean.getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>
		for (MemoryExclusionVertex memExVertex : memEx.vertexSet) {
			if(hosts !== null && !hosts.containsKey(memExVertex) || hosts===null) {
				Distributor.findMObjBankMixed(memExVertex, memExesVerticesSet)
			} else {
				// Check if all hosted mObjects fall in the same bank
				val mobjByBank = new LinkedHashMap<String, Set<MemoryExclusionVertex>>

				// Find the bank for each mObj of the group
				for (mobj : #[memExVertex] + hosts.get(memExVertex)) {
					findMObjBankMixed(mobj, mobjByBank)
				}

				// The bank is, the first if all mObjects fall in the same bank
				// Shared memory otherwise
				var bank = if(mobjByBank.size == 1) {
						mobjByBank.keySet.get(0)
					} else {
						"Shared"
					}

				// Put the mObj in the verticesSet
				var verticesSet = memExesVerticesSet.get(bank)
				if(verticesSet === null) {
					// If the component is not yet in the map, add it
					verticesSet = new LinkedHashSet<MemoryExclusionVertex>
					memExesVerticesSet.put(bank, verticesSet)
				}
				verticesSet.add(memExVertex)
			}
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
	protected def static void findMObjBankMixed(MemoryExclusionVertex mObj, Map<String, Set<MemoryExclusionVertex>> mObjByBank) {
		var memory = "Shared"

		// If dag edge source and target are mapped to the same
		// component
		if(mObj.edge !== null) {
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
		if(verticesSet === null) {
			// If the component is not yet in the map, add it
			verticesSet = new LinkedHashSet<MemoryExclusionVertex>
			mObjByBank.put(memory, verticesSet)
		}

		// Add the memEx Vertex to the set of vertex of the
		// component
		verticesSet.add(mObj)
	}
}
