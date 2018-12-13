/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
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
package org.preesm.algorithm.mapper.abc.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.utils.DesignTools;

/**
 * The scheduling order manager keeps a total order of the vertices and a partial order in each schedule. It is used by
 * the schedule edge adder to insert schedule edges. The scheduling order manager is observed by the time keeper and
 * reports the vertices which timings need to be updated.
 *
 * @author mpelcat
 */
public class OrderManager extends Observable {

  /** Contains the rank list of all the vertices in an implementation. */
  private Map<ComponentInstance, Schedule> schedules = null;

  /** total order of the vertices in the implementation. */
  private Schedule totalOrder = null;

  /**
   * Instantiates a new order manager.
   *
   * @param archi
   *          the archi
   */
  public OrderManager(final Design archi) {

    this.schedules = new LinkedHashMap<>();

    // Adding one schedule per component
    for (final ComponentInstance cmp : DesignTools.getComponentInstances(archi)) {
      this.schedules.put(cmp, new Schedule());
    }

    this.totalOrder = new Schedule();
  }

  /**
   * Find lastest pred index for op.
   *
   * @param cmp
   *          the cmp
   * @param refIndex
   *          the ref index
   * @return the int
   */
  public int findLastestPredIndexForOp(final ComponentInstance cmp, final int refIndex) {

    // Retrieves the schedule corresponding to the component
    final Schedule currentSched = getSchedule(cmp);
    if (currentSched == null) {
      throw new PreesmException("Schedule should not be null", new NullPointerException());
    }
    // Iterates the schedule to find the latest predecessor
    int maxPrec = -1;
    for (final MapperDAGVertex current : currentSched.getList()) {

      // Looking for the preceding vertex with maximum total order in
      // vertex schedule
      final int currentTotalOrder = totalIndexOf(current);

      if (currentTotalOrder < refIndex) {
        maxPrec = currentTotalOrder;
      }
    }

    return maxPrec;
  }

  /**
   * Considering that vertex already has a total order (is already in total order list), inserts it at the appropriate
   * position in its schedule.
   *
   * @param vertex
   *          the vertex
   */
  public void insertGivenTotalOrder(final MapperDAGVertex vertex) {

    if (vertex.hasEffectiveComponent()) {

      final ComponentInstance cmp = vertex.getEffectiveComponent();
      final int newSchedulingTotalOrder = totalIndexOf(vertex);
      final int maxPrec = findLastestPredIndexForOp(vertex.getEffectiveComponent(), newSchedulingTotalOrder);
      // Testing a possible synchronized vertex
      MapperDAGVertex elt = get(newSchedulingTotalOrder);
      if ((elt == null) || elt.equals(vertex)) {
        elt = vertex;
      } else {
        final String msg = "Error in sched order!!";
        throw new PreesmException(msg);
      }

      // Adds vertex or synchro vertices after its chosen predecessor
      final Schedule schedule = getSchedule(cmp);
      if (schedule == null) {
        throw new PreesmException("Schedule should not be null", new NullPointerException());
      }
      if (maxPrec >= 0) {
        final MapperDAGVertex previous = this.totalOrder.get(maxPrec);
        schedule.insertAfter(previous, elt);
      } else {
        schedule.addFirst(elt);
      }

    }

    // Notifies the time keeper that it should update the successors
    Set<MapperDAGVertex> vSet = this.totalOrder.getSuccessors(vertex);
    if ((vSet == null) || vSet.isEmpty()) {
      vSet = new LinkedHashSet<>();
    }
    vSet.add(vertex);
    setChanged();
    notifyObservers(vSet);
  }

  /**
   * If the input is a vertex, appends it at the end of one schedule and at the end of total order. If the input is
   * synschronizedVertices, appends it at the end of all concerned schedules and at the end of total order.
   *
   * @param elt
   *          the elt
   */
  public void addLast(final MapperDAGVertex elt) {

    if (elt instanceof MapperDAGVertex) {
      final MapperDAGVertex vertex = elt;
      if (vertex.hasEffectiveComponent()) {
        final ComponentInstance effectiveCmp = vertex.getEffectiveComponent();

        // Gets the schedule of vertex
        final Schedule currentSchedule = getSchedule(effectiveCmp);
        if (currentSchedule == null) {
          throw new PreesmException("Schedule should not be null", new NullPointerException());
        }

        currentSchedule.addLast(vertex);

        if (this.totalOrder.contains(vertex)) {
          this.totalOrder.remove(vertex);
        }

        this.totalOrder.addLast(vertex);
      }

      // Notifies the time keeper that it should update the vertex
      setChanged();
      notifyObservers(vertex);
    }
  }

