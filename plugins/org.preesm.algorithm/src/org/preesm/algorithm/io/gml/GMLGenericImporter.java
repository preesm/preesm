/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
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

import java.util.List;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.AbstractVertex;
import org.preesm.algorithm.model.IInterface;
import org.preesm.algorithm.model.factories.ModelGraphFactory;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class GMLGenericImporter.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GMLGenericImporter extends GMLImporter<AbstractGraph, AbstractVertex> {

  /**
   * Parses an Edge in the DOM document.
   *
   * @param edgeElt
   *          The DOM Element
   * @param parentGraph
   *          The parent Graph of this Edge
   * @throws PreesmException
   *           the invalid model exception
   */
  @Override
  public void parseEdge(final Element edgeElt, final AbstractGraph parentGraph) {
    final AbstractVertex vertexSource = this.vertexFromId.get(edgeElt.getAttribute("source"));
    final AbstractVertex vertexTarget = this.vertexFromId.get(edgeElt.getAttribute("target"));

    IInterface sourcePort = null;
    IInterface targetPort = null;
    final String sourcePortName = edgeElt.getAttribute("sourceport");
    for (final IInterface sinksPort : (List<IInterface>) vertexSource.getInterfaces()) {
      if (sinksPort.getName().equals(sourcePortName)) {
        sourcePort = sinksPort;
      }
    }
    if (sourcePort == null) {
      sourcePort = this.vertexFactory.createInterface(sourcePortName, 1);
      vertexSource.addInterface(sourcePort);
    }
    final String targetPortName = edgeElt.getAttribute("targetport");
    for (final IInterface sourcesPort : (List<IInterface>) vertexTarget.getInterfaces()) {
      if (sourcesPort.getName().equals(targetPortName)) {
        targetPort = sourcesPort;
      }
    }

    if (targetPort == null) {
      targetPort = this.vertexFactory.createInterface(targetPortName, 0);
      vertexTarget.addInterface(targetPort);
    }
    final AbstractEdge edge = parentGraph.addEdge(vertexSource, sourcePort, vertexTarget, targetPort);
    parseKeys(edgeElt, edge);
  }

  /**
   * Parses a Graph in the DOM document.
   *
   * @param graphElt
   *          The graph Element in the DOM document
   * @return The parsed graph
   * @throws PreesmException
   *           the invalid model exception
   */
  @Override
  public AbstractGraph parseGraph(final Element graphElt) {
    final String parseModel = parseModel(graphElt);
    AbstractGraph graph;
    try {
      graph = ModelGraphFactory.getModel(parseModel);
      this.vertexFactory = graph.getVertexFactory();
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
    } catch (final Exception e) {
      throw new PreesmRuntimeException("Failed to parse graph with message :" + e.getMessage());
    }
  }

  /**
   * Parses a Vertex from the DOM document.
   *
   * @param vertexElt
   *          The node Element in the DOM document
   * @param parentGraph
   *          the parent graph
   * @return The parsed node
   * @throws PreesmException
   *           the invalid model exception
   */
  @Override
  public AbstractVertex parseNode(final Element vertexElt, final AbstractGraph parentGraph) {
    AbstractVertex vertex;
    vertex = this.vertexFactory.createVertex(vertexElt, null);
    vertex.setId(vertexElt.getAttribute("id"));
    vertex.setName(vertexElt.getAttribute("id"));
    parseKeys(vertexElt, vertex);
    parentGraph.addVertex(vertex);
    this.vertexFromId.put(vertex.getId(), vertex);
    parseArguments(vertex, vertexElt);
    parseGraphDescription(vertex, vertexElt);
    return vertex;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parsePort(org.w3c.dom.Element,
   * org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public AbstractVertex parsePort(final Element portElt, final AbstractGraph parentGraph) {
    return null;
  }

}
