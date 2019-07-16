package org.preesm.algorithm.clustering;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.ScheduleFactory;
import org.preesm.model.algorithm.schedule.SequentialActorSchedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;

/**
 * @author dgageot
 *
 */
public class APGANClusteringAlgorithm implements IClusteringAlgorithm {

  @Override
  public ActorSchedule findActors(ClusteringBuilder clusteringBuilder) {
    // Get list of mergeable couple
    List<Pair<AbstractActor, AbstractActor>> listCouple = PiSDFMergeabilty
        .getConnectedCouple(clusteringBuilder.getAlgorithm());

    // Compute RV
    Map<AbstractVertex, Long> rv = clusteringBuilder.getRepetitionVector();
    // Find the couple that maximize gcd
    long maxGcdRv = 0;
    long tmpGcdRv;
    Pair<AbstractActor, AbstractActor> maxCouple = null;
    for (Pair<AbstractActor, AbstractActor> l : listCouple) {
      // Comptute RV gcd
      tmpGcdRv = ArithmeticUtils.gcd(rv.get(l.getLeft()), rv.get(l.getRight()));
      if (tmpGcdRv > maxGcdRv) {
        maxGcdRv = tmpGcdRv;
        maxCouple = l;
      }
    }

    if (maxCouple == null) {
      throw new PreesmRuntimeException("APGANClusteringAlgorithm: Cannot find a couple to work on");
    }

    // Build corresponding sequential schedule
    SequentialActorSchedule schedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
    schedule.getOrderedActors().add(maxCouple.getLeft());
    schedule.getOrderedActors().add(maxCouple.getRight());

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
