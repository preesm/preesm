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

package org.ietr.preesm.workflow.launch.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.ietr.preesm.workflow.WorkflowManager;
import org.ietr.preesm.workflow.sources.ScenarioConfiguration;
import org.ietr.preesm.workflow.tools.PreesmLogger;

/**
 * Launch a workflow in run mode, using the previously created
 * launch configuration.
 * 
 * @author mwipliez
 * @author mpelcat
 */
public class WorkflowLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	public static final String ATTR_WORKFLOW_FILE_NAME = "org.ietr.preesm.workflow.fileName";

	public static String WORKFLOW_LAUNCH_CONFIGURATION_TYPE_ID = "org.ietr.preesm.workflow.launchConfigurationType";

	/**
	 * Launches a workflow
	 * 
	 * @param configuration
	 *            Retrieved from configuration tabs
	 * @param mode
	 *            Run or debug
	 * @param launch
	 *            Not used
	 * @param monitor
	 *            Monitoring the workflow progress
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String workflowPath = configuration.getAttribute(
				ATTR_WORKFLOW_FILE_NAME, "");

		// Retrieving environment variables
		Map<String, String> configEnv = configuration.getAttribute(
				ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map) null);
		if (configEnv == null) {
			configEnv = new HashMap<String, String>();
		}

		PreesmLogger.getLogger().log(
				Level.INFO,"Launching " + workflowPath + "...");

		WorkflowManager workflowManager = new WorkflowManager();
		if(workflowManager.check(workflowPath,monitor)){
			
			String scenarioPath = configuration.getAttribute(
					ScenarioConfiguration.ATTR_SCENARIO_FILE_NAME, "");
			
			workflowManager.execute(workflowPath, scenarioPath);
		} else {
			monitor.setCanceled(true);
		}
	}
}
