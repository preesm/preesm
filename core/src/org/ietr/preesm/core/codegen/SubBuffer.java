package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.sdfProperties.BufferAggregate;

public class SubBuffer extends Buffer {

	private Variable index;
	private Buffer parentBuffer;

	public SubBuffer(String name, Integer size, DataType type,
			BufferAggregate aggregate) {
		super(name, size, type, aggregate);
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

	public void setParentBuffer(Buffer parentBuffer) {
		this.parentBuffer = parentBuffer;
	}

}
