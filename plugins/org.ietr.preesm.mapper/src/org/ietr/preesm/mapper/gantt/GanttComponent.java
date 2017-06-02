/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

// TODO: Auto-generated Javadoc
/**
 * A Gantt component is the information for 1 line in a Gantt chart.
 *
 * @author mpelcat
 */
public class GanttComponent {

  /**
   * Unique ID.
   */
  private final String id;

  /**
   * List of the tasks in the order of their start times.
   */
  private final LinkedList<GanttTask> tasks;

  /**
   * Instantiates a new gantt component.
   *
   * @param id
   *          the id
   */
  public GanttComponent(final String id) {
    super();
    this.id = id;
    this.tasks = new LinkedList<>();
  }

  /**
   * Inserting a task in the order of start times. Checking any incompatibility in the Gantt
   *
   * @param task
   *          the task
   * @return true, if successful
   */
  public boolean insertTask(final GanttTask task) {

    if (this.tasks.size() != 0) {
      int index = this.tasks.size();
      // Looking where to insert the new task element
      for (int i = 0; i < this.tasks.size(); i++) {
        final GanttTask t = this.tasks.get(i);
        // If t is after task, need to insert task
        if (t.getStartTime() > (task.getStartTime() + task.getDuration())) {
          index = i;
        }
        // Checking for multiple concurrent insertions
        if (t.equals(task)) {
          WorkflowLogger.getLogger().log(Level.SEVERE, "Gantt: Trying to add to the Gantt chart several identical tasks: " + t + " and " + task);
          return false;
        }
      }

      if (index == this.tasks.size()) {
        // task is added last
        this.tasks.addLast(task);
      } else {

        // Looking for overlaps with existing tasks
        // new task is added just before taskList.get(index), it should
        // not overlap with the previous taskList.get(index-1)
        if (index > 0) {
          final GanttTask precedingTask = this.tasks.get(index - 1);
          if ((precedingTask.getStartTime() + precedingTask.getDuration()) > task.getStartTime()) {
            WorkflowLogger.getLogger().log(Level.SEVERE, "Gantt: Two tasks are overlapping: " + precedingTask + " and " + task);
            return false;
          }
        }

        this.tasks.add(index, task);
      }

    } else {
      this.tasks.add(task);
    }

    return true;
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
   * Comparing IDs to determine if two components are equal.
   *
   * @param obj
   *          the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(final Object obj) {
    if ((obj instanceof GanttComponent) && (((GanttComponent) obj).getId().equals(this.id))) {
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
    return this.id;
  }

  /**
   * End time of the last task associated to the component.
   *
   * @return the end time
   */
  public long getEndTime() {
    if (this.tasks.getLast() != null) {
      final GanttTask last = this.tasks.getLast();
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
    if (this.tasks.getFirst() != null) {
      return this.tasks.getFirst().getStartTime();
    }
    return 0L;
  }

  /**
   * Gets the tasks.
   *
   * @return the tasks
   */
  public List<GanttTask> getTasks() {
    return this.tasks;
  }
}
