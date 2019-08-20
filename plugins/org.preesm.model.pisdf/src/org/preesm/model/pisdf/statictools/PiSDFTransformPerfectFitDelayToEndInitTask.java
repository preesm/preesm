package org.preesm.model.pisdf.statictools;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiGraphConsistenceChecker;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * @author dgageot
 *
 */
@PreesmTask(id = "org.ietr.preesm.pisdf.transformperfectfitdelay",
    name = "PiSDF Transform Perfect Fit Delay To End Init", inputs = { @Port(name = "PiMM", type = PiGraph.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class) })
public class PiSDFTransformPerfectFitDelayToEndInitTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final PiGraph algorithm = (PiGraph) inputs.get("PiMM");
    // Process graph
    PiSDFTransformPerfectFitDelayToEndInit process = new PiSDFTransformPerfectFitDelayToEndInit(algorithm);
    PiGraph processedAlgorithm = process.replacePerfectFitDelay();
    PiGraphConsistenceChecker.check(processedAlgorithm);
    // Return processed graph
    Map<String, Object> outputs = new HashMap<>();
    outputs.put("PiMM", processedAlgorithm);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return null;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of PiSDF Transform Perfect Fit Delay To End/Init";
  }

}
