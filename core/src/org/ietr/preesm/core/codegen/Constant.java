/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

/**
 * A constant is a parameter given to a function call.
 * It is retrieved from the graph parameters
 * 
 * @author mpelcat
 */
public class Constant extends Parameter {

	String stringValue = null;
	int intValue = 0;
	
	public Constant(String name, String value) {
		super(name, new DataType("string"));
		this.stringValue = value;
	}
	
	public Constant(String name, int value) {
		super(name, new DataType("int"));
		this.intValue = value;
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self
	}
	
	public String getValue(){
		if(stringValue == null){
			return String.valueOf(intValue);
		}
		else{
			return stringValue;
		}
	}

}
