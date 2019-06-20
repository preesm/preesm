/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Alexandre Honorat <alexandre.honorat@insa-rennes.fr> (2019)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
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
package org.preesm.ui.scenario.editor.timings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.impl.MemoryInfoImpl;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.component.Component;
import org.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.ScenarioPage;
import org.preesm.ui.scenario.editor.utils.PreesmAlgorithmListContentProvider;
import org.preesm.ui.scenario.editor.utils.VertexLexicographicalComparator;

/**
 * Timing editor within the implementation editor.
 *
 * @author mpelcat
 * @author kdesnos
 */
public class TimingsPage extends ScenarioPage {

  private static final String OP_DEF_TITLE        = Messages.getString("Timings.MemcopySpeeds.opDefColumn");
  private static final String SETUP_TIME_TITLE    = Messages.getString("Timings.MemcopySpeeds.setupTimeColumn");
  private static final String TIME_PER_UNIT_TITLE = Messages.getString("Timings.MemcopySpeeds.timePerUnitColumn");

  private static final String[] MEM_COLUMN_NAMES = { OP_DEF_TITLE, SETUP_TIME_TITLE, TIME_PER_UNIT_TITLE };

  /** The scenario. */
  final Scenario scenario;

  /** The table viewer. */
  TableViewer tableViewer = null;

  /** The pisdf column names. */
  private static final String[] PISDF_COLUMN_NAMES = { "Actors", "Input Parameters", "Expression", "Evaluation",
      "Value" };

  private static final int[] PISDF_COLUMN_SIZES = { 200, 200, 200, 50, 50 };

