/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;

/**
 * Code container executed once (no loop)
 * 
 * @author mpelcat
 */
public class LinearCodeContainer extends AbstractCodeContainer {


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

		code += "\n{\n";
		
		code += super.toString();
		
		code += "\n}\n";
		
		return code;
	}
}
