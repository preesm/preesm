/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2010 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2010 - 2012)
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
package org.ietr.preesm.mapper.exporter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.ietr.dftools.algorithm.exporter.GMLExporter;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFEndVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.w3c.dom.Element;

// TODO: Auto-generated Javadoc
/**
 * Exporter for the DAG graph that enters the mapping process. This DAG can be opened in Graphiti.
 *
 * @author mpelcat
 *
 */
public class DAGExporter extends GMLExporter<DAGVertex, DAGEdge> {

  /** The in port nb. */
  // Map to keep the number of ports for each DAGVertex
  private Map<DAGVertex, Integer> inPortNb;

  /** The out port nb. */
  private Map<DAGVertex, Integer> outPortNb;

  /**
   * Builds a new DAGExporter.
   */
  public DAGExporter() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportNode(org.ietr.dftools.algorithm.model. AbstractVertex, org.w3c.dom.Element)
   */
  @Override
  protected Element exportNode(final DAGVertex vertex, final Element parentELement) {

    final Element vertexElt = createNode(parentELement, vertex.getName());
    String kind;
    if (vertex.getKind() == null) {
      kind = "vertex";
    } else {
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
    vertexElt.setAttribute(AbstractVertex.KIND, kind);

    exportKeys(vertex, "node", vertexElt);

    Element data = appendChild(vertexElt, "data");
    data.setAttribute("key", "graph_desc");
    data = appendChild(vertexElt, "data");
    data.setAttribute("key", "arguments");
    return vertexElt;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportEdge(org.ietr.dftools.algorithm.model. AbstractEdge, org.w3c.dom.Element)
   */
  @Override
  protected Element exportEdge(final DAGEdge edge, final Element parentELement) {
    // TODO: add port number (maps from vertex to int?)
    final String sourcePort = getOutPortName(edge.getSource());
    final String targetPort = getInPortName(edge.getTarget());
    final Element edgeElt = createEdge(parentELement, edge.getSource().getName(), edge.getTarget().getName(), sourcePort, targetPort);
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

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportGraph(org.ietr.dftools.algorithm.model. AbstractGraph)
   */
  @Override
  public Element exportGraph(final AbstractGraph<DAGVertex, DAGEdge> graph) {
    // Instantiate maps
    this.inPortNb = new HashMap<>();
    this.outPortNb = new HashMap<>();

    addKeySet(this.rootElt);
    final MapperDAG myGraph = (MapperDAG) graph;
    final Element graphElt = createGraph(this.rootElt, true);
    graphElt.setAttribute("edgedefault", "directed");
    graphElt.setAttribute("kind", "sdf");
    exportKeys(myGraph, "graph", graphElt);
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
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#export(org.ietr.dftools.algorithm.model. AbstractGraph, java.lang.String)
   */
  @Override
  public void export(final AbstractGraph<DAGVertex, DAGEdge> graph, final String path) {
    this.path = path;
    try {
      exportGraph(graph);
      transform(new FileOutputStream(path));
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.exporter.GMLExporter#exportPort(org.ietr.dftools.algorithm.model. AbstractVertex, org.w3c.dom.Element)
   */
  @Override
  protected Element exportPort(final DAGVertex interfaceVertex, final Element parentELement) {
    return null;
  }

  /**
   * Export DAG.
   *
   * @param dag
   *          the dag
   * @param path
   *          the path
   */
  public void exportDAG(final DirectedAcyclicGraph dag, final IPath path) {
    // XXX: Why are cloning the dag for a simple serialization (we should
    // not modify the dag)?
    final MapperDAG mapperDag = (MapperDAG) dag;

    final MapperDAG clone = mapperDag.clone();
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IFile iGraphMLFile = workspace.getRoot().getFile(path);

    if (iGraphMLFile.getLocation() != null) {
      export(clone, iGraphMLFile.getLocation().toOSString());
    } else {
      WorkflowLogger.getLogger().log(Level.SEVERE, "The output file " + path + " can not be written.");
    }
  }

  /**
   * Gets the out port name.
   *
   * @param vertex
   *          the vertex
   * @return the out port name
   */
  private String getOutPortName(final DAGVertex vertex) {
    if (!(this.outPortNb.containsKey(vertex))) {
      this.outPortNb.put(vertex, 0);
    }
    int nb = this.outPortNb.get(vertex);
    final String result = "out" + nb;
    nb++;
    this.outPortNb.put(vertex, nb);
    return result;
  }

  /**
   * Gets the in port name.
   *
   * @param vertex
   *          the vertex
   * @return the in port name
   */
  private String getInPortName(final DAGVertex vertex) {
    if (!(this.inPortNb.containsKey(vertex))) {
      this.inPortNb.put(vertex, 0);
    }
    int nb = this.inPortNb.get(vertex);
    final String result = "in" + nb;
    nb++;
    this.inPortNb.put(vertex, nb);
    return result;
  }

}
