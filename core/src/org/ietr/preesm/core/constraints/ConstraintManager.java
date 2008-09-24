package org.ietr.preesm.core.constraints;

public class ConstraintManager {

	/**
	 * Manager of constraint groups
	 */
	private ConstraintGroupManager constraintgroupmanager = null;

	/**
	 * Manager of timings
	 */
	private TimingManager timingmanager = null;

	public ConstraintManager() {
		constraintgroupmanager = new ConstraintGroupManager();
		timingmanager = new TimingManager();
	}

	public ConstraintGroupManager getCGManager() {
		return constraintgroupmanager;
	}

	public TimingManager getTManager() {
		return timingmanager;
	}
}
