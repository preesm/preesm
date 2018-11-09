/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.dftools.algorithm.importer;

import org.ietr.dftools.algorithm.factories.DAGEdgeFactory;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Importer for DAG graphs.
 *
 * @author jpiat
 */
public class GMLDAGImporter extends GMLImporter<DirectedAcyclicGraph, DAGVertex, DAGEdge> {

  /**
   * Constructs a new DAG importer with the specified factories.
   */
  public GMLDAGImporter() {
    super(new DAGEdgeFactory());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parseEdge(org.w3c.dom.Element,
   * org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void parseEdge(final Element edgeElt, final DirectedAcyclicGraph parentGraph) {
    final String sourceId = edgeElt.getAttribute("source");
    final String targetId = edgeElt.getAttribute("target");
    final DAGVertex vertexSource = this.vertexFromId.get(sourceId);
    final DAGVertex vertexTarget = this.vertexFromId.get(targetId);

    final DAGEdge edge = parentGraph.addEdge(vertexSource, vertexTarget);

    parseKeys(edgeElt, edge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parseGraph(org.w3c.dom.Element)
   */
  @Override
  public DirectedAcyclicGraph parseGraph(final Element graphElt) {
    final DirectedAcyclicGraph graph = new DirectedAcyclicGraph(this.edgeFactory);
    parseKeys(graphElt, graph);
    final NodeList childList = graphElt.getChildNodes();
    parseParameters(graph, graphElt);
    parseVariables(graph, graphElt);
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("node")) {
        final Element vertexElt = (Element) childList.item(i);
        parseNode(vertexElt, graph);
      }
    }
    for (int i = 0; i < childList.getLength(); i++) {
      if (childList.item(i).getNodeName().equals("edge")) {
        final Element edgeElt = (Element) childList.item(i);
        parseEdge(edgeElt, graph);
      }
    }
    return graph;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parseNode(org.w3c.dom.Element,
   * org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public DAGVertex parseNode(final Element vertexElt, final DirectedAcyclicGraph parentGraph) {
    final DAGVertex vertex = new DAGVertex();
    final String id = vertexElt.getAttribute("id");
    vertex.setId(id);
    vertex.setName(id);
    parseKeys(vertexElt, vertex);
    parseArguments(vertex, vertexElt);
    parentGraph.addVertex(vertex);
    this.vertexFromId.put(vertex.getId(), vertex);
    return vertex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parsePort(org.w3c.dom.Element,
   * org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public DAGVertex parsePort(final Element portElt, final DirectedAcyclicGraph parentGraph) {
    return null;
  }

}
