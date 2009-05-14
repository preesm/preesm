package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFAbstractVertex;

public class Assignment implements ICodeElement{
	
	private Parameter var ;
	private String value ;
	
	public Assignment(Parameter p, String value){
		var = p ;
		this.value = value ;
	}
	
	public void accept(IAbstractPrinter printer, Object currentLocation) {
		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation);
	}

	public Parameter getVar(){
		return var ;
	}
	
	public String getValue(){
		return value ;
	}
	
	@Override
	public SDFAbstractVertex getCorrespondingVertex() {
		return null;
	}

}
