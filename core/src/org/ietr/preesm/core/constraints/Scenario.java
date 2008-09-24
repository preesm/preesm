package org.ietr.preesm.core.constraints;

public class Scenario implements IScenario {

	/**
	 * Manager of constraint groups
	 */
	private ConstraintGroupManager constraintgroupmanager = null;

	/**
	 * Manager of timings
	 */
	private TimingManager timingmanager = null;

	public Scenario() {
		constraintgroupmanager = new ConstraintGroupManager();
		timingmanager = new TimingManager();
	}

	public ConstraintGroupManager getConstraintGroupManager() {
		return constraintgroupmanager;
	}

	public TimingManager getTimingManager() {
		return timingmanager;
	}
}
