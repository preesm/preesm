package org.ietr.preesm.experiment.memory

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