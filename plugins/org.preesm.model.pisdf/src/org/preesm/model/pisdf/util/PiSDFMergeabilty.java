package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.topology.PiSDFTopologyHelper;

/**
 * @author dgageot
 *
 *         Handle useful method to get actor predecessors/successors and determine if a couple of actors is mergeable
 *         i.e. clusterizable. Many of theses methods have been converted from SDF to PiSDF, originally developed by
 *         Julien Hascoet.
 *
 */
public class PiSDFMergeabilty {

  /**
   * Used to check if these actors are mergeable i.e. do not introduce cycle if clustered
   *
   * @param a
   *          first actor
   * @param b
   *          second actor
   * @return true if the couple is mergeable
   */
  public static boolean isMergeable(final AbstractActor a, final AbstractActor b) {
    final List<AbstractActor> predA = PiSDFTopologyHelper.getPredecessors(a);
    final List<AbstractActor> predB = PiSDFTopologyHelper.getPredecessors(b);
    final List<AbstractActor> succA = PiSDFTopologyHelper.getSuccessors(a);
    final List<AbstractActor> succB = PiSDFTopologyHelper.getSuccessors(b);
    predA.retainAll(succB);
    predB.retainAll(succA);
    return predA.isEmpty() && predB.isEmpty();
  }

  /**
   * Used to get the list of connected-couple that can be merged. Connected-couple means that actors are connected
   * together through one or more Fifo.
   *
   * @param graph
   *          input graph
   * @return list of mergeable connected-couple
   */
  public static List<Pair<AbstractActor, AbstractActor>> getConnectedCouple(final PiGraph graph) {
    List<Pair<AbstractActor, AbstractActor>> listCouple = new LinkedList<>();
    List<AbstractActor> graphActors = graph.getActors();

    // Get every mergeable connected-couple
    for (AbstractActor a : graphActors) {
      for (DataOutputPort dop : a.getDataOutputPorts()) {
        AbstractActor b = dop.getOutgoingFifo().getTargetPort().getContainingActor();
        // Verify that actor are connected together
        if (PiSDFMergeabilty.isMergeable(a, b) && !(b instanceof InterfaceActor)) {
          Pair<AbstractActor, AbstractActor> couple = new ImmutablePair<>(a, b);
          listCouple.add(couple);
        }
      }
    }

    return listCouple;
  }

}
