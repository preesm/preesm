package org.preesm.ui.slam;

import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.ui.utils.DialogUtil;

public class NbNodesValidator implements IInputValidator {
  private static final String INPUT_ERROR = "/!\\ You must enter a strictly positive integer. /!\\";
  private static final String ARCHI_NAME  = "SimSDP_archi.csv";
  String                      path        = "";

  public NbNodesValidator(IProject project) {
    path = "/" + project.getName() + "/Archi/";
  }

  @Override
  public String isValid(String newText) {
    if (newText == null || newText.isEmpty()) {
      return null;
    }
    try {
      final int a = Integer.parseInt(newText);
      if (a < 1) {
        return INPUT_ERROR;
      }
      openNodePropertiesDialog(a);

    } catch (final NumberFormatException e) {
      return INPUT_ERROR;
    }
    return null;
  }

  private void openNodePropertiesDialog(int numNodes) {
    final Shell shell = DialogUtil.getShell();
    final CustomNodePropertiesDialog dialog = new CustomNodePropertiesDialog(shell, numNodes);
    final int returnCode = dialog.open();
    if (returnCode == Window.OK) {
      final List<List<CoreInfo>> nodeInfoList = dialog.getNodeInfoList();
      final StringConcatenation content = processNodeInfoList(nodeInfoList);
      PreesmIOHelper.getInstance().print(path, ARCHI_NAME, content);
      dialog.close();
    }
  }

  private StringConcatenation processNodeInfoList(List<List<CoreInfo>> nodeInfoList) {
    final StringConcatenation content = new StringConcatenation();
    for (final List<CoreInfo> node : nodeInfoList) {
      for (final CoreInfo core : node) {
        content.append(core.getNodeName() + "," + core.getCoreID() + "," + core.getCoreFrequency() + ","
            + core.getCoreCommunicationRate() + "," + core.getNodeCommunicationRate() + "\n");
      }
    }
    return content;
  }
}
