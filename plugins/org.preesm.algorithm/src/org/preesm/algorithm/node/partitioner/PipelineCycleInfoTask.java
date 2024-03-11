package org.preesm.algorithm.node.partitioner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "PipelineCycleInfoTask.identifier", name = "Partitioner",
    inputs = { @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) }

)
public class PipelineCycleInfoTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final PiGraph graph = scenario.getAlgorithm();

    final PipelineCycleInfo pipelineCycleInfo = new PipelineCycleInfo(scenario);
    pipelineCycleInfo.execute();
    pipelineCycleInfo.removeCycle();

    final Map<String, Object> output = new HashMap<>();
    output.put("PiMM", graph);
    output.put("scenario", scenario);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {

    return "Starting Execution of Pipeline/cycle identifier Task";
  }

}
