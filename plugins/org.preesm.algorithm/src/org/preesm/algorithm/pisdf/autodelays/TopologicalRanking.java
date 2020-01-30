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

    StringBuilder sb = new StringBuilder();
    topoRanks.entrySet().stream().forEach(e -> {
      TopoVisit v = e.getValue();
      sb.append("\n\t" + e.getKey().getName() + ": " + v.rank + " (" + v.nbVisit + " visits on " + v.nbMaxVisit + ")");
    });
    PreesmLogger.getLogger().log(Level.FINE, "Ranks: " + sb.toString());

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

    StringBuilder sb = new StringBuilder();
    topoRanks.entrySet().stream().forEach(e -> {
      TopoVisit v = e.getValue();
      sb.append("\n\t" + e.getKey().getName() + ": " + v.rank + " (" + v.nbVisit + " visits on " + v.nbMaxVisit + ")");
    });
    PreesmLogger.getLogger().log(Level.FINE, "RanksT: " + sb.toString());

    return topoRanks;
  }

}
