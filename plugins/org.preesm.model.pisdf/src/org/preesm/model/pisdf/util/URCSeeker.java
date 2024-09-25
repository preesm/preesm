/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
    this.graph.getActors().stream().filter(ExecutableActor.class::isInstance).forEach(x -> doSwitch(x));
    // Return identified URCs
    return identifiedURCs;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor base) {

    // Check that all fifos are homogeneous and without delay
    final boolean homogeneousRates = base.getDataOutputPorts().stream()
        .allMatch(x -> doSwitch(x.getFifo()).booleanValue());
    // Return false if rates are not homogeneous or that the corresponding actor was a sink (no output)
    if (!homogeneousRates || base.getDataOutputPorts().isEmpty()) {
      return false;
    }

    // Get the candidate i.e. the following actor in the topological order
    final AbstractActor candidate = (AbstractActor) base.getDataOutputPorts().get(0).getFifo().getTarget();

    // Check that the actually processed actor as only fifos outgoing to the candidate actor
    final boolean allOutputGoesToCandidate = base.getDataOutputPorts().stream()
        .allMatch(x -> x.getFifo().getTarget().equals(candidate));
    // Check that the candidate actor as only fifos incoming from the base actor
    final boolean allInputComeFromBase = candidate.getDataInputPorts().stream()
        .allMatch(x -> x.getFifo().getSource().equals(base));

    // If the candidate agree with the conditions, register this URC
    if (allOutputGoesToCandidate && allInputComeFromBase) {

      List<AbstractActor> actorURC = new LinkedList<>();

      // URC found with base actor in it?
      final Optional<
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
      final Optional<List<AbstractActor>> candidateListOpt = this.identifiedURCs.stream()
          .filter(x -> x.contains(candidate)).findFirst();
      if (candidateListOpt.isPresent()) {
        final List<AbstractActor> candidateURC = candidateListOpt.get();
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
    return (fifo.getSourcePort().getExpression().evaluateAsLong() == fifo.getTargetPort().getExpression()
        .evaluateAsLong()) && (fifo.getDelay() == null);
  }

}
