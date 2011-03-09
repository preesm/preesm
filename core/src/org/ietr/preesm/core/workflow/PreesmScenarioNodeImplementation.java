/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.parser.DesignParser;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.sdf4j.importer.GMLGenericImporter;
import org.sdf4j.importer.InvalidFileException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractScenarioImplementation;

/**
 * @author mpelcat
 * 
 * Implementing the DFTools scenario node behavior for Preesm
 */
public class PreesmScenarioNodeImplementation extends
		AbstractScenarioImplementation {
	
	/**
	 * The scenario node in Preesm outputs three elements: SDF, architecture and scenario
	 */
	@Override
	public Map<String, Object> extractData(String path)
			throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		
		// Retrieving the scenario from the given path
		PreesmScenario scenario = retrieveScenario(path);

		// Retrieving the algorithm
		SDFGraph algorithm = retrieveAlgorithm(scenario.getAlgorithmURL());

		// Retrieving the architecture
		MultiCoreArchitecture architecture = retrieveArchitecture(scenario
				.getArchitectureURL());

		outputs.put("scenario", scenario);
		outputs.put("SDF", algorithm);
		outputs.put("architecture", architecture);
		return outputs;
	}

	private PreesmScenario retrieveScenario(String path) {
		PreesmScenario scenario = new PreesmScenario();

		ScenarioParser parser = new ScenarioParser();

		Path relativePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		parser.parseXmlFile(file);
		scenario = parser.parseDocument();

		return scenario;
	}

	private SDFGraph retrieveAlgorithm(String path) {
		SDFGraph algorithm = null;
		GMLGenericImporter importer = new GMLGenericImporter();

		Path relativePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		try {
			algorithm = (SDFGraph) importer.parse(file.getContents(), file
					.getLocation().toOSString());

			addVertexPathProperties(algorithm, "");
		} catch (InvalidFileException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return algorithm;
	}

	private MultiCoreArchitecture retrieveArchitecture(String path) {
		DesignParser parser = new DesignParser();

		Path relativePath = new Path(path);
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(relativePath);

		MultiCoreArchitecture architecture = parser.parseXmlFile(file);

		addVertexPathProperties(architecture, "");

		return architecture;
	}

	/**
	 * Adding an information that keeps the path of each vertex relative to the
	 * hierarchy
	 */
	private void addVertexPathProperties(SDFGraph algorithm, String currentPath) {

		for (SDFAbstractVertex vertex : algorithm.vertexSet()) {
			String newPath = currentPath + vertex.getName();
			vertex.setInfo(newPath);
			newPath += "/";
			if (vertex.getGraphDescription() != null) {
				addVertexPathProperties(
						(SDFGraph) vertex.getGraphDescription(), newPath);
			}
		}
	}

	/**
	 * Adding an information that keeps the path of each vertex relative to the
	 * hierarchy
	 */
	private void addVertexPathProperties(MultiCoreArchitecture architecture,
			String currentPath) {

		for (ArchitectureComponent vertex : architecture.vertexSet()) {
			String newPath = currentPath + vertex.getName();
			vertex.setInfo(newPath);
			newPath += "/";
			if (vertex.getGraphDescription() != null) {
				addVertexPathProperties(
						(MultiCoreArchitecture) vertex.getGraphDescription(),
						newPath);
			}
		}
	}

	@Override
	public String monitorMessage() {
		return "Scenario, algorithm and architecture parsing.";
	}

}
