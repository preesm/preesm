package org.preesm.algorithm.pisdf.checker;

import org.preesm.model.pisdf.PiGraph;

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
   * @return Latency as a multiplication factor of a graph iteration duration.
   */
  public static int computeLatency(PiGraph graph) {

    // 0. get flatten graph of PiGraph
    // 1. compute brv and create AbsGraph
    // 2. compute breaking fifos
    // 3. get all source actors
    // 4 max+ algebra of the AbsGraph, as done for TopoRanking of HeuristicPeriodicActorSelection

    return 0;
  }
}
