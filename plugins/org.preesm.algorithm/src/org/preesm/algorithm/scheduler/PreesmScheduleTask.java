package org.preesm.algorithm.scheduler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.algorithm.mapping.Mapping;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author anmorvan
 *
 */
@PreesmTask(id = "pisdf-scheduler.simple", name = "Simple Scheduling", category = "Schedulers",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) },

    outputs = { @Port(name = "Schedule", type = Schedule.class), @Port(name = "Mapping", type = Mapping.class) },

    parameters = { @Parameter(name = "todo", values = { @Value(name = "todo") }) })
public class PreesmScheduleTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    final IScheduler scheduler = new SimpleScheduler();
    final SchedulerResult scheduleAndMap = scheduler.scheduleAndMap(algorithm, architecture, scenario);

    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("Schedule", scheduleAndMap.schedule);
    outputs.put("Mapping", scheduleAndMap.mapping);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Simple scheduler";
  }

}
