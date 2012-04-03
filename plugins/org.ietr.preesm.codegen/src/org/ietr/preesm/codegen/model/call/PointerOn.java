package org.ietr.preesm.codegen.model.call;

import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.core.types.DataType;

public class PointerOn extends FunctionArgument {

	public PointerOn(FunctionArgument elt) {
		super("&" + elt.getName(), new DataType("long"));
	}

	@Override
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
		// self
	}

}
