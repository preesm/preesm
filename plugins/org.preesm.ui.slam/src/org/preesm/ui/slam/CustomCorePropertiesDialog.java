package org.preesm.ui.slam;

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
  private final int nodeIndex;
  private final int numCores;

  public CustomCorePropertiesDialog(Shell parentShell, int nodeIndex, int numCores) {
    super(parentShell);
    this.nodeIndex = nodeIndex;
    this.numCores = numCores;
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    final Composite container = (Composite) super.createDialogArea(parent);
    final GridLayout layout = new GridLayout(1, false);
    container.setLayout(layout);

    final Label nodeLabel = new Label(container, SWT.BOLD);
    nodeLabel.setText("Node " + nodeIndex);

    for (int i = 0; i < numCores; i++) {
      final Label coreLabel = new Label(container, SWT.BOLD);
      coreLabel.setText("Core " + i);
      final Label freqLabel = new Label(container, SWT.NONE);
      freqLabel.setText("Enter the core frequency ");
      final Text freqText = new Text(container, SWT.BORDER);
      freqText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      final Label typeLabel = new Label(container, SWT.NONE);
      typeLabel.setText("Enter the type of core ");
      final Combo combo = new Combo(container, SWT.READ_ONLY);
      combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      // Add options
      combo.add("x86");
      combo.add("CC66");
      combo.add("FPGA");
    }

    return container;
  }
}
