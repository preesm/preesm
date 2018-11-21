/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
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
package org.preesm.algorithm.memory.script;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;

/**
 *
 */
public class Match {
  /**
   * Does not save the match in the buffer matchTable
   */
  public Match(final Buffer localBuffer, final long localIndex, final Buffer remoteBuffer, final long remoteIndex,
      final long size) {
    this.localBuffer = localBuffer;
    this.localIndex = localIndex;
    this.remoteBuffer = remoteBuffer;
    this.remoteIndex = remoteIndex;
    this.length = size;
    this.conflictingMatches = new ArrayList<>();
    this.conflictCandidates = new ArrayList<>();
    this.applied = false;
    this.forbiddenLocalRanges = new ArrayList<>();
    this.originalMatch = new Match(this);
  }

  /**
   * Copy the {@link Match} following attributes:
   * <ul>
   * <li>{@link #getLocalBuffer() localBuffer}</li>
   * <li>{@link #getRemoteBuffer() remoteBuffer}</li>
   * <li>{@link #getLocalIndex() localIndex}</li>
   * <li>{@link #getRemoteIndex() remoteIndex}</li>
   * <li>{@link #getLength() length}</li>
   * <li>{@link #getType() type}</li>
   * </ul>
   */
  private Match(final Match m) {
    this.localBuffer = m.localBuffer;
    this.localIndex = m.localIndex;
    this.remoteBuffer = m.remoteBuffer;
    this.remoteIndex = m.remoteIndex;
    this.length = m.length;
    this.type = m.type;
  }

  private Buffer      localBuffer;
  private long        localIndex;
  private Buffer      remoteBuffer;
  private long        remoteIndex;
  private long        length;
  private List<Match> conflictingMatches;
  private List<Match> conflictCandidates;
  /**
   * For logging purpose
   */
  private Match       originalMatch;

  /**
   * The {@link MatchType} of the current {@link Match}.
   */
  private MatchType type;

  /**
   * This {@link List} contains {@link Range} of the {@link #getLocalBuffer()} that can be matched only if:
   * <ul>
   * <li>The remote buffer has no token for this range (i.e. it is out of the minIndex .. maxIndex range).</li>
   * </ul>
   * The list of a {@link Match} and the one of its {@link #getReciprocate() reciprocate} are not equals.
   */
  private List<Range> forbiddenLocalRanges;

  /**
   * This {@link List} contains {@link Range} of the {@link #getLocalBuffer()} that can be matched only if:
   * <ul>
   * <li>If the match is backward AND both the remote buffers are mergeable for this ranges.<br>
   * or</li>
   * <li>The remote buffer has no token for this range (i.e. it is out of the minIndex .. maxIndex range).</li>
   * </ul>
   * Only {@link #getType() backward} matches can have mergeableLocalRanges.
   */
  private List<Range> mergeableLocalRanges;

  /**
   * This {@link boolean} is set to <code>true</code> if the current {@link Match} was applied.
   */
  private boolean applied;

  private Match _reciprocate;

  public Buffer getLocalBuffer() {
    return this.localBuffer;
  }

  public void setLocalBuffer(final Buffer localBuffer) {
    this.localBuffer = localBuffer;
  }

  public long getLocalIndex() {
    return this.localIndex;
  }

  public void setLocalIndex(final long localIndex) {
    this.localIndex = localIndex;
  }

  public Buffer getRemoteBuffer() {
    return this.remoteBuffer;
  }

  public void setRemoteBuffer(final Buffer remoteBuffer) {
    this.remoteBuffer = remoteBuffer;
  }

  public long getRemoteIndex() {
    return this.remoteIndex;
  }

  public void setRemoteIndex(final long remoteIndex) {
    this.remoteIndex = remoteIndex;
  }

  public long getLength() {
    return this.length;
  }

  public void setLength(final long length) {
    this.length = length;
  }

  public List<Match> getConflictingMatches() {
    return this.conflictingMatches;
  }

  public void setConflictingMatches(final List<Match> conflictingMatches) {
    this.conflictingMatches = conflictingMatches;
  }

  public List<Match> getConflictCandidates() {
    return this.conflictCandidates;
  }

