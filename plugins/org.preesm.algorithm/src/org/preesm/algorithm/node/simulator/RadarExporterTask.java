package org.preesm.algorithm.node.simulator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class export the CSV file in order to generate the radar chart (throughput,memory,energy,cost per network) for
 * SIMSDP multinet analysis (Node simulation workflow)
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "RadarExporterTask.identifier", name = "Multicriteria Stats exporter", category = "Gantt exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class) })
public class RadarExporterTask extends AbstractTaskImplementation {

  String simGcsv          = "simgrid.csv";
  String simulationPath   = "";
  String archiPath        = "";
  String multicriteriacsv = "multicriteria.csv";
  String archiXml         = "SimSDP_network.xml";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    simulationPath = "/" + workflow.getProjectName() + "/Simulation/";
    archiPath = "/" + workflow.getProjectName() + "/Archi/";
    final LatencyAbc abc = (LatencyAbc) inputs.get("ABC");
    final Map<String, String> metrics = new HashMap<>();
    configType(metrics);
    ABC(metrics, abc);
    SimGrid(metrics);

    store(metrics);

    return new LinkedHashMap<>();
  }

  private void store(Map<String, String> metrics) {
    final String data = "type;" + metrics.get("type") + "\n" + "throughput;" + metrics.get("throughput") + "\n"
        + "memory;" + metrics.get("memory") + "\n" + "energy;" + metrics.get("energy") + "\n" + "cost;"
        + metrics.get("cost") + "\n";

    appendCSV(data, simulationPath, multicriteriacsv);
  }

  private void configType(Map<String, String> metrics) {
    final String simGridFile = PreesmIOHelper.getInstance().read(archiPath, archiXml);
    final String[] line = simGridFile.split("\n");
    final String type = line[0].replace("<!-- ", "").replace(" -->", "");
    metrics.put("type", type);
  }

  private void appendCSV(String data, String path, String fileName) {
    final StringConcatenation content = new StringConcatenation();

    // if the file exists, we write to it otherwise we create the template
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + fileName));
    if (iFile.isAccessible()) {
      content.append(PreesmIOHelper.getInstance().read(path, fileName));
      content.append(data + "\n");
      PreesmIOHelper.getInstance().print(path, fileName, content);
    } else {

      content.append(data + "\n");
      PreesmIOHelper.getInstance().print(path, fileName, content);

    }
  }

  private void SimGrid(Map<String, String> metrics) {

    final String simGridFile = PreesmIOHelper.getInstance().read(simulationPath, simGcsv);
    final String[] line = simGridFile.split("\n");
    metrics.put("cost", String.valueOf((line.length - 2) / 2));
    double ener = 0.0;
    for (int i = line.length / 2 + 1; i < line.length; i++) {
      final String[] column = line[i].split(",");
      ener += Double.valueOf(column[2]);
    }
    metrics.put("energy", String.valueOf(ener));
  }

  private void ABC(Map<String, String> metrics, LatencyAbc abc) {
    final StatGeneratorAbc statGen = new StatGeneratorAbc(abc);
    long memory = 0L;
    for (final ComponentInstance op : abc.getArchitecture().getOperatorComponentInstances()) {
      memory += statGen.getMem(op);
    }

    metrics.put("memory", String.valueOf(memory));

    metrics.put("throughput", String.valueOf(abc.getFinalLatency()));
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
