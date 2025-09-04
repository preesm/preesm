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
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.Cluster;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
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
  public static List<Cluster> buildArchHierarchyGraph(PiGraph graph, Scenario scenario) {
    /*
     * Start : find a first actor mapped to FPGA (the seed, rpz segmentation), with at least 1 non-FPGA source actor (so
     * the seed has good chances of being the "first" actor) then find and add its FPGA successor actors. An actor is
     * eligible if it has only FPGA predecessors (since I don't know in which order I iterate over actors, I want to
     * make sure I don't start in the middle of the actor's succession) and the same mapping as the seed.
     */
    final List<Cluster> listClusters = new LinkedList<>();

    final EList<AbstractActor> listActors = graph.getActors();
    final Map<AbstractActor,
        Boolean> actorIsVisited = listActors.stream().collect(Collectors.toMap(Function.identity(), v -> false));

    final ComponentInstance refCPU = scenario.getSimulationInfo().getMainOperator();
    final Component refCPUArch = refCPU.getComponent();

    // list all processing elements (i.e component instances) used in the architecture
    final EList<ComponentInstance> componentList = scenario.getDesign().getComponentInstances();

    int i = 0;
    boolean graph_is_fully_searched = false;
    // visit all actors to search those that can act as seeds
    do {
      boolean seed_found = false;
      AbstractActor actor;
      ComponentInstance clusteringComponent = null;

      // try to find a valid, non-visited seed
      do {
        actor = listActors.get(i);
        i++;

        // all the PEs actor is mappable to that are not the same arch as refCPU
        final var nonMainCpuMappings = scenario.getPossibleMappings(actor).stream()
            .filter(c -> !(c.getComponent().equals(refCPUArch))).toList();

        // check actor has not been tested before, and if it is mapped to a non-CPU PE
        if (!actorIsVisited.get(actor) && !nonMainCpuMappings.isEmpty()) {
          actorIsVisited.put(actor, true);

          /*
           * check if it is a valid seed : there is a non-main arch predecessor actor or no inputs at all, and a
           * non-main arch successor actor
           */

          final List<Actor> predecessors = actor.getDirectPredecessors().stream().filter(Actor.class::isInstance)
              .map(a -> (Actor) a).toList();

          // decide which arch will be used to clusterize
          // TODO faire retourner le composant plutôt que l'instance par seedArchHeuristic
          clusteringComponent = seedArchHeuristic(graph, scenario, actor, refCPUArch);

          // check if, among all the predecessors, any of them has a mapping whose arch is the same as the main PE's
          final boolean anyMainArchPredecessor = predecessors.stream().anyMatch(
              a -> scenario.getPossibleMappings(a).stream().anyMatch(CI -> CI.getComponent().equals(refCPUArch)));

          final List<Actor> successors = actor.getDirectSuccessors().stream().filter(Actor.class::isInstance)
              .map(a -> (Actor) a).toList();

          // cannot use refArch in .contains() because FUCK JAVA
          final var clusteringArchClone = clusteringComponent;

          final boolean anyClusteringArchSuccessor = successors.stream()
              .anyMatch(a -> scenario.getPossibleMappings(a).contains(clusteringArchClone));

          seed_found = (anyMainArchPredecessor || predecessors.isEmpty()) && anyClusteringArchSuccessor;
        }

        if (i == listActors.size()) {
          // this is the last actor to visit, last chance for a clustering
          graph_is_fully_searched = true;
        }
      } while (actorIsVisited.get(actor) && !seed_found && !graph_is_fully_searched);

      if (seed_found) {
        actorIsVisited.put(actor, true);

        // once again, fuck java
        final var clusteringComponentClone = clusteringComponent;
        final Component clusteringArch = clusteringComponentClone.getComponent();

        // now we have a seed, let's build a list of all the actors we want to merge
        // they will be all (un)direct successors of the seed with only fpga inputs
        final Set<AbstractActor> visitedActors = new HashSet<>();
        final MergingHeuristic heuristic = new MinimalMergingHeuristic();
        final Set<AbstractActor> actorsToMerge = buildMergeList(actor, scenario, clusteringComponentClone,
            visitedActors, heuristic);

        // mark the merged actors as visited
        for (final AbstractActor a : actorsToMerge) {
          actorIsVisited.put(a, true);
        }

        // Now we can merge
        // TODO change name to a better one...
        final String clusterName = "Cluster_" + actor.getName();
        final String info = "  Clustering actors " + actorsToMerge.stream().map(a -> a.getName()).toList()
            + " into cluster " + clusterName;
        PreesmLogger.getLogger().log(Level.INFO, info);
        final Cluster mergeActor = ActorMerger.mergeActors(graph, actorsToMerge, clusterName);
        final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker();
        pgcc.check(graph);

        // TODO set better URL
        mergeActor.setUrl("");
        listClusters.add(mergeActor);
        scenario.getConstraints().addConstraint(clusteringComponentClone, mergeActor);

        // set all clustered actors' mapping(s) to the same as the cluster's
        // for that we create a new constraint variable and copy the mappings to it, while filtering those we want to
        // remove
        final EMap<ComponentInstance, EList<AbstractActor>> saveConstraints = new BasicEMap<>();
        final var scenarioConstraintsMap = scenario.getConstraints().getGroupConstraints();
        saveConstraints.putAll(scenario.getConstraints().getGroupConstraints());
        scenarioConstraintsMap.clear();

        for (final var contrainte : saveConstraints) {
          final ComponentInstance PE = contrainte.getKey();
          scenarioConstraintsMap.put(PE, new BasicEList<>());

          for (final AbstractActor a : contrainte.getValue()) {
            if (!mergeActor.getActors().contains(a) || (PE == clusteringComponentClone)) {
              // if the actor is not in the cluster its mappings must not be altered
              // otherwise it is added only if the component is the one we mapped the entire cluster to
              scenarioConstraintsMap.get(PE).add(a);
            }
          }
        }
        scenario.getConstraints().addConstraint(clusteringComponent, mergeActor);
        mergeActor.setClusterValue(true);

      }

    } while (!graph_is_fully_searched);

    // now we add the cluster's mapping to the scenario

    return listClusters;

  }

  /***
   * The heuristic that decides which of the PEs available as maping for actor will be used to start the clustering.
   *
   * @param graph
   *          the algorithm graph
   * @param scenario
   *          the scenario
   * @param actor
   *          the actor
   * @return the component chosen
   */
  private static ComponentInstance seedArchHeuristic(PiGraph graph, Scenario scenario, AbstractActor actor,
      Component refArch) {
    // TODO make it smarter (or at least non-trivial)

    if (scenario.getPossibleMappings(actor).stream().anyMatch(c -> !(c.getComponent().equals(refArch)))) {
      // if there is a PE with a different arch than the main CPU, return it (or the first of the list)
      return scenario.getPossibleMappings(actor).stream().filter(c -> !(c.getComponent().equals(refArch))).toList()
          .getFirst();
    }
    return scenario.getPossibleMappings(actor).getFirst();

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
  public static Set<AbstractActor> buildMergeList(AbstractActor seed, Scenario scenario, ComponentInstance refArchi,
      Set<AbstractActor> visitedActors, MergingHeuristic mergeChecker) {
    final Set<AbstractActor> actorsToMerge = new HashSet<>();
    actorsToMerge.add(seed);

    final List<Actor> seedSuccessorsSameArch = seed
        .getDirectSuccessors().stream().filter(Actor.class::isInstance).map(a -> (Actor) a).filter(a -> scenario
            .getPossibleMappings(a).stream().anyMatch(map -> map.getComponent().equals(refArchi.getComponent())))
        .toList();

    for (final AbstractActor actor : seedSuccessorsSameArch) {

      if (!visitedActors.contains(actor)) {
        visitedActors.add(actor);

        // check if the actor can be added to the merger list
        // (i.e it only has predecessors with the same arch as the seed
        final Map<String, Object> params = new HashMap<>();
        params.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
        params.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, refArchi);
        params.put("position", MergingHeuristic.successor);
        final boolean mergeable = mergeChecker.assess(seed, actor, params);

        if (mergeable) {
          // we can add it to the merging list and probe its successors too
          final Set<
              AbstractActor> successorList = buildMergeList(actor, scenario, refArchi, visitedActors, mergeChecker);
          actorsToMerge.addAll(successorList);
        }
      }

    }

    final List<Actor> seedPredecessorsSameArch = seed
        .getDirectPredecessors().stream().filter(Actor.class::isInstance).map(a -> (Actor) a).filter(a -> scenario
            .getPossibleMappings(a).stream().anyMatch(map -> map.getComponent().equals(refArchi.getComponent())))
        .toList();

    for (final AbstractActor actor : seedPredecessorsSameArch) {
      if (!visitedActors.contains(actor)) {
        visitedActors.add(actor);

        final Map<String, Object> params = new HashMap<>();
        params.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
        params.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, refArchi);
        params.put("position", MergingHeuristic.predecessor);
        final boolean mergeable = mergeChecker.assess(seed, actor, params);

        if (mergeable) {
          // probe its predecessors too
          final Set<
              AbstractActor> predecessorList = buildMergeList(actor, scenario, refArchi, visitedActors, mergeChecker);
          actorsToMerge.addAll(predecessorList);
        }
      }

    }

    return actorsToMerge;
  }
}
