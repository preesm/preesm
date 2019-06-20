package org.preesm.model.pisdf.switches;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * Default switch that iterates on all Parameters, Dependencies, Actors, Fifos and Delays in the topological order. The
 * strategy is to topologically iterate on parameters first, then on actors. Edges are visited right after their source
 * has been visited. Delays are visited right after their
 *
 * @author anmorvan
 *
 */
public class TopologicalSwitch extends PiMMSwitch<Boolean> {

  private final List<AbstractVertex> sortedNodes;
  private final List<AbstractActor>  allActors;
  private final Stack<AbstractActor> visitingActors = new Stack<>();

  /**
   *
   */
  public TopologicalSwitch(final PiGraph graph) {
    this.allActors = graph.getAllActors();
    this.sortedNodes = new ArrayList<>(allActors.size());
  }

  @Override
  public Boolean casePiGraph(final PiGraph piGraph) {
    piGraph.getActors().forEach(this::doSwitch);
    return true;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor actor) {
    if (sortedNodes.contains(actor)) {
      // skip
    } else {
      if (visitingActors.contains(actor)) {
        throw new PreesmRuntimeException("Given graph is not a DAG");
      } else {
        visitingActors.push(actor);
        actor.getDataOutputPorts().stream().map(p -> p.getFifo().getTargetPort().getContainingActor())
            .forEach(this::doSwitch);
        visitingActors.pop();
        sortedNodes.add(0, actor);
      }
    }
    return true;
  }
}
