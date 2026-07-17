package org.preesm.algorithm.clustering.synthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.heuristics.SchedulingHeuristic;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "clustering.synthesis", name = "Cluster Synthesis", inputs = {
  @Port(name = AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, type = List.class,
      description = "all clusters (also called subgraphs)"),
  @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class, description = "The scenario"),
  @Port(name = "Mapping", type = Mapping.class, description = "The global mapping") },

    outputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCHEDULES_LIST, type = List.class,
          description = "Schedules of clusters (who goes after who)"),
      @Port(name = "allocations", type = List.class, description = "Allocations of clusters"),
      @Port(name = "localSyntheses", type = Map.class,
          description = "Only so that the gantt is executed after cluster synthesis task. TODO: find a better way") })

public class ClusterSynthesisTask extends AbstractTaskImplementation {

  public static final String PARAM_SCHEDULING_HEURISTIC = "Scheduling heuristic";
  public static final String PARAM_ALLOCATION_HEURISTIC = "Allocation heuristic";
  public static final String PARAM_VERBOSE              = "Verbose";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting inputs
    final List<PiGraph> clusters = (List<PiGraph>) inputs.get(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Mapping mapping = (Mapping) inputs.get("Mapping");

    // Getting parameters
    final boolean verbose = "true".equalsIgnoreCase(parameters.get(PARAM_VERBOSE));
    String schedulingHeuristicName = parameters.getOrDefault(PARAM_SCHEDULING_HEURISTIC, "").toLowerCase();
    String allocationHeuristicName = parameters.getOrDefault(PARAM_ALLOCATION_HEURISTIC, "").toLowerCase();

    // Init Scheduling
    if (schedulingHeuristicName == null || "".equals(schedulingHeuristicName)) {
      schedulingHeuristicName = HeuristicGetter.DEFAULT_SCHEDULING;
    }
    final SchedulingHeuristic schedulingHeuristic = (SchedulingHeuristic) HeuristicGetter
        .getHeuristic(schedulingHeuristicName);
    schedulingHeuristic.initHeuristicParameters(scenario.getAlgorithm(), scenario, scenario.getDesign(),
        parameters); /* TODO Make links between tasks for arch and algo */

    // Init Allocation
    if (allocationHeuristicName == null || "".equals(allocationHeuristicName)) {
      allocationHeuristicName = HeuristicGetter.DEFAULT_ALLOCATION;
    }
    final AllocationHeuristic allocationHeuristic = (AllocationHeuristic) HeuristicGetter
        .getHeuristic(allocationHeuristicName);
    allocationHeuristic.initHeuristicParameters(scenario.getAlgorithm(), scenario, scenario.getDesign(),
        parameters); /* Make links between tasks for arch and algo */

    // Init output lists
    final List<Schedule> schedules = new ArrayList<>();
    final List<Allocation> allocations = new ArrayList<>();
    final Map<PiGraph, SynthesisResult> localSyntheses = new HashMap<>();

    for (final PiGraph cluster : clusters) {

      // ---------------------------------------------------------------------------------------------- //
      // Schedule cluster
      // ---------------------------------------------------------------------------------------------- //
      // Maybe make a version of scheduling extending IScheduler ? Might be more practical
      final Schedule schedule = schedulingHeuristic.schedule(cluster);
      schedules.add(schedule);

      // ---------------------------------------------------------------------------------------------- //
      // Allocate cluster
      // ---------------------------------------------------------------------------------------------- //
      final Allocation allocation = allocationHeuristic.allocate(cluster, schedule, mapping);

      allocations.add(allocation);

      // Log
      if (verbose) {
        final String log = "Cluster Synthesis: cluster " + cluster.getName() + ", schedule = " + schedule.shortPrint()
            + ", allocation = " + allocation.toString();
        PreesmLogger.getLogger().info(log);

      }

      // ---------------------------------------------------------------------------------------------- //
      // Compute latency of cluster
      // ---------------------------------------------------------------------------------------------- //
      // TODO: make a real latency evaluation class from a PiSDF and not a DAG
      final LatencyCost latency = new LatencyCost(42, null);
      final SynthesisResult synthesisResult = new SynthesisResult(mapping, schedule, allocation);
      synthesisResult.latency = latency;
      cluster.setSynthesisResult(synthesisResult);
      localSyntheses.put(cluster, synthesisResult);
    }

    // Building outputs list
    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCHEDULES_LIST, schedules);
    outputs.put("allocations", allocations);
    outputs.put("localSyntheses", localSyntheses);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String monitorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

}
