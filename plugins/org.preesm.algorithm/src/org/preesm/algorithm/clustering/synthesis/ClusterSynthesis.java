package org.preesm.algorithm.clustering.synthesis;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.preesm.algorithm.clustering.ClusterCreationTask;
import org.preesm.algorithm.clustering.heuristics.AllocationHeuristic;
import org.preesm.algorithm.clustering.heuristics.HeuristicGetter;
import org.preesm.algorithm.clustering.heuristics.SchedulingHeuristic;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This class has one static main method: {@link ScheduleAndAllocate}
 */
public class ClusterSynthesis {

  private ClusterSynthesis() {
  }

  /**
   * For each input cluster, this method will (1) create a specific {@link Schedule schedule} and (2) a specific
   * {@link Allocation allocation}.
   *
   * @param algorithm
   *          The top graph, containing the clusters, and potential other hierarchical levels that might contain other
   *          clusters.
   * @param scenario
   *          the {@link Scenario scenario}
   * @param architecture
   *          the {@link Design architecture}
   * @param clusters
   *          the list of clusters
   * @param parameters
   *          the parameters of the calling task.
   * @return a map containing the {@link Schedule schedule} and the {@link Allocation allocation} for each cluster.
   */
  public static Set<SynthesisResult> scheduleAndAllocate(PiGraph algorithm, Scenario scenario, Design architecture,
      List<PiGraph> clusters, Map<String, String> parameters) {

    // Getting parameters
    final String schedulerName = parameters.getOrDefault(ClusterCreationTask.PARAM_SCHEDULING_HEURISTIC, "")
        .toLowerCase();
    final String allocationName = parameters.getOrDefault(ClusterCreationTask.PARAM_ALLOCATION_HEURISTIC, "")
        .toLowerCase();

    final SchedulingHeuristic schedulerHeuristic = (SchedulingHeuristic) HeuristicGetter.getHeuristic(schedulerName);
    final AllocationHeuristic allocHeuristic = (AllocationHeuristic) HeuristicGetter.getHeuristic(allocationName);

    schedulerHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);
    allocHeuristic.initHeuristicParameters(algorithm, scenario, architecture, parameters);

    final Set<SynthesisResult> results = new HashSet<>();

    for (final PiGraph cluster : clusters) {
      final String log = "[synthesis] cluster " + cluster.getName() + "is being synthesised";
      PreesmLogger.getLogger().info(log);
      final Schedule schedule = schedulerHeuristic.schedule(cluster);
      final Allocation alloc = allocHeuristic.allocate(cluster, schedule);
      results.add(new SynthesisResult(null, schedule, alloc));
    }

    return results;

  }
}
