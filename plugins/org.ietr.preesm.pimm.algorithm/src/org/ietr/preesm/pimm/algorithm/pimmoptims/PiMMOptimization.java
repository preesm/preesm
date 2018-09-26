/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimmoptims;

import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * @author farresti
 *
 */
public interface PiMMOptimization {

  /**
   * Perform graph optimizations on a given PiGraph
   * 
   * @param graph
   *          the graph to optimize
   */
  boolean optimize(final PiGraph graph);

}
