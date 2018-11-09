/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2011 - 2012)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.dftools.algorithm.model.sdf.visitors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.exceptions.CreateCycleException;
import org.ietr.dftools.algorithm.exceptions.CreateMultigraphException;
import org.ietr.dftools.algorithm.factories.IModelVertexFactory;
import org.ietr.dftools.algorithm.iterators.SDFIterator;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFEndVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.LongVertexPropertyType;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.WorkflowException;
import org.jgrapht.alg.cycle.CycleDetector;

/**
 * Visitor to use to transform a SDF Graph in a Directed Acyclic Graph.
 *
 * @author pthebault
 * @author kdesnos
 * @param <T>
 *          The DAG type of the output dag
 */
public class DAGTransformation<T extends DirectedAcyclicGraph>
    implements IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge> {

  /** The output graph. */
  private final T outputGraph;

  /** The factory. */
  private final IModelVertexFactory<DAGVertex> factory;

  /**
   * Builds a new DAGTransformation visitor,.
   *
   * @param outputGraph
   *          The graph in which the DAG will be output
   * @param vertexFactory
   *          The factory used to create vertices
   */
  public DAGTransformation(final T outputGraph, final IModelVertexFactory<DAGVertex> vertexFactory) {
    this.outputGraph = outputGraph;
    this.factory = vertexFactory;
  }

  /**
   * Copy the cycles nb times in the graph.
   *
   * @param graph
   *          The graph in which the cycle should be copied
   * @param vertices
   *          The set of vertices of the cycle
   * @param nb
   *          The number of copy to produce
   */
  private void copyCycle(final SDFGraph graph, final Set<SDFAbstractVertex> vertices, final long nb) {
    SDFAbstractVertex root = null;
    SDFAbstractVertex last = null;
    SDFEdge loop = null;
    for (final SDFAbstractVertex vertex : vertices) {
      vertex.setNbRepeat(vertex.getNbRepeatAsLong() / nb);
      for (final SDFEdge edge : graph.incomingEdgesOf(vertex)) {
        if (edge.getDelay().longValue() > 0) {
          root = edge.getTarget();
          last = edge.getSource();
          loop = edge;
        }
      }
    }
    final Map<SDFAbstractVertex, List<SDFAbstractVertex>> mapCopies = new LinkedHashMap<>();
    final List<SDFAbstractVertex> createdVertices = new ArrayList<>();
    final List<SDFAbstractVertex> sortedCycle = new ArrayList<>();
    final SDFIterator iterator = new SDFIterator(graph, root);
    while (iterator.hasNext()) {
      final SDFAbstractVertex next = iterator.next();
      if (vertices.contains(next) && !sortedCycle.contains(next)) {
        sortedCycle.add(next);
      }
      if (next == last) {
        break;
      }
    }
    if ((root != null) && (last != null)) {
      SDFAbstractVertex previous = last;
      SDFAbstractVertex previousCopy = last;
      for (int i = 1; i < nb; i++) {
        for (final SDFAbstractVertex current : sortedCycle) {
          final SDFAbstractVertex copy = current.copy();
          if (mapCopies.get(current) == null) {
            mapCopies.put(current, new ArrayList<SDFAbstractVertex>());
          }
          mapCopies.get(current).add(copy);
          createdVertices.add(copy);
          copy.setName(copy.getName() + "_" + i);
          graph.addVertex(copy);
          for (final SDFEdge edge : graph.getAllEdges(previous, current)) {
            final SDFEdge newEdge = graph.addEdge(previousCopy, copy);
            newEdge.copyProperties(edge);
            if (newEdge.getDelay().longValue() > 0) {
              newEdge.setDelay(new LongEdgePropertyType(0));
            }
          }
          for (final SDFEdge edge : graph.incomingEdgesOf(current)) {
            if ((edge.getSource() != previous) && !sortedCycle.contains(edge.getSource())
                && !createdVertices.contains(edge.getSource())) {
              final SDFEdge newEdge = graph.addEdge(edge.getSource(), copy);
              newEdge.copyProperties(edge);
              edge.setProd(new LongEdgePropertyType(edge.getCons().longValue()));
            } else if ((edge.getSource() != previous) && sortedCycle.contains(edge.getSource())
                && !createdVertices.contains(edge.getSource())) {
              final SDFEdge newEdge = graph.addEdge(mapCopies.get(edge.getSource()).get(i - 1), copy);
              newEdge.copyProperties(edge);
            }
          }
          final List<SDFEdge> edges = new ArrayList<>(graph.outgoingEdgesOf(current));
          for (int k = 0; k < edges.size(); k++) {
            final SDFEdge edge = edges.get(k);
            if (!sortedCycle.contains(edge.getTarget()) && !createdVertices.contains(edge.getTarget())) {
              final SDFEdge newEdge = graph.addEdge(copy, edge.getTarget());
              newEdge.copyProperties(edge);
              edge.setCons(new LongEdgePropertyType(edge.getProd().longValue()));
            }
          }
          previousCopy = copy;
          previous = current;
        }
      }
    }
    final SDFInitVertex initVertex = new SDFInitVertex();
    initVertex.setName(loop.getTarget().getName() + "_init_" + loop.getTargetInterface().getName());
    final SDFSinkInterfaceVertex sinkInit = new SDFSinkInterfaceVertex();
    sinkInit.setName(loop.getSourceInterface().getName());
    initVertex.addSink(sinkInit);
    initVertex.setNbRepeat(1L);
    graph.addVertex(initVertex);

    final SDFEndVertex endVertex = new SDFEndVertex();
    endVertex.setName(loop.getSource().getName() + "_end_" + loop.getSourceInterface().getName());
    final SDFSourceInterfaceVertex sourceEnd = new SDFSourceInterfaceVertex();
    sourceEnd.setName(loop.getTargetInterface().getName());
    endVertex.addSource(sourceEnd);
    endVertex.setNbRepeat(1L);
    initVertex.setEndReference(endVertex);
    initVertex.setInitSize(loop.getDelay().longValue());
    endVertex.setEndReference(initVertex);
    graph.addVertex(endVertex);

    final SDFEdge initEdge = graph.addEdge(initVertex, loop.getTarget());
    initEdge.copyProperties(loop);
    initEdge.setSourceInterface(sinkInit);
    initEdge.setDelay(new LongEdgePropertyType(0));

    final SDFEdge endEdge = graph.addEdge(createdVertices.get(createdVertices.size() - 1), endVertex);
    endEdge.copyProperties(loop);
    endEdge.setTargetInterface(sourceEnd);
    endEdge.setDelay(new LongEdgePropertyType(0));
    graph.removeEdge(loop);
  }

  /**
   * Gcd of vertices vrb.
   *
   * @param vertices
   *          the vertices
   * @return the int
   */
  private long gcdOfVerticesVrb(final Set<SDFAbstractVertex> vertices) {
    long gcd = 0;
    for (final SDFAbstractVertex vertex : vertices) {
      if (gcd == 0) {
        gcd = vertex.getNbRepeatAsLong();
      } else {
        gcd = ArithmeticUtils.gcd(gcd, vertex.getNbRepeatAsLong());
      }
    }
    return gcd;
  }

  /**
   * GIves this visitor output.
   *
   * @return The output of the visitor
   */
  public T getOutput() {
    return this.outputGraph;
  }

  /**
   * Transforms top.
   *
   * @param graph
   *          the graph
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  private void transformsTop(final SDFGraph graph) {
    try {
      if (graph.validateModel()) {
        // insertImplodeExplodesVertices(graph)
        this.outputGraph.copyProperties(graph);
        this.outputGraph.setCorrespondingSDFGraph(graph);
        for (final DAGVertex vertex : this.outputGraph.vertexSet()) {
          vertex.setNbRepeat(new LongVertexPropertyType(graph.getVertex(vertex.getName()).getNbRepeatAsLong()));
        }

        for (final SDFEdge edge : graph.edgeSet()) {
          if (edge.getDelay().longValue() == 0) {
            try {
              createEdge(edge);
            } catch (final CreateMultigraphException | CreateCycleException e) {
              throw new WorkflowException(
                  "Error in the DAG creation. Check the single-rate SDF to identify where delays are missing", e);
            }
          }
        }
      }
    } catch (final InvalidExpressionException e) {
      throw (new SDF4JException(e.getMessage()));
    }
  }

  /**
   * Create the dag edge associated with the current SDF edge
   *
   * @param edge
   *          the SDF edge
   * @throws CreateMultigraphException
   *           the CreateMultigraphException exception
   * @throws CreateCycleException
   *           the CreateCycleException exception
   */
  private void createEdge(final SDFEdge edge) {
    final SDFAbstractVertex edgeSource = edge.getSource();
    final SDFAbstractVertex edgeTarget = edge.getTarget();
    final DAGVertex source = this.outputGraph.getVertex(edgeSource.getName());
    final DAGVertex target = this.outputGraph.getVertex(edgeTarget.getName());

    if (source == null) {
      throw new DFToolsAlgoException(
          "Output DAG does not contain edge source vertex '" + edgeSource + "' from input SDF.");
    }

    if (target == null) {
      throw new DFToolsAlgoException(
          "Output DAG does not contain edge target vertex '" + edgeTarget + "' from input SDF.");
    }

    final DAGEdge dagEdge;
    // Checks if the DAG edge already exists in the graph
    // If so update its weight
    final long weight = computeEdgeWeight(edge);
    if (this.outputGraph.containsEdge(source, target)) {
      dagEdge = this.outputGraph.getEdge(source, target);
      dagEdge.setWeight(new LongEdgePropertyType(weight + dagEdge.getWeight().longValue()));
    } else {
      dagEdge = this.outputGraph.addDAGEdge(source, target);
      dagEdge.setWeight(new LongEdgePropertyType(weight));
    }

    // Creates an edge to aggregate
    final DAGEdge newEdge = new DAGEdge();
    newEdge.setPropertyValue(SDFEdge.DATA_TYPE, edge.getDataType().toString());
    newEdge.setPropertyValue(SDFEdge.DATA_SIZE, edge.getDataSize().longValue());
    newEdge.setSourcePortModifier(edge.getSourcePortModifier());
    newEdge.setTargetPortModifier(edge.getTargetPortModifier());
    newEdge.setWeight(new LongEdgePropertyType(weight / edge.getDataSize().longValue()));
    newEdge.setSourceLabel(edge.getSourceLabel());
    newEdge.setTargetLabel(edge.getTargetLabel());
    newEdge.setPropertyValue(SDFEdge.BASE, this.outputGraph);
    newEdge.setContainingEdge(dagEdge);

    dagEdge.getAggregate().add(newEdge);
  }

  /**
   * Compute edge weight.
   *
   * @param edge
   *          the edge
   * @return the int
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  long computeEdgeWeight(final SDFEdge edge) {
    final long weight = edge.getCons().longValue() * edge.getTarget().getNbRepeatAsLong();
    final long dataSize = edge.getDataSize().longValue();
    return weight * dataSize;
  }

  /**
   * Treat the cycles in the graph.
   *
   * @param graph
   *          The graph to treat
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  private void treatCycles(final SDFGraph graph) {
    final List<Set<SDFAbstractVertex>> cycles = new ArrayList<>();
    final CycleDetector<SDFAbstractVertex, SDFEdge> detector = new CycleDetector<>(graph);
    final List<SDFAbstractVertex> vertices = new ArrayList<>(graph.vertexSet());
    while (!vertices.isEmpty()) {
      final SDFAbstractVertex vertex = vertices.get(0);
      final Set<SDFAbstractVertex> cycle = detector.findCyclesContainingVertex(vertex);
      if (!cycle.isEmpty()) {
        vertices.removeAll(cycle);
        cycles.add(cycle);
      }
      vertices.remove(vertex);
    }

    for (final Set<SDFAbstractVertex> cycle : cycles) {
      // This code is dumb for single-rate SDF.
      // Since in a single-rate graph, all actors are fired
      // exactly once
      final long gcd = gcdOfVerticesVrb(cycle);
      if (gcd > 1) {
        copyCycle(graph, cycle, gcd);
      } else {
        treatSDFCycles(graph, cycle);
      }
    }

  }

  /**
   * Treat SDF cycles.
   *
   * @param graph
   *          the graph
   * @param cycle
   *          the cycle
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  protected void treatSDFCycles(final SDFGraph graph, final Set<SDFAbstractVertex> cycle) {
    final List<SDFEdge> loops = new ArrayList<>();
    for (final SDFAbstractVertex vertex : cycle) {
      for (final SDFEdge edge : graph.incomingEdgesOf(vertex)) {
        if (edge.getDelay().longValue() > 0) {
          loops.add(edge);
        }
      }
    }
    for (final SDFEdge loop : loops) {
      final SDFInitVertex initVertex = new SDFInitVertex();
      initVertex.setName(loop.getTarget().getName() + "_init_" + loop.getTargetInterface().getName());
      final SDFSinkInterfaceVertex sinkInit = new SDFSinkInterfaceVertex();
      sinkInit.setName("init_out");
      initVertex.addSink(sinkInit);
      initVertex.setNbRepeat(1L);
      graph.addVertex(initVertex);

      final SDFEndVertex endVertex = new SDFEndVertex();
      endVertex.setName(loop.getSource().getName() + "_end_" + loop.getSourceInterface().getName());
      final SDFSourceInterfaceVertex sourceEnd = new SDFSourceInterfaceVertex();
      sourceEnd.setName("end_in");
      endVertex.addSource(sourceEnd);
      endVertex.setNbRepeat(1L);
      initVertex.setEndReference(endVertex);
      initVertex.setInitSize(loop.getDelay().longValue());
      endVertex.setEndReference(initVertex);
      graph.addVertex(endVertex);

      final SDFEdge initEdge = graph.addEdge(initVertex, loop.getTarget());
      initEdge.copyProperties(loop);
      initEdge.setSourceInterface(sinkInit);
      initEdge.setDelay(new LongEdgePropertyType(0));

      final SDFEdge endEdge = graph.addEdge(loop.getSource(), endVertex);
      endEdge.copyProperties(loop);
      endEdge.setTargetInterface(sourceEnd);
      endEdge.setDelay(new LongEdgePropertyType(0));
      graph.removeEdge(loop);
    }
  }

  /**
   * Treat delays.
   *
   * @param graph
   *          the graph
   */
  public void treatDelays(final SDFGraph graph) {
    final ArrayList<SDFEdge> edges = new ArrayList<>(graph.edgeSet());
    while (!edges.isEmpty()) {
      final SDFEdge edge = edges.get(0);
      try {
        if (edge.getDelay().longValue() > 0) {
          final SDFInitVertex initVertex = new SDFInitVertex();
          initVertex.setName(edge.getTarget().getName() + "_init_" + edge.getTargetInterface().getName());
          final SDFSinkInterfaceVertex sinkInit = new SDFSinkInterfaceVertex();
          sinkInit.setName("init_out");
          initVertex.addSink(sinkInit);
          initVertex.setNbRepeat(1L);
          graph.addVertex(initVertex);

          final SDFEndVertex endVertex = new SDFEndVertex();
          endVertex.setName(edge.getSource().getName() + "_end_" + edge.getSourceInterface().getName());
          final SDFSourceInterfaceVertex sourceEnd = new SDFSourceInterfaceVertex();
          sourceEnd.setName("end_in");
          endVertex.addSource(sourceEnd);
          endVertex.setNbRepeat(1L);
          initVertex.setEndReference(endVertex);
          initVertex.setInitSize(edge.getDelay().longValue());
          endVertex.setEndReference(initVertex);
          graph.addVertex(endVertex);

          final SDFEdge initEdge = graph.addEdge(initVertex, edge.getTarget());
          initEdge.copyProperties(edge);
          initEdge.setSourceInterface(sinkInit);
          initEdge.setDelay(new LongEdgePropertyType(0));
          // initEdge.setProd(edge.getDelay())

          final SDFEdge endEdge = graph.addEdge(edge.getSource(), endVertex);
          endEdge.copyProperties(edge);
          endEdge.setTargetInterface(sourceEnd);
          endEdge.setDelay(new LongEdgePropertyType(0));
          graph.removeEdge(edge);
        }
      } catch (final InvalidExpressionException e) {
        throw new DFToolsAlgoException("Could not treat delays", e);
      }
      edges.remove(0);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void visit(final SDFEdge sdfEdge) {
    // Empty constructor
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void visit(final SDFGraph sdf) {
    sdf.insertBroadcasts();
    try {

      int k = 5;
      while (k-- > 0) {
        treatCycles(sdf);
        treatDelays(sdf);
      }

      final ArrayList<SDFAbstractVertex> vertices = new ArrayList<>(sdf.vertexSet());
      for (int i = 0; i < vertices.size(); i++) {
        final SDFAbstractVertex sdfAbstractVertex = vertices.get(i);
        sdfAbstractVertex.accept(this);
      }
      sdf.getPropertyBean().setValue("schedulable", true);
      transformsTop(sdf);
    } catch (final InvalidExpressionException e) {
      throw (new SDF4JException(e.getMessage()));
    }

    // Make sure all ports are in order
    if (!SpecialActorPortsIndexer.checkIndexes(sdf)) {
      throw new SDF4JException("There are still special actors with non-indexed ports. Contact Preesm developers.");
    }
    SpecialActorPortsIndexer.sortIndexedPorts(sdf);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractVertex)
   */
  @Override
  public void visit(final SDFAbstractVertex sdfVertex) {
    DAGVertex vertex;
    if (sdfVertex instanceof SDFRoundBufferVertex) {
      vertex = this.factory.createVertex(DAGBroadcastVertex.DAG_BROADCAST_VERTEX);
      vertex.getPropertyBean().setValue(DAGBroadcastVertex.SPECIAL_TYPE, DAGBroadcastVertex.SPECIAL_TYPE_ROUNDBUFFER);
    } else if (sdfVertex instanceof SDFBroadcastVertex) {
      vertex = this.factory.createVertex(DAGBroadcastVertex.DAG_BROADCAST_VERTEX);
      vertex.getPropertyBean().setValue(DAGBroadcastVertex.SPECIAL_TYPE, DAGBroadcastVertex.SPECIAL_TYPE_BROADCAST);
    } else if (sdfVertex instanceof SDFForkVertex) {
      vertex = this.factory.createVertex(DAGForkVertex.DAG_FORK_VERTEX);
    } else if (sdfVertex instanceof SDFJoinVertex) {
      vertex = this.factory.createVertex(DAGJoinVertex.DAG_JOIN_VERTEX);
    } else if (sdfVertex instanceof SDFEndVertex) {
      vertex = this.factory.createVertex(DAGEndVertex.DAG_END_VERTEX);
    } else if (sdfVertex instanceof SDFInitVertex) {
      vertex = this.factory.createVertex(DAGInitVertex.DAG_INIT_VERTEX);
    } else {
      vertex = this.factory.createVertex(DAGVertex.DAG_VERTEX);
    }

    setProperties(vertex, sdfVertex);

    // Set interfaces name because that's all we use afterall
    for (final SDFInterfaceVertex si : sdfVertex.getSinks()) {
      vertex.addSinkName(si.getName());
    }
    for (final SDFInterfaceVertex si : sdfVertex.getSources()) {
      vertex.addSourceName(si.getName());
    }
    this.outputGraph.addVertex(vertex);
  }

  /**
   * Set all the properties of the DAGVertex we will need.
   *
   * if you're reading this, then you can see that clearly a DAGVertex is just an SDFVertex in disguise.<br>
   * maybe should we just use of them ?
   *
   * @param dagVertex
   *          the dag vertex
   * @param sdfVertex
   *          the sdf vertex
   */
  private void setProperties(final DAGVertex dagVertex, final SDFAbstractVertex sdfVertex) {
    dagVertex.setName(sdfVertex.getName());
    dagVertex.setTime(new LongVertexPropertyType(0));
    dagVertex.setNbRepeat(new LongVertexPropertyType(0));
    dagVertex.setRefinement(sdfVertex.getRefinement());
    dagVertex.setArgumentSet(sdfVertex.getArguments());
    dagVertex.setId(sdfVertex.getId());
    dagVertex.setInfo(sdfVertex.getInfo());
    // Get memory script property
    dagVertex.getPropertyBean().setValue(SDFVertex.MEMORY_SCRIPT,
        sdfVertex.getPropertyBean().getValue(SDFVertex.MEMORY_SCRIPT));

    // Working memory property (from clustering)
    if (sdfVertex.getPropertyBean().getValue("working_memory") != null) {
      dagVertex.getPropertyBean().setValue("working_memory", sdfVertex.getPropertyBean().getValue("working_memory"));
    }

    // We have to check for SDFEndVertex first as it inherits from SDFInitVertex
    if (sdfVertex instanceof SDFEndVertex) {
      // Setting the end reference
      final SDFEndVertex sdfEndVertex = (SDFEndVertex) sdfVertex;
      final String endReferenceName = sdfEndVertex.getEndReference().getName();
      dagVertex.getPropertyBean().setValue(DAGInitVertex.END_REFERENCE, endReferenceName);
    } else if (sdfVertex instanceof SDFInitVertex) {
      // Setting the end reference
      final SDFInitVertex sdfInitVertex = (SDFInitVertex) sdfVertex;
      final String endReferenceName = sdfInitVertex.getEndReference().getName();
      dagVertex.getPropertyBean().setValue(DAGInitVertex.END_REFERENCE, endReferenceName);
      // Setting the init size property
      dagVertex.getPropertyBean().setValue(DAGInitVertex.INIT_SIZE, sdfInitVertex.getInitSize());
    }

    dagVertex.setCorrespondingSDFVertex(sdfVertex);
  }

}
