package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;


public class CodeGenSDFBroadcastVertex extends CodeGenSDFVertex{

	
	public static final String TYPE =ImplementationPropertyNames.Vertex_vertexType;
	
	public CodeGenSDFBroadcastVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}
	
}
