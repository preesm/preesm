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
import org.preesm.algorithm.node.simulator.NetworkInfo;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.ui.PreesmUIPlugin;
import org.preesm.ui.utils.DialogUtil;
import org.preesm.ui.wizards.PreesmProjectNature;

/**
 * Provides commands to generate custom network architectures in a Preesm project. This class extends AbstractHandler to
 * implement the execution logic for the network architecture generation popup.
 *
 * The network architecture generation involves specifying various parameters such as the number of nodes, speed,
 * bandwidth, latency, loopback links, and specific topology options.
 *
 * The generated architecture information is saved to an XML file named "SimSDP_network.xml" in the Archi directory.
 *
 * Usage: Create an instance of ArchitectureSimSDPnetworkGeneratorPopup and call the execute method to trigger the
 * generation popup.
 *
 * @author orenaud
 */
public class ArchitectureGeneratorSimSDPNetworkPopup extends AbstractHandler {

  // Constants
  private String             path;
  public static final String ARCHI_NAME = "SimSDP_network.xml";

  public static final String CLUSTER_CROSSBAR = NetworkInfo.CLUSTER_CROSSBAR;
  public static final String CLUSTER_SHARED   = NetworkInfo.CLUSTER_SHARED;
  public static final String TORUS            = NetworkInfo.TORUS;
  public static final String FAT_TREE         = NetworkInfo.FAT_TREE;
  public static final String DRAGONFLY        = NetworkInfo.DRAGONFLY;

  private static final String TOPO_PARAM_KEY  = "topoparam";
  private static final String TOPO_PARAM_NAME = "topo_parameters";

  /**
   * Executes the network architecture generation logic when the corresponding command is triggered.
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
        openGeneralNetworkPropertiesDialog();
        PreesmLogger.getLogger().log(Level.INFO, "Generate a custom network architecture file.");
      }

    } catch (final Exception e) {
      throw new ExecutionException("Could not generate architecture network.", e);
    }

    return null;
  }

  /**
   * Opens the network properties dialog to gather user input for custom network architecture generation.
   */
  private void openGeneralNetworkPropertiesDialog() {
    final Map<String, String> network = new LinkedHashMap<>();
    final Shell parentShell = DialogUtil.getShell();
    final Shell shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    shell.setText("Generate a custom architecture network file.");
    shell.setLayout(new GridLayout(2, false));
    shell.setSize(400, 400);

    final Text nodeText = createLabelAndText(shell, "Enter the number of nodes:", "1", "");
    final Text speedText = createLabelAndText(shell, "Enter Speed:", "1f", "");
    final Text bandwidthText = createLabelAndText(shell, "Enter bandwidth:", "125MBps", "");
    final Text latencyText = createLabelAndText(shell, "Enter latency:", "50us", "");

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
    dropdown.setItems(CLUSTER_CROSSBAR, CLUSTER_SHARED, TORUS, FAT_TREE, DRAGONFLY);
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

    dropdown.addModifyListener(e -> {
      updateTopoParamInfo(dropdown.getText(), topoLabel, topoText, bbLabel, bbText, nodeText);
      shell.pack();
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
      networkFillDefault(network, nodeText, speedText, bandwidthText, latencyText);

      network.put("loopback", toggleButton.isEnabled() ? "true" : "false");

      network.put("loopbackbandwidth", !loopbackbandwidthText.getText().equals("") ? loopbackbandwidthText.getText()
          : loopbackbandwidthText.getMessage());
      network.put("loopbacklatency",
          !loopbacklatencyText.getText().equals("") ? loopbacklatencyText.getText() : loopbacklatencyText.getMessage());

      network.put("topo", dropdown.getText());
      network.put(TOPO_PARAM_KEY, !topoText.getText().equals("") ? topoText.getText() : topoText.getMessage());
      network.put("bbparam", !bbText.getText().equals("") ? bbText.getText() : bbText.getMessage());

      final StringBuilder content = processXml(network);
      PreesmIOHelper.getInstance().print(path, ARCHI_NAME, content);
      shell.close();
    });

    shell.open();

  }

  /**
   * Fills the provided network map with default values for various parameters.
   *
   * @param network
   *          The network map to fill with default values.
   * @param nodeText
   *          Text widget for entering the number of nodes.
   * @param speedText
   *          Text widget for entering the speed.
   * @param bandwidthText
   *          Text widget for entering the bandwidth.
   * @param latencyText
   *          Text widget for entering the latency.
   */
  private void networkFillDefault(Map<String, String> network, Text nodeText, Text speedText, Text bandwidthText,
      Text latencyText) {
    network.put("node", !nodeText.getText().equals("") ? nodeText.getText() : nodeText.getMessage());
    network.put("speed", !speedText.getText().equals("") ? speedText.getText() : speedText.getMessage());
    network.put("bandwith", !bandwidthText.getText().equals("") ? bandwidthText.getText() : bandwidthText.getMessage());
    network.put("latency", !latencyText.getText().equals("") ? latencyText.getText() : latencyText.getMessage());

  }

