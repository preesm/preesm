/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.pisdf.autodelays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.algorithm.pisdf.autodelays.AbstractGraph.FifoAbstraction;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.FifoBreakingCycleDetector;

/**
 * Check and eventually compute the fifo with delays that break loops.
 *
 * @author ahonorat
 */
public class HeuristicLoopBreakingDelays {

  public final Map<AbstractActor, Integer> actorsNbVisitsTopoRank;
  public final Map<AbstractActor, Integer> actorsNbVisitsTopoRankT;

  public final Set<AbstractActor> allSourceActors;
  public final Set<AbstractActor> allSinkActors;
  public final Set<AbstractActor> cyclesSourceActors;
  public final Set<AbstractActor> cyclesSinkActors;

  public final Map<AbstractVertex, Long> minCycleBrv;
  public final Set<FifoAbstraction>      breakingFifosAbs;
  public final Set<FifoAbstraction>      selfLoopsAbs;

  /**
   * Information about a cycle: repetition factor, breaking fifo, and all fifos.
   * 
   * @author ahonorat
   */
  public static class CycleInfos {
    public long                        repetition;
    public FifoAbstraction             breakingFifo;
    public final List<FifoAbstraction> fifosPerEdge;

    /**
     * Constructs an empty object, to be filled by calling {@link HeuristicLoopBreakingDelays#performAnalysis}.
     */
    public CycleInfos() {
      fifosPerEdge = new ArrayList<>();
    }

    /**
     * Compute list of Fifos between successive actors of the cycle, in the same order.
     * <p>
     * Be cautious, internal lists are the same reference as stored in the abstract graph, modifications are not
     * recommended.
     * 
     * @return List of all fifos in the cycle, regrouped per edge.
     */
    public List<List<Fifo>> buildAllFifosPerEdge() {
      final List<List<Fifo>> allFifosPerEdge = new ArrayList<>();
      for (FifoAbstraction fa : fifosPerEdge) {
        allFifosPerEdge.add(fa.fifos);
      }
      return allFifosPerEdge;
    }
  }

  public final Map<List<AbstractActor>, CycleInfos>    cyclesInfos;
  DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph;

  /**
   * This class computes and stores fifo able to break cycles.
   */
  public HeuristicLoopBreakingDelays() {
    this.actorsNbVisitsTopoRank = new LinkedHashMap<>();
    this.actorsNbVisitsTopoRankT = new LinkedHashMap<>();

    this.allSourceActors = new LinkedHashSet<>();
    this.allSinkActors = new LinkedHashSet<>();
    this.cyclesSourceActors = new LinkedHashSet<>();
    this.cyclesSinkActors = new LinkedHashSet<>();

    this.minCycleBrv = new HashMap<>();
    this.breakingFifosAbs = new HashSet<>();
    this.selfLoopsAbs = new HashSet<>();

    this.cyclesInfos = new HashMap<>();
    this.absGraph = null;
  }

  /**
   * Clears all stored data, so a new analysis can be made with the same object.
   */
  void clearAllData() {
    actorsNbVisitsTopoRank.clear();
    actorsNbVisitsTopoRankT.clear();

    allSourceActors.clear();
    allSinkActors.clear();
    cyclesSourceActors.clear();
    cyclesSinkActors.clear();

    minCycleBrv.clear();
    breakingFifosAbs.clear();
    selfLoopsAbs.clear();

    cyclesInfos.clear();
  }

