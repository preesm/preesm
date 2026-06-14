package org.preesm.algorithm.clustering.synthesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.ECollections;
import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.clustering.heuristics.Heuristic;
import org.preesm.algorithm.clustering.heuristics.MappingHeuristic;
import org.preesm.algorithm.clustering.heuristics.SchedulingHeuristic;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.mapping.model.MappingFactory;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "clustering.synthesis", name = "Cluster Synthesis",
    inputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST, type = List.class, description = "Clusters"),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class, description = "Scenario"),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class,
          description = "Architecture") },

    outputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCHEDULES_LIST, type = List.class,
          description = "Schedules of clusters (who goes after who)"),
      @Port(name = "mappings", type = List.class, description = "mapping of clusters (who goes where)"),
      @Port(name = "allocations", type = List.class,
          description = "allocation of clusters (buffer allocation mostly)"), })

/**
 * @author rcazoulat
 */
public class ClusterSynthesisTask extends AbstractTaskImplementation {

  public static final String PARAM_MAPPING_HEURISTIC = "Mapping heuristic";

  public static final String PARAM_SCHEDULING_HEURISTIC = "Scheduling heuristic";

  public static final String PARAM_ALLOCATION_HEURISTIC = "Allocation heuristic";

  public static final String PARAM_VERBOSE = "Verbose";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // ----- Getting inputs -----
    final List<PiGraph> clusters = (List<PiGraph>) inputs.get(AbstractWorkflowNodeImplementation.KEY_SUBGRAPHS_LIST);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    // ----- Getting parameters -----
    final boolean verbose = "true".equalsIgnoreCase(parameters.get(PARAM_VERBOSE));
    final String mappingHeuristicName = parameters.getOrDefault(PARAM_MAPPING_HEURISTIC, "").toLowerCase();
    final String schedulingHeuristicName = parameters.getOrDefault(PARAM_SCHEDULING_HEURISTIC, "").toLowerCase();
    final String allocationHeuristicName = parameters.getOrDefault(PARAM_ALLOCATION_HEURISTIC, "").toLowerCase();

    // ----- Init Mapping -----
    MappingHeuristic mappingHeuristic = (MappingHeuristic) getHeuristic(mappingHeuristicName);
    if (mappingHeuristic == null) {
      mappingHeuristic = new SimpleMappingHeuristic();
    }
    mappingHeuristic.initHeuristicParameters(null, scenario, architecture, parameters);

    // ----- Init Scheduling -----
    SchedulingHeuristic schedulingHeuristic = (SchedulingHeuristic) getHeuristic(schedulingHeuristicName);
    if (schedulingHeuristic == null) {
      schedulingHeuristic = new APGANSchedulingHeuristic();
    }
    schedulingHeuristic.initHeuristicParameters(null, scenario, architecture, parameters);

    // ----- Init Allocation -----
    AllocationHeuristic allocationHeuristic = (AllocationHeuristic) getHeuristic(allocationHeuristicName);
    if (allocationHeuristic == null) {
      allocationHeuristic = new SimpleAllocationHeuristic();
    }
    allocationHeuristic.initHeuristicParameters(null, scenario, architecture, parameters);

    // ----- Mapping and Scheduling each cluster -----
    final Map<Component, Map<ComponentInstance, Integer>> componentDistribution = new HashMap<>();
    final List<Schedule> schedules = new ArrayList<>();
    final List<Mapping> mappings = new ArrayList<>();
    final List<Allocation> allocations = new ArrayList<>();

    for (final PiGraph cluster : clusters) {

      // Facilitate the memory reuse in cluster
      ClusterSynthesisHelper.addSpecialActors(cluster);

      // ---------------------------------------------------------------------------------------------- //
      // Mapping cluster
      // ---------------------------------------------------------------------------------------------- //

      // First selecting component type thanks to an heuristic chosen by the user
      final Component clusterComponent = mappingHeuristic.selectComponent(cluster, scenario);

      // TODO
      // Might not be the best way to do it -->
      // create heuristic to select the component instance once the component is selected with the first heuristic ?
      // Too many heuristics ...

      // Initializing this component type if it was not seen in any cluster yet
      if (!componentDistribution.containsKey(clusterComponent)) {
        final Map<ComponentInstance, Integer> componentInstanceDistribution = new HashMap<>();
        for (final ComponentInstance componentInstance : architecture.getComponentInstancesOfType(clusterComponent)) {

          // Setting the count to 0 for each component instance of the same type
          componentInstanceDistribution.put(componentInstance, 0);
        }

        // Filling the map that keeps track of every component type
        componentDistribution.put(clusterComponent, componentInstanceDistribution);
      }

      // Getting the final component instance, by choosing the one with the smallest integer value
      final ComponentInstance targetComponentInstance = componentDistribution.get(clusterComponent).entrySet().stream()
          .min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElseThrow();

      // Incrementing the Integer linked to the targetComponentInstance by 1
      componentDistribution.get(clusterComponent).merge(targetComponentInstance, 1, Integer::sum);

      // Adding the result to the final mapping
      final Mapping clusterMapping = MappingFactory.eINSTANCE.createMapping();
      clusterMapping.getMappings().put(cluster, ECollections.singletonEList(targetComponentInstance));

      // ---------------------------------------------------------------------------------------------- //
      // Scheduling cluster
      // ---------------------------------------------------------------------------------------------- //

      // Maybe make a version of scheduling extending IScheduler ? Might be more practical
      final Schedule clusterSchedule = schedulingHeuristic.schedule(cluster);
      schedules.add(clusterSchedule);

      // ---------------------------------------------------------------------------------------------- //
      // Allocate cluster
      // ---------------------------------------------------------------------------------------------- //

      final Allocation clusterAllocation = allocationHeuristic.allocate(cluster, clusterSchedule, clusterMapping);

      // Filling the result lists
      final SynthesisResult result = new SynthesisResult(clusterMapping, clusterSchedule, clusterAllocation);
      cluster.setSynthesisResult(result);
      mappings.add(clusterMapping);
      schedules.add(clusterSchedule);
      allocations.add(clusterAllocation);

      // Log
      if (verbose) {
        final String log = "Cluster Synthesis: cluster " + cluster.getName() + ", schedule = "
            + clusterSchedule.shortPrint() + ", allocation = " + clusterAllocation.toString();
        PreesmLogger.getLogger().info(log);

      }
    }

    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCHEDULES_LIST, schedules);
    outputs.put("mappings", mappings);
    outputs.put("allocations", allocations);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put(PARAM_MAPPING_HEURISTIC, "Simple mapping");
    parameters.put(PARAM_SCHEDULING_HEURISTIC, "APGAN scheduling");
    parameters.put(PARAM_VERBOSE, "False");

    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return null;
  }

  private static Heuristic getHeuristic(String heuristicName) {
    return switch (heuristicName) {
      case null -> null;
      case "simple mapping" -> new SimpleMappingHeuristic();
      case "apgan scheduling" -> new APGANSchedulingHeuristic();
      case "simple allocation" -> new SimpleAllocationHeuristic();
      case "smart allocation" -> new SmartAllocationHeuristic();

      default -> null;
    };
  }

}
