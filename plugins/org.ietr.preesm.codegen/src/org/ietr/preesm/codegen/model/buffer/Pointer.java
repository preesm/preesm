package org.ietr.preesm.codegen.model.buffer;

import net.sf.dftools.algorithm.model.sdf.SDFEdge;

import org.ietr.preesm.codegen.model.expression.IExpression;
import org.ietr.preesm.core.types.DataType;

public class Pointer extends Buffer {

	private IExpression symbolicSize;

	public Pointer(String name, DataType type, SDFEdge edge, IExpression size,
			AbstractBufferContainer container) {
		super(name, 0, type, edge, container);
		symbolicSize = size;
	}

	public void setSymbolicSize(IExpression expr) {
		symbolicSize = expr;
	}

	public IExpression getSymbolicSize() {
		return symbolicSize;
	}

}
