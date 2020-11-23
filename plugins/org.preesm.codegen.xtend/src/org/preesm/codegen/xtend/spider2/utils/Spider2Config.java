/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
  private final String  mapperType;
  private final String  allocatorType;
  private final String  execPolicyType;
  private final String  runMode;
  private final String  runtimeAlgo;

  private static final String TRUE = "true";

  private static final String FALSE = "false";

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
    final String mapperParameter = workflowParameters.get(Spider2CodegenTask.PARAM_MAPPER);
    final String allocatorParameter = workflowParameters.get(Spider2CodegenTask.PARAM_ALLOCATOR);
    final String execPolicyParameter = workflowParameters.get(Spider2CodegenTask.PARAM_EXEC_POLICY);
    final String modeParameter = workflowParameters.get(Spider2CodegenTask.PARAM_RUN_MODE);
    final String algoParameter = workflowParameters.get(Spider2CodegenTask.PARAM_ALGO);

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
    schedulerType = schedulerParameter == null ? "LIST" : schedulerParameter;
    mapperType = mapperParameter == null ? "BEST_FIT" : mapperParameter;
    allocatorType = allocatorParameter == null ? "DEFAULT" : allocatorParameter;
    execPolicyType = execPolicyParameter == null ? "DELAYED" : execPolicyParameter;
    runMode = modeParameter == null ? "LOOP" : modeParameter;
    runtimeAlgo = algoParameter == null ? "PISDF_BASED" : algoParameter;
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

  public String getMapperType() {
    return mapperType;
  }

  public String getAllocatorType() {
    return allocatorType;
  }

  public String getExecPolicyType() {
    return execPolicyType;
  }

  public String getRunMode() {
    return runMode;
  }

  public String getRuntimeAlgo() {
    return runtimeAlgo;
  }

}
