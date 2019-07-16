package org.preesm.model.pisdf.util.topology;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.util.topology.PiSDFPredecessorSwitch.IsPredecessorSwitch;
import org.preesm.model.pisdf.util.topology.PiSDFPredecessorSwitch.PredecessorFoundException;
import org.preesm.model.pisdf.util.topology.PiSDFSuccessorSwitch.IsSuccessorSwitch;
import org.preesm.model.pisdf.util.topology.PiSDFSuccessorSwitch.SuccessorFoundException;

/**
 *
 * @author anmorvan
 *
 */
public class PiSDFTopologyHelper {

  public static final Comparator<AbstractActor> getComparator() {
    return new PiSDFTopologicalComparator();
  }

  public static final List<AbstractActor> sort(final List<AbstractActor> actors) {
    return PiSDFTopologicalSorter.depthFirstTopologicalSort(actors);
  }

  /**
   * returns true if potentialPred is actually a predecessor of target
   */
  public static final boolean isPredecessor(final AbstractActor potentialPred, final AbstractActor target) {
    try {
      new IsPredecessorSwitch(target).doSwitch(potentialPred);
      return false;
    } catch (final PredecessorFoundException e) {
      return true;
    }
  }

  /**
   * returns true if potentialSucc is actually a successor of target
   */
  public static final boolean isSuccessor(final AbstractActor potentialSucc, final AbstractActor target) {
    try {
      new IsSuccessorSwitch(target).doSwitch(potentialSucc);
      return false;
    } catch (final SuccessorFoundException e) {
      return true;
    }
  }

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
}
