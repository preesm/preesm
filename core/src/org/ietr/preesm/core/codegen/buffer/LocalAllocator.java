package org.ietr.preesm.core.codegen.buffer;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

public class LocalAllocator extends GlobalAllocator{

	public LocalAllocator(AbstractBufferContainer container) {
		super(container);
	}
	
	public LocalAllocator(IBufferAllocator parent , AbstractBufferContainer container) {
		super(parent, container);
	}

	
	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		for(BufferAllocation allocToPrint : getBufferAllocations()){
			printer.visit(allocToPrint, CodeZoneId.body, currentLocation);
		}
	}
	
	@Override
	public IBufferAllocator openNewSection(AbstractBufferContainer codeSection) {
		IBufferAllocator newAlloc = new LocalAllocator(this , codeSection);
		this.childAllocator.add(newAlloc);
		return newAlloc;
	}
}
