package org.ietr.workflow.hypervisor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
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
            values = { @Value(name = "Boolean", effect = "...") }),
        @Parameter(name = "Node min", description = "choose the minimum number of architecture nodes",
            values = { @Value(name = "Integer", effect = "...") }),
        @Parameter(name = "Node max", description = "choose the maximum number of architecture nodes",
            values = { @Value(name = "Integer", effect = "...") }),
        @Parameter(name = "Node capacity", description = "select node storage capacity",
            values = { @Value(name = "Long", effect = "...") })

    })
public class HypervisorTask extends AbstractTaskImplementation {
  // global task parameter
  public static final String ITERATION_DEFAULT     = "1";
  public static final String ITERATION_PARAM       = "Iteration";
  public static final String MULTINET_DEFAULT      = "false";
  public static final String MULTINET_PARAM        = "Multinet";
  public static final String NODE_MIN_DEFAULT      = "1";
  public static final String NODE_MIN_PARAM        = "Node min";
  public static final String NODE_MAX_DEFAULT      = "2";
  public static final String NODE_MAX_PARAM        = "Node max";
  public static final String NODE_CAPACITY_DEFAULT = "1.0";
  public static final String NODE_CAPACITY_PARAM   = "Node capacity";

  public static final String SCENARIO_PATH_DEFAULT = "";
  public static final String SCENARIO_PATH_PARAM   = "archi path";

  // global file export data
  List<Long>                 itationTime    = new ArrayList<>();
  List<Long>                 nodePartTime   = new ArrayList<>();
  List<Long>                 threadPartTime = new ArrayList<>();
  List<Long>                 simuTime       = new ArrayList<>();
  public static final String DSE_PART_NAME  = "dse_part_trend.csv";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // retrieve inputs
    final int iteration = Integer.parseInt(parameters.get(ITERATION_PARAM));
    final Boolean multinet = Boolean.getBoolean(parameters.get(MULTINET_PARAM));
    final String project = "/" + workflow.getProjectName();

    // clean project

    PreesmIOHelper.getInstance().deleteFolder(project + "/Simulation");

    final int nodeMin = nodeMin(multinet, project, parameters);
    final int nodeMax = nodeMax(multinet, parameters, nodeMin);

    iterativePartitioning(multinet, nodeMin, nodeMax, iteration, project, parameters, monitor);

