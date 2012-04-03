package org.ietr.preesm.codegen.model.main;

import net.sf.dftools.algorithm.model.psdf.parameters.PSDFDynamicParameter;

import org.ietr.preesm.codegen.model.call.Variable;

public class DynamicParameterAllocation extends VariableAllocation {

	PSDFDynamicParameter parameter;

	public DynamicParameterAllocation(PSDFDynamicParameter param, Variable var) {
		super(var);
		parameter = param;
	}
}
