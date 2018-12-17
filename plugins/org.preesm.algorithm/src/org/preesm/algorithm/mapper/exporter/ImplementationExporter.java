/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2009 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.algorithm.mapper.exporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import org.preesm.algorithm.io.gml.GMLExporter;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.property.VertexInit;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.GMLKey;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.scenario.types.ImplementationPropertyNames;
import org.preesm.model.slam.attributes.Parameter;
import org.preesm.model.slam.impl.ComponentInstanceImpl;
import org.preesm.model.slam.route.AbstractRouteStep;
import org.w3c.dom.Element;

/**
 * Exporter for the mapper DAG graph that represents the implementation. The attributes contain every information on the
 * deployment. It should not be displayed right away by Graphiti and its purpose is to be transformed into another
 * tool's input
 *
 * @author mpelcat
 *
 */
public class ImplementationExporter extends GMLExporter<DAGVertex, DAGEdge> {

  /**
   * Builds a new Implementation Exporter.
   */
  public ImplementationExporter() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportEdge(org.ietr.dftools.algorithm.model. AbstractEdge,
   * org.w3c.dom.Element)
   */
  @Override
  protected Element exportEdge(final DAGEdge edge, final Element parentELement) {
    return createEdge(parentELement, edge.getSource().getId(), edge.getTarget().getId());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportGraph(org.ietr.dftools.algorithm.model. AbstractGraph)
   */
  @Override
  public Element exportGraph(final AbstractGraph<DAGVertex, DAGEdge> graph) {
    try {
      addKeySet(this.rootElt);
      final MapperDAG myGraph = (MapperDAG) graph;
      final Element graphElt = createGraph(this.rootElt, true);
      graphElt.setAttribute("edgedefault", "directed");
      exportKeys(myGraph, "graph", graphElt);
      for (final DAGVertex child : myGraph.vertexSet()) {
        exportNode(child, graphElt);
      }

      for (final DAGEdge edge : myGraph.edgeSet()) {
        exportEdge(edge, graphElt);
      }
      return graphElt;

    } catch (final Exception e) {
      throw new PreesmException("Could not export graph", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportNode(org.ietr.dftools.algorithm.model. AbstractVertex,
   * org.w3c.dom.Element)
   */
  @Override
  protected Element exportNode(final DAGVertex vertex, final Element parentELement) {
    // Pre modification for xml export
    final PropertyBean vtxBeans = vertex.getPropertyBean();
    if ((vtxBeans.getValue("originalId") != null) && (vertex instanceof MapperDAGVertex)) {
      final MapperDAGVertex mapperDagVtx = (MapperDAGVertex) vertex;
      final VertexInit init = mapperDagVtx.getInit();
      final MapperDAGVertex parentVertex = init.getParentVertex();
      parentVertex.setId(vtxBeans.getValue("originalId").toString());
    }

    final String vtxType = vtxBeans.getValue(ImplementationPropertyNames.Vertex_vertexType).toString();
    vertex.setKind(vtxType);

    final Element vertexElt = createNode(parentELement, vertex.getId());
    exportKeys(vertex, "vertex", vertexElt);

    if (vertex instanceof TransferVertex) {
      // Adding route step to the node
      final AbstractRouteStep routeStep = (AbstractRouteStep) vtxBeans
          .getValue(ImplementationPropertyNames.SendReceive_routeStep);
      // Add the Operator_address key
      if (routeStep != null) {
        String memAddress = null;
        final Element operatorAdress = this.domDocument.createElement("data");
        final Iterator<
            Parameter> iter = ((ComponentInstanceImpl) vtxBeans.getValue("Operator")).getParameters().iterator();
        while (iter.hasNext()) {
          final Parameter param = iter.next();
          if (param.getKey().equals("memoryAddress")) {
            memAddress = param.getValue();
            break;
          }
        }

        if (memAddress != null) {
          operatorAdress.setAttribute("key", "Operator_address");
          operatorAdress.setTextContent(memAddress);
          vertexElt.appendChild(operatorAdress);

          addKey("Operator_address", new GMLKey("Operator_address", "vertex", "string", null));
        }

        exportRouteStep(routeStep, vertexElt);
      }

    }
    return vertexElt;
  }

  /**
   * Export route step.
   *
   * @param step
   *          the step
   * @param vertexElt
   *          the vertex elt
   */
  private void exportRouteStep(final AbstractRouteStep step, final Element vertexElt) {
    step.appendRouteStep(this.domDocument, vertexElt);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportPort(org.ietr.dftools.algorithm.model. AbstractVertex,
   * org.w3c.dom.Element)
   */
  @Override
  protected Element exportPort(final DAGVertex interfaceVertex, final Element parentELement) {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#export(org.ietr.dftools.algorithm.model. AbstractGraph,
   * java.lang.String)
   */
  @Override
  public void export(final AbstractGraph<DAGVertex, DAGEdge> graph, final String path) {
    this.path = path;
    try {
      exportGraph(graph);
      transform(new FileOutputStream(path));
    } catch (final FileNotFoundException e) {
      throw new PreesmException("could not export implementation", e);
    }
  }

}
