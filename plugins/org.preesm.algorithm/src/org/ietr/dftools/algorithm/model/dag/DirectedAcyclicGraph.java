/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.dftools.algorithm.model.dag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.ietr.dftools.algorithm.exceptions.CreateCycleException;
import org.ietr.dftools.algorithm.exceptions.CreateMultigraphException;
import org.ietr.dftools.algorithm.factories.DAGEdgeFactory;
import org.ietr.dftools.algorithm.factories.DAGVertexFactory;
import org.ietr.dftools.algorithm.factories.IModelVertexFactory;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.cycle.CycleDetector;

/**
 * Class used to represent a Directed Acyclic Graph.
 *
 * @author jpiat
 * @author kdesnos
 */
public class DirectedAcyclicGraph extends AbstractGraph<DAGVertex, DAGEdge> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3860891539321306793L;

  /** The Constant SDF. */
  private static final String SDF = "sdf";

  /**
   * Constructs a new DAG graph with the default Dag edge factory.
   */
  public DirectedAcyclicGraph() {
    super(new DAGEdgeFactory());
    getPropertyBean().setValue(AbstractGraph.MODEL, "dag");
  }

  /**
   * Creates a new DirectedAcyclicGraph with the given Edge factory.
   *
   * @param arg0
   *          The factory to use to create Edge in this graph
   */
  public DirectedAcyclicGraph(final EdgeFactory<DAGVertex, DAGEdge> arg0) {
    super(arg0);
    getPropertyBean().setValue(AbstractGraph.MODEL, "dag");
  }

  @Override
  public Set<DAGEdge> getAllEdges(DAGVertex sourceVertex, DAGVertex targetVertex) {
    if (!containsVertex(sourceVertex)) {
      throw new IllegalArgumentException("Graph does not contain source vertex '" + sourceVertex + "'");
    }
    if (!containsVertex(targetVertex)) {
      throw new IllegalArgumentException("Graph does not contain target vertex '" + targetVertex + "'");
    }
    return super.getAllEdges(sourceVertex, targetVertex);
  }

  /**
   * Add an Edge to this Graph.
   *
   * @param source
   *          The source vertex of this edge
   * @param target
   *          The target vertex of this edge
   * @return The created Edge
   * @throws CreateMultigraphException
   *           This Edge creates a Multi-graph
   * @throws CreateCycleException
   *           This Edge creates a cycle
   */
  public DAGEdge addDAGEdge(final DAGVertex source, final DAGVertex target) {
    Set<DAGEdge> allEdges = getAllEdges(source, target);
    if (!allEdges.isEmpty()) {
      throw new CreateMultigraphException("There should be no edge existing.");
    } else {
      final DAGEdge newEdge = addEdge(source, target);
      final CycleDetector<DAGVertex, DAGEdge> detector = new CycleDetector<>(this);
      if (detector.detectCyclesContainingVertex(source)) {
        final Set<DAGVertex> cycle = detector.findCyclesContainingVertex(source);
        StringBuilder cycleString = new StringBuilder("Added edge forms a cycle: {");
        for (final DAGVertex vertex : cycle) {
          cycleString.append(vertex.getName() + " ");
        }
        cycleString.append("}");

        this.removeEdge(newEdge);
        throw new CreateCycleException(cycleString.toString());
      } else if (detector.detectCyclesContainingVertex(target)) {
        final Set<DAGVertex> cycle = detector.findCyclesContainingVertex(target);
        StringBuilder cycleString = new StringBuilder("Added edge forms a cycle: {");
        for (final DAGVertex vertex : cycle) {
          cycleString.append(vertex.getName() + " ");
        }
        cycleString.append("}");

        this.removeEdge(newEdge);
        throw new CreateCycleException(cycleString.toString());
      }
      return newEdge;
    }
  }

  /**
   * Gives the DAGVertex with the given name in the graph.
   *
   * @param name
   *          The name of the vertex we want to obtain
   * @return The DAG vertex with the given name
   */
  @Override
  public DAGVertex getVertex(final String name) {
    for (final DAGVertex vertex : vertexSet()) {
      if (vertex.getName().equals(name)) {
        return vertex;
      }
    }
    return null;
  }

  /**
   * Test if this graph respect the DAG rules.
   *
   * @return trues if this graph is DAG compliant
   */
  public boolean isDAG() {
    final CycleDetector<DAGVertex, DAGEdge> detector = new CycleDetector<>(this);
    return !detector.detectCycles();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#clone()
   */
  @Override
  public DirectedAcyclicGraph copy() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#validateModel(java.util.logging.Logger)
   */
  @Override
  public boolean validateModel() {
    return true;
  }

  /**
   * Gets the corresponding SDF graph.
   *
   * @return the corresponding SDF graph
   */
  public SDFGraph getCorrespondingSDFGraph() {
    return getPropertyBean().getValue(DirectedAcyclicGraph.SDF);
  }

  /**
   * Sets the corresponding SDF graph.
   *
   * @param graph
   *          the new corresponding SDF graph
   */
  public void setCorrespondingSDFGraph(final SDFGraph graph) {
    getPropertyBean().setValue(DirectedAcyclicGraph.SDF, graph);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#getVertexFactory()
   */
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public IModelVertexFactory getVertexFactory() {
    return DAGVertexFactory.getInstance();
  }

  /**
   * Returns a set containing all the {@link DAGVertex} that are successors to the given {@link DAGVertex}.
   *
   * @param vertex
   *          the {@link DAGVertex} whose successor vertex list is retrieved
   * @return A {@link Set} containing all {@link DAGVertex} that are successors to the given {@link DAGVertex}
   */
  public Set<DAGVertex> getSuccessorVerticesOf(final DAGVertex vertex) {
    // Create a list of the vertex to process
    final List<DAGVertex> toProcess = new ArrayList<>();
    final Set<DAGVertex> processed = new LinkedHashSet<>();
    toProcess.add(vertex);

    // While there is a vertex in the toProcess list
    while (!toProcess.isEmpty()) {
      final DAGVertex processedVertex = toProcess.remove(0);
      processed.add(processedVertex);

      // Add all its successors vertices to the toProcess list (unless
      // they were already added or processed)
      for (final DAGEdge outEdge : outgoingEdgesOf(processedVertex)) {
        final DAGVertex target = outEdge.getTarget();
        if (!toProcess.contains(target) && !processed.contains(target)) {
          toProcess.add(target);
        }
      }
    }

    processed.remove(vertex);
    return processed;
  }

  /**
   * Returns a set containing all the edges that are successors to the given {@link DAGVertex}.
   *
   * @param vertex
   *          the {@link DAGVertex} whose successor edge list is retrieved
   * @return A {@link Set} containing all {@link DAGEdge} that are successors to the given {@link DAGVertex}
   */
  public Set<DAGEdge> getSuccessorEdgesOf(final DAGVertex vertex) {
    final Set<DAGEdge> result = new LinkedHashSet<>();

    // Create a list of the vertex to process
    final List<DAGVertex> toProcess = new ArrayList<>();
    final Set<DAGVertex> processed = new LinkedHashSet<>();
    toProcess.add(vertex);

    // While there is a vertex in the toProcess list
    while (!toProcess.isEmpty()) {
      final DAGVertex processedVertex = toProcess.remove(0);
      processed.add(processedVertex);

      // Add all its outgoing edges to the result
      final Set<DAGEdge> outgoingEdges = processedVertex.outgoingEdges();
      result.addAll(outgoingEdges);

      // Add all its successors vertices to the toProcess list (unless
      // they were already added or processed)
      for (final DAGEdge outEdge : outgoingEdges) {
        final DAGVertex target = outEdge.getTarget();
        if (!toProcess.contains(target) && !processed.contains(target)) {
          toProcess.add(target);
        }
      }
    }

    return result;
  }

  /**
   * Returns a set containing all the {@link DAGVertex} that are predecessors to the given {@link DAGVertex}.
   *
   * @param vertex
   *          the {@link DAGVertex} whose predecessor vertex list is retrieved
   * @return A {@link Set} containing all {@link DAGVertex} that are predecessor to the given {@link DAGVertex}
   */
  public Set<DAGVertex> getPredecessorVerticesOf(final DAGVertex vertex) {
    // Create a list of the vertex to process
    final List<DAGVertex> toProcess = new ArrayList<>();
    final Set<DAGVertex> processed = new LinkedHashSet<>();
    toProcess.add(vertex);

    // While there is a vertex in the toProcess list
    while (!toProcess.isEmpty()) {
      final DAGVertex processedVertex = toProcess.remove(0);
      processed.add(processedVertex);

      // Add all its predecessors vertices to the toProcess list (unless
      // they were already added or processed)
      for (final DAGEdge inEdge : incomingEdgesOf(processedVertex)) {
        final DAGVertex source = inEdge.getSource();
        if (!toProcess.contains(source) && !processed.contains(source)) {
          toProcess.add(source);
        }
      }
    }

    processed.remove(vertex);
    return processed;
  }

  /**
   * Returns a set containing all the edges that are predecessors to the given {@link DAGVertex}.
   *
   * @param vertex
   *          the {@link DAGVertex} whose predecessor edge list is retrieved
   * @return A {@link Set} containing all {@link DAGEdge} that are predecessor to the given {@link DAGVertex}
   */
  public Set<DAGEdge> getPredecessorEdgesOf(final DAGVertex vertex) {
    final Set<DAGEdge> result = new LinkedHashSet<>();

    // Create a list of the vertex to process
    final List<DAGVertex> toProcess = new ArrayList<>();
    final Set<DAGVertex> processed = new LinkedHashSet<>();
    toProcess.add(vertex);

    // While there is a vertex in the toProcess list
    while (!toProcess.isEmpty()) {
      final DAGVertex processedVertex = toProcess.remove(0);
      processed.add(processedVertex);

      // Add all its incoming edges to the result
      final Set<DAGEdge> incomingEdges = processedVertex.incomingEdges();
      result.addAll(incomingEdges);

      // Add all its predecessors vertices to the toProcess list (unless
      // they were already added or processed)
      for (final DAGEdge inEdge : incomingEdges) {
        final DAGVertex source = inEdge.getSource();
        if (!toProcess.contains(source) && !processed.contains(source)) {
          toProcess.add(source);
        }
      }
    }

    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getFactoryForProperty(java.lang.String)
   */
  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    return null;
  }

}
