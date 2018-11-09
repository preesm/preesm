/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.dftools.algorithm.exporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.w3c.dom.Element;

/**
 * GML exporter for dag.
 *
 * @author jpiat
 */
public class GMLDAGExporter extends GMLExporter<DAGVertex, DAGEdge> {

  /**
   * Builds a new GMLDAGExporter.
   */
  public GMLDAGExporter() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#export(org.ietr.dftools.algorithm.model.AbstractGraph,
   * java.lang.String)
   */
  @Override
  public void export(final AbstractGraph<DAGVertex, DAGEdge> graph, final String path) {
    this.path = path;
    try {
      exportGraph(graph);
      transform(new FileOutputStream(path));
    } catch (final FileNotFoundException e) {
      throw new DFToolsAlgoException("Could not export graph", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportEdge(org.ietr.dftools.algorithm.model.AbstractEdge,
   * org.w3c.dom.Element)
   */
  @Override
  protected Element exportEdge(final DAGEdge edge, final Element parentELement) {
    final Element edgeElt = createEdge(parentELement, edge.getSource().getId(), edge.getTarget().getId());
    exportKeys(edge, "edge", edgeElt);
    return edgeElt;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportGraph(org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public Element exportGraph(final AbstractGraph<DAGVertex, DAGEdge> graph) {
    try {
      addKeySet(this.rootElt);
      final DirectedAcyclicGraph myGraph = (DirectedAcyclicGraph) graph;
      final Element graphElt = createGraph(this.rootElt, true);
      graphElt.setAttribute("edgedefault", "directed");
      exportKeys(graph, "graph", graphElt);
      if (myGraph.getParameters() != null) {
        exportParameters(myGraph.getParameters(), graphElt);
      }
      if (myGraph.getVariables() != null) {
        exportVariables(myGraph.getVariables(), graphElt);
      }
      for (final DAGVertex child : myGraph.vertexSet()) {
        exportNode(child, graphElt);
      }

      for (final DAGEdge edge : myGraph.edgeSet()) {
        exportEdge(edge, graphElt);
      }
      return graphElt;
    } catch (final Exception e) {
      throw new DFToolsAlgoException("Could not export graph", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportNode(org.ietr.dftools.algorithm.model.AbstractVertex,
   * org.w3c.dom.Element)
   */
  @Override
  protected Element exportNode(final DAGVertex vertex, final Element parentELement) {
    final Element vertexElt = createNode(parentELement, vertex.getId());
    exportKeys(vertex, "vertex", vertexElt);
    if (vertex.getArguments() != null) {
      exportArguments(vertex.getArguments(), vertexElt);
    }
    return vertexElt;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportPort(org.ietr.dftools.algorithm.model.AbstractVertex,
   * org.w3c.dom.Element)
   */
  @Override
  protected Element exportPort(final DAGVertex interfaceVertex, final Element parentELement) {
    return null;
  }

}
