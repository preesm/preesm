/**
 * 
 */
package org.ietr.preesm.core.scenario;

/**
 * Handles simulation parameters
 * 
 * @author mpelcat
 */
public class SimulationManager {

	/**
	 * Names of the main operator and medium
	 */
	private String mainMediumName = "";
	private String mainOperatorName = "";

	public SimulationManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getMainMediumName() {
		return mainMediumName;
	}

	public void setMainMediumName(String mainMediumName) {
		this.mainMediumName = mainMediumName;
	}

	public String getMainOperatorName() {
		return mainOperatorName;
	}

	public void setMainOperatorName(String mainOperatorName) {
		this.mainOperatorName = mainOperatorName;
	}
}
