/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
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
package org.ietr.preesm.memory.script

import java.util.List
import java.util.Map

import static extension org.ietr.preesm.memory.script.Range.*
import org.eclipse.xtend.lib.annotations.Accessors

class Match {
	/**
	 * Does not save the match in the buffer matchTable
	 */
	new(Buffer localBuffer, long localIndex, Buffer remoteBuffer, long remoteIndex, long size) {
		this.localBuffer = localBuffer
		this.localIndex = localIndex
		this.remoteBuffer = remoteBuffer
		this.remoteIndex = remoteIndex
		length = size
		this.conflictingMatches = newArrayList
		this.conflictCandidates = newArrayList
		this.applied = false
		this.forbiddenLocalRanges = newArrayList
		this.originalMatch = new Match(this)
	}

  /**
   * Copy the {@link Match} following attributes:<ul>
   * <li>{@link #getLocalBuffer() localBuffer}</li>
   * <li>{@link #getRemoteBuffer() remoteBuffer}</li>
   * <li>{@link #getLocalIndex() localIndex}</li>
   * <li>{@link #getRemoteIndex() remoteIndex}</li>
   * <li>{@link #getLength() length}</li>
   * <li>{@link #getType() type}</li>
   * </ul>
   */
	private new(Match m) {
		this.localBuffer = m.localBuffer
		this.localIndex = m.localIndex
		this.remoteBuffer = m.remoteBuffer
		this.remoteIndex = m.remoteIndex
		this.length = m.length
		this.type = m.type
	}

	@Accessors
	Buffer localBuffer
	@Accessors
	long localIndex
	@Accessors
	Buffer remoteBuffer
	@Accessors
	long remoteIndex
	@Accessors
	long length
	@Accessors
	List<Match> conflictingMatches
	@Accessors
	List<Match> conflictCandidates
	/**
	 * For logging purpose
	 */
	@Accessors
	Match originalMatch

	/**
	 * The {@link MatchType} of the current {@link Match}.
	 */
	@Accessors
	MatchType type

	/**
	 * Set the {@link #_type type} of the current {@link Match}.
	 * If the type is <code>BACKWARD</code> a new list is created for the
	 * {@link #getMergeableLocalRanges() mergeableLocalRanges}. Otherwise
	 * mergeableLocalRanges is set to <code>null</code>.
	 */
	def List<Range> setType(MatchType newType) {
		type = newType
		return mergeableLocalRanges = if (getType == MatchType::BACKWARD) {
			newArrayList
		} else {
			null
		}
	}

	/**
	 * This {@link List} contains {@link Range} of the {@link
	 * #getLocalBuffer()} that can be matched only if:<ul>
	 * <li> The remote buffer has no token for this range (i.e. it is
	 * out of the minIndex .. maxIndex range).</li></ul>
	 * The list of a {@link Match} and the one of its {@link #getReciprocate()
	 * reciprocate} are not equals.
	 */
	@Accessors
	List<Range> forbiddenLocalRanges

	/**
	 * This {@link List} contains {@link Range} of the {@link
	 * #getLocalBuffer()} that can be matched only if:<ul>
	 * <li> If the match is backward AND both the remote buffers are
	 * mergeable for this ranges.<br>
	 * or</li>
	 * <li> The remote buffer has no token for this range (i.e. it is
	 * out of the minIndex .. maxIndex range).</li></ul>
	 * Only {@link #getType() backward} matches can have mergeableLocalRanges.
	 */
	@Accessors
	List<Range> mergeableLocalRanges

	/**
	 * This {@link boolean} is set to <code>true</code> if the current {@link
	 * Match} was applied.
	 */
	@Accessors
	boolean applied

	Match _reciprocate

	def void setReciprocate(Match remoteMatch) {
		_reciprocate = remoteMatch
		remoteMatch._reciprocate = this
	}

	def Match getReciprocate() {
		return _reciprocate
	}

	/**
	 * Returns a {@link Range} going from {@link Match#getLocalIndex()
	 * localIndex} to the end of the matched tokens.
	 *
	 */
	def Range getLocalRange() {
		return new Range(getLocalIndex, getLocalIndex + getLength)
	}

	/**
	 * Get the indivisible {@link Range} in which the current {@link Match}
	 * falls. This method has no side-effects.
	 *
	 * @return the {@link Range} resulting from the {@link
	 * Range#lazyUnion(List,Range) lazyUnion} of the {@link
	 * Match#getLocalRange() localRange} and the {@link
	 * Buffer#getIndivisibleRanges() indivisibleRanges} of the {@link
	 * Match#getLocalBuffer() localBuffer}.
	 *
	 */
	def Range getLocalIndivisibleRange() {

		val localIndivisiblerange = this.getLocalRange

		// Copy the overlapping indivisible range(s)
		val overlappingIndivisibleRanges = this.getLocalBuffer.indivisibleRanges.filter [
			it.hasOverlap(localIndivisiblerange)
		].map[it.copy].toList // toList to make sure the map function is applied only once

		// Do the lazy union of the match and its overlapping indivisible
		// ranges
		return overlappingIndivisibleRanges.lazyUnion(localIndivisiblerange)
	}

