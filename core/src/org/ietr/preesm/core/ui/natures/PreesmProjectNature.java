/**
 * 
 */
package org.ietr.preesm.core.ui.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author mwipliez
 * 
 */
public class PreesmProjectNature implements IProjectNature {

	public static final String ID = "org.ietr.preesm.core.natures.PreesmProjectNature";

	private IProject project;

	/**
	 * Creates a new Preesm project nature.
	 */
	public PreesmProjectNature() {
	}

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
