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
package org.preesm.ui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.preesm.ui.PreesmUIPlugin;

/**
 *
 * @author anmorvan
 *
 */
public class ErrorWithExceptionDialog {

  private ErrorWithExceptionDialog() {

  }

  /**
   *
   */
  public static void errorDialogWithStackTrace(final String msg, final Throwable t) {

    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    t.printStackTrace();

    final String trace = sw.toString(); // stack trace as a string

    // Temp holder of child statuses
    final List<Status> childStatuses = new ArrayList<>();

    // Split output by OS-independend new-line
    for (final String line : trace.split(System.getProperty("line.separator"))) {
      // build & add status
      childStatuses.add(new Status(IStatus.ERROR, PreesmUIPlugin.PLUGIN_ID, line));
    }

    final MultiStatus ms = new MultiStatus(PreesmUIPlugin.PLUGIN_ID, IStatus.ERROR,
        childStatuses.toArray(new Status[] {}), // convert to array of statuses
        t.getLocalizedMessage(), t);

    ErrorDialog.openError(null, "Title", msg, ms);
  }
}
