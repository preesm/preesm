/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
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
package org.preesm.algorithm.synthesis.memalloc.script;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.memory.script.MatchType;

/**
 *
 */
public class PiMatch {
  /**
   * Does not save the match in the buffer matchTable
   */
  public PiMatch(final PiBuffer localBuffer, final long localIndex, final PiBuffer remoteBuffer, final long remoteIndex,
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
    this.originalMatch = new PiMatch(this);
  }

  /**
   * Copy the {@link PiMatch} following attributes:
   * <ul>
   * <li>{@link #getLocalBuffer() localBuffer}</li>
   * <li>{@link #getRemoteBuffer() remoteBuffer}</li>
   * <li>{@link #getLocalIndex() localIndex}</li>
   * <li>{@link #getRemoteIndex() remoteIndex}</li>
   * <li>{@link #getLength() length}</li>
   * <li>{@link #getType() type}</li>
   * </ul>
   */
  private PiMatch(final PiMatch m) {
    this.localBuffer = m.localBuffer;
    this.localIndex = m.localIndex;
    this.remoteBuffer = m.remoteBuffer;
    this.remoteIndex = m.remoteIndex;
    this.length = m.length;
    this.type = m.type;
  }

  private PiBuffer      localBuffer;
  private long          localIndex;
  private PiBuffer      remoteBuffer;
  private long          remoteIndex;
  private long          length;
  private List<PiMatch> conflictingMatches;
  private List<PiMatch> conflictCandidates;
  /**
   * For logging purpose
   */
  private PiMatch       originalMatch;

  /**
   * The {@link MatchType} of the current {@link PiMatch}.
   */
  private MatchType type;

  /**
   * This {@link List} contains {@link PiRange} of the {@link #getLocalBuffer()} that can be matched only if:
   * <ul>
   * <li>The remote buffer has no token for this range (i.e. it is out of the minIndex .. maxIndex range).</li>
   * </ul>
   * The list of a {@link PiMatch} and the one of its {@link #getReciprocate() reciprocate} are not equals.
   */
  private List<PiRange> forbiddenLocalRanges;

  /**
   * This {@link List} contains {@link PiRange} of the {@link #getLocalBuffer()} that can be matched only if:
   * <ul>
   * <li>If the match is backward AND both the remote buffers are mergeable for this ranges.<br>
   * or</li>
   * <li>The remote buffer has no token for this range (i.e. it is out of the minIndex .. maxIndex range).</li>
   * </ul>
   * Only {@link #getType() backward} matches can have mergeableLocalRanges.
   */
  private List<PiRange> mergeableLocalRanges;

  /**
   * This {@link boolean} is set to <code>true</code> if the current {@link PiMatch} was applied.
   */
  private boolean applied;

  private PiMatch reciprocate;

  public PiBuffer getLocalBuffer() {
    return this.localBuffer;
  }

  public void setLocalBuffer(final PiBuffer localBuffer) {
    this.localBuffer = localBuffer;
  }

  public long getLocalIndex() {
    return this.localIndex;
  }

  public long getLocalIndexInByte() {
    return (this.localIndex + 7L) / 8L;
  }

  public void setLocalIndex(final long localIndex) {
    this.localIndex = localIndex;
  }

  public PiBuffer getRemoteBuffer() {
    return this.remoteBuffer;
  }

  public void setRemoteBuffer(final PiBuffer remoteBuffer) {
    this.remoteBuffer = remoteBuffer;
  }

  public long getRemoteIndex() {
    return this.remoteIndex;
  }

  public long getRemoteIndexInByte() {
    return (this.remoteIndex + 7L) / 8L;
  }

  public void setRemoteIndex(final long remoteIndex) {
    this.remoteIndex = remoteIndex;
  }

  public long getLength() {
    return this.length;
  }

  public long getLengthInByte() {
    return (this.length + 7L) / 8L;
  }

  public void setLength(final long length) {
    this.length = length;
  }

  public List<PiMatch> getConflictingMatches() {
    return this.conflictingMatches;
  }

  public void setConflictingMatches(final List<PiMatch> conflictingMatches) {
    this.conflictingMatches = conflictingMatches;
  }

