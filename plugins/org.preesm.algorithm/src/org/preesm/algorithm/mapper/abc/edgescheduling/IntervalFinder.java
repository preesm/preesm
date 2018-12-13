/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
package org.preesm.algorithm.mapper.abc.edgescheduling;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.preesm.algorithm.mapper.abc.order.OrderManager;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.property.VertexTiming;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;

// TODO: Auto-generated Javadoc
/**
 * During edge scheduling, one needs to find intervals to fit the transfers. This class deals with intervals in the
 * transfer scheduling
 *
 * @author mpelcat
 */
public class IntervalFinder {

  /** Contains the rank list of all the vertices in an implementation. */
  private OrderManager orderManager = null;

  /** The random. */
  private final Random random;

  /**
   * The Class FindType.
   */
  private static class FindType {

    /** The Constant largestFreeInterval. */
    public static final FindType largestFreeInterval = new FindType();

    /** The Constant earliestBigEnoughInterval. */
    public static final FindType earliestBigEnoughInterval = new FindType();

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      if (this == FindType.largestFreeInterval) {
        return "largestFreeInterval";
      }
      if (this == FindType.earliestBigEnoughInterval) {
        return "largestFreeInterval";
      }
      return "";
    }
  }

  /**
   * Instantiates a new interval finder.
   *
   * @param orderManager
   *          the order manager
   */
  public IntervalFinder(final OrderManager orderManager) {
    super();
    this.orderManager = orderManager;
    this.random = new Random(System.nanoTime());
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

    return findInterval(component, minVertex, maxVertex, FindType.largestFreeInterval, 0);

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

    return findInterval(component, minVertex, maxVertex, FindType.earliestBigEnoughInterval, 0);

  }

  /**
   * Find earliest big enough interval.
   *
   * @param component
   *          the component
   * @param minVertex
   *          the min vertex
   * @param maxVertex
   *          the max vertex
   * @param size
   *          the size
   * @return the interval
   */
  private Interval findEarliestBigEnoughInterval(final ComponentInstance component, final MapperDAGVertex minVertex,
      final MapperDAGVertex maxVertex, final long size) {

    return findInterval(component, minVertex, maxVertex, FindType.earliestBigEnoughInterval, size);

  }

  /**
   * Finds the largest free interval in a schedule between a minVertex and a maxVertex.
   *
   * @param component
   *          the component
   * @param minVertex
   *          the min vertex
   * @param maxVertex
   *          the max vertex
   * @param type
   *          the type
   * @param data
   *          the data
   * @return the interval
   */
  private Interval findInterval(final ComponentInstance component, final MapperDAGVertex minVertex,
      final MapperDAGVertex maxVertex, final FindType type, final long data) {

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

    int maxIndex = Integer.MAX_VALUE;
    if (maxVertex != null) {
      maxIndex = this.orderManager.totalIndexOf(maxVertex);
    } else {
      maxIndex = this.orderManager.getTotalOrder().size();
    }

    Interval oldInt = new Interval(0, 0, -1);
    Interval newInt = null;
    Interval freeInterval = new Interval(-1, -1, 0);

    if (schedule != null) {
      for (final MapperDAGVertex v : schedule) {
        final VertexTiming props = v.getTiming();

        // If we have the current vertex tLevel
        if (props.getTLevel() >= 0) {

          // newInt is the interval corresponding to the execution of
          // the vertex v: a non free interval
          newInt = new Interval(props.getCost(), props.getTLevel(), this.orderManager.totalIndexOf(v));

          // end of the preceding non free interval
          final long oldEnd = oldInt.getStartTime() + oldInt.getDuration();
          // latest date between the end of minVertex and the end of
          // oldInt
          final long available = Math.max(minIndexVertexEndTime, oldEnd);
          // Computing the size of the free interval
          final long freeIntervalSize = newInt.getStartTime() - available;

          if (type == FindType.largestFreeInterval) {
            // Verifying that newInt is in the interval of search
            if ((newInt.getTotalOrderIndex() > minIndex) && (newInt.getTotalOrderIndex() <= maxIndex)) {

              if (freeIntervalSize > freeInterval.getDuration()) {
                // The free interval takes the index of its
                // following task v.
                // Inserting a vertex in this interval means
                // inserting it before v.
                freeInterval = new Interval(freeIntervalSize, available, newInt.getTotalOrderIndex());
              }
            }
          } else if (type == FindType.earliestBigEnoughInterval) {
            if ((newInt.getTotalOrderIndex() > minIndex) && (newInt.getTotalOrderIndex() <= maxIndex)) {

              if (freeIntervalSize >= data) {
                // The free interval takes the index of its
                // following task v.
                // Inserting a vertex in this interval means
                // inserting it before v.
                freeInterval = new Interval(freeIntervalSize, available, newInt.getTotalOrderIndex());
                break;
              }
            }
          }
          oldInt = newInt;
        }
      }
    }

    return freeInterval;

  }

  /**
   * Display current schedule.
   *
   * @param vertex
   *          the vertex
   * @param source
   *          the source
   */
  public void displayCurrentSchedule(final TransferVertex vertex, final MapperDAGVertex source) {

    final ComponentInstance component = vertex.getEffectiveComponent();
    final List<MapperDAGVertex> schedule = this.orderManager.getVertexList(component);

    final VertexTiming sourceProps = source.getTiming();
    long availability = sourceProps.getTLevel() + sourceProps.getCost();
    if (sourceProps.getTLevel() < 0) {
      availability = -1;
    }

    String trace = "schedule of " + vertex.getName() + " available at " + availability + ": ";

    if (schedule != null) {
      for (final MapperDAGVertex v : schedule) {
        final VertexTiming props = v.getTiming();
        if (props.getTLevel() >= 0) {
          trace += "<" + props.getTLevel() + "," + (props.getTLevel() + props.getCost()) + ">";
        }
      }
    }

    PreesmLogger.getLogger().log(Level.INFO, trace);
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
          final int randomVal = this.random.nextInt(targetIndex - sourceIndex);
          index = sourceIndex + randomVal;
        }
      }

    }

    return index;
  }

  /**
   * Returns the best index to schedule vertex in total order.
   *
   * @param vertex
   *          the vertex
   * @param size
   *          the size
   * @return the index of first big enough hole
   */
  public int getIndexOfFirstBigEnoughHole(final MapperDAGVertex vertex, final long size) {
    int index = -1;
    final int latePred = getLatestPredecessorIndex(vertex);
    final int earlySuc = getEarliestsuccessorIndex(vertex);

    final ComponentInstance op = vertex.getEffectiveOperator();
    final MapperDAGVertex source = (latePred == -1) ? null : this.orderManager.get(latePred);
    final MapperDAGVertex target = (earlySuc == -1) ? null : this.orderManager.get(earlySuc);

    // Finds the largest free hole after the latest predecessor
    if (op != null) {
      final Interval largestInterval = findEarliestBigEnoughInterval(op, source, target, size);

      // If it is big enough, use it
      if (largestInterval.getDuration() >= 0) {
        index = largestInterval.getTotalOrderIndex();
      } else {
        index = -1;
      }

    }

    return index;
  }

  /**
   * Returns the earliest index after the last predecessor.
   *
   * @param vertex
   *          the vertex
   * @return the earliest index
   */
  public int getEarliestIndex(final MapperDAGVertex vertex) {
    int latePred = getLatestPredecessorIndex(vertex);

    if (latePred != -1) {
      latePred++;
    }
    return latePred;
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
