package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class is used to seek chain of actors in a given PiGraph that form a Uniform Repetition Count (URC) without
 * internal state (see the article of Pino et al. "A Hierarchical Multiprocessor Scheduling System For DSP
 * Applications").
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
   * @return List of identified URC chain.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedURCs.clear();
    // Explore all executable actors of the graph
    this.graph.getActors().stream().filter(x -> x instanceof ExecutableActor).forEach(x -> doSwitch(x));
    // Return identified URCs
    return identifiedURCs;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor base) {

    // Check that all fifos are homogeneous and without delay
    boolean homogeneousRates = base.getDataOutputPorts().stream().allMatch(x -> doSwitch(x.getFifo()).booleanValue());
    // Return false if rates are not homogeneous or that the corresponding actor was a sink (no output)
    if (!homogeneousRates || base.getDataOutputPorts().isEmpty()) {
      return false;
    }

    // Get the candidate i.e. the following actor in the topological order
    AbstractActor candidate = (AbstractActor) base.getDataOutputPorts().get(0).getFifo().getTarget();

    // Check that the actually processed actor as only fifos outgoing to the candidate actor
    boolean allOutputGoesToCandidate = base.getDataOutputPorts().stream()
        .allMatch(x -> x.getFifo().getTarget().equals(candidate));
    // Check that the candidate actor as only fifos incoming from the base actor
    boolean allInputComeFromBase = candidate.getDataInputPorts().stream()
        .allMatch(x -> x.getFifo().getSource().equals(base));

    // If the candidate agree with the conditions, register this URC
    if (allOutputGoesToCandidate && allInputComeFromBase) {

      List<AbstractActor> actorURC = new LinkedList<>();

      // URC found with base actor in it?
      Optional<
          List<AbstractActor>> actorListOpt = this.identifiedURCs.stream().filter(x -> x.contains(base)).findFirst();
      if (actorListOpt.isPresent()) {
        actorURC = actorListOpt.get();
      } else {
        // If no URC chain list has been found, create it
        // Create a URC list for the new one
        actorURC.add(base);
        // Add it to identified URC
        this.identifiedURCs.add(actorURC);
      }

      // URC found with candidate actor in it?
      Optional<List<AbstractActor>> candidateListOpt = this.identifiedURCs.stream().filter(x -> x.contains(candidate))
          .findFirst();
      if (candidateListOpt.isPresent()) {
        List<AbstractActor> candidateURC = candidateListOpt.get();
        // Remove the list from identified URCs
        this.identifiedURCs.remove(candidateURC);
        // Add all elements to the list of actor
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
    // Return true if rates are homogeneous and that no delay is involved
    return (fifo.getSourcePort().getExpression().evaluate() == fifo.getTargetPort().getExpression().evaluate())
        && (fifo.getDelay() == null);
  }

}
