package org.ietr.workflow.hypervisor;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;

/**
 * This class represents the SimSDP nodes and exports their information to a CSV file. It includes details such as node
 * name, core ID, core frequency, intranode rate, and internode rate.
 *
 * @author orenaud
 */
public class SimSDPnode {
  int    nodeNum;     // Number of nodes
  int    coreNum;     // Number of cores per node
  int    coreFreq;    // Core frequency
  String projectPath; // Project path

  /**
   * Constructor for SimSDPnode.
   *
   * @param nodeNum
   *          Number of nodes.
   * @param coreNum
   *          Number of cores per node.
   * @param coreFreq
   *          Core frequency.
   * @param projectPath
   *          Project path.
   */
  public SimSDPnode(int nodeNum, int coreNum, int coreFreq, String projectPath) {
    this.nodeNum = nodeNum;
    this.coreNum = coreNum;
    this.coreFreq = coreFreq;
    this.projectPath = projectPath;
  }

  /**
   * Execute the process to generate and export CSV content.
   */
  public void execute() {
    final String content = processCSV();
    PreesmIOHelper.getInstance().print(projectPath + "/Archi/", "SimSDP_node.csv", content);

  }

  /**
   * Process CSV content with information about SimSDP nodes.
   *
   * @return CSV content as a string.
   */
  private String processCSV() {
    final StringConcatenation content = new StringConcatenation();
    content.append("Node name;Core ID;Core frequency;Intranode rate;Internode rate\n");
    int cc = 0;
    int ccc = 0;
    for (int n = 0; n < nodeNum; n++) {
      for (int c = 0; c < coreNum; c++) {
        content.append("Node" + n + ";" + (c + n * coreNum) + ";" + coreFreq + ";" + "500.0;10.0 \n");
        cc = c;
      }
      ccc = cc;
    }
    return content.toString();
  }

}
