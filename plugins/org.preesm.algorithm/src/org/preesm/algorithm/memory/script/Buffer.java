/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
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
package org.preesm.algorithm.memory.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 * This class implements the Buffer concept used in memory scripts.
 *
 * @author ahonorat
 * @author kdesnos
 */
public class Buffer {

  /**
   * Identify which data ranges of a {@link Buffer} are matched multiple times. A range is matched multiple times if
   * several matches involving this ranges are stored in the {@link Buffer#getMatchTable() match table} of the
   * {@link Buffer}. For example, if these to calls are executed: <code>a.matchWith(0,b,0,3)</code> and
   * <code>a.matchWith(0,b,3,3)</code>, then a[0..3[ is matched multiple times.
   *
   * @return a {@link Map} containing the start end end of ranges matched multiple times.
   */
  List<Range> getMultipleMatchRange() {
    return Buffer.getOverlappingRanges(this.matchTable.values().stream().flatMap(List::stream).toList());
  }

  /**
   * Same as {@link #getMultipleMatchRange(Buffer)} but tests only the given {@link List} of {@link Match matches}. This
   * method does not check if all {@link Match matches} in the {@link List} have the same {@link #getLocalBuffer() local
   * buffer}.
   *
   * @param matches
   *          the {@link List} of {@link Match matches}
   * @return a {@link List} of {@link Range} containing the overlapping ranges of the matches.
   */
  static List<Range> getOverlappingRanges(final List<Match> matches) {
    final List<Range> matchRanges = new ArrayList<>();
    final List<Range> multipleMatchRanges = new ArrayList<>();

    // For each Match
    for (final Match match : matches) {
      final Range newRange = match.getLocalRange();
      // Get the intersection of the match and existing match ranges
      final List<Range> intersections = Range.intersection(matchRanges, newRange);
      Range.union(multipleMatchRanges, intersections);
      // Update the existing match ranges
      Range.union(matchRanges, newRange);
    }

    return multipleMatchRanges;
  }

  /**
   * Test if the {@link Buffer} is partially matched.<br>
   * <br>
   * A {@link Buffer} is partially matched if only part of its token range (i.e. from 0 to {@link #getNbTokens()
   * nbTokens}*{@link #getTokenSize() tokenSize}) are involved in a {@link Match} in the {@link Buffer}
   * {@link Buffer#_matchTable match table}. This condition is sufficient since all "virtual" tokens of a {@link Buffer}
   * will always have an overlapping indivisible range with real tokens.
   *
   * @return <code>true</code> if the {@link Buffer} is completely matched, and <code>false</code> otherwise.
   */
  boolean isCompletelyMatched() {
    final List<Range> coveredRange = new ArrayList<>();
    final Iterator<Entry<Long, List<Match>>> iterEntry = this.matchTable.entrySet().iterator();
    boolean stop = false;

    while (iterEntry.hasNext() && !stop) {
      final Entry<Long, List<Match>> entry = iterEntry.next();
      final Iterator<Match> iterMatch = entry.getValue().iterator();
      while (iterMatch.hasNext() && !stop) {
        final Match match = iterMatch.next();
        final Range addedRange = Range.union(coveredRange, match.getLocalRange());

        // Set stop to true if the range covers the complete token range
        stop = ((addedRange.getStart() <= 0) && (addedRange.getEnd() >= (this.tokenSize * this.nbTokens))) || stop;
      }
    }

    // If the loops were stopped, a complete range was reached
    return stop;
  }

  /**
   * Test if all {@link Match matches} contained in the {@link Buffer#_machTable matchTable} are reciprocal.<br>
   * <br>
   *
   * A {@link Match} is reciprocal if the remote {@link Match#buffer} contains an reciprocal {@link Match} in its
   * {@link Buffer#_matchTable matchTable}.
   */
  boolean isReciprocal() {
    return this.matchTable.entrySet().stream().allMatch(entry -> {
      final List<Match> matches = entry.getValue();
      final long localIdx = entry.getKey();
      // for all matches
      return matches.stream().allMatch(match -> {
        final List<Match> remoteMatches = match.getRemoteBuffer().matchTable.get(match.getRemoteIndex());
        return (remoteMatches != null) && remoteMatches
            .contains(new Match(match.getRemoteBuffer(), match.getRemoteIndex(), this, localIdx, match.getLength()));
      });
    });
  }

  /**
   * The objective of this method is to merge as many matches as possible from the {@link Buffer}
   * {@link Buffer#_matchTable match tables}.<br>
   * <br>
   *
   * Two matches are mergeable if they are consecutive and if they match consecutive targets.<br>
   * Example 1: <code>a[0..3]<->b[1..4] and a[4..5]<->b[5..6]</code> are valid candidates.<br>
   * Example 2: <code>a[0..3]<->b[1..4] and a[5..6]<->b[5..6]</code> are not valid candidates. Merging buffers does not
   * change the divisibility of the buffer since if contiguous matches are applied, at least one of them will become
   * indivisible (since subparts of a divided buffer cannot be match within divided buffers.)<br>
   * <b> Before using this method, the {@link Buffer} must pass all checks performed by the {@link ScriptRunner#check()}
   * method.</b>
   *
   * @param buffer
   *          The {@link Buffer} whose {@link Buffer#_matchTable matchTable} is simplified.
   * @param processedMatch
   *          A {@link List} containing {@link Match matches} that will be ignored during simplification. This list will
   *          be updated during the method execution by adding to it the {@link Match#reciprocate} of the processed
   *          {@link Match matches}.
   */
  void simplifyMatches(final List<Match> processedMatch) {
    final List<Long> removedEntry = new ArrayList<>();

    // Process the match table
    for (final Entry<Long, List<Match>> entry : this.matchTable.entrySet()) {
      final long localIdx = entry.getKey();
      final List<Match> matchSet = entry.getValue();
      // For each match
      for (final Match match : matchSet) {
        if (processedMatch.contains(match)) {
          continue;
        }
        Match remMatch = null;
        do {

          // Check if a consecutive match exist
          final List<Match> candidateSet = this.matchTable.get(localIdx + match.getLength());

          // Since Buffer#check() is supposed valid
          // at most one candidate can satisfy the conditions
          remMatch = null;
          if (candidateSet != null) {
            for (final Match candidate : candidateSet) {
              // same target
              if (candidate.getRemoteBuffer().equals(match.getRemoteBuffer())
                  && (candidate.getRemoteIndex() == (match.getRemoteIndex() + match.getLength()))) {
                remMatch = candidate;
                break;
              }
            }
          }
          if (remMatch != null) {

            // Remove the consecutive match from matchTables
            candidateSet.remove(remMatch);
            final List<Match> remMatchSet = remMatch.getRemoteBuffer().matchTable.get(remMatch.getRemoteIndex());
            remMatchSet.remove(remMatch.getReciprocate());

            // Remove empty matchLists from the matchTable
            if (remMatchSet.isEmpty()) {
              remMatch.getRemoteBuffer().matchTable.remove(remMatch.getRemoteIndex());
            }
            if (candidateSet.isEmpty()) {
              removedEntry.add(localIdx + match.getLength());
            }

            // Lengthen the existing match
            match.setLength(match.getLength() + remMatch.getLength());
            match.getReciprocate().setLength(match.getLength());
          }
        } while (remMatch != null);
        // Put the reciprocate match in the in the processes list
        processedMatch.add(match.getReciprocate());
      }

    }

    // Remove empty matchLists from matchTable
    removedEntry.forEach(this.matchTable::remove);
  }

