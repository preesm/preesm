/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2023) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2011 - 2012)
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
package org.preesm.ui.scenario.editor.simulation;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.impl.DataTypeImpl;
import org.preesm.model.scenario.util.DefaultTypeSizes;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.ui.scenario.editor.Messages;
import org.preesm.ui.scenario.editor.ScenarioPage;

/**
 * This page contains parameters to influence the deployment simulator.
 *
 * @author mpelcat
 */
public class SimulationPage extends ScenarioPage {

  private static final String DATA_TYPE_SIZE_TITLE = Messages.getString("Simulation.DataTypes.sizeColumn");
  private static final String DATA_TYPE_NAME_TITLE = Messages.getString("Simulation.DataTypes.typeColumn");

  private static final String[] DATA_TYPE_TABLE_TITLES = { DATA_TYPE_NAME_TITLE, DATA_TYPE_SIZE_TITLE };

  private static final String COM_NODE = "comNode";
  private static final String OPERATOR = "operator";

  /**
   * The listener interface for receiving comboBox events. The class that is interested in processing a comboBox event
   * implements this interface, and the object created with that class is registered with a component using the
   * component's <code>addComboBoxListener</code> method. When the comboBox event occurs, that object's appropriate
   * method is invoked.
   *
   * @see ComboBoxEvent
   */
  private class ComboBoxListener implements SelectionListener {

    // "operator" or "medium"
    String type = "";

    /**
     * Instantiates a new combo box listener.
     *
     * @param type
     *          the type
     */
    public ComboBoxListener(final String type) {
      super();
      this.type = type;
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
      // no behavior by default
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
      if (e.getSource() instanceof final Combo combo) {
        final String item = combo.getItem(combo.getSelectionIndex());

        final ComponentInstance compInstance = scenario.getDesign().getComponentInstance(item);
        if (this.type.equals(OPERATOR)) {
          SimulationPage.this.scenario.getSimulationInfo().setMainOperator(compInstance);
        } else if (this.type.equals(COM_NODE)) {
          SimulationPage.this.scenario.getSimulationInfo().setMainComNode(compInstance);
        }
      }

      firePropertyChange(IEditorPart.PROP_DIRTY);
    }

  }

  /** The current scenario being edited. */
  private final Scenario scenario;

