/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2025) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2025)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2014)
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
package org.preesm.algorithm.mapper.abc.edgescheduling;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.preesm.algorithm.mapper.abc.order.OrderManager;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.property.VertexTiming;
import org.preesm.model.slam.ComponentInstance;

/**
 * During edge scheduling, one needs to find intervals to fit the transfers. This class deals with intervals in the
 * transfer scheduling
 *
 * @author mpelcat
 */
public class IntervalFinder {

  /** Contains the rank list of all the vertices in an implementation. */
  private OrderManager orderManager = null;

  /**
   * Instantiates a new interval finder.
   *
   * @param orderManager
   *          the order manager
   */
  public IntervalFinder(final OrderManager orderManager) {
    super();
    this.orderManager = orderManager;
  }

  /**
   * Find earliest non null interval.
   *
   * @param component
   *          the component
   * @param minVertex
   *          the min vertex
   * @param maxVertex
   *          the max vertex
   * @return the interval
   */
  public Interval findEarliestNonNullInterval(final ComponentInstance component, final MapperDAGVertex minVertex,
      final MapperDAGVertex maxVertex) {

    final int data = 0;

    final List<MapperDAGVertex> schedule = this.orderManager.getVertexList(component);

    long minIndexVertexEndTime = -1;
    int minIndex = -1;

    if (minVertex != null) {
      minIndex = this.orderManager.totalIndexOf(minVertex);

      final VertexTiming props = minVertex.getTiming();
      if (props.getTLevel() >= 0) {
        minIndexVertexEndTime = props.getTLevel() + props.getCost();
      }
    }

    final int maxIndex;
    if (maxVertex != null) {
      maxIndex = this.orderManager.totalIndexOf(maxVertex);
    } else {
      maxIndex = this.orderManager.getTotalOrder().size();
    }

    Interval oldInt = new Interval(0, 0, -1);
    Interval newInt = null;
    Interval freeInterval = new Interval(-1, -1, 0);

    if (schedule == null) {
      return freeInterval;
    }

    for (final MapperDAGVertex v : schedule) {
      final VertexTiming props = v.getTiming();

      // If we have the current vertex tLevel
      if (props.getTLevel() >= 0) {

        final int vertexTotalOrderIndex = this.orderManager.totalIndexOf(v);

        // newInt is the interval corresponding to the execution of
        // the vertex v: a non free interval
        newInt = new Interval(props.getCost(), props.getTLevel(), vertexTotalOrderIndex);

        // end of the preceding non free interval
        final long oldEnd = oldInt.getStartTime() + oldInt.getDuration();
        // latest date between the end of minVertex and the end of
        // oldInt
        final long available = Math.max(minIndexVertexEndTime, oldEnd);
        // Computing the size of the free interval
        final long freeIntervalSize = newInt.getStartTime() - available;

        if ((newInt.getTotalOrderIndex() > minIndex) && (newInt.getTotalOrderIndex() <= maxIndex)
            && freeIntervalSize >= data) {
          // The free interval takes the index of its following task v.
          // Inserting a vertex in this interval means inserting it before v.
          freeInterval = new Interval(freeIntervalSize, available, newInt.getTotalOrderIndex());
          break;
        }
        oldInt = newInt;
      }
    }

    return freeInterval;
  }

  /**
   * Finds the largest free interval in a schedule.
   *
   * @param component
   *          the component
   * @param minVertex
   *          the min vertex
   * @param maxVertex
   *          the max vertex
   * @return the interval
   */

  public Interval findLargestFreeInterval(final ComponentInstance component, final MapperDAGVertex minVertex,
      final MapperDAGVertex maxVertex) {

    final List<MapperDAGVertex> schedule = this.orderManager.getVertexList(component);

    if (schedule == null || schedule.isEmpty()) {
      return new Interval(-1, -1, 0);
    }

    final long minIndexVertexEndTime;
    final int minIndex;
    int minIterIndex = -1;

    if (minVertex != null) {

      minIndex = this.orderManager.totalIndexOf(minVertex);
      minIterIndex = schedule.indexOf(minVertex);

      final VertexTiming props = minVertex.getTiming();
      if (props.getTLevel() >= 0) {
        minIndexVertexEndTime = props.getTLevel() + props.getCost();
      } else {
        minIndexVertexEndTime = -1;
      }
    } else {
      minIndex = -1;
      minIndexVertexEndTime = -1;
    }

    if (minIterIndex == -1) {
      minIterIndex = 0;
    }

    final int maxIndex;
    final int maxIterIndex;
    if (maxVertex != null) {
      maxIndex = this.orderManager.totalIndexOf(maxVertex);
      maxIterIndex = schedule.indexOf(maxVertex);
    } else {
      maxIndex = this.orderManager.getTotalOrder().size();
      maxIterIndex = schedule.size();
    }

    // map to option, filter some, max Interval.getDuration
    final Stream<Interval> stream = IntStream.range(minIterIndex, maxIterIndex).parallel()
        .mapToObj(curIndex -> getOptionalInterval(schedule, curIndex, minIndexVertexEndTime, minIndex, maxIndex))
        .filter(Optional::isPresent).map(Optional::get);

    final Stream<Interval> stream2 = Stream.concat(Stream.of(new Interval(-1, -1, 0)), stream);

    // No need to check Option, we manually added a valid element
    return stream2.max(Comparator.comparing(Interval::getDuration)).get();
  }

