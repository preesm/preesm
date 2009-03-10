/**
 * 
 */
package org.ietr.preesm.plugin.abc.edgescheduling;

/**
 * Time interval for the transfer scheduling
 * 
 * @author mpelcat
 */
public class Interval {
	
	private long startTime;
	private long duration;
	private int totalOrderIndex;

	public Interval(long duration, long startTime, int totalOrderIndex) {
		super();
		this.duration = duration;
		this.startTime = startTime;
		this.totalOrderIndex = totalOrderIndex;
	}
	
	public long getStartTime() {
		return startTime;
	}

	public long getDuration() {
		return duration;
	}

	public int getTotalOrderIndex() {
		return totalOrderIndex;
	}

	@Override
	public String toString() {
		return "<" + startTime + "," + duration + "," + totalOrderIndex + ">";
	}
	
	
}
