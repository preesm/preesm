/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.codegen.model.threads;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;

/**
 * Declaration of a thread for code generation.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class ThreadDeclaration extends AbstractBufferContainer {

	/**
	 * Each code container is a linear or a loop code section
	 */
	protected List<AbstractCodeContainer> codeContainers;

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

		codeContainers = new ArrayList<AbstractCodeContainer>();
	}
	
	public void addContainer(AbstractCodeContainer container){
		codeContainers.add(container);
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self
		super.accept(printer, currentLocation); // Accept the buffer allocation
		for(AbstractBufferContainer container : codeContainers){
			container.accept(printer, currentLocation);
		}
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

		for(AbstractBufferContainer container : codeContainers){
			code += "\n\n";
			code += container.toString();
		}
		
		code += "}//end thread: " + getName() + "\n";

		return code;
	}

}
