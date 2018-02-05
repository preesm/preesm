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
package org.ietr.preesm.memory.script;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Generated;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Pure;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.preesm.memory.script.Match;
import org.ietr.preesm.memory.script.MatchType;
import org.ietr.preesm.memory.script.Range;

@SuppressWarnings("unchecked")
@Generated(value = "org.eclipse.xtend.core.compiler.XtendGenerator", date = "2018-01-17T14:37+0100")
public class Buffer {
  /**
   * Identify which data ranges of a {@link Buffer} are matched multiple
   * times. A range is matched multiple times if several matches involving
   * this ranges are stored in the {@link Buffer#getMatchTable() match table}
   * of the {@link Buffer}. For example, if these to calls are executed: </br>
   * <code>a.matchWith(0,b,0,3)</code> and <code>a.matchWith(0,b,3,3)</code>,
   * then a[0..3[ is matched multiple times.
   * 
   * 
   * @return a {@link Map} containing the start end end of ranges matched
   *         multiple times.
   */
  static ArrayList<Range> getMultipleMatchRange(final Buffer buffer) {
    return Buffer.getOverlappingRanges(Iterables.<Match>concat(buffer.getMatchTable().values()));
  }
  
  /**
   * Same as {@link #getMultipleMatchRange(Buffer)} but tests only the given
   * {@link List} of {@link Match matches}. This method does not check if all
   * {@link Match matches} in the {@link List} have the same {@link
   * #getLocalBuffer() local buffer}.
   * 
   * @param matches
   * 	the {@link List} of {@link Match matches}
   * @return a {@link List} of {@link Range} containing the overlapping
   * ranges of the matches.
   */
  static ArrayList<Range> getOverlappingRanges(final Iterable<Match> matches) {
    final ArrayList<Range> matchRanges = CollectionLiterals.<Range>newArrayList();
    final ArrayList<Range> multipleMatchRanges = CollectionLiterals.<Range>newArrayList();
    final Consumer<Match> _function = (Match match) -> {
      final Range newRange = match.getLocalRange();
      final List<Range> intersections = Range.intersection(matchRanges, newRange);
      Range.union(multipleMatchRanges, intersections);
      Range.union(matchRanges, newRange);
    };
    matches.forEach(_function);
    return multipleMatchRanges;
  }
  
  /**
   * Test if the {@link Buffer} is partially matched.<br>
   * <br>
   * A {@link Buffer} is partially matched if only part of its token range
   * (i.e. from 0 to {@link #getNbTokens() nbTokens}*{@link #getTokenSize()
   * tokenSize}) are involved in a {@link Match} in the {@link Buffer}
   * {@link Buffer#_matchTable match table}. This condition is sufficient
   * since all "virtual" tokens of a {@link Buffer} will always have an
   * overlapping indivisible range with real tokens.
   * 
   * @param buffer
   * 	The tested {@link Buffer}.
   * @return <code>true</code> if the {@link Buffer} is completely matched,
   * and <code>false</code> otherwise.
   */
  boolean isCompletelyMatched() {
    final ArrayList<Range> coveredRange = new ArrayList<Range>();
    final Iterator<Map.Entry<Integer, List<Match>>> iterEntry = this.getMatchTable().entrySet().iterator();
    boolean stop = false;
    while ((iterEntry.hasNext() && (!stop))) {
      {
        final Map.Entry<Integer, List<Match>> entry = iterEntry.next();
        Iterator<Match> iterMatch = entry.getValue().iterator();
        while ((iterMatch.hasNext() && (!stop))) {
          {
            final Match match = iterMatch.next();
            final Range addedRange = Range.union(coveredRange, match.getLocalRange());
            stop = (stop || ((addedRange.getStart() <= 0) && (addedRange.getEnd() >= (this.getTokenSize() * this.getNbTokens()))));
          }
        }
      }
    }
    return stop;
  }
  
  /**
   * Test if all {@link Match matches} contained in the {@link
   * Buffer#_machTable matchTable} are reciprocal.<br><br>
   * 
   * A {@link Match} is reciprocal if the remote {@link Match#buffer}
   * contains an reciprocal {@link Match} in its {@link Buffer#_matchTable
   * matchTable}.
   */
  static boolean isReciprocal(final Buffer localBuffer) {
    final Function1<Map.Entry<Integer, List<Match>>, Boolean> _function = (Map.Entry<Integer, List<Match>> it) -> {
      boolean _xblockexpression = false;
      {
        final List<Match> matches = it.getValue();
        final Integer localIdx = it.getKey();
        final Function1<Match, Boolean> _function_1 = (Match match) -> {
          boolean _xblockexpression_1 = false;
          {
            final List<Match> remoteMatches = match.getRemoteBuffer().getMatchTable().get(Integer.valueOf(match.getRemoteIndex()));
            _xblockexpression_1 = ((remoteMatches != null) && remoteMatches.contains(
              new Match(match.getRemoteBuffer(), match.getRemoteIndex(), localBuffer, (localIdx).intValue(), match.getLength())));
          }
          return Boolean.valueOf(_xblockexpression_1);
        };
        _xblockexpression = IterableExtensions.<Match>forall(matches, _function_1);
      }
      return Boolean.valueOf(_xblockexpression);
    };
    return IterableExtensions.<Map.Entry<Integer, List<Match>>>forall(localBuffer.getMatchTable().entrySet(), _function);
  }
  
  /**
   * The objective of this method is to merge as many matches as possible
   * from the {@link Buffer} {@link Buffer#_matchTable match tables}.<br><br>
   * 
   * Two matches are mergeable if they are consecutive and if they match
   * consecutive targets.<br>
   * Example 1: <code>a[0..3]<->b[1..4] and a[4..5]<->b[5..6]</code> are
   * valid candidates.<br>
   * Example 2: <code>a[0..3]<->b[1..4] and a[5..6]<->b[5..6]</code> are
   * not valid candidates. Merging buffers does not change the divisibility
   * of the buffer since if contiguous matches are applied, at least one
   * of them will become indivisible (since subparts of a divided buffer
   * cannot be match within divided buffers.)<br><b>
   * Before using this method, the {@link Buffer} must pass all checks
   * performed by the {@link ScriptRunner#check()} method.</b>
   * 
   * @param buffer
   * 	The {@link Buffer} whose {@link Buffer#_matchTable matchTable} is
   *  simplified.
   * @param processedMatch
   * 	A {@link List} containing {@link Match matches} that will be ignored
   *  during simplification. This list will be updated during the method
   *  execution by adding to it the {@link Match#reciprocate} of the
   *  processed {@link Match matches}.
   */
  static void simplifyMatches(final Buffer buffer, final List<Match> processedMatch) {
    final ArrayList<Integer> removedEntry = CollectionLiterals.<Integer>newArrayList();
    final Consumer<Map.Entry<Integer, List<Match>>> _function = (Map.Entry<Integer, List<Match>> it) -> {
      final Integer localIdx = it.getKey();
      final List<Match> matchSet = it.getValue();
      final Function1<Match, Boolean> _function_1 = (Match it_1) -> {
        boolean _contains = processedMatch.contains(it_1);
        return Boolean.valueOf((!_contains));
      };
      final Consumer<Match> _function_2 = (Match match) -> {
        Match remMatch = null;
        do {
          {
            int _length = match.getLength();
            int _plus = ((localIdx).intValue() + _length);
            List<Match> candidateSet = buffer.getMatchTable().get(Integer.valueOf(_plus));
            Match _xifexpression = null;
            if ((candidateSet != null)) {
              final Function1<Match, Boolean> _function_3 = (Match candidate) -> {
                return Boolean.valueOf((Objects.equal(candidate.getRemoteBuffer(), match.getRemoteBuffer()) && (candidate.getRemoteIndex() == (match.getRemoteIndex() + match.getLength()))));
              };
              _xifexpression = IterableExtensions.<Match>findFirst(candidateSet, _function_3);
            } else {
              _xifexpression = null;
            }
            remMatch = _xifexpression;
            if ((remMatch != null)) {
              candidateSet.remove(remMatch);
              final List<Match> remMatchSet = remMatch.getRemoteBuffer().getMatchTable().get(Integer.valueOf(remMatch.getRemoteIndex()));
              remMatchSet.remove(remMatch.getReciprocate());
              int _size = remMatchSet.size();
              boolean _equals = (_size == 0);
              if (_equals) {
                remMatch.getRemoteBuffer().getMatchTable().remove(Integer.valueOf(remMatch.getRemoteIndex()));
              }
              int _size_1 = candidateSet.size();
              boolean _equals_1 = (_size_1 == 0);
              if (_equals_1) {
                int _length_1 = match.getLength();
                int _plus_1 = ((localIdx).intValue() + _length_1);
                removedEntry.add(Integer.valueOf(_plus_1));
              }
              int _length_2 = match.getLength();
              int _length_3 = remMatch.getLength();
              int _plus_2 = (_length_2 + _length_3);
              match.setLength(_plus_2);
              Match _reciprocate = match.getReciprocate();
              _reciprocate.setLength(match.getLength());
            }
          }
        } while((remMatch != null));
        processedMatch.add(match.getReciprocate());
      };
      IterableExtensions.<Match>filter(matchSet, _function_1).forEach(_function_2);
    };
    buffer.getMatchTable().entrySet().forEach(_function);
    final Consumer<Integer> _function_1 = (Integer it) -> {
      buffer.getMatchTable().remove(it);
    };
    removedEntry.forEach(_function_1);
  }
  
  /**
   * cf {@link #minIndex}.
   */
  @Accessors
  int maxIndex;
  
