/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015 - 2016)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.preesm.codegen.xtend.spider.utils;

import java.util.Map;
import org.preesm.codegen.xtend.spider.SpiderCodegenTask;

/**
 * Class containing all the config parameters necessary for spider
 * 
 * @author farresti
 *
 */
public class SpiderConfig {
  /** The Constant DEFAULT_SHARED_MEMORY_SIZE. */
  public static final Long DEFAULT_SHARED_MEMORY_SIZE = (long) 67108864;

  private boolean usePapify;
  private boolean useVerbose;
  private boolean useTrace;
  private boolean useGraphOptims;
  private boolean useDynamicStack;
  private String  schedulerType;
  private String  memAllocType;
  private Long    sharedMemorySize;

  private void setSchedulerType(final String scheduleType) {
    if ("list_on_the_go".equalsIgnoreCase(scheduleType)) {
      schedulerType = "SCHEDULER_LIST_ON_THE_GO";
    } else if ("round_robin".equalsIgnoreCase(scheduleType)) {
      schedulerType = "SCHEDULER_ROUND_ROBIN";
    } else if ("round_robin_scattered".equalsIgnoreCase(scheduleType)) {
      schedulerType = "SCHEDULER_ROUND_ROBIN_SCATTERED";
    } else {
      schedulerType = "SCHEDULER_LIST";
    }
  }

  private void setMemAllocType(final String memAllocType) {
    if ("special-actors".equalsIgnoreCase(memAllocType)) {
      this.memAllocType = "MEMALLOC_SPECIAL_ACTOR";
    } else {
      this.memAllocType = "MEMALLOC_DUMMY";
    }
  }

  /**
   * 
   * @param workflowParameters
   *          Parameters of the workflow task
   */
  public SpiderConfig(final Map<String, String> workflowParameters) {
    final String papifyParameter = workflowParameters.get(SpiderCodegenTask.PARAM_PAPIFY);
    final String verboseParameter = workflowParameters.get(SpiderCodegenTask.PARAM_VERBOSE);
    final String traceParameter = workflowParameters.get(SpiderCodegenTask.PARAM_TRACE);
    final String graphOptimsParameter = workflowParameters.get(SpiderCodegenTask.PARAM_GRAPH_OPTIMS);
    final String stackTypeParameter = workflowParameters.get(SpiderCodegenTask.PARAM_STACK_TYPE);
    final String schedulerParameter = workflowParameters.get(SpiderCodegenTask.PARAM_SCHEDULER);
    final String memAllocParameter = workflowParameters.get(SpiderCodegenTask.PARAM_MEMALLOC);
    final String sharedMemoryParameter = workflowParameters.get(SpiderCodegenTask.PARAM_SHMEMORY_SIZE);

    usePapify = "true".equalsIgnoreCase(papifyParameter);
    useVerbose = "true".equalsIgnoreCase(verboseParameter);
    useTrace = "true".equalsIgnoreCase(traceParameter);
    useGraphOptims = !"false".equalsIgnoreCase(graphOptimsParameter);
    useDynamicStack = !"static".equalsIgnoreCase(stackTypeParameter);
    setSchedulerType(schedulerParameter);
    setMemAllocType(memAllocParameter);
    sharedMemorySize = sharedMemoryParameter != null ? Long.decode(sharedMemoryParameter) : DEFAULT_SHARED_MEMORY_SIZE;
  }

  public boolean getUseOfPapify() {
    return usePapify;
  }

  public boolean getUseOfVerbose() {
    return useVerbose;
  }

  public boolean getUseOfTrace() {
    return useTrace;
  }

  public boolean getUseOfGraphOptims() {
    return useGraphOptims;
  }

  public boolean getIsStackDynamic() {
    return useDynamicStack;
  }

  public String getSchedulerType() {
    return schedulerType;
  }

  public String getMemoryAllocType() {
    return memAllocType;
  }

  public Long getSharedMemorySize() {
    return sharedMemorySize;
  }
}
