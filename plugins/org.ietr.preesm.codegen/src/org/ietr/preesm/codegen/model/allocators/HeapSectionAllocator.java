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

	public void setSize(int size) {
		if (size > this.getSize()) {
			this.getBuffer().setSize(size);
		}
		if (parentHeap != null) {
			parentHeap.setSize((parentHeap.getCurrentPos() - parentHeap
					.getBasePos()) + this.getSize());
		}
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		/*
		 * for(SubBufferAllocation allocToPrint : alloc.values()){
		 * printer.visit(allocToPrint, CodeZoneId.body, currentLocation) ; }
		 */
	}

}
