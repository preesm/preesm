/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.ui.editors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.GraphitiModelPlugin;
import org.ietr.dftools.graphiti.io.GenericGraphParser;
import org.ietr.dftools.graphiti.io.GenericGraphWriter;
import org.ietr.dftools.graphiti.io.IncompatibleConfigurationFile;
import org.ietr.dftools.graphiti.model.Configuration;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.IValidator;
import org.ietr.dftools.graphiti.ui.GraphitiUiPlugin;
import org.ietr.dftools.graphiti.ui.actions.CopyAction;
import org.ietr.dftools.graphiti.ui.actions.CutAction;
import org.ietr.dftools.graphiti.ui.actions.PasteAction;
import org.ietr.dftools.graphiti.ui.actions.SetRefinementAction;
import org.ietr.dftools.graphiti.ui.editparts.EditPartFactoryImpl;
import org.ietr.dftools.graphiti.ui.editparts.GraphEditPart;
import org.ietr.dftools.graphiti.ui.properties.PropertiesConstants;
import org.ietr.dftools.graphiti.ui.wizards.SaveAsWizard;

/**
 * This class provides the graph editor.
 *
 * @author Matthieu Wipliez
 *
 */
public class GraphEditor extends GraphicalEditorWithFlyoutPalette implements ITabbedPropertySheetPageContributor {

  private static final String GRAPHITI_CONTEXT_MENU_ID = "net.sf.graphiti.GraphitiContextMenu";

  /** The graph. */
  private Graph graph;

  /** The manager. */
  private ZoomManager manager;

  /** The palette root. */
  private PaletteRoot paletteRoot;

  /** The status. */
  private IStatus status;

  /** The tabbed property sheet page. */
  private TabbedPropertySheetPage tabbedPropertySheetPage;

  /**
   * Create an editor.
   */
  public GraphEditor() {
    setEditDomain(new DefaultEditDomain(this));
    getPalettePreferences().setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
  }

  /**
   * Automatically layout the graph edited with the given direction.
   *
   * @param direction
   *          The direction, one of:
   *          <UL>
   *          <LI>{@link org.eclipse.draw2d.PositionConstants#EAST}
   *          <LI>{@link org.eclipse.draw2d.PositionConstants#SOUTH}
   *          </UL>
   */
  public void automaticallyLayout(final int direction) {
    final GraphEditPart doc = (GraphEditPart) getGraphicalViewer().getRootEditPart().getContents();
    doc.automaticallyLayoutGraphs(direction);
  }

  @Override
  public void commandStackChanged(final EventObject event) {
    firePropertyChange(IEditorPart.PROP_DIRTY);
    super.commandStackChanged(event);
  }

