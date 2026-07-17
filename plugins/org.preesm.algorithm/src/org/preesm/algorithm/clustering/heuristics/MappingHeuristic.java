package org.preesm.algorithm.clustering.heuristics;

import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.slam.Component;

public abstract class MappingHeuristic extends Heuristic {

  public abstract Component selectComponent(final PiGraph cluster);

}
