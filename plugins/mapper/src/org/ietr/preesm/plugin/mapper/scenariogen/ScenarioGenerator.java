/**
 * 
 */
package org.ietr.preesm.plugin.mapper.scenariogen;

import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.IScenarioTransformation;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.core.ui.Activator;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.core.workflow.sources.ArchitectureRetriever;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;
import org.ietr.preesm.core.workflow.sources.ScenarioRetriever;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * This class defines a method to load a new scenario and optionally change some
 * constraints from an output DAG.
 * 
 * @author mpelcat
 */
public class ScenarioGenerator implements IScenarioTransformation {

	@Override
	public TaskResult transform(TextParameters parameters) {

		TaskResult result = new TaskResult();

		PreesmLogger.getLogger().log(Level.INFO, "Generating scenario");

		String scenarioFileName = parameters.getVariable("scenarioFile");

		// Retrieving the scenario
		if (scenarioFileName.isEmpty()) {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"lack of a scenarioFile parameter");
			return null;
		} else {
			ScenarioConfiguration scenarioConfiguration = new ScenarioConfiguration();
			scenarioConfiguration.setScenarioFileName(scenarioFileName);

			ScenarioRetriever retriever = new ScenarioRetriever(
					scenarioConfiguration);

			IScenario scenario = retriever.getScenario();
			result.setScenario(scenario);

			AlgorithmRetriever algoR = new AlgorithmRetriever(scenario
					.getAlgorithmURL());
			if (algoR == null) {
				PreesmLogger.getLogger().log(Level.SEVERE,
						"cannot retrieve algorithm");
				return null;
			} else {
				result.setSDF(algoR.getAlgorithm());
			}

			ArchitectureRetriever archiR = new ArchitectureRetriever(scenario
					.getArchitectureURL());
			if (archiR == null) {
				PreesmLogger.getLogger().log(Level.SEVERE,
						"cannot retrieve architecture");
				return null;
			} else {
				// Setting main core and medium
				archiR.getArchitecture().setMainOperator(scenario.getSimulationManager()
						.getMainOperatorName());
				archiR.getArchitecture().setMainMedium(scenario.getSimulationManager()
						.getMainMediumName());
				result.setArchitecture(archiR.getArchitecture());
			}
		}

		String dagFileName = parameters.getVariable("dagFile");
		// Parsing the output DAG if present and updating the constraints
		if (dagFileName.isEmpty()) {
			PreesmLogger.getLogger().log(Level.WARNING,
					"No dagFile -> retrieving the scenario as is");
		} else {
			GMLMapperDAGImporter importer = new GMLMapperDAGImporter();

			result.getScenario().getConstraintGroupManager().removeAll();
			result.getScenario().getTimingManager().removeAll();

			try {
				Path relativePath = new Path(dagFileName);
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
						relativePath);

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
					SDFAbstractVertex sdfV = result.getSDF().getVertex(vName);
					ArchitectureComponent op = result.getArchitecture()
							.getComponent(opName);

					if (sdfV != null && op != null && op instanceof Operator) {
						result.getScenario().getConstraintGroupManager()
								.addConstraint((Operator) op, sdfV);
						result.getScenario().getTimingManager().setTiming(sdfV,
								(OperatorDefinition) (op.getDefinition()),
								Integer.parseInt(timeStr));
					}
				}

			} catch (Exception e) {
				PreesmLogger.getLogger().log(Level.SEVERE, e.getMessage());
			}

		}

		return result;
	}

}
