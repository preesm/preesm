/**
 * 
 */
package org.ietr.preesm.core.ui.wizards;

import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

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
