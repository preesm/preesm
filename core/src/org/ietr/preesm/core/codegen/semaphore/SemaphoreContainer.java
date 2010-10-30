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

package org.ietr.preesm.core.codegen.semaphore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.ietr.preesm.core.codegen.types.DataType;

/**
 * Container that handles the semaphores
 * 
 * @author mpelcat
 */
public class SemaphoreContainer extends ArrayList<Semaphore> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String semaphoreBufferName = "sem";

	/**
	 * Buffer container containing the current semaphore manager
	 */
	private AbstractBufferContainer parentContainer;

	/**
	 * Allocation of the semaphores
	 */
	private Buffer semaphoreBuffer = null;

	public SemaphoreContainer(AbstractBufferContainer parentContainer) {
		super();
		this.parentContainer = parentContainer;
	}

	public Buffer allocateSemaphores() {
		semaphoreBuffer = new Buffer(semaphoreBufferName, this.size(),
				new DataType("semaphore"), null, parentContainer);

		semaphoreBuffer = parentContainer.allocateBuffer(semaphoreBufferName, this.size(),
				new DataType("semaphore"));
		return semaphoreBuffer;
	}

	public Semaphore createSemaphore(List<Buffer> agg, SemaphoreType semType,
			CodeSectionType codeContainerType) {
		Semaphore sem = getSemaphore(agg, semType, codeContainerType);

		if (sem == null) {
			sem = new Semaphore(this, agg, semType, codeContainerType);
			add(sem);
			return sem;
		} else {
			return sem;
		}
	}

	/**
	 * Returns a semaphore if it exists (with same protected buffers and type)
	 * or null
	 */
	public Semaphore getSemaphore(List<Buffer> bufList, SemaphoreType semType,
			CodeSectionType codeContainerType) {
		Semaphore sem = null;

		Iterator<Semaphore> currentIt = iterator();

		while (currentIt.hasNext()) {
			sem = currentIt.next();

			List<Buffer> semBufList = sem.getProtectedBuffers();
			boolean sameBuffers = semBufList.size() == bufList.size();
			// Two semaphores protecting the same buffers in the same direction
			// are the same semaphore
			if (sameBuffers) {
				for (Buffer buf : semBufList) {
					if (!bufList.contains(buf)) {
						sameBuffers = false;
						break;
					}

				}
			}

			if (sameBuffers && sem.getSemaphoreType().equals(semType)
					&& sem.getCodeContainerType().equals(codeContainerType)) {
				return sem;
			}
		}

		return null;
	}

	public Buffer getSemaphoreBuffer() {
		return semaphoreBuffer;
	}

}