  public List<PiMatch> getConflictCandidates() {
    return this.conflictCandidates;
  }

  public void setConflictCandidates(final List<PiMatch> conflictCandidates) {
    this.conflictCandidates = conflictCandidates;
  }

  public PiMatch getOriginalMatch() {
    return this.originalMatch;
  }

  public void setOriginalMatch(final PiMatch originalMatch) {
    this.originalMatch = originalMatch;
  }

  public List<PiRange> getForbiddenLocalRanges() {
    return this.forbiddenLocalRanges;
  }

  public void setForbiddenLocalRanges(final List<PiRange> forbiddenLocalRanges) {
    this.forbiddenLocalRanges = forbiddenLocalRanges;
  }

  public List<PiRange> getMergeableLocalRanges() {
    return this.mergeableLocalRanges;
  }

  public void setMergeableLocalRanges(final List<PiRange> mergeableLocalRanges) {
    this.mergeableLocalRanges = mergeableLocalRanges;
  }

  public boolean isApplied() {
    return this.applied;
  }

  public void setApplied(final boolean applied) {
    this.applied = applied;
  }

  public MatchType getType() {
    return this.type;
  }

  /**
   * Set the {@link #_type type} of the current {@link PiMatch}. If the type is <code>BACKWARD</code> a new list is
   * created for the {@link #getMergeableLocalRanges() mergeableLocalRanges}. Otherwise mergeableLocalRanges is set to
   * <code>null</code>.
   */
  public List<PiRange> setType(final MatchType newType) {
    this.type = newType;
    if (getType() == MatchType.BACKWARD) {
      this.mergeableLocalRanges = new ArrayList<>();
    } else {
      this.mergeableLocalRanges = null;
    }
    return this.mergeableLocalRanges;
  }

  public void setReciprocate(final PiMatch remoteMatch) {
    this.reciprocate = remoteMatch;
    remoteMatch.reciprocate = this;
  }

  public PiMatch getReciprocate() {
    return this.reciprocate;
  }

  /**
   * Returns a {@link PiRange} going from {@link PiMatch#getLocalIndex() localIndex} to the end of the matched tokens.
   *
   */
  public PiRange getLocalRange() {
    return new PiRange(getLocalIndex(), getLocalIndex() + getLength());
  }

  /**
   * Get the indivisible {@link PiRange} in which the current {@link PiMatch} falls. This method has no side-effects.
   *
   * @return the {@link PiRange} resulting from the {@link PiRange#lazyUnion(List,PiRange) lazyUnion} of the
   *         {@link PiMatch#getLocalRange() localRange} and the {@link PiBuffer#getIndivisibleRanges()
   *         indivisibleRanges} of the {@link PiMatch#getLocalBuffer() localBuffer}.
   *
   */
  public PiRange getLocalIndivisibleRange() {

    final PiRange localIndivisiblerange = getLocalRange();

    // Copy the overlapping indivisible range(s)
    final List<PiRange> indivisibleRanges = getLocalBuffer().indivisibleRanges;
    // toList to make sure the map function is applied only once
    final List<PiRange> overlappingIndivisibleRanges = indivisibleRanges.stream()
        .filter(r -> PiRange.hasOverlap(r, localIndivisiblerange)).map(PiRange::copy).collect(Collectors.toList());

    // Do the lazy union of the match and its overlapping indivisible
    // ranges
    return PiRange.lazyUnion(overlappingIndivisibleRanges, localIndivisiblerange);
  }

