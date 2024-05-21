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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
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
public class ClusteringPatternSeekerUrc extends ClusteringPatternSeeker {

  /**
   * List of identified URCs.
   */
  final List<List<AbstractActor>>              identifiedURCs;
  private final Map<AbstractVertex, Long>      brv;
  private final Map<Long, List<AbstractActor>> topoOrderASAP; // id rank/actor

  /**
   * Builds a URCSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   */
  public ClusteringPatternSeekerUrc(final PiGraph inputGraph, Map<AbstractVertex, Long> brv) {
    super(inputGraph);
    this.identifiedURCs = new LinkedList<>();
    this.topoOrderASAP = new HashMap<>();
    this.brv = brv;
  }

  /**
   * Seek for URC chain in the input graph.
   *
   * @return List of identified URC chain.
   */

  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedURCs.clear();
    this.topoOrderASAP.clear();
    // arranges actors in topological order
    computeTopoASAP();
    // Explore all executable actors of the graph
    for (final Entry<Long, List<AbstractActor>> rank : this.topoOrderASAP.entrySet()) {
      rank.getValue().stream()
          .filter(x -> x instanceof ExecutableActor && !(x instanceof DelayActor) && !(x instanceof PiGraph))
          .forEach(x -> process(x, rank.getKey()));
    }
    // Return identified URCs
    return identifiedURCs;
  }

  /**
   * Processes the given base actor at the specified rank to construct URC lists.
   *
   * @param base
   *          The base actor to process.
   * @param rank
   *          The current rank of the actor.
   * @return true if a URC list is constructed, otherwise false.
   */
  private Boolean process(AbstractActor base, Long rank) {
    // filter dummy single source starter
    if (base.getName().equals("single_source") || base.getDataInputPorts().stream()
        .anyMatch(x -> ((AbstractVertex) x.getFifo().getSource()).getName().equals("single_source"))) {
      return false;
    }

    // filter multinode interface
    if (base.getName().startsWith("urc_") || base.getName().startsWith("src_") || base.getName().startsWith("snk_")
        || base.getDataInputPorts().stream()
            .anyMatch(x -> ((AbstractVertex) x.getFifo().getSource()).getName().startsWith("src_"))
        || base.getDataInputPorts().stream().anyMatch(
            x -> ((AbstractVertex) x.getFifo().getSource()).getName().startsWith("snk_"))
        || base instanceof PiGraph) {
      return false;
    }

    // Check that all fifos are without delay
    final boolean hasDelay = base.getDataOutputPorts().stream().allMatch(x -> doSwitch(x.getFifo()).booleanValue());

    // Return false if rates are not homogeneous or that the corresponding actor was a sink (no output)
    if (!hasDelay || base.getDataOutputPorts().isEmpty()) {
      return false;
    }

    // for each base construct an URC list
    final List<AbstractActor> actorURC = new LinkedList<>();
    actorURC.add(base);
    Long currentRank = rank + 1;
    AbstractActor finisher = base;

    // Continue to find and add candidates to the URC list
    while (currentRank < this.topoOrderASAP.size()) {
      final AbstractActor candidate = hasCandidate(currentRank, base, actorURC, finisher);
      if (candidate == null) {
        break;
      }
      finisher = candidate;
      actorURC.add(candidate);
      currentRank++;
    }

    // Check if the URC list contains at least two actors
    if (actorURC.size() < 2) {
      return false;
    }

    // Add the constructed URC list to identified URCs
    this.identifiedURCs.add(actorURC);
    return true;

  }

  /**
   * A candidate for a URC list should be right after the current finisher of the list, do not introduce cycle, do not
   * remove parallelism
   *
   * @param currentRank
   *          The current rank of actors being considered.
   * @param base
   *          The base actor to which we are attempting to find a candidate.
   * @param actorURC
   *          The list of actors already identified as part of the URC.
   * @param finisher
   *          The actor whose output ports are used to check the parallel conditions.
   * @return The candidate actor if found, or null if no suitable candidate is found.
   */
  private AbstractActor hasCandidate(Long currentRank, AbstractActor base, List<AbstractActor> actorURC,
      AbstractActor finisher) {
    // Iterate over all candidates at the current rank
    for (final AbstractActor candidate : this.topoOrderASAP.get(currentRank)) {
      // Check if adding this candidate would create a cycle
      final Boolean noCycle = candidate.getDataInputPorts().stream()
          .allMatch(x -> actorURC.contains(x.getFifo().getSource())
              || getRank((AbstractActor) x.getFifo().getSource()) < getRank(base));
      // Check if the candidate satisfies the parallel conditions
      final Boolean para = finisher.getDataOutputPorts().stream()
          .allMatch(x -> x.getFifo().getTarget().equals(candidate)
              || getRank((AbstractActor) x.getFifo().getTarget()) > getRank(candidate));
      // If candidate meets all criteria (same BRV as base, no cycles, parallel conditions, and not already part of a
      // URC)
      if (Boolean.TRUE.equals(
          Objects.equals(brv.get(candidate), brv.get(base)) && noCycle && para && !(candidate instanceof PiGraph))
          && !candidate.getName().startsWith("urc_") && !candidate.getName().startsWith("srv_") && candidate != base) {
        // Return the candidate
        return candidate;
      }
    }
    // If no suitable candidate is found, return null
    return null;
  }

  /**
   * Retrieves the rank of a given actor from the topologically ordered list of actors.
   *
   * @param actor
   *          The actor whose rank is to be found.
   * @return The rank of the actor, or 0 if the actor is not found in the list.
   */
  private Long getRank(AbstractActor actor) {
    for (final Entry<Long, List<AbstractActor>> rank : this.topoOrderASAP.entrySet()) {
      if (rank.getValue().contains(actor)) {
        return rank.getKey();
      }
    }
    return 0L;
  }

  private void computeTopoASAP() {
    // Use a lambda expression to filter out actors that are not DelayActor instances
    final List<AbstractActor> fullList = graph.getActors().stream().filter(actor -> !(actor instanceof DelayActor))
        .collect(Collectors.toList());
    final List<AbstractActor> rankList = new ArrayList<>();
    Long rank = 0L;

    // feed the 1st rank
    for (final AbstractActor a : graph.getActors()) {
      if (!(a instanceof DelayActor) && (a.getDataInputPorts().isEmpty()
          || a.getDataInputPorts().stream().allMatch(x -> x.getFifo().isHasADelay()))) {
        rankList.add(a);
        fullList.remove(a);
      }
    }
    topoOrderASAP.put(rank, rankList);
    // feed the rest

    while (!fullList.isEmpty()) {
      final List<AbstractActor> list = new ArrayList<>();

      for (final AbstractActor a : topoOrderASAP.get(rank)) {
        processDirectSuccessors(a, rank, list, fullList);
      }

      rank++;
      topoOrderASAP.put(rank, list);
    }
  }

  private void processDirectSuccessors(AbstractActor a, Long rank, List<AbstractActor> list,
      List<AbstractActor> fullList) {
    for (final Vertex aa : a.getDirectSuccessors()) {
      final Long rankMatch = rank + 1;

      if (isValidSuccessor(aa, rankMatch) && (!list.contains(aa))) {
        list.add((AbstractActor) aa);
        fullList.remove(aa);
      }
    }
  }

  private boolean isValidSuccessor(Vertex aa, Long rankMatch) {
    return aa.getDirectPredecessors().stream().filter(x -> x instanceof Actor || x instanceof SpecialActor)
        .allMatch(x -> isPredecessorInPreviousRanks(x, rankMatch));
  }

  private boolean isPredecessorInPreviousRanks(Vertex x, Long rankMatch) {
    return topoOrderASAP.entrySet().stream().filter(y -> y.getKey() < rankMatch)
        .anyMatch(y -> y.getValue().contains(x));
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    // Return true if no delay is involved
    return (fifo.getDelay() == null) && !(fifo.getTarget() instanceof DelayActor);
  }

}
