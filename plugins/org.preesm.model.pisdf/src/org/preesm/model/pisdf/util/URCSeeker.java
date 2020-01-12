package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class is used to seek chain of actors in a given PiGraph that form a Uniform Repetition Count (URC) without
 * internal state structure.
 * 
 * @author dgageot
 *
 */
public class URCSeeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  final PiGraph graph;

  /**
   * List of identified URCs.
   */
  final List<List<AbstractActor>> identifiedURCs;

  /**
   * Builds a URCSeeker based on a input graph.
   * 
   * @param inputGraph
   *          Input graph to search in.
   */
  public URCSeeker(final PiGraph inputGraph) {
    this.graph = inputGraph;
    this.identifiedURCs = new LinkedList<>();
  }

  /**
   * Seek for URC chain in the input graph.
   * 
   * @return List of identified chain that correspond to a URC.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedURCs.clear();
    // Explore all actors of the graph
    for (AbstractActor actor : this.graph.getActors()) {
      doSwitch(actor);
    }
    return identifiedURCs;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor actor) {

    // Check that all fifos are homogeneous
    for (DataOutputPort dop : actor.getDataOutputPorts()) {
      // Rates are homogeneous?
      if (!doSwitch(dop.getFifo()).booleanValue()) {
        return false;
      }
    }

    // Get the candidate
    AbstractActor candidate = (AbstractActor) actor.getDataOutputPorts().get(0).getFifo().getTarget();

    // Check that the actually processed actor as only fifo outgoing to the candidate actor
    boolean allOutputGoesToCandidate = actor.getDataOutputPorts().stream()
        .allMatch(x -> x.getFifo().getTarget().equals(candidate));
    // Check that the candidate actor as only fifo incoming from the actually processed actor
    boolean allInputGoesFromActor = candidate.getDataInputPorts().stream()
        .allMatch(x -> x.getFifo().getSource().equals(actor));

    // If the candidate agree with the conditions, register this URC
    if (allOutputGoesToCandidate && allInputGoesFromActor) {
      // TODO : Register URC in identifiedURCs
    }

    return true;
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    // Returns true if rates are homogeneous
    return fifo.getSourcePort().getExpression().evaluate() == fifo.getTargetPort().getExpression().evaluate();
  }

}
