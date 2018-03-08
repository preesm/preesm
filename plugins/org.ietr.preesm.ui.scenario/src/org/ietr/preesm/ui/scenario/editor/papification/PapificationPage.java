/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.ui.scenario.editor.papification;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.PapifyComponentListContentProvider;
import org.ietr.preesm.core.scenario.serialize.PapifyEventListContentProvider;
import org.ietr.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.ietr.preesm.ui.scenario.editor.Messages;

/**
 * Papification editor within the implementation editor.
 *
 * @author dmadronal
 */
public class PapificationPage extends FormPage implements IPropertyListener {

  /** Currently edited scenario. */
  private PreesmScenario scenario = null;

  /** The check state listener. */
  private PapificationCheckStateListener checkStateListener = null;

  /** The table viewer. */

  // DM added this
  CheckboxTableViewer componentTableViewer = null;
  CheckboxTableViewer eventTableViewer     = null;
  // CheckboxTableViewer checkTableViewer = null;

  /** Architecture. */
  private Design slamDesign = null;

  /**
   * Instantiates a new papification page.
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
  public PapificationPage(final PreesmScenario scenario, final FormEditor editor, final String id, final String title) {
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
    f.setText(Messages.getString("Papification.title"));
    f.getBody().setLayout(new GridLayout());

    // Constrints file chooser section
    createFileSection(managedForm, Messages.getString("Papification.file"), Messages.getString("Papification.fileDescription"),
        Messages.getString("Papification.fileEdit"), this.scenario.getConstraintGroupManager().getExcelFileURL(),
        Messages.getString("Papification.fileBrowseTitle"), "xls");

    createPapificationSection(managedForm, Messages.getString("Papification.title"), Messages.getString("Papification.description"));

    managedForm.refresh();
    managedForm.reflow(true);

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
   * @return the section
   */
  public Section createSection(final IManagedForm mform, final String title, final String desc, final int numColumns) {

    final ScrolledForm form = mform.getForm();
    final FormToolkit toolkit = mform.getToolkit();
    final Section section = toolkit.createSection(form.getBody(),
        ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
    section.setText(title);
    section.setDescription(desc);
    toolkit.createCompositeSeparator(section);
    return section;
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
  public Composite createSection(final IManagedForm mform, final String title, final String desc, final int numColumns, final GridData gridData) {

    final ScrolledForm form = mform.getForm();
    final FormToolkit toolkit = mform.getToolkit();
    final Section section = toolkit.createSection(form.getBody(),
        ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
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
   * Creates the section editing Papification.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createPapificationSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final Composite container = createSection(managedForm, title, desc, 1, new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
    // section.setLayout(new GridLayout());
    // section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

    // this.checkStateListener = new PapificationCheckStateListener(section, this.scenario);

    // Creates the section part containing the tree with SDF vertices
    // new SDFTreeSection(this.scenario, section, managedForm.getToolkit(), Section.DESCRIPTION, this, this.checkStateListener);

    // DM added this
    final FormToolkit toolkit = managedForm.getToolkit();
    final Combo coreCombo = addCoreSelector(container, toolkit);

    addComponentSelectionTable(container, toolkit, coreCombo);
    addEventSelectionTable(container, toolkit, coreCombo);

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
    if ((source instanceof PapificationCheckStateListener) && (propId == IEditorPart.PROP_DIRTY)) {
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
  private void createFileSection(final IManagedForm mform, final String title, final String desc, final String fileEdit, final String initValue,
      final String browseTitle, final String fileExtension) {

    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 140;
    final Composite client = createSection(mform, title, desc, 2, gridData);
    final FormToolkit toolkit = mform.getToolkit();

    final GridData gd = new GridData();
    toolkit.createLabel(client, fileEdit);

    final Text text = toolkit.createText(client, initValue, SWT.SINGLE);
    text.setData(title);
    text.addModifyListener(e -> {
      final Text text1 = (Text) e.getSource();

      try {
        importData(text1);
      } catch (final Exception ex) {
        ex.printStackTrace();
      }

    });

    text.addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(final KeyEvent e) {
        if (e.keyCode == SWT.CR) {
          final Text text = (Text) e.getSource();
          try {
            importData(text);
          } catch (final Exception ex) {
            ex.printStackTrace();
          }
        }

      }

      @Override
      public void keyReleased(final KeyEvent e) {

      }

    });

    gd.widthHint = 400;
    text.setLayoutData(gd);

    final Button button = toolkit.createButton(client, Messages.getString("Overview.browse"), SWT.PUSH);
    final SelectionAdapter adapter = new FileSelectionAdapter(text, browseTitle, fileExtension);
    button.addSelectionListener(adapter);

    toolkit.paintBordersFor(client);
  }

  /**
   * Import data.
   *
   * @param text
   *          the text
   * @throws InvalidModelException
   *           the invalid model exception
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws CoreException
   *           the core exception
   */
  private void importData(final Text text) throws InvalidModelException, FileNotFoundException, CoreException {

    this.scenario.getConstraintGroupManager().setExcelFileURL(text.getText());
    this.scenario.getConstraintGroupManager().importConstraints(this.scenario);

    firePropertyChange(IEditorPart.PROP_DIRTY);

    if (this.checkStateListener != null) {
      this.checkStateListener.updateCheck();
    }
  }

  /**
   * Adds a table to edit component association.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addComponentSelectionTable(final Composite parent, final FormToolkit toolkit, final Combo coreCombo) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    this.componentTableViewer = CheckboxTableViewer.newCheckList(tablecps, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);

    final Table table = this.componentTableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    this.componentTableViewer.setContentProvider(new PapifyComponentListContentProvider());

    final PapificationComponentLabelProvider labelProvider = new PapificationComponentLabelProvider(this.scenario, this.componentTableViewer, this);
    this.componentTableViewer.setLabelProvider(labelProvider);
    coreCombo.addSelectionListener(labelProvider);

    // Create columns
    String[] componentSelectionColumnNames = { "PAPI components", "Component type" };

    final List<TableColumn> columns = new ArrayList<>();
    for (int i = 0; i < componentSelectionColumnNames.length; i++) {
      final TableColumn column = new TableColumn(table, SWT.NONE, i);
      column.setText(componentSelectionColumnNames[i]);
      columns.add(column);
    }

    // Make the last column (Expression) editable
    // XXX: Through an other way than double clicking (direct editing)
    // this.componentTableViewer.addDoubleClickListener(e -> labelProvider.handleDoubleClick((IStructuredSelection) e.getSelection()));

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
        final Point oldSize = tref.getSize();
        if (oldSize.x > area.width) {
          for (final TableColumn col : fColumns) {
            col.setWidth((width / 5) - 1);
          }
          tref.setSize(area.width, area.height);
        } else {
          tref.setSize(area.width, area.height);
          for (final TableColumn col : fColumns) {
            col.setWidth((width / 5) - 1);
          }
        }
      }
    });

    this.componentTableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = 100;
    gd.widthHint = 400;
    tablecps.setLayoutData(gd);
  }

  /**
   * Adds a table to edit event association.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addEventSelectionTable(final Composite parent, final FormToolkit toolkit, final Combo coreCombo) {

    final Composite tablecps = toolkit.createComposite(parent);
    tablecps.setVisible(true);

    this.eventTableViewer = CheckboxTableViewer.newCheckList(tablecps, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);

    final Table table = this.eventTableViewer.getTable();
    table.setLayout(new GridLayout());
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    this.eventTableViewer.setContentProvider(new PapifyEventListContentProvider());

    final PapificationEventLabelProvider labelProvider = new PapificationEventLabelProvider(this.scenario, this.eventTableViewer, this);
    this.eventTableViewer.setLabelProvider(labelProvider);

    // Create columns
    String[] eventSelectionColumnNames = { "Event Name", "Short Description" };

    final List<TableColumn> columns = new ArrayList<>();
    for (int i = 0; i < eventSelectionColumnNames.length; i++) {
      final TableColumn column = new TableColumn(table, SWT.NONE, i);
      column.setText(eventSelectionColumnNames[i]);
      columns.add(column);
    }

    // Make the last column (Expression) editable
    // XXX: Through an other way than double clicking (direct editing)
    // this.eventTableViewer.addDoubleClickListener(e -> labelProvider.handleDoubleClick((IStructuredSelection) e.getSelection()));

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
        final Point oldSize = tref.getSize();
        if (oldSize.x > area.width) {
          for (final TableColumn col : fColumns) {
            col.setWidth((width / 5) - 1);
          }
          tref.setSize(area.width, area.height);
        } else {
          tref.setSize(area.width, area.height);
          for (final TableColumn col : fColumns) {
            col.setWidth((width / 5) - 1);
          }
        }
      }
    });

    this.eventTableViewer.setInput(this.scenario);
    final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = 200;
    gd.widthHint = 400;
    tablecps.setLayoutData(gd);
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
    combocps.setVisible(true);
    final Combo combo = new Combo(combocps, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setVisibleItemCount(20);
    combo.setToolTipText(Messages.getString("Papification.coreSelectionTooltip"));
    comboDataInit(combo);
    combo.addFocusListener(new FocusListener() {

      @Override
      public void focusGained(final FocusEvent e) {
        comboDataInit((Combo) e.getSource());

      }

      @Override
      public void focusLost(final FocusEvent e) {
      }

    });
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
    for (final String id : this.scenario.getOrderedOperatorIds()) {
      combo.add(id);
    }
  }

}
