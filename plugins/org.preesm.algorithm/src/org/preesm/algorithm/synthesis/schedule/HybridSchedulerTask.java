package org.preesm.algorithm.synthesis.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.communications.ICommunicationInserter;
import org.preesm.algorithm.synthesis.communications.OptimizedCommunicationInserter;
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
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
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

      @Parameter(name = HybridSchedulerTask.PARAM_TOP_THRESHOLD, values = {
        @Value(name = "0", effect = "The top scheduler will never be called, only the bottom scheduler."),
        @Value(name = "1",
            effect = "Default value. The top scheduler will be called on the first level of the graph, "
                + "and the bottom scheduler will be called on every graph under it. The value can be 2, 3, or more, "
                + "and the top scheduler will be used for graphs of depth of 2, 3 or more, respectively."),
        @Value(name = HybridSchedulerTask.VALUE_TOP_THRESHOLD_ALL,
            effect = "This value garanties that the bottom scheduler will never be used, and that the top scheduler"
                + " will be called for every graphs, no matter their depth") },
          description = """

              """),

      @Parameter(name = HybridSchedulerTask.PARAM_BOT_SCHEDULER,
          values = { @Value(name = HybridSchedulerTask.VALUE_BOT_SCHEDULER_APGAN, effect = "") }, description = """

              """),

    })
public class HybridSchedulerTask extends ListSchedulerTask {

  public static final String PARAM_TOP_THRESHOLD     = "Top Threshold";
  public static final String VALUE_TOP_THRESHOLD_ALL = "all";

  public static final String PARAM_BOT_SCHEDULER       = "Bottom Scheduler";
  public static final String VALUE_BOT_SCHEDULER_APGAN = "APGAN";

  public static final String PARAM_VERBOSE = "Verbose";

  private static final Logger logger = PreesmLogger.getLogger();

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting the inputs
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Design arch = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    // Computing the threshold
    final String thresholdValue = parameters.get(PARAM_TOP_THRESHOLD);
    long depthThreshold;
    if (thresholdValue.equals(VALUE_TOP_THRESHOLD_ALL)) {
      depthThreshold = PiSDFFlattener.computeGraphMaxDepth(graph);
    } else {
      depthThreshold = Long.parseLong(thresholdValue);
    }

    // Making the allocation for every hierarchical level of the graph.
    final SynthesisResult result = executeRecursively(graph, scenario, arch, depthThreshold, 0, parameters, monitor,
        nodeName, workflow);

    PreesmLogger.getLogger().log(Level.INFO, " -- Insert communication");
    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(graph, result.schedule);
    final ICommunicationInserter comIns = new OptimizedCommunicationInserter(scheduleOM);
    comIns.insertCommunications(graph, arch, scenario, result.schedule, result.mapping);

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_MAPPING, result.mapping);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCHEDULE, result.schedule);

    return outputs;
  }

  SynthesisResult executeRecursively(PiGraph graph, Scenario scenario, Design arch, long threshold, long currentDepth,
      Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow) {

    /** Choosing witch scheduler to use */
    final String botSchedulerValue = parameters.get(PARAM_BOT_SCHEDULER);

    IScheduler scheduler;
    String schedulerValue;
    if (currentDepth < threshold) {
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
    PreesmLogger.getLogger().info("Scheduling / Mapping graph " + graph.getName() + " with " + schedulerValue);
    final SynthesisResult result = scheduler.scheduleAndMap(graph, arch, scenario, parameters);

    final Schedule currentSchedule = result.schedule;
    final Mapping currentMapping = result.mapping;

    /** Scheduling each individual direct children graph of current graph, and adding it to the result */
    for (final PiGraph child : graph.getChildrenGraphs()) {

      // We get the original subgraph in PiSDF to avoid scheduling multiple times the same subgraph.
      final PiGraph oriChild = PreesmCopyTracker.getOriginalSource(child);
      final EMap<PiGraph, Schedule> internalSchedules = currentSchedule.getInternalSchedules();
      if (internalSchedules.containsKey(oriChild)) {
        continue;
      }
      final SynthesisResult childResult = executeRecursively(oriChild, scenario, arch, threshold, currentDepth + 1,
          parameters, monitor, nodeName, workflow);

      final Schedule childSchedule = childResult.schedule;
      final Mapping childMapping = childResult.mapping;

      /** Merging result in parent Schedule */
      internalSchedules.put(oriChild, childSchedule);

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
          final StringBuilder log = new StringBuilder();
          log.append("Conflicts between current Mapping and child Mapping for actor ");
          log.append(a.getName());
          log.append(". Choosing the child Mapping.");

          logger.log(Level.WARNING, log.toString());

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
    parameters.put(PARAM_TOP_THRESHOLD, "1");
    parameters.put(PARAM_BOT_SCHEDULER, VALUE_BOT_SCHEDULER_APGAN);

    return parameters;
  }
}
