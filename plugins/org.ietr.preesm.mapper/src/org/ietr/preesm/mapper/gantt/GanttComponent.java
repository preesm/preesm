/**
 * 
 */
package org.ietr.preesm.mapper.gantt;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import net.sf.dftools.workflow.tools.WorkflowLogger;

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