  public void setConflictCandidates(final List<Match> conflictCandidates) {
    this.conflictCandidates = conflictCandidates;
  }

  public Match getOriginalMatch() {
    return this.originalMatch;
  }

  public void setOriginalMatch(final Match originalMatch) {
    this.originalMatch = originalMatch;
  }

  public List<Range> getForbiddenLocalRanges() {
    return this.forbiddenLocalRanges;
  }

  public void setForbiddenLocalRanges(final List<Range> forbiddenLocalRanges) {
    this.forbiddenLocalRanges = forbiddenLocalRanges;
  }

  public List<Range> getMergeableLocalRanges() {
    return this.mergeableLocalRanges;
  }

  public void setMergeableLocalRanges(final List<Range> mergeableLocalRanges) {
    this.mergeableLocalRanges = mergeableLocalRanges;
  }

  public boolean isApplied() {
    return this.applied;
  }

  public void setApplied(final boolean applied) {
    this.applied = applied;
  }

  public Match get_reciprocate() {
    return this._reciprocate;
  }

  public void set_reciprocate(final Match _reciprocate) {
    this._reciprocate = _reciprocate;
  }

  public MatchType getType() {
    return this.type;
  }

  /**
   * Set the {@link #_type type} of the current {@link Match}. If the type is <code>BACKWARD</code> a new list is
   * created for the {@link #getMergeableLocalRanges() mergeableLocalRanges}. Otherwise mergeableLocalRanges is set to
   * <code>null</code>.
   */
  public List<Range> setType(final MatchType newType) {
    this.type = newType;
    if (getType() == MatchType.BACKWARD) {
      this.mergeableLocalRanges = new ArrayList<>();
    } else {
      this.mergeableLocalRanges = null;
    }
    return this.mergeableLocalRanges;
  }

  public void setReciprocate(final Match remoteMatch) {
    this._reciprocate = remoteMatch;
    remoteMatch._reciprocate = this;
  }

  public Match getReciprocate() {
    return this._reciprocate;
  }

  /**
   * Returns a {@link Range} going from {@link Match#getLocalIndex() localIndex} to the end of the matched tokens.
   *
   */
  public Range getLocalRange() {
    return new Range(getLocalIndex(), getLocalIndex() + getLength());
  }

  /**
   * Get the indivisible {@link Range} in which the current {@link Match} falls. This method has no side-effects.
   *
   * @return the {@link Range} resulting from the {@link Range#lazyUnion(List,Range) lazyUnion} of the
   *         {@link Match#getLocalRange() localRange} and the {@link Buffer#getIndivisibleRanges() indivisibleRanges} of
   *         the {@link Match#getLocalBuffer() localBuffer}.
   *
   */
  public Range getLocalIndivisibleRange() {

    final Range localIndivisiblerange = getLocalRange();

    // Copy the overlapping indivisible range(s)
    final List<Range> indivisibleRanges = getLocalBuffer().indivisibleRanges;
    // toList to make sure the map function is applied only once
    final List<Range> overlappingIndivisibleRanges = indivisibleRanges.stream()
        .filter(r -> Range.hasOverlap(r, localIndivisiblerange)).map(r -> r.copy()).collect(Collectors.toList());

    // Do the lazy union of the match and its overlapping indivisible
    // ranges
    return Range.lazyUnion(overlappingIndivisibleRanges, localIndivisiblerange);
  }

  /**
   * Returns the {@link Range} of the {@link Match#getLocalBuffer() localBuffer} that will be impacted if
   * <code>this</code> {@link Match} is applied. This {@link Range} corresponds to the largest {@link Range} between the
   * {@link #getLocalRange()} of the current {@link Match} and the {@link Match#getLocalIndivisibleRange()
   * localIndivisibleRange} of the {@link #getReciprocate() reciprocate} {@link Match}.
   *
   * @return a {@link Range} of impacted tokens aligned with the {@link Match#getLocalBuffer() localBuffer} indexes.
   */
  public Range getLocalImpactedRange() {

    // Get the aligned smallest indivisible range (local or remote)
    final Range localRange = getLocalRange();
    final Range remoteIndivisibleRange = getReciprocate().getLocalIndivisibleRange();
    remoteIndivisibleRange.translate(getLocalIndex() - getRemoteIndex());
    final Range smallestRange;
    if (localRange.getLength() > remoteIndivisibleRange.getLength()) {
      smallestRange = localRange;
    } else {
      smallestRange = remoteIndivisibleRange;
    }

    return smallestRange;
  }

