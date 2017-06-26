/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2012)
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
package org.ietr.preesm.mapper.gantt;

// TODO: Auto-generated Javadoc
/**
 * GanttTask carries information for a task displayed in a Gantt chart component.
 *
 * @author mpelcat
 */
public class GanttTask {

  /** Start time of the task in the Gantt. */
  private long startTime = 0;

  /** Duration of the task in the Gantt. */
  private long duration = 0;

  /** ID displayed in the Gantt. */
  private String id = "";

  /** Component in which the task is displayed. */
  private GanttComponent component = null;

  /**
   * Instantiates a new gantt task.
   *
   * @param startTime
   *          the start time
   * @param duration
   *          the duration
   * @param id
   *          the id
   * @param component
   *          the component
   */
  public GanttTask(final long startTime, final long duration, final String id, final GanttComponent component) {
    super();
    this.startTime = startTime;
    this.duration = duration;
    this.id = id;
    this.component = component;
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
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * Gets the component.
   *
   * @return the component
   */
  public GanttComponent getComponent() {
    return this.component;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj instanceof GanttTask) && (((GanttTask) obj).getId().equals(this.id))) {
      return true;
    }
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "(" + this.id + "," + this.component + "," + this.startTime + "," + this.duration + ")";
  }

}
