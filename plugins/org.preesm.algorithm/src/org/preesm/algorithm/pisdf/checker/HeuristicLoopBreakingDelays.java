package org.preesm.algorithm.pisdf.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
    System.err.println("edges: " + absGraph.edgeSet().size());
    // 2. look for cycles
    JohnsonSimpleCycles<AbstractActor, FifoAbstraction> cycleFinder = new JohnsonSimpleCycles<>(absGraph);
    List<List<AbstractActor>> cycles = cycleFinder.findSimpleCycles();
    for (List<AbstractActor> cycle : cycles) {
      StringBuilder sb = new StringBuilder();
      cycle.stream().forEach(a -> sb.append(" -> " + a.getName()));
      PreesmLogger.getLogger().log(Level.INFO, "Cycle: " + sb.toString());
    }

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
