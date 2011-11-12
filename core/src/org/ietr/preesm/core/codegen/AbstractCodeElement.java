/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

/**
 * Generated code within threads consists primarily in a succession of code
 * elements.
 * 
 * @author mpelcat
 */
public abstract class AbstractCodeElement implements ICodeElement {

	/**
	 * The vertex generating this function call
	 */
	private SDFAbstractVertex correspondingVertex;

	private String name;

	/**
	 * Container of the function call
	 */
	private AbstractBufferContainer parentContainer;

	public AbstractCodeElement(String name,
			AbstractBufferContainer parentContainer,
			SDFAbstractVertex correspondingVertex) {
		this.name = name;
		this.parentContainer = parentContainer;
		this.correspondingVertex = correspondingVertex;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self
	}

	public SDFAbstractVertex getCorrespondingVertex() {
		return correspondingVertex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractBufferContainer getParentContainer() {
		return parentContainer;
	}

	public void setCorrespondingVertex(SDFAbstractVertex correspondingVertex) {
		this.correspondingVertex = correspondingVertex;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {

		return getName();
	}
}
