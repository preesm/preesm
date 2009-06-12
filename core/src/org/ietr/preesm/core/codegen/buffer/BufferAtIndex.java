package org.ietr.preesm.core.codegen.buffer;

import org.ietr.preesm.core.codegen.Parameter;
import org.ietr.preesm.core.codegen.expression.Variable;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

public class BufferAtIndex extends Parameter {

	private Variable index;
	private Buffer parentBuffer;

	public BufferAtIndex(Variable index ,Buffer parentBuffer) {
		super(parentBuffer.getName(), parentBuffer.getType());
		this.parentBuffer = parentBuffer;
		this.index = index ;
	}

	public Variable getIndex() {
		return index;
	}

	public Buffer getParentBuffer() {
		return parentBuffer;
	}

	public void setIndex(Variable index) {
		this.index = index;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit self
	}
	
	public void setParentBuffer(Buffer parentBuffer) {
		this.parentBuffer = parentBuffer;
	}

}
