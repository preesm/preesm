package org.preesm.algorithm.pisdf.checker;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.checker.AbstractGraph.FifoAbstraction;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;

/**
 * This class evaluates the application latency in number of graph iteration.
 * 
 * @author ahonorat
 *
 */
public class IterationDelayedEvaluator {

  /**
   * Performs a graph flattening and graph traversal in order to estimate the number of iterations needed to transform
   * all data produced during the first iteration, i.e. the latency.
   * 
   * @param graph
   *          to be analyzed.
   * @return Latency as a multiplication factor of a graph iteration duration.
   */
  public static int computeLatency(PiGraph graph) {

    // 0. get flatten graph of PiGraph
    final PiGraph flatGraph = PiSDFFlattener.flatten(graph, true);
    // 1. compute brv and create AbsGraph
    Map<AbstractVertex, Long> brv = PiBRV.compute(flatGraph, BRVMethod.LCM);
    // 2. compute breaking fifos
    HeuristicLoopBreakingDelays heurFifoBreaks = new HeuristicLoopBreakingDelays();
    heurFifoBreaks.performAnalysis(flatGraph, brv);
    // 3. get absGraph and remove cycles
    DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph = heurFifoBreaks.absGraph;
    absGraph.removeAllEdges(heurFifoBreaks.breakingFifosAbs);
    // TODO check that the new graph is a DAG
    // 4. get source and sink actors
    final Set<AbstractActor> sinkActors = new LinkedHashSet<>(heurFifoBreaks.additionalSinkActors);
    final Set<AbstractActor> sourceActors = new LinkedHashSet<>(heurFifoBreaks.additionalSourceActors);
    for (final AbstractActor absActor : flatGraph.getActors()) {
      if (absActor instanceof ExecutableActor) {
        if (absActor.getDataOutputPorts().isEmpty()) {
          sinkActors.add(absActor);
        }
        if (absActor.getDataInputPorts().isEmpty()) {
          sourceActors.add(absActor);
        }
      }
    }
    // 5 max+ algebra of the AbsGraph, as done for TopoRanking of HeuristicPeriodicActorSelection
    Map<AbstractActor, ActorDelayInfo> actorDelayInfos = topologicalActorDelayEstimation(sourceActors, absGraph);
    int maxIteration = 0;
    for (AbstractActor ska : sinkActors) {
      maxIteration = Math.max(maxIteration, actorDelayInfos.get(ska).delay);
    }

    return maxIteration;
  }

  /**
   * This class helps to count the delays.
   *
   * @author ahonorat
   */
  private static class ActorDelayInfo {
    int nbMaxVisit;
    int nbVisit = 0;
    int delay;

    ActorDelayInfo(final int nbMaxVisit, final int delay) {
      this.nbMaxVisit = nbMaxVisit;
      this.delay = delay;
    }

  }

  private static Map<AbstractActor, ActorDelayInfo> topologicalActorDelayEstimation(
      final Set<AbstractActor> sourceActors, DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph) {
    final Map<AbstractActor, ActorDelayInfo> topoDelays = new LinkedHashMap<>();
    for (final AbstractActor actor : sourceActors) {
      topoDelays.put(actor, new ActorDelayInfo(0, 0));
    }

    final Deque<AbstractActor> toVisit = new ArrayDeque<>(sourceActors);
    while (!toVisit.isEmpty()) {
      final AbstractActor actor = toVisit.removeFirst();
      final int delay = topoDelays.get(actor).delay;
      for (final FifoAbstraction fa : absGraph.outgoingEdgesOf(actor)) {
        final AbstractActor dest = absGraph.getEdgeTarget(fa);
        if (!topoDelays.containsKey(dest)) {
          final ActorDelayInfo av = new ActorDelayInfo(absGraph.inDegreeOf(dest), delay);
          topoDelays.put(dest, av);
        }
        final ActorDelayInfo av = topoDelays.get(dest);
        av.nbVisit++;
        if (av.nbVisit <= av.nbMaxVisit) {
          int delayEstimation = fa.nbIterationDelayed;
          av.delay = Math.max(av.delay, delay + delayEstimation);
          if (av.nbVisit == av.nbMaxVisit) {
            toVisit.addLast(dest);
          }
        }
      }
    }

    StringBuilder sb = new StringBuilder();
    topoDelays.entrySet().stream().forEach(e -> {
      ActorDelayInfo v = e.getValue();
      sb.append("\n\t" + e.getKey().getName() + ": " + v.delay + " (" + v.nbVisit + " visits on " + v.nbMaxVisit + ")");
    });
    PreesmLogger.getLogger().log(Level.FINE, "Actor iteration delays: " + sb.toString());

    return topoDelays;
  }

}
