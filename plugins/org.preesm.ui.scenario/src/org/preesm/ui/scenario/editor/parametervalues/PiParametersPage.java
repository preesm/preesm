/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2014 - 2020)
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

import java.util.Map.Entry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.scenario.Scenario;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.ScenarioPage;
import org.preesm.ui.scenario.editor.utils.VertexLexicographicalComparator;

/**
 * This page contains parameters informations of the {@link PreesmScenario}.
 *
 * @author jheulot
 */
public class PiParametersPage extends ScenarioPage {

  /** The {@link PreesmScenario}. */
  private Scenario scenario = null;

  /** The table viewer. */
  private TableViewer tableViewer;

  /** Table of Column name of the multi-column tree viewer. */
  private static final String[] COLUMN_NAMES = { "Parameters", "Type", "Input Parameters", "Graph Expression",
      "Override Expression", "Value" };

  /** The column size. */
  private static final int[] COLUMN_SIZE = { 200, 75, 200, 200, 200, 50 };

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
  public PiParametersPage(final Scenario scenario, final FormEditor editor, final String id, final String title) {
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

    final Section section = managedForm.getToolkit().createSection(managedForm.getForm().getBody(),
        ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION
            | ExpandableComposite.EXPANDED);

    section.setText(Messages.getString("Parameters.title"));
    section.setDescription(Messages.getString("Parameters.description"));
    section.setLayout(new ColumnLayout());

    // Creates the section part containing the tree with SDF vertices

    final Composite container = managedForm.getToolkit().createComposite(section);
    container.setLayout(new GridLayout());

    // Creating the tree view
    final Table table = managedForm.getToolkit().createTable(container, SWT.SIMPLE);
    table.setLinesVisible(true);
    table.setHeaderVisible(true);

    for (int i = 0; i < COLUMN_NAMES.length; i++) {
      final TableColumn col = new TableColumn(table, SWT.NONE);
      col.setText(COLUMN_NAMES[i]);
      col.setWidth(COLUMN_SIZE[i]);
    }
    table.setSortColumn(table.getColumn(0));
    table.setSortDirection(SWT.UP);

    this.tableViewer = new TableViewer(table);

    // The content provider fills the tree
    this.tableViewer.setContentProvider(new PiParameterTableContentProvider());
    this.tableViewer.setLabelProvider(new PiParameterTableLabelProvider(table, this.scenario));
    this.tableViewer.setComparator(new VertexLexicographicalComparator());
    this.tableViewer.setInput(this.scenario);

    final GridData gd = new GridData(GridData.FILL_BOTH);
    gd.heightHint = 400;
    gd.widthHint = 250;
    this.tableViewer.getTable().setLayoutData(gd);

    final CellEditor[] editors = new CellEditor[table.getColumnCount()];
    for (int i = 0; i < table.getColumnCount(); i++) {
      editors[i] = new TextCellEditor(table);
    }

    this.tableViewer.setColumnProperties(COLUMN_NAMES);
    this.tableViewer.setCellEditors(editors);
    this.tableViewer.setCellModifier(new ICellModifier() {
      @Override
      public void modify(final Object element, final String property, final Object value) {
        if (element instanceof TableItem) {
          @SuppressWarnings("unchecked")
          Entry<Parameter, String> param = (Entry<Parameter, String>) ((TableItem) element).getData();
          String newValue = (String) value;
          if (!newValue.equals(param.getValue())) {
            if (newValue.isEmpty()) {
              newValue = param.getKey().getExpression().getExpressionAsString();
            }
            param.setValue(newValue);
            propertyChanged(this, IEditorPart.PROP_DIRTY);
            PiParametersPage.this.tableViewer.refresh();
          }
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof Entry) {
          @SuppressWarnings("unchecked")
          final Entry<Parameter, String> param = (Entry<Parameter, String>) element;
          return param.getValue();
        }
        return "";
      }

      @Override
      public boolean canModify(final Object element, final String property) {
        final boolean overrideColumn = property.contentEquals(COLUMN_NAMES[4]);
        final boolean properElementType = element instanceof Entry;
        if (overrideColumn && properElementType) {
          @SuppressWarnings("unchecked")
          final Entry<Parameter, String> param = (Entry<Parameter, String>) element;
          return param.getKey().isLocallyStatic();
        }
        return false;
      }
    });

    managedForm.getToolkit().paintBordersFor(container);
    managedForm.getToolkit().paintBordersFor(this.tableViewer.getTable());
    section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
    section.setClient(container);

    managedForm.refresh();
    managedForm.reflow(true);
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
