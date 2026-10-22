package org.preesm.algorithm.synthesis.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.clustering.ClusterCreationTask;
import org.preesm.algorithm.clustering.identification.ClusterIdentifier;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.APGANPiMMScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.LegacyListScheduler;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "mapper2.hybrid", name = "Hybrid Scheduling", category = "Schedulers",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_MAPPING, type = Mapping.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCHEDULE, type = Schedule.class) },

    parameters = {
      @Parameter(name = ListSchedulerTask.PARAM_EDGE_SCHED_TYPE,
          values = { @Value(name = ListSchedulerTask.VALUE_EDGE_SCHED_TYPE_SIMPLE) }),
      @Parameter(name = ListSchedulerTask.PARAM_SIMULATOR_TYPE,
          values = { @Value(name = ListSchedulerTask.VALUE_SIMULATOR_TYPE_LOOSELY_TIMED) }),
      @Parameter(name = ListSchedulerTask.PARAM_CHECK,
          values = { @Value(name = ListSchedulerTask.VALUE_TRUE), @Value(name = ListSchedulerTask.VALUE_FALSE) }),
      @Parameter(name = ListSchedulerTask.PARAM_OPTIMIZE_SYNC,
          values = { @Value(name = ListSchedulerTask.VALUE_TRUE), @Value(name = ListSchedulerTask.VALUE_FALSE) }),
      @Parameter(name = ListSchedulerTask.PARAM_BALANCE_LOADS,
          values = { @Value(name = ListSchedulerTask.VALUE_TRUE), @Value(name = ListSchedulerTask.VALUE_FALSE) }),
      @Parameter(name = ListSchedulerTask.PARAM_ENERGY_AWARNESS,
          values = { @Value(name = ListSchedulerTask.VALUE_TRUE, effect = "Turns on energy aware mapping/scheduling"),
            @Value(name = ListSchedulerTask.VALUE_FALSE) }),
      @Parameter(name = ListSchedulerTask.PARAM_ENERGY_AWARNESS_FIRST_CONFIG,
          values = {
            @Value(name = ListSchedulerTask.VALUE_ENERGY_AWARNESS_FIRST_CONFIG_FIRST,
                effect = "Takes as starting point the first valid combination of PEs"),
            @Value(name = ListSchedulerTask.VALUE_ENERGY_AWARNESS_FIRST_CONFIG_MIDDLE,
                effect = "Takes as starting point half of the available PEs"),
            @Value(name = ListSchedulerTask.VALUE_ENERGY_AWARNESS_FIRST_CONFIG_MAX,
                effect = "Takes as starting point all the available PEs"),
            @Value(name = ListSchedulerTask.VALUE_ENERGY_AWARNESS_FIRST_CONFIG_RANDOM,
                effect = "Takes as starting point a random number of PEs") }),
      @Parameter(name = ListSchedulerTask.PARAM_ENERGY_AWARNESS_SEARCH_TYPE,
          values = {
            @Value(name = ListSchedulerTask.VALUE_ENERGY_AWARNESS_SEARCH_TYPE_THOROUGH,
                effect = "Analyzes PE combinations one by one until the performance objective is reached"),
            @Value(name = ListSchedulerTask.VALUE_ENERGY_AWARNESS_SEARCH_TYPE_HALVES,
                effect = "Divides in halves the remaining available PEs and goes up/down depending"
                    + " if the FPS reached are below/above the objective") }),
      @Parameter(name = HybridSchedulerTask.PARAM_BOT_SCHEDULER,
          values = { @Value(name = HybridSchedulerTask.VALUE_BOT_SCHEDULER_APGAN, effect = "") }, description = ""),
      @Parameter(name = HybridSchedulerTask.PARAM_VERBOSE,
          values = { @Value(name = HybridSchedulerTask.VALUE_TRUE, effect = ""),
            @Value(name = HybridSchedulerTask.VALUE_FALSE, effect = "") },
          description = ""),
      @Parameter(name = HybridSchedulerTask.PARAM_DEBUG,
          values = { @Value(name = HybridSchedulerTask.VALUE_TRUE, effect = ""),
            @Value(name = HybridSchedulerTask.VALUE_FALSE, effect = "") },
          description = ""), })

public class HybridSchedulerTask extends ListSchedulerTask {

  public static final String PARAM_BOT_SCHEDULER       = "Bottom Scheduler";
  public static final String VALUE_BOT_SCHEDULER_APGAN = "APGAN";

