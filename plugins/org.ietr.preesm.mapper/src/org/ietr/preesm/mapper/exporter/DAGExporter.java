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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.exporter.GMLExporter;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFEndVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
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

	// Map to keep the number of ports for each DAGVertex
	private Map<DAGVertex, Integer> inPortNb;
	private Map<DAGVertex, Integer> outPortNb;

	/**
	 * Builds a new DAGExporter
	 */
	public DAGExporter() {
		super();
	}

	@Override
	protected Element exportNode(DAGVertex vertex, Element parentELement) {

		Element vertexElt = createNode(parentELement, vertex.getName());
		String kind;
		if (vertex.getKind() == null)
			kind = "vertex";
		else {
			switch (vertex.getKind()) {
			case DAGVertex.DAG_VERTEX:
				kind = "vertex";
				break;
			case DAGBroadcastVertex.DAG_BROADCAST_VERTEX:
				kind = SDFBroadcastVertex.BROADCAST;
				break;
			case DAGEndVertex.DAG_END_VERTEX:
				kind = SDFEndVertex.END;
				break;
			case DAGForkVertex.DAG_FORK_VERTEX:
				kind = SDFForkVertex.FORK;
				break;
			case DAGInitVertex.DAG_INIT_VERTEX:
				kind = SDFInitVertex.INIT;
				break;
			case DAGJoinVertex.DAG_JOIN_VERTEX:
				kind = SDFJoinVertex.JOIN;
				break;
			default:
				kind = "vertex";
			}
		}
		vertexElt.setAttribute(SDFAbstractVertex.KIND, kind);

		exportKeys(vertex, "node", vertexElt);

		Element data = appendChild(vertexElt, "data");
		data.setAttribute("key", "graph_desc");
		data = appendChild(vertexElt, "data");
		data.setAttribute("key", "arguments");
		return vertexElt;
	}

	@Override
	protected Element exportEdge(DAGEdge edge, Element parentELement) {
		// TODO: add port number (maps from vertex to int?)
		String sourcePort = getOutPortName(edge.getSource());
		String targetPort = getInPortName(edge.getTarget());
		Element edgeElt = createEdge(parentELement, edge.getSource().getName(),
				edge.getTarget().getName(), sourcePort, targetPort);
		exportKeys(edge, "edge", edgeElt);

		Element data = appendChild(edgeElt, "data");
		data.setAttribute("key", "edge_prod");

		if (edge.getWeight() != null) {
			data.setTextContent(edge.getWeight().toString());
		} else {
			data.setTextContent("0");
		}

		data = appendChild(edgeElt, "data");
		data.setAttribute("key", "edge_delay");
		data.setTextContent("0");

		data = appendChild(edgeElt, "data");
		data.setAttribute("key", "edge_cons");

		if (edge.getWeight() != null) {
			data.setTextContent(edge.getWeight().toString());
		} else {
			data.setTextContent("0");
		}

		data = appendChild(edgeElt, "data");
		data.setAttribute("key", "data_type");
		data.setTextContent("memUnit");

		return edgeElt;
	}

	public Element exportGraph(AbstractGraph<DAGVertex, DAGEdge> graph) {
		// Instantiate maps
		inPortNb = new HashMap<DAGVertex, Integer>();
		outPortNb = new HashMap<DAGVertex, Integer>();

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

	public void export(AbstractGraph<DAGVertex, DAGEdge> graph, String path) {
		this.path = path;
		try {
			exportGraph(graph);
			transform(new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected Element exportPort(DAGVertex interfaceVertex,
			Element parentELement) {
		return null;
	}

	public void exportDAG(DirectedAcyclicGraph dag, Path path) {
		// XXX: Why are cloning the dag for a simple serialization (we should
		// not modify the dag)?
		MapperDAG mapperDag = (MapperDAG) dag;

		MapperDAG clone = mapperDag.clone();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile iGraphMLFile = workspace.getRoot().getFile(path);

		if (iGraphMLFile.getLocation() != null) {
			this.export(clone, iGraphMLFile.getLocation().toOSString());
		} else {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"The output file " + path + " can not be written.");
		}
	}

	private String getOutPortName(DAGVertex vertex) {
		if (!(outPortNb.containsKey(vertex)))
			outPortNb.put(vertex, 0);
		int nb = outPortNb.get(vertex);
		String result = "out" + nb;
		nb++;
		outPortNb.put(vertex, nb);
		return result;
	}

	private String getInPortName(DAGVertex vertex) {
		if (!(inPortNb.containsKey(vertex)))
			inPortNb.put(vertex, 0);
		int nb = inPortNb.get(vertex);
		String result = "in" + nb;
		nb++;
		inPortNb.put(vertex, nb);
		return result;
	}

}
