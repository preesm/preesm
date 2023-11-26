package org.preesm.algorithm.node.simulator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Execute SimGrid.
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "SimGridLauncher.identifier", name = "SimGrid", category = "SimGrid bash", parameters = {

    @Parameter(name = "SimGrid Path", description = "Simgrid installation path",
        values = { @Value(name = "path", effect = "change default path") }),
    @Parameter(name = "SimGrid AG Path", description = "Installation path for adrien gougeon's project",
        values = { @Value(name = "path", effect = "change default path") }) })
public class SimGridLauncherTask extends AbstractTaskImplementation {
  public static final String PARAM_SIMPATH    = "SimGrid Path";
  public static final String PARAM_SIMAGPATH  = "SimGrid AG Path";
  String                     simFolder        = "/Algo/generated/top";
  String                     csvSimGridFolder = "simgrid.csv";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    final String os = System.getProperty("os.name").toLowerCase();
    if (!(os.contains("nix") || os.contains("nux"))) {
      PreesmLogger.getLogger().log(Level.SEVERE, () -> "SimSDP with SimGrid is only supported on unix system");
    }
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final IProject project = root.getProject(workflow.getProjectName());
    final String projectFullPath = project.getLocationURI().getPath() + "/";
    final String bashablePath = projectFullPath.replace("\\", "/");

    // Check SimGrid Install
    final String simpath = parameters.get(PARAM_SIMPATH);
    bash("bash " + simpath, projectFullPath);
    // Check @agougeon repository Install
    final String simagpath = parameters.get(PARAM_SIMAGPATH);
    bash(simagpath, projectFullPath);

    // Load/Energy
    bash("simsdp " + simFolder + " -p simSDP_netork.xml - o -j" + csvSimGridFolder, projectFullPath);

    return new LinkedHashMap<>();
  }

  private void bash(String scriptPath, String projectFullPath) throws InterruptedException {
    // if linux
    final ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/bash", "-c", scriptPath);
    // if windows
    // final File directory = new File(projectFullPath);
    // final String[] command = { "C:\\Program Files\\Git\\bin\\bash.exe", "-c", "cd " + projectFullPath.substring(1),
    // "./" + scriptPath };
    // final ProcessBuilder processBuilder = new ProcessBuilder(command);
    // processBuilder.directory(directory);

    try {
      final Process process = processBuilder.start();
      final int exitCode = process.waitFor();
      // Vérification du code de sortie
      if (exitCode == 0) {
        System.out.println("Success bash:" + scriptPath);

      } else {
        System.out.println("Simgrid n'est pas installé sur votre système.");
      }
      System.out.println(" exit code: " + exitCode);

    } catch (final IOException e) {
      throw new PreesmRuntimeException(e);
    }
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(SimGridLauncherTask.PARAM_SIMPATH, "SimGrid/install_simgrid.sh");
    parameters.put(SimGridLauncherTask.PARAM_SIMAGPATH, "SimGrid/install_simgag.sh");

    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Launch SimGrid bash.";
  }

}
