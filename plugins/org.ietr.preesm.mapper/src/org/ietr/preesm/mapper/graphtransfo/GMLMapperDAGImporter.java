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

package org.ietr.preesm.mapper.graphtransfo;

import org.ietr.dftools.algorithm.importer.GMLImporter;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.MapperEdgeFactory;
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

		parseKeys(edgeElt, edge);
	}

	@Override
	public MapperDAG parseGraph(Element graphElt) {
		MapperDAG graph = new MapperDAG(localFactory, null);
		parseKeys(graphElt, graph);
		graph.setReferenceSdfGraph((SDFGraph) graph.getPropertyBean().getValue(
				ImplementationPropertyNames.Graph_SdfReferenceGraph));
		NodeList childList = graphElt.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			if (childList.item(i).getNodeName().equals("node")) {
				Element vertexElt = (Element) childList.item(i);
				parseNode(vertexElt, graph);
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
	public MapperDAGVertex parsePort(Element portElt, MapperDAG parentGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MapperDAGVertex parseNode(Element vertexElt, MapperDAG parentGraph) {
		MapperDAGVertex vertex = new MapperDAGVertex();
		parentGraph.addVertex(vertex);
		vertex.setId(vertexElt.getAttribute("id"));
		vertexFromId.put(vertex.getId(), vertex);
		parseKeys(vertexElt, vertex);
		return vertex;
	}

}
