/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.mapper.exporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.sf.dftools.algorithm.exporter.GMLExporter;
import net.sf.dftools.algorithm.model.AbstractGraph;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

import org.ietr.preesm.mapper.model.MapperDAG;
import org.w3c.dom.Element;

/**
 * Exporter for the DAG graph that enters the mapping process. This DAG can be
 * opened in Graphiti.
 * 
 * @author mpelcat
 * 
 */
public class DAGExporter extends GMLExporter<DAGVertex, DAGEdge> {

	/**
	 * Builds a new DAGExporter
	 */
	public DAGExporter() {
		super();
		/*
		 * addKey(SDFEdge.EDGE_PROD, SDFEdge.EDGE_PROD, "edge", "int",
		 * SDFNumericalEdgePropertyTypeFactory.class);
		 * addKey(SDFEdge.EDGE_DELAY, SDFEdge.EDGE_DELAY, "edge", "int",
		 * SDFNumericalEdgePropertyTypeFactory.class); addKey(SDFEdge.EDGE_CONS,
		 * SDFEdge.EDGE_CONS, "edge", "int",
		 * SDFNumericalEdgePropertyTypeFactory.class);
		 * addKey(SDFVertex.REFINEMENT, SDFVertex.REFINEMENT, "node", "string",
		 * null); addKey(SDFAbstractVertex.ARGUMENTS,
		 * SDFAbstractVertex.ARGUMENTS, "node", null, null);
		 * addKey(SDFEdge.DATA_TYPE, SDFEdge.DATA_TYPE, "edge", "string",
		 * SDFTextualEdgePropertyTypeFactory.class);
		 */
	}

	@Override
	protected Element exportEdge(DAGEdge edge, Element parentELement) {
		String sourcePort = "out";
		String targetPort = "in";
		Element edgeElt = createEdge(parentELement, edge.getSource().getName(),
				edge.getTarget().getName(), sourcePort, targetPort);
		exportKeys(edge, "edge", edgeElt);

		Element data = appendChild(edgeElt, "data");
		data.setAttribute("key", "edge_prod");
		data.setTextContent("0");
		data = appendChild(edgeElt, "data");
		data.setAttribute("key", "edge_delay");
		data.setTextContent("0");
		data = appendChild(edgeElt, "data");
		data.setAttribute("key", "edge_cons");
		data.setTextContent("0");
		data = appendChild(edgeElt, "data");
		data.setAttribute("key", "data_type");
		data.setTextContent("0");
		return edgeElt;
	}

	public Element exportGraph(AbstractGraph<DAGVertex, DAGEdge> graph) {
		addKeySet(rootElt);
		MapperDAG myGraph = (MapperDAG) graph;
		Element graphElt = createGraph(rootElt, true);
		graphElt.setAttribute("edgedefault", "directed");
		graphElt.setAttribute("kind", "sdf");
		exportKeys(myGraph, "graph", graphElt);
		if (myGraph.getParameters() != null) {
			exportParameters(myGraph.getParameters(), graphElt);
		}
		if (myGraph.getVariables() != null) {
			exportVariables(myGraph.getVariables(), graphElt);
		}
		for (DAGVertex child : myGraph.vertexSet()) {
			exportNode(child, graphElt);
		}
		for (DAGEdge edge : myGraph.edgeSet()) {
			exportEdge(edge, graphElt);
		}
		return null;
	}

	@Override
	protected Element exportNode(DAGVertex vertex, Element parentELement) {

		Element vertexElt = createNode(parentELement, vertex.getName());
		vertexElt.setAttribute(SDFAbstractVertex.KIND, "vertex");

		exportKeys(vertex, "node", vertexElt);

		Element data = appendChild(vertexElt, "data");
		data.setAttribute("key", "graph_desc");
		data = appendChild(vertexElt, "data");
		data.setAttribute("key", "arguments");
		return vertexElt;
	}

	public void export(AbstractGraph<DAGVertex, DAGEdge> graph, String path) {
		this.path = path;
		try {
			exportGraph(graph);
			transform(new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected Element exportPort(DAGVertex interfaceVertex,
			Element parentELement) {
		return null;
	}

}
