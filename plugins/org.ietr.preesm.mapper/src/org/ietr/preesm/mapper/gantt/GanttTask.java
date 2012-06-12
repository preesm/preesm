package org.ietr.preesm.mapper.gantt;

/**
 * GanttTask carries information for a task displayed in a Gantt chart component
 * 
 * @author mpelcat
 */
public class GanttTask {
	/**
	 * Start time of the task in the Gantt
	 */
	private long startTime = 0;

	/**
	 * Duration of the task in the Gantt
	 */
	private long duration = 0;

	/**
	 * ID displayed in the Gantt
	 */
	private String id = "";

	/**
	 * Component in which the task is displayed
	 */
	private GanttComponent component = null;

	public GanttTask(long startTime, long duration, String id,
			GanttComponent component) {
		super();
		this.startTime = startTime;
		this.duration = duration;
		this.id = id;
		this.component = component;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getDuration() {
		return duration;
	}

	public String getId() {
		return id;
	}

	public GanttComponent getComponent() {
		return component;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof GanttTask)
				&& (((GanttTask) obj).getId().equals(id))) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(" + id + "," + component + "," + startTime + "," + duration + ")";
	}
	
	
}