  /**
   * cf {@link #minIndex}.
   */
  long maxIndex;

  /**
   * Minimum index for the buffer content. Constructor initialize this value to 0 but it is possible to lower this value
   * by matching another buffer on the "edge" of this one.<br>
   * For example: <code>this.matchWith(-3, a, 0, 6)</code> results in matching this[-3..2] with a[0..5], thus lowering
   * this.minIndex to -3.
   */
  long minIndex;

  public long getBufferSize() {
    return maxIndex - minIndex;
  }

  /**
   * This table is protected to ensure that matches are set only by using {@link #matchWith(long,Buffer,long)} methods
   * in the scripts.
   */
  final Map<Long, List<Match>> matchTable;

  /**
   * This property is used to mark the {@link Buffer buffers} that were {@link #applyMatches(List) matched}. Originally
   * set to <code>null</code>, it is replaced by a {@link List} of applied {@link Match} in the
   * {@link #applyMatches(List) applyMatches} method.
   */
  List<Match> matched = null;

  /**
   * This property is set to <code>true</code> if a remote {@link Buffer} was merged within the current {@link Buffer}
   */
  boolean host = false;

  final String name;

  private final long nbTokens;

  public long getNbTokens() {
    return this.nbTokens;
  }

  private final long tokenSize;

  public long getTokenSize() {
    return this.tokenSize;
  }

  public long getBufferSizeInBit() {
    return nbTokens * tokenSize;
  }

  public long getBufferSizeInByte() {
    return (nbTokens * tokenSize + 7L) / 8L;
  }

  /* 2 strings used for proper error reporting and logging */
  private final String  vertexName;
  private final DAGEdge loggingEdgeName;

  /**
   * This {@link List} of {@link Range} is used to store its indivisible sub-parts. A buffer can effectively be divided
   * only if its is not indivisible and if the division imposed by the matches do not break any indivisible range.
   */
  List<Range> indivisibleRanges;

  /**
   * This {@link List} contains all {@link Match} that must be applied to authorize the division of a {@link Buffer}.
   * The {@link List} contains {@link List} of {@link Match}. To authorize a division, each sublist must contain enough
   * {@link Match#isApplied() applied} {@link Match} to cover all the tokens (real and virtual) of the original
   * {@link Match#getLocalBuffer() localBuffer} of the {@link Match matches}.
   */
  List<List<Match>> divisibilityRequiredMatches;

  protected final Map<Range, Pair<Buffer, Long>> appliedMatches;

  /**
   * This flag is set at the {@link Buffer} instantiation to indicate whether the buffer is mergeable or not. If the
   * buffer is mergeable, all its virtual tokens will be associated to mergeable ranges. Otherwise they won't.
   */
  final boolean originallyMergeable;

  List<Range> mergeableRanges;

  /**
   * Constructor for the {@link Buffer}.
   *
   * @param name
   *          A {@link String} corresponding to the final name of the buffer.
   * @param nbTokens
   *          The number of tokens stored in this buffer.
   * @param tokenSize
   *          The size of one token of the buffer.
   */
  public Buffer(final DAGEdge edge, final String dagVertexName, final String name, final long nbTokens,
      final long tokenSize, final boolean mergeable) {
    this.loggingEdgeName = edge;
    this.vertexName = dagVertexName;
    this.name = name;
    this.nbTokens = nbTokens;
    this.tokenSize = tokenSize;
    this.matchTable = new LinkedHashMap<>();
    this.appliedMatches = new LinkedHashMap<>();
    this.minIndex = 0;
    this.maxIndex = nbTokens * tokenSize;
    this.originallyMergeable = mergeable;
    this.mergeableRanges = new ArrayList<>();
    if (mergeable) {
      this.mergeableRanges.add(new Range(0, nbTokens * tokenSize));
    }
    this.indivisibleRanges = new ArrayList<>();
    this.divisibilityRequiredMatches = new ArrayList<>();
  }

  /**
   * {@link Match} part of the current {@link Buffer} with part of another {@link Buffer}. Example:
   * <code>a.matchWith(3,b,7,5)</code> matches a[3..7] with b[7..11]. Matching two {@link Buffer buffers} means that the
   * matched ranges may be merged, i.e. they may be allocated in the same memory space.<br>
   * The localIdx, remoteIdx and size represent a number of token. (cf. production and consumption rate from the SDF
   * graph).
   * <p>
   * May be called from a BeanShell memory script.
   *
   * @exception Exception
   *              may be thrown if the matched ranges both have elements outside of their {@link Buffer} indexes
   *              ({@link #_maxIndex} and {@link #_minIndex}).
   *
   *
   * @param localIdx
   *          start index of the matched range for the local {@link Buffer}.
   * @param buffer
   *          remote {@link Buffer}
   * @param remoteIdx
   *          start index of the matched range for the remote {@link Buffer}
   * @param size
   *          the size of the matched range
   * @return the created local {@link Match}
   */
  public Match matchWith(final long localIdx, final Buffer buffer, final long remoteIdx, final long size) {

    if (this.tokenSize != buffer.tokenSize) {
      throw new PreesmRuntimeException("Cannot match " + this.getVertexName() + "." + this.name + "with "
          + buffer.getVertexName() + "." + buffer.name + " because buffers have different token sizes ("
          + this.tokenSize + " != " + buffer.tokenSize + " )");
    }

    final long maxLocal = (localIdx + size) - 1;
    final long maxRemote = (remoteIdx + size) - 1;

    // Test if a matched range is completely out of real tokens
    if ((localIdx >= this.nbTokens) || (maxLocal < 0)) {
      final long maxLTokens = this.nbTokens - 1;
      throw new PreesmRuntimeException("Cannot match " + this.getVertexName() + "." + this.name + "[" + localIdx + ".."
          + maxLocal + "] and " + buffer.getVertexName() + "." + buffer.name + "[" + remoteIdx + ".." + maxRemote
          + "] because no \"real\" token from " + this.getVertexName() + "." + this.name + "[0.." + maxLTokens
          + "] is matched.");
    }

    if ((remoteIdx >= buffer.nbTokens) || (maxRemote < 0)) {
      final long maxRTokens = buffer.nbTokens - 1;
      throw new PreesmRuntimeException("Cannot match " + this.getVertexName() + "." + this.name + "[" + localIdx + ".."
          + maxLocal + "] and " + buffer.getVertexName() + "." + buffer.name + "[" + remoteIdx + ".." + maxRemote
          + "] because no \"real\" token from " + buffer.getVertexName() + "." + buffer.name + "[0.." + maxRTokens
          + "] is matched.");
    }

    // Are "virtual" tokens matched together

    // Both ranges begins before the first token
    final boolean bIndexes = (localIdx < 0) && (remoteIdx < 0);
    // or both buffers ends after the last token
    final boolean bTokens = (maxLocal >= this.nbTokens) && (maxRemote >= buffer.nbTokens);
    // or local range begins with less real tokens than the number of virtual tokens beginning remote range
    final boolean bLocalVirtual = (localIdx >= 0) && ((this.nbTokens - localIdx) <= -Math.min(0, remoteIdx));
    // or remote range begins with less real tokens than the number of virtual tokens beginning local range
    final boolean bRemoteVirtual = (remoteIdx >= 0) && ((buffer.nbTokens - remoteIdx) <= -Math.min(0, localIdx));
    if (bIndexes || bTokens || bLocalVirtual || bRemoteVirtual) {
      throw new PreesmRuntimeException("Cannot match " + this.getVertexName() + "." + this.name + "[" + localIdx + ".."
          + maxLocal + "] and " + buffer.getVertexName() + "." + buffer.name + "[" + remoteIdx + ".." + maxRemote
          + "] because \"virtual tokens\" cannot be matched together.\n" + "Information: " + this.getVertexName() + "."
          + this.name + " size = " + this.nbTokens + " and " + buffer.getVertexName() + "." + buffer.name + " size = "
          + buffer.nbTokens + ".");
    }

    return bitMatchWith(localIdx * this.tokenSize, buffer, remoteIdx * this.tokenSize, size * this.tokenSize, false);
  }

