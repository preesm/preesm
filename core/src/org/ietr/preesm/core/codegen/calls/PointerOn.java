package org.ietr.preesm.core.codegen.calls;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.ietr.preesm.core.codegen.types.DataType;

public class PointerOn extends FunctionArgument{

	public PointerOn(FunctionArgument elt) {
		super("&"+elt.getName(), new DataType("long"));
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
		// self
	}

}
