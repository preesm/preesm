package org.preesm.model.pisdf.switches;

import java.util.Comparator;
import org.preesm.model.pisdf.AbstractActor;

/**
 *
 * @author anmorvan
 *
 */
public class TopologicalComparator implements Comparator<AbstractActor> {

  @Override
  public int compare(final AbstractActor o1, final AbstractActor o2) {
    if (o1.equals(o2)) {
      return 0;
    } else if (isPredecessor(o1, o2)) {
      return 1;
    } else if (isPredecessor(o2, o1)) {
      return -1;
    } else {
      return 0;
    }
  }

  private boolean isPredecessor(final AbstractActor toFind, final AbstractActor potentialPredecessor) {
    if (toFind.equals(potentialPredecessor)) {
      return true;
    } else {
      return potentialPredecessor.getDataInputPorts().stream()
          .map(p -> p.getFifo().getTargetPort().getContainingActor())
          .anyMatch(a -> isPredecessor(a, potentialPredecessor));
    }
  }
}
