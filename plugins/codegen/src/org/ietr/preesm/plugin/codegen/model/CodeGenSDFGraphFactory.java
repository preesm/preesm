package org.ietr.preesm.plugin.codegen.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.sdf4j.demo.SDFAdapterDemo;
import org.sdf4j.demo.SDFtoDAGDemo;
import org.sdf4j.factories.DAGVertexFactory;
import org.sdf4j.importer.GMLSDFImporter;
import org.sdf4j.importer.InvalidFileException;
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
import org.sdf4j.visitors.DAGTransformation;
import org.sdf4j.visitors.HierarchyFlattening;
import org.sdf4j.visitors.ToHSDFVisitor;

public class CodeGenSDFGraphFactory {
	
	public static void main(String [] args){
		SDFAdapterDemo applet1 = new SDFAdapterDemo();
		SDFtoDAGDemo applet2 = new SDFtoDAGDemo();
		GMLSDFImporter importer = new GMLSDFImporter();
		// SDFGraph demoGraph = createTestComGraph();
		SDFGraph demoGraph;
		try {
			
			 demoGraph = importer.parse(new File(
			  "D:\\Preesm\\trunk\\tests\\IDCT2D\\idct2dCadOptim.graphml"));
			 
			/*demoGraph = importer.parse(new File(
					"D:\\Preesm\\trunk\\tests\\UMTS\\Tx_UMTS.graphml"));*/
			HierarchyFlattening visitor = new HierarchyFlattening();
			DAGTransformation<DirectedAcyclicGraph> dageur = new DAGTransformation<DirectedAcyclicGraph>(
					new DirectedAcyclicGraph(), new DAGVertexFactory());
			visitor.flattenGraph(demoGraph, 2);
			ToHSDFVisitor hsdf = new ToHSDFVisitor();
			visitor.getOutput().accept(hsdf);
			applet1.init(hsdf.getOutput());
			SDFGraph dag = visitor.getOutput().clone();
			dag.accept(dageur);
			applet2.init(dageur.getOutput());
			CodeGenSDFGraphFactory codeGenGraphFactory = new CodeGenSDFGraphFactory();
			CodeGenSDFGraph codeGenGraph = codeGenGraphFactory.create(dageur.getOutput());
			System.out.println(codeGenGraph.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public CodeGenSDFGraph create(DirectedAcyclicGraph dag){
		CodeGenSDFVertexFactory vertexFactory = new CodeGenSDFVertexFactory() ;
		HashMap<DAGVertex, SDFAbstractVertex> aliases = new  HashMap<DAGVertex, SDFAbstractVertex>() ;
		CodeGenSDFGraph output = new CodeGenSDFGraph(dag.getName()) ;
		for(DAGVertex vertex : dag.vertexSet()){
			SDFAbstractVertex codeGenVertex = vertexFactory.create(vertex);
			if(codeGenVertex instanceof CodeGenSDFVertex){
				((CodeGenSDFVertex)codeGenVertex).setNbRepeat(vertex.getNbRepeat().intValue());
			}
			aliases.put(vertex, codeGenVertex);
			output.addVertex(codeGenVertex);
		}
		for(DAGEdge edge : dag.edgeSet()){
			DAGVertex source = edge.getSource();
			DAGVertex target = edge.getTarget();
			SDFAbstractVertex newSource = aliases.get(source);
			SDFAbstractVertex newTarget = aliases.get(target);
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
		return output ;
	}
	
	public CodeGenSDFGraph create(SDFGraph sdf){
		HashMap<SDFAbstractVertex, Integer> vrb = sdf.getVRB();
		CodeGenSDFVertexFactory vertexFactory = new CodeGenSDFVertexFactory() ;
		HashMap<SDFAbstractVertex, SDFAbstractVertex> aliases = new  HashMap<SDFAbstractVertex, SDFAbstractVertex>() ;
		CodeGenSDFGraph output = new CodeGenSDFGraph(sdf.getName()) ;
		for(SDFAbstractVertex vertex : sdf.vertexSet()){
			SDFAbstractVertex codeGenVertex = vertexFactory.create(vertex);
			if(codeGenVertex instanceof CodeGenSDFVertex){
				((CodeGenSDFVertex)codeGenVertex).setNbRepeat(vrb.get(vertex));
			}
			aliases.put(vertex, codeGenVertex);
			output.addVertex(codeGenVertex);
		}
		for(SDFEdge edge : sdf.edgeSet()){
			SDFAbstractVertex source = edge.getSource();
			SDFAbstractVertex target = edge.getTarget();
			SDFAbstractVertex newSource = aliases.get(source);
			SDFAbstractVertex newTarget = aliases.get(target);
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
		return output ;
	}

}
