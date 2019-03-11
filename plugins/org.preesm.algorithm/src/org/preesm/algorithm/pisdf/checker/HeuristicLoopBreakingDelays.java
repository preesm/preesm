package org.preesm.algorithm.pisdf.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    actorsNbVisitsTopoRank = new HashMap<>();
    actorsNbVisitsTopoRankT = new HashMap<>();

    additionalSourceActors = new HashSet<>();
    additionalSinkActors = new HashSet<>();
  }

  /**
   * Fifo abstraction to get used in the analysis of this package.
   * 
   * @author ahonorat
   */
  protected class FifoAbstraction {
    boolean    fullyDelayed;
    List<Long> delays;

    private FifoAbstraction() {
      fullyDelayed = false;
      delays = new ArrayList<>();
    }
  }

  protected void performAnalysis(final PiGraph graph) {
    // 1. perform flat PiMM to simple JGraphT structure transition.
    DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph = createAbsGraph(graph);
    // 2. look for cycles
    JohnsonSimpleCycles<AbstractActor, FifoAbstraction> cycleFinder = new JohnsonSimpleCycles<>(absGraph);
    List<List<AbstractActor>> cycles = cycleFinder.findSimpleCycles();

    // 3 categorize actors in cycle and retrieve fifo with delays breaking loop
    // it is a set since the same fifo can break several cycles if they have some paths in common
    Set<FifoAbstraction> breakingFifos = new HashSet<>();
    for (List<AbstractActor> cycle : cycles) {
      FifoAbstraction breakingFifo = retrieveBreakingFifo(graph, absGraph, cycle);
      breakingFifos.add(breakingFifo);
      AbstractActor src = absGraph.getEdgeSource(breakingFifo);
      AbstractActor tgt = absGraph.getEdgeTarget(breakingFifo);

      StringBuilder sb = new StringBuilder(src.getName() + " => " + tgt.getName() + " breaks cycle: ");
      cycle.stream().forEach(a -> sb.append(" -> " + a.getName()));
      PreesmLogger.getLogger().log(Level.INFO, sb.toString());
    }

    // 4 Compute actors nbVistis for topoRanking
    for (AbstractActor aa : graph.getActors()) {
      if (aa instanceof ExecutableActor) {
        actorsNbVisitsTopoRank.put(aa, aa.getDataInputPorts().size());
        actorsNbVisitsTopoRankT.put(aa, aa.getDataOutputPorts().size());
      }
    }
    for (FifoAbstraction breakingFifo : breakingFifos) {
      AbstractActor src = absGraph.getEdgeSource(breakingFifo);
      AbstractActor tgt = absGraph.getEdgeTarget(breakingFifo);
      int newNbVisitsT = actorsNbVisitsTopoRankT.get(src) - breakingFifo.delays.size();
      int newNbVisits = actorsNbVisitsTopoRank.get(tgt) - breakingFifo.delays.size();
      if (newNbVisits < 0 || newNbVisitsT < 0) {
        throw new PreesmRuntimeException("A loop breaking fifo gave wrong I/O ports number between <" + src.getName()
            + "> and <" + tgt.getName() + ">, leaving.");
      }
      if (newNbVisits == 0) {
        additionalSourceActors.add(tgt);
      }
      if (newNbVisitsT == 0) {
        additionalSinkActors.add(src);
      }
      actorsNbVisitsTopoRankT.put(src, newNbVisitsT);
      actorsNbVisitsTopoRank.put(tgt, newNbVisits);
    }

  }

  protected FifoAbstraction retrieveBreakingFifo(final PiGraph graph,
      final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph, final List<AbstractActor> cycle) {
    if (cycle.isEmpty()) {
      throw new PreesmRuntimeException("Bad argument: empty cycle.");
    }
    AbstractActor root = cycle.get(0);
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
      AbstractActor next = it.next();
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
    List<AbstractActor> actorsWithEntries = new ArrayList<>();
    List<AbstractActor> actorsWithExits = new ArrayList<>();
    it = cycle.iterator();
    current = it.next();
    fifo = null;
    while (it.hasNext()) {
      AbstractActor next = it.next();
      temp = absGraph.getEdge(current, next);
      int nbCommonPorts = temp.delays.size();
      if (current.getDataOutputPorts().size() > nbCommonPorts) {
        actorsWithExits.add(current);
      }
      if (current.getDataInputPorts().size() > nbCommonPorts) {
        actorsWithEntries.add(current);
      }
      current = next;
    }
    temp = absGraph.getEdge(current, root);
    int nbCommonPorts = temp.delays.size();
    if (current.getDataOutputPorts().size() > nbCommonPorts) {
      actorsWithExits.add(current);
    }
    if (current.getDataInputPorts().size() > nbCommonPorts) {
      actorsWithEntries.add(current);
    }

    int index = retrieveBreakingFifoWhenDifficult(cycle, actorsWithEntries, actorsWithExits);

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

    CycleNodeType(char abbr) {
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
  protected int retrieveBreakingFifoWhenDifficult(List<AbstractActor> cycle, List<AbstractActor> actorsWithEntries,
      List<AbstractActor> actorsWithExits) {
    List<CycleNodeType> types = new ArrayList<>();
    Iterator<AbstractActor> itEntries = actorsWithEntries.iterator();
    Iterator<AbstractActor> itExits = actorsWithExits.iterator();
    AbstractActor nextEntry = itEntries.hasNext() ? itEntries.next() : null;
    AbstractActor nextExit = itExits.hasNext() ? itExits.next() : null;
    int nbBoth = 0;
    StringBuilder sb = new StringBuilder();
    for (AbstractActor aa : cycle) {
      if (aa == nextEntry && aa == nextExit) {
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
    String str = sb.toString();
    if (str.isEmpty()) {
      return 0;
    }
    // this uses the enum abbr without telling it!
    if (Pattern.matches("i*b?i*", str) || Pattern.matches("o*b?o*", str)) {
      if (nbBoth == 1) {
        int index = types.indexOf(CycleNodeType.BOTH);
        return index == 0 ? types.size() - 1 : index - 1;
      }
    } else if (Pattern.matches("i*b?o+i*", str)) {
      return types.lastIndexOf(CycleNodeType.EXIT);
    } else if (Pattern.matches("o*i+b?o*", str)) {
      int index = types.indexOf(CycleNodeType.ENTRY);
      return index == 0 ? types.size() - 1 : index - 1;
    }
    // for all other hazardous cases
    int index = types.indexOf(CycleNodeType.ENTRY);
    if (index >= 0) {
      return index == 0 ? types.size() - 1 : index - 1;
    }
    return 0;
  }

  protected DefaultDirectedGraph<AbstractActor, FifoAbstraction> createAbsGraph(final PiGraph graph) {
    DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph = new DefaultDirectedGraph<>(FifoAbstraction.class);
    for (AbstractActor a : graph.getActors()) {
      if (a instanceof ExecutableActor) {
        absGraph.addVertex((ExecutableActor) a);
      }
    }
    for (Fifo f : graph.getFifos()) {
      AbstractActor absSrc = f.getSourcePort().getContainingActor();
      AbstractActor absTgt = f.getTargetPort().getContainingActor();
      if (absSrc instanceof ExecutableActor && absTgt instanceof ExecutableActor) {
        FifoAbstraction fa = absGraph.getEdge(absSrc, absTgt);
        if (fa == null) {
          fa = new FifoAbstraction();
          boolean res = absGraph.addEdge(absSrc, absTgt, fa);
          if (res != true) {
            throw new PreesmRuntimeException("Problem while creating graph copy.");
          }
        }
        Delay d = f.getDelay();
        if (d == null) {
          fa.delays.add(0L);
        } else {
          fa.delays.add(d.getSizeExpression().evaluate());
        }
        boolean fullyDelayed = true;
        for (long l : fa.delays) {
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
