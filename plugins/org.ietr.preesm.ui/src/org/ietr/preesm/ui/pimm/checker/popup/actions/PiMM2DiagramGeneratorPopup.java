package org.ietr.preesm.ui.pimm.checker.popup.actions;

import java.io.IOException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.PasteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.ui.Activator;
import org.ietr.preesm.ui.pimm.diagram.PiMMDiagramEditor;
import org.ietr.preesm.ui.pimm.features.PasteFeature;
import org.ietr.preesm.ui.pimm.layout.AutoLayoutFeature;

/**
 *
 */
public class PiMM2DiagramGeneratorPopup extends AbstractHandler {

  private static final IWorkspace     WORKSPACE      = ResourcesPlugin.getWorkspace();
  private static final IWorkspaceRoot WORKSPACE_ROOT = WORKSPACE.getRoot();

  // TODO remove
  private static final boolean BYPASS_CONFIRM_DIALOG = WORKSPACE != WORKSPACE.getRoot();

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {

    final IWorkbench workbench = Activator.getDefault().getWorkbench();
    final Shell shell = workbench.getModalDialogShellProvider().getShell();
    final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
    final TreeSelection selection = (TreeSelection) page.getSelection();
    final IFile file = (IFile) selection.getFirstElement();
    try {
      final IPath fullPath = file.getFullPath();
      final IPath diagramFilePath = fullPath.removeFileExtension().addFileExtension("diagram");

      boolean diagramAlreadyExists = checkExists(diagramFilePath);

      int userDecision = SWT.OK;
      if (!BYPASS_CONFIRM_DIALOG && diagramAlreadyExists) {
        userDecision = askUserConfirmation(shell, diagramFilePath);
      }
      if (!diagramAlreadyExists || userDecision == SWT.OK) {
        // Get PiGraph, init empty Diagram, and link them together
        PiGraph graph = ScenarioParser.getPiGraph(fullPath.toString());
        Diagram diagram = Graphiti.getPeCreateService().createDiagram("PiMM", graph.getName(), true);
        linkPiGraphAndDiagram(graph, diagram);

        // create the resource (safe because the wizard does not allow existing
        // resources to be overridden)
        final IFile diagramFile = initDiagramResource(diagramFilePath, diagram, diagramAlreadyExists);
        openAndPopulateDiagram(diagramFile);

      }
    } catch (Exception cause) {
      final String message = "Could not generate diagram from PiMM model file";
      throw new ExecutionException(message, cause);
    }

    return null;
  }

  private int askUserConfirmation(final Shell shell, final IPath diagramFilePath) {
    int userDecision;
    // create a dialog with ok and cancel buttons and a question icon
    MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
    dialog.setText("Confirmation required");
    dialog.setMessage("The file \n\n" + diagramFilePath.toString() + "\n\n already exists. Do you want to overwrite it ?");

    // open dialog and await user selection
    userDecision = dialog.open();
    return userDecision;
  }

  /**
   *
   */
  class PopulateDiagramCommand extends RecordingCommand {

    private PiGraph           graph;
    private PiMMDiagramEditor editor;

    PopulateDiagramCommand(PiMMDiagramEditor editor, TransactionalEditingDomain domain, PiGraph graph) {
      super(domain);
      this.editor = editor;
      this.graph = graph;
    }

    @Override
    protected void doExecute() {

      final IFeatureProvider featureProvider = editor.getDiagramTypeProvider().getFeatureProvider();
      final PasteContext pasteContext = new PasteContext(new PictogramElement[0]);
      pasteContext.setLocation(0, 0);
      final PasteFeature pasteFeature = new PasteFeature(featureProvider);

      for (Parameter p : graph.getParameters()) {
        pasteFeature.addGraphicalRepresentationForVertex(p, 0, 0);
      }
      for (AbstractVertex v : graph.getVertices()) {
        pasteFeature.addGraphicalRepresentationForVertex(v, 0, 0);
      }
      for (Fifo fifo : graph.getFifos()) {
        final FreeFormConnection pe = pasteFeature.addGraphicalRepresentationForFifo(fifo);
        final Delay delay = fifo.getDelay();
        if (delay != null) {
          pasteFeature.addGraphicalRepresentationForDelay(fifo, pe, delay);
        }
      }

      // connect dependencies after fifos (for connecting the delays)
      for (Dependency dep : graph.getDependencies()) {
        pasteFeature.addGraphicalRepresentationForDependency(dep);
      }
      pasteFeature.postProcess();

      final AutoLayoutFeature autoLayoutFeature = new AutoLayoutFeature(featureProvider);
      final CustomContext context = new CustomContext();
      autoLayoutFeature.execute(context);
    }
  }

  private void openAndPopulateDiagram(final IFile diagramFile) throws PartInitException {

    // open editor
    final IWorkbench workbench = Activator.getDefault().getWorkbench();
    final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
    final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(diagramFile.getName());

    final PiMMDiagramEditor editor = (PiMMDiagramEditor) page.openEditor(new FileEditorInput(diagramFile), desc.getId());

    final IDiagramTypeProvider diagramTypeProvider = editor.getDiagramTypeProvider();

    final IDiagramBehavior diagramBehavior = diagramTypeProvider.getDiagramBehavior();
    final TransactionalEditingDomain editingDomain = diagramBehavior.getEditingDomain();
    // use the diagram from the editor instead of the previously created one
    final Diagram diagram = diagramTypeProvider.getDiagram();

    // and the PiGraph from this diagram to get consistent links
    final PiGraph graph = (PiGraph) diagram.getLink().getBusinessObjects().get(0);

    editingDomain.getCommandStack().execute(new PopulateDiagramCommand(editor, editingDomain, graph));

    // save the .diagram file
    editor.doSave(null);
  }

  private IFile initDiagramResource(final IPath diagramFilePath, final Diagram diagram, boolean deleteExistingFileFirst) throws IOException, CoreException {
    final IFile file = WORKSPACE_ROOT.getFile(diagramFilePath);
    if (deleteExistingFileFirst) {
      file.delete(true, null);
      file.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
      WORKSPACE.save(true, null);
    }
    final ResourceSet set = new ResourceSetImpl();
    final URI uri = URI.createPlatformResourceURI(diagramFilePath.toString(), false);
    final Resource resource = set.createResource(uri);
    resource.getContents().add(diagram);
    resource.save(null);
    WORKSPACE.save(true, null);
    file.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
    return file;
  }

  private boolean checkExists(IPath diagramFilePath) {
    return WORKSPACE_ROOT.exists(diagramFilePath);
  }

  private void linkPiGraphAndDiagram(final PiGraph graph, final Diagram diagram) {
    final PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
    link.getBusinessObjects().add(graph);
    diagram.setLink(link);
  }

}
