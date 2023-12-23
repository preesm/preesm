package org.ietr.workflow.hypervisor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.files.PreesmIOHelper;
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
 *      Architectures, published at TACO 2024
 *
 * @author orenaud
 *
 */

@PreesmTask(id = "hypervisor.task.identifier", name = "SimSDP Hypervisor",

    parameters = {

        @Parameter(name = "Iteration", description = "Iteration",
            values = { @Value(name = "integer", effect = "...") }),

        @Parameter(name = "Scenario path", description = "path of the scenario",
            values = { @Value(name = "String", effect = "...") }),
        @Parameter(name = "Multinet", description = "activate multinet node",
            values = { @Value(name = "Boolean", effect = "...") })

    })
public class HypervisorTask extends AbstractTaskImplementation {
  // global task parameter
  public static final String ITERATION_DEFAULT = "1";
  public static final String ITERATION_PARAM   = "Iteration";
  public static final String MULTINET_DEFAULT  = "false";
  public static final String MULTINET_PARAM    = "Multinet";

  public static final String SCENARIO_PATH_DEFAULT = "";
  public static final String SCENARIO_PATH_PARAM   = "archi path";

  // global file export data
  Long                                           initTime       = 0L;
  Map<Integer, Map<Integer, Long>>               nodePartTime   = new LinkedHashMap<>();
  Map<Integer, Map<Integer, Map<Integer, Long>>> threadPartTime = new LinkedHashMap<>();
  Map<Integer, Map<Integer, Long>>               simuTime       = new LinkedHashMap<>();
  public static final String                     DSE_PART_NAME  = "dse_part_trend.csv";
  Boolean                                        multinet       = false;
  String                                         scenarioName   = "";
  Double                                         finalLatency   = Double.MAX_VALUE;
  int                                            nodeMax        = Integer.MAX_VALUE;
  int                                            coreMax        = Integer.MAX_VALUE;
  int                                            configCount    = 0;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // retrieve inputs
    final int iteration = Integer.parseInt(parameters.get(ITERATION_PARAM));

    multinet = parameters.get(MULTINET_PARAM).equals("true");
    scenarioName = parameters.get(SCENARIO_PATH_PARAM);
    final String project = "/" + workflow.getProjectName();
    final WorkflowManager workflowManager = new WorkflowManager();
    // clean project

    PreesmIOHelper.getInstance().deleteFolder(project + "/Simulation");
    long initMemory = 0L;
    if (Boolean.TRUE.equals(multinet)) {
      final long startTimeInit = System.currentTimeMillis();
      initialisationLauncher(workflowManager, monitor, project);
      initTime = System.currentTimeMillis() - startTimeInit;
      final String content = PreesmIOHelper.getInstance().read(project + "/Simulation/", "initialisation.csv");
      final String[] line = content.split("\n");
      final String[] column = line[1].split(";");
      initMemory = Long.decode(column[1]);
    }
    final ArchiMoldableParameter archiParams = new ArchiMoldableParameter(project, multinet, initMemory);

    archiParams.execute();
    nodeMax = archiParams.getNodeMax();
    coreMax = archiParams.getCoreMax();
    final boolean parallelismFound = false;
    for (int nodeIndex = archiParams.getNodeMin(); nodeIndex <= nodeMax; nodeIndex += archiParams.getNodeStep()) {

      for (int coreIndex = archiParams.getCoreMin(); coreIndex <= coreMax; coreIndex += archiParams.getCoreStep()) {
        for (int corefreqIndex = archiParams.getCoreFreqMin(); corefreqIndex <= archiParams.getCoreFreqMax();
            corefreqIndex += archiParams.getCoreFreqStep()) {
          if (Boolean.TRUE.equals(multinet)) {
            final SimSDPnode simSDPnode = new SimSDPnode(nodeIndex, coreIndex, corefreqIndex, project);
            simSDPnode.execute();
          }
          iterativePartitioning(nodeIndex, coreIndex, corefreqIndex, iteration, project, monitor, workflowManager);

        }
        processParallelismMaxBoundary(project + "/Simulation/", nodeIndex, coreIndex, parallelismFound);
      }

    }