  /**
   * Returns the {@link PiRange} of the {@link PiMatch#getLocalBuffer() localBuffer} that will be impacted if
   * <code>this</code> {@link PiMatch} is applied. This {@link PiRange} corresponds to the largest {@link PiRange}
   * between the {@link #getLocalRange()} of the current {@link PiMatch} and the
   * {@link PiMatch#getLocalIndivisibleRange() localIndivisibleRange} of the {@link #getReciprocate() reciprocate}
   * {@link PiMatch}.
   *
   * @return a {@link PiRange} of impacted tokens aligned with the {@link PiMatch#getLocalBuffer() localBuffer} indexes.
   */
  public PiRange getLocalImpactedRange() {

    // Get the aligned smallest indivisible range (local or remote)
    final PiRange localRange = getLocalRange();
    final PiRange remoteIndivisibleRange = getReciprocate().getLocalIndivisibleRange();
    remoteIndivisibleRange.translate(getLocalIndex() - getRemoteIndex());
    final PiRange smallestRange;
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
    if ((obj == null) || (this.getClass() != obj.getClass())) {
      return false;
    }
    final PiMatch other = (PiMatch) obj;
    return (getLocalBuffer() == other.getLocalBuffer()) && (getLocalIndex() == other.getLocalIndex())
        && (getRemoteBuffer() == other.getRemoteBuffer()) && (getRemoteIndex() == other.getRemoteIndex())
        && (getLength() == other.getLength());
  }

  @Override
  public String toString() {
    return "" + getLocalBuffer().getVertexName() + "." + getLocalBuffer().name + "[" + getLocalIndexInByte() + ".."
        + (getLocalIndexInByte() + getLengthInByte()) + "[=>" + getRemoteBuffer().getVertexName() + "."
        + getRemoteBuffer().name + "[" + getRemoteIndexInByte() + ".." + (getRemoteIndexInByte() + getLengthInByte())
        + "[";
  }

  /**
   * Check whether the current match fulfills its forbiddenLocalRanges and mergeableLocalRange conditions. Does not
   * check the reciprocate.
   */
  public boolean isApplicable() {

    // Does not match forbidden tokens
    final PiRange impactedTokens = getLocalImpactedRange()
        .intersection(new PiRange(getLocalBuffer().minIndex, getLocalBuffer().maxIndex));
    final boolean rangeEmpty = PiRange.intersection(getForbiddenLocalRanges(), impactedTokens).isEmpty();
    if (!rangeEmpty) {
      return false;
    }
    if (getType() == MatchType.FORWARD) {
      return true;
    } else {
      final List<PiRange> mustBeMergeableRanges = PiRange.intersection(getMergeableLocalRanges(), impactedTokens);
      final List<PiRange> mergeableRanges = PiRange.intersection(getLocalBuffer().mergeableRanges, impactedTokens);
      return PiRange.difference(mustBeMergeableRanges, mergeableRanges).isEmpty();
    }
  }

  /**
   * Recursive method to find where the {@link #getLocalRange()} of the current {@link #getLocalBuffer() localBuffer} is
   * matched.
   *
   * @return a {@link Map} that associates: a {@link PiRange subranges} of the {@link #getLocalRange() localRange} of
   *         the {@link #getLocalBuffer() localBuffer} to a {@link Pair} composed of a {@link PiBuffer} and a
   *         {@link PiRange} where the associated {@link PiRange subrange} is matched.
   *
   */
  public Map<PiRange, Pair<PiBuffer, PiRange>> getRoot() {
    final Map<PiRange, Pair<PiBuffer, PiRange>> result = new LinkedHashMap<>();
    final PiRange remoteRange = getLocalIndivisibleRange().translate(getRemoteIndex() - getLocalIndex());

    // Termination case if the remote Buffer is not matched
    final List<PiMatch> matched = getRemoteBuffer().matched;
    if (matched == null) {
      result.put(getLocalIndivisibleRange(), new Pair<>(getRemoteBuffer(), remoteRange));
    } else {
      // Else, recursive call

      final List<PiMatch> c = matched.stream()
          .filter(m -> PiRange.hasOverlap(m.getLocalIndivisibleRange(), remoteRange)).toList();

      for (final PiMatch match : c) {
        final Map<PiRange, Pair<PiBuffer, PiRange>> recursiveResult = match.getRoot();
        for (final Entry<PiRange, Pair<PiBuffer, PiRange>> entry : recursiveResult.entrySet()) {
          final PiRange range = entry.getKey();

          // translate back to local range
          range.translate(getLocalIndex() - getRemoteIndex());
          result.put(range, entry.getValue());
        }
      }
    }

    return result;
  }

}
