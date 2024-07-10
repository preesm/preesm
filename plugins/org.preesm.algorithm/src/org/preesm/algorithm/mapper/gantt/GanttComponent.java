/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
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

import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;
import java.util.logging.Level;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.utils.ComponentInstanceNameComparator;

/**
 * A Gantt component is the information for 1 line in a Gantt chart.
 *
 * @author mpelcat
 */
public class GanttComponent implements Comparable<GanttComponent> {

  /**
   * Unique ID.
   */
  private final String id;

  /**
   * List of the tasks in the order of their start times.
   */
  private final TreeSet<GanttTask> tasks;

  /**
   * Instantiates a new gantt component.
   *
   * @param id
   *          the id
   */
  public GanttComponent(final String id) {
    super();
    this.id = id;
    this.tasks = new TreeSet<>();
  }

  /**
   * Inserting a task in the order of start times. Checking any incompatibility in the Gantt
   *
   * @param task
   *          the task
   * @return true, if successful
   */
  public boolean insertTask(final GanttTask task) {

    final boolean inserted = tasks.add(task);
    if (!inserted) {
      final String message = "Gantt: Trying to add to the Gantt chart several identical tasks: " + task;
      PreesmLogger.getLogger().log(Level.SEVERE, message);
      return false;
    }

    final GanttTask prev = tasks.lower(task);
    final GanttTask next = tasks.higher(task);
    final long tss = task.getStartTime();
    final long dur = task.getDuration();

    if ((prev != null && (prev.getStartTime() + prev.getDuration() > tss || prev.getStartTime() == tss))
        || (next != null && (tss + dur > next.getStartTime() || next.getStartTime() == tss))) {
      final String message = "Gantt: task " + task + " is overlapping after being inserted between tasks " + prev
          + " and " + next;
      PreesmLogger.getLogger().log(Level.SEVERE, message);
      return false;
    }
    return true;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(final Object obj) {
    return ((obj instanceof final GanttComponent ganttComp) && (ganttComp.getId().equals(this.getId())));
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getId());
  }

  @Override
  public String toString() {
    return this.getId();
  }

  /**
   * End time of the last task associated to the component.
   *
   * @return the end time
   */
  public long getEndTime() {
    final GanttTask last = tasks.last();
    if (last != null) {
      return last.getStartTime() + last.getDuration();
    }
    return 0L;
  }

  /**
   * Start time of the first task associated to the component.
   *
   * @return the start time
   */
  public long getStartTime() {
    final GanttTask first = tasks.first();
    if (first != null) {
      return first.getStartTime();
    }
    return 0L;
  }

  /**
   * Gets the tasks.
   *
   * @return the tasks
   */
  public Collection<GanttTask> getTasks() {
    return tasks;
  }

  @Override
  public int compareTo(GanttComponent arg0) {
    return ComponentInstanceNameComparator.compareStatic(this.id, arg0.id);
  }
}
