/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen.model.allocators;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.BufferAllocation;
import org.ietr.preesm.codegen.model.buffer.SubBuffer;
import org.ietr.preesm.codegen.model.expression.ConstantExpression;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;

public class HeapSectionAllocator extends VirtualHeapAllocator {

	protected VirtualHeapAllocator parentHeap;

	public HeapSectionAllocator(VirtualHeapAllocator parentHeap,
			AbstractBufferContainer container, int pos) {
		super(container);
		currentPos = pos;
		basePos = pos;
		this.parentHeap = parentHeap;
	}

	public void setBasePos(int newBase) {
		int diffSize = basePos - newBase;
		for (BufferAllocation alloc : allocToPos.keySet()) {
			if (alloc instanceof HeapSectionAllocator) {
				((HeapSectionAllocator) alloc).setBasePos(allocToPos.get(alloc)
						- diffSize);
				allocToPos.put(alloc, allocToPos.get(alloc) - diffSize);
			} else {
				((SubBuffer) alloc.getBuffer())
						.setIndex(new ConstantExpression(allocToPos.get(alloc)
								- diffSize));
				allocToPos.put(alloc, allocToPos.get(alloc) - diffSize);
			}
		}
		this.basePos = newBase;
	}

	@Override
	public void setSize(int size) {
		if (size > this.getSize()) {
			this.getBuffer().setSize(size);
		}
		if (parentHeap != null) {
			parentHeap.setSize((parentHeap.getCurrentPos() - parentHeap
					.getBasePos()) + this.getSize());
		}
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		/*
		 * for(SubBufferAllocation allocToPrint : alloc.values()){
		 * printer.visit(allocToPrint, CodeZoneId.body, currentLocation) ; }
		 */
	}

}
