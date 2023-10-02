package org.preesm.algorithm.node.partitioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;

public class NodeCSVExporter {
  public static final String FILE_NAME = "workload.csv";
  String                     scenarioPath;

  private NodeCSVExporter() {
  }

  public static void exportWorkload(Map<String, Double> wl, Double latency, String path) {
    // compute max workload
    Double maxValue = Double.NEGATIVE_INFINITY;
    for (final Double value : wl.values()) {
      if (value > maxValue) {
        maxValue = value;
      }
    }
    // compute average
    Double sum = 0d;
    for (final Double value : wl.values()) {
      sum += value;
    }
    final Double average = sum / wl.size();

    // compute node deviation
    final Map<String, Double> ws = new HashMap<>();
    for (final Entry<String, Double> entry : wl.entrySet()) {
      ws.put(entry.getKey(), entry.getValue() - average);
    }

    //
    sum = 0d;
    for (final Double value : wl.values()) {
      sum += Math.pow((value - average), 2);
    }
    final Double sigma = Math.sqrt((sum / wl.size()));

    // retrieve previous deviation
    previousDeviation(ws, latency, sigma, path);

    // generate new workload file
    final StringConcatenation content = new StringConcatenation();
    content.append("Nodes;Workload;\n");
    for (final Entry<String, Double> entry : ws.entrySet()) {
      content.append(entry.getKey() + ";" + entry.getValue() + "; \n");
    }
    content.append("Latency;" + latency + ";\n");
    content.append("SigmaW;" + sigma);

    PreesmIOHelper.getInstance().print(path, FILE_NAME, content);
  }

  private static void previousDeviation(Map<String, Double> ws, Double latency, Double sigma, String path) {
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + FILE_NAME));
    if (iFile.isAccessible()) {
      Double prevLatency = 0d;
      Double prevSigmaWorkload = 0d;
      final String content = PreesmIOHelper.getInstance().read(path, FILE_NAME);
      final String[] line = content.split("\\n");
      for (final String element : line) {
        final String[] split = element.split(";");
        if (split[0].equals("Latency")) {
          prevLatency = Double.valueOf(split[1]);
        }

        if (split[0].equals("SigmaW")) {
          prevSigmaWorkload = Double.valueOf(split[1]);
        }
        // update workload deviation (ws)
        for (final Entry<String, Double> entry : ws.entrySet()) {
          if (split[0].equals(entry.getKey())) {
            entry.setValue(entry.getValue() + Double.valueOf(split[1]));
          }
        }
      }
      // convergence check : standard deviation & latency deviation
      convergenceCheck(prevLatency, latency, prevSigmaWorkload, sigma);

    }
  }

  private static void convergenceCheck(Double prevLatency, Double latency, Double prevSigmaWorkload, Double sigma) {
    if (prevLatency <= latency) {
      final String message = "Latency tend to increase from: " + prevLatency + "to: " + latency;
      PreesmLogger.getLogger().log(Level.INFO, message);
    }
    if (prevSigmaWorkload <= sigma) {
      final String message = "Standard workload deviation tend to increase from: " + prevSigmaWorkload + "to: " + sigma;
      PreesmLogger.getLogger().log(Level.INFO, message);

    }
  }

}
