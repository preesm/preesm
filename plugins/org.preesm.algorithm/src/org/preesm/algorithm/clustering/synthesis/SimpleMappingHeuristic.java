package org.preesm.algorithm.clustering.synthesis;

import org.preesm.algorithm.clustering.heuristics.MappingHeuristic;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;

public class SimpleMappingHeuristic extends MappingHeuristic {

  @Override
  public Component selectComponent(PiGraph cluster, Scenario scenario) {

    // Simple mapping : the cluster can be executed on any component instance of the main component type.
    // A component is for example a CPU, a GPU, a FPGA
    // and a component instance is for example CPU_0, CPU_1...

    return scenario.getSimulationInfo().getMainOperator().getComponent();
    // scenario.getDesign().getComponentInstances().stream().filter(ci -> ci.getComponent().equals(clusteringComponent))
    // .forEach(ci -> scenario.getConstraints().addConstraint(ci, cluster));
  }

}
