package org.preesm.algorithm.clustering;

import org.preesm.model.algorithm.schedule.ActorSchedule;

/**
 * @author dgageot
 *
 */
public interface IClusteringAlgorithm {

  public ActorSchedule findActors(ClusteringBuilder clusteringBuilder);

  public boolean clusteringComplete(ClusteringBuilder clusteringBuilder);

}
