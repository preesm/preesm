package org.preesm.algorithm.clustering.clusteringheuristics;

import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 *
 * @author jamorin
 *
 */

public class MinimalArchClusteringHeuristic extends HorizontalClusteringHeuristic {

  public static final int successor   = 0;
  public static final int predecessor = 1;

  int       position;
  Scenario  scenario;
  Component refArchi;

  /***
   * Successor actors are eligible for merging if all their predecessors have a mapping to the same arch refArchi.
   * Predecessors are eligible if they have a mapping to the same arch refArchi. For now clusters are immediately ruled
   * out.
   */
  public boolean assessMergeable(AbstractActor seed, AbstractActor actor) {
    if (actor instanceof PiGraph) {
      return false;
    }

    if (this.position == successor) {
      // check if the actor can be added to the merger list
      // (i.e it has a mapping to the same arch as the seed)

      final List<Actor> actorPredecessorsSameArchi = actor.getDirectPredecessors().stream()
          .filter(Actor.class::isInstance).map(a -> (Actor) a).filter(a -> this.scenario.getPossibleMappings(a).stream()
              .anyMatch(map -> map.getComponent().equals(this.refArchi)))
          .toList();
      // get those that execute on the same archi as the seed

      return !actorPredecessorsSameArchi.isEmpty();
    }

    // if it's a predecessor just check if it si mapped to the same arch
    return this.scenario.getPossibleMappings(actor).stream().anyMatch(map -> map.getComponent().equals(this.refArchi));
  }

  /**
   * Assesses whether actor can be used as a seed for clustering its surrounding actors.
   *
   */
  public boolean assesSeedable(AbstractActor actor) {

    // all the PEs actor is mappable to that are not the same arch as refCPU
    final var nonMainCpuMappings = this.scenario.getPossibleMappings(actor).stream()
        .filter(c -> !(c.getComponent().equals(this.refArchi))).toList();

    // Any actor that has a mapping to refArch is a valid seed, except if it is already a cluster graph, or if it is
    // a UserSpecialActor (broadcast, roundbuffer, join, fork).
    final boolean validActorType = !(actor instanceof UserSpecialActor) && !actor.isCluster();

    return validActorType && !nonMainCpuMappings.isEmpty();
  }

  public Component pickClusteringComponent(AbstractActor actor) {

    if (this.scenario.getPossibleMappings(actor).stream().anyMatch(c -> !(c.getComponent().equals(this.refArchi)))) {
      // if there is a PE with a different arch than the main CPU, return it (or the first of the list) return
      this.scenario.getPossibleMappings(actor).stream().filter(c -> !(c.getComponent().equals(this.refArchi))).toList()
          .getFirst();
    }
    return this.scenario.getPossibleMappings(actor).getFirst().getComponent();
  }

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {

    this.position = successor;
    this.scenario = scenario;

    final ComponentInstance refCPU = scenario.getSimulationInfo().getMainOperator();
    this.refArchi = refCPU.getComponent();

  }

  @Override
  public String getPrefix() {
    return "cluster";
  }

}
