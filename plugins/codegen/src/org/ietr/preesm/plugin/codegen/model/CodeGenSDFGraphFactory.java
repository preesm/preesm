package org.ietr.preesm.plugin.codegen.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.ietr.preesm.core.codegen.model.CodeGenSDFEdge;
import org.ietr.preesm.core.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.core.codegen.model.CodeGenSDFVertex;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.sdf4j.SDFMath;
import org.sdf4j.demo.SDFAdapterDemo;
import org.sdf4j.demo.SDFtoDAGDemo;
import org.sdf4j.factories.DAGVertexFactory;
import org.sdf4j.importer.GMLSDFImporter;
import org.sdf4j.importer.InvalidFileException;
import org.sdf4j.iterators.SDFIterator;
import org.sdf4j.model.AbstractEdge;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFInterfaceVertex;
import org.sdf4j.model.sdf.SDFVertex;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.sdf4j.model.sdf.types.SDFIntEdgePropertyType;
import org.sdf4j.visitors.DAGTransformation;
import org.sdf4j.visitors.SDF4JException;

/**
 * @author jpiat
 */
public class CodeGenSDFGraphFactory {
	
	private IFile mainFile ;
	
	public CodeGenSDFGraphFactory(IFile parentAlgoFile){
		mainFile = parentAlgoFile ;
	}
	
