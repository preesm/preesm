package org.preesm.codegen.xtend.spider2.utils;

import java.util.Map;
import org.preesm.codegen.xtend.spider2.Spider2CodegenTask;

/**
 * Class containing all the config parameters necessary for spider
 * 
 * @author farresti
 *
 */
public class Spider2Config {
  private boolean generateArchiFile;
  private boolean generateCMakeList;
  private boolean useVerbose;
  private boolean useGraphOptims;
  private String  schedulerType;

  private String getSpider2SchedulerType(final String scheduleType) {
    if ("round_robin_list_scheduling".equalsIgnoreCase(scheduleType)) {
      return "SCHEDULER_LIST_ROUND_ROBIN";
    } else if ("greedy_scheduling".equalsIgnoreCase(scheduleType)) {
      return "SCHEDULER_GREEDY";
    } else {
      return "SCHEDULER_LIST_BESTFIT";
    }
  }

  /**
   * 
   * @param workflowParameters
   *          Parameters of the workflow task
   */
  public Spider2Config(final Map<String, String> workflowParameters) {
    final String generateArchiFileParameter = workflowParameters.get(Spider2CodegenTask.PARAM_GENERATE_ARCHI_FILE);
    final String generateCMakeListParameter = workflowParameters.get(Spider2CodegenTask.PARAM_GENERATE_CMAKELIST);
    final String verboseParameter = workflowParameters.get(Spider2CodegenTask.PARAM_VERBOSE);
    final String graphOptimsParameter = workflowParameters.get(Spider2CodegenTask.PARAM_GRAPH_OPTIMS);
    final String schedulerParameter = workflowParameters.get(Spider2CodegenTask.PARAM_SCHEDULER);

    generateArchiFile = "false".equalsIgnoreCase(generateArchiFileParameter);
    generateCMakeList = "false".equalsIgnoreCase(generateCMakeListParameter);
    useVerbose = "true".equalsIgnoreCase(verboseParameter);
    useGraphOptims = !"false".equalsIgnoreCase(graphOptimsParameter);
    schedulerType = getSpider2SchedulerType(schedulerParameter);
  }

  public boolean getGenerateArchiFile() {
    return generateArchiFile;
  }

  public boolean getGenerateCMakeList() {
    return generateCMakeList;
  }

  public boolean getUseOfVerbose() {
    return useVerbose;
  }

  public boolean getUseOfGraphOptims() {
    return useGraphOptims;
  }

  public String getSchedulerType() {
    return schedulerType;
  }

}
