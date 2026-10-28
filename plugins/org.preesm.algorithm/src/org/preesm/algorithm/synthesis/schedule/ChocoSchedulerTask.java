package org.preesm.algorithm.synthesis.schedule;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.ChocoScheduler;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "mapper2.choco", name = "Periodic Scheduling using choco solver from PiSDF", category = "Schedulers",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },

    outputs = { @Port(name = "Mapping", type = Mapping.class), @Port(name = "Schedule", type = Schedule.class) },

    parameters = { @Parameter(name = ChocoScheduler.PARAM_MAX_SOLUTION, values = {}),
      @Parameter(name = ChocoScheduler.PARAM_VERBOSE, values = {}) })

public class ChocoSchedulerTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final PiGraph graph = (PiGraph) inputs.get("PiMM");
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design arch = (Design) inputs.get("architecture");

    final SynthesisResult result = new ChocoScheduler().scheduleAndMap(graph, arch, scenario, parameters);

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put("Mapping", result.mapping);
    outputs.put("Schedule", result.schedule);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

}
