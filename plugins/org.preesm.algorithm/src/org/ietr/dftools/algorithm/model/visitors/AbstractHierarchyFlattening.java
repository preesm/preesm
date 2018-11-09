/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.dftools.algorithm.model.visitors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.NoIntegerValueException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;

/**
 * HierarchyFlattening for a given depth.
 *
 * @author jpiat
 * @param <G>
 *          the generic type
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractHierarchyFlattening<G extends AbstractGraph> {

  /** The output. */
  protected G output;

  /**
   * Gives this visitor output (The flattened graph).
   *
   * @return The output of the visitor
   */
  public G getOutput() {
    return this.output;
  }

  /**
   * Treat the source interface to ensure that there exist only one outgoing connection.
   *
   * @param vertex
   *          the vertex
   * @param parentGraph
   *          the parent graph
   * @param depth
   *          the depth
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  protected abstract void treatSourceInterface(AbstractVertex vertex, AbstractGraph parentGraph, int depth);

  /**
   * Treat the sink interface to ensure that there exist only one incoming connection.
   *
   * @param vertex
   *          the vertex
   * @param parentGraph
   *          the parent graph
   * @param depth
   *          the depth
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  protected abstract void treatSinkInterface(AbstractVertex vertex, AbstractGraph parentGraph, int depth);

  /**
   * Flatten one vertex given it's parent.
   *
   * @param vertex
   *          The vertex to flatten
   * @param parentGraph
   *          The new parent graph
   * @param depth
   *          the depth
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  private void treatVertex(final AbstractVertex vertex, final G parentGraph) {
    final List<SDFAbstractVertex> vertices = new ArrayList<>(vertex.getGraphDescription().vertexSet());
    final Map<AbstractVertex, AbstractVertex> matchCopies = new LinkedHashMap<>();
    for (int i = 0; i < vertices.size(); i++) {
      if (!(vertices.get(i) instanceof IInterface)) {
        final AbstractVertex trueVertex = vertices.get(i);
        final AbstractVertex cloneVertex = vertices.get(i).copy();
        parentGraph.addVertex(cloneVertex);
        matchCopies.put(trueVertex, cloneVertex);
        cloneVertex.copyProperties(trueVertex);
        cloneVertex.setName(vertex.getName() + "_" + cloneVertex.getName());
        if (trueVertex.getArguments() != null) {
          for (final Argument arg : trueVertex.getArguments().values()) {
            try {
              cloneVertex.getArgument(arg.getName()).setValue(String.valueOf(arg.longValue()));
            } catch (final NoIntegerValueException e) {
              throw new DFToolsAlgoException("Could not clone value", e);
            }
          }
        }
      }
    }
    final List<AbstractEdge> edges = new ArrayList<>(vertex.getGraphDescription().edgeSet());
    for (int i = 0; i < edges.size(); i++) {
      AbstractVertex sourceVertex = null;
      AbstractVertex targetVertex = null;
      if (edges.get(i).getSource() instanceof IInterface) {
        final IInterface sourceInterface = (IInterface) edges.get(i).getSource();
        if (vertex.getAssociatedEdge(sourceInterface) != null) {
          sourceVertex = vertex.getAssociatedEdge(sourceInterface).getSource();
          edges.get(i).setSourceLabel(vertex.getAssociatedEdge(sourceInterface).getSourceLabel());
        }

      } else {
        sourceVertex = matchCopies.get(edges.get(i).getSource());
      }
      if (edges.get(i).getTarget() instanceof IInterface) {
        final SDFInterfaceVertex targetInterface = (SDFInterfaceVertex) edges.get(i).getTarget();
        if (vertex.getAssociatedEdge(targetInterface) != null) {
          targetVertex = vertex.getAssociatedEdge(targetInterface).getTarget();
          edges.get(i).setTargetLabel(vertex.getAssociatedEdge(targetInterface).getTargetLabel());
        }

      } else {
        targetVertex = matchCopies.get(edges.get(i).getTarget());
      }
      if ((sourceVertex != null) && (targetVertex != null)) {
        final AbstractEdge newEdge = parentGraph.addEdge(sourceVertex, targetVertex);
        newEdge.copyProperties(edges.get(i));
      }
    }

  }

  /**
   * Flatten the hierarchy of the given graph to the given depth.
   *
   * @param sdf
   *          The graph to flatten
   * @param depth
   *          The depth to flatten the graph
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  public void flattenGraph(final G sdf, final int depth) throws SDF4JException {
    if (depth > 0) {
      final int newDepth = depth - 1;
      this.output = (G) sdf.copy();
      final List<AbstractVertex> vertices = new ArrayList<>(this.output.vertexSet());
      for (int i = 0; i < vertices.size(); i++) {
        if (vertices.get(i).getGraphDescription() != null) {
          try {
            treatVertex(vertices.get(i), this.output);
          } catch (final InvalidExpressionException e) {
            throw (new SDF4JException(e.getMessage()));
          }
          this.output.removeVertex(vertices.get(i));
        }
      }
      flattenGraph(this.output, newDepth);
    }
  }

  /**
   * Prepare hierarchy.
   *
   * @param vertex
   *          the vertex
   * @param depth
   *          the depth
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  protected void prepareHierarchy(final AbstractVertex vertex, final int depth) {
    final List<AbstractVertex> vertices = new ArrayList<>(vertex.getGraphDescription().vertexSet());
    for (int i = 0; i < vertices.size(); i++) {
      if (vertices.get(i) instanceof IInterface) {
        if (vertex.getGraphDescription().incomingEdgesOf(vertices.get(i)).isEmpty()) {
          treatSourceInterface(vertices.get(i), vertex.getGraphDescription(), depth);
        } else if (vertex.getGraphDescription().outgoingEdgesOf(vertices.get(i)).isEmpty()) {
          treatSinkInterface(vertices.get(i), vertex.getGraphDescription(), depth);
        }
      }
    }
  }

}
