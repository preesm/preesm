package org.preesm.algorithm.clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.algorithm.clustering.clusteringheuristics.ClusteringHeuristic;
import org.preesm.algorithm.clustering.clusteringheuristics.MinimalArchClusteringHeuristic;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.Arch;
import org.preesm.model.pisdf.NonExecutableActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.FPGA;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author jamorin
 *
 */

public class ClusterBuilder {

  /**
   * this function will inspect all actors in the graph and try to identify seed actors from which clustering is
   * possible. Their successors will be probed to see if their mapping and inputs allow them to be included in the
   * cluster. For now there must be some edge cases where two hierarchical actors are built for the same arch that could
   * merge into one, but they should be pretty rare.
   *
   * @param graph
   *          the inspected graph
   * @param scenario
   *          the corresponding scenario
   *
   * @return the list of cluster actors created
   */
  public static List<PiGraph> buildArchHierarchyGraph(PiGraph graph, Scenario scenario, String HeuristicName) {
    /*
     * Start : find a first actor mapped to FPGA (the seed, rpz segmentation), with at least 1 non-FPGA source actor (so
     * the seed has good chances of being the "first" actor) then find and add its FPGA successor actors. An actor is
     * eligible if it has only FPGA predecessors (since I don't know in which order I iterate over actors, I want to
     * make sure I don't start in the middle of the actor's succession) and the same mapping as the seed.
     */

    // ----- PART 1 : figure out whether we're already in a homogeneous cluster -----
    // RC : move this in assessGraph of MinimalMergingHeuristic

    // We don't take into account special actors (fork, join...) as they can be executed anywhere
    final List<AbstractActor> actors = graph.getActors().stream()
        .filter(a -> !(a instanceof UserSpecialActor || a instanceof NonExecutableActor)).toList();

    List<Component> sharedComponents = scenario.getDesign().getComponents();

    final List<PiGraph> listClusters = new LinkedList<>();

    // 1) Find all subgraphs that are homogeneous and remove them from the actors to explore
    for (final PiGraph subGraph : graph.getChildrenGraphs()) {
      final var subClusterList = buildArchHierarchyGraph(subGraph, scenario, HeuristicName);
      listClusters.addAll(subClusterList);
    }

    // 2) compute intersection for all actors
    for (final AbstractActor a : actors) {
      final var mappings = scenario.getPossibleMappings(a).stream().map(ci -> ci.getComponent()).distinct().toList();
      sharedComponents = sharedComponents.stream().filter(mappings::contains).toList();
      if (sharedComponents.isEmpty()) {
        // no intersection exists
        break;
      }
    }

    // need to convert to mutable list in order to remove elements later
    final List<AbstractActor> listActors = new LinkedList<>(graph.getActors());
    final Map<AbstractActor,
        Boolean> actorIsVisited = listActors.stream().collect(Collectors.toMap(Function.identity(), v -> false));

    // 3) mark the current graph as "cluster" if all its actors share a common target PE (cpu, fpga...)
    if (!sharedComponents.isEmpty() && sharedComponents.stream().anyMatch(c -> !(c instanceof CPU))) {
      // All components share a common PE ! It's a cluster already
      graph.setClusterValue(true);

      // TODO : have clusters not be mapped to only one type of PE
      final Component cp = sharedComponents.getFirst();

      switch (sharedComponents.getFirst()) {
        case final FPGA f -> graph.setTargetArch(Arch.FPGA);
        default -> graph.setTargetArch(Arch.CPU);
      }
      listActors.remove(graph);
      final String info = "\t - Detected cluster " + graph.getName();
      PreesmLogger.getLogger().log(Level.INFO, info);
      graph.setUrl("");
      listClusters.add(graph);
      scenario.getDesign().getComponentInstances().stream().filter(ci -> ci.getComponent().equals(cp))
          .forEach(pe -> scenario.getConstraints().addConstraint(pe, graph));
      return listClusters;
    }

    // ----- PART 2 : clusterize some of the actors if the graph is not already homogeneous -----

    final ComponentInstance refCPU = scenario.getSimulationInfo().getMainOperator();
    final Component refCPUArch = refCPU.getComponent();

    final ClusteringHeuristic heuristic = getHeuristic(HeuristicName);

    // 3) clusterize actors at this level of hierarchy
    int i = 0;
    boolean graphIsFullySearched = false;

    // visit all actors to search those that can act as seeds
    while (!graphIsFullySearched) {
      boolean seedFound = false;
      AbstractActor actor;
      Component clusteringComponent = null;

      // try to find a valid, non-visited seed
      do {
        actor = listActors.get(i);
        i++;
        if (!actorIsVisited.get(actor)) {
          actorIsVisited.put(actor, true);

          // check if actor has not been tested before, and if it is mapped to a non-CPU PE, and if we even want to
          // clusterize from it
          final Map<String, Object> params = new HashMap<>();
          params.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
          params.put("Component", refCPUArch);

          if (heuristic.assesSeedable(actor, params)) {

            // The actor has at least one non-main PE mapping !Let's decide which arch will be used for clustering
            clusteringComponent = heuristic.pickClusteringComponent(actor, params);

            // now we can mark the actor for clustering
            seedFound = true;

          }

          if (i == listActors.size()) {
            // this is the last actor to visit, last chance for a clustering
            graphIsFullySearched = true;
          }
        }
      } while (actorIsVisited.get(actor) && !seedFound && !graphIsFullySearched);

      if (seedFound) {
        actorIsVisited.put(actor, true);

        final Component clusteringArch = clusteringComponent;
        final var clusteringComponents = scenario.getDesign().getComponentInstances().stream()
            .filter(ci -> ci.getComponent() == clusteringArch).toList();

        // now we have a seed, let's build a list of all the actors we want to merge
        // they will be all (un)direct successors of the seed with only fpga inputs
        final Set<AbstractActor> visitedActors = new HashSet<>();
        final Set<AbstractActor> actorsToMerge = buildMergeList(actor, scenario, clusteringComponent, visitedActors,
            heuristic);

        // mark the merged actors as visited
        for (final AbstractActor a : actorsToMerge) {
          actorIsVisited.put(a, true);
        }

        // Now we can merge
        // TODO change name to a better one...
        final String clusterName = "Cluster_" + actor.getName();
        final String info = "\t - Clustering actors " + actorsToMerge.stream().map(a -> a.getName()).toList()
            + " into cluster " + clusterName + " on component(s) " + clusteringComponents;
        PreesmLogger.getLogger().log(Level.INFO, info);

        final PiGraph clusterActor = ActorMerger.mergeActors(graph, actorsToMerge, clusterName);
        final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker();
        pgcc.check(graph);

        // TODO set better URL
        clusterActor.setUrl("");
        listClusters.add(clusterActor);

        final Component chosenComponent = clusteringComponent; // java needs this to be final...
        // final List<ComponentInstance> clusteringArchInstances =
        scenario.getDesign().getComponentInstances().stream().filter(ci -> ci.getComponent().equals(chosenComponent))
            .forEach(ci -> scenario.getConstraints().addConstraint(ci, clusterActor));

        clusterActor.setClusterValue(true);

        switch (clusteringComponent) {
          case final CPU cpu -> clusterActor.setTargetArch(Arch.CPU);
          case final FPGA fpga -> clusterActor.setTargetArch(Arch.FPGA);
          default -> {
            PreesmLogger.getLogger().log(Level.SEVERE, () -> "Architecture " + chosenComponent.getVlnv().toString()
                + " is not documented in PiSDF.xcore's architecture enum, please add it");
          }
        }
      }
    }

    return listClusters;

  }

