/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
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

import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.Set
import org.ietr.dftools.algorithm.model.dag.DAGVertex
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge

import static extension org.ietr.preesm.memory.script.Range.*
import org.eclipse.xtend.lib.annotations.Accessors

@SuppressWarnings("unchecked")
class Buffer {

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
	package static def getMultipleMatchRange(Buffer buffer) {

		return buffer.getMatchTable.values.flatten.getOverlappingRanges
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
	package static def getOverlappingRanges(Iterable<Match> matches) {
		val matchRanges = newArrayList
		val multipleMatchRanges = newArrayList

		// For each Match
		matches.forEach [ match |
			val newRange = match.getLocalRange
			// Get the intersection of the match and existing match ranges
			val intersections = matchRanges.intersection(newRange)
			multipleMatchRanges.union(intersections)
			// Update the existing match ranges
			matchRanges.union(newRange)
		]

		return multipleMatchRanges
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
	package def isCompletelyMatched() {
		val coveredRange = new ArrayList<Range>
		val iterEntry = this.getMatchTable.entrySet.iterator
		var stop = false

		while (iterEntry.hasNext && !stop) {
			val entry = iterEntry.next
			var iterMatch = entry.value.iterator
			while (iterMatch.hasNext && !stop) {
				val match = iterMatch.next
				val addedRange = coveredRange.union(match.getLocalRange)

				// Set stop to true if the range covers the complete token range
				stop = stop || (addedRange.getStart <= 0 && addedRange.getEnd >= getTokenSize * getNbTokens)
			}
		}

		// If the loops were stopped, a complete range was reached
		return stop
	}

	/**
	 * Test if all {@link Match matches} contained in the {@link
	 * Buffer#_machTable matchTable} are reciprocal.<br><br>
	 *
	 * A {@link Match} is reciprocal if the remote {@link Match#buffer}
     * contains an reciprocal {@link Match} in its {@link Buffer#_matchTable
     * matchTable}.
	 */
	package static def isReciprocal(Buffer localBuffer) {
		return localBuffer.getMatchTable.entrySet.forall [
			val matches = it.value
			val localIdx = it.key
			// for all matches
			matches.forall [ match |
				val remoteMatches = match.getRemoteBuffer.getMatchTable.get(match.getRemoteIndex)
				remoteMatches !== null && remoteMatches.contains(
					new Match(match.getRemoteBuffer, match.getRemoteIndex, localBuffer, localIdx, match.getLength))
			]
		]
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
	package static def simplifyMatches(Buffer buffer, List<Match> processedMatch) {
		val removedEntry = newArrayList

		// Process the match table
		buffer.getMatchTable.entrySet.forEach [
			val localIdx = it.key
			val matchSet = it.value
			// For each match
			matchSet.filter[!processedMatch.contains(it)].forEach [ match |
				var Match remMatch = null
				do {

					// Check if a consecutive match exist
					var candidateSet = buffer.getMatchTable.get(localIdx + match.getLength)

					// Since Buffer#check() is supposed valid
					// at most one candidate can satisfy the conditions
					remMatch = if (candidateSet !== null) {
						candidateSet.findFirst [ candidate |
							candidate.getRemoteBuffer == match.getRemoteBuffer // same target
							&& candidate.getRemoteIndex == match.getRemoteIndex + match.getLength
						]
					} else {
						null
					}
					if (remMatch !== null) {

						// Remove the consecutive match from matchTables
						candidateSet.remove(remMatch)
						val remMatchSet = remMatch.getRemoteBuffer.getMatchTable.get(remMatch.getRemoteIndex)
						remMatchSet.remove(remMatch.getReciprocate)

						// Remove empty matchLists from the matchTable
						if(remMatchSet.size == 0) remMatch.getRemoteBuffer.getMatchTable.remove(remMatch.getRemoteIndex)
						if(candidateSet.size == 0) removedEntry.add(localIdx + match.getLength)

						// Lengthen the existing match
						match.length = match.getLength + remMatch.getLength
						match.getReciprocate.length = match.getLength
					}
				} while (remMatch !== null)
				// Put the reciprocate match in the in the processes list
				processedMatch.add(match.getReciprocate)
			]
		]

		// Remove empty matchLists from matchTable
		removedEntry.forEach[buffer.getMatchTable.remove(it)]
	}

	/**
	 * cf {@link #minIndex}.
	 */
	@Accessors
	package int maxIndex

	/**
	 * Minimum index for the buffer content.
	 * Constructor initialize this value to 0 but it is possible to lower this
	 * value by matching another buffer on the "edge" of this one.<br>
	 * For example: <code>this.matchWith(-3, a, 0, 6)</code> results in
	 * matching this[-3..2] with a[0..5], thus lowering this.minIndex to -3.
	 */
	@Accessors
	package int minIndex

	/**
	 * This table is protected to ensure that matches are set only by using
	 * {@link #matchWith(int,Buffer,int)} methods in the scripts.
	 */
	@Accessors
	final package Map<Integer, List<Match>> matchTable

	/**
	 * This property is used to mark the {@link Buffer buffers} that were
	 * {@link #applyMatches(List) matched}.
	 * Originally set to <code>null</code>, it is replaced by a {@link List}
	 * of applied {@link Match} in the {@link #applyMatches(List) applyMatches}
	 * method.
	 */
	@Accessors
	package var List<Match> matched = null

	/**
	 * This property is set to <code>true</code> if a remote {@link Buffer} was merged
	 * within the current {@link Buffer}
	 */
	@Accessors
	package var boolean host = false

	@Accessors
	final String name

	@Accessors
	final int nbTokens

	@Accessors
	final int tokenSize

	@Accessors
	package final DAGVertex dagVertex

	@Accessors
	package final SDFEdge sdfEdge

	/**
	 * This {@link List} of {@link Range} is used to store its indivisible
	 * sub-parts. A buffer can effectively be divided only if its is not
	 * indivisible and if the division imposed by the matches do not break
	 * any indivisible range.
	 */
	@Accessors
	package List<Range> indivisibleRanges

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
	package List<List<Match>> divisibilityRequiredMatches

	@Accessors
	final protected Map<Range, Pair<Buffer, Integer>> appliedMatches

	/**
	 * This flag is set at the {@link Buffer} instantiation to indicate whether
	 * the buffer is mergeable or not. If the buffer is mergeable, all its
	 * virtual tokens will be associated to mergeable ranges. Otherwise they
	 * won't.
	 */
	@Accessors
	final boolean originallyMergeable

	@Accessors
	package List<Range> mergeableRanges

	/**
    * Constructor for the {@link Buffer}.
    * @param name
    * 	A {@link String} corresponding to the final name of the buffer.
    * @param nbTokens
    * 	The number of tokens stored in this buffer.
    * @param tokenSize
    * 	The size of one token of the buffer.
    */
	new(SDFEdge edge, DAGVertex dagVertex, String name, int nbTokens, int tokenSize, boolean mergeable) {
		this.sdfEdge = edge
		this.name = name
		this.nbTokens = nbTokens
		this.tokenSize = tokenSize
		this.matchTable = newHashMap()
		this.appliedMatches = newHashMap()
		this.minIndex = 0
		this.maxIndex = nbTokens * tokenSize
		this.dagVertex = dagVertex
		this.originallyMergeable = mergeable
		this.mergeableRanges = newArrayList
		if (mergeable) {
			this.mergeableRanges.add(new Range(0, nbTokens * tokenSize))
		}
		this.indivisibleRanges = newArrayList
		this.divisibilityRequiredMatches = newArrayList
	}

	package def getSdfVertex() {
		return getDagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex)
	}

	package def void setMaxIndex(int newValue) {

		// if the buffer was originally mergeable
		if (this.isOriginallyMergeable) {

			// Add a new mergeable range corresponding to the new virtual tokens
			this.getMergeableRanges.union(new Range(maxIndex, newValue))
		}
		this.maxIndex = newValue
	}

	package def void setMinIndex(int newValue) {

		// if the buffer was originally mergeable
		if (this.isOriginallyMergeable) {

			// Add a new mergeable range corresponding to the new virtual tokens
			this.getMergeableRanges.union(new Range(newValue, minIndex))
		}
		this.minIndex = newValue
	}

	/**
	* Cf. {@link Buffer#matchWith(int, Buffer, int, int)} with size = 1
 	*/
	def Match matchWith(int localIdx, Buffer buffer, int remoteIdx) {
		return matchWith(localIdx, buffer, remoteIdx, 1)
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
	def Match matchWith(int localIdx, Buffer buffer, int remoteIdx, int size) {

		if (this.getTokenSize != buffer.getTokenSize) {
			throw new RuntimeException(
				'''Cannot match «this.getDagVertex.name».«this.getName» with «buffer.getDagVertex.name».«buffer.getName» because buffers have different token sized («this.
					getTokenSize» != «buffer.getTokenSize» ")''')
		}

		// Test if a matched range is completely out of real tokens
		if ((localIdx >= this.getNbTokens) || (localIdx + size - 1 < 0)) {
			throw new RuntimeException(
				'''Cannot match «this.getDagVertex.name».«this.getName»[«localIdx»..«(localIdx + size - 1)»] and «buffer.
					getDagVertex.name».«buffer.getName»[«remoteIdx»..«(remoteIdx + size - 1)»] because no "real" token from «this.
					getDagVertex.name».«this.getName»[0..«(this.getNbTokens - 1)»] is matched.''')
		}
		if ((remoteIdx >= buffer.getNbTokens) || (remoteIdx + size - 1 < 0)) {
			throw new RuntimeException(
				'''Cannot match «this.getDagVertex.name».«this.getName»[«localIdx»..«localIdx + size - 1»] and «buffer.
					getDagVertex.name».«buffer.getName»[«remoteIdx»..«remoteIdx + size - 1»] because no "real" token from "«buffer.
					getDagVertex.name».«buffer.getName»[0..«buffer.getNbTokens - 1»] is matched.''')
		}

		// Are "virtual" tokens matched together
		if (// Both ranges begins before the first token
		((localIdx < 0) && (remoteIdx < 0)) ||
			// or both buffers ends after the last token
			((localIdx + size - 1 >= this.getNbTokens) && (remoteIdx + size - 1 >= buffer.getNbTokens)) ||
			// or local range begins with less real tokens than the number of virtual tokens beginning remote range
			(localIdx >= 0 && ((this.getNbTokens - localIdx) <= -Math::min(0, remoteIdx))) ||
			// or remote range begins with less real tokens than the number of virtual tokens beginning local range
			(remoteIdx >= 0 && ((buffer.getNbTokens - remoteIdx) <= -Math::min(0, localIdx)))) {
			throw new RuntimeException(
				'''Cannot match «this.getDagVertex.name».«this.getName»[«localIdx»..«localIdx + size - 1»] and «buffer.
					getDagVertex.name».«buffer.getName»[«remoteIdx»..«(remoteIdx + size - 1)»] because "virtual tokens" cannot be matched together.«"\n"»Information: «this.
					getDagVertex.name».«this.getName» size = «this.getNbTokens» and «buffer.getDagVertex.name».«buffer.getName» size = «buffer.
					getNbTokens».''')
		}

		return this.byteMatchWith(localIdx * getTokenSize, buffer, remoteIdx * getTokenSize, size * getTokenSize)
	}

	def Match byteMatchWith(int localByteIdx, Buffer buffer, int remoteByteIdx, int byteSize) {
		return byteMatchWith(localByteIdx, buffer, remoteByteIdx, byteSize, true)
	}

	def Match byteMatchWith(int localByteIdx, Buffer buffer, int remoteByteIdx, int byteSize, boolean check) {

		// Test if a matched range is completely out of real bytes
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
			if ((localByteIdx >= this.getNbTokens * this.getTokenSize) || (localByteIdx + byteSize - 1 < 0)) {
				throw new RuntimeException(
					'''Cannot match bytes «this.getDagVertex.name».«this.getName»[«localByteIdx»..«(localByteIdx + byteSize -
						1)»] and «buffer.getDagVertex.name».«buffer.getName»[«remoteByteIdx»..«(remoteByteIdx + byteSize - 1)»] because no "real" byte from «this.
						getDagVertex.name».«this.getName»[0..«(this.getNbTokens * this.getTokenSize - 1)»] is matched.''')
			}
			if ((remoteByteIdx >= buffer.getNbTokens * buffer.getTokenSize) || (remoteByteIdx + byteSize - 1 < 0)) {
				throw new RuntimeException(
					'''Cannot match bytes «this.getDagVertex.name».«this.getName»[«localByteIdx»..«localByteIdx + byteSize - 1»] and «buffer.
						getDagVertex.name».«buffer.getName»[«remoteByteIdx»..«remoteByteIdx + byteSize - 1»] because no "real" byte from "«buffer.
						getDagVertex.name».«buffer.getName»[0..«buffer.getNbTokens * buffer.getTokenSize - 1»] is matched.''')
			}

			// Are "virtual" tokens matched together
			if (// Both ranges begins before the first byte
			((localByteIdx < 0) && (remoteByteIdx < 0)) ||
				// or both buffers ends after the last byte
			((localByteIdx + byteSize - 1 >= this.getNbTokens * this.getTokenSize) &&
				(remoteByteIdx + byteSize - 1 >= buffer.getNbTokens * buffer.getTokenSize)) ||
				// or local range begins with less real bytes than the number of virtual bytes beginning remote range
				(localByteIdx >= 0 && ((this.getNbTokens * this.getTokenSize - localByteIdx) <= -Math::min(0, remoteByteIdx))) ||
				// or remote range begins with less real bytes than the number of virtual bytes beginning local range
				(remoteByteIdx >= 0 &&
					((buffer.getNbTokens * buffer.getTokenSize - remoteByteIdx) <= -Math::min(0, localByteIdx)))) {
				throw new RuntimeException(
					'''Cannot match bytes «this.getDagVertex.name».«this.getName»[«localByteIdx»..«localByteIdx + byteSize - 1»] and «buffer.
						getDagVertex.name».«buffer.getName»[«remoteByteIdx»..«(remoteByteIdx + byteSize - 1)»] because "virtual bytes" cannot be matched together.«"\n"»Information: «this.
						getDagVertex.name».«this.getName» size = «this.getNbTokens * this.getTokenSize» and «buffer.getDagVertex.name».«buffer.
						getName» size = «buffer.getNbTokens * buffer.getTokenSize».''')
			}
		}

		// If needed, update the buffers min/max indexes
		if (!(localByteIdx >= 0 && localByteIdx + byteSize - 1 < this.getNbTokens * this.getTokenSize)) {
			this.minIndex = Math::min(minIndex, localByteIdx)
			this.maxIndex = Math::max(maxIndex, (localByteIdx + byteSize))
		}
		if (!(remoteByteIdx >= 0 && remoteByteIdx + byteSize - 1 < buffer.getNbTokens * buffer.getTokenSize)) {
			buffer.minIndex = Math::min(buffer.minIndex, remoteByteIdx)
			buffer.maxIndex = Math::max(buffer.maxIndex, (remoteByteIdx + byteSize))
		}

		// Do the match
		var matchSet = getMatchTable.get(localByteIdx)
		if (matchSet === null) {
			matchSet = newArrayList
			getMatchTable.put(localByteIdx, matchSet)
		}
		val localMatch = new Match(this, localByteIdx, buffer, remoteByteIdx, byteSize)
		matchSet.add(localMatch)

		var remoteMatchSet = buffer.getMatchTable.get(remoteByteIdx)
		if (remoteMatchSet === null) {
			remoteMatchSet = newArrayList
			buffer.getMatchTable.put(remoteByteIdx, remoteMatchSet)
		}
		val remoteMatch = new Match(buffer, remoteByteIdx, this, localByteIdx, byteSize)
		remoteMatchSet.add(remoteMatch)

		localMatch.reciprocate = remoteMatch
		return localMatch
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
	package def boolean isDivisible() {
		return isCompletelyMatched && getIndivisibleRanges.size > 1 && {

			// Test that all ranges are covered by the indivisible ranges
			val List<Range> copy = new ArrayList<Range>(getIndivisibleRanges.map[it.clone as Range].toList)
			val firstElement = copy.head
			copy.remove(0)
			val coveredRange = copy.union(firstElement)
			(new Range(0, getNbTokens * getTokenSize)).difference(coveredRange).size == 0
		} && getMatchTable.values.flatten.forall [
			it.getRemoteBuffer.isIndivisible
		]
	}

	/**
	 *  A {@link Buffer} is indivisible if its {@link #getIndivisibleRanges()
	 * indivisibleRanges} attribute contains a unique {@link Range} that covers
	 * all the {@link #getMinIndex() minIndex} to {@link #getMaxIndex()
	 * maxIndex} {@link Range}. <br>
	 * <b> An {@link Buffer} that is not {@link #isIndivisible() indivisible}
	 * is not necessarily {@link #isDivisible() divisible}. Indeed, it might
	 * fulfill parts of the conditions to be divisible.</b>
	 */
	package def boolean isIndivisible() {
		return this.getIndivisibleRanges.size == 1 && this.getIndivisibleRanges.head.getStart == this.getMinIndex &&
			this.getIndivisibleRanges.head.getEnd == this.getMaxIndex
	}

	override toString() '''«getSdfVertex.name».«getName»[«getNbTokens * getTokenSize»]'''

	/**
	 * We do not check that the match is possible !
	 * We just apply it and assume all checks are performed somewhere else !
	 * The local buffer is merged into the remote buffer
	 * The local buffer does not "exists" afterwards
	 */
	package def void applyMatches(List<Match> matches) {

		// copy the list to iterate on it
		// Otherwise the list would be modified during the iteration since it
		// is the result of a flatten or a filter operation.
		val matchesCopy = new ArrayList(matches)

		// Check that all match have the current buffer as local
		if (!matchesCopy.forall[it.getLocalBuffer == this]) {
			throw new RuntimeException(
				"Incorrect call to applyMatches method.\n One of the given matches does not belong to the this Buffer.")
		}

		// Check that the matches completely cover the buffer
		val matchedRange = matchesCopy.fold(new ArrayList<Range>) [ res, match |
			res.union(match.getLocalIndivisibleRange)
			res
		]
		val tokenRange = new Range(0, getTokenSize * getNbTokens)
		if (matchedRange.intersection(tokenRange).head != tokenRange) {
			throw new RuntimeException(
				"Incorrect call to applyMatches method.\n All real token must be covered by the given matches.\n" +
					matches)
		}

		// Check that the matches do not overlap
		if (!matchesCopy.forall [ match |
			matchesCopy.filter[it !== match].forall[!match.getLocalIndivisibleRange.hasOverlap(it.getLocalIndivisibleRange)]
		]) {
			throw new RuntimeException(
				"Incorrect call to applyMatches method.\n Given matches are overlapping in the localBuffer.\n" +
					matches)
		}

		// Check that all matches are applicable
		if (!matches.forall[it.isApplicable && it.getReciprocate.isApplicable]) {
			throw new RuntimeException(
				"Incorrect call to applyMatches method.\n One or more applied matches are not applicable.\n" +
					matches.filter[!it.isApplicable || !it.getReciprocate.isApplicable])
		}

		for (match : matchesCopy) {

			getAppliedMatches.put(match.getLocalIndivisibleRange, match.getRemoteBuffer -> match.getRemoteIndex)
			match.getRemoteBuffer.host = true;

			// Fill the forbiddenLocalRanges of conflictCandidates and conflictingMatches
			// of the applied match
			updateForbiddenAndMergeableLocalRanges(match)

			// Transfer the forbiddenLocalRanges of the applied match to the
			// matches of its local and remote buffers that have no conflicts
			// with the appliedMatch or its reciprocate
			val forwardMatch = if(match.getType == MatchType::FORWARD) match else match.getReciprocate

			// For each backward match of the localBuffer (i.e. not conflicting with the applied match)
			forwardMatch.getLocalBuffer.getMatchTable.values.flatten.filter[it.getType == MatchType::BACKWARD].forEach [
				// Copy the forbiddenLocalRanges of the applied forward match
				val newForbiddenRanges = new ArrayList(forwardMatch.getForbiddenLocalRanges.map[it.clone as Range])
				// translate to the backward match remoteBuffer indexes
				newForbiddenRanges.translate(it.getRemoteIndex - it.getLocalIndex)
				// Add it to the forward match (i.e. the reciprocate of the backward)
				it.getReciprocate.getForbiddenLocalRanges.union(newForbiddenRanges)
			]

			// For each forward match of the remoteBuffer (i.e. not conflicting with the applied match)
			forwardMatch.getRemoteBuffer.getMatchTable.values.flatten.filter[it.getType == MatchType::FORWARD].forEach [
				// Copy the forbiddenLocalRanges and mergeableLocalRange of the applied backward match
				val newForbiddenRanges = new ArrayList(
					forwardMatch.getReciprocate.getForbiddenLocalRanges.map[it.clone as Range])
				val newMergeableRanges = new ArrayList(
					forwardMatch.getReciprocate.getMergeableLocalRanges.map[it.clone as Range])
				// translate to the forward match remoteBuffer indexes
				newForbiddenRanges.translate(it.getRemoteIndex - it.getLocalIndex)
				newMergeableRanges.translate(it.getRemoteIndex - it.getLocalIndex)
				// Add it to the backward match (i.e. the reciprocate of the forward)
				it.getReciprocate.getForbiddenLocalRanges.union(newForbiddenRanges)
				it.getReciprocate.getMergeableLocalRanges.union(newMergeableRanges)
				// Remove forbiddenRanges from mergeableRanges
				it.getReciprocate.mergeableLocalRanges = it.getReciprocate.getMergeableLocalRanges.difference(
					it.getReciprocate.getForbiddenLocalRanges)
			]

			// Update the conflictCandidates
			// Must be done befor forwarding third-party matches
			updateConflictCandidates(match)

			// Move all third-party matches from the matched range of the merged buffer
			match.getLocalBuffer.getMatchTable.values.flatten.filter [
				it != match && it.getLocalRange.hasOverlap(match.getLocalIndivisibleRange)
			].toList.immutableCopy.forEach [ movedMatch |
				// Remove old match from original match list
				val localList = match.getLocalBuffer.getMatchTable.get(movedMatch.getLocalIndex)
				localList.remove(movedMatch)
				if (localList.size == 0) {
					match.getLocalBuffer.getMatchTable.remove(movedMatch.getLocalIndex)
				}
				// Change the match local buffer and index
				// Length and remoteBuffer are unchanged
				movedMatch.localBuffer = match.getRemoteBuffer
				movedMatch.localIndex = movedMatch.getLocalIndex - (match.getLocalIndex - match.getRemoteIndex)
				// Update the reciprocate
				movedMatch.getReciprocate.remoteBuffer = movedMatch.getLocalBuffer
				movedMatch.getReciprocate.remoteIndex = movedMatch.getLocalIndex
				// Put the moved match in its new host matchTable
				var matchList = match.getRemoteBuffer.getMatchTable.get(movedMatch.getLocalIndex)
				if (matchList === null) {
					matchList = newArrayList
					match.getRemoteBuffer.getMatchTable.put(movedMatch.getLocalIndex, matchList)
				}
				matchList.add(movedMatch)
			]

			// Update the min and max index of the remoteBuffer (if necessary)
			// Must be called before updateRemoteMergeableRange(match)
			updateRemoteIndexes(match)

			// Update divisability if remote buffer
			// The divisability update must not be applied if the applied match involves
			// the division of the local buffer, instead the remote buffer should become !
			// non divisable ! <= Note Since buffer division is conditioned by the
			// indivisibility of the remote buffer, this remark should probably be ignored
			updateDivisibleRanges(match)

			// Update the mergeable range of the remote buffer
			updateRemoteMergeableRange(match)

			// Update Matches
			updateMatches(match)

			// Update conflicting matches
			var matchToUpdate = match.getRemoteBuffer.getMatchTable.values.flatten.filter[it != match.getReciprocate]
			while (matchToUpdate.size != 0) {
				matchToUpdate = updateConflictingMatches(matchToUpdate)
			}

			// Remove the applied match from the buffers match table
			// (local and reciprocate)
			match.unmatch

			// Match was applied (and reciprocate)
			match.applied = true
			match.getReciprocate.applied = true
		}

		// Mark the buffer as Matched
		this.matched = matchesCopy
	}

	package def updateForbiddenAndMergeableLocalRanges(Match match) {

		// For the forward match, simply fill the forbidden ranges
		val forwardMatch = if(match.getType == MatchType::FORWARD) match else match.getReciprocate
		#[forwardMatch.getConflictCandidates, forwardMatch.getConflictingMatches].flatten.forEach [ conflictMatch |
			// Must be extracted for each iteration because the union modifies the range
			val impactedRange = forwardMatch.getReciprocate.getLocalImpactedRange
			impactedRange.translate(conflictMatch.getLocalIndex - conflictMatch.getRemoteIndex)
			conflictMatch.getForbiddenLocalRanges.union(impactedRange)
		]

		// For backward match, fill the forbidden an mergeable ranges (if any)
		val backwardMatch = if(match.getType == MatchType::BACKWARD) match else match.getReciprocate

		// Get the target mergeable range
		val impactedRange = backwardMatch.getReciprocate.getLocalImpactedRange
		impactedRange.translate(backwardMatch.getLocalIndex - backwardMatch.getRemoteIndex)
		val remoteMergeableRange = backwardMatch.getLocalBuffer.getMergeableRanges.intersection(impactedRange)

		// No need to remove forbidden ranges from it. Indeed, if there are such
		// range, the match couldn't have been applied
		// Compute forbidden ranges
		val forbiddenRanges = #[impactedRange].difference(remoteMergeableRange)

		// translate it back to source indexes
		remoteMergeableRange.translate(backwardMatch.getRemoteIndex - backwardMatch.getLocalIndex)
		forbiddenRanges.translate(backwardMatch.getRemoteIndex - backwardMatch.getLocalIndex)

		#[backwardMatch.getConflictCandidates, backwardMatch.getConflictingMatches].flatten.forEach [ conflictMatch |
			val newMergeableRanges = new ArrayList(remoteMergeableRange.map[it.clone as Range])
			val newForbiddenRanges = new ArrayList(forbiddenRanges.map[it.clone as Range])
			// translate it to localBuffer of conflictMatches indexes
			newMergeableRanges.translate(conflictMatch.getLocalIndex - conflictMatch.getRemoteIndex)
			newForbiddenRanges.translate(conflictMatch.getLocalIndex - conflictMatch.getRemoteIndex)
			conflictMatch.getMergeableLocalRanges.union(newMergeableRanges)
			conflictMatch.getForbiddenLocalRanges.union(newForbiddenRanges)
			// remove forbidden Ranges from mergeable ranges
			conflictMatch.mergeableLocalRanges = conflictMatch.getMergeableLocalRanges.difference(
				conflictMatch.getForbiddenLocalRanges)
		]
	}

	package static def updateMatches(Match match) {

		// 1- For all matches of the remote buffer (old and newly added)
		// 1.1- If the match (local and remote) ranges falls within
		// indivisible range(s) larger than the match length
		//      Then:
		// 1.1.1- the match must be enlarged to cover this range
		//        Several matches might become redundant (i.e. identical) in the process
		val List<Pair<Match, Range>> modifiedMatches = newArrayList
		match.getRemoteBuffer.getMatchTable.values.flatten.filter[it != match.getReciprocate].forEach [ testedMatch |
			// Get the aligned smallest indivisible range (local or remote)
			val localIndivisibleRange = testedMatch.getLocalIndivisibleRange
			val remoteIndivisibleRange = testedMatch.getReciprocate.getLocalIndivisibleRange
			remoteIndivisibleRange.translate(testedMatch.getLocalIndex - testedMatch.getRemoteIndex)
			val smallestRange = if (localIndivisibleRange.getLength <= remoteIndivisibleRange.getLength) {
					localIndivisibleRange
				} else {
					remoteIndivisibleRange
				}
			// Check if the range was modified
			if (smallestRange != testedMatch.getLocalRange) {

				// Need to enlarge the match
				modifiedMatches.add(new Pair(testedMatch, smallestRange))
			}
		]
		modifiedMatches.forEach [
			val modifiedMatch = it.key
			val newRange = it.value
			// Update the match
			modifiedMatch.length = newRange.getLength
			modifiedMatch.getReciprocate.length = newRange.getLength
			// If the match must be moved
			val originalIndex = modifiedMatch.getLocalIndex
			val originalRemoteIndex = modifiedMatch.getRemoteIndex
			if (newRange.getStart != originalIndex) {

				// Move the local match
				modifiedMatch.localIndex = newRange.getStart
				modifiedMatch.remoteIndex = originalRemoteIndex + newRange.getStart - originalIndex
				modifiedMatch.getLocalBuffer.getMatchTable.get(originalIndex).remove(modifiedMatch)
				val localList = modifiedMatch.getLocalBuffer.getMatchTable.get(newRange.getStart) ?: {
					val newList = newArrayList
					modifiedMatch.getLocalBuffer.getMatchTable.put(newRange.getStart, newList)
					newList
				}
				localList.add(modifiedMatch)
				// Remove the old list if it is empty
				if(modifiedMatch.getLocalBuffer.getMatchTable.get(originalIndex).empty){
					modifiedMatch.getLocalBuffer.getMatchTable.remove(originalIndex)
				}

				// Move the remote match
				modifiedMatch.getReciprocate.localIndex = modifiedMatch.getRemoteIndex
				modifiedMatch.getReciprocate.remoteIndex = modifiedMatch.getLocalIndex
				modifiedMatch.getRemoteBuffer.getMatchTable.get(originalRemoteIndex).remove(modifiedMatch.getReciprocate)
				val remoteList = modifiedMatch.getRemoteBuffer.getMatchTable.get(modifiedMatch.getRemoteIndex) ?: {
					val newList = newArrayList
					modifiedMatch.getRemoteBuffer.getMatchTable.put(modifiedMatch.getRemoteIndex, newList)
					newList
				}
				remoteList.add(modifiedMatch.getReciprocate)
				// Remove the old list if it is empty
				if(modifiedMatch.getRemoteBuffer.getMatchTable.get(originalRemoteIndex).empty){
					modifiedMatch.getRemoteBuffer.getMatchTable.remove(originalRemoteIndex)
				}

			}
		]

		// Find redundant matches
		val matches = new ArrayList(match.getRemoteBuffer.getMatchTable.values.flatten.toList)
		val Set<Integer> redundantMatches = newHashSet
		var i = 0
		while (i < matches.size - 1) {

			// If the current match is not already redundant
			if (!redundantMatches.contains(i)) {
				val currentMatch = matches.get(i)
				var j = i + 1
				while (j < matches.size) {
					val redundantMatch = matches.get(j)
					if (currentMatch == redundantMatch) {

						// Matches are redundant
						redundantMatches.add(j)

						// It does not matter if the redundant matches were conflicting.
						// If this code is reached, it means that the were not since they
						// now have the same target and destination.
						// Transfer conflictCandidates from the redundantMatch to the currentMatch
						var transferredConflictCandidates = new ArrayList(
							redundantMatch.getConflictCandidates.filter [
								!currentMatch.getConflictCandidates.contains(it) &&
									!currentMatch.getConflictingMatches.contains(it) && it != currentMatch
							].toList)
						transferredConflictCandidates.forEach [
							it.getConflictCandidates.remove(redundantMatch)
							it.getConflictCandidates.add(currentMatch)
							currentMatch.getConflictCandidates.add(it)
						]

						// And reciprocates
						transferredConflictCandidates = new ArrayList(
							redundantMatch.getReciprocate.getConflictCandidates.filter [
								!currentMatch.getReciprocate.getConflictCandidates.contains(it) &&
									!currentMatch.getReciprocate.getConflictingMatches.contains(it) &&
									it != currentMatch.getReciprocate
							].toList)
						transferredConflictCandidates.forEach [
							it.getConflictCandidates.remove(redundantMatch.getReciprocate)
							it.getConflictCandidates.add(currentMatch.getReciprocate)
							currentMatch.getReciprocate.getConflictCandidates.add(it)
						]

						// Transfer conflictCandidates from the redundantMatch to the currentMatch
						var transferredConflictingMatches = new ArrayList(
							redundantMatch.getConflictingMatches.filter [
								!currentMatch.getConflictingMatches.contains(it) && it != currentMatch
							].toList)
						transferredConflictingMatches.forEach [
							// remove from conflict candidates if it was present
							it.getConflictCandidates.remove(currentMatch)
							currentMatch.getConflictCandidates.remove(it)
							it.getConflictingMatches.remove(redundantMatch)
							it.getConflictingMatches.add(currentMatch)
							currentMatch.getConflictingMatches.add(it)
						]

						// and reciprocates
						transferredConflictingMatches = new ArrayList(
							redundantMatch.getReciprocate.getConflictingMatches.filter [
								!currentMatch.getReciprocate.getConflictingMatches.contains(it) &&
									it != currentMatch.getReciprocate
							].toList)
						transferredConflictingMatches.forEach [
							// remove from conflict candidates if it was present
							it.getConflictCandidates.remove(currentMatch.getReciprocate)
							currentMatch.getReciprocate.getConflictCandidates.remove(it)
							it.getConflictingMatches.remove(redundantMatch.getReciprocate)
							it.getConflictingMatches.add(currentMatch.getReciprocate)
							currentMatch.getReciprocate.getConflictingMatches.add(it)
						]

						// Update localForbiddenRanges and localMergeableRanges
						val forwardMatch = if (currentMatch.getType == MatchType::FORWARD)
								currentMatch
							else
								currentMatch.getReciprocate
						val redundantForwardMatch = if (redundantMatch.getType == MatchType::FORWARD)
								redundantMatch
							else
								redundantMatch.getReciprocate
						forwardMatch.forbiddenLocalRanges = forwardMatch.getForbiddenLocalRanges.intersection(
							redundantForwardMatch.getForbiddenLocalRanges)

						forwardMatch.getReciprocate.forbiddenLocalRanges = forwardMatch.getReciprocate.getForbiddenLocalRanges.
							intersection(redundantForwardMatch.getReciprocate.getForbiddenLocalRanges)
						forwardMatch.getReciprocate.mergeableLocalRanges = forwardMatch.getReciprocate.getMergeableLocalRanges.
							intersection(redundantForwardMatch.getReciprocate.getMergeableLocalRanges)

					}
					j = j + 1
				}
			}
			i = i + 1
		}

		// do the removal :
		if (redundantMatches.size > 0) {

			val removedMatches = new ArrayList(redundantMatches.map[matches.get(it)].toList)
			removedMatches.forEach [
				unmatch(it)
			]
		}
	}

	/**
	 * Must be called before {@link ScriptRunner#updateConflictingMatches()
	 * updating conflicting matches}.
	 */
	package def updateConflictCandidates(Match match) {

		// 1. Conflict candidates of the applied local->remote match are
		// added to all remote->other matches (except inter siblings and
		// the already conflicting to remote->local (i.e. the backward if
		// local->remote is forward or vice versa))
		// 2. Conflict candidates of the applied remote->local match are
		// added to all local->other matches (except inter siblings and
		// the already conflicting to local->remote (i.e. the forward if
		// remote->local is backward or vice versa))
		// 1
		val newConflicts = newArrayList
		if (!match.getReciprocate.getConflictCandidates.empty || !match.getReciprocate.getConflictingMatches.empty) {
			match.getRemoteBuffer.getMatchTable.values.flatten.filter [
				it.getType == match.getType
			].forEach [ otherMatch |
				otherMatch.getReciprocate.getConflictCandidates.addAll(match.getReciprocate.getConflictCandidates)
				otherMatch.getReciprocate.getConflictCandidates.addAll(match.getReciprocate.getConflictingMatches)
				newConflicts.add(otherMatch.getReciprocate)
			]
			match.getReciprocate.getConflictCandidates.forEach[it.getConflictCandidates.addAll(newConflicts)]
			match.getReciprocate.getConflictingMatches.forEach[it.getConflictCandidates.addAll(newConflicts)]
			newConflicts.clear
		}

		// 2.
		if (!match.getConflictCandidates.empty || !match.getConflictingMatches.empty) {
			match.getLocalBuffer.getMatchTable.values.flatten.filter [
				it.getType != match.getType
			].forEach [ otherMatch |
				otherMatch.getReciprocate.getConflictCandidates.addAll(match.getConflictCandidates)
				otherMatch.getReciprocate.getConflictCandidates.addAll(match.getConflictingMatches)
				newConflicts.add(otherMatch.getReciprocate)
			]
			match.getConflictCandidates.forEach[it.getConflictCandidates.addAll(newConflicts)]
			match.getConflictingMatches.forEach[it.getConflictCandidates.addAll(newConflicts)]
			newConflicts.clear
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
	package static def List<Match> updateConflictingMatches(Iterable<Match> matchList) {

		val updatedMatches = newArrayList
		matchList.forEach [ match |
			// Check all the conflict candidates
			var iter = match.getConflictCandidates.iterator
			while (iter.hasNext) {
				val candidate = iter.next
				if (candidate.getReciprocate.getLocalImpactedRange.hasOverlap(match.getReciprocate.getLocalImpactedRange)) {
					iter.remove

					// Add the candidate to the conflicting matches
					match.getConflictingMatches.add(candidate)

					// Remove it from the reciprocate candidates (if it was present)
					updatedMatches.add(candidate)
				}
			}
			// Do the same for reciprocate
			iter = match.getReciprocate.getConflictCandidates.iterator
			while (iter.hasNext) {
				val candidate = iter.next
				if (candidate.getReciprocate.getLocalImpactedRange.hasOverlap(match.getLocalImpactedRange)) {
					iter.remove

					// Add the candidate to the conflicting matches
					match.getReciprocate.getConflictingMatches.add(candidate)

					// Remove it from the candidates (if it was present)
					if (!updatedMatches.contains(candidate.getReciprocate)) {
						updatedMatches.add(candidate.getReciprocate)
					}
				}
			}
		]

		return updatedMatches
	}

	/**
	 * MUST be called before updateRemoteMergeableRange because the updated local
	 * indexes are used in the current function, which cause an update of the mergeable ranges.
	 * @return true of the indexes were updated, false otherwise
	 */
	package def updateRemoteIndexes(Match match) {
		var res = false;

		// Get the local indivisible ranges involved in the match
		val localIndivisibleRange = match.getLocalIndivisibleRange

		// Align them with the remote ranges
		localIndivisibleRange.translate(match.getRemoteIndex - match.getLocalIndex)

		// Update the remote buffer indexes if needed.
		if (localIndivisibleRange.getStart < match.getRemoteBuffer.getMinIndex) {
			res = true
			match.getRemoteBuffer.minIndex = localIndivisibleRange.getStart
		}

		if (localIndivisibleRange.getEnd > match.getRemoteBuffer.getMaxIndex) {
			res = true
			match.getRemoteBuffer.maxIndex = localIndivisibleRange.getEnd
		}

		return res
	}

	/**
	 * Also update the {@link #getDivisibilityRequiredMatches()
	 * divisibilityRequiredMatches} {@link List} of the {@link Buffer}.
	 *
	 */
	package def void updateDivisibleRanges(Match match) {
		val localRange = match.getLocalRange

		// Get the local indivisible ranges involved in the match
		val localIndivisibleRanges = match.getLocalBuffer.getIndivisibleRanges.filter [
			// An indivisible range can go beyond the matched
			// range. For example, if the range includes virtual tokens
			it.hasOverlap(localRange)
		].map[it.clone as Range].toList // toList to make sure the map function is applied only once

		// Align them with the remote ranges
		localIndivisibleRanges.translate(match.getRemoteIndex - match.getLocalIndex)

		// Do the lazy union
		// The divisability update must not be applied if the applied match involves
		// the division of the local buffer, instead the remote buffer should become !
		// non divisable !
		match.getRemoteBuffer.getIndivisibleRanges.lazyUnion(localIndivisibleRanges)

		// If the destination range is still divisible,(i.e. if the remote
		// localRange overlaps more than a unique indivisible Range.)
		// Then Forward all DivisibilityRequiredMatches from the local Buffer
		// No risk if the match is applied as a result of a division since
		// in such case, the destination is compulsorily indivisible
		if (match.getRemoteBuffer.getIndivisibleRanges.filter [
			it.hasOverlap(match.getReciprocate.getLocalRange)
		].size > 1) {
			match.getRemoteBuffer.getDivisibilityRequiredMatches.addAll(match.getLocalBuffer.getDivisibilityRequiredMatches)
		}
	}

	/**
	 * Must be called after updateRemoteIndexesAndDivisibleRanges
	 */
	package def void updateRemoteMergeableRange(Match match) {

		// 1 - Get the mergeable ranges that are involved in the match
		// Get the local involved Range
		val involvedRange = match.getLocalIndivisibleRange
		val localMergeableRange = match.getLocalBuffer.getMergeableRanges.intersection(involvedRange)

		// Translate it to get the remote involved range
		involvedRange.translate(match.getRemoteIndex - match.getLocalIndex)
		val remoteMergeableRange = match.getRemoteBuffer.getMergeableRanges.intersection(involvedRange)

		// 2 - Realign the two ranges
		localMergeableRange.translate(-match.getLocalIndex)
		remoteMergeableRange.translate(-match.getRemoteIndex)

		// 3 - Get intersection => the mergeable range of the result
		val resultMergeableRange = localMergeableRange.intersection(remoteMergeableRange)

		// 4 - Update the destination mergeable range
		// no need to update the origin mergeable range since
		// this buffer will no longer be used in the processing
		// 4.1 - compute the Mergeable range that must be removed
		// from the destination buffer
		val unmergeableRange = remoteMergeableRange.difference(resultMergeableRange)

		// 4.2 - Realign unmergeable range with destination buffer
		unmergeableRange.translate(match.getRemoteIndex)

		// 4.3 - Remove it from the remoteMergeableRange
		match.getRemoteBuffer.mergeableRanges = match.getRemoteBuffer.getMergeableRanges.difference(unmergeableRange)
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
	package static def unmatch(Match match) {
		val it = match

		// Local unmatch
		val localList = getLocalBuffer.getMatchTable.get(getLocalIndex)
		var iter = localList.iterator
		while (iter.hasNext) {

			// use the triple === to remove the correct
			// match because several matches might be ==
			if (iter.next === it) {
				iter.remove
			}
		}

		// Remove empty lists
		if (localList.size == 0) {
			getLocalBuffer.getMatchTable.remove(getLocalIndex)
		}

		// Remote unmatch
		val remoteList = getRemoteBuffer.getMatchTable.get(getRemoteIndex)
		iter = remoteList.iterator
		while (iter.hasNext) {

			// use the triple === to remove the correct
			// match because several matches might be ==
			if (iter.next === it.getReciprocate) {
				iter.remove
			}
		}
		if (remoteList.size == 0) {
			getRemoteBuffer.getMatchTable.remove(getRemoteIndex)
		}

		// Remove it from conflictingMatches and conflictCandidates
		match.getConflictCandidates.forEach [
			val iterator = it.getConflictCandidates.iterator
			while (iterator.hasNext) {
				if(iterator.next === match) iterator.remove
			}
		]
		match.getConflictingMatches.forEach [
			val iterator = it.getConflictingMatches.iterator
			while (iterator.hasNext) {
				if(iterator.next === match) iterator.remove
			}
		]
		match.getReciprocate.getConflictCandidates.forEach [
			val iterator = it.getConflictCandidates.iterator
			while (iterator.hasNext) {
				if(iterator.next === match.getReciprocate) iterator.remove
			}
		]
		match.getReciprocate.getConflictingMatches.forEach [
			val iterator = it.getConflictingMatches.iterator
			while (iterator.hasNext) {
				if(iterator.next === match.getReciprocate) iterator.remove
			}
		]
	}

	/**
	 * This method checks if the given {@link Match Matches} are sufficient to
	 * complete the {@link #getDivisibilityRequiredMatches()} condition.
	 *
	 */
	package def boolean doesCompleteRequiredMatches(Iterable<Match> matches) {

		// Remove completed lists
		val iter = this.getDivisibilityRequiredMatches.iterator
		while (iter.hasNext) {

			// In the current version we only check if all lists are completelyMatched
			// for better optimization, we must check if each list contains enough applied matches
			// to cover the complete original range
			val list = iter.next
			if (list.forall[it.isApplied]) {
				iter.remove
			}
		}

		// Check if the proposed matches completes the remaining lists
		return this.getDivisibilityRequiredMatches.forall [ list |
			matches.toList.containsAll(list)
		]
	}

}
