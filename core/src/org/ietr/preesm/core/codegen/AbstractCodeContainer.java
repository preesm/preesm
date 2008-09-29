/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/


/**
 * 
 */
package org.ietr.preesm.core.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.sdf4j.model.AbstractVertex;

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
	private List<ICodeElement> codeElements;
	

	public AbstractCodeContainer(){
		codeElements = new ArrayList<ICodeElement>();
	}

	public void accept(AbstractPrinter printer) {

		printer.visit(this,0); // Visit self
		
		Iterator<ICodeElement> iterator = codeElements.iterator();
		
		while(iterator.hasNext()){
			ICodeElement elt = iterator.next();
			printer.visit(this,1); // Visit self
			elt.accept(printer);
			printer.visit(this,2); // Visit self
		}
		printer.visit(this,3); // Visit self
	}

	public void addCodeElement(ICodeElement element) {
		codeElements.add(element);
	}

	public void addCodeElementAfter(ICodeElement oldElement, ICodeElement newElement) {
		
		int index = codeElements.indexOf(oldElement);
		
		if(index != -1)
			codeElements.add(index + 1, newElement);
		else
			codeElements.add(newElement);
	}

	public void addCodeElementBefore(ICodeElement oldElement, ICodeElement newElement) {
		
		int index = codeElements.indexOf(oldElement);
		
		if(index != -1)
			codeElements.add(index, newElement);
		else
			codeElements.add(newElement);
	}

	public ICodeElement getCodeElement(AbstractVertex<?> vertex) {
		
		Iterator<ICodeElement> iterator = codeElements.iterator();
		
		while(iterator.hasNext()){
			ICodeElement elt = iterator.next();

			AbstractVertex<?> currentVertex = elt.getCorrespondingVertex();
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
		
		Iterator<ICodeElement> iterator = codeElements.iterator();
		
		while(iterator.hasNext()){
			ICodeElement elt = iterator.next();

			code += elt.toString() + "\n";
		}
		
		return code;
	}
}
