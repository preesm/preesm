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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.jgrapht.alg.cycle.JohnsonSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * Check and eventually compute the fifo with delays that break loops.
 *
 * @author ahonorat
 */
public class HeuristicLoopBreakingDelays {

  protected Map<AbstractActor, Integer> actorsNbVisitsTopoRank  = null;
  protected Map<AbstractActor, Integer> actorsNbVisitsTopoRankT = null;

  protected Set<AbstractActor> additionalSourceActors = null;
  protected Set<AbstractActor> additionalSinkActors   = null;

  protected HeuristicLoopBreakingDelays() {
    this.actorsNbVisitsTopoRank = new LinkedHashMap<>();
    this.actorsNbVisitsTopoRankT = new LinkedHashMap<>();

    this.additionalSourceActors = new LinkedHashSet<>();
    this.additionalSinkActors = new LinkedHashSet<>();
  }

  /**
   * Fifo abstraction to get used in the analysis of this package.
   *
   * @author ahonorat
   */
  protected class FifoAbstraction {
    boolean    fullyDelayed;
    int        nbNonZeroDelays;
    List<Long> delays;

    private FifoAbstraction() {
      this.fullyDelayed = false;
      this.nbNonZeroDelays = 0;
      this.delays = new ArrayList<>();
    }
  }

