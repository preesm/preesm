package org.preesm.algorithm.node.simulator;

import java.util.LinkedHashMap;
import java.util.Map;
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

    inputs = { @Port(name = "ABC", type = LatencyAbc.class) },

    shortDescription = "This task exports scheduling results as a *.csv file .")
public class IntranodeExporterTask extends AbstractTaskImplementation {
  public static final String OCCUPATION_NAME = "occupation_trend.csv";
  public static final String SPEEDUP_NAME    = "speedup_trend.csv";
  static String              path            = "";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    path = "/" + workflow.getProjectName() + "/Scenarios/generated/";
    final LatencyAbc abc = (LatencyAbc) inputs.get("ABC");

    // final IEditorInput input = new StatEditorInput(new StatGeneratorAbc(abc));
    // export
    if (workflow.getWorkflowName().equals("ThreadPartitioning.workflow")) {
      nodeOccupationExport(abc);
      nodeSpeedupExport(abc);
    }
    return new LinkedHashMap<>();
  }

  private void nodeSpeedupExport(LatencyAbc abc) {
    final StatGeneratorAbc stat = new StatGeneratorAbc(abc);

    final Long workLength = stat.getDAGWorkLength();
    final long spanLength = stat.getDAGSpanLength();
    final Long implem = stat.getFinalTime();
    final Double currentSpeedup = (double) (workLength) / (double) (implem);

    Double maxSpeedup = 0d;
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
    final Double occupy = (double) (sum)
        / (double) (max * abc.getArchitecture().getOperatorComponentInstances().size());

    PreesmLogger.getLogger().log(Level.INFO, "Node occupation ==> " + occupy);
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
      PreesmLogger.getLogger().log(Level.INFO, "Computation sum ==> " + sumCpt);
      PreesmLogger.getLogger().log(Level.INFO, "Communication sum ==> " + sumCom);
    }
    csvTrend(occupy.toString(), abc, OCCUPATION_NAME);
  }

  private void csvTrend(String data, LatencyAbc abc, String fileName) {
    if (!abc.getArchitecture().getVlnv().getName().equals("top")) {
      // final StringConcatenation content = new StringConcatenation();
      String content = "";
      final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + fileName));
      final int nodeID = Integer.decode(abc.getArchitecture().getVlnv().getName().replace("Node", ""));
      if (iFile.isAccessible()) {
        String originalString = PreesmIOHelper.getInstance().read(path, fileName);
        if (originalString.endsWith("\n")) {
          originalString = originalString.substring(0, originalString.length() - 1); // Retire le dernier "\n"
        }
        // content.append(PreesmIOHelper.getInstance().read(path, fileName));
        if (nodeID == 0) {
          content += originalString + "\n" + data;
          // content.append(PreesmIOHelper.getInstance().read(path, fileName) + "\n" + data);
        } else {
          content += originalString + ";" + data;
          // content.append(PreesmIOHelper.getInstance().read(path, fileName) + ";" + data);
        }
      } else {
        content += data;
        // content.append(data);
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
