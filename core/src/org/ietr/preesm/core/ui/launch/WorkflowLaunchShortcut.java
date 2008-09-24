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
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.ietr.preesm.core.ui.Activator;

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
							file.getLocation().toOSString()));
		} catch (CoreException e) {
			return null;
		}

		workingCopy.setAttribute(
				WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME, file
						.getLocation().toOSString());

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
