/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.pisdf.autodelays;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;

//TODO Use AbstractGraph instead of PiGraph structure and nbVisits map?
/**
 * This class is dedicated to compute a topological ranking of actors in a PiGraph.
 * 
 * @author ahonorat
 */
public class TopologicalRanking {

  /**
   * This class helps to create the topological rank.
   *
   * @author ahonorat
   */
  public static class TopoVisit {
    public final int nbMaxVisit;
    protected int    rank    = 0;
    protected int    nbVisit = 0;

    private TopoVisit(final int nbMaxVisit, final int rank) {
      this.nbMaxVisit = nbMaxVisit;
      this.rank = rank;
    }

    public int getNbVisit() {
      return nbVisit;
    }

    public int getRank() {
      return rank;
    }

  }

  /**
   * Computes ASAP topological ranking of a flat graph.
   * 
   * @param sourceActors
   *          Source actors of the graph.
   * @param actorsNbVisits
   *          Number of considered inputs (thus excluding feedback fifos).
   * @return Rank of each actor.
   */
  public static Map<AbstractActor, TopoVisit> topologicalASAPranking(final Set<AbstractActor> sourceActors,
      final Map<AbstractActor, Integer> actorsNbVisits) {
    final Map<AbstractActor, TopoVisit> topoRanks = new LinkedHashMap<>();
    for (final AbstractActor actor : sourceActors) {
      topoRanks.put(actor, new TopoVisit(0, 1));
    }

    final Deque<AbstractActor> toVisit = new ArrayDeque<>(sourceActors);
    while (!toVisit.isEmpty()) {
      final AbstractActor actor = toVisit.removeFirst();
      final int rank = topoRanks.get(actor).rank;
      for (final DataOutputPort sport : actor.getDataOutputPorts()) {
        final Fifo fifo = sport.getOutgoingFifo();
        final DataInputPort tport = fifo.getTargetPort();
        final AbstractActor dest = tport.getContainingActor();
        if (!topoRanks.containsKey(dest)) {
          final TopoVisit av = new TopoVisit(actorsNbVisits.get(dest), rank);
          topoRanks.put(dest, av);
        }
        final TopoVisit av = topoRanks.get(dest);
        av.nbVisit++;
        if (av.nbVisit <= av.nbMaxVisit) {
          av.rank = Math.max(av.rank, rank + 1);
          if (av.nbVisit == av.nbMaxVisit) {
            toVisit.addLast(dest);
          }
        }
      }
    }

    if (PreesmLogger.getLogger().isLoggable(Level.FINE)) {
      StringBuilder sb = new StringBuilder();
      topoRanks.entrySet().stream().forEach(e -> {
        TopoVisit v = e.getValue();
        sb.append(
            "\n\t" + e.getKey().getName() + ": " + v.rank + " (" + v.nbVisit + " visits on " + v.nbMaxVisit + ")");
      });
      PreesmLogger.getLogger().log(Level.FINE, "Ranks: " + sb.toString());
    }

    return topoRanks;
  }

  /**
   * Computes ASAP topological ranking of a mirrored flat graph.
   * 
   * @param sinkActors
   *          Sinks actors of the graph.
   * @param actorsNbVisits
   *          Number of considered inputs (thus excluding feedback fifos).
   * @return Rank of each actor.
   */
  public static Map<AbstractActor, TopoVisit> topologicalASAPrankingT(final Set<AbstractActor> sinkActors,
      final Map<AbstractActor, Integer> actorsNbVisits) {
    final Map<AbstractActor, TopoVisit> topoRanks = new LinkedHashMap<>();
    for (final AbstractActor actor : sinkActors) {
      topoRanks.put(actor, new TopoVisit(0, 1));
    }

    final Deque<AbstractActor> toVisit = new ArrayDeque<>(sinkActors);
    while (!toVisit.isEmpty()) {
      final AbstractActor actor = toVisit.removeFirst();
      final int rank = topoRanks.get(actor).rank;
      for (final DataInputPort tport : actor.getDataInputPorts()) {
        final Fifo fifo = tport.getIncomingFifo();
        final DataOutputPort sport = fifo.getSourcePort();
        final AbstractActor dest = sport.getContainingActor();
        if (!topoRanks.containsKey(dest)) {
          final TopoVisit av = new TopoVisit(actorsNbVisits.get(dest), rank);
          topoRanks.put(dest, av);
        }
        final TopoVisit av = topoRanks.get(dest);
        av.nbVisit++;
        if (av.nbVisit <= av.nbMaxVisit) {
          av.rank = Math.max(av.rank, rank + 1);
          if (av.nbVisit == av.nbMaxVisit) {
            toVisit.addLast(dest);
          }
        }
      }
    }

    if (PreesmLogger.getLogger().isLoggable(Level.FINE)) {
      StringBuilder sb = new StringBuilder();
      topoRanks.entrySet().stream().forEach(e -> {
        TopoVisit v = e.getValue();
        sb.append(
            "\n\t" + e.getKey().getName() + ": " + v.rank + " (" + v.nbVisit + " visits on " + v.nbMaxVisit + ")");
      });
      PreesmLogger.getLogger().log(Level.FINE, "RanksT: " + sb.toString());
    }

    return topoRanks;
  }

}
