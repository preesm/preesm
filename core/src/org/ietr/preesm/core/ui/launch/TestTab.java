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

package org.ietr.preesm.core.ui.launch;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Implements a properties page that will appear amongst the O'Caml launch
 * configuration tabs and that will allow the user to configure the name of the
 * executable, the name of the project, and the command-line arguments of the
 * executable.
 */
public class TestTab extends AbstractLaunchConfigurationTab {

	public static final String ATTR_ARGS = "attr_ocaml_launch_args";

	public static final String ATTR_FULLPATH = "attr_ocaml_launch_full_path";

	public static final String ATTR_PROJECTNAME = "attr_ocaml_launch_project_name";

	public static final String ATTR_REMOTE_DEBUG_ENABLE = "attr_ocaml_remote_debug_enable";

	public static final String ATTR_REMOTE_DEBUG_PORT = "attr_ocaml_remote_debug_port";

	public static final boolean DEFAULT_REMOTE_DEBUG_ENABLE = false;

	public static final String DEFAULT_REMOTE_DEBUG_PORT = "8000";

	Button buttonRemoteDebugEnable;

	Composite composite;

	Text textArguments;

	Text textExePath;

	Text textProjectName;

	Text textRemoteDebugPort;

	/** Browse button was clicked */
	protected void browse() {
		FileDialog fileDialog = new FileDialog(composite.getShell());
		try {
			fileDialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toOSString());
		} catch (Exception e) {
			// OcamlPlugin.logError("ocaml plugin error", e);
		}