    return new LinkedHashMap<>();
  }

  private void iterativePartitioning(boolean multinet, int nodeMin, int nodeMax, int iterativeBound, String project,
      Map<String, String> parameters, IProgressMonitor monitor) {
    for (int indexNode = nodeMin; indexNode <= nodeMax; indexNode += nodePack()) {

      for (int iter = 0; iter <= iterativeBound; iter++) {

        // suppress generated

        PreesmIOHelper.getInstance().deleteFolder(project + "/Scenarios/generated");
        PreesmIOHelper.getInstance().deleteFolder(project + "/Algo/generated");

        // Launch node partitioning
        final long startTimeNodePartitioning = System.currentTimeMillis();
        String workflowPath = project + "/Workflows/NodePartitioning.workflow";
        String scenarioPath = project + parameters.get(SCENARIO_PATH_PARAM);

        final WorkflowManager workflowManager = new WorkflowManager();
        workflowManager.execute(workflowPath, scenarioPath, monitor);
        nodePartTime.add(System.currentTimeMillis() - startTimeNodePartitioning);

        // Launch thread partitioning
        final long startTimeThreadPartitioning = System.currentTimeMillis();
        for (int i = 0; i < nodeMin; i++) {
          workflowPath = project + "/Workflows/ThreadPartitioning.workflow";
          scenarioPath = project + "/Scenarios/generated/sub" + i + "_Node" + i + ".scenario";
          // it's possible that all node are not exploited
          final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(scenarioPath));
          if (iFile.exists()) {
            workflowManager.execute(workflowPath, scenarioPath, monitor);
          }
        }
        threadPartTime.add(System.currentTimeMillis() - startTimeThreadPartitioning);

        // Launch node simulator
        final long startTimeSimu = System.currentTimeMillis();
        multinetSimulation(multinet, nodeMin, workflowManager, monitor, project);
        simuTime.add(System.currentTimeMillis() - startTimeSimu);

      }
      exportDSE(project + "/Simulation/");

    }

  }

  private int nodePack() {
    // TODO Auto-generated method stub
    return 1;
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
  private void multinetSimulation(boolean multinet, int nNode, WorkflowManager workflowManager,
      IProgressMonitor monitor, String project) {
    // Original simSDP --> 1 config, simSDP multinet --> 5 config
    final int config = multinet ? 5 : 1;

    for (int i = 0; i < config; i++) {
      new Platform(config, nNode).execute();
      final String workflowPath = project + "/Workflows/NodeSimulator.workflow";

      final String scenarioPath = project + "/Scenarios/generated/top_top.scenario";
      workflowManager.execute(workflowPath, scenarioPath, monitor);
    }

  }

  /**
   * Retrieve the maximal number of nodes in the architecture weather or not you use a multi network algo .
   *
   * @param multinet
   *          boolean to activate the multi network mode
   * @param project
   *          the project path
   * @param parameters
   *          the parameters
   * @return the number of node to end the iteration process
   */
  private int nodeMax(Boolean multinet, Map<String, String> parameters, int nodeMin) {
    if (!multinet) {
      return nodeMin;
    }
    return Integer.parseInt(parameters.get(NODE_MAX_PARAM));
  }

  /**
   * Retrieve the minimal number of nodes in the architecture weather or not you use a multi network algo .
   *
   * @param multinet
   *          boolean to activate the multi network mode
   * @param project
   *          the project path
   * @param parameters
   *          the parameters
   * @return the number of node to start the iteration process
   */
  private int nodeMin(boolean multinet, String project, Map<String, String> parameters) {
    if (!multinet) {

      return singleNetInitNode(project);
    }
    return multiNetInitNode(project, parameters);
  }

  /**
   * Retrieve the number of nodes in the architecture when you use a multi network algo .
   *
   * @param project
   *          the project path
   * @param parameters
   *          the node storage capacity parameter
   * @return the number of node
   */
  private int multiNetInitNode(String project, Map<String, String> parameters) {
    final int inputNodeMin = Integer.parseInt(parameters.get(NODE_MIN_PARAM));
    if (inputNodeMin != 0) {
      return inputNodeMin;
    }
    final String content = PreesmIOHelper.getInstance().read(project + "/Simulation/", "initmemory.csv");
    final Long initMemory = Long.decode(content);
    final Long inputNodeCapacity = Long.decode(parameters.get(NODE_CAPACITY_PARAM));
    final int result = (int) Math.ceil((double) initMemory / inputNodeCapacity);
    return result;
  }

  /**
   * Retrieve the number of nodes in the architecture when you use a single network algo .
   *
   * @param project
   *          the project path
   * @return the number of node
   */
  private int singleNetInitNode(String project) {
    final String path = project + "/Archi/";
    final String fileName = "SimSDP_node.csv";
    // read csv file
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path + fileName));
    if (!iFile.isAccessible()) {
      final String message = "Missing file: Archi/" + fileName;
      PreesmLogger.getLogger().log(Level.SEVERE, message);
    }
    final List<String> nodeList = new ArrayList<>();
    final String content = PreesmIOHelper.getInstance().read(path, fileName);
    final String[] line = content.split("\n");
    int nNode = 0;
    for (final String element : line) {
      final String[] column = element.split(",");
      if (!nodeList.contains(column[0])) {
        nNode++;
        nodeList.add(column[0]);
      }
    }
    return nNode;
  }

  /**
   * Export the cumulative DSE analysis CSV file in order to generate SimSDP analysis chart
   *
   * @param path
   *          the file path
   */
  private void exportDSE(String path) {
    final StringConcatenation content = new StringConcatenation();

    for (int i = 0; i < nodePartTime.size(); i++) {
      content.append(nodePartTime.get(i) + ";");
      content.append(threadPartTime.get(i) + ";");
      content.append(simuTime.get(i) + ";\n");
    }
    PreesmIOHelper.getInstance().print(path, DSE_PART_NAME, content);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put(ITERATION_PARAM, ITERATION_DEFAULT);
    parameters.put(MULTINET_PARAM, MULTINET_DEFAULT);
    parameters.put(NODE_MIN_PARAM, NODE_MIN_DEFAULT);
    parameters.put(NODE_MAX_PARAM, NODE_MAX_DEFAULT);
    parameters.put(NODE_CAPACITY_PARAM, NODE_CAPACITY_DEFAULT);
    parameters.put(SCENARIO_PATH_PARAM, SCENARIO_PATH_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of SimSDP hypervisor Task";

  }

}
