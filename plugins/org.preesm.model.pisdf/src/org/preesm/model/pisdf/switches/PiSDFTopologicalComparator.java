package org.preesm.model.pisdf.switches;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.preesm.model.pisdf.AbstractActor;

/**
 *
 * @author anmorvan
 *
 */
public class PiSDFTopologicalComparator implements Comparator<AbstractActor> {

  private final Set<AbstractActor> visitedElements = new HashSet<>();

  @Override
  public int compare(final AbstractActor o1, final AbstractActor o2) {
    int res = 0;
    visitedElements.clear();
    if (o1.equals(o2)) {
      res = 0;
    } else {
      final boolean predecessor = PiSDFPredecessorSwitch.isPredecessor(o1, o2);
      visitedElements.clear();
      if (predecessor) {
        res = 1;
      } else {
        final boolean predecessor2 = PiSDFPredecessorSwitch.isPredecessor(o2, o1);
        visitedElements.clear();
        if (predecessor2) {
          res = -1;
        } else {
          res = 0;
        }
      }
    }
    return res;
  }
}
