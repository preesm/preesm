package org.ietr.preesm.pimm.algorithm.checker.periods;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * This class computes necessary conditions for the schedulability of graphs with periods.
 * 
 * @author ahonorat
 *
 */
public class PeriodsPreschedulingChecker extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {

    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    if (!graph.getChildrenGraphs().isEmpty()) {
      throw new WorkflowException("This task must be called with a flatten PiMM graph, abandon.");
    }

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Checking if periods constraints prevent to have a schedulable application.";
  }

}
