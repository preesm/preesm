/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.mapper.timekeeper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.VertexTiming;

// TODO: Auto-generated Javadoc
/**
 * New version of the time keeper. Trying to minimize the mapping time by reducing the evaluation
 * time of timings.
 *
 * @author mpelcat
 */
public class TimeKeeper implements Observer {

  /** If debug mode is activated, timing actions are traced. */
  // private boolean debugMode = true;

  /**
   * Current implementation: the same as in the ABC
   */
  protected MapperDAG implementation;

  /** Manager of the vertices ordering. */
  private final OrderManager orderManager;

  /** Vertices which timings need to be recomputed. */
  private final Set<MapperDAGVertex> dirtyVertices;

  /**
   * Constructor.
   *
   * @param implementation
   *          the implementation
   * @param orderManager
   *          the order manager
   */
  public TimeKeeper(final MapperDAG implementation, final OrderManager orderManager) {

    this.implementation = implementation;
    this.orderManager = orderManager;
    this.orderManager.addObserver(this);
    this.dirtyVertices = new HashSet<>();
  }

  /**
   * Resets the time keeper timings of the whole DAG.
   */
  public void resetTimings() {
    final Iterator<DAGVertex> it = this.implementation.vertexSet().iterator();

    while (it.hasNext()) {
      ((MapperDAGVertex) it.next()).getTiming().reset();
    }
  }

  // // Final Time Section

  /**
   * Gives the final time of the given vertex in the current implementation. If current
   * implementation information is not enough to calculate this timing, returns UNAVAILABLE
   *
   * @param vertex
   *          the vertex
   * @return the final time
   */
  public long getFinalTime(final MapperDAGVertex vertex) {

    long vertexfinaltime = VertexTiming.UNAVAILABLE;
    final VertexTiming timingproperty = vertex.getTiming();
    // XXX: Why don't we use timingproperty in the following code rather
    // than vertex.getTimin()?
    if (vertex.getTiming().hasCost()) {
      // Returns, if possible, TLevel + vertex timing
      vertexfinaltime = vertex.getTiming().getCost() + timingproperty.getTLevel();
    }

    return vertexfinaltime;
  }

  /**
   * Gives the total implementation time if possible. If current implementation information is not
   * enough to calculate this timing, returns UNAVAILABLE
   *
   * @return the final time
   */
  public long getFinalTime() {

    long finaltime = VertexTiming.UNAVAILABLE;

    for (final ComponentInstance o : this.orderManager.getArchitectureComponents()) {
      final long nextFinalTime = getFinalTime(o);
      // Returns TimingVertexProperty.UNAVAILABLE if at least one
      // vertex has no final time. Otherwise returns the highest final
      // time
      if (nextFinalTime == VertexTiming.UNAVAILABLE) {
        return VertexTiming.UNAVAILABLE;
      } else {
        finaltime = Math.max(finaltime, nextFinalTime);
      }
    }

    return finaltime;
  }

  /**
   * Gives the implementation time on the given operator if possible. It considers a partially
   * mapped graph and ignores the non mapped vertices
   *
   * @param component
   *          the component
   * @return the final time
   */
  public long getFinalTime(final ComponentInstance component) {

    long finaltime = VertexTiming.UNAVAILABLE;

    // XXX: Is this really useful? Can't we use directly component rather
    // than finalTimeRefCmp?
    ComponentInstance finalTimeRefCmp = null;
    for (final ComponentInstance o : this.orderManager.getArchitectureComponents()) {
      if (o.getInstanceName().equals(component.getInstanceName())) {
        finalTimeRefCmp = o;
      }
    }

    if (finalTimeRefCmp != null) {
      final List<MapperDAGVertex> sched = this.orderManager.getVertexList(finalTimeRefCmp);

      if ((sched != null) && !sched.isEmpty()) {
        finaltime = getFinalTime(sched.get(sched.size() - 1));
      } else {
        finaltime = 0;
      }
    }

    return finaltime;
  }

  /**
   * Update T levels.
   */
  public void updateTLevels() {
    final TLevelVisitor tLevelVisitor = new TLevelVisitor(this.dirtyVertices);
    tLevelVisitor.visit(this.implementation);
    this.dirtyVertices.clear();
  }

  /**
   * Update tand B levels.
   */
  public void updateTandBLevels() {
    final TLevelVisitor tLevelVisitor = new TLevelVisitor(this.dirtyVertices);
    final BLevelVisitor bLevelVisitor = new BLevelVisitor();
    tLevelVisitor.visit(this.implementation);
    bLevelVisitor.visit(this.implementation);
    this.dirtyVertices.clear();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void update(final Observable arg0, final Object arg1) {
    if (arg1 instanceof MapperDAGVertex) {
      this.dirtyVertices.add((MapperDAGVertex) arg1);
    } else if (arg1 instanceof Set<?>) {
      this.dirtyVertices.addAll((Set<MapperDAGVertex>) arg1);
    }
  }
}
