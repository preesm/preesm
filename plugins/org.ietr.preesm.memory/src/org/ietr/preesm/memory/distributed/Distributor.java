/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
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
package org.ietr.preesm.memory.distributed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.ietr.preesm.memory.script.Range;

/**
 * This class contains all the code responsible for splitting a {@link MemoryExclusionGraph} into several graphs, each corresponding to a specific memory bank.
 */
public class Distributor {

  /**
   * {@link Logger} used to provide feedback on the distribution to the developer.
   */
  protected static Logger logger;

  /**
   * Set the logger used in this file. It must be done before the first instantiation of this class.
   * 
   * @param logger
   *          Logger to be used.
   */
  public static void setLogger(Logger logger) {
    Distributor.logger = logger;
  }

  /**
   * This method analyzes a {@link MemoryExclusionGraph} and divides it into several {@link MemoryExclusionGraph}, each corresponding to a memory bank.<br>
   * <br>
   * Decisions of this methods are based on the {@link DAGEdge} associated to each {@link MemoryExclusionVertex} of the processed {@link MemoryExclusionGraph}.
   * Number of created {@link MemoryExclusionGraph} also depends on the selected distribution policy (see {@link AbstractMemoryAllocatorTask} parameters.). It
   * should be noted that each {@link MemoryExclusionVertex} of the processed {@link MemoryExclusionGraph} will be put in one or more output
   * {@link MemoryExclusionGraph}.
   *
   * @param valuePolicy
   *          {@link String} containing the selected policy for the distribution of {@link MemoryExclusionVertex} in separate memory banks. (see
   *          {@link AbstractMemoryAllocatorTask} parameters.)
   * @param memEx
   *          The processed {@link MemoryExclusionGraph}
   *
   * @param alignment
   *          This property is used to represent the alignment of buffers in memory. The same value, or a multiple should always be used in the memory
   *          allocation.
   * @return A {@link Map} of {@link String} and {@link MemoryExclusionGraph}. Each {@link String} identifies a memory banks in which the associated
   *         {@link MemoryExclusionGraph} should be allocated.
   *
   * @throws RuntimeException
   *           Currently thrown
   *           <ul>
   *           <li>if the distributed_only policy is applied to a graph containing feedback FIFOs.</li>
   *           <li>if an unknown distribution policy is selected.</li>
   *           </ul>
   */
  public static Map<String, MemoryExclusionGraph> distributeMeg(String valuePolicy, MemoryExclusionGraph memEx, int alignment) throws RuntimeException {
    Map<String, MemoryExclusionGraph> memExes = new LinkedHashMap<>();

    // Generate output
    // Each entry of this map associate a memory to the set
    // of vertices of its MemEx. This map will be differently
    // depending on the policy chosen.
    Map<String, Set<MemoryExclusionVertex>> memExesVerticesSet;
    switch (valuePolicy) {
      case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED:
        // Split merged buffers (! modifies the memEx !)
        splitMergedBuffersMixed(memEx, alignment);
        memExesVerticesSet = distributeMegMixed(memEx);
        break;
      case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED_MERGED:
        memExesVerticesSet = distributeMegMixedMerged(memEx);
        break;
      case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY:
        memExesVerticesSet = distributeMegDistributedOnly(memEx);
        break;
      case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY:
      case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT:
        memExesVerticesSet = distributeMegSharedOnly(memEx);
        break;
      default:
        throw new RuntimeException(
            "Unexpected distribution policy: " + valuePolicy + ".\n Allowed values are " + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);
    }

    // Update the memExexVerticesSet to include hosted mObjects
    // (splitMergedBuffers ensured that all mObjects hosted by another do
    // fall in the same memory bank)
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) memEx.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    if (hosts != null) {
      for (Entry<String, Set<MemoryExclusionVertex>> entry : memExesVerticesSet.entrySet()) {
        // Filter: Get hosts falling in this bank
        // Values: Get the hosted sets of these hosts
        // Flatten: create an iterable of these hosted sets
        // addAll: Add these memory objects to the entry sets of mObject
        entry.getValue().addAll(hosts.entrySet().stream().filter(mapEntry -> entry.getValue().contains(mapEntry.getKey())).map(it -> it.getValue())
            .flatMap(it -> it.stream()).collect(Collectors.toList()));
      }
    }

