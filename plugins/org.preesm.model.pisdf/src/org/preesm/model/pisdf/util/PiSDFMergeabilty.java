package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;

/**
 * @author dgageot
 * 
 *         Handle useful method to get actor predecessors/successors and determine if a couple of actor is mergeable
 *         i.e. clusterizable. Many of theses methods have been converted from SDF to PiSDF, originally developed by
 *         Julien Hascoet.
 * 
 */
public class PiSDFMergeabilty {
  /**
   * Used to get all actors linked in input (only layer n-1) of a specified PiSDF actor
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
   * Used to get all actors linked in output (only layer n-1) of a specified PiSDF actor
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
   * Used to get all actors linked in input (all the graph) of a specified PiSDF actor
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
   * Used to get all actors linked in output (all the graph) of a specified PiSDF actor
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
}
