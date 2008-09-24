package org.ietr.preesm.core.ui.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.ietr.preesm.core.workflow.Workflow;
import org.ietr.preesm.core.workflow.sources.AlgorithmConfiguration;
import org.ietr.preesm.core.workflow.sources.ArchitectureConfiguration;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;

/**
 * Launch a workflow in normal or debug mode, using the previously created
 * launch configuration.
 */
public class WorkflowLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {
	
	public static final String ATTR_WORKFLOW_FILE_NAME = "org.ietr.preesm.core.workflowFileName";
	
	public static String WORKFLOW_LAUNCH_CONFIGURATION_TYPE_ID = "org.ietr.preesm.core.workflowLaunchConfigurationType";

	/**
	 * Launches a workflow
	 * 
	 * @param configuration
	 * 			Retrieved from configuration tabs
	 * @param mode
	 * 			Run or debug
	 * @param launch
	 * 			Not used
	 * @param monitor
	 * 			Monitoring the workflow progress
	 */
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String workflowName = configuration.getAttribute(
				ATTR_WORKFLOW_FILE_NAME, "");
		
		// Retrieving environment variables
		Map<String,String> configEnv = configuration.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map) null);
		if (configEnv == null) {
			configEnv = new HashMap<String,String>() ;
		}
		
		configuration.getName();
		System.out.println("Launching " + workflowName + "...");

		Workflow workflow = new Workflow();
		workflow.parse(workflowName);
		if (workflow.check(monitor)) {

			AlgorithmConfiguration algorithmConfiguration = new AlgorithmConfiguration(configuration);
			
			ArchitectureConfiguration architectureConfiguration = new ArchitectureConfiguration(configuration);
			
			ScenarioConfiguration scenarioConfiguration = new ScenarioConfiguration(configuration);

			workflow.execute(monitor, algorithmConfiguration, architectureConfiguration, scenarioConfiguration,configEnv);
		} else {
			monitor.setCanceled(true);
		}
	}
}
