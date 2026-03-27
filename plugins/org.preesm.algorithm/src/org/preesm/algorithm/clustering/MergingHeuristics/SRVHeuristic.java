package org.preesm.algorithm.clustering.MergingHeuristics;

import java.util.Map;
import org.preesm.algorithm.clustering.MergingHeuristic;
import org.preesm.algorithm.clustering.scape.EuclideTransfo;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;

public class SRVHeuristic extends MergingHeuristic {

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Map<String, Object> params) {

    // Computing the basic repetition vector of the graph
    final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);

    // Adding nCore (from scenario)
    final long nPEs = EuclideTransfo.computeSingleNodeCoreEquivalent(scenario);

    // Adding in params list
    params.put("brv", brv);
    params.put("nPEs", nPEs);

  }

  @Override
  public boolean assesSeedable(AbstractActor actor, Map<String, Object> params) {

    final Map<AbstractVertex, Long> brv = (Map<AbstractVertex, Long>) params.get("brv");
    final Long nPEs = (long) params.get("nPEs");

    boolean condition = true;
    condition &= !(actor instanceof Actor && actor instanceof SpecialActor);
    condition &= !actor.getName().equals("single_source");
    condition &= !actor.getName().contains("urc");
    condition &= !actor.getName().contains("srv");
    condition &= brv.get(actor) > nPEs;

    return condition;
  }

  @Override
  public boolean assessMergeable(AbstractActor seed, AbstractActor actor, Map<String, Object> params) {
    return false;
  }

}
