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

package org.ietr.preesm.core.scenario.editor.simu;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.codegen.DataType;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.scenario.editor.Messages;

/**
 * This page contains parameters to influence the deployment simulator
 * 
 * @author mpelcat
 */
public class SimulationPage extends FormPage implements IPropertyListener {

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
		GridLayout layout = new GridLayout(2, true);
		form.getBody().setLayout(layout);

		// Main operator chooser section
		createComboBoxSection(managedForm, Messages
				.getString("Simulation.mainOperator.title"), Messages
				.getString("Simulation.mainOperator.description"), Messages
				.getString("Simulation.mainOperatorSelectionTooltip"),
				"operator");

		// Main medium chooser section
		createComboBoxSection(managedForm, Messages
				.getString("Simulation.mainMedium.title"), Messages
				.getString("Simulation.mainMedium.description"), Messages
				.getString("Simulation.mainMediumSelectionTooltip"), "medium");

		// Data type section
		createDataTypesSection(managedForm, Messages
				.getString("Simulation.DataTypes.title"), Messages
				.getString("Simulation.DataTypes.description"));

		// Average data size section
		createIntegerSection(managedForm, Messages
				.getString("Simulation.DataAverageSize.title"), Messages
				.getString("Simulation.DataAverageSize.description"));

		managedForm.refresh();
	}

	/**
	 * Creates the section editing the average data size
	 */
	private void createIntegerSection(IManagedForm managedForm, String title,
			String desc) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		Composite client = createSection(managedForm, title, desc, 2, gridData);
		
		FormToolkit toolkit = managedForm.getToolkit();

		Text text = toolkit.createText(client, String.valueOf(scenario.getSimulationManager().getAverageDataSize()), SWT.SINGLE);
		text.setData(title);
		text.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				Text text = (Text)e.getSource();
				int averageSize = 0;
				try {
					averageSize = Integer.valueOf(text.getText());
					scenario.getSimulationManager().setAverageDataSize(averageSize);
					propertyChanged(this, IEditorPart.PROP_DIRTY);
				} catch (NumberFormatException e1) {
				}
			}});
		
		text.setLayoutData(gridData);
		toolkit.paintBordersFor(client);
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
		section.setLayoutData(gridData);
		return client;
	}

	/**
	 * Creates the section editing timings
	 */
	private void createComboBoxSection(IManagedForm managedForm, String title,
			String desc, String tooltip, String type) {
		// Creates the section
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		Composite container = createSection(managedForm, title, desc, 2,
				gridData);

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

			combo.select(combo.indexOf(scenario.getSimulationManager()
					.getMainOperatorName()));
		} else if (type.equals("medium")) {
			for (ArchitectureComponent cmp : archi
					.getComponents(ArchitectureComponentType.medium)) {
				combo.add(((Medium) cmp).getName());
			}

			combo.select(combo.indexOf(scenario.getSimulationManager()
					.getMainMediumName()));
		}

		combo.setData(archi);

		return combo;
	}

	/**
	 * Creates the section editing data types
	 */
	private void createDataTypesSection(IManagedForm managedForm, String title,
			String desc) {

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		Composite container = createSection(managedForm, title, desc, 1,
				new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		FormToolkit toolkit = managedForm.getToolkit();

		addTable(container, toolkit);
	}

	/**
	 * Adds a table to edit data types
	 */
	protected void addTable(Composite parent, FormToolkit toolkit) {

		// Buttons to add and remove data types
		Composite buttonscps = toolkit.createComposite(parent);
		buttonscps.setLayout(new GridLayout(2, true));
		final Button addButton = toolkit.createButton(buttonscps, Messages
				.getString("Simulation.DataTypes.addType"), SWT.PUSH);
		final Button removeButton = toolkit.createButton(buttonscps, Messages
				.getString("Simulation.DataTypes.removeType"), SWT.PUSH);

		Composite tablecps = toolkit.createComposite(parent);
		tablecps.setVisible(true);

		final TableViewer tableViewer = new TableViewer(tablecps, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

		Table table = tableViewer.getTable();
		table.setLayout(new GridLayout());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		// table.setSize(100, 100);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(new DataTypesContentProvider());

		final DataTypesLabelProvider labelProvider = new DataTypesLabelProvider(
				scenario, tableViewer, this);
		tableViewer.setLabelProvider(labelProvider);

		// Create columns
		final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
		column1.setText(Messages.getString("Simulation.DataTypes.typeColumn"));

		final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
		column2.setText(Messages.getString("Simulation.DataTypes.sizeColumn"));

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				labelProvider.handleDoubleClick((IStructuredSelection) e
						.getSelection());
			}
		});

		final Table tref = table;
		final Composite comp = tablecps;

		// Setting the column width
		tablecps.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = comp.getClientArea();
				Point size = tref.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				ScrollBar vBar = tref.getVerticalBar();
				int width = area.width - tref.computeTrim(0, 0, 0, 0).width - 2;
				if (size.y > area.height + tref.getHeaderHeight()) {
					Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				Point oldSize = tref.getSize();
				if (oldSize.x > area.width) {
					column1.setWidth(width / 4 - 1);
					column2.setWidth(width - column1.getWidth());
					tref.setSize(area.width, area.height);
				} else {
					tref.setSize(area.width, area.height);
					column1.setWidth(width / 4 - 1);
					column2.setWidth(width - column1.getWidth());
				}
			}
		});

		tableViewer.setInput(scenario);
		tablecps.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));

		// Adding the new data type on click on add button
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String dialogTitle = Messages
						.getString("Simulation.DataTypes.addType.dialog.title");
				String dialogMessage = Messages
						.getString("Simulation.DataTypes.addType.dialog.message");
				String init = "newType";

				IInputValidator validator = new IInputValidator() {

					// No verification on data type name
					public String isValid(String newText) {
						return null;
					}

				};

				InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), dialogTitle,
						dialogMessage, init, validator);
				if (dialog.open() == Window.OK) {
					DataType dataType = new DataType(dialog.getValue());
					scenario.getSimulationManager().putDataType(dataType);
					tableViewer.refresh();
					propertyChanged(this, IEditorPart.PROP_DIRTY);
				}
			}

		});

		// Removing a data type on click on remove button
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer
						.getSelection();
				if (selection != null
						&& selection.getFirstElement() instanceof DataType) {
					DataType dataType = (DataType) selection.getFirstElement();
					scenario.getSimulationManager().removeDataType(
							dataType.getTypeName());
					tableViewer.refresh();
					propertyChanged(this, IEditorPart.PROP_DIRTY);
				}
			}
		});
	}

	/**
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);

	}
}