		String path = fileDialog.open();
		if (path != null) {
			textExePath.setText(path.trim());
		}
	}

	public void createControl(Composite parent) {

		composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);

		Label label1 = new Label(composite, SWT.NONE);
		label1.setText("Project name:");
		// dummy label, because we have two columns
		new Label(composite, SWT.NONE);
		textProjectName = new Text(composite, SWT.BORDER);
		new Label(composite, SWT.NONE);

		Label label2 = new Label(composite, SWT.NONE);
		label2.setText("Full path to the executable file:");
		// dummy label, because we have two columns
		new Label(composite, SWT.NONE);
		textExePath = new Text(composite, SWT.BORDER);

		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.widthHint = 200;
		textExePath.setLayoutData(layoutData);
		textExePath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		textProjectName.setLayoutData(layoutData);
		textProjectName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		Button buttonBrowse = new Button(composite, SWT.PUSH);
		buttonBrowse.setText("Browse...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browse();
			}
		});

		Label label3 = new Label(composite, SWT.NONE);
		label3.setText("Command line arguments (separated by spaces)\n"
				+ "You can use \" \" and \\ to quote strings");
		// dummy label, because we have two columns
		new Label(composite, SWT.NONE);
		textArguments = new Text(composite, SWT.BORDER);
		textArguments.setLayoutData(layoutData);
		textArguments.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		// dummy label, because we have two columns
		new Label(composite, SWT.NONE);

		// remote debug settings.
		Label labelRemoteDebug = new Label(composite, SWT.NONE);
		labelRemoteDebug.setText("Remote debugging:");
		new Label(composite, SWT.NONE);

		// remote debug enable.
		buttonRemoteDebugEnable = new Button(composite, SWT.CHECK);
		buttonRemoteDebugEnable.setText("Enable remote debugging");
		buttonRemoteDebugEnable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
				textRemoteDebugPort.setEnabled(buttonRemoteDebugEnable
						.getSelection());
			}
		});

		new Label(composite, SWT.NONE);

		// remote debug port.
		Label labelRemoteDebugPort = new Label(composite, SWT.NONE);
		labelRemoteDebugPort.setText("Remote port:");
		new Label(composite, SWT.NONE);
		textRemoteDebugPort = new Text(composite, SWT.BORDER);
		textRemoteDebugPort.setLayoutData(layoutData);
		textRemoteDebugPort.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		textRemoteDebugPort.setEnabled(false);

		new Label(composite, SWT.NONE);
	}

	public String getName() {
		return "Example Tab";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String path = null;
		String project = null;
		String args = null;
		boolean remoteDebugEnable = false;
		String remoteDebugPort = null;
		try {
			path = configuration.getAttribute(ATTR_FULLPATH, "");
			project = configuration.getAttribute(ATTR_PROJECTNAME, "");
			args = configuration.getAttribute(ATTR_ARGS, "");
			remoteDebugEnable = configuration.getAttribute(
					ATTR_REMOTE_DEBUG_ENABLE, DEFAULT_REMOTE_DEBUG_ENABLE);
			remoteDebugPort = configuration.getAttribute(
					ATTR_REMOTE_DEBUG_PORT, DEFAULT_REMOTE_DEBUG_PORT);
		} catch (CoreException e) {
			// OcamlPlugin.logError("ocaml plugin error", e);
			path = "";
			project = "";
			args = "";
			remoteDebugEnable = DEFAULT_REMOTE_DEBUG_ENABLE;
			remoteDebugPort = DEFAULT_REMOTE_DEBUG_PORT;
		}

		textExePath.setText(path);
		textProjectName.setText(project);
		textArguments.setText(args);
		buttonRemoteDebugEnable.setSelection(remoteDebugEnable);
		textRemoteDebugPort.setText(remoteDebugPort);
		textRemoteDebugPort.setEnabled(remoteDebugEnable);
		setDirty(false);
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		String path = null;
		String project = null;
		String remoteDebugPort = null;
		try {
			path = launchConfig.getAttribute(ATTR_FULLPATH, "");
			project = launchConfig.getAttribute(ATTR_PROJECTNAME, "");
			remoteDebugPort = launchConfig.getAttribute(ATTR_REMOTE_DEBUG_PORT,
					DEFAULT_REMOTE_DEBUG_PORT);
		} catch (CoreException e) {
			// OcamlPlugin.logError("ocaml plugin error", e);
			path = "";
			project = "";
			remoteDebugPort = DEFAULT_REMOTE_DEBUG_PORT;
		}

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		boolean bFound = false;
		for (IProject p : projects)
			if (p.getName().equals(project)) {
				bFound = true;
				break;
			}

		if (!bFound) {
			setErrorMessage("Invalid project name: " + project);
			return false;
		}

		File file = new File(path);

		if (!(file.exists() && file.isFile())) {
			setErrorMessage("Invalid filename: " + path);
			return false;
		}

		int remoteDebugPortValue = -1;
		try {
			remoteDebugPortValue = Integer.parseInt(remoteDebugPort);
		} catch (NumberFormatException e) {/* Do nothing */
		}
		if (remoteDebugPortValue < 1 || remoteDebugPortValue > 65535) {
			setErrorMessage("Invalid remote debug port: " + remoteDebugPort);
			return false;
		}

		// Success.
		setErrorMessage(null);
		return true;
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ATTR_FULLPATH, textExePath.getText());
		configuration.setAttribute(ATTR_PROJECTNAME, textProjectName.getText());
		configuration.setAttribute(ATTR_ARGS, textArguments.getText());
		configuration.setAttribute(ATTR_REMOTE_DEBUG_ENABLE,
				buttonRemoteDebugEnable.getSelection());
		configuration.setAttribute(ATTR_REMOTE_DEBUG_PORT, textRemoteDebugPort
				.getText());
		setDirty(false);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ATTR_FULLPATH, "");
		configuration.setAttribute(ATTR_PROJECTNAME, "");
		configuration.setAttribute(ATTR_ARGS, "");
		configuration.setAttribute(ATTR_REMOTE_DEBUG_ENABLE,
				DEFAULT_REMOTE_DEBUG_ENABLE);
		configuration.setAttribute(ATTR_REMOTE_DEBUG_PORT,
				DEFAULT_REMOTE_DEBUG_PORT);
		setDirty(false);
	}

	/** This method was copied from the JDT plug-in */
	protected void updateLaunchConfigurationDialog() {
		if (getLaunchConfigurationDialog() != null) {
			/*
			 * order is important here due to the call to refresh the tab viewer
			 * in updateButtons() which ensures that the messages are up to date
			 */
			getLaunchConfigurationDialog().updateButtons();
			getLaunchConfigurationDialog().updateMessage();
		}
	}
}