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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.w3c.dom.Element;

/**
 * This class represent a GML exporter for SDF.
 *
 * @author jpiat
 */
public class GMLSDFExporter extends GMLExporter<SDFAbstractVertex, SDFEdge> {

  /** The path. */
  private String gmlPath;

  /**
   * Creates a new Instance of GMLExporter.
   */
  public GMLSDFExporter() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#export(org.ietr.dftools.algorithm.model.AbstractGraph,
   * java.lang.String)
   */
  @Override
  public void export(final AbstractGraph<SDFAbstractVertex, SDFEdge> graph, final String path) {
    this.gmlPath = path;
    try (FileOutputStream out = new FileOutputStream(path)) {
      exportGraph(graph);
      transform(out);
    } catch (final IOException e) {
      throw new DFToolsAlgoException("Could not export graph", e);
    }
  }

  /**
   * Export an Edge in the Document.
   *
   * @param edge
   *          The edge to export
   * @param parentELement
   *          The DOM document parent Element
   * @return the element
   */
  @Override
  protected Element exportEdge(final SDFEdge edge, final Element parentELement) {
    String sourcePort = "";
    String targetPort = "";
    if (edge.getSourceInterface() != null) {
      sourcePort = edge.getSourceInterface().getName();
    }
    if (edge.getTargetInterface() != null) {
      targetPort = edge.getTargetInterface().getName();
    }
    final Element edgeElt = createEdge(parentELement, edge.getSource().getName(), edge.getTarget().getName(),
        sourcePort, targetPort);
    exportKeys(edge, "edge", edgeElt);
    return edgeElt;
  }

  /**
   * Exports a Graph in the DOM document.
   *
   * @param graph
   *          The graph to export
   * @return the element
   */
  @Override
  public Element exportGraph(final AbstractGraph<SDFAbstractVertex, SDFEdge> graph) {
    addKeySet(this.rootElt);
    final SDFGraph myGraph = (SDFGraph) graph;
    final Element graphElt = createGraph(this.rootElt, true);
    graphElt.setAttribute("edgedefault", "directed");
    graphElt.setAttribute("kind", "sdf");
    exportKeys(graph, "graph", graphElt);
    if (myGraph.getParameters() != null) {
      exportParameters(myGraph.getParameters(), graphElt);
    }
    if (myGraph.getVariables() != null) {
      exportVariables(myGraph.getVariables(), graphElt);
    }
    for (final SDFAbstractVertex child : myGraph.vertexSet()) {
      exportNode(child, graphElt);
    }
    for (final SDFEdge edge : myGraph.edgeSet()) {
      exportEdge(edge, graphElt);
    }
    return graphElt;
  }

  /**
   * Exports a Vertex in the DOM document.
   *
   * @param vertex
   *          The vertex to export
   * @param parentELement
   *          The parent Element in the DOM document
   * @return the element
   */
  @Override
  @SuppressWarnings("unchecked")
  protected Element exportNode(final SDFAbstractVertex vertex, final Element parentELement) {

    final Element vertexElt = createNode(parentELement, vertex.getName());
    if (vertex instanceof SDFInterfaceVertex) {
      vertexElt.setAttribute("port_direction", ((SDFInterfaceVertex) vertex).getDirection().toString());
    }

    if ((vertex.getGraphDescription() != null) && (vertex.getGraphDescription().getName().length() > 0)) {
      String filePath = vertex.getGraphDescription().getName();
      if (!filePath.contains(".graphml")) {
        filePath = filePath + ".graphml";
        vertex.getGraphDescription().setName(filePath);
      }
      filePath = filePath.replace(File.separator, "/");
      final String thisPathPrefix = this.gmlPath.substring(0, this.gmlPath.lastIndexOf(File.separator) + 1);

      if ((filePath.lastIndexOf('/') > 0) && filePath.contains(thisPathPrefix)) {
        if (filePath.compareTo(thisPathPrefix) > 0) {
          vertex.getGraphDescription()
              .setName(filePath.substring(filePath.length() - filePath.compareTo(thisPathPrefix)));
          final GMLSDFExporter decExporter = new GMLSDFExporter();
          decExporter.export(vertex.getGraphDescription(),
              filePath.substring(filePath.length() - filePath.compareTo(thisPathPrefix)));
        }
      } else {
        final GMLSDFExporter decExporter = new GMLSDFExporter();
        decExporter.export(vertex.getGraphDescription(), thisPathPrefix + filePath);
      }
    }
    exportKeys(vertex, "node", vertexElt);
    if (vertex.getArguments() != null) {
      exportArguments(vertex.getArguments(), vertexElt);
    }
    return vertexElt;
  }

  /**
   * Exports an interface.
   *
   * @param interfaceVertex
   *          The interface to export
   * @param parentELement
   *          The DOM parent Element of this Interface
   * @return the element
   */
  @Override
  protected Element exportPort(final SDFAbstractVertex interfaceVertex, final Element parentELement) {
    final Element interfaceElt = createPort(parentELement, interfaceVertex.getName());
    interfaceElt.setAttribute("port_direction", ((SDFInterfaceVertex) interfaceVertex).getDirection().toString());
    exportKeys(interfaceVertex, "port", interfaceElt);
    return interfaceElt;
  }

}
