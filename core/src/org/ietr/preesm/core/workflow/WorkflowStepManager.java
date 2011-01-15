/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.workflow;

import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.core.workflow.sources.ArchitectureRetriever;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;
import org.ietr.preesm.core.workflow.sources.ScenarioRetriever;

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
		WorkflowLogger.getLogger().log(Level.INFO, message);
		numberOfTasksDone++;
		monitor.worked(numberOfTasksDone);
	}

	public void retrieveAlgorithm(String message, PreesmScenario scenario,
			TaskResult nodeResult) {

		updateMonitor(message);

		if (scenario != null) {
			String algorithmPath = scenario.getAlgorithmURL();
			AlgorithmRetriever retriever = new AlgorithmRetriever(algorithmPath);
			nodeResult.setSDF(retriever.getAlgorithm());
		}
	}

	public void retrieveArchitecture(String message, PreesmScenario scenario,
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
		PreesmScenario theScenario = retriever.getScenario();

		nodeResult.setScenario(theScenario);
	}

}
