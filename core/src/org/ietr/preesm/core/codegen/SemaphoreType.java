/**
 * 
 */
package org.ietr.preesm.core.codegen;

/**
 * A semaphore can be the signal of a full buffer or the 
 * signal of an empty buffer.
 * 
 * @author mpelcat
 */
public enum SemaphoreType {

	/**
	 * the signal of an empty buffer
	 */
	empty,

	/**
	 * the signal of a full buffer
	 */
	full;

	@Override
	public String toString() {

		if(this == full)
			return "full";
		else if(this == empty)
			return "empty";
		
		return "";
	}
	
	
}
