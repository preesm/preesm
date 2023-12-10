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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.preesm.ui.utils.DialogUtil;
import org.preesm.ui.wizards.PreesmProjectNature;

/**
 * Provides commands to generate custom network architectures.
 *
 * @author orenaud
 *
 */
public class ArchitectureGeneratorNetworkPopup extends AbstractHandler {

  private String             path;
  public static final String ARCHI_NAME = "SimSDP_network.xml";

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
        openGeneralNetworkPropertiesDialog();
        PreesmLogger.getLogger().log(Level.INFO, "Generate a custom network architecture file.");
      }

    } catch (final Exception e) {
      throw new ExecutionException("Could not generate architecture network.", e);
    }

    return null;
  }

  private void openGeneralNetworkPropertiesDialog() {
    final Map<String, String> network = new LinkedHashMap<>();
    final Shell parentShell = DialogUtil.getShell();
    final Shell shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    shell.setText("Generate a custom architecture network file.");
    shell.setLayout(new GridLayout(2, false));
    shell.setSize(400, 400);

    final Label nodeLabel = new Label(shell, SWT.NONE);
    nodeLabel.setText("Enter the number of nodes:");

    final Text nodeText = new Text(shell, SWT.BORDER);
    nodeText.setMessage("1");
    nodeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label speedLabel = new Label(shell, SWT.NONE);
    speedLabel.setText("Enter Speed:");

    final Text speedText = new Text(shell, SWT.BORDER);
    speedText.setMessage("1f");
    speedText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label bandwidthLabel = new Label(shell, SWT.NONE);
    bandwidthLabel.setText("Enter bandwidth:");

    final Text bandwidthText = new Text(shell, SWT.BORDER);
    bandwidthText.setMessage("125MBps");
    bandwidthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label latencyLabel = new Label(shell, SWT.NONE);
    latencyLabel.setText("Enter latency:");

    final Text latencyText = new Text(shell, SWT.BORDER);
    latencyText.setMessage("50us");
    latencyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Button toggleButton = new Button(shell, SWT.CHECK);
    toggleButton.setText("Loopback links");

    // empty line
    new Label(shell, SWT.NONE);

    final Label loopbackbandwidthLabel = new Label(shell, SWT.NONE);
    loopbackbandwidthLabel.setText("Enter loopback bandwidth:");
    loopbackbandwidthLabel.setVisible(false);

    final Text loopbackbandwidthText = new Text(shell, SWT.BORDER);
    loopbackbandwidthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    loopbackbandwidthText.setMessage("100MBps");
    loopbackbandwidthText.setVisible(false);

    final Label loopbacklatencyLabel = new Label(shell, SWT.NONE);
    loopbacklatencyLabel.setText("Enter loopback latency:");
    loopbacklatencyLabel.setVisible(false);

    final Text loopbacklatencyText = new Text(shell, SWT.BORDER);
    loopbacklatencyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    loopbacklatencyText.setMessage("0us");
    loopbacklatencyText.setVisible(false);

    toggleButton.addListener(SWT.Selection, event -> {
      final boolean isChecked = toggleButton.getSelection();

      loopbackbandwidthLabel.setVisible(isChecked);
      loopbackbandwidthText.setVisible(isChecked);
      loopbacklatencyLabel.setVisible(isChecked);
      loopbacklatencyText.setVisible(isChecked);

    });

    final Label label = new Label(shell, SWT.NONE);
    label.setText("Select Option:");

    final Combo dropdown = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
    dropdown.setItems("Cluster with crossbar", "Cluster with shared backbone", "Torus cluster", "Fat-tree cluster",
        "Dragonfly cluster");
    dropdown.select(0);
    dropdown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    final Label topoLabel = new Label(shell, SWT.NONE);
    topoLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    final Text topoText = new Text(shell, SWT.BORDER);
    topoText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    topoText.setVisible(false);

    final Label bbLabel = new Label(shell, SWT.NONE);
    bbLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    bbLabel.setVisible(false);
    final Text bbText = new Text(shell, SWT.BORDER);
    bbText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    bbText.setVisible(false);

    dropdown.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        // Mettre à jour le sous-texte en fonction de l'option sélectionnée
        final String selectedOption = dropdown.getText();
        switch (selectedOption) {
          case "Cluster with shared backbone":
            topoLabel.setText("Enter backbone bandwidth");
            topoText.setMessage("2.25GBps");
            topoText.setVisible(true);
            bbLabel.setText("Enter backbone latency");
            bbLabel.setVisible(true);
            bbText.setMessage("500us");
            bbText.setVisible(true);
            break;
          case "Torus cluster":
            topoLabel.setText("Enter x,y,z size");
            topoText.setMessage("3,2,2");
            topoText.setVisible(true);
            bbLabel.setVisible(false);
            bbText.setVisible(false);
            if (!topoText.getText().equals("")) {
              final String[] xyz = topoText.getText().replaceAll("\"", "").split(",");

              nodeText
                  .setText(String.valueOf(Integer.decode(xyz[0]) * Integer.decode(xyz[1]) * Integer.decode(xyz[2])));

            } else {
              nodeText.setText(String.valueOf(3 * 2 * 2));
            }
            break;
          case "Fat-tree cluster":
            topoLabel.setText("Enter nLevel;nDownlink,...;nUplink,...;//link,... parameter");
            topoText.setMessage("2;2,2;1,1;1,1");
            topoText.setVisible(true);
            bbLabel.setVisible(false);
            bbText.setVisible(false);
            if (!topoText.getText().equals("")) {
              final String[] tree = topoText.getText().replaceAll("\"", "").split("[,;]");
              int nNode = 0;
              for (int i = 0; i < Integer.decode(tree[1]); i++) {
                nNode *= Integer.decode(tree[i + 1]);
              }
              nodeText.setText(String.valueOf(nNode));
            } else {
              nodeText.setText(String.valueOf(2 * 2));
            }
            break;
          case "Dragonfly cluster":
            topoLabel.setText("Enter nCluster,nLink;nChassis,nLink;nRouter,nlink;nNode parameter");
            topoText.setMessage("2,1;2,1;2,1;1");
            topoText.setVisible(true);
            bbLabel.setVisible(false);
            bbText.setVisible(false);
            if (!topoText.getText().equals("")) {
              final String[] dragon = topoText.getText().replaceAll("\"", "").split("[,;]");
              nodeText.setText(String.valueOf(Integer.decode(dragon[0]) * Integer.decode(dragon[2])
                  * Integer.decode(dragon[4]) * Integer.decode(dragon[6])));
            } else {
              nodeText.setText(String.valueOf(2 * 2 * 2 * 1));
            }
            break;
          default:
            topoLabel.setText(""); // Aucun sous-texte pour l'option par défaut
            topoText.setVisible(false);
            break;

        }
        shell.pack();
      }
    });
    // empty line
    new Label(shell, SWT.NONE);
    new Label(shell, SWT.NONE);

    final Button cancelButton = new Button(shell, SWT.PUSH);
    cancelButton.setText("Cancel");
    cancelButton.addListener(SWT.Selection, event -> shell.close());

    final Button okButton = new Button(shell, SWT.PUSH);

    okButton.setText("OK");
    okButton.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
    okButton.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

    okButton.addListener(SWT.Selection, event -> {
      if (!nodeText.getText().equals("")) {
        network.put("node", nodeText.getText());
      } else {
        network.put("node", nodeText.getMessage());
      }
      if (!speedText.getText().equals("")) {
        network.put("speed", speedText.getText());
      } else {
        network.put("speed", speedText.getMessage());
      }
      if (!bandwidthText.getText().equals("")) {
        network.put("bandwith", bandwidthText.getText());
      } else {
        network.put("bandwith", bandwidthText.getMessage());
      }
      if (!latencyText.getText().equals("")) {
        network.put("latency", latencyText.getText());
      } else {
        network.put("latency", latencyText.getMessage());
      }

      if (toggleButton.isEnabled()) {
        network.put("loopback", "true");
      } else {
        network.put("loopback", "false");
      }

      if (!loopbackbandwidthText.getText().equals("")) {
        network.put("loopbackbandwidth", loopbackbandwidthText.getText());
      } else {
        network.put("loopbackbandwidth", loopbackbandwidthText.getMessage());
      }
      if (!loopbacklatencyText.getText().equals("")) {
        network.put("loopbacklatency", loopbacklatencyText.getText());
      } else {
        network.put("loopbacklatency", loopbacklatencyText.getMessage());
      }

      network.put("topo", dropdown.getText());

      if (!topoText.getText().equals("")) {
        network.put("topoparam", topoText.getText());
      } else {
        network.put("topoparam", topoText.getMessage());
      }
      if (!bbText.getText().equals("")) {
        network.put("bbparam", bbText.getText());
      } else {
        network.put("bbparam", bbText.getMessage());
      }

      final StringConcatenation content = processXml(network);
      PreesmIOHelper.getInstance().print(path, ARCHI_NAME, content);
      shell.close();
    });

    shell.open();

  }

  private StringConcatenation processXml(Map<String, String> network) {
    final StringConcatenation content = new StringConcatenation();
    content.append("<?xml version='1.0'?>\n");
    content.append("<!DOCTYPE platform SYSTEM \"https://simgrid.org/simgrid.dtd\">\n");
    content.append("<platform version=\"4.1\">\n");
    content.append("<zone id=\"my zone\" routing=\"Floyd\">\n");
    content.append("<cluster id=\"" + network.get("topo") + "\" ");
    content.append("prefix=\"Node\" radical=\"0-" + (Integer.decode(network.get("node")) - 1) + "\" suffix=\"\"");
    content.append("speed=\"" + network.get("speed") + "\" ");
    content.append("bw=\"" + network.get("bandwith") + "\" ");
    content.append("lat=\"" + network.get("latency") + "\" ");

    if (network.get("loopback").equals("true")) {
      content.append("loopback_bw=\"" + network.get("loopbackbandwidth") + "\" ");
      content.append("loopback_lat=\"" + network.get("loopbacklatency") + "\" ");
    }

    final String selectedOption = network.get("topo");
    switch (selectedOption) {
      case "Cluster with shared backbone":
        content.append("bb_bw=\"" + network.get("topoparam") + "\" bb_lat=\"" + network.get("bbparam") + "\">\n");
        break;
      case "Torus cluster":
        content.append("topology=\"TORUS\" ");
        content.append("topo_parameters=\"" + network.get("topoparam") + "\">\n");
        break;
      case "Fat-tree cluster":
        content.append("topology=\"FAT_TREE\" ");
        content.append("topo_parameters=\"" + network.get("topoparam") + "\">\n");
        break;
      case "Dragonfly cluster":
        content.append("topology=\"DRAGONFLY\" ");
        content.append("topo_parameters=\"" + network.get("topoparam") + "\">\n");
        break;
      default:
        content.append(">\n");
        break;
    }
    content.append("<prop id=\"wattage_per_state\" value=\"90.0:90.0:150.0\" />\n");
    content.append("<prop id=\"wattage_range\" value=\"100.0:200.0\" />\n");
    content.append("</cluster>\n");
    content.append("</zone>\n");
    content.append("</platform>\n");
    return content;
  }

}
