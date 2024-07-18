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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;

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
  final Map<AbstractVertex, Long> brv;
  final List<AbstractActor>       nonClusterableList;
  final Map<AbstractActor, Long>  topoOrder;

  /**
   * Builds a URCSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   */
  public URCSeeker(final PiGraph inputGraph, List<AbstractActor> nonClusterableList, Map<AbstractVertex, Long> brv) {
    this.graph = inputGraph;
    this.identifiedURCs = new LinkedList<>();
    this.nonClusterableList = nonClusterableList;
    this.brv = brv;
    this.topoOrder = new HashMap<>();
  }

  /**
   * Seek for URC chain in the input graph.
   *
   * @return List of identified URC chain.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedURCs.clear();
    // compute topological order
    computeTopoOrder();
    // Explore all executable actors of the graph
    this.graph.getActors().stream().filter(x -> x instanceof ExecutableActor).forEach(x -> doSwitch(x));
    // Return identified URCs
    return identifiedURCs;
  }

  private void computeTopoOrder() {
    boolean isLast = false;
    final List<AbstractActor> curentRankList = new LinkedList<>();
    final List<AbstractActor> nextRankList = new LinkedList<>();
    // Init
    for (final DataInputInterface i : graph.getDataInputInterfaces()) {
      if (i.getDirectSuccessors().get(0) instanceof Actor || i.getDirectSuccessors().get(0) instanceof SpecialActor) {
        this.topoOrder.put((AbstractActor) i.getDirectSuccessors().get(0), 0L);
        curentRankList.add((AbstractActor) i.getDirectSuccessors().get(0));
      }
    }
    for (final AbstractActor a : graph.getActors()) {
      if (a.getDataInputPorts().isEmpty() && (a instanceof Actor || a instanceof SpecialActor)) {
        this.topoOrder.put(a, 0L);
        curentRankList.add(a);
      }
    }

    // Loop
    Long currentRank = 1L;
    while (!isLast) {
      for (final AbstractActor a : curentRankList) {
        if (!a.getDirectSuccessors().isEmpty()) {
          for (final Vertex aa : a.getDirectSuccessors()) {
            if (!(aa instanceof DataOutputInterface) || !(aa instanceof DelayActor)) {
              boolean flag = false;
              for (final DataInputPort din : ((AbstractActor) aa).getDataInputPorts()) {
                final AbstractActor aaa = (AbstractActor) din.getFifo().getSource();
                // for (Vertex aaa : aa.getDirectPredecessors()) {
                if (!topoOrder.containsKey(aaa)
                    && (aaa instanceof Actor || aaa instanceof SpecialActor || aaa instanceof PiGraph)
                    && !din.getFifo().isHasADelay() || nextRankList.contains(aaa)) {
                  // predecessors
                  // are in the
                  // list
                  flag = true;
                }
              }
              if (!flag && !topoOrder.containsKey(aa)
                  && (aa instanceof Actor || aa instanceof SpecialActor || aa instanceof PiGraph)) {
                topoOrder.put((AbstractActor) aa, currentRank);
                nextRankList.add((AbstractActor) aa);
              }

            }
          }
        }

      }
      if (nextRankList.isEmpty()) {
        isLast = true;
      }
      curentRankList.clear();
      curentRankList.addAll(nextRankList);
      nextRankList.clear();
      currentRank++;
    }
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor base) {
    boolean barrier = false;
    // Check that all fifos are homogeneous and without delay
    final boolean homogeneousRates = base.getDataOutputPorts().stream()
        .allMatch(x -> doSwitch(x.getFifo()).booleanValue());
    // Return false if rates are not homogeneous or that the corresponding actor was a sink (no output)
    if (!homogeneousRates || base.getDataOutputPorts().isEmpty() || nonClusterableList.contains(base)) {
      return false;
    }
    final boolean hiddenDelayOk = base.getDataInputPorts().stream().allMatch(x -> doSwitch(x).booleanValue());
    if (!hiddenDelayOk) {
      return false;
    }

    // Get the candidate i.e. the following actor in the topological order
    for (final DataOutputPort p : base.getDataOutputPorts()) {

      barrier = false;
      // Check that the actually processed actor as only fifos outgoing to the candidate actor
      final AbstractActor candidate = (AbstractActor) p.getFifo().getTarget();

      final boolean homogeneousRatesBis = candidate.getDataInputPorts().stream()
          // Check that the candidate actor as only fifos incoming from the base actor
          .allMatch(x -> doSwitch(x.getFifo()).booleanValue());

      if (!homogeneousRatesBis || nonClusterableList.contains(candidate) || candidate instanceof DelayActor
          || candidate instanceof PiGraph) {
        barrier = true;
      }

      if (!barrier && topoOrder.get(base) != null && topoOrder.get(candidate) != null) {
        // Check that the actually processed actor as only fifos outgoing to the candidate actor
        final boolean allOutputGoesToOrAfterCandidate = base.getDataOutputPorts().stream()
            .allMatch(x -> topoOrder.get(x.getFifo().getTarget()) > topoOrder.get(candidate)
                || x.getFifo().getTarget().equals(candidate));
        // Check that the candidate actor as only fifos incoming from the base actor
        final boolean allInputComeFromOrBeforeBase = candidate.getDataInputPorts().stream().allMatch(
            x -> topoOrder.get(base) < topoOrder.get(x.getFifo().getSource()) || x.getFifo().getSource().equals(base));

        // If the candidate agree with the conditions, register this URC
        if (allOutputGoesToOrAfterCandidate && allInputComeFromOrBeforeBase && !base.getName().contains("urc")
            && !base.getName().contains("loop") && !(base instanceof PiGraph)) {

          List<AbstractActor> actorURC = new LinkedList<>();

          // URC found with base actor in it?
          final Optional<List<AbstractActor>> actorListOpt = this.identifiedURCs.stream().filter(x -> x.contains(base))
              .findFirst();
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
          return true;
        }
      }

    }

    return false;
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    // Return true if rates are homogeneous and that no delay is involved
    return (fifo.getSourcePort().getExpression().evaluate() == fifo.getTargetPort().getExpression().evaluate())
        && (fifo.getDelay() == null);
  }

}
