package org.ietr.preesm.core.scenario;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class ScenarioUtils {
	/**
	 * Util method generating a name for a given PreesmSceario from its
	 * architecture and algorithm
	 * 
	 * @param scenario
	 *            the PreesmScenario for which we need a name
	 * @return
	 */
	public static String getScenarioName(PreesmScenario scenario) {
		IPath algoPath = new Path(scenario.getAlgorithmURL())
				.removeFileExtension();
		String algoName = algoPath.lastSegment();
		IPath archiPath = new Path(scenario.getArchitectureURL())
				.removeFileExtension();
		String archiName = archiPath.lastSegment();
		return algoName + "_" + archiName;
	}
}