  /**
   * Instantiates a new simulation page.
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
  public SimulationPage(final Scenario scenario, final FormEditor editor, final String id, final String title) {
    super(editor, id, title);

    this.scenario = scenario;
  }

  /**
   * Creation of the sections and their initialization.
   *
   * @param managedForm
   *          the managed form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    final ScrolledForm form = managedForm.getForm();
    form.setText(Messages.getString("Simulation.title"));
    final GridLayout layout = new GridLayout(2, true);
    layout.verticalSpacing = 10;
    final Composite body = form.getBody();
    body.setLayout(layout);

    if (this.scenario.isProperlySet()) {

      // Main operator chooser section
      createComboBoxSection(managedForm, Messages.getString("Simulation.mainOperator.title"),
          Messages.getString("Simulation.mainOperator.description"),
          Messages.getString("Simulation.mainOperatorSelectionTooltip"), OPERATOR);

      // Main medium chooser section
      createComboBoxSection(managedForm, Messages.getString("Simulation.mainMedium.title"),
          Messages.getString("Simulation.mainMedium.description"),
          Messages.getString("Simulation.mainMediumSelectionTooltip"), COM_NODE);

      // Text modification listener that updates the average data size
      final ModifyListener averageDataSizeListener = new ModifyListener() {
        @Override
        public void modifyText(final ModifyEvent e) {
          final Text text = (Text) e.getSource();
          int averageSize = 0;
          try {
            averageSize = Integer.valueOf(text.getText());
            SimulationPage.this.scenario.getSimulationInfo().setAverageDataSize(averageSize);
            propertyChanged(this, IEditorPart.PROP_DIRTY);
          } catch (final NumberFormatException ex) {
            PreesmLogger.getLogger().log(Level.WARNING, "Could not parse integer " + text.getText(), ex);
          }
        }
      };
      // Data type section
      createDataTypesSection(managedForm, Messages.getString("Simulation.DataTypes.title"),
          Messages.getString("Simulation.DataTypes.description"));

      // Cores to execute broadcast/fork/join selection
      createSpecialVertexSection(managedForm, Messages.getString("Simulation.SpecialVertex.title"),
          Messages.getString("Simulation.SpecialVertex.description"));

      // Average data size section
      createIntegerSection(managedForm, Messages.getString("Simulation.DataAverageSize.title"),
          Messages.getString("Simulation.DataAverageSize.description"), averageDataSizeListener,
          String.valueOf(this.scenario.getSimulationInfo().getAverageDataSize()));

      managedForm.refresh();
      managedForm.reflow(true);

    } else {
      final FormToolkit toolkit = managedForm.getToolkit();
      final Label lbl = toolkit.createLabel(body,
          "Please properly set Algorithm and Architecture paths on the overview tab, then save, close and "
              + "reopen this file to enable other tabs.");
      lbl.setEnabled(true);
      body.setEnabled(false);
    }
  }

  /**
   * Creates the section editing the average data size.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   * @param modifListener
   *          the modif listener
   * @param firstValue
   *          the first value
   */
  private void createIntegerSection(final IManagedForm managedForm, final String title, final String desc,
      final ModifyListener modifListener, final String firstValue) {
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    final Composite client = createSection(managedForm, title, desc, 2, gridData);

    final FormToolkit toolkit = managedForm.getToolkit();

    final Text text = toolkit.createText(client, firstValue, SWT.SINGLE);
    text.setData(title);
    text.addModifyListener(modifListener);

    text.setLayoutData(gridData);
    toolkit.paintBordersFor(client);
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
    section.setLayoutData(gridData);
    return client;
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
   * @param tooltip
   *          the tooltip
   * @param type
   *          the type
   */
  private void createComboBoxSection(final IManagedForm managedForm, final String title, final String desc,
      final String tooltip, final String type) {
    // Creates the section
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    final Composite container = createSection(managedForm, title, desc, 2, gridData);

    final FormToolkit toolkit = managedForm.getToolkit();

    final Combo coreCombo = addCoreSelector(container, toolkit, tooltip, type);
    coreCombo.addSelectionListener(new ComboBoxListener(type));
  }

  /**
   * Adds a combo box for the core selection.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   * @param tooltip
   *          the tooltip
   * @param type
   *          the type
   * @return the combo
   */
  protected Combo addCoreSelector(final Composite parent, final FormToolkit toolkit, final String tooltip,
      final String type) {
    final Composite combocps = toolkit.createComposite(parent);
    combocps.setLayout(new FillLayout());

    final GridData componentNameGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
    componentNameGridData.widthHint = 250;
    combocps.setLayoutData(componentNameGridData);

    combocps.setVisible(true);
    final Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setToolTipText(tooltip);

    final Design design = this.scenario.getDesign();
    if (type.equals(OPERATOR)) {
      for (final ComponentInstance opId : design.getOrderedOperatorComponentInstances()) {
        combo.add(opId.getInstanceName());
      }

      final ComponentInstance mainOperator = this.scenario.getSimulationInfo().getMainOperator();
      if (mainOperator != null) {
        combo.select(combo.indexOf(mainOperator.getInstanceName()));
      }
    } else if (type.equals(COM_NODE)) {
      for (final ComponentInstance nodeId : design.getCommunicationComponentInstances()) {
        combo.add(nodeId.getInstanceName());
      }

      final ComponentInstance mainComNode = this.scenario.getSimulationInfo().getMainComNode();
      if (mainComNode != null) {
        combo.select(combo.indexOf(mainComNode.getInstanceName()));
      }
    }

    return combo;
  }

  /**
   * Creates the section editing data types.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createDataTypesSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final Composite container = createSection(managedForm, title, desc, 1,
        new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    final FormToolkit toolkit = managedForm.getToolkit();

    addDataTypeTable(container, toolkit);
  }

  /**
   * Adds a table to edit data types.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  protected void addDataTypeTable(final Composite parent, final FormToolkit toolkit) {

    // Buttons to add and remove data types
    final Composite buttonscps = toolkit.createComposite(parent);
    buttonscps.setLayout(new GridLayout(3, true));
    final Button addButton = toolkit.createButton(buttonscps, Messages.getString("Simulation.DataTypes.addType"),
        SWT.PUSH);
    final Button removeButton = toolkit.createButton(buttonscps, Messages.getString("Simulation.DataTypes.removeType"),
        SWT.PUSH);
    final Button fetchButton = toolkit.createButton(buttonscps, Messages.getString("Simulation.DataTypes.fetchType"),
        SWT.PUSH);

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    final TableViewer tableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

    final Table table = tableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    tableViewer.setContentProvider(new DataTypesContentProvider());
    tableViewer.setLabelProvider(new DataTypesLabelProvider());

    final TableColumn[] columns = new TableColumn[DATA_TYPE_TABLE_TITLES.length];
    for (int i = 0; i < DATA_TYPE_TABLE_TITLES.length; i++) {
      final TableColumn columni = new TableColumn(table, SWT.NONE, i);
      columni.setText(DATA_TYPE_TABLE_TITLES[i]);
      columns[i] = columni;
    }

    final CellEditor[] editors = new CellEditor[table.getColumnCount()];
    for (int i = 0; i < table.getColumnCount(); i++) {
      editors[i] = new TextCellEditor(table);
    }
    tableViewer.setColumnProperties(DATA_TYPE_TABLE_TITLES);
    tableViewer.setCellEditors(editors);

    tableViewer.setCellModifier(new ICellModifier() {
      @Override
      public void modify(final Object element, final String property, final Object value) {
        if (element instanceof final TableItem ti) {
          final DataTypeImpl data = (DataTypeImpl) ti.getData();
          final long oldValue = data.getValue();
          try {
            final long newValue = Long.parseLong((String) value);
            if (oldValue != newValue) {
              data.setValue(newValue);
              firePropertyChange(PROP_DIRTY);
              tableViewer.refresh();
            }
          } catch (final NumberFormatException e) {
            ErrorDialog.openError(SimulationPage.this.getEditorSite().getShell(), "Wrong number format",
                "Data type sizes are Long typed.",
                new Status(IStatus.ERROR, "org.preesm.ui.scenario", "Could not parse long. " + e.getMessage()));
          }
          // toto
        }
      }

      @Override
      public Object getValue(final Object element, final String property) {
        if (element instanceof final DataTypeImpl delayTypeImpl) {
          return Long.toString(delayTypeImpl.getValue());
        }
        return "";
      }

      @Override
      public boolean canModify(final Object element, final String property) {
        return property.contentEquals(DATA_TYPE_SIZE_TITLE);
      }
    });
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
        columns[1].setWidth(width / 4);
        columns[0].setWidth(width - columns[1].getWidth());
        tref.setSize(area.width, area.height);
      }
    });

    tableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);

    gd.heightHint = Math.max(50, Math.min(300, this.scenario.getSimulationInfo().getDataTypes().size() * 20 + 30));
    gd.widthHint = 250;
    tablecps.setLayoutData(gd);

    // Adding the new data type on click on add button
    addButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final String dialogTitle = Messages.getString("Simulation.DataTypes.addType.dialog.title");
        final String dialogMessage = Messages.getString("Simulation.DataTypes.addType.dialog.message");
        final String init = "newType";

        // implements "public String isValid(String newText)"
        final IInputValidator validator = newText -> {
          final String trimmed = newText.trim();
          if (trimmed.isEmpty()) {
            return "The data type cannot be empty.";
          }
          return null;
        };

        final InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dialogTitle, dialogMessage, init, validator);
        if (dialog.open() == Window.OK) {
          SimulationPage.this.scenario.getSimulationInfo().getDataTypes().put(dialog.getValue().trim(),
              DefaultTypeSizes.getInstance().getTypeSizeOrDefault(dialog.getValue().trim()));
          tableViewer.refresh();
          propertyChanged(SimulationPage.this, IEditorPart.PROP_DIRTY);
          gd.heightHint = Math.max(50,
              Math.min(300, SimulationPage.this.scenario.getSimulationInfo().getDataTypes().size() * 20 + 30));
          tablecps.requestLayout();
        }
      }

    });

    // Removing a data type on click on remove button
    removeButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        final Object element = selection.getFirstElement();
        if (element instanceof Entry) {

          @SuppressWarnings("unchecked")
          final Entry<String, Long> dataType = (Entry<String, Long>) element;
          SimulationPage.this.scenario.getSimulationInfo().getDataTypes().removeKey(dataType.getKey());
          tableViewer.refresh();
          propertyChanged(this, IEditorPart.PROP_DIRTY);
          gd.heightHint = Math.max(50,
              Math.min(300, SimulationPage.this.scenario.getSimulationInfo().getDataTypes().size() * 20 + 30));
          tablecps.requestLayout();
        }
      }
    });

    // Adding the new data type on click on add button
    fetchButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        for (final Fifo f : scenario.getAlgorithm().getAllFifos()) {
          final String typeName = f.getType();
          SimulationPage.this.scenario.getSimulationInfo().getDataTypes().put(typeName,
              DefaultTypeSizes.getInstance().getTypeSizeOrDefault(typeName));
        }

        tableViewer.refresh();
        propertyChanged(SimulationPage.this, IEditorPart.PROP_DIRTY);
        gd.heightHint = Math.max(50,
            Math.min(300, SimulationPage.this.scenario.getSimulationInfo().getDataTypes().size() * 20 + 30));
        tablecps.requestLayout();
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
   * Creates the section editing the cores capability to execute special vertices.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createSpecialVertexSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final Composite container = createSection(managedForm, title, desc, 1,
        new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
    final FormToolkit toolkit = managedForm.getToolkit();

    createOperatorTreeSection(container, toolkit, this);
  }

  /**
   * Creates the tree view for operators.
   *
   * @param container
   *          the container
   * @param toolkit
   *          the toolkit
   * @param listener
   *          the listener
   */
  public void createOperatorTreeSection(final Composite container, final FormToolkit toolkit,
      final IPropertyListener listener) {

    container.setLayout(new GridLayout());

    final OperatorCheckStateListener checkStateListener = new OperatorCheckStateListener(
        (Section) container.getParent(), this.scenario);

    // Creating the tree view
    final CheckboxTreeViewer treeviewer = new CheckboxTreeViewer(toolkit.createTree(container, SWT.CHECK));

    // The content provider fills the tree
    final ITreeContentProvider contentProvider = new ITreeContentProvider() {

      @Override
      public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        // no behavior by default
      }

      @Override
      public void dispose() {
        // no behavior by default
      }

      @Override
      public boolean hasChildren(final Object element) {
        if (getChildren(element) != null) {
          return getChildren(element).length > 0;
        }
        return false;
      }

      @Override
      public Object getParent(final Object element) {
        return null;
      }

      @Override
      public Object[] getElements(final Object inputElement) {
        return getChildren(inputElement);
      }

      @Override
      @SuppressWarnings("unchecked")
      public Object[] getChildren(final Object parentElement) {
        if (parentElement instanceof List<?>) {
          return ((List<String>) parentElement).toArray();
        }
        return new Object[0];
      }
    };

    treeviewer.setContentProvider(contentProvider);

    // The check state listener modifies the check status of elements
    checkStateListener.setTreeViewer(treeviewer, listener);
    treeviewer.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(final Object element) {
        if (element instanceof final ComponentInstance ci) {
          return ci.getInstanceName() + " (" + ci.getComponent().getVlnv().getName() + ")";
        }
        return super.getText(element);
      }
    });
    treeviewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

    treeviewer.addCheckStateListener(checkStateListener);

    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = Math.max(50,
        Math.min(300, SimulationPage.this.scenario.getDesign().getOperatorComponentInstances().size() * 20 + 30));
    gd.widthHint = 250;
    treeviewer.getTree().setLayoutData(gd);

    treeviewer.setUseHashlookup(true);
    final Design design = this.scenario.getDesign();
    treeviewer.setInput(design.getOrderedOperatorComponentInstances());
    toolkit.paintBordersFor(container);

    // Tree is refreshed in case of algorithm modifications
    container.addPaintListener(checkStateListener);

  }
}
