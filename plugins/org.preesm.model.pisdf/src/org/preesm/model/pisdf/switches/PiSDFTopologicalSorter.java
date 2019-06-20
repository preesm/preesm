package org.preesm.model.pisdf.switches;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;

/**
 * This class offer a method for sorting a list of actors in the topoligical order.
 *
 * @author anmorvan
 *
 */
public class PiSDFTopologicalSorter {

  private final List<AbstractActor>  visitedOrdered = new ArrayList<>();
  private final Deque<AbstractActor> visiting       = new LinkedList<>();

  /**
   * Sorts the list of actors in topological order. Fails if the graph containing the actors is not a DAG.
   */
  public static final List<AbstractActor> depthFirstSort(final List<AbstractActor> actors) {
    final PiSDFTopologicalSorter piSDFPredecessorSwitch = new PiSDFTopologicalSorter();

    for (AbstractActor a : actors) {
      if (!(piSDFPredecessorSwitch.visitedOrdered.contains(a))) {
        piSDFPredecessorSwitch.visit(a);
      }
    }
    return piSDFPredecessorSwitch.visitedOrdered;
  }

  /*
   * see https://en.wikipedia.org/wiki/Topological_sorting#Depth-first_search
   */
  private void visit(final AbstractActor actor) {
    if (visitedOrdered.contains(actor)) {
      // skip
    } else {
      if (visiting.contains(actor)) {
        // not a DAG
        throw new PreesmRuntimeException("Graph is not a DAG");
      } else {
        visiting.push(actor);
        actor.getDataOutputPorts().forEach(p -> visit(p.getFifo().getTargetPort().getContainingActor()));
        visiting.pop();
        visitedOrdered.add(0, actor);
      }
    }
  }
}
