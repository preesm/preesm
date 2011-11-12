package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

public interface ICodeGenSpecialBehaviorVertex {
	public boolean generateSpecialBehavior(
			AbstractBufferContainer parentContainer)
			throws InvalidExpressionException;
}
