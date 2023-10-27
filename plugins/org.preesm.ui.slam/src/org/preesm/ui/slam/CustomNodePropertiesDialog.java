package org.preesm.ui.slam;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CustomNodePropertiesDialog extends Dialog {
  public static final String         errorInput = "/!\\ You must enter a strictly positive integer. /!\\";
  private final int                  numNodes;
  private final List<List<CoreInfo>> nodeInfoList;

  public CustomNodePropertiesDialog(Shell parentShell, int numNodes) {
    super(parentShell);
    this.numNodes = numNodes;
    this.nodeInfoList = new ArrayList<>();
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    final Composite container = (Composite) super.createDialogArea(parent);
    final GridLayout layout = new GridLayout(1, false);
    container.setLayout(layout);
    final Label ratelabel = new Label(container, SWT.NONE);
    ratelabel.setText("Enter the internode communication rate");
    final Text rateText = new Text(container, SWT.BORDER);
    rateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    for (int i = 0; i < numNodes; i++) {
      final int nodeIndex = i;
      final Label label = new Label(container, SWT.BOLD);
      label.setText("Node " + i);
      final Label sublabel = new Label(container, SWT.NONE);
      sublabel.setText("Enter the number of cores of node " + i);
      final Text text = new Text(container, SWT.BORDER);
      text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      // final Label subsublabel = new Label(container, SWT.NONE);
      // subsublabel.setText("Enter the intranode communication rate on node " + i);
      // final Text rateText = new Text(container, SWT.BORDER);
      // rateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      final Button button = new Button(container, SWT.PUSH);
      button.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
      button.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      button.setText("Configure Node " + i);
      button.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          if (true) {
            final String nodeCommunicationRate = rateText.getText();
            // Composite childrenContainer = container.get
            configureNode(parent, nodeIndex, Integer.parseInt(text.getText()), nodeCommunicationRate);
          }
        }

      });

    }
    return container;
  }

  private boolean isValidInput(String input) {
    try {
      final int value = Integer.parseInt(input);
      return value > 0;
    } catch (final NumberFormatException e) {
      return false;
    }
  }

  private void configureNode(Composite parent, int nodeIndex, int numCores, String nodeCommunicationRate) {
    final CustomCorePropertiesDialog coreDialog = new CustomCorePropertiesDialog(Display.getCurrent().getActiveShell(),
        nodeIndex, nodeCommunicationRate, numCores);

    if (coreDialog.open() == Window.OK) {
      nodeInfoList.add(coreDialog.getCoreInfoList());
    }
  }

  public List<List<CoreInfo>> getNodeInfoList() {
    return nodeInfoList;
  }
}
