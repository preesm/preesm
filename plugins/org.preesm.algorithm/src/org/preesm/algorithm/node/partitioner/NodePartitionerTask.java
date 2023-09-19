package org.preesm.algorithm.node.partitioner;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class partition graph into subgraph assigned to a node For more details, see conference paper: "SimSDP: Dataflow
 * Application Distribution on Heterogeneous Multi-Node Multi-Core Architectures, published at xx 2024
 *
 * @author orenaud
 *
 */

@PreesmTask(id = "node.partitioner.task.identifier", name = "Node Partitioner",
    inputs = { @Port(name = "scenario", type = Scenario.class) },
    parameters = {
        @Parameter(name = "Archi path", description = "provide CSV file containing hierarchical architecture info",
            values = { @Value(name = "String", effect = "oui oui") }),

    })
public class NodePartitionerTask extends AbstractTaskImplementation {

  public static final String ARCHI_PATH_DEFAULT = "";
  public static final String ARCHI_PATH_PARAM   = "archi path";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final String archipath = parameters.get(NodePartitionerTask.ARCHI_PATH_PARAM);
    if (archipath.isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO,
          "Please provide en temp CSV file, hierarchical architecture is not handle yet");
    }

    final Scenario scenario = (Scenario) inputs.get("scenario");
    new NodePartitioner(scenario, archipath).execute();
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(NodePartitionerTask.ARCHI_PATH_PARAM, NodePartitionerTask.ARCHI_PATH_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of node partitioner Task";
  }

}
