package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.schedule.SchedulePrinterSwitch;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Cluster Scheduler Task
 * 
 * @author dgageot
 *
 */
@PreesmTask(id = "cluster-scheduler", name = "Cluster Scheduler",
    inputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Input PiSDF graph") },
    outputs = { @Port(name = "CSs", type = Map.class, description = "Map of Cluster Schedule") },
    parameters = {
        @Parameter(name = "Target",
            description = "Choose if the whole input graph will be scheduled rather than just clusters.",
            values = { @Value(name = "Cluster", effect = "Clusters are scheduled."),
                @Value(name = "Input graph", effect = "Input graph is scheduled.") }),
        @Parameter(name = "Optimization criteria",
            description = "Specify the criteria to optimize. If memory is choosen, some parallelizable "
                + "actors will be sequentialized to minimize memory space. On the other hand, if performance "
                + "is choosen, the algorithm will exploit every parallelism possibility.",
            values = { @Value(name = "Memory", effect = "Minimize memory space of resulting clusters"),
                @Value(name = "Performance", effect = "Maximize performance of resulting clusters") }) })
public class ClusterSchedulerTask extends AbstractTaskImplementation {

  public static final String TARGET_CHOICE        = "Target";
  public static final String TARGET_ONLY_CLUSTERS = "Cluster";
  public static final String TARGET_INPUT_GRAPH   = "Input graph";
  public static final String DEFAULT_TARGET       = TARGET_ONLY_CLUSTERS;

  public static final String OPTIMIZATION_CHOICE      = "Optimization criteria";
  public static final String OPTIMIZATION_MEMORY      = "Memory";
  public static final String OPTIMIZATION_PERFORMANCE = "Performance";
  public static final String DEFAULT_OPTIMIZATION     = OPTIMIZATION_PERFORMANCE;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Retrieve input graph and parameter
    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    String targetParameter = parameters.get(TARGET_CHOICE);

    // Build output map
    Map<String, Object> output = new HashMap<>();

    // Depending on the type of target, schedule the whole graph or clusters.
    if (targetParameter.contains(TARGET_INPUT_GRAPH)) {
      PreesmLogger.getLogger().log(Level.INFO, "Scheduling the input graph.");
      PGANScheduler scheduler = new PGANScheduler(inputGraph);
      Schedule schedule = scheduler.scheduleInputGraph();
      output.put("CSs", scheduler.getScheduleMap());
      // Print resulting schedule (DEGUB)
      PreesmLogger.getLogger().log(Level.INFO, SchedulePrinterSwitch.print(schedule));
    } else {
      PreesmLogger.getLogger().log(Level.INFO, "Scheduling clusters.");
      output.put("CSs", ClusterScheduler.schedule(inputGraph));
    }

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> defaultParams = new LinkedHashMap<>();
    defaultParams.put(TARGET_CHOICE, DEFAULT_TARGET);
    dafaultParams.put(OPTIMIZATION_CHOICE, DEFAULT_OPTIMIZATION);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Cluster Scheduler Task";
  }

}
