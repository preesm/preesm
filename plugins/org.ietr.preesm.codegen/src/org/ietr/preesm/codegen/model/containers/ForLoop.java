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

package org.ietr.preesm.codegen.model.containers;

import java.util.Iterator;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.BufferAllocation;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.codegen.model.main.VariableAllocation;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;

/**
 * Each thread runs indefinitely. It contains a for loop. Thanks to SDF
 * transformation, for loops can be generated to reduce the overall length of
 * the code.
 * 
 * @author mwipliez
 * @author mpelcat
 * @author jpiat
 */
public class ForLoop extends AbstractCodeContainer implements ICodeElement {

	/**
	 * 
	 */
	public ForLoop(AbstractBufferContainer parentContainer, CodeSectionType sectionType, String comment) {
		super(parentContainer, sectionType.toString(), comment);
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self
		Iterator<VariableAllocation> iterator2 = variables.iterator();
		while (iterator2.hasNext()) {
			VariableAllocation alloc = iterator2.next();
			alloc.accept(printer, currentLocation); // Accepts allocations
		}
		for (BufferAllocation buff : this.getBufferAllocations()) {
			if (buff != null) {
				buff.accept(printer, currentLocation);
			}
		}
		super.accept(printer, currentLocation); // Accept the code container
	}

	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public String getName() {
		return "while";
	}
}
