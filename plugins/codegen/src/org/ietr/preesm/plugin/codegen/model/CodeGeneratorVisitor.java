package org.ietr.preesm.plugin.codegen.model;

import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.visitors.GraphVisitor;

public class CodeGeneratorVisitor implements GraphVisitor<CodeGenSDFGraph, CodeGenSDFVertex, CodeGenSDFEdge>{
	
	public void visit(CodeGenSDFEdge edge){
		
	}
	
	public void visit(CodeGenSDFVertex vertex){
		if(vertex.getGraphDescription() != null){
			vertex.getGraphDescription().accept(this);
		}
		
	}
	
	public void visit(CodeGenSDFGraph graph){
		for(SDFEdge edge : graph.edgeSet()){
			edge.accept(this);
		}
		for(SDFAbstractVertex vertex : graph.vertexSet()){
			vertex.accept(this);
		}
	}

}
