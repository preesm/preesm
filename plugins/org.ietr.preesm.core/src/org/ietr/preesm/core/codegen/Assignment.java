package org.ietr.preesm.core.codegen;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

import org.ietr.preesm.core.codegen.calls.FunctionArgument;
import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;

public class Assignment implements ICodeElement {

	private FunctionArgument var;
	private String value;

	public Assignment(FunctionArgument p, String value) {
		var = p;
		this.value = value;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
	}

	public FunctionArgument getVar() {
		return var;
	}

	public String getValue() {
		return value;
	}

	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		return null;
	}

}
