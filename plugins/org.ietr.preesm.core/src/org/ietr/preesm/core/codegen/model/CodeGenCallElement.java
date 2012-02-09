/**
 * 
 */
package org.ietr.preesm.core.codegen.model;

/**
 * Element of a function call that can be a parameter, an argument...
 * 
 * @author mpelcat
 */
public class CodeGenCallElement {

	private String name;

	public CodeGenCallElement(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