  /**
   * This method computes which fifo are able to break cycles. This necessitates a research of all simple cycles, so it
   * can be long. {@link org.preesm.model.pisdf.statictools.PiMMHelper#removeNonExecutedActorsAndFifos} should have
   * previously called if null fifo rates are present.
   * 
   * 
   * @param graph
   *          The PiGraph to analyze (only top level will be analyzed).
   * @param brv
   *          The brv of the PiGraph to analyze.
   */
  public void performAnalysis(final PiGraph graph, final Map<AbstractVertex, Long> brv) {
    // O. init
    clearAllData();
    minCycleBrv.putAll(brv);
    // 1. perform flat PiMM to simple JGraphT structure transition.
    absGraph = AbstractGraph.createAbsGraph(graph, brv);
    // 2. look for cycles
    final JohnsonSimpleCycles<AbstractActor, FifoAbstraction> cycleFinder = new JohnsonSimpleCycles<>(absGraph);
    for (final List<AbstractActor> cycle : cycleFinder.findSimpleCycles()) {
      cyclesInfos.put(cycle, new CycleInfos());
    }

    // 3 categorize actors in cycle and retrieve fifo with delays breaking loop
    // it is a set since the same fifo can break several cycles if they have some paths in common
    for (final Entry<List<AbstractActor>, CycleInfos> entry : cyclesInfos.entrySet()) {
      final List<AbstractActor> cycle = entry.getKey();
      final CycleInfos ci = entry.getValue();
      final FifoAbstraction breakingFifoAbs = retrieveBreakingFifo(absGraph, cycle, ci);
      breakingFifosAbs.add(breakingFifoAbs);
      final AbstractActor src = absGraph.getEdgeSource(breakingFifoAbs);
      final AbstractActor tgt = absGraph.getEdgeTarget(breakingFifoAbs);
      if (cycle.size() == 1) {
        selfLoopsAbs.add(breakingFifoAbs);
      }
      ci.breakingFifo = breakingFifoAbs;
      long gcdCycle = MathFunctionsHelper.gcd(cycle.stream().map(a -> brv.get(a)).collect(Collectors.toList()));
      ci.repetition = gcdCycle;
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
    computeRanks(graph);

    // 5. set all source and sink actors
    allSourceActors.addAll(cyclesSourceActors);
    allSinkActors.addAll(cyclesSinkActors);
    for (final AbstractActor absActor : graph.getActors()) {
      if (absActor instanceof ExecutableActor) {
        if (absActor.getDataOutputPorts().isEmpty()) {
          allSinkActors.add(absActor);
        }
        if (absActor.getDataInputPorts().isEmpty()) {
          allSourceActors.add(absActor);
        }
      }
    }

  }

  protected void computeRanks(final PiGraph graph) {
    // 1. manage regular actors and interfaces
    for (final AbstractActor aa : graph.getActors()) {
      if (aa instanceof ExecutableActor) {
        this.actorsNbVisitsTopoRank.put(aa, aa.getDataInputPorts().size());
        this.actorsNbVisitsTopoRankT.put(aa, aa.getDataOutputPorts().size());
      }
    }
    // for interfaces at top level, we decrease the nbVisit value
    for (final AbstractActor aa : graph.getActors()) {
      if (aa instanceof DataInputInterface) {
        final Fifo f = ((DataInputInterface) aa).getDataPort().getFifo();
        if (f != null) {
          final AbstractActor tgt = f.getTargetPort().getContainingActor();
          if (tgt instanceof ExecutableActor) {
            final int newNbVisits = this.actorsNbVisitsTopoRank.get(tgt) - 1;
            this.actorsNbVisitsTopoRank.put(tgt, newNbVisits);
            if (newNbVisits == 0) {
              this.allSourceActors.add(tgt);
            }
          }
        }

      } else if (aa instanceof DataOutputInterface) {
        final Fifo f = ((DataOutputInterface) aa).getDataPort().getFifo();
        if (f != null) {
          final AbstractActor src = f.getSourcePort().getContainingActor();
          if (src instanceof ExecutableActor) {
            final int newNbVisitsT = this.actorsNbVisitsTopoRankT.get(src) - 1;
            this.actorsNbVisitsTopoRankT.put(src, newNbVisitsT);
            if (newNbVisitsT == 0) {
              this.allSinkActors.add(src);
            }
          }
        }
      }
    }

    // 2. manage cycles
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
        this.cyclesSourceActors.add(tgt);
      }
      if (newNbVisitsT == 0) {
        this.cyclesSinkActors.add(src);
      }
      this.actorsNbVisitsTopoRankT.put(src, newNbVisitsT);
      this.actorsNbVisitsTopoRank.put(tgt, newNbVisits);
    }

  }

  protected static FifoAbstraction retrieveBreakingFifo(
      final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph, final List<AbstractActor> cycle,
      final CycleInfos ci) {
    if (cycle.isEmpty()) {
      throw new PreesmRuntimeException("Bad argument: empty cycle.");
    }
    final AbstractActor root = cycle.get(0);
    // specific case of self loop
    if (cycle.size() == 1) {
      return absGraph.getEdge(root, root);
    }

    // Find the Fifos between each pair of actor of the cycle
    final List<List<Fifo>> cycleFifosPerEdge = new ArrayList<>();
    // if only one fifo has fully delays, it is the one
    Iterator<AbstractActor> it = cycle.iterator();
    AbstractActor current = it.next();
    int i = 0;
    FifoAbstraction fifo = null;
    FifoAbstraction temp = null;
    while (it.hasNext()) {
      final AbstractActor next = it.next();
      temp = absGraph.getEdge(current, next);
      ci.fifosPerEdge.add(temp);
      cycleFifosPerEdge.add(temp.fifos);
      if (temp.fullyDelayed) {
        fifo = temp;
        i++;
      }
      current = next;
    }
    temp = absGraph.getEdge(current, root);
    ci.fifosPerEdge.add(temp);
    cycleFifosPerEdge.add(temp.fifos);

    if (temp.fullyDelayed) {
      fifo = temp;
      i++;
    }
    if (i == 1) {
      return fifo;
    }

    // otherwise we select the fifo ourself if possible
    int index = FifoBreakingCycleDetector.retrieveBreakingFifoWhenDifficult(cycle, cycleFifosPerEdge);

    if (index < 0) {
      index = 0;
    }

    return absGraph.getEdge(cycle.get(index), cycle.get((index + 1) % cycle.size()));
  }

  /**
   * Computes and returns the set of FiFoAbstraction present in cycles, excluding self-loops.
   * 
   * @return FifoAbstraction contained in cycles, excluding self-lops.
   */
  public Set<FifoAbstraction> getForbiddenFifos() {
    final Set<FifoAbstraction> forbiddenFifos = new HashSet<>();
    for (final CycleInfos ci : cyclesInfos.values()) {
      if (ci.fifosPerEdge.size() < 2) {
        continue;
      }
      forbiddenFifos.addAll(ci.fifosPerEdge);
    }
    return forbiddenFifos;
  }

  /**
   * @return The absGraph used for computation, is computed again at each call of {@link #performAnalysis}
   */
  public DefaultDirectedGraph<AbstractActor, FifoAbstraction> getAbsGraph() {
    return absGraph;
  }

}
