package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Cluster Scheduler Task
 * 
 * @author dgageot
 *
 */
@PreesmTask(id = "cluster-scheduler", name = "Cluster Scheduler",
    inputs = { @Port(name = "PiMM", type = PiGraph.class, description = "Input PiSDF graph") },
    outputs = { @Port(name = "CSs", type = Map.class, description = "Map of Cluster Schedule") })
public class ClusterSchedulerTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // Retrieve input graph
    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");

    Map<String, Object> output = new HashMap<>();
    output.put("CSs", ClusterSchedulerBackend.schedule(inputGraph));
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return null;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Cluster Scheduler Task";
  }

}
