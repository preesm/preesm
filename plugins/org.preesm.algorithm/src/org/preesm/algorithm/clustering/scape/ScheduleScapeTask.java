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
 * This class computes the schedule of the cluster using the authentic APGAN method, which relies on Repetition Count
 * computation, specifically the Greatest Common Divisor (GCD) of the repetition vectors (RVs) of a pair of connected
 * actors. The fundamental principle of APGAN involves iteratively clustering pairs of actors until a single entity is
 * obtained. Initiating the process by clustering pairs with the maximum repetition count has been demonstrated to
 * result in a schedule with minimal memory requirements. The resulting schedule consists of nested looped schedules,
 * designed to make the behavior of the cluster sequential.
 *
 * @see "https://apps.dtic.mil/sti/pdfs/ADA455067.pdf"
 * @author orenaud
 *
 */
@PreesmTask(id = "scape.scedule.task.identifier", name = "SCAPE schedule Task",
    inputs = { @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "schedule", type = List.class) })

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
