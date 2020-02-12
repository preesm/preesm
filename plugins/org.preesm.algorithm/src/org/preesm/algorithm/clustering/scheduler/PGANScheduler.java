package org.preesm.algorithm.clustering.scheduler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.algorithm.clustering.APGANAlgorithm;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.algorithm.synthesis.schedule.transform.ScheduleDataParallelismExhibiter;
import org.preesm.algorithm.synthesis.schedule.transform.ScheduleFlattener;
import org.preesm.algorithm.synthesis.schedule.transform.ScheduleParallelismDepthLimiter;
import org.preesm.algorithm.synthesis.schedule.transform.ScheduleParallelismOptimizer;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
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
  private final Map<AbstractActor, Schedule> scheduleMap;
  /**
   * Does PGANScheduler has to optimize the performance?
   */
  private final boolean                      optimizePerformance;

  /**
   * Get schedule map
   * 
   * @return Map of schedule
   */
  public Map<AbstractActor, Schedule> getScheduleMap() {
    return scheduleMap;
  }

  /**
   * Builds a PGAN scheduler.
   * 
   * @param parentGraph
   *          Parent graph of the subgraph to schedule.
   */
  public PGANScheduler(final PiGraph parentGraph, final boolean optimizePerformance) {
    // Flatten input graph and save references
    this.inputGraph = parentGraph;
    // Copy input graph
    this.copiedGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(this.inputGraph);
    // Build schedules map
    this.scheduleMap = new HashMap<>();
    // Store the choice of optimization
    this.optimizePerformance = optimizePerformance;
    // Check that input graph is actually clusterizable
    isClusterizable();
  }

  /**
   * Create a schedule for the specified cluster.
   * 
   * @return Schedule for the corresponding cluster.
   */
  public Schedule scheduleCluster(final PiGraph cluster) {

    // Clear the schedule map
    this.scheduleMap.clear();

    // Search for the copied subgraph
    PiGraph copiedSubGraph = (PiGraph) this.copiedGraph.lookupVertex(cluster.getName());

    // First clustering pass : PGAN clustering + schedule flattener
    Schedule resultingSchedule = firstClusteringPass(copiedSubGraph);

    AbstractActor remainingActor = null;
    // It might happened that sometimes no schedule is built
    // Example: Only one actor in the subgraph -> we may build it schedule manually.
    if (resultingSchedule == null) {
      // Retrieve all actors that are not interface
      List<AbstractActor> actors = copiedSubGraph.getActors().stream().filter(x -> !(x instanceof InterfaceActor))
          .collect(Collectors.toList());
      // Throw an exception if there are more than one actor or if there is zero actor
      if (actors.isEmpty() || actors.size() > 1) {
        throw new PreesmRuntimeException("PGANScheduler: cannot reduce subgraph actor to an atomic one.");
      } else {
        remainingActor = actors.get(0);
      }
      // If an actor has been found, build it own sequential schedule
      SequentialActorSchedule newSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      newSchedule.getActorList().add(PreesmCopyTracker.getSource(remainingActor));
      newSchedule.setRepetition(1);
      resultingSchedule = newSchedule;
    } else {
      // If there is a schedule result, in means that it is a hierarchical schedule
      // Retrieve the attached actor from it
      remainingActor = ((HierarchicalSchedule) resultingSchedule).getAttachedActor();
    }

    // Build a new schedule hierarchy for the base cluster
    Schedule clusterSchedule = null;
    // Parallel or Sequential in function of delay and type of resulting schedule
    if (ClusteringHelper.isActorDelayed(remainingActor)
        || (resultingSchedule instanceof SequentialHiearchicalSchedule)) {
      clusterSchedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();
    } else {
      clusterSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    }
    // Add resulting schedule to the children and set attached actor
    clusterSchedule.getChildren().add(resultingSchedule);
    ((HierarchicalSchedule) clusterSchedule).setAttachedActor(cluster);

    // Compute BRV to set repetition value of child schedule
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(this.copiedGraph, BRVMethod.LCM);
    resultingSchedule.setRepetition(repetitionVector.get(remainingActor));

    // Second clustering pass : cluster from schedule
    // Tell to the second clustering pass to not rebuild a subgraph for the base schedule since
    // it already had been defined by user or partionning algorithm
    clusterSchedule = secondClusteringPass(cluster, clusterSchedule, true);

    return clusterSchedule;
  }

  /**
   * Schedule the input graph.
   * 
   * @return Schedule for input graph.
   */
  public Schedule scheduleInputGraph() {
    // Clear the schedule map
    this.scheduleMap.clear();

    // First clustering pass : PGAN clustering + schedule flattener
    Schedule resultingSchedule = firstClusteringPass(this.copiedGraph);

    // Second clustering pass : cluster from schedule
    resultingSchedule = secondClusteringPass(this.inputGraph, resultingSchedule, false);

    return resultingSchedule;
  }

  private final Schedule firstClusteringPass(final PiGraph graph) {
    // Clusterize from PiGraph
    Schedule resultingSchedule = clusterizeFromPiGraph(graph);

    // Flatten the resulting schedule
    resultingSchedule = new ScheduleFlattener().performTransform(resultingSchedule);

    return resultingSchedule;
  }

  private final Schedule secondClusteringPass(final PiGraph graph, final Schedule schedule,
      final boolean baseClusterIsAlreadyASubGraph) {
    Schedule preprocessSchedule = schedule;
    // If user want performance optimization, perform them
    if (this.optimizePerformance) {
      preprocessSchedule = new ScheduleParallelismOptimizer().performTransform(preprocessSchedule);
    }
    // Clusterize from copied graph
    Schedule resultingSchedule = clusterizeFromSchedule(graph, preprocessSchedule, 0, !baseClusterIsAlreadyASubGraph);
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
      this.scheduleMap.put(clusterSchedule.getAttachedActor(), clusterSchedule);

      // Store the resulting schedule
      result = clusterSchedule;

      // Recompute BRV
      repetitionVector = PiBRV.compute(graph, BRVMethod.LCM);

      // Search again for couple to cluster
      couples = PiSDFMergeabilty.getConnectedCouple(graph, repetitionVector);
    }

    return result;
  }

  private final Schedule clusterizeFromSchedule(final PiGraph graph, Schedule schedule, int clusterId,
      boolean recluster) {
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
        Schedule processedChild = clusterizeFromSchedule(graph, child, clusterId++, true);
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

      if (recluster) {
        // Build new cluster
        PiGraph newCluster = new PiSDFSubgraphBuilder(graph, childActors, "cluster_" + clusterId).build();
        newCluster.setClusterValue(true);
        // Attached to the new schedule
        hierSchedule.setAttachedActor(newCluster);
      }

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
    if (scheduleMap.containsKey(actor)) {
      Schedule subSched = scheduleMap.get(actor);
      scheduleMap.remove(actor);
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
