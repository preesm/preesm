package org.ietr.preesm.experiment.memory

import java.util.ArrayList
import java.util.Map
import java.util.List

class Buffer {

	@Property
	final String name

	@Property
	final int nbTokens

	@Property
	final int tokenSize

	/**
	 * Minimum index for the buffer content.
	 * Constructor initialize this value to 0 but it is possible to lower this
	 * value by matching another buffer on the "edge" of this one.<br>
	 * For example: <code>this.matchWith(-3, a, 0, 6)</code> results in 
	 * matching this[-3..2] with a[0..5], thus lowering this.minIndex to -3.
	 */
	int _minIndex

	/**
	 * cf {@link #minIndex}.
	 */
	int _maxIndex

	/**
	 * This table is protected to ensure that matches are set only by using
	 * {@link #matchWith(int,Buffer,int)} methods in the scripts.
	 */
	@Property
	final protected Map<Integer, List<Match>> matchTable

	/**
    * Constructor for the {@link Buffer}.
    * @param name
    * 	A {@link String} corresponding to the final name of the buffer. 
    * @param nbTokens
    * 	The number of tokens stored in this buffer.
    * @param tokenSize
    * 	The size of one token of the buffer.
    */
	new(String name, int nbTokens, int tokenSize) {
		_name = name
		_nbTokens = nbTokens
		_tokenSize = tokenSize
		_matchTable = newHashMap()
		_minIndex = 0
		_maxIndex = nbTokens * tokenSize
	}

	def getMinIndex() {
		_minIndex
	}

	def getMaxIndex() {
		_maxIndex
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
	 * @return not used.
	 */
	def matchWith(int localIdx, Buffer buffer, int remoteIdx, int size) {
		
		// TODO Move all checks to the ScriptRunner#check method!
		if (this.tokenSize != buffer.tokenSize) {
			throw new RuntimeException(
				"Cannot match " + this.name + " with " + buffer.name + " because buffers have different token sized (" +
					this.tokenSize + "!=" + buffer.tokenSize + ")")
		}

		// Test if a matched range is completely out of real tokens
		if ((localIdx >= this.nbTokens) || (localIdx + size - 1 < 0)) {
			throw new RuntimeException(
				"Cannot match " + this.name + "[" + localIdx + ".." + (localIdx + size - 1) + "] and " + buffer.name +
					"[" + remoteIdx + ".." + (remoteIdx + size - 1) + "] because no \"real\" token from " + this.name +
					"[0.." + (this.nbTokens - 1) + "] is matched.")
		}
		if ((remoteIdx >= buffer.nbTokens) || (remoteIdx + size - 1 < 0)) {
			throw new RuntimeException(
				"Cannot match " + this.name + "[" + localIdx + ".." + (localIdx + size - 1) + "] and " + buffer.name +
					"[" + remoteIdx + ".." + (remoteIdx + size - 1) + "] because no \"real\" token from " + buffer.name +
					"[0.." + (buffer.nbTokens - 1) + "] is matched.")
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
				"Cannot match " + this.name + "[" + localIdx + ".." + (localIdx + size - 1) + "] and " + buffer.name +
					"[" + remoteIdx + ".." + (remoteIdx + size - 1) +
					"] because \"virtual tokens\" cannot be matched together.\n" + "Information: " + this.name +
					" size = " + this.nbTokens + " and " + buffer.name + " size = " + buffer.nbTokens + ".")
		}

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
		val localMatch = new Match(buffer, remoteIdx * tokenSize, size * this.tokenSize)
		matchSet.add(localMatch)

		var remoteMatchSet = buffer.matchTable.get(remoteIdx * tokenSize)
		if (remoteMatchSet == null) {
			remoteMatchSet = newArrayList
			buffer.matchTable.put(remoteIdx * tokenSize, remoteMatchSet)
		}
		val remoteMatch = new Match(this, localIdx * tokenSize, size * this.tokenSize)
		remoteMatchSet.add(remoteMatch)

		localMatch.reciprocate = remoteMatch
	}
}

class Match {
	new(Buffer remoteBuffer, int remoteIndex, int size) {
		buffer = remoteBuffer
		index = remoteIndex
		length = size
	}

	@Property
	Buffer buffer
	@Property
	int index
	@Property
	int length

	Match _reciprocate

	def setReciprocate(Match remoteMatch) {
		_reciprocate = remoteMatch
		remoteMatch._reciprocate = this
	}

	def getReciprocate() {
		_reciprocate
	}

	/**
	 * Overriden to forbid
	 */
	override hashCode() {

		// Forbidden because if a non final attribute is changed, then the hashcode changes.
		// But if the Match is already stored in a map, its original hashcode will have been used
		throw new UnsupportedOperationException("HashCode is not supported for Match class. Do not use HashMap.")

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
		this.buffer == other.buffer && this.index == other.index && this.length == other.length
	}

	override toString() '''«buffer.name»[«index»..«index + length - 1»]'''

}

// cf http://stackoverflow.com/questions/5207162/fixed-size-list-in-java
class FixedSizeList<T> extends ArrayList<T> {

	new(int capacity) {
		super(capacity);
		for (i : 0 .. capacity - 1) {
			super.add(null);
		}
	}

	new(T[] initialElements) {
		super(initialElements.length);
		for (loopElement : initialElements) {
			super.add(loopElement);
		}
	}

	override clear() {
		throw new UnsupportedOperationException("Elements may not be cleared from a fixed size List.");
	}

	override add(T o) {
		throw new UnsupportedOperationException("Elements may not be added to a fixed size List, use set() instead.");
	}

	override add(int index, T element) {
		throw new UnsupportedOperationException("Elements may not be added to a fixed size List, use set() instead.");
	}

	override remove(int index) {
		throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
	}

	override remove(Object o) {
		throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
	}

	override removeRange(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Elements may not be removed from a fixed size List.");
	}
}
