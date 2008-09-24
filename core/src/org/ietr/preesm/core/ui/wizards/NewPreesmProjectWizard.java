package org.ietr.preesm.core.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.ietr.preesm.core.ui.natures.PreesmProjectNature;

/**
 * This class provides a wizard to create a new Preesm Project.
 * 
 * @author Matthieu Wipliez
 */
public class NewPreesmProjectWizard extends BasicNewProjectResourceWizard {

	@Override
	public void addPages() {
		super.addPages();
		super.setWindowTitle("New Preesm Project");
	}

	@Override
	protected void initializeDefaultPageImageDescriptor() {
		super.initializeDefaultPageImageDescriptor();
	}

	@Override
	public boolean performFinish() {
		boolean finish = super.performFinish();
		try {
			IProject project = this.getNewProject();
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];

			System.arraycopy(natures, 0, newNatures, 1, natures.length);
			newNatures[0] = PreesmProjectNature.ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return finish;
	}
}