	/**
	 * Returns the {@link Range} of the {@link Match#getLocalBuffer()
	 * localBuffer} that will be impacted if <code>this</code> {@link Match}
	 * is applied. This {@link Range} corresponds to the largest {@link Range}
	 * between the {@link #getLocalRange()} of the current {@link Match} and
	 * the {@link Match#getLocalIndivisibleRange() localIndivisibleRange} of
	 * the {@link #getReciprocate() reciprocate} {@link Match}.
	 *
	 * @return a {@link Range} of impacted tokens aligned with the {@link
	 * Match#getLocalBuffer() localBuffer} indexes.
	 */
	def Range getLocalImpactedRange() {

		// Get the aligned smallest indivisible range (local or remote)
		val localRange = this.getLocalRange
		val remoteIndivisibleRange = this.getReciprocate.getLocalIndivisibleRange
		remoteIndivisibleRange.translate(this.getLocalIndex - this.getRemoteIndex)
		val smallestRange = if (localRange.getLength > remoteIndivisibleRange.getLength) {
				localRange
			} else {
				remoteIndivisibleRange
			}

		return smallestRange
	}

	/**
	 * Overriden to forbid
	 */
	override hashCode() {

		// Forbidden because if a non final attribute is changed, then the hashcode changes.
		// But if the Match is already stored in a map, its original hashcode will have been used
		throw new UnsupportedOperationException("HashCode is not supported for Match class. Do not use LinkedHashMap.")

	//index.hashCode.bitwiseXor(length.hashCode).bitwiseXor(buffer.hashCode)
	}

	/**
	 * Reciprocate is not considered
 	*/
	override equals(Object obj) {
		if (this === obj)
			return true
		if (obj === null)
			return false
		if (this.class != obj.class)
			return false
		var other = obj as Match
		return this.getLocalBuffer == other.getLocalBuffer && this.getLocalIndex == other.getLocalIndex &&
			this.getRemoteBuffer == other.getRemoteBuffer && this.getRemoteIndex == other.getRemoteIndex &&
			this.getLength == other.getLength
	}

	override toString() '''«getLocalBuffer.dagVertex.name».«getLocalBuffer.name»[«getLocalIndex»..«getLocalIndex + getLength»[=>«getRemoteBuffer.
		dagVertex.name».«getRemoteBuffer.name»[«getRemoteIndex»..«getRemoteIndex + getLength»['''

	/**
	 * Check whether the current match fulfills its forbiddenLocalRanges and
	 * mergeableLocalRange conditions. Does not check the reciprocate.
	 */
	def boolean isApplicable() {

		// Does not match forbidden tokens
		val impactedTokens = this.getLocalImpactedRange.intersection(
			new Range(this.getLocalBuffer.minIndex, this.getLocalBuffer.maxIndex))
		return this.getForbiddenLocalRanges.intersection(impactedTokens).size == 0 &&
		// And match only localMergeableRange are in fact mergeable
		if (this.getType == MatchType::FORWARD) {
			true
		} else {
			val mustBeMergeableRanges = this.getMergeableLocalRanges.intersection(impactedTokens)
			val mergeableRanges = this.getLocalBuffer.mergeableRanges.intersection(impactedTokens)
			mustBeMergeableRanges.difference(mergeableRanges).size == 0
		}
	}

	/**
	 * Recursive method to find where the {@link #getLocalRange()} of the
	 * current {@link #getLocalBuffer() localBuffer} is matched.
	 *
	 * @return a {@link Map} that associates: a {@link Range subranges} of the
	 * {@link #getLocalRange() localRange} of the  {@link #getLocalBuffer()
	 * localBuffer} to a {@link Pair} composed of a {@link Buffer} and a
	 * {@link Range} where the associated {@link Range subrange} is matched.
	 *
	 */
	def Map<Range, Pair<Buffer, Range>> getRoot() {
		val result = newLinkedHashMap
		val remoteRange = this.getLocalIndivisibleRange.translate(this.getRemoteIndex - this.getLocalIndex)

		// Termination case if the remote Buffer is not matched
		if (this.getRemoteBuffer.matched === null) {
			result.put(
				this.getLocalIndivisibleRange,
				this.getRemoteBuffer -> remoteRange
			)
		}
		// Else, recursive call
		else {
			for (match : this.getRemoteBuffer.matched.filter[it.getLocalIndivisibleRange.hasOverlap(remoteRange)]) {
				val recursiveResult = match.getRoot
				for (entry : recursiveResult.entrySet) {
					val range = entry.key

					// translate back to local range
					range.translate(this.getLocalIndex - this.getRemoteIndex)
					result.put(range, entry.value)
				}
			}
		}

		return result
	}

}

/**
 * This enumeration represent the type of the current {@link Match}
 */
enum MatchType {

	/**
	 * The {@link Match} links several inputs (or outputs) together.
	 * Not allowed anymore
	 */
	//INTER_SIBLINGS,
	/**
	 * The {@link Match} is internal to an actor and links an input {@link
	 * Buffer} to an output {@link Buffer}, <b>or</b> the {@link Match} is
	 * external to an actor (i.e. correspond to an edge) and it links an output
	 * {@link Buffer} of an actor to the input {@link Buffer} of the next.
	 */
	FORWARD,

	/**
	 * Opposite of FORWARD
	 */
	BACKWARD
}
