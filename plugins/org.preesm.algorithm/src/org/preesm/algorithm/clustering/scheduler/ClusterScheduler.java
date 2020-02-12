package org.preesm.algorithm.clustering.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.synthesis.schedule.SchedulePrinterSwitch;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;

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
          registerClusterSchedule(schedulesMap, scheduler, inputGraph, subgraph);
        }
      }
    }

    return schedulesMap;
  }

  private static void registerClusterSchedule(Map<AbstractActor, Schedule> scheduleMap, PGANScheduler scheduler,
      final PiGraph inputGraph, final PiGraph subgraph) {
    HierarchicalSchedule childSchedule = (HierarchicalSchedule) scheduler.schedule(subgraph);
    AbstractActor remainingCluster = childSchedule.getAttachedActor();

    // Build a new hierarchy
    HierarchicalSchedule clusterSchedule = null;
    if (ClusteringHelper.isActorDelayed(remainingCluster)) {
      clusterSchedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();
    } else {
      clusterSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    }
    clusterSchedule.getChildren().add(childSchedule);
    clusterSchedule.setAttachedActor(subgraph);

    // Compute BRV
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(inputGraph, BRVMethod.LCM);
    clusterSchedule.setRepetition(repetitionVector.get(childSchedule.getAttachedActor()));

    // Register the schedule in the map
    scheduleMap.put(subgraph, clusterSchedule);

    // Print resulting schedule (DEGUB)
    PreesmLogger.getLogger().log(Level.INFO, SchedulePrinterSwitch.print(clusterSchedule));
  }

}
