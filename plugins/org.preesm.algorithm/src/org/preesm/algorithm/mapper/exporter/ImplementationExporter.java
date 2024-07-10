/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Jonathan Piat [jpiat@laas.fr] (2009 - 2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.io.gml.GMLExporter;
import org.preesm.algorithm.mapper.graphtransfo.ImplementationPropertyNames;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.property.VertexInit;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.PropertyBean;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.GMLKey;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.SlamDMARouteStep;
import org.preesm.model.slam.SlamMemoryRouteStep;
import org.preesm.model.slam.SlamMessageRouteStep;
import org.preesm.model.slam.SlamRouteStep;
import org.preesm.model.slam.SlamRouteStepType;
import org.preesm.model.slam.impl.ComponentInstanceImpl;
import org.w3c.dom.Document;
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
      throw new PreesmRuntimeException("Could not export graph", e);
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

    final String vtxType = vtxBeans.getValue(ImplementationPropertyNames.VERTEX_VERTEX_TYPE).toString();
    vertex.setKind(vtxType);

    final Element vertexElt = createNode(parentELement, vertex.getId());
    exportKeys(vertex, "vertex", vertexElt);

    if (vertex instanceof TransferVertex) {
      // Adding route step to the node
      final SlamRouteStep routeStep = vtxBeans.getValue(ImplementationPropertyNames.SEND_RECEIVE_ROUTE_STEP);
      // Add the Operator_address key
      if (routeStep != null) {
        String memAddress = null;
        final Element operatorAdress = this.domDocument.createElement("data");
        final EMap<String, String> parameters = (vtxBeans.<ComponentInstanceImpl>getValue("Operator")).getParameters();

        memAddress = parameters.get("memoryAddress");

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
  private void exportRouteStep(final SlamRouteStep step, final Element comFct) {
    final Document dom = this.domDocument;

    final Element routeStep = dom.createElement("routeStep");
    comFct.appendChild(routeStep);

    final Element newSender = dom.createElement("sender");
    newSender.setAttribute("name", step.getSender().getInstanceName());
    newSender.setAttribute("def", step.getSender().getComponent().getVlnv().getName());
    routeStep.appendChild(newSender);

    final Element newReceiver = dom.createElement("receiver");
    newReceiver.setAttribute("name", step.getReceiver().getInstanceName());
    newReceiver.setAttribute("def", step.getReceiver().getComponent().getVlnv().getName());
    routeStep.appendChild(newReceiver);

    if (SlamRouteStepType.DMA_TYPE.equals(step.getType())) {
      routeStep.setAttribute("type", "dma");
      final SlamDMARouteStep dStep = (SlamDMARouteStep) step;
      routeStep.setAttribute("name", dStep.getDma().getInstanceName());
      routeStep.setAttribute("dmaDef", dStep.getDma().getComponent().getVlnv().getName());

      for (final ComponentInstance node : dStep.getNodes()) {
        final Element eNode = dom.createElement("node");
        eNode.setAttribute("name", node.getInstanceName());
        eNode.setAttribute("def", node.getComponent().getVlnv().getName());
        routeStep.appendChild(eNode);
      }
    } else if (SlamRouteStepType.NODE_TYPE.equals(step.getType())) {
      routeStep.setAttribute("type", "msg");
      final SlamMessageRouteStep nStep = (SlamMessageRouteStep) step;

      for (final ComponentInstance node : nStep.getNodes()) {
        final Element eNode = dom.createElement("node");
        eNode.setAttribute("name", node.getInstanceName());
        eNode.setAttribute("def", node.getComponent().getVlnv().getName());
        routeStep.appendChild(eNode);
      }
    } else if (SlamRouteStepType.MEM_TYPE.equals(step.getType())) {
      routeStep.setAttribute("type", "ram");
      final SlamMemoryRouteStep rStep = (SlamMemoryRouteStep) step;
      routeStep.setAttribute("name", rStep.getMemory().getInstanceName());
      routeStep.setAttribute("ramDef", rStep.getMemory().getComponent().getVlnv().getName());
    }
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
      throw new PreesmRuntimeException("could not export implementation", e);
    }
  }

}
