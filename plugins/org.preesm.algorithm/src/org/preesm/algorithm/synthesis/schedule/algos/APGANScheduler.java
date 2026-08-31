package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.preesm.algorithm.clustering.ClusterCreator;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * This class create a {@link Schedule schedule} directly from a {@link PiGraph PiSDF} graph (no need to make a SrDAG
 * transformation before). Here is the way it works: (1) identify all possible {@link AbstractActor actors} couples (2)
 * select the one with the highest repetition value (3) cluster the pair (4) update all possible actor couples, with the
 * newly created hierarchical actor containing the selected pair (5) start again. During this process, a
 * {@link HierarchicalSchedule hierarchical schedule} is created, with each sub-cluster containing an actor pair
 * attached to a sub-schedule
 *
 * @author dgageot
 */
public class APGANScheduler extends AbstractScheduler {

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario,
      Map<String, String> parameters) {

    checkSchedulability(piGraph);
    final Schedule schedule = schedule(piGraph, scenario);
    return new SynthesisResult(null, schedule, null);
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
      throw new PreesmRuntimeException(
          "PGANClusterScheduler: hierarchy is not handled in [" + cluster.getName() + "] in current version of PREESM");
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
  private static final Schedule schedule(final PiGraph cluster, Scenario scenario) {

    final PiGraph clusterCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(cluster);
    clusterCopy.setName(cluster.getName() + "_copy");

    Schedule outputSchedule = null; // Resulting schedule
    int clusterId = 0; // Cluster number for naming
    final Map<AbstractActor, Schedule> scheduleMap = new HashMap<>();
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(clusterCopy, BRVMethod.LCM);

    // List all clusterizable couples
    List<Pair<AbstractActor, AbstractActor>> couples = getClusterizableCouples(clusterCopy, repetitionVector, scenario);

    // Cluster until couples list is not empty
    while (!couples.isEmpty()) {
      final Pair<AbstractActor, AbstractActor> couple = getBestCouple(couples, repetitionVector);

      // Creating Schedule from pair
      final HierarchicalSchedule clusterSchedule = (HierarchicalSchedule) clusterize(clusterCopy, couple,
          repetitionVector, clusterId++, scheduleMap, scenario);

      scheduleMap.put(clusterSchedule.getAttachedActor(), clusterSchedule);
      outputSchedule = clusterSchedule;

      // Updating graph and couples
      repetitionVector = PiBRV.compute(clusterCopy, BRVMethod.LCM);
      couples = getClusterizableCouples(clusterCopy, repetitionVector, scenario);
    }

    outputSchedule = finalizeScheduling(clusterCopy, outputSchedule);

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
   * @param rv
   *          the repetition vector of the cluster
   * @param clusterId
   *          the current ID of the pair
   * @param scheduleMap
   *          map to keep track of already {@link Schedule scheduled} {@link AbstractActor actors}.
   * @return the {@link AbstractActor actor} pair {@link Schedule schedule}.
   */
  private static final Schedule clusterize(final PiGraph cluster, Pair<AbstractActor, AbstractActor> couple,
      Map<AbstractVertex, Long> rv, int clusterId, Map<AbstractActor, Schedule> scheduleMap, final Scenario scenario) {

    // Build corresponding hierarchical actor
    final List<AbstractActor> actors = Arrays.asList(couple.getLeft(), couple.getRight());
    final Set<AbstractActor> temporarySet = new HashSet<>(actors);
    final String temporaryClusterName = "cluster" + clusterId;
    final long nClusterRep = MathFunctionsHelper.gcd(rv.get(actors.get(0)), rv.get(actors.get(1)));

    final PiGraph clusteredPair = ClusterCreator.create(cluster, temporarySet, temporaryClusterName);
    setClusterConstraints(clusteredPair, scenario);

    // Build corresponding hierarchical schedule
    final HierarchicalSchedule schedule = buildHierarchicalSchedule(actors, rv, scheduleMap, nClusterRep);

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
   * @param rv
   *          repetition vector of the top cluster
   * @param scheduleMap
   *          map to keep track of actors already scheduled
   * @return a Hierarchical Schedule
   */
  private static final HierarchicalSchedule buildHierarchicalSchedule(List<AbstractActor> actorList,
      Map<AbstractVertex, Long> rv, Map<AbstractActor, Schedule> scheduleMap, long clusterRep) {

    // Create parallel or sequential schedule
    final HierarchicalSchedule schedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();
    schedule.setRepetition(clusterRep);
    for (final AbstractActor a : actorList) {
      addActorToHierarchicalSchedule(schedule, a, rv.get(a) / clusterRep, scheduleMap);
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
      scheduleMap.remove(actor); // We suppose that actors can appear only one time ?
      subSched.setRepetition(repetition);
      schedule.getScheduleTree().add(subSched);

    } else {

      // Create a sequential actor schedule
      final ActorSchedule actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
      actorSchedule.getActorList().add(actor);
      actorSchedule.setRepetition(repetition);

      Schedule outputSchedule = null;

      // If the actor is parallelizable, create a parallel hierarchical schedule
      if (!isActorDelayed(actor)) {
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
   * @param clusterCopy
   *          modified cluster during scheduling.
   * @param childSchedule
   *          Schedule of contained actors.
   * @return the builded schedule.
   */
  private static Schedule finalizeScheduling(PiGraph clusterCopy, Schedule childSchedule) {

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
      newSchedule.getActorList().add(remainingActor);
      newSchedule.setRepetition(1);
      childSchedule = newSchedule;

    } else {

      // If there is a schedule result, it means that it is a hierarchical schedule, retrieve the attached actor from it
      remainingActor = ((HierarchicalSchedule) childSchedule).getAttachedActor();
    }

    // Build a new schedule hierarchy for the cluster schedule. Parallel or sequential in function of delay and the type
    // of child schedule
    HierarchicalSchedule clusterSchedule = null;
    if (isActorDelayed(remainingActor) || (childSchedule instanceof SequentialHiearchicalSchedule)) {
      clusterSchedule = ScheduleFactory.eINSTANCE.createSequentialHiearchicalSchedule();
    } else {
      clusterSchedule = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    }

    // Add child schedule to the cluster schedule and set the attached actor
    clusterSchedule.getChildren().add(childSchedule);
    clusterSchedule.setAttachedActor(clusterCopy);

    // Compute BRV to set repetition value of child schedule
    final Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(clusterCopy, BRVMethod.LCM);
    childSchedule.setRepetition(repetitionVector.get(remainingActor));

    return clusterSchedule;
  }

  /**
   * @param graph
   *          input graph
   * @param brv
   *          repetition vector
   * @return list of clusterizable couple
   */
  private static List<Pair<AbstractActor, AbstractActor>> getClusterizableCouples(final PiGraph graph,
      final Map<AbstractVertex, Long> brv, final Scenario scenario) {
    List<Pair<AbstractActor, AbstractActor>> couples = PiSDFMergeabilty.getConnectedCouple(graph, brv);

    // Removing the pair if they don't share at least one common component
    couples = couples.stream().filter(pair -> {
      final AbstractActor a = pair.getLeft();
      final AbstractActor b = pair.getRight();
      final List<ComponentInstance> ca = new ArrayList<>(scenario.getPossibleMappings(a));
      final List<ComponentInstance> cb = scenario.getPossibleMappings(b);
      ca.retainAll(cb);
      return !ca.isEmpty();
    }).toList();

    return couples;
  }

  private static void setClusterConstraints(final PiGraph subCluster, final Scenario scenario) {

    final List<AbstractActor> actorsInCluster = subCluster.getActors().stream()
        .filter(a -> !(a instanceof DataInterface || a instanceof DelayActor)).toList();

    List<ComponentInstance> clusterConstraints = null;
    for (final AbstractActor a : actorsInCluster) {
      final List<ComponentInstance> ca = new ArrayList<>(scenario.getPossibleMappings(a));
      if (clusterConstraints == null) {
        clusterConstraints = ca;
      } else {
        clusterConstraints.retainAll(ca);
      }
    }
    if (clusterConstraints == null || clusterConstraints.isEmpty()) {
      throw new PreesmRuntimeException(subCluster.getName() + " constraints are empty");
    }

    clusterConstraints.stream().forEach(c -> scenario.getConstraints().addConstraint(c, subCluster));
  }

  /**
   * @param actor
   *          actor to check if it is delayed
   * @return true if actor is delayed, false otherwise
   */
  private static final boolean isActorDelayed(AbstractActor actor) {
    // Retrieve every Fifo with delay connected to actor
    for (final DataPort dp : actor.getAllDataPorts()) {
      if (dp.getFifo().getDelay() != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param couples
   *          list of candidates
   * @param brv
   *          repetition vector
   * @return best candidate
   */
  private static Pair<AbstractActor, AbstractActor> getBestCouple(List<Pair<AbstractActor, AbstractActor>> couples,
      Map<AbstractVertex, Long> brv) {

    // Find the couple that maximize gcd
    long maxGcdRv = 0;
    Pair<AbstractActor, AbstractActor> maxCouple = null;
    for (final Pair<AbstractActor, AbstractActor> l : couples) {
      // Compute RV gcd
      final long tmpGcdRv = ArithmeticUtils.gcd(brv.get(l.getLeft()), brv.get(l.getRight()));
      if (tmpGcdRv > maxGcdRv) {
        maxGcdRv = tmpGcdRv;
        maxCouple = l;
      }
    }
    // If no couple has been found, throw an exception
    if (maxCouple == null) {
      throw new PreesmRuntimeException("APGANAlgorithm: Cannot find a couple to work on");
    }
    return new ImmutablePair<>(maxCouple.getLeft(), maxCouple.getRight());
  }

}
