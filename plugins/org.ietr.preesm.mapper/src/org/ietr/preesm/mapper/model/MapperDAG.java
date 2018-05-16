/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2009 - 2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
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
package org.ietr.preesm.mapper.model;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.EdgeAggregate;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.mapper.model.property.DAGMappings;
import org.ietr.preesm.mapper.model.property.DAGTimings;

// TODO: Auto-generated Javadoc
/**
 * The Class MapperDAG.
 *
 * @author mpelcat
 *
 *         This class represents a Directed Acyclic Graph in the mapper. It holds mapping and scheduling information
 */
public class MapperDAG extends DirectedAcyclicGraph {

  /** Properties of mapped vertices. */
  private static final String MAPPING_PROPERTY = "MAPPING_PROPERTY";

  /** Properties of scheduled vertices. */
  private static final String TIMING_PROPERTY = "TIMING_PROPERTY";

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6757893466692519433L;

  /** Corresponding SDF graph. */
  private SDFGraph sdfGraph;

  /** The cost of the implementation. */
  private static final String SCHEDULE_COST = "SCHEDULE_COST";

  /** Property clustered hierarchical vertex. */
  public static final String CLUSTERED_VERTEX = "clustered_vertex";

  static {
    {
      AbstractGraph.public_properties.add(MapperDAG.SCHEDULE_COST);
      AbstractGraph.public_properties.add(MapperDAG.CLUSTERED_VERTEX);
    }
  }

  /**
   * Creactor of a DAG from a edge factory and a converted graph.
   *
   * @param factory
   *          the factory
   * @param graph
   *          the graph
   */
  public MapperDAG(final MapperEdgeFactory factory, final SDFGraph graph) {
    super(factory);
    this.sdfGraph = graph;
    setScheduleCost(0L);

    getPropertyBean().setValue(MapperDAG.MAPPING_PROPERTY, new DAGMappings());
    getPropertyBean().setValue(MapperDAG.TIMING_PROPERTY, new DAGTimings());

  }

  /**
   * give the number of vertices in the DAG.
   *
   * @return the number of vertices
   */
  public int getNumberOfVertices() {
    return vertexSet().size();
  }

  /**
   * Adds all vertices of a given set.
   *
   * @param set
   *          the set
   */
  public void addAllVertices(final Set<MapperDAGVertex> set) {
    final Iterator<MapperDAGVertex> iterator = set.iterator();

    while (iterator.hasNext()) {
      addVertex(iterator.next());
    }
  }

  /**
   * Gets the schedule cost.
   *
   * @return the schedule cost
   */
  public long getScheduleCost() {
    final long cost = (Long) getPropertyBean().getValue(MapperDAG.SCHEDULE_COST);
    return cost;
  }

  /**
   * Sets the schedule cost.
   *
   * @param scheduleLatency
   *          the new schedule cost
   */
  public void setScheduleCost(final long scheduleLatency) {
    getPropertyBean().setValue(MapperDAG.SCHEDULE_COST, scheduleLatency);
  }

  /**
   * Gets the reference sdf graph.
   *
   * @return the reference sdf graph
   */
  public SDFGraph getReferenceSdfGraph() {
    return this.sdfGraph;
  }

  /**
   * Sets the reference sdf graph.
   *
   * @param sdfGraph
   *          the new reference sdf graph
   */
  public void setReferenceSdfGraph(final SDFGraph sdfGraph) {
    this.sdfGraph = sdfGraph;
  }

  /**
   * Clone a MapperDAG.
   *
   * @return the mapper DAG
   */
  @Override
  public MapperDAG clone() {

    // create clone
    final MapperDAG newDAG = new MapperDAG(new MapperEdgeFactory(), getReferenceSdfGraph());
    newDAG.setScheduleCost(getScheduleCost());

    // add vertex
    final Iterator<DAGVertex> iterV = vertexSet().iterator();
    while (iterV.hasNext()) {
      MapperDAGVertex currentVertex = (MapperDAGVertex) iterV.next();
      currentVertex = currentVertex.clone();
      newDAG.addVertex(currentVertex);
    }

    // add edge
    final Iterator<DAGEdge> iterE = edgeSet().iterator();
    while (iterE.hasNext()) {
      final MapperDAGEdge origEdge = (MapperDAGEdge) iterE.next();

      final DAGVertex source = origEdge.getSource();
      final DAGVertex target = origEdge.getTarget();

      final String sourceName = source.getName();
      final String targetName = target.getName();
      final MapperDAGEdge newEdge = (MapperDAGEdge) newDAG.addEdge(newDAG.getVertex(sourceName), newDAG.getVertex(targetName));
      newEdge.setInit(origEdge.getInit().clone());
      newEdge.setTiming(origEdge.getTiming().clone());
      newEdge.copyProperties(origEdge);
      // Updating the aggregate list with proper reference
      newEdge.setAggregate(new EdgeAggregate());
      for (final AbstractEdge<?, ?> e : origEdge.getAggregate()) {
        final DAGEdge edge = (DAGEdge) e;
        final DAGEdge newAggEdge = new DAGEdge();
        newAggEdge.copyProperties(edge);
        newAggEdge.setContainingEdge(newEdge);
        newEdge.getAggregate().add(newAggEdge);
      }
    }
    newDAG.copyProperties(this);

    newDAG.setMappings((DAGMappings) getMappings().clone());
    newDAG.setTimings((DAGTimings) getTimings().clone());

    return newDAG;
  }

