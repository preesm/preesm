/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
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
package org.ietr.preesm.ui.scenario.editor.papify;

import java.io.FileNotFoundException;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
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
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.papi.PapiConfigParser;
import org.ietr.preesm.core.scenario.papi.PapiEventInfo;
import org.ietr.preesm.core.scenario.papi.PapifyConfigManager;
import org.ietr.preesm.ui.scenario.editor.FileSelectionAdapter;
import org.ietr.preesm.ui.scenario.editor.Messages;
import org.ietr.preesm.utils.files.WorkspaceUtils;

/**
 * Papify editor within the implementation editor.
 *
 * @author dmadronal
 */
public class PapifyPage extends FormPage implements IPropertyListener {

  /** Currently edited scenario. */
  private PreesmScenario scenario = null;

  /** The check state listener. */
  private PapifyCheckStateListener checkStateListener = null;

  /** The table viewer. */

  // DM added this
  CheckboxTreeViewer                         peTreeViewer         = null;
  CheckboxTreeViewer                         actorTreeViewer      = null;
  CheckboxTableViewer                        eventTableViewer     = null;
  PapiEventInfo                              papiEvents           = null;
  PapiConfigParser                           papiParser           = new PapiConfigParser();
  PapifyComponentListContentProvider2DMatrix peContentProvider    = null;
  PapifyActorListContentProvider2DMatrix     actorContentProvider = null;

