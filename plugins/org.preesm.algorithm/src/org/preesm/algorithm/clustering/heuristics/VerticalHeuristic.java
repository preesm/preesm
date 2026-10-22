package org.preesm.algorithm.clustering.heuristics;

import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This interface is made to cluster vertically a {@link PiGraph graph}. For instance, if a graph has too many or not
 * enough hierarchical levels for the given {@link Design architecture} or the given {@link Scenario scenario}, it can
 * adapts the hierarchy of the graph. The implementation classes are stored in the package clustering.identification
 *
 * @author rcazoulat
 */
public interface VerticalHeuristic extends IdentificationHeuristic {

  /***
   * Process the flattening of one of the {@link PiGraph subgraph} of a {@link PiGraph graph}
   *
   * @param parentGraph
   *          the {@link PiGraph parent graph}. It can be set to null. If so, subGraph is considered the top graph of
   *          the algorithm.
   * @param childGraph
   *          the current {@link PiGraph child graph} of topGraph.
   */
  public abstract void assessFlatteningBefore(PiGraph parentGraph, PiGraph childGraph);

  /***
   * Process the flattening of one of the {@link PiGraph subgraph} of a {@link PiGraph graph}
   *
   * @param parentGraph
   *          the {@link PiGraph parent graph}. It can be set to null. If so, subGraph is considered the top graph of
   *          the algorithm.
   * @param childGraph
   *          the current {@link PiGraph child graph} of topGraph.
   */
  public abstract void assessFlatteningAfter(PiGraph parentGraph, PiGraph childGraph);

  /***
   * Optional method. If there is hierarchy in the top graph, an entire subgraph could already be considered a cluster.
   *
   * @param graph
   *          the subgraph to inspect
   * @return true if the graph has been clusterized, false otherwise.
   */
  public boolean assessGraph(PiGraph graph);
}
