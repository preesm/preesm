package org.ietr.preesm.plugin.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureInterface;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;

/**
 * @author jpiat
 */
public class CodeGenSDFReceiveVertex extends CodeGenSDFVertex{

	public static final String INTERFACE ="interface"; 
	public static final String TYPE =ImplementationPropertyNames.Vertex_vertexType;
	
	public CodeGenSDFReceiveVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.receive);
	}
	
	public ArchitectureInterface getInterface(){
		return (ArchitectureInterface) this.getPropertyBean().getValue(INTERFACE, ArchitectureInterface.class);
	}
	
	public void setOperator(ArchitectureInterface inter){
		this.getPropertyBean().setValue(INTERFACE, getInterface(), inter);
	}
}
