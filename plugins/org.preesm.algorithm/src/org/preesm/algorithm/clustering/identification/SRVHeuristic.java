package org.preesm.algorithm.clustering.identification;

import java.util.Map;
import java.util.Set;
import org.preesm.algorithm.clustering.ClusterHelper;
import org.preesm.algorithm.clustering.heuristics.HorizontalHeuristic;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * SRV stands for "Single Repetition Value". It will seek {@link AbstractActor actors} that have a repetition count that
 * is superior or equal to the number of processing elements contained in the current node of the {@link Design
 * architecture}. This heuristic only create clusters of one executable actor, so it is better to execute it after the
 * URC heuristic if multiple heuristics are executed to identify clusters.
 *
 * @author rcazoulat
 */
public class SRVHeuristic extends HorizontalHeuristic {

  Map<AbstractVertex, Long> brv;
  long                      nPEs;

  @Override
  public boolean assesSeedable(AbstractActor actor) {

    boolean condition = true;
    condition &= !(actor instanceof SpecialActor);
    condition &= !(actor instanceof DataInterface);
    condition &= this.brv.get(actor) > this.nPEs;
    condition &= actor.getAllDataPorts().stream().map(p -> p.getFifo()).allMatch(f -> !f.isDelayPresent());

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
    this.nPEs = scenario == null ? 1 : ClusterHelper.computeSingleNodeCoreEquivalent(scenario);

  }

  @Override
  public boolean validateCluster(Set<AbstractActor> cluster) {
    return !ClusterHelper.clusterHasGetterAndSetterActors(cluster);
  }

  @Override
  public String getPrefix() {
    return "srv";
  }

}
