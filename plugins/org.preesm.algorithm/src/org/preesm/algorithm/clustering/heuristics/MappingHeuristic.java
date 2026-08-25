package org.preesm.algorithm.clustering.heuristics;

import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.slam.Component;

/**
 * This abstract {@link Heuristic heuristic} is made to define a {@link Component component} type for a given
 * {@link PiGraph clusters/graph}.
 *
 * @author rcazoulat
 */
public abstract class MappingHeuristic extends Heuristic {

  /**
   * This method selects a {@link Component component} type for the given {@link PiGraph graph}
   *
   * @param cluster
   *          the given graph
   * @return the component
   */
  public abstract Component selectComponent(final PiGraph cluster);

}
