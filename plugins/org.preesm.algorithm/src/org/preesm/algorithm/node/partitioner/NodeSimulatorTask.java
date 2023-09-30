package org.preesm.algorithm.node.partitioner;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Execute SimGrid.
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "NodeSimulatorTask.identifier", name = "SimGrid", category = "SimGrid bash", parameters = {

    @Parameter(name = "SimGrid Path", description = "Path of the folder containing the bash",
        values = { @Value(name = "path", effect = "change default path") }),
    @Parameter(name = "Folder Path", description = "Path of the folder containing the bash",
        values = { @Value(name = "path", effect = "change default path") }) })
public class NodeSimulatorTask extends AbstractTaskImplementation {

  public static final String PARAM_SIMPATH    = "SimGrid Path";
  public static final String PARAM_FOLDERPATH = "Folder Path";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final String simpath = parameters.get(NodeSimulatorTask.PARAM_SIMPATH);
    final String folderpath = parameters.get(NodeSimulatorTask.PARAM_FOLDERPATH);

    final String command = simpath + " " + folderpath + " -c";
    try {
      // Create a process builder for the command
      final ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);

      // Start the process
      final Process process = processBuilder.start();

      // Read and print the output of the command
      final java.io.InputStream inputStream = process.getInputStream();
      final java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
      final String output = scanner.hasNext() ? scanner.next() : "";

      System.out.println(output);

      // Wait for the process to complete
      final int exitCode = process.waitFor();

      // Print the exit code (0 usually means success)
      System.out.println("Command exited with code: " + exitCode);
      scanner.close();
    } catch (IOException | InterruptedException e) {
      final String errorMessage = e.getMessage();
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(NodeSimulatorTask.PARAM_SIMPATH, "simsdp");
    parameters.put(NodeSimulatorTask.PARAM_FOLDERPATH, "/project/SimGrid/");

    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Launch SimGrid bash.";
  }

}
