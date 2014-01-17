package org.ietr.preesm.experiment.memory

import java.util.List
import java.util.Set
import java.util.ArrayList

class Buffer {

	@Property
	final String name

	@Property
	final int nbTokens

	@Property
	final int tokenSize
	
	@Property
	final FixedSizeList<Set<Match>> matchTable 

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
		_matchTable = new FixedSizeList(nbTokens*tokenSize)
	}
	
	def matchWith(int localIdx, Buffer buffer, int remoteIdx){
		matchTable.set(localIdx, (matchTable.get(localIdx)?:newHashSet))
		matchTable.get(localIdx).add(new Match(buffer,remoteIdx))
			
		buffer.matchTable.set(remoteIdx, (buffer.matchTable.get(remoteIdx)?:newHashSet))
		buffer.matchTable.get(remoteIdx).add(new Match(this,localIdx))
			
		//buffer.matchTable.set(remoteIdx,new Match(this,localIdx)) 
	}
	
	def matchWith(int localIdx, Buffer buffer, int remoteIdx, int size){
		for(i : 0 .. (size-1)){
			matchWith(localIdx+i,buffer,remoteIdx+i)
		}
	}
}

@Data
class Match{
	Buffer buffer
	int index
}


// cf http://stackoverflow.com/questions/5207162/fixed-size-list-in-java
class FixedSizeList<T> extends ArrayList<T> {

    new(int capacity) {
        super(capacity);
        for (i : 0..capacity-1) {
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

