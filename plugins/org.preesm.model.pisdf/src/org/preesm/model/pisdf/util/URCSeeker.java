package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
    this.graph.getActors().forEach(x -> doSwitch(x));
    // Return identified URCs
    return identifiedURCs;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor actor) {

    // Check that all fifos are homogeneous
    boolean homogeneousRates = actor.getDataOutputPorts().stream().map(DataOutputPort::getFifo)
        .allMatch(x -> doSwitch(x).booleanValue());
    // Return false if rates are not homogeneous
    if (!homogeneousRates) {
      return false;
    }

    // Get the candidate
    AbstractActor candidate = (AbstractActor) actor.getDataOutputPorts().get(0).getFifo().getTarget();

    // Check that the actually processed actor as only fifos outgoing to the candidate actor
    boolean allOutputGoesToCandidate = actor.getDataOutputPorts().stream()
        .allMatch(x -> x.getFifo().getTarget().equals(candidate));
    // Check that the candidate actor as only fifos incoming from the actually processed actor
    boolean allInputComeFromActor = candidate.getDataInputPorts().stream()
        .allMatch(x -> x.getFifo().getSource().equals(actor));

    // If the candidate agree with the conditions, register this URC
    if (allOutputGoesToCandidate && allInputComeFromActor) {

      List<AbstractActor> actorURC = new LinkedList<>();

      // Base URC found for actor?
      Optional<
          List<AbstractActor>> actorListOpt = this.identifiedURCs.stream().filter(x -> x.contains(actor)).findFirst();
      if (actorListOpt.isPresent()) {
        actorURC = actorListOpt.get();
      } else {
        // If no base has been found, create it
        // Create a URC list for the new one
        actorURC.add(actor);
        // Add it to identified URC
        this.identifiedURCs.add(actorURC);
      }

      Optional<List<AbstractActor>> candidateListOpt = this.identifiedURCs.stream().filter(x -> x.contains(candidate))
          .findFirst();
      if (candidateListOpt.isPresent()) {
        List<AbstractActor> candidateURC = candidateListOpt.get();
        // Remove the list from identified URC
        this.identifiedURCs.remove(candidateURC);
        // Add all elements to URC chain of actor
        actorURC.addAll(candidateURC);
      } else {
        // Add the candidate in the URC chain of actor
        actorURC.add(candidate);
      }

    }

    return true;
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    // Returns true if rates are homogeneous
    return fifo.getSourcePort().getExpression().evaluate() == fifo.getTargetPort().getExpression().evaluate();
  }

}
