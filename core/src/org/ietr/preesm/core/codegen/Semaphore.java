/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;

/**
 * Class representing a semaphore in the code.
 * A semaphore protects a buffer from being read while empty
 * or written while full.
 * 
 * @author mpelcat
 */
public class Semaphore {

	/**
	 * Semaphore container.
	 */
	private SemaphoreContainer container;

	/**
	 * The buffers protected by the current semaphore.
	 */
	private BufferAggregate protectedAggregate;

	/**
	 * A semaphore can be the signal of a full buffer aggregate or the 
	 * signal of an empty buffer aggregate.
	 */
	private SemaphoreType semaphoreType;
	
	public Semaphore(SemaphoreContainer container, BufferAggregate protectedBuffers, SemaphoreType semaphoreType) {
		
		this.semaphoreType = semaphoreType;
		
		this.protectedAggregate = protectedBuffers;
		
		this.container = container;
	}

	public void accept(AbstractPrinter printer) {
		printer.visit(this,0); // Visit self
	}
	
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof Semaphore)
			if(((Semaphore)obj).protectedAggregate == protectedAggregate)
				if(((Semaphore)obj).semaphoreType == semaphoreType)
					return true;
		return false;
	}

	public BufferAggregate getProtectedBuffers() {
		return protectedAggregate;
	}

	/**
	 * A semaphore is determined by its number.
	 */
	public int getSemaphoreNumber() {
		return container.indexOf(this);
	}

	public SemaphoreType getSemaphoreType() {
		return semaphoreType;
	}
	
	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		
		String code = "";
		
		code += "sem[" + getSemaphoreNumber() + "], " + semaphoreType.toString();
		
		return code;
	}
}
