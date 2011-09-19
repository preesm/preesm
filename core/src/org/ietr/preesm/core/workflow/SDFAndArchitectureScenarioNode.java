/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.util.HashMap;
import java.util.Map;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractScenarioImplementation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.SDFAndArchitectureScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Implementing the DFTools scenario node behavior for Preesm
 * 
 * @author mpelcat
 * 
 */
public class SDFAndArchitectureScenarioNode extends
		AbstractScenarioImplementation {

	/**
	 * The scenario node in Preesm outputs three elements: SDF, architecture and
	 * scenario
	 */
	@Override
	public Map<String, Object> extractData(String path)
			throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();

		// Retrieving the scenario from the given path
		ScenarioParser scenarioParser = new ScenarioParser();

		Path relativePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		SDFAndArchitectureScenario scenario = scenarioParser.parseXmlFile(file);

		// Retrieving the algorithm
		SDFGraph algorithm = ScenarioParser.getAlgorithm(scenario
				.getAlgorithmURL());

		// Retrieving the architecture
		MultiCoreArchitecture architecture = ScenarioParser
				.getArchitecture(scenario.getArchitectureURL());

		outputs.put("scenario", scenario);
		outputs.put("SDF", algorithm);
		outputs.put("architecture", architecture);
		return outputs;
	}

	@Override
	public String monitorMessage() {
		return "Scenario, algorithm and architecture parsing.";
	}

}
