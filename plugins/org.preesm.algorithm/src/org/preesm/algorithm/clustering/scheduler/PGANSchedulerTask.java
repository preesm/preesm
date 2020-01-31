package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * PGAN Scheduler Task
 * 
 * @author dgageot
 *
 */
@PreesmTask(id = "pgan-scheduler", name = "PGAN Scheduler", inputs = { @Port(name = "PiMM", type = PiGraph.class) },
    outputs = { @Port(name = "Schedule", type = Schedule.class) })
public class PGANSchedulerTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");

    PGANScheduler scheduler = new PGANScheduler(inputGraph);

    Map<String, Object> outputs = new HashMap<>();
    outputs.put("Schedule", scheduler.schedule());
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return null;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of PGAN Scheduler Task";
  }

}
