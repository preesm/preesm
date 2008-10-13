/**
 * 
 */
package org.ietr.preesm.core.scenario.editor.timings;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.ScenarioParser;
import org.ietr.preesm.core.scenario.editor.Messages;
import org.ietr.preesm.core.scenario.editor.constraints.SDFCheckStateListener;
import org.ietr.preesm.core.scenario.editor.constraints.SDFLabelProvider;

/**
 * Timing editor within the implementation editor
 * 
 * @author mpelcat
 */
public class TimingsPage extends FormPage implements IPropertyListener {

	Scenario scenario;

	public TimingsPage(Scenario scenario, FormEditor editor, String id,
			String title) {
		super(editor, id, title);

		this.scenario = scenario;
	}

	/**
	 * Creates the elements to display
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		// TODO Auto-generated method stub
		super.createFormContent(managedForm);

		ScrolledForm f = managedForm.getForm();
		f.setText(Messages.getString("Timings.title"));
		f.getBody().setLayout(new GridLayout());

		createTimingsSection(managedForm, Messages.getString("Timings.title"),
				Messages.getString("Timings.description"));

		managedForm.refresh();

	}

	/**
	 * Creates the section editing timings
	 */
	private void createTimingsSection(IManagedForm managedForm, String title,
			String desc) {

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		Composite container = createSection(managedForm, title, desc, 1);
		FormToolkit toolkit = managedForm.getToolkit();

		toolkit.paintBordersFor(container);
		container.setSize(100, 100);

		Combo coreCombo = addCoreSelector(container, toolkit);
		addTable(container, toolkit, coreCombo);
	}

	/**
	 * Creates a generic section
	 */
	public Composite createSection(IManagedForm mform, String title,
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
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));
		return client;
	}

	/**
	 * Adds a combo box for the core selection
	 */
	protected Combo addCoreSelector(Composite parent, FormToolkit toolkit) {
		Composite combocps = toolkit.createComposite(parent);
		combocps.setLayout(new FillLayout());
		combocps.setVisible(true);
		Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setToolTipText(Messages
				.getString("Constraints.coreSelectionTooltip"));

		IArchitecture archi = ScenarioParser.getArchitecture(scenario
				.getArchitectureURL());

		for (OperatorDefinition def : archi.getOperatorDefinitions()) {
			combo.add(def.getId());
		}

		combo.setData(archi);

		return combo;
	}

	/**
	 * Adds a table to edit timings
	 */
	protected void addTable(Composite parent, FormToolkit toolkit,
			Combo coreCombo) {

		Composite tablecps = toolkit.createComposite(parent);
		tablecps.setVisible(true);

		TableViewer tableViewer = new TableViewer(tablecps, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayout(new GridLayout());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		// table.setSize(100, 100);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		SDFListContentProvider contentProvider = new SDFListContentProvider();
		tableViewer.setContentProvider(new SDFListContentProvider());

		final SDFTableLabelProvider labelProvider = new SDFTableLabelProvider(
				scenario, tableViewer, this);
		tableViewer.setLabelProvider(labelProvider);
		coreCombo.addSelectionListener(labelProvider);

		
		// Create columns
		final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
		column1.setText(Messages.getString("Timings.taskColumn"));
		
		final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
		column2.setText(Messages.getString("Timings.timeColumn"));
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				labelProvider.handleDoubleClick((IStructuredSelection) e.getSelection());
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
	}

	/**
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if(source instanceof SDFTableLabelProvider && propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);
		
	}
}
