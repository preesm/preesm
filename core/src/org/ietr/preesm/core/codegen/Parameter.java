/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

/**
 * A parameter is a data value provided by the function caller
 * 
 * @author mpelcat
 */
public abstract class Parameter {

	/**
	 * Parameter name.
	 */
	private String name;

	/**
	 * Type of the parameter. Can be the base type of a buffer with a given size
	 */
	private DataType type;

	public Parameter(String name, DataType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public DataType getType() {
		return type;
	}

	public abstract void accept(IAbstractPrinter printer, Object currentLocation);
}
