package org.ietr.preesm.mapper.algo.hierarchical;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;

/**
 * @author hderoui
 *
 */
public class HScheduleTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow)
      throws WorkflowException {

    // get the input graph, the scenario for actors duration, and the method to use
    SDFGraph inputGraph = GraphStructureHelper.cloneIBSDF((SDFGraph) inputs.get("SDF"));
    PreesmScenario inputScenario = (PreesmScenario) inputs.get("scenario");

    //

    WorkflowLogger.getLogger().log(Level.INFO, "Throughput value");
    WorkflowLogger.getLogger().log(Level.WARNING, "ERROR : The graph is deadlock !!");

    // set the outputs
    Map<String, Object> outputs = new HashMap<String, Object>();
    // outputs.put("SDF", inputGraph);
    // outputs.put("scenario", inputScenario);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> parameters = new HashMap<String, String>();
    // parameters.put(,);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Scheduling the graph ...";
  }

}
