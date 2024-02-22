/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.ui.slam.popup.actions;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.slam.CoreInfo;
import org.preesm.ui.utils.DialogUtil;
import org.preesm.ui.wizards.PreesmProjectNature;

/**
 * This class provides commands to generate custom multinode architectures in a Preesm project. It extends
 * AbstractHandler to implement the execution logic for the architecture generation popup.
 *
 * The architecture generation involves specifying the number of nodes, internode communication rate, and customizing
 * each node and its cores, including core communication rate, frequency, and type.
 *
 * The generated architecture information is saved to a CSV file named "SimSDP_node.csv" in the Archi directory.
 *
 * @author orenaud
 */
public class ArchitectureGeneratorSimSDPNodePopup extends AbstractHandler {
  // Constants
  String                     path       = "";
  public static final String ARCHI_NAME = "SimSDP_node.csv";

  // Data structures for storing architecture information
  final Map<Integer, Map<Integer, CoreInfo>> nodeInfoList        = new LinkedHashMap<>();
  private CoreInfo                           activeCoreInfo      = new CoreInfo("", "", "", "", "", "", "");
  Integer                                    activeNodeId        = 0;
  Integer                                    activeCoreId        = 0;
  String                                     internodeRate       = "0";
  String                                     activeIntranodeRate = "0";

