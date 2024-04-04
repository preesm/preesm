package org.preesm.ui.pisdf.popup.actions;

import java.util.Iterator;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 * Class to trigger execution of the command on each PiGraph of the selection.
 *
 * @author ahonorat
 */
public abstract class AbstractGenericMultiplePiHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    final IWorkbenchWindow wwindow = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
    final IWorkbenchPage page = wwindow.getActivePage();

    final ISelection activeSelection = page.getSelection();
    try {
      if (activeSelection instanceof final TreeSelection selection) {
        // in the resource explorer, compute for all selected .pi files
        final Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
          final Object next = iterator.next();
          if (next instanceof final IFile file) {
            final PiGraph piGraphWithReconnection = PiParser.getPiGraphWithReconnection(file.getFullPath().toString());

            processPiSDF(piGraphWithReconnection, file.getProject(), wwindow.getShell());
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
        processPiSDF(piGraphWithReconnection, file.getProject(), wwindow.getShell());
      } else {
        ErrorWithExceptionDialog.errorDialogWithStackTrace("Unsupported selection : " + activeSelection.getClass(),
            new UnsupportedOperationException());
      }
    } catch (final PreesmRuntimeException e) {
      ErrorWithExceptionDialog.errorDialogWithStackTrace("Error during operation.", e);
    }
    return null;
  }

  /**
   * Command to perform on each selected PiGraph.
   *
   * @param pigraph
   *          Input graph.
   * @param iProject
   *          Project of the graph.
   * @param shell
   *          Parent workbench window.
   */
  public abstract void processPiSDF(final PiGraph pigraph, final IProject iProject, final Shell shell);

}
