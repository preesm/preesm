/**
 * 
 */
package org.ietr.preesm.core.ui.launch;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Containing common funtionalities of launch tabs.
 * 
 * @author mpelcat
 */
public abstract class AbstractWorkFlowLaunchTab extends
		AbstractLaunchConfigurationTab {

	/**
	 * current Composite
	 */
	private Composite currentComposite;

	/**
	 * file attribute name to save the entered file
	 */
	private String fileAttributeName = null;
	
	
	/**
	 * file path of the current Tab. There can be only one file chooser for the
	 * moment
	 */
	private Text filePath;

	

	/**
	 * Displays a file browser in a shell
	 */
	protected void browseFiles() {
		FileDialog fileDialog = new FileDialog(currentComposite.getShell());
		try {
			fileDialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toOSString());
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}

		String path = fileDialog.open();
		if (path != null) {
			filePath.setText(path.trim());
		}
	}

	@Override
	public void createControl(Composite parent) {

		currentComposite = new Composite(parent, SWT.NONE);
		setControl(currentComposite);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		currentComposite.setLayout(gridLayout);

	}

	/**
	 * Displays a file text window with a browser button.
	 * 
	 * @param title
	 *            A line of text displayed before the file chooser
	 * @param attributeName
	 *            The name of the attribute in which the property should be
	 *            saved
	 */
	public void drawFileChooser(String title, String attributeName) {

		Label label2 = new Label(currentComposite, SWT.NONE);
		label2.setText(title);
		fileAttributeName = attributeName;

		new Label(currentComposite, SWT.NONE);

		Button buttonBrowse = new Button(currentComposite, SWT.PUSH);
		buttonBrowse.setText("Browse...");
		buttonBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseFiles();
			}
		});

		filePath = new Text(currentComposite, SWT.BORDER);

		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
		layoutData.widthHint = 200;
		filePath.setLayoutData(layoutData);
		filePath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

	}
	
	

	protected Composite getCurrentComposite() {
		return currentComposite;
	}
	

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			filePath.setText(configuration.getAttribute(fileAttributeName, ""));
		} catch (CoreException e) {
			// OcamlPlugin.logError("ocaml plugin error", e);
			filePath.setText("");
		}

		setDirty(false);
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		return true;
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// Saving the file path chosen in a tab attribute
		if(filePath != null && fileAttributeName != null){
			configuration.setAttribute(fileAttributeName,filePath.getText());
		}
		setDirty(false);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
		configuration.setAttribute(fileAttributeName,"");
		setDirty(false);
	}
	
}