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

import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.printer.AbstractPrinter;

/**
 * Source file to be executed on a given core. A source file contains Buffer
 * declarations and threads.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class SourceFile extends AbstractBufferContainer {

	/**
	 * File name
	 */
	private String name;
	/**
	 * Operator on which this source file will run
	 */
	private Operator operator;

	/**
	 * Operator on which this source file will run
	 */
	private List<ThreadDeclaration> threads;

	/**
	 * 
	 */
	public SourceFile(String name, Operator operator) {
		super(null);
		this.name = name;
		this.operator = operator;
		threads = new ArrayList<ThreadDeclaration>();
	}

	/**
	 * Accepts a printer visitor
	 */
	public void accept(AbstractPrinter printer) {
		printer.visit(this, 0); // Visit self
		super.accept(printer); // Accept the buffer allocation
		printer.visit(this, 1); // Visit self

		Iterator<ThreadDeclaration> iterator = threads.iterator();

		while (iterator.hasNext()) {
			ThreadDeclaration thread = iterator.next();

			thread.accept(printer); // Accept the threads
			printer.visit(this, 2); // Visit self
		}
		printer.visit(this, 3); // Visit self
	}

	public void addThread(ThreadDeclaration thread) {
		threads.add(thread);
	}

	public String getName() {
		return name;
	}

	public Operator getOperator() {
		return operator;
	}

	public List<ThreadDeclaration> getThreads() {
		return threads;
	}

	public void removeThread(ThreadDeclaration thread) {
		threads.remove(thread);
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
