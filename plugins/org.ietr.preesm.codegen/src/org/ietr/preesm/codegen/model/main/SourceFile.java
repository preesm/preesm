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

package org.ietr.preesm.codegen.model.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.codegen.model.threads.ThreadDeclaration;

/**
 * Source file to be executed on a given core. A source file contains Buffer
 * declarations and threads.
 * 
 * @author Maxime Pelcat
 * @author Matthieu Wipliez
 */
public class SourceFile extends AbstractBufferContainer {

	/**
	 * File name
	 */
	private String name;

	/**
	 * Operator on which this source file will run
	 */
	private ComponentInstance operator;

	/**
	 * The threads in this file.
	 */
	private List<ThreadDeclaration> threads;

	/**
	 * The list containing this file
	 */
	private SourceFileList fileList;

	/**
	 * Creates a new source file with the given name on the given operator.
	 * 
	 * @param name
	 *            The source file name.
	 * @param operator
	 *            The operator it is created on.
	 */
	public SourceFile(String name, ComponentInstance operator,
			SourceFileList fileList) {
		super(null);
		this.name = name;
		this.operator = operator;
		threads = new ArrayList<ThreadDeclaration>();
		this.fileList = fileList;
	}

	/**
	 * Accepts a printer visitor
	 */
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self
		super.accept(printer, currentLocation); // Accept the buffer allocation

		Iterator<ThreadDeclaration> iterator = threads.iterator();

		while (iterator.hasNext()) {
			ThreadDeclaration thread = iterator.next();
			thread.accept(printer, currentLocation); // Accept the threads
		}
	}

	/**
	 * Adds a thread to this source file.
	 * 
	 * @param thread
	 *            A {@link ThreadDeclaration}.
	 */
	public void addThread(ThreadDeclaration thread) {
		threads.add(thread);
	}

	/**
	 * Returns this source file's name.
	 * 
	 * @return This source file's name.
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Returns this source file's operator.
	 * 
	 * @return This source file's operator.
	 */
	public ComponentInstance getOperator() {
		return operator;
	}

	/**
	 * Returns this source file's thread list.
	 * 
	 * @return This source file's thread list.
	 */
	public List<ThreadDeclaration> getThreads() {
		return threads;
	}

	/**
	 * Removes the given thread from this source file's thread list.
	 * 
	 * @param thread
	 *            A {@link ThreadDeclaration}.
	 */
	public void removeThread(ThreadDeclaration thread) {
		threads.remove(thread);
	}

	/**
	 * Gets the file list containing this file
	 */
	public SourceFileList getFileList() {
		return fileList;
	}

	/**
	 * Returns true the buffer with the given name already exists somewhere
	 * (accessible or not).
	 */
	@Override
	public boolean existBuffer(String name, boolean searchInOtherFiles) {
		boolean bufferFound = super.existBuffer(name, false);

		if (!bufferFound && searchInOtherFiles && fileList != null) {
			for (SourceFile file : fileList) {
				bufferFound |= file.existBuffer(name, false);
			}
		}

		return bufferFound;
	}

	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		String code = "";

		code += "\n/////////////\n//File: " + getName() + "\n//////////////\n";

		// Displays buffer allocation
		code += super.toString();
		code += "\n";

		for (ThreadDeclaration thread : threads) {
			code += thread.toString();
			code += "\n";
		}

		return code;
	}
}
