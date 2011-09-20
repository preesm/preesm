/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.util.HashMap;
import java.util.Map;

import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractScenarioImplementation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.sdf4j.model.sdf.SDFGraph;

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

		PreesmScenario scenario = scenarioParser.parseXmlFile(file);

		// Retrieving the algorithm
		SDFGraph algorithm = ScenarioParser.getAlgorithm(scenario
				.getAlgorithmURL());

		// Retrieving the architecture
		Design slamDesign = parseSlamDesign(scenario.getArchitectureURL());

		outputs.put("scenario", scenario);
		outputs.put("IBSDF", algorithm);
		outputs.put("S-LAM", slamDesign);
		return outputs;
	}

	@Override
	public String monitorMessage() {
		return "Scenario, algorithm and architecture parsing.";
	}

	private Design parseSlamDesign(String path) {
		// Demand load the resource into the resource set.
		ResourceSet resourceSet = new ResourceSetImpl();

		// resourceSet.
		Resource resource = resourceSet.getResource(URI.createFileURI(path),
				true);
		// Extract the root object from the resource.
		Design design = (Design) resource.getContents().get(0);

		return design;
	}

}
