/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
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
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.DataType;
import org.preesm.ui.scenario.editor.Messages;

// TODO: Auto-generated Javadoc
/**
 * This page contains parameters to influence the deployment simulator.
 *
 * @author mpelcat
 */
public class SimulationPage extends FormPage implements IPropertyListener {

  /**
   * The listener interface for receiving comboBox events. The class that is interested in processing a comboBox event
   * implements this interface, and the object created with that class is registered with a component using the
   * component's <code>addComboBoxListener</code> method. When the comboBox event occurs, that object's appropriate
   * method is invoked.
   *
   * @see ComboBoxEvent
   */
  private class ComboBoxListener implements SelectionListener {

    /** The type. */
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

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
      // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(final SelectionEvent e) {
      if (e.getSource() instanceof Combo) {
        final Combo combo = ((Combo) e.getSource());
        final String item = combo.getItem(combo.getSelectionIndex());

        if (this.type.equals("operator")) {
          SimulationPage.this.scenario.getSimulationManager().setMainOperatorName(item);
        } else if (this.type.equals("comNode")) {
          SimulationPage.this.scenario.getSimulationManager().setMainComNodeName(item);
        }
      }

      firePropertyChange(IEditorPart.PROP_DIRTY);
    }

  }

  /** The current scenario being edited. */
  private final PreesmScenario scenario;

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
  public SimulationPage(final PreesmScenario scenario, final FormEditor editor, final String id, final String title) {
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
    /*
     * FormToolkit toolkit = managedForm.getToolkit(); ScrolledForm form = managedForm.getForm(); form.setBackground(new
     * Color(null, 100,100,10)); form.setExpandHorizontal(true); form.setExpandVertical(true);
     * form.setText("Column Object");
     *
     * Composite composite = form.getBody();
     *
     * composite.computeSize(1000, 1000); FormLayout fl = new FormLayout(); fl. composite.setLayout(fl);
     * form.setBounds(0, 0, 1000, 1000); Rectangle r = form.getClientArea(); form.reflow(true);
     */

    final ScrolledForm form = managedForm.getForm();
    // FormToolkit toolkit = managedForm.getToolkit();
    form.setText(Messages.getString("Simulation.title"));
    final GridLayout layout = new GridLayout(2, true);
    layout.verticalSpacing = 10;
    form.getBody().setLayout(layout);

    // Main operator chooser section
    createComboBoxSection(managedForm, Messages.getString("Simulation.mainOperator.title"),
        Messages.getString("Simulation.mainOperator.description"),
        Messages.getString("Simulation.mainOperatorSelectionTooltip"), "operator");

    // Main medium chooser section
    createComboBoxSection(managedForm, Messages.getString("Simulation.mainMedium.title"),
        Messages.getString("Simulation.mainMedium.description"),
        Messages.getString("Simulation.mainMediumSelectionTooltip"), "comNode");

    // Text modification listener that updates the average data size
    final ModifyListener averageDataSizeListener = new ModifyListener() {
      @Override
      public void modifyText(final ModifyEvent e) {
        final Text text = (Text) e.getSource();
        int averageSize = 0;
        try {
          averageSize = Integer.valueOf(text.getText());
          SimulationPage.this.scenario.getSimulationManager().setAverageDataSize(averageSize);
          propertyChanged(this, IEditorPart.PROP_DIRTY);
        } catch (final NumberFormatException ex) {
          ex.printStackTrace();
        }
      }
    };

    // Average data size section
    createIntegerSection(managedForm, Messages.getString("Simulation.DataAverageSize.title"),
        Messages.getString("Simulation.DataAverageSize.description"), averageDataSizeListener,
        String.valueOf(this.scenario.getSimulationManager().getAverageDataSize()));

    // Text modification listener that updates the average data size
    final ModifyListener numberOfTopExecutionsListener = new ModifyListener() {
      @Override
      public void modifyText(final ModifyEvent e) {
        final Text text = (Text) e.getSource();
        int number = 1;
        try {
          number = Integer.valueOf(text.getText());
          SimulationPage.this.scenario.getSimulationManager().setNumberOfTopExecutions(number);
          propertyChanged(this, IEditorPart.PROP_DIRTY);
        } catch (final NumberFormatException ex) {
          ex.printStackTrace();
        }
      }
    };

    if (this.scenario.isPISDFScenario()) {
      // Number of top-level execution section, added only for PiSDF algorithms
      createIntegerSection(managedForm, Messages.getString("Overview.simulationTitle"),
          Messages.getString("Overview.simulationDescription"), numberOfTopExecutionsListener,
          String.valueOf(this.scenario.getSimulationManager().getNumberOfTopExecutions()));
    }

    // Data type section
    createDataTypesSection(managedForm, Messages.getString("Simulation.DataTypes.title"),
        Messages.getString("Simulation.DataTypes.description"));

    // Cores to execute broadcast/fork/join selection
    createSpecialVertexSection(managedForm, Messages.getString("Simulation.SpecialVertex.title"),
        Messages.getString("Simulation.SpecialVertex.description"));

    managedForm.refresh();
    managedForm.reflow(true);
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
    combocps.setVisible(true);
    final Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setToolTipText(tooltip);

    if (type.equals("operator")) {
      for (final String opId : this.scenario.getOrderedOperatorIds()) {
        combo.add(opId);
      }

      combo.select(combo.indexOf(this.scenario.getSimulationManager().getMainOperatorName()));
    } else if (type.equals("comNode")) {
      for (final String nodeId : this.scenario.getComNodeIds()) {
        combo.add(nodeId);
      }

      combo.select(combo.indexOf(this.scenario.getSimulationManager().getMainComNodeName()));
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
    buttonscps.setLayout(new GridLayout(2, true));
    final Button addButton = toolkit.createButton(buttonscps, Messages.getString("Simulation.DataTypes.addType"),
        SWT.PUSH);
    final Button removeButton = toolkit.createButton(buttonscps, Messages.getString("Simulation.DataTypes.removeType"),
        SWT.PUSH);

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    final TableViewer tableViewer = new TableViewer(tablecps,
        SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

    final Table table = tableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    // table.setSize(100, 100);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    tableViewer.setContentProvider(new DataTypesContentProvider());

    final DataTypesLabelProvider labelProvider = new DataTypesLabelProvider(this.scenario, tableViewer, this);
    tableViewer.setLabelProvider(labelProvider);

    // Create columns
    final TableColumn column1 = new TableColumn(table, SWT.NONE, 0);
    column1.setText(Messages.getString("Simulation.DataTypes.typeColumn"));

    final TableColumn column2 = new TableColumn(table, SWT.NONE, 1);
    column2.setText(Messages.getString("Simulation.DataTypes.sizeColumn"));

    tableViewer.addDoubleClickListener(e -> labelProvider.handleDoubleClick((IStructuredSelection) e.getSelection()));

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

    tableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = 400;
    gd.widthHint = 250;
    tablecps.setLayoutData(gd);

    // Adding the new data type on click on add button
    addButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final String dialogTitle = Messages.getString("Simulation.DataTypes.addType.dialog.title");
        final String dialogMessage = Messages.getString("Simulation.DataTypes.addType.dialog.message");
        final String init = "newType";

        final IInputValidator validator = newText -> null;

        final InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            dialogTitle, dialogMessage, init, validator);
        if (dialog.open() == Window.OK) {
          final DataType dataType = new DataType(dialog.getValue());
          SimulationPage.this.scenario.getSimulationManager().putDataType(dataType);
          tableViewer.refresh();
          propertyChanged(this, IEditorPart.PROP_DIRTY);
        }
      }

    });

    // Removing a data type on click on remove button
    removeButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if ((selection != null) && (selection.getFirstElement() instanceof DataType)) {
          final DataType dataType = (DataType) selection.getFirstElement();
          SimulationPage.this.scenario.getSimulationManager().removeDataType(dataType.getTypeName());
          tableViewer.refresh();
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
      }

      @Override
      public void dispose() {
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
        return null;
      }
    };

    treeviewer.setContentProvider(contentProvider);

    // The check state listener modifies the check status of elements
    checkStateListener.setTreeViewer(treeviewer, listener);
    treeviewer.setLabelProvider(new LabelProvider());
    treeviewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

    treeviewer.addCheckStateListener(checkStateListener);

    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = 400;
    gd.widthHint = 250;
    treeviewer.getTree().setLayoutData(gd);

    treeviewer.setUseHashlookup(true);
    treeviewer.setInput(this.scenario.getOrderedOperatorIds());
    toolkit.paintBordersFor(container);

    // Tree is refreshed in case of algorithm modifications
    container.addPaintListener(checkStateListener);

  }
}
