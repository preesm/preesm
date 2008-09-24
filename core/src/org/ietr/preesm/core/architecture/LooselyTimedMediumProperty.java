/**
 * 
 */
package org.ietr.preesm.core.architecture;

/**
 * These properties are used by the loosely timed architecture simulator
 * to roughly evaluate the performance of an implementation
 *         
 * @author mpelcat
 */
public class LooselyTimedMediumProperty {

	/**
	 * Transfer speed in TU(Time Unit)/AU (Allocation Unit) The usual
	 * utilization is with cycles/Byte
	 */
	float speed = 1;

	public LooselyTimedMediumProperty(float speed) {
		super();
		this.speed = speed;
	}

	@Override
	public LooselyTimedMediumProperty clone() {
		return new LooselyTimedMediumProperty(this.getSpeed());
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
