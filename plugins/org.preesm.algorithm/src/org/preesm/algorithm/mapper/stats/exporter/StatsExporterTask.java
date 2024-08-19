/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorAbc;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.brv.BRVExporter;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Generate an xml file containing the stats from the mapping/scheduling steps.
 *
 * TODO: remove scenario from inputs (already contained in ABC)
 *
 * @author cguy
 * @author orenaud
 */
@PreesmTask(id = "org.ietr.preesm.stats.exporter.StatsExporterTask", name = "ABC Gantt exporter",
    category = "Gantt exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class), @Port(name = "scenario", type = Scenario.class) },

    shortDescription = "This task exports scheduling results as a *.pgantt file that can be "
        + "viewed using the ganttDisplay viewer [1].",

    parameters = {
        @Parameter(name = StatsExporterTask.PARAM_PATH,
            description = "Path of the exported *.pgantt file. If the specified directory does not exist, it will "
                + "not be created.",
            values = { @Value(name = StatsExporterTask.DEFAULT_PATH,
                effect = "Path within the Preesm project containing the workflow where the ”Gantt Exporter” task is "
                    + "instantiated. Exported Gantt will be named as follows: "
                    + "**/path/in/proj/<scenario name> stats.pgantt**. If a graph with this name already exists in "
                    + "the given path, it will be overwritten.") }),
        @Parameter(name = "Multinode", description = "oué", values = { @Value(name = "true/false", effect = "oué") }),
        @Parameter(name = "Top", description = "oué", values = { @Value(name = "true/false", effect = "oué") }) },

    description = "This task exports scheduling results as a *.pgantt file that can be viewed using the ganttDisplay"
        + " viewer [1]. The exported *.pgantt file uses the XML syntax.",

    seeAlso = { "**[1]**: https://github.com/preesm/gantt-display" })
public class StatsExporterTask extends AbstractTaskImplementation {

  /**
   * @see BRVExporter
   */
  public static final String DEFAULT_PATH = "/stats/xml/";

  public static final String PARAM_PATH = "path";

  /** The Constant PARAM_MULTINODE. */
  public static final String PARAM_MULTINODE = "Multinode";

  /** The Constant PARAM_MULTINODE. */
  public static final String PARAM_TOPNODE = "Top";

  private final String workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
  private String       scenarioPath      = "";
  static String        fileError         = "Error occurred during file generation: ";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final LatencyAbc abc = (LatencyAbc) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_ABC);
    String folderPath = parameters.get(PARAM_PATH);

    // Retrieve the MULTINODE flag
    final String multinode = parameters.get(StatsExporterTask.PARAM_MULTINODE);
    // Retrieve the TOPNODE flag
    final String topnode = parameters.get(StatsExporterTask.PARAM_TOPNODE);

    // Get the root of the workspace
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    // Get the project
    final String projectName = workflow.getProjectName();
    final IProject project = root.getProject(projectName);

    // Get a complete valid path with all folders existing
    folderPath = project.getLocation() + folderPath;
    final File parent = new File(folderPath);
    parent.mkdirs();

    final String filePath = abc.getScenario().getScenarioName() + "_stats_pgantt.xml";
    final File file = new File(parent, filePath);
    // Generate the stats from the abc and write them in a file at xmlPath
    final StatGeneratorAbc statGen = new StatGeneratorAbc(abc);

    XMLStatsExporter.exportXMLStats(file, statGen);
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

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PARAM_PATH, DEFAULT_PATH);
    parameters.put(StatsExporterTask.PARAM_MULTINODE, "false");
    parameters.put(StatsExporterTask.PARAM_TOPNODE, "false");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Generate the stats of the scheduling.";
  }

}