    return new LinkedHashMap<>();
  }

  private void processParallelismMaxBoundary(String path, int nodeIndex, int coreIndex, boolean parallelismFound) {
    final String content = PreesmIOHelper.getInstance().read(path, "latency_trend.csv");
    final String[] line = content.split("\n");
    final Double curentFinalLatency = Double.valueOf(line[line.length - 1]);
    int maximalParallelism = 0;
    if (curentFinalLatency > finalLatency && !parallelismFound) {
      maximalParallelism = nodeIndex * coreIndex;
    } else {

      finalLatency = curentFinalLatency;
    }

    if (maximalParallelism > 0) {
      nodeMax = maximalParallelism / coreIndex;
      coreMax = maximalParallelism / nodeIndex;
    }
  }

  private void initialisationLauncher(WorkflowManager workflowManager, IProgressMonitor monitor, String project) {
    final String workflowPath = project + "/Workflows/Initialisation.workflow";
    final String scenarioPath = project + scenarioName;
    workflowManager.execute(workflowPath, scenarioPath, monitor);

  }

  private void iterativePartitioning(int nNode, int nCore, int cFreq, int iterativeBound, String project,
      IProgressMonitor monitor, WorkflowManager workflowManager) {
    configCount++;

    for (int iter = 0; iter < iterativeBound; iter++) {

      // delete generated
      PreesmIOHelper.getInstance().deleteFolder(project + "/Scenarios/generated");
      PreesmIOHelper.getInstance().deleteFolder(project + "/Algo/generated");

      // Launch node partitioning
      final long startTimeNodePartitioning = System.currentTimeMillis();

      nodePartitioningLauncher(workflowManager, monitor, project);
      if (!nodePartTime.containsKey(iter + 1)) {
        nodePartTime.put(iter + 1,
            new LinkedHashMap<>(Map.of(configCount, System.currentTimeMillis() - startTimeNodePartitioning)));
      } else {
        nodePartTime.get(iter + 1)
            .putAll(new LinkedHashMap<>(Map.of(configCount, System.currentTimeMillis() - startTimeNodePartitioning)));
      }

      // Launch thread partitioning

      threadPartitioningLaucher(nNode, iter, workflowManager, monitor, project);

      // Launch node simulator
      final long startTimeSimu = System.currentTimeMillis();
      simulationLauncher(nNode, nCore, cFreq, workflowManager, monitor, project);
      if (!simuTime.containsKey(iter + 1)) {
        simuTime.put(iter + 1, new LinkedHashMap<>(Map.of(configCount, System.currentTimeMillis() - startTimeSimu)));
      } else {
        simuTime.get(iter + 1)
            .putAll(new LinkedHashMap<>(Map.of(configCount, System.currentTimeMillis() - startTimeNodePartitioning)));
      }

    }
    exportDSE(project + "/Simulation/", iterativeBound, nNode);

  }

  private void nodePartitioningLauncher(WorkflowManager workflowManager, IProgressMonitor monitor, String project) {
    final String workflowPath = project + "/Workflows/NodePartitioning.workflow";
    final String scenarioPath = project + scenarioName;
    workflowManager.execute(workflowPath, scenarioPath, monitor);

  }

  private void threadPartitioningLaucher(int nbNode, int iter, WorkflowManager workflowManager,
      IProgressMonitor monitor, String project) {
    final Map<Integer, Long> part = new LinkedHashMap<>();
    for (int i = 0; i < nbNode; i++) {
      final long startTimeThreadPartitioning = System.currentTimeMillis();
      final String workflowPath = project + "/Workflows/ThreadPartitioning.workflow";
      final String scenarioPath = project + "/Scenarios/generated/sub" + i + "_Node" + i + ".scenario";
      // it's possible that all node are not exploited
      final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(scenarioPath));
      if (iFile.exists()) {
        workflowManager.execute(workflowPath, scenarioPath, monitor);
      }

      part.put(i, System.currentTimeMillis() - startTimeThreadPartitioning);

    }
    if (!threadPartTime.containsKey(iter + 1)) {
      threadPartTime.put(iter + 1, new LinkedHashMap<>(Map.of(configCount, part)));
    } else {
      threadPartTime.get(iter + 1).putAll(new LinkedHashMap<>(Map.of(configCount, part)));
    }
  }

  /**
   * Simulate on the specific network according to the multinet mode .
   *
   * @param multinet
   *          boolean to activate the multi-network mode
   * @param nNode
   *          the number of nodes
   * @param workflowManager
   *          the workflowManager
   * @param monitor
   *          the monitor
   * @param project
   *          the project path
   */
  private void simulationLauncher(int nNode, int nCore, int cFreq, WorkflowManager workflowManager,
      IProgressMonitor monitor, String project) {
    // Original simSDP --> 1 config, simSDP multinet --> 5 config
    final int config = Boolean.TRUE.equals(multinet) ? 5 : 1;

    for (int i = 0; i < config; i++) {
      Boolean isExistingNetwork = true;
      if (Boolean.TRUE.equals(multinet)) {
        isExistingNetwork = new SimSDPnetwork(i, nNode, nCore, cFreq, project).execute();
      }

      final String workflowPath = project + "/Workflows/NodeSimulator.workflow";

      final String scenarioPath = project + "/Scenarios/generated/top_top.scenario";
      if (Boolean.TRUE.equals(isExistingNetwork)) {
        workflowManager.execute(workflowPath, scenarioPath, monitor);
      }
    }

  }

  /**
   * Export the cumulative DSE analysis CSV file in order to generate SimSDP analysis chart
   *
   * @param path
   *          the file path
   * @param nbNode
   *          the number of nodes
   * @param iteration
   *          the number of iteration
   */
  private void exportDSE(String path, int iteration, int nbNode) {
    final StringConcatenation content = new StringConcatenation();
    content.append("Step;Duration(ms)\n");
    content.append("initialisation:0;" + initTime + "\n");
    for (int i = 1; i <= iteration; i++) {
      for (int c = 1; c <= configCount; c++) {
        content.append("node partitioning:" + i + ":" + c + ";" + nodePartTime.get(i).get(c) + "\n");
        for (int n = 0; n < nbNode; n++) {
          if (threadPartTime.get(i).get(c).containsKey(n)) {
            content.append(
                "thread partitioning node" + n + ":" + i + ":" + c + ";" + threadPartTime.get(i).get(c).get(n) + "\n");
          }
        }
        content.append("simulation:" + i + ":" + c + ";" + simuTime.get(i).get(c) + "\n");
      }
    }
    PreesmIOHelper.getInstance().print(path, DSE_PART_NAME, content);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put(ITERATION_PARAM, ITERATION_DEFAULT);
    parameters.put(MULTINET_PARAM, MULTINET_DEFAULT);

    parameters.put(SCENARIO_PATH_PARAM, SCENARIO_PATH_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of SimSDP hypervisor Task";

  }

}
