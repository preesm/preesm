package org.preesm.algorithm.clustering;

import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

public class MinimalMergingHeuristic extends MergingHeuristic {

  /***
   * Successor actors are eligible for merging if all their predecessors are the same arch refArchi. Predecessors are
   * eligible if they are the same arch refArchi. For now clusters are immediately ruled out.
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
      // (i.e it only has predecessors with the same arch as the seed)
      final List<AbstractActor> actorPredecessorsNotSameArchi = actor.getDataInputPorts().stream()
          .map(dip -> dip.getOppositePort().getContainingActor()) // get all predecessor actors
          .filter(a -> !(scenario.getConstraints().getPossibleMappings(a).contains(refArchi))).toList();
      // get those that execute on a different archi as the seed

      return actorPredecessorsNotSameArchi.isEmpty();
    }
    return scenario.getConstraints().getPossibleMappings(actor).contains(refArchi);
  }
}