  public static final String PARAM_VERBOSE = "Verbose";
  public static final String VALUE_TRUE    = "True";
  public static final String VALUE_FALSE   = "False";

  public static final String PARAM_DEBUG = "Debug";

  private boolean debug = false;

  private static final Logger logger = PreesmLogger.getLogger();

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting the inputs
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Design arch = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    debug = ClusterCreationTask.VALUE_TRUE.equalsIgnoreCase(parameters.get(ClusterCreationTask.PARAM_DEBUG));

    // Making the allocation for every hierarchical level of the graph.
    final SynthesisResult result = executeRecursively(graph, scenario, arch, parameters, monitor, nodeName, workflow);

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_MAPPING, result.mapping);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCHEDULE, result.schedule);

    return outputs;
  }

  SynthesisResult executeRecursively(PiGraph graph, Scenario scenario, Design arch, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    /** Choosing witch scheduler to use */
    final String botSchedulerValue = parameters.get(PARAM_BOT_SCHEDULER);

    IScheduler scheduler;
    String schedulerValue;
    if (!graph.isCluster()) {
      scheduler = new LegacyListScheduler();
      schedulerValue = "List scheduler";
    } else {
      schedulerValue = botSchedulerValue;
      scheduler = switch (botSchedulerValue) {
        case VALUE_BOT_SCHEDULER_APGAN:
          yield new APGANPiMMScheduler();
        default:
          throw new PreesmRuntimeException(PARAM_BOT_SCHEDULER + " " + botSchedulerValue
              + " doesn't exists. Choose between the following ones : " + VALUE_BOT_SCHEDULER_APGAN);
      };
    }

    /** Scheduling and Mapping current graph */
    String log = "Scheduling / Mapping graph " + graph.getName() + " with " + schedulerValue;
    PreesmLogger.getLogger().info(log);
    final SynthesisResult result = scheduler.scheduleAndMap(graph, arch, scenario, parameters);

    final Schedule currentSchedule = result.schedule;
    final Mapping currentMapping = result.mapping;

    if (debug && graph.isCluster()) {
      ClusterIdentifier.exportGraph((PiGraph) ((HierarchicalSchedule) currentSchedule).getAttachedActor(),
          "/Algo/generated/scheduling-debug/1.srdag-with-coms/", workflow);
    }

    /** Scheduling each individual direct children graph of current graph, and adding it to the result */
    for (final PiGraph child : graph.getChildrenGraphs()) {

      // We get the original subgraph in PiSDF to avoid scheduling multiple times the same subgraph.
      final PiGraph sourceChild = PreesmCopyTracker.getSource(child);
      final EMap<PiGraph, Schedule> internalSchedules = currentSchedule.getInternalSchedules();
      if (internalSchedules.containsKey(sourceChild)) {
        continue;
      }
      final SynthesisResult childResult = executeRecursively(sourceChild, scenario, arch, parameters, monitor, nodeName,
          workflow);

      final Schedule childSchedule = childResult.schedule;
      final Mapping childMapping = childResult.mapping;

      /** Merging result in parent Schedule */
      internalSchedules.put(sourceChild, childSchedule);

      /** Merging result in parent Mapping */
      if (currentMapping == null) {
        throw new PreesmRuntimeException("HybridSchedulerTask: There is a problem with the mapping");
      }

      final EList<ComponentInstance> cmpInstances = currentMapping.getMapping(child);

      for (final AbstractActor a : child.getActors()) {

        // Case if there is no info of the mapping of a
        if ((childMapping == null || childMapping.getMapping(a).isEmpty())) {
          currentMapping.getMappings().put(a, cmpInstances);

          // Case if there is not the same info of the mapping of a in current and child mapping
        } else if (childMapping != null && currentMapping.getMapping(a) != childMapping.getMapping(a)) {
          log = "Conflicts between current Mapping and child Mapping for actor " + a.getName()
              + ". Choosing the child Mapping.";

          logger.warning(log::toString);

          currentMapping.getMappings().removeKey(a);
          currentMapping.getMappings().put(a, childMapping.getMapping(a));

        }
      }
    }

    return result;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = super.getDefaultParameters();
    parameters.put(PARAM_BOT_SCHEDULER, VALUE_BOT_SCHEDULER_APGAN);

    return parameters;
  }
}