  /**
   * Instantiates a new papify page.
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
  public PapifyPage(final PreesmScenario scenario, final FormEditor editor, final String id, final String title) {
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
    f.setText(Messages.getString("Papify.title"));
    f.getBody().setLayout(new GridLayout());

    // Papify file chooser section
    createFileSection(managedForm, Messages.getString("Papify.file"), Messages.getString("Papify.fileDescription"),
        Messages.getString("Papify.fileEdit"), this.scenario.getPapifyConfigManager().getXmlFileURL(),
        Messages.getString("Papify.fileBrowseTitle"), "xml");

    createPapifyPESection(managedForm, Messages.getString("Papify.titlePESection"),
        Messages.getString("Papify.descriptionPE"));
    createPapifyActorSection(managedForm, Messages.getString("Papify.titleActorSection"),
        Messages.getString("Papify.descriptionActor"));

    if (!this.scenario.getPapifyConfigManager().getXmlFileURL().equals("")) {
      final String xmlFullPath = getFullXmlPath(this.scenario.getPapifyConfigManager().getXmlFileURL());
      if (!xmlFullPath.equals("")) {
        parseXmlData(xmlFullPath);
      }
      if ((this.papiEvents != null) && !this.papiEvents.getComponents().isEmpty()) {
        scenario.getPapifyConfigManager().addPapifyData(this.papiEvents);
        updateTables();
      } else {
        this.scenario.setPapifyConfigManager(new PapifyConfigManager());
        this.scenario.getPapifyConfigManager().addPapifyData(this.papiEvents);
      }
      managedForm.refresh();
      managedForm.reflow(true);
    }

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
    final Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TWISTIE
        | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION | ExpandableComposite.EXPANDED);
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
   * Creates the section editing PAPIFY PE <--> PAPI_component(s) association.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createPapifyPESection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final Composite container = createSection(managedForm, title, desc, 1,
        new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

    this.checkStateListener = new PapifyCheckStateListener(container, this.scenario);
    container.addPaintListener(this.checkStateListener);

    final FormToolkit toolkit = managedForm.getToolkit();

    addComponentCheckBoxTreeViewer(container, toolkit);

  }

  /**
   * Creates the section editing PAPIFY Actor <--> EventSet(s) association.
   *
   * @param managedForm
   *          the managed form
   * @param title
   *          the title
   * @param desc
   *          the desc
   */
  private void createPapifyActorSection(final IManagedForm managedForm, final String title, final String desc) {

    // Creates the section
    managedForm.getForm().setLayout(new FillLayout());
    final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    final Composite container = createSection(managedForm, title, desc, 1, gridData);

    // this.checkStateListener = new PapifyCheckStateListener(container, this.scenario);
    // container.addPaintListener(this.checkStateListener);

    final FormToolkit toolkit = managedForm.getToolkit();

    addEventCheckBoxTreeViewer(container, toolkit);

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
    if ((source instanceof PapifyCheckStateListener) && (propId == IEditorPart.PROP_DIRTY)) {
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
    gridData.heightHint = 180;
    final Composite client = createSection(mform, title, desc, 1, gridData);
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

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    WorkspaceUtils.updateWorkspace();

    final Path path = new Path(text.getText());
    final IFile file = workspace.getRoot().getFile(path);

    this.papiEvents = this.papiParser.parse(file.getLocation().toString());

    if (!text.getText().equals(this.scenario.getPapifyConfigManager().getXmlFileURL())
        && (this.papiEvents.getComponents() != null)) {
      this.scenario.setPapifyConfigManager(new PapifyConfigManager());
      this.scenario.getPapifyConfigManager().addPapifyData(this.papiEvents);
      this.scenario.getPapifyConfigManager().setExcelFileURL(text.getText());
      this.peTreeViewer.setInput(this.papiEvents);
      this.peContentProvider.setInput();
      this.actorTreeViewer.setInput(this.papiEvents);
      this.actorContentProvider.setInput();
    }
    firePropertyChange(IEditorPart.PROP_DIRTY);
  }

  /**
   * Get full xml path.
   *
   * @param xmlfile
   *          the xmlfile
   */
  private String getFullXmlPath(final String xmlfile) {

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    String fullPath = "";

    WorkspaceUtils.updateWorkspace();

    final Path path = new Path(xmlfile);
    final IFile file = workspace.getRoot().getFile(path);

    if (file.exists()) {
      fullPath = file.getLocation().toString();
    }
    return fullPath;
  }

  /**
   * Parse xml data.
   *
   * @param xmlpath
   *          the xmlfile
   */
  private void parseXmlData(final String xmlfile) {

    this.papiEvents = this.papiParser.parse(xmlfile);
    scenario.getPapifyConfigManager().addPapifyData(this.papiEvents);

  }

  /**
   * Update info.
   *
   * @param xmlpath
   *          the xmlfile
   */
  private void updateTables() {

    this.peTreeViewer.setInput(this.papiEvents);
    // this.peTreeViewer.refresh();
    this.peContentProvider.setInput();
    this.actorTreeViewer.setInput(this.papiEvents);
    // this.actorTreeViewer.refresh();
    this.actorContentProvider.setInput();
  }

  /**
   * Adds a checkBoxTreeViewer to edit component association.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addComponentCheckBoxTreeViewer(final Composite parent, final FormToolkit toolkit) {

    /*
     * final Composite tablecps = toolkit.createComposite(parent); tablecps.setVisible(true);
     */

    // Creating the tree view
    this.peTreeViewer = new CheckboxTreeViewer(toolkit.createTree(parent, SWT.NONE)) {

    };

    this.peContentProvider = new PapifyComponentListContentProvider2DMatrix(this.scenario);

    peTreeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

    peTreeViewer.setContentProvider(this.peContentProvider);

    peTreeViewer.setLabelProvider(new LabelProvider() {

    });
    peTreeViewer.addCheckStateListener(this.checkStateListener);

    peTreeViewer.setUseHashlookup(true);

    peTreeViewer.getTree().setLinesVisible(true);
    peTreeViewer.getTree().setHeaderVisible(true);

    final TreeViewerColumn peViewerColumn = new TreeViewerColumn(peTreeViewer, SWT.CENTER | SWT.CHECK);
    final TreeColumn peColumn = peViewerColumn.getColumn();
    peColumn.setText("Component type \\ PE type");
    peColumn.setWidth(200);
    peViewerColumn.setLabelProvider(new ColumnLabelProvider());

    this.checkStateListener.setComponentTreeViewer(this);
    this.peContentProvider.addCheckStateListener(this.checkStateListener);

    for (final String columnLabel : this.scenario.getOperatorDefinitionIds()) {

      final TreeViewerColumn viewerColumn = new TreeViewerColumn(peTreeViewer, SWT.CENTER | SWT.CHECK);
      final TreeColumn column = viewerColumn.getColumn();

      column.setText(columnLabel);
      column.setMoveable(true);
      column.setWidth(150);

      PapifyComponentListContentProvider2DMatrixES editingSupport = new PapifyComponentListContentProvider2DMatrixES(
          this.scenario, peTreeViewer, columnLabel, this.peContentProvider);

      viewerColumn.setLabelProvider(new PapifyComponentListContentProvider2DMatrixCLP(this.scenario, columnLabel));
      viewerColumn.setEditingSupport(editingSupport);
      this.peContentProvider.addEstatusSupport(editingSupport);

    }

    peTreeViewer.setInput(this.papiEvents);
    final GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = 100;
    peTreeViewer.getTree().setLayoutData(gd);

    // toolkit.paintBordersFor(parent);

    // Tree is refreshed in case of algorithm modifications
    parent.addPaintListener(checkStateListener);
  }

