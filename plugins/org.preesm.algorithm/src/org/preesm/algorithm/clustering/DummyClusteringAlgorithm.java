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

  @Override
  public Pair<ScheduleType, List<AbstractActor>> findActors(ClusteringBuilder clusteringBuilder) {

    // Search for the first mergeable couple
    List<Pair<AbstractActor, AbstractActor>> listCouple = PiSDFMergeabilty
        .getConnectedCouple(clusteringBuilder.getAlgorithm());

    // Build corresponding actor list
    List<AbstractActor> actorsList = new LinkedList<>();
    actorsList.add(listCouple.get(0).getLeft());
    actorsList.add(listCouple.get(0).getRight());

    return new ImmutablePair<>(ScheduleType.Sequential, actorsList);
  }

  @Override
  public boolean clusteringComplete(ClusteringBuilder clusteringBuilder) {
    boolean returnValue = true;
    if (clusteringBuilder.getAlgorithm().getActors().size() > 1) {
      returnValue = false;
    }
    return returnValue;
  }

}
