package org.preesm.algorithm.node.simulator;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
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
public class SimGridLauncher extends AbstractTaskImplementation {
  public static final String PARAM_SIMPATH    = "SimGrid Path";
  public static final String PARAM_SIMAGPATH  = "SimGrid AG Path";
  String                     simFolder        = "/Algo/generated/top";
  String                     csvSimGridFolder = "/CSV/simgrid.csv";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    // Check SimGrid Install
    bash(PARAM_SIMPATH);
    // Check @agougeon repository Install
    bash(PARAM_SIMPATH);

    // Load/Energy
    bash("simsdp " + simFolder + " -p simSDP_netork.xml - o -j" + csvSimGridFolder);

    return new LinkedHashMap<>();
  }

  private void bash(String scriptPath) throws InterruptedException {
    final ProcessBuilder processBuilder = new ProcessBuilder("/usr/bin/bash", scriptPath);
    try {
      final Process process = processBuilder.start();
      final int exitCode = process.waitFor();
      PreesmLogger.getLogger().log(Level.INFO, "SimGrid installation exit code: " + exitCode);

    } catch (final IOException e) {
      throw new PreesmRuntimeException(e);
    }
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(SimGridLauncher.PARAM_SIMPATH, "SimGrid/install_simgrid.sh");
    parameters.put(SimGridLauncher.PARAM_SIMAGPATH, "SimGrid/install_simgag.sh");

    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Launch SimGrid bash.";
  }

}
