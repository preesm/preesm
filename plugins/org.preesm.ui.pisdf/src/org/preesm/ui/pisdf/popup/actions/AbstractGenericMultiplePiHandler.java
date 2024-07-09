/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
import org.eclipse.ui.PlatformUI;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 * Class to trigger execution of the command on each PiGraph of the selection.
 *
 * @author ahonorat
 */
public abstract class AbstractGenericMultiplePiHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    final IWorkbenchWindow wwindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
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
