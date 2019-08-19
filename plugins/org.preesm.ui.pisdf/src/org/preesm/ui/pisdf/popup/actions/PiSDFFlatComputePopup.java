package org.preesm.ui.pisdf.popup.actions;

import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.pisdf.util.SavePiGraph;
import org.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 * Enables to call the PiSDF flat transformation directly from the menu.
 * 
 * @author ahonorat
 */
public class PiSDFFlatComputePopup extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    final IWorkbenchPage page = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
    final ISelection activeSelection = page.getSelection();
    try {
      if (activeSelection instanceof TreeSelection) {
        // in the resource explorer, compute for all selected .pi files
        final TreeSelection selection = (TreeSelection) activeSelection;
        final Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
          final Object next = iterator.next();
          if (next instanceof IFile) {
            final IFile file = (IFile) next;
            final PiGraph piGraphWithReconnection = PiParser.getPiGraphWithReconnection(file.getFullPath().toString());

            processPiSDF(piGraphWithReconnection, file.getProject());
          }
        }
      } else if (activeSelection instanceof StructuredSelection) {
        // in the PiSDF editor, compute for active graph
        final IEditorPart editor = page.getActiveEditor();
        final DiagramEditorInput input = (DiagramEditorInput) editor.getEditorInput();
        final String uriString = input.getUri().path().replace("/resource", "").replace(".diagram", ".pi");
        final IPath fromPortableString = Path.fromPortableString(uriString);
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);

        final String string = file.getFullPath().toString();
        final PiGraph piGraphWithReconnection = PiParser.getPiGraphWithReconnection(string);
        processPiSDF(piGraphWithReconnection, file.getProject());
      } else {
        ErrorWithExceptionDialog.errorDialogWithStackTrace("unsupported selection : " + activeSelection.getClass(),
            new UnsupportedOperationException());
      }
    } catch (PreesmRuntimeException e) {
      ErrorWithExceptionDialog.errorDialogWithStackTrace("Error during operation.", e);
    }
    return null;
  }

  private void processPiSDF(final PiGraph pigraph, final IProject iProject) {

    // performs optim only if already flat
    boolean optim = pigraph.getChildrenGraphs().isEmpty();
    String message = optim ? "Computing optimized graph of " : "Computing flat graph of ";

    PreesmLogger.getLogger().log(Level.INFO, message + pigraph.getName());

    final PiGraph flat = PiSDFFlattener.flatten(pigraph, optim);

    String suffix = optim ? "_optimzed" : "";

    SavePiGraph.save(iProject, flat, suffix);
  }

}
