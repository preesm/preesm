package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;

public class CodeGenSDFInitVertex extends CodeGenSDFVertex{
	
	
	public static final String TYPE =ImplementationPropertyNames.Vertex_vertexType;
	
	public CodeGenSDFInitVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}
	
	public String toString(){
		return "";
	}

}
