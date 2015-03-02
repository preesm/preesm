/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.ui.scenario.editor.codegen;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.ietr.preesm.ui.scenario.editor.Messages;

/**
 * code generation properties editor within the implementation editor
 * 
 * @author mpelcat
 */
public class CodegenPage extends FormPage {

	/**
	 * Currently edited scenario
	 */
	private PreesmScenario scenario;

	public CodegenPage(PreesmScenario scenario, FormEditor editor, String id,
			String title) {
		super(editor, id, title);
		this.scenario = scenario;
	}

	/**
	 * Initializes the display content
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();
		form.setText(Messages.getString("Codegen.title"));

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

		Set<String> algoExtensions = new HashSet<String>();
		algoExtensions.add("graphml");

		// Algorithm file chooser section
		createDirectorySection(managedForm,
				Messages.getString("Codegen.codeDirectory"),
				Messages.getString("Codegen.codeDirectoryDescription"),
				Messages.getString("Codegen.codeDirectoryEdit"), scenario
						.getCodegenManager().getCodegenDirectory(),
				Messages.getString("Codegen.codeDirectoryBrowseTitle"));

		managedForm.refresh();
		managedForm.reflow(true);

	}

	/**
	 * Creates a blank section with expansion capabilities
	 */
	private Composite createSection(IManagedForm mform, String title,
			String desc, int numColumns) {

		final ScrolledForm form = mform.getForm();
		FormToolkit toolkit = mform.getToolkit();
		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TWISTIE
				| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
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
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(false);
			}
		});
		return client;
	}

	/**
	 * Creates a section to edit a directory
	 * 
	 * @param mform
	 *            form containing the section
	 * @param title
	 *            section title
	 * @param desc
	 *            description of the section
	 * @param fileEdit
	 *            text to display in text label
	 * @param initValue
	 *            initial value of Text
	 * @param browseTitle
	 *            title of file browser
	 */
	private void createDirectorySection(IManagedForm mform, String title,
			String desc, String fileEdit, String initValue, String browseTitle) {

		Composite client = createSection(mform, title, desc, 2);

		FormToolkit toolkit = mform.getToolkit();

		GridData gd = new GridData();
		toolkit.createLabel(client, fileEdit);

		Text text = toolkit.createText(client, initValue, SWT.SINGLE);
		text.setData(title);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.getSource();

				scenario.getCodegenManager()
						.setCodegenDirectory(text.getText());

				firePropertyChange(PROP_DIRTY);

			}
		});

		gd.widthHint = 400;
		text.setLayoutData(gd);

		final Button button = toolkit.createButton(client,
				Messages.getString("Codegen.browse"), SWT.PUSH);
		SelectionAdapter adapter = new FileSelectionAdapter(text,
				client.getShell(), browseTitle);
		button.addSelectionListener(adapter);

		toolkit.paintBordersFor(client);
	}
}
