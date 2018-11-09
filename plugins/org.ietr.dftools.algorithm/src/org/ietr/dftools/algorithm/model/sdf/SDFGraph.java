/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011 - 2013)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2014)
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
package org.ietr.dftools.algorithm.model.sdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.SDFMath;
import org.ietr.dftools.algorithm.factories.IModelVertexFactory;
import org.ietr.dftools.algorithm.factories.SDFEdgeFactory;
import org.ietr.dftools.algorithm.factories.SDFVertexFactory;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.PropertyBean;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.SpecialActorPortsIndexer;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.StringEdgePropertyType;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.jgrapht.EdgeFactory;
import org.math.array.DoubleArray;
import org.math.array.LinearAlgebra;

/**
 * Abstract Class representing an SDF graph.
 *
 * @author jpiat
 * @author kdesnos
 * @author jheulot
 */
public class SDFGraph extends AbstractGraph<SDFAbstractVertex, SDFEdge> {

  private static final String TOPOLOGY_LITERAL = "topology";

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant TOPOLOGY. */
  protected static final String TOPOLOGY = TOPOLOGY_LITERAL;

  /** The Constant VALID_MODEL. */
  protected static final String VALID_MODEL = "valid_model";

  /** The old ref. */
  // use HashMap for inheriting serializable
  private final HashMap<SDFEdge, SDFEdge> oldRef = new LinkedHashMap<>();

  /**
   * Construct a new SDFGraph with the default edge factory.
   */
  public SDFGraph() {
    super(new SDFEdgeFactory());
    getPropertyBean().setValue(AbstractGraph.MODEL, "sdf");
  }

  /**
   * COnstruct a new SDFAbstractGraph using the given EdgeFactory ef.
   *
   * @param ef
   *          the ef
   */
  public SDFGraph(final EdgeFactory<SDFAbstractVertex, SDFEdge> ef) {
    super(ef);
    setName("");
    getPropertyBean().setValue(AbstractGraph.MODEL, "sdf");
  }

  /**
   * Creates a new SDFAbstractGraph with the given factory.
   *
   * @param factory
   *          The factory used to create edges
   */
  public SDFGraph(final SDFEdgeFactory factory) {
    super(factory);
    setName("");
    getPropertyBean().setValue(AbstractGraph.MODEL, "sdf");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#addEdge(org.ietr.dftools.algorithm.model.AbstractVertex,
   * org.ietr.dftools.algorithm.model.IInterface, org.ietr.dftools.algorithm.model.AbstractVertex,
   * org.ietr.dftools.algorithm.model.IInterface)
   */
  @Override
  public SDFEdge addEdge(final SDFAbstractVertex source, final IInterface sourcePort, final SDFAbstractVertex target,
      final IInterface targetPort) {
    final SDFEdge edge = this.addEdge(source, target);
    edge.setSourceInterface((SDFInterfaceVertex) sourcePort);
    source.setInterfaceVertexExternalLink(edge, (SDFInterfaceVertex) sourcePort);
    edge.setTargetInterface((SDFInterfaceVertex) targetPort);
    target.setInterfaceVertexExternalLink(edge, (SDFInterfaceVertex) targetPort);
    return edge;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#addEdge(org.ietr.dftools.algorithm.model.AbstractVertex,
   * org.ietr.dftools.algorithm.model.AbstractVertex)
   */
  @Override
  public SDFEdge addEdge(final SDFAbstractVertex source, final SDFAbstractVertex target) {
    final SDFEdge newEdge = super.addEdge(source, target);
    if ((source instanceof SDFForkVertex)
        || ((source instanceof SDFBroadcastVertex) && !(source instanceof SDFRoundBufferVertex))) {
      source.connectionAdded(newEdge);
    }

    if ((target instanceof SDFJoinVertex) || (target instanceof SDFRoundBufferVertex)) {
      target.connectionAdded(newEdge);
    }
    return newEdge;
  }

