package org.preesm.ui.slam;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.preesm.ui.utils.DialogUtil;

public class NbNodesValidator implements IInputValidator {
  public static final String errorInput = "/!\\ You must enter a strictly positive integer. /!\\";

  @Override
  public String isValid(String newText) {
    if (newText == null || newText.isEmpty()) {
      return null;
    }
    try {
      final int a = Integer.parseInt(newText);
      if (a < 1) {
        return errorInput;
      }
      openNodePropertiesDialog(a);

    } catch (final NumberFormatException e) {
      return errorInput;
    }
    return null;
  }

  private void openNodePropertiesDialog(int numNodes) {
    final Shell shell = DialogUtil.getShell();
    final CustomNodePropertiesDialog dialog = new CustomNodePropertiesDialog(shell, numNodes);
    final int returnCode = dialog.open();
    if (returnCode == Window.OK) {
      // OK button pressed
      // You can add additional logic here if needed
    }
  }
}
