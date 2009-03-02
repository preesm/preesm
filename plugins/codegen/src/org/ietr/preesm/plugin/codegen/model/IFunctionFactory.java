package org.ietr.preesm.plugin.codegen.model;

import org.ietr.preesm.core.codegen.model.FunctionCall;

/**
 * @author jpiat
 */
public interface IFunctionFactory {
	public FunctionCall create(String calPath);
}