  /**
   * Instantiates a new timings page.
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
  public TimingsPage(final Scenario scenario, final FormEditor editor, final String id, final String title) {
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
    form.setText(Messages.getString("Timings.title"));

    final GridLayout layout = new GridLayout();
    final Composite body = form.getBody();
    body.setLayout(layout);

    if (this.scenario.isProperlySet()) {

      // Timing file chooser section
      createFileSection(managedForm, Messages.getString("Timings.timingFile"),
          Messages.getString("Timings.timingFileDescription"), Messages.getString("Timings.timingFileEdit"),
          this.scenario.getTimings().getExcelFileURL(), Messages.getString("Timings.timingFileBrowseTitle"),
          new LinkedHashSet<>(Arrays.asList("xls", "csv")));

      createTimingsSection(managedForm, Messages.getString("Timings.title"), Messages.getString("Timings.description"));

      // Data type section
      createMemcopySpeedsSection(managedForm, Messages.getString("Timings.MemcopySpeeds.title"),
          Messages.getString("Timings.MemcopySpeeds.description"));

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
   * Creates the section editing memcopy speeds.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createMemcopySpeedsSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    final Composite container = createSection(managedForm, title, desc, 1, gridData);
    final FormToolkit toolkit = managedForm.getToolkit();

    addMemcopySpeedsTable(container, toolkit);
  }

  /**
   * Adds a table to edit memcopy speeds.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addMemcopySpeedsTable(final Composite parent, final FormToolkit toolkit) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    final TableViewer newTableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

    final Table table = newTableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    newTableViewer.setContentProvider(new MemCopySpeedContentProvider());
    newTableViewer.setLabelProvider(new MemCopySpeedLabelProvider());

    // Create columns
    final TableColumn[] columns = new TableColumn[MEM_COLUMN_NAMES.length];
    for (int i = 0; i < MEM_COLUMN_NAMES.length; i++) {
      final TableColumn columni = new TableColumn(table, SWT.NONE, i);
      columni.setText(MEM_COLUMN_NAMES[i]);
      columns[i] = columni;
    }

    newTableViewer.setCellModifier(new ICellModifier() {
      @Override
      public void modify(final Object element, final String property, final Object value) {
        if (element instanceof TableItem) {
          final TableItem ti = (TableItem) element;
          final MemoryInfoImpl memInfo = (MemoryInfoImpl) ti.getData();
          final String newValue = (String) value;
          boolean dirty = false;
          if (SETUP_TIME_TITLE.equals(property)) {
            final long oldSetupTime = memInfo.getValue().getSetupTime();
            try {
              final long parseLong = Long.parseLong(newValue);
              if (oldSetupTime != parseLong) {
                dirty = true;
                memInfo.getValue().setSetupTime(parseLong);
              }
            } catch (final NumberFormatException e) {
              ErrorDialog.openError(TimingsPage.this.getEditorSite().getShell(), "Wrong number format",
                  "Setup time values are Long typed.",
                  new Status(IStatus.ERROR, "org.preesm.ui.scenario", "Could not parse long. " + e.getMessage()));
            }
          } else if (TIME_PER_UNIT_TITLE.equals(property)) {
            final double oldTimePerUnit = memInfo.getValue().getTimePerUnit();
            try {
              final double newUnitPerTime = Double.parseDouble(newValue);
              final double newTimePerUnit = 1. / newUnitPerTime;
              if (oldTimePerUnit != newTimePerUnit) {
                dirty = true;
                memInfo.getValue().setTimePerUnit(newTimePerUnit);
              }
            } catch (final NumberFormatException e) {
              ErrorDialog.openError(TimingsPage.this.getEditorSite().getShell(), "Wrong number format",
                  "Unit per time values are Double typed.",
                  new Status(IStatus.ERROR, "org.preesm.ui.scenario", "Could not parse double. " + e.getMessage()));
            }
          }

          if (dirty) {
            firePropertyChange(IEditorPart.PROP_DIRTY);
            newTableViewer.refresh();
          }
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof MemoryInfoImpl) {
          final MemoryInfoImpl memInfo = (MemoryInfoImpl) element;
          if (SETUP_TIME_TITLE.equals(property)) {
            return Long.toString(memInfo.getValue().getSetupTime());
          } else if (TIME_PER_UNIT_TITLE.equals(property)) {
            return Double.toString(1. / memInfo.getValue().getTimePerUnit());
          }
        }
        return "";
      }

      @Override
      public boolean canModify(final Object element, final String property) {
        return property.contentEquals(MEM_COLUMN_NAMES[1]) || property.contentEquals(MEM_COLUMN_NAMES[2]);
      }
    });

    final CellEditor[] editors = new CellEditor[table.getColumnCount()];
    for (int i = 0; i < table.getColumnCount(); i++) {
      editors[i] = new TextCellEditor(table);
    }
    newTableViewer.setColumnProperties(MEM_COLUMN_NAMES);
    newTableViewer.setCellEditors(editors);

    // Setting the column width
    tablecps.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(final ControlEvent e) {
        final Rectangle area = tablecps.getClientArea();
        final Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        final ScrollBar vBar = table.getVerticalBar();
        int width = area.width - table.computeTrim(0, 0, 0, 0).width - 2;
        if (size.y > (area.height + table.getHeaderHeight())) {
          final Point vBarSize = vBar.getSize();
          width -= vBarSize.x;
        }
        table.setSize(area.width, area.height);
        columns[0].setWidth((width / 4) - 1);
        columns[1].setWidth((width - columns[0].getWidth()) / 2);
        columns[2].setWidth((width - columns[0].getWidth()) / 2);
      }
    });

    newTableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    final EList<Component> components = scenario.getDesign().getComponents();
    gd.heightHint = Math.max(50, Math.min(200, components.size() * 20 + 30));
    gd.widthHint = 400;
    gd.grabExcessVerticalSpace = true;
    tablecps.setLayoutData(gd);
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
  private void createTimingsSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final Composite container = createSection(managedForm, title, desc, 1,
        new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    final FormToolkit toolkit = managedForm.getToolkit();

    final Combo coreCombo = addCoreSelector(container, toolkit);
    addTimingsTable(container, toolkit, coreCombo);
  }

  /**
   * Adds a combo box for the core selection.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   * @return the combo
   */
  private Combo addCoreSelector(final Composite parent, final FormToolkit toolkit) {
    final Composite combocps = toolkit.createComposite(parent);
    combocps.setLayout(new FillLayout());

    final GridData componentNameGridData = new GridData();
    componentNameGridData.widthHint = 250;
    combocps.setLayoutData(componentNameGridData);

    combocps.setVisible(true);
    final Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setToolTipText(Messages.getString("Constraints.coreSelectionTooltip"));
    comboDataInit(combo);
    combo.select(0);
    return combo;
  }

  /**
   * Combo data init.
   *
   * @param combo
   *          the combo
   */
  private void comboDataInit(final Combo combo) {
    combo.removeAll();
    final Design design = this.scenario.getDesign();
    for (final Component defId : design.getOperatorComponents()) {
      combo.add(defId.getVlnv().getName());
    }
  }

  /**
   * Adds a table to edit timings.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   * @param coreCombo
   *          the core combo
   */
  private void addTimingsTable(final Composite parent, final FormToolkit toolkit, final Combo coreCombo) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    this.tableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
    tableViewer.setComparator(new VertexLexicographicalComparator());
    final Table table = this.tableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    this.tableViewer.setContentProvider(new PreesmAlgorithmListContentProvider());

    final TimingsTableLabelProvider labelProvider = new TimingsTableLabelProvider(this.scenario, this.tableViewer,
        this);
    this.tableViewer.setLabelProvider(labelProvider);
    coreCombo.addSelectionListener(labelProvider);

    // Create columns
    final String[] columnNames = PISDF_COLUMN_NAMES;

    final List<TableColumn> columns = new ArrayList<>();
    for (int i = 0; i < columnNames.length; i++) {
      final TableColumn column = new TableColumn(table, SWT.NONE, i);
      column.setText(columnNames[i]);
      column.setWidth(PISDF_COLUMN_SIZES[i]);
      columns.add(column);
    }

