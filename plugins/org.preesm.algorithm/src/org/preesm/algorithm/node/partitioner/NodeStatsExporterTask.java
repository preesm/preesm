package org.preesm.algorithm.node.partitioner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Generate an CSV file containing the stats from the mapping/scheduling steps.
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "NodeStatsExporterTask.identifier", name = "ABC Node exporter", category = "CSV exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class) },

    shortDescription = "This task exports scheduling results as a *.csv file .",

    parameters = {

        @Parameter(name = "Multinode", description = "Fill in the SimSDP top-graph timing file",
            values = { @Value(name = "true/false", effect = "Enable to fill in the SimSDP top-graph timing file") }),
        @Parameter(name = "Top", description = "Fill in the SimSDP node workload file",
            values = { @Value(name = "true/false", effect = "Enable to fill in the SimSDP node workload file") }) })
public class NodeStatsExporterTask extends AbstractTaskImplementation {

  /** The Constant PARAM_MULTINODE. */

  public static final String PARAM_MULTINODE = "Multinode";

  /** The Constant PARAM_MULTINODE. */

  public static final String PARAM_TOPNODE = "Top";

  public static final String FILE_NAME = "workload.csv";

  private String scenarioPath = "";
  static String  fileError    = "Error occurred during file generation: ";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final LatencyAbc abc = (LatencyAbc) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_ABC);
    // Retrieve the MULTINODE flag
    final String multinode = parameters.get(NodeStatsExporterTask.PARAM_MULTINODE);
    // Retrieve the TOPNODE flag
    final String topnode = parameters.get(NodeStatsExporterTask.PARAM_TOPNODE);
    final String[] uriString = abc.getScenario().getScenarioURL().split("/");
    scenarioPath = "/" + uriString[1] + "/" + uriString[2] + "/generated/";

    if (Boolean.valueOf(multinode) && Boolean.valueOf(topnode)) {
      final String errorMessage = "Multinode is true for SimSDP subgraph's simulation, "
          + "Top is true for SimSDP Topgraph's simulation, not both, choose your camp";
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
    // Fill in the SimSDP top-graph timing file
    if (Boolean.TRUE.equals(Boolean.valueOf(multinode))) {
      exportNodeTiming(abc);
    }
    // Fill in the SimSDP node workload file
    if (Boolean.TRUE.equals(Boolean.valueOf(topnode))) {
      final Map<String, Double> wl = convertABC(abc);
      final Double latency = (double) abc.getFinalLatency();
      NodeCSVExporter.exportWorkload(wl, latency, scenarioPath);
      // exportWorkload(wl, latency);
    }
    return new LinkedHashMap<>();
  }

  /**
   * The method check the inter-node workload and latency convergence
   *
   * @param abc
   *          the PREESM inter-node simulation
   * @return export CSV file
   */
  private Map<String, Double> convertABC(LatencyAbc abc) {
    // read (normally SimGrid CSV simulation file) but here PREESM inter-node simulation
    final Map<String, Double> wl = new HashMap<>();
    // retrieve inter-node workload |nodename|workload|
    for (final ComponentInstance cp : abc.getArchitecture().getComponentInstances()) {
      wl.put("node" + cp.getHardwareId(), (double) abc.getLoad(cp));
    }

    return wl;

  }

  /**
   * The method fills a CSV file storing the simulation of each node for SimSDP
   *
   * @param abc
   *          the PREESM intra-node simulation
   * @return export CSV file
   */
  private void exportNodeTiming(LatencyAbc abc) {
    final String fileName = "top_tim.csv";
    final String filePath = scenarioPath;
    final StringConcatenation content = new StringConcatenation();

    // if the file exists, we write to it otherwise we create the template
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath + fileName));
    if (!iFile.exists()) {
      content.append("Actors;Node;\n");
    } else {

      InputStream inputStream;
      try {
        inputStream = iFile.getContents();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
          content.append(line + "\n");
        }
        inputStream.close();
      } catch (CoreException | IOException e) {
        throw new PreesmRuntimeException("Could not generate source file for " + fileName, e);
      }
    }
    content.append(abc.getScenario().getAlgorithm().getName() + ";" + abc.getFinalLatency() + "; \n");
    PreesmIOHelper.getInstance().print(scenarioPath, fileName, content);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(NodeStatsExporterTask.PARAM_MULTINODE, "false");
    parameters.put(NodeStatsExporterTask.PARAM_TOPNODE, "false");
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Generate the stats of multinode scheduling.";
  }

}
