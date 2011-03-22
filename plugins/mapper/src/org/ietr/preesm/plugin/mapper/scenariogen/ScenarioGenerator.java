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
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.ui.Activator;
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
			ScenarioConfiguration scenarioConfiguration = new ScenarioConfiguration();
			scenarioConfiguration.setScenarioFileName(scenarioFileName);

			ScenarioRetriever retriever = new ScenarioRetriever(
					scenarioConfiguration);

			PreesmScenario scenario = retriever.getScenario();
			outputs.put("scenario", scenario);

			AlgorithmRetriever algoR = new AlgorithmRetriever(
					scenario.getAlgorithmURL());
			if (algoR.getAlgorithm() == null) {
				AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
						"cannot retrieve algorithm");
				return null;
			} else {
				outputs.put("SDF", algoR.getAlgorithm());
			}

			ArchitectureRetriever archiR = new ArchitectureRetriever(
					scenario.getArchitectureURL());
			if (archiR.getArchitecture() == null) {
				AbstractWorkflowLogger.getLogger().log(Level.SEVERE,
						"cannot retrieve architecture");
				return null;
			} else {
				// Setting main core and medium
				archiR.getArchitecture().setMainOperator(
						scenario.getSimulationManager().getMainOperatorName());
				archiR.getArchitecture().setMainMedium(
						scenario.getSimulationManager().getMainMediumName());
				outputs.put("architecture", archiR.getArchitecture());
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
					ArchitectureComponent op = ((MultiCoreArchitecture) outputs
							.get("architecture")).getComponent(opName);

					if (sdfV != null && op != null && op instanceof Operator) {
						((PreesmScenario) outputs.get("scenario"))
								.getConstraintGroupManager().addConstraint(
										(Operator) op, sdfV);
						((PreesmScenario) outputs.get("scenario"))
								.getTimingManager().setTiming(
										sdfV,
										(OperatorDefinition) (op
												.getDefinition()),
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
