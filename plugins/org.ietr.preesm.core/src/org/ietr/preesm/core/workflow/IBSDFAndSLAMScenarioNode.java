/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.implement.AbstractScenarioImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;

/**
 * Implementing the new DFTools scenario node behavior for Preesm. This version
 * generates an architecture with the S-LAM2 meta-model type and an algorithm
 * with the IBSDF type
 * 
 * @author mpelcat
 * 
 */
public class IBSDFAndSLAMScenarioNode extends AbstractScenarioImplementation {

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

		PreesmScenario scenario;
		// Retrieving the algorithm
		SDFGraph algorithm;
		
		try {
			scenario = scenarioParser.parseXmlFile(file);

			algorithm = ScenarioParser.getAlgorithm(scenario
					.getAlgorithmURL());
		} catch (Exception e) {
			throw new WorkflowException(e.getMessage());
		}

		// Retrieving the architecture
		Design slamDesign = ScenarioParser.parseSlamDesign(scenario
				.getArchitectureURL());

		outputs.put("scenario", scenario);
		outputs.put("IBSDF", algorithm);
		outputs.put("S-LAM", slamDesign);
		return outputs;
	}

	@Override
	public String monitorMessage() {
		return "Scenario, algorithm and architecture parsing.";
	}

}
