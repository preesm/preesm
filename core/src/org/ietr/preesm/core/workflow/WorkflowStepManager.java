/**
 * 
 */
package org.ietr.preesm.core.workflow;

import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.ICodeGeneration;
import org.ietr.preesm.core.task.IFileConversion;
import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.IMapping;
import org.ietr.preesm.core.task.ITask;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.core.workflow.sources.ArchitectureRetriever;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;
import org.ietr.preesm.core.workflow.sources.ScenarioRetriever;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Defining the steps called during workflow execution
 * 
 * @author mpelcat
 */
public class WorkflowStepManager {

	private IProgressMonitor monitor;
	int numberOfTasksDone = 0;

	public WorkflowStepManager(IProgressMonitor monitor, int workFlowSize) {
		super();
		this.monitor = monitor;

		monitor.beginTask("Executing workflow", workFlowSize);
	}

	void updateMonitor(String message) {

		monitor.subTask(message);
		PreesmLogger.getLogger().log(Level.INFO, message);
		numberOfTasksDone++;
		monitor.worked(numberOfTasksDone);
	}

	public void retrieveAlgorithm(String message, IScenario scenario,
			TaskResult nodeResult) {

		updateMonitor(message);

		if (scenario != null) {
			String algorithmPath = scenario.getAlgorithmURL();
			AlgorithmRetriever retriever = new AlgorithmRetriever(algorithmPath);
			nodeResult.setSDF(retriever.getAlgorithm());
		}
	}

	public void retrieveArchitecture(String message, IScenario scenario,
			TaskResult nodeResult) {

		updateMonitor(message);

		if (scenario != null) {
			String architecturePath = scenario.getArchitectureURL();
			ArchitectureRetriever retriever = new ArchitectureRetriever(
					architecturePath);
			nodeResult.setArchitecture(retriever.getArchitecture());
			MultiCoreArchitecture architecture = nodeResult.getArchitecture();

			// Setting main core and medium
			architecture.setMainOperator(scenario.getSimulationManager()
					.getMainOperatorName());
			architecture.setMainMedium(scenario.getSimulationManager()
					.getMainMediumName());
		}
	}

	public void retrieveScenario(String message,
			ScenarioConfiguration scenarioConfiguration, TaskResult nodeResult) {

		updateMonitor(message);

		ScenarioRetriever retriever = new ScenarioRetriever(
				scenarioConfiguration);
		IScenario theScenario = retriever.getScenario();

		nodeResult.setScenario(theScenario);
	}

}
