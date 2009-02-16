package org.ietr.preesm.plugin.codegen.model;

import org.sdf4j.model.AbstractEdgePropertyType;
import org.sdf4j.model.sdf.SDFEdge;

public class CodeGenSDFEdge extends SDFEdge{

	
	public CodeGenSDFEdge(AbstractEdgePropertyType<?> prod,
			AbstractEdgePropertyType<?> cons, AbstractEdgePropertyType<?> delay,
			AbstractEdgePropertyType<?> dataType){
		super(prod, cons, delay, dataType);
	}
	
	public int getSize(){
		return Math.max(getProd().intValue(), getCons().intValue());
	}
	
	public String toString(){
		return getDataType().toString()+" "+getSource().getName()+"_"+getSourceInterface().getName()+ " ["+getProd().intValue()+"];\n" ;
	}
}
