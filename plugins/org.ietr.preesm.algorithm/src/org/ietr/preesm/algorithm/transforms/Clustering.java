package org.ietr.preesm.algorithm.transforms;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 *
 *
 */
public class Clustering extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {
    final Map<String, Object> outputs = new HashMap<>();
    final SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
    final HSDFBuildLoops loopBuilder = new HSDFBuildLoops(scenario);
    outputs.put("SDF", loopBuilder.execute(algorithm));
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
