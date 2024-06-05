/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2020) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.ui.pisdf.popup.actions;

import java.util.Iterator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.preesm.ui.pisdf.diagram.PiMMDiagramEditor;
import org.preesm.ui.pisdf.util.PiMM2DiagramGenerator;
import org.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 *
 */
public class PiMM2DiagramGeneratorPopup extends AbstractHandler {

  private static final IWorkbench     WORKBENCH      = PlatformUI.getWorkbench();
  private static final Shell          SHELL          = PiMM2DiagramGeneratorPopup.WORKBENCH
      .getModalDialogShellProvider().getShell();
  private static final IWorkspace     WORKSPACE      = ResourcesPlugin.getWorkspace();
  private static final IWorkspaceRoot WORKSPACE_ROOT = PiMM2DiagramGeneratorPopup.WORKSPACE.getRoot();

  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    final IWorkbenchPage page = PiMM2DiagramGeneratorPopup.WORKBENCH.getActiveWorkbenchWindow().getActivePage();
    final TreeSelection selection = (TreeSelection) page.getSelection();

    final Iterator<?> iterator = selection.iterator();
    while (iterator.hasNext()) {
      final Object next = iterator.next();
      if (next instanceof final IFile file) {
        generateDiagramFile(file);
      }
    }
    return null;
  }

  private void generateDiagramFile(final IFile piFile) throws ExecutionException {
    try {
      final IPath fullPath = piFile.getFullPath();
      final IPath diagramFilePath = fullPath.removeFileExtension().addFileExtension("diagram");

      final boolean diagramAlreadyExists = checkExists(diagramFilePath);

      int userDecision = SWT.OK;
      if (diagramAlreadyExists) {
        userDecision = askUserConfirmation(PiMM2DiagramGeneratorPopup.SHELL, diagramFilePath);
      }

      if (!diagramAlreadyExists || (userDecision == SWT.OK)) {
        closeEditorIfOpen(diagramFilePath);

        final IFile file = PiMM2DiagramGeneratorPopup.WORKSPACE_ROOT.getFile(diagramFilePath);
        file.delete(true, null);
        file.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
        PiMM2DiagramGeneratorPopup.WORKSPACE.save(true, null);

        PiMM2DiagramGenerator.generateDiagramFile(piFile);

      }
    } catch (final Exception cause) {
      final String message = "Could not generate diagram from PiMM model file";
      ErrorWithExceptionDialog.errorDialogWithStackTrace(message, cause);
    }
  }

  private void closeEditorIfOpen(final IPath diagramFilePath) {
    final IWorkbench workbench = PiMM2DiagramGeneratorPopup.WORKBENCH;
    final IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
    for (final IEditorReference activeEditorReference : page.getEditorReferences()) {
      final IEditorPart activeEditor = activeEditorReference.getEditor(true);
      if (activeEditor instanceof final PiMMDiagramEditor diagEditor) {
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
  }

  private int askUserConfirmation(final Shell shell, final IPath diagramFilePath) {
    int userDecision;
    // create a dialog with ok and cancel buttons and a question icon
    final MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
    dialog.setText("Confirmation required");
    dialog.setMessage(
        "The file \n\n" + diagramFilePath.toString() + "\n\n already exists. Do you want to overwrite it ?");

    // open dialog and await user selection
    userDecision = dialog.open();
    return userDecision;
  }

  private boolean checkExists(final IPath diagramFilePath) {
    return PiMM2DiagramGeneratorPopup.WORKSPACE_ROOT.exists(diagramFilePath);
  }
}
