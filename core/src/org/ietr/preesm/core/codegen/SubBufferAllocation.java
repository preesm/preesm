package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

public class SubBufferAllocation extends BufferAllocation{

	public SubBufferAllocation(Buffer buffer) {
		super(buffer);
	}
	
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
	}

}
