/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.synthesis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.heuristics.SchedulingHeuristic;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.communications.ICommunicationInserter;
import org.preesm.algorithm.synthesis.communications.OptimizedCommunicationInserter;
import org.preesm.algorithm.synthesis.memalloc.AClusterAwareMemoryAllocation;
import org.preesm.algorithm.synthesis.memalloc.IMemoryAllocation;
import org.preesm.algorithm.synthesis.memalloc.LegacyMemoryAllocation;
import org.preesm.algorithm.synthesis.memalloc.SimpleMemoryAllocation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.ChocoScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.FpgaScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.LegacyListScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.SimpleScheduler;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.check.FifoTypeChecker;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author anmorvan
 * @author rcazoulat
 *
 */
@PreesmTask(id = "pisdf-synthesis.clusteraware", name = "Cluster Aware Synhtesis", category = "Synthesis",
    shortDescription = "Schedule and map actors with clusters, and allocate their memory.",
    description = "Schedule and map actors and clusters and their communications, and allocate the buffer memory."
        + "Multiple available schedulers. Output is working only for the new code generation workflow tasks codegen2.",

    parameters = {
      @Parameter(name = ClusterAwareSynthesisTask.PARAM_TOPGRAPH_SCHEDULER,
          description = "Scheduler used to schedule and map the topgraph actors.",
          values = { @Value(name = "simple", effect = "Naive greedy list scheduler."),
            @Value(name = "legacy", effect = "See workflow task pisdf-mapper.list."),
            @Value(name = "periodic",
                effect = "List scheduler (without communication times) respecting actor or graph periods, if any."),
            @Value(name = "choco",
                effect = "Optimal scheduler (without communication times) "
                    + "respecting actor or graph periods, if any.") }),
      @Parameter(name = ClusterAwareSynthesisTask.PARAM_TOPGRAPH_ALLOCATOR,
          description = "Allocate the memory for buffers.",
          values = { @Value(name = "simple"), @Value(name = "legacy") }),
      @Parameter(name = ClusterAwareSynthesisTask.PARAM_CLUSTER_SCHEDULER, description = "",
          values = { @Value(name = "legacy") }),
      @Parameter(name = ClusterAwareSynthesisTask.PARAM_CLUSTER_ALLOCATOR, description = "",
          values = { @Value(name = "legacy") }) },

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
      @Port(name = "scenario", type = Scenario.class), @Port(name = "subgraphs", type = List.class) },

    outputs = { @Port(name = "Schedule", type = Schedule.class), @Port(name = "Mapping", type = Mapping.class),
      @Port(name = "Allocation", type = Allocation.class), @Port(name = "ClusterSchedules", type = List.class),
      @Port(name = "ClusterAllocations", type = List.class), })
public class ClusterAwareSynthesisTask extends AbstractTaskImplementation {

  public static final String PARAM_TOPGRAPH_ALLOCATOR = "topgraph allocation";
  public static final String VALUE_ALLOCATORS_SIMPLE  = "simple";
  public static final String VALUE_ALLOCATORS_LEGACY  = "legacy";

  public static final String PARAM_TOPGRAPH_SCHEDULER    = "topgraph scheduler";
  public static final String VALUE_SCHEDULER_SIMPLE      = "simple";
  public static final String VALUE_SCHEDULER_LEGACY      = "legacy";
  public static final String VALUE_SCHEDULER_PERIODIC    = "periodic";
  public static final String VALUE_SCHEDULER_CHOCO       = "choco";
  public static final String VALUE_SCHEDULER_FPGA_LINEAR = "adfgfifoevallinear";
  public static final String VALUE_SCHEDULER_FPGA_EXACT  = "adfgfifoevalexact";

  public static final String PARAM_CLUSTER_ALLOCATOR         = "cluster allocator";
  public static final String VALUE_CLUSTER_ALLOCATORS_LEGACY = "legacy";

  public static final String PARAM_CLUSTER_SCHEDULER        = "cluster scheduler";
  public static final String VALUE_CLUSTER_SCHEDULER_LEGACY = "legacy";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final List<PiGraph> clusters = (List<PiGraph>) inputs.get("subgraphs");

    final String schedulerName = parameters.get(PARAM_TOPGRAPH_SCHEDULER).toLowerCase();
    final String allocationName = parameters.get(PARAM_TOPGRAPH_ALLOCATOR).toLowerCase();

    final String clusterSchedulerName = parameters.get(PARAM_CLUSTER_SCHEDULER).toLowerCase();
    final String clusterAllocationName = parameters.get(PARAM_CLUSTER_ALLOCATOR).toLowerCase();

    final IScheduler scheduler = selectScheduler(schedulerName);
    final IMemoryAllocation alloc = selectAllocation(allocationName);

    // ==========================================================================================
    //
    // TOP GRAPH MAPPING SCHEDULING
    //
    // ==========================================================================================
    PreesmLogger.getLogger().log(Level.INFO, () -> " -- Mapping / Scheduling topgraph - " + schedulerName);
    final SynthesisResult scheduleAndMap = scheduler.scheduleAndMap(algorithm, architecture, scenario);

    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(algorithm, scheduleAndMap.schedule);