  /**
   * Appends the vertex at the beginning of a schedule and at the end of total order.
   *
   * @param vertex
   *          the vertex
   */
  public void addFirst(final MapperDAGVertex vertex) {

    if (vertex.hasEffectiveComponent()) {
      final ComponentInstance effectiveCmp = vertex.getEffectiveComponent();

      // Gets the schedule of vertex
      final Schedule currentSchedule = getSchedule(effectiveCmp);

      currentSchedule.addFirst(vertex);

      if (this.totalOrder.contains(vertex)) {
        this.totalOrder.remove(vertex);
      }

      this.totalOrder.addFirst(vertex);
    }

    // Notifies the time keeper that it should update the successors
    setChanged();
    notifyObservers(new LinkedHashSet<>(this.totalOrder.getList()));
  }

  /**
   * Inserts vertex after previous.
   *
   * @param previous
   *          the previous
   * @param vertex
   *          the vertex
   */
  public void insertAfter(final MapperDAGVertex previous, final MapperDAGVertex vertex) {

    if (previous == null) {
      addLast(vertex);
    } else {

      if (previous.hasEffectiveComponent() && vertex.hasEffectiveComponent()) {

        if (!this.totalOrder.contains(vertex)) {
          if (this.totalOrder.indexOf(previous) >= 0) {
            this.totalOrder.insertAfter(previous, vertex);
          }
        }
        insertGivenTotalOrder(vertex);

      }
    }
  }

  /**
   * Inserts vertex before next.
   *
   * @param next
   *          the next
   * @param vertex
   *          the vertex
   */
  public void insertBefore(final MapperDAGVertex next, final MapperDAGVertex vertex) {

    if (next == null) {
      addFirst(vertex);
    } else {

      if (next.hasEffectiveComponent() && vertex.hasEffectiveComponent()) {

        if (!this.totalOrder.contains(vertex)) {
          if (this.totalOrder.indexOf(next) >= 0) {
            this.totalOrder.insertBefore(next, vertex);
          }
        }
        insertGivenTotalOrder(vertex);

      }
    }

  }

  /**
   * Inserts vertex after previous.
   *
   * @param index
   *          the index
   * @param vertex
   *          the vertex
   */
  public void insertAtIndex(final int index, final MapperDAGVertex vertex) {

    if ((index < this.totalOrder.size()) && (index >= 0)) {
      final MapperDAGVertex ref = this.totalOrder.get(index);
      insertBefore(ref, vertex);
    } else {
      addLast(vertex);
    }
  }

  /**
   * Gets the local scheduling order, -1 if not present.
   *
   * @param vertex
   *          the vertex
   * @return the int
   */
  public int localIndexOf(final MapperDAGVertex vertex) {

    if (vertex.hasEffectiveComponent()) {

      final Schedule sch = getSchedule(vertex.getEffectiveComponent());
      if (sch != null) {
        return sch.indexOf(vertex);
      }
    }

    return -1;
  }

  /**
   * Gets the total scheduling order.
   *
   * @param vertex
   *          the vertex
   * @return the int
   */
  public int totalIndexOf(final MapperDAGVertex vertex) {

    return this.totalOrder.indexOf(vertex);
  }

  /**
   * Gets the vertex with the given total scheduling order.
   *
   * @param totalOrderIndex
   *          the total order index
   * @return the mapper DAG vertex
   */
  public MapperDAGVertex get(final int totalOrderIndex) {
    final MapperDAGVertex elt = this.totalOrder.get(totalOrderIndex);
    return elt;
  }

  /**
   * Gets the scheduling components.
   *
   * @return the architecture components
   */
  public Set<ComponentInstance> getArchitectureComponents() {

    return this.schedules.keySet();

  }

  /**
   * Removes a given vertex.
   *
   * @param vertex
   *          the vertex
   * @param removeFromTotalOrder
   *          the remove from total order
   */
  public void remove(final MapperDAGVertex vertex, final boolean removeFromTotalOrder) {

    // Notifies the time keeper that it should update the successors
    Set<MapperDAGVertex> successors = this.totalOrder.getSuccessors(vertex);
    if (successors == null) {
      successors = new LinkedHashSet<>();
    }
    successors.add(vertex);
    setChanged();
    notifyObservers(successors);

    // If the vertex has an effective component,
    // removes it from the corresponding scheduling
    Schedule sch = null;
    if (vertex.hasEffectiveComponent()) {

      final ComponentInstance cmp = vertex.getEffectiveComponent();
      sch = getSchedule(cmp);
    } else { // Looks for the right scheduling to remove the vertex
      for (final Schedule locSched : this.schedules.values()) {
        if (locSched.contains(vertex)) {
          sch = locSched;
          break;
        }
      }
    }

    if (sch != null) {
      final MapperDAGVertex elt = sch.getScheduleElt(vertex);
      if (elt != null) {
        if (elt.equals(vertex)) {
          sch.remove(elt);
        }
      }
    }

    if (removeFromTotalOrder) {
      final MapperDAGVertex elt = this.totalOrder.getScheduleElt(vertex);

      if (elt != null) {
        this.totalOrder.remove(elt);
      }
    }

  }

