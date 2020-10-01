/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * Scheduler that use Pairwise Grouping of Adjacent Nodes (PGAN) to generate schedule with for-loops.
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
   * Schedule map.
   */
  private final Map<AbstractActor, Schedule> scheduleMap;
  /**
   * Scenario.
   */
  private final Scenario                     scenario;
  /**
   * Optimize performance by exhibiting parallelism hidden by sequential hierarchy.
   */
  private final boolean                      optimizePerformance;
  /**
   * Do CSs have to contain parallel information?
   */
  private final boolean                      parallelism;

  /**
   * Builds a PGAN scheduler.
   * 
   * @param inputGraph
   *          Base graph to schedule or that contained a cluster to schedule.
   * @param optimizePerformance
   *          If true, second pass of clustering will optimize parallelism inside of sequential hierarchy.
   * @param parallelism
   *          If true, parallelism information are expressed in resulting cluster schedules.
   */
  public PGANScheduler(final PiGraph inputGraph, final Scenario scenario, final boolean optimizePerformance,
      final boolean parallelism) {
    // Save reference of input graph
    this.inputGraph = inputGraph;
    // Copy input graph for the first pass of clustering
    this.copiedGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(this.inputGraph);
    // Build an empty schedule map
    this.scheduleMap = new HashMap<>();
    // Save reference of scenario
    this.scenario = scenario;
    // Store the choice of optimization and parallelism information
    this.optimizePerformance = optimizePerformance;
    this.parallelism = parallelism;
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

  /**
   * Builds a schedule hierarchy for the specified cluster.
   * 
   * @param cluster
   *          Reference to the cluster in the original graph.
   * @param copiedCluster
   *          Reference to the cluster in the copied graph.
   * @param childSchedule
   *          Schedule of contained actors.
   * @return
   */
  private Schedule buildClusterSchedule(final PiGraph cluster, PiGraph copiedCluster, Schedule childSchedule) {

    AbstractActor remainingActor = null;
    // It might happened that sometimes, no schedule is built. Example: Only one actor in the subgraph, the PGAN
    // algorithm cannot cluster anything so no schedule is produced. We may schedule it manually
    if (childSchedule == null) {
      // Retrieve all actors that are not interface
      List<AbstractActor> actors = copiedCluster.getActors().stream().filter(x -> !(x instanceof InterfaceActor))
          .collect(Collectors.toList());
      // Throw an exception if there are more than one actor or if there is zero actor
      if (actors.isEmpty() || actors.size() > 1) {
        throw new PreesmRuntimeException("PGANScheduler: cannot reduce subgraph actor to an atomic one.");
      } else {
        remainingActor = actors.get(0);
      }
      // If an actor has been found, build it schedule
      SequentialActorSchedule newSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
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
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(this.copiedGraph, BRVMethod.LCM);
    childSchedule.setRepetition(repetitionVector.get(remainingActor));

    return clusterSchedule;
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

  /**
   * Throws an exception if the scheduler is unable to process the given graph.
   * 
   * @param graph
   *          Graph to be checked.
   */
  private final void checkSchedulability(final PiGraph graph) {

    // Check if the graph is flatten
    if (graph.getActors().stream().anyMatch(x -> x instanceof PiGraph)) {
      throw new PreesmRuntimeException("PGANScheduler: hierarchy are not handled in [" + graph.getName() + "]");
    }

    // Check for incompatible delay (with getter/setter)
    for (Fifo fifo : graph.getFifosWithDelay()) {
      Delay delay = fifo.getDelay();
      // If delay has getter/setter, throw an exception
      if (delay.getActor().getDataInputPort().getIncomingFifo() != null
          || delay.getActor().getDataOutputPort().getOutgoingFifo() != null) {
        throw new PreesmRuntimeException(
            "PGANScheduler: getter/setter are not handled on [" + delay.getActor().getName() + "]");
      }
    }

  }

  private final Schedule clusterize(final PiGraph graph, List<AbstractActor> actors,
      Map<AbstractVertex, Long> repetitionVector, int clusterId) {

    // Build corresponding hierarchical actor
    PiGraph cluster = new PiSDFSubgraphBuilder(graph, actors, "cluster_" + clusterId).build();
    cluster.setClusterValue(true);

    // Add constraint to the cluster
    for (ComponentInstance component : ClusteringHelper.getListOfCommonComponent(actors, scenario)) {
      scenario.getConstraints().addConstraint(component, cluster);
    }

    // Build corresponding hierarchical schedule
    HierarchicalSchedule schedule = buildHierarchicalSchedule(actors, repetitionVector);

    // Attach cluster to hierarchical schedule
    schedule.setAttachedActor(cluster);

    return schedule;
  }

  private final Schedule clusterizeFromPiGraph(final PiGraph graph) {
    // Resulting schedule
    Schedule result = null;

    // Init cluster number
    int clusterId = 0;

    // Compute BRV
    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(graph, BRVMethod.LCM);

    // List all clusterizable couples
    List<Pair<AbstractActor, AbstractActor>> couples = ClusteringHelper.getClusterizableCouples(graph, repetitionVector,
        scenario);

    // Cluster until couples list is not empty
    while (!couples.isEmpty()) {
      // Search best candidate to be clustered according the highest common repetition count
      Pair<AbstractActor, AbstractActor> couple = APGANAlgorithm.getBestCouple(couples, repetitionVector);

      // Cluster given actors and generate a schedule
      HierarchicalSchedule clusterSchedule = (HierarchicalSchedule) clusterize(graph,
          Arrays.asList(couple.getLeft(), couple.getRight()), repetitionVector, clusterId++);

      // Register the resulting schedule into the schedules map
      this.scheduleMap.put(clusterSchedule.getAttachedActor(), clusterSchedule);

      // Store the resulting schedule
      result = clusterSchedule;

      // Recompute BRV
      repetitionVector = PiBRV.compute(graph, BRVMethod.LCM);

      // Search again for couple to cluster
      couples = ClusteringHelper.getClusterizableCouples(graph, repetitionVector, scenario);
    }

    return result;
  }

  private final Schedule clusterizeFromSchedule(final PiGraph graph, Schedule schedule, final int clusterId,
      boolean recluster) {

    // If it is an hierarchical schedule, explore
    if (schedule instanceof HierarchicalSchedule && schedule.hasAttachedActor()) {

      HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      // Retrieve children schedules and actors
      List<Schedule> childSchedules = new LinkedList<>();
      childSchedules.addAll(hierSchedule.getChildren());
      List<AbstractActor> childActors = new LinkedList<>();

      // Clear list of children schedule
      hierSchedule.getChildren().clear();
      for (Schedule child : childSchedules) {

        // Explore children and process clustering into
        Schedule processedChild = clusterizeFromSchedule(graph, child, clusterId + 1, true);
        hierSchedule.getChildren().add(processedChild);

        // Retrieve list of children AbstractActor
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

        // Add constraint to the cluster
        for (ComponentInstance component : ClusteringHelper.getListOfCommonComponent(childActors, scenario)) {
          scenario.getConstraints().addConstraint(component, newCluster);
        }

        // Attached to the new schedule
        hierSchedule.setAttachedActor(newCluster);
      }

    }

    return schedule;
  }

  /**
   * Schedule the specified cluster.
   * 
   * @param cluster
   *          Reference to the cluster contained in the parent graph.
   * 
   * @return Schedule for the corresponding cluster.
   */
  public Schedule scheduleCluster(final PiGraph cluster) {

    // Check if the subgraph is schedulable
    checkSchedulability(cluster);

    // Search for the cluster in the copied graph
    PiGraph copiedCluster = (PiGraph) this.copiedGraph.lookupVertex(cluster.getName());

    // First clustering pass: schedule actors inside the cluster
    Schedule childSchedule = clusterizeFromPiGraph(copiedCluster);

    // Build the schedule of the given cluster
    Schedule clusterSchedule = buildClusterSchedule(cluster, copiedCluster, childSchedule);

    // Second clustering pass: regroup actor in input graph in respect to the cluster schedule with various
    // optimization
    clusterSchedule = scheduleOptimization(cluster, clusterSchedule,
        true /* it specifies that the top level cluster will not be clustered again */);

    return clusterSchedule;
  }

  /**
   * Schedule the input graph.
   * 
   * @return Schedule for input graph.
   */
  public Map<AbstractActor, Schedule> scheduleInputGraph() {

    // Check if the input graph is schedulable
    checkSchedulability(this.inputGraph);

    // First clustering pass
    clusterizeFromPiGraph(this.copiedGraph);

    // Second clustering pass
    List<Schedule> schedules = new LinkedList<>();
    schedules.addAll(this.scheduleMap.values());
    this.scheduleMap.clear();
    for (Schedule schedule : schedules) {
      HierarchicalSchedule processedSchedule = (HierarchicalSchedule) scheduleOptimization(inputGraph, schedule, false);
      this.scheduleMap.put(processedSchedule.getAttachedActor(), processedSchedule);
    }

    return this.scheduleMap;
  }

  /**
   * Cluster the input graph according to a schedule. Performs operations such as schedule flattening, parallelism
   * optimization, data parallelism exhibition and parallelism depth limitation.
   * 
   * @param graph
   *          Graph to cluster.
   * @param schedule
   *          Schedule to follow.
   * @param baseClusterIsAlreadyASubGraph
   *          If true, no sub graph will be built at the top level of the schedule. It is needed when you are scheduling
   *          in a cluster.
   * @return Schedule optimized.
   */
  private final Schedule scheduleOptimization(final PiGraph graph, final Schedule schedule,
      final boolean baseClusterIsAlreadyASubGraph) {

    // Flatten the input schedule
    Schedule preprocessSchedule = new ScheduleFlattener().performTransform(schedule);

    // If user want performance optimization, perform them
    if (this.optimizePerformance && this.parallelism) {
      preprocessSchedule = new ScheduleParallelismOptimizer().performTransform(preprocessSchedule);
    }

    // Regroup following the optimized schedule
    Schedule resultingSchedule = clusterizeFromSchedule(graph, preprocessSchedule, 0, !baseClusterIsAlreadyASubGraph);

    if (this.parallelism) {
      // Exhibit data parallelism
      resultingSchedule = new ScheduleDataParallelismExhibiter().performTransform(resultingSchedule);

      // Limit parallelism at the first layer
      resultingSchedule = new ScheduleParallelismDepthLimiter(1).performTransform(resultingSchedule);
    }

    return resultingSchedule;

  }

}
