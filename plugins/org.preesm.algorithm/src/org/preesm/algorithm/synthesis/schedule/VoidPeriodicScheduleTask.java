package org.preesm.algorithm.synthesis.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.synthesis.schedule.algos.ChocoScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author ahonorat
 *
 */
@PreesmTask(id = "pisdf-synthesis.void-periodic-schedule", name = "Periodic scheduling (without output)",
    parameters = { @Parameter(name = "solver", values = { @Value(name = "list"), @Value(name = "choco") }) },
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) })
public class VoidPeriodicScheduleTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    final String solverName = parameters.get("solver").toLowerCase();

    IScheduler scheduler = null;

    if ("list".equalsIgnoreCase(solverName)) {
      scheduler = new PeriodicScheduler();
      PreesmLogger.getLogger().log(Level.INFO, () -> " -- Periodic list scheduling without output ");
    } else if ("choco".equalsIgnoreCase(solverName)) {
      scheduler = new ChocoScheduler();
      PreesmLogger.getLogger().log(Level.INFO, () -> " -- Periodic constraint programming scheduling without output ");
    } else {
      throw new PreesmRuntimeException("Unknown solver.");
    }

    scheduler.scheduleAndMap(algorithm, architecture, scenario);
    // we do not care about result, if it fails, it will throw an exception

    return new HashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> map = new HashMap<>();
    map.put("solver", "list");
    return map;
  }

  @Override
  public String monitorMessage() {
    return "Only Periodic Scheduling (without output)";
  }

}
