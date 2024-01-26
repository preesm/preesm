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
        @Parameter(name = NodePartitionerTask.ARCHI_NAME_PARAM,
            description = "Browse the CSV file containing hierarchical architecture info",
            values = { @Value(name = "String", effect = "Read file") }),
        @Parameter(name = "Partitioning mode",
            description = "equivalentTimed : estimate balanced worload partitioning,"
                + "random : random workload partitioning",
            values = { @Value(name = "String", effect = "compute equivalent time") }),

    })
public class NodePartitionerTask extends AbstractTaskImplementation {

  public static final String ARCHI_NAME_DEFAULT        = "SimSDP_node.csv";
  public static final String ARCHI_NAME_PARAM          = "archi name";
  public static final String PARTITIONING_MODE_DEFAULT = "";
  public static final String PARTITIONING_MODE_PARAM   = "Partitioning mode";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final String archipath = parameters.get(NodePartitionerTask.ARCHI_NAME_PARAM);
    if (archipath.isEmpty()) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "Please provide en temp CSV file, hierarchical architecture is not handle yet");
    }
    final String partitioningMode = parameters.get(NodePartitionerTask.PARTITIONING_MODE_PARAM);
    if (!(partitioningMode.equals("equivalentTimed") || partitioningMode.equals("random"))) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Expecting equivalentTimed or random Partitioning mode");
    }

    final Scenario scenario = (Scenario) inputs.get("scenario");
    new NodePartitioner(scenario, archipath, partitioningMode).execute();
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(NodePartitionerTask.ARCHI_NAME_PARAM, NodePartitionerTask.ARCHI_NAME_DEFAULT);
    parameters.put(NodePartitionerTask.PARTITIONING_MODE_PARAM, NodePartitionerTask.PARTITIONING_MODE_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of node partitioner Task";
  }

}