    final CellEditor[] editors = new CellEditor[table.getColumnCount()];
    for (int i = 0; i < table.getColumnCount(); i++) {
      editors[i] = new TextCellEditor(table);
    }
    this.tableViewer.setColumnProperties(PISDF_COLUMN_NAMES);
    this.tableViewer.setCellEditors(editors);

    this.tableViewer.setCellModifier(new ICellModifier() {
      @Override
      public void modify(final Object element, final String property, final Object value) {
        if (element instanceof TableItem) {
          final TableItem ti = (TableItem) element;
          final AbstractActor actor = (AbstractActor) ti.getData();
          final String componentType = coreCombo.getText();
          final Component component = TimingsPage.this.scenario.getDesign().getComponent(componentType);

          final String oldValue = TimingsPage.this.scenario.getTimings().getTimingOrDefault(actor, component);
          final String newValue = (String) value;

          if (!oldValue.equals(newValue)) {
            TimingsPage.this.scenario.getTimings().setTiming(actor, component, newValue);
            firePropertyChange(IEditorPart.PROP_DIRTY);
            tableViewer.refresh(actor, false, false);
          }
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof AbstractActor) {
          final AbstractActor actor = (AbstractActor) element;
          final String componentType = coreCombo.getText();
          final Component component = TimingsPage.this.scenario.getDesign().getComponent(componentType);
          return TimingsPage.this.scenario.getTimings().getTimingOrDefault(actor, component);
        }
        return "";
      }

      @Override
      public boolean canModify(final Object element, final String property) {
        return property.contentEquals(PISDF_COLUMN_NAMES[2]);
      }
    });

    final Table tref = table;
    final Composite comp = tablecps;
    final List<TableColumn> fColumns = columns;

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
        for (final TableColumn col : fColumns) {

          col.setWidth((width / PISDF_COLUMN_NAMES.length) - 1);
        }
        tref.setSize(area.width, area.height);
      }
    });

    this.tableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    final List<AbstractActor> sortedPISDFVertices = PreesmAlgorithmListContentProvider
        .getSortedPISDFVertices(TimingsPage.this.scenario);
    gd.heightHint = Math.min(400, sortedPISDFVertices.size() * 20 + 50);
    gd.widthHint = 400;
    gd.grabExcessVerticalSpace = false;
    tablecps.setLayoutData(gd);

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
    if ((source instanceof TimingsTableLabelProvider) && (propId == IEditorPart.PROP_DIRTY)) {
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
    if (this.tableViewer != null) {
      this.tableViewer.refresh();
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
      final String initValue, final String browseTitle, final Set<String> fileExtension) {

    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    final Composite client = createSection(mform, title, desc, 3, gridData);

    final FormToolkit toolkit = mform.getToolkit();

    final GridData gd = new GridData();
    toolkit.createLabel(client, fileEdit);

    final Text text = toolkit.createText(client, initValue, SWT.SINGLE);
    text.setData(title);

    // If the text is modified or Enter key pressed, timings are imported
    text.addModifyListener(e -> {
      final Text text1 = (Text) e.getSource();
      TimingsPage.this.scenario.getTimings().setExcelFileURL(text1.getText());
      TimingImporter.importTimings(TimingsPage.this.scenario);
      TimingsPage.this.tableViewer.refresh();
      firePropertyChange(IEditorPart.PROP_DIRTY);

    });
    text.addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent e) {
        if (e.keyCode == SWT.CR) {
          final Text text = (Text) e.getSource();
          TimingsPage.this.scenario.getTimings().setExcelFileURL(text.getText());
          TimingsPage.this.tableViewer.refresh();
        }

      }

      @Override
      public void keyReleased(final KeyEvent e) {
        // no behavior by default
      }
    });

    gd.widthHint = 400;
    text.setLayoutData(gd);

    // Add a "Refresh" button to the scenario editor
    final Button refreshButton = toolkit.createButton(client, Messages.getString("Timings.timingFileRefresh"),
        SWT.PUSH);
    refreshButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        // Cause scenario editor to import timings from excel sheet
        TimingImporter.importTimings(TimingsPage.this.scenario);
        TimingsPage.this.tableViewer.refresh();
        // Force the "file has changed" property of scenario.
        // Timing changes will have no effects if the scenario
        // is not saved.
        firePropertyChange(IEditorPart.PROP_DIRTY);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // no behavior by default
      }
    });

    toolkit.paintBordersFor(client);

    final Button browseButton = toolkit.createButton(client, Messages.getString("Overview.browse"), SWT.PUSH);
    final SelectionAdapter browseAdapter = new FileSelectionAdapter(text, browseTitle, fileExtension);
    browseButton.addSelectionListener(browseAdapter);

    final Button exportButton = toolkit.createButton(client, Messages.getString("Timings.timingExportExcel"), SWT.PUSH);
    exportButton.addSelectionListener(new ExcelTimingWriter(this.scenario));

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
}
