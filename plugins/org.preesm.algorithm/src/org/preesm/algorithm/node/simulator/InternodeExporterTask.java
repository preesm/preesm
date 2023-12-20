package org.preesm.algorithm.node.simulator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class export the CSV file in order to generate the trend chart (latency, standard deviation workload) for SIMSDP
 * inter-node analysis (Simulation workflow). Depending on the recognized operating system, nodes are simulated on
 * SimGrid if the system is linux, Preesm otherwise.
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "InternodeExporterTask.identifier", name = "Internode Stats exporter", category = "SimGrid bash",
    inputs = { @Port(name = "ABC", type = LatencyAbc.class) }, parameters = {

        @Parameter(name = "SimGrid Path", description = "Simgrid installation path",
            values = { @Value(name = "path", effect = "change default path") }),
        @Parameter(name = "SimGrid AG Path", description = "Installation path for adrien gougeon's project",
            values = { @Value(name = "path", effect = "change default path") }) })
public class InternodeExporterTask extends AbstractTaskImplementation {
  public static final String PARAM_SIMPATH   = "SimGrid/install_simgrid.sh";
  public static final String PARAM_SIMAGPATH = "SimGrid/install_simgag.sh";
  public static final String STD_NAME        = "std_trend.csv";
  public static final String LATENCY_NAME    = "latency_trend.csv";

  String              csvSimGridFolder = "Simulation/simgrid.csv";
  Double              latency          = 0.0;
  Map<String, Double> loadPerNode      = new HashMap<>();

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {

    final LatencyAbc abc = (LatencyAbc) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_ABC);

    // Detect OS
    final String os = System.getProperty("os.name").toLowerCase();
    Boolean simOk = false;
    if ((os.contains("nix") || os.contains("nux"))) {
      simOk = simgridSimulation(parameters, workflow);
      if (simOk) {
        simgridReader(workflow.getProjectName());
      }
    }

    if (os.contains("win") || Boolean.TRUE.equals(!simOk)) {
      preesmSimulation(abc);
    } else {
      PreesmLogger.getLogger().log(Level.SEVERE, () -> "The operation system is not recognised to support SimSDP.");
    }
    // standard deviation & latency trend

    final Double sigma = sigma(loadPerNode);

    appendCSV(String.valueOf(latency), workflow.getProjectName() + "/Simulation/", LATENCY_NAME);
    appendCSV(String.valueOf(sigma), workflow.getProjectName() + "/Simulation/", STD_NAME);

