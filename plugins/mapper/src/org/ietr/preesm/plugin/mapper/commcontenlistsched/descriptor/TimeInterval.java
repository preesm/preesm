package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

public class TimeInterval implements Comparable<TimeInterval> {

	private int startTime = 0;

	private int finishTime = 0;

	public TimeInterval(int startTime, int finishTime) {
		this.startTime = startTime;
		this.finishTime = finishTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	@Override
	public int compareTo(TimeInterval arg0) {
		return (startTime - arg0.getStartTime());
	}

}
