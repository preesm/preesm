package org.preesm.ui.pisdf.popup.actions;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.pisdf.statictools.PiSDFParameterResolverVisitor;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 *
 */
public class PiSDFBRVComputePopup extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    final IWorkbenchPage page = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
    final ISelection activeSelection = page.getSelection();

    if (activeSelection instanceof TreeSelection) {
      // in the resource explorer, compute for all selected .pi files
      final TreeSelection selection = (TreeSelection) activeSelection;
      final Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
        final Object next = iterator.next();
        if (next instanceof IFile) {
          final IFile file = (IFile) next;
          final PiGraph piGraphWithReconnection = PiParser.getPiGraphWithReconnection(file.getFullPath().toString());
          processPiSDF(piGraphWithReconnection);
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
      processPiSDF(piGraphWithReconnection);
    } else {
      ErrorWithExceptionDialog.errorDialogWithStackTrace("unsupported selection : " + activeSelection.getClass(),
          new UnsupportedOperationException());
    }
    return null;
  }

  private void processPiSDF(final PiGraph pigraph) {
    PreesmLogger.getLogger().log(Level.INFO, "Computing BRV for " + pigraph.getName());

    final EList<ConfigInputPort> configInputPorts = pigraph.getConfigInputPorts();
    if (!configInputPorts.isEmpty()) {
      PreesmLogger.getLogger().log(Level.WARNING, "Cannot compute the BRV of a subgraph");
    } else {
      new PiSDFParameterResolverVisitor().doSwitch(pigraph);
      final Map<AbstractVertex, Long> brv = PiBRV.compute(pigraph, BRVMethod.LCM);
      PiBRV.printRV(brv);
    }
  }

}