  /**
   * Overriden to forbid
   */
  @Override
  public int hashCode() {

    // Forbidden because if a non final attribute is changed, then the hashcode changes.
    // But if the Match is already stored in a map, its original hashcode will have been used
    throw new UnsupportedOperationException("HashCode is not supported for Match class. Do not use LinkedHashMap.");

    // index.hashCode.bitwiseXor(length.hashCode).bitwiseXor(buffer.hashCode)
  }

  /**
   * Reciprocate is not considered
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Match other = (Match) obj;
    return (getLocalBuffer() == other.getLocalBuffer()) && (getLocalIndex() == other.getLocalIndex())
        && (getRemoteBuffer() == other.getRemoteBuffer()) && (getRemoteIndex() == other.getRemoteIndex())
        && (getLength() == other.getLength());
  }

  @Override
  public String toString() {
    return "" + getLocalBuffer().dagVertex.getName() + "." + getLocalBuffer().name + "[" + getLocalIndex() + ".."
        + (getLocalIndex() + getLength()) + "[=>" + getRemoteBuffer().dagVertex.getName() + "." + getRemoteBuffer().name
        + "[" + getRemoteIndex() + ".." + (getRemoteIndex() + getLength()) + "[";
  }

  /**
   * Check whether the current match fulfills its forbiddenLocalRanges and mergeableLocalRange conditions. Does not
   * check the reciprocate.
   */
  public boolean isApplicable() {

    // Does not match forbidden tokens
    final Range impactedTokens = getLocalImpactedRange()
        .intersection(new Range(getLocalBuffer().minIndex, getLocalBuffer().maxIndex));
    final boolean rangeEmpty = Range.intersection(getForbiddenLocalRanges(), impactedTokens).isEmpty();
    if (!rangeEmpty) {
      return false;
    } else {
      // And match only localMergeableRange are in fact mergeable
      if (getType() == MatchType.FORWARD) {
        return true;
      } else {
        final List<Range> mustBeMergeableRanges = Range.intersection(getMergeableLocalRanges(), impactedTokens);
        final List<Range> mergeableRanges = Range.intersection(getLocalBuffer().mergeableRanges, impactedTokens);
        return Range.difference(mustBeMergeableRanges, mergeableRanges).isEmpty();
      }
    }
  }

  /**
   * Recursive method to find where the {@link #getLocalRange()} of the current {@link #getLocalBuffer() localBuffer} is
   * matched.
   *
   * @return a {@link Map} that associates: a {@link Range subranges} of the {@link #getLocalRange() localRange} of the
   *         {@link #getLocalBuffer() localBuffer} to a {@link Pair} composed of a {@link Buffer} and a {@link Range}
   *         where the associated {@link Range subrange} is matched.
   *
   */
  public Map<Range, Pair<Buffer, Range>> getRoot() {
    final Map<Range, Pair<Buffer, Range>> result = new LinkedHashMap<>();
    final Range remoteRange = getLocalIndivisibleRange().translate(getRemoteIndex() - getLocalIndex());

    // Termination case if the remote Buffer is not matched
    final List<Match> matched = getRemoteBuffer().matched;
    if (matched == null) {
      result.put(getLocalIndivisibleRange(), new Pair<>(getRemoteBuffer(), remoteRange));
    } else {
      // Else, recursive call

      final List<Match> c = matched.stream().filter(m -> Range.hasOverlap(m.getLocalIndivisibleRange(), remoteRange))
          .collect(Collectors.toList());

      for (final Match match : c) {
        final Map<Range, Pair<Buffer, Range>> recursiveResult = match.getRoot();
        for (final Entry<Range, Pair<Buffer, Range>> entry : recursiveResult.entrySet()) {
          final Range range = entry.getKey();

          // translate back to local range
          range.translate(getLocalIndex() - getRemoteIndex());
          result.put(range, entry.getValue());
        }
      }
    }

    return result;
  }

}
