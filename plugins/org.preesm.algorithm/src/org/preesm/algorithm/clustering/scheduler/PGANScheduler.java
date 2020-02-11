package org.preesm.algorithm.clustering.scheduler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.algorithm.clustering.APGANAlgorithm;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.synthesis.schedule.SchedulePrinterSwitch;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.algorithm.synthesis.schedule.transform.ScheduleDataParallelismExhibiter;
import org.preesm.algorithm.synthesis.schedule.transform.ScheduleFlattener;
import org.preesm.algorithm.synthesis.schedule.transform.ScheduleParallelismDepthLimiter;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;

/**
 * PGAN scheduler used to schedule actors contained in a cluster.
 *
 * @author dgageot
 */
public class PGANScheduler {

  /**
   * Input graph to schedule.
   */
  private final PiGraph                      inputGraph;
  /**
   * Copy of input graph.
   */
  private PiGraph                            copiedGraph;
  /**
   * Schedules map.
   */
  private final Map<AbstractActor, Schedule> schedulesMap;

  /**
   * Builds a PGAN scheduler.
   * 
   * @param parentGraph
   *          Parent graph of the subgraph to schedule.
   */
  public PGANScheduler(final PiGraph parentGraph) {
    // Flatten input graph and save references
    this.inputGraph = parentGraph;
    // Copy input graph
    this.copiedGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(this.inputGraph);
    // Build schedules map
    this.schedulesMap = new HashMap<>();
    // Check that input graph is actually clusterizable
    isClusterizable();
  }

  /**
   * Create a schedule for the specified cluster.
   * 
   * @return Created schedule.
   */
  public Schedule schedule(final PiGraph subGraph) {

    // Search for the copied subgraph
    PiGraph copiedSubGraph = (PiGraph) this.copiedGraph.lookupVertex(subGraph.getName());

    // First clustering pass : PGAN clustering + schedule flattener
    Schedule resultingSchedule = firstClusteringPass(copiedSubGraph);

    // Second clustering pass : cluster from schedule
    resultingSchedule = secondClusteringPass(subGraph, resultingSchedule);

    new SchedulePrinterSwitch();
    PreesmLogger.getLogger().log(Level.INFO, SchedulePrinterSwitch.print(resultingSchedule));

    return resultingSchedule;
  }

  private final Schedule firstClusteringPass(final PiGraph graph) {
    // Clusterize from PiGraph
    Schedule resultingSchedule = clusterizeFromPiGraph(graph);
    // Flatten the resulting schedule
    resultingSchedule = new ScheduleFlattener().performTransform(resultingSchedule);

    return resultingSchedule;
  }

  private final Schedule secondClusteringPass(final PiGraph graph, final Schedule schedule) {
    // Clusterize from copied graph
    Schedule resultingSchedule = clusterizeFromSchedule(graph, schedule, 0);
    // Exhibit data parallelism
    resultingSchedule = new ScheduleDataParallelismExhibiter().performTransform(resultingSchedule);
    // Limit parallelism at the first layer
    resultingSchedule = new ScheduleParallelismDepthLimiter(1).performTransform(resultingSchedule);

    return resultingSchedule;

  }

  private final Schedule clusterizeFromPiGraph(final PiGraph graph) {
    // Resulting schedule
    Schedule result = null;
    // Init cluster number
    int clusterId = 0;
    // Compute BRV
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(graph, BRVMethod.LCM);
    // List all clusterizable couples
    List<Pair<AbstractActor, AbstractActor>> couples = PiSDFMergeabilty.getConnectedCouple(graph, repetitionVector);

    // Cluster until couples list is not empty
    while (!couples.isEmpty()) {
      // Search best candidate to be clustered according the highest common repetition count
      Pair<AbstractActor, AbstractActor> couple = APGANAlgorithm.getBestCouple(couples, repetitionVector);

      // Clusterize given actors and generate a schedule
      HierarchicalSchedule clusterSchedule = (HierarchicalSchedule) clusterize(graph,
          Arrays.asList(couple.getLeft(), couple.getRight()), repetitionVector, clusterId++);

      // Register the resulting schedule into the schedules map
      this.schedulesMap.put(clusterSchedule.getAttachedActor(), clusterSchedule);

      // Store the resulting schedule
      result = clusterSchedule;

      // Recompute BRV
      repetitionVector = PiBRV.compute(graph, BRVMethod.LCM);

      // Search again for couple to cluster
      couples = PiSDFMergeabilty.getConnectedCouple(graph, repetitionVector);
    }

    return result;
  }