  Optional<Interval> getOptionalInterval(List<MapperDAGVertex> schedule, int vertexIndex, long minIndexVertexEndTime,
      int minIndex, int maxIndex) {

    final MapperDAGVertex v = schedule.get(vertexIndex);
    final VertexTiming props = v.getTiming();

    // If we have the current vertex tLevel
    if (props.getTLevel() < 0) {
      return Optional.empty();
    }

    final int vertexTotalOrderIndex = this.orderManager.totalIndexOf(v);

    // If current vertex is outside of lookup window
    if (vertexTotalOrderIndex <= minIndex || vertexTotalOrderIndex > maxIndex) {
      return Optional.empty();
    }

    final Interval newInt = new Interval(props.getCost(), props.getTLevel(), vertexTotalOrderIndex);

    // get valid prev vertex
    Interval oldInt = new Interval(-1, -1, 0);

    int i = 1;
    // Seems like the vertex at vertexIndex-1 always have a valid start time, making loop useless
    while (oldInt.getStartTime() < 0) {

      if (vertexIndex - i < 0) {
        oldInt = new Interval(0, 0, -1);
        break;
      }

      final MapperDAGVertex prevVertex = schedule.get(vertexIndex - i);
      // don't care about total order here
      oldInt = new Interval(prevVertex.getTiming().getCost(), prevVertex.getTiming().getTLevel(), -1);

      i++;
    }

    // end of the preceding non free interval
    final long oldEnd = oldInt.getStartTime() + oldInt.getDuration();
    // latest date between the end of minVertex and the end of oldInt
    final long available = Math.max(minIndexVertexEndTime, oldEnd);
    // Computing the size of the free interval
    final long freeIntervalSize = newInt.getStartTime() - available;

    return Optional.of(new Interval(freeIntervalSize, available, newInt.getTotalOrderIndex()));

  }

  /**
   * Gets the order manager.
   *
   * @return the order manager
   */
  public OrderManager getOrderManager() {
    return this.orderManager;
  }

  /**
   * Returns the best index to schedule vertex in total order.
   *
   * @param vertex
   *          the vertex
   * @param minimalHoleSize
   *          the minimal hole size
   * @return the best index
   */
  public int getBestIndex(final MapperDAGVertex vertex, final long minimalHoleSize) {
    int index = -1;
    final int latePred = getLatestPredecessorIndex(vertex);
    final int earlySuc = getEarliestsuccessorIndex(vertex);

    final ComponentInstance op = vertex.getEffectiveOperator();
    final MapperDAGVertex source = (latePred == -1) ? null : this.orderManager.get(latePred);
    final MapperDAGVertex target = (earlySuc == -1) ? null : this.orderManager.get(earlySuc);

    // Finds the largest free hole after the latest predecessor
    if (op != null) {
      final Interval largestInterval = findLargestFreeInterval(op, source, target);

      // If it is big enough, use it
      if (largestInterval.getDuration() > minimalHoleSize) {
        index = largestInterval.getTotalOrderIndex();
      } else if (latePred != -1) {
        // Otherwise, place the vertex randomly
        final int sourceIndex = latePred + 1;
        int targetIndex = earlySuc;
        if (targetIndex == -1) {
          targetIndex = this.orderManager.getTotalOrder().size();
        }

        if ((targetIndex - sourceIndex) > 0) {
          index = sourceIndex;
        }
      }

    }

    return index;
  }

  /**
   * Returns the highest index of vertex predecessors.
   *
   * @param testVertex
   *          the test vertex
   * @return the latest predecessor index
   */
  private int getLatestPredecessorIndex(final MapperDAGVertex testVertex) {
    int index = -1;

    for (final MapperDAGVertex v : testVertex.getPredecessors(true).keySet()) {
      index = Math.max(index, this.orderManager.totalIndexOf(v));
    }

    return index;
  }

  /**
   * Returns the lowest index of vertex successors.
   *
   * @param testVertex
   *          the test vertex
   * @return the earliestsuccessor index
   */
  private int getEarliestsuccessorIndex(final MapperDAGVertex testVertex) {
    int index = Integer.MAX_VALUE;

    for (final MapperDAGVertex v : testVertex.getSuccessors(true).keySet()) {
      index = Math.min(index, this.orderManager.totalIndexOf(v));
    }

    if (index == Integer.MAX_VALUE) {
      index = -1;
    }

    return index;
  }
}
