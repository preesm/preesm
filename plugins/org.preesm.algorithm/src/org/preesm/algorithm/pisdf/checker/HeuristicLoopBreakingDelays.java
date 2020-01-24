/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.pisdf.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.checker.AbstractGraph.FifoAbstraction;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.FifoBreakingCycleDetector;

/**
 * Check and eventually compute the fifo with delays that break loops.
 *
 * @author ahonorat
 */
public class HeuristicLoopBreakingDelays {

  protected final Map<AbstractActor, Integer> actorsNbVisitsTopoRank;
  protected final Map<AbstractActor, Integer> actorsNbVisitsTopoRankT;

  protected final Set<AbstractActor> additionalSourceActors;
  protected final Set<AbstractActor> additionalSinkActors;

  protected final Map<AbstractVertex, Long>            minCycleBrv;
  protected final Set<FifoAbstraction>                 breakingFifosAbs;
  DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph;

  protected HeuristicLoopBreakingDelays() {
    this.actorsNbVisitsTopoRank = new LinkedHashMap<>();
    this.actorsNbVisitsTopoRankT = new LinkedHashMap<>();

    this.additionalSourceActors = new LinkedHashSet<>();
    this.additionalSinkActors = new LinkedHashSet<>();

    this.minCycleBrv = new HashMap<>();
    this.breakingFifosAbs = new HashSet<>();
    this.absGraph = null;
  }

  protected void performAnalysis(final PiGraph graph, final Map<AbstractVertex, Long> brv) {
    actorsNbVisitsTopoRank.clear();
    actorsNbVisitsTopoRankT.clear();
    additionalSourceActors.clear();
    additionalSinkActors.clear();
    breakingFifosAbs.clear();

    minCycleBrv.clear();
    minCycleBrv.putAll(brv);
    // 1. perform flat PiMM to simple JGraphT structure transition.
    absGraph = AbstractGraph.createAbsGraph(graph, brv);
    // 2. look for cycles
    final JohnsonSimpleCycles<AbstractActor, FifoAbstraction> cycleFinder = new JohnsonSimpleCycles<>(absGraph);
    final List<List<AbstractActor>> cycles = cycleFinder.findSimpleCycles();

    // 3 categorize actors in cycle and retrieve fifo with delays breaking loop
    // it is a set since the same fifo can break several cycles if they have some paths in common
    for (final List<AbstractActor> cycle : cycles) {
      final FifoAbstraction breakingFifoAbs = retrieveBreakingFifo(absGraph, cycle);
      breakingFifosAbs.add(breakingFifoAbs);
      final AbstractActor src = absGraph.getEdgeSource(breakingFifoAbs);
      final AbstractActor tgt = absGraph.getEdgeTarget(breakingFifoAbs);
      long gcdCycle = MathFunctionsHelper.gcd(cycle.stream().map(a -> brv.get(a)).collect(Collectors.toList()));
      cycle.forEach(a -> {
        long localBrv = brv.get(a) / gcdCycle;
        long cycleBrv = minCycleBrv.get(a);
        minCycleBrv.put(a, Math.min(localBrv, cycleBrv));
      });

      final StringBuilder sb = new StringBuilder(src.getName() + " => " + tgt.getName() + " breaks cycle: ");
      cycle.stream().forEach(a -> sb.append(" -> " + a.getName()));
      PreesmLogger.getLogger().log(Level.INFO, sb.toString());
    }

    // 4 Compute actors nbVistis for topoRanking
    for (final AbstractActor aa : graph.getActors()) {
      if (aa instanceof ExecutableActor) {
        this.actorsNbVisitsTopoRank.put(aa, aa.getDataInputPorts().size());
        this.actorsNbVisitsTopoRankT.put(aa, aa.getDataOutputPorts().size());
      }
    }
    for (final FifoAbstraction breakingFifoAbs : breakingFifosAbs) {
      final AbstractActor src = absGraph.getEdgeSource(breakingFifoAbs);
      final AbstractActor tgt = absGraph.getEdgeTarget(breakingFifoAbs);
      final int newNbVisitsT = this.actorsNbVisitsTopoRankT.get(src) - breakingFifoAbs.nbNonZeroDelays;
      final int newNbVisits = this.actorsNbVisitsTopoRank.get(tgt) - breakingFifoAbs.nbNonZeroDelays;
      if ((newNbVisits < 0) || (newNbVisitsT < 0)) {
        throw new PreesmRuntimeException("A loop breaking fifo gave wrong I/O ports number between <" + src.getName()
            + "> and <" + tgt.getName() + ">, leaving.");
      }
      if (newNbVisits == 0) {
        this.additionalSourceActors.add(tgt);
      }
      if (newNbVisitsT == 0) {
        this.additionalSinkActors.add(src);
      }
      this.actorsNbVisitsTopoRankT.put(src, newNbVisitsT);
      this.actorsNbVisitsTopoRank.put(tgt, newNbVisits);
    }

  }

  protected FifoAbstraction retrieveBreakingFifo(final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph,
      final List<AbstractActor> cycle) {
    if (cycle.isEmpty()) {
      throw new PreesmRuntimeException("Bad argument: empty cycle.");
    }
    final AbstractActor root = cycle.get(0);
    // specific case of self loop
    if (cycle.size() == 1) {
      return absGraph.getEdge(root, root);
    }

    // if only one fifo has fully delays, it is the one
    Iterator<AbstractActor> it = cycle.iterator();
    AbstractActor current = it.next();
    int i = 0;
    FifoAbstraction fifo = null;
    FifoAbstraction temp = null;
    while (it.hasNext()) {
      final AbstractActor next = it.next();
      temp = absGraph.getEdge(current, next);
      if (temp.fullyDelayed) {
        fifo = temp;
        i++;
      }
      current = next;
    }
    temp = absGraph.getEdge(current, root);
    if (temp.fullyDelayed) {
      fifo = temp;
      i++;
    }
    if (i == 1) {
      return fifo;
    }

    // otherwise we select the fifo ourself if possible
    final List<AbstractActor> actorsWithEntries = new ArrayList<>();
    final List<AbstractActor> actorsWithExits = new ArrayList<>();
    it = cycle.iterator();
    current = it.next();
    while (it.hasNext()) {
      final AbstractActor next = it.next();
      temp = absGraph.getEdge(current, next);
      final int nbCommonPorts = temp.delays.size();
      if (current.getDataOutputPorts().size() > nbCommonPorts) {
        actorsWithExits.add(current);
      }
      if (next.getDataInputPorts().size() > nbCommonPorts) {
        actorsWithEntries.add(next);
      }
      current = next;
    }
    temp = absGraph.getEdge(current, root);
    final int nbCommonPorts = temp.delays.size();
    if (current.getDataOutputPorts().size() > nbCommonPorts) {
      actorsWithExits.add(current);
    }
    if (root.getDataInputPorts().size() > nbCommonPorts) {
      actorsWithEntries.add(0, root);
    }

    int index = FifoBreakingCycleDetector.retrieveBreakingFifoWhenDifficult(cycle, actorsWithEntries, actorsWithExits);

    if (index < 0) {
      index = 0;
    }

    return absGraph.getEdge(cycle.get(index), cycle.get((index + 1) % cycle.size()));
  }

}
