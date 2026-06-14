package org.preesm.algorithm.clustering;

import java.util.Map;
import org.preesm.algorithm.clustering.deprecated.EuclideTransfo;
import org.preesm.algorithm.clustering.heuristics.HorizontalClusteringHeuristic;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class SRVHeuristic extends HorizontalClusteringHeuristic {

  Map<AbstractVertex, Long> brv;
  long                      nPEs;

  @Override
  public boolean assesSeedable(AbstractActor actor) {

    boolean condition = true;
    condition &= !(actor instanceof SpecialActor);
    condition &= this.brv.get(actor) > this.nPEs;

    return condition;
  }

  @Override
  public boolean assessMergeable(AbstractActor seed, AbstractActor actor) {
    return false;
  }

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);

    // Computing the basic repetition vector of the graph
    this.brv = PiBRV.compute(graph, BRVMethod.LCM);

    // Adding nCore (from scenario)
    this.nPEs = EuclideTransfo.computeSingleNodeCoreEquivalent(scenario);

  }

  @Override
  public String getPrefix() {
    return "srv";
  }

}