	public static void main(String [] args){
		SDFAdapterDemo applet1 = new SDFAdapterDemo();
		SDFtoDAGDemo applet2 = new SDFtoDAGDemo();
		GMLSDFImporter importer = new GMLSDFImporter();
		// SDFGraph demoGraph = createTestComGraph();
		SDFGraph demoGraph;
		try {
			
			 demoGraph = importer.parse(new File(
			  "D:\\Preesm\\trunk\\tests\\SmallTestCase\\Algo\\TestCase.graphml"));
			 
			/*demoGraph = importer.parse(new File(
					"D:\\Preesm\\trunk\\tests\\UMTS\\Tx_UMTS.graphml"));*/
			DAGTransformation<DirectedAcyclicGraph> dageur = new DAGTransformation<DirectedAcyclicGraph>(
					new DirectedAcyclicGraph(), new DAGVertexFactory());
			SDFGraph dag = demoGraph.clone();
			dag.accept(dageur);
			applet2.init(dageur.getOutput());
			/*CodeGenSDFGraphFactory codeGenGraphFactory = new CodeGenSDFGraphFactory();
			CodeGenSDFGraph codeGenGraph = codeGenGraphFactory.create(dageur.getOutput());
			System.out.println(codeGenGraph.toString());*/
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SDF4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public CodeGenSDFGraph create(DirectedAcyclicGraph dag){
		CodeGenSDFVertexFactory vertexFactory = new CodeGenSDFVertexFactory(mainFile) ;
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
		clusterizeStronglyConnected(sdf);
		CodeGenSDFVertexFactory vertexFactory = new CodeGenSDFVertexFactory(mainFile) ;
		HashMap<SDFAbstractVertex, SDFAbstractVertex> aliases = new  HashMap<SDFAbstractVertex, SDFAbstractVertex>() ;
		CodeGenSDFGraph output = new CodeGenSDFGraph(sdf.getName()) ;
		SDFIterator iterator = new SDFIterator(sdf);
		int pos = 0 ;
		while(iterator.hasNext()){
			SDFAbstractVertex vertex = iterator.next();
			SDFAbstractVertex codeGenVertex = vertexFactory.create(vertex);
			if(codeGenVertex instanceof CodeGenSDFVertex){
				((CodeGenSDFVertex)codeGenVertex).setNbRepeat(vertex.getNbRepeat());
				((CodeGenSDFVertex) codeGenVertex).setPos(pos);
				pos ++ ;
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
	
	public void clusterizeStronglyConnected(SDFGraph graph){
		int i = 0 ;
		StrongConnectivityInspector<SDFAbstractVertex, SDFEdge> inspector = new StrongConnectivityInspector<SDFAbstractVertex, SDFEdge>(graph) ;
		for(Set<SDFAbstractVertex> strong : inspector.stronglyConnectedSets()){
			boolean noInterface = true ;
			for(SDFAbstractVertex vertex :strong){
				noInterface &= !(vertex instanceof SDFInterfaceVertex) ;
			}
			if(noInterface && strong.size() > 1){
				SDFAbstractVertex cluster = culsterizeLoopTest(graph, new ArrayList<SDFAbstractVertex>(strong), "cluster_"+i);
				i ++ ;
			}
			
		}
	}
	
	public static SDFAbstractVertex culsterizeLoopTest(SDFGraph graph,
			List<SDFAbstractVertex> block, String name) {
		graph.validateModel();
		
		if (block.size() > 1) {
			int pgcd = 0 ;
			int nbLoopPort = 0 ;
			SDFGraph clusterGraph = graph.clone();
			clusterGraph.setName(name);
			SDFVertex cluster = new SDFVertex();
			cluster.setName(name);
			cluster.setGraphDescription(clusterGraph);
			graph.addVertex(cluster);
			HashMap<SDFAbstractVertex, SDFAbstractVertex> copies = new HashMap<SDFAbstractVertex, SDFAbstractVertex>();
			List<SDFAbstractVertex> vertices = new ArrayList<SDFAbstractVertex>(clusterGraph.vertexSet());
			for(int i = 0 ; i < vertices.size() ; i ++){
				boolean isInBlock = false;
				for (int r = 0; r < block.size(); r++) {
					if(block.get(r).getName().equals(vertices.get(i).getName())){
						isInBlock = true ;
						copies.put(block.get(r), vertices.get(i));
					}
				}
				if(!isInBlock){
					clusterGraph.removeVertex(vertices.get(i));
				}
			}
			for (int r = 0; r < block.size(); r++) {
				SDFAbstractVertex seed = copies.get(block.get(r));
				if(pgcd == 0){
					pgcd = seed.getNbRepeat();
				}else{
					pgcd = SDFMath.gcd(pgcd, seed.getNbRepeat());
				}
				List<SDFEdge> outgoingEdges = new ArrayList<SDFEdge>(graph
						.outgoingEdgesOf(block.get(r)));
				for (SDFEdge edge : outgoingEdges) {
					SDFAbstractVertex target = graph.getEdgeTarget(edge);
					if(!block.contains(target)){
						SDFInterfaceVertex targetPort = new SDFSinkInterfaceVertex();
						targetPort.setName(cluster.getName() + "_"
								+ edge.getTargetInterface().getName());
						cluster.addSink(targetPort);
						SDFEdge extEdge = graph.addEdge(cluster, target);
						extEdge.copyProperties(edge);
						extEdge.setSourceInterface(targetPort);
						cluster.setInterfaceVertexExternalLink(extEdge, targetPort);
						SDFEdge newEdge = clusterGraph
								.addEdge(seed, targetPort);
						newEdge.copyProperties(edge);
						newEdge.setCons(new SDFIntEdgePropertyType(newEdge.getProd().intValue()));
						graph.removeEdge(edge);
					}
				}
				List<SDFEdge> incomingEdges = new ArrayList<SDFEdge>(graph
						.incomingEdgesOf(block.get(r)));
				for (SDFEdge edge : incomingEdges) {
					SDFAbstractVertex source = graph.getEdgeSource(edge);
					SDFAbstractVertex target = graph.getEdgeTarget(edge);
					if(block.contains(source) && block.contains(target)&& edge.getDelay().intValue() > 0){
						SDFInterfaceVertex targetPort = new SDFSinkInterfaceVertex();
						targetPort.setName("outLoopPort_"+nbLoopPort);
						SDFInterfaceVertex sourcePort = new SDFSourceInterfaceVertex();
						sourcePort.setName("inLoopPort_"+nbLoopPort);
						nbLoopPort ++ ;
						cluster.addSink(targetPort);
						cluster.addSource(sourcePort);
						
						SDFEdge loopEdge = graph.addEdge(cluster, cluster);
						loopEdge.copyProperties(edge);
						edge.setSourceInterface(targetPort);
						edge.setTargetInterface(sourcePort);
						
						
						SDFEdge lastLoop = clusterGraph.addEdge(copies.get(source), targetPort);
						lastLoop.copyProperties(edge);
						lastLoop.setDelay(new SDFIntEdgePropertyType(0));
						
						SDFEdge firstLoop = clusterGraph.addEdge(sourcePort, copies.get(target));
						firstLoop.copyProperties(edge);
						firstLoop.setDelay(new SDFIntEdgePropertyType(0));
						SDFEdge inLoopEdge = clusterGraph.getEdge(copies.get(source), copies.get(target));
						if(inLoopEdge.getDelay().intValue() > 0){
							clusterGraph.removeEdge(inLoopEdge);
						}
						graph.removeEdge(edge);
					}else if (!block.contains(source)) {
						SDFInterfaceVertex sourcePort = new SDFSourceInterfaceVertex();
						sourcePort.setName(cluster.getName() + "_"
								+ edge.getSourceInterface().getName());
						cluster.addSource(sourcePort);
						SDFEdge extEdge = graph.addEdge(source, cluster);
						extEdge.copyProperties(edge);
						extEdge.setTargetInterface(sourcePort);
						cluster.setInterfaceVertexExternalLink(extEdge, sourcePort);
						SDFEdge newEdge = clusterGraph
								.addEdge(sourcePort, seed);
						newEdge.copyProperties(edge);
						newEdge.setProd(newEdge.getCons());
						graph.removeEdge(edge);
					}
				}
			}
			for (int r = 0; r < block.size(); r++) {
				graph.removeVertex(block.get(r));
			}
			clusterGraph.validateModel();	
			cluster.setNbRepeat(pgcd);
			return cluster;
		}else{
			return null ;
		}
	}
	
	public static SDFAbstractVertex culsterizeLoop(SDFGraph graph,
			List<SDFAbstractVertex> block, String name) {
		
		if (block.size() > 1) {
			int pgcd = 0 ;
			int nbLoopPort = 0 ;
			SDFGraph clusterGraph = new SDFGraph();
			clusterGraph.setName(name);
			SDFVertex cluster = new SDFVertex();
			cluster.setName(name);
			cluster.setGraphDescription(clusterGraph);
			graph.addVertex(cluster);
			for (int r = 0; r < block.size(); r++) {
				SDFAbstractVertex seed = block.get(r);
				if(! clusterGraph.vertexSet().contains(seed)){
					clusterGraph.addVertex(seed);
				}
				if(pgcd == 0){
					pgcd = seed.getNbRepeat();
				}else{
					pgcd = SDFMath.gcd(pgcd, seed.getNbRepeat());
				}
				List<SDFEdge> outgoingEdges = new ArrayList<SDFEdge>(graph
						.outgoingEdgesOf(seed));
				for (SDFEdge edge : outgoingEdges) {
					SDFAbstractVertex target = graph.getEdgeTarget(edge);
					SDFAbstractVertex source = graph.getEdgeSource(edge);

					if (block.contains(target) && edge.getDelay().intValue() == 0) {
						if (!clusterGraph.vertexSet().contains(target)) {
							clusterGraph.addVertex(target);
						}
						SDFEdge newEdge = clusterGraph.addEdge(seed, target);
						newEdge.copyProperties(edge);
					} else if(!block.contains(target)){
						SDFInterfaceVertex targetPort = new SDFSinkInterfaceVertex();
						targetPort.setName(cluster.getName() + "_"
								+ edge.getTargetInterface().getName());
						cluster.addSink(targetPort);
						SDFEdge extEdge = graph.addEdge(cluster, target);
						extEdge.copyProperties(edge);
						extEdge.setSourceInterface(targetPort);
						cluster.setInterfaceVertexExternalLink(extEdge, targetPort);
						SDFEdge newEdge = clusterGraph
								.addEdge(seed, targetPort);
						newEdge.copyProperties(edge);
						newEdge.setCons(new SDFIntEdgePropertyType(newEdge.getProd().intValue()));
					}
					graph.removeEdge(edge);
				}
				List<SDFEdge> incomingEdges = new ArrayList<SDFEdge>(graph
						.incomingEdgesOf(seed));
				for (SDFEdge edge : incomingEdges) {
					SDFAbstractVertex source = graph.getEdgeSource(edge);
					if(block.contains(source) && edge.getDelay().intValue() > 0){
						SDFInterfaceVertex targetPort = new SDFSinkInterfaceVertex();
						targetPort.setName("outLoopPort_"+nbLoopPort);
						SDFInterfaceVertex sourcePort = new SDFSourceInterfaceVertex();
						sourcePort.setName("inLoopPort_"+nbLoopPort);
						nbLoopPort ++ ;
						cluster.addSink(targetPort);
						cluster.addSource(sourcePort);
						
						SDFEdge loopEdge = graph.addEdge(cluster, cluster);
						loopEdge.copyProperties(edge);
						edge.setSourceInterface(targetPort);
						edge.setTargetInterface(sourcePort);
						
						SDFAbstractVertex target = graph.getEdgeTarget(edge);
						
						SDFEdge lastLoop = clusterGraph.addEdge(source, targetPort);
						lastLoop.copyProperties(edge);
						lastLoop.setDelay(new SDFIntEdgePropertyType(0));
						
						SDFEdge firstLoop = clusterGraph.addEdge(sourcePort, target);
						firstLoop.copyProperties(edge);
						firstLoop.setDelay(new SDFIntEdgePropertyType(0));
					}else if (!block.contains(source)) {
						SDFInterfaceVertex sourcePort = new SDFSourceInterfaceVertex();
						sourcePort.setName(cluster.getName() + "_"
								+ edge.getSourceInterface().getName());
						cluster.addSource(sourcePort);
						SDFEdge extEdge = graph.addEdge(source, cluster);
						extEdge.copyProperties(edge);
						extEdge.setTargetInterface(sourcePort);
						cluster.setInterfaceVertexExternalLink(extEdge, sourcePort);
						SDFEdge newEdge = clusterGraph
								.addEdge(sourcePort, seed);
						newEdge.copyProperties(edge);
						newEdge.setProd(newEdge.getCons());
						graph.removeEdge(edge);
					}
				}
			}
			for (int r = 0; r < block.size(); r++) {
				graph.removeVertex(block.get(r));
			}
			clusterGraph.validateModel();	
			cluster.setNbRepeat(pgcd);
			return cluster;
		}
		return null;
	}


}
