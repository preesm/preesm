package org.preesm.algorithm.synthesis.memalloc.passiveactors;

import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PassiveActor;

public class PassiveActorHelper {
  /**
   *
   * @param actor
   *          currentActor
   * @return list of neighbors
   */
  public static List<AbstractActor> getNeighbors(AbstractActor actor) {
    return actor.getAllDataPorts().stream().map(p -> p.getOppositePort().getContainingActor()).toList();
  }

  /**
   *
   * @param actor
   *          currentActor
   * @return list of neighbors
   */
  public static List<PassiveActor> getPassiveNeighbors(AbstractActor actor) {
    return PassiveActorHelper.getNeighbors(actor).stream().filter(PassiveActor.class::isInstance)
        .map(a -> (PassiveActor) a).toList();
  }

}
