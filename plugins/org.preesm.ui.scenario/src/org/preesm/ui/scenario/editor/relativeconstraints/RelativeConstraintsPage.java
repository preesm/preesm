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
package org.preesm.ui.scenario.editor.relativeconstraints;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.preesm.algorithm.core.scenario.PreesmScenario;
import org.preesm.algorithm.core.scenario.serialize.PreesmAlgorithmListContentProvider;
import org.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.preesm.ui.scenario.editor.Messages;

// TODO: Auto-generated Javadoc
/**
 * Relative constraints editor within the scenario editor.
 *
 * @author mpelcat
 * @author kdesnos
 */
public class RelativeConstraintsPage extends FormPage implements IPropertyListener {

  /** The scenario. */
  final PreesmScenario scenario;

  /** The table viewer. */
  TableViewer tableViewer = null;

  /**
   * Instantiates a new relative constraints page.
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
  public RelativeConstraintsPage(final PreesmScenario scenario, final FormEditor editor, final String id,
      final String title) {
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
    // TODO Auto-generated method stub
    super.createFormContent(managedForm);

    final ScrolledForm form = managedForm.getForm();
    // FormToolkit toolkit = managedForm.getToolkit();
    form.setText(Messages.getString("RelativeConstraints.title"));

    final GridLayout layout = new GridLayout();
    final Composite body = form.getBody();
    body.setLayout(layout);

    if (this.scenario.isProperlySet()) {
      // Timing file chooser section
      createFileSection(managedForm, Messages.getString("RelativeConstraints.file"),
          Messages.getString("RelativeConstraints.fileDescription"), Messages.getString("RelativeConstraints.fileEdit"),
          this.scenario.getTimingManager().getExcelFileURL(), Messages.getString("RelativeConstraints.fileBrowseTitle"),
          "xls");

      createRelativeConstraintsSection(managedForm, Messages.getString("RelativeConstraints.title"),
          Messages.getString("RelativeConstraints.description"));
    } else {
      final FormToolkit toolkit = managedForm.getToolkit();
      final Label lbl = toolkit.createLabel(body,
          "Please properly set Algorithm and Architecture paths on the overview tab, then save, close and "
              + "reopen this file to enable other tabs.");
      lbl.setEnabled(true);
      body.setEnabled(false);
    }
    managedForm.refresh();
    managedForm.reflow(true);

  }

  /**
   * Creates the section editing relarive constraints.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createRelativeConstraintsSection(final IManagedForm managedForm, final String title, final String desc) {

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
   * Adds a table to edit relative constraints.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  protected void addTable(final Composite parent, final FormToolkit toolkit) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    this.tableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
    final Table table = this.tableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    this.tableViewer.setContentProvider(new PreesmAlgorithmListContentProvider());

    final SDFTableLabelProvider labelProvider = new SDFTableLabelProvider(this.scenario, this.tableViewer, this);
    this.tableViewer.setLabelProvider(labelProvider);

    // Create columns
    final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
    column1.setText(Messages.getString("RelativeConstraints.taskColumn"));

    final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
    column2.setText(Messages.getString("RelativeConstraints.constraintColumn"));

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
    if ((source instanceof SDFTableLabelProvider) && (propId == IEditorPart.PROP_DIRTY)) {
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

    // If the text is modified or Enter key pressed, relative constraints are imported
    text.addModifyListener(e -> {
      final Text text1 = (Text) e.getSource();
      RelativeConstraintsPage.this.scenario.getTimingManager().setExcelFileURL(text1.getText());
      RelativeConstraintsPage.this.scenario.getTimingManager().importTimings(RelativeConstraintsPage.this.scenario);
      RelativeConstraintsPage.this.tableViewer.refresh();
      firePropertyChange(IEditorPart.PROP_DIRTY);

    });
    text.addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent e) {
        if (e.keyCode == SWT.CR) {
          final Text text = (Text) e.getSource();
          RelativeConstraintsPage.this.scenario.getTimingManager().setExcelFileURL(text.getText());
          RelativeConstraintsPage.this.scenario.getTimingManager().importTimings(RelativeConstraintsPage.this.scenario);
          RelativeConstraintsPage.this.tableViewer.refresh();
        }

      }

      @Override
      public void keyReleased(final KeyEvent e) {

      }
    });

    gd.widthHint = 400;
    text.setLayoutData(gd);

    // Add a "Refresh" button to the scenario editor
    final Button refreshButton = toolkit.createButton(client, Messages.getString("RelativeConstraints.fileRefresh"),
        SWT.PUSH);
    refreshButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        // Cause scenario editor to import timings from excel sheet
        RelativeConstraintsPage.this.scenario.getTimingManager().importTimings(RelativeConstraintsPage.this.scenario);
        RelativeConstraintsPage.this.tableViewer.refresh();
        // Force the "file has changed" property of scenario.
        // Timing changes will have no effects if the scenario
        // is not saved.
        firePropertyChange(IEditorPart.PROP_DIRTY);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
      }
    });

    toolkit.paintBordersFor(client);

    final Button browseButton = toolkit.createButton(client, Messages.getString("Overview.browse"), SWT.PUSH);
    final SelectionAdapter browseAdapter = new FileSelectionAdapter(text, browseTitle, fileExtension);
    browseButton.addSelectionListener(browseAdapter);

    final Button exportButton = toolkit.createButton(client, Messages.getString("RelativeConstraints.exportExcel"),
        SWT.PUSH);
    exportButton.addSelectionListener(new ExcelRelativeConstraintsWriter(this.scenario));

  }
}
