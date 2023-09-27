package org.preesm.workflow.simsdp;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.workflow.WorkflowManager;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * SimSDP is an iterative, heterogeneous, multi-core, multinode simulator. This class launches the workflows associated
 * with SimSDP's 4 main stages (Node Partitioning/Readjustment, Thread Partitioning, Node Simulation). The process
 * boasts the optimal iteration over a parameter-defined number of turns if the deviation and latency targets are not
 * reached beforehand.
 *
 * @see conference paper: "SimSDP: Dataflow Application Distribution on Heterogeneous Multi-Node Multi-Core
 *      Architectures, published at xx 2024
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "hypervisor.task.identifier", name = "SimSDP Hypervisor",
    // inputs = { @Port(name = "void", type = String.class) },
    parameters = {
        @Parameter(name = "Latency Target", description = "Latency target",
            values = { @Value(name = "integer", effect = "...") }),
        @Parameter(name = "Deviation Target", description = "Deviation target",
            values = { @Value(name = "integer", effect = "...") }),
        @Parameter(name = "Round", description = "Round", values = { @Value(name = "integer", effect = "...") })

    })
public class HypervisorTask extends AbstractTaskImplementation {
  public static final String LATENCY_DEFAULT   = "";
  public static final String LATENCY_PARAM     = "Latency Target";
  public static final String DEVIATION_DEFAULT = "";
  public static final String DEVIATION_PARAM   = "Deviation Target";
  public static final String ROUND_DEFAULT     = "";
  public static final String ROUND_PARAM       = "Round";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    boolean boundary = false;
    // retrieve inputs
    final int targetLatency = Integer.parseInt(parameters.get(HypervisorTask.LATENCY_PARAM));
    final int targetDeviation = Integer.parseInt(parameters.get(HypervisorTask.DEVIATION_PARAM));
    final int targetRound = Integer.parseInt(parameters.get(HypervisorTask.ROUND_PARAM));

    int countRound = 0;
    while (!boundary) {
      // Launch node partitioning
      String workflowPath = "/rfifilter/Workflows/NodePartitioning.workflow";
      String scenarioPath = "/rfifilter/Scenarios/rfi.scenario";

      final WorkflowManager workflowManager = new WorkflowManager();
      workflowManager.execute(workflowPath, scenarioPath, monitor);

      // Launch thread partitioning
      final int nSubGraphs = 3;
      for (int i = 0; i < nSubGraphs; i++) {
        workflowPath = "/rfifilter/Workflows/ThreadPartitioning.workflow";
        scenarioPath = "/rfifilter/Scenarios/generated/Node" + i + ".scenario";
        workflowManager.execute(workflowPath, scenarioPath, monitor);
      }

      // Launch node simulator
      workflowPath = "/rfifilter/Workflows/ThreadPartitioning.workflow";
      scenarioPath = "/rfifilter/Scenarios/generated/Top.scenario";
      workflowManager.execute(workflowPath, scenarioPath, monitor);

      // Convergence check
      countRound++;
      if (countRound >= targetRound) {
        boundary = true;
      }
    }
    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(HypervisorTask.LATENCY_PARAM, HypervisorTask.LATENCY_DEFAULT);
    parameters.put(HypervisorTask.DEVIATION_PARAM, HypervisorTask.DEVIATION_DEFAULT);
    parameters.put(HypervisorTask.ROUND_PARAM, HypervisorTask.ROUND_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of SimSDP hypervisor Task";
  }

}