  /**
   * May be called from a BeanShell memory script.
   *
   * @param localByteIdx
   *          start index of the matched range for the local {@link Buffer}.
   * @param buffer
   *          remote {@link Buffer}
   * @param remoteByteIdx
   *          start index of the matched range for the remote {@link Buffer}
   * @param byteSize
   *          the size of the matched range
   * @return the created local {@link Match}
   */
  public Match byteMatchWith(final long localByteIdx, final Buffer buffer, final long remoteByteIdx,
      final long byteSize) {
    return bitMatchWith(localByteIdx * 8L, buffer, remoteByteIdx * 8L, byteSize * 8L, true);
  }

  /**
   * May be called from a BeanShell memory script.
   *
   * @param localBitIdx
   *          start index of the matched range for the local {@link Buffer}.
   * @param buffer
   *          remote {@link Buffer}
   * @param remoteBitIdx
   *          start index of the matched range for the remote {@link Buffer}
   * @param bitSize
   *          the size of the matched range
   * @return the created local {@link Match}
   */
  public Match bitMatchWith(final long localBitIdx, final Buffer buffer, final long remoteBitIdx, final long bitSize) {
    return bitMatchWith(localBitIdx, buffer, remoteBitIdx, bitSize, true);
  }

  /**
   * Cf. {@link Buffer#bitMatchWith(long, Buffer, long, long)} with possibility to disable the checking.
   */
  private Match bitMatchWith(final long localBitIdx, final Buffer buffer, final long remoteBitIdx, final long bitSize,
      final boolean check) {
    final long bitLMax = (localBitIdx + bitSize) - 1;
    final long bitRMax = (remoteBitIdx + bitSize) - 1;

    // Test if a matched range is completely out of real bits
    // This rule is indispensable to make sure that "virtual" token
    // exist for a reason. Without this rule, the match application would
    // fall down, especially because if a pure virtual token was matched
    // this match would not be forwarding when matching the "real" tokens
    // since only matches overlapping the match.localIndivisibleRange are
    // forwarded when this match is applied.
    // eg.
    //
    // Actor A with one input (2 tokens) and one output (4 tokens)
    // A.in tokens {0, 1} and virtual tokens {-3, -2, -1}
    // A.out tokens {0, 1, 2, 3}
    //
    // Match1 A.in[-3..-1[ with A.out[0..2[
    // Match2 A.in[0..2[ with A.out[2..4[
    //
    // Because of a graph edge, A.in is matched into B.out(2 tokens),
    // Then, Match1 will not be forwarded to B.out because it has no overlap with the
    // real tokens.
    if (check) {
      if ((localBitIdx >= (this.nbTokens * this.tokenSize)) || (bitLMax < 0)) {
        final long tokenLMax = (this.nbTokens * this.tokenSize) - 1;
        throw new PreesmRuntimeException("Cannot match bits " + this.getVertexName() + "." + this.name + "["
            + localBitIdx + ".." + bitLMax + "] and " + buffer.getVertexName() + "." + buffer.name + "[" + remoteBitIdx
            + ".." + bitRMax + "] because no \"real\" bit from " + this.getVertexName() + "." + this.name + "[0.."
            + tokenLMax + "] is matched.");
      }
      if ((remoteBitIdx >= (buffer.nbTokens * buffer.tokenSize)) || (bitRMax < 0)) {
        final long tokenRMax = (buffer.nbTokens * buffer.tokenSize) - 1;
        throw new PreesmRuntimeException("Cannot match bit " + this.getVertexName() + "." + this.name + "["
            + localBitIdx + ".." + bitLMax + "] and " + buffer.getVertexName() + "." + buffer.name + "[" + remoteBitIdx
            + ".." + bitRMax + "] because no \"real\" bit from " + buffer.getVertexName() + "." + buffer.name + "[0.."
            + tokenRMax + "] is matched.");
      }

      // Are "virtual" tokens matched together

      // Both ranges begins before the first bit
      final boolean bPositiveIndex = (localBitIdx < 0) && (remoteBitIdx < 0);
      // or both buffers ends after the last bit
      final boolean bTooLargeBuffer = (bitLMax >= (this.nbTokens * this.tokenSize))
          && (bitRMax >= (buffer.nbTokens * buffer.tokenSize));
      // or local range begins with less real bits than the number of virtual bits beginning remote range
      final boolean bLocalVirtual = (localBitIdx >= 0)
          && (((this.nbTokens * this.tokenSize) - localBitIdx) <= -Math.min(0, remoteBitIdx));
      // or remote range begins with less real bits than the number of virtual bits beginning local range
      final boolean bRemoteVirtual = (remoteBitIdx >= 0)
          && (((buffer.nbTokens * buffer.tokenSize) - remoteBitIdx) <= -Math.min(0, localBitIdx));
      if (bPositiveIndex || bTooLargeBuffer || bLocalVirtual || bRemoteVirtual) {
        throw new PreesmRuntimeException("Cannot match bits " + this.getVertexName() + "." + this.name + "["
            + localBitIdx + ".." + bitLMax + "] and " + buffer.getVertexName() + "." + buffer.name + "[" + remoteBitIdx
            + ".." + bitRMax + "] because \"virtual bits\" cannot be matched together.\nInformation: "
            + this.getVertexName() + "." + this.name + " size = " + (this.nbTokens * this.tokenSize) + " and "
            + buffer.getVertexName() + "." + buffer.name + " size = " + (buffer.nbTokens * buffer.tokenSize) + ".");
      }
    }

    // If needed, update the buffers min/max indexes
    if (!((localBitIdx >= 0) && (bitLMax < (this.nbTokens * this.tokenSize)))) {
      this.minIndex = Math.min(this.minIndex, localBitIdx);
      this.maxIndex = Math.max(this.maxIndex, (localBitIdx + bitSize));
    }
    if (!((remoteBitIdx >= 0) && (bitRMax < (buffer.nbTokens * buffer.tokenSize)))) {
      buffer.minIndex = Math.min(buffer.minIndex, remoteBitIdx);
      buffer.maxIndex = Math.max(buffer.maxIndex, (remoteBitIdx + bitSize));
    }

    // Do the match
    // if (!this.matchTable.containsKey(localBitIdx)) {
    // this.matchTable.put(localBitIdx, new ArrayList<>());
    // }
    this.matchTable.computeIfAbsent(localBitIdx, k -> new ArrayList<>());

    final List<Match> matchSet = this.matchTable.get(localBitIdx);
    final Match localMatch = new Match(this, localBitIdx, buffer, remoteBitIdx, bitSize);
    matchSet.add(localMatch);

    if (!buffer.matchTable.containsKey(remoteBitIdx)) {
      buffer.matchTable.put(remoteBitIdx, new ArrayList<>());
    }
    final List<Match> remoteMatchSet = buffer.matchTable.get(remoteBitIdx);

    final Match remoteMatch = new Match(buffer, remoteBitIdx, this, localBitIdx, bitSize);
    remoteMatchSet.add(remoteMatch);

    localMatch.setReciprocate(remoteMatch);
    return localMatch;
  }

