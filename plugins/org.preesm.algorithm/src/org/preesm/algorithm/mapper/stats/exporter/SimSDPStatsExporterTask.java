package org.preesm.algorithm.mapper.stats.exporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "org.ietr.preesm.stats.exporter.SimSDPStatsExporterTask", name = "ABC Node exporter",
    category = "Node exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class), @Port(name = "scenario", type = Scenario.class) },

    shortDescription = "This task ...",

    parameters = { @Parameter(name = SimSDPStatsExporterTask.PARAM_MULTINODE,

        description = "Enables to export final latency simulated on each node as a timing in a CSV file",
        values = { @Value(name = "false", effect = "Do not print timing (implementation length"),
            @Value(name = "true", effect = "Print timing (implementation length") }),
        @Parameter(name = SimSDPStatsExporterTask.PARAM_TOPNODE,
            description = "Enables use PREESM instead of Simgrid to simulate multi-node architecture",
            values = { @Value(name = "false", effect = "Do not Print the workload"),
                @Value(name = "true", effect = "Print the workload with PREESM simulation") }) }

)

public class SimSDPStatsExporterTask extends AbstractTaskImplementation {
  public static final String DEFAULT_BOOL = "false";

  /** The Constant PARAM_MULTINODE. */
  public static final String PARAM_MULTINODE = "Multinode";

  /** The Constant PARAM_MULTINODE. */
  public static final String PARAM_TOPNODE = "Top";

  private String scenarioPath = "";

  private final String workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();

  static String fileError = "Error occurred during file generation: ";

  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {

    final LatencyAbc abc = (LatencyAbc) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_ABC);
    // Retrieve the MULTINODE flag
    final String multinode = parameters.get(PARAM_MULTINODE);
    // Retrieve the TOPNODE flag
    final String topnode = parameters.get(PARAM_TOPNODE);
    final String[] uriString = abc.getScenario().getScenarioURL().split("/");
    scenarioPath = "/" + uriString[1] + "/" + uriString[2] + "/generated/";
    if (bool(multinode) && bool(topnode)) {
      final String errorMessage = "Multinode is true for SimSDP subgraph simulation, Top for SimSDP Topgraph simulation. Choose one.";
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
    // Fill in the SimSDP top-graph timing file
    if (bool(multinode)) {
      exportNodeTiming(abc);
    }
    // Fill in the SimSDP node workload file
    if (bool(topnode)) {
      exportWorkload(abc);
    }
    return new LinkedHashMap<>();
  }

  private void exportWorkload(LatencyAbc abc) {
    // read timing file
    final String timPath = workspaceLocation + scenarioPath + "top_tim.csv";
    if (!timPath.isEmpty()) {
      final Map<String, Long> wl = new HashMap<>();
      final File file = new File(timPath);
      try {
        final FileReader read = new FileReader(file);
        final BufferedReader buffer = new BufferedReader(read);
        try {
          String line;
          while ((line = buffer.readLine()) != null) {
            final String[] split = line.split(";");
            final String actor = split[0];
            final Long Latency = Long.valueOf(split[1]);
            wl.put(actor, Latency);
          }
        } finally {
          buffer.close();
        }

      } catch (final IOException e) {
        final String errorMessage = fileError + timPath;
        PreesmLogger.getLogger().log(Level.INFO, errorMessage);
      }
      // compute max worload
      long maxValue = Long.MIN_VALUE;
      for (final Long value : wl.values()) {
        if (value > maxValue) {
          maxValue = value;
        }
      }
      // compute average
      long sum = 0;
      for (final Long value : wl.values()) {
        sum += value;
      }
      final long average = sum / wl.size();

      // compute sigma
      final Map<String, Long> ws = new HashMap<>();
      for (final Entry<String, Long> entry : wl.entrySet()) {
        ws.put(entry.getKey(), entry.getValue() - average);
      }
    }

    final String fileName = "node_workload.csv";
    final String path = workspaceLocation + scenarioPath + fileName;
    final StringConcatenation content = new StringConcatenation();
    content.append("Nodes;Workload;");
    content.append("\n");
    // compute max workload
    final Long maxWL = 0L;

    // fil da shit

    try (FileOutputStream outputStream = new FileOutputStream(path)) {
      final byte[] bytes = content.toString().getBytes();
      outputStream.write(bytes);
    } catch (final IOException e) {
      PreesmLogger.getLogger().log(Level.INFO, e.getMessage());
    }
  }

  private void exportNodeTiming(LatencyAbc abc) {
    final String fileName = "top_tim.csv";
    final String path = workspaceLocation + scenarioPath + fileName;
    final String firstLine = "Actors;Node;";

    final String newNodeLine = abc.getScenario().getAlgorithm().getName() + ";" + abc.getFinalLatency() + ";";
    final File file = new File(path);
    boolean emptyFile = true;
    FileReader read = null;
    try {
      read = new FileReader(file);
      final BufferedReader buffer = new BufferedReader(read);
      final String line = buffer.readLine();
      if (line != null && !line.isEmpty()) {
        emptyFile = false;
      }
      buffer.close();
      read.close();
    } catch (final IOException e) {
      PreesmLogger.getLogger().log(Level.INFO, e.getMessage());
    }

    try {
      final FileOutputStream fos = new FileOutputStream(path, true);
      final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

      if (emptyFile) {
        writer.write(firstLine + "\n");
        writer.write(newNodeLine + "\n");
        writer.close();
      } else {
        writer.write(newNodeLine + "\n");
        writer.close();
      }
    } catch (final IOException e) {
      PreesmLogger.getLogger().log(Level.INFO, e.getMessage());
    }
  }

  private boolean bool(String bool) {
    if (bool.equals("true")) {
      return true;
    }
    return false;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PARAM_MULTINODE, DEFAULT_BOOL);
    parameters.put(PARAM_TOPNODE, DEFAULT_BOOL);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Generate the stats of the SimSDP scheduling.";
  }

}
