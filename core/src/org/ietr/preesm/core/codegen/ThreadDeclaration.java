/**
 * 
 */
package org.ietr.preesm.core.codegen;


/**
 * Declaration of a thread for code generation. Threads can be communication or
 * computation threads.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class ThreadDeclaration extends AbstractBufferContainer {

	/**
	 * A thread is composed of:
	 * <ul>
	 * <li>a buffer allocation</li>
	 * <li>a linear beginning code</li>
	 * <li>a loop code</li>
	 * <li>a linear end code</li>
	 * </ul>
	 * -
	 */
	protected LinearCodeContainer beginningCode;

	protected LinearCodeContainer endCode;
	protected ForLoop loopCode;
	/**
	 * Thread name
	 */
	private String name;

	/**
	 * Creates a new thread declaration with the given name.
	 * 
	 * @param name
	 *            The thread name.
	 */
	public ThreadDeclaration(String name,
			AbstractBufferContainer parentContainer) {

		super(parentContainer);

		this.name = name;
		
		beginningCode = new LinearCodeContainer();
		loopCode = new ForLoop();
		endCode = new LinearCodeContainer();
	}

	public void accept(AbstractPrinter printer) {

		printer.visit(this,0); // Visit self
		super.accept(printer); // Accept the buffer allocation
		printer.visit(this,1); // Visit self
		
		beginningCode.accept(printer);
		printer.visit(this,2); // Visit self
		loopCode.accept(printer);
		printer.visit(this,3); // Visit self
		endCode.accept(printer);
		printer.visit(this,4); // Visit self
	}
	
	public boolean equals(Object obj) {

		if (obj instanceof ThreadDeclaration) {
			ThreadDeclaration threadDecl = (ThreadDeclaration) obj;
			return threadDecl.name.equals(this.name);
		} else {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		
		String code = "";
		
		code += "\n//Thread: " + getName() + "\n";
		code += "void " + getName() + "(){\n";

		// Buffer allocation
		code += super.toString();
		
		code += "\n//beginningCode\n";
		code += beginningCode.toString();
		
		code += "\n//loopCode\n";
		code += loopCode.toString();
		
		code += "\n//endCode\n";
		code += endCode.toString();

		code += "}//end thread: " + getName() + "\n";
		
		return code;
	}

}