  /**
   * Minimum index for the buffer content.
   * Constructor initialize this value to 0 but it is possible to lower this
   * value by matching another buffer on the "edge" of this one.<br>
   * For example: <code>this.matchWith(-3, a, 0, 6)</code> results in
   * matching this[-3..2] with a[0..5], thus lowering this.minIndex to -3.
   */
  @Accessors
  int minIndex;
  
  /**
   * This table is protected to ensure that matches are set only by using
   * {@link #matchWith(int,Buffer,int)} methods in the scripts.
   */
  @Accessors
  final Map<Integer, List<Match>> matchTable;
  
  /**
   * This property is used to mark the {@link Buffer buffers} that were
   * {@link #applyMatches(List) matched}.
   * Originally set to <code>null</code>, it is replaced by a {@link List}
   * of applied {@link Match} in the {@link #applyMatches(List) applyMatches}
   * method.
   */
  @Accessors
  List<Match> matched = null;
  
  /**
   * This property is set to <code>true</code> if a remote {@link Buffer} was merged
   * within the current {@link Buffer}
   */
  @Accessors
  boolean host = false;
  
  @Accessors
  private final String name;
  
  @Accessors
  private final int nbTokens;
  
  @Accessors
  private final int tokenSize;
  
  @Accessors
  final DAGVertex dagVertex;
  
  @Accessors
  final SDFEdge sdfEdge;
  
  /**
   * This {@link List} of {@link Range} is used to store its indivisible
   * sub-parts. A buffer can effectively be divided only if its is not
   * indivisible and if the division imposed by the matches do not break
   * any indivisible range.
   */
  @Accessors
  List<Range> indivisibleRanges;
  
  /**
   * This {@link List} contains all {@link Match} that must be applied
   * to authorize the division of a {@link Buffer}.
   * The {@link List} contains {@link List} of {@link Match}. To authorize
   * a division, each sublist must contain enough {@link Match#isApplied()
   * applied} {@link Match} to cover all the tokens (real and virtual) of
   * the original {@link Match#getLocalBuffer() localBuffer} of the
   *  {@link Match matches}.
   */
  @Accessors
  List<List<Match>> divisibilityRequiredMatches;
  
  @Accessors
  protected final Map<Range, Pair<Buffer, Integer>> appliedMatches;
  
  /**
   * This flag is set at the {@link Buffer} instantiation to indicate whether
   * the buffer is mergeable or not. If the buffer is mergeable, all its
   * virtual tokens will be associated to mergeable ranges. Otherwise they
   * won't.
   */
  @Accessors
  private final boolean originallyMergeable;
  
  @Accessors
  List<Range> mergeableRanges;
  
  /**
   * Constructor for the {@link Buffer}.
   * @param name
   * 	A {@link String} corresponding to the final name of the buffer.
   * @param nbTokens
   * 	The number of tokens stored in this buffer.
   * @param tokenSize
   * 	The size of one token of the buffer.
   */
  public Buffer(final SDFEdge edge, final DAGVertex dagVertex, final String name, final int nbTokens, final int tokenSize, final boolean mergeable) {
    this.sdfEdge = edge;
    this.name = name;
    this.nbTokens = nbTokens;
    this.tokenSize = tokenSize;
    this.matchTable = CollectionLiterals.<Integer, List<Match>>newHashMap();
    this.appliedMatches = CollectionLiterals.<Range, Pair<Buffer, Integer>>newHashMap();
    this.minIndex = 0;
    this.maxIndex = (nbTokens * tokenSize);
    this.dagVertex = dagVertex;
    this.originallyMergeable = mergeable;
    this.mergeableRanges = CollectionLiterals.<Range>newArrayList();
    if (mergeable) {
      Range _range = new Range(0, (nbTokens * tokenSize));
      this.mergeableRanges.add(_range);
    }
    this.indivisibleRanges = CollectionLiterals.<Range>newArrayList();
    this.divisibilityRequiredMatches = CollectionLiterals.<List<Match>>newArrayList();
  }
  
