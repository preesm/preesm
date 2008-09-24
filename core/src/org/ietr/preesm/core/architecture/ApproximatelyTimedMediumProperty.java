/**
 * 
 */
package org.ietr.preesm.core.architecture;

/**
 * These properties are used by the approximately timed architecture
 * simulator to evaluate the performance of an implementation
 *         
 * @author mpelcat
 */
public class ApproximatelyTimedMediumProperty {

	/**
	 * Transmission overhead on sender in TU(Time Unit) The usual utilization is
	 * with cycles
	 */
	int overhead = 0;

	/**
	 * Reception time on receiver in TU(Time Unit) The usual utilization is with
	 * cycles
	 */
	int receptionTime = 0;

	/**
	 * Transfer speed in TU(Time Unit)/AU (Allocation Unit) The usual
	 * utilization is with cycles/Byte
	 * 
	 * The speed can depend on parameters like data size
	 */
	int speed = 0;

	public ApproximatelyTimedMediumProperty(int speed, int overhead,
			int receptionTime) {
		super();
		this.speed = speed;
		this.overhead = overhead;
		this.receptionTime = receptionTime;
	}

	@Override
	public ApproximatelyTimedMediumProperty clone() {
		return new ApproximatelyTimedMediumProperty(this.getSpeed(), this
				.getOverhead(), this.getReceptionTime());
	}

	/**
	 * @return the overhead
	 */
	public int getOverhead() {
		return overhead;
	}

	/**
	 * @return the receptionTime
	 */
	public int getReceptionTime() {
		return receptionTime;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}
}
