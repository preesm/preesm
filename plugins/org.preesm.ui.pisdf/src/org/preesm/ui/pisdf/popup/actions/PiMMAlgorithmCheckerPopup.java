/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.PiMMAlgorithmChecker;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.utils.ErrorWithExceptionDialog;

/**
 * Class to launch a PiGraph check through pop-up menu.
 *
 * @author cguy
 */
public class PiMMAlgorithmCheckerPopup extends AbstractHandler {

  /** The shell. */
  private Shell shell;

  /**
   * Constructor.
   */
  public PiMMAlgorithmCheckerPopup() {
    super();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    final PiMMAlgorithmChecker checker = new PiMMAlgorithmChecker();
    try {

      final IWorkbenchPage page = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
      final TreeSelection selection = (TreeSelection) page.getSelection();
      final IFile file = (IFile) selection.getFirstElement();
      PiGraph graph;
      try {
        graph = PiParser.getPiGraphWithReconnection(file.getFullPath().toString());
      } catch (PreesmRuntimeException e) {
        ErrorWithExceptionDialog.errorDialogWithStackTrace("Error during operation.", e);
        return null;
      }

      final StringBuffer message = new StringBuffer();
      if (checker.checkGraph(graph)) {
        message.append(checker.getOkMsg());
      } else {
        if (checker.isErrors()) {
          message.append(checker.getErrorMsg());
          if (checker.isWarnings()) {
            message.append("\n");
          }
        }
        if (checker.isWarnings()) {
          message.append(checker.getWarningMsg());
        }
      }

      MessageDialog.openInformation(this.shell, "Checker", message.toString());
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
