package org.preesm.algorithm.node.simulator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "InternodeExporter.identifier", name = "ABC Gantt displayer", category = "Gantt exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class) })
public class InternodeExporter extends AbstractTaskImplementation {
  String csvLoadFolder = "/CSV/load.csv";
  String csvEnerFolder = "/CSV/ener.csv";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    final LatencyAbc abc = (LatencyAbc) inputs.get("ABC");
    final Map<String, String> metrics = new HashMap<>();
    ABC(metrics, abc);
    SimGrid(metrics);

    return new LinkedHashMap<>();
  }

  private void SimGrid(Map<String, String> metrics) {
    final Long cost = parseSim(csvLoadFolder);
    metrics.put("cost", String.valueOf(cost));
    final Long energy = parseSim(csvEnerFolder);
    metrics.put("energy", String.valueOf(energy));
  }

  private Long parseSim(String path) {
    return 0L;
  }

  private void ABC(Map<String, String> metrics, LatencyAbc abc) {
    final StatGeneratorAbc statGen = new StatGeneratorAbc(abc);
    long memory = 0L;
    for (final ComponentInstance op : abc.getArchitecture().getOperatorComponentInstances()) {
      memory += statGen.getMem(op);
    }

    metrics.put("memory", String.valueOf(memory));

    metrics.put("througput", String.valueOf(abc.getFinalLatency()));
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
