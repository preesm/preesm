package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;


public class CodeGenSDFForkVertex extends CodeGenSDFVertex{
	
	
	public static final String TYPE =ImplementationPropertyNames.Vertex_vertexType;
	
	public CodeGenSDFForkVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}
	
	public String toString(){
		return "";
	}

}
