package org.preesm.algorithm.node.simulator;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Preesm task for exporting CSV files to generate a radar chart (final latency, memory, energy, cost per network) for
 * SIMSDP multinet analysis (Node simulation workflow).
 *
 * This task takes input metrics, processes them, and exports the results to CSV files. The exported CSV files are used
 * to generate a radar chart representing various criteria for network analysis.
 *
 * @author orenaud
 */
@PreesmTask(id = "RadarExporterTask.identifier", name = "Multicriteria Stats exporter", category = "Gantt exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class) })

public class RadarExporterTask extends AbstractTaskImplementation {
  // File names and paths
  String simGcsv          = "simgrid.csv";
  String simulationPath   = "";
  String archiPath        = "";
  String multicriteriacsv = "multicriteria.csv";
  String archiXml         = "SimSDP_network.xml";

  public static final String TYPE          = "type";
  public static final String FINAL_LATENCY = "finalLatency";
  public static final String ENERGY        = "energy";
  public static final String COST          = "cost";
  public static final String MEMORY        = "memory";

  /**
   * Executes the radar exporter task.
   *
   * @param inputs
   *          Input parameters for the task.
   * @param parameters
   *          Additional parameters for the task.
   * @param monitor
   *          Progress monitor for the task execution.
   * @param nodeName
   *          Name of the node executing the task.
   * @param workflow
   *          The workflow in which the task is executed.
   * @return A map containing the results of the task execution.
   * @throws InterruptedException
   *           If the execution is interrupted.
   */
  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    simulationPath = File.separator + workflow.getProjectName() + "/Simulation/";
    archiPath = File.separator + workflow.getProjectName() + "/Archi/";
    final LatencyAbc abc = (LatencyAbc) inputs.get("ABC");
    final Map<String, String> metrics = new HashMap<>();
    configType(metrics);
    abcResult(metrics, abc);
    if (ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(simulationPath + simGcsv)).isAccessible()) {
      simgrid(metrics);
    } else {
      preesm(metrics, abc);
    }

    store(metrics);

    return new LinkedHashMap<>();
  }

  /**
   * Processes the metrics and updates the map with Preesm-specific values.
   *
   * @param metrics
   *          The map containing the metrics.
   * @param abc
   *          The input LatencyAbc object.
   */
  private void preesm(Map<String, String> metrics, LatencyAbc abc) {

    metrics.put(COST, String.valueOf(abc.getArchitecture().getProcessingElements().size()));
    metrics.put(ENERGY, "1");
    Long maxLoad = Long.MIN_VALUE;
    for (final ComponentInstance cp : abc.getArchitecture().getComponentInstances()) {
      maxLoad = Math.max(abc.getLoad(cp), maxLoad);

    }
    metrics.put(FINAL_LATENCY, String.valueOf(maxLoad));
  }

  /**
   * Stores the processed metrics in a CSV file.
   *
   * @param metrics
   *          The map containing the metrics.
   */
  private void store(Map<String, String> metrics) {
    final StringBuilder data = new StringBuilder();
    data.append(TYPE + ";" + metrics.get(TYPE) + "\n");
    data.append(FINAL_LATENCY + ";" + metrics.get(FINAL_LATENCY) + "\n");
    data.append(MEMORY + ";" + metrics.get(MEMORY) + "\n");
    data.append(ENERGY + ";" + metrics.get(ENERGY) + "\n");
    data.append(COST + ";" + metrics.get(COST) + "\n");

    PreesmIOHelper.getInstance().append(simulationPath, multicriteriacsv, data.toString());
  }

  /**
   * Configures the type of simulation and updates the metrics map.
   *
   * @param metrics
   *          The map containing the metrics.
   */
  private void configType(Map<String, String> metrics) {
    final String simGridFile = PreesmIOHelper.getInstance().read(archiPath, archiXml);
    final String[] line = simGridFile.split("\n");
    final String type = line[0].replace("<!-- ", "").replace(" -->", "");
    metrics.put(TYPE, type);
  }

  /**
   * Processes metrics from a SimGrid CSV file and updates the metrics map.
   *
   * @param metrics
   *          The map containing the metrics.
   */
  private void simgrid(Map<String, String> metrics) {

    final String simGridFile = PreesmIOHelper.getInstance().read(simulationPath, simGcsv);
    final String[] line = simGridFile.split("\n");
    metrics.put(COST, String.valueOf((line.length - 2) / 2));
    double ener = 0.0;
    for (final String element : line) {
      final String[] column = element.split(",");
      if (column[1].equals("energy (J)")) {
        ener += Double.valueOf(column[2]);
      }
    }
    metrics.put(ENERGY, String.valueOf(ener));
    final double latency = Double.parseDouble(line[1].split(",")[2]);
    metrics.put(FINAL_LATENCY, String.valueOf(latency));
  }

  /**
   * Processes metrics from LatencyAbc and updates the metrics map.
   *
   * @param metrics
   *          The map containing the metrics.
   * @param abc
   *          The input LatencyAbc object.
   */
  private void abcResult(Map<String, String> metrics, LatencyAbc abc) {
    final StatGeneratorAbc statGen = new StatGeneratorAbc(abc);
    long memory = 0L;
    for (final ComponentInstance op : abc.getArchitecture().getOperatorComponentInstances()) {
      memory += statGen.getMem(op);
    }

    metrics.put(MEMORY, String.valueOf(memory));

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Generate chart of multinode scheduling.";
  }

}