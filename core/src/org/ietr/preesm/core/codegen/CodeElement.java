/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;



/**
 * Generated code within threads consists primarily in a succession
 * of code elements. 
 * 
 * @author mpelcat
 */
public abstract class CodeElement {

	/**
	 * The vertex generating this function call
	 */
	private DAGVertex correspondingVertex;
	
	private String name;

	/**
	 * Container of the function call
	 */
	private AbstractBufferContainer parentContainer;


	public CodeElement(String name,AbstractBufferContainer parentContainer, DAGVertex correspondingVertex) {
		this.name = name;
		this.parentContainer = parentContainer;
		this.correspondingVertex = correspondingVertex;
	}

	public void accept(AbstractPrinter printer) {
		printer.visit(this,0); // Visit self
	}

	public DAGVertex getCorrespondingVertex() {
		return correspondingVertex;
	}

	public String getName() {
		return name;
	}

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}
	
	public void setCorrespondingVertex(DAGVertex correspondingVertex) {
		this.correspondingVertex = correspondingVertex;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		
		return getName();
	}
}
