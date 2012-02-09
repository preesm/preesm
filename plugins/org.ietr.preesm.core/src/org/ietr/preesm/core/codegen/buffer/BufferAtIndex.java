package org.ietr.preesm.core.codegen.buffer;

import org.ietr.preesm.core.codegen.calls.FunctionArgument;
import org.ietr.preesm.core.codegen.expression.VariableExpression;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

public class BufferAtIndex extends FunctionArgument {

	private VariableExpression index;
	private Buffer parentBuffer;

	public BufferAtIndex(VariableExpression index, Buffer parentBuffer) {
		super(parentBuffer.getName(), parentBuffer.getType());
		this.parentBuffer = parentBuffer;
		this.index = index;
	}

	public VariableExpression getIndex() {
		return index;
	}

	public Buffer getParentBuffer() {
		return parentBuffer;
	}

	public void setIndex(VariableExpression index) {
		this.index = index;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self
	}

	public void setParentBuffer(Buffer parentBuffer) {
		this.parentBuffer = parentBuffer;
	}

}
