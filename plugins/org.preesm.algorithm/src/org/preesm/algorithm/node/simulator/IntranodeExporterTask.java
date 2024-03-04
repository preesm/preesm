package org.preesm.algorithm.node.simulator;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.gantt.GanttComponent;
import org.preesm.algorithm.mapper.gantt.GanttTask;
import org.preesm.algorithm.mapper.ui.stats.StatGeneratorAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class export the CSV file in order to generate the bar chart (occupation, speedup) for SIMSDP intra-node
 * analysis (Thread partitioning workflow)
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "IntranodeExporterTask.identifier", name = "Intranode Stats exporter", category = "CSV exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class), @Port(name = "cMem", type = Map.class) },

    shortDescription = "This task exports scheduling results as a *.csv file .")

public class IntranodeExporterTask extends AbstractTaskImplementation {

  public static final String OCCUPATION_NAME = "occupation_trend.csv";
  public static final String SPEEDUP_NAME    = "speedup_trend.csv";

  public static final String MEMORY_NAME = "memory_trend.csv";
  private String             path        = "";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    path = File.separator + workflow.getProjectName() + "/Simulation/";
    final LatencyAbc abc = (LatencyAbc) inputs.get("ABC");
    final Map<Actor, Long> clusterMemory = (Map<Actor, Long>) inputs.get("cMem");
    // export
    if (workflow.getWorkflowName().equals("ThreadPartitioning.workflow")) {
      nodeOccupationExport(abc);
      nodeSpeedupExport(abc);
      nodeMemoryExport(abc, clusterMemory);
    }
    return new LinkedHashMap<>();
  }

  private void nodeMemoryExport(LatencyAbc abc, Map<Actor, Long> clusterMemory) {
    final StatGeneratorAbc stat = new StatGeneratorAbc(abc);
    long memory = 0L;
    // retrieve intranode memory from ABC estimator (sum of all buffers allocated by the mapping)
    for (final ComponentInstance op : abc.getArchitecture().getOperatorComponentInstances()) {
      memory += stat.getMem(op);
    }
    // retrieve memory inside cluster
    for (final Entry<Actor, Long> i : clusterMemory.entrySet()) {
      memory += i.getValue();
    }

    csvTrend(String.valueOf(memory), abc, MEMORY_NAME);

  }

  private void nodeSpeedupExport(LatencyAbc abc) {
    final StatGeneratorAbc stat = new StatGeneratorAbc(abc);

    final Long workLength = stat.getDAGWorkLength();
    final long spanLength = stat.getDAGSpanLength();
    final Long implem = stat.getFinalTime();
    final Double currentSpeedup = (double) (workLength) / (double) (implem);

    Double maxSpeedup;
    final int nbCore = abc.getArchitecture().getOperatorComponentInstances().size();
    final double absoluteBestSpeedup = ((double) workLength) / ((double) spanLength);

    if (nbCore < absoluteBestSpeedup) {
      maxSpeedup = (double) nbCore;
    } else {
      maxSpeedup = absoluteBestSpeedup;
    }
    final String data = currentSpeedup + ";" + maxSpeedup;

    csvTrend(data, abc, SPEEDUP_NAME);
  }

  private void nodeOccupationExport(LatencyAbc abc) {
    final StatGeneratorAbc stat = new StatGeneratorAbc(abc);
    long sum = 0L;
    Long max = Long.MIN_VALUE;
    for (final ComponentInstance ci : abc.getArchitecture().getOperatorComponentInstances()) {
      sum += stat.getLoad(ci);
      max = Math.max(stat.getLoad(ci), max);
    }

    final Double occupy = sum / (double) (max * abc.getArchitecture().getOperatorComponentInstances().size());
    final String message = "Node occupation is: " + occupy;
    PreesmLogger.getLogger().log(Level.INFO, message);
    for (final GanttComponent ci : abc.getGanttData().getComponents()) {
      Long sumCpt = 0L;
      Long sumCom = 0L;
      for (final GanttTask a : ci.getTasks()) {
        if (a.getColor() == null) {
          sumCpt += a.getDuration();
        } else {
          sumCom += a.getDuration();
        }
      }
      final String message2 = "Computation sum is:" + sumCpt + " ,and communication sum is:" + sumCom;
      PreesmLogger.getLogger().log(Level.INFO, message2);
    }
    csvTrend(occupy.toString(), abc, OCCUPATION_NAME);
  }

  private void csvTrend(String data, LatencyAbc abc, String fileName) {
    if (!abc.getArchitecture().getVlnv().getName().equals("top")) {
      String content = "";
      final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + fileName));
      final int nodeID = Integer.decode(abc.getArchitecture().getVlnv().getName().replace("Node", ""));
      if (iFile.isAccessible()) {
        String originalString = PreesmIOHelper.getInstance().read(path, fileName);
        if (originalString.endsWith("\n")) {
          originalString = originalString.substring(0, originalString.length() - 1); // Retire le dernier "\n"
        }
        if (nodeID == 0) {
          content += originalString + "\n" + data;
        } else {
          content += originalString + ";" + data;
        }
      } else {
        content += data;
      }
      PreesmIOHelper.getInstance().print(path, fileName, content);
    }
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Generate the stats of multinode scheduling (intranode).";
  }

}
