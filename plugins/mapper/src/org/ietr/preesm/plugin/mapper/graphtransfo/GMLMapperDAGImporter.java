package org.ietr.preesm.plugin.mapper.graphtransfo;

import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.MapperEdgeFactory;
import org.sdf4j.importer.GMLImporter;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Importer for mapper DAG graphs
 * 
 * @author mpelcat
 * 
 */
public class GMLMapperDAGImporter extends
		GMLImporter<MapperDAG, MapperDAGVertex, MapperDAGEdge> {
	
	MapperEdgeFactory localFactory = null;
	
	/**
	 * Constructs a new DAG importer with the specified factories
	 */
	public GMLMapperDAGImporter() {
		super(null);
		localFactory = new MapperEdgeFactory();
	}

	@Override
	public void parseEdge(Element edgeElt, MapperDAG parentGraph) {
		DAGVertex vertexSource = vertexFromId.get(edgeElt
				.getAttribute("source"));
		DAGVertex vertexTarget = vertexFromId.get(edgeElt
				.getAttribute("target"));

		DAGEdge edge = parentGraph.addEdge(vertexSource, vertexTarget);

		parseKeys(edgeElt, edge.getPropertyBean(), "edge") ;
	}

	@Override
	public MapperDAG parseGraph(Element graphElt) {
		MapperDAG graph = new MapperDAG(localFactory,null);
		parseKeys(graphElt, graph.getPropertyBean(), "graph");
		graph.setReferenceSdfGraph((SDFGraph)graph.getPropertyBean().getValue("SdfReferenceGraph"));
		NodeList childList = graphElt.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			if (childList.item(i).getNodeName().equals("node")) {
				Element vertexElt = (Element) childList.item(i);
				graph.addVertex(parseNode(vertexElt));
			}
		}
		for (int i = 0; i < childList.getLength(); i++) {
			if (childList.item(i).getNodeName().equals("edge")) {
				Element edgeElt = (Element) childList.item(i);
				parseEdge(edgeElt, graph);
			}
		}
		return graph;
	}

	@Override
	public MapperDAGVertex parsePort(Element portElt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapperDAGVertex parseNode(Element vertexElt) {
		MapperDAGVertex vertex = new MapperDAGVertex();
		vertex.setId(vertexElt.getAttribute("id"));
		vertexFromId.put(vertex.getId(), vertex);
		parseKeys(vertexElt, vertex.getPropertyBean(), "vertex");
		return vertex;
	}
	

}
