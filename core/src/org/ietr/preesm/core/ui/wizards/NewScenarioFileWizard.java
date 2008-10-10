/**
 * 
 */
package org.ietr.preesm.core.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.ietr.preesm.core.ui.natures.PreesmProjectNature;

/**
 * @author mpelcat
 */
public class NewScenarioFileWizard extends BasicNewFileResourceWizard {

	@Override
	public void addPages() {
		super.addPages();
		super.setWindowTitle("New Scenario File");
	}

	@Override
	protected void initializeDefaultPageImageDescriptor() {
		super.initializeDefaultPageImageDescriptor();
	}

}