    // cumulated deviation
    final Map<String, Double> deviationPerNode = deviationPerNodeCompute(loadPerNode);
    return new LinkedHashMap<>();
  }

  private void simgridReader(String path) {
    final String simcsv = PreesmIOHelper.getInstance().read(path + "/Simulation/", "simgrid.csv");
    final String[] line = simcsv.split("\n");

    latency = Double.valueOf(line[1].split(",")[2]);

    for (int i = 2; i < line.length / 2; i++) {
      final String[] column = line[i].split(",");
      if (column[0].contains("Node")) {
        loadPerNode.put(column[0], Double.valueOf(column[2]));
      }

    }

  }

  private Double sigma(Map<String, Double> loadPerNode) {
    Double sum = 0d;
    // Calculate the sum of values
    for (final Double value : loadPerNode.values()) {
      sum += value;
    }
    // Calculate the mean
    final Double average = sum / loadPerNode.size();
    // Reset the sum for calculating the sum of squares
    sum = 0d;
    // Calculate the sum of squares of deviations from the mean
    for (final Double value : loadPerNode.values()) {
      sum += Math.pow((value - average), 2);
    }
    // Calculate the standard deviation
    return Math.sqrt(sum / loadPerNode.size());
  }

  private Map<String, Double> deviationPerNodeCompute(Map<String, Double> loadPerNode) {
    // compute average
    Double sum = 0d;
    for (final Double value : loadPerNode.values()) {
      sum += value;
    }
    final Double average = sum / loadPerNode.size();

    // compute node deviation
    final Map<String, Double> ws = new HashMap<>();
    for (final Entry<String, Double> entry : loadPerNode.entrySet()) {
      ws.put(entry.getKey(), entry.getValue() - average);
    }
    return ws;
  }

  private void appendCSV(String data, String path, String fileName) {
    final StringConcatenation content = new StringConcatenation();

    // if the file exists, we write to it otherwise we create the template
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + fileName));
    if (iFile.isAccessible()) {
      content.append(PreesmIOHelper.getInstance().read(path, fileName));
      content.append(data + "\n");
      PreesmIOHelper.getInstance().print(path, fileName, content);
    } else {

      content.append(data + "\n");
      PreesmIOHelper.getInstance().print(path, fileName, content);

    }
  }

  private Boolean simgridSimulation(Map<String, String> parameters, Workflow workflow) throws InterruptedException {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(workflow.getProjectName());
    final String projectFullPath = project.getLocationURI().getPath() + "/";
    // to run click on .sh otherwise check if folder exist
    final File simgridBuildFolder = new File(projectFullPath + "SimGrid/simgrid");
    if (!simgridBuildFolder.exists()) {
      PreesmLogger.getLogger().log(Level.INFO,
          "SimGrid is not installed in your system, please run: install_simgrid.sh");
      return false;
    }
    final File simgridSimuFolder = new File(projectFullPath + "SimGrid/simsdp");
    if (!simgridSimuFolder.exists()) {
      PreesmLogger.getLogger().log(Level.INFO, "SimSDP is not linked to SimGrid, please run: install_simgridag.sh");
      return false;
    }
    // check if the required files are at the right place
    final File simFolder = new File(projectFullPath + "Algo/generated/top");
    if (!simFolder.exists()) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "Missing top.pi and gantt.xml for simgrid simulation in folder: Algo/generated/top/");
    }

    final File xmlFolder = new File(projectFullPath + "Archi/SimSDP_network.xml");
    if (!xmlFolder.exists()) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Missing simSDP_network.xml for simgrid simulation in folder: Archi/");
    }

    // Load/Energy
    bash("simsdp " + projectFullPath + "Algo/generated/top" + " -p " + projectFullPath
        + "Archi/SimSDP_network.xml -j -o " + projectFullPath + "Simulation/simgrid.csv");
    WorkspaceUtils.updateWorkspace();
    return true;
  }

  private void preesmSimulation(LatencyAbc abc) {

    latency = maxLoad(abc);
    for (final ComponentInstance cp : abc.getArchitecture().getOperatorComponentInstances()) {
      loadPerNode.put("node" + cp.getHardwareId(), abc.getLoad(cp) / latency);
    }

  }

  private Double maxLoad(LatencyAbc abc) {
    Long maxLoad = Long.MIN_VALUE;
    for (final ComponentInstance cp : abc.getArchitecture().getComponentInstances()) {
      maxLoad = Math.max(abc.getLoad(cp), maxLoad);
    }
    return (double) maxLoad;
  }

  private Double maxLoad(Map<String, Double> loadPerNode) {
    Double maxLoad = Double.NEGATIVE_INFINITY;
    for (final Double value : loadPerNode.values()) {
      if (value > maxLoad) {
        maxLoad = value;
      }
    }
    return maxLoad;
  }

  private void bash(String prompt) throws InterruptedException {
    try {
      // Create a process builder for the command
      final ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/bash", "-c", prompt);

      // Start the process
      final Process process = processBuilder.start();

      // Read and print the output of the command
      final java.io.InputStream inputStream = process.getInputStream();
      final java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
      final String output = scanner.hasNext() ? scanner.next() : "";
      PreesmLogger.getLogger().log(Level.INFO, output);

      // Wait for the process to complete
      final int exitCode = process.waitFor();

      // Print the exit code (0 usually means success)
      PreesmLogger.getLogger().log(Level.INFO, () -> "Command exited with code: " + exitCode);
      scanner.close();
    } catch (final IOException e) {
      throw new PreesmRuntimeException(e);
    }

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Launch SimGrid bash.";
  }

}
