package org.preesm.workflow.simsdp;

import java.io.IOException;

public class SimGridLauncher {
  private final String path;

  public SimGridLauncher(final String path) {
    this.path = path;
  }

  public void execute() {
    try {
      final ProcessBuilder processBuilder = new ProcessBuilder("bash", path);
      processBuilder.redirectErrorStream(true); // Redirect error stream to standard output
      final Process process = processBuilder.start();

      // Read the output of the script
      final java.io.InputStream inputStream = process.getInputStream();
      final java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
      final String output = scanner.hasNext() ? scanner.next() : "";

      final int exitCode = process.waitFor();

      if (exitCode == 0) {
        System.out.println("Script executed successfully:");
        System.out.println(output);
      } else {
        System.err.println("Script failed to execute. Exit code: " + exitCode);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

}
