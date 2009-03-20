package org.ietr.preesm.core.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;
import org.sdf4j.model.sdf.esdf.SDFRoundBufferVertex;

public class CodeGenSDFRoundBufferVertex extends SDFRoundBufferVertex implements ICodeGenSDFVertex{

	
	public static final String TYPE =ImplementationPropertyNames.Vertex_vertexType;
	
	public CodeGenSDFRoundBufferVertex() {
		this.getPropertyBean().setValue(TYPE, VertexType.task);
	}
	
	public ArchitectureComponent getOperator(){
		return (ArchitectureComponent) this.getPropertyBean().getValue(OPERATOR, ArchitectureComponent.class);
	}
	
	public void setOperator(ArchitectureComponent op){
		this.getPropertyBean().setValue(OPERATOR, getOperator(), op);
	}
	
	public int getPos(){
		if(this.getPropertyBean().getValue(POS) != null){
			return (Integer) this.getPropertyBean().getValue(POS, Integer.class);
		}
		return 0 ;
	}
	
	public void setPos(int pos){
		this.getPropertyBean().setValue(POS, getPos(), pos);
	}
	
	public int getNbRepeat(){
		return 1;
	}
	
	public void setNbRepeat(int nb){
		this.getPropertyBean().setValue(ICodeGenSDFVertex.NB_REPEAT, nb);
	}
}
