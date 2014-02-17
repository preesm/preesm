package org.ietr.preesm.experiment.memory

import java.util.List

class Match {
	new(Buffer localBuffer, int localIndex, Buffer remoteBuffer, int remoteIndex, int size) {
		this.localBuffer = localBuffer
		this.localIndex = localIndex
		this.remoteBuffer = remoteBuffer
		this.remoteIndex = remoteIndex
		length = size
		this.conflictingMatches = newArrayList
	}

	@Property
	Buffer localBuffer
	@Property
	int localIndex
	@Property
	Buffer remoteBuffer
	@Property
	int remoteIndex
	@Property
	int length
	@Property
	List<Match> conflictingMatches

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
		this.localBuffer == other.localBuffer && this.localIndex == other.localIndex &&
			this.remoteBuffer == other.remoteBuffer && this.remoteIndex == other.remoteIndex &&
			this.length == other.length
	}

	override toString() '''«remoteBuffer.dagVertex.name».«remoteBuffer.name»[«remoteIndex»..«remoteIndex + length»['''
}
