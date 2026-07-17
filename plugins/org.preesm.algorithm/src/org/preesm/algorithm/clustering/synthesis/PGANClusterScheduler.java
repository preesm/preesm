package org.preesm.algorithm.clustering.synthesis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.algorithm.clustering.identifier.ActorMerger;
import org.preesm.algorithm.clustering.identifier.ClusteringHelper;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
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

/**
 * It differ from the class PGANScheduler in the way that is made to only {@link Schedule schedule} cluster, never the
 * top graph. It allows to get rid of different methods, or part of methods that become useless, compared to
 * {@link PGANScheduler} class.
 */
public class PGANClusterScheduler {

  private PGANClusterScheduler() {

  }

  /***
   * Main function. It Builds a {@link Schedule} from a cluster. It will use the Acyclic Pair-wise Grouping of Adjacent
   * Nodes (APGAN) method, that uses the PGAN method with the best choices of {@link AbstractActor actors} pair to
   * minimize the size of the {@link Fifo Fifos} and the size of the generated code.
   *
   * @param cluster
   *          The cluster to {@link Schedule schedule}
   * @param optimize
   *          boolean that unlocks an optimization pass if set to true.
   * @param parallelism
   *          boolean that unlocks parallelization expression in output {@link Schedule}.
   * @return the {@link Schedule} of cluster
   */
  public static Schedule performAPGANSchedule(final PiGraph cluster, final boolean optimize,
      final boolean parallelism) {

    checkSchedulability(cluster);

    // First clustering pass: schedule actors inside the cluster
    final Schedule clusterSchedule = schedule(cluster);

    // If user want performance optimization, perform them
    if (optimize) {
      new ScheduleParallelismOptimizer().performTransform(clusterSchedule);
    }

    if (parallelism) {

      // Exhibit data parallelism
      new ScheduleDataParallelismExhibiter().performTransform(clusterSchedule);

      // Limit parallelism at the first layer
      new ScheduleParallelismDepthLimiter(1).performTransform(clusterSchedule);
    }

    return clusterSchedule;
  }

  /***
   * Checks if the cluster can be scheduled. If no, it will throw an error.
   *
   * @param cluster
   *          the cluster to check for.
   */
  private static final void checkSchedulability(final PiGraph cluster) {

    // Check if the graph is flatten
    if (cluster.getActors().stream().anyMatch(PiGraph.class::isInstance)) {
      throw new PreesmRuntimeException("PGANClusterScheduler: hierarchy is not handled in [" + cluster.getName() + "]");
    }

    // Check for incompatible delay (with getter/setter)
    for (final Fifo fifo : cluster.getFifosWithDelay()) {
      final Delay delay = fifo.getDelay();

      // If delay has getter/setter, throw an exception
      if (delay.getDelayActor().getDataInputPort().getIncomingFifo() != null
          || delay.getDelayActor().getDataOutputPort().getOutgoingFifo() != null) {
        throw new PreesmRuntimeException(
            "PGANClusterScheduler: getter/setter are not handled on [" + delay.getDelayActor().getName() + "]");
      }
    }
  }