  /**
   * A {@link Buffer} is divisible if its {@link #getIndivisibleRanges() indivisible ranges} are not unique and
   * completely cover the 0 to {@link #getNbTokens() nbTokens}*{@link #getTokenSize() tokenSize} {@link Range}, if it is
   * {@link #isCompletelyMatched() completelyMatched}, and if it is matched only in {@link #isIndivisible() indivisible}
   * {@link Buffer buffers}.<br>
   * <b> An {@link Buffer} that is not {@link #isIndivisible() indivisible} is not necessarily divisible. Indeed, it
   * might fulfill parts of the conditions to be divisible.</b>
   *
   * @return <code>true</code> if the {@link Buffer} is divisible, <code> false</code> otherwise.
   */

  boolean isDivisible() {
    if (isCompletelyMatched() && (this.indivisibleRanges.size() > 1)) {
      // Test that all ranges are covered by the indivisible ranges
      final List<Range> copy = new ArrayList<>(this.indivisibleRanges.stream().map(Range::copy).toList());
      final Range firstElement = copy.get(0);
      copy.remove(0);
      final Range coveredRange = Range.union(copy, firstElement);
      final List<Range> difference = new Range(0, this.nbTokens * this.tokenSize).difference(coveredRange);
      final boolean b = difference.isEmpty();
      return b && this.matchTable.values().stream().flatMap(List::stream)
          .allMatch(it -> it.getRemoteBuffer().isIndivisible());
    }
    return false;
  }

  /**
   * A {@link Buffer} is indivisible if its {@link #getIndivisibleRanges() indivisibleRanges} attribute contains a
   * unique {@link Range} that covers all the {@link #getMinIndex() minIndex} to {@link #getMaxIndex() maxIndex}
   * {@link Range}. <br>
   * <b> An {@link Buffer} that is not {@link #isIndivisible() indivisible} is not necessarily {@link #isDivisible()
   * divisible}. Indeed, it might fulfill parts of the conditions to be divisible.</b>
   */
  private boolean isIndivisible() {
    return (this.indivisibleRanges.size() == 1) && (this.indivisibleRanges.get(0).getStart() == this.minIndex)
        && (this.indivisibleRanges.get(0).getEnd() == this.maxIndex);
  }

