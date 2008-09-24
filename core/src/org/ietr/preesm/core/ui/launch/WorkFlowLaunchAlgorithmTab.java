/**
 * 
 */
package org.ietr.preesm.core.ui.launch;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.resource.ImageDescriptor;
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
		workflowText.setText("");
	}

	@Override
	public Image getImage() {
		Image image = Activator.getImage("icons/preesm2mini.png");
		
		if(image != null)
			return image;
		
		return super.getImage();
	}
}