  /**
   * Adds a checkBoxTreeViewer to edit event association.
   *
   * @param parent
   *          the parent
   * @param toolkit
   *          the toolkit
   */
  private void addEventCheckBoxTreeViewer(final Composite parent, final FormToolkit toolkit) {

    /*
     * final Composite tablecps = toolkit.createComposite(parent); tablecps.setVisible(true);
     */

    // Creating the tree view
    this.actorTreeViewer = new CheckboxTreeViewer(toolkit.createTree(parent, SWT.NONE)) {

    };

    this.actorContentProvider = new PapifyActorListContentProvider2DMatrix(this.scenario);

    actorTreeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

    actorTreeViewer.setContentProvider(this.actorContentProvider);

    actorTreeViewer.setLabelProvider(new LabelProvider() {

    });
    this.checkStateListener.setEventTreeViewer(this);
    actorTreeViewer.addCheckStateListener(this.checkStateListener);

    actorTreeViewer.setUseHashlookup(true);

    actorTreeViewer.getTree().setLinesVisible(true);
    actorTreeViewer.getTree().setHeaderVisible(true);

    final TreeViewerColumn peViewerColumn = new TreeViewerColumn(actorTreeViewer, SWT.CENTER | SWT.CHECK);
    final TreeColumn peColumn = peViewerColumn.getColumn();
    peColumn.setText("Event name \\ Actor name");
    peColumn.setWidth(200);
    peViewerColumn.setLabelProvider(new ColumnLabelProvider());

    this.actorContentProvider.addCheckStateListener(this.checkStateListener);

    Map<String, Integer> actorNamesAndLevels = this.checkStateListener.getAllActorNamesAndLevels();
    for (String columnLabel : actorNamesAndLevels.keySet()) {
      System.out.println("Actor " + columnLabel + " has level " + actorNamesAndLevels.get(columnLabel));

      final TreeViewerColumn viewerColumn = new TreeViewerColumn(actorTreeViewer, SWT.CENTER | SWT.CHECK);
      final TreeColumn column = viewerColumn.getColumn();

      String finalColumnLabel = Integer.toString(actorNamesAndLevels.get(columnLabel)).concat(". ").concat(columnLabel);
      column.setText(finalColumnLabel);
      column.setMoveable(true);
      column.setWidth(150);

      PapifyActorListContentProvider2DMatrixES editingSupport = new PapifyActorListContentProvider2DMatrixES(
          this.scenario, actorTreeViewer, columnLabel, this.actorContentProvider);

      viewerColumn.setLabelProvider(new PapifyActorListContentProvider2DMatrixCLP(this.scenario, columnLabel));
      viewerColumn.setEditingSupport(editingSupport);
      this.actorContentProvider.addEstatusSupport(editingSupport);

    }

    actorTreeViewer.setInput(this.papiEvents);
    final GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    gd.heightHint = 140;
    actorTreeViewer.getTree().setLayoutData(gd);

    // toolkit.paintBordersFor(parent);

    // Tree is refreshed in case of algorithm modifications
    parent.addPaintListener(checkStateListener);
  }

}
