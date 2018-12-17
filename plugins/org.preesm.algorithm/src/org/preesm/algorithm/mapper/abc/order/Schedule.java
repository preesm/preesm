/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * A schedule represents the consecutive tasks mapped on a single component.
 *
 * @author mpelcat
 */
public class Schedule {

  /** The ordered list of vertices in this schedule. */
  private final LinkedList<MapperDAGVertex> elementList;

  /** The total time of the schedule vertices. */
  private long busyTime;

  /**
   * Instantiates a new schedule.
   */
  public Schedule() {

    super();
    this.elementList = new LinkedList<>();
    resetBusyTime();
  }

  /**
   * Appends a vertex at the end of the schedule.
   *
   * @param vertex
   *          the vertex
   */
  public void addLast(final MapperDAGVertex vertex) {
    if (!contains(vertex)) {
      if (vertex.getTiming().hasCost()) {
        this.busyTime += vertex.getTiming().getCost();
      }
      this.elementList.addLast(vertex);
    }
  }

  /**
   * Inserts a vertex at the beginning of the schedule.
   *
   * @param vertex
   *          the vertex
   */
  public void addFirst(final MapperDAGVertex vertex) {
    if (!contains(vertex)) {
      if (vertex.getTiming().hasCost()) {
        this.busyTime += vertex.getTiming().getCost();
      }
      this.elementList.addFirst(vertex);
    }
  }

  /**
   * Inserts a vertex after the given one.
   *
   * @param previous
   *          the previous
   * @param vertex
   *          the vertex
   */
  public void insertAfter(final MapperDAGVertex previous, final MapperDAGVertex vertex) {

    if (!contains(vertex)) {
      // Updating schedule busy time
      if (vertex.getTiming().hasCost()) {
        this.busyTime += vertex.getTiming().getCost();
      }

      final int prevIndex = indexOf(previous);
      if (prevIndex >= 0) {
        if ((prevIndex + 1) < this.elementList.size()) {
          final MapperDAGVertex next = this.elementList.get(prevIndex + 1);
          this.elementList.add(indexOf(next), vertex);
        } else {
          this.elementList.addLast(vertex);
        }
      }
    }
  }

  /**
   * Inserts a vertex before the given one.
   *
   * @param next
   *          the next
   * @param vertex
   *          the vertex
   */
  public void insertBefore(final MapperDAGVertex next, final MapperDAGVertex vertex) {
    if (!contains(vertex)) {
      if (vertex.getTiming().hasCost()) {
        this.busyTime += vertex.getTiming().getCost();
      }

      final int nextIndex = indexOf(next);
      if (nextIndex >= 0) {
        this.elementList.add(nextIndex, vertex);
      }
    }
  }

  /**
   * Clear.
   */
  public void clear() {
    resetBusyTime();

    this.elementList.clear();
  }

  /**
   * Reset busy time.
   */
  private void resetBusyTime() {
    this.busyTime = 0;
  }

  /**
   * Removes the.
   *
   * @param element
   *          the element
   */
  public void remove(final MapperDAGVertex element) {
    if (this.elementList.contains(element)) {
      if (element.getTiming().hasCost()) {
        this.busyTime -= element.getTiming().getCost();
      }

      this.elementList.remove(element);
    }
  }

  // Access without modification

  /**
   * Gets the.
   *
   * @param i
   *          the i
   * @return the mapper DAG vertex
   */
  public MapperDAGVertex get(final int i) {
    return this.elementList.get(i);
  }

  /**
   * Gets the last.
   *
   * @return the last
   */
  public MapperDAGVertex getLast() {
    return this.elementList.getLast();
  }

  /**
   * Gets the previous vertex in the current schedule.
   *
   * @param vertex
   *          the vertex
   * @return the previous
   */
  public MapperDAGVertex getPrevious(final MapperDAGVertex vertex) {
    final int index = indexOf(vertex);
    if (index <= 0) {
      return null;
    } else {
      return (this.elementList.get(index - 1));
    }
  }

  /**
   * Gets the next vertex in the current schedule.
   *
   * @param vertex
   *          the vertex
   * @return the next
   */
  public MapperDAGVertex getNext(final MapperDAGVertex vertex) {
    final int currentIndex = indexOf(vertex);
    if ((currentIndex < 0) || (currentIndex >= (this.elementList.size() - 1))) {
      return null;
    } else {
      return (this.elementList.get(currentIndex + 1));
    }
  }

  /**
   * Gets the next vertices in the current schedule.
   *
   * @param vertex
   *          the vertex
   * @return the successors
   */
  public Set<MapperDAGVertex> getSuccessors(final MapperDAGVertex vertex) {
    final Set<MapperDAGVertex> vSet = new LinkedHashSet<>();
    final int currentIndex = indexOf(vertex);
    if ((currentIndex < 0) || (currentIndex >= this.elementList.size())) {
      return null;
    }

    for (int i = currentIndex + 1; i < this.elementList.size(); i++) {
      vSet.add(this.elementList.get(i));
    }
    return vSet;
  }

  /**
   * Giving the index of the vertex if present in the list.
   *
   * @param v
   *          the v
   * @return the int
   */
  public int indexOf(final MapperDAGVertex v) {
    return this.elementList.indexOf(getScheduleElt(v));
  }

  /**
   * Giving the vertex if present in the list.
   *
   * @param v
   *          the v
   * @return the schedule elt
   */
  public MapperDAGVertex getScheduleElt(final MapperDAGVertex v) {
    final int index = this.elementList.indexOf(v);

    // Searching in synchronized vertices
    if (index != -1) {
      return v;
    }

    return null;
  }

  /**
   * Looks into the synchronized vertices to extract the vertex.
   *
   * @param v
   *          the v
   * @return true, if successful
   */
  public boolean contains(final MapperDAGVertex v) {
    return getScheduleElt(v) != null;
  }

  /**
   * Checks if is empty.
   *
   * @return true, if is empty
   */
  public boolean isEmpty() {
    return this.elementList.isEmpty();
  }

  /**
   * Gets the list.
   *
   * @return the list
   */
  public List<MapperDAGVertex> getList() {
    return Collections.unmodifiableList(this.elementList);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.elementList.toString();
  }

  /**
   * Converts this schedule to a list associating a vertex to its rank.
   *
   * @return the vertex order list
   */
  public VertexOrderList toOrderList() {

    final VertexOrderList order = new VertexOrderList();

    for (final MapperDAGVertex elt : this.elementList) {
      if (elt instanceof MapperDAGVertex) {
        final MapperDAGVertex v = elt;
        final VertexOrderList.OrderProperty op = order.new OrderProperty(v.getName(), indexOf(v));
        order.addLast(op);
      }
    }

    return order;
  }

  /**
   * Gets the busy time.
   *
   * @return the busy time
   */
  public long getBusyTime() {
    return this.busyTime;
  }

  /**
   * Size.
   *
   * @return the int
   */
  public int size() {
    return this.elementList.size();
  }
}
