package org.ietr.preesm.experiment.memory

import java.util.ArrayList
import java.util.List
import java.util.Map
import org.ietr.dftools.algorithm.model.dag.DAGVertex
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge

import static extension org.ietr.preesm.experiment.memory.Range.*

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
	static def getMultipleMatchRange(Buffer buffer) {

		buffer.matchTable.values.flatten.overlappingRanges
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
	static def getOverlappingRanges(Iterable<Match> matches) {
		val matchRanges = newArrayList
		val multipleMatchRanges = newArrayList

		// For each Match
		matches.forEach [ match |
			val newRange = match.localRange
			// Get the intersection of the match and existing match ranges
			val intersections = matchRanges.intersection(newRange)
			multipleMatchRanges.union(intersections)
			// Update the existing match ranges
			matchRanges.union(newRange)
		]

		multipleMatchRanges
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
	def isCompletelyMatched() {
		val coveredRange = new ArrayList<Range>
		val iterEntry = this.matchTable.entrySet.iterator
		var stop = false

		while (iterEntry.hasNext && !stop) {
			val entry = iterEntry.next
			var iterMatch = entry.value.iterator
			while (iterMatch.hasNext && !stop) {
				val match = iterMatch.next
				val addedRange = coveredRange.union(match.localRange)

				// Set stop to true if the range covers the complete token range
				stop = stop || (addedRange.start <= 0 && addedRange.end >= tokenSize * nbTokens)
			}
		}

		// If the loops were stopped, a complete range was reached
		stop
	}

	/**
	 * Test if all {@link Match matches} contained in the {@link 
	 * Buffer#_machTable matchTable} are reciprocal.<br><br>
	 * 
	 * A {@link Match} is reciprocal if the remote {@link Match#buffer}
     * contains an reciprocal {@link Match} in its {@link Buffer#_matchTable
     * matchTable}.  
	 */
	static def isReciprocal(Buffer localBuffer) {
		localBuffer.matchTable.entrySet.forall [
			val matches = it.value
			val localIdx = it.key
			// for all matches
			matches.forall [ match |
				val remoteMatches = match.remoteBuffer.matchTable.get(match.remoteIndex)
				remoteMatches != null && remoteMatches.contains(
					new Match(match.remoteBuffer, match.remoteIndex, localBuffer, localIdx, match.length))
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
	static def simplifyMatches(Buffer buffer, List<Match> processedMatch) {
		val removedEntry = newArrayList

		// Process the match table
		buffer.matchTable.entrySet.forEach [
			val localIdx = it.key
			val matchSet = it.value
			// For each match
			matchSet.filter[!processedMatch.contains(it)].forEach [ match |
				var Match remMatch = null
				do {

					// Check if a consecutive match exist
					var candidateSet = buffer.matchTable.get(localIdx + match.length)

					// Since Buffer#check() is supposed valid
					// at most one candidate can satisfy the conditions
					remMatch = if (candidateSet != null) {
						candidateSet.findFirst [ candidate |
							candidate.remoteBuffer == match.remoteBuffer // same target
							&& candidate.remoteIndex == match.remoteIndex + match.length
						]
					} else {
						null
					}
					if (remMatch != null) {

						// Remove the consecutive match from matchTables
						candidateSet.remove(remMatch)
						val remMatchSet = remMatch.remoteBuffer.matchTable.get(remMatch.remoteIndex)
						remMatchSet.remove(remMatch.reciprocate)

						// Remove empty matchLists from the matchTable
						if(remMatchSet.size == 0) remMatch.remoteBuffer.matchTable.remove(remMatch.remoteIndex)
						if(candidateSet.size == 0) removedEntry.add(localIdx + match.length)

						// Lengthen the existing match
						match.length = match.length + remMatch.length
						match.reciprocate.length = match.length
					}
				} while (remMatch != null)
				// Put the reciprocate match in the in the processes list
				processedMatch.add(match.reciprocate)
			]
		]

		// Remove empty matchLists from matchTable
		removedEntry.forEach[buffer.matchTable.remove(it)]
	}

	/**
	 * cf {@link #minIndex}.
	 */
	@Property
	int maxIndex

	/**
	 * Minimum index for the buffer content.
	 * Constructor initialize this value to 0 but it is possible to lower this
	 * value by matching another buffer on the "edge" of this one.<br>
	 * For example: <code>this.matchWith(-3, a, 0, 6)</code> results in 
	 * matching this[-3..2] with a[0..5], thus lowering this.minIndex to -3.
	 */
	@Property
	int minIndex

	/**
	 * This table is protected to ensure that matches are set only by using
	 * {@link #matchWith(int,Buffer,int)} methods in the scripts.
	 */
	@Property
	final protected Map<Integer, List<Match>> matchTable

	@Property
	final String name

	@Property
	final int nbTokens

	@Property
	final int tokenSize

	@Property
	final DAGVertex dagVertex

	@Property
	final SDFEdge sdfEdge

	/**
	 * This {@link List} of {@link Range} is used to store its indivisible 
	 * sub-parts. A buffer can effectively be divided only if its is not 
	 * indivisible and if the division imposed by the matches do not break
	 * any indivisible range.
	 */
	@Property
	List<Range> indivisibleRanges

	/**
	 * This {@link List} contains all {@link Match} that must be applied
	 * to authorize the division of a {@link Buffer}. 
	 * The {@link List} contains {@link List} of {@link Match}. To authorize
	 * a division, each sublist must contain enough {@link Match#isApplied() 
	 * applied} {@link Match} to cover all the tokens (real and virtual) of
	 * the original {@link Match#getLocalBuffer() localBuffer} of the
	 *  {@link Match matches}.
	 */
	@Property
	List<List<Match>> divisibilityRequiredMatches

	@Property
	final protected Map<Range, Pair<Buffer, Integer>> appliedMatches

	/**
	 * This flag is set at the {@link Buffer} instantiation to indicate whether
	 * the buffer is mergeable or not. If the buffer is mergeable, all its 
	 * virtual tokens will be associated to mergeable ranges. Otherwise they 
	 * won't.
	 */
	@Property
	final boolean originallyMergeable

	@Property
	List<Range> mergeableRanges

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
		_sdfEdge = edge
		_name = name
		_nbTokens = nbTokens
		_tokenSize = tokenSize
		_matchTable = newHashMap()
		_appliedMatches = newHashMap()
		_minIndex = 0
		_maxIndex = nbTokens * tokenSize
		_dagVertex = dagVertex
		_originallyMergeable = mergeable
		_mergeableRanges = newArrayList
		if (mergeable) {
			_mergeableRanges.add(new Range(0, nbTokens * tokenSize))
		}
		_indivisibleRanges = newArrayList
		_divisibilityRequiredMatches = newArrayList
	}

	def getSdfVertex() {
		dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex) as SDFAbstractVertex
	}

	private def setMaxIndex(int newValue) {

		// if the buffer was originally mergeable
		if (this.originallyMergeable) {

			// Add a new mergeable range corresponding to the new virtual tokens
			this.mergeableRanges.union(new Range(_maxIndex, newValue))
		}
		_maxIndex = newValue
	}

	private def setMinIndex(int newValue) {

		// if the buffer was originally mergeable
		if (this.originallyMergeable) {

			// Add a new mergeable range corresponding to the new virtual tokens
			this.mergeableRanges.union(new Range(newValue, _minIndex))
		}
		_minIndex = newValue
	}

	/**
	* Cf. {@link Buffer#matchWith(int, Buffer, int, int)} with size = 1
 	*/
	def matchWith(int localIdx, Buffer buffer, int remoteIdx) {
		matchWith(localIdx, buffer, remoteIdx, 1)
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
	def matchWith(int localIdx, Buffer buffer, int remoteIdx, int size) {

		if (this.tokenSize != buffer.tokenSize) {
			throw new RuntimeException(
				'''Cannot match «this.dagVertex.name».«this.name» with «buffer.dagVertex.name».«buffer.name» because buffers have different token sized («this.
					tokenSize» != «buffer.tokenSize» ")''')
		}

		// Test if a matched range is completely out of real tokens
		if ((localIdx >= this.nbTokens) || (localIdx + size - 1 < 0)) {
			throw new RuntimeException(
				'''Cannot match «this.dagVertex.name».«this.name»[«localIdx»..«(localIdx + size - 1)»] and «buffer.
					dagVertex.name».«buffer.name»[«remoteIdx»..«(remoteIdx + size - 1)»] because no "real" token from «this.
					dagVertex.name».«this.name»[0..«(this.nbTokens - 1)»] is matched.''')
		}
		if ((remoteIdx >= buffer.nbTokens) || (remoteIdx + size - 1 < 0)) {
			throw new RuntimeException(
				'''Cannot match «this.dagVertex.name».«this.name»[«localIdx»..«localIdx + size - 1»] and «buffer.
					dagVertex.name».«buffer.name»[«remoteIdx»..«remoteIdx + size - 1»] because no "real" token from "«buffer.
					dagVertex.name».«buffer.name»[0..«buffer.nbTokens - 1»] is matched.''')
		}

		// Are "virtual" tokens matched together
		if (// Both ranges begins before the first token
		((localIdx < 0) && (remoteIdx < 0)) ||
			// or both buffers ends after the last token
			((localIdx + size - 1 >= this.nbTokens) && (remoteIdx + size - 1 >= buffer.nbTokens)) ||
			// or local range begins with less real tokens than the number of virtual tokens beginning remote range
			(localIdx >= 0 && ((this.nbTokens - localIdx) <= -Math::min(0, remoteIdx))) ||
			// or remote range begins with less real tokens than the number of virtual tokens beginning local range
			(remoteIdx >= 0 && ((buffer.nbTokens - remoteIdx) <= -Math::min(0, localIdx)))) {
			throw new RuntimeException(
				'''Cannot match «this.dagVertex.name».«this.name»[«localIdx»..«localIdx + size - 1»] and «buffer.
					dagVertex.name».«buffer.name»[«remoteIdx»..«(remoteIdx + size - 1)»] because "virtual tokens" cannot be matched together.«"\n"»Information: «this.
					dagVertex.name».«this.name» size = «this.nbTokens» and «buffer.dagVertex.name».«buffer.name» size = «buffer.
					nbTokens».''')
		}

		this.byteMatchWith(localIdx * tokenSize, buffer, remoteIdx * tokenSize, size * tokenSize)

	/*
		// If needed, update the buffers min/max indexes
		if (!(localIdx >= 0) && (localIdx + size - 1 < this.nbTokens)) {
			this._minIndex = Math::min(_minIndex, localIdx * tokenSize)
			this._maxIndex = Math::max(_maxIndex, (localIdx + size) * tokenSize)
		}
		if (!(remoteIdx >= 0) && (remoteIdx + size - 1 < buffer.nbTokens)) {
			buffer._minIndex = Math::min(buffer._minIndex, remoteIdx * tokenSize)
			buffer._maxIndex = Math::max(buffer._maxIndex, (remoteIdx + size) * tokenSize)
		}

		// Do the match
		var matchSet = matchTable.get(localIdx * tokenSize)
		if (matchSet == null) {
			matchSet = newArrayList
			matchTable.put(localIdx * tokenSize, matchSet)
		}
		val localMatch = new Match(this, localIdx * tokenSize, buffer, remoteIdx * tokenSize, size * this.tokenSize)
		matchSet.add(localMatch)

		var remoteMatchSet = buffer.matchTable.get(remoteIdx * tokenSize)
		if (remoteMatchSet == null) {
			remoteMatchSet = newArrayList
			buffer.matchTable.put(remoteIdx * tokenSize, remoteMatchSet)
		}
		val remoteMatch = new Match(buffer, remoteIdx * tokenSize, this, localIdx * tokenSize, size * this.tokenSize)
		remoteMatchSet.add(remoteMatch)

		localMatch.reciprocate = remoteMatch
		return localMatch*/
	}

	def byteMatchWith(int localByteIdx, Buffer buffer, int remoteByteIdx, int byteSize) {
		byteMatchWith(localByteIdx, buffer, remoteByteIdx, byteSize, true)
	}

	def byteMatchWith(int localByteIdx, Buffer buffer, int remoteByteIdx, int byteSize, boolean check) {

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
			if ((localByteIdx >= this.nbTokens * this.tokenSize) || (localByteIdx + byteSize - 1 < 0)) {
				throw new RuntimeException(
					'''Cannot match bytes «this.dagVertex.name».«this.name»[«localByteIdx»..«(localByteIdx + byteSize -
						1)»] and «buffer.dagVertex.name».«buffer.name»[«remoteByteIdx»..«(remoteByteIdx + byteSize - 1)»] because no "real" byte from «this.
						dagVertex.name».«this.name»[0..«(this.nbTokens * this.tokenSize - 1)»] is matched.''')
			}
			if ((remoteByteIdx >= buffer.nbTokens * buffer.tokenSize) || (remoteByteIdx + byteSize - 1 < 0)) {
				throw new RuntimeException(
					'''Cannot match bytes «this.dagVertex.name».«this.name»[«localByteIdx»..«localByteIdx + byteSize - 1»] and «buffer.
						dagVertex.name».«buffer.name»[«remoteByteIdx»..«remoteByteIdx + byteSize - 1»] because no "real" byte from "«buffer.
						dagVertex.name».«buffer.name»[0..«buffer.nbTokens * buffer.tokenSize - 1»] is matched.''')
			}

			// Are "virtual" tokens matched together
			if (// Both ranges begins before the first byte
			((localByteIdx < 0) && (remoteByteIdx < 0)) ||
				// or both buffers ends after the last byte
			((localByteIdx + byteSize - 1 >= this.nbTokens * this.tokenSize) &&
				(remoteByteIdx + byteSize - 1 >= buffer.nbTokens * buffer.tokenSize)) ||
				// or local range begins with less real bytes than the number of virtual bytes beginning remote range
				(localByteIdx >= 0 && ((this.nbTokens * this.tokenSize - localByteIdx) <= -Math::min(0, remoteByteIdx))) ||
				// or remote range begins with less real bytes than the number of virtual bytes beginning local range
				(remoteByteIdx >= 0 &&
					((buffer.nbTokens * buffer.tokenSize - remoteByteIdx) <= -Math::min(0, localByteIdx)))) {
				throw new RuntimeException(
					'''Cannot match bytes «this.dagVertex.name».«this.name»[«localByteIdx»..«localByteIdx + byteSize - 1»] and «buffer.
						dagVertex.name».«buffer.name»[«remoteByteIdx»..«(remoteByteIdx + byteSize - 1)»] because "virtual bytes" cannot be matched together.«"\n"»Information: «this.
						dagVertex.name».«this.name» size = «this.nbTokens * this.tokenSize» and «buffer.dagVertex.name».«buffer.
						name» size = «buffer.nbTokens * buffer.tokenSize».''')
			}
		}

		// If needed, update the buffers min/max indexes
		if (!(localByteIdx >= 0 && localByteIdx + byteSize - 1 < this.nbTokens * this.tokenSize)) {
			this._minIndex = Math::min(_minIndex, localByteIdx)
			this._maxIndex = Math::max(_maxIndex, (localByteIdx + byteSize))
		}
		if (!(remoteByteIdx >= 0 && remoteByteIdx + byteSize - 1 < buffer.nbTokens * buffer.tokenSize)) {
			buffer._minIndex = Math::min(buffer._minIndex, remoteByteIdx)
			buffer._maxIndex = Math::max(buffer._maxIndex, (remoteByteIdx + byteSize))
		}

		// Do the match
		var matchSet = matchTable.get(localByteIdx)
		if (matchSet == null) {
			matchSet = newArrayList
			matchTable.put(localByteIdx, matchSet)
		}
		val localMatch = new Match(this, localByteIdx, buffer, remoteByteIdx, byteSize)
		matchSet.add(localMatch)

		var remoteMatchSet = buffer.matchTable.get(remoteByteIdx)
		if (remoteMatchSet == null) {
			remoteMatchSet = newArrayList
			buffer.matchTable.put(remoteByteIdx, remoteMatchSet)
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
	def boolean isDivisible() {
		isCompletelyMatched && indivisibleRanges.size > 1 && {

			// Test that all ranges are covered by the indivisible ranges
			val List<Range> copy = new ArrayList<Range>(indivisibleRanges.map[it.clone as Range].toList)
			val firstElement = copy.head
			copy.remove(0)
			val coveredRange = copy.union(firstElement)
			coveredRange == new Range(0, nbTokens * tokenSize)
		} && matchTable.values.flatten.forall [
			it.remoteBuffer.indivisible
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
	def boolean isIndivisible() {
		this.indivisibleRanges.size == 1 && this.indivisibleRanges.head.start == this.minIndex &&
			this.indivisibleRanges.head.end == this.maxIndex
	}

	override toString() '''«sdfVertex.name».«name»[«nbTokens * tokenSize»]'''

	/**
	 * We do not check that the match is possible !
	 * We just apply it and assume all checks are performed somewhere else !
	 * The local buffer is merged into the remote buffer
	 * The local buffer does not "exists" afterwards
	 */
	def applyMatches(List<Match> matches) {

		// copy the list to iterate on it
		val matchesCopy = new ArrayList(matches)

		// Check that all match have the current buffer as local
		if (!matchesCopy.forall[it.localBuffer == this]) {
			throw new RuntimeException(
				"Incorrect call to applyMatches method.\n One of the given matches does not belong to the this Buffer.")
		}

		// Check that the matches completely cover the buffer
		val matchedRange = matchesCopy.fold(new ArrayList<Range>) [ res, match |
			res.union(match.localRange)
			res
		]
		val tokenRange = new Range(0, tokenSize * nbTokens)
		if (matchedRange.intersection(tokenRange).head != tokenRange) {
			throw new RuntimeException(
				"Incorrect call to applyMatches method.\n All real token must be covered by the given matches.\n" +
					matches)
		}

		for (match : matchesCopy) {

			// Temp version with unique match for a complete buffer <= not true anymore
			appliedMatches.put(match.localIndivisibleRange, match.remoteBuffer -> match.remoteIndex)

			// Move all third-party matches from the matched range of the merged buffer
			match.localBuffer.matchTable.values.flatten.filter [
				it != match && it.localRange.hasOverlap(match.localIndivisibleRange)
			].toList.immutableCopy.forEach [ movedMatch |
				// Remove old match from original match list
				val localList = match.localBuffer.matchTable.get(movedMatch.localIndex)
				localList.remove(movedMatch)
				if (localList.size == 0) {
					match.localBuffer.matchTable.remove(movedMatch.localIndex)
				}
				// Change the match local buffer and index	
				// Length and remoteBuffer are unchanged
				movedMatch.localBuffer = match.remoteBuffer
				movedMatch.localIndex = movedMatch.localIndex - (match.localIndex - match.remoteIndex)
				// Update the reciprocate
				movedMatch.reciprocate.remoteBuffer = movedMatch.localBuffer
				movedMatch.reciprocate.remoteIndex = movedMatch.localIndex
				// Put the moved match in its new host matchTable
				var matchList = match.remoteBuffer.matchTable.get(movedMatch.localIndex)
				if (matchList === null) {
					matchList = newArrayList
					match.remoteBuffer.matchTable.put(movedMatch.localIndex, matchList)
				}
				matchList.add(movedMatch)
			]

			// Update the conflictCandidates
			updateConflictCandidates(match)

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
			var matchToUpdate = match.remoteBuffer.matchTable.values.flatten.filter[it != match.reciprocate]
			while (matchToUpdate.size != 0) {
				matchToUpdate = updateConflictingMatches(matchToUpdate)
			}

			// Remove the applied match from the buffers match table 
			// (local and reciprocate)
			match.unmatch

			// Match was applied (and reciprocate)
			match.applied = true
			match.reciprocate.applied = true
		}
	}

	def updateMatches(Match match) {

		// 1- For all matches of the remote buffer (old and newly added)
		// 1.1- If the match (local and remote) ranges falls within 
		// indivisible range(s) larger than the match length
		//      Then:
		// 1.1.1- the match must be enlarged to cover this range
		//        Several matches might become redundant (i.e. identical) in the process
		val List<Pair<Match, Range>> modifiedMatches = newArrayList
		match.remoteBuffer.matchTable.values.flatten.filter[it != match.reciprocate].forEach [ testedMatch |
			// Get the aligned smallest indivisible range (local or remote)
			val localIndivisibleRange = testedMatch.localIndivisibleRange
			val remoteIndivisibleRange = testedMatch.reciprocate.localIndivisibleRange
			remoteIndivisibleRange.translate(testedMatch.localIndex - testedMatch.remoteIndex)
			val smallestRange = if (localIndivisibleRange.length <= remoteIndivisibleRange.length) {
					localIndivisibleRange
				} else {
					remoteIndivisibleRange
				}
			// Check if the range was modified
			if (smallestRange != testedMatch.localRange) {

				// Need to enlarge the match
				modifiedMatches.add(new Pair(testedMatch, smallestRange))
			}
		]
		modifiedMatches.forEach [
			val modifiedMatch = it.key
			val newRange = it.value
			// Update the match
			modifiedMatch.length = newRange.length
			modifiedMatch.reciprocate.length = newRange.length
			// If the match must be moved
			val originalIndex = modifiedMatch.localIndex
			val originaRemotelIndex = modifiedMatch.remoteIndex
			if (newRange.start != originalIndex) {

				// Move the local match
				modifiedMatch.localIndex = newRange.start
				modifiedMatch.remoteIndex = originaRemotelIndex + newRange.start - originalIndex
				modifiedMatch.localBuffer.matchTable.get(originalIndex).remove(modifiedMatch)
				val localList = modifiedMatch.localBuffer.matchTable.get(newRange.start) ?: {
					val newList = newArrayList
					modifiedMatch.localBuffer.matchTable.put(newRange.start, newList)
					newList
				}
				localList.add(modifiedMatch)

				// Move the remote match  
				modifiedMatch.reciprocate.localIndex = modifiedMatch.remoteIndex
				modifiedMatch.reciprocate.remoteIndex = modifiedMatch.localIndex
				modifiedMatch.remoteBuffer.matchTable.get(originaRemotelIndex).remove(modifiedMatch.reciprocate)
				val remoteList = modifiedMatch.remoteBuffer.matchTable.get(modifiedMatch.remoteIndex) ?: {
					val newList = newArrayList
					modifiedMatch.remoteBuffer.matchTable.put(modifiedMatch.remoteIndex, newList)
					newList
				}
				remoteList.add(modifiedMatch.reciprocate)
			}
		]
	}

	/**
	 * Must be called before {@link ScriptRunner#updateConflictingMatches() 
	 * updating conflicting matches}.
	 */
	def updateConflictCandidates(Match match) {

		// 1. If the applied match is not interSiblings
		// 1.1. Conflict candidates of the applied local->remote match are 
		// added to all remote->other matches (except inter siblings and 
		// the already conflicting to remote->local (i.e. the backward if
		// local->remote is forward or vice versa))
		// 1.2. Conflict candidates of the applied remote->local match are 
		// added to all local->other matches (except inter siblings and 
		// the already conflicting to local->remote (i.e. the forward if
		// remote->local is backward or vice versa))
		// 1.3. InterSibling matches of the localBuffer and remoteBuffer of
		// the applied Match become conflict candidates of each other.
		// 2. If the applied match is interSiblings
		// 2.1 InterSiblings local->other and remote->other matches become conflict
		// candidate (like 1.3).
		// 2.2 New conflicts are added between Forward match of the 
		// remoteBuffer and forward match of the localBuffer
		// 2.3 New conflicts are added between Backward match of the 
		// remoteBuffer and backward matches of the localBuffer
		// 1.1 and 2.1
		val newConflicts = newArrayList
		if (!match.conflictCandidates.empty || !match.conflictingMatches.empty) {
			match.remoteBuffer.matchTable.values.flatten.filter [
				it.type == match.type
			].forEach [ otherMatch |
				otherMatch.conflictCandidates.addAll(match.conflictCandidates)
				otherMatch.conflictCandidates.addAll(match.conflictingMatches)
				newConflicts.add(otherMatch)
			]
			match.conflictCandidates.forEach[it.conflictCandidates.addAll(newConflicts)]
			match.conflictingMatches.forEach[it.conflictCandidates.addAll(newConflicts)]
			newConflicts.clear
		}

		//1.
		if (match.type != MatchType::INTER_SIBLINGS) {

			// 1.2
			if (!match.reciprocate.conflictCandidates.empty || !match.reciprocate.conflictingMatches.empty) {
				match.localBuffer.matchTable.values.flatten.filter [
					it.type != MatchType::INTER_SIBLINGS && it.type != match.type
				].forEach [ otherMatch |
					otherMatch.conflictCandidates.addAll(match.reciprocate.conflictCandidates)
					otherMatch.conflictCandidates.addAll(match.reciprocate.conflictingMatches)
					newConflicts.add(otherMatch)
				]
				match.reciprocate.conflictCandidates.forEach[it.conflictCandidates.addAll(newConflicts)]
				match.reciprocate.conflictingMatches.forEach[it.conflictCandidates.addAll(newConflicts)]
				newConflicts.clear
			}

			// 1.3
			val localSiblingMatches = match.localBuffer.matchTable.values.flatten.filter [
				it.type == MatchType::INTER_SIBLINGS
			]
			if (! localSiblingMatches.empty) {
				match.remoteBuffer.matchTable.values.flatten.filter [
					it.type == MatchType::INTER_SIBLINGS
				].forEach [ remoteMatch |
					remoteMatch.conflictCandidates.addAll(localSiblingMatches)
					localSiblingMatches.forEach [ localMatch |
						localMatch.conflictCandidates.add(remoteMatch)
					]
				]
			}
		} 			
		// 2. 
		else {

			// 2.2 & 2.3
			val localForward = newArrayList
			val localBackward = newArrayList
			match.localBuffer.matchTable.values.flatten.forEach [
				switch (it.type) {
					case MatchType::FORWARD:
						localForward.add(it)
					case MatchType::BACKWARD:
						localBackward.add(it)
				}
			]
			match.remoteBuffer.matchTable.values.flatten.forEach [ otherMatch |
				switch (otherMatch.type) {
					case MatchType::FORWARD: {
						otherMatch.conflictCandidates.addAll(localForward)
						localForward.forEach[it.conflictCandidates.add(otherMatch)]
					}
					case MatchType::BACKWARD: {
						otherMatch.conflictCandidates.addAll(localBackward)
						localBackward.forEach[it.conflictCandidates.add(otherMatch)]
					}
				}
			]
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
	static def updateConflictingMatches(Iterable<Match> matchList) {

		val updatedMatches = newArrayList
		matchList.forEach [ match |
			// Check all the conflict candidaes
			var iter = match.conflictCandidates.iterator
			while (iter.hasNext) {
				val candidate = iter.next
				if (candidate.localImpactedRange.hasOverlap(match.localImpactedRange)) {
					iter.remove

					// Add the candidate to the conflicting matches
					//match.conflictingMatches.add(candidate)
					match.reciprocate.conflictingMatches.add(candidate.reciprocate)

					// Remove it from the reciprocate candidates (if it was present)
					//match.reciprocate.conflictCandidates.remove(candidate.reciprocate)

					updatedMatches.add(candidate)
				}
			}
			// Do the same for reciprocate
			iter = match.reciprocate.conflictCandidates.iterator
			while (iter.hasNext) {
				val candidate = iter.next
				if (candidate.localImpactedRange.hasOverlap(match.reciprocate.localImpactedRange)) {
					iter.remove

					// Add the candidate to the conflicting matches
					match.conflictingMatches.add(candidate.reciprocate)

					//match.reciprocate.conflictingMatches.add(candidate)
					// Remove it from the candidates (if it was present)
					//match.conflictCandidates.remove(candidate.reciprocate)

					if (!updatedMatches.contains(candidate.reciprocate)) {
						updatedMatches.add(candidate.reciprocate)
					}
				}
			}
		]

		return updatedMatches
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
	static def backupupdateConflictingMatches(Iterable<Match> matchList) {

		val updatedMatches = newArrayList
		matchList.forEach [ match |
			// Check all the conflict candidaes
			var iter = match.conflictCandidates.iterator
			while (iter.hasNext) {
				val candidate = iter.next
				if (candidate.localImpactedRange.hasOverlap(match.localImpactedRange)) {
					iter.remove

					// Add the candidate to the conflicting matches
					match.conflictingMatches.add(candidate)
					match.reciprocate.conflictingMatches.add(candidate.reciprocate)

					// Remove it from the reciprocate candidates (if it was present)
					match.reciprocate.conflictCandidates.remove(candidate.reciprocate)

					updatedMatches.add(candidate)
				}
			}
			// Do the same for reciprocate
			iter = match.reciprocate.conflictCandidates.iterator
			while (iter.hasNext) {
				val candidate = iter.next
				if (candidate.localImpactedRange.hasOverlap(match.reciprocate.localImpactedRange)) {
					iter.remove

					// Add the candidate to the conflicting matches
					match.conflictingMatches.add(candidate.reciprocate)
					match.reciprocate.conflictingMatches.add(candidate)

					// Remove it from the candidates (if it was present)
					match.conflictCandidates.remove(candidate.reciprocate)

					if (!updatedMatches.contains(candidate.reciprocate)) {
						updatedMatches.add(candidate.reciprocate)
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
	def updateRemoteIndexes(Match match) {
		var res = false;

		// Get the local indivisible ranges involved in the match
		val localIndivisibleRange = match.localIndivisibleRange

		// Align them with the remote ranges
		localIndivisibleRange.translate(match.remoteIndex - match.localIndex)

		// Update the remote buffer indexes if needed.
		if (localIndivisibleRange.start < match.remoteBuffer.minIndex) {
			res = true
			match.remoteBuffer.minIndex = localIndivisibleRange.start
		}

		if (localIndivisibleRange.end > match.remoteBuffer.maxIndex) {
			res = true
			match.remoteBuffer.maxIndex = localIndivisibleRange.end
		}

		res
	}

	/** 
	 * Also update the {@link #getDivisibilityRequiredMatches() 
	 * divisibilityRequiredMatches} {@link List} of the {@link Buffer}.
	 * 
	 */
	def updateDivisibleRanges(Match match) {
		val localRange = match.localRange

		// Get the local indivisible ranges involved in the match
		val localIndivisibleRanges = match.localBuffer.indivisibleRanges.filter [
			// An indivisible range can go beyond the matched
			// range. For example, if the range includes virtual tokens
			it.hasOverlap(localRange)
		].map[it.clone as Range].toList // toList to make sure the map function is applied only once

		// Align them with the remote ranges
		localIndivisibleRanges.translate(match.remoteIndex - match.localIndex)

		// Do the lazy union
		// The divisability update must not be applied if the applied match involves
		// the division of the local buffer, instead the remote buffer should become !
		// non divisable !
		match.remoteBuffer.indivisibleRanges.lazyUnion(localIndivisibleRanges)

		// If the destination range is still divisible,(i.e. if the remote 
		// localRange overlaps more than a unique indivisible Range.)
		// Then Forward all DivisibilityRequiredMatches from the local Buffer
		// No risk if the match is applied as a result of a division since
		// in such case, the destination is compulsorily indivisible			
		if (match.remoteBuffer.indivisibleRanges.filter [
			it.hasOverlap(match.reciprocate.localRange)
		].size > 1) {
			match.remoteBuffer.divisibilityRequiredMatches.addAll(match.localBuffer.divisibilityRequiredMatches)
		}
	}

	/**
	 * Must be called after updateRemoteIndexesAndDivisibleRanges
	 */
	def updateRemoteMergeableRange(Match match) {

		// 1 - Get the mergeable ranges that are involved in the match
		// Get the local involved Range
		val involvedRange = match.localIndivisibleRange
		val localMergeableRange = match.localBuffer.mergeableRanges.intersection(involvedRange)

		// Translate it to get the remote involved range
		involvedRange.translate(match.remoteIndex - match.localIndex)
		val remoteMergeableRange = match.remoteBuffer.mergeableRanges.intersection(involvedRange)

		// 2 - Realign the two ranges 
		localMergeableRange.translate(-match.localIndex)
		remoteMergeableRange.translate(-match.remoteIndex)

		// 3 - Get intersection => the mergeable range of the result
		val resultMergeableRange = localMergeableRange.intersection(remoteMergeableRange)

		// 4 - Update the destination mergeable range
		// no need to update the origin mergeable range since 
		// this buffer will no longer be used in the processing
		// 4.1 - compute the Mergeable range that must be removed
		// from the destination buffer
		val unmergeableRange = remoteMergeableRange.difference(resultMergeableRange)

		// 4.2 - Realign unmergeable range with destination buffer
		unmergeableRange.translate(match.remoteIndex)

		// 4.3 - Remove it from the remoteMergeableRange
		match.remoteBuffer.mergeableRanges = match.remoteBuffer.mergeableRanges.difference(unmergeableRange)
	}

	def unmatch(Match match) {
		val it = match

		// Local unmatch
		val localList = localBuffer.matchTable.get(localIndex)
		localList.remove(it)
		if (localList.size == 0) {
			localBuffer.matchTable.remove(localIndex)
		}

		// Remote unmatch
		val remoteList = remoteBuffer.matchTable.get(remoteIndex)
		remoteList.remove(reciprocate)
		if (remoteList.size == 0) {
			remoteBuffer.matchTable.remove(remoteIndex)
		}

		// Remove it from conflictingMatches and conflictCandidates
		match.conflictCandidates.forEach[it.conflictCandidates.remove(match)]
		match.conflictingMatches.forEach[it.conflictingMatches.remove(match)]
		match.reciprocate.conflictCandidates.forEach[it.conflictCandidates.remove(match.reciprocate)]
		match.reciprocate.conflictingMatches.forEach[it.conflictingMatches.remove(match.reciprocate)]

	}

	/**
	 * This method checks if the given {@link Matches} are sufficient to 
	 * complete the {@link #getDivisibilityRequiredMatches()} condition.
	 * 
	 */
	def doesCompleteRequiredMatches(Iterable<Match> matches) {

		// Remove completed lists
		val iter = this.divisibilityRequiredMatches.iterator
		while (iter.hasNext) {

			// In the current version we only check if all lists are completelyMatched
			// for better optimization, we must check if each list contains enough applied matches
			// to cover the complete original range
			val list = iter.next
			if (list.forall[it.applied]) {
				iter.remove
			}
		}
		
		// Check if the proposed matches completes the remaining lists
		this.divisibilityRequiredMatches.forall[ list|
			matches.toList.containsAll(list)
		]
	}

}
