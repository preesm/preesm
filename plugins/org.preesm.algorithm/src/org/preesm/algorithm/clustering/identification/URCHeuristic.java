package org.preesm.algorithm.clustering.identification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.preesm.algorithm.clustering.ClusterHelper;
import org.preesm.algorithm.clustering.heuristics.Heuristic;
import org.preesm.algorithm.clustering.heuristics.HorizontalHeuristic;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This {@link Heuristic heuristic} identifies the "Unique Repetition Count" pattern in a graph. If multiple
 * {@link AbstractActor actors} linked one to each other have the same repetition count and are not linked to any other
 * actors, then they can be regrouped together by the URC definition. If there is a delay between 2 of them, we can't
 * regroup them.
 */
public class URCHeuristic extends HorizontalHeuristic {

  /**
   * basic repetition vector, one value for each vertex of the graph
   */
  Map<AbstractVertex, Long> brv;

  /**
   * List to keep track of actors identified in the cluster
   */
  List<AbstractActor> alreadyIdentifiedActors = new ArrayList<>();

  /**
   * Number of equivalent Processing Elements
   */
  long nPEs;

  /*
   * ------------------------------------------------------------------------------------------------------
   *
   * Override methods
   *
   * ------------------------------------------------------------------------------------------------------
   */

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    super.initHeuristicParameters(graph, scenario, arch, taskParameters);

    // Computing the basic repetition vector of the graph
    brv = PiBRV.compute(graph, BRVMethod.LCM);

    // Computing number of equivalent cores
    nPEs = scenario == null ? 1 : ClusterHelper.computeSingleNodeCoreEquivalent(scenario);
  }

  @Override
  public boolean assesSeedable(AbstractActor actor) {

    // We verify that the potential future seed is an executable actor,
    // and if so, not a delay, hierarchical, or special actor.
    final boolean isSeedable = actor instanceof ExecutableActor && !(actor instanceof DelayActor)
        && !(actor instanceof PiGraph) && !(actor instanceof SpecialActor);

    if (isSeedable) {
      alreadyIdentifiedActors.clear();
      alreadyIdentifiedActors.add(actor);
    }
    return isSeedable;
  }

  @Override
  public boolean validateCluster(Set<AbstractActor> cluster) {
    boolean result = cluster.size() != 1;

    if (!result) {
      return false;
    }

    result &= !ClusterHelper.clusterHasGetterAndSetterActors(cluster);

    return result;

  }

  @Override
  public boolean assessMergeable(AbstractActor seed, AbstractActor actor) {

    // Check actor type
    boolean isMergeable = actor instanceof ExecutableActor && !(actor instanceof DelayActor)
        && !(actor instanceof PiGraph) && !(actor instanceof SpecialActor);

    // Check if actor has the correct repetition value
    isMergeable &= Objects.equals(brv.get(actor), brv.get(seed));

    if (seed.getDirectPredecessors().contains(actor)) {
      // If actor is predecessor of seed

      // Check if every FIFO of actor are good
      isMergeable &= seed.getDataInputPorts().stream().allMatch(x -> checkFifo(x.getFifo()));

      // Check that the candidate actor as only fifos incoming from the base actor
      isMergeable &= actor.getDataOutputPorts().stream().allMatch(x -> x.getFifo().getTarget().equals(seed));

      // Check that the actually processed actor as only fifos outgoing to the candidate actor
      isMergeable &= seed.getDataInputPorts().stream().allMatch(x -> x.getFifo().getSource().equals(actor));

    } else if (seed.getDirectSuccessors().contains(actor)) {
      // If actor is successor of seed

      // Check if every FIFO of actor are good
      isMergeable &= seed.getDataOutputPorts().stream().allMatch(x -> checkFifo(x.getFifo()));

      // Check that the actually processed actor as only fifos outgoing to the candidate actor
      isMergeable &= seed.getDataOutputPorts().stream().allMatch(x -> x.getFifo().getTarget().equals(actor));

      // Check that the candidate actor as only fifos incoming from the base actor
      isMergeable &= actor.getDataInputPorts().stream().allMatch(x -> x.getFifo().getSource().equals(seed));

    } else {
      throw new PreesmRuntimeException("Actor " + actor.getName() + " is not a direct pred/succ of seed "
          + seed.getName() + ". How is it possible to get there ?");
    }

    if (!isMergeable) {
      return false;
    }

    /* If all the checks are good, we can add actors in the list and return true */
    alreadyIdentifiedActors.add(actor);
    return true;
  }

  @Override
  public String getPrefix() {
    return "urc";
  }

  /*
   * ------------------------------------------------------------------------------------------------------
   *
   * URC heuristic specific methods
   *
   * ------------------------------------------------------------------------------------------------------
   */

  /**
   * This method verifies if the {@link Fifo fifo} is suited to potentially merge the other actor in the URC chain. It
   * has to be homogeneous and delayless.
   *
   * @param fifo
   *          the explored {@link Fifo fifo}
   * @return true if suited, false otherwise
   */
  private boolean checkFifo(Fifo fifo) {
    // Return true if rates are homogeneous and that no delay is involved
    return (fifo.getSourcePort().getExpression().evaluateAsLong() == fifo.getTargetPort().getExpression()
        .evaluateAsLong()) && (fifo.getDelay() == null);
  }
}
