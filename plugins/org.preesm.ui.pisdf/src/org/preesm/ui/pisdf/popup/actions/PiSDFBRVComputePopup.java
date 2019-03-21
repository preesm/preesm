/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2019)
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
import org.preesm.commons.exceptions.PreesmRuntimeException;
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
    } catch (PreesmRuntimeException e) {
      ErrorWithExceptionDialog.errorDialogWithStackTrace("Error during operation.", e);
    }
    return null;
  }

  private void processPiSDF(final PiGraph pigraph) {
    PreesmLogger.getLogger().log(Level.INFO, "Computing BRV for " + pigraph.getName());

    final EList<ConfigInputPort> configInputPorts = pigraph.getConfigInputPorts();
    if (!configInputPorts.isEmpty()) {
      PreesmLogger.getLogger().log(Level.WARNING, "Cannot compute the BRV of a subgraph");
      return;
    }

    final boolean locallyStatic = pigraph.isLocallyStatic();
    if (!locallyStatic) {
      PreesmLogger.getLogger().log(Level.WARNING, "Cannot compute the BRV of a dynamic graph");
      return;
    }

    new PiSDFParameterResolverVisitor().doSwitch(pigraph);
    final Map<AbstractVertex, Long> brv = PiBRV.compute(pigraph, BRVMethod.LCM);
    PiBRV.printRV(brv);
  }

}
