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
 
package org.ietr.preesm.core.scenario.editor.codegen;

import org.eclipse.swt.SWT;
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
import org.ietr.preesm.core.scenario.editor.constraints.ConstraintsCheckStateListener;

/**
 * code generation properties editor within the implementation editor
 * 
 * @author mpelcat
 */
public class CodegenPage extends FormPage implements IPropertyListener {

	/**
	 * Currently edited scenario
	 */
	private Scenario scenario;

	public CodegenPage(Scenario scenario, FormEditor editor, String id, String title) {
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
		f.setText(Messages.getString("Codegen.title"));
		f.getBody().setLayout(new GridLayout());

		// Section to select the code generation phase for each task
		createCodegenPhaseSection(managedForm, Messages.getString("Codegen.Phase.title"),
				Messages.getString("Codegen.Phase.description"));
		
		
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
	 * Creates the section editing constraints
	 */
	private void createCodegenPhaseSection(IManagedForm managedForm, String title, String desc) {

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		Section section = createSection(managedForm, title, desc, 2);
		section.setLayout(new ColumnLayout());
	

		CodegenPhasesCheckStateListener checkStateListener = new CodegenPhasesCheckStateListener(
				section, scenario);
		
		// Creates the section part containing the tree with SDF vertices
		new SDFTreeSection(scenario, section, managedForm.getToolkit(),Section.DESCRIPTION,this, checkStateListener);
	}

	/**
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if(propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);
		
	}
}
