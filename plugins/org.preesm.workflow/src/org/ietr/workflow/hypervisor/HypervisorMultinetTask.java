package org.ietr.workflow.hypervisor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.workflow.WorkflowManager;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "HypervisorMultinetTask.identifier", name = "SimSDP Hypervisor",
    parameters = { @Parameter(name = "Scenario path", description = "path of the scenario",
        values = { @Value(name = "String", effect = "...") }), })
@Parameter(name = "Algorithm", description = "choose algorithm 0: binary, 1:K-ary, 2:PGFT",
    values = { @Value(name = "String", effect = "...") })
public class HypervisorMultinetTask extends AbstractTaskImplementation {
  public static final String SCENARIO_PATH_DEFAULT = "";
  public static final String SCENARIO_PATH_PARAM   = "archi path";
  public static final String ALGO_DEFAULT          = "1";
  public static final String ALGO_PARAM            = "Algorithm";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {

    final String project = "/" + workflow.getProjectName();
    final WorkflowManager workflowManager = new WorkflowManager();
    // Init memory need
    String workflowPath = project + "/Workflows/ThreadPartitioning.workflow";
    final String scenarioPath = project + parameters.get("1coreX86.scenario");
    final String algo = parameters.get(ALGO_PARAM);

    workflowManager.execute(workflowPath, scenarioPath, monitor);

    // Init confi
    int nodeNum = initNodeNumber();
    final int nodePack = initNodePack();
    final int throughputPrev = computeThroughput();
    int throughput = 0;
    //
    do {
      for (int config = 0; config < 5; config++) {
        new Platform(config, nodeNum, Integer.valueOf(algo)).execute();

        workflowPath = project + "/Workflows/Hypervisor.workflow";

        workflowManager.execute(workflowPath, scenarioPath, monitor);
        throughput = Math.max(throughput, config);
      }
      nodeNum += nodePack;
    } while (throughput < throughputPrev);
    return new LinkedHashMap<>();
  }

  private int computeThroughput() {
    // TODO Auto-generated method stub
    return 0;
  }

  private int initNodePack() {
    // TODO Auto-generated method stub
    return 0;
  }

  private int initNodeNumber() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Map<String, String> getDefaultParameters() {

    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(SCENARIO_PATH_PARAM, SCENARIO_PATH_DEFAULT);
    parameters.put(ALGO_PARAM, ALGO_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of SimSDP multinet hypervisor Task";
  }

}
