package org.preesm.ui.slam;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CustomCorePropertiesDialog extends Dialog {
  private final int            nodeIndex;
  private final int            numCores;
  private final List<CoreInfo> coreInfoList;
  String                       nodeCommunicationRate;

  public CustomCorePropertiesDialog(Shell parentShell, int nodeIndex, String nodeCommunicationRate, int numCores) {
    super(parentShell);
    this.nodeIndex = nodeIndex;
    this.nodeCommunicationRate = nodeCommunicationRate;
    this.numCores = numCores;
    this.coreInfoList = new ArrayList<>();
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    final Composite container = (Composite) super.createDialogArea(parent);
    final GridLayout layout = new GridLayout(1, false);
    container.setLayout(layout);

    final Label nodeLabel = new Label(container, SWT.BOLD);
    nodeLabel.setText("Node " + nodeIndex);

    for (int i = 0; i < numCores; i++) {
      final int index = i;
      final Label coreLabel = new Label(container, SWT.BOLD);
      coreLabel.setText("Core " + i);
      final Label freqLabel = new Label(container, SWT.NONE);
      freqLabel.setText("Enter the core frequency ");
      final Text freqText = new Text(container, SWT.BORDER);
      freqText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      final Label rateLabel = new Label(container, SWT.NONE);
      rateLabel.setText("Enter the core communication rate ");
      final Text rateText = new Text(container, SWT.BORDER);
      rateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      final Label typeLabel = new Label(container, SWT.NONE);
      typeLabel.setText("Enter the type of core ");
      final Combo combo = new Combo(container, SWT.READ_ONLY);
      combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      // Add options
      combo.add("x86");
      combo.add("CC66");
      combo.add("FPGA");
      combo.addModifyListener(e -> {
        final String nodeName = "Node" + nodeIndex;
        // final String nodeCommunicationRate = nodeCommunicationRate;
        final String coreName = "Core " + index;
        final String coreCommunicationRate = rateText.getText();
        final String coreFrequency = freqText.getText();
        final String coreType = combo.getText();
        final CoreInfo coreInfo = new CoreInfo(nodeName, nodeCommunicationRate, coreName, String.valueOf(index),
            coreCommunicationRate, coreFrequency, coreType);
        coreInfoList.add(coreInfo);
      });
    }

    return container;
  }

  public List<CoreInfo> getCoreInfoList() {
    return coreInfoList;
  }
}