  /**
   * We do not check that the match is possible ! We just apply it and assume all checks are performed somewhere else !
   * The local buffer is merged into the remote buffer The local buffer does not "exists" afterwards
   */
  void applyMatches(final List<Match> matches) {

    // Check that all match have the current buffer as local
    if (matches.stream().anyMatch(it -> !it.getLocalBuffer().equals(this))) {
      throw new PreesmRuntimeException(
          "Incorrect call to applyMatches method.\nOne of the given matches does not belong to the this Buffer.");
    }

    // copy the list to iterate on it
    // Otherwise the list would be modified during the iteration since it
    // is the result of a flatten or a filter operation.
    final List<Match> matchesCopy = new ArrayList<>(matches);

    // Check that the matches completely cover the buffer
    final List<Range> matchedRange = matchesCopy.stream().collect(ArrayList<Range>::new,
        (previousRes, currentMatch) -> Range.union(previousRes, currentMatch.getLocalIndivisibleRange()), Range::union);
    final Range tokenRange = new Range(0, this.tokenSize * this.nbTokens);
    if (!Range.intersection(matchedRange, tokenRange).get(0).equals(tokenRange)) {
      throw new PreesmRuntimeException("Incorrect call to applyMatches method.\n "
          + "All real token must be covered by the given matches.\n" + matches);
    }

    // Check that the matches do not overlap
    if (matchesCopy.stream().anyMatch(match1 -> matchesCopy.stream().filter(it -> it != match1)
        .anyMatch(match2 -> Range.hasOverlap(match1.getLocalIndivisibleRange(), match2.getLocalIndivisibleRange())))) {
      throw new PreesmRuntimeException("Incorrect call to applyMatches method.\n "
          + "Given matches are overlapping in the localBuffer.\n" + matches);
    }

    // Check that all matches are applicable
    matches.stream().filter(it -> !it.isApplicable() || !it.getReciprocate().isApplicable()).findFirst()
        .ifPresent(o -> {
          throw new PreesmRuntimeException(
              "Incorrect call to applyMatches method.\n " + "One or more applied matches are not applicable.\n" + o);
        });

    for (final Match match : matchesCopy) {

      this.appliedMatches.put(match.getLocalIndivisibleRange(),
          new Pair<>(match.getRemoteBuffer(), match.getRemoteIndex()));
      match.getRemoteBuffer().host = true;

      // Fill the forbiddenLocalRanges of conflictCandidates and conflictingMatches
      // of the applied match
      updateForbiddenAndMergeableLocalRanges(match);

      // Transfer the forbiddenLocalRanges of the applied match to the
      // matches of its local and remote buffers that have no conflicts
      // with the appliedMatch or its reciprocate
      Match tmpMatch = match;
      if (match.getType() != MatchType.FORWARD) {
        tmpMatch = match.getReciprocate();
      }
      final Match forwardMatch = tmpMatch;// must be final to be used in lambda

      // For each backward match of the localBuffer (i.e. not conflicting with the applied match)
      forwardMatch.getLocalBuffer().matchTable.values().stream().flatMap(List::stream)
          .filter(it -> it.getType() == MatchType.BACKWARD).forEach(item -> {
            // Copy the forbiddenLocalRanges of the applied forward match
            final List<
                Range> newForbiddenRanges = forwardMatch.getForbiddenLocalRanges().stream().map(Range::copy).toList();
            // translate to the backward match remoteBuffer indexes
            Range.translate(newForbiddenRanges, item.getRemoteIndex() - item.getLocalIndex());
            // Add it to the forward match (i.e. the reciprocate of the backward)
            Range.union(item.getReciprocate().getForbiddenLocalRanges(), newForbiddenRanges);
          });

      // For each forward match of the remoteBuffer (i.e. not conflicting with the applied match)
      forwardMatch.getRemoteBuffer().matchTable.values().stream().flatMap(List::stream)
          .filter(it -> it.getType() == MatchType.FORWARD).forEach(item -> {

            // Copy the forbiddenLocalRanges and mergeableLocalRange of the applied backward match
            final List<Range> newForbiddenRanges = forwardMatch.getReciprocate().getForbiddenLocalRanges().stream()
                .map(Range::copy).toList();
            final List<Range> newMergeableRanges = forwardMatch.getReciprocate().getMergeableLocalRanges().stream()
                .map(Range::copy).toList();
            // translate to the forward match remoteBuffer indexes
            Range.translate(newForbiddenRanges, item.getRemoteIndex() - item.getLocalIndex());
            Range.translate(newMergeableRanges, item.getRemoteIndex() - item.getLocalIndex());
            // Add it to the backward match (i.e. the reciprocate of the forward)
            Range.union(item.getReciprocate().getForbiddenLocalRanges(), newForbiddenRanges);
            Range.union(item.getReciprocate().getMergeableLocalRanges(), newMergeableRanges);
            // Remove forbiddenRanges from mergeableRanges
            item.getReciprocate().setMergeableLocalRanges(Range.difference(
                item.getReciprocate().getMergeableLocalRanges(), item.getReciprocate().getForbiddenLocalRanges()));
          });

      // Update the conflictCandidates
      // Must be done befor forwarding third-party matches
      updateConflictCandidates(match);

      // Move all third-party matches from the matched range of the merged buffer
      final List<Match> ze = match.getLocalBuffer().matchTable.values().stream().flatMap(List::stream)
          .filter(it -> !it.equals(match) && Range.hasOverlap(it.getLocalRange(), match.getLocalIndivisibleRange()))
          .toList();
      for (final Match movedMatch : ze) {
        // Remove old match from original match list
        final List<Match> localList = match.getLocalBuffer().matchTable.get(movedMatch.getLocalIndex());
        localList.remove(movedMatch);
        if (localList.isEmpty()) {
          match.getLocalBuffer().matchTable.remove(movedMatch.getLocalIndex());
        }
        // Change the match local buffer and index
        // Length and remoteBuffer are unchanged
        movedMatch.setLocalBuffer(match.getRemoteBuffer());
        movedMatch.setLocalIndex(movedMatch.getLocalIndex() - (match.getLocalIndex() - match.getRemoteIndex()));
        // Update the reciprocate
        movedMatch.getReciprocate().setRemoteBuffer(movedMatch.getLocalBuffer());
        movedMatch.getReciprocate().setRemoteIndex(movedMatch.getLocalIndex());
        // Put the moved match in its new host matchTable
        List<Match> matchList = match.getRemoteBuffer().matchTable.get(movedMatch.getLocalIndex());
        if (matchList == null) {
          matchList = new ArrayList<>();
          match.getRemoteBuffer().matchTable.put(movedMatch.getLocalIndex(), matchList);
        }
        matchList.add(movedMatch);
      }

      // Update the min and max index of the remoteBuffer (if necessary)
      // Must be called before updateRemoteMergeableRange(match)
      updateRemoteIndexes(match);

      // Update divisability if remote buffer
      // The divisability update must not be applied if the applied match involves
      // the division of the local buffer, instead the remote buffer should become !
      // non divisable ! <= Note Since buffer division is conditioned by the
      // indivisibility of the remote buffer, this remark should probably be ignored
      updateDivisibleRanges(match);

      // Update the mergeable range of the remote buffer
      updateRemoteMergeableRange(match);

      // Update Matches
      Buffer.updateMatches(match);

      // Update conflicting matches
      List<Match> matchToUpdate = match.getRemoteBuffer().matchTable.values().stream().flatMap(List::stream)
          .filter(it -> !it.equals(match.getReciprocate())).toList();
      while (!matchToUpdate.isEmpty()) {
        matchToUpdate = Buffer.updateConflictingMatches(matchToUpdate);
      }

      // Remove the applied match from the buffers match table
      // (local and reciprocate)
      Buffer.unmatch(match);

      // Match was applied (and reciprocate)
      match.setApplied(true);
      match.getReciprocate().setApplied(true);
    }

    // Mark the buffer as Matched
    this.matched = matchesCopy;

  }

  private void unionForwardMatchConflictCandidatesRanges(final Match forwardMatch, final List<Match> matches) {
    for (final Match conflictMatch : matches) {
      // Must be extracted for each iteration because the union modifies the range
      final Range impactedRange = forwardMatch.getReciprocate().getLocalImpactedRange();
      impactedRange.translate(conflictMatch.getLocalIndex() - conflictMatch.getRemoteIndex());
      Range.union(conflictMatch.getForbiddenLocalRanges(), impactedRange);
    }
  }

  private void unionBackwardMatchConflictCandidatesRanges(final List<Range> remoteMergeableRange,
      final List<Range> forbiddenRanges, final List<Match> matches) {
    for (final Match conflictMatch : matches) {
      final List<Range> newMergeableRanges = remoteMergeableRange.stream().map(Range::copy).toList();
      final List<Range> newForbiddenRanges = forbiddenRanges.stream().map(Range::copy).toList();
      // translate it to localBuffer of conflictMatches indexes
      Range.translate(newMergeableRanges, conflictMatch.getLocalIndex() - conflictMatch.getRemoteIndex());
      Range.translate(newForbiddenRanges, conflictMatch.getLocalIndex() - conflictMatch.getRemoteIndex());
      Range.union(conflictMatch.getMergeableLocalRanges(), newMergeableRanges);
      Range.union(conflictMatch.getForbiddenLocalRanges(), newForbiddenRanges);
      // remove forbidden Ranges from mergeable ranges
      conflictMatch.setMergeableLocalRanges(
          Range.difference(conflictMatch.getMergeableLocalRanges(), conflictMatch.getForbiddenLocalRanges()));
    }
  }

  private void updateForbiddenAndMergeableLocalRanges(final Match match) {

    // For the forward match, simply fill the forbidden ranges
    Match forwardMatch = match;
    Match backwardMatch = match.getReciprocate();
    if (match.getType() != MatchType.FORWARD) {
      final Match tmp = forwardMatch;
      forwardMatch = backwardMatch;
      backwardMatch = tmp;
    }

    unionForwardMatchConflictCandidatesRanges(forwardMatch, forwardMatch.getConflictCandidates());
    unionForwardMatchConflictCandidatesRanges(forwardMatch, forwardMatch.getConflictingMatches());

    // For backward match, fill the forbidden an mergeable ranges (if any)

    // Get the target mergeable range
    final Range impactedRange = backwardMatch.getReciprocate().getLocalImpactedRange();
    impactedRange.translate(backwardMatch.getLocalIndex() - backwardMatch.getRemoteIndex());
    final List<
        Range> remoteMergeableRange = Range.intersection(backwardMatch.getLocalBuffer().mergeableRanges, impactedRange);

    // No need to remove forbidden ranges from it. Indeed, if there are such
    // range, the match couldn't have been applied
    // Compute forbidden ranges
    final List<Range> forbiddenRanges = Range.difference(Arrays.asList(impactedRange), remoteMergeableRange);

    // translate it back to source indexes
    Range.translate(remoteMergeableRange, backwardMatch.getRemoteIndex() - backwardMatch.getLocalIndex());
    Range.translate(forbiddenRanges, backwardMatch.getRemoteIndex() - backwardMatch.getLocalIndex());

    unionBackwardMatchConflictCandidatesRanges(remoteMergeableRange, forbiddenRanges,
        backwardMatch.getConflictCandidates());
    unionBackwardMatchConflictCandidatesRanges(remoteMergeableRange, forbiddenRanges,
        backwardMatch.getConflictingMatches());
  }

