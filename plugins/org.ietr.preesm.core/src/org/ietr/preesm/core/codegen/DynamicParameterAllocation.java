package org.ietr.preesm.core.codegen;

import net.sf.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;

import org.ietr.preesm.core.codegen.calls.Variable;

public class DynamicParameterAllocation extends VariableAllocation {

	PSDFDynamicParameter parameter;

	public DynamicParameterAllocation(PSDFDynamicParameter param, Variable var) {
		super(var);
		parameter = param;
	}
}
