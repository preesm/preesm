/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.preesm.algorithm.mapper.model.property;

import org.preesm.commons.CloneableProperty;

// TODO: Auto-generated Javadoc
/**
 * Property added to a DAG edge to give its timing properties. Only used within ABCs.
 *
 * @author mpelcat
 */
public class EdgeTiming implements CloneableProperty<EdgeTiming> {

  /** The Constant UNAVAILABLE. */
  private static final long UNAVAILABLE = -1;

  /** time to execute the edge. */
  private long cost;

  /**
   * Instantiates a new edge timing.
   */
  public EdgeTiming() {
    super();
    reset();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#clone()
   */
  @Override
  public EdgeTiming copy() {
    final EdgeTiming property = new EdgeTiming();
    property.setCost(getCost());
    return property;
  }

  /**
   * Reset.
   */
  public void reset() {
    this.cost = EdgeTiming.UNAVAILABLE;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "cost: " + this.cost;
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
    return (this.cost != EdgeTiming.UNAVAILABLE);
  }

  /**
   * Reset cost.
   */
  public void resetCost() {
    setCost(EdgeTiming.UNAVAILABLE);
  }
}