    PreesmLogger.getLogger().log(Level.INFO, " -- Insert communication");
    final ICommunicationInserter comIns = new OptimizedCommunicationInserter(scheduleOM);

    comIns.insertCommunications(algorithm, architecture, scenario, scheduleAndMap.schedule, scheduleAndMap.mapping);

    // ==========================================================================================
    //
    // CLUSTERS SCHEDULING
    //
    // ==========================================================================================
    PreesmLogger.getLogger().log(Level.INFO, () -> " -- Scheduling clusters - " + clusterSchedulerName);
    final List<Schedule> clusterSchedules = new ArrayList<>();

    final SchedulingHeuristic schedulingHeuristic = (SchedulingHeuristic) HeuristicGetter
        .getHeuristic(HeuristicGetter.DEFAULT_SCHEDULING);
    schedulingHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);

    for (final PiGraph cluster : clusters) {
      // Maybe make a version of scheduling extending IScheduler ? Might be more practical
      final Schedule schedule = schedulingHeuristic.schedule(cluster);
      clusterSchedules.add(schedule);
    }

    // ==========================================================================================
    //
    // CLUSTERS ALLOCATION
    //
    // ==========================================================================================
    PreesmLogger.getLogger().log(Level.INFO, () -> " -- Allocating clusters memories - " + clusterAllocationName);
    final List<Allocation> clusterAllocations = new ArrayList<>();

    final AllocationHeuristic allocationHeuristic = (AllocationHeuristic) HeuristicGetter
        .getHeuristic(HeuristicGetter.DEFAULT_ALLOCATION);
    allocationHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);

    for (int i = 0; i < clusters.size(); i++) {
      // Maybe make a version of scheduling extending IScheduler ? Might be more practical
      final Allocation allocation = allocationHeuristic.allocate(clusters.get(i), clusterSchedules.get(i),
          scheduleAndMap.mapping);
      clusterAllocations.add(allocation);
    }

    // ==========================================================================================
    //
    // TOP GRAPH ALLOCATION
    //
    // ==========================================================================================
    PreesmLogger.getLogger().log(Level.INFO, () -> " -- Allocating topgraph Memory - " + allocationName);
    try {
      FifoTypeChecker.checkMissingFifoTypeSizes(scenario);
    } catch (final PreesmRuntimeException e) {
      throw new PreesmSynthesisException(
          "Cannot perform the memory allocation since not all fifo types have a defined size.", e);
    }

    if (!(alloc instanceof final AClusterAwareMemoryAllocation clusterAlloc)) {
      throw new PreesmSynthesisException(
          "Chosen allocator is not suited for a clustered graph. Please change allocator.");

    }
    clusterAlloc.giveClustersInfos(clusters, clusterSchedules, clusterAllocations);

    final Allocation memalloc = clusterAlloc.allocateMemory(algorithm, architecture, scenario, scheduleAndMap.schedule,
        scheduleAndMap.mapping);

    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("Schedule", scheduleAndMap.schedule);
    outputs.put("Mapping", scheduleAndMap.mapping);
    outputs.put("Allocation", memalloc);
    outputs.put("ClusterSchedules", clusterSchedules);
    outputs.put("ClusterAllocations", clusterAllocations);
    return outputs;
  }

  private IScheduler selectScheduler(final String schedulerName) {
    return switch (schedulerName) {
      case VALUE_SCHEDULER_SIMPLE -> new SimpleScheduler();
      case VALUE_SCHEDULER_LEGACY -> new LegacyListScheduler();
      case VALUE_SCHEDULER_PERIODIC -> new PeriodicScheduler();
      case VALUE_SCHEDULER_CHOCO -> new ChocoScheduler();
      case VALUE_SCHEDULER_FPGA_LINEAR -> new FpgaScheduler(VALUE_SCHEDULER_FPGA_LINEAR);
      case VALUE_SCHEDULER_FPGA_EXACT -> new FpgaScheduler(VALUE_SCHEDULER_FPGA_EXACT);
      default -> throw new PreesmRuntimeException("unknown scheduler: " + schedulerName);
    };
  }

  private IMemoryAllocation selectAllocation(final String allocationName) {
    return switch (allocationName) {
      case VALUE_ALLOCATORS_SIMPLE -> new SimpleMemoryAllocation();
      case VALUE_ALLOCATORS_LEGACY -> new LegacyMemoryAllocation();
      default -> throw new PreesmRuntimeException("unknown allocation: " + allocationName);
    };
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> res = new LinkedHashMap<>();
    res.put("scheduler", VALUE_SCHEDULER_SIMPLE);
    res.put("allocation", VALUE_ALLOCATORS_SIMPLE);
    return res;
  }

  @Override
  public String monitorMessage() {
    return "Synthesis";
  }

}
