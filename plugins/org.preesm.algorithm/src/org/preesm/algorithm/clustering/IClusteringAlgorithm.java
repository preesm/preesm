package org.preesm.algorithm.clustering;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.model.pisdf.AbstractActor;

/**
 * @author dgageot
 *
 */
public interface IClusteringAlgorithm {

  public Pair<ScheduleType, List<AbstractActor>> findActors(ClusteringBuilder clusteringBuilder);

  public boolean clusteringComplete(ClusteringBuilder clusteringBuilder);

}
