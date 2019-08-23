package org.preesm.algorithm.clustering;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;

/**
 * @author dgageot
 *
 */
public class DummyClusteringAlgorithm implements IClusteringAlgorithm {

  List<Pair<AbstractActor, AbstractActor>> couples;

  @Override
  public Pair<ScheduleType, List<AbstractActor>> findActors(ClusteringBuilder clusteringBuilder) {

    // Build corresponding actor list
    List<AbstractActor> actorsList = new LinkedList<>();
    actorsList.add(couples.get(0).getLeft());
    actorsList.add(couples.get(0).getRight());

    return new ImmutablePair<>(ScheduleType.Sequential, actorsList);
  }

  @Override
  public boolean clusteringComplete(ClusteringBuilder clusteringBuilder) {
    // Get mergeable couple
    couples = PiSDFMergeabilty.getConnectedCouple(clusteringBuilder.getAlgorithm(),
        clusteringBuilder.getRepetitionVector());
    return couples.isEmpty();
  }

}
