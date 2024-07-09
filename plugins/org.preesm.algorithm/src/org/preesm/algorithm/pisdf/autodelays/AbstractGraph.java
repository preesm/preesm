/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019 - 2022)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019 - 2020)
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2022)
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
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

  private AbstractGraph() {
    // Forbids instantiation
  }

  /**
   * Fifo abstraction to get used in the analysis of this package.
   *
   * @author ahonorat
   */
  public static class FifoAbstraction {
    protected boolean       fullyDelayed;
    protected int           nbNonZeroDelays;
    protected int           nbIterationDelayed;
    protected long          prodRate;
    protected long          consRate;
    public final List<Long> pipelineValues;
    public final List<Long> delays;
    public final List<Fifo> fifos;

    private FifoAbstraction() {

      this.fullyDelayed = false;
      this.nbNonZeroDelays = 0;
      this.nbIterationDelayed = 0;
      this.prodRate = 0;
      this.consRate = 0;

      this.pipelineValues = new ArrayList<>();
      this.delays = new ArrayList<>();
      this.fifos = new ArrayList<>();
    }

    public boolean isFullyDelayed() {
      return fullyDelayed;
    }

    public int getNbNonZeroDelays() {
      return nbNonZeroDelays;
    }

    public int getNbIterationDelayed() {
      return nbIterationDelayed;
    }

    public long getProdRate() {
      return prodRate;
    }

    public long getConsRate() {
      return consRate;
    }

    public List<Long> getDelays() {
      return delays;
    }

    public List<Fifo> getFifos() {
      return fifos;
    }
  }

  /**
   * Creates an abstract graph from the given PiGraph.
   *
   * @param graph
   *          PiGraph to abstract. Fifo with rates equal to 0 are ignored.
   * @param brv
   *          Repetition vector of the PiGraph.
   * @return AbstractGraph of PiGraph.
   */
  public static DefaultDirectedGraph<AbstractActor, FifoAbstraction> createAbsGraph(final PiGraph graph,
      final Map<AbstractVertex, Long> brv) {
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

      if (!(absSrc instanceof ExecutableActor) || !(absTgt instanceof ExecutableActor)) {
        continue;
      }

      FifoAbstraction fa = absGraph.getEdge(absSrc, absTgt);
      final long srcRate = dop.getPortRateExpression().evaluate();
      final long tgtRate = dip.getPortRateExpression().evaluate();
      if (srcRate > 0 && tgtRate > 0) {
        final long gcd = MathFunctionsHelper.gcd(srcRate, tgtRate);
        if (fa == null) {
          fa = new FifoAbstraction();
          fa.prodRate = srcRate / gcd;
          fa.consRate = tgtRate / gcd;
          final boolean res = absGraph.addEdge(absSrc, absTgt, fa);
          if (!res) {
            throw new PreesmRuntimeException("Problem while creating graph copy.");
          }
        }
        fa.fifos.add(f);
        final Delay d = f.getDelay();
        long delayRawSize = 0L;
        long delay = 0L;
        if (d != null) {
          fa.nbNonZeroDelays++;
          delayRawSize = d.getSizeExpression().evaluate();
          delay = delayRawSize / gcd;
        }
        fa.delays.add(delay);

        final long brvDest = brv.get(absTgt);
        final long tgtPipelineCons = brvDest * tgtRate;
        fa.pipelineValues.add(tgtPipelineCons);

        final int nbIterDelayed = (int) Math.ceil((double) delayRawSize / tgtPipelineCons);
        fa.nbIterationDelayed = Math.max(fa.nbIterationDelayed, nbIterDelayed);

        // boolean fullyDelayed = true;
        // for (final long l : fa.delays) {
        // if (l == 0) {
        // fullyDelayed = false;
        // break;
        // }
        // }

        fa.fullyDelayed = fa.delays.stream().noneMatch(l -> l == 0);
      }

    }
    return absGraph;
  }

  /**
   * Computes a subpart of an abstract graph.
   *
   * @param absGraph
   *          The given abstract graph.
   * @param start
   *          The node where the subgraph starts.
   * @param fifosToIgnore
   *          A list of fifos to ignore (typically breaking fifos).
   * @param reverse
   *          If traversal is performed on graph transpose.
   * @return A subgraph containing the given start node and all other nodes accessible from it.
   */
  public static DefaultDirectedGraph<AbstractActor, FifoAbstraction> subDAGFrom(
      final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph, final AbstractActor start,
      final Set<FifoAbstraction> fifosToIgnore, final boolean reverse) {

    final DefaultDirectedGraph<AbstractActor,
        FifoAbstraction> subGraph = new DefaultDirectedGraph<>(FifoAbstraction.class);
    subGraph.addVertex(start);

    final List<AbstractActor> toVisit = new LinkedList<>();
    toVisit.add(start);
    subGraphDFS(absGraph, subGraph, fifosToIgnore, reverse, toVisit);

    return subGraph;
  }

  private static void subGraphDFS(final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph,
      final DefaultDirectedGraph<AbstractActor, FifoAbstraction> subGraph, final Set<FifoAbstraction> fifosToIgnore,
      final boolean reverse, final List<AbstractActor> visitPathStack) {

    final AbstractActor currentNode = visitPathStack.get(0);
    Set<FifoAbstraction> edges = null;
    if (reverse) {
      edges = absGraph.incomingEdgesOf(currentNode);
    } else {
      edges = absGraph.outgoingEdgesOf(currentNode);
    }

    for (final FifoAbstraction fa : edges) {
      if (fifosToIgnore.contains(fa)) {
        continue;
      }
      AbstractActor opposite = null;
      if (reverse) {
        opposite = absGraph.getEdgeSource(fa);
      } else {
        opposite = absGraph.getEdgeTarget(fa);
      }
      if (visitPathStack.contains(opposite)) {
        throw new PreesmRuntimeException("SubGraph is not a DAG, abandon.");
      }
      final boolean mustGoDeeper = !subGraph.vertexSet().contains(opposite);
      if (mustGoDeeper) {
        subGraph.addVertex(opposite);
        visitPathStack.add(0, opposite);
      }
      subGraph.addEdge(currentNode, opposite, fa);
      if (mustGoDeeper) {
        subGraphDFS(absGraph, subGraph, fifosToIgnore, reverse, visitPathStack);
      }

    }
    visitPathStack.remove(currentNode);
  }

  /**
   * Shallow copy of a graph.
   *
   * @param absGraph
   *          Graph to be copied.
   * @return Shallow copy of the input.
   */
  public static DefaultDirectedGraph<AbstractActor, FifoAbstraction>

      copyGraph(final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph) {
    final DefaultDirectedGraph<AbstractActor,
        FifoAbstraction> copyGraph = new DefaultDirectedGraph<>(FifoAbstraction.class);
    for (final AbstractActor aa : absGraph.vertexSet()) {
      copyGraph.addVertex(aa);
    }
    for (final FifoAbstraction fa : absGraph.edgeSet()) {
      final AbstractActor src = absGraph.getEdgeSource(fa);
      final AbstractActor tgt = absGraph.getEdgeTarget(fa);
      copyGraph.addEdge(src, tgt, fa);
    }
    return copyGraph;
  }

  /**
   * Shallow undirected copy of a graph.
   * <p>
   * If two FifoAbstracttion are present in the original graph (going in opposite directions by construction), then only
   * the first encountered one is copied.
   *
   * @param absGraph
   *          Graph to be copied with undirected edges.
   * @return Shallow copy of the input as an undirected graph.
   */
  public static DefaultUndirectedGraph<AbstractActor, FifoAbstraction>

      undirectedGraph(final DefaultDirectedGraph<AbstractActor, FifoAbstraction> absGraph) {
    final DefaultUndirectedGraph<AbstractActor,
        FifoAbstraction> undirectedGraph = new DefaultUndirectedGraph<>(FifoAbstraction.class);
    for (final AbstractActor aa : absGraph.vertexSet()) {
      undirectedGraph.addVertex(aa);
    }
    for (final FifoAbstraction fa : absGraph.edgeSet()) {
      final AbstractActor src = absGraph.getEdgeSource(fa);
      final AbstractActor tgt = absGraph.getEdgeTarget(fa);
      final FifoAbstraction edge = undirectedGraph.getEdge(src, tgt);
      if (edge == null) {
        undirectedGraph.addEdge(src, tgt, fa);
      } else if (edge.fullyDelayed) {
        // Gives priority to edges without delays. Required for ADFG computation in FPGA scheduling.
        undirectedGraph.removeEdge(edge);
        undirectedGraph.addEdge(src, tgt, fa);
      }
    }
    return undirectedGraph;
  }

}
