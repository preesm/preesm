/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.preesm.algorithm.mapper.model;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.preesm.algorithm.mapper.model.property.DAGMappings;
import org.preesm.algorithm.mapper.model.property.DAGTimings;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.dag.EdgeAggregate;
import org.preesm.model.pisdf.PiGraph;

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
  private final PiGraph piSDFGraph;

  /** The cost of the implementation. */
  private static final String SCHEDULE_COST = "SCHEDULE_COST";

  /** Property clustered hierarchical vertex. */
  public static final String CLUSTERED_VERTEX = "clustered_vertex";

  static {
    AbstractGraph.PUBLIC_PROPERTIES.add(MapperDAG.SCHEDULE_COST);
    AbstractGraph.PUBLIC_PROPERTIES.add(MapperDAG.CLUSTERED_VERTEX);
  }

  /**
   *
   */
  public MapperDAG(final PiGraph piGraph) {
    super(() -> new MapperDAGEdge());
    this.piSDFGraph = piGraph;
    setScheduleCost(0L);

    getPropertyBean().setValue(MapperDAG.MAPPING_PROPERTY, new DAGMappings());
    getPropertyBean().setValue(MapperDAG.TIMING_PROPERTY, new DAGTimings());

  }

  public MapperDAG() {
    this(null);
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
   * Gets the reference PiMM graph.
   *
   * @return the reference PiMM graph
   */
  public PiGraph getReferencePiMMGraph() {
    return this.piSDFGraph;
  }

  /**
   * Clone a MapperDAG.
   *
   * @return the mapper DAG
   */
  @Override
  public MapperDAG copy() {

    // create clone
    final MapperDAG newDAG = new MapperDAG(getReferencePiMMGraph());
    newDAG.setScheduleCost(getScheduleCost());

    // add vertex
    final Iterator<DAGVertex> iterV = vertexSet().iterator();
    while (iterV.hasNext()) {
      MapperDAGVertex currentVertex = (MapperDAGVertex) iterV.next();
      currentVertex = currentVertex.copy();
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
      final MapperDAGEdge newEdge = (MapperDAGEdge) newDAG.addEdge(newDAG.getVertex(sourceName),
          newDAG.getVertex(targetName));
      newEdge.setInit(origEdge.getInit().copy());
      newEdge.setTiming(origEdge.getTiming().copy());
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

    newDAG.setMappings(getMappings().copy());
    newDAG.setTimings(getTimings().copy());

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
      final MapperDAGVertex v = (MapperDAGVertex) getVertex(name);
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
