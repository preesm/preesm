package org.ietr.preesm.ui.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.ietr.preesm.ui.Activator;

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
      childStatuses.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, line));
    }

    final MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, childStatuses.toArray(new Status[] {}), // convert to array of statuses
        t.getLocalizedMessage(), t);

    ErrorDialog.openError(null, "Title", msg, ms);
  }
}