  /**
   * Resets Total Order.
   */
  public void resetTotalOrder() {
    this.totalOrder.clear();

    for (final Schedule s : this.schedules.values()) {
      s.clear();
    }
  }

  /**
   * Reconstructs the total order using the total order stored in DAG. Creates synchronized vertices when several
   * vertices have the same order
   *
   * @param dag
   *          the dag
   */
  public void reconstructTotalOrderFromDAG(final MapperDAG dag) {

    resetTotalOrder();

    final List<DAGVertex> newTotalOrder = new ArrayList<>(dag.vertexSet());

    Collections.sort(newTotalOrder, new SchedulingOrderComparator());

    for (final DAGVertex vertex : newTotalOrder) {
      final MapperDAGVertex mVertex = (MapperDAGVertex) vertex;
      addLast(mVertex);
    }
  }

  /**
   * Sets the total order of each implementation property in DAG.
   *
   * @param dag
   *          the dag
   */
  public void tagDAG(final MapperDAG dag) {

    for (final MapperDAGVertex internalVertex : this.totalOrder.getList()) {
      final MapperDAGVertex vertex = dag.getMapperDAGVertex(internalVertex.getName());

      if (vertex != null) {
        tagVertex(vertex);
      }
    }
  }

  /**
   * Sets the total order of vertex implementation property in DAG.
   *
   * @param vertex
   *          the vertex
   */
  private void tagVertex(final MapperDAGVertex vertex) {

    vertex.setTotalOrder(this.totalOrder.indexOf(vertex));
  }

  /**
   * Gets the previous vertex in the same schedule. Searches in the synchronized vertices if any
   *
   * @param vertex
   *          the vertex
   * @return the previous
   */
  public MapperDAGVertex getPrevious(final MapperDAGVertex vertex) {

    MapperDAGVertex prevElt = null;
    MapperDAGVertex prevVertex = null;
    final ComponentInstance cmp = vertex.getEffectiveComponent();
    final Schedule schedule = getSchedule(cmp);

    if (schedule != null) {
      prevElt = schedule.getPrevious(vertex);

      if (prevElt instanceof MapperDAGVertex) {
        prevVertex = prevElt;
      }
    }

    return prevVertex;
  }

  /**
   * Gets the next vertex in the same schedule.
   *
   * @param vertex
   *          the vertex
   * @return the next
   */
  public MapperDAGVertex getNext(final MapperDAGVertex vertex) {
    MapperDAGVertex nextVertex = null;

    final ComponentInstance cmp = vertex.getEffectiveComponent();
    final Schedule schedule = getSchedule(cmp);

    if (schedule != null) {
      nextVertex = schedule.getNext(vertex);
    }

    return nextVertex;
  }

  /**
   * Gets the total order.
   *
   * @return the total order
   */
  public Schedule getTotalOrder() {
    return this.totalOrder;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.totalOrder.toString();
  }

  /**
   * Gets the schedule of a given component.
   *
   * @param cmp
   *          the cmp
   * @return the schedule
   */
  private Schedule getSchedule(final ComponentInstance cmp) {

    // Preventing from creating several schedules with same name
    for (final ComponentInstance o : this.schedules.keySet()) {
      if (o.getInstanceName().equals(cmp.getInstanceName())) {
        return this.schedules.get(o);
      }
    }
    return null;
  }

  /**
   * Gets the mapperdag vertex list of a given component. Splits the synchronized vertices objects into their components
   *
   * @param cmp
   *          the cmp
   * @return the vertex list
   */
  public List<MapperDAGVertex> getVertexList(final ComponentInstance cmp) {
    final Schedule s = getSchedule(cmp);
    final List<MapperDAGVertex> vList = new ArrayList<>();

    if (s != null) {
      for (final MapperDAGVertex elt : s.getList()) {
        if (elt instanceof MapperDAGVertex) {
          vList.add(elt);
        }
      }
    }

    return Collections.unmodifiableList(vList);
  }

  /**
   * Gets the busy time.
   *
   * @param c
   *          the c
   * @return the busy time
   */
  public long getBusyTime(final ComponentInstance c) {
    final Schedule sched = getSchedule(c);
    if (sched != null) {
      return sched.getBusyTime();
    }

    return 0L;
  }
}
