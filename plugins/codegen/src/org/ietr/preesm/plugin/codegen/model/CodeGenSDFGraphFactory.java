package org.ietr.preesm.plugin.codegen.model;

import java.util.HashMap;

import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;

public class CodeGenSDFGraphFactory {
	
	@SuppressWarnings("unchecked")
	public CodeGenSDFGraph create(DirectedAcyclicGraph dag){
		CodeGenSDFVertexFactory vertexFactory = new CodeGenSDFVertexFactory() ;
		HashMap<DAGVertex, CodeGenSDFVertex> aliases = new  HashMap<DAGVertex, CodeGenSDFVertex>() ;
		CodeGenSDFGraph output = new CodeGenSDFGraph(dag.getName()) ;
		for(DAGVertex vertex : dag.vertexSet()){
			CodeGenSDFVertex codeGenVertex = vertexFactory.create(vertex);
			aliases.put(vertex, codeGenVertex);
			output.addVertex(codeGenVertex);
		}
		for(DAGEdge edge : dag.edgeSet()){
			DAGVertex source = edge.getSource();
			DAGVertex target = edge.getTarget();
			CodeGenSDFVertex newSource = aliases.get(source);
			CodeGenSDFVertex newTarget = aliases.get(target);
			for(AbstractEdge subEdge : edge.getAggregate()){
				if(subEdge instanceof SDFEdge){
					SDFEdge sdfSubEdge = (SDFEdge) subEdge ;
					CodeGenSDFEdge newEdge = (CodeGenSDFEdge) output.addEdge(newSource, newTarget);
					SDFInterfaceVertex sourceInterface = null;
					SDFInterfaceVertex targetInterface = null;
					if((sourceInterface = newSource.getInterface(sdfSubEdge.getSourceInterface().getName())) == null){
						sourceInterface = new SDFSinkInterfaceVertex();
						sourceInterface.setName(sdfSubEdge.getSourceInterface().getName());
						newSource.addSink(sourceInterface);
					}
					if((targetInterface = newSource.getInterface(sdfSubEdge.getTargetInterface().getName())) == null){
						targetInterface = new SDFSourceInterfaceVertex();
						targetInterface.setName(sdfSubEdge.getTargetInterface().getName());
						newTarget.addSource(targetInterface);
					}
					newEdge.setSourceInterface(sourceInterface);
					newEdge.setTargetInterface(targetInterface);
					newEdge.setCons(sdfSubEdge.getCons().clone());
					newEdge.setProd(sdfSubEdge.getProd().clone());
				}
			}
		}
		return null ;
	}
	
	public CodeGenSDFGraph create(SDFGraph sdf){
		CodeGenSDFVertexFactory vertexFactory = new CodeGenSDFVertexFactory() ;
		HashMap<SDFAbstractVertex, CodeGenSDFVertex> aliases = new  HashMap<SDFAbstractVertex, CodeGenSDFVertex>() ;
		CodeGenSDFGraph output = new CodeGenSDFGraph(sdf.getName()) ;
		for(SDFAbstractVertex vertex : sdf.vertexSet()){
			CodeGenSDFVertex codeGenVertex = vertexFactory.create(vertex);
			aliases.put(vertex, codeGenVertex);
			output.addVertex(codeGenVertex);
		}
		for(SDFEdge edge : sdf.edgeSet()){
			SDFAbstractVertex source = edge.getSource();
			SDFAbstractVertex target = edge.getTarget();
			CodeGenSDFVertex newSource = aliases.get(source);
			CodeGenSDFVertex newTarget = aliases.get(target);
			CodeGenSDFEdge newEdge = (CodeGenSDFEdge) output.addEdge(newSource, newTarget);
			SDFInterfaceVertex sourceInterface = null;
			SDFInterfaceVertex targetInterface = null;
			if((sourceInterface = newSource.getInterface(edge.getSourceInterface().getName())) == null){
				sourceInterface = new SDFSinkInterfaceVertex();
				sourceInterface.setName(edge.getSourceInterface().getName());
				newSource.addSink(sourceInterface);
			}
			if((targetInterface = newSource.getInterface(edge.getTargetInterface().getName())) == null){
				targetInterface = new SDFSourceInterfaceVertex();
				targetInterface.setName(edge.getTargetInterface().getName());
				newTarget.addSource(targetInterface);
			}
			newEdge.setSourceInterface(sourceInterface);
			newEdge.setTargetInterface(targetInterface);
			newEdge.setCons(edge.getCons().clone());
			newEdge.setProd(edge.getProd().clone());
			newEdge.setDelay(edge.getDelay().clone());
		}
		return null ;
	}

}
