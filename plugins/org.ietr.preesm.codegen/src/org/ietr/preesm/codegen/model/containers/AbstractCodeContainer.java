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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.dftools.algorithm.model.AbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;

/**
 * An abstract code container within a thread contains code elements (function
 * calls, samaphore pend and post, for loops...).
 * 
 * @author Maxime Pelcat
 * @author Matthieu Wipliez
 * @author jpiat
 */
public abstract class AbstractCodeContainer extends AbstractBufferContainer {

	/**
	 * List of the elements that are not included in the main loop
	 */
	private List<ICodeElement> codeElements;

	/**
	 * Position corresponding to the end of an init phase inside the container
	 */
	@Deprecated
	private int initPos = 0;

	/**
	 * Number of communications present in the container
	 */
	private int comNumber;

	/**
	 * Comment on the code phase that should appear in generated code
	 */
	private String comment;

	/**
	 * ID that can help identifying code phase
	 */
	private String id;

	public AbstractCodeContainer(AbstractBufferContainer parentContainer,
			String id, String comment) {
		super(parentContainer);
		codeElements = new ArrayList<ICodeElement>();
		comNumber = 0;
		this.comment = comment;
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public String getId() {
		return id;
	}

	/**
	 * Get number of communications present in the thread
	 */
	public int getComNumber() {
		return comNumber;
	}

	/**
	 * Increment number of communications present in the thread
	 */
	public int incrementComNumber() {
		return comNumber = comNumber + 1;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self

		Iterator<ICodeElement> iterator = codeElements.iterator();

		while (iterator.hasNext()) {
			ICodeElement elt = iterator.next();
			elt.accept(printer, currentLocation);
		}
	}

	/**
	 * Adds the given code element at the end of the code element list.
	 * 
	 * @param element
	 *            An object implementing {@link ICodeElement}.
	 */
	public void addCodeElement(ICodeElement element) {
		codeElements.add(element);
	}

	public void addInitCodeElement(ICodeElement element) {
		codeElements.add(initPos, element);
		initPos++;
	}

	/**
	 * Adds the given code element at the beginning of the code element list.
	 * 
	 * @param element
	 *            An object implementing {@link ICodeElement}.
	 */
	public void addCodeElementFirst(ICodeElement element) {
		codeElements.add(0, element);
	}

	/**
	 * Adds the given given code element <code>newElement</code> after the
	 * reference code element <code>oldElement</code>.
	 * 
	 * @param oldElement
	 *            The reference code element.
	 * @param newElement
	 *            The code element to add.
	 */
	public void addCodeElementAfter(ICodeElement oldElement,
			ICodeElement newElement) {
		int index = codeElements.indexOf(oldElement);
		if (index != -1) {
			codeElements.add(index + 1, newElement);
		} else {
			codeElements.add(newElement);
		}
	}

	/**
	 * Adds the given given code element <code>newElement</code> before the
	 * reference code element <code>oldElement</code>.
	 * 
	 * @param oldElement
	 *            The reference code element.
	 * @param newElement
	 *            The code element to add.
	 */
	public void addCodeElementBefore(ICodeElement oldElement,
			ICodeElement newElement) {
		int index = codeElements.indexOf(oldElement);
		if (index != -1) {
			codeElements.add(index, newElement);
		} else {
			codeElements.add(newElement);
		}
	}

	/**
	 * Returns the code element that corresponds to the given vertex.
	 * 
	 * @param vertex
	 *            An {@link AbstractVertex}.
	 * @return The matching code element or <code>null</code>.
	 */
	public List<ICodeElement> getCodeElements(SDFAbstractVertex vertex) {
		List<ICodeElement> vList = new ArrayList<ICodeElement>();

		for (ICodeElement elt : codeElements) {
			AbstractVertex<?> currentVertex = elt.getCorrespondingVertex();
			if (elt.getCorrespondingVertex() != null
					&& currentVertex.equals(vertex)) {
				vList.add(elt);
			}
		}

		return vList;
	}

	public int getCodeElementPosition(ICodeElement elt) {
		return codeElements.indexOf(elt);
	}

	public List<ICodeElement> getCodeElements() {
		return codeElements;
	}

	public ICodeElement getCodeElement(int index) {
		if (index >= codeElements.size() || index < 0) {
			return null;
		} else {
			return codeElements.get(index);
		}
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		String code = "\n";
		for (ICodeElement elt : codeElements) {
			code += elt.toString() + "\n";
		}
		return code;
	}
}
