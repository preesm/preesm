/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2015)
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
package org.ietr.preesm.ui.scenario.editor.variables;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.ietr.preesm.ui.scenario.editor.Messages;

// TODO: Auto-generated Javadoc
/**
 * Variables values overriding editor within the scenario editor.
 *
 * @author mpelcat
 * @author kdesnos
 */
public class VariablesPage extends FormPage implements IPropertyListener {

  /** The scenario. */
  final PreesmScenario scenario;

  /** The table viewer. */
  TableViewer tableViewer = null;

  /**
   * Current graph in the scenario initialized when the variables page is displayed.
   */
  SDFGraph currentGraph = null;

  /**
   * Instantiates a new variables page.
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
  public VariablesPage(final PreesmScenario scenario, final FormEditor editor, final String id, final String title) {
    super(editor, id, title);

    this.scenario = scenario;
  }

  /**
   * Creates the elements to display.
   *
   * @param managedForm
   *          the managed form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    super.createFormContent(managedForm);

    final ScrolledForm form = managedForm.getForm();
    // FormToolkit toolkit = managedForm.getToolkit();
    form.setText(Messages.getString("Variables.title"));

    final GridLayout layout = new GridLayout();
    form.getBody().setLayout(layout);

    if (this.scenario.isIBSDFScenario()) {

      // Variable file chooser section
      createFileSection(managedForm, Messages.getString("Variables.excelFile"),
          Messages.getString("Variables.excelFileDescription"), Messages.getString("Variables.excelFileEdit"),
          this.scenario.getVariablesManager().getExcelFileURL(), Messages.getString("Variables.excelFileBrowseTitle"),
          "xls");

      createVariablesSection(managedForm, Messages.getString("Variables.title"),
          Messages.getString("Variables.description"));

      managedForm.refresh();
      managedForm.reflow(true);
    }

  }

  /**
   * Creates the section editing timings.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createVariablesSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final Composite container = createSection(managedForm, title, desc, 1,
        new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
    final FormToolkit toolkit = managedForm.getToolkit();

    addTable(container, toolkit);
  }

  /**
   * Creates a generic section.
   *
   * @param mform
   *          the mform
   * @param title
   *          the title
   * @param desc
   *          the desc
   * @param numColumns
   *          the num columns
   * @param gridData
   *          the grid data
   * @return the composite
   */
  public Composite createSection(final IManagedForm mform, final String title, final String desc, final int numColumns,
      final GridData gridData) {

    final ScrolledForm form = mform.getForm();
    final FormToolkit toolkit = mform.getToolkit();
    final Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TWISTIE
        | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
    section.setText(title);
    section.setDescription(desc);
    toolkit.createCompositeSeparator(section);
    final Composite client = toolkit.createComposite(section);
    final GridLayout layout = new GridLayout();
    layout.marginWidth = layout.marginHeight = 0;
    layout.numColumns = numColumns;
    client.setLayout(layout);
    section.setClient(client);
    section.addExpansionListener(new ExpansionAdapter() {
      @Override
      public void expansionStateChanged(final ExpansionEvent e) {
        form.reflow(false);
      }
    });
    section.setLayoutData(gridData);
    return client;
  }

  /**
   * Adds a table to edit timings.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  protected void addTable(final Composite parent, final FormToolkit toolkit) {

    final Composite buttonscps = toolkit.createComposite(parent);
    buttonscps.setLayout(new GridLayout(2, true));

    final Button addButton = toolkit.createButton(buttonscps, Messages.getString("Variables.addVar"), SWT.PUSH);
    final Button removeButton = toolkit.createButton(buttonscps, Messages.getString("Variables.removeVar"), SWT.PUSH);

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    this.tableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
    final Table table = this.tableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    this.tableViewer.setContentProvider(new VariablesContentProvider());

    final VariablesLabelProvider labelProvider = new VariablesLabelProvider(this.scenario, this.tableViewer, this);
    this.tableViewer.setLabelProvider(labelProvider);

    // Create columns
    final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
    column1.setText(Messages.getString("Variables.variableNameColumn"));

    final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
    column2.setText(Messages.getString("Variables.variableValueColumn"));

    this.tableViewer
        .addDoubleClickListener(e -> labelProvider.handleDoubleClick((IStructuredSelection) e.getSelection()));

    final Table tref = table;
    final Composite comp = tablecps;

    // Setting the column width
    tablecps.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(final ControlEvent e) {
        final Rectangle area = comp.getClientArea();
        final Point size = tref.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        final ScrollBar vBar = tref.getVerticalBar();
        int width = area.width - tref.computeTrim(0, 0, 0, 0).width - 2;
        if (size.y > (area.height + tref.getHeaderHeight())) {
          final Point vBarSize = vBar.getSize();
          width -= vBarSize.x;
        }
        final Point oldSize = tref.getSize();
        if (oldSize.x > area.width) {
          column1.setWidth((width / 4) - 1);
          column2.setWidth(width - column1.getWidth());
          tref.setSize(area.width, area.height);
        } else {
          tref.setSize(area.width, area.height);
          column1.setWidth((width / 4) - 1);
          column2.setWidth(width - column1.getWidth());
        }
      }
    });

    this.tableViewer.setInput(this.scenario);
    tablecps.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

    // Tree is refreshed in case of algorithm modifications
    /*
     * parent.addPaintListener(new PaintListener() {
     *
     * @Override public void paintControl(PaintEvent e) { tableViewer.refresh();
     *
     * }
     *
     * });
     */

