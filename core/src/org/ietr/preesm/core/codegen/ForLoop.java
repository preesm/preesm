/**
 * 
 */
package org.ietr.preesm.core.codegen;


/**
 * Each thread runs indefinitely. It contains a for loop.
 * Thanks to SDF transformation, for loops can be generated
 * to reduce the overall length of the code.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class ForLoop extends AbstractCodeContainer {
	
	/**
	 * 
	 */
	public ForLoop() {
		super();
	}

	public void accept(AbstractPrinter printer) {

		printer.visit(this,0); // Visit self
		super.accept(printer); // Accept the code container
		printer.visit(this,1); // Visit self
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		String code = "";

		code += "\n\nfor(;;){\n";
		code += super.toString();
		code += "\n\n}\n";
		
		return code;
	}
}
