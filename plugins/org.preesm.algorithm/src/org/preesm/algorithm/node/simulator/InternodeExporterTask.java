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
import org.preesm.workflow.WorkflowParser;
import org.preesm.workflow.elements.AbstractWorkflowNode;
import org.preesm.workflow.elements.TaskNode;
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
  public static final String STD_NAME         = "std_trend.csv";
  public static final String LATENCY_NAME     = "latency_trend.csv";
  public static final String WORKLOAD_NAME    = "workload.csv";
  public static final String CORRELATION_NAME = "correlation.csv";
  public static final String FOLDER           = "/Simulation/";

  Double              latency     = 0.0;
  Map<String, Double> loadPerNode = new HashMap<>();
  Map<String, Double> loadPerLink = new HashMap<>();

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {

    final LatencyAbc abc = (LatencyAbc) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_ABC);

    // Detect OS
    final String os = System.getProperty("os.name").toLowerCase();
    Boolean simOk = false;
    if ((os.contains("nix") || os.contains("nux"))) {
      simOk = simgridSimulation(workflow);
      if (Boolean.TRUE.equals(simOk)) {
        simgridReader(workflow.getProjectName());
      }
    }

    if (os.contains("win") || Boolean.TRUE.equals(!simOk)) {
      preesmSimulation(abc);
    }
    // standard deviation & latency trend

    final Double sigma = sigma(loadPerNode);
    PreesmIOHelper.getInstance().append(workflow.getProjectName() + FOLDER, LATENCY_NAME, String.valueOf(latency));
    PreesmIOHelper.getInstance().append(workflow.getProjectName() + FOLDER, STD_NAME, String.valueOf(sigma));
    fillCorrelation(sigma(loadPerNode), sigma(loadPerLink), latency, workflow.getProjectName() + FOLDER);

    final Boolean singlenet = processWorkflowFile(workflow);

    if (Boolean.TRUE.equals(singlenet)) {
      // cumulated deviation
      final Map<String, Double> deviationPerNode = deviationPerNodeCompute(loadPerNode);
      final Map<String, Double> previousdeviationPerNode = previousDeviationPerNodeCompute(workflow.getProjectName());
      fillWorkload(deviationPerNode, previousdeviationPerNode, workflow.getProjectName() + FOLDER);

    }
    return new LinkedHashMap<>();
  }

  private void fillCorrelation(Double sigma, Double sigma2, Double latency2, String filePath) {
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath + CORRELATION_NAME));
    final StringConcatenation content = new StringConcatenation();
    if (!iFile.exists()) {
      // If the file does not exist, append the header
      content.append("STD workload;STD link load;Final latency;\n");
    }
    content.append(sigma + ";" + sigma2 + ";" + latency2 + ";");
    PreesmIOHelper.getInstance().append(filePath, CORRELATION_NAME, content.toString());

  }

  private boolean processWorkflowFile(Workflow workflow) {
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot()
        .getFile(new Path(workflow.getProjectName() + "/Workflows/hypervisor.workflow"));

    if (iFile.exists()) {
      final Workflow hypervisorWorkflow = new WorkflowParser()
          .parse(workflow.getProjectName() + "/Workflows/hypervisor.workflow");
      final AbstractWorkflowNode<?> node = hypervisorWorkflow.vertexTopologicalList().get(1);
      final TaskNode taskNode = (TaskNode) node;

      return taskNode.getParameter("Multinet").equals("false");

    }
    return false;
  }

  /**
   * Reads a workload file from the specified path and computes the previous deviation per node.
   *
   * @param path
   *          The path to the folder containing the workload file.
   * @return A map associating each node with its corresponding previous deviation.
   */
  private Map<String, Double> previousDeviationPerNodeCompute(String path) {
    final Map<String, Double> wp = new HashMap<>();
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + FOLDER + WORKLOAD_NAME));
    if (iFile.isAccessible()) {
      // Read the content of the workload file
      final String content = PreesmIOHelper.getInstance().read(path + FOLDER, WORKLOAD_NAME);

      // Initialize a map to store node-to-deviation associations

      // Split the content into lines
      final String[] lines = content.split("\n");

      // Process each line to extract node and deviation information
      for (final String line : lines) {
        final String[] columns = line.split(";");

        // Extract node and deviation, then store in the map
        wp.put(columns[0], Double.valueOf(columns[1]));
      }
    }

    return wp;
  }

  /**
   * Fills a workload file based on the deviation per node and previous deviation per node.
   *
   * @param deviationPerNode
   *          Map associating each node with its current deviation.
   * @param previousDeviationPerNode
   *          Map associating each node with its previous deviation.
   * @param path
   *          The path to the folder where the workload file will be stored.
   */
  private void fillWorkload(Map<String, Double> deviationPerNode, Map<String, Double> previousDeviationPerNode,
      String path) {
    // Initialize a string concatenation for building the content
    final StringConcatenation content = new StringConcatenation();

    // Process each entry in the deviationPerNode map
    for (final Entry<String, Double> entry : deviationPerNode.entrySet()) {
      // Calculate the result by adding the current and previous deviations
      Double result;
      if (!previousDeviationPerNode.isEmpty()) {
        result = entry.getValue() + previousDeviationPerNode.get(entry.getKey());
      } else {
        result = entry.getValue();
      }

      // Append the node and result to the content
      content.append(entry.getKey() + ";" + result + "\n");
    }

    // Print the content to the workload file
    PreesmIOHelper.getInstance().print(path, WORKLOAD_NAME, content);
  }

  /**
   * Reads simulation results from a SimGrid CSV file.
   *
   * @param path
   *          The path to the folder where the SimGrid CSV file is located.
   */
  private void simgridReader(String path) {
    // Read the content of the SimGrid CSV file
    final String simcsv = PreesmIOHelper.getInstance().read(path + FOLDER, "simgrid.csv");

    // Split the content into lines
    final String[] lines = simcsv.split("\n");

    // Extract latency information from the second line
    latency = Double.valueOf(lines[1].split(",")[2]);

    // Process each line starting from the third line up to half of the total lines
    for (int i = 2; i < lines.length / 2; i++) {
      // Split the line into columns
      final String[] columns = lines[i].split(",");

      // Check if the first column contains "Node"
      if (columns[0].contains("Node")) {
        // Add node and corresponding load information to the map
        loadPerNode.put(columns[0], Double.valueOf(columns[2]));
      }

      if (columns[0].contains("link")) {
        // Add node and corresponding load information to the map
        loadPerLink.put(columns[0], Double.valueOf(columns[2]));
      }
    }
  }

  /**
   * Calculates the standard deviation of loads per node.
   *
   * @param loadPerNode
   *          A map containing node IDs as keys and corresponding load values.
   * @return The standard deviation of loads per node.
   */
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

  /**
   * Computes the deviation of loads per node from the average load.
   *
   * @param loadPerNode
   *          A map containing node IDs as keys and corresponding load values.
   * @return A map with node IDs as keys and their load deviations from the average.
   */
  private Map<String, Double> deviationPerNodeCompute(Map<String, Double> loadPerNode) {
    // Compute the average load
    Double sum = 0d;
    for (final Double value : loadPerNode.values()) {
      sum += value;
    }
    final Double average = sum / loadPerNode.size();

    // Compute node deviation
    final Map<String, Double> ws = new HashMap<>();
    for (final Entry<String, Double> entry : loadPerNode.entrySet()) {
      ws.put(entry.getKey(), entry.getValue() - average);
    }
    return ws;
  }

  /**
   * Performs SimGrid simulation for the given workflow.
   *
   * @param workflow
   *          The workflow for which SimGrid simulation is performed.
   * @return True if the simulation is successful, false otherwise.
   * @throws InterruptedException
   *           If the simulation process is interrupted.
   */
  private Boolean simgridSimulation(Workflow workflow) throws InterruptedException {
    // Get the root of the workspace
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    // Get the project from the workspace
    final IProject project = root.getProject(workflow.getProjectName());
    final String projectFullPath = project.getLocationURI().getPath() + "/";

    // Check if SimGrid is installed
    final File simgridBuildFolder = new File(projectFullPath + "SimGrid/simgrid");
    if (!simgridBuildFolder.exists()) {
      PreesmLogger.getLogger().log(Level.INFO,
          "SimGrid is not installed in your system, please run: install_simgrid.sh");
      return false;
    }

    // Check if SimSDP is linked to SimGrid
    final File simgridSimuFolder = new File(projectFullPath + "SimGrid/simsdp");
    if (!simgridSimuFolder.exists()) {
      PreesmLogger.getLogger().log(Level.INFO, "SimSDP is not linked to SimGrid, please run: install_simgridag.sh");
      return false;
    }

    // Check if required files are in the right place
    final File simFolder = new File(projectFullPath + "Algo/generated/top");
    if (!simFolder.exists()) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "Missing top.pi and gantt.xml for SimGrid simulation in folder: Algo/generated/top/");
    }

    final File xmlFolder = new File(projectFullPath + "Archi/SimSDP_network.xml");
    if (!xmlFolder.exists()) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Missing SimSDP_network.xml for SimGrid simulation in folder: Archi/");
    }

    // Run SimSDP for Load/Energy
    bash("simsdp " + projectFullPath + "Algo/generated/top" + " -p " + projectFullPath
        + "Archi/SimSDP_network.xml -j -o " + projectFullPath + "Simulation/simgrid.csv");
    WorkspaceUtils.updateWorkspace();
    return true;
  }

  /**
   * Performs a Preesm simulation using the provided latency ABC.
   *
   * @param abc
   *          The LatencyAbc containing component instances and their corresponding loads.
   */
  private void preesmSimulation(LatencyAbc abc) {
    latency = maxLoad(abc);
    for (final ComponentInstance cp : abc.getArchitecture().getOperatorComponentInstances()) {
      loadPerNode.put("node" + cp.getHardwareId(), abc.getLoad(cp) / latency);
    }
  }

  /**
   * Computes the maximum load among all component instances in the given latency ABC.
   *
   * @param abc
   *          The LatencyAbc containing component instances and their corresponding loads.
   * @return The maximum load among all component instances.
   */
  private Double maxLoad(LatencyAbc abc) {
    Long maxLoad = Long.MIN_VALUE;
    for (final ComponentInstance cp : abc.getArchitecture().getComponentInstances()) {
      maxLoad = Math.max(abc.getLoad(cp), maxLoad);
    }
    return (double) maxLoad;
  }

  /**
   * Executes a Bash command in a separate process and logs the output.
   *
   * @param prompt
   *          The Bash command to be executed.
   * @throws InterruptedException
   *           If the process is interrupted while waiting for completion.
   */
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
