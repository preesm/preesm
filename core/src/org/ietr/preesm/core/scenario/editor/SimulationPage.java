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

package org.ietr.preesm.core.scenario.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;

/**
 * This page contains parameters to influence the deployment simulator
 * 
 * @author mpelcat
 */
public class SimulationPage extends FormPage {

	private class ComboBoxListener implements SelectionListener {

		// "operator" or "medium"
		String type = "";

		public ComboBoxListener(String type) {
			super();
			this.type = type;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.getSource() instanceof Combo) {
				Combo combo = ((Combo) e.getSource());
				String item = combo.getItem(combo.getSelectionIndex());
				MultiCoreArchitecture archi = (MultiCoreArchitecture) combo
						.getData();

				if (type.equals("operator")) {
					Operator mainOp = (Operator) archi.getComponent(
							ArchitectureComponentType.operator, item);

					scenario.getSimulationManager().setMainOperatorName(
							mainOp.getName());
				} else if (type.equals("medium")) {
					Medium mainMed = (Medium) archi.getComponent(
							ArchitectureComponentType.medium, item);

					scenario.getSimulationManager().setMainMediumName(
							mainMed.getName());
				}
			}

			firePropertyChange(PROP_DIRTY);
		}

	}

	/**
	 * The current scenario being edited
	 */
	private Scenario scenario;

	public SimulationPage(Scenario scenario, FormEditor editor, String id,
			String title) {
		super(editor, id, title);

		this.scenario = scenario;
	}

	/**
	 * Creation of the sections and their initialization
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {

		ScrolledForm form = managedForm.getForm();
		// FormToolkit toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("Simulation.title"));
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

		// Main operator chooser section
		createComboBoxSection(managedForm, Messages
				.getString("Simulation.mainOperator.title"), Messages
				.getString("Simulation.mainOperator.description"), Messages
				.getString("Simulation.mainOperatorSelectionTooltip"), "operator");

		// Main medium chooser section
		createComboBoxSection(managedForm, Messages
				.getString("Simulation.mainMedium.title"), Messages
				.getString("Simulation.mainMedium.description"), Messages
				.getString("Simulation.mainMediumSelectionTooltip"), "medium");

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
	 * Creates the section editing timings
	 */
	private void createComboBoxSection(IManagedForm managedForm, String title,
			String desc, String tooltip, String type) {

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		Composite container = createSection(managedForm, title, desc, 2);
		FormToolkit toolkit = managedForm.getToolkit();

		Combo coreCombo = addCoreSelector(container, toolkit, tooltip, type);
		coreCombo.addSelectionListener(new ComboBoxListener(type));
	}

	/**
	 * Adds a combo box for the core selection
	 */
	protected Combo addCoreSelector(Composite parent, FormToolkit toolkit,
			String tooltip, String type) {
		Composite combocps = toolkit.createComposite(parent);
		combocps.setLayout(new FillLayout());
		combocps.setVisible(true);
		Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setToolTipText(tooltip);

		MultiCoreArchitecture archi = ScenarioParser.getArchitecture(scenario
				.getArchitectureURL());

		if (type.equals("operator")) {
			for (ArchitectureComponent cmp : archi
					.getComponents(ArchitectureComponentType.operator)) {
				combo.add(((Operator) cmp).getName());
			}

			combo.select(combo.indexOf(scenario.getSimulationManager().getMainOperatorName()));
		} else if (type.equals("medium")) {
			for (ArchitectureComponent cmp : archi
					.getComponents(ArchitectureComponentType.medium)) {
				combo.add(((Medium) cmp).getName());
			}

			combo.select(combo.indexOf(scenario.getSimulationManager().getMainMediumName()));
		}

		combo.setData(archi);

		return combo;
	}
}