  @Override
  protected void configureGraphicalViewer() {

    super.configureGraphicalViewer();
    final GraphicalViewer viewer = getGraphicalViewer();
    viewer.setEditPartFactory(new EditPartFactoryImpl());

    final ScalableFreeformRootEditPart rootEditPart = new ScalableFreeformRootEditPart();
    viewer.setRootEditPart(rootEditPart);

    this.manager = rootEditPart.getZoomManager();
    getActionRegistry().registerAction(new ZoomInAction(this.manager));
    getActionRegistry().registerAction(new ZoomOutAction(this.manager));

    final double[] zoomLevels = new double[] { 0.1, 0.15, 0.25, 0.5, 0.75, 1.0, 1.5, 2.0 };
    this.manager.setZoomLevels(zoomLevels);

    // Predefined zoom levels
    final ArrayList<String> zoomContributions = new ArrayList<>();
    zoomContributions.add(ZoomManager.FIT_ALL);
    zoomContributions.add(ZoomManager.FIT_HEIGHT);
    zoomContributions.add(ZoomManager.FIT_WIDTH);
    this.manager.setZoomLevelContributions(zoomContributions);

    viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL), MouseWheelZoomHandler.SINGLETON);

    // Context menu
    final ContextMenuProvider provider = new GraphEditorContextMenuProvider(viewer, getActionRegistry());
    viewer.setContextMenu(provider);
    getSite().registerContextMenu(GraphEditor.GRAPHITI_CONTEXT_MENU_ID, provider, viewer);
  }

  @Override
  protected void createActions() {
    // create actions that will be used inside the editor, for instance in a
    // contextual menu
    super.createActions();

    final ActionRegistry registry = getActionRegistry();
    final Class<?>[] actions = { CopyAction.class, CutAction.class, PasteAction.class, PrintAction.class,
        SelectAllAction.class, SetRefinementAction.class };

    // Constructs all actions
    for (final Class<?> clz : actions) {
      try {
        final Constructor<?> ctor = clz.getConstructor(IWorkbenchPart.class);

        final IAction action = (IAction) ctor.newInstance(this);
        registry.registerAction(action);
        @SuppressWarnings("unchecked")
        final List<String> selectionActions = getSelectionActions();
        final String id = action.getId();
        selectionActions.add(id);
      } catch (final Exception e) {
        throw new GraphitiException("Could not create actions.", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
   */
  @Override
  protected PaletteViewerProvider createPaletteViewerProvider() {
    return new PaletteViewerProvider(getEditDomain()) {
      @Override
      protected void configurePaletteViewer(final PaletteViewer viewer) {
        super.configurePaletteViewer(viewer);
        viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
      }
    };
  }

  @Override
  public void dispose() {
    removeMarkers();
    super.dispose();
  }

  @Override
  public void doSave(final IProgressMonitor monitor) {
    // validate and then save
    validate();

    final IFile file = ((IFileEditorInput) getEditorInput()).getFile();

    final GenericGraphWriter writer = new GenericGraphWriter(this.graph);
    try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      writer.write(file.getLocation().toString(), out);
      file.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, monitor);
      getCommandStack().markSaveLocation();

      // refresh folder if we have written layout
      file.getParent().refreshLocal(IResource.DEPTH_ONE, null);
    } catch (final Exception e) {
      errorMessage(e.getMessage(), e);
      monitor.setCanceled(true);
    }
  }

  @Override
  public void doSaveAs() {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final IWorkbenchPage page = window.getActivePage();
    final IEditorPart editor = page.getActiveEditor();

    final SaveAsWizard wizard = new SaveAsWizard();
    wizard.init(workbench, new StructuredSelection(editor));

    final WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
    dialog.open();
  }

  /**
   * Displays an error message with the given exception.
   *
   * @param message
   *          A description of the error.
   * @param exception
   *          An exception.
   */
  private void errorMessage(final String message, final Throwable exception) {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final Shell shell = window.getShell();

    final IStatus errorStatus = new Status(IStatus.ERROR, GraphitiUiPlugin.PLUGIN_ID, message, exception);
    ErrorDialog.openError(shell, "Save error", "The file could not be saved.", errorStatus, IStatus.ERROR);
  }

  /**
   * Executes the given command.
   *
   * @param command
   *          the command
   */
  public void executeCommand(final Command command) {
    final CommandStack stack = getEditDomain().getCommandStack();
    stack.execute(command);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Class type) {
    if (type == ZoomManager.class) {
      return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
    } else if (type == IContentOutlinePage.class) {
      return new ThumbnailOutlinePage(this);
    } else if (type == IPropertySheetPage.class) {
      return this.tabbedPropertySheetPage;
    } else {
      return super.getAdapter(type);
    }
  }

  /**
   * Returns the contents of this editor.
   *
   * @return The contents of this editor.
   */
  public Graph getContents() {
    return this.graph;
  }

  @Override
  public String getContributorId() {
    return PropertiesConstants.CONTRIBUTOR_ID;
  }

  @Override
  protected PaletteRoot getPaletteRoot() {
    if (this.paletteRoot == null) {
      this.paletteRoot = GraphitiPalette.getPaletteRoot(this.graph);
    }
    return this.paletteRoot;
  }

  /**
   * Gives the current zoom factor.
   *
   * @return double
   */
  public double getZoom() {
    return this.manager.getZoom();
  }

  @Override
  protected void initializeGraphicalViewer() {
    final GraphicalViewer viewer = getGraphicalViewer();

    if (this.graph == null) {
      viewer.setContents(this.status);
    } else {
      viewer.setContents(this.graph);
      if (Boolean.FALSE.equals(this.graph.getValue(Graph.PROPERTY_HAS_LAYOUT))) {
        automaticallyLayout(PositionConstants.EAST);
      }
    }

    this.tabbedPropertySheetPage = new TabbedPropertySheetPage(this);
  }

  @Override
  public boolean isSaveAsAllowed() {
    return true;
  }

  /**
   * Remove existing markers.
   */
  private void removeMarkers() {
    // remove existing markers
    final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
    try {
      file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    } catch (final CoreException e) {
      throw new GraphitiException("Could not remove markers", e);
    }
  }

  @Override
  protected void setInput(final IEditorInput input) {
    super.setInput(input);

    try {
      final IFile file = ((IFileEditorInput) input).getFile();
      setPartName(file.getName());
      final GraphitiModelPlugin modelPlugin = GraphitiModelPlugin.getDefault();
      final Collection<Configuration> pluginConfigurations = modelPlugin.getConfigurations();
      final GenericGraphParser graphParser = new GenericGraphParser(pluginConfigurations);
      this.graph = graphParser.parse(file);

      // Updates the palette
      getEditDomain().setPaletteRoot(getPaletteRoot());

      // show properties
      final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (page != null) {
        page.showView(IPageLayout.ID_PROP_SHEET);
      }

      // initial validation
      validate();

      firePropertyChange(IEditorPart.PROP_INPUT);
    } catch (IncompatibleConfigurationFile | PartInitException e) {
      this.status = new Status(IStatus.ERROR, GraphitiUiPlugin.PLUGIN_ID, "An error occurred while parsing the file",
          e);
    }
  }

  /**
   * Sets the zoom to see the entire width of the graph in editor.
   */
  public void setWidthZoom() {
    this.manager.setZoomAsText(ZoomManager.FIT_WIDTH);
  }

  /**
   * Validate the graph.
   *
   * @return True if the graph is valid, false otherwise.
   */
  private void validate() {
    removeMarkers();
    final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
    final IValidator validator = this.graph.getConfiguration().getValidator();
    if (!validator.validate(this.graph, file)) {
      // activate problems view
      final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
        page.showView(IPageLayout.ID_PROBLEM_VIEW);
      } catch (final PartInitException e) {
        throw new GraphitiException("Could not validate input graph", e);
      }
    }
  }

  public Control getGraphicalViewerControl() {
    return super.getGraphicalViewer().getControl();
  }

  public void removeSelectionSynchronizerViewer(final EditPartViewer viewer) {
    getSelectionSynchronizer().removeViewer(viewer);
  }

  public RootEditPart getGraphicalViewerRootEditPart() {
    return getGraphicalViewer().getRootEditPart();
  }

}
