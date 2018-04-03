/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.ui.pimm.popup.actions;

import java.io.IOException;
import java.util.Iterator;
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
import org.eclipse.emf.common.command.CommandStack;
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
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.serialize.PiParser;
import org.ietr.preesm.ui.PreesmUIPlugin;
import org.ietr.preesm.ui.pimm.diagram.PiMMDiagramEditor;
import org.ietr.preesm.ui.pimm.features.PasteFeature;
import org.ietr.preesm.ui.pimm.layout.AutoLayoutFeature;
import org.ietr.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 *
 */
public class PiMM2DiagramGeneratorPopup extends AbstractHandler {

  private static final IWorkbench     WORKBENCH      = PreesmUIPlugin.getDefault().getWorkbench();
  private static final Shell          SHELL          = PiMM2DiagramGeneratorPopup.WORKBENCH.getModalDialogShellProvider().getShell();
  private static final IWorkspace     WORKSPACE      = ResourcesPlugin.getWorkspace();
  private static final IWorkspaceRoot WORKSPACE_ROOT = PiMM2DiagramGeneratorPopup.WORKSPACE.getRoot();

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    final IWorkbenchPage page = PiMM2DiagramGeneratorPopup.WORKBENCH.getActiveWorkbenchWindow().getActivePage();
    final TreeSelection selection = (TreeSelection) page.getSelection();

    final Iterator<?> iterator = selection.iterator();
    while (iterator.hasNext()) {
      final Object next = iterator.next();
      if (next instanceof IFile) {
        final IFile file = (IFile) next;
        generateDiagramFile(file);
      }
    }
    return null;
  }

  private void generateDiagramFile(final IFile file) throws ExecutionException {
    try {
      final IPath fullPath = file.getFullPath();
      final IPath diagramFilePath = fullPath.removeFileExtension().addFileExtension("diagram");

      final boolean diagramAlreadyExists = checkExists(diagramFilePath);

      int userDecision = SWT.OK;
      if (diagramAlreadyExists) {
        userDecision = askUserConfirmation(PiMM2DiagramGeneratorPopup.SHELL, diagramFilePath);

      }
      if (!diagramAlreadyExists || (userDecision == SWT.OK)) {
        closeEditorIfOpen(diagramFilePath);
        // Get PiGraph, init empty Diagram, and link them together
        final PiGraph graph = PiParser.getPiGraph(fullPath.toString());
        final Diagram diagram = Graphiti.getPeCreateService().createDiagram("PiMM", graph.getName(), true);
        linkPiGraphAndDiagram(graph, diagram);

        // create the resource (safe because the wizard does not allow existing
        // resources to be overridden)
        final IFile diagramFile = initDiagramResource(diagramFilePath, diagram, diagramAlreadyExists);
        openAndPopulateDiagram(diagramFile);

      }
    } catch (final Exception cause) {
      final String message = "Could not generate diagram from PiMM model file";
      ErrorWithExceptionDialog.errorDialogWithStackTrace(message, cause);
    }
  }

  private void closeEditorIfOpen(final IPath diagramFilePath) {
    final IWorkbench workbench = PiMM2DiagramGeneratorPopup.WORKBENCH;
    final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
    final IEditorPart activeEditor = page.getActiveEditor();
    if (activeEditor instanceof PiMMDiagramEditor) {
      final PiMMDiagramEditor diagEditor = (PiMMDiagramEditor) activeEditor;
      // check if current diagram editor targets the diagram file we want to overwrite
      final IDiagramEditorInput diagramEditorInput = diagEditor.getDiagramEditorInput();
      final URI uri = diagramEditorInput.getUri();
      final URI trimFragment = uri.trimFragment();
      final URI createPlatformResourceURI = URI.createPlatformResourceURI(diagramFilePath.toString(), false);
      final boolean equals = trimFragment.equals(createPlatformResourceURI);
      if (equals) {
        // close current editor
        ((PiMMDiagramEditor) activeEditor).close();
      }
    }

  }

  private int askUserConfirmation(final Shell shell, final IPath diagramFilePath) {
    int userDecision;
    // create a dialog with ok and cancel buttons and a question icon
    final MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
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

    private final PiGraph           graph;
    private final PiMMDiagramEditor editor;

    PopulateDiagramCommand(final PiMMDiagramEditor editor, final TransactionalEditingDomain domain, final PiGraph graph) {
      super(domain);
      this.editor = editor;
      this.graph = graph;
    }

    @Override
    protected void doExecute() {

      final IFeatureProvider featureProvider = this.editor.getDiagramTypeProvider().getFeatureProvider();
      final PasteContext pasteContext = new PasteContext(new PictogramElement[0]);
      pasteContext.setLocation(0, 0);
      final PasteFeature pasteFeature = new PasteFeature(featureProvider);

      for (final Parameter p : this.graph.getParameters()) {
        pasteFeature.addGraphicalRepresentationForVertex(p, 0, 0);
      }
      for (final AbstractVertex v : this.graph.getActors()) {
        pasteFeature.addGraphicalRepresentationForVertex(v, 0, 0);
      }

      for (final Fifo fifo : this.graph.getFifos()) {
        final FreeFormConnection pe = pasteFeature.addGraphicalRepresentationForFifo(fifo);
        final Delay delay = fifo.getDelay();
        if (delay != null) {
          pasteFeature.addGraphicalRepresentationForDelay(fifo, pe, delay);
        }
      }

      // connect dependencies after fifos (for connecting the delays)
      for (final Dependency dep : this.graph.getDependencies()) {
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
    final IWorkbench workbench = PiMM2DiagramGeneratorPopup.WORKBENCH;
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

    PopulateDiagramCommand command = new PopulateDiagramCommand(editor, editingDomain, graph);
    CommandStack commandStack = editingDomain.getCommandStack();
    commandStack.execute(command);

    // save the .diagram file
    editor.doSave(null);
  }

  private IFile initDiagramResource(final IPath diagramFilePath, final Diagram diagram, final boolean deleteExistingFileFirst)
      throws IOException, CoreException {
    final IFile file = PiMM2DiagramGeneratorPopup.WORKSPACE_ROOT.getFile(diagramFilePath);
    if (deleteExistingFileFirst) {
      file.delete(true, null);
      file.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
      PiMM2DiagramGeneratorPopup.WORKSPACE.save(true, null);
    }
    final ResourceSet set = new ResourceSetImpl();
    final URI uri = URI.createPlatformResourceURI(diagramFilePath.toString(), false);
    final Resource resource = set.createResource(uri);
    resource.getContents().add(diagram);
    resource.save(null);
    PiMM2DiagramGeneratorPopup.WORKSPACE.save(true, null);
    file.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
    return file;
  }

  private boolean checkExists(final IPath diagramFilePath) {
    return PiMM2DiagramGeneratorPopup.WORKSPACE_ROOT.exists(diagramFilePath);
  }

  private void linkPiGraphAndDiagram(final PiGraph graph, final Diagram diagram) {
    final PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
    link.getBusinessObjects().add(graph);
    diagram.setLink(link);
  }

}