  /**
   * Make the {@link Schedule} of the cluster.
   *
   * @param cluster
   *          the cluster to be scheduled.
   * @return the {@link Schedule schedule} of the cluster
   */
  private static final Schedule schedule(final PiGraph cluster) {

    final PiGraph clusterCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(cluster);

    // Resulting schedule
    Schedule outputSchedule = null;

    // Init cluster number
    int clusterId = 0;

    // Init schedule map
    final Map<AbstractActor, Schedule> scheduleMap = new HashMap<>();

    // Compute BRV
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(clusterCopy, BRVMethod.LCM);

    // List all clusterizable couples
    List<Pair<AbstractActor, AbstractActor>> couples = ClusteringHelper
        .getClusterizableCouplesWithoutScenarioConstraints(clusterCopy, repetitionVector);

    // Cluster until couples list is not empty
    while (!couples.isEmpty()) {

      // Search best candidate to be clustered according the highest common repetition count
      final Pair<AbstractActor, AbstractActor> couple = APGANAlgorithm.getBestCouple(couples, repetitionVector);

      // Cluster given actors pair and generate a schedule
      final HierarchicalSchedule clusterSchedule = (HierarchicalSchedule) clusterize(clusterCopy, couple,
          repetitionVector, clusterId++, scheduleMap);

      // Register the resulting schedule into the schedules map
      scheduleMap.put(clusterSchedule.getAttachedActor(), clusterSchedule);

      // Store the resulting schedule
      outputSchedule = clusterSchedule;

      // Recompute BRV
      repetitionVector = PiBRV.compute(clusterCopy, BRVMethod.LCM);

      // Search again for couple to cluster
      couples = ClusteringHelper.getClusterizableCouplesWithoutScenarioConstraints(clusterCopy, repetitionVector);
    }

    outputSchedule = finalizeScheduling(cluster, clusterCopy, outputSchedule);

    // Flatten the schedule
    new ScheduleFlattener().performTransform(outputSchedule);

    return outputSchedule;
  }

  /***
   * Makes a little cluster of the chosen pair of {@link AbstractActor actors} inside the cluster. It is used to update
   * the APGAN method, to select another {@link AbstractActor actor} pair.
   *
   * @param cluster
   *          the cluster to {@link Schedule schedule}.
   * @param actors
   *          the {@link AbstractActor actor} pair
   * @param repetitionVector
   *          the repetition vector of the cluster
   * @param clusterId
   *          the current ID of the pair
   * @param scheduleMap
   *          map to keep track of already {@link Schedule scheduled} {@link AbstractActor actors}.
   * @return the {@link AbstractActor actor} pair {@link Schedule schedule}.
   */
  private static final Schedule clusterize(final PiGraph cluster, Pair<AbstractActor, AbstractActor> couple,
      Map<AbstractVertex, Long> repetitionVector, int clusterId, Map<AbstractActor, Schedule> scheduleMap) {

    // Build corresponding hierarchical actor
    final List<AbstractActor> actors = Arrays.asList(couple.getLeft(), couple.getRight());
    final Set<AbstractActor> temporarySet = new HashSet<>(actors);
    final String temporaryClusterName = "cluster" + clusterId;
    final PiGraph clusteredPair = ActorMerger.mergeActors(cluster, temporarySet, temporaryClusterName);
    clusteredPair.setClusterValue(true);

    // Build corresponding hierarchical schedule
    final HierarchicalSchedule schedule = buildHierarchicalSchedule(actors, repetitionVector, scheduleMap);

    // Attach cluster to hierarchical schedule
    schedule.setAttachedActor(clusteredPair);

    return schedule;
  }

  /***
   * Builds a {@link HierarchicalSchedule hierarchical schedule} for the given {@link AbstractActor actors}. A
   * hierarchical schedule can contains actors, or another {@link Schedule schedule}. It matches the hierarchy of the
   * {@link PiGraph} cluster.
   *
   * @param actors
   *          target
   * @param repetitionVector
   *          repetition vector of the top cluster
   * @param scheduleMap
   *          map to keep track of actors already scheduled
   * @return a Hierarchical Schedule
   */
  private static final HierarchicalSchedule buildHierarchicalSchedule(List<AbstractActor> actors,
      Map<AbstractVertex, Long> repetitionVector, Map<AbstractActor, Schedule> scheduleMap) {

    // Create parallel or sequential schedule
    final HierarchicalSchedule schedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();

    // Retrieve actor list
    final List<AbstractActor> actorList = actors;

    // Compute cluster repetition count
    final long clusterRepetition = MathFunctionsHelper.gcd(CollectionUtil.mapGetAll(repetitionVector, actorList));

    // Construct a sequential schedule
    for (final AbstractActor a : actorList) {
      addActorToHierarchicalSchedule(schedule, a, repetitionVector.get(a) / clusterRepetition, scheduleMap);
    }

    return schedule;
  }