  /**
   * Adds the edge.
   *
   * @param source
   *          the source
   * @param sourcePort
   *          the source port
   * @param target
   *          the target
   * @param targetPort
   *          the target port
   * @param prod
   *          the prod
   * @param cons
   *          the cons
   * @param delay
   *          the delay
   * @return the SDF edge
   */
  public SDFEdge addEdge(final SDFAbstractVertex source, final IInterface sourcePort, final SDFAbstractVertex target,
      final IInterface targetPort, final AbstractEdgePropertyType<?> prod, final AbstractEdgePropertyType<?> cons,
      final AbstractEdgePropertyType<?> delay) {
    // Create the edge
    final SDFEdge newEdge = this.addEdge(source, sourcePort, target, targetPort);
    // Set its production rate, consumption rate and delay
    newEdge.setCons(cons);
    newEdge.setProd(prod);
    newEdge.setDelay(delay);
    return newEdge;
  }

  /**
   * Adds the edge.
   *
   * @param source
   *          the source
   * @param target
   *          the target
   * @param prod
   *          the prod
   * @param cons
   *          the cons
   * @param delay
   *          the delay
   * @return the SDF edge
   */
  public SDFEdge addEdge(final SDFAbstractVertex source, final SDFAbstractVertex target,
      final AbstractEdgePropertyType<?> prod, final AbstractEdgePropertyType<?> cons,
      final AbstractEdgePropertyType<?> delay) {
    // Create the edge
    final SDFEdge newEdge = this.addEdge(source, target);
    // Set its production rate, consumption rate and delay
    newEdge.setCons(cons);
    newEdge.setProd(prod);
    newEdge.setDelay(delay);
    return newEdge;
  }