  SDFAbstractVertex getSdfVertex() {
    return this.getDagVertex().getPropertyBean().<SDFAbstractVertex>getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex.class);
  }
  
  void setMaxIndex(final int newValue) {
    boolean _isOriginallyMergeable = this.isOriginallyMergeable();
    if (_isOriginallyMergeable) {
      Range _range = new Range(this.maxIndex, newValue);
      Range.union(this.getMergeableRanges(), _range);
    }
    this.maxIndex = newValue;
  }
  
  void setMinIndex(final int newValue) {
    boolean _isOriginallyMergeable = this.isOriginallyMergeable();
    if (_isOriginallyMergeable) {
      Range _range = new Range(newValue, this.minIndex);
      Range.union(this.getMergeableRanges(), _range);
    }
    this.minIndex = newValue;
  }
  
  /**
   * Cf. {@link Buffer#matchWith(int, Buffer, int, int)} with size = 1
   */
  public Match matchWith(final int localIdx, final Buffer buffer, final int remoteIdx) {
    return this.matchWith(localIdx, buffer, remoteIdx, 1);
  }
  
  /**
   * {@link Match} part of the current {@link Buffer} with part of another
   * {@link Buffer}. Example: <code>a.matchWith(3,b,7,5)</code> matches
   * a[3..7] with b[7..11]. Matching two {@link Buffer buffers} means that the
   * matched ranges may be merged, i.e. they may be allocated in the same
   * memory space.<br>
   * The localIdx, remoteIdx and size represent a number of token. (cf.
   * production and consumption rate from the SDF graph).
   * 
   * @exception Exception
   *                may be thrown if the matched ranges both have elements
   * 				  outside of their {@link Buffer} indexes
   * 				  ({@link #_maxIndex} and {@link #_minIndex}).
   * 
   * 
   * @param localIdx
   *            start index of the matched range for the local {@link Buffer}.
   * @param buffer
   *            remote {@link Buffer}
   * @param remoteIdx
   *            start index of the matched range for the remote {@link Buffer}
   * @param size
   *            the size of the matched range
   * @return the created local {@link Match}
   */
  public Match matchWith(final int localIdx, final Buffer buffer, final int remoteIdx, final int size) {
    int _tokenSize = this.getTokenSize();
    int _tokenSize_1 = buffer.getTokenSize();
    boolean _notEquals = (_tokenSize != _tokenSize_1);
    if (_notEquals) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Cannot match ");
      String _name = this.getDagVertex().getName();
      _builder.append(_name);
      _builder.append(".");
      String _name_1 = this.getName();
      _builder.append(_name_1);
      _builder.append(" with ");
      String _name_2 = buffer.getDagVertex().getName();
      _builder.append(_name_2);
      _builder.append(".");
      String _name_3 = buffer.getName();
      _builder.append(_name_3);
      _builder.append(" because buffers have different token sized (");
      int _tokenSize_2 = this.getTokenSize();
      _builder.append(_tokenSize_2);
      _builder.append(" != ");
      int _tokenSize_3 = buffer.getTokenSize();
      _builder.append(_tokenSize_3);
      _builder.append(" \")");
      throw new RuntimeException(_builder.toString());
    }
    if (((localIdx >= this.getNbTokens()) || (((localIdx + size) - 1) < 0))) {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append("Cannot match ");
      String _name_4 = this.getDagVertex().getName();
      _builder_1.append(_name_4);
      _builder_1.append(".");
      String _name_5 = this.getName();
      _builder_1.append(_name_5);
      _builder_1.append("[");
      _builder_1.append(localIdx);
      _builder_1.append("..");
      _builder_1.append(((localIdx + size) - 1));
      _builder_1.append("] and ");
      String _name_6 = buffer.getDagVertex().getName();
      _builder_1.append(_name_6);
      _builder_1.append(".");
      String _name_7 = buffer.getName();
      _builder_1.append(_name_7);
      _builder_1.append("[");
      _builder_1.append(remoteIdx);
      _builder_1.append("..");
      _builder_1.append(((remoteIdx + size) - 1));
      _builder_1.append("] because no \"real\" token from ");
      String _name_8 = this.getDagVertex().getName();
      _builder_1.append(_name_8);
      _builder_1.append(".");
      String _name_9 = this.getName();
      _builder_1.append(_name_9);
      _builder_1.append("[0..");
      int _nbTokens = this.getNbTokens();
      int _minus = (_nbTokens - 1);
      _builder_1.append(_minus);
      _builder_1.append("] is matched.");
      throw new RuntimeException(_builder_1.toString());
    }
    if (((remoteIdx >= buffer.getNbTokens()) || (((remoteIdx + size) - 1) < 0))) {
      StringConcatenation _builder_2 = new StringConcatenation();
      _builder_2.append("Cannot match ");
      String _name_10 = this.getDagVertex().getName();
      _builder_2.append(_name_10);
      _builder_2.append(".");
      String _name_11 = this.getName();
      _builder_2.append(_name_11);
      _builder_2.append("[");
      _builder_2.append(localIdx);
      _builder_2.append("..");
      _builder_2.append(((localIdx + size) - 1));
      _builder_2.append("] and ");
      String _name_12 = buffer.getDagVertex().getName();
      _builder_2.append(_name_12);
      _builder_2.append(".");
      String _name_13 = buffer.getName();
      _builder_2.append(_name_13);
      _builder_2.append("[");
      _builder_2.append(remoteIdx);
      _builder_2.append("..");
      _builder_2.append(((remoteIdx + size) - 1));
      _builder_2.append("] because no \"real\" token from \"");
      String _name_14 = buffer.getDagVertex().getName();
      _builder_2.append(_name_14);
      _builder_2.append(".");
      String _name_15 = buffer.getName();
      _builder_2.append(_name_15);
      _builder_2.append("[0..");
      int _nbTokens_1 = buffer.getNbTokens();
      int _minus_1 = (_nbTokens_1 - 1);
      _builder_2.append(_minus_1);
      _builder_2.append("] is matched.");
      throw new RuntimeException(_builder_2.toString());
    }
    if ((((((localIdx < 0) && (remoteIdx < 0)) || ((((localIdx + size) - 1) >= this.getNbTokens()) && (((remoteIdx + size) - 1) >= buffer.getNbTokens()))) || ((localIdx >= 0) && ((this.getNbTokens() - localIdx) <= (-Math.min(0, remoteIdx))))) || ((remoteIdx >= 0) && ((buffer.getNbTokens() - remoteIdx) <= (-Math.min(0, localIdx)))))) {
      StringConcatenation _builder_3 = new StringConcatenation();
      _builder_3.append("Cannot match ");
      String _name_16 = this.getDagVertex().getName();
      _builder_3.append(_name_16);
      _builder_3.append(".");
      String _name_17 = this.getName();
      _builder_3.append(_name_17);
      _builder_3.append("[");
      _builder_3.append(localIdx);
      _builder_3.append("..");
      _builder_3.append(((localIdx + size) - 1));
      _builder_3.append("] and ");
      String _name_18 = buffer.getDagVertex().getName();
      _builder_3.append(_name_18);
      _builder_3.append(".");
      String _name_19 = buffer.getName();
      _builder_3.append(_name_19);
      _builder_3.append("[");
      _builder_3.append(remoteIdx);
      _builder_3.append("..");
      _builder_3.append(((remoteIdx + size) - 1));
      _builder_3.append("] because \"virtual tokens\" cannot be matched together.");
      _builder_3.append("\n");
      _builder_3.append("Information: ");
      String _name_20 = this.getDagVertex().getName();
      _builder_3.append(_name_20);
      _builder_3.append(".");
      String _name_21 = this.getName();
      _builder_3.append(_name_21);
      _builder_3.append(" size = ");
      int _nbTokens_2 = this.getNbTokens();
      _builder_3.append(_nbTokens_2);
      _builder_3.append(" and ");
      String _name_22 = buffer.getDagVertex().getName();
      _builder_3.append(_name_22);
      _builder_3.append(".");
      String _name_23 = buffer.getName();
      _builder_3.append(_name_23);
      _builder_3.append(" size = ");
      int _nbTokens_3 = buffer.getNbTokens();
      _builder_3.append(_nbTokens_3);
      _builder_3.append(".");
      throw new RuntimeException(_builder_3.toString());
    }
    int _tokenSize_4 = this.getTokenSize();
    int _multiply = (localIdx * _tokenSize_4);
    int _tokenSize_5 = this.getTokenSize();
    int _multiply_1 = (remoteIdx * _tokenSize_5);
    int _tokenSize_6 = this.getTokenSize();
    int _multiply_2 = (size * _tokenSize_6);
    return this.byteMatchWith(_multiply, buffer, _multiply_1, _multiply_2);
  }
  
  public Match byteMatchWith(final int localByteIdx, final Buffer buffer, final int remoteByteIdx, final int byteSize) {
    return this.byteMatchWith(localByteIdx, buffer, remoteByteIdx, byteSize, true);
  }
  
  public Match byteMatchWith(final int localByteIdx, final Buffer buffer, final int remoteByteIdx, final int byteSize, final boolean check) {
    if (check) {
      if (((localByteIdx >= (this.getNbTokens() * this.getTokenSize())) || (((localByteIdx + byteSize) - 1) < 0))) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Cannot match bytes ");
        String _name = this.getDagVertex().getName();
        _builder.append(_name);
        _builder.append(".");
        String _name_1 = this.getName();
        _builder.append(_name_1);
        _builder.append("[");
        _builder.append(localByteIdx);
        _builder.append("..");
        _builder.append(((localByteIdx + byteSize) - 
          1));
        _builder.append("] and ");
        String _name_2 = buffer.getDagVertex().getName();
        _builder.append(_name_2);
        _builder.append(".");
        String _name_3 = buffer.getName();
        _builder.append(_name_3);
        _builder.append("[");
        _builder.append(remoteByteIdx);
        _builder.append("..");
        _builder.append(((remoteByteIdx + byteSize) - 1));
        _builder.append("] because no \"real\" byte from ");
        String _name_4 = this.getDagVertex().getName();
        _builder.append(_name_4);
        _builder.append(".");
        String _name_5 = this.getName();
        _builder.append(_name_5);
        _builder.append("[0..");
        int _nbTokens = this.getNbTokens();
        int _tokenSize = this.getTokenSize();
        int _multiply = (_nbTokens * _tokenSize);
        int _minus = (_multiply - 1);
        _builder.append(_minus);
        _builder.append("] is matched.");
        throw new RuntimeException(_builder.toString());
      }
      if (((remoteByteIdx >= (buffer.getNbTokens() * buffer.getTokenSize())) || (((remoteByteIdx + byteSize) - 1) < 0))) {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("Cannot match bytes ");
        String _name_6 = this.getDagVertex().getName();
        _builder_1.append(_name_6);
        _builder_1.append(".");
        String _name_7 = this.getName();
        _builder_1.append(_name_7);
        _builder_1.append("[");
        _builder_1.append(localByteIdx);
        _builder_1.append("..");
        _builder_1.append(((localByteIdx + byteSize) - 1));
        _builder_1.append("] and ");
        String _name_8 = buffer.getDagVertex().getName();
        _builder_1.append(_name_8);
        _builder_1.append(".");
        String _name_9 = buffer.getName();
        _builder_1.append(_name_9);
        _builder_1.append("[");
        _builder_1.append(remoteByteIdx);
        _builder_1.append("..");
        _builder_1.append(((remoteByteIdx + byteSize) - 1));
        _builder_1.append("] because no \"real\" byte from \"");
        String _name_10 = buffer.getDagVertex().getName();
        _builder_1.append(_name_10);
        _builder_1.append(".");
        String _name_11 = buffer.getName();
        _builder_1.append(_name_11);
        _builder_1.append("[0..");
        int _nbTokens_1 = buffer.getNbTokens();
        int _tokenSize_1 = buffer.getTokenSize();
        int _multiply_1 = (_nbTokens_1 * _tokenSize_1);
        int _minus_1 = (_multiply_1 - 1);
        _builder_1.append(_minus_1);
        _builder_1.append("] is matched.");
        throw new RuntimeException(_builder_1.toString());
      }
      if ((((((localByteIdx < 0) && (remoteByteIdx < 0)) || ((((localByteIdx + byteSize) - 1) >= (this.getNbTokens() * this.getTokenSize())) && (((remoteByteIdx + byteSize) - 1) >= (buffer.getNbTokens() * buffer.getTokenSize())))) || ((localByteIdx >= 0) && (((this.getNbTokens() * this.getTokenSize()) - localByteIdx) <= (-Math.min(0, remoteByteIdx))))) || ((remoteByteIdx >= 0) && (((buffer.getNbTokens() * buffer.getTokenSize()) - remoteByteIdx) <= (-Math.min(0, localByteIdx)))))) {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("Cannot match bytes ");
        String _name_12 = this.getDagVertex().getName();
        _builder_2.append(_name_12);
        _builder_2.append(".");
        String _name_13 = this.getName();
        _builder_2.append(_name_13);
        _builder_2.append("[");
        _builder_2.append(localByteIdx);
        _builder_2.append("..");
        _builder_2.append(((localByteIdx + byteSize) - 1));
        _builder_2.append("] and ");
        String _name_14 = buffer.getDagVertex().getName();
        _builder_2.append(_name_14);
        _builder_2.append(".");
        String _name_15 = buffer.getName();
        _builder_2.append(_name_15);
        _builder_2.append("[");
        _builder_2.append(remoteByteIdx);
        _builder_2.append("..");
        _builder_2.append(((remoteByteIdx + byteSize) - 1));
        _builder_2.append("] because \"virtual bytes\" cannot be matched together.");
        _builder_2.append("\n");
        _builder_2.append("Information: ");
        String _name_16 = this.getDagVertex().getName();
        _builder_2.append(_name_16);
        _builder_2.append(".");
        String _name_17 = this.getName();
        _builder_2.append(_name_17);
        _builder_2.append(" size = ");
        int _nbTokens_2 = this.getNbTokens();
        int _tokenSize_2 = this.getTokenSize();
        int _multiply_2 = (_nbTokens_2 * _tokenSize_2);
        _builder_2.append(_multiply_2);
        _builder_2.append(" and ");
        String _name_18 = buffer.getDagVertex().getName();
        _builder_2.append(_name_18);
        _builder_2.append(".");
        String _name_19 = buffer.getName();
        _builder_2.append(_name_19);
        _builder_2.append(" size = ");
        int _nbTokens_3 = buffer.getNbTokens();
        int _tokenSize_3 = buffer.getTokenSize();
        int _multiply_3 = (_nbTokens_3 * _tokenSize_3);
        _builder_2.append(_multiply_3);
        _builder_2.append(".");
        throw new RuntimeException(_builder_2.toString());
      }
    }
    boolean _not = (!((localByteIdx >= 0) && (((localByteIdx + byteSize) - 1) < (this.getNbTokens() * this.getTokenSize()))));
    if (_not) {
      this.minIndex = Math.min(this.minIndex, localByteIdx);
      this.maxIndex = Math.max(this.maxIndex, (localByteIdx + byteSize));
    }
    boolean _not_1 = (!((remoteByteIdx >= 0) && (((remoteByteIdx + byteSize) - 1) < (buffer.getNbTokens() * buffer.getTokenSize()))));
    if (_not_1) {
      buffer.minIndex = Math.min(buffer.minIndex, remoteByteIdx);
      buffer.maxIndex = Math.max(buffer.maxIndex, (remoteByteIdx + byteSize));
    }
    List<Match> matchSet = this.getMatchTable().get(Integer.valueOf(localByteIdx));
    if ((matchSet == null)) {
      matchSet = CollectionLiterals.<Match>newArrayList();
      this.getMatchTable().put(Integer.valueOf(localByteIdx), matchSet);
    }
    final Match localMatch = new Match(this, localByteIdx, buffer, remoteByteIdx, byteSize);
    matchSet.add(localMatch);
    List<Match> remoteMatchSet = buffer.getMatchTable().get(Integer.valueOf(remoteByteIdx));
    if ((remoteMatchSet == null)) {
      remoteMatchSet = CollectionLiterals.<Match>newArrayList();
      buffer.getMatchTable().put(Integer.valueOf(remoteByteIdx), remoteMatchSet);
    }
    final Match remoteMatch = new Match(buffer, remoteByteIdx, this, localByteIdx, byteSize);
    remoteMatchSet.add(remoteMatch);
    localMatch.setReciprocate(remoteMatch);
    return localMatch;
  }
  
  /**
   * A {@link Buffer} is divisible if its {@link #getIndivisibleRanges()
   * indivisible ranges} are not unique and completely cover the 0 to {@link
   * #getNbTokens() nbTokens}*{@link #getTokenSize() tokenSize} {@link
   * Range}, if it is {@link #isCompletelyMatched() completelyMatched},
   * and if it is matched only in {@link #isIndivisible() indivisible} {@link
   * Buffer buffers}.<br>
   * <b> An {@link Buffer} that is not {@link #isIndivisible() indivisible}
   * is not necessarily divisible. Indeed, it might fulfill parts of the
   * conditions to be divisible.</b>
   * 
   * @return <code>true</code> if the {@link Buffer} is divisible, <code>
   * false</code> otherwise.
   */
  boolean isDivisible() {
    boolean _and = false;
    boolean _and_1 = false;
    if (!(this.isCompletelyMatched() && (this.getIndivisibleRanges().size() > 1))) {
      _and_1 = false;
    } else {
      boolean _xblockexpression = false;
      {
        final Function1<Range, Range> _function = (Range it) -> {
          Object _clone = it.clone();
          return ((Range) _clone);
        };
        List<Range> _list = IterableExtensions.<Range>toList(ListExtensions.<Range, Range>map(this.getIndivisibleRanges(), _function));
        final List<Range> copy = new ArrayList<Range>(_list);
        final Range firstElement = IterableExtensions.<Range>head(copy);
        copy.remove(0);
        final Range coveredRange = Range.union(copy, firstElement);
        int _nbTokens = this.getNbTokens();
        int _tokenSize = this.getTokenSize();
        int _multiply = (_nbTokens * _tokenSize);
        int _size = new Range(0, _multiply).difference(coveredRange).size();
        _xblockexpression = (_size == 0);
      }
      _and_1 = _xblockexpression;
    }
    if (!_and_1) {
      _and = false;
    } else {
      final Function1<Match, Boolean> _function = (Match it) -> {
        return Boolean.valueOf(it.getRemoteBuffer().isIndivisible());
      };
      boolean _forall = IterableExtensions.<Match>forall(Iterables.<Match>concat(this.getMatchTable().values()), _function);
      _and = _forall;
    }
    return _and;
  }
  
  /**
   * A {@link Buffer} is indivisible if its {@link #getIndivisibleRanges()
   * indivisibleRanges} attribute contains a unique {@link Range} that covers
   * all the {@link #getMinIndex() minIndex} to {@link #getMaxIndex()
   * maxIndex} {@link Range}. <br>
   * <b> An {@link Buffer} that is not {@link #isIndivisible() indivisible}
   * is not necessarily {@link #isDivisible() divisible}. Indeed, it might
   * fulfill parts of the conditions to be divisible.</b>
   */
  boolean isIndivisible() {
    return (((this.getIndivisibleRanges().size() == 1) && (IterableExtensions.<Range>head(this.getIndivisibleRanges()).getStart() == this.getMinIndex())) && 
      (IterableExtensions.<Range>head(this.getIndivisibleRanges()).getEnd() == this.getMaxIndex()));
  }
  
  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    String _name = this.getSdfVertex().getName();
    _builder.append(_name);
    _builder.append(".");
    String _name_1 = this.getName();
    _builder.append(_name_1);
    _builder.append("[");
    int _nbTokens = this.getNbTokens();
    int _tokenSize = this.getTokenSize();
    int _multiply = (_nbTokens * _tokenSize);
    _builder.append(_multiply);
    _builder.append("]");
    return _builder.toString();
  }
  
  /**
   * We do not check that the match is possible !
   * We just apply it and assume all checks are performed somewhere else !
   * The local buffer is merged into the remote buffer
   * The local buffer does not "exists" afterwards
   */
  void applyMatches(final List<Match> matches) {
    final ArrayList<Match> matchesCopy = new ArrayList<Match>(matches);
    final Function1<Match, Boolean> _function = (Match it) -> {
      Buffer _localBuffer = it.getLocalBuffer();
      return Boolean.valueOf(Objects.equal(_localBuffer, this));
    };
    boolean _forall = IterableExtensions.<Match>forall(matchesCopy, _function);
    boolean _not = (!_forall);
    if (_not) {
      throw new RuntimeException(
        "Incorrect call to applyMatches method.\n One of the given matches does not belong to the this Buffer.");
    }
    ArrayList<Range> _arrayList = new ArrayList<Range>();
    final Function2<ArrayList<Range>, Match, ArrayList<Range>> _function_1 = (ArrayList<Range> res, Match match) -> {
      ArrayList<Range> _xblockexpression = null;
      {
        Range.union(res, match.getLocalIndivisibleRange());
        _xblockexpression = res;
      }
      return _xblockexpression;
    };
    final ArrayList<Range> matchedRange = IterableExtensions.<Match, ArrayList<Range>>fold(matchesCopy, _arrayList, _function_1);
    int _tokenSize = this.getTokenSize();
    int _nbTokens = this.getNbTokens();
    int _multiply = (_tokenSize * _nbTokens);
    final Range tokenRange = new Range(0, _multiply);
    Range _head = IterableExtensions.<Range>head(Range.intersection(matchedRange, tokenRange));
    boolean _notEquals = (!Objects.equal(_head, tokenRange));
    if (_notEquals) {
      throw new RuntimeException(
        ("Incorrect call to applyMatches method.\n All real token must be covered by the given matches.\n" + matches));
    }
    final Function1<Match, Boolean> _function_2 = (Match match) -> {
      final Function1<Match, Boolean> _function_3 = (Match it) -> {
        return Boolean.valueOf((it != match));
      };
      final Function1<Match, Boolean> _function_4 = (Match it) -> {
        boolean _hasOverlap = Range.hasOverlap(match.getLocalIndivisibleRange(), it.getLocalIndivisibleRange());
        return Boolean.valueOf((!_hasOverlap));
      };
      return Boolean.valueOf(IterableExtensions.<Match>forall(IterableExtensions.<Match>filter(matchesCopy, _function_3), _function_4));
    };
    boolean _forall_1 = IterableExtensions.<Match>forall(matchesCopy, _function_2);
    boolean _not_1 = (!_forall_1);
    if (_not_1) {
      throw new RuntimeException(
        ("Incorrect call to applyMatches method.\n Given matches are overlapping in the localBuffer.\n" + matches));
    }
    final Function1<Match, Boolean> _function_3 = (Match it) -> {
      return Boolean.valueOf((it.isApplicable() && it.getReciprocate().isApplicable()));
    };
    boolean _forall_2 = IterableExtensions.<Match>forall(matches, _function_3);
    boolean _not_2 = (!_forall_2);
    if (_not_2) {
      final Function1<Match, Boolean> _function_4 = (Match it) -> {
        return Boolean.valueOf(((!it.isApplicable()) || (!it.getReciprocate().isApplicable())));
      };
      Iterable<Match> _filter = IterableExtensions.<Match>filter(matches, _function_4);
      String _plus = ("Incorrect call to applyMatches method.\n One or more applied matches are not applicable.\n" + _filter);
      throw new RuntimeException(_plus);
    }
    for (final Match match : matchesCopy) {
      {
        Range _localIndivisibleRange = match.getLocalIndivisibleRange();
        Buffer _remoteBuffer = match.getRemoteBuffer();
        int _remoteIndex = match.getRemoteIndex();
        Pair<Buffer, Integer> _mappedTo = Pair.<Buffer, Integer>of(_remoteBuffer, Integer.valueOf(_remoteIndex));
        this.getAppliedMatches().put(_localIndivisibleRange, _mappedTo);
        Buffer _remoteBuffer_1 = match.getRemoteBuffer();
        _remoteBuffer_1.host = true;
        this.updateForbiddenAndMergeableLocalRanges(match);
        Match _xifexpression = null;
        MatchType _type = match.getType();
        boolean _equals = Objects.equal(_type, MatchType.FORWARD);
        if (_equals) {
          _xifexpression = match;
        } else {
          _xifexpression = match.getReciprocate();
        }
        final Match forwardMatch = _xifexpression;
        final Function1<Match, Boolean> _function_5 = (Match it) -> {
          MatchType _type_1 = it.getType();
          return Boolean.valueOf(Objects.equal(_type_1, MatchType.BACKWARD));
        };
        final Consumer<Match> _function_6 = (Match it) -> {
          final Function1<Range, Range> _function_7 = (Range it_1) -> {
            Object _clone = it_1.clone();
            return ((Range) _clone);
          };
          List<Range> _map = ListExtensions.<Range, Range>map(forwardMatch.getForbiddenLocalRanges(), _function_7);
          final ArrayList<Range> newForbiddenRanges = new ArrayList<Range>(_map);
          int _remoteIndex_1 = it.getRemoteIndex();
          int _localIndex = it.getLocalIndex();
          int _minus = (_remoteIndex_1 - _localIndex);
          Range.translate(newForbiddenRanges, _minus);
          Range.union(it.getReciprocate().getForbiddenLocalRanges(), newForbiddenRanges);
        };
        IterableExtensions.<Match>filter(Iterables.<Match>concat(forwardMatch.getLocalBuffer().getMatchTable().values()), _function_5).forEach(_function_6);
        final Function1<Match, Boolean> _function_7 = (Match it) -> {
          MatchType _type_1 = it.getType();
          return Boolean.valueOf(Objects.equal(_type_1, MatchType.FORWARD));
        };
        final Consumer<Match> _function_8 = (Match it) -> {
          final Function1<Range, Range> _function_9 = (Range it_1) -> {
            Object _clone = it_1.clone();
            return ((Range) _clone);
          };
          List<Range> _map = ListExtensions.<Range, Range>map(forwardMatch.getReciprocate().getForbiddenLocalRanges(), _function_9);
          final ArrayList<Range> newForbiddenRanges = new ArrayList<Range>(_map);
          final Function1<Range, Range> _function_10 = (Range it_1) -> {
            Object _clone = it_1.clone();
            return ((Range) _clone);
          };
          List<Range> _map_1 = ListExtensions.<Range, Range>map(forwardMatch.getReciprocate().getMergeableLocalRanges(), _function_10);
          final ArrayList<Range> newMergeableRanges = new ArrayList<Range>(_map_1);
          int _remoteIndex_1 = it.getRemoteIndex();
          int _localIndex = it.getLocalIndex();
          int _minus = (_remoteIndex_1 - _localIndex);
          Range.translate(newForbiddenRanges, _minus);
          int _remoteIndex_2 = it.getRemoteIndex();
          int _localIndex_1 = it.getLocalIndex();
          int _minus_1 = (_remoteIndex_2 - _localIndex_1);
          Range.translate(newMergeableRanges, _minus_1);
          Range.union(it.getReciprocate().getForbiddenLocalRanges(), newForbiddenRanges);
          Range.union(it.getReciprocate().getMergeableLocalRanges(), newMergeableRanges);
          Match _reciprocate = it.getReciprocate();
          _reciprocate.setMergeableLocalRanges(Range.difference(it.getReciprocate().getMergeableLocalRanges(), 
            it.getReciprocate().getForbiddenLocalRanges()));
        };
        IterableExtensions.<Match>filter(Iterables.<Match>concat(forwardMatch.getRemoteBuffer().getMatchTable().values()), _function_7).forEach(_function_8);
        this.updateConflictCandidates(match);
        final Function1<Match, Boolean> _function_9 = (Match it) -> {
          return Boolean.valueOf(((!Objects.equal(it, match)) && Range.hasOverlap(it.getLocalRange(), match.getLocalIndivisibleRange())));
        };
        final Consumer<Match> _function_10 = (Match movedMatch) -> {
          final List<Match> localList = match.getLocalBuffer().getMatchTable().get(Integer.valueOf(movedMatch.getLocalIndex()));
          localList.remove(movedMatch);
          int _size = localList.size();
          boolean _equals_1 = (_size == 0);
          if (_equals_1) {
            match.getLocalBuffer().getMatchTable().remove(Integer.valueOf(movedMatch.getLocalIndex()));
          }
          movedMatch.setLocalBuffer(match.getRemoteBuffer());
          int _localIndex = movedMatch.getLocalIndex();
          int _localIndex_1 = match.getLocalIndex();
          int _remoteIndex_1 = match.getRemoteIndex();
          int _minus = (_localIndex_1 - _remoteIndex_1);
          int _minus_1 = (_localIndex - _minus);
          movedMatch.setLocalIndex(_minus_1);
          Match _reciprocate = movedMatch.getReciprocate();
          _reciprocate.setRemoteBuffer(movedMatch.getLocalBuffer());
          Match _reciprocate_1 = movedMatch.getReciprocate();
          _reciprocate_1.setRemoteIndex(movedMatch.getLocalIndex());
          List<Match> matchList = match.getRemoteBuffer().getMatchTable().get(Integer.valueOf(movedMatch.getLocalIndex()));
          if ((matchList == null)) {
            matchList = CollectionLiterals.<Match>newArrayList();
            match.getRemoteBuffer().getMatchTable().put(Integer.valueOf(movedMatch.getLocalIndex()), matchList);
          }
          matchList.add(movedMatch);
        };
        ImmutableList.<Match>copyOf(IterableExtensions.<Match>toList(IterableExtensions.<Match>filter(Iterables.<Match>concat(match.getLocalBuffer().getMatchTable().values()), _function_9))).forEach(_function_10);
        this.updateRemoteIndexes(match);
        this.updateDivisibleRanges(match);
        this.updateRemoteMergeableRange(match);
        Buffer.updateMatches(match);
        final Function1<Match, Boolean> _function_11 = (Match it) -> {
          Match _reciprocate = match.getReciprocate();
          return Boolean.valueOf((!Objects.equal(it, _reciprocate)));
        };
        Iterable<Match> matchToUpdate = IterableExtensions.<Match>filter(Iterables.<Match>concat(match.getRemoteBuffer().getMatchTable().values()), _function_11);
        while ((IterableExtensions.size(matchToUpdate) != 0)) {
          matchToUpdate = Buffer.updateConflictingMatches(matchToUpdate);
        }
        Buffer.unmatch(match);
        match.setApplied(true);
        Match _reciprocate = match.getReciprocate();
        _reciprocate.setApplied(true);
      }
    }
    this.matched = matchesCopy;
  }
  
  void updateForbiddenAndMergeableLocalRanges(final Match match) {
    Match _xifexpression = null;
    MatchType _type = match.getType();
    boolean _equals = Objects.equal(_type, MatchType.FORWARD);
    if (_equals) {
      _xifexpression = match;
    } else {
      _xifexpression = match.getReciprocate();
    }
    final Match forwardMatch = _xifexpression;
    List<Match> _conflictCandidates = forwardMatch.getConflictCandidates();
    List<Match> _conflictingMatches = forwardMatch.getConflictingMatches();
    final Consumer<Match> _function = (Match conflictMatch) -> {
      final Range impactedRange = forwardMatch.getReciprocate().getLocalImpactedRange();
      int _localIndex = conflictMatch.getLocalIndex();
      int _remoteIndex = conflictMatch.getRemoteIndex();
      int _minus = (_localIndex - _remoteIndex);
      impactedRange.translate(_minus);
      Range.union(conflictMatch.getForbiddenLocalRanges(), impactedRange);
    };
    Iterables.<Match>concat(Collections.<List<Match>>unmodifiableList(CollectionLiterals.<List<Match>>newArrayList(_conflictCandidates, _conflictingMatches))).forEach(_function);
    Match _xifexpression_1 = null;
    MatchType _type_1 = match.getType();
    boolean _equals_1 = Objects.equal(_type_1, MatchType.BACKWARD);
    if (_equals_1) {
      _xifexpression_1 = match;
    } else {
      _xifexpression_1 = match.getReciprocate();
    }
    final Match backwardMatch = _xifexpression_1;
    final Range impactedRange = backwardMatch.getReciprocate().getLocalImpactedRange();
    int _localIndex = backwardMatch.getLocalIndex();
    int _remoteIndex = backwardMatch.getRemoteIndex();
    int _minus = (_localIndex - _remoteIndex);
    impactedRange.translate(_minus);
    final List<Range> remoteMergeableRange = Range.intersection(backwardMatch.getLocalBuffer().getMergeableRanges(), impactedRange);
    final List<Range> forbiddenRanges = Range.difference(Collections.<Range>unmodifiableList(CollectionLiterals.<Range>newArrayList(impactedRange)), remoteMergeableRange);
    int _remoteIndex_1 = backwardMatch.getRemoteIndex();
    int _localIndex_1 = backwardMatch.getLocalIndex();
    int _minus_1 = (_remoteIndex_1 - _localIndex_1);
    Range.translate(remoteMergeableRange, _minus_1);
    int _remoteIndex_2 = backwardMatch.getRemoteIndex();
    int _localIndex_2 = backwardMatch.getLocalIndex();
    int _minus_2 = (_remoteIndex_2 - _localIndex_2);
    Range.translate(forbiddenRanges, _minus_2);
    List<Match> _conflictCandidates_1 = backwardMatch.getConflictCandidates();
    List<Match> _conflictingMatches_1 = backwardMatch.getConflictingMatches();
    final Consumer<Match> _function_1 = (Match conflictMatch) -> {
      final Function1<Range, Range> _function_2 = (Range it) -> {
        Object _clone = it.clone();
        return ((Range) _clone);
      };
      List<Range> _map = ListExtensions.<Range, Range>map(remoteMergeableRange, _function_2);
      final ArrayList<Range> newMergeableRanges = new ArrayList<Range>(_map);
      final Function1<Range, Range> _function_3 = (Range it) -> {
        Object _clone = it.clone();
        return ((Range) _clone);
      };
      List<Range> _map_1 = ListExtensions.<Range, Range>map(forbiddenRanges, _function_3);
      final ArrayList<Range> newForbiddenRanges = new ArrayList<Range>(_map_1);
      int _localIndex_3 = conflictMatch.getLocalIndex();
      int _remoteIndex_3 = conflictMatch.getRemoteIndex();
      int _minus_3 = (_localIndex_3 - _remoteIndex_3);
      Range.translate(newMergeableRanges, _minus_3);
      int _localIndex_4 = conflictMatch.getLocalIndex();
      int _remoteIndex_4 = conflictMatch.getRemoteIndex();
      int _minus_4 = (_localIndex_4 - _remoteIndex_4);
      Range.translate(newForbiddenRanges, _minus_4);
      Range.union(conflictMatch.getMergeableLocalRanges(), newMergeableRanges);
      Range.union(conflictMatch.getForbiddenLocalRanges(), newForbiddenRanges);
      conflictMatch.setMergeableLocalRanges(Range.difference(conflictMatch.getMergeableLocalRanges(), 
        conflictMatch.getForbiddenLocalRanges()));
    };
    Iterables.<Match>concat(Collections.<List<Match>>unmodifiableList(CollectionLiterals.<List<Match>>newArrayList(_conflictCandidates_1, _conflictingMatches_1))).forEach(_function_1);
  }
  
  static void updateMatches(final Match match) {
    final List<Pair<Match, Range>> modifiedMatches = CollectionLiterals.<Pair<Match, Range>>newArrayList();
    final Function1<Match, Boolean> _function = (Match it) -> {
      Match _reciprocate = match.getReciprocate();
      return Boolean.valueOf((!Objects.equal(it, _reciprocate)));
    };
    final Consumer<Match> _function_1 = (Match testedMatch) -> {
      final Range localIndivisibleRange = testedMatch.getLocalIndivisibleRange();
      final Range remoteIndivisibleRange = testedMatch.getReciprocate().getLocalIndivisibleRange();
      int _localIndex = testedMatch.getLocalIndex();
      int _remoteIndex = testedMatch.getRemoteIndex();
      int _minus = (_localIndex - _remoteIndex);
      remoteIndivisibleRange.translate(_minus);
      Range _xifexpression = null;
      int _length = localIndivisibleRange.getLength();
      int _length_1 = remoteIndivisibleRange.getLength();
      boolean _lessEqualsThan = (_length <= _length_1);
      if (_lessEqualsThan) {
        _xifexpression = localIndivisibleRange;
      } else {
        _xifexpression = remoteIndivisibleRange;
      }
      final Range smallestRange = _xifexpression;
      Range _localRange = testedMatch.getLocalRange();
      boolean _notEquals = (!Objects.equal(smallestRange, _localRange));
      if (_notEquals) {
        Pair<Match, Range> _pair = new Pair<Match, Range>(testedMatch, smallestRange);
        modifiedMatches.add(_pair);
      }
    };
    IterableExtensions.<Match>filter(Iterables.<Match>concat(match.getRemoteBuffer().getMatchTable().values()), _function).forEach(_function_1);
    final Consumer<Pair<Match, Range>> _function_2 = (Pair<Match, Range> it) -> {
      final Match modifiedMatch = it.getKey();
      final Range newRange = it.getValue();
      modifiedMatch.setLength(newRange.getLength());
      Match _reciprocate = modifiedMatch.getReciprocate();
      _reciprocate.setLength(newRange.getLength());
      final int originalIndex = modifiedMatch.getLocalIndex();
      final int originalRemoteIndex = modifiedMatch.getRemoteIndex();
      int _start = newRange.getStart();
      boolean _notEquals = (_start != originalIndex);
      if (_notEquals) {
        modifiedMatch.setLocalIndex(newRange.getStart());
        int _start_1 = newRange.getStart();
        int _plus = (originalRemoteIndex + _start_1);
        int _minus = (_plus - originalIndex);
        modifiedMatch.setRemoteIndex(_minus);
        modifiedMatch.getLocalBuffer().getMatchTable().get(Integer.valueOf(originalIndex)).remove(modifiedMatch);
        List<Match> _elvis = null;
        List<Match> _get = modifiedMatch.getLocalBuffer().getMatchTable().get(Integer.valueOf(newRange.getStart()));
        if (_get != null) {
          _elvis = _get;
        } else {
          ArrayList<Match> _xblockexpression = null;
          {
            final ArrayList<Match> newList = CollectionLiterals.<Match>newArrayList();
            modifiedMatch.getLocalBuffer().getMatchTable().put(Integer.valueOf(newRange.getStart()), newList);
            _xblockexpression = newList;
          }
          _elvis = _xblockexpression;
        }
        final List<Match> localList = _elvis;
        localList.add(modifiedMatch);
        boolean _isEmpty = modifiedMatch.getLocalBuffer().getMatchTable().get(Integer.valueOf(originalIndex)).isEmpty();
        if (_isEmpty) {
          modifiedMatch.getLocalBuffer().getMatchTable().remove(Integer.valueOf(originalIndex));
        }
        Match _reciprocate_1 = modifiedMatch.getReciprocate();
        _reciprocate_1.setLocalIndex(modifiedMatch.getRemoteIndex());
        Match _reciprocate_2 = modifiedMatch.getReciprocate();
        _reciprocate_2.setRemoteIndex(modifiedMatch.getLocalIndex());
        modifiedMatch.getRemoteBuffer().getMatchTable().get(Integer.valueOf(originalRemoteIndex)).remove(modifiedMatch.getReciprocate());
        List<Match> _elvis_1 = null;
        List<Match> _get_1 = modifiedMatch.getRemoteBuffer().getMatchTable().get(Integer.valueOf(modifiedMatch.getRemoteIndex()));
        if (_get_1 != null) {
          _elvis_1 = _get_1;
        } else {
          ArrayList<Match> _xblockexpression_1 = null;
          {
            final ArrayList<Match> newList = CollectionLiterals.<Match>newArrayList();
            modifiedMatch.getRemoteBuffer().getMatchTable().put(Integer.valueOf(modifiedMatch.getRemoteIndex()), newList);
            _xblockexpression_1 = newList;
          }
          _elvis_1 = _xblockexpression_1;
        }
        final List<Match> remoteList = _elvis_1;
        remoteList.add(modifiedMatch.getReciprocate());
        boolean _isEmpty_1 = modifiedMatch.getRemoteBuffer().getMatchTable().get(Integer.valueOf(originalRemoteIndex)).isEmpty();
        if (_isEmpty_1) {
          modifiedMatch.getRemoteBuffer().getMatchTable().remove(Integer.valueOf(originalRemoteIndex));
        }
      }
    };
    modifiedMatches.forEach(_function_2);
    List<Match> _list = IterableExtensions.<Match>toList(Iterables.<Match>concat(match.getRemoteBuffer().getMatchTable().values()));
    final ArrayList<Match> matches = new ArrayList<Match>(_list);
    final Set<Integer> redundantMatches = CollectionLiterals.<Integer>newHashSet();
    int i = 0;
    while ((i < (matches.size() - 1))) {
      {
        boolean _contains = redundantMatches.contains(Integer.valueOf(i));
        boolean _not = (!_contains);
        if (_not) {
          final Match currentMatch = matches.get(i);
          int j = (i + 1);
          while ((j < matches.size())) {
            {
              final Match redundantMatch = matches.get(j);
              boolean _equals = Objects.equal(currentMatch, redundantMatch);
              if (_equals) {
                redundantMatches.add(Integer.valueOf(j));
                final Function1<Match, Boolean> _function_3 = (Match it) -> {
                  return Boolean.valueOf((((!currentMatch.getConflictCandidates().contains(it)) && 
                    (!currentMatch.getConflictingMatches().contains(it))) && (!Objects.equal(it, currentMatch))));
                };
                List<Match> _list_1 = IterableExtensions.<Match>toList(IterableExtensions.<Match>filter(redundantMatch.getConflictCandidates(), _function_3));
                ArrayList<Match> transferredConflictCandidates = new ArrayList<Match>(_list_1);
                final Consumer<Match> _function_4 = (Match it) -> {
                  it.getConflictCandidates().remove(redundantMatch);
                  it.getConflictCandidates().add(currentMatch);
                  currentMatch.getConflictCandidates().add(it);
                };
                transferredConflictCandidates.forEach(_function_4);
                final Function1<Match, Boolean> _function_5 = (Match it) -> {
                  return Boolean.valueOf((((!currentMatch.getReciprocate().getConflictCandidates().contains(it)) && 
                    (!currentMatch.getReciprocate().getConflictingMatches().contains(it))) && 
                    (!Objects.equal(it, currentMatch.getReciprocate()))));
                };
                List<Match> _list_2 = IterableExtensions.<Match>toList(IterableExtensions.<Match>filter(redundantMatch.getReciprocate().getConflictCandidates(), _function_5));
                ArrayList<Match> _arrayList = new ArrayList<Match>(_list_2);
                transferredConflictCandidates = _arrayList;
                final Consumer<Match> _function_6 = (Match it) -> {
                  it.getConflictCandidates().remove(redundantMatch.getReciprocate());
                  it.getConflictCandidates().add(currentMatch.getReciprocate());
                  currentMatch.getReciprocate().getConflictCandidates().add(it);
                };
                transferredConflictCandidates.forEach(_function_6);
                final Function1<Match, Boolean> _function_7 = (Match it) -> {
                  return Boolean.valueOf(((!currentMatch.getConflictingMatches().contains(it)) && (!Objects.equal(it, currentMatch))));
                };
                List<Match> _list_3 = IterableExtensions.<Match>toList(IterableExtensions.<Match>filter(redundantMatch.getConflictingMatches(), _function_7));
                ArrayList<Match> transferredConflictingMatches = new ArrayList<Match>(_list_3);
                final Consumer<Match> _function_8 = (Match it) -> {
                  it.getConflictCandidates().remove(currentMatch);
                  currentMatch.getConflictCandidates().remove(it);
                  it.getConflictingMatches().remove(redundantMatch);
                  it.getConflictingMatches().add(currentMatch);
                  currentMatch.getConflictingMatches().add(it);
                };
                transferredConflictingMatches.forEach(_function_8);
                final Function1<Match, Boolean> _function_9 = (Match it) -> {
                  return Boolean.valueOf(((!currentMatch.getReciprocate().getConflictingMatches().contains(it)) && 
                    (!Objects.equal(it, currentMatch.getReciprocate()))));
                };
                List<Match> _list_4 = IterableExtensions.<Match>toList(IterableExtensions.<Match>filter(redundantMatch.getReciprocate().getConflictingMatches(), _function_9));
                ArrayList<Match> _arrayList_1 = new ArrayList<Match>(_list_4);
                transferredConflictingMatches = _arrayList_1;
                final Consumer<Match> _function_10 = (Match it) -> {
                  it.getConflictCandidates().remove(currentMatch.getReciprocate());
                  currentMatch.getReciprocate().getConflictCandidates().remove(it);
                  it.getConflictingMatches().remove(redundantMatch.getReciprocate());
                  it.getConflictingMatches().add(currentMatch.getReciprocate());
                  currentMatch.getReciprocate().getConflictingMatches().add(it);
                };
                transferredConflictingMatches.forEach(_function_10);
                Match _xifexpression = null;
                MatchType _type = currentMatch.getType();
                boolean _equals_1 = Objects.equal(_type, MatchType.FORWARD);
                if (_equals_1) {
                  _xifexpression = currentMatch;
                } else {
                  _xifexpression = currentMatch.getReciprocate();
                }
                final Match forwardMatch = _xifexpression;
                Match _xifexpression_1 = null;
                MatchType _type_1 = redundantMatch.getType();
                boolean _equals_2 = Objects.equal(_type_1, MatchType.FORWARD);
                if (_equals_2) {
                  _xifexpression_1 = redundantMatch;
                } else {
                  _xifexpression_1 = redundantMatch.getReciprocate();
                }
                final Match redundantForwardMatch = _xifexpression_1;
                forwardMatch.setForbiddenLocalRanges(Range.intersection(forwardMatch.getForbiddenLocalRanges(), 
                  redundantForwardMatch.getForbiddenLocalRanges()));
                Match _reciprocate = forwardMatch.getReciprocate();
                _reciprocate.setForbiddenLocalRanges(Range.intersection(forwardMatch.getReciprocate().getForbiddenLocalRanges(), redundantForwardMatch.getReciprocate().getForbiddenLocalRanges()));
                Match _reciprocate_1 = forwardMatch.getReciprocate();
                _reciprocate_1.setMergeableLocalRanges(Range.intersection(forwardMatch.getReciprocate().getMergeableLocalRanges(), redundantForwardMatch.getReciprocate().getMergeableLocalRanges()));
              }
              j = (j + 1);
            }
          }
        }
        i = (i + 1);
      }
    }
    int _size = redundantMatches.size();
    boolean _greaterThan = (_size > 0);
    if (_greaterThan) {
      final Function1<Integer, Match> _function_3 = (Integer it) -> {
        return matches.get((it).intValue());
      };
      List<Match> _list_1 = IterableExtensions.<Match>toList(IterableExtensions.<Integer, Match>map(redundantMatches, _function_3));
      final ArrayList<Match> removedMatches = new ArrayList<Match>(_list_1);
      final Consumer<Match> _function_4 = (Match it) -> {
        Buffer.unmatch(it);
      };
      removedMatches.forEach(_function_4);
    }
  }
  
  /**
   * Must be called before {@link ScriptRunner#updateConflictingMatches()
   * updating conflicting matches}.
   */
  void updateConflictCandidates(final Match match) {
    final ArrayList<Match> newConflicts = CollectionLiterals.<Match>newArrayList();
    if (((!match.getReciprocate().getConflictCandidates().isEmpty()) || (!match.getReciprocate().getConflictingMatches().isEmpty()))) {
      final Function1<Match, Boolean> _function = (Match it) -> {
        MatchType _type = it.getType();
        MatchType _type_1 = match.getType();
        return Boolean.valueOf(Objects.equal(_type, _type_1));
      };
      final Consumer<Match> _function_1 = (Match otherMatch) -> {
        otherMatch.getReciprocate().getConflictCandidates().addAll(match.getReciprocate().getConflictCandidates());
        otherMatch.getReciprocate().getConflictCandidates().addAll(match.getReciprocate().getConflictingMatches());
        newConflicts.add(otherMatch.getReciprocate());
      };
      IterableExtensions.<Match>filter(Iterables.<Match>concat(match.getRemoteBuffer().getMatchTable().values()), _function).forEach(_function_1);
      final Consumer<Match> _function_2 = (Match it) -> {
        it.getConflictCandidates().addAll(newConflicts);
      };
      match.getReciprocate().getConflictCandidates().forEach(_function_2);
      final Consumer<Match> _function_3 = (Match it) -> {
        it.getConflictCandidates().addAll(newConflicts);
      };
      match.getReciprocate().getConflictingMatches().forEach(_function_3);
      newConflicts.clear();
    }
    if (((!match.getConflictCandidates().isEmpty()) || (!match.getConflictingMatches().isEmpty()))) {
      final Function1<Match, Boolean> _function_4 = (Match it) -> {
        MatchType _type = it.getType();
        MatchType _type_1 = match.getType();
        return Boolean.valueOf((!Objects.equal(_type, _type_1)));
      };
      final Consumer<Match> _function_5 = (Match otherMatch) -> {
        otherMatch.getReciprocate().getConflictCandidates().addAll(match.getConflictCandidates());
        otherMatch.getReciprocate().getConflictCandidates().addAll(match.getConflictingMatches());
        newConflicts.add(otherMatch.getReciprocate());
      };
      IterableExtensions.<Match>filter(Iterables.<Match>concat(match.getLocalBuffer().getMatchTable().values()), _function_4).forEach(_function_5);
      final Consumer<Match> _function_6 = (Match it) -> {
        it.getConflictCandidates().addAll(newConflicts);
      };
      match.getConflictCandidates().forEach(_function_6);
      final Consumer<Match> _function_7 = (Match it) -> {
        it.getConflictCandidates().addAll(newConflicts);
      };
      match.getConflictingMatches().forEach(_function_7);
      newConflicts.clear();
    }
  }
  
  /**
   * This method update the {@link Match#getConflictingMatches()
   * conflictingMatches} {@link List} of all the {@link Match} passed as a
   * parameter. To do so, the method scan all the {@link
   * Match#getConflictCandidates() conflictCandidates} of each {@link Match}
   * and check if any candidate has an overlapping range. In such case, the
   * candidate is moved to the {@link Match#getConflictingMatches()
   * conflictingMatches} of the {@link Match} and its {@link
   * Match#getReciprocate() reciprocate}. To ensure consistency, one should
   * make sure that if a {@link Match} is updated with this method, then all
   * the {@link Match matches} contained in its {@link
   * Match#getConflictCandidates() conflictCandidates} {@link List} are
   * updated too.
   * 
   * @param matchList
   * 		The {@link Iterable} of {@link Match} to update
   * 
   * @return the {@link List} of {@link Match} updated by the method
   */
  static List<Match> updateConflictingMatches(final Iterable<Match> matchList) {
    final ArrayList<Match> updatedMatches = CollectionLiterals.<Match>newArrayList();
    final Consumer<Match> _function = (Match match) -> {
      Iterator<Match> iter = match.getConflictCandidates().iterator();
      while (iter.hasNext()) {
        {
          final Match candidate = iter.next();
          boolean _hasOverlap = Range.hasOverlap(candidate.getReciprocate().getLocalImpactedRange(), match.getReciprocate().getLocalImpactedRange());
          if (_hasOverlap) {
            iter.remove();
            match.getConflictingMatches().add(candidate);
            updatedMatches.add(candidate);
          }
        }
      }
      iter = match.getReciprocate().getConflictCandidates().iterator();
      while (iter.hasNext()) {
        {
          final Match candidate = iter.next();
          boolean _hasOverlap = Range.hasOverlap(candidate.getReciprocate().getLocalImpactedRange(), match.getLocalImpactedRange());
          if (_hasOverlap) {
            iter.remove();
            match.getReciprocate().getConflictingMatches().add(candidate);
            boolean _contains = updatedMatches.contains(candidate.getReciprocate());
            boolean _not = (!_contains);
            if (_not) {
              updatedMatches.add(candidate.getReciprocate());
            }
          }
        }
      }
    };
    matchList.forEach(_function);
    return updatedMatches;
  }
  
  /**
   * MUST be called before updateRemoteMergeableRange because the updated local
   * indexes are used in the current function, which cause an update of the mergeable ranges.
   * @return true of the indexes were updated, false otherwise
   */
  boolean updateRemoteIndexes(final Match match) {
    boolean res = false;
    final Range localIndivisibleRange = match.getLocalIndivisibleRange();
    int _remoteIndex = match.getRemoteIndex();
    int _localIndex = match.getLocalIndex();
    int _minus = (_remoteIndex - _localIndex);
    localIndivisibleRange.translate(_minus);
    int _start = localIndivisibleRange.getStart();
    int _minIndex = match.getRemoteBuffer().getMinIndex();
    boolean _lessThan = (_start < _minIndex);
    if (_lessThan) {
      res = true;
      Buffer _remoteBuffer = match.getRemoteBuffer();
      _remoteBuffer.minIndex = localIndivisibleRange.getStart();
    }
    int _end = localIndivisibleRange.getEnd();
    int _maxIndex = match.getRemoteBuffer().getMaxIndex();
    boolean _greaterThan = (_end > _maxIndex);
    if (_greaterThan) {
      res = true;
      Buffer _remoteBuffer_1 = match.getRemoteBuffer();
      _remoteBuffer_1.maxIndex = localIndivisibleRange.getEnd();
    }
    return res;
  }
  
  /**
   * Also update the {@link #getDivisibilityRequiredMatches()
   * divisibilityRequiredMatches} {@link List} of the {@link Buffer}.
   */
  void updateDivisibleRanges(final Match match) {
    final Range localRange = match.getLocalRange();
    final Function1<Range, Boolean> _function = (Range it) -> {
      return Boolean.valueOf(Range.hasOverlap(it, localRange));
    };
    final Function1<Range, Range> _function_1 = (Range it) -> {
      Object _clone = it.clone();
      return ((Range) _clone);
    };
    final List<Range> localIndivisibleRanges = IterableExtensions.<Range>toList(IterableExtensions.<Range, Range>map(IterableExtensions.<Range>filter(match.getLocalBuffer().getIndivisibleRanges(), _function), _function_1));
    int _remoteIndex = match.getRemoteIndex();
    int _localIndex = match.getLocalIndex();
    int _minus = (_remoteIndex - _localIndex);
    Range.translate(localIndivisibleRanges, _minus);
    Range.lazyUnion(match.getRemoteBuffer().getIndivisibleRanges(), localIndivisibleRanges);
    final Function1<Range, Boolean> _function_2 = (Range it) -> {
      return Boolean.valueOf(Range.hasOverlap(it, match.getReciprocate().getLocalRange()));
    };
    int _size = IterableExtensions.size(IterableExtensions.<Range>filter(match.getRemoteBuffer().getIndivisibleRanges(), _function_2));
    boolean _greaterThan = (_size > 1);
    if (_greaterThan) {
      match.getRemoteBuffer().getDivisibilityRequiredMatches().addAll(match.getLocalBuffer().getDivisibilityRequiredMatches());
    }
  }
  
  /**
   * Must be called after updateRemoteIndexesAndDivisibleRanges
   */
  void updateRemoteMergeableRange(final Match match) {
    final Range involvedRange = match.getLocalIndivisibleRange();
    final List<Range> localMergeableRange = Range.intersection(match.getLocalBuffer().getMergeableRanges(), involvedRange);
    int _remoteIndex = match.getRemoteIndex();
    int _localIndex = match.getLocalIndex();
    int _minus = (_remoteIndex - _localIndex);
    involvedRange.translate(_minus);
    final List<Range> remoteMergeableRange = Range.intersection(match.getRemoteBuffer().getMergeableRanges(), involvedRange);
    int _localIndex_1 = match.getLocalIndex();
    int _minus_1 = (-_localIndex_1);
    Range.translate(localMergeableRange, _minus_1);
    int _remoteIndex_1 = match.getRemoteIndex();
    int _minus_2 = (-_remoteIndex_1);
    Range.translate(remoteMergeableRange, _minus_2);
    final List<Range> resultMergeableRange = Range.intersection(localMergeableRange, remoteMergeableRange);
    final List<Range> unmergeableRange = Range.difference(remoteMergeableRange, resultMergeableRange);
    Range.translate(unmergeableRange, match.getRemoteIndex());
    Buffer _remoteBuffer = match.getRemoteBuffer();
    _remoteBuffer.mergeableRanges = Range.difference(match.getRemoteBuffer().getMergeableRanges(), unmergeableRange);
  }
  
  /**
   * Remove the current {@link Match} from its {@link #getLocalBuffer()
   * localBuffer} and {@link #getRemoteBuffer() remoteBuffer} {@link
   * Buffer#getMatchTable() matchTable}.
   * Each time the current match is retrieved in a List, the reference
   * equality (===) from XTend is used. Indeed, several matches might be
   * {@link Match#equals(Object) equals} which would result in removing the
   * wrong match.
   */
  static void unmatch(final Match match) {
    final Match it = match;
    final List<Match> localList = it.getLocalBuffer().getMatchTable().get(Integer.valueOf(it.getLocalIndex()));
    Iterator<Match> iter = localList.iterator();
    while (iter.hasNext()) {
      Match _next = iter.next();
      boolean _tripleEquals = (_next == it);
      if (_tripleEquals) {
        iter.remove();
      }
    }
    int _size = localList.size();
    boolean _equals = (_size == 0);
    if (_equals) {
      it.getLocalBuffer().getMatchTable().remove(Integer.valueOf(it.getLocalIndex()));
    }
    final List<Match> remoteList = it.getRemoteBuffer().getMatchTable().get(Integer.valueOf(it.getRemoteIndex()));
    iter = remoteList.iterator();
    while (iter.hasNext()) {
      Match _next = iter.next();
      Match _reciprocate = it.getReciprocate();
      boolean _tripleEquals = (_next == _reciprocate);
      if (_tripleEquals) {
        iter.remove();
      }
    }
    int _size_1 = remoteList.size();
    boolean _equals_1 = (_size_1 == 0);
    if (_equals_1) {
      it.getRemoteBuffer().getMatchTable().remove(Integer.valueOf(it.getRemoteIndex()));
    }
    final Consumer<Match> _function = (Match it_1) -> {
      final Iterator<Match> iterator = it_1.getConflictCandidates().iterator();
      while (iterator.hasNext()) {
        Match _next = iterator.next();
        boolean _tripleEquals = (_next == match);
        if (_tripleEquals) {
          iterator.remove();
        }
      }
    };
    match.getConflictCandidates().forEach(_function);
    final Consumer<Match> _function_1 = (Match it_1) -> {
      final Iterator<Match> iterator = it_1.getConflictingMatches().iterator();
      while (iterator.hasNext()) {
        Match _next = iterator.next();
        boolean _tripleEquals = (_next == match);
        if (_tripleEquals) {
          iterator.remove();
        }
      }
    };
    match.getConflictingMatches().forEach(_function_1);
    final Consumer<Match> _function_2 = (Match it_1) -> {
      final Iterator<Match> iterator = it_1.getConflictCandidates().iterator();
      while (iterator.hasNext()) {
        Match _next = iterator.next();
        Match _reciprocate = match.getReciprocate();
        boolean _tripleEquals = (_next == _reciprocate);
        if (_tripleEquals) {
          iterator.remove();
        }
      }
    };
    match.getReciprocate().getConflictCandidates().forEach(_function_2);
    final Consumer<Match> _function_3 = (Match it_1) -> {
      final Iterator<Match> iterator = it_1.getConflictingMatches().iterator();
      while (iterator.hasNext()) {
        Match _next = iterator.next();
        Match _reciprocate = match.getReciprocate();
        boolean _tripleEquals = (_next == _reciprocate);
        if (_tripleEquals) {
          iterator.remove();
        }
      }
    };
    match.getReciprocate().getConflictingMatches().forEach(_function_3);
  }
  
  /**
   * This method checks if the given {@link Match Matches} are sufficient to
   * complete the {@link #getDivisibilityRequiredMatches()} condition.
   */
  boolean doesCompleteRequiredMatches(final Iterable<Match> matches) {
    final Iterator<List<Match>> iter = this.getDivisibilityRequiredMatches().iterator();
    while (iter.hasNext()) {
      {
        final List<Match> list = iter.next();
        final Function1<Match, Boolean> _function = (Match it) -> {
          return Boolean.valueOf(it.isApplied());
        };
        boolean _forall = IterableExtensions.<Match>forall(list, _function);
        if (_forall) {
          iter.remove();
        }
      }
    }
    final Function1<List<Match>, Boolean> _function = (List<Match> list) -> {
      return Boolean.valueOf(IterableExtensions.<Match>toList(matches).containsAll(list));
    };
    return IterableExtensions.<List<Match>>forall(this.getDivisibilityRequiredMatches(), _function);
  }
  
  @Pure
  public int getMaxIndex() {
    return this.maxIndex;
  }
  
  @Pure
  public int getMinIndex() {
    return this.minIndex;
  }
  
  @Pure
  public Map<Integer, List<Match>> getMatchTable() {
    return this.matchTable;
  }
  
  @Pure
  public List<Match> getMatched() {
    return this.matched;
  }
  
  public void setMatched(final List<Match> matched) {
    this.matched = matched;
  }
  
  @Pure
  public boolean isHost() {
    return this.host;
  }
  
  public void setHost(final boolean host) {
    this.host = host;
  }
  
  @Pure
  public String getName() {
    return this.name;
  }
  
  @Pure
  public int getNbTokens() {
    return this.nbTokens;
  }
  
  @Pure
  public int getTokenSize() {
    return this.tokenSize;
  }
  
  @Pure
  public DAGVertex getDagVertex() {
    return this.dagVertex;
  }
  
  @Pure
  public SDFEdge getSdfEdge() {
    return this.sdfEdge;
  }
  
  @Pure
  public List<Range> getIndivisibleRanges() {
    return this.indivisibleRanges;
  }
  
  public void setIndivisibleRanges(final List<Range> indivisibleRanges) {
    this.indivisibleRanges = indivisibleRanges;
  }
  
  @Pure
  public List<List<Match>> getDivisibilityRequiredMatches() {
    return this.divisibilityRequiredMatches;
  }
  
  public void setDivisibilityRequiredMatches(final List<List<Match>> divisibilityRequiredMatches) {
    this.divisibilityRequiredMatches = divisibilityRequiredMatches;
  }
  
  @Pure
  public Map<Range, Pair<Buffer, Integer>> getAppliedMatches() {
    return this.appliedMatches;
  }
  
  @Pure
  public boolean isOriginallyMergeable() {
    return this.originallyMergeable;
  }
  
  @Pure
  public List<Range> getMergeableRanges() {
    return this.mergeableRanges;
  }
  
  public void setMergeableRanges(final List<Range> mergeableRanges) {
    this.mergeableRanges = mergeableRanges;
  }
}
