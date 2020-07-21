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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.commons.exceptions.PreesmRuntimeException;
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
   * @return Latency as a multiplication factor of a graph iteration duration (should start at 1).
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
    // 3.5 check if new graph is a DAG
    final JohnsonSimpleCycles<AbstractActor, FifoAbstraction> cycleFinder = new JohnsonSimpleCycles<>(absGraph);
    final List<List<AbstractActor>> cycles = cycleFinder.findSimpleCycles();
    if (!cycles.isEmpty()) {
      throw new PreesmRuntimeException("The iteration latency evaluation computed a DAG which is not a DAG, abandon.");
    }
    // 4. get source and sink actors
    final Set<AbstractActor> sinkActors = new LinkedHashSet<>(heurFifoBreaks.additionalSinkActors);
    final Set<AbstractActor> sourceActors = new LinkedHashSet<>(heurFifoBreaks.additionalSourceActors);
    for (final AbstractActor absActor : absGraph.vertexSet()) {
      if (absActor instanceof ExecutableActor) {
        if (absGraph.outDegreeOf(absActor) == 0) {
          sinkActors.add(absActor);
        }
        if (absGraph.inDegreeOf(absActor) == 0) {
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
      topoDelays.put(actor, new ActorDelayInfo(0, 1));
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
