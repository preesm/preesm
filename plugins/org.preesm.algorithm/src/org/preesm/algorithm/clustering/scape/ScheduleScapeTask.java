package org.preesm.algorithm.clustering.scape;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class schedule
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "scape.scedule.task.identifier", name = "SCAPE schedule Task",
    inputs = { @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "schedule", type = List.class) }, parameters = {})
public class ScheduleScapeTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final List<ScapeSchedule> schedule = new ScheduleScape(scenario.getAlgorithm()).execute();
    // Build output map
    final Map<String, Object> output = new HashMap<>();
    // return scenario updated
    output.put("schedule", schedule);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {

    return "Starting Execution of Scheduling SCAPE Task";
  }

}
