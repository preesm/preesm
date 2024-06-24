/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2015)
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
package org.preesm.ui.scenario.editor.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.osgi.framework.FrameworkUtil;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.impl.PEPowerImpl;
import org.preesm.model.scenario.impl.PeCommsEnergyImpl;
import org.preesm.model.scenario.serialize.EnergyImporter;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.ScenarioPage;
import org.preesm.ui.scenario.editor.utils.PreesmAlgorithmListContentProvider;
import org.preesm.ui.scenario.editor.utils.VertexLexicographicalComparator;

/**
 * Energy editor within the implementation editor.
 *
 * @author dmadronal
 */
public class EnergyPage extends ScenarioPage {

  private static final String COMMS_HEADER         = Messages.getString("Energy.commsHeader");
  private static final String OP_DEF_TITLE         = Messages.getString("Energy.opDefColumn");
  private static final String POWER_PLATFORM_TITLE = Messages.getString("Energy.powerPlatformColumn");

  private static final String ERROR_DIALOG_TITLE = "Wrong number format";
  private static final String ERROR_DIALOG_MSG   = "Power PE values are Double typed.";

  private static final String ERROR_STATUS_MSG = "Could not parse double. ";

  private static final String PLUGIN_ID = FrameworkUtil.getBundle(EnergyPage.class).getSymbolicName();

  private static final String[] POWER_PLATFORM_NAMES = { OP_DEF_TITLE, POWER_PLATFORM_TITLE };

  /** The scenario. */
  final Scenario scenario;

  /** The table viewer. */
  TableViewer tableViewer = null;

  /** The pisdf column names. */
  private static final String[] PISDF_COLUMN_NAMES = { "Actors", "Input Parameters", "Expression", "Evaluation",
      "Value" };

  private static final int[] PISDF_COLUMN_SIZES = { 200, 200, 200, 50, 50 };

  /** The objective column names. */
  private static final String[] OBJECTIVE_COLUMN_NAMES = { "Parameter", "Value" };