  /**
   * Add an actor to the builded hierarchical schedule.
   *
   * @param schedule
   *          The target schedule
   * @param actor
   *          the actor to add
   * @param repetition
   *          the repetition of this actor
   * @param scheduleMap
   *          map to keep track of actors already scheduled
   *
   */
  private static final void addActorToHierarchicalSchedule(HierarchicalSchedule schedule, AbstractActor actor,
      long repetition, Map<AbstractActor, Schedule> scheduleMap) {

    // If actor was already clustered, retrieve its schedule
    if (scheduleMap.containsKey(actor)) {
      final Schedule subSched = scheduleMap.get(actor);
      scheduleMap.remove(actor);
      subSched.setRepetition(repetition);
      schedule.getScheduleTree().add(subSched);

    } else {

      // Create an sequential actor schedule
      final ActorSchedule actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      actorSchedule.getActorList().add(PreesmCopyTracker.getSource(actor));
      actorSchedule.setRepetition(repetition);

      Schedule outputSchedule = null;

      // If the actor is parallelizable, create a parallel hierarchical schedule
      if (!ClusteringHelper.isActorDelayed(actor)) {
        final ParallelHiearchicalSchedule parallelNode = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
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

  /**
   * Performs additional operations and verifications of the newly created Schedule, like verifying that the schedule
   * exists, and adding another level of hierarchy.
   *
   * @param cluster
   *          untouched cluster.
   * @param clusterCopy
   *          modified cluster during scheduling.
   * @param childSchedule
   *          Schedule of contained actors.
   * @return the builded schedule.
   */
  private static Schedule finalizeScheduling(final PiGraph cluster, PiGraph clusterCopy, Schedule childSchedule) {

    AbstractActor remainingActor = null;

    // It might happened that sometimes, no schedule is built. Example: Only one actor in the subgraph, the PGAN
    // algorithm cannot cluster anything so no schedule is produced. We may schedule it manually
    if (childSchedule == null) {

      // Retrieve all actors that are not interface
      final List<
          AbstractActor> actors = clusterCopy.getActors().stream().filter(x -> !(x instanceof InterfaceActor)).toList();

      // Throw an exception if there are more than one actor or if there is zero actor
      if (actors.isEmpty() || actors.size() > 1) {
        throw new PreesmRuntimeException("PGANScheduler: cannot reduce subgraph actor to an atomic one.");
      }

      remainingActor = actors.get(0);

      // If an actor has been found, build it schedule
      final SequentialActorSchedule newSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      newSchedule.getActorList().add(PreesmCopyTracker.getSource(remainingActor));
      newSchedule.setRepetition(1);
      childSchedule = newSchedule;

    } else {

      // If there is a schedule result, it means that it is a hierarchical schedule, retrieve the attached actor from it
      remainingActor = ((HierarchicalSchedule) childSchedule).getAttachedActor();
    }

    // Build a new schedule hierarchy for the cluster schedule. Parallel or sequential in function of delay and the type
    // of child schedule
    HierarchicalSchedule clusterSchedule = null;
    if (ClusteringHelper.isActorDelayed(remainingActor) || (childSchedule instanceof SequentialHiearchicalSchedule)) {
      clusterSchedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();
    } else {
      clusterSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    }

    // Add child schedule to the cluster schedule and set the attached actor
    clusterSchedule.getChildren().add(childSchedule);
    clusterSchedule.setAttachedActor(cluster);

    // Compute BRV to set repetition value of child schedule
    final Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(clusterCopy, BRVMethod.LCM);
    childSchedule.setRepetition(repetitionVector.get(remainingActor));

    return clusterSchedule;
  }

}
