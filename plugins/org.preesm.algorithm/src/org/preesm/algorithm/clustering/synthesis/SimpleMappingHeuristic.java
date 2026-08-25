package org.preesm.algorithm.clustering.synthesis;

import org.preesm.algorithm.clustering.heuristics.MappingHeuristic;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.slam.Component;

/**
 * This simple {@link MappingHeuristic} will select for every cluster the main operator's component type. There is no
 * intelligent choices made with the different component types or the different actors constraints in the cluster.
 *
 * @author rcazoulat
 */
public class SimpleMappingHeuristic extends MappingHeuristic {

  @Override
  public Component selectComponent(PiGraph cluster) {

    // Simple mapping : the cluster can be executed on any component instance of the main component type.
    // A component is for example a CPU, a GPU, a FPGA
    // and a component instance is for example CPU_0, CPU_1...
    return scenario.getSimulationInfo().getMainOperator().getComponent();
  }

}