  static void updateMatches(final Match match) {

    // 1- For all matches of the remote buffer (old and newly added)
    // 1.1- If the match (local and remote) ranges falls within
    // indivisible range(s) larger than the match length
    // Then:
    // 1.1.1- the match must be enlarged to cover this range
    // Several matches might become redundant (i.e. identical) in the process
    final List<Pair<Match, Range>> modifiedMatches = new ArrayList<>();
    match.getRemoteBuffer().matchTable.values().stream().flatMap(List::stream)
        .filter(it -> !it.equals(match.getReciprocate())).forEach(testedMatch -> {
          // Get the aligned smallest indivisible range (local or remote)
          final Range localIndivisibleRange = testedMatch.getLocalIndivisibleRange();
          final Range remoteIndivisibleRange = testedMatch.getReciprocate().getLocalIndivisibleRange();
          remoteIndivisibleRange.translate(testedMatch.getLocalIndex() - testedMatch.getRemoteIndex());
          Range smallestRange = localIndivisibleRange;
          if (localIndivisibleRange.getLength() > remoteIndivisibleRange.getLength()) {
            smallestRange = remoteIndivisibleRange;
          }
          // Check if the range was modified
          if (!smallestRange.equals(testedMatch.getLocalRange())) {
            // Need to enlarge the match
            modifiedMatches.add(new Pair<>(testedMatch, smallestRange));
          }
        });

    modifiedMatches.stream().forEach(it -> {

      final Match modifiedMatch = it.getKey();
      final Range newRange = it.getValue();
      // Update the match
      modifiedMatch.setLength(newRange.getLength());
      modifiedMatch.getReciprocate().setLength(newRange.getLength());
      // If the match must be moved
      final long originalIndex = modifiedMatch.getLocalIndex();
      final long originalRemoteIndex = modifiedMatch.getRemoteIndex();
      if (newRange.getStart() != originalIndex) {

        // Move the local match
        modifiedMatch.setLocalIndex(newRange.getStart());
        modifiedMatch.setRemoteIndex((originalRemoteIndex + newRange.getStart()) - originalIndex);
        modifiedMatch.getLocalBuffer().matchTable.get(originalIndex).remove(modifiedMatch);
        List<Match> localList = modifiedMatch.getLocalBuffer().matchTable.get(newRange.getStart());
        if (localList == null) {
          localList = new ArrayList<>();
          modifiedMatch.getLocalBuffer().matchTable.put(newRange.getStart(), localList);
        }
        localList.add(modifiedMatch);
        // Remove the old list if it is empty
        if (modifiedMatch.getLocalBuffer().matchTable.get(originalIndex).isEmpty()) {
          modifiedMatch.getLocalBuffer().matchTable.remove(originalIndex);
        }

        // Move the remote match
        modifiedMatch.getReciprocate().setLocalIndex(modifiedMatch.getRemoteIndex());
        modifiedMatch.getReciprocate().setRemoteIndex(modifiedMatch.getLocalIndex());
        modifiedMatch.getRemoteBuffer().matchTable.get(originalRemoteIndex).remove(modifiedMatch.getReciprocate());
        List<Match> remoteList = modifiedMatch.getRemoteBuffer().matchTable.get(modifiedMatch.getRemoteIndex());
        if (remoteList == null) {
          remoteList = new ArrayList<>();
          modifiedMatch.getRemoteBuffer().matchTable.put(modifiedMatch.getRemoteIndex(), remoteList);
        }
        remoteList.add(modifiedMatch.getReciprocate());

        // Remove the old list if it is empty
        if (modifiedMatch.getRemoteBuffer().matchTable.get(originalRemoteIndex).isEmpty()) {
          modifiedMatch.getRemoteBuffer().matchTable.remove(originalRemoteIndex);
        }
      }
    });

    // Find redundant matches
    final List<Match> matches = match.getRemoteBuffer().matchTable.values().stream().flatMap(List::stream).toList();
    final Set<Integer> redundantMatches = new LinkedHashSet<>();
    int i = 0;
    while (i < (matches.size() - 1)) {

      // If the current match is not already redundant
      if (!redundantMatches.contains(i)) {
        final Match currentMatch = matches.get(i);
        int j = i + 1;
        while (j < matches.size()) {
          final Match redundantMatch = matches.get(j);
          if (currentMatch.equals(redundantMatch)) {

            // Matches are redundant
            redundantMatches.add(j);

            // It does not matter if the redundant matches were conflicting.
            // If this code is reached, it means that the were not since they
            // now have the same target and destination.
            // Transfer conflictCandidates from the redundantMatch to the currentMatch
            List<Match> transferredConflictCandidates = redundantMatch.getConflictCandidates().stream()
                .filter(it -> !currentMatch.getConflictCandidates().contains(it)
                    && !currentMatch.getConflictingMatches().contains(it) && !it.equals(currentMatch))
                .toList();
            transferredConflictCandidates.forEach(it -> {
              it.getConflictCandidates().remove(redundantMatch);
              it.getConflictCandidates().add(currentMatch);
              currentMatch.getConflictCandidates().add(it);
            });

            // And reciprocates
            transferredConflictCandidates = redundantMatch.getReciprocate().getConflictCandidates().stream()
                .filter(it -> !currentMatch.getReciprocate().getConflictCandidates().contains(it)
                    && !currentMatch.getReciprocate().getConflictingMatches().contains(it)
                    && !it.equals(currentMatch.getReciprocate()))
                .toList();
            transferredConflictCandidates.forEach(it -> {
              it.getConflictCandidates().remove(redundantMatch.getReciprocate());
              it.getConflictCandidates().add(currentMatch.getReciprocate());
              currentMatch.getReciprocate().getConflictCandidates().add(it);
            });

            // Transfer conflictCandidates from the redundantMatch to the currentMatch
            List<Match> transferredConflictingMatches = redundantMatch.getConflictingMatches().stream()
                .filter(it -> !currentMatch.getConflictingMatches().contains(it) && !it.equals(currentMatch)).toList();
            transferredConflictingMatches.forEach(it -> {
              // remove from conflict candidates if it was present
              it.getConflictCandidates().remove(currentMatch);
              currentMatch.getConflictCandidates().remove(it);
              it.getConflictingMatches().remove(redundantMatch);
              it.getConflictingMatches().add(currentMatch);
              currentMatch.getConflictingMatches().add(it);
            });

            // and reciprocates
            transferredConflictingMatches = redundantMatch.getReciprocate().getConflictingMatches().stream()
                .filter(it -> !currentMatch.getReciprocate().getConflictingMatches().contains(it)
                    && !it.equals(currentMatch.getReciprocate()))
                .toList();
            transferredConflictingMatches.forEach(it -> {
              // remove from conflict candidates if it was present
              it.getConflictCandidates().remove(currentMatch.getReciprocate());
              currentMatch.getReciprocate().getConflictCandidates().remove(it);
              it.getConflictingMatches().remove(redundantMatch.getReciprocate());
              it.getConflictingMatches().add(currentMatch.getReciprocate());
              currentMatch.getReciprocate().getConflictingMatches().add(it);
            });

            // Update localForbiddenRanges and localMergeableRanges
            Match forwardMatch = currentMatch;
            if (currentMatch.getType() != MatchType.FORWARD) {
              forwardMatch = currentMatch.getReciprocate();
            }
            Match redundantForwardMatch = redundantMatch;
            if (redundantMatch.getType() != MatchType.FORWARD) {
              redundantForwardMatch = redundantMatch.getReciprocate();
            }
            forwardMatch.setForbiddenLocalRanges(Range.intersection(forwardMatch.getForbiddenLocalRanges(),
                redundantForwardMatch.getForbiddenLocalRanges()));

            forwardMatch.getReciprocate()
                .setForbiddenLocalRanges(Range.intersection(forwardMatch.getReciprocate().getForbiddenLocalRanges(),
                    redundantForwardMatch.getReciprocate().getForbiddenLocalRanges()));
            forwardMatch.getReciprocate()
                .setMergeableLocalRanges(Range.intersection(forwardMatch.getReciprocate().getMergeableLocalRanges(),
                    redundantForwardMatch.getReciprocate().getMergeableLocalRanges()));

          }
          j = j + 1;
        }
      }
      i = i + 1;

    }

    // do the removal :
    if (!redundantMatches.isEmpty()) {
      final List<Match> removedMatches = redundantMatches.stream().map(matches::get).toList();
      removedMatches.forEach(Buffer::unmatch);
    }
  }

