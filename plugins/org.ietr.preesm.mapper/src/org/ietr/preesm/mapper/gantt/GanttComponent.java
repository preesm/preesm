/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.mapper.gantt;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * A Gantt component is the information for 1 line in a Gantt chart
 * 
 * @author mpelcat
 */
public class GanttComponent {

	/**
	 * Unique ID.
	 */
	private String id;

	/**
	 * List of the tasks in the order of their start times.
	 */
	private LinkedList<GanttTask> tasks;

	public GanttComponent(String id) {
		super();
		this.id = id;
		tasks = new LinkedList<GanttTask>();
	}

	/**
	 * Inserting a task in the order of start times. Checking any
	 * incompatibility in the Gantt
	 */
	public boolean insertTask(GanttTask task) {

		if (tasks.size() != 0) {
			int index = tasks.size();
			// Looking where to insert the new task element
			for (int i = 0; i < tasks.size(); i++) {
				GanttTask t = tasks.get(i);
				// If t is after task, need to insert task
				if (t.getStartTime() > task.getStartTime() + task.getDuration()) {
					index = i;
				}
				// Checking for multiple concurrent insertions
				if (t.equals(task)) {
					WorkflowLogger.getLogger().log(
							Level.SEVERE,
							"Gantt: Trying to add to the Gantt chart several identical tasks: "
									+ t + " and " + task);
					return false;
				}
			}

			if (index == tasks.size()) {
				// task is added last
				tasks.addLast(task);
			} else {
				
				// Looking for overlaps with existing tasks
				// new task is added just before taskList.get(index), it should
				// not overlap with the previous taskList.get(index-1)
				if (index > 0) {
					GanttTask precedingTask = tasks.get(index-1);
					if (precedingTask.getStartTime() + precedingTask.getDuration() > task.getStartTime()) {
						WorkflowLogger.getLogger().log(
								Level.SEVERE,
								"Gantt: Two tasks are overlapping: " + precedingTask
										+ " and " + task);
						return false;
					}
				}
				
				tasks.add(index, task);
			}

		} else {
			tasks.add(task);
		}

		return true;
	}

	public String getId() {
		return id;
	}

	/**
	 * Comparing IDs to determine if two components are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof GanttComponent)
				&& (((GanttComponent) obj).getId().equals(id))) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return id;
	}

	/**
	 * End time of the last task associated to the component
	 */
	public long getEndTime() {
		if (tasks.getLast() != null) {
			GanttTask last = tasks.getLast();
			return last.getStartTime() + last.getDuration();
		}
		return 0l;
	}

	/**
	 * Start time of the first task associated to the component
	 */
	public long getStartTime() {
		if (tasks.getFirst() != null) {
			return tasks.getFirst().getStartTime();
		}
		return 0l;
	}

	public List<GanttTask> getTasks() {
		return tasks;
	}
}
