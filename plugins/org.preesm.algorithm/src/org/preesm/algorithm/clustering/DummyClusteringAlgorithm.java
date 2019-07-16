package org.preesm.algorithm.clustering;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.ScheduleFactory;
import org.preesm.model.algorithm.schedule.SequentialActorSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;

/**
 * @author dgageot
 *
 */
public class DummyClusteringAlgorithm implements IClusteringAlgorithm {

  @Override
  public ActorSchedule findActors(ClusteringBuilder clusteringBuilder) {

    // Search for the first mergeable couple
    List<Pair<AbstractActor, AbstractActor>> listCouple = PiSDFMergeabilty
        .getConnectedCouple(clusteringBuilder.getAlgorithm());

    // Build corresponding sequential schedule
    SequentialActorSchedule schedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
    schedule.getOrderedActors().add(listCouple.get(0).getLeft());
    schedule.getOrderedActors().add(listCouple.get(0).getRight());

    return schedule;
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
