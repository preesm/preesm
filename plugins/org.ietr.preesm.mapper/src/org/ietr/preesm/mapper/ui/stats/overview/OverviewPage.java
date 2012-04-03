/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.mapper.ui.stats.overview;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.mapper.ui.Messages;
import org.ietr.preesm.mapper.ui.stats.StatGenerator;

/**
 * This page contains general informations of the scenario including current
 * algorithm and current architecture
 * 
 * @author mpelcat
 */
public class OverviewPage extends FormPage {

	private StatGenerator statGen = null;

	public OverviewPage(StatGenerator statGen, FormEditor editor, String id,
			String title) {
		super(editor, id, title);

		this.statGen = statGen;
	}

	/**
	 * Creation of the sections and their initialization
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {

		ScrolledForm form = managedForm.getForm();
		// FormToolkit toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("Overview.title"));

		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);

		CreatePropSection(managedForm,
				Messages.getString("Overview.properties.title"),
				Messages.getString("Overview.properties.description"));
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
	 * Creates the section editing timings
	 */
	private void CreatePropSection(IManagedForm managedForm, String title,
			String desc) {

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());
		Composite container = createSection(managedForm, title, desc, 1,
				new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		FormToolkit toolkit = managedForm.getToolkit();

		final DeploymentProperties props = new DeploymentProperties(statGen);
		Text text = addPaceEditor(container, toolkit, props);
		addTable(container, toolkit, text, props);

	}

	private Text addPaceEditor(Composite parent, FormToolkit toolkit,
			DeploymentProperties props) {

		toolkit.createLabel(parent,
				Messages.getString("Overview.properties.paceEditor.label"));

		Text text = toolkit.createText(parent,
				String.valueOf(props.getRepetitionPeriod()), SWT.SINGLE);

		GridData gd = new GridData();
		gd.widthHint = 400;
		text.setLayoutData(gd);

		return text;
	}

	public StatGenerator getStatGen() {
		return statGen;
	}

	/**
	 * Adds a table to edit timings
	 */
	protected void addTable(Composite parent, FormToolkit toolkit, Text text,
			final DeploymentProperties props) {

		Composite tablecps = toolkit.createComposite(parent);
		tablecps.setVisible(true);

		final TableViewer tableViewer = new TableViewer(tablecps, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayout(new GridLayout());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tableViewer.setContentProvider(props);
		tableViewer.setLabelProvider(props);

		// Create columns
		final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
		column1.setText(Messages.getString("Overview.properties.opColumn"));

		final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
		column2.setText(Messages.getString("Overview.properties.loadColumn"));

		final TableColumn column3 = new TableColumn(table, SWT.NONE, 2);
		column3.setText(Messages.getString("Overview.properties.memColumn"));

		Listener charSortListener = new Listener() {
			public void handleEvent(Event e) {
				TableColumn column = (TableColumn) e.widget;
				props.setColumnOrder(column.getText());
				tableViewer.refresh();
			}
		};

		column1.addListener(SWT.Selection, charSortListener);
		column2.addListener(SWT.Selection, charSortListener);
		column3.addListener(SWT.Selection, charSortListener);

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
					column1.setWidth(width / 3 - 1);
					column2.setWidth(width / 3 - 1);
					column3.setWidth(width / 3 - 1);
					tref.setSize(area.width, area.height);
				} else {
					tref.setSize(area.width, area.height);
					column1.setWidth(width / 3 - 1);
					column2.setWidth(width / 3 - 1);
					column3.setWidth(width / 3 - 1);
				}
			}
		});

		tableViewer.setInput(props);
		tablecps.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));

		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.getSource();
				props.setRepetitionPeriod(Integer.valueOf(text.getText()));
				tableViewer.refresh();
			}
		});
	}
}
