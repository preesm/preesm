/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2014)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.ui.scenario.editor.parametervalues;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.ParameterValue;
import org.ietr.preesm.core.scenario.ParameterValue.ParameterType;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.preesm.algorithm.importer.InvalidModelException;
import org.preesm.ui.scenario.editor.Messages;

// TODO: Auto-generated Javadoc
/**
 * This page contains parameters informations of the {@link PreesmScenario}.
 *
 * @author jheulot
 */
public class PiParametersPage extends FormPage implements IPropertyListener {

  /** The {@link PreesmScenario}. */
  private PreesmScenario scenario = null;

  /** Page attributes. */
  private Section section;

  /** The table viewer. */
  private TableViewer tableViewer;

  /** Table of Column name of the multi-column tree viewer. */
  private final String[] COLUMN_NAMES = { "Parameters", "Path", "Type", "Input Parameters", "Expression" };

  /** The column size. */
  private final int[] COLUMN_SIZE = { 110, 200, 200, 200, 50 };

  /**
   * Default Constructor of an Variables Page.
   *
   * @param scenario
   *          the scenario
   * @param editor
   *          the editor
   * @param id
   *          the id
   * @param title
   *          the title
   */
  public PiParametersPage(final PreesmScenario scenario, final FormEditor editor, final String id, final String title) {
    super(editor, id, title);
    this.scenario = scenario;
  }

  /**
   * Initializes the display content.
   *
   * @param managedForm
   *          the managed form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    super.createFormContent(managedForm);

    final ScrolledForm f = managedForm.getForm();
    f.setText(Messages.getString("Parameters.title"));
    f.getBody().setLayout(new GridLayout());

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());

    this.section = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), ExpandableComposite.TWISTIE
        | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);

    this.section.setText(Messages.getString("Parameters.title"));
    this.section.setDescription(Messages.getString("Parameters.description"));
    this.section.setLayout(new ColumnLayout());

    if (this.scenario.isPISDFScenario()) {
      // Creates the section part containing the tree with SDF vertices

      final Composite container = managedForm.getToolkit().createComposite(this.section);
      container.setLayout(new GridLayout());

      // Creating the tree view
      final Table table = managedForm.getToolkit().createTable(container, SWT.SIMPLE);
      table.setLinesVisible(true);
      table.setHeaderVisible(true);

      for (int i = 0; i < this.COLUMN_NAMES.length; i++) {
        final TableColumn col = new TableColumn(table, SWT.CENTER);
        col.setText(this.COLUMN_NAMES[i]);
        col.setWidth(this.COLUMN_SIZE[i]);
      }
      table.setSortColumn(table.getColumn(0));
      table.setSortDirection(SWT.UP);

      this.tableViewer = new TableViewer(table);

      // The content provider fills the tree
      this.tableViewer.setContentProvider(new PiParameterTableContentProvider());
      this.tableViewer.setLabelProvider(new PiParameterTableLabelProvider(table));
      this.tableViewer.setComparator(new ViewerComparator() {
        @Override
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
          return ((ParameterValue) e1).getName().compareTo(((ParameterValue) e2).getName());
        }

      });
      this.tableViewer.setInput(this.scenario);

      final GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 400;
      gd.widthHint = 250;
      this.tableViewer.getTable().setLayoutData(gd);

      this.section.addPaintListener(e -> {
        try {
          PiParametersPage.this.scenario.update(false, false);
        } catch (InvalidModelException | CoreException ex) {
          ex.printStackTrace();
        }
        PiParametersPage.this.tableViewer.refresh();
      });

      final CellEditor[] editors = new CellEditor[table.getColumnCount()];
      for (int i = 0; i < table.getColumnCount(); i++) {
        editors[i] = new TextCellEditor(table);
      }

      this.tableViewer.setColumnProperties(this.COLUMN_NAMES);
      this.tableViewer.setCellEditors(editors);
      this.tableViewer.setCellModifier(new ICellModifier() {
        @Override
        public void modify(final Object element, final String property, final Object value) {
          if (element instanceof TableItem) {
            final ParameterValue param = (ParameterValue) ((TableItem) element).getData();
            switch (param.getType()) {
              case INDEPENDENT:
                final String newValue = (String) value;
                if (!newValue.equals(param.getValue())) {
                  param.setValue(newValue);
                  propertyChanged(this, IEditorPart.PROP_DIRTY);
                }
                break;
              case ACTOR_DEPENDENT:
                String s = (String) value;

                if ((s.charAt(0) == '[') && (s.charAt(s.length() - 1) == ']')) {
                  s = s.substring(1, s.length() - 1);
                  final String[] values = s.split(",");

                  final Set<Integer> newValues = new LinkedHashSet<>();
                  boolean modified = true;

                  for (final String val : values) {
                    try {
                      newValues.add(Integer.parseInt(val.trim()));
                    } catch (final NumberFormatException e) {
                      modified = false;
                      break;
                    }
                  }

                  final boolean equalSet = newValues.containsAll(param.getValues())
                      && param.getValues().containsAll(newValues);
                  if (modified && !equalSet) {
                    param.getValues().clear();
                    param.getValues().addAll(newValues);
                    propertyChanged(this, IEditorPart.PROP_DIRTY);
                  }
                }
                break;
              case PARAMETER_DEPENDENT:
                if (!param.getExpression().contentEquals((String) value)) {
                  param.setExpression((String) value);
                  propertyChanged(this, IEditorPart.PROP_DIRTY);
                }
                break;
              default:
            }
            PiParametersPage.this.tableViewer.refresh();
          }
        }

        @Override
        public Object getValue(final Object element, final String property) {
          if (element instanceof ParameterValue) {
            final ParameterValue param = (ParameterValue) element;
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
        public boolean canModify(final Object element, final String property) {
          if (property.contentEquals("Expression")) {
            if (element instanceof ParameterValue) {
              return true;
            }
          }
          return false;
        }
      });

      managedForm.getToolkit().paintBordersFor(container);
      managedForm.getToolkit().paintBordersFor(this.tableViewer.getTable());
      this.section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
      this.section.setClient(container);

      managedForm.refresh();
      managedForm.reflow(true);
    }
  }

  /**
   * Function of the property listener used to transmit the dirty property.
   *
   * @param source
   *          the source
   * @param propId
   *          the prop id
   */
  @Override
  public void propertyChanged(final Object source, final int propId) {
    if ((source instanceof ICellModifier) && (propId == IEditorPart.PROP_DIRTY)) {
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
  }
}
