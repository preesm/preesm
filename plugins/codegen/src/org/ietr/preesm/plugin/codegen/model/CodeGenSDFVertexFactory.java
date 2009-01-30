package org.ietr.preesm.plugin.codegen.model;

import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;

public class CodeGenSDFVertexFactory {

	public SDFAbstractVertex create(DAGVertex dagVertex){
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory() ;
		CodeGenSDFVertex newVertex = new CodeGenSDFVertex();
		newVertex.setName(dagVertex.getName());
		if(dagVertex.getCorrespondingSDFVertex().getGraphDescription() != null){
			newVertex.setGraphDescription(graphFactory.create((SDFGraph) dagVertex.getCorrespondingSDFVertex().getGraphDescription()) );
			for(SDFAbstractVertex child : ((CodeGenSDFGraph)newVertex.getGraphDescription()).vertexSet()){
				if(child instanceof SDFSinkInterfaceVertex){
					newVertex.getSinks().remove((newVertex.getInterface(child.getName())));
					newVertex.addSink((SDFSinkInterfaceVertex) child);
				}else if(child instanceof SDFSourceInterfaceVertex){
					newVertex.getSources().remove((newVertex.getInterface(child.getName())));
					newVertex.addSource((SDFSourceInterfaceVertex) child);
				}
			}
		}
		return newVertex ;
	}
	
	public SDFAbstractVertex create(SDFAbstractVertex sdfVertex){
		SDFAbstractVertex newVertex ;
		if(sdfVertex instanceof SDFSinkInterfaceVertex){
			newVertex = sdfVertex.clone();
		}else if(sdfVertex instanceof SDFSinkInterfaceVertex){
			newVertex = sdfVertex.clone();
		}else{
			newVertex = new CodeGenSDFVertex() ;
		}
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory() ;
		newVertex.setName(sdfVertex.getName());
		if(sdfVertex.getGraphDescription() != null){
			newVertex.setGraphDescription(graphFactory.create((SDFGraph) sdfVertex.getGraphDescription()) );
			for(SDFAbstractVertex child : ((CodeGenSDFGraph)newVertex.getGraphDescription()).vertexSet()){
				if(child instanceof SDFSinkInterfaceVertex){
					newVertex.getSinks().remove((newVertex.getInterface(child.getName())));
					newVertex.addSink((SDFSinkInterfaceVertex) child);
				}else if(child instanceof SDFSourceInterfaceVertex){
					newVertex.getSources().remove((newVertex.getInterface(child.getName())));
					newVertex.addSource((SDFSourceInterfaceVertex) child);
				}
			}
		}
		return newVertex ;
	}
}