  /**
   * Executes the architecture generation logic when the corresponding command is triggered.
   *
   * @param event
   *          The execution event triggering the command.
   * @return null, as no specific return value is expected.
   * @throws ExecutionException
   *           If an exception occurs during execution.
   */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      // Get the selected IProject
      final IWorkbenchPage page = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
      final TreeSelection selection = (TreeSelection) page.getSelection();
      final IProject project = (IProject) selection.getFirstElement();
      path = File.separator + project.getName() + "/Archi/";
      // If it is a Preesm project, generate default design in Archi/ folder
      if (project.hasNature(PreesmProjectNature.ID)) {
        openMutinodePropertiesDialog();
        PreesmLogger.getLogger().log(Level.INFO, "Generate a custom multinode architecture file.");
      }

    } catch (final Exception e) {
      throw new ExecutionException("Could not generate SimSDP architecture.", e);
    }
    return null;
  }

  /**
   * Opens the architecture properties dialog to gather user input for custom multinode architecture generation.
   */
  private void openMutinodePropertiesDialog() {

    final Shell parentShell = DialogUtil.getShell();
    final Shell shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    shell.setText("Generate a custom architecture network file.");
    shell.setLayout(new GridLayout(2, false));
    shell.setSize(400, 600);

    final Label nodeLabel = new Label(shell, SWT.NONE);
    nodeLabel.setText("Enter the number of nodes:");

    final Text nodeText = new Text(shell, SWT.BORDER);
    nodeText.setMessage("3");// display example
    nodeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label ratelabel = new Label(shell, SWT.NONE);
    ratelabel.setText("Enter the internode communication rate:");

    final Text nodeRateText = new Text(shell, SWT.BORDER);
    nodeRateText.setMessage("9.42");
    nodeRateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nodeRateText.addModifyListener(e -> internodeRate = nodeRateText.getText());

    final Label nodeCombolabel = new Label(shell, SWT.NONE);
    nodeCombolabel.setText("Customize the selected node:");

    final Combo nodeCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
    final GridData comboData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    comboData.horizontalSpan = 1;
    nodeCombo.setLayoutData(comboData);
    nodeText.addModifyListener(e -> updateComboItems(nodeCombo, nodeText.getText()));
    nodeCombo.addModifyListener(e -> initNodeInfo(nodeCombo));

    final Label coreLabel = new Label(shell, SWT.NONE);
    coreLabel.setText("Enter the number of cores:");

    final Text coreText = new Text(shell, SWT.BORDER);
    coreText.setMessage("2");
    coreText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label rateCoreLabel = new Label(shell, SWT.NONE);
    rateCoreLabel.setText("Enter the core communication rate ");

    final Text coreRateText = new Text(shell, SWT.BORDER);
    coreRateText.setMessage(activeCoreInfo.getCoreCommunicationRate());
    coreRateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    coreRateText.addModifyListener(e -> activeCoreInfo.setCoreCommunicationRate(coreRateText.getText()));
    coreRateText.addModifyListener(e -> activeIntranodeRate = coreRateText.getText());

    final Label coreCombolabel = new Label(shell, SWT.NONE);
    coreCombolabel.setText("Customize the selected core:");
    final Combo coreCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
    final GridData coreData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    coreData.horizontalSpan = 1;
    coreCombo.setLayoutData(coreData);
    coreText.addModifyListener(e -> updateCoreComboItems(coreCombo, coreText.getText()));
    coreCombo.addModifyListener(e -> initCoreInfo(nodeCombo, coreCombo));

    final Label freqLabel = new Label(shell, SWT.NONE);
    freqLabel.setText("Enter the core frequency ");
    final Text freqText = new Text(shell, SWT.BORDER);
    freqText.setMessage(activeCoreInfo.getCoreFrequency());
    freqText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    freqText.addModifyListener(e -> activeCoreInfo.setCoreFrequency(freqText.getText()));

    final Label typeLabel = new Label(shell, SWT.NONE);
    typeLabel.setText("Enter the type of core ");
    final Combo comboType = new Combo(shell, SWT.READ_ONLY);
    comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    // Add options
    comboType.add("x86");
    comboType.add("CC66");
    comboType.add("FPGA");
    if (!nodeCombo.getText().isEmpty()) {
      comboType.addModifyListener(e -> activeCoreInfo.setCoreType(comboType.getText()));
    }
    final Button cancelButton = new Button(shell, SWT.PUSH);
    cancelButton.setText("Cancel");
    cancelButton.addListener(SWT.Selection, event -> shell.close());

    final Button okButton = new Button(shell, SWT.PUSH);
    okButton.setText("OK");
    okButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
    okButton.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    okButton.addListener(SWT.Selection, event -> {
      final StringBuilder content = processCsv(nodeInfoList);
      PreesmIOHelper.getInstance().print(path, ARCHI_NAME, content);
      shell.close();
    });
    shell.pack();

    shell.open();

  }

  /**
   * Initializes core information based on user input from the architecture properties dialog.
   *
   * @param nodeCombo
   *          The Combo box for selecting nodes.
   * @param coreCombo
   *          The Combo box for selecting cores.
   */
  protected void initCoreInfo(Combo nodeCombo, Combo coreCombo) {
    activeCoreId = Integer.parseInt(coreCombo.getText().replace("Core", ""));

    if (!nodeInfoList.containsKey(activeNodeId) && nodeInfoList.get(activeNodeId).containsKey(activeCoreId)) {
      activeCoreInfo = nodeInfoList.get(activeNodeId).get(activeCoreId);

    } else {
      final CoreInfo newCoreInfo = new CoreInfo(nodeCombo.getText(), "9.42", "Core" + activeCoreId,
          String.valueOf(activeCoreId), "2000", activeIntranodeRate, "x86");
      nodeInfoList.get(activeNodeId).put(activeCoreId, newCoreInfo);
      activeCoreInfo = newCoreInfo;
    }

  }

  /**
   * Initializes node information based on user input from the architecture properties dialog.
   *
   * @param nodeCombo
   *          The Combo box for selecting nodes.
   */
  protected void initNodeInfo(Combo nodeCombo) {
    if (!nodeCombo.getText().isEmpty()) {
      activeNodeId = Integer.parseInt(nodeCombo.getText().replace("Node", ""));

      if (nodeInfoList.containsKey(activeNodeId)) {
        activeCoreInfo = nodeInfoList.get(activeNodeId).get(0);

      } else {
        final CoreInfo newCoreInfo = new CoreInfo(nodeCombo.getText(), "9.42", "Core0", "0", "2000", "472.0", "x86");
        final Map<Integer, CoreInfo> newMap = new LinkedHashMap<>();
        newMap.put(0, newCoreInfo);
        nodeInfoList.put(activeNodeId, newMap);
        activeCoreInfo = newCoreInfo;
      }
    }

  }

  /**
   * Updates the items in the core Combo box based on the number of cores specified.
   *
   * @param combo
   *          The Combo box for selecting cores.
   * @param text
   *          The user-specified number of cores.
   */
  protected void updateCoreComboItems(Combo combo, String text) {
    try {
      final int numberOfCores = Integer.parseInt(text);
      combo.removeAll();
      for (int i = 0; i < numberOfCores; i++) {
        combo.add("Core" + i);
      }
      combo.select(0);
    } catch (final NumberFormatException e) {
      combo.removeAll();
    }
  }

  /**
   * Updates the items in the node Combo box based on the number of nodes specified.
   *
   * @param combo
   *          The Combo box for selecting nodes.
   * @param text
   *          The user-specified number of nodes.
   */
  protected void updateComboItems(Combo combo, String text) {
    try {
      final int numberOfNodes = Integer.parseInt(text);
      combo.removeAll();
      for (int i = 0; i < numberOfNodes; i++) {
        combo.add("Node" + i);
      }
      combo.select(0);
    } catch (final NumberFormatException e) {
      combo.removeAll();
    }
  }

  /**
   * Processes the architecture information and generates a CSV content for saving to a file.
   *
   * @param nodeInfoList2
   *          The map containing architecture information.
   * @return The generated CSV content.
   */
  private StringBuilder processCsv(Map<Integer, Map<Integer, CoreInfo>> nodeInfoList2) {
    final StringBuilder content = new StringBuilder();
    content.append("Node name;Core ID;Core frequency;Intranode rate;Internode rate\n");
    for (final Entry<Integer, Map<Integer, CoreInfo>> node : nodeInfoList2.entrySet()) {
      for (final Entry<Integer, CoreInfo> core : node.getValue().entrySet()) {
        final CoreInfo coreInfo = core.getValue();
        content.append(coreInfo.getNodeName() + ";" + coreInfo.getCoreID() + ";" + coreInfo.getCoreFrequency() + ";"
            + coreInfo.getCoreCommunicationRate() + ";" + internodeRate + "\n");
      }
    }
    return content;
  }

}