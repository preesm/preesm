/**
 * 
 */
package org.ietr.preesm.experiment.model.transformation;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.implement.AbstractScenarioImplementation;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.experiment.core.piscenario.serialize.PiScenarioParser;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * Implementing the new DFTools scenario node behavior for Preesm. This version
 * generates an architecture with the S-LAM2 meta-model type and an algorithm
 * with the PiMM type
 * 
 * @author mpelcat
 * @author jheulot
 * 
 */
public class PiMMAndSLAMScenarioNode extends AbstractScenarioImplementation {

	/**
	 * The {@link PiScenario} node in Preesm outputs three elements: PiMM, architecture and
	 * scenario
	 */
	@Override
	public Map<String, Object> extractData(String path)
			throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();

		// Retrieving the scenario from the given path
		PiScenarioParser piScenarioParser = new PiScenarioParser();

		Path relativePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		PiScenario piScenario;
		PiGraph pigraph;
		
		// Retrieving the algorithm
		try {
			piScenario = piScenarioParser.parseXmlFile(file);
			pigraph = PiScenarioParser.getAlgorithm(piScenario.getAlgorithmURL());

		} catch (Exception e) {
			throw new WorkflowException(e.getMessage());
		}

		// Retrieving the architecture
		Design slamDesign = PiScenarioParser.parseSlamDesign(piScenario
				.getArchitectureURL());

		outputs.put("scenario", piScenario);
		outputs.put("PiMM", pigraph);
		outputs.put("architecture", slamDesign);
		return outputs;
	}

	@Override
	public String monitorMessage() {
		return "Scenario, algorithm and architecture parsing.";
	}

}
