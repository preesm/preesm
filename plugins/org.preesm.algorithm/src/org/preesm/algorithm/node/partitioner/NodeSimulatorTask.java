package org.preesm.algorithm.node.partitioner;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
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
        values = { @Value(name = "path", effect = "change default path") }) })
public class NodeSimulatorTask extends AbstractTaskImplementation {
  /** The Constant PARAM_MULTINODE. */
  public static final String PARAM_SIMPATH = "SimGrid Path";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final String simpath = parameters.get(NodeSimulatorTask.PARAM_SIMPATH);
    final String folder = "/home/orenaud/eclipse/runtime-EclipseApplication/simSDP.RFIfilter/SimGrid/tests/";
    // final String folder = "/home/orenaud/Téléchargements/simsdp-master/tests";
    final String pythonScript = folder + "test_ABC_codegen.py";
    // final String pythonScript = folder + "script.py";
    // final String pythonScript =
    // "/home/orenaud/eclipse/runtime-EclipseApplication/simSDP.RFIfilter/simgrid/script.py";
    // try {
    // // Définir la commande pour exécuter le script Python
    //
    // // Python
    //
    // // Créer un processus pour exécuter la commande
    // final ProcessBuilder processBuilder = new ProcessBuilder(pythonScript);
    //
    // // Rediriger la sortie standard (stdout) du processus
    // processBuilder.redirectErrorStream(true);
    //
    // // Démarrer le processus
    // final Process process = processBuilder.start();
    //
    // // Lire la sortie du script Python
    // final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    // String line;
    // while ((line = reader.readLine()) != null) {
    // System.out.println(line);
    // }
    //
    // // Attendre la fin du processus
    // final int exitCode = process.waitFor();
    // System.out.println("Le script Python a terminé avec le code de sortie : " + exitCode);
    //
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // }
    // try {
    //
    // final Process process = Runtime.getRuntime().exec("python3 " + pythonScript);
    // final BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    // String errorLine;
    // while ((errorLine = errorReader.readLine()) != null) {
    // System.err.println(errorLine);
    // }
    //
    // final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    // String line;
    // while ((line = reader.readLine()) != null) {
    // System.out.println(line);
    // }
    //
    // final int exitCode = process.waitFor();
    // if (exitCode == 0) {
    // System.out.println("Script executed successfully:");
    // // System.out.println(output);
    // } else {
    // System.err.println("Script failed to execute. Exit code: " + exitCode);
    // }
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // }
    // Command to run
    // final String command = "/home/orenaud/.local/bin/simsdp
    // /home/orenaud/Téléchargements/simsdp-master/tests/test_data/ABC\\ -\\ 8n\\ -\\ round0";
    //
    // try {
    // // Create a process builder for the command
    // final ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
    //
    // // Start the process
    // final Process process = processBuilder.start();
    //
    // // Wait for the process to complete
    // final int exitCode = process.waitFor();
    //
    // // Print the exit code (0 usually means success)
    // System.out.println("Command exited with code: " + exitCode);
    // } catch (IOException | InterruptedException e) {
    // e.printStackTrace();
    // }
    // Command to run
    // final String command = "/home/orenaud/.local/bin/simsdp
    // /home/orenaud/Téléchargements/simsdp-master/tests/test_data/ABC\\ -\\ 8n\\ -\\ round0 -c";
    // final String simsdpLocation = "/home/orenaud/.local/bin/simsdp ";
    // final String pixmlLocation = "/home/orenaud/Téléchargements/simsdp-master/tests/test_data/ABC\\ -\\ 8n\\ -\\
    // round0 ";
    // final String cmd = simsdpLocation + pixmlLocation + "-c";
    // final String command = "ls -l";
    final String command = "";
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
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(NodeSimulatorTask.PARAM_SIMPATH, "/simSDP.RFIfilter/SimGrid/");
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Launch SimGrid bash.";
  }

}
