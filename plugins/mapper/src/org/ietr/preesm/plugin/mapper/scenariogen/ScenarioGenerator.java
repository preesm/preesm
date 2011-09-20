/**
 * 
 */
package org.ietr.preesm.plugin.mapper.scenariogen;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.AbstractWorkflowLogger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.Activator;
import org.ietr.preesm.core.architecture.Component;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This class defines a method to load a new scenario and optionally change some
 * constraints from an output DAG.
 * 
 * @author mpelcat
 */
public class ScenarioGenerator extends AbstractTaskImplementation {

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("scenarioFile", "");
		parameters.put("dagFile", "");

		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "generating a scenario";
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();

		AbstractWorkflowLogger.getLogger().log(Level.INFO,
				"Generating scenario");

		String scenarioFileName = parameters.get("scenarioFile");

		// Retrieving the scenario
		if (scenarioFileName.isEmpty()) {
			AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
					"lack of a scenarioFile parameter");
			return null;
		} else {

			// Retrieving the scenario from the given path
			ScenarioParser parser = new ScenarioParser();

			Path relativePath = new Path(scenarioFileName);
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(relativePath);

			PreesmScenario scenario = parser.parseXmlFile(file);

			// Retrieving the algorithm
			SDFGraph algo = ScenarioParser.getAlgorithm(scenario
					.getAlgorithmURL());
			if (algo == null) {
				AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
						"cannot retrieve algorithm");
				return null;
			} else {
				outputs.put("SDF", algo);
			}

			// Retrieving the architecture
			MultiCoreArchitecture archi = ScenarioParser
					.getArchitecture(scenario.getArchitectureURL());
			if (archi == null) {
				AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
						"cannot retrieve architecture");
				return null;
			} else {
				// Setting main core and medium
				archi.setMainOperator(scenario.getSimulationManager()
						.getMainOperatorName());
				archi.setMainMedium(scenario.getSimulationManager()
						.getMainMediumName());
				outputs.put("architecture", archi);
			}

		}

		String dagFileName = parameters.get("dagFile");
		// Parsing the output DAG if present and updating the constraints
		if (dagFileName.isEmpty()) {
			AbstractWorkflowLogger.getLogger().log(Level.WARNING,
					"No dagFile -> retrieving the scenario as is");
		} else {
			GMLMapperDAGImporter importer = new GMLMapperDAGImporter();

			((PreesmScenario) outputs.get("scenario"))
					.getConstraintGroupManager().removeAll();
			((PreesmScenario) outputs.get("scenario")).getTimingManager()
					.removeAll();

			try {
				Path relativePath = new Path(dagFileName);
				IFile file = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(relativePath);

				Activator.updateWorkspace();
				SDFGraph graph = importer
						.parse(file.getContents(), dagFileName);
				graph = importer.parse(file.getContents(), dagFileName);

				for (SDFAbstractVertex dagV : graph.vertexSet()) {
					String vName = (String) dagV.getPropertyBean().getValue(
							"name");
					String opName = (String) dagV.getPropertyBean().getValue(
							ImplementationPropertyNames.Vertex_Operator);
					String timeStr = (String) dagV.getPropertyBean().getValue(
							ImplementationPropertyNames.Task_duration);
					SDFAbstractVertex sdfV = ((SDFGraph) outputs.get("SDF"))
							.getVertex(vName);
					Component op = ((MultiCoreArchitecture) outputs
							.get("architecture")).getComponent(opName);

					if (sdfV != null && op != null && op instanceof Operator) {
						((PreesmScenario) outputs.get("scenario"))
								.getConstraintGroupManager().addConstraint(
										(Operator) op, sdfV);
						((PreesmScenario) outputs.get("scenario"))
								.getTimingManager().setTiming(
										sdfV.getName(),
										op
												.getDefinition().getId(),
										Integer.parseInt(timeStr));
					}
				}

			} catch (Exception e) {
				AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
						e.getMessage());
			}

		}

		return outputs;
	}

}
