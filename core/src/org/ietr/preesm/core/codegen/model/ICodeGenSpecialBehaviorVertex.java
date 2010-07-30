package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.sdf4j.model.parameters.InvalidExpressionException;


public interface ICodeGenSpecialBehaviorVertex {
	public boolean generateSpecialBehavior(AbstractBufferContainer parentContainer) throws InvalidExpressionException;
}
