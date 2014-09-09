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
package org.ietr.preesm.ui.scenario.editor.parametervalues;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.preesm.core.scenario.ParameterValue;
import org.ietr.preesm.core.scenario.ParameterValue.ParameterType;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.ui.scenario.editor.Messages;

/**
 * This page contains parameters informations of the {@link PreesmScenario}
 * 
 * @author jheulot
 */
public class PiParametersPage extends FormPage implements IPropertyListener {
	/**
	 * The {@link PreesmScenario}
	 */
	private PreesmScenario scenario = null;

	/**
	 * Page attributes
	 */
	private Section section;
	private TableViewer tableViewer;

	/**
	 * Table of Column name of the multi-column tree viewer
	 */
	private final String[] COLUMN_NAMES = { "Parameters", "Path", "Type",
			"Input Parameters", "Expression" };
	private final int[] COLUMN_SIZE = { 110, 200, 200, 200, 50 };

	/**
	 * Default Constructor of an Variables Page
	 */
	public PiParametersPage(PreesmScenario scenario, FormEditor editor,
			String id, String title) {
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
		f.setText(Messages.getString("Parameters.title"));
		f.getBody().setLayout(new GridLayout());

		// Creates the section
		managedForm.getForm().setLayout(new FillLayout());

		section = managedForm.getToolkit().createSection(
				managedForm.getForm().getBody(),
				Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION
						| Section.EXPANDED);

		section.setText(Messages.getString("Parameters.title"));
		section.setDescription(Messages.getString("Parameters.description"));
		section.setLayout(new ColumnLayout());

		if (scenario.isPISDFScenario()) {
			// Creates the section part containing the tree with SDF vertices

			Composite container = managedForm.getToolkit().createComposite(
					section);
			container.setLayout(new GridLayout());

			// Creating the tree view
			Table table = managedForm.getToolkit().createTable(container,
					SWT.SIMPLE);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);

			for (int i = 0; i < COLUMN_NAMES.length; i++) {
				TableColumn col = new TableColumn(table, SWT.CENTER);
				col.setText(COLUMN_NAMES[i]);
				col.setWidth(COLUMN_SIZE[i]);
			}
			table.setSortColumn(table.getColumn(0));
			table.setSortDirection(SWT.UP);

			tableViewer = new TableViewer(table);

			// The content provider fills the tree
			tableViewer
					.setContentProvider(new PiParameterTableContentProvider());
			tableViewer.setLabelProvider(new PiParameterTableLabelProvider(
					table));
			tableViewer.setComparator(new ViewerComparator() {
				public int compare(Viewer viewer, Object e1, Object e2) {
					return ((ParameterValue) e1).getName().compareTo(
							((ParameterValue) e2).getName());
				}

			});
			tableViewer.setInput(scenario);

			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 400;
			gd.widthHint = 250;
			tableViewer.getTable().setLayoutData(gd);

			section.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					try {
						scenario.update(false, false);
					} catch (InvalidModelException | CoreException
							| FileNotFoundException e1) {
						e1.printStackTrace();
					}
					tableViewer.refresh();
				}
			});

			CellEditor[] editors = new CellEditor[table.getColumnCount()];
			for (int i = 0; i < table.getColumnCount(); i++) {
				editors[i] = new TextCellEditor(table);
			}

			tableViewer.setColumnProperties(COLUMN_NAMES);
			tableViewer.setCellEditors(editors);
			tableViewer.setCellModifier(new ICellModifier() {
				@Override
				public void modify(Object element, String property, Object value) {
					if (element instanceof TableItem) {
						ParameterValue param = (ParameterValue) ((TableItem) element)
								.getData();
						switch (param.getType()) {
						case INDEPENDENT:
							String newValue = (String) value;
							if (newValue != param.getValue()) {
								param.setValue(newValue);
								propertyChanged(this, PROP_DIRTY);
							}
							break;
						case ACTOR_DEPENDENT:
							String s = (String) value;

							if (s.charAt(0) == '['
									&& s.charAt(s.length() - 1) == ']') {
								s = s.substring(1, s.length() - 1);
								String[] values = s.split(",");

								Set<Integer> newValues = new HashSet<Integer>();
								boolean modified = true;

								for (String val : values) {
									try {
										newValues.add(Integer.parseInt(val
												.trim()));
									} catch (NumberFormatException e) {
										modified = false;
										break;
									}
								}

								boolean equalSet = newValues.containsAll(param
										.getValues())
										&& param.getValues().containsAll(
												newValues);
								if (modified && !equalSet) {
									param.getValues().clear();
									param.getValues().addAll(newValues);
									propertyChanged(this, PROP_DIRTY);
								}
							}
							break;
						case PARAMETER_DEPENDENT:
							if (!param.getExpression().contentEquals(
									(String) value)) {
								param.setExpression((String) value);
								propertyChanged(this, PROP_DIRTY);
							}
							break;
						}
						tableViewer.refresh();
					}
				}

				@Override
				public Object getValue(Object element, String property) {
					if (element instanceof ParameterValue) {
						ParameterValue param = (ParameterValue) element;
						if (param.getType() == ParameterType.INDEPENDENT) {
							return param.getValue();
						} else if (param.getType() == ParameterType.ACTOR_DEPENDENT) {
							return param.getValues().toString();
						} else if (param.getType() == ParameterType.PARAMETER_DEPENDENT) {
							return param.getExpression();
						}
					}
					return "";
				}

				@Override
				public boolean canModify(Object element, String property) {
					if (property.contentEquals("Expression")) {
						if (element instanceof ParameterValue) {
							return true;
						}
					}
					return false;
				}
			});

			managedForm.getToolkit().paintBordersFor(container);
			managedForm.getToolkit().paintBordersFor(tableViewer.getTable());
			section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
					| GridData.FILL_VERTICAL));
			section.setClient(container);

			managedForm.refresh();
			managedForm.reflow(true);
		}
	}

	/**
	 * Function of the property listener used to transmit the dirty property
	 */
	@Override
	public void propertyChanged(Object source, int propId) {
		if (source instanceof ICellModifier && propId == PROP_DIRTY)
			firePropertyChange(PROP_DIRTY);
	}
}
