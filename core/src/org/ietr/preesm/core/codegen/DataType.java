package org.ietr.preesm.core.codegen;

/**
 * Representing a data type in code generation (exple: char, int...).
 * 
 * @author mpelcat
 */
public class DataType {

	private String typeName;

	public DataType(String typeName) {
		super();
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
	
}
