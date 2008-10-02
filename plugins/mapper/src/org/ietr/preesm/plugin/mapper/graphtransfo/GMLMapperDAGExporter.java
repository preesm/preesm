package org.ietr.preesm.plugin.mapper.graphtransfo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.codegen.VertexType;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.jgrapht.Graph;
import org.sdf4j.exporter.GMLExporter;
import org.sdf4j.model.dag.DAGDefaultEdgePropertyType;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFDefaultEdgePropertyType;
import org.w3c.dom.Element;

/**
 * Exporter for mapper DAG graphs
 * 
 * @author mpelcat
 * 
 */
public class GMLMapperDAGExporter extends GMLExporter<DAGVertex, DAGEdge> {

	/**
	 * Builds a new GMLDAGExporter
	 */
	public GMLMapperDAGExporter() {
		super();
		addKey(DAGVertex.NAME, DAGVertex.NAME, "vertex", "string", String.class);
		addKey(VertexType.propertyBeanName, VertexType.propertyBeanName, "vertex", "string", String.class);
		addKey(Operator.propertyBeanName, Operator.propertyBeanName, "vertex", "int",
				DAGDefaultEdgePropertyType.class);
		addKey(Medium.propertyBeanName, Medium.propertyBeanName, "vertex", "int",
				SDFDefaultEdgePropertyType.class);
		addKey("schedulingOrder", "schedulingOrder", "vertex", "int",
				SDFDefaultEdgePropertyType.class);
	}

	@Override
	protected void exportEdge(DAGEdge edge, Element parentELement) {
		Element edgeElt = createEdge(parentELement, edge.getSource().getId(),
				edge.getTarget().getId());
		exportKeys("edge", edgeElt, edge.getPropertyBean());
	}

	@Override
	public void exportGraph(Graph<DAGVertex, DAGEdge> graph, OutputStream out) {
		try {
			addKeySet(rootElt);
			MapperDAG myGraph = (MapperDAG) graph;
			Element graphElt = createGraph(rootElt, true);
			graphElt.setAttribute("edgedefault", "directed");
			exportKeys("graph", graphElt, myGraph.getPropertyBean());
			for (DAGVertex child : myGraph.vertexSet()) {
				exportNode(child, graphElt);
			}

			for (DAGEdge edge : myGraph.edgeSet()) {
				exportEdge(edge, graphElt);
			}
			transform(out);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void exportNode(DAGVertex vertex, Element parentELement) {
		Element vertexElt = createNode(parentELement, vertex.getId());
		exportKeys("vertex", vertexElt, vertex.getPropertyBean());
	}

	@Override
	protected void exportPort(DAGVertex interfaceVertex, Element parentELement) {
		// TODO Auto-generated method stub
	}

	@Override
	public void export(Graph<DAGVertex, DAGEdge> graph, String path) {
		this.path = path ;
		try {
			exportGraph(graph, new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