  /**
   * Instantiates a new energy page.
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
  public EnergyPage(final Scenario scenario, final FormEditor editor, final String id, final String title) {
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
    form.setText(Messages.getString("Energy.title"));

    final GridLayout layout = new GridLayout(2, true);
    final Composite body = form.getBody();
    body.setLayout(layout);

    if (this.scenario.isProperlySet()) {

      // Timing file chooser section
      createFileSection(managedForm, Messages.getString("Energy.fileTitle"),
          Messages.getString("Energy.fileDescription"), Messages.getString("Energy.fileEdit"),
          this.scenario.getEnergyConfig().getExcelFileURL(), Messages.getString("Energy.timingFileBrowseTitle"),
          Messages.getString("Energy.energyImportPapifyFolder"),
          new LinkedHashSet<>(Arrays.asList("xls", "csv", "papify")));
      createObjectiveSection(managedForm, Messages.getString("Energy.objectiveTitle"),
          Messages.getString("Energy.objectiveDescription"));

      // Data type section
      createPlatformPowerSection(managedForm, Messages.getString("Energy.platformTitle"),
          Messages.getString("Energy.platformDescription"));
      // Data type section
      createCommunicationEnergySection(managedForm, Messages.getString("Energy.commsTitle"),
          Messages.getString("Energy.commsDescription"));

      createActorEnergySection(managedForm, Messages.getString("Energy.actorsTitle"),
          Messages.getString("Energy.actorsDescription"));

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
   * Creates the section editing platform power.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createPlatformPowerSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    final Composite container = createSection(managedForm, title, desc, 1, gridData);
    final FormToolkit toolkit = managedForm.getToolkit();

    addPlatformPowerTable(container, toolkit);
  }

  /**
   * Creates the section editing platform power.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createCommunicationEnergySection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    final Composite container = createSection(managedForm, title, desc, 1, gridData);
    final FormToolkit toolkit = managedForm.getToolkit();

    addCommsEnergyTable(container, toolkit);
  }

  /**
   * Adds a table to edit platform communication energy consumptions.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addCommsEnergyTable(final Composite parent, final FormToolkit toolkit) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    final TableViewer newTableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

    final Table table = newTableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    // Create columns
    final List<String> operatorTypes = new ArrayList<>();
    for (final Component opDefId : this.scenario.getDesign().getProcessingElements()) {
      if (!operatorTypes.contains(opDefId.getVlnv().getName())) {
        operatorTypes.add(opDefId.getVlnv().getName());
      }
    }
    Collections.sort(operatorTypes, Comparable::compareTo);
    newTableViewer.setContentProvider(new CommsEnergyContentProvider());
    newTableViewer.setLabelProvider(new CommsEnergyLabelProvider(operatorTypes, this.scenario));

    final String[] columnNames = new String[operatorTypes.size() + 1];
    final TableColumn[] columns = new TableColumn[operatorTypes.size() + 1];
    final TableColumn columnBase = new TableColumn(table, SWT.NONE, 0);
    columnBase.setText(COMMS_HEADER);
    columns[0] = columnBase;
    columnNames[0] = COMMS_HEADER;
    for (int i = 1; i < operatorTypes.size() + 1; i++) {
      final TableColumn columni = new TableColumn(table, SWT.NONE, i);
      columnNames[i] = operatorTypes.get(i - 1);
      columni.setText(operatorTypes.get(i - 1));
      columns[i] = columni;
    }

    newTableViewer.setCellModifier(new ICellModifier() {
      @Override
      public void modify(final Object element, final String property, final Object value) {
        if (element instanceof final TableItem ti) {
          final PeCommsEnergyImpl commsEnergyPe = (PeCommsEnergyImpl) ti.getData();
          final String newValue = (String) value;
          boolean dirty = false;
          double parseDouble = 0.0;
          if (!property.equals(COMMS_HEADER)) {
            final double oldpowerPE = EnergyPage.this.scenario.getEnergyConfig()
                .getCommValueOrDefault(commsEnergyPe.getKey(), property);
            try {
              parseDouble = Double.parseDouble(newValue);
              if (oldpowerPE != parseDouble) {
                dirty = true;
              }
            } catch (final NumberFormatException e) {
              ErrorDialog.openError(EnergyPage.this.getEditorSite().getShell(), ERROR_DIALOG_TITLE, ERROR_DIALOG_MSG,
                  new Status(IStatus.ERROR, PLUGIN_ID, ERROR_STATUS_MSG + e.getMessage()));
            }
          }

          if (dirty) {
            EnergyPage.this.scenario.getEnergyConfig().setCommEnergy(commsEnergyPe.getKey(), property, parseDouble);
            firePropertyChange(IEditorPart.PROP_DIRTY);
            newTableViewer.refresh();
          }
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof final PeCommsEnergyImpl commsEnergyPe && !property.equals(COMMS_HEADER)) {
          return Double.toString(
              EnergyPage.this.scenario.getEnergyConfig().getCommValueOrDefault(commsEnergyPe.getKey(), property));
        }

        return "";
      }

      @Override
      public boolean canModify(final Object element, final String property) {
        return !property.contentEquals(COMMS_HEADER);
      }
    });

    final CellEditor[] editors = new CellEditor[table.getColumnCount()];
    for (int i = 0; i < table.getColumnCount(); i++) {
      editors[i] = new TextCellEditor(table);
    }
    newTableViewer.setColumnProperties(columnNames);
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
        for (int i = 0; i < table.getColumnCount(); i++) {
          columns[i].setWidth((width / table.getColumnCount()) - 1);
        }
      }
    });

    newTableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    final Integer entryNumber = scenario.getEnergyConfig().getPlatformPower().entrySet().size();
    gd.heightHint = Math.max(50, entryNumber * 25 + 30);
    gd.widthHint = 400;
    gd.grabExcessVerticalSpace = true;
    tablecps.setLayoutData(gd);
  }

  /**
   * Adds a table to edit static platform power consumptions.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addPlatformPowerTable(final Composite parent, final FormToolkit toolkit) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    final TableViewer newTableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

    final Table table = newTableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    newTableViewer.setContentProvider(new PowerPlatformContentProvider());
    newTableViewer.setLabelProvider(new PowerPlatformLabelProvider());

    // Create columns
    final TableColumn[] columns = new TableColumn[POWER_PLATFORM_NAMES.length];
    for (int i = 0; i < POWER_PLATFORM_NAMES.length; i++) {
      final TableColumn columni = new TableColumn(table, SWT.NONE, i);
      columni.setText(POWER_PLATFORM_NAMES[i]);
      columns[i] = columni;
    }

    newTableViewer.setCellModifier(new ICellModifier() {
      @Override
      public void modify(final Object element, final String property, final Object value) {
        if (element instanceof final TableItem ti) {
          final PEPowerImpl powerPe = (PEPowerImpl) ti.getData();
          final String newValue = (String) value;
          boolean dirty = false;
          if (POWER_PLATFORM_TITLE.equals(property)) {
            final double oldpowerPE = powerPe.getValue();
            try {
              final double parseDouble = Double.parseDouble(newValue);
              if (oldpowerPE != parseDouble) {
                dirty = true;
                powerPe.setValue(parseDouble);
              }
            } catch (final NumberFormatException e) {
              ErrorDialog.openError(EnergyPage.this.getEditorSite().getShell(), ERROR_DIALOG_TITLE, ERROR_DIALOG_MSG,
                  new Status(IStatus.ERROR, PLUGIN_ID, ERROR_STATUS_MSG + e.getMessage()));
            }
          }

          if (dirty) {
            EnergyPage.this.scenario.getEnergyConfig().getPlatformPower().add(powerPe);
            firePropertyChange(IEditorPart.PROP_DIRTY);
            newTableViewer.refresh();
          }
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof final PEPowerImpl powerPe && POWER_PLATFORM_TITLE.equals(property)) {
          return Double.toString(powerPe.getValue());
        }

        return "";
      }

      @Override
      public boolean canModify(final Object element, final String property) {
        return property.contentEquals(POWER_PLATFORM_NAMES[1]);
      }
    });

    final CellEditor[] editors = new CellEditor[table.getColumnCount()];
    for (int i = 0; i < table.getColumnCount(); i++) {
      editors[i] = new TextCellEditor(table);
    }
    newTableViewer.setColumnProperties(POWER_PLATFORM_NAMES);
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
      }
    });

    newTableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    final Integer entryNumber = scenario.getEnergyConfig().getPlatformPower().entrySet().size();
    gd.heightHint = Math.max(50, entryNumber * 25 + 30);
    gd.widthHint = 400;
    gd.grabExcessVerticalSpace = true;
    tablecps.setLayoutData(gd);
  }

  /**
   * Creates the section editing energy.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createActorEnergySection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    gridData.horizontalSpan = 2;
    gridData.grabExcessVerticalSpace = true;
    final Composite container = createSection(managedForm, title, desc, 1, gridData);
    final FormToolkit toolkit = managedForm.getToolkit();

    final Combo coreCombo = addCoreSelector(container, toolkit);
    addActorEnergyTable(container, toolkit, coreCombo);
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
    for (final Component defId : design.getProcessingElements()) {
      combo.add(defId.getVlnv().getName());
    }
  }

  /**
   * Adds a table to edit energy.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   * @param coreCombo
   *          the core combo
   */
  private void addActorEnergyTable(final Composite parent, final FormToolkit toolkit, final Combo coreCombo) {

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

    final EnergyTableLabelProvider labelProvider = new EnergyTableLabelProvider(this.scenario, this.tableViewer, this);
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
        if (element instanceof final TableItem ti) {
          final AbstractActor actor = (AbstractActor) ti.getData();

          final String componentType = coreCombo.getText();
          final Component component = EnergyPage.this.scenario.getDesign().getComponent(componentType);

          final String oldValue = EnergyPage.this.scenario.getEnergyConfig().getEnergyActorOrDefault(actor, component);
          final String newValue = (String) value;

          if (!oldValue.equals(newValue)) {
            EnergyPage.this.scenario.getEnergyConfig().setActorPeEnergy(actor, component, newValue);
            firePropertyChange(IEditorPart.PROP_DIRTY);
            tableViewer.refresh(actor, false, false);
          }
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof final AbstractActor actor) {
          final String componentType = coreCombo.getText();
          final Component component = EnergyPage.this.scenario.getDesign().getComponent(componentType);
          return EnergyPage.this.scenario.getEnergyConfig().getEnergyActorOrDefault(actor, component);
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
        .getSortedPISDFVertices(EnergyPage.this.scenario);
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
    if ((source instanceof EnergyTableLabelProvider) && (propId == IEditorPart.PROP_DIRTY)) {
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
      final String initValue, final String browseTitle, final String importPapifyOutput,
      final Set<String> fileExtension) {

    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    final Composite client = createSection(mform, title, desc, 3, gridData);

    final FormToolkit toolkit = mform.getToolkit();

    final GridData gd = new GridData();
    toolkit.createLabel(client, fileEdit);

    final Text text = toolkit.createText(client, initValue, SWT.SINGLE);
    text.setData(title);

    // If the text is modified or Enter key pressed, the energy of the actors is imported
    text.addModifyListener(e -> {
      final Text text1 = (Text) e.getSource();
      EnergyPage.this.scenario.getEnergyConfig().setExcelFileURL(text1.getText());
      EnergyImporter.importEnergy(EnergyPage.this.scenario);
      EnergyPage.this.tableViewer.refresh();
      firePropertyChange(IEditorPart.PROP_DIRTY);

    });
    text.addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent e) {
        if (e.keyCode == SWT.CR) {
          final Text text = (Text) e.getSource();
          EnergyPage.this.scenario.getEnergyConfig().setExcelFileURL(text.getText());
          EnergyPage.this.tableViewer.refresh();
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
    final Button refreshButton = toolkit.createButton(client, Messages.getString("Energy.fileRefresh"), SWT.PUSH);
    refreshButton.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        // Cause scenario editor to import energy from excel sheet
        EnergyImporter.importEnergy(EnergyPage.this.scenario);
        EnergyPage.this.tableViewer.refresh();
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

    final Button exportButton = toolkit.createButton(client, Messages.getString("Energy.energyExportExcel"), SWT.PUSH);
    exportButton.addSelectionListener(new ExcelEnergyWriter(this.scenario));

    final Button importPapifyButton = toolkit.createButton(client, importPapifyOutput, SWT.PUSH);
    final SelectionAdapter importPapifyAdapter = new FileSelectionAdapter(text, client.getShell(),
        Messages.getString("Energy.energyImportPapifyFolderTitle"));
    importPapifyButton.addSelectionListener(importPapifyAdapter);

  }

  /**
   * Creates a section to set the objective.
   *
   * @param mform
   *          form containing the section
   * @param title
   *          section title
   * @param desc
   *          description of the section
   */
  private void createObjectiveSection(final IManagedForm mform, final String title, final String desc) {

    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    final Composite client = createSection(mform, title, desc, 2, gridData);

    final FormToolkit toolkit = mform.getToolkit();

    addObjectiveTable(client, toolkit);

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
   * Adds a table to edit the performance objective.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addObjectiveTable(final Composite parent, final FormToolkit toolkit) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    final TableViewer newTableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

    final Table table = newTableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    newTableViewer.setContentProvider(new ObjectiveContentProvider());
    newTableViewer.setLabelProvider(new ObjectiveLabelProvider());

    // Create columns
    final TableColumn[] columns = new TableColumn[OBJECTIVE_COLUMN_NAMES.length];
    for (int i = 0; i < OBJECTIVE_COLUMN_NAMES.length; i++) {
      final TableColumn columni = new TableColumn(table, SWT.NONE, i);
      columni.setText(OBJECTIVE_COLUMN_NAMES[i]);
      columns[i] = columni;
    }

    newTableViewer.setCellModifier(new ICellModifier() {
      @Override
      public void modify(final Object element, final String property, final Object value) {
        if (element instanceof final TableItem ti) {
          @SuppressWarnings("unchecked")
          final Entry<String, Double> objective = (Entry<String, Double>) ti.getData();
          final String newValue = (String) value;
          boolean dirty = false;
          double parseDouble = 0.0;
          if (OBJECTIVE_COLUMN_NAMES[1].equals(property)) {
            final double oldValue = objective.getValue();
            try {
              parseDouble = Double.parseDouble(newValue);
              if (oldValue != parseDouble) {
                dirty = true;
              }
            } catch (final NumberFormatException e) {
              ErrorDialog.openError(EnergyPage.this.getEditorSite().getShell(), ERROR_DIALOG_TITLE, ERROR_DIALOG_MSG,
                  new Status(IStatus.ERROR, PLUGIN_ID, ERROR_STATUS_MSG + e.getMessage()));
            }
          }

          if (dirty) {
            if (objective.getKey().equals(Messages.getString("Energy.objectiveLabel"))) {
              EnergyPage.this.scenario.getEnergyConfig().getPerformanceObjective().setObjectiveEPS(parseDouble);
            }
            firePropertyChange(IEditorPart.PROP_DIRTY);
            newTableViewer.refresh();
          }
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof Entry<?, ?>) {
          @SuppressWarnings("unchecked")
          final Entry<String, Double> objective = (Entry<String, Double>) element;
          if (OBJECTIVE_COLUMN_NAMES[1].equals(property)) {
            return Double.toString(objective.getValue());
          }
        }
        return "";
      }

      @Override
      public boolean canModify(final Object element, final String property) {
        return property.contentEquals(OBJECTIVE_COLUMN_NAMES[1]);
      }
    });

    final CellEditor[] editors = new CellEditor[table.getColumnCount()];
    for (int i = 0; i < table.getColumnCount(); i++) {
      editors[i] = new TextCellEditor(table);
    }
    newTableViewer.setColumnProperties(OBJECTIVE_COLUMN_NAMES);
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
        columns[0].setWidth((width / 2));
        columns[1].setWidth((width / 2));
      }
    });

    newTableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
    final Integer entryNumber = scenario.getEnergyConfig().getPlatformPower().entrySet().size();
    gd.heightHint = Math.max(75, entryNumber * 25 + 10);
    gd.widthHint = 400;
    gd.grabExcessVerticalSpace = true;
    tablecps.setLayoutData(gd);
  }
}
