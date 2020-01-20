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
  private final boolean generateArchiFile;
  private final boolean generateCMakeList;
  private final boolean moveIncludes;
  private final boolean useVerbose;
  private final boolean useGraphOptims;
  private final String  schedulerType;

  private static final String TRUE = "true";

  private static final String FALSE = "false";

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
    final String moveIncludesParameter = workflowParameters.get(Spider2CodegenTask.PARAM_MOVE_INCLUDES);
    final String verboseParameter = workflowParameters.get(Spider2CodegenTask.PARAM_VERBOSE);
    final String graphOptimsParameter = workflowParameters.get(Spider2CodegenTask.PARAM_GRAPH_OPTIMS);
    final String schedulerParameter = workflowParameters.get(Spider2CodegenTask.PARAM_SCHEDULER);

    if (generateArchiFileParameter == null) {
      generateArchiFile = true;
    } else {
      generateArchiFile = TRUE.equalsIgnoreCase(generateArchiFileParameter);
    }
    if (generateCMakeListParameter == null) {
      generateCMakeList = true;
    } else {
      generateCMakeList = TRUE.equalsIgnoreCase(generateCMakeListParameter);
    }
    if (moveIncludesParameter == null) {
      moveIncludes = false;
    } else {
      moveIncludes = TRUE.equalsIgnoreCase(moveIncludesParameter);
    }
    if (verboseParameter == null) {
      useVerbose = false;
    } else {
      useVerbose = TRUE.equalsIgnoreCase(verboseParameter);
    }
    if (graphOptimsParameter == null) {
      useGraphOptims = true;
    } else {
      useGraphOptims = TRUE.equalsIgnoreCase(graphOptimsParameter);
    }
    schedulerType = getSpider2SchedulerType(schedulerParameter);
  }

  public boolean getGenerateArchiFile() {
    return generateArchiFile;
  }

  public boolean getGenerateCMakeList() {
    return generateCMakeList;
  }

  public boolean getMoveIncludes() {
    return moveIncludes;
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