  /**
   * Creates a Label and Text widget in the provided shell with the specified label text, message, and default text.
   *
   * @param shell
   *          The shell in which to create the Label and Text widget.
   * @param labelText
   *          The label text for the Label widget.
   * @param message
   *          The message for the Text widget.
   * @param defaultText
   *          The default text for the Text widget.
   * @return The Text widget created.
   */
  private Text createLabelAndText(Shell shell, String labelText, String message, String defaultText) {
    final Label label = new Label(shell, SWT.NONE);
    label.setText(labelText);
    final Text text = new Text(shell, SWT.BORDER);
    text.setMessage(message);
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    text.setText(defaultText);

    return text;
  }

  /**
   * Updates the information related to topology parameters based on the selected option in the dropdown.
   *
   * @param selectedOption
   *          The selected topology option.
   * @param topoLabel
   *          Label widget for displaying the topology parameter label.
   * @param topoText
   *          Text widget for entering the topology parameter.
   * @param bbLabel
   *          Label widget for displaying the backbone parameter label.
   * @param bbText
   *          Text widget for entering the backbone parameter.
   * @param nodeText
   *          Text widget for entering the number of nodes.
   */
  protected void updateTopoParamInfo(String selectedOption, Label topoLabel, Text topoText, Label bbLabel, Text bbText,
      Text nodeText) {
    switch (selectedOption) {
      case CLUSTER_SHARED:
        topoLabel.setText("Enter backbone bandwidth");
        topoText.setMessage("2.25GBps");
        topoText.setVisible(true);
        bbLabel.setText("Enter backbone latency");
        bbLabel.setVisible(true);
        bbText.setMessage("500us");
        bbText.setVisible(true);
        break;

      case TORUS:
        topoLabel.setText("Enter x,y,z size");
        topoText.setMessage("3,2,2");
        topoText.setVisible(true);
        bbLabel.setVisible(false);
        bbText.setVisible(false);
        if (!topoText.getText().equals("")) {
          final String[] xyz = topoText.getText().replace("\"", "").split(",");
          nodeText.setText(String.valueOf(Integer.decode(xyz[0]) * Integer.decode(xyz[1]) * Integer.decode(xyz[2])));
        } else {
          nodeText.setText(String.valueOf(3 * 2 * 2));
        }
        break;

      case FAT_TREE:
        topoLabel.setText("Enter nLevel;nDownlink,...;nUplink,...;//link,... parameter");
        topoText.setMessage("2;2,2;1,1;1,1");
        topoText.setVisible(true);
        bbLabel.setVisible(false);
        bbText.setVisible(false);
        if (!topoText.getText().equals("")) {
          final String[] tree = topoText.getText().replace("\"", "").split("[,;]");
          int nNode = 0;
          for (int i = 0; i < Integer.decode(tree[1]); i++) {
            nNode *= Integer.decode(tree[i + 1]);
          }
          nodeText.setText(String.valueOf(nNode));
        } else {
          nodeText.setText(String.valueOf(2 * 2));
        }
        break;

      case DRAGONFLY:
        topoLabel.setText("Enter nCluster,nLink;nChassis,nLink;nRouter,nlink;nNode parameter");
        topoText.setMessage("2,1;2,1;2,1;1");
        topoText.setVisible(true);
        bbLabel.setVisible(false);
        bbText.setVisible(false);
        if (!topoText.getText().equals("")) {
          final String[] dragon = topoText.getText().replace("\"", "").split("[,;]");
          nodeText.setText(String.valueOf(Integer.decode(dragon[0]) * Integer.decode(dragon[2])
              * Integer.decode(dragon[4]) * Integer.decode(dragon[6])));
        } else {
          nodeText.setText(String.valueOf(2 * 2 * 2 * 1));
        }
        break;

      default:
        topoLabel.setText("");
        topoText.setVisible(false);
        break;
    }

  }

  /**
   * Processes the network information and generates XML content for saving to a file.
   *
   * @param network
   *          The network map containing various parameters.
   * @return The generated XML content.
   */
  private StringBuilder processXml(Map<String, String> network) {
    final StringBuilder content = new StringBuilder();
    content.append("<!-- " + network.get("topo") + ":" + network.get("node") + " -->\n");
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
      case CLUSTER_SHARED:
        content.append("bb_bw=\"" + network.get(TOPO_PARAM_KEY) + "\" bb_lat=\"" + network.get("bbparam") + "\"");
        break;
      case TORUS:
        content.append("topology=\"TORUS\" ");
        content.append(TOPO_PARAM_NAME + "=\"" + network.get(TOPO_PARAM_KEY) + "\"");
        break;
      case FAT_TREE:
        content.append("topology=\"FAT_TREE\" ");
        content.append(TOPO_PARAM_NAME + "=\"" + network.get(TOPO_PARAM_KEY) + "\"");
        break;
      case DRAGONFLY:
        content.append("topology=\"DRAGONFLY\" ");
        content.append(TOPO_PARAM_NAME + "=\"" + network.get(TOPO_PARAM_KEY) + "\"");
        break;
      default:
        break;
    }

    content.append(">\n");
    content.append("<prop id=\"wattage_per_state\" value=\"90.0:90.0:150.0\" />\n");
    content.append("<prop id=\"wattage_range\" value=\"100.0:200.0\" />\n");
    content.append("</cluster>\n");
    content.append("</zone>\n");
    content.append("</platform>\n");
    return content;
  }

}
