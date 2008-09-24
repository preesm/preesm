/**
 * 
 */
package org.ietr.preesm.core.workflow.sources;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Representing the configuration for the scenario retrieved from launch tab.
 * It feeds a {@link ScenarioRetriever} to create the input scenario.
 * 
 * @author mpelcat
 *
 */
public class ScenarioConfiguration {

	/**
	 * ID used to save scenario file name in tab attributes
	 */
	public static final String ATTR_SCENARIO_FILE_NAME = "org.ietr.preesm.core.scenarioFileName";

	private String scenarioFileName = null;

	public ScenarioConfiguration(ILaunchConfiguration configuration) throws CoreException {
		super();
		this.scenarioFileName = configuration.getAttribute(
				ATTR_SCENARIO_FILE_NAME, "");
	}

	public String getScenarioFileName() {
		return scenarioFileName;
	}
}
