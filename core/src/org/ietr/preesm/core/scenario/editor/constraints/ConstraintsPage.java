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
 
package org.ietr.preesm.core.scenario.editor.constraints;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPropertyListener;
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
import org.ietr.preesm.core.scenario.editor.FileSelectionAdapter;
import org.ietr.preesm.core.scenario.editor.Messages;
import org.ietr.preesm.core.scenario.editor.SDFTreeSection;

/**
 * Constraint editor within the implementation editor
 * 
 * @author mpelcat
 */
public class ConstraintsPage extends FormPage implements IPropertyListener {

	/**
	 * Currently edited scenario
	 */
	private Scenario scenario = null;
	private ConstraintsCheckStateListener checkStateListener = null;

	public ConstraintsPage(Scenario scenario, FormEditor editor, String id, String title) {
		super(editor, id, title);
		this.scenario = scenario;
	}

	/**
	 * Initializes the display content
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		ScrolledForm f = managedForm.getForm();
		f.setText(Messages.getString("Constraints.title"));
		f.getBody().setLayout(new GridLayout());

		// Constrints file chooser section
		createFileSection(managedForm, Messages.getString("Constraints.file"),
				Messages.getString("Constraints.fileDescription"),
				Messages.getString("Constraints.fileEdit"),
				scenario.getConstraintGroupManager().getExcelFileURL(),
				Messages.getString("Constraints.fileBrowseTitle"),
				"xls");
		
		createConstraintsSection(managedForm, Messages.getString("Constraints.title"),
				Messages.getString("Constraints.description"));
		
		
		managedForm.refresh();

	}

	/**
	 * Creates a generic section
	 */
	public Section createSection(IManagedForm mform, String title,
			String desc, int numColumns) {
		
		final ScrolledForm form = mform.getForm();
		FormToolkit toolkit = mform.getToolkit();
		Section section = toolkit.createSection(form.getBody(), Section.TWISTIE
				| Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		section.setText(title);
		section.setDescription(desc);
		toolkit.createCompositeSeparator(section);
		return section;
	}

	/**
	 * Creates a generic section
	 */
	public Composite createSection(IManagedForm mform, String title,
			String desc, int numColumns, GridData gridData) {

		
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
		section.setLayoutData(gridData);
		return client;
	}

	/**
	 * Creates the section editing constraints
	 */
	private void createConstraintsSection(IManagedForm managedForm, String title, String desc) {

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		Section section = createSection(managedForm, title, desc, 2);
		section.setLayout(new ColumnLayout());
	

		checkStateListener = new ConstraintsCheckStateListener(
				section, scenario);
		
		// Creates the section part containing the tree with SDF vertices
		new SDFTreeSection(scenario, section, managedForm.getToolkit(),Section.DESCRIPTION,this,checkStateListener);
	}

	/**
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if(source instanceof ConstraintsCheckStateListener && propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);
		
	}


	/**
	 * Creates a section to edit a file
	 * 
	 * @param mform form containing the section
	 * @param title section title
	 * @param desc description of the section
	 * @param fileEdit text to display in text label
	 * @param initValue initial value of Text
	 * @param browseTitle title of file browser
	 */
	private void createFileSection(IManagedForm mform, String title, String desc, String fileEdit, String initValue, String browseTitle,String fileExtension) {
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 120;
		Composite client = createSection(mform, title, desc, 2,gridData);
		FormToolkit toolkit = mform.getToolkit();

		GridData gd = new GridData();
		toolkit.createLabel(client, fileEdit);

		Text text = toolkit.createText(client, initValue, SWT.SINGLE);
		text.setData(title);
		text.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				Text text = (Text)e.getSource();

				importData(text);
				
			}});
		
		text.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == SWT.CR){
					Text text = (Text)e.getSource();
					importData(text);
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
		});
		
		gd.widthHint =400;
		text.setLayoutData(gd);

		final Button button = toolkit.createButton(client, Messages.getString("Overview.browse"), SWT.PUSH);
		SelectionAdapter adapter = new FileSelectionAdapter(text,client.getShell(),browseTitle,fileExtension);
		button.addSelectionListener(adapter);
		
		toolkit.paintBordersFor(client);
	}
	
	private void importData(Text text){

		scenario.getConstraintGroupManager().setExcelFileURL(text.getText());
		scenario.getConstraintGroupManager().importConstraints(scenario);
		
		firePropertyChange(PROP_DIRTY);
		
		if(checkStateListener != null){
			checkStateListener.updateCheck();
		}
	}
}