    // Create Memory Specific MemEx using their verticesSet
    for (String memory : memExesVerticesSet.keySet()) {
      // Clone the input exclusion graph
      MemoryExclusionGraph copiedMemEx = memEx.deepClone();
      // Obtain the list of vertices to remove from it (including hosted vertices)
      Set<MemoryExclusionVertex> verticesToRemove = new LinkedHashSet<>(copiedMemEx.getTotalSetOfVertices());
      verticesToRemove.removeAll(memExesVerticesSet.get(memory));
      // Remove them
      copiedMemEx.deepRemoveAllVertices(verticesToRemove);
      // If the DistributedOnl policy is used, split the merge memory
      // objects.
      if (valuePolicy == AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY) {
        splitMergedBuffersDistributedOnly(copiedMemEx, alignment, memory);
      }
      // Save the MemEx
      memExes.put(memory, copiedMemEx);
    }
    return memExes;
  }

  /**
   * Finds the {@link MemoryExclusionVertex} in the given {@link MemoryExclusionGraph} that result from a merge operation of the memory scripts, and split them
   * into several memory objects according to the DistributedOnly distribution policy (see {@link AbstractMemoryAllocatorTask} parameters).<br>
   * <b>This method modifies the {@link MemoryExclusionGraph} passed as a parameter.</b>
   *
   * @param meg
   *          {@link MemoryExclusionGraph} whose {@link MemoryExclusionVertex} are processed. This graph will be modified by the method.
   *
   * @param alignment
   *          This property is used to represent the alignment of buffers in memory. The same value, or a multiple should always be used in the memory
   *          allocation.
   *
   * @param memory
   *          Contains the name of the component to which the given {@link MemoryExclusionGraph} is associated. Only merged {@link MemoryExclusionVertex}
   *          accessed by this core will be kept in the {@link MemoryExclusionGraph} when the split is applied. (others will be removed).
   *
   */
  protected static void splitMergedBuffersDistributedOnly(MemoryExclusionGraph meg, int alignment, String memory) {
    // Get the map of host Mobjects
    // (A copy of the map is used because the original map will be modified during iterations)
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) meg.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

    // Exit method if no host Mobjects can be found in this MEG
    if (hosts == null || hosts.isEmpty()) {
      return;
    }

    // var hostsCopy = hosts.immutableCopy // Immutable copy for iteration purposes
    // removed by ahonorat, is that really needed?

    // Iterate over the Host MObjects of the MEG
    // identify mObject to be removed because the belong to another
    // memory.
    Set<MemoryExclusionVertex> mObjectsToRemove = new LinkedHashSet<>();
    for (Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : hosts.entrySet()) {
      // Create a group of MObject for each memory similarly to what is done
      // in distributeMeg method.
      // Map storing the groups of Mobjs
      Map<String, Set<MemoryExclusionVertex>> mobjByBank = new LinkedHashMap<>();

      // Iteration List including the host Mobj
      findMObjBankDistributedOnly(entry.getKey(), mobjByBank, meg);
      for (MemoryExclusionVertex mobj : entry.getValue()) {
        findMObjBankDistributedOnly(mobj, mobjByBank, meg);
      }

      // If only one bank is used for all MObjs of this host,
      // skip the split operation as it is useless (and may induce extra
      // bytes for alignment reasons)
      if (mobjByBank.size() != 1) {
        // Apply only for mObj falling in the current bank
        splitMergedBuffers(mobjByBank, Collections.singleton(memory), entry, meg, alignment);

        // Fill the mObjectsToRemove list
        // with all mObjects that are not in the current memory
        List<MemoryExclusionVertex> mObjInOtherMem = mobjByBank.entrySet().stream().filter(mapEntry -> mapEntry.getKey() != memory).map(it -> it.getValue())
            .flatMap(it -> it.stream()).collect(Collectors.toList());
        // remove mobj that are duplicated in the current memory from this list
        mObjInOtherMem.removeAll(mobjByBank.get(memory));

        mObjectsToRemove.addAll(mObjInOtherMem);

      } else {
        // Check that this unique bank is the current one, otherwise,
        // something went wrong or this and/or this meg is not the
        // result of a call to distributeMegDistributedOnly method
        String firstObj = mobjByBank.keySet().iterator().next();
        if (firstObj != memory) {
          throw new RuntimeException("Merged memory objects " + mobjByBank.values() + " should not be allocated in memory bank " + firstObj + " but in memory "
              + memory + " instead.");
        }
      }
    }

    // Remove all mObjects from other banks
    meg.deepRemoveAllVertices(mObjectsToRemove);
  }

  /**
   * Finds the {@link MemoryExclusionVertex} in the given {@link MemoryExclusionGraph} that result from a merge operation of the memory scripts, and split them
   * into several memory objects according to the Mixed distribution policy (see {@link AbstractMemoryAllocatorTask} parameters).<br>
   * <b>This method modifies the {@link MemoryExclusionGraph} passed as a parameter.</b>
   *
   * @param meg
   *          {@link MemoryExclusionGraph} whose {@link MemoryExclusionVertex} are processed. This graph will be modified by the method.
   *
   * @param alignment
   *          This property is used to represent the alignment of buffers in memory. The same value, or a multiple should always be used in the memory
   *          allocation.
   *
   */
  protected static void splitMergedBuffersMixed(MemoryExclusionGraph meg, int alignment) {
    // Get the map of host Mobjects
    // (A copy of the map is used because the original map will be modified during iterations)
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) meg.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

    // Exit method if no host Mobjects can be found in this MEG
    if (hosts == null || hosts.isEmpty()) {
      return;
    }

    // Immutable copy for iteration purposes
    // Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostsCopy = hosts.immutableCopy;
    // removed by ahonorat, is that really needed to iterate over it?

    // Iterate over the Host MObjects of the MEG
    for (Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : hosts.entrySet()) {
      // Create a group of MObject for each memory similarly to what is done
      // in distributeMeg method.
      // Map storing the groups of Mobjs
      Map<String, Set<MemoryExclusionVertex>> mobjByBank = new LinkedHashMap<>();

      // Iteration List including the host Mobj
      findMObjBankMixed(entry.getKey(), mobjByBank);
      for (MemoryExclusionVertex mobj : entry.getValue()) {
        findMObjBankMixed(mobj, mobjByBank);
      }

      // If only one bank is used for all MObjs of this host,
      // skip the split operation as it is useless (and may induce extra
      // bytes for alignment reasons)
      if (mobjByBank.size() != 1) {

        // Create the reverse bankByMobj map
        splitMergedBuffers(mobjByBank, mobjByBank.keySet(), entry, meg, alignment);
      }
    }
  }

  /**
   * Split the host {@link MemoryExclusionVertex} from the {@link MemoryExclusionGraph}.
   *
   * @param mobjByBank
   *          {@link Map} of {@link String} and {@link Set} of {@link MemoryExclusionVertex}. Each memory bank (the {@link String}) is associated to the
   *          {@link Set} of {@link MemoryExclusionVertex} that is to be allocated in this bank.
   * @param banks
   *          {@link Set} of {@link String} representing the bank to process. This must be a sub-set of the keys of the mobjByBank param.
   * @param hosts
   *          {@link MemoryExclusionVertex#HOST_MEMORY_OBJECT_PROPERTY} from the given {@link MemoryExclusionGraph}.
   * @param entry
   *          {@link Entry} copied from an immutable copy of the {@link MemoryExclusionVertex#HOST_MEMORY_OBJECT_PROPERTY} of the given
   *          {@link MemoryExclusionGraph}. This {@link Entry} is the only one to be processed when calling this method. (i.e. the only one that is split.)
   * @param meg
   *          {@link MemoryExclusionGraph} whose {@link MemoryExclusionVertex} are processed. This graph will be modified by the method.
   * @param alignment
   *          This property is used to represent the alignment of buffers in memory. The same value, or a multiple should always be used in the memory
   *          allocation.
   */
  protected static void splitMergedBuffers(Map<String, Set<MemoryExclusionVertex>> mobjByBank, Set<String> banks,
      Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry, MemoryExclusionGraph meg, int alignment) {
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) meg.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

    LinkedHashMap<MemoryExclusionVertex, String> bankByMobj = new LinkedHashMap<>();
    mobjByBank.forEach((bank, mobjs) -> {
      mobjs.forEach((megVertex) -> bankByMobj.put(megVertex, bank));
    });

    // Set of all the divided memory objects that can not be divided
    // because they are matched in several banks.
    Set<MemoryExclusionVertex> mObjsToUndivide = new LinkedHashSet();

    // Remove information of the current host from the MEG
    // This is safe since a copy of the hosts is used for iteration
    hosts.remove(entry.getKey());
    meg.removeVertex(entry.getKey());
    Range realRange = ((List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) entry.getKey().getPropertyBean()
        .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY)).get(0).getValue().getValue();
    entry.getKey().setWeight(realRange.getLength());

    // Put the real range in the same referential as other ranges (cf REAL_TOKEN_RANGE_PROPERTY comments)
    realRange.translate(-realRange.getStart());

    // For each bank, create as many hosts as the number of
    // non-contiguous ranges formed by the memory objects falling
    // into this memory bank.
    LinkedHashSet<MemoryExclusionVertex> newHostsMObjs = new LinkedHashSet<>();
    for (Entry<String, Set<MemoryExclusionVertex>> bankEntry : mobjByBank.entrySet().stream().filter(mapEntry -> banks.contains(mapEntry.getKey()))
        .collect(Collectors.toList())) {
      // Iterate over Mobjects to build the range(s) of this memory
      // bank (all ranges are relative to the host mObj)
      List<Range> rangesInBank = new ArrayList<>();
      for (MemoryExclusionVertex mobj : bankEntry.getValue()) {
        List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> rangesInHost = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) mobj.getPropertyBean()
            .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
        // Process non-divided buffers
        if (rangesInHost.size() == 1) {
          // add the range covered by this buffer to the
          // rangesInBank (use a clone because the range will be
          // modified during call to the union method
          Range.union(rangesInBank, (Range) rangesInHost.get(0).getValue().getValue().clone());
        } else {
          // Divided buffers:
          // Check if all parts of the MObj were allocated in the same memory bank
          // i.e. check if source and dest memory objects are
          // all stored in the same memory bank
          List<MemoryExclusionVertex> dividedPartsHosts = (List<MemoryExclusionVertex>) mobj.getPropertyBean()
              .getValue(MemoryExclusionVertex.DIVIDED_PARTS_HOSTS);
          List<String> partsHostsSet = dividedPartsHosts.stream().map(it -> bankByMobj.get(it)).collect(Collectors.toList());
          if (partsHostsSet.size() == 1 && partsHostsSet.get(0).equals(bankEntry.getKey())) {
            // All hosts were allocated in the same bank
            // And this bank is the current bankEntry
            // The split is maintained, and rangesInHost must be updated
            // (use a clone because the range will be
            // modified during call to the union method)
            rangesInHost.forEach((range) -> Range.union(rangesInBank, (Range) range.getValue().getValue().clone()));
          } else {
            // Not all hosts were allocated in the same bank
            // The mObj cannot be splitted
            // => It must be restored to its original size in
            // the graph
            mObjsToUndivide.add(mobj);
          }
        }
      }

      // Find the list of mObjs falling in each range
      // Store the result in the rangesInBankAndMObjs map
      // mObjsToUndivide are not part of these memory objects
      Set<MemoryExclusionVertex> mObjInCurrentBank = mobjByBank.get(bankEntry.getKey());
      mObjInCurrentBank.removeAll(mObjsToUndivide);
      Map<Range, List<MemoryExclusionVertex>> rangesInBankAndMObjs = new LinkedHashMap<>();
      for (Range currentRange : rangesInBank) {
        // 1. Get the list of mObjects falling in this range
        List<MemoryExclusionVertex> mObjInCurrentRange = new ArrayList<>();
        for (MemoryExclusionVertex mObj : mObjInCurrentBank) {
          List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> ranges = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) mObj.getPropertyBean()
              .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
          if (ranges.size() == 1) {
            // Buffer is undivided, check if it intersect with
            // the current range in bank
            if (ranges.get(0).getValue().getValue().intersection(currentRange) != null) {
              // Add undivided object at the beginning of the list
              // to make sure that no divided object will ever be selected
              // as a host in the next step
              mObjInCurrentRange.add(0, mObj);
            }
          } else {
            // Buffer is divided, check if *any* of its range
            // intersects with the current range in bank
            // (i.e. check if *not* none of its range intersect with the range)
            if (ranges.stream().anyMatch(range -> range.getValue().getValue().intersection(currentRange) != null)) {
              // Add divided object at the end of the list
              // to make sure that no divided object will ever be selected
              // as a host in the next step
              mObjInCurrentRange.add(mObj);
            }
          }
        }
        rangesInBankAndMObjs.put(currentRange, mObjInCurrentRange);
      }

      // Create a memory object for each range in the bank
      for (Entry<Range, List<MemoryExclusionVertex>> currentRangeAndMobjs : rangesInBankAndMObjs.entrySet()) {
        Range currentRange = currentRangeAndMobjs.getKey();
        List<MemoryExclusionVertex> mObjInCurrentRange = currentRangeAndMobjs.getValue();

        // 1. Select the first object as the new host
        // (cannot be a divided mObject as divided mObjects were
        // always added at the end of the list)
        MemoryExclusionVertex newHostMobj = mObjInCurrentRange.get(0);
        newHostsMObjs.add(newHostMobj);

        // 2. Give the new host the right size
        // (pay attention to alignment)
        // Get aligned min index range
        List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> newHostOldRange = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) newHostMobj
            .getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
        int minIndex = currentRange.getStart();
        if (alignment > 0) {
          // Make sure that index aligned in the buffer are in
          // fact aligned.
          // This goal is here to make sure that
          // index 0 of the new host buffer is aligned !
          int newHostOldStart = newHostOldRange.get(0).getValue().getValue().getStart(); // .value.value
          // If the result of te modulo is not null, unaligned
          // corresponds to the number of "extra" bytes making
          // index 0 of newHost not aligned with respect to
          // currentRangeStart
          int unaligned = (newHostOldStart - currentRange.getStart()) % alignment;
          if (unaligned == 0) {
            // Index 0 of new host is already aligned
            minIndex = currentRange.getStart();
          } else {
            // Index 0 of new host is not aligned
            // Extra-bytes are added to the new range
            // to re-align it.
            minIndex = currentRange.getStart() - (alignment - unaligned);
          }
        }
        newHostMobj.setWeight(currentRange.getEnd() - minIndex);

        if (mObjInCurrentRange.size() <= 1) {
          // The new host Mobj does not host any other MObj
          // last code was still applied to make sure that
          // the mObject has the right size (although it
          // will never be larger than its original weight
          // and never by misaligned as it does not not
          // host anything)
          newHostMobj.getPropertyBean().removeProperty(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
        } else {
          // The remaining processing should only be applied if
          // the mObject is not alone in its range and does
          // actually host other mObjects
          // Update newHostMobj realTokenRange property
          Range newHostRealTokenRange = newHostOldRange.get(0).getValue().getKey();
          Range newHostActualRealTokenRange = newHostOldRange.get(0).getValue().getValue().translate(-minIndex);
          List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> ranges = new ArrayList<>();
          ranges.add(new Pair<>(newHostMobj, new Pair<>(newHostRealTokenRange, newHostActualRealTokenRange)));
          newHostMobj.setPropertyValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, ranges);

          // Add the mobj to the meg hosts list
          Set<MemoryExclusionVertex> hostMObjSet = new LinkedHashSet<>();
          hosts.put(newHostMobj, hostMObjSet);

          // 3. Add all hosted mObjects to the list
          // and set their properties
          // (exclude first mObj from iteration, as it is the
          // new host)
          for (MemoryExclusionVertex mObj : mObjInCurrentRange.subList(1, mObjInCurrentRange.size())) {
            // update the real token range property by translating
            // ranges to the current range referential
            List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> mObjRanges = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) mObj.getPropertyBean()
                .getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
            List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> mObjNewRanges = new ArrayList<>();
            for (Pair<MemoryExclusionVertex, Pair<Range, Range>> mObjRangePair : mObjRanges) {
              // Check if the current mObjRangePair overlaps with
              // the current range.
              // Always OK for undivided buffers
              // If a divided buffer is mapped into several
              // hosts, this code makes sure that each mapped
              // ranged is updated only when the corresponding
              // range is processed.
              if (Range.hasOverlap(mObjRangePair.getValue().getValue(), currentRange)) {
                // case for Undivided buffer and divided
                // range falling into the current range
                mObjNewRanges.add(new Pair<>(newHostMobj, new Pair<>(mObjRangePair.getValue().getKey(),
                    mObjRangePair.getValue().getValue().translate(-minIndex - newHostActualRealTokenRange.getStart()))));
              } else {
                // Case for divided range not falling into
                // the current range
                mObjNewRanges.add(mObjRangePair);
              }
            }

            // Save property and update hostMObjSet
            mObj.getPropertyBean().setValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, mObjNewRanges);
            hostMObjSet.add(mObj);
          }
        }
      }
    }

    // Add the new host MObjects to the MEG
    // and add exclusion of the host
    for (MemoryExclusionVertex newHostMobj : newHostsMObjs) {
      // Add new host to the MEG
      meg.addVertex(newHostMobj);
      // Add exclusions
      restoreExclusions(meg, newHostMobj);
    }

    if (logger != null && !mObjsToUndivide.isEmpty()) {
      logger.log(Level.WARNING,
          "The following divided memory object " + mObjsToUndivide + " are unified again during the memory distribution process.\n"
              + "This unification was applied because divided memory objects cannot be merged in several distinct memories.\n"
              + "Deactivating memory script causing this division may lead to lower memory footprints in this distributed memory context.");
    }

    // Process the mObjects to "undivide".
    for (MemoryExclusionVertex mObj : mObjsToUndivide) {

      // Remove the Mobject from the MEG HOST_MEM_OBJ property
      // Already done when host are removed from HOST_MEM_OBJ in previous loop
      // val hostMemObjProp = meg.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY) as Map<MemoryExclusionVertex,
      // Set<MemoryExclusionVertex>>
      // hostMemObjProp.forEach[hostMObj, hostedMobjs|hostedMobjs.remove(mObj)]
      // Add the mobj back to the Meg
      meg.addVertex(mObj);

      // Restore original exclusions of the mobj
      restoreExclusions(meg, mObj);
    }
  }

  /**
   * Restore exclusions in the {@link MemoryExclusionGraph MEG} for the given {@link MemoryExclusionVertex}, (and its hosted {@link MemoryExclusionVertex}, if
   * any).
   * <p>
   * <b>Method called by {@link #splitMergedBuffers(String, MemoryExclusionGraph, int)} only.</b>
   *
   * @param meg
   *          the {@link MemoryExclusionGraph} to which the exclusion are to be added.
   * @param mObj
   *          the {@link MemoryExclusionVertex} whose exclusions are restored (may be a host vertex).
   */
  protected static void restoreExclusions(MemoryExclusionGraph meg, MemoryExclusionVertex mObj) {
    // Get the hosts property of the MEG
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) meg.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);

    // iteration over the host and its hosted vertices, if any
    Set<MemoryExclusionVertex> mObjAndHostedMObjs = hosts.get(mObj);
    if (mObjAndHostedMObjs == null) {
      mObjAndHostedMObjs = new LinkedHashSet<>();
    }
    mObjAndHostedMObjs.add(mObj);

    for (MemoryExclusionVertex curentMObj : mObjAndHostedMObjs) {
      List<MemoryExclusionVertex> adjacentMObjs = (List<MemoryExclusionVertex>) curentMObj.getPropertyBean()
          .getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
      for (MemoryExclusionVertex adjacentMObj : adjacentMObjs) {
        // Check if the adjacent mObj is already in the graph
        if (!adjacentMObj.equals(mObj) // No self-exclusion
            && meg.vertexSet().contains(adjacentMObj)) {
          // the adjacent mObj is already in the graph
          // Add the exclusion back
          meg.addEdge(mObj, adjacentMObj);
        } else {
          // Happens if adjacentMObj is:
          // - a hosted MObj
          // - a mobjToUndivide (not yet added back to the graph)
          // The adjacent mObj is not in the graph
          // It must be merged within a host
          // (or several host in case of a division)
          for (Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : hosts.entrySet()) {
            MemoryExclusionVertex hostMObj = entry.getKey();
            Set<MemoryExclusionVertex> hostedMObjs = entry.getValue();
            if (hostMObj != mObj // No self-exclusion
                && hostedMObjs.contains(adjacentMObj) // Does the tested host contain the adjacentMObj
                && meg.containsVertex(hostMObj) // If hostMobj was already added to the MEG
            // && !meg.containsEdge(mObj,hostMObj) // Exclusion does not already exists
            ) {
              meg.addEdge(mObj, hostMObj);
            }
          }
          // If the adjacent mObj was not yet added back to the MEG,
          // this forEach will have no impact, but edges will be
          // created on processing of this adjacent mObjs in
          // a following iteration of the current for loop.
        }
      }
    }
  }

  /**
   * Method used to separate the {@link MemoryExclusionVertex} of a {@link MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the SHARED_ONLY
   * policy (see {@link AbstractMemoryAllocatorTask} parameters). <br>
   * <br>
   * With this policy, all {@link MemoryExclusionVertex} are put in a single Shared memory.
   *
   * @param memEx
   *          The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegSharedOnly(MemoryExclusionGraph memEx) {
    LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<>();
    memExesVerticesSet.put("Shared", new LinkedHashSet(memEx.vertexSet()));
    return memExesVerticesSet;
  }

  /**
   * Method used to separate the {@link MemoryExclusionVertex} of a {@link MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the
   * DISTRIBUTED_ONLY policy (see {@link AbstractMemoryAllocatorTask} parameters). <br>
   * <br>
   * With this policy, each {@link MemoryExclusionVertex} is put in as many memory banks as the number of processing elements accessing it during an iteration
   * of the original dataflow graph.
   *
   * @param memEx
   *          The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegDistributedOnly(MemoryExclusionGraph memEx) {
    LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<>();
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) memEx.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    for (MemoryExclusionVertex memExVertex : memEx.vertexSet()) {
      Set<MemoryExclusionVertex> hostedMObjs = null;
      if (hosts != null) {
        hostedMObjs = hosts.get(memExVertex);
      }

      // For source then sink of DAG edge corresponding to the memex
      // vertex
      findMObjBankDistributedOnly(memExVertex, memExesVerticesSet, memEx);

      // special processing for host Mobj
      if (hostedMObjs != null) {
        // Create a fake map that will store the theoretical banks of all
        // hosted mObjects
        LinkedHashMap<String, Set<MemoryExclusionVertex>> hostedMObjsBanks = new LinkedHashMap<>();
        for (MemoryExclusionVertex hostedMobj : hostedMObjs) {
          findMObjBankDistributedOnly(hostedMobj, hostedMObjsBanks, memEx);
        }

        // Add the banks for the hosted MObjs (the split of hosted mObj will
        // be done later, after duplication of the MEG to avoid having the
        // same MObj several time in a pre-duplication MEG)
        for (String bank : hostedMObjsBanks.keySet()) {
          Set<MemoryExclusionVertex> verticesSet = memExesVerticesSet.get(bank);
          if (verticesSet == null) {
            // If the component is not yet in the map, add it
            verticesSet = new LinkedHashSet<>();
            memExesVerticesSet.put(bank, verticesSet);
          }
          // Add the memEx Vertex to the set of vertex of the
          // component
          verticesSet.add(memExVertex);
        }
      }
    }
    return memExesVerticesSet;
  }

  /**
   * The purpose of this method is to find the bank associated to a given {@link MemoryExclusionVertex} according to the DISTRIBUTED_ONLY distribution policy.
   * The mObj is put in the {@link Map} passed as a parameter where keys are the names of the memory banks and values are the {@link Set} of associated
   * {@link MemoryExclusionVertex}.
   *
   * @param mObj
   *          The {@link MemoryExclusionVertex} whose memory banks are identified.
   * @param mObjByBank
   *          The {@link Map} in which results of this method are put.
   * @param memEx
   *          The {@link MemoryExclusionGraph} whose vertices are allocated. (only used to retrieved the corresponding {@link DirectedAcyclicGraph}).
   */
  protected static void findMObjBankDistributedOnly(MemoryExclusionVertex mObj, Map<String, Set<MemoryExclusionVertex>> mObjByBank,
      MemoryExclusionGraph memEx) {
    // Process the given mObj
    for (int i = 0; i < 2; i++) {
      // Retrieve the component on which the DAG Vertex is mapped
      DAGEdge edge = mObj.getEdge();
      DAGVertex dagVertex;
      if (edge != null) {
        if (i == 0) {
          dagVertex = edge.getSource();
        } else {
          dagVertex = edge.getTarget();
        }
      } else {
        // retrieve
        DirectedAcyclicGraph dag = (DirectedAcyclicGraph) memEx.getPropertyBean().getValue(MemoryExclusionGraph.SOURCE_DAG);
        if (i == 0) {
          dagVertex = dag.getVertex(mObj.getSource().substring(("FIFO_Head_").length()));
        } else {
          dagVertex = dag.getVertex(mObj.getSink());
        }
      }

      ComponentInstance component = (ComponentInstance) dagVertex.getPropertyBean().getValue("Operator");

      Set<MemoryExclusionVertex> verticesSet = mObjByBank.get(component.getInstanceName());
      if (verticesSet == null) {
        // If the component is not yet in the map, add it
        verticesSet = new LinkedHashSet<>();
        mObjByBank.put(component.getInstanceName(), verticesSet);
      }

      // Add the memEx Vertex to the set of vertex of the
      // component
      verticesSet.add(mObj);
    }
  }

  /**
   * Method used to separate the {@link MemoryExclusionVertex} of a {@link MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the MIXED
   * policy (see {@link AbstractMemoryAllocatorTask} parameters). <br>
   * <br>
   * With this policy, each {@link MemoryExclusionVertex} is put
   * <ul>
   * <li>in the memory banks of a processing elements if it is the only PE accessing it during an iteration of the original dataflow graph.</li>
   * <li>in shared memory otherwise (i.e. if multiple PE access this memory object during a graph iteration).</li>
   * </ul>
   *
   * @param memEx
   *          The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegMixed(MemoryExclusionGraph memEx) {
    LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<>();
    for (MemoryExclusionVertex memExVertex : memEx.vertexSet()) {
      findMObjBankMixed(memExVertex, memExesVerticesSet);
    }
    return memExesVerticesSet;
  }

  /**
   * Method used to separate the {@link MemoryExclusionVertex} of a {@link MemoryExclusionGraph} into {@link LinkedHashSet subsets} according to the
   * MIXED_MERGED policy (see {@link AbstractMemoryAllocatorTask} parameters). <br>
   * <br>
   * With this policy, each {@link MemoryExclusionVertex} is put
   * <ul>
   * <li>in the memory banks of a processing elements if it is the only PE accessing it during an iteration of the original dataflow graph.</li>
   * <li>in shared memory if it is a merged buffer, unless all mObjects of the merged buffer fall in the same memory bank.</li>
   * <li>in shared memory otherwise (i.e. if multiple PE access this memory object during a graph iteration).</li>
   * </ul>
   *
   * @param memEx
   *          The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegMixedMerged(MemoryExclusionGraph memEx) {
    LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<>();
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) memEx.getPropertyBean()
        .getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    for (MemoryExclusionVertex memExVertex : memEx.vertexSet()) {
      if (hosts != null && !hosts.containsKey(memExVertex) || hosts == null) {
        findMObjBankMixed(memExVertex, memExesVerticesSet);
      } else {
        // Check if all hosted mObjects fall in the same bank
        LinkedHashMap<String, Set<MemoryExclusionVertex>> mobjByBank = new LinkedHashMap<>();

        // Find the bank for each mObj of the group
        findMObjBankMixed(memExVertex, mobjByBank);
        for (MemoryExclusionVertex mobj : hosts.get(memExVertex)) {
          findMObjBankMixed(mobj, mobjByBank);
        }

        // The bank is, the first if all mObjects fall in the same bank
        // Shared memory otherwise
        String bank = "Shared";
        if (mobjByBank.size() == 1) {
          Iterator<String> it = mobjByBank.keySet().iterator();
          bank = it.next();
        }
        // Put the mObj in the verticesSet
        Set<MemoryExclusionVertex> verticesSet = memExesVerticesSet.get(bank);
        if (verticesSet == null) {
          // If the component is not yet in the map, add it
          verticesSet = new LinkedHashSet<>();
          memExesVerticesSet.put(bank, verticesSet);
        }
        verticesSet.add(memExVertex);
      }
    }
    return memExesVerticesSet;
  }

  /**
   * The purpose of this method is to find the bank associated to a given {@link MemoryExclusionVertex} according to the MIXED distribution policy. The mObj is
   * put in the {@link Map} passed as a parameter where keys are the names of the memory banks and values are the {@link Set} of associated
   * {@link MemoryExclusionVertex}.
   *
   * @param mObj
   *          The {@link MemoryExclusionVertex} whose memory banks are identified.
   * @param mObjByBank
   *          The {@link Map} in which results of this method are put.
   */
  protected static void findMObjBankMixed(MemoryExclusionVertex mObj, Map<String, Set<MemoryExclusionVertex>> mObjByBank) {
    String memory = "Shared";

    // If dag edge source and target are mapped to the same
    // component
    if (mObj.getEdge() != null) {
      // If source and target are mapped to te same core
      if (mObj.getEdge().getSource().getPropertyBean().getValue("Operator").equals(mObj.getEdge().getTarget().getPropertyBean().getValue("Operator"))) {
        DAGVertex dagVertex = mObj.getEdge().getSource();
        ComponentInstance component = (ComponentInstance) dagVertex.getPropertyBean().getValue("Operator");
        memory = component.getInstanceName();
      } // Else => Shared memory
    } else {
      // The MObject is not associated to a DAGEdge
      // It is either a FIFO_head/body or working memory
      // For now these mobjects are put in shared memory
      // The MObject is not associated to a DAGEdge
      // It is either a FIFO_head/body or working memory
      // If this is a working memory object
      // logger.log(Level.INFO, "findMObjBankMixed sink " + mObj.sink + " source " + mObj.source)
      if (mObj.getSource().equals(mObj.getSink())) {
        // This is a wMem mObj
        // The mObj is allocated in the MEG of the core executing the corresponding actor.
        ComponentInstance component = (ComponentInstance) mObj.getVertex().getPropertyBean().getValue("Operator");
        memory = component.getInstanceName();
      } else {
        // For now fifos are allocated in shared memory
      }
    }

    Set<MemoryExclusionVertex> verticesSet = mObjByBank.get(memory);
    if (verticesSet == null) {
      // If the component is not yet in the map, add it
      verticesSet = new LinkedHashSet<>();
      mObjByBank.put(memory, verticesSet);
    }

    // Add the memEx Vertex to the set of vertex of the
    // component
    verticesSet.add(mObj);
  }

}
