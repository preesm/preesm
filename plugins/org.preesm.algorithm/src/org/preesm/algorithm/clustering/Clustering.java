package org.preesm.algorithm.clustering;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 *
 *
 */
@PreesmTask(id = "org.ietr.preesm.pisdfclustering", name = "Clustering",

    inputs = { @Port(name = "PiMM", type = PiGraph.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "schedules", type = Map.class) },
    description = "Workflow task responsible for clustering hierarchical actors.",
    parameters = { @Parameter(name = "Algorithm", values = { @Value(name = "APGAN", effect = ""),
        @Value(name = "Dummy", effect = ""), @Value(name = "Random", effect = "") }) })
public class Clustering extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    // Retrieve inputs and parameters
    final PiGraph algorithm = (PiGraph) inputs.get("PiMM");
    String clusteringAlgorithm = parameters.get("Algorithm");

    // Instantiate a ClusteringBuilder and process clustering
    ClusteringBuilder clusteringBuilder = new ClusteringBuilder(algorithm, clusteringAlgorithm);
    Map<AbstractActor, Schedule> scheduleMapping = clusteringBuilder.processClustering();

    // Perform parallelism depth limitation and print information in console
    for (Entry<AbstractActor, Schedule> entry : scheduleMapping.entrySet()) {
      // Depth control
      Schedule schedule = entry.getValue();
      schedule = ClusteringHelper.setParallelismDepth(schedule, 0 /* depth-iterator */, 1 /* depth */);
      scheduleMapping.replace(entry.getKey(), schedule);
      // Printing
      String scheduleStr = "Schedule for cluster " + entry.getKey().getName() + ":";
      PreesmLogger.getLogger().log(Level.INFO, scheduleStr);
      scheduleStr = schedule.shortPrint();
      PreesmLogger.getLogger().log(Level.INFO, scheduleStr);
      scheduleStr = "Estimated memory space needed: " + ClusteringHelper.getMemorySpaceNeededFor(schedule) + " bytes";
      PreesmLogger.getLogger().log(Level.INFO, scheduleStr);
    }

    // Output PiSDF and Schedule Mapping attachment
    Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("PiMM", algorithm);
    outputs.put("schedules", scheduleMapping);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Clustering";
  }

}
