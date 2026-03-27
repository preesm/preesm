package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.algorithm.clustering.MergingHeuristics.SRVHeuristic;
import org.preesm.algorithm.clustering.MergingHeuristics.URCHeuristic;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Arch;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.FPGA;

public class GenericClusterBuilder {

  private GenericClusterBuilder() {
    /* This utility class should not be instantiated */
  }

  public static List<PiGraph> buildArchHierarchyGraph(PiGraph graph, Scenario scenario, String HeuristicName) {

    // ----- INIT ----- //
    // The list that will be returned
    final List<PiGraph> listClusters = new LinkedList<>();

    // The heuristic that will be used to cluster the graph
    final MergingHeuristic heuristic = getHeuristic(HeuristicName);

    // Computing the parameters of the heuristic following the current graph
    final Map<String, Object> heuristicParameters = new HashMap<>();
    heuristic.initHeuristicParameters(graph, scenario, heuristicParameters);

    // Actors of the graph
    final List<AbstractActor> actors = graph.getActors();

    // ----- STEP 1 ----- //
    // This step is optional, and its purpose is only to accelerate the clustering.
    // Check if the whole graph can be clustered.
    // If so, we modify the scenario constraints and returns it.
    if (heuristic.assessGraph(graph, heuristicParameters)) {
      graph.setClusterValue(true);
      graph.setUrl("");

      // -- Mapping of new cluster
      // Getting architecture of first actor.
      // For now, it is supposed that it is the same architecture component (CPU, GPU, FPGA...)
      // for every actor of the cluster, & that this component is chosen during assessGraph method.
      // Could be changed if needed.
      final AbstractActor a = actors.get(0); // Getting an actor of the cluster to retrieve its components

      // Getting all the components actor a can be mapped on
      final List<Component> mappings = scenario.getPossibleMappings(a).stream().map(ci -> ci.getComponent()).distinct()
          .toList();

      // Getting the first one. Could be chosen more intelligently ?
      final Component cp = scenario.getDesign().getComponents().stream().filter(mappings::contains).toList().getFirst();

      // Switch on all different architectures
      switch (cp) {
        case final FPGA f -> graph.setTargetArch(Arch.FPGA);
        default -> graph.setTargetArch(Arch.CPU);
      }
      // --

      // Log
      final String info = "\t - Detected cluster " + graph.getName();
      PreesmLogger.getLogger().log(Level.INFO, info);

      // Setting up the constraints in the scenario
      scenario.getDesign().getComponentInstances().stream().filter(ci -> ci.getComponent().equals(cp))
          .forEach(pe -> scenario.getConstraints().addConstraint(pe, graph));

      // Return
      listClusters.add(graph);
      return listClusters;
    }

    // ----- STEP 2 ----- //
    // Exploring the children graphs, in a recursive way
    for (final PiGraph subGraph : graph.getChildrenGraphs()) {
      final var subClusterList = buildArchHierarchyGraph(subGraph, scenario, HeuristicName);
      listClusters.addAll(subClusterList);
    }

    // ----- STEP 3 ----- //
    // Seek for clusters within the graph
    // The first step is to choose an actor to start the cluster from : the "seed".
    // The second step is to merge (un)direct neighbors of the seed, respecting the heuristic.
    // Finally, all identified actors are clustered

    // Actors of the current graph
    final Map<AbstractActor,
        Boolean> actorIsVisited = actors.stream().collect(Collectors.toMap(Function.identity(), v -> false));

    int i = 0;
    boolean graphIsFullySearched = false;

    // -- First while loop : Visit all actors of current graph
    while (!graphIsFullySearched) {

      boolean seedFound = false;
      AbstractActor actor = actors.get(i);
      Component clusteringComponent = null;

      // ----- SUB-STEP 1 : identify a seed
      // -- Second while loop : Try to find a valid non visited seed
      while (Boolean.TRUE.equals(actorIsVisited.get(actor) && !seedFound && !graphIsFullySearched)) {

        if (Boolean.FALSE.equals(actorIsVisited.get(actor))) {

          if (heuristic.assesSeedable(actor, heuristicParameters)) {

            // Depending on the seed, the architecture component might be different
            clusteringComponent = heuristic.pickClusteringComponent(actor, heuristicParameters);

            // If a seed is found, end of the second while loop
            seedFound = true;
          }

          i++;
          actor = actors.get(i);

          // All actor have been explored, end of the while loops
          if (i == actors.size()) {
            graphIsFullySearched = true;
          }
        }
      }
      if (!seedFound) {
        continue;
      }

      // ----- SUB-STEP 2 : identify mergeable actors according to the seed.
      actorIsVisited.put(actor, true);

      // clusteringComponent must be "final" for some operations
      final Component finalClusteringComponent = clusteringComponent;

      // Now we have a seed, let's build a list of all the actors we want to merge.
      // They will all be (un)direct neighbors of the seed.
      final Set<AbstractActor> visitedActors = new HashSet<>();
      final Set<
          AbstractActor> actorsToMerge = buildMergeList(actor, scenario, visitedActors, heuristic, heuristicParameters);

      // Mark the merged actors as visited
      for (final AbstractActor a : actorsToMerge) {
        actorIsVisited.put(a, true);
      }

      // ----- SUB-STEP 3 : Merging of identified actors

      final String clusterName = "Cluster_" + actor.getName();

      // Log
      final String info = "\t - Clustering actors " + actorsToMerge.stream().map(a -> a.getName()).toList()
          + " into cluster " + clusterName;
      PreesmLogger.getLogger().log(Level.INFO, info);

      // Creating the cluster
      final PiGraph clusterActor = ActorMerger.mergeActors(graph, actorsToMerge, clusterName);

      // Checking modified graph (with the new cluster) consistency
      final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker();
      pgcc.check(graph);

      // Setting URL
      clusterActor.setUrl("");
      listClusters.add(clusterActor);

      // Adding scenario constraints
      scenario.getDesign().getComponentInstances().stream()
          .filter(ci -> ci.getComponent().equals(finalClusteringComponent))
          .forEach(ci -> scenario.getConstraints().addConstraint(ci, clusterActor));

      clusterActor.setClusterValue(true);

      // Setting target architecture
      switch (finalClusteringComponent) {
        case final CPU cpu -> clusterActor.setTargetArch(Arch.CPU);
        case final FPGA fpga -> clusterActor.setTargetArch(Arch.FPGA);
        default -> PreesmLogger.getLogger().log(Level.SEVERE,
            () -> "Architecture " + finalClusteringComponent.getVlnv().toString()
                + " is not documented in PiSDF.xcore's architecture enum, please add it");
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
  private static MergingHeuristic getHeuristic(String heuristicName) {
    return switch (heuristicName) {
      case "minimal" -> new MinimalMergingHeuristic();
      case "SRV" -> new SRVHeuristic();
      case "URC" -> new URCHeuristic();
      default -> new MinimalMergingHeuristic();
    };
  }

  /**
   * This function will build a list of actors that can be merged with the seed actor.
   *
   * @param seed
   *          Starting actor of the clustering
   * @param scenario
   *          the scenario
   * @param refArchi
   *          target architecture type of cluster
   * @param visitedActors
   *          the set of already visited actors, used to prevent infinite loops
   * @return a cluster of actors that can be merge
   */
  private static Set<AbstractActor> buildMergeList(AbstractActor seed, Scenario scenario,
      Set<AbstractActor> visitedActors, MergingHeuristic heuristic, final Map<String, Object> heuristicParameters) {

    // The set that will be returned
    final Set<AbstractActor> actorsToMerge = new HashSet<>();

    // adding the seed
    actorsToMerge.add(seed);

    // Getting all seed neighbors
    final List<AbstractActor> seedSucc = seed.getDirectSuccessors().stream().map(a -> (AbstractActor) a).toList();
    final List<AbstractActor> seedPred = seed.getDirectPredecessors().stream().map(a -> (AbstractActor) a).toList();
    final List<AbstractActor> seedNeighbors = new ArrayList<>(seedSucc);
    seedNeighbors.addAll(seedPred);

    // Iterating on direct neighbors
    for (final AbstractActor actor : seedNeighbors) {
      if (visitedActors.contains(actor)) {
        continue;
      }
      visitedActors.add(actor);

      // check if the actor can be added to the merge list
      final boolean mergeable = heuristic.assessMergeable(seed, actor, heuristicParameters);
      if (mergeable) {

        // If actor is mergeable, exploring the neighbors of the current actor as a seed
        final Set<AbstractActor> successorList = buildMergeList(actor, scenario, visitedActors, heuristic,
            heuristicParameters);
        actorsToMerge.addAll(successorList);
      }
    }
    return actorsToMerge;
  }
}
