package org.preesm.algorithm.clustering;

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
    parameters = {
        @Parameter(name = "Algorithm",
            values = { @Value(name = "APGAN", effect = ""), @Value(name = "Dummy", effect = ""),
                @Value(name = "Random", effect = ""), @Value(name = "Parallel", effect = "") }),
        @Parameter(name = "Seed",
            values = { @Value(name = "$$n\\in \\mathbb{N}^*$$", effect = "Seed for random generator") }) })
public class Clustering extends AbstractTaskImplementation {

  public static final String ALGORITHM_CHOICE  = "Algorithm";
  public static final String DEFAULT_ALGORITHM = "APGAN";

  public static final String SEED_CHOICE  = "Seed";
  public static final String DEFAULT_SEED = "0";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    // Retrieve inputs and parameters
    final PiGraph graph = (PiGraph) inputs.get("PiMM");
    String algorithm = parameters.get("Algorithm");
    String seed = parameters.get("Seed");

    // Instantiate a ClusteringBuilder and process clustering
    ClusteringBuilder clusteringBuilder = new ClusteringBuilder(graph, algorithm, Long.parseLong(seed));
    Map<AbstractActor, Schedule> scheduleMapping = clusteringBuilder.processClustering();

    // Print information in console
    for (Entry<AbstractActor, Schedule> entry : scheduleMapping.entrySet()) {
      Schedule schedule = entry.getValue();
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
    outputs.put("PiMM", graph);
    outputs.put("schedules", scheduleMapping);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> defaultParams = new LinkedHashMap<>();
    defaultParams.put(ALGORITHM_CHOICE, DEFAULT_ALGORITHM);
    defaultParams.put(SEED_CHOICE, DEFAULT_SEED);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Clustering";
  }

}
