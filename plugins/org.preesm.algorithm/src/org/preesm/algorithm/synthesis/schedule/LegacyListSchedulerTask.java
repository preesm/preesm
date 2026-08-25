package org.preesm.algorithm.synthesis.schedule;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.LegacyListScheduler;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "mapper2.list", name = "List Scheduling from PiSDF", category = "Schedulers",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },

    outputs = { @Port(name = "Mapping", type = Mapping.class), @Port(name = "Schedule", type = Schedule.class) },

    parameters = { @Parameter(name = "edgeSchedType", values = { @Value(name = "Simple") }),
      @Parameter(name = "simulatorType", values = { @Value(name = "LooselyTimed") }),
      @Parameter(name = "Check", values = { @Value(name = "True"), @Value(name = "False") }),
      @Parameter(name = "Optimize synchronization", values = { @Value(name = "True"), @Value(name = "False") }),
      @Parameter(name = "balanceLoads", values = { @Value(name = "True"), @Value(name = "False") }),
      @Parameter(name = "EnergyAwareness",
          values = { @Value(name = "True", effect = "Turns on energy aware mapping/scheduling"),
            @Value(name = "False") }),
      @Parameter(name = "EnergyAwarenessFirstConfig",
          values = { @Value(name = "First", effect = "Takes as starting point the first valid combination of PEs"),
            @Value(name = "Middle", effect = "Takes as starting point half of the available PEs"),
            @Value(name = "Max", effect = "Takes as starting point all the available PEs"),
            @Value(name = "Random", effect = "Takes as starting point a random number of PEs") }),
      @Parameter(name = "EnergyAwarenessSearchType",
          values = {
            @Value(name = "Thorough",
                effect = "Analyzes PE combinations one by one until the performance objective is reached"),
            @Value(name = "Halves", effect = "Divides in halves the remaining available PEs and goes up/down depending"
                + " if the FPS reached are below/above the objective") })

    })
public class LegacyListSchedulerTask extends AbstractTaskImplementation {

  public static final String PARAM_EDGE_SCHED_TYPE        = "edgeSchedType";
  public static final String VALUE_EDGE_SCHED_TYPE_SIMPLE = "Simple";

  public static final String PARAM_SIMULATOR_TYPE               = "simulatorType";
  public static final String VALUE_SIMULATOR_TYPE_LOOSELY_TIMED = "LooselyTimed";

  public static final String PARAM_CHECK = "Check";
  public static final String VALUE_TRUE  = "True";
  public static final String VALUE_FALSE = "False";

  public static final String PARAM_OPTIMIZE_SYNC = "Optimize synchronization";

  public static final String PARAM_BALANCE_LOADS = "balanceLoads";

  public static final String PARAM_ENERGY_AWARNESS = "EnergyAwareness";

  public static final String PARAM_ENERGY_AWARNESS_FIRST_CONFIG        = "EnergyAwarenessFirstConfig";
  public static final String VALUE_ENERGY_AWARNESS_FIRST_CONFIG_FIRST  = "First";
  public static final String VALUE_ENERGY_AWARNESS_FIRST_CONFIG_MIDDLE = "Middle";
  public static final String VALUE_ENERGY_AWARNESS_FIRST_CONFIG_MAX    = "Max";
  public static final String VALUE_ENERGY_AWARNESS_FIRST_CONFIG_RANDOM = "Random";

  public static final String PARAM_ENERGY_AWARNESS_SEARCH_TYPE          = "EnergyAwarenessSearchType";
  public static final String VALUE_ENERGY_AWARNESS_SEARCH_TYPE_THOROUGH = "Thorough";
  public static final String VALUE_ENERGY_AWARNESS_SEARCH_TYPE_HALVES   = "Halves";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final PiGraph graph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design arch = (Design) inputs.get("architecture");

    final SynthesisResult result = new LegacyListScheduler().scheduleAndMap(graph, arch, scenario, parameters);

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put("Mapping", result.mapping);
    outputs.put("Schedule", result.schedule);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put(PARAM_SIMULATOR_TYPE, VALUE_SIMULATOR_TYPE_LOOSELY_TIMED);
    parameters.put(PARAM_EDGE_SCHED_TYPE, VALUE_EDGE_SCHED_TYPE_SIMPLE);
    parameters.put(PARAM_BALANCE_LOADS, VALUE_FALSE);
    parameters.put(PARAM_CHECK, VALUE_TRUE);
    parameters.put(PARAM_OPTIMIZE_SYNC, VALUE_FALSE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

}
