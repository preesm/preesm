/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.ui.piscenario.editor;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.ui.scenario.editor.Messages;

/**
 * Constraint editor within the implementation editor
 * 
 * @author jheulot
 */
public class PiConstraintsPage extends FormPage implements IPropertyListener {

	/**
	 * Currently edited scenario
	 */
	private PiScenario piscenario = null;

	/**
	 * Default Constructor of an Constraint Page
	 */	
	public PiConstraintsPage(PiScenario scenario, FormEditor editor,
			String id, String title) {
		super(editor, id, title);
		this.piscenario = scenario;
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

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		
		Section section = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), 
				Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);

		section.setText(Messages.getString("Constraints.title"));
		section.setDescription(Messages.getString("Constraints.description"));
		managedForm.getToolkit().createCompositeSeparator(section);
		section.setLayout(new ColumnLayout());


		// Creates the section part containing the tree with SDF vertices
		new PiConstraintsTreeSection(piscenario, section, managedForm.getToolkit(),
				Section.DESCRIPTION, this, new PiConstraintsListener(section, piscenario));

		managedForm.refresh();
		managedForm.reflow(true);
	}

	/**
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (source instanceof PiConstraintsListener
				&& propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);

	}
}
