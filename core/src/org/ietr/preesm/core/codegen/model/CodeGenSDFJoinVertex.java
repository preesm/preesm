package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;


public class CodeGenSDFJoinVertex extends CodeGenSDFVertex{

	
	public static final String TYPE =ImplementationPropertyNames.Vertex_vertexType;
	
	public CodeGenSDFJoinVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}
	
	public String toString(){
		return "";
	}
}
