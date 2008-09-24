/**
 * 
 */
package org.ietr.preesm.core.architecture;

/**
 * These properties are used by the timed architecture simulator to
 * evaluate the performance of an implementation
 * @author mpelcat
 */
public class MediumProperty {

	/**
	 * Transfer inverse speed in TU(Time Unit)/AU (Allocation Unit) The usual
	 * utilization is with cycles/Byte
	 * 
	 * The speed can depend on parameters like data size
	 */
	float invSpeed = 0f;

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

	public MediumProperty(float invSpeed, int overhead, int receptionTime) {
		super();
		this.invSpeed = invSpeed;
		this.overhead = overhead;
		this.receptionTime = receptionTime;
	}

	@Override
	public MediumProperty clone() {
		return new MediumProperty(this.getInvSpeed(), this.getOverhead(), this
				.getReceptionTime());
	}

	/**
	 * @return the speed
	 */
	public float getInvSpeed() {
		return invSpeed;
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
}
