package org.ietr.preesm.plugin.codegen.model;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.VertexType;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;

public class CodeGenSDFVertexFactory {

	public SDFAbstractVertex create(DAGVertex dagVertex){
		CodeGenSDFGraphFactory graphFactory = new CodeGenSDFGraphFactory() ;
		CodeGenSDFVertex newVertex ; ;
		if(dagVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType).equals(VertexType.task)){
			newVertex  = new CodeGenSDFVertex();
		}else if(dagVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType).equals(VertexType.send)){
			newVertex  = new CodeGenSDFSendVertex();
		}else if(dagVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_vertexType).equals(VertexType.receive)){
			newVertex  = new CodeGenSDFReceiveVertex();
		}else{
			newVertex  = new CodeGenSDFVertex();
		}
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
		newVertex.setOperator((ArchitectureComponent) dagVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_Operator));
		newVertex.setPos((Integer) dagVertex.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_schedulingOrder));
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
