/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * An abstract code container within a thread contains code elements 
 * (function calls, samaphore pend and post, for loops...).
 * 
 * @author mpelcat
 */
public abstract class AbstractCodeContainer {

	/**
	 * List of the elements that are not included in the main loop
	 */
	private List<CodeElement> codeElements;
	

	public AbstractCodeContainer(){
		codeElements = new ArrayList<CodeElement>();
	}

	public void accept(AbstractPrinter printer) {

		printer.visit(this,0); // Visit self
		
		Iterator<CodeElement> iterator = codeElements.iterator();
		
		while(iterator.hasNext()){
			CodeElement elt = iterator.next();
			printer.visit(this,1); // Visit self
			elt.accept(printer);
			printer.visit(this,2); // Visit self
		}
		printer.visit(this,3); // Visit self
	}

	public void addCodeElement(CodeElement element) {
		codeElements.add(element);
	}

	public void addCodeElementAfter(CodeElement oldElement, CodeElement newElement) {
		
		int index = codeElements.indexOf(oldElement);
		
		if(index != -1)
			codeElements.add(index + 1, newElement);
		else
			codeElements.add(newElement);
	}

	public void addCodeElementBefore(CodeElement oldElement, CodeElement newElement) {
		
		int index = codeElements.indexOf(oldElement);
		
		if(index != -1)
			codeElements.add(index, newElement);
		else
			codeElements.add(newElement);
	}

	public CodeElement getCodeElement(DAGVertex vertex) {
		
		Iterator<CodeElement> iterator = codeElements.iterator();
		
		while(iterator.hasNext()){
			CodeElement elt = iterator.next();

			DAGVertex currentVertex = elt.getCorrespondingVertex();
			if(elt.getCorrespondingVertex() != null && currentVertex.equals(vertex)){
				return elt;
			}
		}
		
		return null;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		
		String code = "";

		code += "\n";
		
		Iterator<CodeElement> iterator = codeElements.iterator();
		
		while(iterator.hasNext()){
			CodeElement elt = iterator.next();

			code += elt.toString() + "\n";
		}
		
		return code;
	}
}
