/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.plugin.mapper.exporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
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
		addKey(Operator.propertyBeanName, Operator.propertyBeanName, "vertex", "string",
				DAGDefaultEdgePropertyType.class);
		addKey(Medium.propertyBeanName, Medium.propertyBeanName, "vertex", "string",
				DAGDefaultEdgePropertyType.class);
		addKey("schedulingOrder", "schedulingOrder", "vertex", "int",
				DAGDefaultEdgePropertyType.class);
		addKey("dataSize", "dataSize", "vertex", "int",
				DAGDefaultEdgePropertyType.class);
		addKey("duration", "duration", "vertex", "int",
				DAGDefaultEdgePropertyType.class);
		addKey("senderGraphName", "senderGraphName", "vertex", "string",
				DAGDefaultEdgePropertyType.class);
		addKey("receiverGraphName", "receiverGraphName", "vertex", "string",
				DAGDefaultEdgePropertyType.class);
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
