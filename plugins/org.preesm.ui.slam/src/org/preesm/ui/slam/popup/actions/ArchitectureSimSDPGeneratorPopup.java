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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.slam.CoreInfo;
import org.preesm.ui.utils.DialogUtil;
import org.preesm.ui.wizards.PreesmProjectNature;

/**
 * Provides commands to generate custom multinode architectures.
 *
 * @author orenaud
 *
 */
public class ArchitectureSimSDPGeneratorPopup extends AbstractHandler {
  String                     path       = "";
  public static final String ARCHI_NAME = "SimSDP_archi.csv";

  final Map<Integer, List<CoreInfo>> nodeInfoList = new LinkedHashMap<>();
  private List<CoreInfo>             coreInfoList = new ArrayList<>();
  CoreInfo                           coreInfo     = new CoreInfo("", "", "", "", "", "", "");

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      // Get the selected IProject
      final IWorkbenchPage page = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
      final TreeSelection selection = (TreeSelection) page.getSelection();
      final IProject project = (IProject) selection.getFirstElement();
      path = "/" + project.getName() + "/Archi/";
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

  private void openMutinodePropertiesDialog() {

    final Shell parentShell = DialogUtil.getShell();
    final Shell shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    shell.setText("Generate a custom architecture network file.");
    shell.setLayout(new GridLayout(2, false));
    shell.setSize(400, 600);

    final Label nodeLabel = new Label(shell, SWT.NONE);
    nodeLabel.setText("Enter the number of nodes:");

    final Text nodeText = new Text(shell, SWT.BORDER);
    nodeText.setMessage("3");
    nodeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label ratelabel = new Label(shell, SWT.NONE);
    ratelabel.setText("Enter the internode communication rate:");

    final Text nodeRateText = new Text(shell, SWT.BORDER);
    nodeRateText.setMessage("9.42");
    nodeRateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    nodeRateText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        coreInfo.setNodeCommunicationRate(nodeRateText.getText());
      }
    });

    final Label nodeCombolabel = new Label(shell, SWT.NONE);
    nodeCombolabel.setText("Customize the selected node:");

    final Combo nodeCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
    final GridData comboData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    comboData.horizontalSpan = 1;
    nodeCombo.setLayoutData(comboData);

    nodeText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        updateComboItems(nodeCombo, nodeText.getText());
        for (int i = 0; i < Integer.valueOf(nodeText.getText()); i++) {
          final CoreInfo newCoreInfo = new CoreInfo("Node" + i, "9.42", "Core0", "0", "2000", "472.0", "x86");
          final List<CoreInfo> newCoreInfoList = new ArrayList<>();
          newCoreInfoList.add(newCoreInfo);
          nodeInfoList.put(i, newCoreInfoList);
        }
        coreInfoList = nodeInfoList.get(0);
        coreInfo = coreInfoList.get(0);
      }
    });
    if (!nodeCombo.getText().isEmpty()) {
      final int nodeIndex = Integer.parseInt(nodeCombo.getText().replace("Node", ""));

      nodeCombo.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          coreInfoList = nodeInfoList.get(nodeIndex);
          coreInfo = coreInfoList.get(0);

        }
      });
    }
    final Label coreLabel = new Label(shell, SWT.NONE);
    coreLabel.setText("Enter the number of cores:");

    final Text coreText = new Text(shell, SWT.BORDER);
    coreText.setMessage(String.valueOf(coreInfoList.size()));
    coreText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label rateCoreLabel = new Label(shell, SWT.NONE);
    rateCoreLabel.setText("Enter the core communication rate ");

    final Text coreRateText = new Text(shell, SWT.BORDER);
    coreRateText.setMessage(coreInfo.getCoreCommunicationRate());
    coreRateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    coreRateText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        coreInfo.setCoreCommunicationRate(coreRateText.getText());
      }
    });

    final Label coreCombolabel = new Label(shell, SWT.NONE);
    coreCombolabel.setText("Customize the selected core:");
    final Combo coreCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
    final GridData coreData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    coreData.horizontalSpan = 1;
    coreCombo.setLayoutData(coreData);

    coreText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        updateCoreComboItems(coreCombo, coreText.getText());
        for (int i = 0; i < Integer.valueOf(coreText.getText()); i++) {
          final CoreInfo newCoreInfo = new CoreInfo(nodeCombo.getText(), nodeRateText.getText(), "Core" + i,
              String.valueOf(i), "2000", "472", "x86");
          final int nodeIndex = Integer.parseInt(nodeCombo.getText().replace("Node", ""));
          nodeInfoList.get(nodeIndex).add(newCoreInfo);
        }
      }
    });

    final Label freqLabel = new Label(shell, SWT.NONE);
    freqLabel.setText("Enter the core frequency ");
    final Text freqText = new Text(shell, SWT.BORDER);
    freqText.setMessage(coreInfo.getCoreFrequency());
    freqText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    freqText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        coreInfo.setCoreFrequency(freqText.getText());
      }
    });

    final Label typeLabel = new Label(shell, SWT.NONE);
    typeLabel.setText("Enter the type of core ");
    final Combo comboType = new Combo(shell, SWT.READ_ONLY);

    comboType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    coreCombo.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {

        final int i = 0;
      }
    });

    // Add options
    comboType.add("x86");
    comboType.add("CC66");
    comboType.add("FPGA");
    if (!nodeCombo.getText().isEmpty()) {
      comboType.addModifyListener(e -> {
        coreInfo.setCoreType(comboType.getText());

        // final CoreInfo coreInfoUpdate = new CoreInfo(nodeCombo.getText(), nodeRateText.getText(),
        // coreCombo.getText(),
        // coreCombo.getText().replace("Core", ""), coreRateText.getText(), freqText.getText(), comboType.getText());
        //
        // coreInfoList = nodeInfoList.get(nodeIndex);
        //
        // boolean coreExists = false;
        // for (int i = 0; i < coreInfoList.size(); i++) {
        // if (coreInfoList.get(i).getCoreName().equals(coreInfoUpdate.getCoreName())) {
        // coreInfoList.set(i, coreInfoUpdate);
        // coreExists = true;
        // break;
        // }
        // }
        // if (!coreExists) {
        // coreInfoList.add(coreInfoUpdate);
        // }
      });
    }
    final Button cancelButton = new Button(shell, SWT.PUSH);
    cancelButton.setText("Cancel");
    cancelButton.addListener(SWT.Selection, event -> shell.close());

    final Button okButton = new Button(shell, SWT.PUSH);
    okButton.setText("OK");
    okButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
    okButton.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    okButton.addListener(SWT.Selection, event -> {
      final StringConcatenation content = processCsv(nodeInfoList);
      PreesmIOHelper.getInstance().print(path, ARCHI_NAME, content);
      shell.close();
    });
    shell.pack();

    shell.open();

  }

  protected void updateCoreComboItems(Combo combo, String text) {
    try {
      final int numberOfCodes = Integer.parseInt(text);
      combo.removeAll();
      for (int i = 0; i < numberOfCodes; i++) {
        combo.add("Core" + i);
      }
      combo.select(0);
    } catch (final NumberFormatException e) {
      combo.removeAll();
    }
  }

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

  private StringConcatenation processCsv(Map<Integer, List<CoreInfo>> nodeInfoList2) {
    final StringConcatenation content = new StringConcatenation();
    for (final Entry<Integer, List<CoreInfo>> node : nodeInfoList2.entrySet()) {
      for (final CoreInfo core : node.getValue()) {
        content.append(core.getNodeName() + "," + core.getCoreID() + "," + core.getCoreFrequency() + ","
            + core.getCoreCommunicationRate() + "," + core.getNodeCommunicationRate() + "\n");
      }
    }
    return content;
  }

}