  /**
   * Add an edge an creates default interfaces on the source and target vertices.
   *
   * @param sourceVertex
   *          the source vertex
   * @param targetVertex
   *          the target vertex
   * @return The created edge
   */
  public SDFEdge addEdgeWithInterfaces(final SDFAbstractVertex sourceVertex, final SDFAbstractVertex targetVertex) {
    final SDFEdge edge = addEdge(sourceVertex, targetVertex);
    if (edge != null) {
      final SDFSinkInterfaceVertex sinkInterface = new SDFSinkInterfaceVertex();
      sinkInterface.setName("O_" + sourceVertex.getName() + "_" + sourceVertex.getSinks().size());
      sourceVertex.addSink(sinkInterface);
      edge.setSourceInterface(sinkInterface);

      final SDFSourceInterfaceVertex sourceInterface = new SDFSourceInterfaceVertex();
      sourceInterface.setName("I_" + targetVertex.getName() + "_" + targetVertex.getSources().size());
      targetVertex.addSource(sourceInterface);
      edge.setTargetInterface(sourceInterface);
    }
    return edge;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#addVertex(org.ietr.dftools.algorithm.model.AbstractVertex)
   */
  @Override
  public boolean addVertex(final SDFAbstractVertex vertex) {
    if (super.addVertex(vertex)) {
      getPropertyBean().setValue(TOPOLOGY_LITERAL, null);
      return true;
    }
    return false;

  }

  /**
   * Clean the graph, removes all edges and vertices.
   */
  public void clean() {
    final ArrayList<SDFEdge> edges = new ArrayList<>(edgeSet());
    for (int i = 0; i < edges.size(); i++) {
      this.removeEdge(edges.get(i));
    }
    final ArrayList<SDFAbstractVertex> vertices = new ArrayList<>(vertexSet());
    for (int i = 0; i < vertices.size(); i++) {
      removeVertex(vertices.get(i));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#clone()
   */
  @Override
  public SDFGraph copy() {
    final SDFGraph newGraph = new SDFGraph();
    final Map<SDFAbstractVertex, SDFAbstractVertex> matchCopies = new LinkedHashMap<>();
    for (final SDFAbstractVertex vertices : vertexSet()) {
      final SDFAbstractVertex newVertex = vertices.copy();
      newGraph.addVertex(newVertex);
      matchCopies.put(vertices, newVertex);
    }
    for (final SDFEdge edge : edgeSet()) {
      final SDFEdge newEdge = newGraph.addEdge(matchCopies.get(edge.getSource()), matchCopies.get(edge.getTarget()));
      for (final SDFInterfaceVertex sink : matchCopies.get(edge.getSource()).getSinks()) {
        if ((edge.getTargetInterface() != null) && edge.getTargetInterface().getName().equals(sink.getName())) {
          matchCopies.get(edge.getSource()).setInterfaceVertexExternalLink(newEdge, sink);
        }
      }
      for (final SDFInterfaceVertex source : matchCopies.get(edge.getTarget()).getSources()) {
        if ((edge.getSourceInterface() != null) && edge.getSourceInterface().getName().equals(source.getName())) {
          matchCopies.get(edge.getTarget()).setInterfaceVertexExternalLink(newEdge, source);
        }
      }
      newEdge.copyProperties(edge);
    }

    // Make sure the ports of special actors are ordered according to
    // their indices.
    SpecialActorPortsIndexer.sortIndexedPorts(newGraph);

    newGraph.copyProperties(this);
    newGraph.getPropertyBean().setValue(TOPOLOGY_LITERAL, null);
    newGraph.getPropertyBean().setValue("vrb", null);
    return newGraph;
  }

  /**
   * Compute the vrb of this graph and affect the nbRepeat property to vertices.
   *
   * @return true, if successful
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  protected boolean computeVRB() {
    final Map<SDFAbstractVertex, Long> vrb = new LinkedHashMap<>();
    final List<List<SDFAbstractVertex>> subgraphs = getAllSubGraphs();

    for (final List<SDFAbstractVertex> subgraph : subgraphs) {
      boolean hasInterface = false;
      for (final SDFAbstractVertex vertex : subgraph) {
        hasInterface |= vertex instanceof SDFInterfaceVertex;
      }

      if (hasInterface) {
        vrb.putAll(SDFMath.computeRationnalVRBWithInterfaces(subgraph, this));
      } else {
        vrb.putAll(SDFMath.computeRationnalVRB(subgraph, this));
      }
    }
    for (final SDFAbstractVertex vertex : vrb.keySet()) {
      vertex.setNbRepeat(vrb.get(vertex));
    }
    return true;
  }

  /**
   * Fill this graph object with the given graph content.
   *
   * @param content
   *          The content to fill in this graph
   */
  public void fill(final SDFGraph content) {
    final SDFGraph cleanGraph = content.copy();
    for (final SDFAbstractVertex vertex : cleanGraph.vertexSet()) {
      addVertex(vertex);
    }
    for (final SDFEdge edge : cleanGraph.edgeSet()) {
      final SDFAbstractVertex source = cleanGraph.getEdgeSource(edge);
      final SDFAbstractVertex target = cleanGraph.getEdgeTarget(edge);
      final SDFEdge newEdge = this.addEdge(source, target);
      newEdge.setSourceInterface(edge.getSourceInterface());
      newEdge.setTargetInterface(edge.getTargetInterface());
      target.setInterfaceVertexExternalLink(newEdge, edge.getTargetInterface());
      source.setInterfaceVertexExternalLink(newEdge, edge.getSourceInterface());

      newEdge.setCons(edge.getCons().copy());
      newEdge.setProd(edge.getProd().copy());
      newEdge.setDelay(edge.getDelay().copy());

    }

    for (final String propertyKey : cleanGraph.getPropertyBean().keys()) {
      final Object property = cleanGraph.getPropertyBean().getValue(propertyKey);
      getPropertyBean().setValue(propertyKey, property);
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#getEdgeSource(java.lang.Object)
   */
  @Override
  public SDFAbstractVertex getEdgeSource(final SDFEdge edge) {
    try {
      return super.getEdgeSource(edge);

    } catch (final Exception e) {
      if (this.oldRef.get(edge) != null) {
        return getEdgeSource(this.oldRef.get(edge));
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#getEdgeTarget(java.lang.Object)
   */
  @Override
  public SDFAbstractVertex getEdgeTarget(final SDFEdge edge) {
    try {
      return super.getEdgeTarget(edge);

    } catch (final Exception e) {
      if (this.oldRef.get(edge) != null) {
        return getEdgeTarget(this.oldRef.get(edge));
      }
    }
    return null;
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

  /**
   * Iterative function of getAllSubGraphs.
   *
   * @param vertex
   *          the current vertex
   * @param subgraph
   *          the current subgraph
   * @return the sub graph
   */
  private void getSubGraph(final SDFAbstractVertex vertex, final List<SDFAbstractVertex> subgraph) {
    for (final SDFEdge edge : outgoingEdgesOf(vertex)) {
      if (!subgraph.contains(getEdgeTarget(edge))) {
        subgraph.add(getEdgeTarget(edge));
        getSubGraph(getEdgeTarget(edge), subgraph);
      }
    }
    for (final SDFEdge edge : incomingEdgesOf(vertex)) {
      if (!subgraph.contains(getEdgeSource(edge))) {
        subgraph.add(getEdgeSource(edge));
        getSubGraph(getEdgeSource(edge), subgraph);
      }
    }
  }

  /**
   * Divide the current graph into a list of subgraph.
   *
   * @return the list of subgraph
   */
  public List<List<SDFAbstractVertex>> getAllSubGraphs() {
    final List<List<SDFAbstractVertex>> subgraphs = new ArrayList<>();

    for (final SDFAbstractVertex vertex : vertexSet()) {
      boolean notAssignedToASubgraph = true;
      for (final List<SDFAbstractVertex> subgraph : subgraphs) {
        if (subgraph.contains(vertex)) {
          notAssignedToASubgraph = false;
          break;
        }
      }
      if (notAssignedToASubgraph) {
        final List<SDFAbstractVertex> subgraph = new ArrayList<>();
        subgraph.add(vertex);

        getSubGraph(vertex, subgraph);

        subgraphs.add(subgraph);
      }
    }

    return subgraphs;
  }

  /**
   * Gets the all vertices.
   *
   * @return the set of all the vertices contained by the graph and its subgraphs
   */
  public Set<SDFAbstractVertex> getAllVertices() {
    final Set<SDFAbstractVertex> vertices = new LinkedHashSet<>();
    for (final SDFAbstractVertex v : vertexSet()) {
      vertices.add(v);
      if (v.getGraphDescription() != null) {
        final SDFGraph g = ((SDFGraph) v.getGraphDescription());
        vertices.addAll(g.getAllVertices());
      }
    }
    return vertices;
  }

  /**
   * Gives the topology matrix of a subgraph of this graph as an array of double The subgraph must not contain
   * InterfaceVertex.
   *
   * @param subgraph
   *          the subgraph
   * @return the topology matrix
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  public double[][] getTopologyMatrix(final List<SDFAbstractVertex> subgraph) {
    final List<double[]> topologyListMatrix = new ArrayList<>();
    double[][] topologyArrayMatrix;

    for (final SDFAbstractVertex vertex : subgraph) {
      if (vertex instanceof SDFInterfaceVertex) {
        throw new IllegalArgumentException("Cannot get topology matrix " + "from a subgraph with interface vertices");
      }
    }

    for (final SDFEdge edge : edgeSet()) {
      final SDFAbstractVertex source = getEdgeSource(edge);
      final SDFAbstractVertex target = getEdgeTarget(edge);
      if (subgraph.contains(source) && subgraph.contains(target) && !source.equals(target)) {
        final double[] line = DoubleArray.fill(subgraph.size(), 0);
        final long prodIntValue = edge.getProd().longValue();
        final long consIntValue = edge.getCons().longValue();
        line[subgraph.indexOf(source)] += prodIntValue;
        line[subgraph.indexOf(target)] -= consIntValue;
        topologyListMatrix.add(line);
      }
    }

    if (topologyListMatrix.isEmpty()) {
      topologyArrayMatrix = new double[0][0];
    } else {
      topologyArrayMatrix = new double[topologyListMatrix.size()][topologyListMatrix.get(0).length];

      for (int i = 0; i < topologyListMatrix.size(); i++) {
        topologyArrayMatrix[i] = topologyListMatrix.get(i);
      }

    }

    return topologyArrayMatrix;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractGraph#getVertexFactory()
   */
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public IModelVertexFactory getVertexFactory() {
    return SDFVertexFactory.getInstance();
  }

  /**
   * Insert Broadcast where is needed. Multiple edges connected to one output for example
   *
   * @param vertex
   *          the current vertex.
   * @param logger
   *          the logger where display a warning.
   */
  private void insertBroadcast(final SDFVertex vertex) {
    final Map<SDFInterfaceVertex, ArrayList<SDFEdge>> connections = new LinkedHashMap<>();
    for (final SDFEdge edge : outgoingEdgesOf(vertex)) {
      if (connections.get(edge.getSourceInterface()) == null) {
        connections.put(edge.getSourceInterface(), new ArrayList<SDFEdge>());
      }
      connections.get(edge.getSourceInterface()).add(edge);
    }
    for (final SDFInterfaceVertex port : connections.keySet()) {
      if (connections.get(port).size() > 1) {
        final String message = "Warning: Implicit Broadcast added in graph " + getName() + " at port " + vertex + "."
            + port.getName();
        WorkflowLogger.getLogger().log(Level.WARNING, message);
        final SDFBroadcastVertex broadcastPort = new SDFBroadcastVertex();
        broadcastPort.setName("br_" + vertex.getName() + "_" + port.getName());
        final SDFSourceInterfaceVertex inPort = new SDFSourceInterfaceVertex();
        inPort.setName("in");
        broadcastPort.addSource(inPort);
        if (!addVertex(broadcastPort)) {
          throw new DFToolsAlgoException("Could not insert broadcast vertex");
        }
        final SDFEdge baseEdge = this.addEdge(vertex, broadcastPort);
        baseEdge.setSourceInterface(port);
        baseEdge.setTargetInterface(inPort);
        baseEdge.setTargetPortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));

        // Add all outgoing edges
        int nbTokens = 0;
        for (final SDFEdge oldEdge : connections.get(port)) {
          try {
            // Create a new outport
            final SDFSinkInterfaceVertex outPort = new SDFSinkInterfaceVertex();
            outPort.setName("out_" + (nbTokens / baseEdge.getCons().longValue()) + "_"
                + (nbTokens % baseEdge.getCons().longValue()));
            nbTokens += oldEdge.getProd().longValue();

            broadcastPort.addSink(outPort);

            final SDFEdge newEdge = this.addEdge(broadcastPort, oldEdge.getTarget());
            newEdge.setSourceInterface(outPort);
            newEdge.setTargetInterface(oldEdge.getTargetInterface());
            newEdge.setTargetPortModifier(oldEdge.getTargetPortModifier());
            newEdge.setProd(oldEdge.getProd());
            newEdge.setCons(oldEdge.getCons());
            newEdge.setDelay(oldEdge.getDelay());
            newEdge.setDataType(oldEdge.getDataType());
            newEdge.setSourcePortModifier(new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
            baseEdge.setSourcePortModifier(oldEdge.getSourcePortModifier());
            baseEdge.setProd(oldEdge.getProd().copy());
            baseEdge.setCons(oldEdge.getProd().copy());
            baseEdge.setDelay(new LongEdgePropertyType(0));
            baseEdge.setDataType(oldEdge.getDataType());
            this.removeEdge(oldEdge);
          } catch (final InvalidExpressionException e) {
            throw new DFToolsAlgoException("Could not insert broadcast", e);
          }
        }
      }
    }
  }

  /**
   * Check the schedulability of the graph.
   *
   * @return True if the graph is schedulable
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  public boolean isSchedulable() throws SDF4JException {
    boolean schedulable = true;
    for (final SDFAbstractVertex vertex : vertexSet()) {
      if (!(vertex instanceof SDFInterfaceVertex) && vertex.getGraphDescription() instanceof SDFGraph) {
        schedulable &= ((SDFGraph) vertex.getGraphDescription()).isSchedulable();
      }

    }
    final List<List<SDFAbstractVertex>> subgraphs = getAllSubGraphs();

    try {
      for (final List<SDFAbstractVertex> subgraph : subgraphs) {

        final List<SDFAbstractVertex> subgraphWOInterfaces = new ArrayList<>();
        for (final SDFAbstractVertex vertex : subgraph) {
          if (!(vertex instanceof SDFInterfaceVertex)) {
            subgraphWOInterfaces.add(vertex);
          }
        }

        final double[][] topologyMatrix = getTopologyMatrix(subgraphWOInterfaces);

        final int length = topologyMatrix.length;
        if (length > 0) {
          final int rank = LinearAlgebra.rank(topologyMatrix);
          final int expectedRankValue = subgraphWOInterfaces.size() - 1;
          if (rank == expectedRankValue) {
            schedulable &= true;
          } else {
            schedulable &= false;
            VisitorOutput.getLogger().log(Level.SEVERE, "Graph " + getName() + " is not schedulable");
          }
        }
      }
    } catch (final InvalidExpressionException e) {
      throw new SDF4JException(getName() + ": " + e.getMessage(), e);
    }
    return schedulable;
  }

  /**
   * Gives a Set of all this graph child property beans.
   *
   * @return The properties Set
   */
  public List<PropertyBean> propertiesSet() {
    final List<PropertyBean> properties = new ArrayList<>();
    for (final SDFAbstractVertex child : vertexSet()) {
      properties.add(child.getPropertyBean());
    }
    return properties;
  }

  /**
   * This method is used to remove an {@link SDFEdge} from a {@link SDFGraph}. Side effects are: the deletion of the
   * {@link SDFSourceInterfaceVertex} and {@link SDFSinkInterfaceVertex} associated to this {@link SDFEdge} (unless
   * several vertices are linked to this interface). For {@link SDFForkVertex} {@link SDFJoinVertex},
   * {@link SDFBroadcastVertex} and {@link SDFRoundBufferVertex}, the ordered list of input/output edges is updated.
   *
   * @param edge
   *          the removed {@link SDFEdge}
   * @return <code>true</code> if the edge was correctly removed, <code>false</code> else.
   *
   * @see AbstractGraph#removeEdge(SDFEdge)
   *
   *
   */
  @Override
  public boolean removeEdge(final SDFEdge edge) {
    final SDFAbstractVertex sourceVertex = edge.getSource();
    final SDFAbstractVertex targetVertex = edge.getTarget();
    final boolean res = super.removeEdge(edge);
    if (res) {
      if (sourceVertex instanceof SDFVertex) {
        ((SDFVertex) sourceVertex).removeSink(edge);
      }
      if (targetVertex instanceof SDFVertex) {
        ((SDFVertex) targetVertex).removeSource(edge);
      }

      if (sourceVertex instanceof SDFForkVertex) {
        ((SDFForkVertex) sourceVertex).connectionRemoved(edge);
      }
      if (targetVertex instanceof SDFJoinVertex) {
        ((SDFJoinVertex) targetVertex).connectionRemoved(edge);
      }

      // Beware of the Broadcast - RoundBuffer inheritance
      if ((sourceVertex instanceof SDFBroadcastVertex) && !(sourceVertex instanceof SDFRoundBufferVertex)) {
        ((SDFBroadcastVertex) sourceVertex).connectionRemoved(edge);
      }
      if (targetVertex instanceof SDFRoundBufferVertex) {
        ((SDFRoundBufferVertex) targetVertex).connectionRemoved(edge);
      }

    }
    return res;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractGraph#toString()
   */
  @Override
  public String toString() {
    return getName();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.IModelObserver#update(org.ietr.dftools.algorithm.model.AbstractGraph,
   * java.lang.Object)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void update(final AbstractGraph<?, ?> observable, final Object arg) {
    if (arg != null) {
      if (arg instanceof AbstractVertex) {
        if (observable.vertexSet().contains(arg)) {
          final SDFVertex newVertex = new SDFVertex();
          newVertex.setName(((AbstractVertex) arg).getName());
          newVertex.setId(((AbstractVertex) arg).getId());
          newVertex.setRefinement(((AbstractVertex) arg).getRefinement());
          addVertex(newVertex);
        } else {
          removeVertex(getVertex(((AbstractVertex) arg).getName()));
        }
      } else if (arg instanceof AbstractEdge) {
        if (observable.edgeSet().contains(arg)) {
          if (arg instanceof SDFEdge) {
            final SDFAbstractVertex source = ((SDFEdge) arg).getSource();
            final SDFAbstractVertex target = ((SDFEdge) arg).getTarget();
            final SDFAbstractVertex newSource = getVertex(source.getName());
            final SDFAbstractVertex newTarget = getVertex(target.getName());
            this.addEdge(newSource, newTarget, (SDFEdge) arg);
          } else if (arg instanceof DAGEdge) {
            final DAGVertex source = ((DAGEdge) arg).getSource();
            final DAGVertex target = ((DAGEdge) arg).getTarget();
            final SDFAbstractVertex newSource = getVertex(source.getName());
            final SDFAbstractVertex newTarget = getVertex(target.getName());
            for (final AbstractEdge edge : ((DAGEdge) arg).getAggregate()) {
              final SDFEdge newEdge = this.addEdge(newSource, newTarget);
              newEdge.copyProperties(edge);
            }
          }
        } else {
          if (arg instanceof SDFEdge) {
            final SDFAbstractVertex source = ((SDFEdge) arg).getSource();
            final SDFAbstractVertex target = ((SDFEdge) arg).getTarget();
            final SDFAbstractVertex newSource = getVertex(source.getName());
            final SDFAbstractVertex newTarget = getVertex(target.getName());
            for (final SDFEdge edge : getAllEdges(newSource, newTarget)) {
              if (edge.getSourceInterface().getName().equals(((SDFEdge) arg).getSourceInterface().getName())
                  && edge.getTargetInterface().getName().equals(((SDFEdge) arg).getTargetInterface().getName())) {
                this.removeEdge(edge);
                break;
              }
            }
          } else if (arg instanceof DAGEdge) {
            final DAGVertex source = ((DAGEdge) arg).getSource();
            final DAGVertex target = ((DAGEdge) arg).getTarget();
            final SDFAbstractVertex newSource = getVertex(source.getName());
            final SDFAbstractVertex newTarget = getVertex(target.getName());
            this.removeAllEdges(newSource, newTarget);
          }
        }
      } else if (arg instanceof String) {
        final Object property = observable.getPropertyBean().getValue((String) arg);
        if (property != null) {
          getPropertyBean().setValue((String) arg, property);
        }
      }
    }

  }

  /**
   * Validate child.
   *
   * @param child
   *          the child
   * @param logger
   *          the logger
   * @throws InvalidExpressionException
   *           thrown if the child contains invalid expressions
   * @throws SDF4JException
   *           thrown if the child is not valid
   */
  private void validateChild(final SDFAbstractVertex child) {

    // validate vertex
    if (!child.validateModel()) {
      throw new DFToolsAlgoException(child.getName() + " is not a valid vertex, verify arguments");
    }

    if (child.getGraphDescription() != null) {
      // validate child graph
      final String childGraphName = child.getGraphDescription().getName();
      final SDFGraph descritption = ((SDFGraph) child.getGraphDescription());
      if (!descritption.validateModel()) {
        throw (new SDF4JException(childGraphName + " is not schedulable"));
      }
      // validate child graph I/Os w.r.t. actor I/Os
      final List<SDFAbstractVertex> validatedInputs = validateInputs(child);
      final List<SDFAbstractVertex> validatedOutputs = validateOutputs(child);
      // make sure
      final boolean disjoint = Collections.disjoint(validatedInputs, validatedOutputs);
      if (!disjoint) {
        validatedInputs.retainAll(validatedOutputs);
        final List<SDFAbstractVertex> multiplyDefinedEdges = validatedInputs.stream().peek(AbstractVertex::getName)
            .collect(Collectors.toList());
        throw new SDF4JException(multiplyDefinedEdges + " are multiply connected, consider using broadcast ");
      }
    } else {
      // validate concrete actor implementation
      // not supported yet
    }
  }

  private List<SDFAbstractVertex> validateOutputs(final SDFAbstractVertex hierarchicalActor) throws SDF4JException {
    final SDFGraph subGraph = ((SDFGraph) hierarchicalActor.getGraphDescription());
    final List<SDFAbstractVertex> validatedOutInterfaces = new ArrayList<>();
    final Set<SDFEdge> actorOutgoingEdges = outgoingEdgesOf(hierarchicalActor);
    for (final SDFEdge actorOutgoingEdge : actorOutgoingEdges) {
      final SDFSinkInterfaceVertex subGraphSinkInterface = (SDFSinkInterfaceVertex) actorOutgoingEdge
          .getSourceInterface();
      final String subGraphSinkInterfaceName = subGraphSinkInterface.getName();
      if (validatedOutInterfaces.contains(subGraphSinkInterface)) {
        throw new SDF4JException(subGraphSinkInterfaceName + " is multiply connected, consider using broadcast ");
      } else {
        validatedOutInterfaces.add(subGraphSinkInterface);
      }
      if (subGraph.getVertex(subGraphSinkInterfaceName) != null) {
        final AbstractEdgePropertyType<?> actorOutEdgeProdExpr = actorOutgoingEdge.getProd();
        final long actorOutEdgeProdRate = actorOutEdgeProdExpr.longValue();
        final SDFAbstractVertex trueSinkInterface = subGraph.getVertex(subGraphSinkInterfaceName);
        for (final SDFEdge subGraphSinkInterfaceInEdge : subGraph.incomingEdgesOf(trueSinkInterface)) {
          final AbstractEdgePropertyType<?> subInterfaceConsExpr = subGraphSinkInterfaceInEdge.getCons();
          final long sinkInterfaceConsrate = subInterfaceConsExpr.longValue();
          if (sinkInterfaceConsrate != actorOutEdgeProdRate) {
            throw new SDF4JException("Sink [" + subGraphSinkInterfaceName + "] in actor [" + hierarchicalActor.getName()
                + "] has incompatible outside actor production and inside sink vertex consumption "
                + sinkInterfaceConsrate + " != " + actorOutEdgeProdRate
                + " (sub graph sink interface consumption rate of " + subInterfaceConsExpr
                + " does not match actor production rate of " + actorOutEdgeProdExpr + ")");
          }
        }
      }
    }
    return validatedOutInterfaces;
  }

  private List<SDFAbstractVertex> validateInputs(final SDFAbstractVertex hierarchicalActor) throws SDF4JException {
    final SDFGraph subGraph = ((SDFGraph) hierarchicalActor.getGraphDescription());
    final List<SDFAbstractVertex> validatedInInterfaces = new ArrayList<>();
    final Set<SDFEdge> actorIncomingEdges = incomingEdgesOf(hierarchicalActor);
    for (final SDFEdge actorIncomingEdge : actorIncomingEdges) {
      final SDFSourceInterfaceVertex subGraphSourceInterface = (SDFSourceInterfaceVertex) actorIncomingEdge
          .getTargetInterface();
      final String subGraphSourceInterfaceName = subGraphSourceInterface.getName();
      if (validatedInInterfaces.contains(subGraphSourceInterface)) {
        throw new SDF4JException(subGraphSourceInterfaceName + " is multiply connected, consider using broadcast ");
      } else {
        validatedInInterfaces.add(subGraphSourceInterface);
      }
      if (subGraph.getVertex(subGraphSourceInterfaceName) != null) {
        final AbstractEdgePropertyType<?> actorInEdgeConsExpr = actorIncomingEdge.getCons();
        final long actorInEdgeConsRate = actorInEdgeConsExpr.longValue();
        final SDFAbstractVertex trueSourceInterface = subGraph.getVertex(subGraphSourceInterfaceName);
        final Set<SDFEdge> subGraphSourceInterfaceOutEdges = subGraph.outgoingEdgesOf(trueSourceInterface);
        for (final SDFEdge subGraphSourceInterfaceOutEdge : subGraphSourceInterfaceOutEdges) {
          final AbstractEdgePropertyType<?> subInterfaceProdExpr = subGraphSourceInterfaceOutEdge.getProd();
          final long sourceInterfaceProdRate = subInterfaceProdExpr.longValue();
          if (sourceInterfaceProdRate != actorInEdgeConsRate) {
            throw new SDF4JException(
                "Source [" + subGraphSourceInterfaceName + "] in actor [" + hierarchicalActor.getName()
                    + "} has incompatible outside actor consumption and inside source vertex production "
                    + sourceInterfaceProdRate + " != " + actorInEdgeConsRate
                    + " (sub graph source interface production rate of " + subInterfaceProdExpr
                    + " does not match actor consumption rate of " + actorInEdgeConsExpr + ")");
          }
        }
      }
    }
    return validatedInInterfaces;
  }

  /**
   * Validate the model's schedulability.
   *
   * @return True if the model is valid, false otherwise ...
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  @Override
  public boolean validateModel() {
    try {
      final boolean schedulable = isSchedulable();
      if (schedulable) {
        computeVRB();
        // TODO: variable should only need to be resolved once, but
        // keep memory of their integer value
        final Set<SDFAbstractVertex> vertexSet = vertexSet();
        for (final SDFAbstractVertex child : vertexSet) {
          validateChild(child);
        }
        return true;
      }
      return false;
    } catch (final InvalidExpressionException e) {
      throw new SDF4JException(getName() + ": " + e.getMessage(), e);
    }
  }

  /**
   *
   */
  public void insertBroadcasts() {
    final Set<SDFAbstractVertex> vertexSet = vertexSet();
    List<SDFAbstractVertex> array = new ArrayList<>(vertexSet);
    for (final SDFAbstractVertex child : array) {
      if (child.getGraphDescription() != null) {
        // validate child graph
        final SDFGraph descritption = ((SDFGraph) child.getGraphDescription());
        descritption.insertBroadcasts();
      }
      // solving all the parameter for the rest of the processing ...
      int i = 0;
      while (i < array.size()) {
        final SDFAbstractVertex vertex = array.get(i);
        /*
         * (15/01/14) Removed by jheulot: allowing unconnected actor
         */
        if (vertex instanceof SDFVertex) {
          insertBroadcast((SDFVertex) vertex);
        }
        i++;
      }

    }
  }
}
