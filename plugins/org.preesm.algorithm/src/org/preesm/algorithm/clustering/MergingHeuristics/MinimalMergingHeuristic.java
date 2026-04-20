package org.preesm.algorithm.clustering.MergingHeuristics;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.MergingHeuristic;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author jamorin
 *
 */

public class MinimalMergingHeuristic extends MergingHeuristic {

  public MinimalMergingHeuristic() {

  }

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Map<String, Object> params) {
    // TODO Auto-generated method stub

  }

  /***
   * Successor actors are eligible for merging if all their predecessors have a mapping to the same arch refArchi.
   * Predecessors are eligible if they have a mapping to the same arch refArchi. For now clusters are immediately ruled
   * out.
   */
  public boolean assessMergeable(AbstractActor seed, AbstractActor actor, Map<String, Object> params) {
    if (actor instanceof PiGraph) {
      return false;
    }

    final int position = (int) params.get("position");
    final Scenario scenario = (Scenario) params.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Component refArchi = (Component) params.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    if (position == MergingHeuristic.successor) {
      // check if the actor can be added to the merger list
      // (i.e it has a mapping to the same arch as the seed)

      final List<Actor> actorPredecessorsSameArchi = actor.getDirectPredecessors().stream()
          .filter(Actor.class::isInstance).map(a -> (Actor) a)
          .filter(a -> scenario.getPossibleMappings(a).stream().anyMatch(map -> map.getComponent().equals(refArchi)))
          .toList();
      // get those that execute on the same archi as the seed

      return !actorPredecessorsSameArchi.isEmpty();
    }

    // if it's a predecessor just check if it si mapped to the same arch
    return scenario.getPossibleMappings(actor).stream().anyMatch(map -> map.getComponent().equals(refArchi));
  }

  /**
   * Assesses whether actor can be used as a seed for clustering its surrounding actors.
   *
   */
  public boolean assesSeedable(AbstractActor actor, Map<String, Object> params) {
    final Scenario scenario = (Scenario) params.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Component refCPUArch = (Component) params.get("Component");

    // all the PEs actor is mappable to that are not the same arch as refCPU
    final var nonMainCpuMappings = scenario.getPossibleMappings(actor).stream()
        .filter(c -> !(c.getComponent().equals(refCPUArch))).toList();

    // Any actor that has a mapping to refArch is a valid seed, except if it is already a cluster graph, or if it is
    // a UserSpecialActor (broadcast, roundbuffer, join, fork).
    final boolean validActorType = !(actor instanceof UserSpecialActor) && !actor.isCluster();

    return validActorType && !nonMainCpuMappings.isEmpty();
  }

  public Component pickClusteringComponent(AbstractActor actor, Map<String, Object> params) {
    final Scenario scenario = (Scenario) params.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Component refCPUArch = (Component) params.get("Component");

    if (scenario.getPossibleMappings(actor).stream().anyMatch(c -> !(c.getComponent().equals(refCPUArch)))) {
      // if there is a PE with a different arch than the main CPU, return it (or the first of the list) return
      scenario.getPossibleMappings(actor).stream().filter(c -> !(c.getComponent().equals(refCPUArch))).toList()
          .getFirst();
    }
    return scenario.getPossibleMappings(actor).getFirst().getComponent();
  }

}
