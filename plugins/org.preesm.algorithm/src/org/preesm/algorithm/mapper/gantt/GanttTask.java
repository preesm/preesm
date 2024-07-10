/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2012)
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
package org.preesm.algorithm.mapper.gantt;

import java.awt.Color;
import java.util.Objects;

/**
 * GanttTask carries information for a task displayed in a Gantt chart component.
 *
 * @author mpelcat
 */
public class GanttTask implements Comparable<GanttTask> {

  /** Start time of the task in the Gantt. */
  private final long startTime;

  /** Duration of the task in the Gantt. */
  private final long duration;

  /** ID displayed in the Gantt. */
  private final String id;

  /** Color **/
  private final Color color;

  /**
   * Instantiates a new gantt task.
   *
   * @param startTime
   *          the start time
   * @param duration
   *          the duration
   * @param id
   *          the id
   */
  public GanttTask(final long startTime, final long duration, final String id, final Color color) {
    super();
    this.startTime = startTime;
    this.duration = duration;
    this.id = id;
    this.color = color;
  }

  public long getStartTime() {
    return this.startTime;
  }

  public long getDuration() {
    return this.duration;
  }

  public String getId() {
    return this.id;
  }

  public Color getColor() {
    return this.color;
  }

  @Override
  public boolean equals(final Object obj) {
    return ((obj instanceof final GanttTask ganttTask) && (ganttTask.getId().equals(this.getId())));
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public String toString() {
    return "(" + this.id + "," + this.startTime + "," + this.duration + ")";
  }

  @Override
  public int compareTo(GanttTask o) {
    final int res = Long.compare(startTime, o.startTime);
    if (res != 0) {
      return res;
    }
    return id.compareTo(o.id);
  }

}
