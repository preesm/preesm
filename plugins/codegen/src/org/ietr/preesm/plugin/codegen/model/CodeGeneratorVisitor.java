package org.ietr.preesm.plugin.codegen.model;

import org.ietr.preesm.core.codegen.model.CodeGenSDFEdge;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.visitors.GraphVisitor;
import org.sdf4j.visitors.SDF4JException;

/**
 * Visitor of a graph of type CodegenSDF
 * 
 * @author jpiat
 */
public class CodeGeneratorVisitor implements GraphVisitor<CodeGenSDFGraph, CodeGenSDFVertex, CodeGenSDFEdge>{
	
	public void visit(CodeGenSDFEdge edge){
		
	}
	
	/**
	 * visiting a vertex implies accepting its children
	 */
	public void visit(CodeGenSDFVertex vertex) throws SDF4JException{
		if(vertex.getGraphDescription() != null){
			vertex.getGraphDescription().accept(this);
		}
		
	}
	
	public void visit(CodeGenSDFGraph graph)throws SDF4JException{
		for(SDFEdge edge : graph.edgeSet()){
			edge.accept(this);
		}
		for(SDFAbstractVertex vertex : graph.vertexSet()){
			vertex.accept(this);
		}
	}

}
