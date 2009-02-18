package org.ietr.preesm.core.codegen.model;

import org.sdf4j.iterators.SDFIterator;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;

public class CodeGenSDFGraph extends SDFGraph{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4150105790430446583L;
	
	public CodeGenSDFGraph(String name){
		super(new CodeGenSDFEdgeFactory());
		this.setName(name);
	}
	
	public CodeGenSDFGraph(CodeGenSDFEdgeFactory factory){
		super(factory);
	}

	public String toString(){
		String code = new String();
		if(this.getParentVertex() == null){
			code += "void main(int argc, char ** argv){\n";
		}
		for(SDFEdge edge : this.edgeSet()){
			code += edge.toString();
		}
		SDFIterator iterator = new SDFIterator(this);
		while(iterator.hasNext()){
			SDFAbstractVertex func = iterator.next();
			code += func.toString();
		}
		return code ;
	}
}