  private final Schedule clusterizeFromSchedule(final PiGraph graph, Schedule schedule, int clusterId) {
    // If it is an hierarchical schedule, explore
    if (schedule instanceof HierarchicalSchedule && schedule.hasAttachedActor()) {
      HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      // Retrieve childrens schedule and actors
      List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(hierSchedule.getChildren());
      List<AbstractActor> childActors = new LinkedList<>();
      // Clear list of children schedule
      hierSchedule.getChildren().clear();
      for (Schedule child : childSchedules) {
        // Explore children and process clustering into
        Schedule processedChild = clusterizeFromSchedule(graph, child, clusterId++);
        hierSchedule.getChildren().add(processedChild);
        // Retrieve list of children AbstractActor (needed for clusterization)
        if (child instanceof HierarchicalSchedule && child.hasAttachedActor()) {
          childActors.add(((HierarchicalSchedule) processedChild).getAttachedActor());
        } else if (child instanceof HierarchicalSchedule && !child.hasAttachedActor()) {
          final List<AbstractActor> actors = ScheduleUtil.getAllReferencedActors(child.getChildren().get(0));
          childActors.addAll(actors);
        } else {
          final List<AbstractActor> actors = ScheduleUtil.getAllReferencedActors(processedChild);
          childActors.addAll(actors);
        }
      }

      // Build new cluster
      PiGraph newCluster = new PiSDFSubgraphBuilder(graph, childActors, "cluster_" + clusterId).build();
      newCluster.setClusterValue(true);
      // Attached to the new schedule
      hierSchedule.setAttachedActor(newCluster);
    }

    return schedule;
  }

  private final Schedule clusterize(final PiGraph graph, List<AbstractActor> actors,
      Map<AbstractVertex, Long> repetitionVector, int clusterId) {

    // Build corresponding hierarchical actor
    PiGraph cluster = new PiSDFSubgraphBuilder(graph, actors, "cluster_" + clusterId).build();
    cluster.setClusterValue(true);

    // Build corresponding hierarchical schedule
    HierarchicalSchedule schedule = buildHierarchicalSchedule(actors, repetitionVector);

    // Attach cluster to hierarchical schedule
    schedule.setAttachedActor(cluster);

    return schedule;
  }

  private final HierarchicalSchedule buildHierarchicalSchedule(List<AbstractActor> actors,
      Map<AbstractVertex, Long> repetitionVector) {

    // Create parallel or sequential schedule
    HierarchicalSchedule schedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();

    // Retrieve actor list
    List<AbstractActor> actorList = actors;

    // Compute cluster repetition count
    long clusterRepetition = MathFunctionsHelper.gcd(CollectionUtil.mapGetAll(repetitionVector, actorList));

    // Construct a sequential schedule
    for (AbstractActor a : actorList) {
      addActorToHierarchicalSchedule(schedule, a, repetitionVector.get(a) / clusterRepetition);
    }

    return schedule;
  }

  private final void addActorToHierarchicalSchedule(HierarchicalSchedule schedule, AbstractActor actor,
      long repetition) {
    // If we already had clustered actor, retrieve it schedule
    if (schedulesMap.containsKey(actor)) {
      Schedule subSched = schedulesMap.get(actor);
      schedulesMap.remove(actor);
      subSched.setRepetition(repetition);
      schedule.getScheduleTree().add(subSched);
    } else {
      // Create an sequential actor schedule
      ActorSchedule actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      actorSchedule.getActorList().add(PreesmCopyTracker.getSource(actor));
      actorSchedule.setRepetition(repetition);

      Schedule outputSchedule = null;
      // If the actor is parallelizable, create a parallel hierarchical schedule
      if (!ClusteringHelper.isActorDelayed(actor)) {
        ParallelHiearchicalSchedule parallelNode = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
        parallelNode.getChildren().add(actorSchedule);
        parallelNode.setRepetition(1);
        parallelNode.setAttachedActor(null);
        outputSchedule = parallelNode;
      } else {
        outputSchedule = actorSchedule;
      }

      // Register in the schedule with original actor to be able to clusterize the non-copy graph
      schedule.getScheduleTree().add(outputSchedule);
    }
  }

  private final void isClusterizable() {
    // Check for uncompatible delay (with getter/setter)
    for (Fifo fifo : inputGraph.getFifosWithDelay()) {
      Delay delay = fifo.getDelay();

      // If delay has getter/setter, throw an exception
      if (delay.getActor().getDataInputPort().getIncomingFifo() != null
          || delay.getActor().getDataOutputPort().getOutgoingFifo() != null) {
        throw new PreesmRuntimeException(
            "PGANScheduler: getter/setter are not handled on [" + delay.getActor().getName() + "]");
      }
    }
  }

}
