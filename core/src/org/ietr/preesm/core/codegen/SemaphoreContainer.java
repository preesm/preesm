/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.Iterator;

import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;

/**
 * Container that handles the semaphores
 * 
 * @author mpelcat
 */
public class SemaphoreContainer extends ArrayList<Semaphore> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Buffer container containing the current semaphore manager
	 */
	AbstractBufferContainer parentContainer;

	public SemaphoreContainer(AbstractBufferContainer parentContainer) {
		super();
		this.parentContainer = parentContainer;
	}

	public void allocateSemaphores(){
		
		Buffer buf = new Buffer("sem", this.size(),new DataType("semaphore"),null);
		
		parentContainer.addBuffer(new BufferAllocation(buf));
	}

	public Semaphore createSemaphore(BufferAggregate agg, SemaphoreType type) {

		Semaphore sem = getSemaphore(agg, type);

		if (sem == null) {
			sem = new Semaphore(this, agg, type);
			add(sem);
			return sem;
		} else {
			return sem;
		}
	}
	
	
	public Semaphore getSemaphore(BufferAggregate agg, SemaphoreType type) {

		Semaphore sem = null;

		Iterator<Semaphore> currentIt = iterator();

		while (currentIt.hasNext()) {
			sem = currentIt.next();

			// Two semaphores protecting the same buffer in the same direction
			// are the same semaphore
			if (sem.getProtectedBuffers() == agg
					&& sem.getSemaphoreType() == type) {
				return sem;
			}
		}
		
		return null;
	}
}
