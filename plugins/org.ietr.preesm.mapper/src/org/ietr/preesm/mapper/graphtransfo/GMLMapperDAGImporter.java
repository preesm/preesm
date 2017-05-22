/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

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

// TODO: Auto-generated Javadoc
/**
 * Importer for mapper DAG graphs.
 *
 * @author mpelcat
 */
public class GMLMapperDAGImporter extends GMLImporter<MapperDAG, MapperDAGVertex, MapperDAGEdge> {

  /** The local factory. */
  MapperEdgeFactory localFactory = null;

  /**
   * Constructs a new DAG importer with the specified factories.
   */
  public GMLMapperDAGImporter() {
    super(null);
    this.localFactory = new MapperEdgeFactory();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parseEdge(org.w3c.dom.Element, org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void parseEdge(final Element edgeElt, final MapperDAG parentGraph) {
    final DAGVertex vertexSource = this.vertexFromId.get(edgeElt.getAttribute("source"));
    final DAGVertex vertexTarget = this.vertexFromId.get(edgeElt.getAttribute("target"));

    final DAGEdge edge = parentGraph.addEdge(vertexSource, vertexTarget);

    parseKeys(edgeElt, edge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parseGraph(org.w3c.dom.Element)
   */
  @Override
  public MapperDAG parseGraph(final Element graphElt) {
    final MapperDAG graph = new MapperDAG(this.localFactory, null);
    parseKeys(graphElt, graph);
    graph.setReferenceSdfGraph((SDFGraph) graph.getPropertyBean().getValue(ImplementationPropertyNames.Graph_SdfReferenceGraph));
    final NodeList childList = graphElt.getChildNodes();
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
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parsePort(org.w3c.dom.Element, org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public MapperDAGVertex parsePort(final Element portElt, final MapperDAG parentGraph) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.importer.GMLImporter#parseNode(org.w3c.dom.Element, org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public MapperDAGVertex parseNode(final Element vertexElt, final MapperDAG parentGraph) {
    final MapperDAGVertex vertex = new MapperDAGVertex();
    parentGraph.addVertex(vertex);
    vertex.setId(vertexElt.getAttribute("id"));
    this.vertexFromId.put(vertex.getId(), vertex);
    parseKeys(vertexElt, vertex);
    return vertex;
  }

}