    // Adding the new data type on click on add button
    addButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final String dialogTitle = Messages.getString("Variables.addVar.dialog.title");
        final String dialogMessage = Messages.getString("Variables.addVar.dialog.message");
        final String init = "newType";

        final IInputValidator validator = newText -> {
          if ((VariablesPage.this.currentGraph != null)
              && VariablesPage.this.currentGraph.getVariables().keySet().contains(newText)) {
            return null;
          } else {
            return "the top graph does not contain the variable.";
          }
        };

        final InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dialogTitle, dialogMessage, init, validator);
        if (dialog.open() == Window.OK) {
          VariablesPage.this.scenario.getVariablesManager().setVariable(dialog.getValue(), "0");
          VariablesPage.this.tableViewer.refresh();
          propertyChanged(this, IEditorPart.PROP_DIRTY);
        }
      }

    });

    // Removing a data type on click on remove button
    removeButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection = (IStructuredSelection) VariablesPage.this.tableViewer.getSelection();
        if ((selection != null) && (selection.getFirstElement() instanceof Variable)) {
          final Variable var = (Variable) selection.getFirstElement();
          VariablesPage.this.scenario.getVariablesManager().removeVariable(var.getName());
          VariablesPage.this.tableViewer.refresh();
          propertyChanged(this, IEditorPart.PROP_DIRTY);
        }
      }
    });
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
    if (propId == IEditorPart.PROP_DIRTY) {
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }

  }

  /**
   * Creates a section to edit a file.
   *
   * @param mform
   *          form containing the section
   * @param title
   *          section title
   * @param desc
   *          description of the section
   * @param fileEdit
   *          text to display in text label
   * @param initValue
   *          initial value of Text
   * @param browseTitle
   *          title of file browser
   * @param fileExtension
   *          the file extension
   */
  private void createFileSection(final IManagedForm mform, final String title, final String desc, final String fileEdit,
      final String initValue, final String browseTitle, final String fileExtension) {

    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 120;
    final Composite client = createSection(mform, title, desc, 3, gridData);

    final FormToolkit toolkit = mform.getToolkit();

    final GridData gd = new GridData();
    toolkit.createLabel(client, fileEdit);

    final Text text = toolkit.createText(client, initValue, SWT.SINGLE);
    text.setData(title);

    // If the text is modified or Enter key pressed, timings are imported
    text.addModifyListener(e -> {
      final Text text1 = (Text) e.getSource();
      VariablesPage.this.scenario.getVariablesManager().setExcelFileURL(text1.getText());
      VariablesPage.this.scenario.getVariablesManager().importVariables(VariablesPage.this.scenario);
      VariablesPage.this.tableViewer.refresh();
      firePropertyChange(IEditorPart.PROP_DIRTY);

    });
    text.addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent e) {
        if (e.keyCode == SWT.CR) {
          final Text text = (Text) e.getSource();
          VariablesPage.this.scenario.getVariablesManager().setExcelFileURL(text.getText());
          VariablesPage.this.scenario.getVariablesManager().importVariables(VariablesPage.this.scenario);
          VariablesPage.this.tableViewer.refresh();
        }

      }

      @Override
      public void keyReleased(final KeyEvent e) {

      }
    });

    gd.widthHint = 400;
    text.setLayoutData(gd);

    // Add a "Refresh" button to the scenario editor
    final Button refreshButton = toolkit.createButton(client, Messages.getString("Variables.variablesFileRefresh"),
        SWT.PUSH);
    refreshButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        // Cause scenario editor to import variables from excel sheet
        VariablesPage.this.scenario.getVariablesManager().importVariables(VariablesPage.this.scenario);
        VariablesPage.this.tableViewer.refresh();
        // Force the "file has changed" property of scenario.
        // Variables changes will have no effects if the scenario
        // is not saved.
        firePropertyChange(IEditorPart.PROP_DIRTY);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
      }
    });

    final Button browseButton = toolkit.createButton(client, Messages.getString("Overview.browse"), SWT.PUSH);
    final SelectionAdapter browseAdapter = new FileSelectionAdapter(text, browseTitle, fileExtension);
    browseButton.addSelectionListener(browseAdapter);

    final Button exportButton = toolkit.createButton(client, Messages.getString("Variables.variablesExportExcel"),
        SWT.PUSH);
    exportButton.addSelectionListener(new ExcelVariablesWriter(this.scenario));

    toolkit.paintBordersFor(client);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.forms.editor.FormPage#setActive(boolean)
   */
  @Override
  public void setActive(final boolean active) {
    super.setActive(active);

    if (active) {
      // Setting the current graph when the variables tab is shown
      try {
        if (this.scenario.isIBSDFScenario()) {
          this.currentGraph = ScenarioParser.getSDFGraph(this.scenario.getAlgorithmURL());
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }
}