  /**
   * Must be called before {@link ScriptRunner#updateConflictingMatches() updating conflicting matches}.
   */

  private void updateConflictCandidates(final Match match) {

    // 1. Conflict candidates of the applied local->remote match are
    // added to all remote->other matches (except inter siblings and
    // the already conflicting to remote->local (i.e. the backward if
    // local->remote is forward or vice versa))
    // 2. Conflict candidates of the applied remote->local match are
    // added to all local->other matches (except inter siblings and
    // the already conflicting to local->remote (i.e. the forward if
    // remote->local is backward or vice versa))
    // 1
    final List<Match> newConflicts = new ArrayList<>();
    if (!match.getReciprocate().getConflictCandidates().isEmpty()
        || !match.getReciprocate().getConflictingMatches().isEmpty()) {
      match.getRemoteBuffer().matchTable.values().stream().flatMap(List::stream)
          .filter(it -> it.getType() == match.getType()).forEach(otherMatch -> {
            otherMatch.getReciprocate().getConflictCandidates().addAll(match.getReciprocate().getConflictCandidates());
            otherMatch.getReciprocate().getConflictCandidates().addAll(match.getReciprocate().getConflictingMatches());
            newConflicts.add(otherMatch.getReciprocate());
          });
      match.getReciprocate().getConflictCandidates().forEach(it -> it.getConflictCandidates().addAll(newConflicts));
      match.getReciprocate().getConflictingMatches().forEach(it -> it.getConflictCandidates().addAll(newConflicts));
      newConflicts.clear();
    }

    // 2.
    if (!match.getConflictCandidates().isEmpty() || !match.getConflictingMatches().isEmpty()) {
      match.getLocalBuffer().matchTable.values().stream().flatMap(List::stream)
          .filter(it -> it.getType() != match.getType()).forEach(otherMatch -> {
            otherMatch.getReciprocate().getConflictCandidates().addAll(match.getConflictCandidates());
            otherMatch.getReciprocate().getConflictCandidates().addAll(match.getConflictingMatches());
            newConflicts.add(otherMatch.getReciprocate());
          });
      match.getConflictCandidates().forEach(it -> it.getConflictCandidates().addAll(newConflicts));
      match.getConflictingMatches().forEach(it -> it.getConflictCandidates().addAll(newConflicts));
      newConflicts.clear();
    }
  }

  /**
   * This method update the {@link Match#getConflictingMatches() conflictingMatches} {@link List} of all the
   * {@link Match} passed as a parameter. To do so, the method scan all the {@link Match#getConflictCandidates()
   * conflictCandidates} of each {@link Match} and check if any candidate has an overlapping range. In such case, the
   * candidate is moved to the {@link Match#getConflictingMatches() conflictingMatches} of the {@link Match} and its
   * {@link Match#getReciprocate() reciprocate}. To ensure consistency, one should make sure that if a {@link Match} is
   * updated with this method, then all the {@link Match matches} contained in its {@link Match#getConflictCandidates()
   * conflictCandidates} {@link List} are updated too.
   *
   * @param matchList
   *          The {@link List} of {@link Match} to update
   *
   * @return the {@link List} of {@link Match} updated by the method
   */
  static List<Match> updateConflictingMatches(final List<Match> matchList) {

    final List<Match> updatedMatches = new ArrayList<>();
    matchList.forEach(match -> {
      // Check all the conflict candidates
      Iterator<Match> iter = match.getConflictCandidates().iterator();
      while (iter.hasNext()) {
        final Match candidate = iter.next();
        if (Range.hasOverlap(candidate.getReciprocate().getLocalImpactedRange(),
            match.getReciprocate().getLocalImpactedRange())) {
          iter.remove();

          // Add the candidate to the conflicting matches
          match.getConflictingMatches().add(candidate);

          // Remove it from the reciprocate candidates (if it was present)
          updatedMatches.add(candidate);
        }
      }
      // Do the same for reciprocate
      iter = match.getReciprocate().getConflictCandidates().iterator();
      while (iter.hasNext()) {
        final Match candidate = iter.next();
        if (Range.hasOverlap(candidate.getReciprocate().getLocalImpactedRange(), match.getLocalImpactedRange())) {
          iter.remove();

          // Add the candidate to the conflicting matches
          match.getReciprocate().getConflictingMatches().add(candidate);

          // Remove it from the candidates (if it was present)
          if (!updatedMatches.contains(candidate.getReciprocate())) {
            updatedMatches.add(candidate.getReciprocate());
          }
        }
      }
    });

    return updatedMatches;
  }

  /**
   * MUST be called before updateRemoteMergeableRange because the updated local indexes are used in the current
   * function, which cause an update of the mergeable ranges.
   *
   * @return true of the indexes were updated, false otherwise
   */

  private boolean updateRemoteIndexes(final Match match) {
    boolean res = false;

    // Get the local indivisible ranges involved in the match
    final Range localIndivisibleRange = match.getLocalIndivisibleRange();

    // Align them with the remote ranges
    localIndivisibleRange.translate(match.getRemoteIndex() - match.getLocalIndex());

    // Update the remote buffer indexes if needed.
    if (localIndivisibleRange.getStart() < match.getRemoteBuffer().minIndex) {
      res = true;
      match.getRemoteBuffer().minIndex = localIndivisibleRange.getStart();
    }

    if (localIndivisibleRange.getEnd() > match.getRemoteBuffer().maxIndex) {
      res = true;
      match.getRemoteBuffer().maxIndex = localIndivisibleRange.getEnd();
    }

    return res;
  }

