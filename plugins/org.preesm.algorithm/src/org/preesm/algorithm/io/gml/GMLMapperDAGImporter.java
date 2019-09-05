/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2012)
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
package org.preesm.algorithm.io.gml;

import org.preesm.algorithm.model.factories.SDFVertexFactory;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Importer for DAG graphs produced by the mapper.
 *
 * @author mpelcat
 */
public class GMLMapperDAGImporter extends GMLImporter<SDFGraph, SDFAbstractVertex> {

  /**
   * Parses an Edge in the DOM document.
   *
   * @param edgeElt
   *          The DOM Element
   * @param parentGraph
   *          The parent Graph of this Edge
   */
  @Override
  public void parseEdge(final Element edgeElt, final SDFGraph parentGraph) {
    // not needed
  }

  /**
   * Parses a Graph in the DOM document.
   *
   * @param graphElt
   *          The graph Element in the DOM document
   * @return The parsed graph
   */
  @Override
  public SDFGraph parseGraph(final Element graphElt) {
    final SDFGraph graph = new SDFGraph();
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
    parseKeys(graphElt, graph);
    return graph;
  }

  /**
   * Parses a Vertex from the DOM document.
   *
   * @param vertexElt
   *          The node Element in the DOM document
   * @param parentGraph
   *          the parent graph
   * @return The parsed node
   */
  @Override
  public SDFAbstractVertex parseNode(final Element vertexElt, final SDFGraph parentGraph) {

    SDFAbstractVertex vertex;
    vertex = SDFVertexFactory.getInstance().createVertex(vertexElt, null);

    vertex.setId(vertexElt.getAttribute("id"));

    for (int i = 0; i < vertexElt.getChildNodes().getLength(); i++) {
      final Node n = vertexElt.getChildNodes().item(i);
      if (n.getNodeName().equals("data")) {
        vertex.getPropertyBean().setValue(n.getAttributes().getNamedItem("key").getTextContent(), n.getTextContent());
      }
    }

    parseKeys(vertexElt, vertex);
    this.vertexFromId.put(vertex.getId(), vertex);
    parseArguments(vertex, vertexElt);
    return vertex;
  }

  @Override
  public SDFAbstractVertex parsePort(final Element portElt, final SDFGraph parentGraph) {
    return null;
  }

}
