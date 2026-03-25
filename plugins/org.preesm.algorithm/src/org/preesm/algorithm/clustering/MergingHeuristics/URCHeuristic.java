package org.preesm.algorithm.clustering.MergingHeuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.preesm.algorithm.clustering.MergingHeuristic;
import org.preesm.commons.graph.Vertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;

public class URCHeuristic extends MergingHeuristic {

  /*
   * ------------------------------------------------------------------------------------------------------
   *
   * Override methods
   *
   * ------------------------------------------------------------------------------------------------------
   */

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Map<String, Object> params) {

    // Computing topoOrderASAP
    final Map<Long, List<AbstractActor>> topoOrderASAP = new HashMap<>();
    computeTopoASAP(graph, topoOrderASAP);

    // Computing brv
    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);

    // Creating alreadyIdentifiedActors list
    final List<AbstractActor> alreadyIdentifiedActors = new ArrayList<>();

    // TODO : Adding nCore (from scenario)

    // Adding in params list
    params.put("topoOrderASAP", topoOrderASAP);
    params.put("brv", brv);
    params.put("alreadyIdentifiedActors", alreadyIdentifiedActors);

  }

  @Override
  public boolean assessMergeable(AbstractActor seed, AbstractActor actor, Map<String, Object> params) {

    // ----------{ Retrieving parameters }---------- //
    // Getting the list of actors already identified as part of the URC cluster.
    final List<AbstractActor> alreadyIdentifiedActors = (List<AbstractActor>) params.get("alreadyIdentifiedActors");

    // Getting the graph stored in topological ASAP order
    final Map<Long, List<AbstractActor>> topoOrderASAP = (Map<Long, List<AbstractActor>>) params.get("topoOrderASAP");

    // Getting basic repetition vector of all actors
    final Map<AbstractVertex, Long> brv = (Map<AbstractVertex, Long>) params.get("brv");

    // ----------{ Making clustering condition }---------- //
    // Check if adding this candidate would create a cycle
    final Boolean noCycle = actor.getDataInputPorts().stream()
        .allMatch(x -> alreadyIdentifiedActors.contains(x.getFifo().getSource())
            || getRank(x.getFifo().getSource(), topoOrderASAP) < getRank(seed, topoOrderASAP));

    // Check if the candidate satisfies the parallel conditions
    final Boolean para = actor.getDataOutputPorts().stream().allMatch(x -> x.getFifo().getTarget().equals(actor)
        || getRank(x.getFifo().getTarget(), topoOrderASAP) > getRank(actor, topoOrderASAP));

    // If candidate meets all criteria :
    // same BRV as base, no cycles, parallel conditions, and not already part of a URC, SRV, or LOOP cluster
    return Boolean.TRUE.equals(Objects.equals(brv.get(actor), brv.get(seed)) && noCycle && para
        && !(actor instanceof PiGraph) && !actor.getName().startsWith("urc_") && !actor.getName().startsWith("srv_")
        && !actor.getName().startsWith("loop_"));
  }

  @Override
  public boolean assesSeedable(AbstractActor actor, Map<String, Object> params) {

    // ----------{ Retrieving parameters }---------- //
    final Map<AbstractVertex, Long> brv = (Map<AbstractVertex, Long>) params.get("brv");

    // ----------{ Making seed condition }---------- //
    // TODO : complete the condition
    boolean isSeedable = true;
    isSeedable &= actor.getHierarchichalRV(brv) > (int) params.get("nCore");

    if (isSeedable) {
      final List<AbstractActor> alreadyIdentifiedActors = (List<AbstractActor>) params.get("alreadyIdentifiedActors");
      alreadyIdentifiedActors.clear();
      alreadyIdentifiedActors.add(actor);
    }
    return isSeedable;
  }

  @Override
  public Component pickClusteringComponent(AbstractActor seed, Map<String, Object> params) {
    return null;
  }

  /*
   * ------------------------------------------------------------------------------------------------------
   *
   * Heuristic specific methods
   *
   * ------------------------------------------------------------------------------------------------------
   */

  /***
   * Get rank of an actor in the topological ASAP Graph. For example, if an actor is the first to be fired, its rank
   * will be 0. If it comes just next the first, its rank will be 1, etc.
   *
   * @param actor
   *          actor to seek for
   * @param topoOrderASAP
   *          the topological ASAP Graph
   * @return the rank of the actor
   */
  private Long getRank(AbstractActor actor, Map<Long, List<AbstractActor>> topoOrderASAP) {
    for (final Entry<Long, List<AbstractActor>> rank : topoOrderASAP.entrySet()) {
      if (rank.getValue().contains(actor)) {
        return rank.getKey();
      }
    }
    return 0L;
  }

  private void computeTopoASAP(PiGraph graph, Map<Long, List<AbstractActor>> topoOrderASAP) {
    // Use a lambda expression to filter out actors that are not DelayActor instances
    final List<AbstractActor> fullList = graph.getActors().stream().filter(actor -> !(actor instanceof DelayActor))
        .collect(Collectors.toList());
    final List<AbstractActor> rankList = new ArrayList<>();
    Long rank = 0L;

    // feed the 1st rank
    for (final AbstractActor a : graph.getActors()) {

      // if actor has no input port or actor has delay in every input ports
      if (!(a instanceof DelayActor) && (a.getDataInputPorts().isEmpty()
          || a.getDataInputPorts().stream().allMatch(x -> x.getFifo().isDelayPresent()))) {
        rankList.add(a);
        fullList.remove(a);
      }
    }

    topoOrderASAP.put(rank, rankList);

    // feed the rest
    while (!fullList.isEmpty()) {
      final List<AbstractActor> list = new ArrayList<>();

      for (final AbstractActor a : topoOrderASAP.get(rank)) {
        processDirectSuccessors(a, rank, list, fullList, topoOrderASAP);
        processGetter(list, fullList, rank);
      }

      if (list.isEmpty()) {
        PreesmLogger.getLogger().log(Level.SEVERE,
            "Issue computing Topological order, it will run in an infinite loop");
      }
      rank++;
      topoOrderASAP.put(rank, list);
    }
  }

  private void processDirectSuccessors(AbstractActor a, Long rank, List<AbstractActor> list,
      List<AbstractActor> fullList, Map<Long, List<AbstractActor>> topoOrderASAP) {
    for (final Vertex aa : a.getDirectSuccessors()) {
      final Long rankMatch = rank + 1;

      if (isValidSuccessor(aa, rankMatch, topoOrderASAP) && (!list.contains(aa)) && !(aa instanceof DelayActor)
          && topoOrderASAP.entrySet().stream().filter(y -> y.getKey() < rankMatch)
              .noneMatch(y -> y.getValue().contains(aa))) {
        list.add((AbstractActor) aa);
        fullList.remove(aa);
      }
    }
  }

  private boolean isValidSuccessor(Vertex aa, Long rankMatch, Map<Long, List<AbstractActor>> topoOrderASAP) {
    return aa.getDirectPredecessors().stream()
        .filter(x -> x instanceof Actor || x instanceof SpecialActor || x instanceof DelayActor)
        .allMatch(x -> isPredecessorInPreviousRanks(x, rankMatch, topoOrderASAP) || x == aa);
  }

  private boolean isPredecessorInPreviousRanks(Vertex x, Long rankMatch, Map<Long, List<AbstractActor>> topoOrderASAP) {
    return topoOrderASAP.entrySet().stream().filter(y -> y.getKey() < rankMatch)
        .anyMatch(y -> y.getValue().contains(x));
  }

  private void processGetter(List<AbstractActor> list, List<AbstractActor> fullList, Long rank) {
    final List<AbstractActor> filteredActors = fullList.stream().filter(x -> x.getDataInputPorts().size() == 1
        && x.getDataOutputPorts().isEmpty() && x.getDirectPredecessors().get(0) instanceof DelayActor).toList();

    filteredActors.forEach(actor -> {
      list.add(actor);
      fullList.remove(actor);
    });

  }

}
