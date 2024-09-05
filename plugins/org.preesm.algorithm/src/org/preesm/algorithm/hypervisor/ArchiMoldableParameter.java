package org.preesm.algorithm.hypervisor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;

public class ArchiMoldableParameter {

  String                     projectPath;
  Boolean                    multinet;
  public static final String MP_NAME = "SimSDP_moldable.csv";

  private int nodeMin  = 1;
  private int nodeMax  = 0;
  private int nodeStep = 1;

  private int coreMin  = 1;
  private int coreMax  = 1;
  private int coreStep = 1;

  private int coreFreqMin  = 1;
  private int coreFreqMax  = 1;
  private int coreFreqStep = 1;

  int topoMin  = 1;
  int topoMax  = 1;
  int topoStep = 1;

  long nodeMemMin  = 0;
  long nodeMemMax  = 0;
  long nodeMemStep = 1;

  public ArchiMoldableParameter(String projectPath, Boolean multinet) {
    this.projectPath = projectPath;
    this.multinet = multinet;
  }

  public void execute() {
    if (Boolean.TRUE.equals(multinet)) {
      final String content = PreesmIOHelper.getInstance().read(projectPath + "/Archi/", MP_NAME);

      final String[] line = content.split("\n");
      if (!line[0].equals("Parameters;min;max;step")) {
        PreesmLogger.getLogger().log(Level.SEVERE, "Missing the first line: Parameters;min;max;step");
      }
      for (int i = 1; i < line.length; i++) {
        final String[] column = line[i].split(";");
        switch (column[0]) {
          case "number of nodes":
            nodeMin = Integer.valueOf(column[1]);
            nodeMax = Integer.valueOf(column[2]);
            nodeStep = Integer.valueOf(column[3]);
            break;
          case "number of cores":
            coreMin = Integer.valueOf(column[1]);
            coreMax = Integer.valueOf(column[2]);
            coreStep = Integer.valueOf(column[3]);
            break;
          case "core frequency":
            coreFreqMin = Integer.valueOf(column[1]);
            coreFreqMax = Integer.valueOf(column[2]);
            coreFreqStep = Integer.valueOf(column[3]);
            break;
          case "network topology":
            topoMin = Integer.valueOf(column[1]);
            topoMax = Integer.valueOf(column[2]);
            topoStep = Integer.valueOf(column[3]);
            break;
          case "node memory":
            nodeMemMin = Long.valueOf(column[1]);
            nodeMemMax = Long.valueOf(column[2]);
            nodeMemStep = Long.valueOf(column[3]);
            break;
          default:
            break;
        }
      }

    } else {
      nodeMin = singleNetInitNode(projectPath);
      nodeMax = nodeMin;
    }
  }

  public void refine(long initMemory) {
    final int minimalNodeRequired = (int) Math.ceil((double) initMemory / nodeMemMin);
    nodeMin = minimalNodeRequired > nodeMin ? minimalNodeRequired : nodeMin;

    nodeMax = nodeMin > nodeMax ? nodeMin : nodeMax;
  }

  /**
   * Retrieve the number of nodes in the architecture when you use a single network algo .
   *
   * @param project
   *          the project path
   * @return the number of node
   */
  private int singleNetInitNode(String project) {
    final String path = project + "/Archi/";
    final String fileName = "SimSDP_node.csv";
    // read csv file
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + fileName));
    if (!iFile.isAccessible()) {
      final String message = "Missing file: Archi/" + fileName;
      PreesmLogger.getLogger().log(Level.SEVERE, message);
    }
    final List<String> nodeList = new ArrayList<>();
    final String content = PreesmIOHelper.getInstance().read(path, fileName);
    final String[] line = content.split("\n");
    int nNode = 0;
    for (int i = 1; i < line.length; i++) {
      final String[] column = line[i].split(";");
      if (!nodeList.contains(column[0])) {
        nNode++;
        nodeList.add(column[0]);
      }
    }
    return nNode;
  }

  public int getNodeMin() {
    return nodeMin;
  }

  public int getNodeMax() {
    return nodeMax;
  }

  public int getNodeStep() {
    return nodeStep;
  }

  public int getCoreMin() {
    return coreMin;
  }

  public int getCoreMax() {
    return coreMax;
  }

  public int getCoreStep() {
    return coreStep;
  }

  public int getCoreFreqMin() {
    return coreFreqMin;
  }

  public int getCoreFreqMax() {
    return coreFreqMax;
  }

  public int getCoreFreqStep() {
    return coreFreqStep;
  }

  public int getTopoMin() {
    return topoMin;
  }

  public int getTopoMax() {
    return topoMax;
  }

  public int getTopoStep() {
    return topoStep;
  }

  public long getNodeMemMin() {
    return nodeMemMin;
  }

  public long getNodeMemMax() {
    return nodeMemMax;
  }

  public long getNodeMemStep() {
    return nodeMemStep;
  }
}
