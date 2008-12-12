/**
 * 
 */
package org.ietr.preesm.plugin.mapper.edgescheduling;

/**
 * Time interval for the transfer scheduling
 * 
 * @author mpelcat
 */
public class Interval {
	
	private int startTime;
	private int duration;
	private int totalOrderIndex;

	public Interval(int duration, int startTime, int totalOrderIndex) {
		super();
		this.duration = duration;
		this.startTime = startTime;
		this.totalOrderIndex = totalOrderIndex;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public int getDuration() {
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
