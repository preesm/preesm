package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;

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
   * Used to get actors connected in input a specified PiSDF actor
   * 
   * @param a
   *          actor
   * @return actors that are directly connected in input of a
   */
  public static final List<AbstractActor> getDirectPredecessorsOf(final AbstractActor a) {
    List<AbstractActor> result = new ArrayList<>();
    a.getDataInputPorts().stream().forEach(x -> result.add(x.getIncomingFifo().getSourcePort().getContainingActor()));
    return result;
  }

  /**
   * Used to get actors connected in output of a specified PiSDF actor
   * 
   * @param a
   *          actor
   * @return actors that are directly connected in output of a
   */
  public static final List<AbstractActor> getDirectSuccessorsOf(final AbstractActor a) {
    List<AbstractActor> result = new ArrayList<>();
    a.getDataOutputPorts().stream().forEach(x -> result.add(x.getOutgoingFifo().getTargetPort().getContainingActor()));
    return result;
  }

  /**
   * Used to get all predecessors of a specified PiSDF actor
   * 
   * @param a
   *          actor
   * @return predecessors of actor a
   */
  public static final List<AbstractActor> getPredecessors(final AbstractActor a) {
    List<AbstractActor> result = new ArrayList<>();
    List<AbstractActor> tmp = getDirectPredecessorsOf(a);
    boolean exit = false;
    do {
      // avoid cycles deadlock
      tmp.removeAll(result);
      result.addAll(tmp);
      final List<AbstractActor> tmp1 = new ArrayList<>();
      tmp1.addAll(tmp);
      if (tmp.isEmpty()) {
        exit = true;
      }
      tmp.clear();
      for (final AbstractActor e : tmp1) {
        tmp.addAll(getDirectPredecessorsOf(e));
      }
    } while (!exit);
    return result;
  }

  /**
   * Used to get all successors of a specified PiSDF actor
   *
   * @param a
   *          actor
   * @return successors of actor a
   */
  public static final List<AbstractActor> getSuccessors(final AbstractActor a) {
    List<AbstractActor> result = new ArrayList<>();
    List<AbstractActor> tmp = getDirectSuccessorsOf(a);
    boolean exit = false;
    do {
      // avoid cycles deadlock
      tmp.removeAll(result);
      result.addAll(tmp);
      final List<AbstractActor> tmp1 = new ArrayList<>();
      tmp1.addAll(tmp);
      if (tmp.isEmpty()) {
        exit = true;
      }
      tmp.clear();
      for (final AbstractActor e : tmp1) {
        tmp.addAll(getDirectSuccessorsOf(e));
      }
    } while (!exit);
    return result;
  }

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
    final List<AbstractActor> predA = getPredecessors(a);
    final List<AbstractActor> predB = getPredecessors(b);
    final List<AbstractActor> succA = getSuccessors(a);
    final List<AbstractActor> succB = getSuccessors(b);
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
