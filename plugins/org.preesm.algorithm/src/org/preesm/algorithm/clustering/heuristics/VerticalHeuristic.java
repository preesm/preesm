package org.preesm.algorithm.clustering.heuristics;

import java.util.Map;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This abstract {@link Heuristic heuristic} is made to cluster vertically a {@link PiGraph graph}. For instance, if a
 * graph has too many or not enough hierarchical levels for the given {@link Design architecture} or the given
 * {@link Scenario scenario}, it can adapts the hierarchy of the graph. The implementation classes are stored in the
 * package clustering.identification
 */
public abstract class VerticalHeuristic extends IdentificationHeuristic {

  /**
   * This enumeration is used to define if the compute is made before or after visiting the children graph.
   */
  public enum FlatteningOrder {
    TOP_DOWN, BOTTOM_UP
  }

  /**
   * If top-down, it will call the method {@link AssessFlattening} before visiting the {@link PiGraph children graphs}.
   * If bottom-up, it will call the same method after visting the {@link PiGraph children graphs}.
   */
  FlatteningOrder flatteningOrder;

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);
    flatteningOrder = setFlatteningOrder();
  }

  /***
   * Setter for attribute {@link flatteningOrder}. This method will be call in method {@link initHeuristicParameters}
   */
  abstract FlatteningOrder setFlatteningOrder();

  /***
   * Getter for attribute {@link flatteningOrder}
   *
   * @return the flattening order
   */
  public FlatteningOrder getFlatteningOrder() {
    return flatteningOrder;
  }

  /***
   * Process the flattening of one of the {@link PiGraph subgraph} of a {@link PiGraph graph}
   *
   * @param topGraph
   *          the {@link PiGraph parent graph}. It can be set to null. If so, subGraph is considered the top graph of
   *          the algorithm.
   * @param subGraph
   *          the current {@link PiGraph child graph} of topGraph.
   */
  public abstract void assessFlattening(PiGraph topGraph, PiGraph subGraph);

  /***
   * Optional method. If there is hierarchy in the top graph, an entire subgraph could already be considered a cluster.
   *
   * @param graph
   *          the subgraph to inspect
   * @return true if the graph has been clusterized, false otherwise.
   */
  public boolean assessGraph(PiGraph graph) {
    return false;
  }
}
