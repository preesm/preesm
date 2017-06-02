/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.mapper.model.property;

import java.util.HashMap;
import java.util.Map;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * Property added to a DAG vertex to give its timing properties. Can be shared by several synchronous vertices.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class VertexTiming extends GroupProperty {

  /** The Constant UNAVAILABLE. */
  public static final long UNAVAILABLE = -1;

  /** time to execute the vertex. */
  private long cost;

  /**
   * B Level is the time between the vertex start and the total end of execution. Valid only with infinite homogeneous architecture simulator
   */
  private long bLevel;

  /** T Level is the time between the start of execution and the vertex start. */
  private long tLevel;

  /**
   * The total order range in the schedule. Each vertex ID is associated to its total order IDs must be consecutive to ensure possibility of synchronous
   * scheduling!
   */
  private Map<String, Integer> totalOrders;

  /**
   * Instantiates a new vertex timing.
   */
  public VertexTiming() {
    super();
    reset();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.model.property.GroupProperty#clone()
   */
  @Override
  public VertexTiming clone() {
    final VertexTiming property = (VertexTiming) super.clone();
    property.setBLevel(getBLevel());
    property.setTLevel(getTLevel());
    property.setCost(getCost());
    for (final String id : this.totalOrders.keySet()) {
      property.setTotalOrder(id, this.totalOrders.get(id));
    }
    return property;
  }

  /**
   * Reset.
   */
  public void reset() {
    this.cost = VertexTiming.UNAVAILABLE;
    this.tLevel = VertexTiming.UNAVAILABLE;
    this.bLevel = VertexTiming.UNAVAILABLE;
    this.totalOrders = new HashMap<>();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.model.property.GroupProperty#toString()
   */
  @Override
  public String toString() {
    return "";
  }

  /**
   * Gets the cost.
   *
   * @return the cost
   */
  public long getCost() {
    return this.cost;
  }

  /**
   * Sets the cost.
   *
   * @param cost
   *          the new cost
   */
  public void setCost(final long cost) {
    this.cost = cost;
  }

  /**
   * Checks for cost.
   *
   * @return true, if successful
   */
  public boolean hasCost() {
    return (this.cost != VertexTiming.UNAVAILABLE);
  }

  /**
   * Reset cost.
   */
  public void resetCost() {
    setCost(VertexTiming.UNAVAILABLE);
  }

  /**
   * Gets the b level.
   *
   * @return the b level
   */
  public long getBLevel() {
    return this.bLevel;
  }

  /**
   * Sets the b level.
   *
   * @param newbLevel
   *          the new b level
   */
  public void setBLevel(final long newbLevel) {
    this.bLevel = newbLevel;
  }

  /**
   * Reset B level.
   */
  public void resetBLevel() {
    this.bLevel = VertexTiming.UNAVAILABLE;
  }

  /**
   * Checks for B level.
   *
   * @return true, if successful
   */
  public boolean hasBLevel() {
    return this.bLevel != VertexTiming.UNAVAILABLE;
  }

  /**
   * Gets the t level.
   *
   * @return the t level
   */
  public long getTLevel() {
    return this.tLevel;
  }

  /**
   * Sets the t level.
   *
   * @param newtLevel
   *          the new t level
   */
  public void setTLevel(final long newtLevel) {
    this.tLevel = newtLevel;
  }

  /**
   * Reset T level.
   */
  public void resetTLevel() {
    this.tLevel = VertexTiming.UNAVAILABLE;
  }

  /**
   * Checks for T level.
   *
   * @return true, if successful
   */
  public boolean hasTLevel() {
    return (this.tLevel != VertexTiming.UNAVAILABLE);
  }

  /**
   * Gets the total order.
   *
   * @param v
   *          the v
   * @return the total order
   */
  public int getTotalOrder(final MapperDAGVertex v) {
    return this.totalOrders.get(v.getName());
  }

  /**
   * Sets the total order.
   *
   * @param vertexId
   *          the vertex id
   * @param totalOrder
   *          the total order
   */
  public void setTotalOrder(final String vertexId, final int totalOrder) {
    this.totalOrders.put(vertexId, totalOrder);
  }
}
