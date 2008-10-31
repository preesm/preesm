/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.ui.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.ietr.preesm.core.scenario.editor.EditorTools;
import org.ietr.preesm.core.scenario.editor.Messages;
import org.ietr.preesm.core.ui.Activator;
import org.ietr.preesm.core.workflow.sources.ScenarioConfiguration;

/**
 * Shortcut for launching an executable directly from the navigator, without
 * having to create a launch configuration manually.
 */
public class WorkflowLaunchShortcut implements ILaunchShortcut {

	public static ILaunchConfiguration createLaunchConfiguration(IFile file) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(WorkflowLaunchConfigurationDelegate.WORKFLOW_LAUNCH_CONFIGURATION_TYPE_ID);

		ILaunchConfigurationWorkingCopy workingCopy;
		try {
			workingCopy = type.newInstance(null, DebugPlugin.getDefault()
					.getLaunchManager()
					.generateUniqueLaunchConfigurationNameFrom(
							file.getFullPath().toString()));
		} catch (CoreException e) {
			return null;
		}

		workingCopy.setAttribute(
				WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME,
				file.getFullPath().toString());

		// We ask for the scenario to use with the selected workflow
		String scenarioPath = EditorTools.browseFiles(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), Messages
				.getString("Shortcut.browseScenarioTitle"), "scenario");
		
		workingCopy.setAttribute(ScenarioConfiguration.ATTR_SCENARIO_FILE_NAME,
				scenarioPath);

		if (scenarioPath.isEmpty())
			return null;

		// set the defaults on the common tab
		CommonTab tab = new CommonTab();
		tab.setDefaults(workingCopy);
		tab.dispose();

		try {
			return workingCopy.doSave();
		} catch (CoreException e) {
			return null;
		}
	}

	/**
	 * Show a selection dialog that allows the user to choose one of the
	 * specified launch configurations. Return the chosen config, or
	 * <code>null</code> if the user canceled the dialog.
	 */
	protected ILaunchConfiguration chooseConfiguration(
			List<ILaunchConfiguration> configList) {
		IDebugModelPresentation labelProvider = DebugUITools
				.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("Choose the workflow configuration");
		dialog.setMessage("");
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	/**
	 * Search for the existing launch configurations with the same executable,
	 * so as to not create a new configuration if there is already one for the
	 * same executable.
	 * 
	 * @return the first matching configuration, or null if none was found
	 */
	private ILaunchConfiguration findExistingLaunchConfiguration(IFile file) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(WorkflowLaunchConfigurationDelegate.WORKFLOW_LAUNCH_CONFIGURATION_TYPE_ID);

		if (type != null) {
			List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
			try {
				ILaunchConfiguration[] configs = manager
						.getLaunchConfigurations(type);

				if (configs != null && configs.length > 0) {
					for (int i = 0; i < configs.length; i++) {
						ILaunchConfiguration configuration = configs[i];
						if (configuration
								.getAttribute(
										WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME,
										"").equals(
										file.getLocation().toOSString())) {
							candidateConfigs.add(configuration);
						}
					}
				}
			} catch (CoreException e) {

			}

			// If there are no existing configs associated with the IFile,
			// create one.
			// If there is exactly one config associated with the IFile, return
			// it.
			// Otherwise, if there is more than one config associated with the
			// IFile, prompt the
			// user to choose one.
			int candidateCount = candidateConfigs.size();
			if (candidateCount < 1) {
				return createLaunchConfiguration(file);
			} else if (candidateCount == 1) {
				return (ILaunchConfiguration) candidateConfigs.get(0);
			} else {
				// Prompt the user to choose a config. A null result means the
				// user
				// canceled the dialog, in which case this method returns null,
				// since canceling the dialog should also cancel launching
				// anything.
				ILaunchConfiguration config = chooseConfiguration(candidateConfigs);
				if (config != null) {
					return config;
				}
			}
		}

		return null;
	}

	private Shell getShell() {
		return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getShell();
	}

	/**
	 * This is normally used to launch the contents of the current editor. We
	 * don't currently enable this feature.
	 */
	public void launch(IEditorPart editor, String mode) {
	}

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;

			Object first = sel.getFirstElement();
			if (first != null && first instanceof IFile) {
				IFile file = (IFile) first;

				ILaunchConfiguration configuration = findExistingLaunchConfiguration(file);
				if (configuration != null) {
					DebugUITools.launch(configuration, mode);
				}
			}
		}
	}

}