  /**
   * Also update the {@link #getDivisibilityRequiredMatches() divisibilityRequiredMatches} {@link List} of the
   * {@link Buffer}.
   *
   */

  private void updateDivisibleRanges(final Match match) {
    final Range localRange = match.getLocalRange();

    // Get the local indivisible ranges involved in the match
    // An indivisible range can go beyond the matched
    // range. For example, if the range includes virtual tokens
    // toList to make sure the map function is applied only once
    final List<Range> localIndivisibleRanges = match.getLocalBuffer().indivisibleRanges.stream()
        .filter(it -> Range.hasOverlap(it, localRange)).map(Range::copy).toList();

    // Align them with the remote ranges
    Range.translate(localIndivisibleRanges, match.getRemoteIndex() - match.getLocalIndex());

    // Do the lazy union
    // The divisability update must not be applied if the applied match involves
    // the division of the local buffer, instead the remote buffer should become !
    // non divisable !
    Range.lazyUnion(match.getRemoteBuffer().indivisibleRanges, localIndivisibleRanges);

    // If the destination range is still divisible,(i.e. if the remote
    // localRange overlaps more than a unique indivisible Range.)
    // Then Forward all DivisibilityRequiredMatches from the local Buffer
    // No risk if the match is applied as a result of a division since
    // in such case, the destination is compulsorily indivisible
    if (match.getRemoteBuffer().indivisibleRanges.stream()
        .filter(it -> Range.hasOverlap(it, match.getReciprocate().getLocalRange())).count() > 1) {
      match.getRemoteBuffer().divisibilityRequiredMatches.addAll(match.getLocalBuffer().divisibilityRequiredMatches);
    }
  }

  /**
   * Must be called after updateRemoteIndexesAndDivisibleRanges
   */

  private void updateRemoteMergeableRange(final Match match) {

    // 1 - Get the mergeable ranges that are involved in the match
    // Get the local involved Range
    final Range involvedRange = match.getLocalIndivisibleRange();
    final List<Range> localMergeableRange = Range.intersection(match.getLocalBuffer().mergeableRanges, involvedRange);

    // Translate it to get the remote involved range
    involvedRange.translate(match.getRemoteIndex() - match.getLocalIndex());
    final List<Range> remoteMergeableRange = Range.intersection(match.getRemoteBuffer().mergeableRanges, involvedRange);

    // 2 - Realign the two ranges
    Range.translate(localMergeableRange, -match.getLocalIndex());
    Range.translate(remoteMergeableRange, -match.getRemoteIndex());

    // 3 - Get intersection => the mergeable range of the result
    final List<Range> resultMergeableRange = Range.intersection(localMergeableRange, remoteMergeableRange);

    // 4 - Update the destination mergeable range
    // no need to update the origin mergeable range since
    // this buffer will no longer be used in the processing
    // 4.1 - compute the Mergeable range that must be removed
    // from the destination buffer
    final List<Range> unmergeableRange = Range.difference(remoteMergeableRange, resultMergeableRange);

    // 4.2 - Realign unmergeable range with destination buffer
    Range.translate(unmergeableRange, match.getRemoteIndex());

    // 4.3 - Remove it from the remoteMergeableRange
    match.getRemoteBuffer().mergeableRanges = Range.difference(match.getRemoteBuffer().mergeableRanges,
        unmergeableRange);
  }

  /**
   * Remove the current {@link Match} from its {@link #getLocalBuffer() localBuffer} and {@link #getRemoteBuffer()
   * remoteBuffer} {@link Buffer#getMatchTable() matchTable}. Each time the current match is retrieved in a List, the
   * reference equality (===) from XTend is used. Indeed, several matches might be {@link Match#equals(Object) equals}
   * which would result in removing the wrong match.
   */
  private static void unmatch(final Match match) {
    // Local unmatch
    final List<Match> localList = match.getLocalBuffer().matchTable.get(match.getLocalIndex());
    Iterator<Match> iter = localList.iterator();

    while (iter.hasNext()) {
      // use the triple === to remove the correct
      // match because several matches might be ==
      if (iter.next() == match) {
        iter.remove();
      }
    }

    // Remove empty lists
    if (localList.isEmpty()) {
      match.getLocalBuffer().matchTable.remove(match.getLocalIndex());
    }

    // Remote unmatch
    final List<Match> remoteList = match.getRemoteBuffer().matchTable.get(match.getRemoteIndex());
    iter = remoteList.iterator();
    while (iter.hasNext()) {
      // use the triple === to remove the correct
      // match because several matches might be ==
      if (iter.next() == match.getReciprocate()) {
        iter.remove();
      }
    }
    if (remoteList.isEmpty()) {
      match.getRemoteBuffer().matchTable.remove(match.getRemoteIndex());
    }

    // Remove it from conflictingMatches and conflictCandidates
    match.getConflictCandidates().stream().forEach(it -> {
      final Iterator<Match> iterator = it.getConflictCandidates().iterator();
      while (iterator.hasNext()) {
        if (iterator.next() == match) {
          iterator.remove();
        }
      }
    });
    match.getConflictingMatches().stream().forEach(it -> {
      final Iterator<Match> iterator = it.getConflictingMatches().iterator();
      while (iterator.hasNext()) {
        if (iterator.next() == match) {
          iterator.remove();
        }
      }
    });
    match.getReciprocate().getConflictCandidates().stream().forEach(it -> {
      final Iterator<Match> iterator = it.getConflictCandidates().iterator();
      while (iterator.hasNext()) {
        if (iterator.next() == match.getReciprocate()) {
          iterator.remove();
        }
      }
    });

    match.getReciprocate().getConflictingMatches().stream().forEach(it -> {
      final Iterator<Match> iterator = it.getConflictingMatches().iterator();
      while (iterator.hasNext()) {
        if (iterator.next() == match.getReciprocate()) {
          iterator.remove();
        }
      }
    });
  }

  /**
   * This method checks if the given {@link Match Matches} are sufficient to complete the
   * {@link #getDivisibilityRequiredMatches()} condition.
   *
   */

  boolean doesCompleteRequiredMatches(final List<Match> matches) {

    // Remove completed lists
    final Iterator<List<Match>> iter = this.divisibilityRequiredMatches.iterator();
    while (iter.hasNext()) {

      // In the current version we only check if all lists are completelyMatched
      // for better optimization, we must check if each list contains enough applied matches
      // to cover the complete original range
      final List<Match> list = iter.next();
      if (list.stream().allMatch(Match::isApplied)) {
        iter.remove();
      }
    }

    // Check if the proposed matches completes the remaining lists
    return this.divisibilityRequiredMatches.stream().allMatch(matches::containsAll);
  }

  @Override
  public String toString() {
    final long size = this.getBufferSizeInByte();
    return this.getVertexName() + "." + this.name + "[" + size + "]";
  }

  public String getVertexName() {
    return vertexName;
  }

  public DAGEdge getLoggingEdgeName() {
    return loggingEdgeName;
  }

}
