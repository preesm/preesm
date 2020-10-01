/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
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
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Cluster Scheduler Task
 * 
 * @author dgageot
 *
 */
@PreesmTask(id = "cluster-scheduler", name = "Cluster Scheduler",
    inputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Input PiSDF graph"),
        @Port(name = "scenario", type = Scenario.class, description = "Scenario") },
    outputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Output PiSDF graph"),
        @Port(name = "CS", type = Map.class, description = "Map of Cluster Schedule") },
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
                @Value(name = "Performance", effect = "Maximize performance of resulting clusters") }),
        @Parameter(name = "Parallelism",
            description = "Specify if resulting Cluster Schedules have to contain parallelism information.",
            values = { @Value(name = "True", effect = "Cluster Schedules contain data parallelism information."),
                @Value(name = "False", effect = "Cluster Schedules are purely sequential.") }) })
public class ClusterSchedulerTask extends AbstractTaskImplementation {

  public static final String TARGET_CHOICE        = "Target";
  public static final String TARGET_ONLY_CLUSTERS = "Cluster";
  public static final String TARGET_INPUT_GRAPH   = "Input graph";
  public static final String DEFAULT_TARGET       = TARGET_ONLY_CLUSTERS;

  public static final String OPTIMIZATION_CHOICE      = "Optimization criteria";
  public static final String OPTIMIZATION_MEMORY      = "Memory";
  public static final String OPTIMIZATION_PERFORMANCE = "Performance";
  public static final String DEFAULT_OPTIMIZATION     = OPTIMIZATION_PERFORMANCE;

  public static final String PARALLELISM_CHOICE  = "Parallelism";
  public static final String PARALLELISM_FALSE   = "False";
  public static final String PARALLELISM_TRUE    = "True";
  public static final String DEFAULT_PARALLELISM = PARALLELISM_TRUE;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Task inputs
    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
    Scenario scenario = (Scenario) inputs.get("scenario");

    // Parameters
    String targetParameter = parameters.get(TARGET_CHOICE);
    String optimizationParameter = parameters.get(OPTIMIZATION_CHOICE);
    boolean optimizePerformance = optimizationParameter.contains(OPTIMIZATION_PERFORMANCE);
    String parallelismParameter = parameters.get(PARALLELISM_CHOICE);
    boolean parallelism = parallelismParameter.contains(PARALLELISM_TRUE);

    // Build output map
    Map<String, Object> output = new HashMap<>();

    // Depending on the type of target, schedule the whole graph or just clusters.
    Map<AbstractActor, Schedule> scheduleMap = null;
    if (targetParameter.contains(TARGET_INPUT_GRAPH)) {
      PreesmLogger.getLogger().log(Level.INFO, "Scheduling the input graph.");
      PGANScheduler scheduler = new PGANScheduler(inputGraph, scenario, optimizePerformance, parallelism);
      scheduleMap = scheduler.scheduleInputGraph();
    } else {
      PreesmLogger.getLogger().log(Level.INFO, "Scheduling clusters.");
      scheduleMap = ClusterScheduler.schedule(inputGraph, scenario, optimizePerformance, parallelism);
    }

    // Print schedule results in console
    for (Entry<AbstractActor, Schedule> entry : scheduleMap.entrySet()) {
      String str = "Schedule for " + entry.getKey().getName() + ":";
      PreesmLogger.getLogger().log(Level.INFO, str);
      str = entry.getValue().shortPrint();
      PreesmLogger.getLogger().log(Level.INFO, str);
    }

    // Register outputs
    output.put("CS", scheduleMap);
    output.put("PiMM", inputGraph);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> defaultParams = new LinkedHashMap<>();
    defaultParams.put(TARGET_CHOICE, DEFAULT_TARGET);
    defaultParams.put(OPTIMIZATION_CHOICE, DEFAULT_OPTIMIZATION);
    defaultParams.put(PARALLELISM_CHOICE, DEFAULT_PARALLELISM);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Cluster Scheduler Task";
  }

}
