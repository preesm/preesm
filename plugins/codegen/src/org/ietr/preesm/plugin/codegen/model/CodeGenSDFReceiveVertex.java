package org.ietr.preesm.plugin.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureInterface;

public class CodeGenSDFReceiveVertex extends CodeGenSDFVertex{

	public static final String INTERFACE ="interface"; 
	
	public ArchitectureInterface getInterface(){
		return (ArchitectureInterface) this.getPropertyBean().getValue(INTERFACE, ArchitectureInterface.class);
	}
	
	public void setOperator(ArchitectureInterface inter){
		this.getPropertyBean().setValue(INTERFACE, getInterface(), inter);
	}
}
