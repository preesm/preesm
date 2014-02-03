package org.ietr.preesm.experiment.memory

import java.util.ArrayList
import java.util.Map
import java.util.Set

class Buffer {

	@Property
	final String name

	@Property
	final int nbTokens

	@Property
	final int tokenSize

	/**
	 * This table is protected to ensure that matches are set only by using
	 * {@link #matchWith(int,Buffer,int)} methods in the scripts.
	 */
	@Property
	final protected Map<Integer, Set<Match>> matchTable

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
	 *                may be thrown if the matched ranges exceeds the
	 *                {@link Buffer#getNbTokens()} or if the two {@link Buffer
	 *                buffers} have different {@link Buffer#getTokenSize()}.
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
		if (this.tokenSize != buffer.tokenSize) {
			throw new RuntimeException(
				"Cannot match " + this.name + " with " + buffer.name + " because buffers have different token sized (" +
					this.tokenSize + "!=" + buffer.tokenSize + ")")
		}

		if (localIdx + size - 1 >= nbTokens * tokenSize) {
			throw new RuntimeException(
				"Cannot match " + this.name + " with " + buffer.name + " because matched range [" + localIdx + ".." +
					(localIdx + size - 1) + "] exceeds buffer size:" + nbTokens * tokenSize)
		}

		if (remoteIdx + size -1 >= buffer.nbTokens * buffer.tokenSize) {
			throw new RuntimeException(
				"Cannot match " + buffer.name + " with " + this.name + " because matched range [" + remoteIdx + ".." +
					(remoteIdx + size - 1)  + "] exceeds buffer size:" + buffer.nbTokens * tokenSize)
		}

		var matchSet = matchTable.get(localIdx)
		if (matchSet == null) {
			matchSet = newHashSet
			matchTable.put(localIdx, matchSet)
		}
		matchSet.add(new Match(buffer, remoteIdx, size * this.tokenSize))

		var remoteMatchSet = buffer.matchTable.get(remoteIdx)
		if (remoteMatchSet == null) {
			remoteMatchSet = newHashSet
			buffer.matchTable.put(remoteIdx, remoteMatchSet)
		}
		remoteMatchSet.add(new Match(this, localIdx, size * this.tokenSize))
	}
}

@Data
class Match {
	Buffer buffer
	int index
	int length
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
