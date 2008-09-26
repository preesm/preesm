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

import org.ietr.preesm.core.codegen.printer.AbstractPrinter;
import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;

/**
 * Class representing a semaphore in the code.
 * A semaphore protects a buffer from being read while empty
 * or written while full.
 * 
 * @author mpelcat
 */
public class Semaphore {

	/**
	 * Semaphore container.
	 */
	private SemaphoreContainer container;

	/**
	 * The buffers protected by the current semaphore.
	 */
	private BufferAggregate protectedAggregate;

	/**
	 * A semaphore can be the signal of a full buffer aggregate or the 
	 * signal of an empty buffer aggregate.
	 */
	private SemaphoreType semaphoreType;
	
	public Semaphore(SemaphoreContainer container, BufferAggregate protectedBuffers, SemaphoreType semaphoreType) {
		
		this.semaphoreType = semaphoreType;
		
		this.protectedAggregate = protectedBuffers;
		
		this.container = container;
	}

	public void accept(AbstractPrinter printer) {
		printer.visit(this,0); // Visit self
	}
	
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof Semaphore)
			if(((Semaphore)obj).protectedAggregate == protectedAggregate)
				if(((Semaphore)obj).semaphoreType == semaphoreType)
					return true;
		return false;
	}

	public BufferAggregate getProtectedBuffers() {
		return protectedAggregate;
	}

	/**
	 * A semaphore is determined by its number.
	 */
	public int getSemaphoreNumber() {
		return container.indexOf(this);
	}

	public SemaphoreType getSemaphoreType() {
		return semaphoreType;
	}
	
	/**
	 * Displays pseudo-code for test
	 */
	public String toString() {
		
		String code = "";
		
		code += "sem[" + getSemaphoreNumber() + "], " + semaphoreType.toString();
		
		return code;
	}
}
