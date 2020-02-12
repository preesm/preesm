package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 */
public class ClusterScheduler {

  /**
   * Schedule all clusters of input graph.
   * 
   * @param inputGraph
   *          Input graph.
   * @return Map of Cluster Schedule.
   */
  public static Map<AbstractActor, Schedule> schedule(final PiGraph inputGraph, final boolean optimizePerformance) {
    // Build a map for Cluster Schedule
    Map<AbstractActor, Schedule> schedulesMap = new HashMap<>();

    // Build a PGAN scheduler
    PGANScheduler scheduler = new PGANScheduler(inputGraph, optimizePerformance);

    // Compute a schedule for every cluster
    for (AbstractActor actor : inputGraph.getAllActors()) {
      if (actor instanceof PiGraph) {
        PiGraph subgraph = (PiGraph) actor;
        if (subgraph.isCluster()) {
          registerClusterSchedule(schedulesMap, scheduler, inputGraph, subgraph);
        }
      }
    }

    return schedulesMap;
  }

  private static void registerClusterSchedule(Map<AbstractActor, Schedule> scheduleMap, PGANScheduler scheduler,
      final PiGraph inputGraph, final PiGraph subgraph) {
    // Schedule subgraph
    Schedule clusterSchedule = scheduler.scheduleCluster(subgraph);

    // Register the schedule in the map
    scheduleMap.put(subgraph, clusterSchedule);

  }

}
