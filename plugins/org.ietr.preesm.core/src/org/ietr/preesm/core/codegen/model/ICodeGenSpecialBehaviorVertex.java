package org.ietr.preesm.core.codegen.model;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;


public interface ICodeGenSpecialBehaviorVertex {
	public boolean generateSpecialBehavior(
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException;
}
