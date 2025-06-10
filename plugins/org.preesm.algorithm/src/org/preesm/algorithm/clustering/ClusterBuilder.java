package org.preesm.algorithm.clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.ProcessingElement;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author jmorin
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
  public static List<PiGraph> buildArchHierarchyGraph(PiGraph graph, Scenario scenario) {
    /*
     * Start : find a first actor mapped to FPGA (the seed, rpz segmentation), with at least 1 non-FPGA source actor (so
     * the seed has good chances of being the "first" actor) then find and add its FPGA successor actors. An actor is
     * eligible if it has only FPGA predecessors (since I don't know in which order I iterate over actors, I want to
     * make sure I don't start in the middle of the actor's succession) and the same mapping as the seed.
     */
    final List<PiGraph> listClusterActors = new LinkedList<>();

    final EList<AbstractActor> listActors = graph.getActors();
    final Map<AbstractActor,
        Boolean> actorIsVisited = listActors.stream().collect(Collectors.toMap(Function.identity(), v -> false));

    final ComponentInstance refCPU = scenario.getSimulationInfo().getMainOperator();
    final Component refCPUArch = refCPU.getComponent();
    final ComponentInstance refFPGA = scenario.getDesign().getComponentInstances().stream()
        .filter(c -> c.getComponent() instanceof FPGA).findFirst().orElse(null);

    // list all processing elements (i.e component instances) used in the architecture
    final EList<ComponentInstance> componentList = scenario.getDesign().getComponentInstances();

    // find all non-main cpu PEs (the ones whose actors we want to cluster)
    // E.G if the main proc is x86, ARM proc will be listed and conversely, as well as all fpgas.
    final List<ComponentInstance> nonX86List = componentList.stream()
        .filter(c -> !(c.getComponent().equals(refCPUArch)) && c.getComponent() instanceof ProcessingElement).toList();
    final List<ComponentInstance> nonCpuList = componentList.stream()
        .filter(c -> !(c.getComponent() instanceof CPU && c.getComponent() instanceof ProcessingElement)).toList();

    int i = 0;
    boolean graph_is_fully_searched = false;
    // visit all actors to search those that can act as seeds
    do {
      boolean seed_found = false;
      AbstractActor actor;
      ComponentInstance refArch = null;

      // try to find a valid, non-visited seed
      do {
        actor = listActors.get(i);
        i++;

        // all the PEs actor is mappable to that are not CPUs
        final var nonCpuMappings = scenario.getPossibleMappings(actor).stream()
            .filter(c -> !(c.getComponent() instanceof CPU)).toList();

        // check actor has not been tested before, and if it is mapped to a non-CPU PE
        if (!actorIsVisited.get(actor) && !nonCpuMappings.isEmpty()) {

          // check if it is a valid seed :
          // there is a non-fpga predecessor actor or no inputs at all, and an fpga successor actor
          final List<Actor> predecessors = actor.getDirectPredecessors().stream().filter(Actor.class::isInstance)
              .map(a -> (Actor) a).toList();

          // decide which arch will be used to clusterize
          refArch = seedArchHeuristic(graph, scenario, actor);

          final boolean anyCPUPredecessor = predecessors.stream()
              .anyMatch(a -> scenario.getConstraints().getPossibleMappings(a).contains(refCPU));

          final List<Actor> successors = actor.getDirectSuccessors().stream().filter(Actor.class::isInstance)
              .map(a -> (Actor) a).toList();

          // cannot use refArch in .contains() because FUCK JAVA
          final var refArchClone = refArch;

          final boolean anyRefArchSuccessor = successors.stream()
              .anyMatch(a -> scenario.getConstraints().getPossibleMappings(a).contains(refArchClone));

          seed_found = (anyCPUPredecessor || predecessors.isEmpty()) && anyRefArchSuccessor;
        }

        if (i == listActors.size()) {
          // this is the last actor to visit, last chance for a clustering
          graph_is_fully_searched = true;
        }
      } while (actorIsVisited.get(actor) && !seed_found && !graph_is_fully_searched);

      if (seed_found) {
        actorIsVisited.put(actor, true);

        // once again, fuck java
        final var refArchClone = refArch;

        // now we have a seed, let's build a list of all the actors we want to merge
        // they will be all (un)direct successors of the seed with only fpga inputs
        final Set<AbstractActor> visitedActors = new HashSet<>();
        final MergingHeuristic heuristic = new MinimalMergingHeuristic();
        final Set<
            AbstractActor> actorsToMerge = buildMergeList(actor, scenario, refArchClone, visitedActors, heuristic);

        // mark the merged actors as visited
        for (final AbstractActor a : actorsToMerge) {
          actorIsVisited.put(a, true);
        }

        // Now we can merge
        // TODO change name to a better one...
        final String clusterName = "Merged" + actor.getName();
        final PiGraph mergeActor = ActorMerger.mergeActors(graph, actorsToMerge, clusterName);
        // TODO set better URL
        mergeActor.setUrl("");
        listClusterActors.add(mergeActor);
        scenario.getConstraints().addConstraint(refArchClone, mergeActor);
      }

    } while (!graph_is_fully_searched);

    // new we add the cluster's mapping to the scenario

    return listClusterActors;

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
  private static ComponentInstance seedArchHeuristic(PiGraph graph, Scenario scenario, AbstractActor actor) {
    // TODO make it smarter (or at least non-trivial)
    if (scenario.getPossibleMappings(actor).stream().anyMatch(c -> c.getComponent() instanceof FPGA)) {
      return scenario.getPossibleMappings(actor).stream().filter(c -> c.getComponent() instanceof FPGA).toList()
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

    final List<AbstractActor> seedSuccessorsSameArch = seed.getDataOutputPorts().stream()
        .map(dop -> dop.getOppositePort().getContainingActor())
        .filter(a -> scenario.getPossibleMappings(a).contains(refArchi)).toList();

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

    final List<AbstractActor> seedPredecessorsSameArch = seed.getDataInputPorts().stream()
        .map(dip -> dip.getOppositePort().getContainingActor())
        .filter(a -> scenario.getConstraints().getPossibleMappings(a).contains(refArchi)).toList();

    for (final AbstractActor actor : seedPredecessorsSameArch) {
      if (!visitedActors.contains(actor)) {
        visitedActors.add(actor);

        final Map<String, Object> params = new HashMap<>();
        params.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
        params.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, refArchi);
        params.put("position", MergingHeuristic.successor);
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