  /**
   * Gets the mappings.
   *
   * @return the mappings
   */
  public DAGMappings getMappings() {
    return (DAGMappings) getPropertyBean().getValue(MapperDAG.MAPPING_PROPERTY);
  }

  /**
   * Sets the mappings.
   *
   * @param implementationVertexProperty
   *          the new mappings
   */
  private void setMappings(final DAGMappings implementationVertexProperty) {
    getPropertyBean().setValue(MapperDAG.MAPPING_PROPERTY, implementationVertexProperty);
  }

  /**
   * Gets the timings.
   *
   * @return the timings
   */
  public DAGTimings getTimings() {
    return (DAGTimings) getPropertyBean().getValue(MapperDAG.TIMING_PROPERTY);
  }

  /**
   * Sets the timings.
   *
   * @param timingVertexProperty
   *          the new timings
   */
  private void setTimings(final DAGTimings timingVertexProperty) {
    getPropertyBean().setValue(MapperDAG.TIMING_PROPERTY, timingVertexProperty);
  }

  /**
   * Gets the vertex with the given reference graph.
   *
   * @param sdfvertex
   *          the sdfvertex
   * @return the vertex
   */
  public MapperDAGVertex getVertex(final SDFAbstractVertex sdfvertex) {

    final Iterator<DAGVertex> iter = vertexSet().iterator();
    MapperDAGVertex currentvertex = null;
    while (iter.hasNext()) {
      currentvertex = (MapperDAGVertex) iter.next();
      if (currentvertex.getName().equals(sdfvertex.getName())) {
        return currentvertex;
      }
    }
    return null;
  }

  /**
   * Gets all the DAG vertices corresponding to a given SDF graph.
   *
   * @param sdfvertex
   *          the sdfvertex
   * @return the vertices
   */
  public Set<MapperDAGVertex> getVertices(final SDFAbstractVertex sdfvertex) {

    final Set<MapperDAGVertex> currentset = new LinkedHashSet<>();
    MapperDAGVertex currentvertex = null;
    for (final DAGVertex currentv : vertexSet()) {
      currentvertex = (MapperDAGVertex) currentv;

      // Special vertices have null info
      if ((currentvertex.getCorrespondingSDFVertex().getInfo() != null) && currentvertex.getCorrespondingSDFVertex().getInfo().equals(sdfvertex.getInfo())) {
        currentset.add(currentvertex);
      }
    }
    return currentset;
  }

  /**
   * Returns all vertices corresponding to a set of names.
   *
   * @param nameSet
   *          the name set
   * @return the vertex set
   */
  public Set<MapperDAGVertex> getVertexSet(final Set<String> nameSet) {
    final Set<MapperDAGVertex> vSet = new LinkedHashSet<>();

    final Iterator<String> iterator = nameSet.iterator();

    while (iterator.hasNext()) {
      final String name = iterator.next();
      final MapperDAGVertex v = (MapperDAGVertex) this.getVertex(name);
      vSet.add(v);

    }

    return vSet;
  }

  /**
   * Returns all vertices with no incoming edges.
   *
   * @return the sources
   */
  public Set<MapperDAGVertex> getSources() {
    final Set<MapperDAGVertex> vSet = new LinkedHashSet<>();

    for (final DAGVertex v : vertexSet()) {
      if (incomingEdgesOf(v).isEmpty()) {
        vSet.add((MapperDAGVertex) v);
      }

    }

    return vSet;
  }

  /**
   * Gets the mapper DAG vertex.
   *
   * @param name
   *          the name
   * @return the mapper DAG vertex
   */
  public MapperDAGVertex getMapperDAGVertex(final String name) {

    return (MapperDAGVertex) super.getVertex(name);
  }

}
