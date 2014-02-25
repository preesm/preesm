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
		this.conflictCandidates = newArrayList
		this.applied = false
		this.siblingMatch = false
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
	@Property
	List<Match> conflictCandidates
	@Property
	MatchType type 
	
	
	/** 
	 * This Property is set to <code>true</code> if the current {@link Match}
	 * links a input {@link Buffer} to another input or an output {@link 
	 * Buffer} to another output. This property is set within the {@link 
	 * ScriptRunner#identifySiblingMatches(Pair)}
	 * 
	 */
	@Property
	boolean siblingMatch
	
	/**
	 * This {@link boolean} is set to <code>true</code> if the current {@link 
	 * Match} was applied.
	 */
	@Property
	boolean applied
	

	Match _reciprocate

	def setReciprocate(Match remoteMatch) {
		_reciprocate = remoteMatch
		remoteMatch._reciprocate = this
	}

	def getReciprocate() {
		_reciprocate
	}
	
	/**
	 * Returns a {@link Range} going from {@link Match#getLocalIndex() 
	 * localIndex} to the end of the matched tokens.
	 * 
	 */
	def getLocalRange(){
		new Range(localIndex, localIndex + length)	
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

	override toString() '''«localBuffer.dagVertex.name».«localBuffer.name»[«localIndex»..«localIndex + length»[=>«remoteBuffer.dagVertex.name».«remoteBuffer.name»[«remoteIndex»..«remoteIndex + length»['''
}

/**
 * This enumeration represent the type of the current {@link Match}
 */
public enum MatchType{
	/**
	 * The {@link Match} links several inputs (or outputs) together.
	 */
	INTER_SIBLINGS,
	/**
	 * The {@link Match} is internal to an actor and links an input {@link 
	 * Buffer} to an output {@link Buffer}, <b>or</b> the {@link Match} is
	 * external to an actor (i.e. correspond to an edge) and it links an output
	 * {@link Buffer} of an actor to the input {@link Buffer} of the next.
	 */
	FORWARD,
	/**
	 * Opposite of {@link MatchType.FORWARD}
	 */
	BACKWARD
}
