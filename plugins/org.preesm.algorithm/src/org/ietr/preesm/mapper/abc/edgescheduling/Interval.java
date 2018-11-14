/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.mapper.abc.edgescheduling;

// TODO: Auto-generated Javadoc
/**
 * Time interval for the transfer scheduling.
 *
 * @author mpelcat
 */
public class Interval {

  /** The start time. */
  private final long startTime;

  /** The duration. */
  private final long duration;

  /** The total order index. */
  private final int totalOrderIndex;

  /**
   * Instantiates a new interval.
   *
   * @param duration
   *          the duration
   * @param startTime
   *          the start time
   * @param totalOrderIndex
   *          the total order index
   */
  public Interval(final long duration, final long startTime, final int totalOrderIndex) {
    super();
    this.duration = duration;
    this.startTime = startTime;
    this.totalOrderIndex = totalOrderIndex;
  }

  /**
   * Gets the start time.
   *
   * @return the start time
   */
  public long getStartTime() {
    return this.startTime;
  }

  /**
   * Gets the duration.
   *
   * @return the duration
   */
  public long getDuration() {
    return this.duration;
  }

  /**
   * Gets the total order index.
   *
   * @return the total order index
   */
  public int getTotalOrderIndex() {
    return this.totalOrderIndex;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "<" + this.startTime + "," + this.duration + "," + this.totalOrderIndex + ">";
  }

}
