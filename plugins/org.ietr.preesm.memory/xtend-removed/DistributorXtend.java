/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Generated;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.MapExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.ietr.preesm.memory.script.Range;

/**
 * This class contains all the code responsible for splitting a {@link
 * MemoryExclusionGraph} into several graphs, each corresponding to a specific
 * memory bank.
 */
@SuppressWarnings("unchecked")
@Generated(value = "org.eclipse.xtend.core.compiler.XtendGenerator", date = "2018-01-08T16:03+0100")
public class Distributor {
  /**
   * {@link Logger} used to provide feedback on the distribution to the developer.
   */
  @Accessors
  protected static Logger logger;
  
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
   *       {@link String} containing the selected policy for the
   *       distribution of {@link MemoryExclusionVertex} in separate
   *       memory banks. (see {@link
   *       AbstractMemoryAllocatorTask} parameters.)
   * @param memEx
   *       The processed {@link MemoryExclusionGraph}
   * 
   * @param alignment
   *       This property is used to represent the alignment of buffers
   *       in memory. The same value, or a multiple should always be
   *       used in the memory allocation.
   * @return
   *  A {@link Map} of {@link String} and {@link
   *    MemoryExclusionGraph}. Each {@link String} identifies a memory
   *    banks in which the associated {@link MemoryExclusionGraph} should
   *    be allocated.
   * 
   * @throws RuntimeException
   *    Currently thrown <ul>
   *    <li>if the distributed_only policy is applied to a
   *    graph containing feedback FIFOs.</li>
   *    <li>if an unknown distribution policy is selected.</li>
   *    </ul>
   */
  public static Map<String, MemoryExclusionGraph> distributeMeg(final String valuePolicy, final MemoryExclusionGraph memEx, final int alignment) throws RuntimeException {
    Map<String, MemoryExclusionGraph> memExes = new LinkedHashMap<String, MemoryExclusionGraph>();
    Map<String, Set<MemoryExclusionVertex>> _switchResult = null;
    if (valuePolicy != null) {
      switch (valuePolicy) {
        case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED:
          Map<String, Set<MemoryExclusionVertex>> _xblockexpression = null;
          {
            Distributor.splitMergedBuffersMixed(memEx, alignment);
            _xblockexpression = Distributor.distributeMegMixed(memEx);
          }
          _switchResult = _xblockexpression;
          break;
        case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED_MERGED:
          _switchResult = Distributor.distributeMegMixedMerged(memEx);
          break;
        case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY:
          _switchResult = Distributor.distributeMegDistributedOnly(memEx);
          break;
        case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY:
        case AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT:
          _switchResult = Distributor.distributeMegSharedOnly(memEx);
          break;
        default:
          throw new RuntimeException(
            ((("Unexpected distribution policy: " + valuePolicy) + ".\n Allowed values are ") + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT));
      }
    } else {
      throw new RuntimeException(
        ((("Unexpected distribution policy: " + valuePolicy) + ".\n Allowed values are ") + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT));
    }
    Map<String, Set<MemoryExclusionVertex>> memExesVerticesSet = _switchResult;
    Object _value = memEx.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = ((Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) _value);
    if ((hosts != null)) {
      Set<Map.Entry<String, Set<MemoryExclusionVertex>>> _entrySet = memExesVerticesSet.entrySet();
      for (final Map.Entry<String, Set<MemoryExclusionVertex>> entry : _entrySet) {
        final Function2<MemoryExclusionVertex, Set<MemoryExclusionVertex>, Boolean> _function = (MemoryExclusionVertex host, Set<MemoryExclusionVertex> hosted) -> {
          return Boolean.valueOf(entry.getValue().contains(host));
        };
        Iterables.<MemoryExclusionVertex>addAll(entry.getValue(), Iterables.<MemoryExclusionVertex>concat(MapExtensions.<MemoryExclusionVertex, Set<MemoryExclusionVertex>>filter(hosts, _function).values()));
      }
    }
    Set<String> _keySet = memExesVerticesSet.keySet();
    for (final String memory : _keySet) {
      {
        MemoryExclusionGraph copiedMemEx = memEx.deepClone();
        Set<MemoryExclusionVertex> _totalSetOfVertices = copiedMemEx.getTotalSetOfVertices();
        LinkedHashSet<MemoryExclusionVertex> verticesToRemove = new LinkedHashSet<MemoryExclusionVertex>(_totalSetOfVertices);
        verticesToRemove.removeAll(memExesVerticesSet.get(memory));
        copiedMemEx.deepRemoveAllVertices(verticesToRemove);
        boolean _equals = Objects.equal(valuePolicy, AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY);
        if (_equals) {
          Distributor.splitMergedBuffersDistributedOnly(copiedMemEx, alignment, memory);
        }
        memExes.put(memory, copiedMemEx);
      }
    }
    return memExes;
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
   *      {@link MemoryExclusionGraph} whose {@link
   *      MemoryExclusionVertex} are processed. This graph will be
   *      modified by the method.
   * 
   * @param alignment
   *       This property is used to represent the alignment of buffers
   *       in memory. The same value, or a multiple should always be
   *       used in the memory allocation.
   * 
   * @param memory
   *      Contains the name of the component to which the given {@link
   *      MemoryExclusionGraph} is associated. Only merged {@link
   *      MemoryExclusionVertex} accessed by this core will be kept in
   *      the {@link MemoryExclusionGraph} when the split is applied.
   *      (others will be removed).
   */
  protected static void splitMergedBuffersDistributedOnly(final MemoryExclusionGraph meg, final int alignment, final String memory) {
    Object _value = meg.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = ((Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) _value);
    if (((hosts == null) || hosts.isEmpty())) {
      return;
    }
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostsCopy = ImmutableMap.<MemoryExclusionVertex, Set<MemoryExclusionVertex>>copyOf(hosts);
    final LinkedHashSet<MemoryExclusionVertex> mObjectsToRemove = new LinkedHashSet<MemoryExclusionVertex>();
    Set<Map.Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>>> _entrySet = hostsCopy.entrySet();
    for (final Map.Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : _entrySet) {
      {
        final LinkedHashMap<String, Set<MemoryExclusionVertex>> mobjByBank = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
        MemoryExclusionVertex _key = entry.getKey();
        Set<MemoryExclusionVertex> _value_1 = entry.getValue();
        Iterable<MemoryExclusionVertex> _plus = Iterables.<MemoryExclusionVertex>concat(Collections.<MemoryExclusionVertex>unmodifiableList(CollectionLiterals.<MemoryExclusionVertex>newArrayList(_key)), _value_1);
        for (final MemoryExclusionVertex mobj : _plus) {
          Distributor.findMObjBankDistributedOnly(mobj, mobjByBank, meg);
        }
        int _size = mobjByBank.size();
        boolean _notEquals = (_size != 1);
        if (_notEquals) {
          Distributor.splitMergedBuffers(mobjByBank, Collections.<String>unmodifiableSet(CollectionLiterals.<String>newLinkedHashSet(memory)), entry, meg, alignment);
          final Function2<String, Set<MemoryExclusionVertex>, Boolean> _function = (String bank, Set<MemoryExclusionVertex> mObjects) -> {
            return Boolean.valueOf((!Objects.equal(bank, memory)));
          };
          final List<MemoryExclusionVertex> mObjInOtherMem = IterableExtensions.<MemoryExclusionVertex>toList(Iterables.<MemoryExclusionVertex>concat(MapExtensions.<String, Set<MemoryExclusionVertex>>filter(mobjByBank, _function).values()));
          mObjInOtherMem.removeAll(mobjByBank.get(memory));
          mObjectsToRemove.addAll(mObjInOtherMem);
        } else {
          String _get = ((String[])Conversions.unwrapArray(mobjByBank.keySet(), String.class))[0];
          boolean _notEquals_1 = (!Objects.equal(_get, memory));
          if (_notEquals_1) {
            Collection<Set<MemoryExclusionVertex>> _values = mobjByBank.values();
            String _plus_1 = ("Merged memory objects " + _values);
            String _plus_2 = (_plus_1 + " should not be allocated in memory bank ");
            String _get_1 = ((String[])Conversions.unwrapArray(mobjByBank.keySet(), String.class))[0];
            String _plus_3 = (_plus_2 + _get_1);
            String _plus_4 = (_plus_3 + " but in memory ");
            String _plus_5 = (_plus_4 + memory);
            String _plus_6 = (_plus_5 + " instead.");
            throw new RuntimeException(_plus_6);
          }
        }
      }
    }
    meg.deepRemoveAllVertices(mObjectsToRemove);
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
   *      {@link MemoryExclusionGraph} whose {@link
   *      MemoryExclusionVertex} are processed. This graph will be
   *      modified by the method.
   * 
   * @param alignment
   *       This property is used to represent the alignment of buffers
   *       in memory. The same value, or a multiple should always be
   *       used in the memory allocation.
   */
  protected static void splitMergedBuffersMixed(final MemoryExclusionGraph meg, final int alignment) {
    Object _value = meg.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = ((Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) _value);
    if (((hosts == null) || hosts.isEmpty())) {
      return;
    }
    Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostsCopy = ImmutableMap.<MemoryExclusionVertex, Set<MemoryExclusionVertex>>copyOf(hosts);
    Set<Map.Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>>> _entrySet = hostsCopy.entrySet();
    for (final Map.Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : _entrySet) {
      {
        final LinkedHashMap<String, Set<MemoryExclusionVertex>> mobjByBank = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
        MemoryExclusionVertex _key = entry.getKey();
        Set<MemoryExclusionVertex> _value_1 = entry.getValue();
        Iterable<MemoryExclusionVertex> _plus = Iterables.<MemoryExclusionVertex>concat(Collections.<MemoryExclusionVertex>unmodifiableList(CollectionLiterals.<MemoryExclusionVertex>newArrayList(_key)), _value_1);
        for (final MemoryExclusionVertex mobj : _plus) {
          Distributor.findMObjBankMixed(mobj, mobjByBank);
        }
        int _size = mobjByBank.size();
        boolean _notEquals = (_size != 1);
        if (_notEquals) {
          Distributor.splitMergedBuffers(mobjByBank, mobjByBank.keySet(), entry, meg, alignment);
        }
      }
    }
  }
  
  /**
   * Split the host {@link MemoryExclusionVertex} from the {@link
   * MemoryExclusionGraph}.
   * 
   * @param mobjByBank
   *    {@link Map} of {@link String} and {@link Set} of {@link
   *    MemoryExclusionVertex}. Each memory bank (the {@link String}) is
   *    associated to the {@link Set} of {@link MemoryExclusionVertex}
   *    that is to be allocated in this bank.
   * @param banks
   *    {@link Set} of {@link String} representing the bank to process.
   *    This must be a sub-set of the keys of the mobjByBank param.
   * @param hosts
   *    {@link MemoryExclusionVertex#HOST_MEMORY_OBJECT_PROPERTY} from the
   *    given {@link MemoryExclusionGraph}.
   * @param entry
   *    {@link Entry} copied from an immutable copy of the {@link
   *    MemoryExclusionVertex#HOST_MEMORY_OBJECT_PROPERTY} of the given {@link
   *    MemoryExclusionGraph}.
   *    This {@link Entry} is the only one to be processed when calling this
   *    method. (i.e. the only one that is split.)
   * @param meg
   *    {@link MemoryExclusionGraph} whose {@link
   *    MemoryExclusionVertex} are processed. This graph will be
   *    modified by the method.
   * @param alignment
   *    This property is used to represent the alignment of buffers
   *    in memory. The same value, or a multiple should always be
   *    used in the memory allocation.
   */
  protected static void splitMergedBuffers(final Map<String, Set<MemoryExclusionVertex>> mobjByBank, final Set<String> banks, final Map.Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry, final MemoryExclusionGraph meg, final int alignment) {
    Object _value = meg.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = ((Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) _value);
    final LinkedHashMap<MemoryExclusionVertex, String> bankByMobj = new LinkedHashMap<MemoryExclusionVertex, String>();
    final BiConsumer<String, Set<MemoryExclusionVertex>> _function = (String bank, Set<MemoryExclusionVertex> mobjs) -> {
      final Consumer<MemoryExclusionVertex> _function_1 = (MemoryExclusionVertex it) -> {
        bankByMobj.put(it, bank);
      };
      mobjs.forEach(_function_1);
    };
    mobjByBank.forEach(_function);
    final LinkedHashSet<MemoryExclusionVertex> mObjsToUndivide = CollectionLiterals.<MemoryExclusionVertex>newLinkedHashSet();
    hosts.remove(entry.getKey());
    meg.removeVertex(entry.getKey());
    Object _value_1 = entry.getKey().getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
    final Range realRange = ((List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) _value_1).get(0).getValue().getValue();
    MemoryExclusionVertex _key = entry.getKey();
    _key.setWeight(Integer.valueOf(realRange.getLength()));
    int _start = realRange.getStart();
    int _minus = (-_start);
    realRange.translate(_minus);
    final LinkedHashSet<MemoryExclusionVertex> newHostsMObjs = new LinkedHashSet<MemoryExclusionVertex>();
    final Function2<String, Set<MemoryExclusionVertex>, Boolean> _function_1 = (String bank, Set<MemoryExclusionVertex> mobjs) -> {
      return Boolean.valueOf(banks.contains(bank));
    };
    Set<Map.Entry<String, Set<MemoryExclusionVertex>>> _entrySet = MapExtensions.<String, Set<MemoryExclusionVertex>>filter(mobjByBank, _function_1).entrySet();
    for (final Map.Entry<String, Set<MemoryExclusionVertex>> bankEntry : _entrySet) {
      {
        final List<Range> rangesInBank = new ArrayList<Range>();
        Set<MemoryExclusionVertex> _value_2 = bankEntry.getValue();
        for (final MemoryExclusionVertex mobj : _value_2) {
          {
            Object _value_3 = mobj.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
            final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> rangesInHost = ((List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) _value_3);
            int _size = rangesInHost.size();
            boolean _equals = (_size == 1);
            if (_equals) {
              Object _clone = rangesInHost.get(0).getValue().getValue().clone();
              Range.union(rangesInBank, ((Range) _clone));
            } else {
              Object _value_4 = mobj.getPropertyBean().getValue(MemoryExclusionVertex.DIVIDED_PARTS_HOSTS);
              final List<MemoryExclusionVertex> dividedPartsHosts = ((List<MemoryExclusionVertex>) _value_4);
              final Function1<MemoryExclusionVertex, String> _function_2 = (MemoryExclusionVertex it) -> {
                return bankByMobj.get(it);
              };
              final Set<String> partsHostsSet = IterableExtensions.<String>toSet(ListExtensions.<MemoryExclusionVertex, String>map(dividedPartsHosts, _function_2));
              if (((partsHostsSet.size() == 1) && ((String[])Conversions.unwrapArray(partsHostsSet, String.class))[0].equals(bankEntry.getKey()))) {
                final Consumer<Pair<MemoryExclusionVertex, Pair<Range, Range>>> _function_3 = (Pair<MemoryExclusionVertex, Pair<Range, Range>> range) -> {
                  Object _clone_1 = range.getValue().getValue().clone();
                  Range.union(rangesInBank, ((Range) _clone_1));
                };
                rangesInHost.forEach(_function_3);
              } else {
                mObjsToUndivide.add(mobj);
              }
            }
          }
        }
        final Set<MemoryExclusionVertex> mObjInCurrentBank = mobjByBank.get(bankEntry.getKey());
        mObjInCurrentBank.removeAll(mObjsToUndivide);
        final Map<Range, List<MemoryExclusionVertex>> rangesInBankAndMObjs = CollectionLiterals.<Range, List<MemoryExclusionVertex>>newLinkedHashMap();
        for (final Range currentRange : rangesInBank) {
          {
            final ArrayList<MemoryExclusionVertex> mObjInCurrentRange = new ArrayList<MemoryExclusionVertex>();
            for (final MemoryExclusionVertex mObj : mObjInCurrentBank) {
              {
                Object _value_3 = mObj.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
                final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> ranges = ((List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) _value_3);
                int _size = ranges.size();
                boolean _equals = (_size == 1);
                if (_equals) {
                  Range _intersection = ranges.get(0).getValue().getValue().intersection(currentRange);
                  boolean _tripleNotEquals = (_intersection != null);
                  if (_tripleNotEquals) {
                    mObjInCurrentRange.add(0, mObj);
                  }
                } else {
                  final Function1<Pair<MemoryExclusionVertex, Pair<Range, Range>>, Boolean> _function_2 = (Pair<MemoryExclusionVertex, Pair<Range, Range>> range) -> {
                    Range _intersection_1 = range.getValue().getValue().intersection(currentRange);
                    return Boolean.valueOf((_intersection_1 == null));
                  };
                  boolean _forall = IterableExtensions.<Pair<MemoryExclusionVertex, Pair<Range, Range>>>forall(ranges, _function_2);
                  boolean _not = (!_forall);
                  if (_not) {
                    mObjInCurrentRange.add(mObj);
                  }
                }
              }
            }
            rangesInBankAndMObjs.put(currentRange, mObjInCurrentRange);
          }
        }
        Set<Map.Entry<Range, List<MemoryExclusionVertex>>> _entrySet_1 = rangesInBankAndMObjs.entrySet();
        for (final Map.Entry<Range, List<MemoryExclusionVertex>> currentRangeAndMobjs : _entrySet_1) {
          {
            final Range currentRange_1 = currentRangeAndMobjs.getKey();
            final List<MemoryExclusionVertex> mObjInCurrentRange = currentRangeAndMobjs.getValue();
            final MemoryExclusionVertex newHostMobj = mObjInCurrentRange.get(0);
            newHostsMObjs.add(newHostMobj);
            Object _value_3 = newHostMobj.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
            final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> newHostOldRange = ((List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) _value_3);
            int _xifexpression = (int) 0;
            if ((alignment <= 0)) {
              _xifexpression = currentRange_1.getStart();
            } else {
              int _xblockexpression = (int) 0;
              {
                final int newHostOldStart = newHostOldRange.get(0).getValue().getValue().getStart();
                int _start_1 = currentRange_1.getStart();
                int _minus_1 = (newHostOldStart - _start_1);
                final int unaligned = (_minus_1 % alignment);
                int _xifexpression_1 = (int) 0;
                if ((unaligned == 0)) {
                  _xifexpression_1 = currentRange_1.getStart();
                } else {
                  int _start_2 = currentRange_1.getStart();
                  _xifexpression_1 = (_start_2 - (alignment - unaligned));
                }
                _xblockexpression = _xifexpression_1;
              }
              _xifexpression = _xblockexpression;
            }
            final int minIndex = _xifexpression;
            int _end = currentRange_1.getEnd();
            int _minus_1 = (_end - minIndex);
            newHostMobj.setWeight(Integer.valueOf(_minus_1));
            int _size = mObjInCurrentRange.size();
            boolean _lessEqualsThan = (_size <= 1);
            if (_lessEqualsThan) {
              newHostMobj.getPropertyBean().removeProperty(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
            } else {
              final Range newHostRealTokenRange = newHostOldRange.get(0).getValue().getKey();
              final Range newHostActualRealTokenRange = newHostOldRange.get(0).getValue().getValue().translate((-minIndex));
              final ArrayList<Pair<MemoryExclusionVertex, Pair<Range, Range>>> ranges = CollectionLiterals.<Pair<MemoryExclusionVertex, Pair<Range, Range>>>newArrayList();
              Pair<Range, Range> _mappedTo = Pair.<Range, Range>of(newHostRealTokenRange, newHostActualRealTokenRange);
              Pair<MemoryExclusionVertex, Pair<Range, Range>> _mappedTo_1 = Pair.<MemoryExclusionVertex, Pair<Range, Range>>of(newHostMobj, _mappedTo);
              ranges.add(_mappedTo_1);
              newHostMobj.setPropertyValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, ranges);
              final LinkedHashSet<MemoryExclusionVertex> hostMObjSet = CollectionLiterals.<MemoryExclusionVertex>newLinkedHashSet();
              hosts.put(newHostMobj, hostMObjSet);
              Iterable<MemoryExclusionVertex> _tail = IterableExtensions.<MemoryExclusionVertex>tail(mObjInCurrentRange);
              for (final MemoryExclusionVertex mObj : _tail) {
                {
                  Object _value_4 = mObj.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);
                  final List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> mObjRanges = ((List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) _value_4);
                  final ArrayList<Pair<MemoryExclusionVertex, Pair<Range, Range>>> mObjNewRanges = CollectionLiterals.<Pair<MemoryExclusionVertex, Pair<Range, Range>>>newArrayList();
                  for (final Pair<MemoryExclusionVertex, Pair<Range, Range>> mObjRangePair : mObjRanges) {
                    boolean _hasOverlap = Range.hasOverlap(mObjRangePair.getValue().getValue(), currentRange_1);
                    if (_hasOverlap) {
                      Range _key_1 = mObjRangePair.getValue().getKey();
                      int _start_1 = newHostActualRealTokenRange.getStart();
                      int _minus_2 = ((-minIndex) - _start_1);
                      Range _translate = mObjRangePair.getValue().getValue().translate(_minus_2);
                      Pair<Range, Range> _mappedTo_2 = Pair.<Range, Range>of(_key_1, _translate);
                      Pair<MemoryExclusionVertex, Pair<Range, Range>> _mappedTo_3 = Pair.<MemoryExclusionVertex, Pair<Range, Range>>of(newHostMobj, _mappedTo_2);
                      mObjNewRanges.add(_mappedTo_3);
                    } else {
                      mObjNewRanges.add(mObjRangePair);
                    }
                  }
                  mObj.getPropertyBean().setValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY, mObjNewRanges);
                  hostMObjSet.add(mObj);
                }
              }
            }
          }
        }
      }
    }
    for (final MemoryExclusionVertex newHostMobj : newHostsMObjs) {
      {
        meg.addVertex(newHostMobj);
        Distributor.restoreExclusions(meg, newHostMobj);
      }
    }
    if (((Distributor.logger != null) && (!mObjsToUndivide.isEmpty()))) {
      Distributor.logger.log(Level.WARNING, (((("The following divided memory object " + mObjsToUndivide) + " are unified again during the memory distribution process.\n") + "This unification was applied because divided memory objects cannot be merged in several distinct memories.\n") + 
        "Deactivating memory script causing this division may lead to lower memory footprints in this distributed memory context."));
    }
    for (final MemoryExclusionVertex mObj : mObjsToUndivide) {
      {
        meg.addVertex(mObj);
        Distributor.restoreExclusions(meg, mObj);
      }
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
  protected static void restoreExclusions(final MemoryExclusionGraph meg, final MemoryExclusionVertex mObj) {
    Object _value = meg.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = ((Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) _value);
    Set<MemoryExclusionVertex> _elvis = null;
    Set<MemoryExclusionVertex> _get = hosts.get(mObj);
    if (_get != null) {
      _elvis = _get;
    } else {
      _elvis = Collections.<MemoryExclusionVertex>unmodifiableSet(CollectionLiterals.<MemoryExclusionVertex>newLinkedHashSet());
    }
    final Iterable<MemoryExclusionVertex> mObjAndHostedMObjs = Iterables.<MemoryExclusionVertex>concat(Collections.<MemoryExclusionVertex>unmodifiableSet(CollectionLiterals.<MemoryExclusionVertex>newLinkedHashSet(mObj)), _elvis);
    for (final MemoryExclusionVertex curentMObj : mObjAndHostedMObjs) {
      {
        Object _value_1 = curentMObj.getPropertyBean().getValue(MemoryExclusionVertex.ADJACENT_VERTICES_BACKUP);
        final List<MemoryExclusionVertex> adjacentMObjs = ((List<MemoryExclusionVertex>) _value_1);
        for (final MemoryExclusionVertex adjacentMObj : adjacentMObjs) {
          if (((!Objects.equal(adjacentMObj, mObj)) && meg.vertexSet().contains(adjacentMObj))) {
            meg.addEdge(mObj, adjacentMObj);
          } else {
            final BiConsumer<MemoryExclusionVertex, Set<MemoryExclusionVertex>> _function = (MemoryExclusionVertex hostMObj, Set<MemoryExclusionVertex> hostedMObjs) -> {
              if ((((!Objects.equal(hostMObj, mObj)) && hostedMObjs.contains(adjacentMObj)) && meg.containsVertex(hostMObj))) {
                meg.addEdge(mObj, hostMObj);
              }
            };
            hosts.forEach(_function);
          }
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
   *      The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the
   *       associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegSharedOnly(final MemoryExclusionGraph memEx) {
    final LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
    Set<MemoryExclusionVertex> _vertexSet = memEx.vertexSet();
    LinkedHashSet<MemoryExclusionVertex> _linkedHashSet = new LinkedHashSet<MemoryExclusionVertex>(_vertexSet);
    memExesVerticesSet.put("Shared", _linkedHashSet);
    return memExesVerticesSet;
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
   *      The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the
   *       associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegDistributedOnly(final MemoryExclusionGraph memEx) {
    final LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
    Object _value = memEx.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = ((Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) _value);
    Set<MemoryExclusionVertex> _vertexSet = memEx.vertexSet();
    for (final MemoryExclusionVertex memExVertex : _vertexSet) {
      {
        Set<MemoryExclusionVertex> _xifexpression = null;
        if ((hosts != null)) {
          _xifexpression = hosts.get(memExVertex);
        } else {
          _xifexpression = null;
        }
        final Set<MemoryExclusionVertex> hostedMObjs = _xifexpression;
        Distributor.findMObjBankDistributedOnly(memExVertex, memExesVerticesSet, memEx);
        if ((hostedMObjs != null)) {
          final LinkedHashMap<String, Set<MemoryExclusionVertex>> hostedMObjsBanks = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
          for (final MemoryExclusionVertex hostedMobj : hostedMObjs) {
            Distributor.findMObjBankDistributedOnly(hostedMobj, hostedMObjsBanks, memEx);
          }
          Set<String> _keySet = hostedMObjsBanks.keySet();
          for (final String bank : _keySet) {
            {
              Set<MemoryExclusionVertex> verticesSet = memExesVerticesSet.get(bank);
              if ((verticesSet == null)) {
                LinkedHashSet<MemoryExclusionVertex> _linkedHashSet = new LinkedHashSet<MemoryExclusionVertex>();
                verticesSet = _linkedHashSet;
                memExesVerticesSet.put(bank, verticesSet);
              }
              verticesSet.add(memExVertex);
            }
          }
        }
      }
    }
    return memExesVerticesSet;
  }
  
  /**
   * The purpose of this method is to find the bank associated to a given
   * {@link MemoryExclusionVertex} according to the DISTRIBUTED_ONLY
   * distribution policy. The mObj is put in the {@link Map} passed as a
   * parameter where keys are the names of the memory banks and values are
   * the {@link Set} of associated {@link MemoryExclusionVertex}.
   * 
   * @param mObj
   *      The {@link MemoryExclusionVertex} whose memory banks are
   *      identified.
   * @param mObjByBank
   *      The {@link Map} in which results of this method are put.
   * @param memEx
   *      The {@link MemoryExclusionGraph} whose vertices are allocated.
   *      (only used to retrieved the corresponding {@link
   *      DirectedAcyclicGraph}).
   */
  protected static void findMObjBankDistributedOnly(final MemoryExclusionVertex mObj, final Map<String, Set<MemoryExclusionVertex>> mObjByBank, final MemoryExclusionGraph memEx) {
    for (int i = 0; (i < 2); i++) {
      {
        ComponentInstance component = null;
        DAGEdge edge = mObj.getEdge();
        DAGVertex _xifexpression = null;
        if ((edge != null)) {
          DAGVertex _xifexpression_1 = null;
          if ((i == 0)) {
            _xifexpression_1 = edge.getSource();
          } else {
            _xifexpression_1 = edge.getTarget();
          }
          _xifexpression = _xifexpression_1;
        } else {
          DAGVertex _xblockexpression = null;
          {
            Object _value = memEx.getPropertyBean().getValue(MemoryExclusionGraph.SOURCE_DAG);
            final DirectedAcyclicGraph dag = ((DirectedAcyclicGraph) _value);
            DAGVertex _xifexpression_2 = null;
            if ((i == 0)) {
              _xifexpression_2 = dag.getVertex(mObj.getSource().substring("FIFO_Head_".length()));
            } else {
              _xifexpression_2 = dag.getVertex(mObj.getSink());
            }
            _xblockexpression = _xifexpression_2;
          }
          _xifexpression = _xblockexpression;
        }
        DAGVertex dagVertex = _xifexpression;
        Object _value = dagVertex.getPropertyBean().getValue("Operator");
        component = ((ComponentInstance) _value);
        Set<MemoryExclusionVertex> verticesSet = mObjByBank.get(component.getInstanceName());
        if ((verticesSet == null)) {
          LinkedHashSet<MemoryExclusionVertex> _linkedHashSet = new LinkedHashSet<MemoryExclusionVertex>();
          verticesSet = _linkedHashSet;
          mObjByBank.put(component.getInstanceName(), verticesSet);
        }
        verticesSet.add(mObj);
      }
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
   *      The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the
   *       associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegMixed(final MemoryExclusionGraph memEx) {
    final LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
    Set<MemoryExclusionVertex> _vertexSet = memEx.vertexSet();
    for (final MemoryExclusionVertex memExVertex : _vertexSet) {
      Distributor.findMObjBankMixed(memExVertex, memExesVerticesSet);
    }
    return memExesVerticesSet;
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
   *      The processed {@link MemoryExclusionGraph}.
   * @return {@link Map} containing the name of the memory banks and the
   *       associated {@link LinkedHashSet subsets} of {@link MemoryExclusionVertex}.
   */
  protected static Map<String, Set<MemoryExclusionVertex>> distributeMegMixedMerged(final MemoryExclusionGraph memEx) {
    final LinkedHashMap<String, Set<MemoryExclusionVertex>> memExesVerticesSet = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
    Object _value = memEx.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
    final Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hosts = ((Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) _value);
    Set<MemoryExclusionVertex> _vertexSet = memEx.vertexSet();
    for (final MemoryExclusionVertex memExVertex : _vertexSet) {
      if ((((hosts != null) && (!hosts.containsKey(memExVertex))) || (hosts == null))) {
        Distributor.findMObjBankMixed(memExVertex, memExesVerticesSet);
      } else {
        final LinkedHashMap<String, Set<MemoryExclusionVertex>> mobjByBank = new LinkedHashMap<String, Set<MemoryExclusionVertex>>();
        Set<MemoryExclusionVertex> _get = hosts.get(memExVertex);
        Iterable<MemoryExclusionVertex> _plus = Iterables.<MemoryExclusionVertex>concat(Collections.<MemoryExclusionVertex>unmodifiableList(CollectionLiterals.<MemoryExclusionVertex>newArrayList(memExVertex)), _get);
        for (final MemoryExclusionVertex mobj : _plus) {
          Distributor.findMObjBankMixed(mobj, mobjByBank);
        }
        String _xifexpression = null;
        int _size = mobjByBank.size();
        boolean _equals = (_size == 1);
        if (_equals) {
          _xifexpression = ((String[])Conversions.unwrapArray(mobjByBank.keySet(), String.class))[0];
        } else {
          _xifexpression = "Shared";
        }
        String bank = _xifexpression;
        Set<MemoryExclusionVertex> verticesSet = memExesVerticesSet.get(bank);
        if ((verticesSet == null)) {
          LinkedHashSet<MemoryExclusionVertex> _linkedHashSet = new LinkedHashSet<MemoryExclusionVertex>();
          verticesSet = _linkedHashSet;
          memExesVerticesSet.put(bank, verticesSet);
        }
        verticesSet.add(memExVertex);
      }
    }
    return memExesVerticesSet;
  }
  
  /**
   * The purpose of this method is to find the bank associated to a given
   * {@link MemoryExclusionVertex} according to the MIXED distribution
   * policy. The mObj is put in the {@link Map} passed as a parameter where
   * keys are the names of the memory banks and values are the {@link Set}
   * of associated {@link MemoryExclusionVertex}.
   * 
   * @param mObj
   *      The {@link MemoryExclusionVertex} whose memory banks are
   *      identified.
   * @param mObjByBank
   *      The {@link Map} in which results of this method are put.
   */
  protected static void findMObjBankMixed(final MemoryExclusionVertex mObj, final Map<String, Set<MemoryExclusionVertex>> mObjByBank) {
    String memory = "Shared";
    DAGEdge _edge = mObj.getEdge();
    boolean _tripleNotEquals = (_edge != null);
    if (_tripleNotEquals) {
      Object _value = mObj.getEdge().getSource().getPropertyBean().getValue("Operator");
      Object _value_1 = mObj.getEdge().getTarget().getPropertyBean().getValue("Operator");
      boolean _equals = Objects.equal(_value, _value_1);
      if (_equals) {
        ComponentInstance component = null;
        DAGVertex dagVertex = mObj.getEdge().getSource();
        Object _value_2 = dagVertex.getPropertyBean().getValue("Operator");
        component = ((ComponentInstance) _value_2);
        memory = component.getInstanceName();
      }
    } else {
      String _source = mObj.getSource();
      String _sink = mObj.getSink();
      boolean _equals_1 = Objects.equal(_source, _sink);
      if (_equals_1) {
        ComponentInstance component_1 = null;
        Object _value_3 = mObj.getVertex().getPropertyBean().getValue("Operator");
        component_1 = ((ComponentInstance) _value_3);
        memory = component_1.getInstanceName();
      } else {
      }
    }
    Set<MemoryExclusionVertex> verticesSet = mObjByBank.get(memory);
    if ((verticesSet == null)) {
      LinkedHashSet<MemoryExclusionVertex> _linkedHashSet = new LinkedHashSet<MemoryExclusionVertex>();
      verticesSet = _linkedHashSet;
      mObjByBank.put(memory, verticesSet);
    }
    verticesSet.add(mObj);
  }
  
  @Pure
  public static Logger getLogger() {
    return Distributor.logger;
  }
  
  public static void setLogger(final Logger logger) {
    Distributor.logger = logger;
  }
}
