package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
import java.util.Map;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 */
public class ClusterSchedulerBackend {

  /**
   * Schedule all clusters of input graph.
   * 
   * @param inputGraph
   *          Input graph.
   * @return Map of Cluster Schedule.
   */
  public static Map<AbstractActor, Schedule> schedule(final PiGraph inputGraph) {
    // Build a map for Cluster Schedule
    Map<AbstractActor, Schedule> schedulesMap = new HashMap<>();
    // Build a PGAN scheduler
    PGANScheduler scheduler = new PGANScheduler(inputGraph);
    // Compute a schedule for every cluster
    for (AbstractActor actor : inputGraph.getAllActors()) {
      if (actor instanceof PiGraph) {
        PiGraph subgraph = (PiGraph) actor;
        if (subgraph.isCluster()) {
          Schedule childSchedule = scheduler.schedule(subgraph);
          HierarchicalSchedule clusterSchedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();
          clusterSchedule.getChildren().add(childSchedule);
          clusterSchedule.setAttachedActor(subgraph);
          // Repetition count?
          schedulesMap.put(subgraph, clusterSchedule);
        }
      }
    }
    return schedulesMap;
  }

}
