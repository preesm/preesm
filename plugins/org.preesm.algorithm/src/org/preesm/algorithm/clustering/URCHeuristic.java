package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.algorithm.clustering.deprecated.EuclideTransfo;
import org.preesm.algorithm.clustering.heuristics.HorizontalClusteringHeuristic;
import org.preesm.commons.graph.Vertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class URCHeuristic extends HorizontalClusteringHeuristic {

  /**
   * basic repetition vector, one value for each vertex of the graph
   */
  Map<AbstractVertex, Long> brv;

  /**
   * List to keep track of actors identified in the cluster
   */
  List<AbstractActor> alreadyIdentifiedActors = new ArrayList<>();

  /**
   * Graph that stores the topological order of firings of the actors in the input PiGraph.
   */
  final Map<Long, List<AbstractActor>> topoOrderASAP = new HashMap<>();

  /**
   * Number of equivalent Processing Elements
   */
  long nPEs;

  /*
   * ------------------------------------------------------------------------------------------------------
   *
   * Override methods
   *
   * ------------------------------------------------------------------------------------------------------
   */

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {

    super.initHeuristicParameters(graph, scenario, arch, taskParameters);

    // Computing the topological graph, in ASAP order
    // Will complete this.topoOrderASAP
    computeTopoASAP(graph);

    // Computing the basic repetition vector of the graph
    this.brv = PiBRV.compute(graph, BRVMethod.LCM);

    // Computing number of equivalent cores
    this.nPEs = EuclideTransfo.computeSingleNodeCoreEquivalent(scenario);
  }

  @Override
  public boolean assessMergeable(AbstractActor seed, AbstractActor actor) {
    /**
     * Several checks are made here :
     */

    /* 1. Check if actor is not a hierarchical actor */
    final Boolean isNotHierarchical = !(actor instanceof PiGraph);

    // Double negation
    if (Boolean.FALSE.equals(isNotHierarchical)) {
      return false;
    }

    /* 2 . Check if actor is a special/passive (for now, only special) actor */
    final Boolean isSpecialActor = actor instanceof SpecialActor;
    if (Boolean.TRUE.equals(isSpecialActor)) {
      return false;
    }

    /* 3. Check if actors have the same Repetition Vector Value */
    final Boolean sameBRVValue = Objects.equals(brv.get(actor), brv.get(seed));
    if (Boolean.FALSE.equals(sameBRVValue)) {
      return false;
    }

    /* 4. Check if adding this candidate would create a cycle */
    final Boolean noCycle = actor.getDataInputPorts().stream()
        .allMatch(x -> this.alreadyIdentifiedActors.contains(x.getFifo().getSource())
            || getRank(x.getFifo().getSource()) < getRank(actor));

    // meaning of if : **there is a cycle** (double negation employed here -> we are in Normandie??)
    if (Boolean.FALSE.equals(noCycle)) {
      return false;
    }

    /* 5. Check if the candidate satisfies the parallel conditions */
    final Boolean parallelCondition = actor.getDataOutputPorts().stream()
        .allMatch(x -> x.getFifo().getTarget().equals(actor) || getRank(x.getFifo().getTarget()) > getRank(actor));
    if (Boolean.FALSE.equals(parallelCondition)) {
      return false;
    }

    /* If all the checks are good, we can add actors in the list and return true */
    this.alreadyIdentifiedActors.add(actor);
    return true;
  }

  @Override
  public boolean assesSeedable(AbstractActor actor) {

    // We verify that the potential future seed is an executable actor,
    // and if so, not a delay, hierarchical, or special actor.
    final boolean isSeedable = actor instanceof ExecutableActor && !(actor instanceof DelayActor)
        && !(actor instanceof PiGraph) && !(actor instanceof SpecialActor);

    if (isSeedable) {
      this.alreadyIdentifiedActors.clear();
      this.alreadyIdentifiedActors.add(actor);
    }

    return isSeedable;
  }

  @Override
  public boolean validateCluster(Set<AbstractActor> cluster) {
    return cluster.size() != 1;
  }

  /*
   * ------------------------------------------------------------------------------------------------------
   *
   * URC heuristic specific methods
   *
   * ------------------------------------------------------------------------------------------------------
   */

  /**
   * Compute the topological ASAP graph. The result will be stored in this.topoOrderASAP. All actors of rank 0 are the
   * ones that are the first to be executed. Then, actors of rank 1 are those that can be executed just after the
   * execution of rank 0 actors, and so on.
   *
   * @param graph
   *          the input PiSDF graph
   */
  private void computeTopoASAP(PiGraph graph) {

    // Input list of all the actors.
    // Use a lambda expression to filter out actors that are not DelayActor instances
    final List<AbstractActor> fullList = graph.getActors().stream().filter(actor -> !(actor instanceof DelayActor))
        .collect(Collectors.toList());

    Long rank = 0L; // Keep track of current rank

    // Feed the 1st rank
    final List<AbstractActor> firstRankList = new ArrayList<>();

    for (final AbstractActor a : graph.getActors()) {

      // If actor has no input port or actor has delay in every input ports
      if (!(a instanceof DelayActor) && (a.getDataInputPorts().isEmpty()
          || a.getDataInputPorts().stream().allMatch(x -> x.getFifo().isDelayPresent()))) {
        firstRankList.add(a);
        fullList.remove(a);
      }
    }

    this.topoOrderASAP.put(rank, firstRankList);

    // Feed other ranks
    // While this list is not empty, we continue to feed this.topoOrderASAP
    while (!fullList.isEmpty()) {

      // To store actors of current ranks
      final List<AbstractActor> list = new ArrayList<>();

      // Find the successors of current rank actors to compute next rank
      for (final AbstractActor a : this.topoOrderASAP.get(rank)) {
        processDirectSuccessors(a, rank, list, fullList);

        // Weird -> processGetter is not using rank, which means that it will put all the getter actors in second rank.
        processGetter(list, fullList);
      }

      if (list.isEmpty()) {
        PreesmLogger.getLogger().log(Level.SEVERE,
            "Issue computing Topological order, it will run in an infinite loop");
      }
      rank++;
      this.topoOrderASAP.put(rank, list);
    }
  }

  /***
   * Get rank of an actor in the topological ASAP Graph (this.topoOrderASAP). For example, if an actor is the first to
   * be fired, its rank will be 0. If it comes just next the first, its rank will be 1, etc.
   *
   * @param actor
   *          actor to seek for
   * @return the rank of the actor
   */
  private Long getRank(AbstractActor actor) {
    for (final Entry<Long, List<AbstractActor>> rank : this.topoOrderASAP.entrySet()) {
      if (rank.getValue().contains(actor)) {
        return rank.getKey();
      }
    }
    return 0L;
  }

  /**
   * Will identify the direct successors actors of actor a. It will store the result in list, and remove it from
   * fullList.
   *
   * @param a
   *          the current actor
   * @param rank
   *          the rank of actor a
   * @param list
   *          the output
   * @param fullList
   *          the input, that will be modified (at the end -> fullList = fullList - list)
   */
  private void processDirectSuccessors(AbstractActor a, Long rank, List<AbstractActor> list,
      List<AbstractActor> fullList) {
    for (final Vertex aa : a.getDirectSuccessors()) {
      final Long rankMatch = rank + 1;

      if (isValidSuccessor(aa, rankMatch) && (!list.contains(aa)) && !(aa instanceof DelayActor) && this.topoOrderASAP
          .entrySet().stream().filter(y -> y.getKey() < rankMatch).noneMatch(y -> y.getValue().contains(aa))) {
        list.add((AbstractActor) aa);
        fullList.remove(aa);
      }
    }
  }

  private boolean isValidSuccessor(Vertex aa, Long rankMatch) {
    return aa.getDirectPredecessors().stream()
        .filter(x -> x instanceof Actor || x instanceof SpecialActor || x instanceof DelayActor)
        .allMatch(x -> isPredecessorInPreviousRanks(x, rankMatch) || x == aa);
  }

  private boolean isPredecessorInPreviousRanks(Vertex x, Long rankMatch) {
    return topoOrderASAP.entrySet().stream().filter(y -> y.getKey() < rankMatch)
        .anyMatch(y -> y.getValue().contains(x));
  }

  /**
   * Retrieve getter actors (inputPort size = 1, no outputPort, and unique predecessor is a DelayActor). Put them in
   * list (output) and remove them from input (fullList)
   *
   * @param list
   *          output
   * @param fullList
   *          input, that will be modifier (at the end -> fullList = fullList - list)
   */
  private void processGetter(List<AbstractActor> list, List<AbstractActor> fullList) {
    final List<AbstractActor> filteredActors = fullList.stream().filter(x -> x.getDataInputPorts().size() == 1
        && x.getDataOutputPorts().isEmpty() && x.getDirectPredecessors().get(0) instanceof DelayActor).toList();

    filteredActors.forEach(actor -> {
      list.add(actor);
      fullList.remove(actor);
    });

  }

  @Override
  public String getPrefix() {
    return "urc";

  }

}
