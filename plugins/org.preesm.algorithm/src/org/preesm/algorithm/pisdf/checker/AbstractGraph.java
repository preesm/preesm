package org.preesm.algorithm.pisdf.checker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * To ease the analysis, the PiGraph is transformed into JGraphT graph thanks to this abstract graph class.
 * 
 * @author ahonorat
 */
public class AbstractGraph {

  /**
   * Fifo abstraction to get used in the analysis of this package.
   *
   * @author ahonorat
   */
  protected static class FifoAbstraction {
    boolean          fullyDelayed;
    int              nbNonZeroDelays;
    long             prodRate;
    long             consRate;
    final List<Long> delays;
    final List<Fifo> fifos;

    private FifoAbstraction() {

      this.fullyDelayed = false;
      this.nbNonZeroDelays = 0;
      this.prodRate = 0;
      this.consRate = 0;

      this.delays = new ArrayList<>();
      this.fifos = new ArrayList<>();
    }
  }

  protected static DefaultDirectedGraph<AbstractActor, FifoAbstraction> createAbsGraph(final PiGraph graph) {
    final DefaultDirectedGraph<AbstractActor,
        FifoAbstraction> absGraph = new DefaultDirectedGraph<>(FifoAbstraction.class);
    for (final AbstractActor a : graph.getActors()) {
      if (a instanceof ExecutableActor) {
        absGraph.addVertex(a);
      }
    }
    for (final Fifo f : graph.getFifos()) {
      final DataOutputPort dop = f.getSourcePort();
      final DataInputPort dip = f.getTargetPort();

      final AbstractActor absSrc = dop.getContainingActor();
      final AbstractActor absTgt = dip.getContainingActor();
      if ((absSrc instanceof ExecutableActor) && (absTgt instanceof ExecutableActor)) {
        FifoAbstraction fa = absGraph.getEdge(absSrc, absTgt);
        if (fa == null) {
          fa = new FifoAbstraction();
          long srcRate = dop.getPortRateExpression().evaluate();
          long tgtRate = dip.getPortRateExpression().evaluate();
          long gcd = MathFunctionsHelper.gcd(srcRate, tgtRate);
          fa.prodRate = gcd != 0 ? srcRate / gcd : srcRate;
          fa.consRate = gcd != 0 ? tgtRate / gcd : tgtRate;

          final boolean res = absGraph.addEdge(absSrc, absTgt, fa);
          if (!res) {
            throw new PreesmRuntimeException("Problem while creating graph copy.");
          }
        }
        fa.fifos.add(f);
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

  protected static DefaultDirectedGraph<AbstractActor, FifoAbstraction> subDAGFrom(
      DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph, AbstractActor start,
      Set<FifoAbstraction> fifosToIgnore, boolean reverse) {

    final DefaultDirectedGraph<AbstractActor,
        FifoAbstraction> subGraph = new DefaultDirectedGraph<>(FifoAbstraction.class);
    subGraph.addVertex(start);

    List<AbstractActor> toVisit = new LinkedList<>();
    toVisit.add(start);
    subGraphDFS(absGraph, subGraph, fifosToIgnore, reverse, toVisit);

    return subGraph;
  }

  private static void subGraphDFS(DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph,
      DefaultDirectedGraph<AbstractActor, FifoAbstraction> subGraph, Set<FifoAbstraction> fifosToIgnore,
      boolean reverse, List<AbstractActor> visitPathStack) {

    Iterator<AbstractActor> it = visitPathStack.iterator();
    AbstractActor currentNode = it.next();
    Set<FifoAbstraction> edges = null;
    if (reverse) {
      edges = absGraph.incomingEdgesOf(currentNode);
    } else {
      edges = absGraph.outgoingEdgesOf(currentNode);
    }

    for (FifoAbstraction fa : edges) {
      if (!fifosToIgnore.contains(fa)) {
        AbstractActor opposite = null;
        if (reverse) {
          opposite = absGraph.getEdgeSource(fa);
        } else {
          opposite = absGraph.getEdgeTarget(fa);
        }
        if (visitPathStack.contains(opposite)) {
          throw new PreesmRuntimeException("SubGraph is not a DAG, abandon.");
        }
        boolean mustGoDeeper = !subGraph.vertexSet().contains(opposite);
        if (mustGoDeeper) {
          subGraph.addVertex(opposite);
          visitPathStack.add(0, opposite);
        }
        subGraph.addEdge(currentNode, opposite);
        if (mustGoDeeper) {
          subGraphDFS(absGraph, subGraph, fifosToIgnore, reverse, visitPathStack);
        }

      }
    }
    it.remove();
  }

}
