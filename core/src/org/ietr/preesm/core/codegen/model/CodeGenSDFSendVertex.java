package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureInterface;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;

public class CodeGenSDFSendVertex extends CodeGenSDFVertex{

	public static final String INTERFACE ="interface";
	public static final String TYPE =ImplementationPropertyNames.Vertex_vertexType;
	
	public CodeGenSDFSendVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.send);
	}
	
	public ArchitectureInterface getInterface(){
		return (ArchitectureInterface) this.getPropertyBean().getValue(INTERFACE, ArchitectureInterface.class);
	}
	
	public void setOperator(ArchitectureInterface inter){
		this.getPropertyBean().setValue(INTERFACE, getInterface(), inter);
	}
	
}
