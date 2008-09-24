package org.ietr.preesm.core.constraints;

/**
 * Manager of implementation scenario.
 * 
 * @author mpelcat
 */
public interface IScenario {

	/**
	 * Manager of constraint groups linking vertices to operators.
	 */
	public ConstraintGroupManager getConstraintGroupManager();

	/**
	 * Manager of implementation timings.
	 */
	public TimingManager getTimingManager();
}
