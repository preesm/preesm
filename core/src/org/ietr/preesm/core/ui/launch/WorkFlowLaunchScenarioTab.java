/**
 * 
 */
package org.ietr.preesm.core.ui.launch;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.ietr.preesm.core.ui.Activator;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;
import org.ietr.preesm.core.workflow.sources.ScenarioRetriever;

/**
 * Launch Tab for scenario options. From this tab, an {@link ScenarioConfiguration}
 * is generated that feeds an {@link ScenarioRetriever} to create the input scenario.
 * 
 * @author mpelcat
 */
public class WorkFlowLaunchScenarioTab extends AbstractWorkFlowLaunchTab {
	
	@Override
	public void createControl(Composite parent) {

		super.createControl(parent);
		
		drawFileChooser("Scenario file:",ScenarioConfiguration.ATTR_SCENARIO_FILE_NAME);
		
	}

	@Override
	public String getName() {
		return "Scenario";
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getImage() {
		Image image = Activator.getImage("icons/preesm4mini.png");
		
		if(image != null)
			return image;
		
		return super.getImage();
	}
}
