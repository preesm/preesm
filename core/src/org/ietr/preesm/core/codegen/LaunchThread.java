/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

/**
 * Calling a function launching a thread
 * 
 * @author mpelcat
 */
public class LaunchThread extends AbstractCodeElement {

	private String threadName;
	private int stackSize;
	private int priority;
	
	public LaunchThread(AbstractBufferContainer parentContainer, String threadName, int stackSize, int priority) {
		super("launchThread", parentContainer, null);
		
		this.threadName = threadName;
		this.stackSize = stackSize;
		this.priority = priority;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
	}

	public String getThreadName() {
		return threadName;
	}

	public int getStackSize() {
		return stackSize;
	}

	public int getPriority() {
		return priority;
	}
}
