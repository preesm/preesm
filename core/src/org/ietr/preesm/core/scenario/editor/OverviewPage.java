/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.Scenario;

/**
 * This page contains general informations of the scenario including
 * current algorithm and current architecture
 * 
 * @author mpelcat
 */
public class OverviewPage extends FormPage {


	/**
	 * This class calls the file browser when a "browse" button
	 * is pushed
	 */
	public class FileSelectionAdapter extends SelectionAdapter{
		
		private Text filePath;
		private Shell shell;
		
		public FileSelectionAdapter(Text filePath,Shell shell) {
			super();
			this.filePath = filePath;
			this.shell = shell;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			EditorTools.browseFiles(shell, filePath);
		}
	}

	/**
	 * The current scenario being edited
	 */
	private Scenario scenario;
	
	public OverviewPage(Scenario scenario, FormEditor editor, String id, String title) {
		super(editor, id, title);

		this.scenario = scenario;
	}

	/**
	 * Creation of the sections and their initialization
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		
		ScrolledForm form = managedForm.getForm();
		//FormToolkit toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("Overview.title"));
		ColumnLayout layout = new ColumnLayout();
		layout.topMargin = 0;
		layout.bottomMargin = 5;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.maxNumColumns = 4;
		layout.minNumColumns = 1;
		form.getBody().setLayout(layout);

		// Algorithm file chooser section
		createFileSection(managedForm, Messages.getString("Overview.algorithmFile"),
				Messages.getString("Overview.algorithmDescription"),
				Messages.getString("Overview.algorithmFileEdit"),
				scenario.getAlgorithmURL());

		// Architecture file chooser section
		createFileSection(managedForm, Messages.getString("Overview.architectureFile"),
				Messages.getString("Overview.architectureDescription"),
				Messages.getString("Overview.architectureFileEdit"),
				scenario.getArchitectureURL());
		
	}

	/**
	 * Creates a blank section with expansion capabilities
	 */
	private Composite createSection(IManagedForm mform, String title,
			String desc, int numColumns) {
		
		final ScrolledForm form = mform.getForm();
		FormToolkit toolkit = mform.getToolkit();
		Section section = toolkit.createSection(form.getBody(), Section.TWISTIE
				| Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		section.setText(title);
		section.setDescription(desc);

		toolkit.createCompositeSeparator(section);
		Composite client = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		layout.numColumns = numColumns;
		client.setLayout(layout);
		section.setClient(client);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(false);
			}
		});
		return client;
	}


	/**
	 * Creates a section to edit a file
	 * 
	 * @param mform form containing the section
	 * @param title section title
	 * @param desc description of the section
	 * @param fileEdit text to display in text label
	 * @param initValue initial value of Text
	 */
	private void createFileSection(IManagedForm mform, String title, String desc, String fileEdit, String initValue) {
		
		Composite client = createSection(mform, title, desc, 2);
		
		FormToolkit toolkit = mform.getToolkit();

		GridData gd = new GridData();
		Label label = toolkit.createLabel(client, fileEdit);

		Text text = toolkit.createText(client, initValue, SWT.SINGLE);
		text.setData(title);
		text.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				Text text = (Text)e.getSource();
				String type = ((String)text.getData());
				
				if(type.equals(Messages.getString("Overview.algorithmFile")))
					scenario.setAlgorithmURL(text.getText());
				else if(type.equals(Messages.getString("Overview.architectureFile")))
					scenario.setArchitectureURL(text.getText());
				
				firePropertyChange(PROP_DIRTY);
				
			}});
		
		gd.widthHint =400;
		text.setLayoutData(gd);

		final Button button = toolkit.createButton(client, Messages.getString("Overview.browse"), SWT.PUSH);
		SelectionAdapter adapter = new FileSelectionAdapter(text,client.getShell());
		button.addSelectionListener(adapter);
		
		toolkit.paintBordersFor(client);
	}
	
}
