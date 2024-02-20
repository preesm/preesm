package org.preesm.model.pisdf.util;

import org.preesm.model.pisdf.PiGraph;

public class ClusteringPatternSeeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  protected final PiGraph graph;

  protected ClusteringPatternSeeker(final PiGraph inputGraph) {
    this.graph = inputGraph;
  }
}
