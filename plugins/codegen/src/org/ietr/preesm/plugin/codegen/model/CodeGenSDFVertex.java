package org.ietr.preesm.plugin.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.sdf4j.model.sdf.SDFVertex;

public class CodeGenSDFVertex extends SDFVertex{

	public static final String OPERATOR ="operator"; 
	public static final String NB_REPEAT ="nb_repeat";
	public static final String POS ="pos";
	
	
	public ArchitectureComponent getOperator(){
		return (ArchitectureComponent) this.getPropertyBean().getValue(OPERATOR, ArchitectureComponent.class);
	}
	
	public void setOperator(ArchitectureComponent op){
		this.getPropertyBean().setValue(OPERATOR, getOperator(), op);
	}
	
	public int getPos(){
		return (Integer) this.getPropertyBean().getValue(POS, Integer.class);
	}
	
	public void setPos(int pos){
		this.getPropertyBean().setValue(POS, getPos(), pos);
	}
	
	public int getNbRepeat(){
		return (Integer) this.getPropertyBean().getValue(NB_REPEAT, Integer.class);
	}
	
	public void setNbRepeat(int nb){
		this.getPropertyBean().setValue(NB_REPEAT, getNbRepeat(), nb);
	}
}
