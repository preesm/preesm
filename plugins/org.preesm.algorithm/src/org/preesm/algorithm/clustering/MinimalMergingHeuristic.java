package org.preesm.algorithm.clustering;

import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 *
 * @author jamorin
 *
 */

public class MinimalMergingHeuristic extends MergingHeuristic {

  /***
   * Successor actors are eligible for merging if all their predecessors have a mapping to the same arch refArchi.
   * Predecessors are eligible if they have a mapping to the same arch refArchi. For now clusters are immediately ruled
   * out.
   */
  public boolean assess(AbstractActor seed, AbstractActor actor, Map<String, Object> params) {
    if (actor instanceof PiGraph) {
      return false;
    }

    final int position = (int) params.get("position");
    final Scenario scenario = (Scenario) params.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final ComponentInstance refArchi = (ComponentInstance) params
        .get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    if (position == MergingHeuristic.successor) {
      // check if the actor can be added to the merger list
      // (i.e it has a mapping to the same arch as the seed)

      final List<Actor> actorPredecessorsSameArchi = actor
          .getDirectPredecessors().stream().filter(Actor.class::isInstance).map(a -> (Actor) a).filter(a -> scenario
              .getPossibleMappings(a).stream().anyMatch(map -> map.getComponent().equals(refArchi.getComponent())))
          .toList();
      // get those that execute on the same archi as the seed

      return !actorPredecessorsSameArchi.isEmpty();
    }

    // if it's a predecessor just check if it si mapped to the same arch
    return scenario.getPossibleMappings(actor).stream()
        .anyMatch(map -> map.getComponent().equals(refArchi.getComponent()));
  }
}
