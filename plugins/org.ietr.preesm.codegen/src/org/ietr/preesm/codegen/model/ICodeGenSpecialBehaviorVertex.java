package org.ietr.preesm.codegen.model;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;

public interface ICodeGenSpecialBehaviorVertex {
	public boolean generateSpecialBehavior(
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException;
}
