/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Hascoet [jhascoet@kalray.eu] (2016)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2016)
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

import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A few methods useful for dialog windows.
 */
public class DialogUtil {

  private DialogUtil() {
    // prevent instantiation
  }

  /**
   * Returns the currently active Shell.
   *
   * @return The currently active Shell.
   */
  public static Shell getShell() {
    return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
  }

  /**
   * Opens an simple input dialog with OK and Cancel buttons.
   *
   * @param dialogTitle
   *          the dialog title, or <code>null</code> if none
   * @param dialogMessage
   *          the dialog message, or <code>null</code> if none
   * @param initialValue
   *          the initial input value, or <code>null</code> if none (equivalent to the empty string)
   * @param validator
   *          an input validator, or <code>null</code> if none
   * @return the string, or <code>null</code> if user cancels
   */
  public static String askString(final String dialogTitle, final String dialogMessage, final String initialValue,
      final IInputValidator validator) {
    String ret = null;
    final Shell shell = DialogUtil.getShell();
    final InputDialog inputDialog = new InputDialog(shell, dialogTitle, dialogMessage, initialValue, validator);
    final int retDialog = inputDialog.open();
    if (retDialog == Window.OK) {
      ret = inputDialog.getValue();
    }
    return ret;
  }

  /**
   * Ask save file.
   *
   * @param dialogText
   *          the dialog text
   * @param fileExtensions
   *          the file extensions
   * @return the i path
   */
  public static IPath askSaveFile(final String dialogText, final Set<String> fileExtensions) {
    final Shell shell = DialogUtil.getShell();

    final FileDialog inputDialog = new FileDialog(shell, SWT.SAVE);
    inputDialog.setText(dialogText);
    inputDialog.setOverwrite(true);
    inputDialog.setFilterExtensions(fileExtensions.toArray(new String[fileExtensions.size()]));

    final String retDialog = inputDialog.open();
    if (retDialog != null) {
      return new Path(retDialog);
    }
    return null;
  }

}
