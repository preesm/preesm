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


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.preesm.core.ui.Activator;
import org.ietr.preesm.core.workflow.sources.AlgorithmConfiguration;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;

/**
 * Launch Tab for algorithm options. From this tab, an
 * {@link AlgorithmConfiguration} is generated that feeds an
 * {@link AlgorithmRetriever} to create the input algorithm.
 * 
 * @author mpelcat
 */
public class WorkFlowLaunchAlgorithmTab extends AbstractWorkFlowLaunchTab {

	private IPath workflowPath = null;

	private Text workflowText;

	/**
	 * Displays a file browser in a shell
	 */
	private void browseWorkflow(Shell shell) {
		ElementTreeSelectionDialog tree = new ElementTreeSelectionDialog(shell,
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		tree.setAllowMultiple(false);
		tree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		tree.setMessage("Please select an existing file:");
		tree.setTitle("Choose an existing file");
		// opens the dialog
		if (tree.open() == Window.OK) {
			workflowPath = ((IFile) tree.getFirstResult()).getLocation();
			workflowText.setText(workflowPath.toString());
		}
	}

	@Override
	public void createControl(Composite parent) {

		super.createControl(parent);
		drawFileChooser("Algorithm file:",
				AlgorithmConfiguration.ATTR_ALGORITHM_FILE_NAME);
		drawWorkflowChooser("Select Workflow");
	}

	public void drawWorkflowChooser(String title) {

		Label label2 = new Label(getCurrentComposite(), SWT.NONE);
		label2.setText(title);

		new Label(getCurrentComposite(), SWT.NONE);

		Button buttonBrowse = new Button(getCurrentComposite(), SWT.PUSH);
		buttonBrowse.setText("Browse...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWorkflow(getCurrentComposite().getShell());
			}
		});

		workflowText = new Text(getCurrentComposite(), SWT.BORDER);

		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.widthHint = 200;
		workflowText.setLayoutData(layoutData);
		workflowText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
	}

	@Override
	public String getName() {
		return "Algorithm";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
		try {
			workflowText.setText(configuration.getAttribute(WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME, ""));
		} catch (CoreException e) {
			workflowText.setText("");
		}

		setDirty(false);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		super.performApply(configuration);
		if (workflowPath != null)
			configuration
					.setAttribute(
							WorkflowLaunchConfigurationDelegate.ATTR_WORKFLOW_FILE_NAME,
							workflowPath.toOSString());
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	
	}

	@Override
	public Image getImage() {
		Image image = Activator.getImage("icons/preesm2mini.png");
		
		if(image != null)
			return image;
		
		return super.getImage();
	}
}