  /**
   * Returns the merging heuristic corresponding to heuristicName. Expand at will !
   *
   * @param heuristicName
   *          the name
   *
   * @return a MergingHeuristic implementation class
   */
  private static ClusteringHeuristic getHeuristic(String heuristicName) {
    return switch (heuristicName) {
      case "minimal" -> new MinimalArchClusteringHeuristic();
      default -> new MinimalArchClusteringHeuristic();
    };
  }

  /**
   * This function will build a list of actors that can be merged with the seed actor.
   *
   * @param seed
   *          an actor we know will be added to the merging list and whose successors are to be evaluated for merging
   *          Actors are mergeable if all their successors and themself are the same arch as the seed (pas au point)
   * @param scenario
   *          the scenario
   * @param refArchi
   *          the arch whose mapped actors we want to merge
   * @param visitedActors
   *          the set of already visited actors, used to prevent infinite loops
   * @return a cluster of actors that can be merge
   */
  public static Set<AbstractActor> buildMergeList(AbstractActor seed, Scenario scenario, Component refArchi,
      Set<AbstractActor> visitedActors, ClusteringHeuristic heuristic) {
    final Set<AbstractActor> actorsToMerge = new HashSet<>();
    actorsToMerge.add(seed);

    final List<Actor> seedSuccessorsSameArch = seed.getDirectSuccessors().stream().filter(Actor.class::isInstance)
        .map(a -> (Actor) a)
        .filter(a -> scenario.getPossibleMappings(a).stream().anyMatch(map -> map.getComponent().equals(refArchi)))
        .toList();

    for (final AbstractActor actor : seedSuccessorsSameArch) {

      if (!visitedActors.contains(actor)) {
        visitedActors.add(actor);

        // check if the actor can be added to the merger list
        // (i.e it only has predecessors with the same arch as the seed
        final Map<String, Object> params = new HashMap<>();
        params.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
        params.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, refArchi);
        params.put("position", ClusteringHeuristic.successor);
        final boolean mergeable = heuristic.assessMergeable(seed, actor, params);

        if (mergeable) {
          // we can add it to the merging list and probe its successors too
          final Set<AbstractActor> successorList = buildMergeList(actor, scenario, refArchi, visitedActors, heuristic);
          actorsToMerge.addAll(successorList);
        }
      }
    }

    final List<Actor> seedPredecessorsSameArch = seed.getDirectPredecessors().stream().filter(Actor.class::isInstance)
        .map(a -> (Actor) a)
        .filter(a -> scenario.getPossibleMappings(a).stream().anyMatch(map -> map.getComponent().equals(refArchi)))
        .toList();

    for (final AbstractActor actor : seedPredecessorsSameArch) {
      if (!visitedActors.contains(actor)) {
        visitedActors.add(actor);

        final Map<String, Object> params = new HashMap<>();
        params.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
        params.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, refArchi);
        params.put("position", ClusteringHeuristic.predecessor);
        final boolean mergeable = heuristic.assessMergeable(seed, actor, params);

        if (mergeable) {
          // probe its predecessors too
          final Set<
              AbstractActor> predecessorList = buildMergeList(actor, scenario, refArchi, visitedActors, heuristic);
          actorsToMerge.addAll(predecessorList);
        }
      }

    }

    return actorsToMerge;
  }
}
