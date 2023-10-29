package org.preesm.algorithm.node.partitioner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
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
  private Float              Latency          = 0.0f;
  String                     workloadPath     = "";
  String                     scriptPath       = "";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    workloadPath = "/" + workflow.getProjectName() + "/Scenarios/generated/";
    scriptPath = "/" + workflow.getProjectName() + "/Script/";

    final String osName = System.getProperty("os.name").toLowerCase();
    if (!(osName.contains("nix") || osName.contains("nux"))) {

      PreesmLogger.getLogger().log(Level.INFO, "Operating system not supported : " + osName);
      return new LinkedHashMap<>();
    }
    generateScript();
    executeScript();

    final String simpath = parameters.get(NodeSimulatorTask.PARAM_SIMPATH);
    final String folderpath = parameters.get(NodeSimulatorTask.PARAM_FOLDERPATH);

    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(workflow.getProjectName());
    final String projectFullPath = project.getLocationURI().getPath();
    final String command = simpath + " " + projectFullPath + folderpath + " -c";
    launchSimGrid(command);

    return new LinkedHashMap<>();
  }

  private void launchSimGrid(String command) throws InterruptedException {
    try {
      // Create a process builder for the command
      final ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/bash", "-c", command);

      // Start the process
      final Process process = processBuilder.start();

      // Read and print the output of the command
      final java.io.InputStream inputStream = process.getInputStream();
      final java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
      final String output = scanner.hasNext() ? scanner.next() : "";
      //
      if (!"".equals(output)) {
        PreesmLogger.getLogger().log(Level.INFO, "simsdp bash command not found");
      } else {
        PreesmLogger.getLogger().log(Level.INFO, output);
        final Map<String, Double> wl = extractWorkload(output);
        NodeCSVExporter.exportWorkload(wl, (double) Math.round(Latency), workloadPath);
      }
      // Wait for the process to complete
      final int exitCode = process.waitFor();

      // Print the exit code (0 usually means success)
      PreesmLogger.getLogger().log(Level.INFO, () -> "Command exited with code: " + exitCode);
      scanner.close();
    } catch (final IOException e) {
      throw new PreesmRuntimeException(e);
    }
  }

  private void executeScript() throws InterruptedException {
    final String scriptFileName = "install_script.sh";

    try {
      // Créer un processus pour exécuter le script
      final ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/bash", scriptPath + scriptFileName);
      final Process process = processBuilder.start();

      // Rediriger la sortie standard du processus vers la console
      final InputStream inputStream = process.getInputStream();
      int bytesRead;
      final byte[] buffer = new byte[1024];
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        System.out.write(buffer, 0, bytesRead);
      }

      final int exitCode = process.waitFor();
      System.out.println("Le script s'est terminé avec le code de sortie : " + exitCode);

    } catch (final IOException e) {
      throw new PreesmRuntimeException(e);
    }
  }

  private void generateScript() {
    final String scriptFileName = "install_script.sh";
    final StringConcatenation content = new StringConcatenation();
    content.append("#!/bin/bash\n"); // Entête du script

    // Commande pour vérifier si un dossier existe, sinon cloner depuis GitHub
    content.append("if [ ! -d \"simsdp\" ]; then\n");
    content.append(" git clone https://github.com/adrgougeon/simsdp.git\n");
    content.append("sudo apt update\n");
    content.append("sudo apt install -y python3 pip default-jdk cmake git libboost-dev meson pkg-config doxygen\n");
    content.append("pip install -r requirements.txt\n");
    content.append("pip install .\n");
    content.append("fi\n");

    // Cloner le référentiel SimGrid
    content.append("if [ ! -d \"simgrid\" ]; then\n");
    content.append(" git clone https://framagit.org/simgrid/simgrid\n");
    content.append("cd simgrid\n");
    content.append("cmake -B build .\n");
    content.append("sudo make install -j$(nproc) -C build\n");
    content.append("pip install .\n");
    content.append("sudo ldconfig\n");
    content.append("fi\n");
    PreesmIOHelper.getInstance().print(scriptPath, scriptFileName, content);
  }

  private Map<String, Double> extractWorkload(String output) {
    final Map<String, Double> wl = new HashMap<>();
    final String cleanData = output.replace("{'load (%)': ", "").replaceAll("[{}'\":%]", "");
    final String[] parseData = cleanData.split(", ");
    for (final String element : parseData) {
      final String[] pairs = element.split("\\s+");
      if (pairs[0].contains("Core") && !pairs[0].contains("router")) {
        wl.put("node" + pairs[0].replace("Core", ""), Double.valueOf(pairs[1]));
      }
      if (pairs[0].contains("latency")) {

        Latency = Float.valueOf(pairs[2]);
      }
    }
    return wl;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(NodeSimulatorTask.PARAM_SIMPATH, "~/.local/bin/simsdp");
    parameters.put(NodeSimulatorTask.PARAM_FOLDERPATH, "/Algo/generated/top");

    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Launch SimGrid bash.";
  }

}
