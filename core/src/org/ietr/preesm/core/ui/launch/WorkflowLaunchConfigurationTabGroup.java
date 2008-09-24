/**
 * 
 */
package org.ietr.preesm.core.ui.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * @author Matthieu Wipliez
 * 
 */
public class WorkflowLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	
	/**
	 * 
	 */
	public WorkflowLaunchConfigurationTabGroup() {
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new WorkFlowLaunchAlgorithmTab(),
				new WorkFlowLaunchArchitectureTab(),
				new WorkFlowLaunchScenarioTab(),
				new EnvironmentTab()};
		this.setTabs(tabs);
	}

}
