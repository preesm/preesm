/**
 * 
 */
package org.ietr.preesm.plugin.mapper.fastalgo;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.mapper.AbstractParameters;

/**
 * Parameters for list scheduling
 * 
 * @author pmenuet
 */
public class ListSchedulingParameters extends AbstractParameters {

	private ArchitectureSimulatorType simulatorType;

	/**
	 * 
	 */
	public ListSchedulingParameters(TextParameters textParameters) {
		super(textParameters);

		this.simulatorType = ArchitectureSimulatorType.fromString(textParameters.getVariable("simulatorType"));
	}

	public ListSchedulingParameters(ArchitectureSimulatorType simulatorType) {
		super();
		textParameters.addVariable("simulatorType", simulatorType.toString());
		this.simulatorType = simulatorType;
	}

	/**
	 * @return the simulatorType
	 */
	public ArchitectureSimulatorType getSimulatorType() {
		return simulatorType;
	}

	/**
	 * @param simulatorType
	 *            the simulatorType to set
	 */
	public void setSimulatorType(ArchitectureSimulatorType simulatorType) {
		this.simulatorType = simulatorType;
	}

}