  protected void performAnalysis(final PiGraph graph) {
    // 1. perform flat PiMM to simple JGraphT structure transition.
    final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph = createAbsGraph(graph);
    // 2. look for cycles
    final JohnsonSimpleCycles<AbstractActor, FifoAbstraction> cycleFinder = new JohnsonSimpleCycles<>(absGraph);
    final List<List<AbstractActor>> cycles = cycleFinder.findSimpleCycles();

    // 3 categorize actors in cycle and retrieve fifo with delays breaking loop
    // it is a set since the same fifo can break several cycles if they have some paths in common
    final Set<FifoAbstraction> breakingFifos = new LinkedHashSet<>();
    for (final List<AbstractActor> cycle : cycles) {
      final FifoAbstraction breakingFifo = retrieveBreakingFifo(graph, absGraph, cycle);
      breakingFifos.add(breakingFifo);
      final AbstractActor src = absGraph.getEdgeSource(breakingFifo);
      final AbstractActor tgt = absGraph.getEdgeTarget(breakingFifo);

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
    for (final FifoAbstraction breakingFifo : breakingFifos) {
      final AbstractActor src = absGraph.getEdgeSource(breakingFifo);
      final AbstractActor tgt = absGraph.getEdgeTarget(breakingFifo);
      final int newNbVisitsT = this.actorsNbVisitsTopoRankT.get(src) - breakingFifo.nbNonZeroDelays;
      final int newNbVisits = this.actorsNbVisitsTopoRank.get(tgt) - breakingFifo.nbNonZeroDelays;
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

  protected FifoAbstraction retrieveBreakingFifo(final PiGraph graph,
      final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph, final List<AbstractActor> cycle) {
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
      if (current.getDataInputPorts().size() > nbCommonPorts) {
        actorsWithEntries.add(current);
      }
      current = next;
    }
    temp = absGraph.getEdge(current, root);
    final int nbCommonPorts = temp.delays.size();
    if (current.getDataOutputPorts().size() > nbCommonPorts) {
      actorsWithExits.add(current);
    }
    if (current.getDataInputPorts().size() > nbCommonPorts) {
      actorsWithEntries.add(current);
    }

    final int index = retrieveBreakingFifoWhenDifficult(cycle, actorsWithEntries, actorsWithExits);

    return absGraph.getEdge(cycle.get(index), cycle.get((index + 1) % cycle.size()));
  }

  /**
   * Indicate whether or not a node in the cycle has extra input/output.
   *
   * @author ahonorat
   */
  protected enum CycleNodeType {
    NONE('n'), ENTRY('i'), EXIT('o'), BOTH('b');

    public final char abbr;

    CycleNodeType(final char abbr) {
      this.abbr = abbr;
    }

  }

  /**
   * This method needs a research paper to be explicated. Do not try to modify it.
   *
   * @param cycle
   *          Cycle to consider.
   * @param actorsWithEntries
   *          Actors in the cycle having also inputs from actors not being the cycle.
   * @param actorsWithExits
   *          Actors in the cycle having also outputs to actors not being the cycle.
   * @return index of the actor after whom the cycle can be broken.
   */
  protected int retrieveBreakingFifoWhenDifficult(final List<AbstractActor> cycle,
      final List<AbstractActor> actorsWithEntries, final List<AbstractActor> actorsWithExits) {
    final List<CycleNodeType> types = new ArrayList<>();
    final Iterator<AbstractActor> itEntries = actorsWithEntries.iterator();
    final Iterator<AbstractActor> itExits = actorsWithExits.iterator();
    AbstractActor nextEntry = itEntries.hasNext() ? itEntries.next() : null;
    AbstractActor nextExit = itExits.hasNext() ? itExits.next() : null;
    int nbBoth = 0;
    final StringBuilder sb = new StringBuilder();
    for (final AbstractActor aa : cycle) {
      if ((aa == nextEntry) && (aa == nextExit)) {
        types.add(CycleNodeType.BOTH);
        nextEntry = itEntries.hasNext() ? itEntries.next() : null;
        nextExit = itExits.hasNext() ? itExits.next() : null;
        nbBoth += 1;
        sb.append(CycleNodeType.BOTH.abbr);
      } else if (aa == nextEntry) {
        types.add(CycleNodeType.ENTRY);
        nextEntry = itEntries.hasNext() ? itEntries.next() : null;
        sb.append(CycleNodeType.ENTRY.abbr);
      } else if (aa == nextExit) {
        types.add(CycleNodeType.EXIT);
        nextExit = itExits.hasNext() ? itExits.next() : null;
        sb.append(CycleNodeType.EXIT.abbr);
      } else {
        types.add(CycleNodeType.NONE);
      }
    }
    // hazardous case with multiple I/O
    if (nbBoth > 1) {
      return types.lastIndexOf(CycleNodeType.BOTH) - 1;
    }
    // test correct cases
    final String str = sb.toString();
    if (str.isEmpty()) {
      return 0;
    }
    // this uses the enum abbr without telling it!
    if (Pattern.matches("i*b?i*", str) || Pattern.matches("o*b?o*", str)) {
      if (nbBoth == 1) {
        final int index = types.indexOf(CycleNodeType.BOTH);
        return index == 0 ? types.size() - 1 : index - 1;
      }
    } else if (Pattern.matches("i*b?o+i*", str)) {
      return types.lastIndexOf(CycleNodeType.EXIT);
    } else if (Pattern.matches("o*i+b?o*", str)) {
      final int index = types.indexOf(CycleNodeType.ENTRY);
      return index == 0 ? types.size() - 1 : index - 1;
    }
    // for all other hazardous cases
    final int index = types.indexOf(CycleNodeType.ENTRY);
    if (index >= 0) {
      return index == 0 ? types.size() - 1 : index - 1;
    }
    return 0;
  }

  protected DefaultDirectedGraph<AbstractActor, FifoAbstraction> createAbsGraph(final PiGraph graph) {
    final DefaultDirectedGraph<AbstractActor,
        FifoAbstraction> absGraph = new DefaultDirectedGraph<>(FifoAbstraction.class);
    for (final AbstractActor a : graph.getActors()) {
      if (a instanceof ExecutableActor) {
        absGraph.addVertex(a);
      }
    }
    for (final Fifo f : graph.getFifos()) {
      final AbstractActor absSrc = f.getSourcePort().getContainingActor();
      final AbstractActor absTgt = f.getTargetPort().getContainingActor();
      if ((absSrc instanceof ExecutableActor) && (absTgt instanceof ExecutableActor)) {
        FifoAbstraction fa = absGraph.getEdge(absSrc, absTgt);
        if (fa == null) {
          fa = new FifoAbstraction();
          final boolean res = absGraph.addEdge(absSrc, absTgt, fa);
          if (!res) {
            throw new PreesmRuntimeException("Problem while creating graph copy.");
          }
        }
        final Delay d = f.getDelay();
        if (d == null) {
          fa.delays.add(0L);
        } else {
          fa.nbNonZeroDelays++;
          fa.delays.add(d.getSizeExpression().evaluate());
        }
        boolean fullyDelayed = true;
        for (final long l : fa.delays) {
          if (l == 0) {
            fullyDelayed = false;
            break;
          }
        }
        fa.fullyDelayed = fullyDelayed;
      }
    }
    return absGraph;
  }

}
